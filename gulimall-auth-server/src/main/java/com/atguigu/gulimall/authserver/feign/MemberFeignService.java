package com.atguigu.gulimall.authserver.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.vo.RegisterParam;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.authserver.vo.UserLoginParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {
	@PostMapping("/member/member/register")
	R register(@RequestBody RegisterParam registerParam);

	@PostMapping("/member/member/login")
	R login(@RequestBody UserLoginParam param);

	@PostMapping("/member/member/oauthLogin")
	R oauthLogin(@RequestBody MemberRespVo param);
}
