<%@page import="cn.com.infosec.netseal.appapi.common.define.Constants"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.*"%>
<%@page import="java.io.*"%>
<%@page errorPage="error.jsp"%>
<%
	SysProperty sp = new SysProperty("10.20.87.55", 8451);
	sp.setMinConn(0);
	sp.setMaxConn(10);
	sp.setGetConnTimeout(30);
	sp.setReadTimeout(30);

	String path = request.getRealPath("/");
	if (!(path.substring(path.length() - 1).equals(System.getProperty("file.separator"))))
		path = path + System.getProperty("file.separator");

	//配置为SSL通讯
	//sp.setTrustStore(path + "sslserver.jks");
	//sp.setTrustStorePwd("11111111");
	//sp.setClientStore(path + "sslserver.jks");
	//sp.setClientStorePwd("11111111");

	// 配置为数字信封通讯
	//sp.setServerCertPath(path + "server.cer");
	//sp.setClientCertPath(path + "clientRSA.cer");
	//sp.setClientKeyPath(path + "clientRSA.pfx");
	//sp.setClientKeyPwd("11111111");

	NetSealClient.setAPIProperty(sp);

	//FileOutputStream fos = new FileOutputStream("f:/temp/appapi.log");
	//NetSealClient.setLogWriter(new PrintWriter(fos));
%>
<html>
<head>
<title>信安世纪签章API演示系统</title>
</head>
<frameset rows="130,*,30" cols="*" border="0" framespacing="0">
	<frame src="top.jsp" marginwidth="0" marginheight="0" frameborder="NO" scrolling="NO" name="top">
	<frameset cols="200,*" rows="*">
		<frame src="left.jsp" name="left" target="right" scrolling="yes" marginwidth="0" marginheight="0" frameborder="NO">
		<frame src="right.jsp" name="right" scrolling="default" frameborder="NO" marginwidth="0" marginheight="0">
	</frameset>
	<frame src="bottom.jsp" name="bottom" marginwidth="0" marginheight="0" scrolling="NO" frameborder="NO">
</frameset>
<noframes>
	<body bgcolor="#FFFFFF">
	</body>
</noframes>
</html>