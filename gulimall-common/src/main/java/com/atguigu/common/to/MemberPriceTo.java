package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceTo {
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
