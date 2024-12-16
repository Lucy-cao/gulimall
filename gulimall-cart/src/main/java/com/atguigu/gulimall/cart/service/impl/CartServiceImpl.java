package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartLoginInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	ThreadPoolExecutor executor;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	ProductFeignService productFeignService;

	public static final String CART_PREFIX = "gulimall:cart:";

	@Override
	public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
		//根据用户是否登录判断是获取临时购物车
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();

		//需要判断redis是否有相同的商品已放入购物车
		Object skuRes = cartOps.get(skuId.toString());

		CartItem cartItem = null;
		if(skuRes != null){
			//如果redis里面已经有商品，则取出来商品数量加上新添加的数量
			cartItem = JSON.parseObject(skuRes.toString(), CartItem.class);
			cartItem.setCount(cartItem.getCount() + num);
		}else{
			//如果redis里面没有此商品，获取加入购物车商品的详情信息
			cartItem = new CartItem();
			//使用异步线程同时获取商品的基础信息和销售属性信息
			CartItem finalCartItem = cartItem;
			CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
				R res = productFeignService.getSkuInfo(skuId);
				SkuInfoVo skuInfo = res.getData("skuInfo", new TypeReference<SkuInfoVo>() {
				});
				finalCartItem.setSkuId(skuId);
				finalCartItem.setCheck(true);
				finalCartItem.setCount(num);
				finalCartItem.setImage(skuInfo.getSkuDefaultImg());
				finalCartItem.setTitle(skuInfo.getSkuTitle());
				finalCartItem.setPrice(skuInfo.getPrice());
			}, executor);
			//获取商品的销售属性
			CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
				List<String> saleAttrList = productFeignService.getSaleAttrList(skuId);
				finalCartItem.setAttrs(saleAttrList);
			});
			//组合两个异步操作，等待操作结束
			CompletableFuture.allOf(task1, task2).get();
		}
		//将商品详情放入至redis
		cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
		return cartItem;
	}

	private BoundHashOperations<String, Object, Object> getCartOps() {
		String redisKey = "";
		UserInfoTo userInfoTo = CartLoginInterceptor.threadLocal.get();
		if (userInfoTo.getUserId() != null) {
			//用户已登录
			redisKey = CART_PREFIX + userInfoTo.getUserId();
		} else {
			//用户未登录
			redisKey = CART_PREFIX + userInfoTo.getUserKey();
		}
		//获取对当前正式用户或临时用户数据的哈希操作
		return redisTemplate.boundHashOps(redisKey);
	}
}
