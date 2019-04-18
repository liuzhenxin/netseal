<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
<h2 style="font-size:34px; font-weight:bold;">印章管理</h2>
</div>
<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">印章管理    /    印章信息查看</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			印章信息查看</a>
	</li>
</ul>
<div id="myTabContent" class="tab-content" style="margin-top: 20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="x_content">
							<br>
							<form id="demo-form4" action="" method="post" data-parsley-validate="" 
							class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
								<div class="form-group">
									<label class="control-label col-xs-3">印章名称 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.name }" 
										style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">印章类型 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.typeCn }" 
										style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">签章人 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.userName }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">证书DN </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.certDn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">单位名称 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.companyName }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">状态 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.statusCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<c:if test="${not empty seal.photoPath}">
									<div class="form-group">
										<label class="control-label col-xs-3">图片 </label>
										<div class="col-xs-6" style="margin-top: 8px;">
											<a href="#" data-toggle="modal" data-target="#myModal">查看</a>
										</div>
									</div>
								</c:if>
								<c:if test="${not empty seal.photoPath}">
									<div class="form-group">
										<label class="control-label col-xs-3">图片透明度 </label>
										<div class="col-xs-5">
											<input class="form-control col-xs-5" type="text" value="${seal.transparency }%"
												style="border: none; border-bottom: 1px solid #666; background: none;"readonly />
										</div>
									</div>
								</c:if>
								<div class="form-group">
									<label class="control-label col-xs-3">申请是否审核 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.isAuditReqCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">制章是否验证书 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.isAuthCertGenSealCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">下载是否验证书  </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.isAuthCertDownloadCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">印章是否可下载 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.isDownloadCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">印章生成时间 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.generateTimeCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">印章起止时间 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.notBeforCn }~${seal.notAfterCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">签章次数 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.usedCount }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="form-group">
									<label class="control-label col-xs-3">最大签章次数 </label>
									<div class="col-xs-5">
										<input class="form-control col-xs-5" type="text" value="${seal.usedLimitCn }"
											style="border: none; border-bottom: 1px solid #666; background: none;" readonly />
									</div>
								</div>
								<div class="ln_solid"></div>
								<div class="form-actions">
									<div class="row">
										<div class="col-md-offset-3 col-xs-6">
											<button type="button" class="btn btn-primary col-md-offset-3" id="back">返回</button>
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
				<h4 class="modal-title" id="myModalLabel" style="color:#3c8dbc;font-size:20px;">图片</h4>
			</div>
			<div class="modal-body">
				<div align="center">
					<img src="${ctx }/sealManage/viewPhoto.do?id=${seal.id }&time=<%=System.currentTimeMillis()%>" />
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>
	

<script type="text/javascript">
	$(function() {
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/sealManage/sealList.do");
		});
	});
</script>