package cn.com.infosec.netseal.appserver.base;

import java.util.Properties;

import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class BaseProcessor {

	private String processorname = "BaseProcessor";

	private Response res = null;
	protected Properties pro = new Properties();

	public BaseProcessor() {
		/**
		 * 初始化响应数据的集合器
		 */
		res = new Response();
	}

	public BaseProcessor(String name) {
		this();
		processorname = name;
		debuglog("");
		debuglog("===========================================================================");
		debuglog("===========================================================================");
		debuglog("The  " + processorname + " is starting ... ...");
	}

	public Response process(Request req) {
		return res;
	}

	/**
	 * 记录调试日志
	 * 
	 * @param info
	 *            日志内容
	 */
	public void debuglog(String info) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.debuglog(info);
	}

	/**
	 * 记录调试日志
	 * 
	 * @param info
	 *            日志内容
	 * @param tr
	 *            异常
	 */
	public void debuglog(String info, Throwable tr) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.debuglog(info, tr);
	}

	/**
	 * 记录系统日志
	 * 
	 * @param info
	 *            日志内容
	 * @param tr
	 *            异常
	 */
	public void systemlog(String info, Throwable tr) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.systemlog(info, tr);

	}

	/**
	 * 记录错误日志
	 * 
	 * @param info
	 *            日志内容
	 */
	public void errorlog(String info) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.errorlog(info);
	}

	/**
	 * 记录错误日志
	 * 
	 * @param info
	 *            日志内容
	 * @param tr
	 *            异常
	 */
	public void errorlog(String info, Throwable tr) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.errorlog(info, tr);

	}

	/**
	 * 记录系统日志
	 * 
	 * @param info
	 *            日志内容
	 */
	public void systemlog(String info) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.systemlog(info);
	}

	/**
	 * 记录访问日志
	 * 
	 * @param info
	 *            日志内容
	 */
	public void accesslog(String info) {
		info = "[ " + processorname + " ]   	" + info;
		LoggerUtil.accesslog(info);
	}

	/**
	 * 当操作出现错误的时候，返回客户端的响应信息
	 * 
	 * @param debuginfo
	 *            调试信息
	 * @param errorno
	 *            错误号
	 * @return
	 */
	public Response getErrorResponse(String debuginfo, int errorno) {
		res.setErrCode(errorno);

		debuglog(debuginfo);
		errorlog(debuginfo);
		debuglog(res.toString());

		return res;
	}

	public Response getErrorResponse(String debuginfo, int errorno, Exception e) {
		res.setErrCode(errorno);

		debuglog(debuginfo);
		errorlog(debuginfo, e);
		debuglog(res.toString());

		return res;
	}

	/**
	 * 当操作成功后，返回给客户端的响应信息
	 * 
	 * @return
	 */
	public Response getSucceedResponse() {
		res.setData(pro);
		return res;
	}

	protected void checkReqDataInvalid(Request req) {
		debuglog("check request ...");
		if (req == null)
			throw new NetSealRuntimeException(ErrCode.REQUEST_ISNULL, " request is null");

		debuglog("check request data ...");
		if (req.getData() == null || req.getData().size() == 0)
			throw new NetSealRuntimeException(ErrCode.REQUEST_DATA_ISNULL, " request data is null");
	}

	/**
	 * 校验参数值有效性
	 * 
	 * @param name
	 * @param value
	 * @param type
	 */
	protected void checkParamValue(String name, String value, String type) {
		checkParamValue(name, value, type, Constants.DEFAULT_INT);
	}

	/**
	 * 校验参数值有效性
	 * 
	 * @param name
	 * @param value
	 * @param type
	 */
	protected void checkParamValue(String name, String value, String type, long lenLimit) {
		debuglog("check request param value, name is " + name + ", value is " + value + ", type is " + type + ", len limit is " + lenLimit);

		switch (type) {
		case Constants.PARAM_TYPE_STRING_NOT_NULL:
			if (value == null)
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_ISNULL, name + " value is null");

			if ("".equals(value.trim()))
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_ISEMPTY, name + " value is empty");

			if (getBytes(value).length > lenLimit)
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_LEN_OVER_LIMIT, name + " value len is over limit " + lenLimit);
			break;

		case Constants.PARAM_TYPE_STRING_NULLABLE:
			if (StringUtil.isNotBlank(value))
				if (getBytes(value).length > lenLimit)
					throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_LEN_OVER_LIMIT, name + " value len is over limit " + lenLimit);
			break;

		case Constants.PARAM_TYPE_INT:
			try {
				Integer.parseInt(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_LONG:
			try {
				Long.parseLong(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_DATE:
			try {
				if (!DateUtil.checkDateValid(value))
					throw new Exception();
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_FLOAT:
			try {
				Float.parseFloat(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_BOOLEAN:
			try {
				Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		default:
			break;
		}
	}

	protected byte[] getBytes(String key) {
		return StringUtil.getBytes(key);
	}

	protected String getValue(String key) {
		return StringUtil.parseStringWithDefault(key, Constants.DEFAULT_STRING);
	}

	protected String getValue(Properties reqdata, String key) {
		return StringUtil.parseStringWithDefault(reqdata.getProperty(key), Constants.DEFAULT_STRING);
	}

}
