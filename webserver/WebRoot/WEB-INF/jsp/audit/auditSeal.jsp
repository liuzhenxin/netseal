<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">印章审核</h2>
</div>
<ul class="breadcrumb" style="">
	<li><i class="fa fa-home"></i> <i style="color:rgb(42,63,84); font-style:normal;">印章审核 / 审核</i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;"> 审核</a></li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">

						<div class="x_content">
							<br>
							<form id="auditSealForm" class="form-horizontal form-label-left" action="${ctx }/audit/auditSealSign.do" method="post">
								<input id="certSn" name="certSn" type="hidden" value="${request.certSn }"> <input id="adminCertSn" name="adminCertSn" type="hidden" value="${adminCertSn }">
								<div class="form-group">
									<label for="name" class="col-xs-3 control-label">审核</label>
									<div class="col-xs-6">
										<input id="id" name="id" type="hidden" value="${request.id }"> <input id="name" name="name" readonly class="form-control input-sm col-xs-6" type="text" value="${request.name }" />
									</div>
								</div>
								<div class="form-group">
									<label for="remark" class="col-xs-3 control-label">最大签章次数</label>
									<div class="col-xs-6">
										<input id="usedLimit" name="usedLimit" class="form-control col-xs-6" type="text" placeholder="(0-999, 0无限制, -1不能盖章)" value="0" /> (0-999, 0无限制, -1不能盖章)
									</div>
								</div>
								<c:if test="${not empty request.photoPath}">
									<div class="form-group">
										<label for="name" class="col-xs-3 control-label">图片</label>
										<div class="col-xs-6" style="margin-top:8px;">
											<span> <a href="#" data-toggle="modal" data-target="#myModal">查看</a>
											</span>
										</div>
									</div>
								</c:if>
								<div class="form-group">
									<label for="remark" class="col-xs-3 control-label">备注</label>
									<div class="col-xs-6">
										<input id="remark" name="remark" class="form-control col-xs-6" type="text" />
									</div>
								</div>
								<div class="form-group" id="div_pin">
									<label for="after" class="col-xs-3 control-label">输入PIN码</label>
									<div class="col-xs-6">
										<input id="pin" name="pin" class="form-control input-sm col-xs-6" type="password" />
									</div>
								</div>
								<div class="ln_solid"></div>
								<div class="form-actions">
									<div class="row">
										<div class="col-md-offset-3 col-xs-6">
											<button id="submitAuditSealButton" type="button" class="btn btn-primary col-md-offset-3" style="">确定</button>
											<button onclick="javascript:loadUrl('${ctx }/audit/auditList.do')" class="btn btn-primary col-md-offset-3" type="button" style="" id="return">返回</button>
										</div>
									</div>
								</div>

							</form>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</div>





<!-- 查看印模图片模态框 -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel" style="color:#3c8dbc;font-size:20px;">图片</h4>
			</div>
			<div class="modal-body">
				<div align="center">
					<img src="${ctx }/audit/viewPhoto.do?id=${request.id }&time=${request.updateTime }" />
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
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

	function readCertSignature(certs, certSn) { //返回选择的证书
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
		
		/* if(cert[11] == "1.2.156.10197.1.501")
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
		}  */
	}

	$(function() {
		var ver = navigator.platform;
		if ('Win32' == ver)
			Enroll = document.getElementById('infosecEnroll_32');
		else
			Enroll = document.getElementById('infosecEnroll_64');

		var certs = enumCerts(); //所有可用于签名的证书
		var adminCertSn = $("#adminCertSn").val();
		var cert = readCertSignature(certs, adminCertSn);
		if (cert == null) {
			layer.alert("没有读取到管理员证书", {
				icon : 2
			});
			return;
		}
		var retCert = isRsaCert(cert);
		if (retCert != null) { //管理员为rsa证书,隐藏输入pin密码框
			$("#div_pin").hide();
		}
		//--表单验证
		$("#auditSealForm").bootstrapValidator({
			fields : {
				usedLimit : {
					validators : {
						notEmpty : {
							message : '最大签章次数不能为空'
						},

						regexp : {
							regexp : /^(\-?(1)|[0-9]{1,3})$/,
							message : '最大签章次数范围为(-1)~999'
						}
					}
				},
				remark : {
					validators : {
						stringLength : {
							min : 0,
							max : 66,
							message : '长度0~66'
						}
					}
				}
			}
		});

		$("#submitAuditSealButton").click(function() {
			var form = $("#auditSealForm");
			form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				var certs = enumCerts(); //所有可用于签名的证书
				var adminCertSn = $("#adminCertSn").val();
				var cert = readCertSignature(certs, adminCertSn);
				if (cert == null) {
					layer.alert("没有读取到管理员证书", {
						icon : 2
					});
					return;
				}

				//第一次提交 签名
				form.ajaxSubmit({
					success : function(data) {
						if (data.success) {
							var generateTime = data.generateTime;
							var signData = "";
							var toSignData = data.sealData;
							var hashAlg = data.hashAlg;
							//对印章数据签名				        
							if (retCert != null) { //rsa证书
								Enroll.rsa_csp_setProvider(retCert[0]);
								try {
									signData = Enroll.rsa_csp_signDataOfBytesBase64(retCert[1], toSignData, hashAlg);
								} catch (e) {
									layer.alert("证书签名错误 " + e.message, {
										icon : 2
									});
									return;
								}
							} else { //sm证书
								var pin = $("#pin").val();
								if (pin.length == 0) {
									layer.alert("国密证书PIN码不能为空", {
										icon : 2
									});
									return;
								}
								Enroll.sm_skf_setDevice(cert[0], cert[1], cert[3]);
								var res = Enroll.sm_skf_VerifyPin(pin);
								if (!res) {
									layer.alert("验证管理员U_KEY失败,PIN码不正确", {
										icon : 2
									});
									return;
								}
								try {
									signData = Enroll.sm_skf_signDataOfBytesBase64(cert[4], toSignData, "", true);
								} catch (e) {
									layer.alert("证书签名错误 " + e.message, {
										icon : 2
									});
									return;
								}
							}

							//第二次提交 审核
							$.ajax({
								type : "post",
								dataType : "json",
								data : {
									"signData" : signData,
									"id" : $("#id").val(),
									"toSignData" : toSignData,
									"remark" : $("#remark").val(),
									"usedLimit" : $("#usedLimit").val(),
									"generateTime" : generateTime
								},
								url : "${ctx }/audit/auditSeal.do",
								success : function(data) {
									if (data.success) {
										var url = "${ctx }/audit/auditList.do";
										loadUrl(url);
									} else {
										layer.alert("审核失败：" + data.message, {
											icon : 2
										});
									}
								},
								error : function() {
									layer.alert("请求失败", {
										icon : 2
									});
								}
							});
						} else {
							layer.alert("审核失败," + data.message, {
								icon : 2
							});
						}

					},
					error : function() {
						layer.alert("请求失败", {
							icon : 2
						});
					}
				});
			}
		});
	});
</script>