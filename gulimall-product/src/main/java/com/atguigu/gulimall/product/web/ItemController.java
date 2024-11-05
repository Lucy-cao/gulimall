package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.sku.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
	@Autowired
	SkuInfoService skuInfoService;
	/**
	 * 返回详情页
	 */
	@GetMapping("/{skuId}.html")
	public String item(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
		System.out.println("获取商品" + skuId + "详情");
		//获取商品详情信息
		SkuItemVo skuItemVo = skuInfoService.getItemBySkuId(skuId);
		model.addAttribute("item", skuItemVo);
		return "item";
	}
}
