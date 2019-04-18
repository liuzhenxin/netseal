<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>


		<section class="content">
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
						<form id="sm2CaConfigForm" action="${ctx }/system/rads/sm2CaConfigSave.do" method="post" enctype="multipart/form-data" class="form-horizontal form-label-left" style="margin-top:20px;" >						
							<input id="certTypeGM2" name="certType" type="hidden">
							<div class="form-group">
								<label class="control-label col-xs-3">CA服务器IP:</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6" name="transIP" value="${netCertCa.transIP }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">CA服务器端口:</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6" name="transPort" value="${netCertCa.transPort }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">签名算法:</label>
								<div class="col-xs-6">
								  <input type="text" class="form-control col-xs-6" name="signAlgName" value="${netCertCa.signAlgName }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">签名证书:</label>
								<div class="col-xs-6">
								  <input type="file" class="form-control col-xs-6" name="signCertFile">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">签名密钥:</label>
								<div class="col-xs-3">
								  <input type="file" class="form-control col-xs-3" name="keyIdxFile">(私钥文件)
								</div>
								<div class="col-xs-3">
								  <input type="file" class="form-control col-xs-3" name="keyIdx2File">(公钥文件)
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">密钥口令:</label>
								<div class="col-xs-6">
								  <input type="password" class="form-control col-xs-6" name="pwd" value="${netCertCa.pwd }">
								</div>
							</div>
							
							<div class="form-group">
								<label class="control-label col-xs-3">通道加密方式:</label>
								<div class="col-xs-6">
								<select class="form-control " id="chanelEncryptName2" name="chanelEncryptName">
									<option value="plain">plain</option>
									<option value="ssl">ssl</option>
								 </select>
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">ssl信任库:</label>
								<div class="col-xs-6">
								  <input type="file" class="form-control col-xs-6" id="trustStoreFile2" name="trustStoreFile">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">ssl信任库密码:</label>
								<div class="col-xs-6">
								  <input type="password" class="form-control col-xs-6" id="trustPassword2" name="trustPassword" value="${netCertCa.trustPassword }">
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">由CA生成UUID:</label>
								<div class="col-xs-6">
								  <select class="form-control " id="isGenUuid" name="isGenUuid">
										<option value="1">是</option>
										<option value="0">否</option>
								  </select>
								</div>
							</div>
							
							<div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-3 col-xs-6">
									  <button type="button" id="sm2CaConfigSaveBtn" class="btn btn-primary col-md-offset-2">保存</button>
									  <button type="button" id="sm2CaConfigTestBtn" class="btn btn-primary col-md-offset-2">测试</button>
									
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
function checkForm(){
	if($("#chanelEncryptName2").val()=="ssl"){
	  if($("#trustStoreFile2").val()==""){
		  layer.alert("ssl信任库不能为空",{icon:2});
		  return false;
	 		}
	  if($("#trustPassword2").val()==""){
		  layer.alert("ssl信任库密码不能为空",{icon:2});
		  return false;
	 	}
	 }
	return true;
}
$(function(){
	var chanelEncryptName2="${netCertCa.chanelEncryptName }";
	if(chanelEncryptName2!="")
		$("#chanelEncryptName2").val(chanelEncryptName2);
		
	  $("#sm2CaConfigSaveBtn").click(function(){
		  var form = $("#sm2CaConfigForm");
		  form.bootstrapValidator('validate');
		  if(form.data('bootstrapValidator').isValid()){
			  if(!checkForm()){
				return;
			  }
			  form.attr("action", "${ctx }/system/rads/sm2CaConfigSave.do");
			  form.ajaxSubmit({
				  success:function(data){
						if(data == "ok") {
							layer.alert("保存成功,实时生效", {icon:1}); 
						} else {
							layer.alert(data, {icon:2});
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
	  $("#sm2CaConfigTestBtn").click(function(){
		  var form = $("#sm2CaConfigForm");
		  form.bootstrapValidator('validate');
		  if(form.data('bootstrapValidator').isValid()){
			  if(!checkForm()){
				return;
			  }

			  form.attr("action", "${ctx }/system/rads/sm2CaConfigTest.do");
			  $("#certTypeGM2").val("testSM2Ca");
			  form.ajaxSubmit({
				  success:function(data){
					if(data == "ok") {
						layer.alert("测试成功", {icon:1});
					} else {
						layer.alert(data, {icon:2});
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
	  $("#sm2CaConfigForm").bootstrapValidator({
		  fields: {
			  transIP:{
				  validators:{
					  notEmpty:{
						  message:'CA服务器IP不能为空'
					  },
						stringLength : {
							min : 1,
							max : 20,
							message : 'CA服务器IP长度为1~20'
						}
				  }
			  },
			  transPort:{
				  validators:{
					  notEmpty:{
						  message:'CA服务器端口不能为空'
					  },
					stringLength : {
						min : 1,
						max : 10,
						message : 'CA服务器端口为1~10'
					},regexp: {
	                	regexp: "^[1-9][0-9]*$",
	                    message: 'CA服务器端口必须为有效数字'
	                }
				  }
			  },
			  signAlgName:{
				  validators:{					  
					 notEmpty:{
						 message:'签名算法不能为空'
					},
				  	stringLength : {
						min : 1,
						max : 50,
						message : '签名算法长度为1~50'
					}
				  }
			  },
			  signCertFile:{
				  validators:{
					  notEmpty:{
						  message:'签名证书文件不能为空'
					  }
				  }
			  },
			  keyIdxFile:{
				  validators:{
					  notEmpty:{
						  message:'签名密钥私钥文件不能为空'
					  }
				  }
			  },
			  keyIdx2File:{
				  validators:{
					  notEmpty:{
						  message:'签名密钥公钥文件不能为空'
					  }
				  }
			  },
			  pwd:{
				  validators:{
					  notEmpty:{
						  message:'密钥口令不能为空'
					  }
				  }
			  }
			  
		  }
	  });
	  
  });
 
</script>