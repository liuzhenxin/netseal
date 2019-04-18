package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class AccessLog extends Base {

	private String account;
	private String clientHost;
	private String optype;
	private Long sealId;
	private String returnCode;
	private String errMsg;

	// 扩展
	private Seal seal;

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

	public String calMac() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(account));
			baos.write(calMac(clientHost));
			baos.write(calMac(optype));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(sealId));
			baos.write(calMac(returnCode));
			baos.write(calMac(errMsg));
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}

	public Seal getSeal() {
		return seal;
	}

	public void setSeal(Seal seal) {
		this.seal = seal;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" clientHost:");
		sb.append(clientHost);
		sb.append(" optype:");
		sb.append(optype);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" returnCode:");
		sb.append(returnCode);
		sb.append(" errMsg:");
		sb.append(errMsg);
		return sb.toString();
	}
}
