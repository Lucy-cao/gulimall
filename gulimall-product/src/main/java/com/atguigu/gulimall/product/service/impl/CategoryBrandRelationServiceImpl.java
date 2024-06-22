package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.exception.RRException;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> queryByBrandId(Long brandId) {
        //根据品牌id查询关联分类信息
        return this.list(Wrappers.lambdaQuery(CategoryBrandRelationEntity.class)
                .eq(CategoryBrandRelationEntity::getBrandId, brandId));
    }

    @Override
    public void saveRelation(CategoryBrandRelationEntity categoryBrandRelation) {
        //查询品牌和分类的关联是否已存在，已存在，无需新增
        CategoryBrandRelationEntity relation = this.getOne(Wrappers.lambdaQuery(CategoryBrandRelationEntity.class)
                .eq(CategoryBrandRelationEntity::getBrandId, categoryBrandRelation.getBrandId())
                .eq(CategoryBrandRelationEntity::getCatelogId, categoryBrandRelation.getCatelogId()));
        if (relation != null) {
            throw new RRException("关联关系已存在，不可重复新增");
        }
        //获取品牌信息
        BrandEntity brand = brandService.getById(categoryBrandRelation.getBrandId());
        //获取分类信息
        CategoryEntity category = categoryService.getById(categoryBrandRelation.getCatelogId());

        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());
        this.save(categoryBrandRelation);
    }
}
