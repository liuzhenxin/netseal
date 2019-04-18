<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
<h2 style="font-size:34px; font-weight:bold;">签章人管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    签章人管理    /    印章申请</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			印章申请</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			  <div class="x_content">
				<br>
				<form id="requestSealForm" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;"  autocomplete="off" action="${ctx }/userManage/requestSeal.do" method="post">
				  <input id="userId" name="userId" type="hidden" value="${user.id }"/>
				  <div class="form-group">
					<label class="control-label col-xs-3">姓名 
					</label>
					<div class="col-xs-6">
					  <input name="userName" type="text" readonly value="${user.name }" class="form-control col-xs-6">
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">印章名称 
					</label>
					<div class="col-xs-6">
					  <input id="name" name="name" type="text" class="form-control col-xs-6">
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">印模 
					</label>
					<div class="col-xs-6">
					  	<select id="template" class="form-control" name="templateId">
							<c:forEach items="${templateList }" var="tList">
								<option value="${tList.id }" >${tList.name } </option>
							</c:forEach>
						</select>
					</div>
				  </div>
				  <div class="form-group" id="photo">
						<label class="control-label col-xs-3">印章图片</label>
						<div class="col-xs-6">
							<input id="photoFile" name="photoFile" type="file" class="form-control input-sm">
						</div>
				  </div>
				  
				  <div class="form-group" id="alpha">
						<label class="control-label col-xs-3">图片透明度</label>
						<div class="col-xs-8" style="margin-top:6px;">
						  <div id="sliderParent" style="padding: 6px;background-color: gray;display: inline-block;">  </div>
						  <p>&nbsp;(上传印章图片时生效，否则以印模透明度为准)</p>
						  <input id="transparency" name="transparency" type="hidden"   class="form-control col-xs-6"> 
						    
					    </div>
				   </div>
				  
				  <div class="form-group">
					<label class="control-label col-xs-3">已注册证书 
					</label>
					<div class="col-xs-6">
					  <select class="form-control " name="certId">
						<c:forEach items="${certList }" var="cert">
						 	<option value="${cert.id}" >${cert.certDn}</option>
						</c:forEach>
					 </select>
					</div>
				  </div>
				  <div class="form-group ">
					<label class="control-label col-xs-3">起始日期 
					</label>
					<div class="col-xs-6">
					  <input type="text" id="notBefor" name="notBeforCn" class="form-control col-xs-6" style="cursor: pointer;" placeholder="请选择" readonly>
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">截止日期 
					</label>
					<div class="col-xs-6">
					  <input id="notAfter" name="notAfterCn"  type="text" class="form-control col-xs-6" style="cursor: pointer;" placeholder="请选择"  readonly>
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">备注 
						</label>
						<div class="col-xs-6">
						  <input id="remark" name="remark"  type="text" class="form-control col-xs-6">
						</div>
					  </div>

					  
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
									  <button id="submitRequestSealButton" type="button" class="btn btn-primary col-md-offset-3">确定</button>
									  <button  onclick="javascript:loadUrl('${ctx }/userManage/userList.do')" class="btn btn-primary col-md-offset-3" type="button" >返回</button>
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
	
	var option = {
	        color: '#337ab7',
	        width: '400px',
	        progress: 0.3,
	        handleSrc: '${ctx }/img/slider_handle.png',
	        isCustomText: false
	    };
	    $('#sliderParent')
	        .jackWeiSlider(option)
	        // .setText('2018-4-5 02:39:00')
	        .setProgress(1.0)
	        .setOnStartDragCallback(function () {
	            //console.log('start');
	        })
	        .setOnDragCallback(function (p) {
	           // console.log(p);
	        })
	        .setOnStopDragCallback(function () {
	           // console.log('stop');
	        });
	
	function  getTemplateType(){
		$.ajax({
			 type: 'post', 
	         url: '${ctx }/userManage/getTemplate.do',
	         data:{"templateId":$("select[name='templateId']").val()},
			 success:function(data){
				 if(data.type == 3){
					$('#photoFile').val("");
					document.getElementById("photo").style.display="none";//隐藏
					document.getElementById("alpha").style.display="none";//隐藏
				 }else{
					document.getElementById("photo").style.display="";
					document.getElementById("alpha").style.display="";
				 }
			}
		 });		
	}
	
	getTemplateType();
	
	//下拉框联动图片事件
	$("select[name='templateId']").change(function(){
		getTemplateType();
	  });
	
	
	
	//--表单验证
	// validate("#requestSealForm");
	$("#requestSealForm").bootstrapValidator({
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
                },
                remark: {
    	            validators: {
    	                stringLength: {
    	                    min: 0,
    	                    max: 66,
    	                    message: '长度0~66'
    	                }
    	            }
    	        }
	            
	         }
		});
	$("#submitRequestSealButton").click(function(){
		var before =  document.getElementById("notBefor").value;
		var after =  document.getElementById("notAfter").value;
		var tem =  document.getElementById("template").value;
		var num = $(".jws-text").html();
        num = num.substr(0,num.length-1);
        $("#transparency").val(num);
        
		var form=$("#requestSealForm");
		form.bootstrapValidator('validate');
		if (form.data('bootstrapValidator').isValid()) {
			if(!tem){
				layer.alert( "请选择印模", {icon:2});
			}else if(!before){
				layer.alert( "请选取起始日期", {icon:2});
			}else if(!after){
				layer.alert( "请选取截止日期", {icon:2});
			}else{
			 form.ajaxSubmit({
				 success:function(data){
				     if(data == "ok"){	
				    	layer.alert("申请印章成功",{icon:1});
			        	var url="${ctx }/userManage/userList.do";
			        	loadUrl(url);
				      }else{
				    	  layer.alert(data,{icon:2});
				      }
				    	
				    },error:function(){
				    	layer.alert("请求失败",{icon:2});
				    }
			 });
			}
		 }
	});
	
	 //时间控件
	   var start = {
	   elem: '#notBefor',
	   format: 'YYYY-MM-DD',
	   choose: function(datas){
	      end.min = datas;
	      end.start = datas;
	   }
	};
	var end = {
		elem: '#notAfter',
		format:"YYYY-MM-DD",
		choose: function(datas){
			start.max = datas;
	   }
	};
	laydate(start);
	laydate(end); 

});

</script>