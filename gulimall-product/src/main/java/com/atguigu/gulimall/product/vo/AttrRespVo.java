package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * 所属分类名字
     */
    private String catelogName;

    /**
     * 所属分类级联id
     */
    private List<Long> catelogPath;

    /**
     * 所属分组名字
     */
    private String groupName;
}
