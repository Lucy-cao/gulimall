package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartLoginInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
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
import java.util.stream.Collectors;

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
		if (skuRes != null) {
			//如果redis里面已经有商品，则取出来商品数量加上新添加的数量
			cartItem = JSON.parseObject(skuRes.toString(), CartItem.class);
			cartItem.setCount(cartItem.getCount() + num);
		} else {
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

	@Override
	public CartItem getCartItem(Long skuId) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		Object skuRes = cartOps.get(skuId.toString());
		CartItem cartItem = (skuRes != null) ? JSON.parseObject(skuRes.toString(), CartItem.class) : null;
		return cartItem;
	}

	@Override
	public Cart getCart() throws ExecutionException, InterruptedException {
		Cart cart = new Cart();
		UserInfoTo userInfoTo = CartLoginInterceptor.threadLocal.get();
		String tempKey = CART_PREFIX + userInfoTo.getUserKey();
		if (userInfoTo.getUserId() != null) {
			//用户已登录
			//判断是否有临时用户的购物车，如果有需要合并到当前登录用户的购物车
			List<CartItem> tempItems = getCartItemsByKey(tempKey);
			if (tempItems != null && tempItems.size() > 0) {
				for (CartItem tempItem : tempItems) {
					addToCart(tempItem.getSkuId(), tempItem.getCount());
				}
				//删除临时用户的购物车
				clearCart(tempKey);
			}
			//返回当前登录用户的购物车
			String userKey = CART_PREFIX + userInfoTo.getUserId();
			List<CartItem> items = getCartItemsByKey(userKey);
			cart.setItems(items);
		} else {
			//用户未登录
			List<CartItem> items = getCartItemsByKey(tempKey);
			cart.setItems(items);
		}

		return cart;
	}

	@Override
	public void clearCart(String cartKey) {
		redisTemplate.delete(cartKey);
	}

	@Override
	public void checkItem(Long skuId, Integer check) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		Object skuRes = cartOps.get(skuId.toString());
		CartItem cartItem = JSON.parseObject(skuRes.toString(), CartItem.class);
		cartItem.setCheck(check == 1 ? true : false);
		cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
	}

	@Override
	public void changeItemCount(Long skuId, Integer count) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		Object skuRes = cartOps.get(skuId.toString());
		CartItem cartItem = JSON.parseObject(skuRes.toString(), CartItem.class);
		cartItem.setCount(count);
		cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
	}

	@Override
	public void deleteItem(Long skuId) {
		BoundHashOperations<String, Object, Object> cartOps = getCartOps();
		cartOps.delete(skuId.toString());
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

	private List<CartItem> getCartItemsByKey(String cartKey) {
		List<Object> values = redisTemplate.boundHashOps(cartKey).values();
		if (values != null && values.size() > 0) {
			List<CartItem> collect = values.stream().map(obj -> {
				return JSON.parseObject(obj.toString(), CartItem.class);
			}).collect(Collectors.toList());
			return collect;
		}
		return null;
	}
}
