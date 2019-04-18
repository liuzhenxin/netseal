package cn.com.infosec.netseal.appserver.processor;

import java.util.Properties;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.DateUtil;

@Component("GetSystemProProcessor")
@Scope("prototype")
public class GetSystemProProcessor extends BaseProcessor {

	public GetSystemProProcessor() {
		super("GetSystemProProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String proKey = getValue(reqdata, Constants.PRO_KEY);

		// 检查数据值有效性
		checkParamValue(Constants.PRO_KEY, proKey, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_TWENTY);

		switch (proKey) {
			case Constants.PRO_GET_CURRENT_TIME:
				pro.setProperty(Constants.RESULT, String.valueOf(DateUtil.getCurrentTime()));
				break;
			default:
				break;
		}

		return getSucceedResponse();
	}
}
