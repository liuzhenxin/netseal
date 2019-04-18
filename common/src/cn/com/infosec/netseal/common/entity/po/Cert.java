package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;


/**
 * 签证人注册的证书
 */
public class Cert extends Base {

	private String certDn;
	private String certSn;
	private String certIssueDn;
	private Integer certUsage;
	private String certPath;
	private Long notBefor;
	private Long notAfter;
	private Long certDataId;
	private Long userId;
	private Long sysUserId;

	// 扩展
	private User user;
	private SysUser sysUser;
	private CertData certData;

	public Long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Long sysUserId) {
		this.sysUserId = sysUserId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getCertSn() {
		return certSn;
	}

	public void setCertSn(String certSn) {
		this.certSn = certSn;
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

	public Long getNotBefor() {
		return notBefor;
	}

	public void setNotBefor(Long notBefor) {
		this.notBefor = notBefor;
	}

	public Long getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Long notAfter) {
		this.notAfter = notAfter;
	}
	
	public Long getCertDataId() {
		return certDataId;
	}

	public void setCertDataId(Long certDataId) {
		this.certDataId = certDataId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public CertData getCertData() {
		return certData;
	}

	public void setCertData(CertData certData) {
		this.certData = certData;
	}
	public Integer getCertUsage() {
		return certUsage;
	}

	public void setCertUsage(Integer certUsage) {
		this.certUsage = certUsage;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
		baos.write(calMac(getId()));
		baos.write(calMac(certSn));
		baos.write(calMac(certDn));
		baos.write(calMac(certIssueDn));
		baos.write(calMac(certUsage));
		baos.write(calMac(certPath));
		baos.write(calMac(notBefor));
		baos.write(calMac(notAfter));
		baos.write(calMac(certDataId));
		baos.write(calMac(userId));
		baos.write(calMac(sysUserId));
		baos.write(calMac(getGenerateTime()));
		baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}



		
		return CryptoHandler.hashEnc64(baos.toByteArray());
		
		
	}

	

	// 重写
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" certSn:");
		sb.append(certSn);
		sb.append(" certDn:");
		sb.append(certDn);
		sb.append(" certIssueDn:");
		sb.append(certIssueDn);
		sb.append(" certPath:");
		sb.append(certPath);
		sb.append(" notBefor:");
		sb.append(notBefor);
		sb.append(" notAfter:");
		sb.append(notAfter);
		sb.append(" certDataId:");
		sb.append(certDataId);
		sb.append(" userId:");
		sb.append(userId);
		sb.append(" sysUserId:");
		sb.append(sysUserId);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:");
		sb.append(getMac());
		return sb.toString();
	}

	

}
