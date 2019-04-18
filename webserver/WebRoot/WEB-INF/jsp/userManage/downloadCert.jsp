<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">签章人管理</h2>
</div>
<ul class="breadcrumb" style="">
	<li><i class="fa fa-home"></i> <i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理 / 签章人管理 / 证书申请下载</i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">证书申请下载</a></li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="x_content">
							<br>
							<form id="downloadCertForm" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;" autocomplete="off"
								action="${ctx }/userCertReuqest/downloadCert.do" method="post">
								<input type="hidden" id="p10" name="p10" /> 
								<input type="hidden" id="tmpPubKey" name="tmpPubKey" /> 
								<input id="id" name="id" type="hidden" value="${userCertRequest.id }" /> 
								<input id="userId" name="userId" type="hidden" value="${user.id }" />
								<div class="form-group">
									<label class="control-label col-xs-3">姓名</label>
									<div class="col-xs-6">
										<input class="form-control col-xs-5" type="text" value="${user.name }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">证书有效期</label>
									<div class="col-xs-6">
										<input class="form-control col-xs-5" type="text" value="${userCertRequest.validityLen }天" style="border:none; border-bottom: 1px solid #444; background:none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">CA</label>
									<div class="col-xs-6">
										<input class="form-control col-xs-5" type="text" value="${userCertRequest.certTypeCN }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">证书模板</label>
									<div class="col-xs-6">
										<input class="form-control col-xs-5" type="text" value="${userCertRequest.certTemplate }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">证书DN</label>
									<div class="col-xs-6">
										<input class="form-control col-xs-5" id="certDN" type="text" value="${userCertRequest.certDn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">密钥长度</label>
									<div class="col-xs-6">
										<select class="form-control " id="keyLen" name="keyLen">
										</select>
									</div>
								</div>
								<div id='rsakey' class="form-group">
									<label class="control-label col-xs-3">CSP列表</label>
									<div class="col-xs-6">
										<select class="form-control " id="cspName" name="cspName">

										</select>
									</div>
								</div>
								<div id='sm2key'>
									<div class="form-group">
										<label class="control-label col-xs-3">选择提供者</label>
										<div class="col-xs-6">
											<div id="gender" class="btn-group" data-toggle="buttons">
												<div class="input-group">
													<select class="form-control " id="sm2Cryptprov" name="sm2Cryptprov"></select>
													<span class="input-group-btn">
														<button type="button" class="btn btn-primary col-md-offset-1"  id="selectPri">确定</button>
													</span>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3">选择设备</label>
										<div class="col-xs-6">
											<div id="gender" class="btn-group" data-toggle="buttons">
												<div class="input-group">
													<select class="form-control " id="device" name="device"></select>
													<span class="input-group-btn">
														<button type="button" class="btn btn-primary col-md-offset-1"  id="selectDev">确定</button>
													</span>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3">Label</label>
										<div class="col-xs-6">
											<input id="label" name="label" class="form-control " type="text" value="" />
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3">选择设备可选应用</label>
										<div class="col-xs-6">
											<div id="gender" class="btn-group" data-toggle="buttons">
												<div class="input-group">
													<select class="form-control " id="appliaction" name="appliaction"></select>
													<span class="input-group-btn">
														<button type="button" class="btn btn-primary col-md-offset-1"  id="selectApp">选择</button>
													</span>
												</div>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3">PIN</label>
										<div class="col-xs-6">
											<input id="pin" name="pin" class="form-control " type="password" value="" />
										</div>
									</div>
								</div>

								<div class="ln_solid"></div>
								<div class="form-actions">
									<div class="row">
										<div class="col-md-offset-3 col-xs-6">
											<button id="submitDownloadCertButton" type="button" class="btn btn-primary col-md-offset-3">下载</button>
											<button onclick="javascript:loadUrl('${ctx }/userManage/userList.do')" class="btn btn-primary col-md-offset-3" type="button">返回</button>
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

