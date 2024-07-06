package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {
    /**
     * sku id
     */
    private Long skuId;
    /**
     * 设置折扣：满xx件
     */
    private Integer fullCount;
    /**
     * 设置折扣：打xx折
     */
    private BigDecimal discount;
    /**
     * 设置折扣是否可以叠加优惠：1=是，0=否
     */
    private int countStatus;
    /**
     * 设置满减：满xx元
     */
    private BigDecimal fullPrice;
    /**
     * 设置满减：减xx元
     */
    private BigDecimal reducePrice;
    /**
     * 设置满减是否可以叠加优惠：1=是，0=否
     */
    private int priceStatus;
    /**
     * 设置会员价
     */
    private List<MemberPriceTo> memberPrice;
}
