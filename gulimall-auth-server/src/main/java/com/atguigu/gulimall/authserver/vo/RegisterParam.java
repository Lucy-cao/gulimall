package com.atguigu.gulimall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class RegisterParam {
	/**
	 * 用户名
	 */
	@NotBlank(message = "用户名不能为空")
	@Length(min = 4, max = 20, message = "用户名长度只能在4-20个字符之间")
	private String userName;
	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	@Length(min = 6, max = 20, message = "密码长度只能在6-20个字符之间")
	private String password;
	/**
	 * 确认密码
	 */
	@NotBlank(message = "密码不能为空")
	@Length(min = 6, max = 20, message = "密码长度只能在6-20个字符之间")
	private String confirmPassword;
	/**
	 * 手机号
	 */
	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
	private String phone;
	/**
	 * 验证码
	 */
	@NotBlank(message = "验证码不能为空")
	private String code;
}
