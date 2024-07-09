package com.atguigu.gulimall.ware.vo;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import lombok.Data;

@Data
public class WareSkuVo extends WareSkuEntity {
    /**
     * 仓库名称
     */
    private String wareName;
}
