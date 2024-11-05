package com.atguigu.gulimall.product.vo.sku;

import cn.hutool.core.lang.Range;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
	//1、sku的基本信息 pms_sku_info
	private SkuInfoEntity skuInfo;

	private Boolean hasStock = true;

	//2、sku的图片信息 pms_sku_images
	private List<SkuImagesEntity> images;

	//3、spu的销售属性 pms_sku_sale_attr_value
	private List<SkuSaleAttrVo> saleAttrs;

	//4、spu的介绍信息 pms_spu_info_desc
	private SpuInfoDescEntity spuInfoDesc;

	//5、spu的规格包装 pms_product_attr_value
	private List<SpuAttrGroupVo> attrGroups;

	@Data
	public static class SkuSaleAttrVo {
		private Long attrId;
		private String attrName;
		private List<SkuSaleAttrValueVo> attrValues;
	}

	@Data
	public static class SkuSaleAttrValueVo{
		private String attrValue;
		private String skuIds;
	}

	@Data
	public static class SpuAttrGroupVo {
		private String groupName;
		private List<SpuAttrVo> attrs;
	}

	@Data
	public static class SpuAttrVo {
		private String attrName;
		private String attrValues;
	}
}
