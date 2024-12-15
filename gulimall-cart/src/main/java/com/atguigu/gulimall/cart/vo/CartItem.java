package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartItem {
	private Long skuId;//商品id
	private Boolean check;//是否被选中
	private String title;//商品标题
	private String image;//图片
	private List<String> attrs;//销售属性
	private BigDecimal price;//单价
	private Integer count;//商品数量
	private BigDecimal totalPrice;//商品总金额。需要自己进行计算


	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Boolean getCheck() {
		return check;
	}

	public void setCheck(Boolean check) {
		this.check = check;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<String> getAttrs() {
		return attrs;
	}

	public void setAttrs(List<String> attrs) {
		this.attrs = attrs;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getTotalPrice() {
		return this.price.multiply(new BigDecimal(this.count));
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
}
