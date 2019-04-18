package cn.com.infosec.netseal.common.entity.vo;

public class AccessLogVO extends BaseVO {

	private String account;
	private String clientHost;
	private String optype;
	private Long sealId;
	private String returnCode;
	private String errMsg;

	// 扩展
	private String sealName;

	public Long getSealId() {
		return sealId;
	}

	public void setSealId(Long sealId) {
		this.sealId = sealId;
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

	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
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

	public String getSealName() {
		return sealName;
	}

	public void setSealName(String sealName) {
		this.sealName = sealName;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append(" ");
		sb.append(account);
		sb.append(" ");
		sb.append(clientHost);
		sb.append(" ");
		sb.append(optype);
		sb.append(" ");
		sb.append(sealId);
		sb.append(" ");
		sb.append(returnCode);
		sb.append(" ");
		sb.append(errMsg);
		return sb.toString();
	}
}
