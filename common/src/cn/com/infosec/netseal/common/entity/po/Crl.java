package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class Crl extends Base {

	/*private Long certSn;

	public Long getCertSn() {
		return certSn;
	}

	public void setCertSn(Long certSn) {
		this.certSn = certSn;
	}*/

	private Long certId;

	public Long getCertId() {
		return certId;
	}

	public void setCertId(Long certId) {
		this.certId = certId;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(certId));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" certId:");
		sb.append(certId);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:");
		sb.append(getMac());
		
		return sb.toString();
	}
	
}
