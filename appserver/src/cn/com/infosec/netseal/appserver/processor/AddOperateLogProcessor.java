package cn.com.infosec.netseal.appserver.processor;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.operateLog.OperateLogServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.OperateLog;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.DateUtil;

@Component("AddOperateLogProcessor")
@Scope("prototype")
public class AddOperateLogProcessor extends BaseProcessor {

	@Autowired
	private OperateLogServiceImpl operateLogService;

	public AddOperateLogProcessor() {
		super("AddOperateLogProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String account = getValue(reqdata, Constants.OP_LOG_ACCOUNT);
		String opType = getValue(reqdata, Constants.OP_LOG_TYPE);
		String opTime = getValue(reqdata, Constants.OP_LOG_TIME);
		String returnCode = getValue(reqdata, Constants.OP_LOG_RETURN_CODE);
		String errMsg = getValue(reqdata, Constants.OP_LOG_ERR_MSG);
		String clientHost = getValue(req.getClientHost());

		// 检查数据值有效性
		checkParamValue(Constants.OP_LOG_ACCOUNT, account, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_TWO_HUNDRED);
		checkParamValue(Constants.OP_LOG_TYPE, opType, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.OP_LOG_TIME, opTime, Constants.PARAM_TYPE_DATE);
		checkParamValue(Constants.OP_LOG_RETURN_CODE, returnCode, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_EIGHT);
		checkParamValue(Constants.OP_LOG_ERR_MSG, errMsg, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_HUNDRED);

		// 创建POJO类
		OperateLog op = new OperateLog();
		op.setAccount(account);
		op.setOpType(opType);
		op.setGenerateTime(DateUtil.getLongTime(opTime));
		op.setUpdateTime(DateUtil.getLongTime(opTime));
		op.setReturnCode(returnCode);
		op.setErrMsg(errMsg);
		op.setClientHost(clientHost);

		// 保存数据
		try {
			operateLogService.insertOperateLog(op);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.PROCESS_REQUEST_ERROR, e.getMessage());
		}
		return getSucceedResponse();
	}
}
