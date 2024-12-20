package com.atguigu.gulimall.authserver.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {

	@GetMapping("/sms/sendcode")
	public R smsSendcode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
