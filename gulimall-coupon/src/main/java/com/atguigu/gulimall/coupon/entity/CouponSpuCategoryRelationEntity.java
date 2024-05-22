package com.atguigu.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券分类关联
 * 
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 17:43:36
 */
@Data
@TableName("sms_coupon_spu_category_relation")
public class CouponSpuCategoryRelationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 优惠券id
	 */
	private Long couponId;
	/**
	 * 产品分类id
	 */
	private Long categoryId;
	/**
	 * 产品分类名称
	 */
	private String categoryName;

}
