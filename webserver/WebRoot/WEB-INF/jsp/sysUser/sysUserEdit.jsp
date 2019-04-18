<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<form id="editSysUserForm" class="form-horizontal" autocomplete="off" action="${ctx }/sysUser/editSysUser.do" method="post">
	<input name="id" type="hidden" value="${sysUser.id }" />
	<!-- <input id="notBeforStrE" name="notBeforStr" type="hidden">
		<input id="notAfterStrE" name="notAfterStr" type="hidden"> -->
	<input id="X509CertE" name="X509Cert" type="hidden"> <input id="certSnE" name="certSn" type="hidden">

	<div class="form-group">
		<label for="accountE" class="col-xs-3 control-label">用户名</label>
		<div class="col-xs-6">
			<input id="accountE" name="account" readonly="readonly" class="form-control input-xs" type="text" value="${sysUser.account }" />
		</div>
		<div class="col-sm-3">
			<span style="color:red;display:none" class="editsp">*此用户为当前用户,信息不可改</span>
		</div>
	</div>
	<div class="form-group">
		<label for="name" class="col-xs-3 control-label">姓名</label>
		<div class="col-xs-6">
			<input id="nameE" name="name" class="form-control input-xs selfread" type="text" value="${sysUser.name }" />
		</div>
	</div>
	<div class="form-group">
		<label for="roleIdE" class="col-xs-3 control-label">角色</label>
		<div class="col-sm-6">
			<select class="form-control  input-xs selfread" name="roleId" id="roleIdE">
				<c:forEach items="${roleList }" var="rlist" begin="0" end="4">
					<option value="${rlist.id }" ${rlist.id==sysUser.roleId ? 'selected':'' }>${rlist.name }</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="form-group">
		<label for="companyNameE" class="col-xs-3 control-label">选取单位</label>
		<div class="col-xs-6">
			<div class="input-group">

				<input name="companyHidden" readonly class="form-control input-xs" type="hidden" value="${sysUser.companyName }" /> <input name="companyHiddenID" class="form-control" type="hidden"
					value="${sysUser.companyId }" /> <input id="companyName" name="companyName" readonly class="form-control input-xs" type="text" value="${sysUser.companyName }" /> <input id="companyId"
					name="companyId" class="form-control" type="hidden" value="${sysUser.companyId }" /> <span class="input-group-btn">
					<button type="button" id="selectCom" class="btn btn-primary " data-toggle="modal" data-target="#addsysuserCom">修改</button>
				</span>
			</div>
		</div>
	</div>
	<div class="form-group">
		<label for="statusE" class="col-xs-3 control-label">启用</label>
		<div class="col-xs-6">
			<!-- <div class="col-xs-3">
					<input type="radio" name="status"  class="selfread"   value="0"  id="radio1E">
					<label for="radio1E">停用</label>
				</div>
				<div class="col-xs-3">
					<input type="radio" name="status" class="rd selfread" value="1" id="radio2E">
						<label for="radio2E">启用</label>
				</div> -->
			<ul class="ui-choose">
				<li ${sysUser.status ==1 ? 'class="selected"':'' }>是</li>
				<li ${sysUser.status ==0 ? 'class="selected"':'' }>否</li>
				<input type="hidden" name="status" value="${sysUser.status }">
			</ul>

		</div>
	</div>
	<div class="form-group">
		<label for="certDn_r" class="col-xs-3 control-label">已注册证书主题</label>
		<div class="col-xs-6">
			<input name="certDn_r" readonly="readonly" class="form-control input-xs" type="text" value="${certDn }" />
		</div>
	</div>
	<div class="form-group">
		<label for="certDn" class="col-xs-3 control-label">证书主题</label>
		<div class="col-xs-6">
			<select id="certDn_E" name="certDn" class="form-control input-xs">
			</select>
		</div>
	</div>
	<div class="form-group">
		<label for="name" class="control-label col-xs-3">手机令牌：</label>
		<div class="col-xs-6">${sysUser.tokenSeed }</div>
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
				<input id="editSysUserReadCert" type="button" value="读取证书" class="btn btn-primary col-md-offset-2">
				<button type="button" id="submitSysUserButton" class="btn btn-primary col-md-offset-2">确定</button>
				<button type="button" id="back1" class="btn btn-primary col-md-offset-2">返回</button>
			</div>
		</div>
	</div>
