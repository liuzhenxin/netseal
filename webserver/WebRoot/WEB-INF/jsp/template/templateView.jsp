<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
      <h2 style="font-size:34px; font-weight:bold;">添加印模</h2></div>
    <ul class="breadcrumb" style="">
      <li>
        <i class="fa fa-home"></i>
        <i style="color:rgb(42,63,84); font-style:normal;">印模管理    /    印模信息查看</i></li>
    </ul>
    <ul id="myTab" class="nav nav-tabs" style="height:30px;">
      <li class="active">
        <a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">印模信息查看</a></li>
    </ul>

    <div id="myTabContent" class="tab-content" style="margin-top:20px;">
      <div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
						  <form id="demo-form3" class="form-horizontal form-label-left" style="margin-top:20px;" >
								<div class="form-group">
									<label class="control-label col-xs-3" >印模名称: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.name}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >印模类型:
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.typeCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >单位名称: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.companyName}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<c:if test="${not empty template.photoPath}">
									<div class="form-group">
										<label class="control-label col-xs-3" >印模图片:
										</label>
										<div class="col-xs-8">
											<div class="col-sm-3" style="margin-top:8px;">
												<a href="#" data-toggle="modal" data-target="#myModal" >查看</a>
										  	</div>
										</div>
									</div>
								</c:if>
								<c:if test="${not empty template.photoPath}">
									<div class="form-group">
										<label class="control-label col-xs-3" >图片透明度:
										</label>
										<div class="col-xs-5" >
											<input class="form-control col-xs-5" type="text" value="${template.transparency}%" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
										</div>
									</div>
								</c:if>
								<div class="form-group">
									<label class="control-label col-xs-3" >申请是否审核: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.isAuditReqCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >制章是否验证书: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.isAuthCertGenSealCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >下载是否验证书: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.isAuthCertDownloadCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >印章是否可下载:
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.isDownloadCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >注册时间: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.generateTimeCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >启用时间: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.notBeforCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >停用时间: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.notAfterCn}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >用户列表: 
									</label>
									<div class="col-xs-5">
									  <input class="form-control col-xs-5" type="text" value="${template.userNames}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3" >备注: 
									</label>
									<div class="col-xs-5"  >
										<input class="form-control col-xs-5" type="text" value="${template.remark}" style="border:none; border-bottom: 1px solid #999; background:none;" readonly/>
									</div>
								</div>


						  <div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-4 col-xs-6">
										  <button  id="back" type="button" class="btn btn-primary col-md-offset-1" >返回</button>
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


   <!-- 查看印模图片模态框 -->	
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	   		<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel" style="color:#3c8dbc;font-size:20px;"> 印模图片 </h4>
		            </div>
		            <div class="modal-body">
				        <div align="center">
	                       <img src="${ctx }/template/viewPhoto.do?id=${template.id }&time=${template.updateTime }" /> 
				        </div>
			
					</div>
					<div class="modal-footer">
		                <button type="button"   class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
				</div>
	 	   	</div>
		</div>
	

<script type="text/javascript">
	$(function() {
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/template/templateList.do");
		});
	});


</script>