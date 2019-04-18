package cn.com.infosec.netseal.appserver.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;

@Component("TestServerProcessor")
@Scope("prototype")
public class TestServerProcessor extends BaseProcessor {

	public TestServerProcessor() {
		super("TestServerProcessor");
	}

	public Response process(Request req) {
		return getSucceedResponse();
	}
}
