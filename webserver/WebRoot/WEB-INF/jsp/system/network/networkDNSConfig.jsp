<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
        <!-- Main content -->
<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">网络配置</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /    DNS设置</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
						DNS设置</a>
				</li>
			</ul>
  			  
  			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
							
								<div class="x_title" id="x_title1" >
							  		<input type="button" value="添加" class="btn btn-primary" id="networkDNSAddButton" style=" float:left;"/>
							  		<input type="button" value="修改" class="btn btn-primary" id="networkDNSEditButton" style=" float:left; margin-left:10px;"/>
							  		<input type="button" value="返回" class="btn btn-primary" id="returnButton" style=" float:left; margin-left:10px;"/>
							  		<input type="button" value="删除" class="btn btn-primary" id="networkDNSDeleteButton" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm"/>
								<div class="clearfix"></div>
							  </div>
							  
							  
							  <div id="table-box1">
									<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
									  <thead>
										<tr>
											<th width="1"><input id="checkboxChange" class="js-checkbox-all " type="checkbox" /></th>
											<th>DNS服务器</th>
										</tr>
									  </thead>
									  <tbody>
									   <c:forEach items="${dnsIpList }" var="dnsIp">
										<tr>
											<th width="1">
												<input name="checkboxt" class="input_checked" type="checkbox" value="${dnsIp }">
											</th>
											<td>${dnsIp}</td>
										</tr>
										</c:forEach>
									  </tbody>
									</table>
								</div>
							</div>
						  </div>
						</div>
					</section>
				</div>
			</div>	
    
  			
  			
  			
  			
	
<script type="text/javascript">
	$(function() {	
		$("#returnButton").click(function(){
			loadUrl("${ctx }/system/network/networkList.do");
			
		});
		
		$("#networkDNSAddButton").click(function() {
			 loadUrl("${ctx }/system/network/networkDNSAdd.do"); 
		 });
		$("#networkDNSEditButton").click(function() {
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
				 layer.alert( "请选择一条要修改的记录", {icon:0});
				 return;
			 }
				 
			 loadUrl("${ctx }/system/network/networkDNSEdit.do?dnsIp="+id); 
			 
		 });
		 $("#checkboxChange").click(function() {
			 $("[name=checkboxt]:checkbox").prop("checked",this.checked);	 			
		 });
		//删除方法
		$("#networkDNSDeleteButton").click(function() {
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
				 layer.alert( "请选择一条要删除的记录", {icon:0});
				 return;
			 }
			 
			layer.confirm("确定要删除该DNS服务器设置吗?",{btn:["确定","取消"]},function(){
				$.ajax({
					url : "${ctx }/system/network/networkDNSDelete.do",
					type : "get",
					data : "dnsIp=" + id,
					dataType : "json",
					success : function(data) {
						if (data.success) {
							layer.alert( data.message, {icon:1});
							loadUrl("${ctx }/system/network/networkDNSConfig.do");
						} else {
							layer.alert( data.message, {icon:2});
						}
					},
					error : function() {
						layer.alert( "请求失败", {icon:2});
					}
				});
			});
		});
			
		 
	});
	
	
	
</script>