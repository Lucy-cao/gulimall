package com.atguigu.gulimall.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {
	@GetMapping("/list.html")
	public String listPage(Model model){
		return "list";
	}
}
