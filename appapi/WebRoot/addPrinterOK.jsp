<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.*"%>
<%@page import="cn.com.infosec.netseal.appapi.common.define.Constants"%>
<%@page import="java.util.Properties"%>
<%@page errorPage="error.jsp"%>
<%!
	public String getEncode(String str) throws Exception{
		return new String(str.getBytes("iso-8859-1"),"utf-8");
	}
%>
<%
	Properties pro = new Properties();
	String limit=getEncode(request.getParameter("PRINTER_LIMIT"));
	String num=getEncode(request.getParameter("PRINTER_NUM"));
	pro.setProperty(Constants.PRINTER_NAME, getEncode(request.getParameter("PRINTER_NAME")));
	pro.setProperty(Constants.SEAL_NAME, getEncode(request.getParameter("SEAL_NAME")));
	pro.setProperty(Constants.SEAL_TYPE, getEncode(request.getParameter("SEAL_TYPE")));
	pro.setProperty(Constants.USER_NAME, getEncode(request.getParameter("USER_NAME")));
	pro.setProperty(Constants.PRINTER_NUM,num );
	pro.setProperty(Constants.PRINTER_LIMIT, limit);
	pro.setProperty(Constants.PRINTER_PWD, getEncode(request.getParameter("PRINTER_PWD"))); 
try {
		
		int num_int=Integer.parseInt(num);
		int limit_int=Integer.parseInt(limit);
		if (num_int < 0)
			throw new Exception("打印数值不能为负");
		
		if (limit_int < 0)
			throw new Exception("最大打印数值不能为负");
		
		if(num_int > limit_int)
			throw new Exception("打印数不可以大于最大打印数");
		
	} catch (Exception e) {
		throw e;
	}
	try {
		
		NetSealClient.addPrinter(pro);
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
				<td align=center colspan="2"><b>文档打印设置</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right>结果：</td>
				<td>成功!</td>
			</tr>
		</table>
	</form>

</body>
</html>
