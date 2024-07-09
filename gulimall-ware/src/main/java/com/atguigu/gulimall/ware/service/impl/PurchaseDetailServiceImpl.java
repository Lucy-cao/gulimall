package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.gulimall.ware.service.WareInfoService;
import com.atguigu.gulimall.ware.vo.PurchaseDetailVo;
import com.atguigu.gulimall.ware.vo.WareSkuVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {
    @Autowired
    WareInfoService wareInfoService;
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = Wrappers.lambdaQuery();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq(PurchaseDetailEntity::getPurchaseId, key);
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId) && !"0".equals(wareId)) {
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<PurchaseDetailVo> collect = page.getRecords().stream().map(entity -> {
            PurchaseDetailVo detailVo = new PurchaseDetailVo();
            BeanUtils.copyProperties(entity, detailVo);
            detailVo.setWareName(wareInfoService.getById(entity.getWareId()).getName());
            return detailVo;
        }).collect(Collectors.toList());
        pageUtils.setList(collect);
        return pageUtils;
    }

}