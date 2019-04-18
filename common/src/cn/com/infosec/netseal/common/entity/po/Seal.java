package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;


/**
 * 印章
 */
public class Seal extends Base {

	private String name;
	private Long templateId;
	private Long auditId;
	private Long sysUserId;
	private Long userId;
	private Long certId;
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
	private Template template;
	private SysUser sysUser;
	private User user;
	private Cert cert;
	private SealData sealData;
	private Audit audit;
	private PhotoData photoData;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIsAuditReq() {
		return isAuditReq;
	}

	public void setIsAuditReq(Integer isAuditReq) {
		this.isAuditReq = isAuditReq;
	}

	public Integer getIsAuthCertDownload() {
		return isAuthCertDownload;
	}

	public void setIsAuthCertDownload(Integer isAuthCertDownload) {
		this.isAuthCertDownload = isAuthCertDownload;
	}

	public Integer getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(Integer isDownload) {
		this.isDownload = isDownload;
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

	public Long getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Long downloadTime) {
		this.downloadTime = downloadTime;
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

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(name));
			baos.write(calMac(templateId));
			baos.write(calMac(auditId));
			baos.write(calMac(sysUserId));
			baos.write(calMac(userId));
			baos.write(calMac(certId));
			baos.write(calMac(type));
			baos.write(calMac(isAuditReq));
			baos.write(calMac(isAuthCertGenSeal));
			baos.write(calMac(isAuthCertDownload));
			baos.write(calMac(isDownload));
			baos.write(calMac(photoDataId));
			baos.write(calMac(photoPath));
			baos.write(calMac(transparency));
			baos.write(calMac(status));
			baos.write(calMac(usedCount));
			baos.write(calMac(usedLimit));
			baos.write(calMac(notBefor));
			baos.write(calMac(notAfter));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(downloadTime));
			baos.write(calMac(photoWidth));
			baos.write(calMac(photoHigh));
			baos.write(calMac(sealDataId));
			baos.write(calMac(sealPath));
			baos.write(calMac(remark));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
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

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Cert getCert() {
		return cert;
	}

	public void setCert(Cert cert) {
		this.cert = cert;
	}

	public SealData getSealData() {
		return sealData;
	}

	public void setSealData(SealData sealData) {
		this.sealData = sealData;
	}

	public Audit getAudit() {
		return audit;
	}

	public void setAudit(Audit audit) {
		this.audit = audit;
	}

	public PhotoData getPhotoData() {
		return photoData;
	}

	public void setPhotoData(PhotoData photoData) {
		this.photoData = photoData;
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
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:");
		sb.append(name);
		sb.append(" templateId:");
		sb.append(templateId);
		sb.append(" auditId:");
		sb.append(auditId);
		sb.append(" sysUserId:");
		sb.append(sysUserId);
		sb.append(" userId:");
		sb.append(userId);
		sb.append(" certId:");
		sb.append(certId);
		sb.append(" type:");
		sb.append(type);
		sb.append(" isAuditReq:");
		sb.append(isAuditReq);
		sb.append(" isAuthCertDownload:");
		sb.append(isAuthCertDownload);
		sb.append(" isDownload:");
		sb.append(isDownload);
		sb.append(" photoDataId:");
		sb.append(photoDataId);
		sb.append(" photoPath:");
		sb.append(photoPath);
		sb.append(" transparency:");
		sb.append(transparency);
		sb.append(" status:");
		sb.append(status);
		sb.append(" usedCount:");
		sb.append(usedCount);
		sb.append(" usedLimit:");
		sb.append(usedLimit);
		sb.append(" notBefor:");
		sb.append(notBefor);
		sb.append(" notAfter:");
		sb.append(notAfter);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" downloadTime:");
		sb.append(downloadTime);
		sb.append(" photoWidth:");
		sb.append(photoWidth);
		sb.append(" photoHigh:");
		sb.append(photoHigh);
		sb.append(" sealDataId:");
		sb.append(sealDataId);
		sb.append(" sealPath:");
		sb.append(sealPath);
		sb.append(" remark:");
		sb.append(remark);
		sb.append(" mac").append(getMac());
		return sb.toString();
	}

	

	

}