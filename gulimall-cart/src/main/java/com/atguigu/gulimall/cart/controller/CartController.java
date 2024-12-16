package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.interceptor.CartLoginInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
	@Autowired
	CartService cartService;

	/**
	 * 查看购物车列表
	 * 浏览器有一个cookie user-key标识用户身份，过期时间一个月
	 * 如果是第一次使用，京东会给浏览器生成一个user-key，放到cookie中
	 * <p>
	 * 登录：有session
	 * 没登录：使用cookie里的user-key标识身份，查看临时购物车数据
	 */
	@GetMapping("/cart.html")
	public String cartListPage() {
		//根据是否登录，如果登录，返回正式用户的购物车数据；如果未登录，返回临时用户的购物车数据
		UserInfoTo userInfoTo = CartLoginInterceptor.threadLocal.get();
		System.out.println("userInfoTo = " + userInfoTo);
		return "cartList";
	}

	/**
	 * RedirectAttributes
	 * 		redirectAttributes.addFlashAttribute();将参数放入到session，且只能获取一次
	 * 		redirectAttributes.addAttribute("skuId", skuId);将参数拼接到重定向的地址后，作为RequestParam
	 */
	@GetMapping("/addToCart")
	public String successPage(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num,
							  RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
		//添加到购物车
		cartService.addToCart(skuId, num);
		redirectAttributes.addAttribute("skuId", skuId);
		//重定向到购物车添加成功页面，防止页面重复提交
		return "redirect:http://cart.gulimall.com:9099/addToCartSuccess.html";
	}

	@GetMapping("/addToCartSuccess.html")
	public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
		//添加到购物车成功页面，防止页面重复提交
		//从购物车再次查询商品信息
		CartItem item = cartService.getCartItem(skuId);
		model.addAttribute("item", item);
		return "success";
	}
}
