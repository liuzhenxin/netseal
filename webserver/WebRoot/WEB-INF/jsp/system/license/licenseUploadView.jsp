<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
       		<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">License</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /    License&nbsp;/&nbsp;更新</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
						更新</a>
				</li>
			</ul>
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
										class="btn btn-primary col-md-offset-1" type="button" style="">保存</button>
								</div>
							</div>
						</div>

					</form>

				</div>
			</div>

	<script type="text/javascript">
		$(function() {
			//放弃License文件
			$("#deleteLicense").click(function() {
				loadUrl("${ctx }/system/license/deleteLicense.do");
			});
	
			//保存License文件
			$("#saveLicense").click(function() {
				layer.confirm("更新成功后, 实时生效, 确定更新?",{btn:["确定","取消"]},function(index){
					layer.close(index);
					var url="${ctx }/system/license/saveLicense.do";
					$.ajax({
					    url:url,
					    type:"get",
					    data:"",
					    dataType:"json",
					    success:function(data){
					        if(data.success){
					        	layer.alert(data.message,{icon:1});
					        }else{
					        	layer.alert(data.message,{icon:2});
					        }
					        loadUrl("${ctx }/system/license/licenseShow.do");					    	
					    },error:function(){
					    	layer.alert("请求失败",{icon:2});
					    }
					 });
				});
			});
		});
	</script>
