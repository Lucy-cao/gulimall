package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.atguigu.common.exception.RRException;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSaveServiceImpl implements ProductSaveService {
	@Autowired
	ElasticsearchClient esClient;

	@Override
	public void skuUp(List<SkuEsModel> esModels) {
		//使用bulk批量添加数据
		BulkRequest.Builder builder = new BulkRequest.Builder();
		for (SkuEsModel model : esModels) {
			builder.operations(op -> op
					.index(idx -> idx
							.index(EsConstant.PRODUCT_INDEX)
							.id(model.getSkuId().toString())
							.document(model)
					)
			);
		}
		try {
			BulkResponse bulk = esClient.bulk(builder.build());
			//返回批量上架是否存在错误
			if (bulk.errors()) {
				List<String> errors = bulk.items().stream().filter(item -> item.error() != null)
						.map(item -> item.error().reason()).collect(Collectors.toList());
				throw new RRException("商品上架失败：" + errors);
			}
		} catch (Exception e) {
			throw new RRException(e.getMessage());
		}
	}
}
