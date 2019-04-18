<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
<h2 style="font-size:34px; font-weight:bold;">签章人管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    签章人管理    /    证书申请下载</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			证书申请下载</a>
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
				<form id="requestCertForm" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;"  autocomplete="off" action="${ctx }/userCertReuqest/requestCert.do" method="post">
				  <input id="userId" name="userId" type="hidden" value="${user.id }"/>
				  <div class="form-group">
					<label class="control-label col-xs-3">姓名</label>
					<div class="col-xs-6">
					  <input name="userName" type="text" value="${user.name }" class="form-control col-xs-6" readonly>
					</div>
				  </div>
				 <div class="form-group">
					<label class="control-label col-xs-3">证书有效期</label>
					<div class="col-xs-6">
					  <input id="validityLen" name="validityLen" class="form-control " type="text" value=""/>(天)
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">CA</label>
					<div class="col-xs-6">
					  <select class="form-control " id="certType" name="certType">
						<option value="rsa_ca">RSA</option>
						<option value="sm2_ca">SM2</option>
					 </select>
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">证书模板</label>
					<div class="col-xs-6">
					  <select class="form-control " id="certTemplate" name="certTemplate">
						<c:forEach items="${templateList }" var="template">
							<option value="${template}">${template}</option>
						</c:forEach>
					 </select>
					</div>
				  </div>
				<div class="form-group">
					<label class="control-label col-xs-3">证书DN</label>
					<div class="col-xs-6">
					  <input id="certDn" name="certDn" class="form-control " type="text" value=""/>
					</div>
				  </div>
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
									  <button id="submitRequestCertButton" type="button" class="btn btn-primary col-md-offset-3">申请两码</button>
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
	//--表单验证
	$("#requestCertForm").bootstrapValidator({
			fields: {
				validityLen: {
                    validators: {
                        notEmpty: {
                            message: '证书有效期不能为空'
                        },
                        stringLength: {
                            min: 1,
                            max: 5,
                            message: '证书有效期长度为1~5'
                        },regexp: {
                        	regexp: "^[1-9][0-9]*$",
                            message: '证书有效期必须为有效数字'
                        }
                    }
                },
                certDn:{
					validators:{
						notEmpty: {
							message: '证书DN不能为空'
						},
						stringLength: {
	                        min: 2,
	                        max: 300,
	                        message: '证书DN长度为2~300'
	                    }
					}
	           }
	         }
		});
	
	$("#certType").change(function(){
		var certType=$(this).val();
		$.ajax({
			url : "${ctx }/userCertReuqest/getTemplateList.do",
			type : "get",
			data : "certType=" + certType,
			dataType : "json",
			success : function(data) {
				if (data.success) {				
					$("#certTemplate").empty();
					var templateList=data.templateList;
					for (var i = 0; i < templateList.length; i++){
						$("#certTemplate").append("<option value='"+templateList[i]+"'>"+templateList[i]+"</option>");
					}
					
				}else{
					layer.alert(data.message,{icon:2});
				}
			},
			error : function() {
				layer.alert("请求失败",{icon:2});
			}
		});
	});
	
	$("#submitRequestCertButton").click(function(){
		var form=$("#requestCertForm");
		form.bootstrapValidator('validate');
		if (form.data('bootstrapValidator').isValid()) {
			
		 form.ajaxSubmit({
			 success:function(data){
			     if(data.success){
			    	layer.alert(data.message,{icon:1});
		        	var url="${ctx }/userCertReuqest/toDownloadCert.do?requestCertId="+data.requestCertId;
		        	loadUrl(url);
			      }else{
			    	  layer.alert(data.message,{icon:2});
			      }
			    	
			    },error:function(){
			    	layer.alert("请求失败",{icon:2});
			    }
		 });
		}
		 
	});
	

});

</script>