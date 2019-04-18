<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@ include file="../common/resource.jsp"%>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>NetSeal电子签章管理系统</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.7 -->
  <!-- Theme style -->

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

</head>
<body class="hold-transition login-page" style="background-color: rgb(42,63,84); overflow-y: hidden;">
<div class="login-box">
  <div class="login-logo" style="font-size:30px; color:aqua;">
    <p style="color:rgb(205,210,215); font-size:20px;">第二步、配置License，上传License文件</p>
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body" style="width:1000px; position:absolute; left: 50%; top:50%; margin-top:-200px; margin-left:-500px;">

    <div class="x_panel">
	  <div class="x_title">
	    <form id="upload" class="form-horizontal" action="${ctx }/sysUser/uploadLicense.do" method="post" enctype="multipart/form-data">
		<label for="photoFile" class="col-sm-5 control-label">选择License文件</label>
	
			<input class="pull-left"  id="licenseFile" name="licenseFiles" type="file" style=" height:34px;  float:right; margin-left:10px;"/>
		
		    <input type="button" value="上传" class="btn btn-primary importLicense" id="delete" style=" float:left; margin-left:10px;"/>
		</form>
		<div class="clearfix"></div>
	  </div>
	  <div class="x_content">
		<br>
		<div id="table-box" style="display: block;">
			<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
			  <thead>
				<tr>
					<th width="20%">文件名</th>
					<th width="20%">操作</th>
				</tr>
			  </thead>
			  <tbody>
				<c:if test="${FileNames !=''}">
					<tr>
						<td width="20%">${FileNames}</td>
						<td width="20%">
							<a href="${ctx }/sysUser/downLicenseApp.do">下载</a>
						</td>
					</tr>
				</c:if>
			  </tbody>
			</table>
		</div>
		 <div class="ln_solid"></div>
			<div class="form-actions">
				<div class="row">
					<div class="col-md-offset-5 col-xs-12">

				  <button id="backSysConfigInitBtn" type="button" class="btn btn-primary ">返回上一步</button>
				 
					</div>
				</div>
			</div>
	  </div>
	</div>

  </div>
  <!-- /.login-box-body -->
</div>
<script type="text/javascript">
	
$(function(){
	$(".importLicense").click(function(){
		var value= $("#licenseFile").val();
		if(value==""){
			layer.alert("请先选择文件",{icon:0});
			return;
		}
		var form = $("#upload");
		form.ajaxSubmit({
			success : function(data) {
				 if (data == "ok") {
					window.location.href("${ctx }/sysUser/viewLicense.do"); 
				} else {
					layer.alert(data, {icon : 0});
				} 
			},
			error : function() {
				layer.alert(data, {icon : 0});
			}
		});
	});
	$("#backSysConfigInitBtn").click(function() {		
		window.location.href="${ctx }/sysUser/sysConfigInit.do";
	});
});
</script>
</body>
</html>