<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>

	<div class="title clearfix">
		<h2 style="font-size:34px; font-weight:bold;">RADS配置</h2>
	</div>
	<ul class="breadcrumb" style="">
		 <li>
			<i class="fa fa-home"></i>
			<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /    RADS配置</i>
		</li>
	</ul>	
	 <ul id="maintab" class="nav nav-tabs" style="height:30px;">
		<li class="active"><a href="#tab1"  onclick='showPage("tab1","${ctx }/system/rads/rsaCaConfig.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
				CA(RSA)</a>
		</li>
		<li class=""><a href="#tab2"  onclick='showPage("tab2","${ctx }/system/rads/sm2CaConfig.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle1">
				CA(SM2)</a>
		</li>
		<li class=""><a href="#tab3"  onclick='showPage("tab3","${ctx }/system/rads/certTemplateConfig.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle1">
				证书模板</a>
		</li>
		
	 </ul>
	 <div id="myTabContent" class="tab-content" style="margin-top:20px;">
			<div class="tab-pane fade in active" id="tab1"></div>
			<div class="tab-pane fade in" id="tab2"></div>
			<div class="tab-pane fade in" id="tab3"></div>
			
	 </div>


<script type="text/javascript">
function showPage(tabId, url) {
	$('#maintab a[href="#' + tabId + '"]').tab('show');
	$('#' + tabId).html('页面加载中，请稍后...');
	$('#' + tabId).load(url); // ajax加载页面

}
$(function() {
	$('#tab1').load("${ctx }/system/rads/rsaCaConfig.do");
	
});	
</script>

