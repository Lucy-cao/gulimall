package com.atguigu.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class OrderWebController {
	@GetMapping("/list.html")
	public String listPage(HttpSession session) {
		if (session.getAttribute("loginUser") == null) {
			//未登录
			return "redirect:http://auth.gulimall.com:9099/login.html";
		}
		return "list";
	}

	/**
	 * 跳转结算页面
	 */
	@GetMapping("/toTrade")
	public String toTrade() {
		return "confirm";
	}
}
