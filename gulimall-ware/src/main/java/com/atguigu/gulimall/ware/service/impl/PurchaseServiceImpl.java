package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.exception.RRException;
import com.atguigu.gulimall.ware.constant.PurchaseConstant;
import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.vo.MergePurchaseVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailDao purchaseDetailDao;
    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    Sequence sequence;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(PurchaseEntity::getStatus, PurchaseConstant.PurchaseStatusEnum.NEW.getCode())
                .or().eq(PurchaseEntity::getStatus, PurchaseConstant.PurchaseStatusEnum.ALLOCATED.getCode());

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Transactional
    @Override
    public void mergePurchaseDetail(MergePurchaseVo mergeVo) {
        //1、查询出待合并的采购需求
        List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailDao.selectBatchIds(mergeVo.getItems());
        //2、验证采购需求：是否已被合并、是否属于同个仓库
        List<PurchaseDetailEntity> purchaseExist = purchaseDetailEntities.stream()
                .filter(detail -> detail.getPurchaseId() != null && detail.getPurchaseId() != 0).collect(Collectors.toList());
        if (purchaseExist.size() > 0) {
            throw new RRException("已存在采购需求被合并，请重新选择");
        }
        List<Long> wareIds = purchaseDetailEntities.stream().map(PurchaseDetailEntity::getWareId).distinct().collect(Collectors.toList());
        if (wareIds.size() > 1) {
            throw new RRException("只能合并同个仓库的采购需求，请重新选择");
        }
        //计算采购需求的总金额
        BigDecimal amount = purchaseDetailEntities.stream().map(PurchaseDetailEntity::getSkuPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        PurchaseEntity purchase = null;
        if (mergeVo.getPurchaseId() != null) {
            //1、如果有采购单id，则根据最新的数据合并需求
            purchase = this.getById(mergeVo.getPurchaseId());
            if (purchase.getWareId() != null && purchase.getWareId() != wareIds.get(0)) {
                throw new RRException("所选采购单与采购需求的仓库不一致，请重新选择");
            }
            purchase.setWareId(wareIds.get(0));
            purchase.setAmount(amount.add(purchase.getAmount()));
            purchase.setUpdateTime(new Date());
            this.updateById(purchase);
        } else {
            //2、如果没有采购单id，则创建采购单并保存
            purchase = new PurchaseEntity();
            purchase.setId(sequence.nextId());
            purchase.setWareId(wareIds.get(0));
            purchase.setAmount(amount);
            purchase.setStatus(PurchaseConstant.PurchaseStatusEnum.NEW.getCode());
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            this.save(purchase);
        }

        //更新采购需求
        PurchaseEntity finalPurchase = purchase;
        int isAllocated = purchase.getAssigneeId() == null ? PurchaseConstant.PurchaseDetailStatusEnum.NEW.getCode()
                : PurchaseConstant.PurchaseDetailStatusEnum.ALLOCATED.getCode();
        List<PurchaseDetailEntity> detailEntities = purchaseDetailEntities.stream().map(purDetail -> {
            purDetail.setPurchaseId(finalPurchase.getId());
            purDetail.setStatus(isAllocated);
            return purDetail;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(detailEntities);
    }


}