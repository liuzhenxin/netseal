package cn.com.infosec.netseal.webserver.aop;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.ManageLog;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.util.SessionUtil;
import cn.com.infosec.netseal.webserver.util.WebUtil;

/**
 * 切面
 * 
 */
public class LogAspect {
	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private LoggerUtil loggerUtil;

	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		String optype = Constants.DEFAULT_STRING;
		String errMsg = Constants.DEFAULT_STRING;
		String returnCode = "0";
		Object result = null;

		try {
			String targetName = pjp.getTarget().getClass().getName();
			String methodName = pjp.getSignature().getName();
			Object[] arguments = pjp.getArgs();
			Class<?> targetClass = Class.forName(targetName);

			Method[] methods = targetClass.getMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					Class<?>[] clazzs = method.getParameterTypes();
					if (clazzs.length == arguments.length) {
						SealLog sealLog = method.getAnnotation(SealLog.class);
						if (sealLog != null) {
							optype = sealLog.optype();
						}
						break;
					}
				}
			}

			result = pjp.proceed();
		} catch (Exception e) {
			returnCode = "-1";
			errMsg = e.getMessage();
			LoggerUtil.errorlog(errMsg, e);
			throw e;
		} finally {
			try {
				String account = SessionUtil.getSysUserAccount(httpSession);
				if (!Constants.DEFAULT_STRING.equals(optype) && !Constants.DEFAULT_STRING.equals(account)) {
					String ip = WebUtil.getClientHost(httpRequest);
					ManageLog log = new ManageLog(account, ip, optype);
					log.setOpType(optype);
					log.setReturnCode(returnCode);

					byte[] bs = StringUtil.getBytes(errMsg);
					if (bs.length > Constants.LENGTH_HUNDRED)
						errMsg = StringUtil.getString(StringUtil.subLeft(bs, Constants.LENGTH_HUNDRED));

					log.setErrMsg(errMsg);
					log.setGenerateTime(DateUtil.getCurrentTime());
					log.setUpdateTime(DateUtil.getCurrentTime());
					loggerUtil.managelog(log);
				}
			} catch (Exception e1) {
				errMsg = e1.getMessage();
				LoggerUtil.errorlog(errMsg, e1);
			}
		}
		return result;
	}
}
