package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberFeignService;
import com.atguigu.gulimall.order.interceptor.LoginInterceptor;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
	@Autowired
	MemberFeignService memberFeignService;
	@Autowired
	CartFeignService cartFeignService;
	@Autowired
	ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public OrderConfirmVo getConfirmData() throws ExecutionException, InterruptedException {
		OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
		MemberRespVo memberRespVo = LoginInterceptor.userLogin.get();
		/**
		 * 使用异步编排获取收货信息和购物车信息，存在问题：当使用异步线程调用feign请求时，获取不到原线程的请求信息
		 * 解决方案：获取当前中的requestAttributes，并赋值到异步线程的RequestContextHolder。
		 */
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
			//异步调用的第一步将主线程的请求信息赋值到当前线程
			RequestContextHolder.setRequestAttributes(requestAttributes);
			//1、获取收货人信息
			R response = memberFeignService.getMemberAddress(memberRespVo.getId());
			List<MemberAddressVo> address = response.getData(new TypeReference<List<MemberAddressVo>>() {
			});
			orderConfirmVo.setAddress(address);
		}, executor);

		CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
			//异步调用的第一步将主线程的请求信息赋值到当前线程
			RequestContextHolder.setRequestAttributes(requestAttributes);
			//2、获取购物车的商品信息
			List<OrderItemVo> items = cartFeignService.getUserCartItems();
			orderConfirmVo.setItems(items);
		}, executor);

		//3、获取优惠券
		Integer integration = memberRespVo.getIntegration();
		orderConfirmVo.setIntegration(integration);

		//4、其他数据自动计算

		// 需要等上面两个异步线程执行结束后才能返回
		CompletableFuture.allOf(addressFuture, cartFuture).get();

		return orderConfirmVo;
	}

}