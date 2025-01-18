package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {
	// 收货人信息
	List<MemberAddressVo> address;

	// 待结算的商品信息
	List<OrderItemVo> items;

	// 发票信息

	// 优惠券信息
	Integer integration;

	// 商品总金额
	BigDecimal total;

	// 应付总额
	BigDecimal payPrice;

}
