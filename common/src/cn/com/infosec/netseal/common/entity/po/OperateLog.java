package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class OperateLog extends Base {

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

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(account));
			baos.write(calMac(clientHost));
			baos.write(calMac(opType));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(returnCode));
			baos.write(calMac(errMsg));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		
		return CryptoHandler.hashEnc64(baos.toByteArray());
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
		sb.append("id:").append(getId());
		sb.append(" account:").append(account);
		sb.append(" clientHost:").append(clientHost);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" opType:").append(opType);
		sb.append(" returnCode:").append(returnCode);
		sb.append(" errMsg:").append(errMsg);
		sb.append(" mac:").append(getMac());
		return sb.toString();
	}

}