<script type="text/javascript">
	var Enroll;
	var ver = navigator.platform;
	if('Win32'==ver)
		Enroll=  document.getElementById('infosecEnroll_32');
	else
		Enroll = document.getElementById('infosecEnroll_64');
		
	function VBATOA(v){
		var tmp = new VBArray(v);
		return tmp.toArray();
	}

	function findProviders(){
		Enroll.isShowErrorWindow(true);
		var vs = (new VBArray(Enroll.rsa_csp_listProvider())).toArray();
		for(var i = 0; i < vs.length; i++){
			$("#cspName").append("<option value='" + vs[i] + "'>" + vs[i] + "</option>");
		}
	}
	function sm2Cryptprov() {
		Enroll.isShowErrorWindow(true);
		var vs = (new VBArray(Enroll.sm_skf_listProvider())).toArray();
		for(var i = 0; i < vs.length; i++){
			$("#sm2Cryptprov").append("<option value='" + vs[i] + "'>" + vs[i] + "</option>");
		}
	}
	
	$("#selectPri").click(function(){
		var provider = $("#sm2Cryptprov").val();
		if (!provider) {
			layer.alert("未获取到可选Provider", {icon : 2});
			return;
		}
		var vs = (new VBArray(Enroll.sm_skf_listDevice(provider))).toArray();
		var vsLength = vs.length;
		for(var i = 0; i < vsLength; i++){
			$("#device").append("<option value='" + vs[i] + "'>" + vs[i] + "</option>");
		}
	});
	
	$("#selectDev").click(function(){
		var provider = $("#sm2Cryptprov").val();
		if (!provider) {
			layer.alert("请先选择可选提供者", {icon : 2});
			return;
		}
		var device = $("#device").val();
		if (!device) {
			layer.alert("未确定提供者或未获取到设备信息", {icon : 2});
			return;
		}
		var vs = (new VBArray(Enroll.sm_skf_getDeviceInfo(provider, device))).toArray();
		$("#label").val(vs[1]);
	});
	
	$("#selectApp").click(function(){
		var provider = $("#sm2Cryptprov").val();
		if (!provider) {
			layer.alert("请先选择可选提供者", {icon : 2});
			return;
		}
		var device = $("#device").val();
		if (!device) {
			layer.alert("请先选择设备", {icon : 2});
			return;
		}
		var vs = (new VBArray(Enroll.sm_skf_listApplication(provider, device))).toArray();
		for(var i = 0; i < vs.length; i++){
			$("#appliaction").append("<option value='" + vs[i] + "'>" + vs[i] + "</option>");
		}
	});

	$("#sm2Cryptprov").change(function() {
		$("#device").val('');
		$("#label").val('');
		$("#appliaction").val('');
		$("#pin").val('');
	});
	
	$("#device").change(function() {
		$("#label").val('');
		$("#appliaction").val('');
		$("#pin").val('');
	});

	$(function() {
		var certType = "${userCertRequest.certTypeCN }";
		if (certType == "RSA") {
			findProviders();
			$("#sm2key").remove();
			$("#keyLen").append("<option value='" + 1024 + "'>" + 1024 + "</option>");
			$("#keyLen").append("<option value='" + 2048 + "'>" + 2048 + "</option>");
		} else {
			sm2Cryptprov();
			$("#rsakey").remove();
			$("#keyLen").append("<option value='" + 256 + "'>" + 256 + "</option>");
		}
		$("#submitDownloadCertButton").click(
			function() {
				var form = $("#downloadCertForm");
				var container = "";
				form.bootstrapValidator('validate');
				if (form.data('bootstrapValidator').isValid()) {
					if (certType == "RSA") {
						Enroll.rsa_csp_setProvider($("#cspName").val());
						var vs = (new VBArray(Enroll.rsa_csp_genContainerP10(true,parseInt($("#keyLen").val(),10), $("#certDN").val(), "", "", "", false, false))).toArray();
						container = vs[0];
						var p10 = vs[1];
						$("#p10").val(p10);
						var isDCert = "${userCertRequest.isDCert }";
						if (isDCert == "1") {// 双证，产生临时公钥
							$("#tmpPubKey").val(Enroll.rsa_csp_genEncKeyPair());
						}
					} else { // sm2
						var sm2Cryptprov = $("#sm2Cryptprov").val();
						var device = $("#device").val();
						var appliaction = $("#appliaction").val();
						if (!sm2Cryptprov) {
							layer.alert("没有查询到设备信息", {icon : 2});
							return;
						}
						if (!sm2Cryptprov) {
							layer.alert("请设置provider", {icon : 2});
							return;
						}
						if (!device) {
							layer.alert("请选择设备", {icon : 2});
							return;
						}
						if (!appliaction) {
							layer.alert("请选择设备应用", {icon : 2});
							return;
						}
						Enroll.sm_skf_setDevice(sm2Cryptprov, device, appliaction);
						
						var pin = $("#pin").val();
						var bool = Enroll.sm_skf_VerifyPin(pin); //设置pin码，判断错误信息
						if (!bool) {
							layer.alert("PIN码校验失败", {icon : 2});
							return;
						} else {
							var p10vs = (new VBArray(Enroll.sm_skf_genContainerP10("", "", true))).toArray();
							container = p10vs[0];
							$("#p10").val(p10vs[1]); //产生sm2的p10				
						}
					}
					form.ajaxSubmit({
						success : function(data) {
							if (data.success) {
								var ucr = data.userCertRequest;
								try {
									if (certType == "RSA") {
										Enroll.rsa_csp_setProvider(ucr.cspName);
										Enroll.rsa_csp_importSignP7Cert(container, ucr.signCert);
										if (ucr.encCert) {
											Enroll.rsa_csp_importEncP7Cert(container, ucr.encCert, ucr.encPri, ucr.ukek, false, false);
										}
									} else {
										Enroll.sm_skf_importSignX509Cert(container, ucr.signCert);
										if (ucr.encCert) {
											var SMECB = 1025; // 用SM4算法
											var encPriKeyEnvelop = Enroll.sm_skf_getEnveloped(ucr.encCert, parseInt(SMECB, 10), ucr.encPri, ucr.ukek);
											Enroll.sm_skf_importEncX509Cert(container, ucr.encCert, encPriKeyEnvelop);
										}
									}
								} catch (e) {
									layer.alert("证书下载失败" + e.message, { icon : 2 });
									return;
								}
								layer.alert("证书下载完成", { icon : 1 });
								loadUrl("${ctx }/userManage/userList.do");
							} else {
								layer.alert(data.message, { icon : 2 });
							}
						},
						error : function() {
							layer.alert("请求失败", { icon : 2 });
							return;
						}
					});
				}

			});

		//--表单验证
		$("#downloadCertForm").bootstrapValidator({
			fields : {
				label : {
					validators : {
						notEmpty : {
							message : 'label不能为空'
						},
						stringLength : {
							min : 1,
							max : 50,
							message : 'label长度为1~50'
						}
					}
				},
				pin : {
					validators : {
						notEmpty : {
							message : 'pin不能为空'
						},
						stringLength : {
							min : 1,
							max : 50,
							message : 'pin长度为1~50'
						}
					}
				}
			}
		});

	});
</script>