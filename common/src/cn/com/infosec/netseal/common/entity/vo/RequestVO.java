package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 印章申请
 */
public class RequestVO extends BaseVO {

	private String name;
	private Long templateId;
	private Long photoDataId;
	private String photoPath;
	private Integer transparency; //图片透明度
	private Long notBefor;
	private Long notAfter;
	private Long userId;// 签章人ID
	private Long certId;
	private Long companyId;
	private Integer status;// 0申请中 1审核通过 2审核不通过
	private String remark;

	// 扩展
	private Long sealId;
	private String sealPath;
	private String companyName;
	private String templateName;
	private String certDn;
	private String certSn;
	private String userName;
	private Integer usedLimit; // 最大签章次数（默认无限制）（0-999），0无限制，-1不能使用
	private Long sysUserId;// 管理员ID
	private String x509Cert;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getNotBefor() {
		return notBefor;
	}

	public Long getCertId() {
		return certId;
	}

	public void setCertId(Long certId) {
		this.certId = certId;
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

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
		setStatusCn(getArrValue(statusArr, status));
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMac() {
		return "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
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

	public Long getSealId() {
		return sealId;
	}

	public void setSealId(Long sealId) {
		this.sealId = sealId;
	}

	public String getSealPath() {
		return sealPath;
	}

	public void setSealPath(String sealPath) {
		this.sealPath = sealPath;
	}

	public Long getPhotoDataId() {
		return photoDataId;
	}

	public void setPhotoDataId(Long photoId) {
		this.photoDataId = photoId;
	}

	public Integer getUsedLimit() {
		return usedLimit;
	}

	public void setUsedLimit(Integer usedLimit) {
		this.usedLimit = usedLimit;
	}

	public Long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Long sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String getX509Cert() {
		return x509Cert;
	}

	public void setX509Cert(String x509Cert) {
		this.x509Cert = x509Cert;
	}

	public Integer getTransparency() {
		return transparency;
	}

	public void setTransparency(Integer transparency) {
		this.transparency = transparency;
	}

}
