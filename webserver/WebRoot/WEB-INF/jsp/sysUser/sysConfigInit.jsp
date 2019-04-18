<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@ include file="../common/resource.jsp"%>
<html>
<head>
  <meta charset="utf-8">
  <title>NetSeal电子签章管理系统</title>
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

</head>
<body class="hold-transition login-page" style="background-color: rgb(42,63,84); overflow-y: hidden;">
<div class="login-box">
  <div class="login-logo" style="font-size:30px; color:aqua;">
    <p style="color:rgb(205,210,215); font-size:20px;">第一步、系统基本信息配置</p>
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body" style="width:1000px; position:absolute; left: 50%; top:50%; margin-top:-200px; margin-left:-500px;">

    <div class="x_panel">
    <div class="main">
   		
		<form id="sysConfigInitForm" class="form-horizontal form-label-left" action="${ctx }/sysUser/sysConfigInitSave.do" method="post"  style="margin-top:20px;" >
			<div class="form-group">
				<label class="control-label col-xs-3">应用标识</label>
				<div class="col-xs-4">
					<input class="form-control" type="text" id="tablePrefixId" name="tablePrefixId" value="${tablePrefixId }">
				</div>
				<div class="col-sm-3">区分不同的服务器</div>
			</div>
			<div class="form-group">
				<label class="control-label col-xs-3">绑定网口名称 
				</label>
				<div class="col-xs-4">
				  <select id="networkCard" name="networkCard" class="form-control col-xs-6">
						<c:forEach items="${netWorkNameList}" var="value">
							<option value="${value}">${value}</option>
						</c:forEach>
						</select>
				</div>
				<div class="col-sm-3">用于License获取网口信息</div>
			</div>

			<div class="form-group">
				<label class="control-label col-xs-3">WebService地址
				</label>
				<div class="col-xs-4">
				  <input type="text" id="wsUrl" name="wsUrl" value="${wsUrl }" class="form-control col-xs-6">
				</div>
				<div class="col-sm-3">WebService提供服务的地址</div>
			</div>
			

		  <div class="ln_solid"></div>
			<div class="form-actions">
				<div class="row">
					<div class="col-md-offset-3 col-xs-6">

				  <button id="sysConfigInitSaveBtn" type="button" class="btn btn-primary col-md-offset-4">保存并下一步</button>
				 
					</div>
				</div>
			</div>

		</form>

	</div>
	</div>

  </div>
</div>
<script type="text/javascript">
	$(function() {
		$("#tablePrefixId").val("${tablePrefixId}");
		$("#networkCard").val("${networkCard}");
		$("#wsUrl").val("${wsUrl}");
		
		$("#sysConfigInitSaveBtn").click(function() {
			var form = $("#sysConfigInitForm");
			form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				form.ajaxSubmit({
					success : function(data) {
						if(data.success){
							window.location.href="${ctx }/sysUser/toLogin.do";
						}else{
							layer.alert( data.message, {icon:2});
						}
					},
					error : function() {
						layer.alert( "请求失败", {icon:2});
					}
				});
			} else {
				form.bootstrapValidator('validate');
			}

		});
		
		$("#sysConfigInitForm").bootstrapValidator({			
			fields : {
				tablePrefixId : {
					validators : {
						notEmpty : {
							message : '应用标识不能为空'
						},
						regexp: {
	                         regexp: /^[1-9][0-9]$/,
	                         message: '应用标识由10到99两位数字组成'
	                    }
					}
				},
				networkCard : {
					validators : {
						notEmpty : {
							message : '绑定网口名称不能为空'
						}
					}
				},
				wsUrl : {
					validators : {
						notEmpty : {
							message : 'WebService地址不能为空'
						},
						stringLength : {
							min : 1,
							max : 150,
							message : 'WebService地址长度为1~150'
						}
					}
				}
			}
		});
	});
</script>
</body>
</html>