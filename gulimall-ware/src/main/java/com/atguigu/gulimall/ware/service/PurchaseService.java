package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.vo.MergePurchaseVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 18:02:37
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPageByCondition(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchaseDetail(MergePurchaseVo mergeVo);

    void allocateUser(PurchaseEntity purchase);

    void received(List<Long> purchaseIds);

    void done(PurchaseDoneVo purchaseDoneVo);

}

