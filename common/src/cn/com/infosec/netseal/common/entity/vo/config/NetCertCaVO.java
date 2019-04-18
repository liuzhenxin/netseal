package cn.com.infosec.netseal.common.entity.vo.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.com.infosec.netseal.common.util.StringUtil;

public class NetCertCaVO {
	private String certType;
	private String transIP;
	private int transPort;
	private String hsmName;
	private String keyIdx;
	private String pwd;
	private String signAlgName;
	private String signCert;
	private String chanelEncryptName;
	private String trustStore;
	private String trustPassword;
	private String protocolName;
	private String country;
	private String template;// 英文逗号分隔
	private String templateCn;// 英文逗号分隔

	private List<String> templateList;
	private int isGenUuid;// 是否ca生成uuid 0否 1是
	private Integer isDCert;// 是否是双证 0不是 1是
	private String certDn;

	private List<String> templateCnList;

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getTransIP() {
		return transIP;
	}

	public void setTransIP(String transIP) {
		this.transIP = transIP;
	}

	public int getTransPort() {
		return transPort;
	}

	public void setTransPort(int transPort) {
		this.transPort = transPort;
	}

	public String getHsmName() {
		return hsmName;
	}

	public void setHsmName(String hsmName) {
		this.hsmName = hsmName;
	}

	public String getKeyIdx() {
		return keyIdx;
	}

	public void setKeyIdx(String keyIdx) {
		this.keyIdx = keyIdx;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSignAlgName() {
		return signAlgName;
	}

	public void setSignAlgName(String signAlgName) {
		this.signAlgName = signAlgName;
	}

	public String getSignCert() {
		return signCert;
	}

	public void setSignCert(String signCert) {
		this.signCert = signCert;
	}

	public String getChanelEncryptName() {
		return chanelEncryptName;
	}

	public void setChanelEncryptName(String chanelEncryptName) {
		this.chanelEncryptName = chanelEncryptName;
	}

	public String getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	public String getTrustPassword() {
		return trustPassword;
	}

	public void setTrustPassword(String trustPassword) {
		this.trustPassword = trustPassword;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public String getCountry() {
		return country;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTemplateCn() {
		if (StringUtil.isNotBlank(template)) {
			String[] templateList = template.split(",");
			List<String> templateCnList = new ArrayList<String>();
			for (String temp : templateList) {
				if ("".equals(temp))
					continue;
				String sub = temp.substring(temp.length() - 2);
				if (sub.equals("_s")) {
					templateCnList.add(temp.substring(0, temp.length() - 2) + "_单证");
				} else if (sub.equals("_d")) {
					templateCnList.add(temp.substring(0, temp.length() - 2) + "_双证");
				} else
					templateCnList.add(temp);
			}
			templateCn = StringUtils.join(templateCnList.toArray(), ",");
		}
		return templateCn;
	}

	public void setTemplateCn(String templateCn) {
		this.templateCn = templateCn;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<String> getTemplateList() {
		return templateList;
	}

	public void setTemplateList(List<String> templateList) {
		this.templateList = templateList;
	}

	public int getIsGenUuid() {
		return isGenUuid;
	}

	public void setIsGenUuid(int isGenUuid) {
		this.isGenUuid = isGenUuid;
	}

	public List<String> getTemplateCnList() {
		if (templateList != null && templateList.size() > 0) {
			templateCnList = new ArrayList<String>();
			for (String temp : templateList) {
				if ("".equals(temp))
					continue;
				String sub = temp.substring(temp.length() - 2, temp.length());
				if (sub.equals("_s")) {
					templateCnList.add(temp.substring(0, temp.length() - 2) + "_单证");
				} else if (sub.equals("_d")) {
					templateCnList.add(temp.substring(0, temp.length() - 2) + "_双证");
				} else
					templateCnList.add(temp);
			}
		}
		return templateCnList;
	}

	public void setTemplateCnList(List<String> templateCnList) {
		this.templateCnList = templateCnList;
	}

	public Integer getIsDCert() {
		return isDCert;
	}

	public void setIsDCert(Integer isDCert) {
		this.isDCert = isDCert;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" hsmName: " + hsmName);
		sb.append(" transIP: " + getTransIP());
		sb.append(" transPort: " + getTransPort());
		sb.append(" keyIdx: " + keyIdx);
		sb.append(" pwd: " + pwd);
		sb.append(" signAlgName: " + signAlgName);
		sb.append(" signCert: " + signCert);
		sb.append(" chanelEncryptName: " + chanelEncryptName);
		sb.append(" trustStore: " + trustStore);
		sb.append(" trustPassword: " + trustPassword);
		sb.append(" protocolName: " + protocolName);
		sb.append(" country: " + country);
		sb.append(" isGenUuid: " + isGenUuid);

		return sb.toString();
	}
}
