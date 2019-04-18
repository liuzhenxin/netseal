package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 打印控制
 */
public class Printer extends Base {

	private String name;
	private String sealName;
	private Integer sealType;
	private String userName;
	private Integer printNum;
	private Integer printedNum;
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSealName() {
		return sealName;
	}

	public void setSealName(String sealName) {
		this.sealName = sealName;
	}

	public Integer getSealType() {
		return sealType;
	}

	public void setSealType(Integer sealType) {
		this.sealType = sealType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public Integer getPrintedNum() {
		return printedNum;
	}

	public void setPrintedNum(Integer printedNum) {
		this.printedNum = printedNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String calMac() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(name));
			baos.write(calMac(sealName));
			baos.write(calMac(sealType));
			baos.write(calMac(userName));
			baos.write(calMac(printNum));
			baos.write(calMac(printedNum));
			baos.write(calMac(password));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:").append(name);
		sb.append(" sealName:").append(sealName);
		sb.append(" sealType:").append(sealType);
		sb.append(" userName:").append(userName);
		sb.append(" printNum:").append(printNum);
		sb.append(" printedNum:").append(printedNum);
		sb.append(" password:").append(password);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" mac:").append(getMac());
		
		return sb.toString();
	}

}
