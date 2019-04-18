<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.*"%>
<%
	String signPlain = request.getParameter("signPlain").trim();
	String x509Cert = request.getParameter("x509Cert");
	byte [] toSignData = StringUtil.getBytes(signPlain);
	String hashAlg = OidUtil.getHashAlg(CertUtil.parseCert(x509Cert.getBytes()).getSigAlgOID()).toLowerCase().trim();
	String result = "{'success':'true', "+"'toSignData':'"+ StringUtil.base64Encode(toSignData) + "', 'hashAlg':'" + hashAlg + "'}";
	response.getWriter().write(result);
%>



