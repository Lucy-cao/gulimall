package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryLevel2Vo {
	/**
	 * 二级分类的id
	 */
	private Long id;
	/**
	 * 二级分类的名字
	 */
	private String name;
	/**
	 * 所属一级分类的id
	 */
	private Long catalog1Id;
	/**
	 * 包含的三级分类列表
	 */
	private List<CategoryLevel3Vo> catalog3List;
}
