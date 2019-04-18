<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
<h2 style="font-size:34px; font-weight:bold;">操作日志</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;">日志管理    /    操作日志    /    错误详情</i>
	</li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			 错误详情</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			  <div class="x_content">
				<br>
				<div id="table-box">
					<form data-parsley-validate="" class="form-horizontal form-label-left" action="" method="post" novalidate style="margin-top:20px;" >
					  <div class="form-group">
						<label class="control-label col-xs-3">错误详情 
						</label>
						<div class="col-xs-6">
						  <c:forEach items="${page.result}" var="operateLog">
					     	<textarea readonly rows="6"  class="form-control" >${operateLog.errMsg }</textarea>
				         </c:forEach>
						</div>
					  </div>
					  
					 
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
								  <button class="btn btn-primary col-md-offset-6" type="button" style="" id="back">返回</button>
									</div>
								</div>
							</div>
						</form>
					</div>
				  </div>
				</div>
			  </div>
			</div>
		</section>
	</div>
	
	
</div>

<script type="text/javascript">
	$(function() {
		
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/log/operateLog.do");
		});
	});


</script>