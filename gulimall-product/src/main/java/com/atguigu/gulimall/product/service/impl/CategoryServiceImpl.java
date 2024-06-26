package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.vo.CategoryCascaderVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、获取所有的商品类型
        List<CategoryEntity> entities = this.list();
        //2、将商品种类按照树形结构组装
        //1)、获取根节点
        List<CategoryEntity> rootMenu = entities.stream().filter(item -> {
                    return item.getParentCid() == 0;
                }).map(root -> {
                    //2)、使用递归获取子节点
                    root.setChildren(getChildren(root, entities));
                    return root;
                })
                //进行排序
                .sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());
        return rootMenu;
    }

    @Override
    public void removeMenusById(List<Long> asList) {
        //TODO: 删除前需要校验菜单是否被引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public CategoryCascaderVo getCascaderById(Long catId) {
        //获取级联分类
        List<Long> catelogIds = new ArrayList<>();
        List<String> catelogNames = new ArrayList<>();
        Long currentCatlogId = catId;
        while (!currentCatlogId.equals(0L)) {
            //将当前id放进去
            catelogIds.add(0, currentCatlogId);
            CategoryEntity category = this.getById(currentCatlogId);
            catelogNames.add(0, category.getName());
            //将父级id赋值给当前id
            currentCatlogId = category.getParentCid();
        }
        //返回数据
        CategoryCascaderVo cascaderVo = new CategoryCascaderVo();
        cascaderVo.setCascaderId(catelogIds);
        cascaderVo.setCascaderNames(String.join("/", catelogNames));
        return cascaderVo;
    }

    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        //更新详情，同时更新关联的冗余表
        this.updateById(category);
        categoryBrandRelationDao.updateCategory(category.getCatId(), category.getName());
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        //获取当前菜单的子菜单
        List<CategoryEntity> childrenMenus = all.stream().filter(item -> Objects.equals(item.getParentCid(), root.getCatId()))
                .map(menu -> {
                    //子菜单递归获取各自的子菜单
                    menu.setChildren(getChildren(menu, all));
                    return menu;
                }).sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());

        return childrenMenus;
    }

}
