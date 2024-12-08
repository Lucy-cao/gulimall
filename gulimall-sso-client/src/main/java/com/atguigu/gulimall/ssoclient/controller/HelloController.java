package com.atguigu.gulimall.ssoclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {
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
	public String employees(Model model, HttpSession session) {
		Object loginUser = session.getAttribute("loginUser");
		if(loginUser==null){
			//没有登录，重定向到sso-server登录页面
			return "redirect:";
		}

		List<String> emps = new ArrayList<>();
		emps.add("张三");
		emps.add("李四");
		model.addAttribute("emps", emps);
		return "list";
	}
}
