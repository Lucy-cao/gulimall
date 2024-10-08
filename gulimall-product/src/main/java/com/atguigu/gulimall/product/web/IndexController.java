package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.CategoryLevel2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
	@Autowired
	CategoryService categoryService;
	/**
	 * 访问首页
	 * @return 首页html页面
	 */
	@GetMapping({"/","/index.html"})
	public String indexPage(Model model){
		//1、查出所有一级分类
		List<CategoryEntity> categories = categoryService.getAllFirstLevelCat();

		model.addAttribute("categories", categories);
		//视图解析器会进行拼接：classpath:/templates/ + 返回值 + .html
		return "index";
	}

	@GetMapping("/index/getCatByLevel")
	@ResponseBody
	public Map<Long,List<CategoryLevel2Vo>> getCatByLevel(){
		Map<Long,List<CategoryLevel2Vo>> vos = categoryService.getCatByLevel();
		return vos;
	}

}
