package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrGroupRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-18 17:02:39
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //根据查询条件搜索分页数据
    PageUtils queryByCatId(Map<String, Object> params, Long catelogId);
    //根据id获取信息，返回所属分类的级联id
    AttrGroupEntity getCascaderById(Long attrGroupId);
}

