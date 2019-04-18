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
			

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
                <div class="tab-pane fade in active" id="home">
                    <section class="content">                       
                        <div class="row">
                          <div class="col-md-12 col-sm-12 col-xs-12">	
						    <div class="x_panel">
							  <div class="x_title">
							    <form id="upload" class="form-horizontal" action="${ctx }/sysUser/uploadLicense.do" method="post" enctype="multipart/form-data">
							    <label  class="col-xs-2 control-label">选择License文件</label>
									<div class="col-xs-4 pull-left">
									  <input id="licenseFile" name="licenseFiles" type="file" class="form-control input-sm col-xs-8" style="cursor: pointer;">
									</div>
							  		<input type="button" value="上传" class="btn btn-primary importLicense" id="addCertChain" style=" float:left; margin-left:10px;"/>
							    
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
											<div class="col-md-offset-4">
										      <button id="backLicenseBtn" type="button" class="btn btn-primary ">返回</button>
											</div>
										</div>
									</div>
							  </div>
							</div>
						</div>  
                    </div>
                </section>
            </div>  
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
					loadUrl("${ctx }/system/license/licenseUploadView.do"); 
				} else {
					layer.alert(data, {icon : 0});
				} 
			},
			error : function() {
				layer.alert(data, {icon : 0});
			}
		});
	});
	$("#backLicenseBtn").click(function() {
		loadUrl("${ctx }/system/license/licenseShow.do");
	});
});
</script>
