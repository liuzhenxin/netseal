<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">管理员管理</h2>
</div>
<ul class="breadcrumb" style="">
	<li><i class="fa fa-home"></i> <i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理 / 管理员管理 </i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle"> 管理员管理</a></li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="x_content">
							<form id="addSysUserForm" action="${ctx }/sysUser/addSysUser.do" method="post" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px; ">
								<input id="X509Cert" name="X509Cert" type="hidden"> <input id="certSn" name="certSn" type="hidden">
								<div class="form-group">
									<label for="account" class="control-label col-xs-3">用户名 </label>
									<div class="col-xs-6">
										<input id="account" name="account" type="text" class="form-control">
									</div>
								</div>
								<div class="form-group">
									<label for="name" class="control-label col-xs-3">姓名 </label>
									<div class="col-xs-6">
										<input id="name" name="name" type="text" class="form-control">
									</div>
								</div>

								<div class="form-group">
									<label for="roleId" class="control-label col-xs-3">角色 </label>
									<div class="col-xs-6">
										<select class="form-control  input-sm" name="roleId">
											<c:forEach items="${list }" var="rlist" begin="0" end="4">
												<option value="${rlist.id }">${rlist.name }</option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="companyName" class="control-label col-xs-3">用户单位</label>
									<div class="col-xs-6">
										<div id="gender" class="btn-group" data-toggle="buttons">
											<div class="input-group">
												<input name="companyHidden" readonly class="form-control input-sm" type="hidden" /> <input name="companyHiddenID" class="form-control" type="hidden" /> <input id="companyName"
													name="companyName" readonly class="form-control" type="text" /> <input id="companyId" name="companyId" class="form-control" type="hidden" /> <span class="input-group-btn">
													<button type="button" id="selectCom" class="btn btn-primary" data-toggle="modal" data-target=".bs-example-modal-lg2">选择</button>
												</span>
											</div>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label for="certDN" class="control-label col-xs-3">证书主题 </label>
									<div class="col-xs-6">
										<select id="certDn" name="certDn" class="form-control col-xs-6">
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="name" class="control-label col-xs-3">手机令牌：</label>
									<div class="col-xs-6"></div>
								</div>
								<div class="form-group">
									<label for="name" class="control-label col-xs-3">令牌序列号</label>
									<div class="col-xs-6">
										<input id="tokenSn" name="tokenSn" type="text" class="form-control">
									</div>
								</div>
								<div class="form-group">
									<label for="name" class="control-label col-xs-3">令牌激活码</label>
									<div class="col-xs-6">
										<input id="tokenActiveCode" name="tokenActiveCode" type="text" class="form-control">
									</div>
								</div>
								<div class="ln_solid"></div>
								<div class="form-actions">
									<div class="row">
										<div class="col-md-offset-3 col-xs-6">
											<button id="addSysUserReadCert" type="button" class="btn btn-primary col-md-offset-2">读取证书</button>
											<button id="addSysUserButton" type="button" class="btn btn-primary col-md-offset-2" style="">保存</button>
											<button id="back" class="btn btn-primary col-md-offset-2" type="button">返回</button>
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


<div id="addsysuserCom" class="modal fade bs-example-modal-lg2" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="width:500px; height:300px; position:absolute; left:50%; top:50%; margin-left:-250px; margin-top:-150px;">
		<form id="editPrintNumForm" class="form-horizontal" action="${ctx }/printer/EditPrintNum.do" method="post">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">选择单位</h4>
				</div>
				<div class="modal-body">
					<div class="tree-dialog-content">
						<ul id="setCompanyTree" class="ztree"></ul>
					</div>
				</div>
				<div class="modal-footer">
					<button id="modalClose" type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button id="submitCompanyButton" type="button" class="btn btn-primary" data-dismiss="modal">确定</button>
				</div>
			</div>
		</form>
	</div>
</div>





