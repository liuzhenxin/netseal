<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>


		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
					
						<form id="ntpConfigForm" action="${ctx }/system/ntpConfigSave.do"  method="post" class="form-horizontal form-label-left" style="margin-top:20px;" >						
							<div class="form-group">
								<label class="control-label col-xs-3">当前状态: 
								</label>
								<div class="col-xs-6">
								  <c:if test="${ntpIsRunning==true}"><font color="green"><b>NTP定时同步运行中...</b></font></c:if>
								  <c:if test="${ntpIsRunning==false}"><font color="red"><b>NTP定时同步没有启动</b></font></c:if>
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">NTP服务器地址: 
								</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6"  name="ntpIp" value="${config.ntpIp }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">同步时间间隔:
								</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6" name="ntpInterval" value="${config.ntpInterval }"> (单位 秒)
								</div>
							</div>
							<div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-3 col-xs-6">
									  <button type="button" id="saveConfigBtn" class="btn btn-primary col-md-offset-2" data-toggle="modal"  data-target=".bs-example-modal-sm11">立即同步</button>
									  <c:if test="${ntpIsRunning==false}"><input class="btn btn-primary col-md-offset-2" type="button" onclick="ntpConfigOper(1)" value="定时同步启动"></c:if>
									  <c:if test="${ntpIsRunning==true}"><input class="btn btn-primary col-md-offset-2" type="button" onclick="ntpConfigOper(0)" value="定时同步停止"></c:if>
									  <input id="operType" name="operType" type="hidden"  value="">
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

  
  $(function(){
	  $("#saveConfigBtn").click(function(){
		  var form = $("#ntpConfigForm");
		  form.bootstrapValidator('validate');
		
		  if($("input[name='ntpInterval']").val()==""){
			 $("input[name='ntpInterval']").val(0);
		  }
		  if(form.data('bootstrapValidator').isValid()){
			  form.ajaxSubmit({
				  success:function(data){
						if(data.success) {
							layer.alert(data.message, {icon:1});
							$('#tab1').load("${ctx }/system/ntpConfig.do");
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
	  });
  
	  
	  //表单验证
	  $("#ntpConfigForm").bootstrapValidator({
		  fields: {
			  ntpIp:{
				  validators:{
					  notEmpty:{
						  message:'时间同步服务器IP不能为空'
					  },
					  stringLength : {
							min : 1,
							max : 150,
	                        message: '时间同步服务器地址长度为1~150'
					  }
				  }
			  },
			  ntpInterval:{
				  validators:{
					  regexp: {
	                      regexp: /^[0-9]*$/   ,
	                      message: '间隔时间只能由数字组成'
	                  },
	                  stringLength : {
							min : 1,
							max : 8,
							message : '间隔时间长度为1~8'
						}
				  }
			  }
		  }
	  });
	  
  });
  
  
  
  
  function  ntpConfigOper(operType){
	  var ntpInterval = $("input[name='ntpInterval']").val();
	  $("#operType").val(operType);
	  var form = $("#ntpConfigForm");
	  
	  if(operType==1 && (ntpInterval =="" ||ntpInterval<3600)){
			  layer.alert("间隔时间不能为空且不小于3600",{icon:2});
			  return;
		 
	  }
	  
	  if(operType==0 && ntpInterval ==""){
			 $("input[name='ntpInterval']").val(0);
	   }
		  form.ajaxSubmit({
			   type:'post',
			   url:"${ctx}/system/ntpConfigOper.do",
			   success:function(data){
					if(data.success) {
						layer.alert(data.message, {icon:1});
						$('#tab1').load("${ctx }/system/ntpConfig.do");
					} else {
						layer.alert(data.message, {icon:2});
					}						
					},
					error:function(){
						layer.alert("请求失败",{icon:2});
					}
		  });
	 
  }

  
 
</script>