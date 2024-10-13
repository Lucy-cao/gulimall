package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.CategoryLevel2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
	@Autowired
	CategoryService categoryService;
	@Autowired
	RedissonClient redisson;
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

	@GetMapping("/hello")
	@ResponseBody
	public String hello(){
		//1、获取一把锁，只要锁的名字一样，就是同一把锁
		RLock lock = redisson.getLock("my-lock");
		//2、加锁
//		lock.lock(); //阻塞式等待，只要拿到这把锁的前一个请求还没执行完，后一个想要拿这把锁的请求就需要一直等待着
		lock.lock(10, TimeUnit.SECONDS);
		try {
			System.out.println("加锁成功，执行业务。。。"+Thread.currentThread().getId());
			Thread.sleep(30000);
		} catch (Exception e){

		}finally {
			//3、解锁
			System.out.println("释放锁。。。"+Thread.currentThread().getId());
			lock.unlock();
		}
		return "hello";
	}

}
