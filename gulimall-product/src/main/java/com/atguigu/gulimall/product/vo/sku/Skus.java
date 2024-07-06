/**
  * Copyright 2024 bejson.com 
  */
package com.atguigu.gulimall.product.vo.sku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2024-07-05 20:48:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Skus {
    /**
     * 销售属性信息
     */
    private List<Attr> attr;
    /**
     * 商品名称
     */
    private String skuName;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 标题
     */
    private String skuTitle;
    /**
     * 副标题
     */
    private String skuSubtitle;
    /**
     * 所选图集
     */
    private List<Images> images;
    /**
     * 笛卡尔积结果
     */
    private List<String> descar;
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
    private List<MemberPrice> memberPrice;
}