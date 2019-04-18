package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 用户
 */
public class User extends Base implements Serializable {
	private static final long serialVersionUID = -2158646284221765149L;

	private String name;
	private String password;
	private String email;
	private String phone;
	private Long companyId;

	// 扩展
	private Company company;
	private List<Cert> certs;
	private List<Template> templates;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
			baos.write(calMac(name));
			baos.write(calMac(password));
			baos.write(calMac(email));
			baos.write(calMac(phone));
			baos.write(calMac(companyId));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<Cert> getCerts() {
		return certs;
	}

	public void setCerts(List<Cert> certs) {
		this.certs = certs;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" name:").append(name);
		sb.append(" password:").append(password);
		sb.append(" email:").append(email);
		sb.append(" phone:").append(phone);
		sb.append(" companyId:").append(companyId);
		sb.append(" generateTime:").append(getGenerateTime());
		sb.append(" updateTime:").append(getUpdateTime());
		sb.append(" mac:").append(getMac());
		
		return sb.toString();
	}
	
	
}
