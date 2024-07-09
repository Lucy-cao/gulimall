package com.atguigu.gulimall.ware.vo;

import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import lombok.Data;

@Data
public class PurchaseDetailVo extends PurchaseDetailEntity {
    /**
     * 仓库名称
     */
    private String wareName;
}
