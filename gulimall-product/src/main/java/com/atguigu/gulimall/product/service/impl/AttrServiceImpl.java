package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.constant.AttrTypeEnum;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryService categoryService;

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
        PageUtils pageUtils = new PageUtils(page);
        //获取分类和分组信息
        List<AttrEntity> attrEntities = page.getRecords();
        List<AttrRespVo> respVos = attrEntities.stream().map(attr -> {
            AttrRespVo respVo = new AttrRespVo();
            BeanUtils.copyProperties(attr, respVo);
            //获取分组信息
            AttrAttrgroupRelationEntity relation = attrAttrgroupRelationDao.selectOne(
                    Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
            if (relation != null) {
                AttrGroupEntity groupEntity = attrGroupDao.selectById(relation.getAttrGroupId());
                respVo.setGroupName(groupEntity.getAttrGroupName());
            }

            //获取分类信息
            respVo.setCatelogName(categoryService.getCascaderById(attr.getCatelogId()).getCascaderNames());
            return respVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public void saveDetail(AttrVo attrVo) {
        //保存属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);
        //保存关联数据
        AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
        relation.setAttrId(attrEntity.getAttrId());
        relation.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationDao.insert(relation);
    }

}
