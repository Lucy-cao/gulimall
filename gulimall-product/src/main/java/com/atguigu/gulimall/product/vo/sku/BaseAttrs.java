/**
  * Copyright 2024 bejson.com 
  */
package com.atguigu.gulimall.product.vo.sku;

import lombok.Data;

/**
 * Auto-generated: 2024-07-05 20:48:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class BaseAttrs {
    /**
     * 规格参数属性id
     */
    private Long attrId;
    /**
     * 规格参数属性值
     */
    private String attrValues;
    /**
     * 是否快速展示：1=是，0=否
     */
    private int showDesc;
}