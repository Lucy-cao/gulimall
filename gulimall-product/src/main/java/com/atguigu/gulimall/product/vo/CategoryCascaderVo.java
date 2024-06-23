package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryCascaderVo {
    /**
     * 分组级联id
     */
    private List<Long> cascaderId;
    /**
     * 级联分类名
     */
    private String cascaderNames;
}
