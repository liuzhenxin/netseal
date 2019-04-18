<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display: none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display: none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>NetSeal电子签章管理系统</title>
<!-- Tell the browser to be responsive to screen width -->
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- Bootstrap 3.3.7 -->
<link rel="stylesheet" href="${ctx }/css/bootstrap/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="${ctx }/css/font-awesome/css/font-awesome.min.css">
<!-- Theme style -->
<link rel="stylesheet" href="${ctx }/css/AdminLTE.min.css">
<!-- jQuery 3 -->
<script src="${ctx }/js/jQuery-1.7.2.min.js"></script>
<!-- Bootstrap 3.3.7 -->
<script src="${ctx }/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx }/js/jquery.form.js"></script>
<script type="text/javascript" src="${ctx }/js/layer/layer.js"></script>
<script>
	var Enroll;
	function VBATOA(v) {
		var tmp = new VBArray(v);
		return tmp.toArray();
	}

	//所有可用于签名的证书
	function enumCerts() {
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

	//返回选择的证书
	function readCertSignature(certs, certSn) {
		var rSn;
		var Count = certs.length;
		for (var i = 0; i < Count; i++) {
			if (certs[i].length == 12)
				rSn = certs[i][9]; //证书序列号
			else
				rSn = certs[i][6];

			if (rSn == certSn)
				return certs[i];
		}
		return null;
	}

	//判断是否是rsa证书,如果是rsa证书返回rsa证书,否则返回null
	function isRsaCert(cert) {
		if (cert.length == 12)
			return null;
		else
			return cert;
	/*
	if (cert[11] == "1.2.156.10197.1.501")
		return null;
	else {
		var ret = null;
		var Count = Enroll.rsa_csp_getCountOfCert();
		for (var i = 0; i < Count; i++) {
			var tempCert = VBATOA(Enroll.rsa_csp_getCertInfo(i));
			var tc3 = tempCert[3]; //证书颁发者
			var tc6 = tempCert[6]; //证书序列号
			var c6 = cert[6]; //证书颁发者
			var c9 = cert[9]; //证书序列号
			if ((tc3 == c6) && (tc6 == c9)) {
				ret = tempCert;
				break;
			}
		}
		return ret;
	}*/
	}

	$(function() {
		$(".errUsername").hide();
		$(".errPw").hide();

		var ver = navigator.platform;
		if ('Win32' == ver)
			Enroll = document.getElementById('infosecEnroll_32');
		else
			Enroll = document.getElementById('infosecEnroll_64');

		$("#loginSubmit").click(function() {
			$("#errorTip").html("");
			var loginType = $('input[name="loginType"]:checked').val();
			if (loginType == "1" || loginType == "3") {
				loginSubmit();
			} else if (loginType == "2") {
				loginCertSubmit();
			} else {
				$("#errorTip").html("错误的登录方式");
			}
		});
		var loginType = '${loginType}';
		if (loginType != '')
			$("input[name='loginType'][value=" + loginType + "]").attr("checked", true);
		
		$('input[name="loginType"]').click(function() {
			var loginType = $('input[name="loginType"]:checked').val();
			if (loginType == "2") {
				$('#pwdInput').hide();
			} else {
				$('#pwdInput').show();
			}
			
		});
	});

	function login(e) {
		e = e || event;
		if (e.keyCode == 13) {
			loginSubmit();
		}
	}

	// 密码提交
	function loginSubmit() {
		if ($("#account").val() == null || trim($("#account").val()) == "") {
			$(".errUsername").html("<strong >不能为空!</strong>");
			$(".errUsername").show().delay(2000).hide(0);
			return false;
		}
		if ($("#pwd").val() == null || trim($("#pwd").val()) == "") {
			$(".errPw").html("<strong >不能为空!</strong>");
			$(".errPw").show().delay(2000).hide(0);
			return false;
		}
		var element = document.getElementById('loginForm');
		element.action = '${ctx }/sysUser/login.do';
		element.submit();
	}

	// 证书提交
	function loginCertSubmit() {
		//获取用户证书DN
		if ($("#account").val() == null || trim($("#account").val()) == "") {
			$(".errUsername").html("用户名不能为空！");
			return false;
		}
		// 第一次提交,验证管理员及证书信息,返回证书SN
		var form = $("#loginForm");
		form.ajaxSubmit({
			success : function(data) {
				if (data.success) {
					var SignatureData = "";
					var adminCertSn = data.certSn; //证书SN
					var hashAlg = data.hashAlg; //证书摘要算法				
					var certs = enumCerts(); //所有可用于签名的证书
					var cert = readCertSignature(certs, adminCertSn);
					if (cert == null) {
						$("#errorTip").html("没有读取到管理员证书");
						return false;
					}
					var retCert = isRsaCert(cert);
					if (retCert != null) { //管理员为rsa证书,隐藏输入pin密码框
						layer.confirm("厂商驱动可能会缓存PIN码,登录注销后请及时拔出USBKey.",{btn:["确定","取消"]},function(){
							Enroll.rsa_csp_setProvider(retCert[0]);
							try {
								SignatureData = Enroll.rsa_csp_signDataOfBytesBase64(retCert[1], data.randomData, hashAlg);
							} catch (e) {
								$("#errorTip").html("证书签名错误 ");
								return false;
							}
							// 第二次提交,验证签名信息 登录
							loginVerifySign(data.sysId,SignatureData,data.randomData);
						});
					} else { //sm2证书						
						layer.prompt({title: '国密证书请输入PIN码：', formType: 1}, function(pin, index){
							layer.close(index);
							if (pin.length == 0) {
								$("#errorTip").html("国密证书PIN码不能为空");
								return false;
							}
							Enroll.sm_skf_setDevice(cert[0], cert[1], cert[3]);
							var res = Enroll.sm_skf_VerifyPin(pin);
							if (!res) {
								$("#errorTip").html("验证管理员U_KEY失败,PIN码不正确");
								return false;
							}
							try {
								SignatureData = Enroll.sm_skf_signDataOfBytesBase64(cert[4], data.randomData, "", true);
							} catch (e) {
								$("#errorTip").html("证书签名错误 ");
								return false;
							}
							// 第二次提交,验证签名信息 登录
							loginVerifySign(data.sysId,SignatureData,data.randomData);
						});
					}

				} else {
					$("#errorTip").html(data.message);
				}
			},
			error : function() {
				$("#errorTip").html("请求失败");
			}
		});
	}
//
	function loginVerifySign(sysId,SignatureData,randomData){
		$.ajax({
			type : "post",
			dataType : "json",
			data : {
				"SignatureData" : SignatureData,
				"plainData" : randomData,
				"sysId" : sysId
			},
			url : "${ctx }/sysUser/loginVerifySign.do",
			success : function(datas) {
				if (datas.success) {
					document.write("<form id='loginSignForm' action = '${ctx }/sysUser/loginSign.do' method='post'>");
					document.write("<input type = 'hidden' name = 'sysId' value = '" + sysId + "'>");
					document.write("</form>");
					var element = document.getElementById('loginSignForm');
					element.submit();
				} else {
					$("#errorTip").html("签名验证失败：" + datas.message);
				}
			},
			error : function() {
				$("#errorTip").html("请求失败");
			}
		});
	}
	function trim(str) {
		return str.replace(/(^\s*)|(\s*$)/g, "");
	}
</script>
</head>
<body class="hold-transition login-page" style="background-color: rgb(35, 60, 80);">
	<div class="clearfix"></div>
	<div>
		<div class="login-box" style="background-color: rgb(42, 63, 84);" style="margin-left:-40px; padding-top:17px;">
			<div class="login-logo" style="font-size: 30px; color: aqua;">
				<a style="color: rgb(205, 210, 215);"><b>NetSeal电子签章管理系统</b></a>
				<div id="myCarousel" class="carousel slide">
					<!-- 轮播（Carousel）项目 -->
					<div class="carousel-inner ">
						<div class="item active" style="height: 130px">
							<img src="${ctx }/img/infosec.jpg" alt="First slide">
						</div>
						<div class="item" style="height: 130px">
							<div class="login-logo" style="font-size: 14px; color: aqua;">
								<a style="color: rgb(205, 210, 215);"> 北京信安世纪科技股份有限公司作为国内最早从事PKI技术研发和应用的专业厂商之一，成立于1998年1月，是中国领先的应用安全产品和解决方案供应商， 主要为全行业提供应用安全的产品、解决方案和服务，致力于实现互联网络和企业内部网络的通讯安全、交易安全和网络资源安全保护。 </a>
							</div>
						</div>
					</div>
					<!-- 轮播（Carousel）导航 -->
					<a class="carousel-control left" href="#myCarousel" data-slide="prev">&lsaquo;</a> <a class="carousel-control right" href="#myCarousel" data-slide="next">&rsaquo;</a>
				</div>
			</div>

			<div class="login-box-body" style="background-color: rgb(42, 63, 84);">
				<p class="login-box-msg">欢迎登录</p>
				<p class="login-box-msg">
					<font color="red"><span id="errorTip">${msg }</span></font>
				</p>
				<form id="loginForm" action="${ctx }/sysUser/loginByCert.do" method="post">
					<div class="col-xs-12 col-xs-offset-2">
						<div class="form-group has-feedback col-xs-8">
							<div class="input-icon-group">
								<div class="input-group">
									<span class="input-group-addon"><i class="fa fa-user-o"></i></span> <input name="account" id="account" type="text" class="form-control" onkeypress="javascript:login(event);"><br /> <span
										class="input-group-addon errUsername"></span>
								</div>
							</div>
						</div>
						<div id="pwdInput" class="form-group has-feedback col-xs-8">
							<div class="input-icon-group">
								<div class="input-group">
									<span class="input-group-addon"><i class="fa fa-lock"></i></span> <input name="password" id="pwd" type="password" class="form-control" onkeypress="javascript:login(event);"><br /> <span
										class="input-group-addon errPw"></span>
								</div>
							</div>
						</div>
						<div class="form-group has-feedback col-xs-8"></div>
						
						<div class="form-group has-feedback col-xs-8">
							<div align="center">
								<input name="loginType" id="type1" type="radio" value="1" checked="checked">&nbsp; <label for="type1" style="color: white;">普通用户</label>&nbsp;&nbsp;&nbsp;&nbsp; <input name="loginType"
									id="type2" type="radio" value="2">&nbsp; <label for="type2" style="color: white;">证书用户</label>&nbsp;&nbsp;&nbsp;&nbsp; <input name="loginType" id="type3" type="radio" value="3">&nbsp;
								<label for="type3" style="color: white;">手机令牌</label>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-8">
							<div class="checkbox icheck"></div>
						</div>
						<div class="col-xs-12 col-xs-offset-3">
							<div class="col-xs-3">
								<button type="button" id="loginSubmit" class="btn btn-primary btn-block btn-flat">登录</button>
							</div>
							<div class="col-xs-3 ">
								<button type="reset" class="btn btn-primary btn-block btn-flat">重置</button>
							</div>
						</div>
					</div>
				</form>
				<footer class="main-footer" id="" style="width: 100%; position: fixed; left: 0; bottom: 0; margin: 0; text-align: center;">
					<span style="text-align: center; display: block;"> <strong>Copyright &copy;信安世纪|</strong> All rights reserved. <a href="http://www.infosec.com.cn" target="blank">关于我们</a>
					</span>
				</footer>
			</div>
		</div>
	</div>
</body>
</html>
