package com.atguigu.gulimall.cart.vo;

import lombok.Data;

@Data
public class UserInfoTo {
	private Long userId;//用户id
	private String userKey;//用户标识
	private Boolean hasUserKey = false;//标识原始请求中是否已经携带user-key
}
