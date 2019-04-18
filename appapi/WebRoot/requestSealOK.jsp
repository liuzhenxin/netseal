<%@page import="java.io.FileOutputStream"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.*"%>
<%@page import="cn.com.infosec.netseal.appapi.common.define.Constants"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.FileUtil"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.StringUtil"%>
<%@page import="java.util.Properties"%>
<%@page errorPage="error.jsp"%>
<%!public String getEncode(String str) throws Exception {
		return new String(str.getBytes("iso-8859-1"), "utf-8");
	}
%>

<%
String path = getEncode(request.getParameter("TEMPLATE_PHOTO"));
String base64 = "";
if (!"".equals(path)) {
	if (!path.endsWith(Constants.PHOTO_SUFFIX))
		throw new Exception("file suffix must " + Constants.PHOTO_SUFFIX);

	byte[] data = FileUtil.getFile(path);
	base64 = StringUtil.base64Encode(data);
}
	Properties pro = new Properties();
	pro.setProperty(Constants.SEAL_NAME, getEncode(request.getParameter("SEAL_NAME")));
	pro.setProperty(Constants.TEMPLATE_NAME, getEncode(request.getParameter("TEMPLATE_NAME")));
	pro.setProperty(Constants.CERT_DN, getEncode(request.getParameter("CERT_DN")));
	pro.setProperty(Constants.PHOTO_DATA, getEncode(base64));

	try {
		NetSealClient.requestSeal(pro);
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
				<td align=center colspan="2"><b>印章申请</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2">成功</td>
			</tr>
		</table>
	</form>

</body>
</html>
