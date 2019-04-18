<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<style type="text/css">
	.check-table0 {border-collapse:collapse;line-height: 40px;width:100%;}
	.check-table0 td {border: 0px;padding: 0 3px;}
	.check-table {border-collapse:collapse;line-height: 40px;width:100%;}
	.check-table td {border: 1px solid #CCCCCC;padding: 0 3px;font-size:14px;}
	.check-table th{border: 1px solid #CCCCCC;font-size:14px;text-align: center;}
	.text-r{text-align:right;}
	.text-c{text-align:center;}
	.font20{font-size:20px;}
	.font18{font-size:18px;}
	.font14{font-size:14px;}
</style>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">产品巡检</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理&nbsp;/&nbsp;产品巡检</i>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-xs-12">
							<div class="x_panel">
							<div class="x_title" id="x_title">
							 	 <input id="downCheckReport" class="btn btn-primary" type="button" value="下载巡检报告" style=" float:left;">
							  		<div class="clearfix"></div>
							  </div>
  <table class="check-table0">
    <tr>
        <td class="font14 text-r"><b>报告编号：${reportNumber }</b></td>
    </tr>
	</table>
   <table class="check-table">
    <tr>
        <th><b><span class="font20">产品巡检(电子签章)</span></b></th>
    </tr>
	</table><br>
	<strong class="font18">1. 系统概况</strong><br>
	信安电子签章系统巡检报告<br><br>
	<strong class="font18">2. 产品信息</strong><br><br>
    <strong class="font14">产品信息</strong><br>
	<table class="check-table">
    <tr class="text-c">
        <th>服务器名称</th>
        <th>产品型号</th>
        <th>版本号</th>
        <th>License信息</th>
        <th>IP地址</th>
    </tr>
	<tr class="text-c">
        <td>电子签章服务器</td>
        <td>NetSeal</td>
        <td>${version }</td>
        <td>${licenseNumber }</td>
        <td>${ip }</td>
    </tr>	
	</table><br>
	<strong class="font18">3. 运行状况</strong><br><br>
	 <strong class="font14">系统信息</strong><br>
	      <table class="check-table">   
    <tr class="text-c">
        <th>机器名称</th>
        <td>NetSeal</td>
        <th>主/备机</th>
        <td>${nodeNameCN }</td>
        <th>管理地址</th>
        <td>https://${ip }:8443/webserver</td>
    </tr>	
	</table><br>
	<strong class="font14">端口和IP对应关系</strong><br>
	<table class="check-table">   
    <tr class="text-c">
        <th>序号</th>
        <th>网口</th>
        <th>IP地址</th>
    </tr>
    <c:forEach items="${ipList}" var="item">
   		${item }
    </c:forEach>
	</table><br>
	<strong class="font14">系统检测</strong><br>
	<table class="check-table">   
    <tr class="text-c">
        <th>检测内容</th>
        <th>检测方法</th>
        <th>检测结果</th>
        <th>判断标准</th>
        <th>状况分析</th>
    </tr>
	 <c:forEach items="${checkList}" var="item">
    	${item }
    </c:forEach>
	</table><br>
	
	<strong class="font18">4. 巡检结论</strong><br>
	<c:forEach items="${resultList}" var="item">
    	<strong> ${item }</strong><br>
    </c:forEach>
	
	   	
	</div>
			  </div>
			</div>
		  </section>
	    </div>
  	  </div>  
<script type="text/javascript">
$(function() {
	$("#downCheckReport").click(function() {
		var path="${reportPath}";
		if(path != ""){			
			window.location.href="${ctx }/system/downCheckReport.do?reportPath="+path;
		}else{
			layer.alert( "请求失败", {icon:2});
		}
	});
	
});
</script>