package com.atguigu.gulimall.search.controller;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search/save/")
public class ElasticSaveController {
	@Autowired
	ProductSaveService productSaveService;
	@PostMapping("skuUp")
	public R skuUp(@RequestBody List<SkuEsModel> esModels){
		productSaveService.skuUp(esModels);
		return R.ok();
	}
}
