package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.StringUtil;

public class OperateLogVO extends BaseVO {

	private String account;
	private String clientHost;
	private String opType;
	private String returnCode;
	private String errMsg;

	//扩展
	private String OpTimeStart;
	private String OpTimeEnd;
		
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getClientHost() {
		return clientHost;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getMac() {
		return "";
	}

	public String getOpTimeStart() {
		return OpTimeStart;
	}

	public void setOpTimeStart(String opTimeStart) {
		OpTimeStart = opTimeStart;
	}

	public String getOpTimeEnd() {
		return OpTimeEnd;
	}

	public void setOpTimeEnd(String opTimeEnd) {
		OpTimeEnd = opTimeEnd;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName());
		sb.append(" [");
		sb.append("id:");
		sb.append(getId());
		sb.append(" account:");
		sb.append(StringUtil.parseStringWithDefault(account, Constants.DEFAULT_STRING));
		sb.append(" clientHost:");
		sb.append(StringUtil.parseStringWithDefault(clientHost, Constants.DEFAULT_STRING));
		sb.append(" optype:");
		sb.append(StringUtil.parseStringWithDefault(opType, Constants.DEFAULT_STRING));
		sb.append(" optime:");
		sb.append(getGenerateTime());
		sb.append(" returnCode:");
		sb.append(StringUtil.parseStringWithDefault(returnCode, Constants.DEFAULT_STRING));
		sb.append(" errMsg:");
		sb.append(StringUtil.parseStringWithDefault(errMsg, Constants.DEFAULT_STRING));
		sb.append(" mac:");
		sb.append(getMac());
		sb.append("]");

		return sb.toString();
	}

}
