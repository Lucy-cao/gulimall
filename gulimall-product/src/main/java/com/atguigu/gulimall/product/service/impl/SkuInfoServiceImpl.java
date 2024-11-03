package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuInfoVo;
import com.atguigu.gulimall.product.vo.sku.SkuItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
	@Autowired
	CategoryService categoryService;
	@Autowired
	BrandService brandService;
	@Autowired
	SkuImagesService skuImagesService;
	@Autowired
	SpuInfoDescService spuInfoDescService;
	@Autowired
	AttrGroupService attrGroupService;
	@Autowired
	SkuSaleAttrValueService skuSaleAttrValueService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuInfoEntity> page = this.page(
				new Query<SkuInfoEntity>().getPage(params),
				new QueryWrapper<SkuInfoEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params) {
		//构造查询条件
		LambdaQueryWrapper<SkuInfoEntity> wrapper = Wrappers.lambdaQuery();
		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			wrapper.like(SkuInfoEntity::getSkuName, key);
		}
		String catelogId = (String) params.get("catelogId");
		if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
			wrapper.like(SkuInfoEntity::getCatalogId, catelogId);
		}
		String brandId = (String) params.get("brandId");
		if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
			wrapper.like(SkuInfoEntity::getBrandId, brandId);
		}
		String min = (String) params.get("min");
		String max = (String) params.get("max");
		if (!StringUtils.isEmpty(min) && (new BigDecimal(min).compareTo(new BigDecimal("0")) > 0)) {
			wrapper.ge(SkuInfoEntity::getPrice, new BigDecimal(min));
		}
		if (!StringUtils.isEmpty(max) && (new BigDecimal(max).compareTo(new BigDecimal("0")) > 0)) {
			wrapper.le(SkuInfoEntity::getPrice, new BigDecimal(max));
		}

		//执行查询语句，转换为输出的格式
		IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
		PageUtils pageUtils = new PageUtils(page);
		List<SkuInfoVo> skuInfoVos = page.getRecords().stream().map(skuInfoEntity -> {
			SkuInfoVo skuInfoVo = new SkuInfoVo();
			BeanUtils.copyProperties(skuInfoEntity, skuInfoVo);
			skuInfoVo.setCascaderNames(categoryService.getCascaderById(skuInfoEntity.getCatalogId()).getCascaderNames());
			skuInfoVo.setBrandName(brandService.getById(skuInfoEntity.getBrandId()).getName());
			return skuInfoVo;
		}).collect(Collectors.toList());
		pageUtils.setList(skuInfoVos);
		return pageUtils;
	}

	@Override
	public SkuItemVo getItemBySkuId(Long skuId) {
		SkuItemVo skuItemVo = new SkuItemVo();
		//1、sku的基本信息 pms_sku_info
		SkuInfoEntity skuInfo = this.getById(skuId);
		skuItemVo.setSkuInfo(skuInfo);
		Long spuId = skuInfo.getSpuId();
		Long catalogId = skuInfo.getCatalogId();

		//2、sku的图片信息 pms_sku_images
		List<SkuImagesEntity> images = skuImagesService.list(Wrappers.lambdaQuery(SkuImagesEntity.class)
				.eq(SkuImagesEntity::getSkuId, skuId));
		skuItemVo.setImages(images);

		//3、spu的销售属性 pms_sku_sale_attr_value
		List<SkuItemVo.SkuSaleAttrVo> skuSaleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(spuId);
		skuItemVo.setSaleAttrs(skuSaleAttrVos);

		//4、spu的介绍信息 pms_spu_info_desc
		SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(spuId);
		skuItemVo.setSpuInfoDesc(spuInfoDesc);

		//5、spu的规格包装 pms_product_attr_value
		List<SkuItemVo.SpuAttrGroupVo> spuAttrGroupVos = attrGroupService.getSpuGroupAttrs(catalogId, spuId);
		skuItemVo.setAttrGroups(spuAttrGroupVos);

		return skuItemVo;
	}

}
