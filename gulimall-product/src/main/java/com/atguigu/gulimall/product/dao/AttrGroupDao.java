package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.vo.sku.SkuItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:39
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

	List<SkuItemVo.SpuAttrGroupVo> getSpuGroupAttrs(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}
