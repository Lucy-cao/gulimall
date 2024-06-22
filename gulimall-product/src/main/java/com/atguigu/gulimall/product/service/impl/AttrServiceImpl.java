package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.constant.AttrTypeEnum;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.w3c.dom.Attr;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据分类id查询规格参数
     *
     * @param params
     * @param catId
     * @return
     */
    @Override
    public PageUtils queryPageByCatId(Map<String, Object> params, Long catId, Integer attrType) {
        //构造查询条件
        LambdaQueryWrapper<AttrEntity> wrapper = Wrappers.lambdaQuery();
        if (!Objects.equals(attrType, AttrTypeEnum.BOTH_ATTR.getCode()))
            wrapper.in(AttrEntity::getAttrType, Arrays.asList(attrType, AttrTypeEnum.BOTH_ATTR.getCode()));

        if (!catId.equals(0L)) {
            wrapper.eq(AttrEntity::getCatelogId, catId);
        }
        //拼接关键词查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like(AttrEntity::getAttrName, key);
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}
