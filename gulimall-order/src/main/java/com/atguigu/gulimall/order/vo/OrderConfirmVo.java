package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.List;

//@Data
public class OrderConfirmVo {
	// 收货人信息
	@Getter
	@Setter
	List<MemberAddressVo> address;

	// 待结算的商品信息
	@Getter
	@Setter
	List<OrderItemVo> items;

	// 发票信息

	// 优惠券信息
	@Getter
	@Setter
	Integer integration;

	public Integer getTotalCount() {
		Integer i = 0;
		if (items != null) {
			for (OrderItemVo item : items) {
				i += item.getCount();
			}
		}
		return i;
	}


	// 商品总金额
//	BigDecimal totalPrice;

	public BigDecimal getTotalPrice() {
		BigDecimal sum = new BigDecimal(0);
		if (items != null) {
			for (OrderItemVo item : items) {
				BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(item.getCount()));
				sum = sum.add(totalPrice);
			}
		}
		return sum;
	}

	// 应付总额
//	BigDecimal payPrice;

	public BigDecimal getPayPrice() {
		return getTotalPrice();
	}
}
