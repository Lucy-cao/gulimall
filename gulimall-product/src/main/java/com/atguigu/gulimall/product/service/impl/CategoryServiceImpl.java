package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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
                .sorted((menu1, menu2) -> (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()))
                .collect(Collectors.toList());
        return rootMenu;
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        //获取当前菜单的子菜单
        List<CategoryEntity> childrenMenus = all.stream().filter(item -> Objects.equals(item.getParentCid(), root.getCatId()))
                .map(menu -> {
                    //子菜单递归获取各自的子菜单
                    menu.setChildren(getChildren(menu, all));
                    return menu;
                }).sorted((menu1, menu2) ->  (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()))
                .collect(Collectors.toList());

        return childrenMenus;
    }

}
