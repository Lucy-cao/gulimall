package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    Sequence sequence;
    @Autowired
    SkuLadderService ladderService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveReduction(SkuReductionTo skuReductionTo) {
        //保存折扣和满减信息、会员价格 gulimall_sms->sms_sku_ladder、sms_sku_full_reduction、sms_member_price
        //sms_sku_ladder
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo, ladderEntity);
        ladderEntity.setId(sequence.nextId());
        ladderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (ladderEntity.getFullCount() > 0 && ladderEntity.getDiscount().compareTo(new BigDecimal("0")) > 0) {
            ladderService.save(ladderEntity);
        }

        //sms_sku_full_reduction
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, reductionEntity);
        reductionEntity.setId(sequence.nextId());
        reductionEntity.setAddOther(skuReductionTo.getPriceStatus());
        if (reductionEntity.getFullPrice().compareTo(new BigDecimal(0)) > 0
                && reductionEntity.getReducePrice().compareTo(new BigDecimal(0)) > 0) {
            this.save(reductionEntity);
        }

        //sms_member_price
        List<MemberPriceEntity> memberPriceEntities = skuReductionTo.getMemberPrice().stream()
                .filter(memberPriceTo -> memberPriceTo.getPrice().compareTo(new BigDecimal(0)) > 0)
                .map(price -> {
                    MemberPriceEntity priceEntity = new MemberPriceEntity();
                    priceEntity.setId(sequence.nextId());
                    priceEntity.setSkuId(skuReductionTo.getSkuId());
                    priceEntity.setMemberLevelId(price.getId());
                    priceEntity.setMemberLevelName(price.getName());
                    priceEntity.setMemberPrice(price.getPrice());
                    return priceEntity;
                }).collect(Collectors.toList());
        if (memberPriceEntities.size() > 0) {
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }

}