package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
	private Long skuId;//商品id
	private String title;//商品标题
	private String image;//图片
	private List<String> attrs;//销售属性
	private BigDecimal price;//单价
	private Integer count;//商品数量
	private BigDecimal totalPrice;//商品总金额
}
