<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>


<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">加密卡用户管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;">用户管理    /    加密卡用户管理   /  加密卡用户修改PIN码</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			加密卡用户修改PIN码</a>
	</li>
</ul>



<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
					
						<form id="cardUserEditFrom" action="${ctx }/system/card/editCardUser.do"  method="post" class="form-horizontal form-label-left" style="margin-top:20px;" >						
							<input  class="form-control col-xs-6"  name="userId"  type="hidden" value="${userId }">
							<div class="form-group">
								<label class="control-label col-xs-3">原始PIN码:
								</label>
								<div class="col-xs-6">
								  <input id="oldPin" name="oldPin" type="password" class="form-control col-xs-6" >
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">新PIN码:
								</label>
								<div class="col-xs-6">
								  <input id="newPin" name="newPin" type="password" class="form-control col-xs-6" >
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">确认PIN码:
								</label>
								<div class="col-xs-6">
								  <input id="checkNewpin" name="checkNewPin" type="password" class="form-control col-xs-6" >
								</div>
							</div>
							<div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-4 col-xs-6">
									  <button type="button" id="editCardUserBtn" class="btn btn-primary col-md-offset-1" data-toggle="modal"  data-target=".bs-example-modal-sm11">修改</button>
									  <button type="button" id="cardUserBackBtn" class="btn btn-primary col-md-offset-2" >返回</button>
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
	
	$("#editCardUserBtn").click(function(){
		 var newPin = $("#newPin").val();
		 var checkNewpin = $("#checkNewpin").val();
		 if(newPin==checkNewpin){
			 var form = $("#cardUserEditFrom");
			  form.bootstrapValidator('validate');
			  if(form.data('bootstrapValidator').isValid()){
				  form.ajaxSubmit({
					  success:function(data){
							if(data.success) {
								layer.alert(data.message, {icon:1});
								loadUrl("${ctx }/system/card/cardUserManage.do");
							} else {
								layer.alert(data.message, {icon:2});
							}						
							},
							error:function(){
								layer.alert("请求失败",{icon:2});
							}
				  })
			  }else{
				  form.bootstrapValidator('validate');
			  }
		 }else{
			 layer.alert("新PIN码与确认PIN码不一致",{icon:2});
		 } 
	  });
	
	  //表单验证
	  $("#cardUserEditFrom").bootstrapValidator({
		  fields: {
			  oldPin:{
				  validators:{
					  notEmpty:{
						  message:'PIN码 不能为空'
					  },
	                  stringLength : {
							min : 8,
							max : 8,
							message : 'PIN码长度为8位'
						}
				  }
			  },
			  newPin: {
                  validators: {
                      notEmpty: {
                          message: '新PIN码不能为空'
                      },
                      stringLength: {
                          min: 8,
                          max: 8,
                          message: '长度为8'
                      }
                }
              },
              checkNewPin: {
                  validators: {
                      notEmpty: {
                          message: '确认PIN码不能为空'
                      },
                      stringLength: {
                          min: 8,
                          max: 8,
                          message: '长度为8'
                      },
                      identical: {
                      	field: 'newPin',
                      	message: '两次输入PIN码不一致'
                      }
                  }
              }
	  
		  }
	  });
		
	  $("#cardUserBackBtn").click(function(){
		  loadUrl("${ctx }/system/card/cardUserManage.do");
	  });
	
	
 	
});

</script>