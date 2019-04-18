package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 印章审核
 */
public class AuditVO extends BaseVO {

	private String name;
	private Long templateId;
	private Long notBefor;
	private Long notAfter;
	private Long sysUserId;
	private Long userId;
	private Long certId;
	private Long companyId;
	private Integer usedLimit;
	private String remark;

	// 扩展
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
		this.setNotAfterCn(DateUtil.getDate(notAfter));
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

	public String getMac() {
		return "";
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

	public Long getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Long sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		sb.append(companyName);
		sb.append(" ");
		sb.append(templateId);
		sb.append(" ");
		sb.append(notBefor);
		sb.append(" ");
		sb.append(notAfter);
		sb.append(" ");
		sb.append(userId);
		sb.append(" ");
		sb.append(sysUserId);
		return sb.toString();
	}
}
