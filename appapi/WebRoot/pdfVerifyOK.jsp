<%@page import="java.io.FileOutputStream"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.*"%>
<%@page import="cn.com.infosec.netseal.appapi.common.define.Constants"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.FileUtil"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.StringUtil"%>
<%@page import="java.util.Properties"%>
<%@page errorPage="error.jsp"%>
<%!
	public String getEncode(String str) throws Exception{
		return new String(str.getBytes("iso-8859-1"),"utf-8");
	}
%>
<%
	String path = getEncode(request.getParameter("PDF_DATA"));
	if (!path.endsWith(Constants.PDF_SUFFIX))
		throw new Exception("file suffix must " + Constants.PDF_SUFFIX);

	byte[] data = FileUtil.getFile(path);
	String base64 = StringUtil.base64Encode(data);
	
	String checkSealDate = request.getParameter("PDF_CHECK_SEAL_DATE");
	String checkCertDate = request.getParameter("PDF_CHECK_CERT_DATE");

	Properties pro = new Properties();
	pro.setProperty(Constants.PDF_DATA, base64);
	pro.setProperty(Constants.PDF_CHECK_SEAL_DATE, checkSealDate);
	pro.setProperty(Constants.PDF_CHECK_CERT_DATE, checkCertDate);

	boolean result = false;
	try {
		result = NetSealClient.verifyPdfStamp(pro);
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
		<table width="60%" border="0" cellpadding="2" class="top" cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>PDF验章</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2">结果:<%=result%></td>
			</tr>
		</table>
	</form>

</body>
</html>
