package com.atguigu.gulimall.member;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GulimallMemberApplicationTests {
	@Autowired
	MemberService memberService;

	@Test
	void contextLoads() {
		MemberEntity member = new MemberEntity();
		member.setNickname("Lucy_CL4");
		memberService.save(member);
	}

	@Test
	public void encodePwd() {
		//原始md5加密
//		String md5Hex = DigestUtils.md5Hex("123456");
//		System.out.println("md5Hex = " + md5Hex);
		//md5Hex = e10adc3949ba59abbe56e057f20f883e

		//盐值加密。默认盐值：$1$+8位随机数
//		String md5Crypt = Md5Crypt.md5Crypt("123456".getBytes());
//		System.out.println("md5Crypt = " + md5Crypt);
		//字符串123456进行盐值加密
		//第一次加密：md5Crypt = $1$mrEIIH6u$i/vV6wtVvvDAJDI2CUDvk0
		//第二次加密：md5Crypt = $1$t6/8ycTU$ERdjZp9Gvq4b/BujD2UX//
		//两次结果不一致

		//BCrypt
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encode = encoder.encode("123456");
		System.out.println("encode = " + encode);
		//第一次加密：encode = $2a$10$ZiwYJi28/zIUawqRg1x9ruMiC2HBUDcqCpW4G9WGeOWa.QamkNnyy
		//第二次加密：encode = $2a$10$TphoAkLka6/KWrQXTwzK0OxubgQ67xu288gDgjTJFHi2CGupZiFli

		//解密：
		String firstEncode = "$2a$10$ZiwYJi28/zIUawqRg1x9ruMiC2HBUDcqCpW4G9WGeOWa.QamkNnyy";
		boolean firstMatch = encoder.matches("123456", firstEncode);
		System.out.println("firstMatch = " + firstMatch);

		String secondEncode = "$2a$10$TphoAkLka6/KWrQXTwzK0OxubgQ67xu288gDgjTJFHi2CGupZiFli";
		boolean secondMatch = encoder.matches("123456", secondEncode);
		System.out.println("secondMatch = " + secondMatch);
		/**
		 * 两次不同的密文都能够与原密码完整匹配上
		 * firstMatch = true
		 * secondMatch = true
		 */
	}

}
