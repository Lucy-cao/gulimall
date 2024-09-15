package com.atguigu.common.to.es;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsModel {
	/**
	 * skuId
	 */
	private Long skuId;
	/**
	 * spuId
	 */
	private Long spuId;
	/**
	 * sku商品标题
	 */
	private String skuTitle;
	/**
	 * 商品价格
	 */
	private BigDecimal skuPrice;
	/**
	 * sku商品图片
	 */
	private String skuImg;
	/**
	 * 销量
	 */
	private Long saleCount;
	/**
	 * 是否有库存
	 */
	private Boolean hasStock;
	/**
	 * 热评分
	 */
	private Long hotScore;
	/**
	 * 品牌id
	 */
	private Long brandId;
	/**
	 * 商品分类id
	 */
	private Long catalogId;
	/**
	 * 品牌名称
	 */
	private String brandName;
	/**
	 * 品牌logo图片
	 */
	private String brandImg;
	/**
	 * 商品分类名称
	 */
	private String catalogName;

	private List<Attr> attrs;

	@Data
	public static class Attr {
		/**
		 * 属性id
		 */
		private Long attrId;
		/**
		 * 属性名
		 */
		private String attrName;
		/**
		 * 属性值
		 */
		private String attrValue;
	}
}
