package com.atguigu.common.to;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SkuInfoTo {
    /**
     * skuId
     */
    @TableId
    private Long skuId;
    /**
     * sku名称
     */
    private String skuName;
}
