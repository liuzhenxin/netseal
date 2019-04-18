package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.DateUtil;

public class BaseVO {

	// 继承属性
	private Long id;
	private Long generateTime;
	private Long updateTime;
	private String mac;
	private boolean sealMac;

	// 日志文件属性
	private String fileName;
	private String filePath;
	private Long fileSize;
	private Long fileTime;

	// 中文描述
	private String fileTimeCn;
	private String notBeforCn;
	private String notAfterCn;
	private String generateTimeCn;
	private String updateTimeCn;
	private String downloadTimeCn;
	private String statusCn;
	private String typeCn;
	private String usedLimitCn;
	private String isPhotoCn;// 是否有图片
	private String isAuditReqCn;// 申请是否审核
	private String isAuthCertGenSealCn; // 生成是否审核
	private String isAuthCertDownloadCn; // 下载是否审核
	private String isDownloadCn; // 是否支持下载

	// 常量数组
	protected static final String[] judgeArr = new String[] { "否", "是" };
	protected static final String[] statusArr = new String[] { "停用", "启用" };
	protected static final String[] typeArr = new String[] { "N/A", "单位公章", "个人章","手写章" };

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public boolean isSealMac() {
		return sealMac;
	}

	public void setSealMac(boolean sealMac) {
		this.sealMac = sealMac;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Long getFileTime() {
		return fileTime;
	}

	public void setFileTime(Long fileTime) {
		this.fileTime = fileTime;
		setFileTimeCn(DateUtil.getDate(fileTime));
	}

	public String getFileTimeCn() {
		return fileTimeCn;
	}

	public void setFileTimeCn(String fileTimeCn) {
		this.fileTimeCn = fileTimeCn;
	}

	public String getNotBeforCn() {
		return notBeforCn;
	}

	public void setNotBeforCn(String notBeforCn) {
		this.notBeforCn = notBeforCn;
	}

	public String getNotAfterCn() {
		return notAfterCn;
	}

	public void setNotAfterCn(String notAfterCn) {
		this.notAfterCn = notAfterCn;
	}

	public String getGenerateTimeCn() {
		return generateTimeCn;
	}

	public void setGenerateTimeCn(String generateTimeCn) {
		this.generateTimeCn = generateTimeCn;
	}

	public String getStatusCn() {
		return statusCn;
	}

	public void setStatusCn(String statusCn) {
		this.statusCn = statusCn;
	}

	public String getTypeCn() {
		return typeCn;
	}

	public void setTypeCn(String typeCn) {
		this.typeCn = typeCn;
	}

	public String getIsAuditReqCn() {
		return isAuditReqCn;
	}

	public void setIsAuditReqCn(String isAuditReqCn) {
		this.isAuditReqCn = isAuditReqCn;
	}

	public String getIsAuthCertGenSealCn() {
		return isAuthCertGenSealCn;
	}

	public void setIsAuthCertGenSealCn(String isAuthCertGenSealCn) {
		this.isAuthCertGenSealCn = isAuthCertGenSealCn;
	}

	public String getIsAuthCertDownloadCn() {
		return isAuthCertDownloadCn;
	}

	public void setIsAuthCertDownloadCn(String isAuthCertDownloadCn) {
		this.isAuthCertDownloadCn = isAuthCertDownloadCn;
	}

	public String getIsDownloadCn() {
		return isDownloadCn;
	}

	public void setIsDownloadCn(String isDownloadCn) {
		this.isDownloadCn = isDownloadCn;
	}

	public String getUpdateTimeCn() {
		return updateTimeCn;
	}

	public void setUpdateTimeCn(String updateTimeCn) {
		this.updateTimeCn = updateTimeCn;
	}

	public String getDownloadTimeCn() {
		return downloadTimeCn;
	}

	public void setDownloadTimeCn(String downloadTimeCn) {
		this.downloadTimeCn = downloadTimeCn;
	}

	public String getIsPhotoCn() {
		return isPhotoCn;
	}

	public void setIsPhotoCn(String isPhotoCn) {
		this.isPhotoCn = isPhotoCn;
	}

	public String getUsedLimitCn() {
		return usedLimitCn;
	}

	public void setUsedLimitCn(String usedLimitCn) {
		this.usedLimitCn = usedLimitCn;
	}

	protected String getArrValue(String[] arr, int index) {
		int tmp = index;
		if (tmp >= arr.length || tmp <= 0)
			tmp = 0;

		return arr[tmp];
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
		setUpdateTimeCn(DateUtil.getDate(updateTime));
	}

	public Long getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(Long generateTime) {
		this.generateTime = generateTime;
		this.setGenerateTimeCn(DateUtil.getDate(generateTime));
	}

}
