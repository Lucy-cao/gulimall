package com.atguigu.gulimall.product.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttrTypeEnum {
    SALE_ATTR(0, "销售属性"),
    BASE_ATTR(1, "基本属性"),
    BOTH_ATTR(2, "既是销售属性又是基本属性");

    private Integer code;
    private String message;

}
