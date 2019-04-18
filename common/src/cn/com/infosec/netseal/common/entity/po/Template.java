package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 印模
 */
public class Template extends Base {

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
	private Company company;
	private List<User> users;

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
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
	}

	public Integer getIsAuthCertGenSeal() {
		return isAuthCertGenSeal;
	}

	public void setIsAuthCertGenSeal(Integer isAuthCertGenSeal) {
		this.isAuthCertGenSeal = isAuthCertGenSeal;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Integer getTransparency() {
		return transparency;
	}

	public void setTransparency(Integer transparency) {
		this.transparency = transparency;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(name));
			baos.write(calMac(type));
			baos.write(calMac(status));
			baos.write(calMac(companyId));
			baos.write(calMac(isPhoto));
			baos.write(calMac(photoDataId));
			baos.write(calMac(photoPath));
			baos.write(calMac(isAuditReq));
			baos.write(calMac(isAuthCertGenSeal));
			baos.write(calMac(isAuthCertDownload));
			baos.write(calMac(isDownload));
			baos.write(calMac(transparency));
			baos.write(calMac(notBefor));
			baos.write(calMac(notAfter));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:");
		sb.append(name);
		sb.append(" type:");
		sb.append(type);
		sb.append(" status:");
		sb.append(status);
		sb.append(" companyId:");
		sb.append(companyId);
		sb.append(" isPhoto:");
		sb.append(isPhoto);
		sb.append(" photoDataId:");
		sb.append(photoDataId);
		sb.append(" photoPath:");
		sb.append(photoPath);
		sb.append(" isAuditReq:");
		sb.append(isAuditReq);
		sb.append(" isAuthCertGenSeal:");
		sb.append(isAuthCertGenSeal);
		sb.append(" isAuthCertDownload:");
		sb.append(isAuthCertDownload);
		sb.append(" isDownload:");
		sb.append(isDownload);
		sb.append(" transparency:");
		sb.append(transparency);
		sb.append(" notBefor:");
		sb.append(notBefor);
		sb.append(" notAfter:");
		sb.append(notAfter);
		sb.append(" genarateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" remark:");
		sb.append(remark);
		return sb.toString();
	}

	

	
}
