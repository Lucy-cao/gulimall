package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.CategoryLevel2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
	@Autowired
	CategoryService categoryService;
	@Autowired
	RedissonClient redisson;
	@Autowired
	StringRedisTemplate redisTemplate;

	/**
	 * 访问首页
	 *
	 * @return 首页html页面
	 */
	@GetMapping({"/", "/index.html"})
	public String indexPage(Model model) {
		//1、查出所有一级分类
		List<CategoryEntity> categories = categoryService.getAllFirstLevelCat();

		model.addAttribute("categories", categories);
		//视图解析器会进行拼接：classpath:/templates/ + 返回值 + .html
		return "index";
	}

	@GetMapping("/index/getCatByLevel")
	@ResponseBody
	public Map<Long, List<CategoryLevel2Vo>> getCatByLevel() {
		Map<Long, List<CategoryLevel2Vo>> vos = categoryService.getCatByLevel();
		return vos;
	}

	@GetMapping("/hello")
	@ResponseBody
	public String hello() {
		//1、获取一把锁，只要锁的名字一样，就是同一把锁
		RLock lock = redisson.getLock("my-lock");
		//2、加锁
//		lock.lock(); //阻塞式等待，只要拿到这把锁的前一个请求还没执行完，后一个想要拿这把锁的请求就需要一直等待着
		lock.lock(10, TimeUnit.SECONDS);
		try {
			System.out.println("加锁成功，执行业务。。。" + Thread.currentThread().getId());
			Thread.sleep(30000);
		} catch (Exception e) {

		} finally {
			//3、解锁
			System.out.println("释放锁。。。" + Thread.currentThread().getId());
			lock.unlock();
		}
		return "hello";
	}

	@GetMapping("/write")
	@ResponseBody
	public String writeValue() {
		//加写锁
		RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
		RLock wLock = readWriteLock.writeLock();
		String str = "";
		try {
			//改数据加写锁，读数据加读锁
			wLock.lock();
			System.out.println("写锁加锁成功。。。" + Thread.currentThread().getId());
			//将uuid的数据写入redis
			str = UUID.randomUUID().toString();
			Thread.sleep(30000); //模拟业务长时间执行
			redisTemplate.opsForValue().set("writeValue", str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wLock.unlock();
			System.out.println("写锁解锁成功。。。" + Thread.currentThread().getId());
		}

		return str;
	}

	@GetMapping("/read")
	@ResponseBody
	public String readValue() {
		//加读锁
		RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
		RLock rLock = lock.readLock();
		String str = "";
		try {
			rLock.lock();
			System.out.println("读锁加锁成功。。。" + Thread.currentThread().getId());
			Thread.sleep(20000);//模拟业务长时间执行
			str = redisTemplate.opsForValue().get("writeValue");
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			rLock.unlock();
			System.out.println("读锁解锁成功。。。" + Thread.currentThread().getId());
		}
		return str;
	}

}
