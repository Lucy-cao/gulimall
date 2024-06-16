package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名称不能为空")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo地址需要是一个合法的URL地址")
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "首字母不能为空")
	@Pattern(regexp = "/^[a-zA-Z]$/", message = "首字母只能为一个字母")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空")
	@Min(value = 0, message = "排序只能为大于等于0的整数")
	private Integer sort;

}
