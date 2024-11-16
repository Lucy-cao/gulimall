package com.atguigu.gulimall.member.service;

import com.atguigu.gulimall.member.vo.RegisterParam;
import com.atguigu.gulimall.member.vo.UserLoginParam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author lucy
 * @email caolu8815@163.com
 * @date 2024-05-21 17:48:35
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

	Long register(RegisterParam registerParam);

	MemberEntity login(UserLoginParam param);

	MemberEntity oauthLogin(MemberEntity member);
}

