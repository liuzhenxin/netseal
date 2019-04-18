<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">印章审核</h2>
</div>
<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;"> 印章管理/ 印章审核    / 印章详情</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			印章详情</a>
	</li>
</ul>


	<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-xs-12 col-xs-12">
				<div class="x_panel">
				  <div class="x_content">
				  
				<form  class="form-horizontal form-label-left" style="margin-top:20px;" action="" method="post">
				<div class="form-group">
					<label for="accountE" class="col-xs-3 control-label">印章名称:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${request.name }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>
				<div class="form-group">
					<label for="accountE" class="col-xs-3 control-label">签章人:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${uName }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>
				<div class="form-group">
					<label for="accountE" class="col-xs-3 control-label">单位名称:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${companyName }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>
				<div class="form-group">
					<label for="accountE" class="col-xs-3 control-label">注册证书DN:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${certDn}" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>
				<div class="form-group">
					<label for="name" class="col-xs-3 control-label">申请时间:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${request.generateTimeCn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>
				<div class="form-group">
					<label for="name" class="col-xs-3 control-label">印章启用日期:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${request.notBeforCn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>	
				<div class="form-group">
					<label for="name" class="col-xs-3 control-label">印章结束日期:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${request.notAfterCn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>	
				<div class="form-group">
					<label for="name" class="col-xs-3 control-label">印模:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${request.templateName }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>
				<c:if test="${not empty request.photoPath}">
					<div class="form-group">
						<label for="name" class="col-xs-3 control-label">图片</label>
						<div class="col-xs-6" style="margin-top:8px">
							<a href="#" data-toggle="modal" data-target="#myModal">查看</a>
						</div>
					</div>
				</c:if>
				<div class="form-group">
					<label for="name" class="col-xs-3 control-label">备注:</label>
					<div class="col-xs-6">
						<input class="form-control col-xs-5" type="text" value="${request.remark }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
					</div>
				</div>														
				<div class="form-group">
			        <div class="col-md-offset-5">
			           <button type="button" id="back" class="btn btn-primary ">返回</button>
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
							<img src="${ctx }/audit/viewPhoto.do?id=${request.id }&time=${request.updateTime }" />
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
			loadUrl("${ctx }/audit/auditList.do");
		});
	});


</script>