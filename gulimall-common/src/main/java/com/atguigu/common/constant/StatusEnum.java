package com.atguigu.common.constant;

public enum StatusEnum {
	ENABLE(1, "启用"),
	DISABLE(0, "禁用");

	private Integer code;
	private String msg;

	StatusEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode(){
		return this.code;
	}

	public String getMsg(){
		return this.msg;
	}
}
