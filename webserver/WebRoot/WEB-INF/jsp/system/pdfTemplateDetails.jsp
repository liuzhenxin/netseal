<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">PDF模板管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;">印模管理    /    PDF模板管理      / 文本域名称</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			文本域名称</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
    <div class="tab-pane fade in active" id="home">
<section class="content">						
	<div class="row" id="templateAddShow">
	  <div class="col-md-12 col-sm-12 col-xs-12">
		<div class="x_panel">
			<div class="main">
			 <form id="configForm"  method="post" class="form-horizontal form-label-left" style="margin-top:20px;" >
		  
		  	<div class="form-group">
				<label class="control-label col-xs-3">模板名称 
				</label>
				<div class="col-xs-6">
				  <input class="form-control col-xs-3" type="text" value="${name }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
				</div>
			</div>
			
			<div class="form-group">
				<label class="control-label col-xs-3">文本域名称
				</label>
				<div class="col-xs-6"  >
				   <input class="form-control col-xs-5" id="hiddenList" type="hidden" value="${FieldName }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
				  
				  <table border="1" id= "details" width="100%"  style="border-right-style:none; border-left-style:none; border-top-style:none;border-bottom: 1px solid #444; " >
				  </table>
				</div>
			</div>
		  
			<div class="form-actions">
				<div class="row">
					<div class="col-md-offset-3 col-xs-6">
						 <!--  <button id="submitTemplateButton"  type="button" class="btn btn-primary col-md-offset-3" >提交</button> -->
						  <button id="return"  type="button" class="btn btn-primary col-md-offset-5" >返回</button>
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

$(function(){
	
	var detailsList =$("#hiddenList").val().split(",");
	var details = "";
	var htm = "";
	for(var i=1;i<=detailsList.length;i++){
		if(!detailsList[i-1]){
			detailsList[i-1] = "此模板没有输入项";
		}
		details = details + '<td  width="20%" style="white-space:nowrap; word-break: keep-all; border-right-style:none; border-left-style:none; border-top-style:none;border-bottom: 1px solid #444; ">' +detailsList[i-1]+'</td>';
		if(i%5==0 ||i==detailsList.length){
			htm =  "<tr>" + details + "</tr>"
			$('#details').append(htm);
			details = "";
		}
	}
	
	
	
	$("#return").click(function() {
		loadUrl("${ctx }/system/PDFTemplateManager.do");
	});
	
	
	
});


</script>




