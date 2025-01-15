package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单服务所有的接口都需要进行登录。如果未登录，进行拦截
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

	public static ThreadLocal<MemberRespVo> userLogin = new ThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//获取请求中是否有登录用户
		MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthConstant.LOGIN_USER);
		if (attribute != null) {
			//已登录，将登录信息放到threadlocal中，确保当前请求的后续操作都能获取到当前用户
			userLogin.set(attribute);
			return true;
		} else {
			//未登录，重定向到登录页面
			response.sendRedirect("http://auth.gulimall.com:9099/login.html");
			return false;
		}
	}
}
