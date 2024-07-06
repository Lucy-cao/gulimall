/**
  * Copyright 2024 bejson.com 
  */
package com.atguigu.gulimall.product.vo.sku;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2024-07-05 20:48:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MemberPrice {
    /**
     * 会员等级id
     */
    private Long id;
    /**
     * 会员等级名
     */
    private String name;
    /**
     * 会员价格
     */
    private BigDecimal price;

}