<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>

<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">License</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;">系统管理    /    License</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active">
				<a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">License</a>
				</li>
			</ul>
		
			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
							<div class="x_title" id="x_title">
						  		<a href="${ctx }/system/license/downloadLicense.do">
						  		<input type="button" value="备份下载" class="btn btn-primary" id="downloadLicenseButton" style="float:left; margin-left:10px;"/>
						  		</a>
						  		<input type="button" value="更新" class="btn btn-primary" id="updateLicenseButton" style="float:left; margin-left:10px;"/>
						  		<div class="clearfix"></div>
							  </div>
								<div class="main">
									<form id="configForm"  method="post" class="form-horizontal form-label-left" style="margin-top:20px;" >
										<div class="form-group">
											<label class="control-label col-xs-3">Guid 
											</label>
											<div class="col-xs-8">
											  <p name="account" class="col-xs-8 showdetail">${li.guid }</p>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">起始时间 
											</label>
											<div class="col-xs-8">
											   <p class="col-xs-8 showdetail">${strTimeStart }</p>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">截止时间 
											</label>
											<div class="col-xs-8">
											  <p class="col-xs-8 showdetail">${strTimeEnd }</p>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">连接数限制 
											</label>
											<div class="col-xs-8">
											  <p class="col-xs-8 showdetail">${li.maxThred }</p>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">证书数限制 
											</label>
											<div class="col-xs-8">
											  <p class="col-xs-8 showdetail">${li.maxCertNum }</p>
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
$(function() {
	
	 $("#updateLicenseButton").click(function() {
		 layer.confirm("更新前请 <a href='${ctx }/system/license/downloadLicense.do'>备份下载</a>, 确定更新?",{btn:["确定","取消"]},function(index){
			layer.close(index);
		 	loadUrl("${ctx }/system/license/toUpdateLicense.do");
		 });
	 });
});
</script>