package com.atguigu.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 17:58:08
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

