<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">PDF模板管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;">印模管理    /    PDF模板管理      / 增加PDF模板</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			增加PDF模板</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
    <div class="tab-pane fade in active" id="home">
<section class="content">						
	<div class="row" id="templateAddShow">
	  <div class="col-md-12 col-sm-12 col-xs-12">
		<div class="x_panel">
			<div class="main">
			  <form id="addPdfTemplateForm"  class="form-horizontal bv-form" autocomplete="off" action="${ctx }/system/addPdfTemplate.do"
			  method="post" enctype="multipart/form-data">
				<button type="submit" class="bv-hidden-submit" style="display: none; width: 0px; height: 0px;"></button>
		   <div class="sys-tab">

		  <div class="form-group">
			<label class="control-label col-xs-3">模板名称 
			</label>
			<div class="col-xs-6">
			  <input id="name" name="name" type="text"  class="form-control col-xs-6">
			</div>
		  </div>
		  
		  <div class="form-group">
			<label class="control-label col-xs-3">模板文件</label>
			<div class="col-xs-6">
			  <input id="pdfTemplate" name="pdfTemplate" type="file" class="form-control input-sm"></div>
		  </div>
		  
			<div class="form-actions">
				<div class="row">
					<div class="col-md-offset-3 col-xs-6">
						  <button id="submitTemplateButton"  type="button" class="btn btn-primary col-md-offset-3" >提交</button>
						  <button id="return"  type="button" class="btn btn-primary col-md-offset-3" >返回</button>
					</div>
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
	
	$("#addPdfTemplateForm").bootstrapValidator({
		message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
		fields: {
			name: {
                validators: {
                    notEmpty: {
                        message: '不能为空'
                    },
                    stringLength: {
                        min: 2,
                        max: 16,
                        message: '长度为2~16'
                    },
                    regexp: {
                        regexp: /^[\u4e00-\u9fa5a-zA-Z0-9]+$/,
                        message: '请输入正确格式(中文,数字或字母)'
                    },
                   
                }
            }
	    }
	});
	
	
	$("#submitTemplateButton").click(function(){
		var form = $("#addPdfTemplateForm");
		form.bootstrapValidator('validate');
		if(!$('#pdfTemplate').val()){
			layer.alert( "请选择模板文件", {icon:2});
			return false;
		}
		if(form.data('bootstrapValidator').isValid()){
			form.ajaxSubmit({
				success:function(data){
					if(data == 'ok'){
						layer.alert('增加成功', {icon:1});
						loadUrl("${ctx }/system/PDFTemplateManager.do");
					} else {
						layer.alert(data, {icon:2});
					}
					
				},
				error:function(){
					layer.alert("请求失败",{icon:2});
				}
					
			})
		}else{
			form.bootstrapValidator('validate');
		}
		
	});
	
	
	
	$("#return").click(function() {
		loadUrl("${ctx }/system/PDFTemplateManager.do");
	});
	
	
	
});


</script>




