package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 印模
 */
public class TemplateVO extends BaseVO {

	private String name;
	private Integer type;
	private Integer status;// 0停用 1启用
	private Long companyId;
	private Integer isPhoto;// 是否上传图片
	private Long photoDataId;
	private String photoPath;
	private Integer isAuditReq;// 申请是否需要审核
	private Integer isAuthCertGenSeal;// 审核是否验证书
	private Integer isAuthCertDownload;// 下载是否验证书
	private Integer isDownload;// 印章是否支持下载
	private Integer transparency; //图片透明度
	private Long notBefor;
	private Long notAfter;
	private String remark;

	// 扩展
	private String companyName;
	private String userIds;
	private String userNames;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
		if(type!=null)
			setTypeCn(getArrValue(typeArr, type));
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
		if(status!=null)
			setStatusCn(getArrValue(statusArr, status));
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Integer getIsPhoto() {
		return isPhoto;
	}

	public void setIsPhoto(Integer isPhoto) {
		this.isPhoto = isPhoto;
			setIsPhotoCn(getArrValue(judgeArr, isPhoto));
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public Integer getIsAuditReq() {
		return isAuditReq;
	}

	public void setIsAuditReq(Integer isAuditReq) {
		this.isAuditReq = isAuditReq;
		if(isAuditReq!=null)
			setIsAuditReqCn(getArrValue(judgeArr, isAuditReq));
	}

	public Integer getIsAuthCertGenSeal() {
		return isAuthCertGenSeal;
	}

	public void setIsAuthCertGenSeal(Integer isAuthCertGenSeal) {
		this.isAuthCertGenSeal = isAuthCertGenSeal;
		if(isAuthCertGenSeal!=null)
			setIsAuthCertGenSealCn(getArrValue(judgeArr, isAuthCertGenSeal));
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
	
	public Integer getTransparency() {
		return transparency;
	}

	public void setTransparency(Integer transparency) {
		this.transparency = transparency;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMac() {
		return "";
	}

	public Long getPhotoDataId() {
		return photoDataId;
	}

	public void setPhotoDataId(Long photoDataId) {
		this.photoDataId = photoDataId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

}
