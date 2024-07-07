package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.sku.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:39
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    SpuInfoEntity saveBaseSpuInfo(SpuSaveVo spuSaveVo);

    void saveSpuDesc(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo);

    void saveSpuImages(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo);

    void saveProductAttrValue(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo);

    void saveSpuBounds(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void spuUp(Long spuId);
}

