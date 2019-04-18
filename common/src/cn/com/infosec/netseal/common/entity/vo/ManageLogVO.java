package cn.com.infosec.netseal.common.entity.vo;

public class ManageLogVO extends BaseVO {

	private String account;
	private String clientHost;
	private String opType;
	private String returnCode;
	private String errMsg;

	//扩展
	private String OpTimeStart;
	private String OpTimeEnd;
	
	public ManageLogVO() {
	}

	/**
	 * @param account
	 * @param clientHost
	 * @param opType
	 */
	public ManageLogVO(String account, String clientHost, String opType) {
		this.account = account;
		this.clientHost = clientHost;
		this.opType = opType;
	}

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
		sb.append(account).append(" ");
		sb.append(clientHost).append(" ");
		sb.append(opType).append(" ");
		sb.append(returnCode).append(" ");
		sb.append(errMsg);
		return sb.toString();
	}
}
