<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">CRL管理</h2>
</div>
<ul class="breadcrumb" style="">
	<li><i class="fa fa-home"></i> <i style="color:rgb(42,63,84); font-style:normal;">系统管理 / CRL管理</i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;"> CRL管理</a></li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="main">

							<form id="crlManageForm" action="${ctx }/system/crlManageSave.do" method="post" class="form-horizontal form-label-left" style="margin-top:20px;">
								<div class="form-group">
									<label class="control-label col-xs-3">当前状态 </label>
									<div class="col-xs-6">
										<c:if test="${crlIsRunning==true}">
											<font color="green"><b>CRL同步运行中...</b></font>
										</c:if>
										<c:if test="${crlIsRunning==false}">
											<font color="red"><b>CRL同步没有启动</b></font>
										</c:if>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap连接地址 </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapUrl" value="${config.ldapUrl }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap上下文类 </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapContextFactory" value="${config.ldapContextFactory }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap用户名 </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapAccount" value="${config.ldapAccount }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap密码 </label>
									<div class="col-xs-6">
										<input type="password" class="form-control col-xs-6" name="ldapPassword" value="${config.ldapPassword }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap认证方式 </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapSecurityAuthentication" value="${config.ldapSecurityAuthentication }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap基准DN </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapBaseDn" value="${config.ldapBaseDn }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap同步过滤 </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapFilter" value="${config.ldapFilter }">
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">ldap同步间隔 </label>
									<div class="col-xs-6">
										<input type="text" class="form-control col-xs-6" name="ldapInterval" value="${config.ldapInterval }">(单位 秒)
									</div>
								</div>
								<div class="ln_solid"></div>
								<div class="form-actions">
									<div class="row">
										<div class="col-md-offset-3 col-xs-6">
											<button type="button" id="saveConfigBtn" class="btn btn-primary col-md-offset-3" data-toggle="modal" data-target=".bs-example-modal-sm11">保存</button>
											<c:if test="${crlIsRunning==false}">
												<input class="btn btn-primary col-md-offset-3" type="button" onclick="crlManageOper(1)" value="启动">
											</c:if>
											<c:if test="${crlIsRunning==true}">
												<input class="btn btn-primary col-md-offset-3" type="button" onclick="crlManageOper(0)" value="停止">
											</c:if>

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
	function crlManageOper(operType) {
		$.ajax({
			url : "${ctx }/system/crlManageOper.do",
			type : "get",
			data : "operType=" + operType,
			dataType : "json",
			success : function(data) {
				if (data.success) {
					layer.alert(data.message, {
						icon : 1
					});
				} else {
					layer.alert(data.message, {
						icon : 2
					});
				}
				loadUrl("${ctx }/system/crlManage.do");
			},
			error : function() {
				layer.alert("请求失败", {
					icon : 2
				});
			}
		});
	}
	$(function() {
		$("#saveConfigBtn").click(function() {
			var form = $("#crlManageForm");
			var ldapAccount = $("input[name='ldapAccount']").val();
			var ldapPassword = $("input[name='ldapPassword']").val();
			if (ldapAccount) {
				if (ldapAccount.length < 2 || ldapAccount.length > 30) {
					layer.alert("用户名长度为2~30", {
						icon : 2
					});
					return false;
				}
				if (ldapPassword.length == 0) {
					layer.alert("密码不能为空", {
						icon : 2
					});
					return false;
				}
			}
			form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				form.ajaxSubmit({
					success : function(data) {
						if (data.success) {
							layer.alert(data.message, {
								icon : 1
							});
						} else {
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
				form.bootstrapValidator('validate');
			}
		});
		//---表单验证---
		$("#crlManageForm").bootstrapValidator({
			fields : {
				ldapUrl : {
					validators : {
						notEmpty : {
							message : '连接地址不能为空'
						}
					}
				},
				ldapContextFactory : {
					validators : {
						notEmpty : {
							message : 'ldap上下文类不能为空'
						}
					}
				},

				ldapSecurityAuthentication : {
					validators : {
						notEmpty : {
							message : 'ldap认证方式不能为空'
						}
					}
				},
				ldapBaseDn : {
					validators : {
						notEmpty : {
							message : 'ldap基准DN不能为空'
						},
					}
				},
				ldapInterval : {
					validators : {
						notEmpty : {
							message : 'ldap同步间隔不能为空'
						},
						regexp : {
							regexp : /^[0-9]*$/,
							message : 'ldap同步间隔只能由数字组成'
						},
						greaterThan : {
							value : 3600,
							message : 'ldap同步间隔须>=3600'
						}
					}
				}
			}
		});
	});
</script>