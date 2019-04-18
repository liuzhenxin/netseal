package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 签证人注册的证书
 */
public class CertVO extends BaseVO {

	private String certDn;
	private String certSn;
	private String certIssueDn=Constants.DEFAULT_UNKNOWN_STRING;
	private Integer certUsage = Constants.USAGE_SIGNATURE; //1是签名证书,2是加密证书
	private String certPath;
	private Long notBefor;
	private Long notAfter;
	private Long certDataId;
	private Long userId;
	private Long sysUserId;

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
		setNotBeforCn(DateUtil.getDate(notBefor));
	}

	public Long getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Long notAfter) {
		this.notAfter = notAfter;
		setNotAfterCn(DateUtil.getDate(notAfter));
	}

	public String getMac() {
		return "";
	}

	public Long getCertDataId() {
		return certDataId;
	}

	public void setCertDataId(Long certDataId) {
		this.certDataId = certDataId;
	}
	
	public Integer getCertUsage() {
		return certUsage;
	}

	public void setCertUsage(Integer certUsage) {
		this.certUsage = certUsage;
	}


	// 重写
	public String getCertString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append(" ");
		sb.append(certDn);
		sb.append(" ");
		sb.append(certPath);
		sb.append(" ");
		sb.append(notBefor);
		sb.append(" ");
		sb.append(notAfter);
		sb.append(" ");
		return sb.toString();
	}


}
