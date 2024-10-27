package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.SourceConfigBuilders;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	private ElasticsearchClient esClient;

	@Override
	public SearchResult search(SearchParam param) {
		//1、构造es请求
		SearchRequest searchRequest = buildSearchRequest(param);
		System.out.println("searchRequest = " + searchRequest);
		SearchResult result = null;
		try {
			//2、执行搜索方法
			SearchResponse<SkuEsModel> response = esClient.search(searchRequest, SkuEsModel.class);
			System.out.println("response = " + response);
			//3、搜索结果封装成所需的结果
			result = buildSearchResult(response, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private SearchResult buildSearchResult(SearchResponse<SkuEsModel> response, SearchParam param) {
		SearchResult result = new SearchResult();
		//1、返回所有查询到的商品，如果有关键词，则获取高亮的结果
		List<SkuEsModel> products = response.hits().hits().stream().map(hit->{
			SkuEsModel esModel = hit.source();
			//判断是否需要获取高亮的标题
			if(!StringUtils.isEmpty(param.getKeyword())){
				String skuTitle = hit.highlight().get("skuTitle").get(0);
				esModel.setSkuTitle(skuTitle);
			}
			return esModel;
		}).collect(Collectors.toList());
		result.setProducts(products);

		//2、返回所有商品涉及到的属性信息
		NestedAggregate attrAgg = response.aggregations().get("attr_agg").nested();
		LongTermsAggregate attrIdAgg = attrAgg.aggregations().get("attr_id_agg").lterms();
		List<LongTermsBucket> attrBuckets = attrIdAgg.buckets().array();
		List<SearchResult.AttrsVo> attrsVos = attrBuckets.stream().map(bucket -> {
			SearchResult.AttrsVo vo = new SearchResult.AttrsVo();
			//设置属性id
			vo.setAttrId(Long.valueOf(bucket.key()));
			//设置属性名
			String attrName = bucket.aggregations().get("attr_name_agg").sterms()
					.buckets().array().get(0).key().stringValue();
			vo.setAttrName(attrName);
			//设置属性值
			List<String> attrValues = bucket.aggregations().get("attr_values_agg").sterms()
					.buckets().array().stream().map(b -> b.key().stringValue()).collect(Collectors.toList());
			vo.setAttrs(attrValues);
			return vo;
		}).collect(Collectors.toList());
		result.setAttrs(attrsVos);

		//3、返回所有商品涉及到的品牌信息
		List<LongTermsBucket> brandAgg = response.aggregations().get("brand_agg").lterms().buckets().array();
		List<SearchResult.BrandVo> brandVos = brandAgg.stream().map(bucket -> {
			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
			//获取品牌id
			brandVo.setBrandId(Long.valueOf(bucket.key()));
			//获取品牌名
			String brandName = bucket.aggregations().get("brand_name_agg").sterms()
					.buckets().array().get(0).key().stringValue();
			brandVo.setBrandName(brandName);
			//获取品牌Logo
			String brandImg = bucket.aggregations().get("brand_img_agg").sterms()
					.buckets().array().get(0).key().stringValue();
			brandVo.setBrandImg(brandImg);
			return brandVo;
		}).collect(Collectors.toList());
		result.setBrandVos(brandVos);

//		//4、返回所有商品的分类信息
		List<LongTermsBucket> catalogAgg = response.aggregations().get("catalog_agg").lterms().buckets().array();
		List<SearchResult.CatalogVo> catalogVos = catalogAgg.stream().map(bucket -> {
			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
			//获取分类id
			catalogVo.setCatalogId(Long.valueOf(bucket.key()));
			//获取分类值
			String catalogName = bucket.aggregations().get("catalog_name_agg").sterms()
					.buckets().array().get(0).key().stringValue();
			catalogVo.setCatalogName(catalogName);
			return catalogVo;
		}).collect(Collectors.toList());
		result.setCatalogs(catalogVos);
//		//5、分页信息-当前页码
		result.setPageNum(param.getPageNum());
//		//5、分页信息-总记录数
		Long total = response.hits().total().value();
		result.setTotal(total);
//		//5、分页信息-总页数
		int totalPage = (int) Math.ceil((double) total / (double) EsConstant.PRODUCT_PAGESIZE);
		result.setTotalPage(totalPage);

		return result;
	}

	private SearchRequest buildSearchRequest(SearchParam param) {
		SearchRequest.Builder searchBuilder = new SearchRequest.Builder();
		searchBuilder.index(EsConstant.PRODUCT_INDEX);
		//构建bool查询
		BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
		if (!StringUtils.isEmpty(param.getKeyword())) {
			//模糊匹配：关键字
			boolBuilder.must(mu -> mu.match(m -> m.field("skuTitle").query(param.getKeyword())));
		}
		//精确匹配：类别、品牌、是否有货、属性、价格区间
		if (param.getCatalog3Id() != null && param.getCatalog3Id() != 0) {
			boolBuilder.filter(f -> f.term(t -> t.field("catalogId").value(param.getCatalog3Id())));
		}
		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
			boolBuilder.filter(f -> f.terms(t -> t.field("brandId").terms(TermsQueryField.of(tf -> tf.value(
					param.getBrandId().stream().map(FieldValue::of).collect(Collectors.toList()))
			))));
		}
		if (param.getHasStock() != null) {
			boolBuilder.filter(f -> f.term(t -> t.field("hasStock").value(param.getHasStock() == 1)));
		}
		if (!StringUtils.isEmpty(param.getSkuPrice())) {
			List<String> skuRange = Arrays.asList(param.getSkuPrice().split("_"));
			RangeQuery.Builder builder = new RangeQuery.Builder().field("skuPrice")
					.gte(JsonData.of(Objects.equals(skuRange.get(0), "") ? 0 : skuRange.get(0)));
			if (skuRange.size() == 2) {
				builder.lte(JsonData.of(skuRange.get(1)));
			}
			boolBuilder.filter(f -> f.range(builder.build()));
		}
		if (!Objects.equals(param.getAttrs(), null) && param.getAttrs().size() > 0) {
			//拼接嵌套属性的查询条件 attrs=1_华为:小米&attrs=2_白色:黑色
			param.getAttrs().forEach(attr -> {
				String[] split = attr.split("_");
				Query nestedTerm = new Query.Builder().term(t -> t.field("attrs.attrId").value(split[0])).build();
				Query nestedTerms = new Query.Builder().terms(t -> t.field("attrs.attrValue").terms(TermsQueryField.of(tf -> tf.value(
						Arrays.stream(split[1].split(":")).map(FieldValue::of).collect(Collectors.toList()))
				))).build();
				boolBuilder.filter(f -> f.nested(n -> n.path("attrs").query(q -> q.bool(b -> b.must(nestedTerm, nestedTerms)))));
			});
		}
		searchBuilder.query(q -> q.bool(boolBuilder.build()));

		// 排序、分页、高亮
		//排序
		if (!StringUtils.isEmpty(param.getSort())) {
			String[] sortSplit = param.getSort().split("_");
			searchBuilder.sort(s -> s.field(f -> f.field(sortSplit[0]).order(Objects.equals(sortSplit[1], "asc") ? SortOrder.Asc : SortOrder.Desc)));
		}
		//分页
		searchBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		searchBuilder.size(EsConstant.PRODUCT_PAGESIZE);
		//高亮
		if (!StringUtils.isEmpty(param.getKeyword())) {
			searchBuilder.highlight(h -> h.fields("skuTitle", HighlightField.of(f -> f.preTags("<b style='color:red'>").postTags("</b>"))));
		}

		//聚合分析
		Map<String, Aggregation> aggMap = new HashMap<>();
		//构造品牌聚合
		Map<String, Aggregation> brandSubAggMap = new HashMap<>();
		brandSubAggMap.put("brand_name_agg", Aggregation.of(a -> a.terms(t -> t.field("brandName").size(10))));
		brandSubAggMap.put("brand_img_agg", Aggregation.of(a -> a.terms(t -> t.field("brandImg").size(10))));
		aggMap.put("brand_agg", Aggregation.of(a -> a.terms(t ->
				t.field("brandId").size(50)).aggregations(brandSubAggMap)));
		//构造分类聚合
		Aggregation catalogNameAgg = Aggregation.of(a -> a.terms(t -> t.field("catalogName").size(10)));
		aggMap.put("catalog_agg", Aggregation.of(a -> a.terms(t -> t.field("catalogId").size(10))
				.aggregations("catalog_name_agg", catalogNameAgg)));
		//构造属性的聚合
		Map<String, Aggregation> attrAggMap = new HashMap<>();
		attrAggMap.put("attr_name_agg", Aggregation.of(a -> a.terms(t -> t.field("attrs.attrName").size(10))));
		attrAggMap.put("attr_values_agg", Aggregation.of(a -> a.terms(t -> t.field("attrs.attrValue").size(50))));
		aggMap.put("attr_agg", Aggregation.of(a -> a.nested(n -> n.path("attrs")).aggregations("attr_id_agg",
				attr -> attr.terms(te -> te.field("attrs.attrId").size(10)).aggregations(attrAggMap))));

		searchBuilder.aggregations(aggMap);
		SearchRequest searchRequest = searchBuilder.build();
		return searchRequest;
	}
}
