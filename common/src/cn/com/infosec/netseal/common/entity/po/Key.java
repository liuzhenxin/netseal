package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.StringUtil;

/**
 * 系统密钥
 */
public class Key extends Base {

	private String certDn; // 证书主题
	private String certSn;
	private Integer certUsage; // 1是签名证书,2是加密证书
	private String certIssueDn; // 颁发者主题
	private String csrPath;
	private String certPath;
	private String keyPath;
	private String backupPath;// 密钥备份路径
	private Long notBefor;
	private Long notAfter;
	private Long certDataId;
	private Long keyDataId;
	private Long csrDataId;
	private String keyMode; // 密钥文件类型 jks pfx
	private String keyPwd; // PFX密码
	private Integer status;
	private Integer hsmId; // 加密卡密钥位置   默认-1

	// 扩展
	private CertData certData;
	private KeyData keyData;

	public Long getCertDataId() {
		return certDataId;
	}

	public void setCertDataId(Long certDataId) {
		this.certDataId = certDataId;
	}

	public Long getKeyDataId() {
		return keyDataId;
	}

	public void setKeyDataId(Long keyDataId) {
		this.keyDataId = keyDataId;
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

	public Integer getCertUsage() {
		return certUsage;
	}

	public void setCertUsage(Integer certUsage) {
		this.certUsage = certUsage;
	}

	public String getCertIssueDn() {
		return certIssueDn;
	}

	public void setCertIssueDn(String certIssueDn) {
		this.certIssueDn = certIssueDn;
	}

	public String getCsrPath() {
		return csrPath;
	}

	public void setCsrPath(String csrPath) {
		this.csrPath = csrPath;
	}

	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getBackupPath() {
		return backupPath;
	}

	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
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

	public String getKeyMode() {
		return keyMode;
	}

	public void setKeyMode(String keyMode) {
		this.keyMode = keyMode;
	}

	public String getKeyPwd() {
		return keyPwd;
	}
	
	public String getKeyPwdPlain() {
		return StringUtil.getString(StringUtil.base64Decode(this.getKeyPwd()));
	}

	public void setKeyPwd(String keyPwd) {
		this.keyPwd = keyPwd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getHsmId() {
		return hsmId;
	}

	public void setHsmId(Integer hsmId) {
		this.hsmId = hsmId;
	}

	public CertData getCertData() {
		return certData;
	}

	public void setCertData(CertData certData) {
		this.certData = certData;
	}

	public KeyData getKeyData() {
		return keyData;
	}

	public void setKeyData(KeyData keyData) {
		this.keyData = keyData;
	}
	
	public Long getCsrDataId() {
		return csrDataId;
	}

	public void setCsrDataId(Long csrDataId) {
		this.csrDataId = csrDataId;
	}

	public String calMac() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(certDn));
			baos.write(calMac(certSn));
			baos.write(calMac(certUsage));
			baos.write(calMac(certIssueDn));
			baos.write(calMac(csrPath));
			baos.write(calMac(certPath));
			baos.write(calMac(keyPath));
			baos.write(calMac(backupPath));
			baos.write(calMac(notBefor));
			baos.write(calMac(notAfter));
			baos.write(calMac(certDataId));
			baos.write(calMac(keyDataId));
			baos.write(calMac(csrDataId));
			baos.write(calMac(keyMode));
			baos.write(calMac(keyPwd));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(status));
			baos.write(calMac(hsmId));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" certDn:");
		sb.append(certDn);
		sb.append(" certSn:");
		sb.append(certSn);
		sb.append(" certUsage:");
		sb.append(certUsage);
		sb.append(" certIssueDn:");
		sb.append(certIssueDn);
		sb.append(" csrPath:");
		sb.append(csrPath);
		sb.append(" certPath:");
		sb.append(certPath);
		sb.append(" keyPath:");
		sb.append(keyPath);
		sb.append(" backupPath:").append(backupPath);
		sb.append(" notBefor:");
		sb.append(notBefor);
		sb.append(" notAfter:");
		sb.append(notAfter);
		sb.append(" certDataId:");
		sb.append(certDataId);
		sb.append(" keyDataId:");
		sb.append(keyDataId);
		sb.append(" keyMode:");
		sb.append(keyMode);
		sb.append(" keyPwd:");
		sb.append(keyPwd);
		sb.append(" generateTime");
		sb.append(getGenerateTime());
		sb.append(" updateTime");
		sb.append(getUpdateTime());
		sb.append(" status:");
		sb.append(status);
		sb.append(" mac:");
		sb.append(getMac());
		sb.append(" csrDataId:");
		sb.append(getCsrDataId());
		sb.append(" hsmId:");
		sb.append(getHsmId());

		return sb.toString();
	}

	

}
