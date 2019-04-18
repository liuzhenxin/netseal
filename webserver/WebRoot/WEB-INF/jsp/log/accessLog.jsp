<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">访问日志</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;">日志管理    /    访问日志</i>
	</li>
</ul>	
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#tab1" onclick='showPage("tab1","${ctx }/log/accessLogList.do")' data-toggle="tab" style="height:30px; line-height:3px;">
						数据库</a>
				</li>
				<li class=""><a href="#tab2" onclick='showPage("tab2","${ctx }/log/accessLogFileList.do")' data-toggle="tab" style="height:30px; line-height:3px;">
						文件</a>
				</li>
			</ul>
			
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="tab1">
					
				</div>
				
				<div class="tab-pane fade in " id="tab2">
				</div>	
			  </div>
	
<script type="text/javascript">
$(function(){
	$('#tab1').load("${ctx }/log/accessLogList.do");
});

function showPage(tabId, url){
	$('#maintab a[href="#'+tabId+'"]').tab('show');
	$('#'+tabId).html('<br/>页面加载中，请稍后...');
	$('#'+tabId).load(url); // ajax加载页面
	
}
</script>

