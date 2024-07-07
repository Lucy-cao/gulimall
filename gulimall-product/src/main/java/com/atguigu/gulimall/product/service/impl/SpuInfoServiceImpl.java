package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.exception.RRException;
import com.atguigu.common.to.MemberPriceTo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SpuInfoVo;
import com.atguigu.gulimall.product.vo.sku.BaseAttrs;
import com.atguigu.gulimall.product.vo.sku.Images;
import com.atguigu.gulimall.product.vo.sku.SpuSaveVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    Sequence sequence;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrDao attrDao;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = this.saveBaseSpuInfo(spuSaveVo);

        //2、保存spu商品介绍信息 pms_spu_info_desc
        this.saveSpuDesc(spuInfoEntity, spuSaveVo);

        //3、保存spu商品图集 pms_spu_images
        this.saveSpuImages(spuInfoEntity, spuSaveVo);

        //4、保存商品的规格参数 pms_product_attr_value
        this.saveProductAttrValue(spuInfoEntity, spuSaveVo);

        //5、保存商品的积分信息 gulimall_sms->sms_spu_bounds
        this.saveSpuBounds(spuInfoEntity, spuSaveVo);

        //6、保存sku的信息
        spuSaveVo.getSkus().forEach(sku -> {
            //获取默认图片
            List<Images> defaultImage = sku.getImages().stream().filter(image -> image.getDefaultImg() == 1).collect(Collectors.toList());
            //1）保存sku的基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, skuInfoEntity);
            skuInfoEntity.setSkuId(sequence.nextId());
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSkuDesc("");
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setSkuDefaultImg(defaultImage.size() > 0 ? defaultImage.get(0).getImgUrl() : "");
            skuInfoEntity.setSaleCount(0L);
            skuInfoService.save(skuInfoEntity);

            //2）保存sku的销售属性 pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = sku.getAttr().stream().map(saleAttr -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(saleAttr, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setId(sequence.nextId());
                skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            if (skuSaleAttrValueEntities.size() > 0) {
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
            }
            //3）保存sku的图片信息 pms_sku_images
            List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream()
                    .filter(img -> !Objects.equals(img.getImgUrl(), ""))
                    .map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(image, skuImagesEntity);
                        skuImagesEntity.setId(sequence.nextId());
                        skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
            if (skuImagesEntities.size() > 0) {
                skuImagesService.saveBatch(skuImagesEntities);
            }
            //4）保存折扣和满减信息、会员价格 gulimall_sms->sms_sku_ladder、sms_sku_full_reduction、sms_member_price
            SkuReductionTo reductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(sku, reductionTo);
            reductionTo.setSkuId(skuInfoEntity.getSkuId());
            List<MemberPriceTo> priceToList = sku.getMemberPrice().stream()
                    .filter(memberPrice -> memberPrice.getPrice().compareTo(new BigDecimal("0")) > 0)
                    .map(price -> {
                        MemberPriceTo memberPriceTo = new MemberPriceTo();
                        BeanUtils.copyProperties(price, memberPriceTo);
                        return memberPriceTo;
                    }).collect(Collectors.toList());
            reductionTo.setMemberPrice(priceToList);
            R response = couponFeignService.saveSkuReduction(reductionTo);
            if (response.getCode() != 0) {
                throw new RRException("保存满减信息有问题，请联系管理员");
            }
        });
    }

    @Override
    public SpuInfoEntity saveBaseSpuInfo(SpuSaveVo spuSaveVo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setId(sequence.nextId());
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        return spuInfoEntity;
    }

    @Override
    public void saveSpuDesc(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo) {
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", spuSaveVo.getDecript()));
        spuInfoDescService.save(spuInfoDescEntity);
    }

    @Override
    public void saveSpuImages(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo) {
        if (spuSaveVo.getImages() == null || spuSaveVo.getImages().size() == 0)
            return;
        List<SpuImagesEntity> imageList = spuSaveVo.getImages().stream().map(image -> {
            SpuImagesEntity imageEntity = new SpuImagesEntity();
            imageEntity.setId(sequence.nextId());
            imageEntity.setSpuId(spuInfoEntity.getId());
            imageEntity.setImgUrl(image);
            return imageEntity;
        }).collect(Collectors.toList());
        spuImagesService.saveBatch(imageList);
    }

    @Override
    public void saveProductAttrValue(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo) {
        if (spuSaveVo.getBaseAttrs() == null || spuSaveVo.getBaseAttrs().size() == 0)
            return;
        //1、获取属性信息
        List<Long> attrIds = spuSaveVo.getBaseAttrs().stream().map(BaseAttrs::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
        Map<Long, String> attrMap = new HashMap<>();
        attrEntities.stream().map(attr -> {
            attrMap.put(attr.getAttrId(), attr.getAttrName());
            return null;
        }).collect(Collectors.toList());
        //2、保存商品规格参数的属性信息
        List<ProductAttrValueEntity> baseAttrList = spuSaveVo.getBaseAttrs().stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(baseAttr, productAttrValueEntity);
            productAttrValueEntity.setId(sequence.nextId());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrName(attrMap.get(baseAttr.getAttrId()));
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(baseAttrList);
    }

    @Override
    public void saveSpuBounds(SpuInfoEntity spuInfoEntity, SpuSaveVo spuSaveVo) {
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R response = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (response.getCode() != 0) {
            throw new RRException("保存积分信息有问题，请联系管理员");
        }
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        //构造查询条件
        LambdaQueryWrapper<SpuInfoEntity> wrapper = Wrappers.lambdaQuery();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.like(SpuInfoEntity::getSpuName, key);
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }

        //进行数据查询
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<SpuInfoVo> spuInfoVos = page.getRecords().stream().map(spuInfo -> {
            SpuInfoVo spuInfoVo = new SpuInfoVo();
            BeanUtils.copyProperties(spuInfo, spuInfoVo);
            spuInfoVo.setCascaderNames(categoryService.getCascaderById(spuInfo.getCatalogId()).getCascaderNames());
            spuInfoVo.setBrandName(brandService.getById(spuInfo.getBrandId()).getName());
            return spuInfoVo;
        }).collect(Collectors.toList());
        pageUtils.setList(spuInfoVos);
        return pageUtils;
    }

    @Override
    public void spuUp(Long spuId) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
        spuInfoEntity.setUpdateTime(new Date());
        this.updateById(spuInfoEntity);
    }

}
