package com.atguigu.gulimall.authserver.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.config.GiteeConfiguration;
import com.atguigu.gulimall.authserver.feign.MemberFeignService;
import com.atguigu.gulimall.authserver.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.authserver.vo.RegisterParam;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.authserver.vo.UserLoginParam;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.AuthConstant.SMS_CODE_CACHE_PREFIX;

@Controller
public class AuthController {
	@Autowired
	private ThirdPartyFeignService thirdPartyFeignService;
	@Autowired
	private MemberFeignService memberFeignService;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private GiteeConfiguration giteeConfig;

	/**
	 * 发送短信验证码，同手机号防刷，保存进redis
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("/sms/sendCode")
	@ResponseBody
	public R sendSmsCode(@RequestParam("phone") String phone) {
		String redisKey = SMS_CODE_CACHE_PREFIX + phone;
		//1、接口防刷：60s内同一个手机号不允许请求多次验证码，防止恶意刷验证码
		String redisCode = redisTemplate.opsForValue().get(redisKey);
		if (redisCode != null) {
			Long diff = System.currentTimeMillis() - Long.parseLong(redisCode.split("_")[1]);
			if (diff <= 60000) {
				return R.error("验证码获取频繁，请60s后再试");
			}
		}

		//2、验证码需要在注册的时候再次校验，所以需要保存到redis中。key中需要保存手机号，才能根据手机号获取到值
		//生成6位随机验证码
		Random random = new Random();
		int min = 100000;
		int max = 999999;
		String code = String.valueOf(random.nextInt((max - min) + 1) + min);
		//redis缓存验证码，防止同一个手机号在60s内再次发送验证码。所以code后面拼接当前的时间戳
		redisTemplate.opsForValue().set(redisKey, code + "_" + System.currentTimeMillis(),
				10, TimeUnit.MINUTES);

		//调用远程第三方发送短信服务
		thirdPartyFeignService.smsSendcode(phone, code);
		return R.ok();
	}

	@PostMapping("/register")
	public String regist(@Validated RegisterParam registerParam, BindingResult result, RedirectAttributes attributes) {
		Map<String, String> errors = new HashMap<>();
		boolean hasError = false;
		if (result.hasErrors()) {
			errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//			model.addAttribute("errors", errors);
			hasError = true;
			/**
			 * return "forward:/register.html";
			 * 报错：Request method 'POST' not supported
			 * 分析错误产生的过程：
			 * 用户注册->以post方式访问/register请求->有错误转发给register.html（路径映射默认都是get方式访问的）
			 * forward转发是指原请求原封不动转给下一个请求，原请求是post方式，但是register.html是get方式，所以返回错误不支持post
			 * 修改为直接跳转到register.html即可
			 */
		} else if (!registerParam.getPassword().equals(registerParam.getConfirmPassword())) {
			//进行数据验证：两次输入的密码是否一致；验证码是否正确
			errors.put("confirmPassword", "两次密码输入不一致");
			hasError = true;
		} else {
			//获取redis的验证码
			String redisCode = redisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + registerParam.getPhone());
			if (!StringUtils.isEmpty(redisCode)) {
				//分割后进行校验
				if (!registerParam.getCode().equals(redisCode.split("_")[0])) {
					//验证码错误
					errors.put("code", "验证码错误");
					hasError = true;
				} else {
					//验证码正确，删除redis的验证码
					redisTemplate.delete(SMS_CODE_CACHE_PREFIX + registerParam.getPhone());
					//调用会员服务，进行用户注册
					R registerRes = memberFeignService.register(registerParam);
					if (registerRes.getCode() != 0) {
						errors.put("registerResMsg", registerRes.getMsg());
						hasError = true;
					}
				}
			} else {
				errors.put("code", "验证码失效，请重新获取");
				hasError = true;
			}
		}

		if (hasError) {
			attributes.addFlashAttribute("errors", errors);
			//校验出错，转发到注册页
			return "redirect:http://auth.gulimall.com:9099/register.html";
		}
		//注册成功，重定向到登录页面
		return "redirect:http://auth.gulimall.com:9099/login.html";
	}

	@GetMapping("/login.html")
	public String loginPage(HttpSession session) {
		//访问登录页面，需要根据是否有session数据判断
		if (session.getAttribute(AuthConstant.LOGIN_USER) == null) {
			//未登录
			return "login";
		}
		return "redirect:http://gulimall.com:9099";
	}

	@PostMapping("/login")
	public String login(UserLoginParam param, RedirectAttributes attributes, HttpSession session) {
		//调用远程登录服务
		R login = memberFeignService.login(param);
		if (login.getCode() == 0) {
			MemberRespVo data = login.getData(new TypeReference<MemberRespVo>() {
			});
			session.setAttribute(AuthConstant.LOGIN_USER, data);
			return "redirect:http://gulimall.com:9099";
		}
		Map<String, String> errors = new HashMap<>();
		errors.put("msg", login.getMsg());
		attributes.addFlashAttribute("errors", errors);
		return "redirect:http://auth.gulimall.com:9099/login.html";
	}

	@GetMapping("/oauth2/{source}/success")
	public String oauthSuccess(@PathVariable("source") String source, @RequestParam("code") String code,
							   HttpSession session) throws Exception {
		//oauth接口回调方法
		//根据授权码code获取访问令牌access_token
		Map<String, String> querys = new HashMap<>();
		//redirect_uri={redirect_uri}&client_secret={client_secret}
		querys.put("grant_type", "authorization_code");
		querys.put("code", code);
		querys.put("client_id", giteeConfig.getClientId());
		querys.put("redirect_uri", "http://auth.gulimall.com:9099/oauth2/gitee/success");
		Map<String, String> body = new HashMap<>();
		body.put("client_secret", giteeConfig.getClientSecret());
		HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post",
				new HashMap<String, String>(), querys, body);
		if (response.getStatusLine().getStatusCode() == 200) {
			// 成功获取访问令牌
			String json = EntityUtils.toString(response.getEntity());
			JSONObject jsonObject = JSON.parseObject(json);
			String accessToken = jsonObject.getString("access_token");
			String expiresIn = jsonObject.getString("expires_in");
			// 使用访问令牌获取用户信息
			Map<String, String> getQuery = new HashMap<>();
			getQuery.put("access_token", accessToken);
			HttpResponse userResponse = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get",
					new HashMap<>(), getQuery);
			if (userResponse.getStatusLine().getStatusCode() == 200) {
				String userJson = EntityUtils.toString(userResponse.getEntity());
				JSONObject userJsonObject = JSON.parseObject(userJson);

				// 调用远程用户服务，添加用户，与第三方应用建立绑定关系
				MemberRespVo socialUserParam = new MemberRespVo();
				socialUserParam.setUsername(userJsonObject.getString("name"));
				socialUserParam.setHeader(userJsonObject.getString("avatar_url"));
				socialUserParam.setNickname(userJsonObject.getString("name"));
				socialUserParam.setSocialUid(userJsonObject.getString("id"));
				socialUserParam.setAccessToken(accessToken);
				socialUserParam.setExpires_in(expiresIn);
				R feignResponse = memberFeignService.oauthLogin(socialUserParam);
				if (feignResponse.getCode() == 0) {
					MemberRespVo user = feignResponse.getData(new TypeReference<MemberRespVo>() {
					});
					System.out.println("成功登录：" + user);
					session.setAttribute(AuthConstant.LOGIN_USER, user);
					// 成功则返回首页
					return "redirect:http://gulimall.com:9099";
				}
			}
		}
		//失败则重定向到登录页面
		return "redirect:http://auth.gulimall.com:9099/login.html";
	}

	@GetMapping("/logout.html")
	public String logout(HttpSession session){
		//清空session中的用户
		session.removeAttribute(AuthConstant.LOGIN_USER);
		//将session注销
		session.invalidate();
		return "login";
	}
}
