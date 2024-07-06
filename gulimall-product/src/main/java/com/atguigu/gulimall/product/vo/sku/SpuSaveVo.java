/**
 * Copyright 2024 bejson.com
 */
package com.atguigu.gulimall.product.vo.sku;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2024-07-05 20:48:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SpuSaveVo {
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String spuName;
    /**
     * 商品描述
     */
    @NotBlank(message = "商品描述不能为空")
    private String spuDescription;
    /**
     * 分类id
     */
    @NotNull(message = "请选择分类")
    private Long catalogId;
    /**
     * 品牌id
     */
    @NotNull(message = "请选择品牌")
    private Long brandId;
    /**
     * 商品重量（kg）
     */
    @Min(value = 0, message = "商品重量需要大于0")
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架]
     */
    private Integer publishStatus;
    /**
     * 商品介绍
     */
    @Size(min = 1, message = "请上传商品介绍图")
    private List<String> decript;
    /**
     * 商品图集
     */
    @Size(min = 1, message = "请上传商品图集")
    private List<String> images;
    /**
     * 设置积分
     */
    private Bounds bounds;
    /**
     * 规格参数
     */
    private List<BaseAttrs> baseAttrs;
    /**
     * SKU信息
     */
    private List<Skus> skus;

}