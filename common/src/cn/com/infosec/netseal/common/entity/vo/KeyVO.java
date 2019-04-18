package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

/**
 * 系统密钥
 */
public class KeyVO extends BaseVO {

	private String certDn; // 证书主题
	private String certSn; 
	private Integer certUsage; //1是签名证书,2是加密证书
	private String certIssueDn; // 颁发者主题
	private String csrPath;
	private String certPath;
	private String keyPath;
	private String backupPath;//密钥备份路径
	private Long notBefor;
	private Long notAfter;
	private Long certDataId;
	private Long keyDataId;
	private String keyMode; // 密钥文件类型 jks pfx
	private String keyPwd; // PFX密码
	private Integer status;
	private Long csrDataId;
	private Integer hsmId; // 加密卡密钥位置
	
	// 扩展项
	private String certUsageCn;

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
		return CertUtil.transCertDn(certDn);
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getCertSn() {
		return CertUtil.transCertSn(certSn);
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
		setNotBeforCn(DateUtil.getDate(notBefor));
	}

	public Long getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Long notAfter) {
		this.notAfter = notAfter;
		setNotAfterCn(DateUtil.getDate(notAfter));
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

	public String getMac() {
		return "";
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
		setStatusCn(getArrValue(statusArr, status));
	}

	public Integer getHsmId() {
		return hsmId;
	}

	public void setHsmId(Integer hsmId) {
		this.hsmId = hsmId;
	}

	public Long getCsrDataId() {
		return csrDataId;
	}

	public void setCsrDataId(Long csrDataId) {
		this.csrDataId = csrDataId;
	}

	public Integer getCertUsage() {
		return certUsage;
	}

	public void setCertUsage(Integer certUsage) {
		this.certUsage = certUsage;
		if(certUsage == 1)
			setCertUsageCn("签名");
		if(certUsage == 2)
			setCertUsageCn("加密");
		if(certUsage == 3)
			setCertUsageCn("签名、加密");
	}

	public String getCertUsageCn() {
		return certUsageCn;
	}

	public void setCertUsageCn(String certUsageCn) {
		this.certUsageCn = certUsageCn;
	}
	
	
}
