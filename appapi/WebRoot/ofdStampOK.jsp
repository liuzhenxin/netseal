<%@page import="cn.com.infosec.asn1.pkcs.Pfx"%>
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
	String path = getEncode(request.getParameter("OFD_DATA"));
	if(StringUtil.isBlank(path))
		throw new Exception("OFD路径无效");
		
	String SERVER_CERT_DN = request.getParameter("SERVER_CERT_DN");
	/* if(StringUtil.isBlank(SERVER_CERT_DN))
		throw new Exception("服务器证书DN不能为空"); */
	String CERT_DN = request.getParameter("CERT_DN");
	if(StringUtil.isBlank(CERT_DN))
		throw new Exception("证书DN不能为空");
	
	String OFD_PAGENUM = request.getParameter("OFD_PAGENUM");
	String OFD_KEYWORDS = request.getParameter("OFD_KEYWORDS");		
	OFD_KEYWORDS = getEncode(OFD_KEYWORDS);
		
	String OFD_X = request.getParameter("OFD_X");
	String OFD_Y = request.getParameter("OFD_Y");
	String OFD_QFZ = request.getParameter("OFD_QFZ");
	if(OFD_QFZ == null)
		OFD_QFZ = Constants.DEFAULT_STRING;
		
	if(StringUtil.isBlank(OFD_KEYWORDS) && StringUtil.isBlank(OFD_X) && StringUtil.isBlank(OFD_Y) && StringUtil.isBlank(OFD_QFZ))
		throw new Exception("盖章方式不能为空");
	
	String OFD_BIZNUM = getEncode(request.getParameter("OFD_BIZNUM"));
	if(OFD_BIZNUM==null)
		OFD_BIZNUM="";
	
	if (!path.endsWith(Constants.OFD_SUFFIX))
		throw new Exception("OFD后缀必须是 " + Constants.OFD_SUFFIX);
	
	String base64 = "";
	byte[] data = FileUtil.getFile(path);
	base64 = StringUtil.base64Encode(data);
	
	Properties pro = new Properties();
	pro.setProperty(Constants.OFD_DATA, base64);
	pro.setProperty(Constants.CERT_DN_SERVER, getEncode(SERVER_CERT_DN));
	pro.setProperty(Constants.CERT_DN, getEncode(CERT_DN));
	pro.setProperty(Constants.OFD_PAGENUM, OFD_PAGENUM);
	pro.setProperty(Constants.OFD_BIZNUM, OFD_BIZNUM);
	pro.setProperty(Constants.OFD_KEYWORDS, OFD_KEYWORDS);
	pro.setProperty(Constants.OFD_X, OFD_X);
	pro.setProperty(Constants.OFD_Y, OFD_Y);
	pro.setProperty(Constants.OFD_QFZ, OFD_QFZ);
	
	String ofdStampPath = getEncode(request.getParameter("ofdStampPath"));
	if(StringUtil.isBlank(ofdStampPath))
		throw new Exception("盖章后OFD文件输出路径无效");
	
	String ofdStamp = null;
	try {
		ofdStamp = NetSealClient.ofdStamp(pro);
		FileUtil.storeFile(ofdStampPath, StringUtil.base64Decode(ofdStamp));
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
				<td align=center colspan="2"><b>OFD盖章文件路径</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><%=ofdStampPath%></td>
			</tr>
		</table>
	</form>

</body>
</html>
