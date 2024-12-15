package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.interceptor.CartLoginInterceptor;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
	/**
	 * 查看购物车列表
	 * 浏览器有一个cookie user-key标识用户身份，过期时间一个月
	 * 如果是第一次使用，京东会给浏览器生成一个user-key，放到cookie中
	 *
	 * 登录：有session
	 * 没登录：使用cookie里的user-key标识身份，查看临时购物车数据
	 */
	@GetMapping("/cart.html")
	public String cartListPage(){
		//根据是否登录，如果登录，返回正式用户的购物车数据；如果未登录，返回临时用户的购物车数据
		UserInfoTo userInfoTo = CartLoginInterceptor.threadLocal.get();
		System.out.println("userInfoTo = " + userInfoTo);
		return "cartList";
	}
}
