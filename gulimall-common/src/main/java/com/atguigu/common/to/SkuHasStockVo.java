package com.atguigu.common.to;

import lombok.Data;

@Data
public class SkuHasStockVo {
	/**
	 * skuId
	 */
	private Long skuId;
	/**
	 * 是否有库存
	 */
	private Boolean hasStock;
}
