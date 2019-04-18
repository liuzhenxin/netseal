<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">批量制作图章</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">批量制作图章 </i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			批量制作图章</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
			    <div class="x_panel"> 
				  <div class="x_content">
				    <form id="genStampForm" action="${ctx }/stamp/genStamp.do" method="post" class="form-horizontal form-label-left" novalidate style="margin-top:20px;" enctype="multipart/form-data">					
					   <div class="form-group">
						<label for="stampStyle" class="control-label col-xs-3">图章样式</label>
						<div class="col-xs-6">
						  <select id="stampStyle" class="form-control input-sm viewStampChange stampStyleChange" name="stampStyle">
							<c:forEach items="${stampStyleList }" var="stampStyle" varStatus="style">
								<option value="${style.count }">${stampStyle }</option>
							</c:forEach>
						  </select>
						</div>
						</div>
					  <div class="form-group">
						<label  for="width" class="control-label col-xs-3">图章长度</label>
						<div class="col-xs-6">
						  <input id="width" name="width" type="text" class="form-control viewStampChange" value="150">
						</div>
					  </div>
					  <div class="form-group">
						<label  for="height" class="control-label col-xs-3">图章高度 </label>
						<div class="col-xs-6">
						  <input id="height" name="height" type="text" class="form-control viewStampChange" value="100">
						</div>
					  </div>
					   <div class="form-group">
						<label for="fontType" class="control-label col-xs-3">图章字体</label>
						<div class="col-xs-6">
						  <select class="form-control input-sm viewStampChange" name="fontType">
							<option value="宋体">宋体</option>
							<option value="楷体">楷体</option>
							<option value="隶书">隶书</option>
							<option value="新宋体">新宋体</option>
							<option value="幼圆">幼圆</option>
						  </select>
						</div>
					  </div>
					  <div class="form-group">
						<label  for="name" class="control-label col-xs-3">图章名称(预览)</label>
						<div class="col-xs-6">
						  <input id="name" name="name" type="text" class="form-control viewStampChange" value="测试专用章">
						</div>
					  </div>
					  <div class="form-group">
						<label  for="nameFontSize" class="control-label col-xs-3">图章名称字体大小</label>
						<div class="col-xs-6">
						  <input id="nameFontSize" name="nameFontSize" type="text" class="form-control viewStampChange" value="12">
						</div>
					  </div>
					  <div class="form-group">
						<label  for="company" class="control-label col-xs-3">正文名称(预览)</label>
						<div class="col-xs-6">
						  <input id="company" name="company" type="text" class="form-control viewStampChange" value="电子签章系统测试">
						</div>
					  </div>
					  <div class="form-group">
						<label  for="companyFontSize" class="control-label col-xs-3">正文名称字体大小</label>
						<div class="col-xs-6">
						  <input id="companyFontSize" name="companyFontSize" type="text" class="form-control viewStampChange" value="20">
						</div>
					  </div>
					  <div class="form-group" id="stampFile">
						<label class="control-label col-xs-3">图章文字文件</label>
						<div class="col-xs-6">
						  <input id="textFile" name="textFile" type="file" class="form-control input-sm">
						  <span id="textFiletip"></span>
						  </div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">实时预览</label>
						<div class="col-xs-6">
						 <img id="viewStampId" alt="预览请求失败" src="">
						</div>
					  </div>
					 
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
								  <button id="genStampButton"  type="button" class="btn btn-primary col-md-offset-2" style="">制作图章</button>
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
function viewStamp(param){
	param = decodeURIComponent(param,true);
	param=encodeURI(encodeURI(param));
	$("#viewStampId").attr("src", "${ctx }/stamp/viewStamp.do?"+param+"&rd="+Math.random());
}
function checkForm(){
	var stampStyle = $("#stampStyle").val();
	if(stampStyle==1 || stampStyle==3)
		$("#height").attr("disabled",true);
	else
		$("#height").attr("disabled",false);
	
	if(stampStyle==3 || stampStyle==4){
		$("#company").attr("disabled",true);
		$("#companyFontSize").attr("disabled",true);
		$("#textFiletip").html("(txt文件格式：每个图章名称为一行,每次最多制作500个)");
	}else{
		$("#company").attr("disabled",false);
		$("#companyFontSize").attr("disabled",false);
		$("#textFiletip").html("(txt文件格式：图章名称和正文用英文逗号分隔,每一行为一个图章,每次最多制作500个)");
	}
	
}
function changeValue(){
	var stampStyle = $("#stampStyle").val();
	if(stampStyle==1){
		$("#width").val("150");
		$("#companyFontSize").val("20");
		$("#height").val("100");
		$("#nameFontSize").val("12");
	}
	if(stampStyle==2){
		$("#width").val("150");
		$("#companyFontSize").val("16");
		$("#height").val("100");
		$("#nameFontSize").val("12");
	}
	if(stampStyle==3){
		$("#width").val("150");
		$("#nameFontSize").val("45");
	}
	if(stampStyle==4){
		$("#width").val("150");
		$("#height").val("50");
		$("#nameFontSize").val("25");
	}
}
$(function() {
	checkForm();
	var param=$("#genStampForm").serialize();
	viewStamp(param);
	
	$(".stampStyleChange").change(function() {
		changeValue();
	});
	$(".viewStampChange").change(function() {
		checkForm();
		var form = $("#genStampForm");
		form.bootstrapValidator('validate');
		if (form.data('bootstrapValidator').isValid()) {
			var param=form.serialize();
			viewStamp(param);
		} else {
			form.bootstrapValidator('validate');
		}
	});
    $("#genStampForm").bootstrapValidator({fields:{
    	width: {
            validators: {
                notEmpty: {
                    message: '图章长度不能为空'
                },stringLength: {
                    min: 2,
                    max: 4,
                    message: '图章长度长度为2~4'
                },regexp: {
                	regexp: "^[1-9][0-9]*$",
                    message: '图章长度必须为有效数字'
                }
            }
        },
        height: {
            validators: {
                notEmpty: {
                    message: '图章高度不能为空'
                },stringLength: {
                    min: 2,
                    max: 4,
                    message: '图章高度长度为2~4'
                },regexp: {
                	regexp: "^[1-9][0-9]*$",
                    message: '图章高度必须为有效数字'
                }
            }
        },
        company: {
            validators: {
                stringLength: {
                    min: 0,
                    max: 50,
                    message: '正文名称(预览)长度为0~50'
                }
            }
        },
        companyFontSize: {
            validators: {
                notEmpty: {
                    message: '正文名称字体大小不能为空'
                },stringLength: {
                    min: 1,
                    max: 3,
                    message: '正文名称字体大小长度为1~3'
                },regexp: {
                	regexp: "^[1-9][0-9]*$",
                    message: '正文名称字体大小必须为有效数字'
                }
            }
        },
        name: {
            validators: {
                stringLength: {
                    min: 0,
                    max: 50,
                    message: '图章名称(预览)长度为0~50'
                }
            }
        },
        nameFontSize: {
            validators: {
                notEmpty: {
                    message: '图章名称字体大小不能为空'
                },stringLength: {
                    min: 1,
                    max: 3,
                    message: '图章名称字体大小长度为1~3'
                },regexp: {
                	regexp: "^[1-9][0-9]*$",
                    message: '图章名称字体大小必须为有效数字'
                }
            }
        }
       }
    });
	
	$("#genStampButton").click(function() {
		var form = $("#genStampForm");
		form.bootstrapValidator('validate');
		if (form.data('bootstrapValidator').isValid()) {
			if($('#textFile').val()==""){
				layer.alert("请选择图章文字文件", {icon:2});
				return;
			}
			layer.alert("正在制作图章，请等待...", {icon:0});
			form.ajaxSubmit({
				success : function(data) {
					if (data == "ok") {
						layer.alert("制作图章成功", {icon:1});
						var url = "${ctx }/stamp/toDownLoadStamp.do";
						loadUrl(url);
					}else{
						layer.alert("操作失败" + data, {icon:2});
					}
				},
				error : function() {
					layer.alert("请求失败", {icon:2});
				}
			});
			
		} else {
			form.bootstrapValidator('validate');
		}
	});
});

	
</script>