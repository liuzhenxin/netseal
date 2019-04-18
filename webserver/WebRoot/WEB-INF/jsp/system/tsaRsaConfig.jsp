<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>


		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
					
						<form id="tsaConfigForm" action="${ctx }/system/tsaRsaConfigSave.do"  method="post" class="form-horizontal form-label-left" style="margin-top:20px;" >						
							
							<div class="form-group">
								<label class="control-label col-xs-3">服务器地址:</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6"  name="tsaRsaUrl" value="${config.tsaRsaUrl }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">用户名:</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6"  name="tsaRsaUsername" value="${config.tsaRsaUsername }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">密码:</label>
								<div class="col-xs-6">
								  <input type="password" class="form-control col-xs-6"  name="tsaRsaUserpwd" value="${config.tsaRsaUserpwd }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">策略:</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6"  name="tsaRsaPolicy" value="${config.tsaRsaPolicy }">
								</div>
							</div>
							<div class="form-group" >
								<label class="control-label col-xs-3">使用时间戳:</label>
								<div class="col-xs-6" style="margin-top:8px;">
								  <ul class="ui-choose" >
				                     <li ${config.tsaRsaUsetsa == true ? 'class="selected"':'' }>是</li>
				                     <li ${config.tsaRsaUsetsa == false ? 'class="selected"':'' }>否</li>
				                 	<input type="hidden" name="tsaRsaUsetsa"  value="${config.tsaRsaUsetsa }" >
					              </ul>
								  
								</div>
							</div>
							<div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-3 col-xs-6">
									  <button type="button" id="saveConfigBtns" class="btn btn-primary col-md-offset-2">保存</button>
									  <button type="button" id="testConfigBtn" class="btn btn-primary col-md-offset-2">测试</button>
									
									   </div>
									</div>
								</div>
							</form>		
						</div>
					</div>
				</div>	
			</div>
		</section>
		
		



<script type="text/javascript">
//将所有.ui-choose实例化
$('.ui-choose').ui_choose();

$('.ui-choose').each(function(){
	var uc_01 = $(this).data('ui-choose'); // 取回已实例化的对象
	var uc  = $(this);
	uc_01.click = function(index, item) {
	    if(index==0){
	    	uc.find("input").val(true);
	    }else{
	    	uc.find("input").val(false);
	    }
	  
	}
	
});
		

  
  $(function(){
	  
	  $("#saveConfigBtns").click(function(){
		  var form = $("#tsaConfigForm");
		  form.bootstrapValidator('validate');
		  if(form.data('bootstrapValidator').isValid()){
			  form.attr("action", "${ctx }/system/tsaRsaConfigSave.do");
			  form.ajaxSubmit({
				  success:function(data){
						if(data.success) {
							layer.alert(data.message, {icon:1});
						} else {
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
	  $("#testConfigBtn").click(function(){
		  var form = $("#tsaConfigForm");
		  form.bootstrapValidator('validate');
		  if(form.data('bootstrapValidator').isValid()){
			  form.attr("action", "${ctx }/system/tsaRsaConfigTest.do");
			  form.ajaxSubmit({
				  success:function(data){
						if(data.success) {
							layer.alert(data.message, {icon:1});
						} else {
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
	  $("#tsaConfigForm").bootstrapValidator({
		  fields: {		 
			  tsaRsaUrl:{
				  validators:{
					  notEmpty:{
						  message:'服务器地址不能为空'
					  },
						stringLength : {
							min : 1,
							max : 150,
							message : '服务器地址长度为1~150'
						}
				  }
			  },
			  tsaRsaUsername:{
				  validators:{
					stringLength : {
						min : 1,
						max : 100,
						message : '用户名长度为1~100'
					}
				  }
			  },
			  tsaRsaUserpwd:{
				  validators:{
				  	stringLength : {
						min : 1,
						max : 100,
						message : '密码长度为1~100'
					}
				  }
			  }/* ,
			  tsaPolicy:{
				  validators:{
					  notEmpty:{
						  message:'策略不能为空'
					  },
						stringLength : {
							min : 1,
							max : 150,
							message : '策略长度为1~150'
						}
				  }
			  } */
		  }
	  });
	  
  });
 
</script>