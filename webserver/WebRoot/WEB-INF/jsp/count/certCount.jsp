<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
		<h2 style="font-size:34px; font-weight:bold;">统计</h2>
</div>
	  <ul class="breadcrumb" style="">
		 <li>
			<i class="fa fa-home"></i>
			<i style="color:rgb(42,63,84); font-style:normal;" id="bread">统计    /    证书统计</i>
		</li>
	 </ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			证书统计</a>
	</li>
</ul>	


<div id="myTabContent" class="tab-content" style="margin-top:20px;">
		<div class="tab-pane fade in active" id="home">
			<section class="content">						
				<div class="row">
				  <div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
					   	<div class="x_content" id="tab1">
					   		<form id="demo-form" class="form-horizontal form-label-left" style="margin-top:20px;" >
								<div class="form-group">
									<label class="control-label col-xs-3">管理员证书数量
									</label>
									<div id="countS" class="col-xs-8">
									  <p id="sysUserCount" class="col-xs-8 showdetail">${sysUserCount}</p>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">签章人证书数量
									</label>
									<div id="count" class="col-xs-8">
									  <p id="userCount" class="col-xs-8 showdetail">${userCount}</p>
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
