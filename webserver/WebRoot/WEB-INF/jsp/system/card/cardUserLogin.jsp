<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>


<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">加密卡用户管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;">用户管理    /    加密卡用户管理   /  加密卡用户登录</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			加密卡用户登录</a>
	</li>
</ul>



<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
					
						<form id="cardUserLoginFrom" action="${ctx }/system/card/loginCardUser.do"  method="post" class="form-horizontal form-label-left" style="margin-top:20px;" >						
							<div class="form-group">
								<label class="control-label col-xs-3">PIN码: 
								</label>
								<div class="col-xs-6">
								  <input type="password" class="form-control col-xs-6"  name="pin" value="">
								</div>
								
							</div>
							<div class="form-group">
							<div class="col-md-offset-3 col-xs-6">
								<p><font color="red" class="errormsg" id="msg"></font></p>
							</div>
							</div>
							 
							<div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-4 col-xs-6">
									  <button type="button" id="loginCardUserBtn" class="btn btn-primary col-md-offset-1" data-toggle="modal"  data-target=".bs-example-modal-sm11">登录</button>
									  <button type="button" id="cardUserbBackBtn" class="btn btn-primary col-md-offset-2" >返回</button>
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
	var counum=10;
	
	$("#loginCardUserBtn").click(function(){
		 var form = $("#cardUserLoginFrom");
		  form.bootstrapValidator('validate');
		  if(form.data('bootstrapValidator').isValid()){
				  form.ajaxSubmit({
					  success:function(data){
							if(data.success) {
								layer.alert(data.message, {icon:1});
								loadUrl("${ctx }/system/card/cardUserManage.do");
							} else {
								document.getElementById("msg").innerHTML =data.message ;
								layer.alert(data.message, {icon:2});
							}						
							},  
							error:function(){
								layer.alert("请求失败",{icon:2});
							}
				  });
		  }else{
			  form.bootstrapValidator('validate');
		  }
	  });
	
	  //表单验证
	  $("#cardUserLoginFrom").bootstrapValidator({
		  fields: {
			  pin:{
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
			  }
		  }
	  });
	  
	  
	  $("#cardUserbBackBtn").click(function(){
		  loadUrl("${ctx }/system/card/cardUserManage.do");
	  })
		
	
	
	
 	
});

</script>