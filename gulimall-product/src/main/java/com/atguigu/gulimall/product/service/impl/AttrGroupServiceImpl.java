package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRespVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrAttrgroupRelationService attrgroupRelationService;
    @Autowired
    AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryByCatId(Map<String, Object> params, Long catelogId) {
        //构造查询条件
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        //如果分类id不为0，则拼接上查询条件
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        //根据关键字搜索
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(obj -> {
                obj.like("attr_group_name", key).or().like("descript", key);
            });
        }
        //获取实际的返回值
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        //拼接所属分类完整信息
        List<AttrGroupRespVo> respVos = page.getRecords().stream().map(attrGroupEntity -> {
            AttrGroupRespVo respVo = new AttrGroupRespVo();
            BeanUtils.copyProperties(attrGroupEntity, respVo);
            respVo.setCatelogNames(categoryService.getCascaderById(attrGroupEntity.getCatelogId()).getCascaderNames());
            return respVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrGroupEntity getCascaderById(Long attrGroupId) {
        //获取基本信息
        AttrGroupEntity attrGroup = this.getById(attrGroupId);
        //获取分类的级联完整id，并赋值返回
        attrGroup.setCatelogPath(categoryService.getCascaderById(attrGroup.getCatelogId()).getCascaderId());
        return attrGroup;
    }

    @Override
    public List<AttrGroupRespVo> getAttrGroupWithAttrByCatlogId(Long catelogId) {
        //1、根据分类id获取属性分组
        List<AttrGroupEntity> attrGroupEntityList = this.list(Wrappers.lambdaQuery(AttrGroupEntity.class)
                .eq(AttrGroupEntity::getCatelogId, catelogId));

        //2、根据分组信息获取里面的属性信息
        List<AttrGroupRespVo> respVos = attrGroupEntityList.stream().map(item -> {
            AttrGroupRespVo respVo = new AttrGroupRespVo();
            BeanUtils.copyProperties(item, respVo);
            //1、获取属性分组关联的属性
            List<AttrAttrgroupRelationEntity> relation = attrgroupRelationService.list(
                    Wrappers.lambdaQuery(AttrAttrgroupRelationEntity.class)
                            .eq(AttrAttrgroupRelationEntity::getAttrGroupId, item.getAttrGroupId()));
            //2、获取属性的详情
            List<Long> attrIds = relation.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            if (attrIds.size() > 0) {
                respVo.setAttrs(attrDao.selectBatchIds(attrIds));
            }
            return respVo;
        }).collect(Collectors.toList());

        return respVos;
    }
}
