package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.SkuInfoVo;
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
import com.atguigu.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

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

}
