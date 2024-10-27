package com.atguigu.gulimall.search.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import java.util.List;

/**
 * 保存前端用户的所有搜索项
 */
@Data
public class SearchParam {
	/**
	 * 三级分类id
	 */
	private Long catalog3Id;
	/**
	 * 搜索关键字
	 */
	private String keyword;
	/**
	 * 排序:
	 * 销量：saleCount_asc/desc
	 * 价格：skuPrice_asc/desc
	 * 热度评分：hotScore_asc/desc
	 */
	private String sort;
	/**
	 * 是否有货
	 */
	private Integer hasStock;
	/**
	 * sku价格区间格式：最小值_最大值，例如01_500，_500，500_
	 */
	private String skuPrice;
	/**
	 * 品牌id，可以多选查询
	 */
	private List<Long> brandId;
	/**
	 * 按照属性筛选，一个属性可以筛多个值，以冒号分隔
	 * attrs=1_华为:小米&attrs=2_白色:黑色
	 */
	private List<String> attrs;
	/**
	 * 当前页码
	 */
	private Integer pageNum = 1;

}
