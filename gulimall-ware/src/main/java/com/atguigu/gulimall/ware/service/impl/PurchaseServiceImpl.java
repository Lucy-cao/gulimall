package com.atguigu.gulimall.ware.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.atguigu.common.exception.RRException;
import com.atguigu.common.to.SkuInfoTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.constant.PurchaseConstant;
import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareInfoService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergePurchaseVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    Sequence sequence;
    @Autowired
    PurchaseDetailDao purchaseDetailDao;
    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareInfoService wareInfoService;
    @Autowired
    PurchaseDao purchaseDao;
    @Autowired
    WareSkuService wareSkuService;
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> wrapper = Wrappers.lambdaQuery();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq(PurchaseEntity::getId, key).or().like(PurchaseEntity::getAssigneeName, key);
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq(PurchaseEntity::getStatus, status);
        }

        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<PurchaseVo> collect = page.getRecords().stream().map(purchase -> {
            PurchaseVo purchaseVo = new PurchaseVo();
            BeanUtils.copyProperties(purchase, purchaseVo);
            purchaseVo.setWareName(wareInfoService.getById(purchase.getWareId()).getName());
            return purchaseVo;
        }).collect(Collectors.toList());
        pageUtils.setList(collect);
        return pageUtils;
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

    @Transactional
    @Override
    public void allocateUser(PurchaseEntity purchase) {
        //更新采购单的分配人员和状态
        this.updateById(purchase);
        //更新关联的采购需求的状态
        List<PurchaseDetailEntity> detailList = purchaseDetailService.list(Wrappers.lambdaQuery(PurchaseDetailEntity.class)
                .eq(PurchaseDetailEntity::getPurchaseId, purchase.getId()));
        List<PurchaseDetailEntity> collect = detailList.stream().map(detail -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(detail.getId());
            purchaseDetailEntity.setStatus(PurchaseConstant.PurchaseDetailStatusEnum.ALLOCATED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
    }

    @Transactional
    @Override
    public void received(List<Long> purchaseIds) {
        //1、获取采购单信息，校验采购单状态是新建或已分配，其他情况不可领取
        List<PurchaseEntity> purchaseEntities = purchaseDao.selectBatchIds(purchaseIds);
        List<PurchaseEntity> collect = purchaseEntities.stream().filter(x ->
                x.getStatus() != PurchaseConstant.PurchaseStatusEnum.NEW.getCode() &&
                        x.getStatus() != PurchaseConstant.PurchaseStatusEnum.ALLOCATED.getCode()).collect(Collectors.toList());
        if (collect.size() > 0) {
            throw new RRException("只能领取新建或已分配状态的采购单，请重新选择");
        }

        //2、更新采购单状态为已领取
        List<PurchaseEntity> entities = purchaseEntities.stream().map(purchase -> {
            purchase.setStatus(PurchaseConstant.PurchaseStatusEnum.GOTTED.getCode());
            purchase.setUpdateTime(new Date());
            return purchase;
        }).collect(Collectors.toList());
        this.updateBatchById(entities);

        //3、更新采购需求的状态为已领取
        entities.forEach(purchase -> {
            List<PurchaseDetailEntity> detailEntities = purchaseDetailService.list(
                    Wrappers.lambdaQuery(PurchaseDetailEntity.class)
                            .eq(PurchaseDetailEntity::getPurchaseId, purchase.getId()));

            List<PurchaseDetailEntity> details = detailEntities.stream().map(detail -> {
                detail.setStatus(PurchaseConstant.PurchaseDetailStatusEnum.PURCHASING.getCode());
                return detail;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(details);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //1、更新采购需求的状态
        Boolean flag = true;
        List<PurchaseDetailEntity> detailEntities = new ArrayList<>();
        List<WareSkuEntity> newWareSkuEntities = new ArrayList<>();
        List<WareSkuEntity> updateWareSkuEntities = new ArrayList<>();
        for (PurchaseDoneVo.ItemVo detailVo : purchaseDoneVo.getItems()) {
            //创建采购需求保存的实体
            PurchaseDetailEntity detailEntity = purchaseDetailService.getById(detailVo.getItemId());
            if (detailVo.getStatus() == PurchaseConstant.PurchaseDetailStatusEnum.FAIL.getCode()) {
                flag = false;
            } else {
                //4、更新商品的库存。需要先查询是否有商品在当前仓库是否有库存数据
                WareSkuEntity one = wareSkuService.getOne(Wrappers.lambdaQuery(WareSkuEntity.class)
                        .eq(WareSkuEntity::getSkuId, detailEntity.getSkuId())
                        .eq(WareSkuEntity::getWareId, detailEntity.getWareId()));
                if (one == null) {
                    WareSkuEntity wareSkuEntity = new WareSkuEntity();
                    wareSkuEntity.setId(sequence.nextId());
                    wareSkuEntity.setSkuId(detailEntity.getSkuId());
                    wareSkuEntity.setWareId(detailEntity.getWareId());
                    wareSkuEntity.setStock(detailEntity.getSkuNum());
                    wareSkuEntity.setStockLocked(0);
                    R response = productFeignService.getSkuById(detailEntity.getSkuId());
                    if(response.getCode()==0){
                        SkuInfoTo sku = JSONUtil.toBean(JSONUtil.toJsonStr(response.get("skuInfo")),SkuInfoTo.class);
                        wareSkuEntity.setSkuName(sku.getSkuName());
                    }else{
                        throw new RRException("远程获取sku信息失败");
                    }
                    newWareSkuEntities.add(wareSkuEntity);
                } else {
                    one.setStock(one.getStock() + detailEntity.getSkuNum());
                    updateWareSkuEntities.add(one);
                }
            }
            detailEntity.setStatus(detailVo.getStatus());
            detailEntity.setReason(detailVo.getReason());
            detailEntities.add(detailEntity);
        }
        //更新采购需求
        purchaseDetailService.updateBatchById(detailEntities);
        //更新库存
        if(newWareSkuEntities.size()>0){
            wareSkuService.saveBatch(newWareSkuEntities);
        }
        if(updateWareSkuEntities.size()>0){
            wareSkuService.updateBatchById(updateWareSkuEntities);
        }

        //2、更新采购单的状态，获取是否有异常的采购需求
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseDoneVo.getId());
        if (flag) {
            purchaseEntity.setStatus(PurchaseConstant.PurchaseStatusEnum.DONE.getCode());
        } else {
            purchaseEntity.setStatus(PurchaseConstant.PurchaseStatusEnum.FAIL.getCode());
        }
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);


    }


}