package cn.com.infosec.netseal.common.entity.vo.report;

import java.util.List;

import cn.com.infosec.netseal.common.util.StringUtil;

public class ReportVO {

	private String reportNumber;
	private String reportPath;
	private String genDate;
	private String ip;
	private String version;// 版本号
	private String licenseNumber;// license数量
	private List<String> ipList;// 端口和IP对应关系
	private List<String> checkList;// 系统检测
	private List<String> resultList;// 巡检结果
	private long certNum;// 证书数量
	private String nodeNameCN;// HA当前节点名称

	public String getReportNumber() {
		return reportNumber;
	}

	public void setReportNumber(String reportNumber) {
		this.reportNumber = reportNumber;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public String getGenDate() {
		return genDate;
	}

	public void setGenDate(String genDate) {
		this.genDate = genDate;
	}

	public String getIp() {
		if (StringUtil.isBlank(ip))
			ip = "";
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public List<String> getIpList() {
		return ipList;
	}

	public void setIpList(List<String> ipList) {
		this.ipList = ipList;
	}

	public List<String> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<String> checkList) {
		this.checkList = checkList;
	}

	public List<String> getResultList() {
		return resultList;
	}

	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}

	public long getCertNum() {
		return certNum;
	}

	public void setCertNum(long certNum) {
		this.certNum = certNum;
	}

	public String getNodeNameCN() {
		return nodeNameCN;
	}

	public void setNodeNameCN(String nodeNameCN) {
		this.nodeNameCN = nodeNameCN;
	}

}
