package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:40
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCatId(Map<String, Object> params, Long catId, Integer attrType);

    void saveDetail(AttrVo attrVo);

    AttrRespVo getDetailById(Long attrId);

    void updateDetailById(AttrVo attr);

    List<AttrEntity> getRelatedAttr(Long attrGroupId);

    void deleteRelation(List<AttrGroupRelationVo> relationVos);

    PageUtils getNoRelatedAttr(Map<String, Object> params, Long attrGroupId);
}

