package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 系统用户
 * 
 */
public class SysUser extends Base implements Serializable {
	private static final long serialVersionUID = -2158566284221789562L;

	private String account;
	private String name;
	private String password;	
	private Long roleId;
	private Integer status;
	private Integer failedNum;
	private Integer changePass; // 密码0修改 1已修改
	private Long companyId;
	private String tokenSeed;//手机令牌种子
	
	// 扩展
	private Role role;
	private Company company;
	private Cert cert;

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

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(account));
			baos.write(calMac(name));
			baos.write(calMac(password));
			baos.write(calMac(roleId));
			baos.write(calMac(status));
			baos.write(calMac(failedNum));
			baos.write(calMac(changePass));
			baos.write(calMac(companyId));
			baos.write(calMac(tokenSeed));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Cert getCert() {
		return cert;
	}

	public void setCert(Cert cert) {
		this.cert = cert;
	}
	
	public String getTokenSeed() {
		return tokenSeed;
	}

	public void setTokenSeed(String tokenSeed) {
		this.tokenSeed = tokenSeed;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" account:").append(account);
		sb.append(" name:").append(name);
		sb.append(" password:").append(password);
		sb.append(" roleId:").append(roleId);
		sb.append(" status:").append(status);
		sb.append(" failedNum:").append(failedNum);
		sb.append(" changePass:").append(changePass);
		sb.append(" companyId:").append(companyId);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" mac:").append(getMac());
		
		return sb.toString();
	}
	
	

}