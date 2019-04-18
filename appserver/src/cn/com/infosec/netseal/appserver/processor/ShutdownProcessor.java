package cn.com.infosec.netseal.appserver.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.manager.ApplicationContextManager;
import cn.com.infosec.netseal.appserver.manager.ListenerManager;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;

@Component("ShutdownProcessor")
@Scope("prototype")
public class ShutdownProcessor extends BaseProcessor {

	public ShutdownProcessor() {
		super("ShutdownProcessor");
	}

	/**
	 * 处理客户的停止的请求
	 * 
	 */
	public Response process(Request req) {
		ApplicationContextManager.getBean("listenerManager", ListenerManager.class).stopService();
		return getSucceedResponse();
	}
}
