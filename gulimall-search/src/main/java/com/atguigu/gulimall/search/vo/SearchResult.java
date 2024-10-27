package com.atguigu.gulimall.search.vo;

import co.elastic.clients.elasticsearch._types.analysis.StemmerOverrideTokenFilter;
import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;
import org.w3c.dom.ls.LSException;

import java.util.List;

@Data
public class SearchResult {
	/**
	 * 从es查询出的商品信息
	 */
	private List<SkuEsModel> products;
	/**
	 * 以下是分页信息
	 */
	private Integer pageNum;//当前页码
	private Long total;//总记录数
	private Integer totalPage;//总页数
	/**
	 * 当前查询到的品牌结果
	 */
	private List<BrandVo> brandVos;

	/**
	 * 当前查询到的商品属性值
	 */
	private List<AttrsVo> attrs;

	/**
	 * 当前查询到的商品分类
	 */
	private List<CatalogVo> catalogs;

	@Data
	public static class BrandVo{
		private Long brandId;
		private String brandName;
		private String brandImg;
	}
	@Data
	public static class AttrsVo{
		private Long attrId;
		private String attrName;
		private List<String> attrs;
	}
	@Data
	public static class CatalogVo{
		private Long catalogId;
		private String catalogName;
	}

}
