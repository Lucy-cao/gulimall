package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 17:58:08
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
