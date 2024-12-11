package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
	@Autowired
	private StringRedisTemplate redisTemplate;

	/**
	 * 返回登录页面
	 * @param redirect_url 表示从哪个页面来源访问服务端，登录成功之后需要再次跳转回去
	 * @param model 用于给页面传值
	 * @param token 标识服务端是否已登录过
	 * @return
	 */
	@GetMapping("login.html")
	public String loginPage(@RequestParam("redirect_url") String redirect_url, Model model,
							@CookieValue(value = "sso-token", required = false) String token) {
		if (!StringUtils.isEmpty(token)) {
			//cookie里面有token值，说明ssoserver已经登录过了，可以直接返回到重定向来的地址，并携带token
			return "redirect:" + redirect_url + "?token=" + token;
		}

		model.addAttribute("redirect_url", redirect_url);
		return "login";
	}

	/**
	 * 执行登录请求
	 */
	@PostMapping("doLogin")
	public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password,
						  @RequestParam("redirect_url") String redirect_url, HttpServletResponse response) {
		//执行登录验证
		if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			//当前用户的唯一标识放入到redis中
			redisTemplate.opsForValue().set(uuid, username, 24, TimeUnit.HOURS);
			//给ssoserver网站返回一个cookie，用于下次访问的时候带上标识ssoserver是否已登录过
			Cookie cookie = new Cookie("sso-token", uuid);
			response.addCookie(cookie);
			return "redirect:" + redirect_url + "?token=" + uuid;
		}

		return "login";
	}
}