<script type="text/javascript">
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

	function readCertSignature(certs, certDN) { //返回选择的证书
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

		var certs = new Array();
		//读取U_KEY
		$("#addSysUserReadCert").click(function() {
			certs = enumCerts(); //所有可用于签名的证书
			if (certs.length == 0) {
				$("#certDn").empty();
				layer.alert("没有读取到证书", {
					icon : 0
				});
				return;
			}
			$("#certDn").empty();
			var certDn;
			for (var i = 0; i < certs.length; i++) {
				if (certs[i].length == 12)
					certDn = certs[i][5]; //主题
				else
					certDn = certs[i][2];
				$("#certDn").append("<option value='" + certDn + "'>" + certDn + "</option>");
			}
			$("#addSysUserForm").data('bootstrapValidator').updateStatus('certDn', 'NOT_VALIDATED', null).validateField('certDn');
		});
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/sysUser/sysUserList.do");
		});
		$("#addSysUserForm").bootstrapValidator({
			fields : {
				account : {
					validators : {
						notEmpty : {
							message : '用户名不能为空'
						},
						stringLength : {
							min : 2,
							max : 16,
							message : '用户名长度为2~16'
						},
						regexp : {
							regexp : /^[-_a-zA-Z0-9@]+$/,
							message : '用户名只能由字母数字  _ - @ 组成'
						}
					}
				},
				name : {
					validators : {
						notEmpty : {
							message : '姓名不能为空'
						},
						stringLength : {
							min : 2,
							max : 16,
							message : '用户名长度为2~16'
						},
						regexp : {
							regexp : /^[\u4e00-\u9fa5a-zA-Z0-9]+$/,
							message : '请输入正确格式(中文,数字或字母)'
						}
					}
				},
				certDn : {
					validators : {
						notEmpty : {
							message : '没有读取到证书主题'
						},
						stringLength : {
							min : 2,
							max : 300,
							message : '长度为2~300'
						}
					}
				},
				roleId : {
					validators : {
						notEmpty : {
							message : '角色不能为空'
						}
					}
				},
				tokenSn : {
					validators : {
						stringLength : {
							min : 2,
							max : 30,
							message : '令牌序列号长度为2~30'
						}
					}
				},
				tokenActiveCode : {
					validators : {
						stringLength : {
							min : 2,
							max : 50,
							message : '令牌激活码长度为2~50'
						}
					}
				}
			}
		});
		//添加
		$("#addSysUserButton").click(function() {
			var form = $("#addSysUserForm");
			$('#addSysUserForm').bootstrapValidator('validate');
			if ($('#addSysUserForm').data('bootstrapValidator').isValid()) {
				var certDn = $("#certDn").val();
				if (!$('#companyName').val()) {
					layer.alert("单位不能为空", {
						icon : 0
					});
				} else {
					certs = enumCerts(); //所有可用于签名的证书
					var cert = readCertSignature(certs, certDn);
					if (cert == null) {
						layer.alert("没有读取到所选证书", {
							icon : 0
						});
						return;
					}
					if (cert.length == 12) {
						var c0 = cert[0]; //提供者
						var c4 = cert[4]; //容器名
						Enroll.sm_skf_setDevice(c0, cert[1], cert[3]);
						var exp = Enroll.sm_skf_exportSignX509Cert(c4);
						$("#X509Cert").val(exp);
						$("#certSn").val(cert[9]);
					} else {
						var c0 = cert[0]; //提供者
						var c1 = cert[1]; //容器名
						Enroll.rsa_csp_setProvider(c0);
						var exp = Enroll.rsa_csp_exportSignX509Cert(c1);
						$("#X509Cert").val(exp);
						$("#certSn").val(cert[6]);
					}

					form.ajaxSubmit({
						success : function(data) {
							if (data.success) {
								if (typeof (data.message) == "undefined")
									layer.alert("操作成功", {
										icon : 1
									});
								else
									layer.alert(data.message, {
										icon : 1
									});
								var url = "${ctx }/sysUser/sysUserList.do";
								loadUrl(url);
							} else {
								if (typeof (data.message) == "undefined")
									/* swal("", "操作失败", "error"); */
									layer.alert(data.message, {
										icon : 2
									});
								else
									layer.alert(data.message, {
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
			} else {
				$('#addSysUserForm').bootstrapValidator('validate');
			}
		});
	});

	/*--- 以下代码是将原来单独打开的选择单位的页面给替换成当前页面,即上边的selectCompany()方法无效
	 -以及sysususercompanyconfig页面也无效-以及相对应的controller里边的--toconfigcompany方法也无效--2017/3/1*/
	$("#selectCom").click(function() {
		var setting = {
			async : {
				enable : true,
				url : "${ctx }/sysUser/setCompanyTree.do",
				autoParam : [ "id" ]
			},
			callback : {
				beforeClick : function(treeId, treeNode) {
					var id = treeNode.id;
					$("[name=companyId]").val(id);
					$("[name=companyName]").val(treeNode.name);
				},
				onAsyncSuccess : function(event, treeId, treeNode, msg) {

					if (treeNode == null) {
						var treeObj = $.fn.zTree.getZTreeObj(treeId);
						var nodes = treeObj.getNodes();
						if (nodes.length > 0) {
							treeObj.expandNode(nodes[0], true, false, false);
						}
					}
				}
			}
		};
		$.fn.zTree.init($("#setCompanyTree"), setting);
	});

	$("#submitCompanyButton").click(function() {
		var companyId = $("[name=companyId]").val();
		document.getElementById("companyId").value = companyId;
		document.getElementById("companyName").value = $("[name=companyName]").val();
	});
	//模态框关闭
	$("#modalClose").click(function() {
		var oldId = $("[name=companyHiddenID]").val();
		var oldname = $("[name=companyHidden]").val();

		var oldIdE = $("[name=companyHiddenIDE]").val();
		var oldnameE = $("[name=companyHiddenE]").val();
		$("[id=companyIdE]").val(oldIdE);
		$("[id=companyNameE]").val(oldnameE);
		$("[id=companyId]").val(oldId);
		$("[id=companyName]").val(oldname);




	});
</script>