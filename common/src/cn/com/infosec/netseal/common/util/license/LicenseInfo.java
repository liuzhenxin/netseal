package cn.com.infosec.netseal.common.util.license;

import java.util.Date;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class LicenseInfo {

	private String guid;
	private Date validTime_start;
	private Date validTime_end;
	private int maxThred;
	private int maxCertNum;
	private String bindMsg = "";
	private String softwareName = "NetSeal";
	private String licenceExtension = "";
	private String features = "";

	LicenseInfo() {
	}

	public String getSoftwareName() {
		return softwareName;
	}

	public String getLicenceExtension() {
		return licenceExtension;
	}

	public String getBindMsg() {
		return bindMsg;
	}

	public int getMaxCertNum() {
		return maxCertNum < 0 ? 0 : maxCertNum;
	}

	public int getMaxThred() {
		return maxThred;
	}

	public Date getValidTime_end() {
		return validTime_end;
	}

	public Date getValidTime_start() {
		return validTime_start;
	}

	public String getFeatures() {
		return features;
	}

	void setValidTime_start(Date validTime_start) {
		this.validTime_start = validTime_start;
	}

	void setValidTime_end(Date validTime_end) {
		this.validTime_end = validTime_end;
	}

	void setMaxThred(int maxThred) {
		this.maxThred = maxThred;
	}

	void setMaxCertNum(int maxCertNum) {
		this.maxCertNum = maxCertNum;
	}

	void setBindMsg(String bindMsg) {
		this.bindMsg = bindMsg;
	}

	void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}

	void setLicenceExtension(String licenceExtension) {
		this.licenceExtension = licenceExtension;
	}

	void setFeatures(String features) {
		this.features = features;
	}

	public String getGuid() {
		return guid;
	}

	void setGuid(String guid) {
		this.guid = guid;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("guid ").append(guid).append(" ").append("\r\n");
		sb.append("validTime_start ").append(validTime_start).append(" ").append("\r\n");
		sb.append("validTime_end ").append(validTime_end).append(" ").append("\r\n");
		sb.append("maxThred ").append(maxThred).append(" ").append("\r\n");
		sb.append("maxCertNum ").append(maxCertNum).append(" ").append("\r\n");
		sb.append("bindMsg ").append(bindMsg).append(" ").append("\r\n");
		sb.append("softwareName ").append(softwareName).append(" ").append("\r\n");
		sb.append("licenceExtension ").append(licenceExtension).append(" ").append("\r\n");
		sb.append("features ").append(features).append(" ").append("\r\n");
		return sb.toString();
	}

}