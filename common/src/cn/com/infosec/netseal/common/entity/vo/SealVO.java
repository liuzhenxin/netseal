package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 印章
 */
public class SealVO extends BaseVO {

	private String name;
	private Long templateId;
	private Long auditId;
	private Long sysUserId;
	private Long userId;
	private Long certId;
	private Long companyId;
	private Integer type;
	private Integer isAuditReq; // 是否需要审批
	private Integer isAuthCertGenSeal;// 审核是否验证书
	private Integer isAuthCertDownload;// 下载验证证书 1是 0否
	private Integer isDownload;// 是否支持下载 1是 0否
	private Long photoDataId;
	private String photoPath;
	private Integer transparency;
	private Integer status;
	private Integer usedCount;
	private Integer usedLimit; // 最大签章次数（默认无限制）（0-999），0无限制，-1不能使用
	private Long notBefor;
	private Long notAfter;
	private Long downloadTime;
	private Integer photoWidth;
	private Integer photoHigh;
	private Long sealDataId;
	private String sealPath;
	private String remark;

	// 扩展
	private String companyName;
	private String templateName;
	private String usedLimitCn;
	private String downloadTimeCn;
	private String sysUserAccount;
	private String userAccount;
	private String sysUserName;
	private String userName;
	private String certDn;
	private String certSn;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

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

	public Long getCertId() {
		return certId;
	}

	public void setCertId(Long certId) {
		this.certId = certId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
		setTypeCn(getArrValue(typeArr, type));
	}

	public Integer getIsAuditReq() {
		return isAuditReq;
	}

	public void setIsAuditReq(Integer isAuditReq) {
		this.isAuditReq = isAuditReq;
		if(isAuditReq!=null)
			setIsAuditReqCn(getArrValue(judgeArr, isAuditReq));
	}

	public Integer getIsAuthCertDownload() {
		return isAuthCertDownload;
	}

	public void setIsAuthCertDownload(Integer isAuthCertDownload) {
		this.isAuthCertDownload = isAuthCertDownload;
		if(isAuthCertDownload!=null)
			setIsAuthCertDownloadCn(getArrValue(judgeArr, isAuthCertDownload));
	}

	public Integer getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(Integer isDownload) {
		this.isDownload = isDownload;
		if(isDownload!=null)
			setIsDownloadCn(getArrValue(judgeArr, isDownload));
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
		if(status!=null)
			setStatusCn(getArrValue(statusArr, status));
	}

	public Integer getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(Integer usedCount) {
		this.usedCount = usedCount;
	}

	public Integer getUsedLimit() {
		return usedLimit;
	}

	public void setUsedLimit(Integer usedLimit) {
		this.usedLimit = usedLimit;
		if(usedLimit!=null){
			if (usedLimit == -1)
				setUsedLimitCn("不能使用");
			else if (usedLimit == 0)
				setUsedLimitCn("不限制");
			else
				setUsedLimitCn(String.valueOf(usedLimit));
		}
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

	public Long getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Long downloadTime) {
		this.downloadTime = downloadTime;
		setDownloadTimeCn(DateUtil.getDate(downloadTime));
	}

	public String getDownloadTimeCn() {
		return downloadTimeCn;
	}

	public void setDownloadTimeCn(String downloadTimeCn) {
		this.downloadTimeCn = downloadTimeCn;
	}

	public Integer getPhotoWidth() {
		return photoWidth;
	}

	public void setPhotoWidth(Integer photoWidth) {
		this.photoWidth = photoWidth;
	}

	public Integer getPhotoHigh() {
		return photoHigh;
	}

	public void setPhotoHigh(Integer photoHigh) {
		this.photoHigh = photoHigh;
	}

	public String getSealPath() {
		return sealPath;
	}

	public void setSealPath(String sealPath) {
		this.sealPath = sealPath;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUsedLimitCn() {
		return usedLimitCn;
	}

	public void setUsedLimitCn(String usedLimitCn) {
		this.usedLimitCn = usedLimitCn;
	}

	public String getSysUserAccount() {
		return sysUserAccount;
	}

	public void setSysUserAccount(String sysUserAccount) {
		this.sysUserAccount = sysUserAccount;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getSysUserName() {
		return sysUserName;
	}

	public void setSysUserName(String sysUserName) {
		this.sysUserName = sysUserName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCertDn() {
		return CertUtil.transCertDn(certDn);
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public Long getPhotoDataId() {
		return photoDataId;
	}

	public void setPhotoDataId(Long photoDataId) {
		this.photoDataId = photoDataId;
	}

	public Long getSealDataId() {
		return sealDataId;
	}

	public void setSealDataId(Long sealDataId) {
		this.sealDataId = sealDataId;
	}

	public String getCertSn() {
		return CertUtil.transCertSn(certSn);
	}

	public void setCertSn(String certSn) {
		this.certSn = certSn;
	}

	public Integer getTransparency() {
		return transparency;
	}

	public void setTransparency(Integer transparency) {
		this.transparency = transparency;
	}

	public Integer getIsAuthCertGenSeal() {
		return isAuthCertGenSeal;
	}

	public void setIsAuthCertGenSeal(Integer isAuthCertGenSeal) {
		this.isAuthCertGenSeal = isAuthCertGenSeal;
		if(isAuthCertGenSeal!=null)
			setIsAuthCertGenSealCn(getArrValue(judgeArr, isAuthCertGenSeal));
	}

	

}