package cn.com.infosec.netseal.appserver.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.AccessLog;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.CounterManager;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.resource.ErrMsg;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Component
@Aspect
public class ProcessorAOP {

	@Autowired
	private LoggerUtil loggerUtil;

	// 切入点表达式按需配置
	@Pointcut("execution(* cn.com.infosec.netseal.appserver.service..*.*(..))")
	private void myPointcut() {
	}

	@Before(value = "myPointcut()")
	public void before(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();

		Object[] args = joinPoint.getArgs();
		StringBuilder log = new StringBuilder("args( ");
		for (Object arg : args) {
			log.append(arg + " ");
		}
		log.append(")");
		LoggerUtil.debuglog("oper " + className + " 's " + methodName + " method, " + log.toString());
	}

	@AfterReturning(value = "myPointcut()", returning = "returnVal")
	public void afterReturin(Object returnVal) {
		LoggerUtil.debuglog("method is complete, return: " + returnVal);
	}

	@AfterThrowing(value = "myPointcut()", throwing = "e")
	public void afterThrowing(Throwable e) {
		if (e instanceof Exception)
			LoggerUtil.errorlog("method error, " + e.getMessage());
	}

	@Around("execution(* cn.com.infosec.netseal.appserver.processor..*.*(..))")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String className = proceedingJoinPoint.getTarget().getClass().getName();
		String methodName = proceedingJoinPoint.getSignature().getName();
		LoggerUtil.debuglog("oper " + className + " 's " + methodName + " method");

		Object[] args = proceedingJoinPoint.getArgs();

		Object result = null;
		AccessLog log = new AccessLog();
		log.setGenerateTime(DateUtil.getCurrentTime());
		log.setUpdateTime(DateUtil.getCurrentTime());
		log.setReturnCode("0");

		Request request = null;
		try {
			if (args.length > 0) {
				request = (Request) args[0];
				log.setClientHost(request.getClientHost());
				log.setOptype(request.getType());
			} else {
				log.setClientHost(Constants.DEFAULT_UNKNOWN_STRING);
				log.setOptype(Constants.DEFAULT_UNKNOWN_STRING);
			}

			// 交易处理
			result = proceedingJoinPoint.proceed();
			// 记录成功交易数量
			CounterManager.increment(Constants.DEAL_S_NUM);
			// 记录日志
			LoggerUtil.debuglog("oper " + className + " 's " + methodName + " method, complete");
		} catch (Exception e) {
			// 记录失败交易数量
			CounterManager.increment(Constants.DEAL_F_NUM);

			LoggerUtil.debuglog("oper " + className + " 's " + methodName + " method, error: " + e.getMessage());
			LoggerUtil.errorlog("oper " + className + " 's " + methodName + " method, error: ", e);

			int errCode = ErrCode.INNER_OPERATION_ERROR;
			String errMsg = "system error";

			if (e instanceof NetSealRuntimeException) {
				NetSealRuntimeException ne = (NetSealRuntimeException) e;
				errCode = ne.getErrNum();
				errMsg = ErrMsg.getErrMsg(errCode);
			} else if (e instanceof DataAccessException) {
				errCode = ErrCode.DATABASE_OPERATION_ERROR;
				errMsg = ErrMsg.getErrMsg(errCode);
			}

			Response res = new Response();
			res.setErrCode(errCode);
			res.setErrMsg(errMsg);

			// 截位处理
			byte[] bs = StringUtil.getBytes(errMsg);
			if (bs.length > Constants.LENGTH_HUNDRED)
				errMsg = StringUtil.getString(StringUtil.subLeft(bs, Constants.LENGTH_HUNDRED));

			log.setReturnCode(String.valueOf(errCode));
			log.setErrMsg(errMsg);

			return res;
		} finally {
			if (request != null && !request.getType().equalsIgnoreCase("Shutdown") && !request.getType().equalsIgnoreCase("TestServer"))
				loggerUtil.accesslog(log);
		}
		return result;
	}
}