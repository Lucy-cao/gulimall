package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 17:43:36
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
