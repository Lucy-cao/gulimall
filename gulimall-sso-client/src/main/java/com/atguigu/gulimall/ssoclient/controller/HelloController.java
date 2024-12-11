package com.atguigu.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {
	@Value("${sso.server.url}")
	String ssoServerUrl;

	/**
	 * 无需登录就可以访问的资源
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	/**
	 * 需要登录才可以访问
	 * @param model
	 * @return
	 */
	@GetMapping("/employees")
	public String employees(Model model, HttpSession session,
							@RequestParam(value = "token", required = false)String token) {
		//判断是否携带token，如果有token表明已经是登录成功了
		if(!StringUtils.isEmpty(token)){
			//去ssoserver获取登录的用户信息
			session.setAttribute("loginUser", "zhangsan");
		}

		Object loginUser = session.getAttribute("loginUser");
		if (loginUser == null) {
			//没有登录，重定向到sso-server登录页面
			return "redirect:" + ssoServerUrl+"?redirect_url=http://client1.com:8081/employees";
		}

		List<String> emps = new ArrayList<>();
		emps.add("张三");
		emps.add("李四");
		model.addAttribute("emps", emps);
		return "list";
	}

	/**
	 * 另外一个需要登录才可以访问的接口
	 * @param model
	 * @return
	 */
	@GetMapping("/boss")
	public String boss(Model model, HttpSession session,
							@RequestParam(value = "token", required = false)String token) {
		//判断是否携带token，如果有token表明已经是登录成功了
		if(!StringUtils.isEmpty(token)){
			//去ssoserver获取登录的用户信息
			session.setAttribute("loginUser", "zhangsan");
		}

		Object loginUser = session.getAttribute("loginUser");
		if (loginUser == null) {
			//没有登录，重定向到sso-server登录页面
			return "redirect:" + ssoServerUrl+"?redirect_url=http://client2.com:8081/boss";
		}

		List<String> emps = new ArrayList<>();
		emps.add("boss1");
		emps.add("boss2");
		model.addAttribute("emps", emps);
		return "list";
	}
}
