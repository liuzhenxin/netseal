package cn.com.infosec.netseal.webserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SysUserInterceptor implements HandlerInterceptor {
	private final String sysUserSession = "sysUser";

	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {

	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {

	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		Object sessionObj = request.getSession().getAttribute(sysUserSession);

		if (sessionObj != null) {
			return true;
		}
		response.sendRedirect(request.getContextPath() + "/sysUser/timeout.do?sid=" + System.currentTimeMillis());
		return false;
	}

}
