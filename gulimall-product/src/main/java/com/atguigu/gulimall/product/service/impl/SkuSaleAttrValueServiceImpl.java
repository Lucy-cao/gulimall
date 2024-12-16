package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.vo.sku.SkuItemVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuSaleAttrValueEntity> page = this.page(
				new Query<SkuSaleAttrValueEntity>().getPage(params),
				new QueryWrapper<SkuSaleAttrValueEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<SkuItemVo.SkuSaleAttrVo> getSaleAttrBySpuId(Long spuId) {
		List<SkuItemVo.SkuSaleAttrVo> saleAttrs = this.baseMapper.getSaleAttrBySpuId(spuId);
		return saleAttrs;
	}

	@Override
	public List<String> getSaleAttrList(Long skuId) {
		return this.baseMapper.getSaleAttrList(skuId);
	}

}
