package cn.com.infosec.netseal.common.entity.vo;

import java.io.Serializable;

import cn.com.infosec.netseal.common.util.CertUtil;

/**
 * 用户
 */
public class UserVO extends BaseVO implements Serializable {
	private static final long serialVersionUID = -2158646284221765149L;

	private String name;
	private String password;
	private String email;
	private String phone;
	private Long companyId;

	// 扩展属性
	private String companyName;
	private String certDn;
	private String certSn;
	private String x509Cert;
	private String certenSn;
	private String x509Certen;
	private Integer certUsage;
	private String certUsageCN;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email == null)
			email = "";
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		if (phone == null)
			phone = "";
		this.phone = phone;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getMac() {
		return "";
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public String getX509Cert() {
		if (x509Cert != null)
			x509Cert = "-----BEGIN CERTIFICATE-----\n" + x509Cert + "\n-----END CERTIFICATE-----";
		return x509Cert;
	}

	public void setX509Cert(String x509Cert) {
		this.x509Cert = x509Cert;
	}

	public String getCertenSn() {
		return certenSn;
	}

	public void setCertenSn(String certenSn) {
		this.certenSn = certenSn;
	}

	public String getX509Certen() {
		if (x509Certen != null)
			x509Certen = "-----BEGIN CERTIFICATE-----\n" + x509Certen + "\n-----END CERTIFICATE-----";
		return x509Certen;
	}

	public void setX509Certen(String x509Certen) {
		this.x509Certen = x509Certen;
	}

	public String getCertUsageCN() {
		return certUsageCN;
	}

	public void setCertUsageCN(String certUsageCN) {
		this.certUsageCN = certUsageCN;
	}

	public Integer getCertUsage() {
		return certUsage;
	}
	
	public String getCertUsage(Integer CertUsage) {
		if(CertUsage == 1) {
			return "签名";
		}else if (CertUsage == 2){
			return "加密";
		}else {
			return "签名+加密";
		}
		
		
	}

	public void setCertUsage(Integer certUsage) {
		this.certUsage = certUsage;
	}

}
