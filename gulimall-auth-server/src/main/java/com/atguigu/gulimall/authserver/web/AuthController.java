package com.atguigu.gulimall.authserver.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.feign.MemberFeignService;
import com.atguigu.gulimall.authserver.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.authserver.vo.RegisterParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.gulimall.authserver.constant.AuthConstant.SMS_CODE_CACHE_PREFIX;

@Controller
public class AuthController {
	@Autowired
	private ThirdPartyFeignService thirdPartyFeignService;
	@Autowired
	private MemberFeignService memberFeignService;
	@Autowired
	private StringRedisTemplate redisTemplate;

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
}
