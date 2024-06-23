package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.constant.AttrTypeEnum;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.atguigu.gulimall.product.vo.CategoryCascaderVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
        if (attrVo.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrId(attrEntity.getAttrId());
            relation.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationDao.insert(relation);
        }
    }

    /**
     * 根据属性id获取详情信息
     *
     * @param attrId 属性id
     * @return
     */
    @Override
    public AttrRespVo getDetailById(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        //获取实体基本信息
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);
        //获取完整的分类路径id
        CategoryCascaderVo cascaderById = categoryService.getCascaderById(attrEntity.getCatelogId());
        respVo.setCatelogPath(cascaderById.getCascaderId());
        //获取关联的分组id
        AttrAttrgroupRelationEntity relation = attrAttrgroupRelationDao.selectOne(
                Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                        .eq(AttrAttrgroupRelationEntity::getAttrId, attrId));
        if (relation != null) {
            respVo.setAttrGroupId(relation.getAttrGroupId());
        }
        return respVo;
    }

    /**
     * 修改属性，和关联的属性分组
     *
     * @param attrVo
     */
    @Override
    public void updateDetailById(AttrVo attrVo) {
        //修改属性的基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        //修改关联的属性分组信息
        //判断是否有关联数据
        AttrAttrgroupRelationEntity relation = attrAttrgroupRelationDao.selectOne(Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                .eq(AttrAttrgroupRelationEntity::getAttrId, attrVo.getAttrId()));
        if (relation != null) {//有关联数据
            if (attrVo.getAttrGroupId() != null) {
                //有分组id，需要更新
                relation.setAttrGroupId(attrVo.getAttrGroupId());
                attrAttrgroupRelationDao.updateById(relation);
            } else {
                //没有分组id，需要删除
                attrAttrgroupRelationDao.deleteById(relation);
            }
        } else {//没有关联数据
            if (attrVo.getAttrGroupId() != null) {
                //有分组id，需要新增
                relation = new AttrAttrgroupRelationEntity();
                relation.setAttrId(attrVo.getAttrId());
                relation.setAttrGroupId(attrVo.getAttrGroupId());
                attrAttrgroupRelationDao.insert(relation);
            }
            //没有分组id，无需操作
        }
    }

    /**
     * 获取属性分组关联的属性
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelatedAttr(Long attrGroupId) {
        //获取属性分组关联的属性id
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(
                Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId));
        if (relations == null || relations.size() == 0) {
            return null;
        }
        List<Long> collect = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        //根据属性id获取属性对象
        List<AttrEntity> attrEntities = this.listByIds(collect);
        return attrEntities;
    }

    /**
     * 批量删除属性和属性分组的关系
     *
     * @param relationVos 待删除的属性和属性分组id列表
     */
    @Override
    public void deleteRelation(List<AttrGroupRelationVo> relationVos) {
        attrAttrgroupRelationDao.deleteRelations(relationVos);
    }

    /**
     * 获取当前属性分组未关联的属性
     *
     * @param params
     * @param attrGroupId
     * @return
     */
    @Override
    public PageUtils getNoRelatedAttr(Map<String, Object> params, Long attrGroupId) {
        //1、获取当前属性分组所在的分类
        AttrGroupEntity groupEntity = attrGroupDao.selectById(attrGroupId);
        Long catlogId = groupEntity.getCatelogId();
        //2、获取当前分类下所有的属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(Wrappers.lambdaQuery(AttrGroupEntity.class)
                .eq(AttrGroupEntity::getCatelogId, catlogId));
        List<Long> attrGroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //3、获取所有属性分组已关联的属性
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
        List<Long> relatedAttrIds = new ArrayList<>();
        if (relations != null) {
            relatedAttrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        }
        //4、获取当前分类下，不在已关联范围的其他属性
        LambdaQueryWrapper<AttrEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AttrEntity::getCatelogId, catlogId).eq(AttrEntity::getAttrType, AttrTypeEnum.BASE_ATTR.getCode());
        if (relatedAttrIds.size() > 0) {
            wrapper.notIn(AttrEntity::getAttrId, relatedAttrIds);
        }
        //根据关键词查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like(AttrEntity::getAttrName, key);
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}
