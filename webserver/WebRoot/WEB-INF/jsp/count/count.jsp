<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>



<div class="title clearfix">
		<h2 style="font-size:34px; font-weight:bold;">统计</h2>
</div>
	  <ul class="breadcrumb" style="">
		 <li>
			<i class="fa fa-home"></i>
			<i style="color:rgb(42,63,84); font-style:normal;" id="bread">统计    /    统计</i>
		</li>
	 </ul>	
	 

	 <ul id="maintab" class="nav nav-tabs" style="height:30px;">
		<li class="active"><a href="#tab1"  onclick='showPage("tab1","${ctx }/count/userCount.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
				印章统计</a>
		</li>
		<li class=""><a href="#tab2"  onclick='showPage("tab2","${ctx }/count/sealCount.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle1">
				签章人统计</a>
		</li>
	 </ul>
	 <div id="myTabContent" class="tab-content" style="margin-top:20px;">
			<div class="tab-pane fade in active" id="tab1"></div>
			<div class="tab-pane fade in" id="tab2"></div>
	 </div>


<script type="text/javascript">
    
	$(function() {
		$('#tab1').load("${ctx }/count/userCount.do");
	});
	
	function showPage(tabId, url) {
		$('#maintab a[href="#' + tabId + '"]').tab('show');
		$('#' + tabId).html('页面加载中，请稍后...');
		$('#' + tabId).load(url); // ajax加载页面

	}
</script>