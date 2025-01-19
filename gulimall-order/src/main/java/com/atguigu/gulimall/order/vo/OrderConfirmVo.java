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

	// 商品总金额
//	BigDecimal total;

	public BigDecimal getTotal() {
		BigDecimal sum = new BigDecimal(0);
		for (OrderItemVo item : items) {
			if (item != null) {
				BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(item.getCount()));
				sum = sum.add(totalPrice);
			}
		}
		return sum;
	}

	// 应付总额
//	BigDecimal payPrice;

	public BigDecimal getPayPrice() {
		return getTotal();
	}
}
