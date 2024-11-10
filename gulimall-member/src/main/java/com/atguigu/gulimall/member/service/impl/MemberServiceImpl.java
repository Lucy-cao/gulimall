package com.atguigu.gulimall.member.service.impl;

import cn.hutool.core.date.DateTime;
import com.atguigu.common.constant.StatusEnum;
import com.atguigu.common.exception.RRException;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.service.MemberLevelService;
import com.atguigu.gulimall.member.vo.RegisterParam;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
	@Autowired
	Sequence sequence;
	@Autowired
	BCryptPasswordEncoder encoder;
	@Autowired
	MemberLevelService memberLevelService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<MemberEntity> page = this.page(
				new Query<MemberEntity>().getPage(params),
				new QueryWrapper<MemberEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public Long register(RegisterParam registerParam) {
		//检查用户名或手机号有没有存在
		long count = this.count(Wrappers.lambdaQuery(MemberEntity.class)
				.eq(MemberEntity::getUsername, registerParam.getUserName())
				.or().eq(MemberEntity::getMobile, registerParam.getPhone()));
		if (count > 0) {
			throw new RRException("用户名或手机号已存在，请重新输入");
		}

		//校验通过，正常注册用户
		MemberEntity memberEntity = new MemberEntity();
		memberEntity.setId(sequence.nextId());
		//初始注册需要获取默认等级
		MemberLevelEntity levelEntity = memberLevelService.getDefaultLevel();
		memberEntity.setLevelId(levelEntity.getId());
		memberEntity.setMobile(registerParam.getPhone());
		//密码需要进行加密
		memberEntity.setPassword(encoder.encode(registerParam.getPassword()));
		memberEntity.setUsername(registerParam.getUserName());
		memberEntity.setCreateTime(DateTime.now());
		memberEntity.setStatus(StatusEnum.ENABLE.getCode());
		//保存用户数据
		this.save(memberEntity);
		return memberEntity.getId();
	}

}