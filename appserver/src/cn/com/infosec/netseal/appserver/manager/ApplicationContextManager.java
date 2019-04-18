package cn.com.infosec.netseal.appserver.manager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.com.infosec.netseal.common.define.Constants;

public class ApplicationContextManager {
	private static ApplicationContext context = new ClassPathXmlApplicationContext(Constants.APPLICATION_CONTEXT_PATH);

	private ApplicationContextManager() {
	}

	public static <T> T getBean(String str, Class<T> cls) {
		return context.getBean(str, cls);
	}

	public static void flushAppContext() {
		context = new ClassPathXmlApplicationContext(Constants.APPLICATION_CONTEXT_PATH);
	}

}
