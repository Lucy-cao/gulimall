package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 17:48:35
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
