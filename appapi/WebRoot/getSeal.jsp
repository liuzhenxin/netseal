<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="cn.com.infosec.netseal.appapi.common.util.*"%>
<html>
<head>
<title>信安世纪签章API演示系统</title>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display: none" codebase="cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display: none" codebase="cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>

<script src="jQuery-1.7.2.min.js"></script>
<script src="bootstrap.min.js"></script>
<script src="jquery.form.js"></script>
<link rel="stylesheet" type="text/css" href="bootstrap.min.css">
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

	<form name="free" action="getSealOK.jsp" method="post">
		<table width="60%" border="1" cellpadding="2" class="top"
			cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>获取印章</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">证书：</td>
				<td style="width:80%">
					<select id="cert_Dn" name="CERT_DN" style="width: 50%;"> </select>
					<input id="getCert" type="button" value="读取证书">
				</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">PIN码：</td>
				<td style="width:80%">
				    <input style="width: 50%;" onkeydown="if(event.keyCode==13){return false;}" type="password" id="pin">
				</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">证书DN签名数据：</td>
				<td style="width:80%">
				    <textarea id="signData" style="width: 50%;" name="SIGN_DATA"></textarea>
				</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td colspan=2 align=center>
					<input type="button" value="获取签名" id="getSignData" style="margin-right: 20px;">
					<input type="submit" value="提交">
				</td>
			</tr>
		</table>
		<input id="signPlain" type="hidden"> 
		<input id="X509Cert" type="hidden"> 
	</form>
</body>
<script type="text/javascript">
<!--[if lt IE 9]>
jQuery(function($) {
	var el;
	$("select").each(function() {
		el = $(this);
		el.data("origWidth", el.css("width"));
	}).focusin(function() {
		el = $(this);
		el.css("width", "auto");
	}).bind("blur change ", function() {
		el = $(this);
		el.css("width", el.data("origWidth"));
	});
});

var Enroll;
function VBATOA(v) {
	var tmp = new VBArray(v);
	return tmp.toArray();
}

function enumCerts() { //所有可用于签名的证书
	var ret = new Array();
	// sm2 cert
	var Count = Enroll.sm_skf_getCountOfCert();
	for (var i = 0; i < Count; i++) {
		var Cert = VBATOA(Enroll.sm_skf_getCertInfo(i));
		var c10 = Cert[10];
		if (c10 == 'AT_SIGNATURE')
			ret.push(Cert);
	}
	// rsa cert
	var Count1 = Enroll.rsa_csp_getCountOfCert();
	for (var i = 0; i < Count1; i++) {
		var Cert = VBATOA(Enroll.rsa_csp_getCertInfo(i));
		var c0 = Cert[0];
		var c7 = Cert[7];
		if (c0.indexOf('Microsoft') != 0 && c7 == 'AT_SIGNATURE')
			ret.push(Cert);
	}
	return ret;
}

//判断是否是rsa证书,如果是rsa证书返回rsa证书,否则返回null
function isRsaCert(cert) {
	if (cert.length == 12)
		return null;
	else
		return cert;
}

function readCertSignature(certs, certDN) {//返回选择的证书
	var c2;
	var Count = certs.length;
	for (var i = 0; i < Count; i++) {
		if (certs[i].length == 12)
			c2 = certs[i][5]; //主题
		else
			c2 = certs[i][2];

		if (c2 == certDN)
			return certs[i];
	}
}

$(function() {
	var ver = navigator.platform;
	if ('Win32' == ver)
		Enroll = document.getElementById('infosecEnroll_32');
	else
		Enroll = document.getElementById('infosecEnroll_64');

	var certs = enumCerts();//所有可用于签名的证书
	
	//读取U_KEY
	$("#getCert").click(
		function() {
			if (certs.length == 0) {
				$("#cert_Dn").empty();
				alert("没有读取到证书");
				return;
			}
			$("#cert_Dn").empty();
			for (var i = 0; i < certs.length; i++) {
				if (certs[i].length == 12)
					$("#cert_Dn").append("<option value='"+certs[i][5]+"'>" + certs[i][5] + "</option>");
				else {
					$("#cert_Dn").append("<option value='"+certs[i][2]+"'>" + certs[i][2] + "</option>");
					Enroll.rsa_csp_setProvider(certs[i][0]);
					var exp = Enroll.rsa_csp_exportSignX509Cert(certs[i][1]);
					$("#X509Cert").val(exp);
				}
			}
	});


	// 获取签名数据
	$("#getSignData").click(
		function() {
			var certDn = $("#cert_Dn").val();
			$("#signPlain").val(certDn);
			var cert = readCertSignature(certs, certDn);
			if (cert == null) {
				alert("没有读取到证书");
				return false;
			}
			
			var retCert = isRsaCert(cert);
			if (retCert != null) { //管理员为rsa证书,隐藏输入pin密码框
				$.ajax({
			       data: {
						"signPlain" : $("#signPlain").val(),
						"x509Cert" : $("#X509Cert").val()
					},
			        type: "post",
			        dataType: "text",
			        url: "getSeal_handle.jsp",
			        success: function(data) {
			        	var result = eval("("+data+")");
			        	if(result.success){
			        		Enroll.rsa_csp_setProvider(retCert[0]);
							var hashAlg = result.hashAlg;
							var toSign = result.toSignData;
							try {
								SignatureData = Enroll.rsa_csp_signDataOfBytesBase64(retCert[1], toSign, hashAlg);
							} catch (e) {
								alert("证书签名错误 ");
								return false;
							}
							$("#signData").val(SignatureData);
			        	}else{
			                alert("签名错误");
			                return;
			            }
			        },
			        error: function(XMLHttpRequest, textStatus, errorThrown) {
			        	alert("RSA证书签名错误");
			        }
				 });
			} else { //sm2证书
				var pin = $("#pin").val();
				if (pin.length == 0) {
					alert("国密证书PIN码不能为空");
					return false;
				}
				Enroll.sm_skf_setDevice(cert[0], cert[1], cert[3]);
				var res = Enroll.sm_skf_VerifyPin(pin);
				if (!res) {
					alert("验证U_KEY失败,PIN码不正确");
					return false;
				}
				//sm2证书
				$.ajax({
			       data: {
						"signPlain" : $("#signPlain").val(),
						"x509Cert" : $("#X509Cert").val()
					},
			        type: "post",
			        dataType: "text",
			        url: "getSeal_handle.jsp",
			        success: function(data) {
			        	var result = eval("("+data+")");
			        	if(result.success){
			        		var SignatureData; // 签名数据
			        		var toSign = result.toSignData;
							try {
								SignatureData = Enroll.sm_skf_signDataOfBytesBase64(cert[4], toSign, "", true);
							} catch (e) {
								alert("证书签名错误 ");
								return false;
							}
							$("#signData").val(SignatureData);
			        	}else{
			                alert("签名错误");
			                return;
			            }
			        },
			        error: function(XMLHttpRequest, textStatus, errorThrown) {
			        	alert("RSA证书签名错误");
			        }
				 });
			}
	});
});
</script>
</html>
