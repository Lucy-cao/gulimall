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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
	@Autowired
	MemberFeignService memberFeignService;
	@Autowired
	CartFeignService cartFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public OrderConfirmVo getConfirmData() {
		OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
		MemberRespVo memberRespVo = LoginInterceptor.userLogin.get();
		//1、获取收货人信息
		R response = memberFeignService.getMemberAddress(memberRespVo.getId());
		List<MemberAddressVo> address = response.getData(new TypeReference<List<MemberAddressVo>>() {
		});
		orderConfirmVo.setAddress(address);

		//2、获取购物车的商品信息
		List<OrderItemVo> items = cartFeignService.getUserCartItems();
		orderConfirmVo.setItems(items);

		//3、获取优惠券
		Integer integration = memberRespVo.getIntegration();
		orderConfirmVo.setIntegration(integration);

		//4、其他数据自动计算

		return orderConfirmVo;
	}

}