<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<section class="content-header">
	<div class="headercontainer">
		<ul style="background-color: transparent;" class="breadcrumb location">
			<li><a style="text-decoration: none; cursor: default">印模管理/印模信息显示</a></li>
		</ul>
	</div>
</section>
<br /><br />
<section class="content">
	<div class="main">
		<div class="text-center" style="color:red;">
			<h4><strong>注: 该印模已制发印章,不允许修改</strong></h4>
		</div>
		<form id="editTemplateForm" class="form-horizontal" action="${ctx }/template/editTemplate.do" method="post"
			enctype="multipart/form-data">
			<input name="id" id="id" type="hidden" value="${template.id }" />
			<div class="sys-tab">
				
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">印模名称</label>
					<div class="col-sm-3">
						<input id="name" name="name" readonly class="form-control input-sm" type="text" value="${template.name }" />
					</div>
				</div>
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">印模类型</label>
					<div class="col-sm-3">
						<input id="type" name="type" readonly class="form-control input-sm" type="text" value="${template.typeCN }" />
					</div>
				</div>
				<div class="form-group">
					<label for="companyName" class="col-sm-3 control-label">单位</label>
					<div class="col-sm-3">
						<input id="companyName" readonly name="companyName" class="form-control input-sm" type="text" value="${template.companyName }" /> 
					</div>
				</div>
				<div class="form-group">
					<label for="photoFile" class="col-sm-3 control-label">印模图片</label>
					<div class="col-sm-3">
						<span>
							<c:if test="${template.photoPath!=null}">
								<a href="#" data-toggle="modal" data-target="#myModal">查看</a>
							</c:if>
							<c:if test="${template.photoPath==null}">无</c:if>
						</span>
					</div>
				</div>
				<div class="form-group">
					<label for="isAuditReq" class="col-sm-3 control-label">申请是否需要审核</label>
					<div class="col-sm-3">
						<label>${template.isAuditReqCN }</label>
					</div>
				</div>
				<div class="form-group">
					<label for="isAuthCertGenSeal" class="col-sm-3 control-label">制章是否验证用户证书</label>
					<div class="col-sm-3">
						<label>${template.isAuthCertGenSealCN }</label>
					</div>
				</div>
				<div class="form-group">
					<label for="isAuthCertDownload" class="col-sm-3 control-label">下载是否验证用户证书</label>
					<div class="col-sm-3">
						<label>${template.isAuthCertDownloadCN }</label>
					</div>
				</div>
				<div class="form-group">
					<label for="isDownload" class="col-sm-3 control-label">印章支持下载</label>
					<div class="col-sm-3">
						<label>${template.isDownloadCN }</label>
					</div>
				</div>
				<div class="form-group">
					<label for="befor" class="col-sm-3 control-label">启用日期</label>
					<div class="col-sm-3">
						<input id="befor" name="befor" class="form-control input-sm" readonly
							type="text" value="${template.notBeforTime }" />
					</div>
				</div>
				<div class="form-group">
					<label for="after" class="col-sm-3 control-label">结束日期</label>
					<div class="col-sm-3">
						<input id="after" name="after" class="form-control input-sm" readonly
							type="text" value="${template.notAfterTime }" />

					</div>
				</div>
				<div class="form-group">
					<label for="userListId" class="col-sm-3 control-label">用户列表</label>
					<div class="col-sm-3">
						<input id="userListId" readonly class="form-control input-sm"
							name="userListId" type="text" value="${template.userListId }" />
					</div>
				</div>
				<div class="form-group">
					<label for="remark" class="col-sm-3 control-label">备注</label>
					<div class="col-sm-3">
						<input id="remark" name="remark" class="form-control input-sm" readonly
							type="text" value="${template.remark }"><br>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label"></label>
					<div class="col-sm-2">
						<a href="javascript:loadUrl('${ctx }/template/templateList.do')">
							<input id="returnTemplateButton" type="button" class="btn btn-primary btn-sm" value="返回">
						</a>
					</div>
				</div>
			</div>
		</form>
		
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
	                    	<img src="${ctx }/template/viewPhoto.do?id=${template.id }"/> 
				        </div>
					</div>
					<div class="modal-footer">
		            	<button type="button"   class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
				</div>
	   		</div><!-- /.modal -->
		</div>
		
	</div>
</section>
<script type="text/javascript">
   
	
</script>
