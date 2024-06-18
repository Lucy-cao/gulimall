package com.atguigu.gulimall.product.entity;

import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.ListValue;
import com.atguigu.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:39
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id，新增的时候不能有品牌id，修改的时候必须有品牌id
     */
    @Null(message = "新增不需要传入品牌id", groups = {AddGroup.class})
    @NotNull(message = "修改时必须传入品牌id", groups = {UpdateGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;
    /**
     * 品牌logo地址
     */
    @NotBlank(message = "品牌logo不能为空", groups = {AddGroup.class})
    @URL(message = "logo地址需要是一个合法的URL地址", groups = {AddGroup.class, UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(message = "显示状态不能为空", groups = AddGroup.class)
    @ListValue(value = {0, 1}, groups = {AddGroup.class, UpdateGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    //只在新增的时候校验
    @NotBlank(message = "首字母不能为空", groups = {AddGroup.class})
    //新增和修改的时候都会校验.修改的时候如果前端没有传值，则不会校验Pattern，只有传值了才会校验。
    @Pattern(regexp = "/^[a-zA-Z]$/", message = "首字母只能为一个字母", groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = {AddGroup.class})
    @Min(value = 0, message = "排序只能为大于等于0的整数", groups = {AddGroup.class, UpdateGroup.class})
    private Integer sort;

}
