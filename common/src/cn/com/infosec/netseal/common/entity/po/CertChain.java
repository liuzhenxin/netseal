package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 证书链
 */
public class CertChain extends Base {

	private Long pid;
	private String certDn;
	private String certIssueDn;
	private String certPath;
	private Long certDataId;
	private Long notBefor;
	private Long notAfter;
	
	// 扩展
	private CertData certData;
	
	public Long getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Long notAfter) {
		this.notAfter = notAfter;
	}
	
	public Long getNotBefor() {
		return notBefor;
	}

	public void setNotBefor(Long notBefor) {
		this.notBefor = notBefor;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getCertIssueDn() {
		return certIssueDn;
	}

	public void setCertIssueDn(String certIssueDn) {
		this.certIssueDn = certIssueDn;
	}

	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	public String calMac() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
		baos.write(calMac(getId()));
		baos.write(calMac(pid));
		baos.write(calMac(certDn));
		baos.write(calMac(certIssueDn));
		baos.write(calMac(certPath));
		baos.write(calMac(certDataId));
		baos.write(calMac(notBefor));
		baos.write(calMac(notAfter));
		baos.write(calMac(getGenerateTime()));
		baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
		
	}

	public Long getCertDataId() {
		return certDataId;
	}

	public void setCertDataId(Long certDataId) {
		this.certDataId = certDataId;
	}

	public CertData getCertData() {
		return certData;
	}

	public void setCertData(CertData certData) {
		this.certData = certData;
	}

	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" pid:");
		sb.append(pid);
		sb.append(" certDn:");
		sb.append(certDn);
		sb.append(" certIssueDn:");
		sb.append(certIssueDn);
		sb.append(" certPath:");
		sb.append(certPath);
		sb.append(" certDataId:");
		sb.append(certDataId);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:");
		sb.append(getMac());
		
		return sb.toString();
	}

	
	
	
}
