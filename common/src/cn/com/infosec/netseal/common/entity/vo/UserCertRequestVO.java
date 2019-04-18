package cn.com.infosec.netseal.common.entity.vo;

import java.io.Serializable;

/**
 * 用户证书申请
 */
public class UserCertRequestVO extends BaseVO implements Serializable {
	private static final long serialVersionUID = 4797221519502642307L;
	private Long userId;// 签章人ID
	private String certType;
	private String certTypeCN;
	private String certTemplate;
	private String certDn;
	private String userUuid;
	private String certRefno;
	private String certAuthCode;
	private Integer status;
	private Integer validityLen;
	
	private String p10;
	private String tmpPubKey;
	private String keyLen;
	private String label;
	private String pin;
	private String cspName;
	private String sm2Cryptprov;
	
	private String signCert;
	private String encCert;
	private String encPri;
	private String ukek;
	private Integer isDCert;//是否是双证 0不是 1是
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertTemplate() {
		return certTemplate;
	}

	public void setCertTemplate(String certTemplate) {
		this.certTemplate = certTemplate;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getCertRefno() {
		return certRefno;
	}

	public void setCertRefno(String certRefno) {
		this.certRefno = certRefno;
	}

	public String getCertAuthCode() {
		return certAuthCode;
	}

	public void setCertAuthCode(String certAuthCode) {
		this.certAuthCode = certAuthCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getValidityLen() {
		return validityLen;
	}

	public void setValidityLen(Integer validityLen) {
		this.validityLen = validityLen;
	}

	public String getP10() {
		return p10;
	}

	public void setP10(String p10) {
		this.p10 = p10;
	}

	public String getTmpPubKey() {
		return tmpPubKey;
	}

	public void setTmpPubKey(String tmpPubKey) {
		this.tmpPubKey = tmpPubKey;
	}

	public String getKeyLen() {
		return keyLen;
	}

	public void setKeyLen(String keyLen) {
		this.keyLen = keyLen;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getCspName() {
		return cspName;
	}

	public void setCspName(String cspName) {
		this.cspName = cspName;
	}

	public String getSm2Cryptprov() {
		return sm2Cryptprov;
	}

	public void setSm2Cryptprov(String sm2Cryptprov) {
		this.sm2Cryptprov = sm2Cryptprov;
	}

	public String getSignCert() {
		return signCert;
	}

	public void setSignCert(String signCert) {
		this.signCert = signCert;
	}

	public String getEncCert() {
		return encCert;
	}

	public void setEncCert(String encCert) {
		this.encCert = encCert;
	}

	public String getEncPri() {
		return encPri;
	}

	public void setEncPri(String encPri) {
		this.encPri = encPri;
	}

	public String getUkek() {
		return ukek;
	}

	public void setUkek(String ukek) {
		this.ukek = ukek;
	}

	public Integer getIsDCert() {
		if (certTemplate != null) {
			if (certTemplate.substring(certTemplate.length() - 2).equals("双证"))
				isDCert = 1;
			else
				isDCert = 0;
		}
		return isDCert;
	}

	public void setIsDCert(Integer isDCert) {
		this.isDCert = isDCert;
	}

	public String getCertTypeCN() {
		if(certType != null) {
			if("rsa_ca".equals(certType))
				certTypeCN = "RSA";
			else
				certTypeCN = "SM2";
		}
		return certTypeCN;
	}

	public void setCertTypeCN(String certTypeCN) {
		this.certTypeCN = certTypeCN;
	}


}
