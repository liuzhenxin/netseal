package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;


/**
 * 印章申请
 */
public class Request extends Base {

	private String name;
	private Long templateId;
	private Long photoDataId;
	private String photoPath;
	private Integer transparency; //图片透明度
	private Long notBefor;
	private Long notAfter;
	private Long userId;// 签章人ID
	private Long certId;
	private String remark;

	// 扩展
	private Template template;
	private User user;
	private Cert cert;

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
	}

	public Long getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Long notAfter) {
		this.notAfter = notAfter;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getPhotoDataId() {
		return photoDataId;
	}

	public void setPhotoDataId(Long photoId) {
		this.photoDataId = photoId;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
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
			baos.write(calMac(templateId));
			baos.write(calMac(photoDataId));
			baos.write(calMac(photoPath));
			baos.write(calMac(transparency));
			baos.write(calMac(notBefor));
			baos.write(calMac(notAfter));
			baos.write(calMac(userId));
			baos.write(calMac(certId));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(remark));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:").append(name);
		sb.append(" photoDataId:").append(templateId);
		sb.append(" ").append(photoDataId);
		sb.append(" photoPath:").append(photoPath);
		sb.append(" notBefor:").append(notBefor);
		sb.append(" notAfter:").append(notAfter);
		sb.append(" userId:").append(userId);
		sb.append(" certId:").append(certId);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" remark:").append(remark);
		sb.append(" mac:").append(getMac());
		
		return sb.toString();
	}
	
	
	
	public String getRequestAuditString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append(name);
		sb.append(templateId);
		sb.append(notBefor);
		sb.append(notAfter);
		sb.append(userId);
		sb.append(notBefor);
		sb.append(notAfter);
		return sb.toString();
	}

	
}
