<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">HA管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统配置    /    HA管理</i>
	</li>
</ul>
  
<ul class="nav nav-tabs" id="maintab" style="height:30px;">
  <li class="active"><a href="#tab1"  onclick='showPage("tab1","${ctx }/system/ha/haConfig.do")' style="height:30px; line-height:3px;">HA配置</a></li>
  <li><a href="#tab2" onclick='showPage("tab2","${ctx }/system/ha/haService.do")'  style="height:30px; line-height:3px;">HA服务</a></li>
  <li><a href="#tab3" onclick='showPage("tab3","${ctx }/system/ha/haLog.do")'  style="height:30px; line-height:3px;">HA日志</a></li>
  <li><a href="#tab4" onclick='showPage("tab4","${ctx }/system/ha/hostsConfig.do")'  style="height:30px; line-height:3px;">hosts文件配置</a></li>
</ul>

<div  id="myTabContent" class="tab-content"  style="margin-top:20px;">
  	<div class="tab-pane fade in active" id="tab1"></div>
  	<div class="tab-pane fade in" id="tab2"></div>
  	<div class="tab-pane fade in" id="tab3"></div>
  	<div class="tab-pane fade in" id="tab4"></div>
</div>
	

<script type="text/javascript">
$(function(){
	$('#tab1').load("${ctx }/system/ha/haConfig.do");
});

function showPage(tabId, url){
	$('#maintab a[href="#'+tabId+'"]').tab('show');
	$('#'+tabId).html('<br/>页面加载中，请稍后...');
	$('#'+tabId).load(url); // ajax加载页面
	
}
</script>

