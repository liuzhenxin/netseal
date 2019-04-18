<%@page import="java.io.FileOutputStream"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.*"%>
<%@page import="cn.com.infosec.netseal.appapi.common.define.Constants"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.StringUtil"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.FileUtil"%>
<%@page import="java.util.Properties"%>
<%@page errorPage="error.jsp"%>
<%!public String getEncode(String str) throws Exception {
		return new String(str.getBytes("iso-8859-1"), "utf-8");
	}%>

<%
	String PDF_TEMPLATE_NAME = request.getParameter("PDF_TEMPLATE_NAME");
	if(StringUtil.isBlank(PDF_TEMPLATE_NAME))
		throw new Exception("PDF模板名称无效");
	
	String PDF_TEMPLATE_FIELD = request.getParameter("PDF_TEMPLATE_FIELD");
	if(PDF_TEMPLATE_FIELD==null)
		PDF_TEMPLATE_FIELD="";
	
	String SERVER_CERT_DN = request.getParameter("SERVER_CERT_DN");
	/* if(StringUtil.isBlank(SERVER_CERT_DN))
		throw new Exception("服务器证书DN不能为空"); */
	String CERT_DN = request.getParameter("CERT_DN");
	if(StringUtil.isBlank(CERT_DN))
		throw new Exception("证书DN不能为空");
	String PDF_PAGENUM = request.getParameter("PDF_PAGENUM");
	String PDF_KEYWORDS = request.getParameter("PDF_KEYWORDS");		
		PDF_KEYWORDS = getEncode(PDF_KEYWORDS);
	String PDF_X = request.getParameter("PDF_X");
	String PDF_Y = request.getParameter("PDF_Y");
	String PDF_QFZ = request.getParameter("PDF_QFZ");
	if(PDF_QFZ == null)
		PDF_QFZ = Constants.DEFAULT_STRING;
		
	if(StringUtil.isBlank(PDF_KEYWORDS) && StringUtil.isBlank(PDF_X) && StringUtil.isBlank(PDF_Y) && StringUtil.isBlank(PDF_QFZ))
		throw new Exception("盖章方式不能为空");
	
	String PDF_BIZNUM = getEncode(request.getParameter("PDF_BIZNUM"));
	if(PDF_BIZNUM==null)
		PDF_BIZNUM="";
	
	String base64Pd = "";
	String photoPath = getEncode(request.getParameter("PHOTO_DATA"));
	if (StringUtil.isNotBlank(photoPath)) {
		byte[] photoData = FileUtil.getFile(photoPath);
		base64Pd = StringUtil.base64Encode(photoData);
	}
	
	String BARCODE_X = request.getParameter("BARCODE_X");
	String BARCODE_Y = request.getParameter("BARCODE_Y");
	String BARCODE_WIDTH = request.getParameter("BARCODE_WIDTH");
	String BARCODE_CONTENT = getEncode(request.getParameter("BARCODE_CONTENT"));
	String BARCODE_PAGENUM = request.getParameter("BARCODE_PAGENUM");
	
	Properties pro = new Properties();
	pro.setProperty(Constants.PDF_TEMPLATE_NAME, getEncode(PDF_TEMPLATE_NAME));
	pro.setProperty(Constants.PDF_TEMPLATE_FIELD, getEncode(PDF_TEMPLATE_FIELD));
	pro.setProperty(Constants.CERT_DN_SERVER, getEncode(SERVER_CERT_DN));
	pro.setProperty(Constants.CERT_DN, getEncode(CERT_DN));
	pro.setProperty(Constants.PHOTO_DATA, getEncode(base64Pd));
	pro.setProperty(Constants.PDF_PAGENUM, PDF_PAGENUM);
	pro.setProperty(Constants.PDF_KEYWORDS, PDF_KEYWORDS);
	pro.setProperty(Constants.PDF_X, PDF_X);
	pro.setProperty(Constants.PDF_Y, PDF_Y);
	pro.setProperty(Constants.PDF_QFZ, PDF_QFZ);
	pro.setProperty(Constants.PDF_BIZNUM, PDF_BIZNUM);
	
	pro.setProperty(Constants.PDF_BARCODE_X, BARCODE_X);
	pro.setProperty(Constants.PDF_BARCODE_Y, BARCODE_Y);
	pro.setProperty(Constants.PDF_BARCODE_WIDTH, BARCODE_WIDTH);
	pro.setProperty(Constants.PDF_BARCODE_CONTENT, BARCODE_CONTENT);
	pro.setProperty(Constants.PDF_BARCODE_PAGENUM, BARCODE_PAGENUM);
	
	String pdfStamp = null;
	String pdfStampPath = getEncode(request.getParameter("pdfStampPath"));
	if(StringUtil.isBlank(pdfStampPath))
		throw new Exception("盖章后PDF文件输出路径无效");
	
	try {
		pdfStamp = NetSealClient.pdfStampTemplate(pro);
		FileUtil.storeFile(pdfStampPath, StringUtil.base64Decode(pdfStamp));
	} catch (Exception e) {
		throw e;
	}
%>

<html>
<head>
<title>信安世纪签章API演示系统</title>
<link rel="stylesheet" type="text/css" href="css.css">
</head>
<body bgcolor="#FFFFFF">

	<table width="100%" border="0" cellspacing="4" cellpadding="2">
		<tr bgcolor="#336699">
			<td>
				<div align="center" class="hei14">
					<b><font color="#FFFFFF">信安世纪签章API演示系统</font> </b>
				</div>
			</td>
		</tr>
	</table>
	<p>&nbsp;</p>

	<form name="free" action="authcard2.jsp" method="post">
		<table width="60%" border="0" cellpadding="2" class="top"
			cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>pdf盖章文件路径</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><area><%=pdfStampPath%></area></td>
			</tr>
		</table>
	</form>

</body>
</html>
