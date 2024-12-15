package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 拦截器：
 * 在执行目标方法之前，判断用户的状态，并将用户信息封装传递到controller目标请求
 * 在执行目标方法之后，将用户标识user-key放入cookie，正式用户和临时用户都会有user-key
 */
public class CartLoginInterceptor implements HandlerInterceptor {
	public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//封装用于传递到controller请求的用户信息
		UserInfoTo userInfoTo = new UserInfoTo();
		//判断用户是否登录
		HttpSession session = request.getSession();
		MemberRespVo member = (MemberRespVo) session.getAttribute(AuthConstant.LOGIN_USER);
		if (member != null) {
			//用户已登录，从session获取信息
			userInfoTo.setUserId(member.getId());
		}
		//判断cookie是否已经携带user-key
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(AuthConstant.USER_KEY)) {
					userInfoTo.setUserKey(cookie.getValue());
					userInfoTo.setHasUserKey(true);
				}
			}
		}

		//如果没有user-key，系统自动生成
		if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
			String uuid = UUID.randomUUID().toString();
			userInfoTo.setUserKey(uuid);
		}
		//通过threadlocal向同一个线程后续请求传递用户数据
		threadLocal.set(userInfoTo);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		UserInfoTo userInfoTo = threadLocal.get();
		if (!userInfoTo.getHasUserKey()) {
			Cookie cookie = new Cookie(AuthConstant.USER_KEY, userInfoTo.getUserKey());
			cookie.setDomain("gulimall.com");
			cookie.setMaxAge(AuthConstant.USER_KEY_TIMEOUT);
			response.addCookie(cookie);
		}
	}
}
