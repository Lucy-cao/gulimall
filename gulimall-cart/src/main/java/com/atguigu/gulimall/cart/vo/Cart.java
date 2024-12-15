package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车主信息
 */
public class Cart {

	private List<CartItem> items;
	private Integer CartTypeNum;//商品种类数量
	private Integer CartNum;//商品总数量
	private BigDecimal totalAmount;//商品总金额
	private BigDecimal totalReduceAmount;//优惠总金额

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public Integer getCartTypeNum() {
		return items != null ? items.size() : 0;
	}

	public Integer getCartNum() {
		return items.stream().mapToInt(CartItem::getCount).sum();
	}

	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = new BigDecimal(0);
		for (CartItem item : items) {
			totalAmount = totalAmount.add(item.getTotalPrice());
		}
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalReduceAmount() {
		return totalReduceAmount;
	}

	public void setTotalReduceAmount(BigDecimal totalReduceAmount) {
		this.totalReduceAmount = totalReduceAmount;
	}
}
