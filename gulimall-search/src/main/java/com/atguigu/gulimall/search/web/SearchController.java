package com.atguigu.gulimall.search.web;

import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.jws.WebParam;

@Controller
public class SearchController {
	@Autowired
	SearchService searchService;
	@GetMapping("/list.html")
	public String listPage(SearchParam param, Model model){
		//按照条件进行搜索
		SearchResult result = searchService.search(param);
		model.addAttribute("result", result);
		return "list";
	}
}
