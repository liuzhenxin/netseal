package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 证书链
 */
public class CertChainVO extends BaseVO {

	private Long pid;
	private String certDn;
	private String certIssueDn;
	private String certPath;
	private Long certDataId;
	private Long notBefor;
	private Long notAfter;

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

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getCertDn() {
		return CertUtil.transCertDn(certDn);
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

	public String getMac() {
		return "";
	}

	public Long getCertDataId() {
		return certDataId;
	}

	public void setCertDataId(Long certDataId) {
		this.certDataId = certDataId;
	}

}
