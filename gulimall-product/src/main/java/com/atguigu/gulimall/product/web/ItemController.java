package com.atguigu.gulimall.product.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
	/**
	 * 返回详情页
	 */
	@GetMapping("/{skuId}.html")
	public String item(@PathVariable("skuId") Long skuId) {
		System.out.println("获取商品" + skuId + "详情");
		return "item";
	}
}
