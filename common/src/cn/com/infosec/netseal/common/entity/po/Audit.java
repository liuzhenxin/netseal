package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;


/**
 * 印章审核
 */
public class Audit extends Base {

	private String name;
	private Long templateId;
	private Long sysUserId;
	private Long userId;
	private Long certId;
	private Long notBefor;
	private Long notAfter;
	private Integer usedLimit;
	private String remark;

	// 扩展
	private Template template;
	private SysUser sysUser;
	private User user;
	private Cert cert;

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

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getCertId() {
		return certId;
	}

	public void setCertId(Long certId) {
		this.certId = certId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public Integer getUsedLimit() {
		return usedLimit;
	}

	public void setUsedLimit(Integer usedLimit) {
		this.usedLimit = usedLimit;
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

	public Long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Long sysUserId) {
		this.sysUserId = sysUserId;
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

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(name));
			baos.write(calMac(templateId));
			baos.write(calMac(sysUserId));
			baos.write(calMac(userId));
			baos.write(calMac(certId));
			baos.write(calMac(notBefor));
			baos.write(calMac(notAfter));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
			baos.write(calMac(usedLimit));
			baos.write(calMac(remark));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		
		return CryptoHandler.hashEnc64(baos.toByteArray());
		
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" name:");
		sb.append(name);
		sb.append(" templateId:");
		sb.append(templateId);
		sb.append(" notBefor:");
		sb.append(notBefor);
		sb.append(" notAfter:");
		sb.append(notAfter);
		sb.append(" sysUserId:");
		sb.append(sysUserId);
		sb.append(" userId:");
		sb.append(userId);
		sb.append(" certId:");
		sb.append(certId);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" usedLimit:");
		sb.append(usedLimit);
		sb.append(" remark:");
		sb.append(remark);
		sb.append(" mac:");
		sb.append(getMac());
		return sb.toString();
	}
}
