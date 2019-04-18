package cn.com.infosec.netseal.webserver.ws.entity.json;

import com.alibaba.fastjson.JSON;

import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class JsonRequest {
	private String userId;
	private String userPwd;

	private String pdfData;
	private String ofdData;
	private String certDn;
	private String certDnServer;
	private String pageNum;
	private String keywords;
	private String x;
	private String y;
	private String qfz;
	private String biznum;
	private String barcodeX;
	private String barcodeY;
	private String barcodeWidth;
	private String barcodeContent;
	private String barcodePageNum;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getPdfData() {
		return pdfData;
	}

	public void setPdfData(String pdfData) {
		this.pdfData = pdfData;
	}

	public String getOfdData() {
		return ofdData;
	}

	public void setOfdData(String ofdData) {
		this.ofdData = ofdData;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getCertDnServer() {
		return certDnServer;
	}

	public void setCertDnServer(String certDnServer) {
		this.certDnServer = certDnServer;
	}

	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getQfz() {
		return qfz;
	}

	public void setQfz(String qfz) {
		this.qfz = qfz;
	}

	public String getBiznum() {
		return biznum;
	}

	public void setBiznum(String biznum) {
		this.biznum = biznum;
	}

	public String getBarcodeX() {
		return barcodeX;
	}

	public void setBarcodeX(String barcodeX) {
		this.barcodeX = barcodeX;
	}

	public String getBarcodeY() {
		return barcodeY;
	}

	public void setBarcodeY(String barcodeY) {
		this.barcodeY = barcodeY;
	}

	public String getBarcodeWidth() {
		return barcodeWidth;
	}

	public void setBarcodeWidth(String barcodeWidth) {
		this.barcodeWidth = barcodeWidth;
	}

	public String getBarcodeContent() {
		return barcodeContent;
	}

	public void setBarcodeContent(String barcodeContent) {
		this.barcodeContent = barcodeContent;
	}

	public String getBarcodePageNum() {
		return barcodePageNum;
	}

	public void setBarcodePageNum(String barcodePageNum) {
		this.barcodePageNum = barcodePageNum;
	}

	public static void main(String[] args) {
		JsonRequest req = new JsonRequest();
		req.setUserId("admin");
		req.setUserPwd(StringUtil.base64Encode("admin1"));
		req.setOfdData(StringUtil.base64Encode(FileUtil.getFile("f:/temp/ofd/2.ofd")));
		req.setCertDn("CN=test8");
		req.setCertDnServer("CN=NetSeal_Sign");
		req.setX("0");
		req.setY("0");
		req.setPageNum("1");
		req.setBarcodeX("100");
		req.setBarcodeY("100");
		req.setBarcodeContent("123213213213");
		req.setBarcodePageNum("1");

		String json = JSON.toJSONString(req);
		System.out.println(json);
	}

}
