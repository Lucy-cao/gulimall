package com.atguigu.gulimall.cart.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService {

	@RequestMapping("/product/skuinfo/info/{skuId}")
	R getSkuInfo(@PathVariable("skuId") Long skuId);

	@GetMapping("/product/skusaleattrvalue/getSaleAttrList/{skuId}")
	List<String> getSaleAttrList(@PathVariable("skuId") Long skuId);
}