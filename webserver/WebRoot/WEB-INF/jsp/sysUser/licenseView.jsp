<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@ include file="../common/resource.jsp"%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>NetSeal电子签章管理系统</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

</head>
<body class="hold-transition login-page"
	style="background-color: rgb(42,63,84); overflow-y: hidden;">
	<div class="login-box">
		<div class="login-logo" style="font-size:30px; color:aqua;">
			<p style="color:rgb(205,210,215); font-size:20px;">第二步、配置License，License文件信息</p>
		</div>
		<!-- /.login-logo -->
		<div class="login-box-body" style="width:1000px; position:absolute; left: 50%; top:50%; margin-top:-200px; margin-left:-500px;">
			<div class="x_panel">
				<div class="main">
					<form id="demo-form" action="" method="post"
						class="form-horizontal form-label-left" style="margin-top:20px;">


						<div class="form-group">
							<label class="control-label col-xs-3">Guid </label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${li.guid }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">起始时间 </label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${strTimeStart }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">截止时间 </label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${strTimeEnd }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">连接数限制 </label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${li.maxThred }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">证书数限制 </label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${li.maxCertNum }</p>
							</div>
						</div>
						<div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-4 col-xs-6">
									<button id="deleteLicense"
										class="btn btn-primary col-md-offset-1"  type="button" style="">取消</button>
									<button id="saveLicense"
										class="btn btn-primary col-md-offset-1" type="button" style="">保存并下一步</button>
								</div>
							</div>
						</div>

					</form>

				</div>
			</div>

		</div>
		<!-- /.login-box-body -->
	</div>
	<script type="text/javascript">
		$(function() {
			//放弃License文件
			$("#deleteLicense").click(function() {
				window.location.href("${ctx }/sysUser/deleteLicense.do");
			});
	
			//保存License文件
			$("#saveLicense").click(function() {
				var url="${ctx }/sysUser/saveLicense.do";		
				$.ajax({
				    url:url,
				    type:"get",
				    data:"",
				    dataType:"json",
				    success:function(data){
				        if(data.success){	
				        	window.location.href("${ctx }/sysUser/toLogin.do");
				        }else{
				        	layer.alert(data.message,{icon:2});
				        }
				    	
				    },error:function(){
				    	layer.alert("请求失败",{icon:2});
				    }
				 });
			});
		});
	</script>
</body>
</html>