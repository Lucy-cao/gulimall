package com.atguigu.gulimall.product.vo;

import lombok.Data;

@Data
public class CategoryLevel3Vo {
	/**
	 * 三级分类的id
	 */
	private Long id;
	/**
	 * 所属二级分类的id
	 */
	private Long catalog2Id;
	/**
	 * 三级分类的名称
	 */
	private String name;
}
