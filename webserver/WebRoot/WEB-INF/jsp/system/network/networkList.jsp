<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
       		<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">网络配置</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /    网络配置</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
						网络配置</a>
				</li>
			</ul>
  			
  			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12"> 
							<div class="x_panel">
							  <div class="x_title" id="x_title">
							  		<input type="button" value="网口设置" class="btn btn-primary" id="networkCardConfigButton" style=" float:left;"/>
							  		<input type="button" value="DNS设置" class="btn btn-primary" id="networkDNSConfigButton" style=" float:left; margin-left:10px;"/>
							  		<input type="button" value="默认网关设置" class="btn btn-primary" id="networkGatewayConfigButton" style=" float:left; margin-left:10px;"/>
							  		<div class="clearfix"></div>
							  </div>	
							 <div class="x_content">
								<div id="table-box" style="display: block;">
									<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
									  <thead>
										<tr>
											<th width="1"><input id="checkboxChange" class="js-checkbox-all " type="checkbox" /></th>
											<th>网口名</th>
											<th>IP地址</th>
											<th>子网掩码</th>
										</tr>
									  </thead>
									  <tbody>
									  <c:forEach items="${networkCardList}" var="networkCard">
										 <tr>
											<th width="1">
												<input name="checkboxt" class="input_checked" type="checkbox" value="${networkCard.name }">
											</th>
											<td>${networkCard.name}</td>
											<td>${networkCard.ip }</td>
											<td>${networkCard.mask }</td>
										  </tr>
										 
										</c:forEach>
									  </tbody>
									</table>
									<span><font color="red"><b>${msg }</b></font></span>	
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
	
		 $("#networkCardConfigButton").click(function() {
			 var id="";
			 var index = 0;
			 $("[name=checkboxt]:checkbox:checked").each(function(){
					if (this.checked) {
						if (index == 0) {
							id = $(this).val();
						} 
						index++;
					}
			 });
			 if(index == 0 || index>1){
				 layer.alert( "请选择一条要设置的记录", {icon:0});
				 return;
			 }else{
				 
			 loadUrl("${ctx }/system/network/networkCardConfig.do?name="+id); 
			 }
		 });
		 $("#checkboxChange").click(function() {
			 $("[name=checkboxt]:checkbox").prop("checked",this.checked);	 			
		 });
		 
		 $("#networkGatewayConfigButton").click(function() {
			 loadUrl("${ctx }/system/network/networkGatewayConfig.do"); 
		 });
		 $("#networkDNSConfigButton").click(function() {
			 loadUrl("${ctx }/system/network/networkDNSConfig.do"); 
		 });
	});

	
</script>