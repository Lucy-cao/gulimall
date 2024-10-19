package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.CategoryLevel2Vo;
import org.redisson.api.*;
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
	public Map<String, List<CategoryLevel2Vo>> getCatByLevel() {
		Map<String, List<CategoryLevel2Vo>> vos = categoryService.getCatByLevel();
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
			System.out.println("读锁解锁成功。。。" + Thread.currentThread().getId());
		}
		return str;
	}

	/**
	 * 闭锁演示
	 * 场景：学校放假了，共有5个班级，5个班级的全部同学都走了才能锁大门
	 */
	//模拟锁门
	@GetMapping("/lockDoor")
	@ResponseBody
	public String lockDoor() throws InterruptedException {
		//创建闭锁
		RCountDownLatch countDownLatch = redisson.getCountDownLatch("door-lock");
		//设置倒数的数量
		countDownLatch.trySetCount(5);
		//等待闭锁都完成
		countDownLatch.await();

		return "放假了。。。";
	}

	//模拟学生离开班级
	@GetMapping("/gogogo/{id}")
	@ResponseBody
	public String gogogo(@PathVariable("id") Long id) {
		RCountDownLatch countDownLatch = redisson.getCountDownLatch("door-lock");
		//每走一个班级，计数减一
		countDownLatch.countDown();

		return id + "班的人都走了";
	}

	/**
	 * 信号量演示
	 * 场景：假设一个停车场初始车位有3个，开进来一辆车占用一个车位，开走一辆车释放一个车位。
	 * 当车位已经占满，后来的车就要等停车场有车开走释放才能再停
	 * 在分布式场景中可以用来限流
	 */
	@GetMapping("/park")
	@ResponseBody
	public String park() throws InterruptedException {
		RSemaphore park = redisson.getSemaphore("park");
		park.acquire(); //获取一个信号值，也就是说占一个车位。如果数量为0了，则会阻塞等待有位子释放
//		park.tryAcquire(); // 获取一个值，如果占不到位子了，就继续往下执行，就相当于先来看一看
		return "ok，占到一个车位";
	}

	@GetMapping("leave")
	@ResponseBody
	public String leave(){
		RSemaphore park = redisson.getSemaphore("park");
		park.release();//释放一个车位
		return "ok，释放一个车位";
	}
}
