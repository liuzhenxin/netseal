<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.*"%>
<%@page import="cn.com.infosec.netseal.appapi.common.define.Constants"%>
<%@page import="java.util.Properties"%>
<%@page errorPage="error.jsp"%>
<%!public String getEncode(String str) throws Exception {
		return new String(str.getBytes("iso-8859-1"), "utf-8");
	}%>
<%
	Properties pro = new Properties();
	pro.setProperty(Constants.OP_LOG_ACCOUNT, getEncode(request.getParameter("OP_LOG_ACCOUNT")));
	pro.setProperty(Constants.OP_LOG_TYPE, getEncode(request.getParameter("OP_LOG_TYPE")));
	pro.setProperty(Constants.OP_LOG_TIME, request.getParameter("OP_LOG_TIME"));
	pro.setProperty(Constants.OP_LOG_RETURN_CODE, getEncode(request.getParameter("OP_LOG_RETURN_CODE")));
	pro.setProperty(Constants.OP_LOG_ERR_MSG, getEncode(request.getParameter("OP_LOG_ERR_MSG")));

	try {
		NetSealClient.addOperateLog(pro); 
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
				<td align=center colspan="2"><b>添加交易日志</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right>结果：</td>
				<td>成功!</td>
			</tr>
		</table>
	</form>

</body>
</html>
