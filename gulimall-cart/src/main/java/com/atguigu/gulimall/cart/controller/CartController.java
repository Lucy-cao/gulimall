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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
	@Autowired
	CartService cartService;

	@ResponseBody
	@GetMapping("/getUserCartItems")
	public List<CartItem> getUserCartItems(){
		List<CartItem> items = cartService.getUserCartItems();
		return items;
	}

	/**
	 * 查看购物车列表
	 * 浏览器有一个cookie user-key标识用户身份，过期时间一个月
	 * 如果是第一次使用，京东会给浏览器生成一个user-key，放到cookie中
	 * <p>
	 * 登录：有session
	 * 没登录：使用cookie里的user-key标识身份，查看临时购物车数据
	 */
	@GetMapping("/cart.html")
	public String cartListPage(Model model) throws ExecutionException, InterruptedException {
		//获取正式用户或临时用户的购物车
		Cart cart = cartService.getCart();
		model.addAttribute("cart", cart);
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

	@GetMapping("/checkItem")
	public String checkItem(@RequestParam("skuId")Long skuId, @RequestParam("check")Integer check){
		cartService.checkItem(skuId, check);
		return "redirect:http://cart.gulimall.com:9099/cart.html";
	}

	@GetMapping("/changeItemCount")
	public String changeItemCount(@RequestParam("skuId")Long skuId, @RequestParam("count")Integer count){
		cartService.changeItemCount(skuId, count);
		return "redirect:http://cart.gulimall.com:9099/cart.html";
	}

	//deleteItem
	@GetMapping("/deleteItem")
	public String deleteItem(@RequestParam("skuId")Long skuId){
		cartService.deleteItem(skuId);
		return "redirect:http://cart.gulimall.com:9099/cart.html";
	}
}
