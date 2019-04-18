<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">PDF模板管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;">印模管理    /    PDF模板管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			PDF模板管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
				  <div class="x_title clearfix">
				  		
				  		<input type="button" value="增加" class="btn btn-primary" id="add"  onclick="addTemplate()" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
				  		<input type="button" value="下载" class="btn btn-primary" id="download"  onclick="downloadTemplate()" style=" float:left; margin-left:25px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
				  		<input type="button" value="文本域名称" class="btn btn-primary" id="details"  onclick="templateDetails()" style=" float:left; margin-left:25px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
				  		<input type="button" value="删除" class="btn btn-primary" id="delCert"  onclick="judgeCheckbox2('删除所选文件','${ctx }/system/delPdfTemplate.do');" style=" float:left; margin-left:25px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
				  		
						
					  <div class="clearfix"></div>
				  </div>
				  
				  <div class="x_content">
					<div id="table-box" style="display: block;">
						<table class="table table-striped table-bordered dt-responsive nowrap" >
						    <thead>
						      <tr>
						      	<th width="1"><input id="checkboxChange" class="js-checkbox-all" type="checkbox" /></th>
						        <th>文件名</th>
						        <th>上传时间</th>
						        <th>大小(KB)</th>
						       </tr>
						    </thead>
						    <tbody>
						    <c:forEach items="${page.result}" var="pdfTemplate">
						     	<c:choose>
						     	 <c:when test="${pdfTemplate.sealMac == true}">
							      <tr>
							        <th width="1">
										<input name="checkboxt" class="input_checked" type="checkbox" value="${pdfTemplate.id }">
									</th>
							        <td width="40%">${pdfTemplate.name }</td>
							        <td width="30%">${pdfTemplate.generateTimeCn}</td>
							        <td width="29%">${pdfTemplate.fileSize }</td>
							       </tr>
							     </c:when>  
							     <c:when test="${pdfTemplate.sealMac == false}">
							      <tr>
							        <th width="1">
										<input name="checkboxt" class="input_checked" type="checkbox" value="${pdfTemplate.id }">
									</th>
							        <td  width="40%" class="errorMac">${pdfTemplate.name}</td>
							        <td  width="30%" class="errorMac">${pdfTemplate.generateTimeCn}</td>
							        <td  width="29%" class="errorMac">${pdfTemplate.fileSize }</td>
							       </tr>
							     </c:when>  
						       </c:choose>
						    </c:forEach>
						    
						    </tbody>
						    
						</table>
						<div class="text-right" id="pdfTemplatePage"></div>
					</div>
				  </div>
				</div>
			  </div>
			</div>
		</section>
	</div>
</div>	


<script type="text/javascript">

	$(function(){
		 $("#checkboxChange").click(function() {
			 $("[name=checkboxt]:checkbox").prop("checked",this.checked);	 			
		 });
		 
		 //---复选框样式
		 icheck(".js-checkbox-all");
		 
		 laypage({
				cont : 'pdfTemplatePage',
				skip : true,//跳转页面选项
				pages : '${page.totalPage}',
				curr : function() {
					var pageNo = '${page.pageNo}';
					return pageNo ? pageNo : 1;
				}(),
				jump : function(e, first) {
					if (!first) {
						loadUrl("${ctx }/system/PDFTemplateManager.do?pageNo=" + e.curr);
					}
				}
			});
		 
	});
	
	
	function addTemplate(){
		loadUrl("${ctx }/system/pdfTemplateAdd.do");
	}
	
	//删除
	function judgeCheckbox2(title,url) {
		var id = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					id = $(this).val();
				}else{
					id = id + ";" + $(this).val();
				}
				index++;
			}
		});
		if(index == 0){
			layer.alert( "请至少选择一条要操作的记录",{icon:0});
			return;
		} 
		//---开始之前的判断结束--可以单独抽取出来
		layer.confirm("确定要"+title+"?",{btn:["确定","取消"]},function(){
			$.ajax({
				url : url,
				type : "post",
				data : "id=" + id,
				dataType : "json",
				success : function(data) {
					if(data.success){
						layer.alert(data.message,{icon:1});
						loadUrl("${ctx }/system/PDFTemplateManager.do");
					}else{
						layer.alert(data.message,{icon:2});
						loadUrl("${ctx }/system/PDFTemplateManager.do");
					}
				},
				error : function() {
					layer.alert("请求失败",{icon:2});
				}
			});
		});
	} 
	
	
	//下载
	function downloadTemplate(){
		id = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0 ){
					id = $(this).val();
				}
				index++;
			}
		});
		if(index == 0 || index > 1){
			layer.alert( "请选择一条下载对象",{icon:0});
			return;
		} 
		
		var url = "${ctx }/system/toDownloadPdfTemplate.do?id=" + id;
		$.ajax({
			type : "get",
			dataType : "json",
			url : url,
			success : function(data) {
				if (data.message == 'ok') {
					// 进行下载
					window.location.href = "${ctx }/system/downloadPdfTemplate.do?id=" + id ;
				} else {
					layer.alert( data.message, {icon:2});
				}
			},
			error : function() {
				layer.alert( data.message, {icon:2});
			}
		});
	}

	//详情
	function templateDetails(){
		id = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0 ){
					id = $(this).val();
				}
				index++;
			}
		});
		if(index == 0 || index > 1){
			layer.alert( "请选择一条查看对象",{icon:0});
			return;
		} 
		
		var url = "${ctx }/system/toDownloadPdfTemplate.do?id=" + id;
		$.ajax({
			type : "get",
			dataType : "json",
			url : url,
			success : function(data) {
				if (data.message == 'ok') {
					// 显示输入项详情
					loadUrl("${ctx }/system/templateDetails.do?id=" + id);
				} else {
					layer.alert( data.message, {icon:2});
				}
			},
			error : function() {
				layer.alert( data.message, {icon:2});
			}
		});
		
	}
	

</script>