</form>

<!-- model -->
<div class="modal fade" id="addsysuserCom" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="width:500px; height:300px; position:absolute; left:50%; top:50%; margin-left:-250px; margin-top:-150px;">
		<form id="editPrintNumForm" class="form-horizontal" action="" method="post">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">选择单位</h4>
				</div>
				<div class="modal-body">
					<div class="tree-dialog-content">
						<ul id="setCompanyTree" class="ztree"></ul>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" id="modalClose" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" id="submitCompanyButton" class="btn btn-primary">确定</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</form>
	</div>
	<!-- /.modal -->
</div>
<script>
	var Enroll;
	var ver = navigator.platform;
	if ('Win32' == ver)
		Enroll = document.getElementById('infosecEnroll_32');
	else
		Enroll = document.getElementById('infosecEnroll_64');

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
		// 将所有.ui-choose实例化
		$('.ui-choose').ui_choose();

		$('.ui-choose').each(function() {
			var uc_01 = $(this).data('ui-choose'); // 取回已实例化的对象
			var uc = $(this);
			uc_01.click = function(index, item) {
				if (index == 0) {
					uc.find("input").val(1);
				} else {
					uc.find("input").val(0);
				}

			}

		});



		// validate("#editSysUserForm");
		//--表单验证---修改
		$("#editSysUserForm").bootstrapValidator({
			fields : {
				name : {
					validators : {
						notEmpty : {
							message : '姓名不能为空'
						},
						stringLength : {
							min : 2,
							max : 16,
							message : '姓名长度为2~16'
						},
						regexp : {
							regexp : /^[\u4e00-\u9fa5a-zA-Z0-9]+$/,
							message : '输入格式不正确'
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

		var certs = new Array();
		//读取U_KEY
		$("#editSysUserReadCert").click(function() {
			certs = enumCerts(); //所有可用于签名的证书

			if (certs.length == 0) {
				$("#certDn_E").empty();
				layer.alert("没有读取到证书", {
					icon : 2
				});

				return;
			}
			$("#certDn_E").empty();
			var certDn;
			for (var i = 0; i < certs.length; i++) {
				if (certs[i].length == 12)
					certDn = certs[i][5]; //主题
				else
					certDn = certs[i][2];
				$("#certDn_E").append("<option value='" + certDn + "'>" + certDn + "</option>");
			}

		});

		//返回
		$("#back1").click(function() {
			$("#content").load("${ctx }/sysUser/sysUserList.do");
		});

		//修改
		$("#submitSysUserButton").click(function() {
			var form = $("#editSysUserForm");
			$('#editSysUserForm').bootstrapValidator('validate');
			if ($('#editSysUserForm').data('bootstrapValidator').isValid()) {
				var certDn = $('#certDn_E').val();
				if (certDn != null) { //要注册证书
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
						$("#X509CertE").val(exp);
						$("#certSnE").val(cert[9]);
					} else {
						var c0 = cert[0]; //提供者
						var c1 = cert[1]; //容器名
						Enroll.rsa_csp_setProvider(c0);
						var exp = Enroll.rsa_csp_exportSignX509Cert(c1);
						$("#X509CertE").val(exp);
						$("#certSnE").val(cert[6]);
					}
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
							loadUrl("${ctx }/sysUser/sysUserList.do");
						} else {
							if (typeof (data.message) == "undefined")
								layer.alert("操作失败", {
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
			} else {
				$('#editSysUserForm').bootstrapValidator('validate');
			}

		});



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
			$('#addsysuserCom').modal('hide');
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
			$('#addsysuserCom').modal('hide');



		});




	});
</script>