package cn.com.infosec.netseal.common.entity.vo;

import java.io.Serializable;

import cn.com.infosec.netseal.common.util.CertUtil;

/**
 * 系统用户
 * 
 */
public class SysUserVO extends BaseVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String account;
	private String name;
	private String password;
	private Long roleId;
	private Integer status;
	private Integer failedNum;
	private Integer changePass;//密码0修改 1已修改
	private Long companyId;
	private String tokenSn;//令牌序列号
	private String tokenActiveCode;//令牌激活码
	private String tokenSeed;
	
	// 扩展属性
	private String roleName;
	private String companyName;
	private String certDn;
	private String certSn;
	private String x509Cert;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
		if(status==0)
			setStatusCn("停用");
		else 
			if(failedNum!=null && failedNum==6)
				setStatusCn("锁定");
			else
				setStatusCn("启用");
		
		
	}

	public Integer getFailedNum() {
		return failedNum;
	}

	public void setFailedNum(Integer failedNum) {
		this.failedNum = failedNum;
	}

	public Integer getChangePass() {
		return changePass;
	}

	public void setChangePass(Integer changePass) {
		this.changePass = changePass;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getMac() {
		return "";
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public String getTokenSn() {
		return tokenSn;
	}

	public void setTokenSn(String tokenSn) {
		this.tokenSn = tokenSn;
	}

	public String getTokenActiveCode() {
		return tokenActiveCode;
	}

	public void setTokenActiveCode(String tokenActiveCode) {
		this.tokenActiveCode = tokenActiveCode;
	}

	public String getTokenSeed() {
		return tokenSeed;
	}

	public void setTokenSeed(String tokenSeed) {
		this.tokenSeed = tokenSeed;
	}

	public String getX509Cert() {
		if (x509Cert != null)
			x509Cert = "-----BEGIN CERTIFICATE-----\n" + x509Cert + "\n-----END CERTIFICATE-----";

		return x509Cert;
	}

	public void setX509Cert(String x509Cert) {
		this.x509Cert = x509Cert;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append(" ");
		sb.append(account);
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		sb.append(password);
		sb.append(" ");
		sb.append(roleId);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(failedNum);
		sb.append(" ");
		sb.append(changePass);
		sb.append(" ");
		sb.append(companyId);
		sb.append(" ");
		sb.append(getGenerateTime());
		return sb.toString();
	}

}