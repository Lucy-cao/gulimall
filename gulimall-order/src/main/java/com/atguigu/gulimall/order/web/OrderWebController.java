package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {
	@Autowired
	OrderService orderService;

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
	public String toTrade(Model model) throws ExecutionException, InterruptedException {
		//获取结算页面的数据
		OrderConfirmVo confirmVo = orderService.getConfirmData();
		model.addAttribute("orderConfirmData", confirmVo);

		return "confirm";
	}
}
