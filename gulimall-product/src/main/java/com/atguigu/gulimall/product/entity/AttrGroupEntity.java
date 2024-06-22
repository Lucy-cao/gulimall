package com.atguigu.gulimall.product.entity;

import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 属性分组
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:39
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分组id
     */
    @TableId
    @Null(message = "新增不需要传入分组id", groups = {AddGroup.class})
    @NotNull(message = "修改时必须传入分组id", groups = {UpdateGroup.class})
    private Long attrGroupId;
    /**
     * 组名
     */
    @NotBlank(message = "组名不能为空", groups = {AddGroup.class})
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    @NotBlank(message = "组图标不能为空", groups = {AddGroup.class})
    private String icon;
    /**
     * 所属分类id
     */
    @NotNull(message = "所属分类id不能为空", groups = {AddGroup.class})
    private Long catelogId;

    /**
     * 所属分类级联id
     */
    @TableField(exist = false)
    private List<Long> catelogIds;

}
