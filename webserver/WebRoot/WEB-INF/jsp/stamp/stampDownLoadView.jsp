<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">批量制作图章</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">批量制作图章&nbsp;/&nbsp;图章下载</i>
	</li>
</ul>	
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			图章下载</a>
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
					<form id="stampDownLoadForm" class="form-horizontal form-label-left" action="" method="post" style="margin-top:20px;" >
						<div class="form-group">
							<label class="control-label col-xs-3"></label>
							<div class="col-xs-6">
							<font color="green"><strong>制作图章成功,请下载图章压缩文件：${stampPath }</strong></font>
							</div>
						</div>
					
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
									  <button id="downLoadStampButton" type="button" class="btn btn-primary col-md-offset-3">下载</button>
									  <button onclick="javascript:loadUrl('${ctx }/stamp/toGenStamp.do')" type="button" class="btn btn-primary col-md-offset-3">返回</button>
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

<script type="text/javascript">

$(function() {
	//下载
	$("#downLoadStampButton").click(function() {
		window.location.href="${ctx }/stamp/downLoadStamp.do?stampPath=${stampPath }";
	});
});


</script>