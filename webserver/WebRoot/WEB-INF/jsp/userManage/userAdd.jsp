<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">签章人管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    签章人管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			签章人管理</a>
	</li>
</ul>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			
				<form id="addUserForm" action="${ctx }/userManage/addUser.do" method="post" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px; " >
				  
				  <input id="X509Cert" name="X509Cert" type="hidden">
				  <input id="certSn" name="certSn" type="hidden">
				  
				  <input id="X509Certen" name="X509Certen" type="hidden">
				  <input id="certenSn" name="certenSn" type="hidden">
				  
				  <div class="form-group">
					<label  for="name" class="control-label col-xs-3">姓名 </label> 
					<div class="col-xs-6">
					  <input id="name" name="name" type="text" class="form-control col-xs-6">
					</div>
				  </div>
				  
				  <div class="form-group">
					<label  for="companyName" class="control-label col-xs-3">用户单位</label>
					<div class="col-xs-6">
					  <div id="gender" class="btn-group" data-toggle="buttons">
						
					  <div class="input-group">
						<input  name="companyHidden" type="hidden" />
		 				<input  name="companyHiddenID" type="hidden" /> 
						<input id="companyName" name="companyName" readonly class="form-control" type="text" /> 
						<input id="companyId" name="companyId" class="form-control input-sm" type="hidden" /> 
						<span class="input-group-btn">
							<button type="button" class="btn btn-primary col-md-offset-1"  id="selectCom" data-toggle="modal" data-target=".bs-example-modal-lg2">选择</button>
						</span>
					  </div>
					  
					  </div>
					</div>
				  </div>
				  
				  <div class="form-group">
					<label for="certDn" class="control-label col-xs-3">证书主题 
					</label>
					<div class="col-xs-6">
					  <select id="certDn" name="certDn" class="form-control col-xs-6"></select>
					</div>
				  </div>
				  <div class="form-group">
					<label  for="phone" class="control-label col-xs-3">手机 
					</label>
					<div class="col-xs-6">
					  <input id="phone" name="phone" type="text" class="form-control col-xs-6">
					</div>
				  </div>
				  <div class="form-group">
					<label for="email"  class="control-label col-xs-3">邮箱 
					</label>
					<div class="col-xs-6">
					  <input id="email" name="email" type="text" class="form-control col-xs-6">
					</div>
				  </div>

				  <div class="ln_solid"></div>
					<div class="form-actions">
						<div class="row">
							<div class="col-xs-6 col-md-offset-3">
								  <button  id="addUserReadCert" type="button" class="btn btn-primary col-md-offset-2">读取证书</button>
								  <button  id="submitUserAddButton" type="button" class="btn btn-primary col-md-offset-2">提交</button>
								  <button  onclick="javascript:loadUrl('${ctx }/userManage/userList.do')" type="button" class="btn btn-primary col-md-offset-2"  id="return">返回</button>
							</div>
						</div>
					</div>

				</form>
			</div>
		  </div>
		</div>
	</section>
 </div>
</div>
	<div  id="addUserCom" class="modal fade bs-example-modal-lg2" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog modal-lg"  style="width:500px; height:300px; position:absolute; left:50%; top:50%; margin-left:-250px; margin-top:-150px;">
		  <form id="editPrintNumForm" class="form-horizontal" action="${ctx }/printer/EditPrintNum.do" method="post" >
			  <div class="modal-content">
				<div class="modal-header">
				  <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span>
				  </button>
				  <h4 class="modal-title" id="myModalLabel">选择单位</h4>
				</div>
				<div class="modal-body">
				  <div class="tree-dialog-content">
			            <ul id="configCompanyTree" class="ztree"></ul>
			     </div>
				</div>
				<div class="modal-footer">
				  <button type="button" id="modalClose" class="btn btn-default" data-dismiss="modal">关闭</button>
				  <button type="button" id="submitCompanyButton" class="btn btn-primary" data-dismiss="modal" >保存</button>
				</div>
			
			    </div><!-- modal-content -->
  		 </form>
 	</div> <!-- modal-dialog -->
</div>

<script type="text/javascript">
var Enroll;
var ver = navigator.platform;
if('Win32'==ver)
	Enroll=  document.getElementById('infosecEnroll_32');
else
	Enroll = document.getElementById('infosecEnroll_64');
	
function VBATOA(v){
	var tmp = new VBArray(v);
	return tmp.toArray();
}

//根据证书用途列出所有证书
function enumCerts(certUsage){
	var ret = new Array();
	// sm2 cert
	var Count = Enroll.sm_skf_getCountOfCert();
	for (var i = 0; i < Count; i++) {
		var Cert = VBATOA(Enroll.sm_skf_getCertInfo(i));
		var c10 = Cert[10];
		if (c10 == certUsage)
			ret.push(Cert);
	}
	// rsa cert
	var Count1 = Enroll.rsa_csp_getCountOfCert();
	for (var i = 0; i < Count1; i++) {
		var Cert = VBATOA(Enroll.rsa_csp_getCertInfo(i));
		var c0 = Cert[0];
		var c7 = Cert[7];
		if (c0.indexOf('Microsoft') != 0 && c7 == certUsage)
			ret.push(Cert);
	}
	return ret;
}

function readCertSignature(certs,certDN){//比对,返回选择的证书
	var Count = certs.length;
	for(var i = 0; i < Count; i++){
		var c2;//主题
		if (certs[i].length == 12)
			c2 = certs[i][5]; //主题
		else
			c2 = certs[i][2];
		if(c2==certDN){
			return certs[i];
		}
	}
} 


  $(function() {
	  var certsSign=new Array();
	  var certsEnc=new Array();
	  
	  //读取U_KEY
	   $("#addUserReadCert").click(function() {
		  var certDnAll="";
		  var certDn;
		  certsSign = enumCerts("AT_SIGNATURE");//所有可用于签名的证书
		  certsEnc = enumCerts("AT_KEYEXCHANGE");//所有可用于加密的证书
		  if(certsSign.length==0 && certsEnc.length==0){
			  $("#certDn").empty();
			  layer.alert("没有读取到证书",{icon:2});
			  return;
		  }
		  
		  $("#certDn").empty();
		for(var i = 0; i < certsSign.length; i++){
			if (certsSign[i].length == 12)
				certDn = certsSign[i][5]; //主题
			else
				certDn = certsSign[i][2];
			certDnAll +=certDn +";";
			$("#certDn").append("<option value='"+certDn+"'>"+certDn+"</option>");	
		}
		for(var i = 0; i < certsEnc.length; i++){
			if (certsEnc[i].length == 12)
				certDn = certsEnc[i][5]; //主题
			else
				certDn = certsEnc[i][2];
			if(certDnAll.indexOf(certDn) == -1){
				$("#certDn").append("<option value='"+certDn+"'>"+certDn+"</option>");
			}
		}
		//alert(certDN);
		//$("#addUserForm").data('bootstrapValidator').updateStatus('certDn', 'NOT_VALIDATED',null).validateField('certDn');
		
	  }); 
		$("#submitUserAddButton").click(function() {
			var form = $("#addUserForm");
			$('#addUserForm').bootstrapValidator('validate'); 
			if($('#addUserForm').data('bootstrapValidator').isValid()){
				if(!$('#companyName').val()){
					layer.alert("单位不能为空",{icon:0});
				}else{
					certsSign = enumCerts("AT_SIGNATURE");//所有可用于签名的证书
					certsEnc = enumCerts("AT_KEYEXCHANGE");//所有可用于加密的证书
					var certSign=readCertSignature(certsSign,$('#certDn').val());
					var certEnc =readCertSignature(certsEnc,$('#certDn').val());
					/* if(cert==null && certen == null ){
						layer.alert("没有读取到所选证书",{icon:2});
						return;
					} */
					if(certSign!=null){
						if (certSign.length == 12) {
							var c4 = certSign[4]; //容器名
							Enroll.sm_skf_setDevice(certSign[0], certSign[1], certSign[3]);
							var exp = Enroll.sm_skf_exportSignX509Cert(c4);
							$("#X509Cert").val(exp);
							$("#certSn").val(certSign[9]);
						} else {
							var c1 = certSign[1]; //容器名
							Enroll.rsa_csp_setProvider(certSign[0]);
							var exp = Enroll.rsa_csp_exportSignX509Cert(c1);
							$("#X509Cert").val(exp);
							$("#certSn").val(certSign[6]);
						}			  
					}
				  	if(certEnc != null ){
				  		if (certEnc.length == 12) {
							var c4 = certEnc[4]; //容器名
							Enroll.sm_skf_setDevice(certEnc[0], certEnc[1], certEnc[3]);
							var exp = Enroll.sm_skf_exportEncX509Cert(c4);
							$("#X509Certen").val(exp);
							$("#certenSn").val(certEnc[9]);
						} else {
							var c1 = certEnc[1]; //容器名
							Enroll.rsa_csp_setProvider(certEnc[0]);
							var exp = Enroll.rsa_csp_exportEncX509Cert(c1);
							$("#X509Certen").val(exp);
							$("#certenSn").val(certEnc[6]);
						}
				  	}
				  	var message ="";
				  	if(certSign==null && certEnc==null ){
				  		message="没有读取证书，";
				  	}
				  	if(certSign!=null && certEnc==null){
				  		message="该证书主题中只含有签名证书，不能用于数字信封。";
					}
					if(certSign==null && certEnc!=null){
						message="该证书主题中只含有加密证书，不能生成印章。";
					}
					if(certSign!=null && certEnc!=null){
						message="该证书主题中含有签名证书和加密证书。";
					}
				  	
				  	layer.confirm(message+"确定要提交？",{btn:["确定","取消"]},function(){
				  		form.ajaxSubmit({
							success : function(data) {
								if (data.success) {
									layer.alert("添加用户成功",{icon:1});
									var url = "${ctx }/userManage/userList.do";
									loadUrl(url);
								}else{
									layer.alert(data.message,{icon:2});
								}
									
							},
							error : function() {
								layer.alert("请求失败",{icon:2});
							}
						});
				  	});
				}
				
			}
			else{
			    $('#addUserForm').bootstrapValidator('validate'); 
			}		 
		});
		

      $("#selectCom").click(function(){
	    var setting = {
				async: {
					enable: true,
					url:"${ctx }/userManage/configCompanyTree.do",
					autoParam:["id"]
				},
				callback: {
					beforeClick: function(treeId, treeNode) {
						var id=treeNode.id;
						$("[name=companyId]").val(id);
						$("[name=companyName]").val(treeNode.name);
					},
					onAsyncSuccess: function(event, treeId, treeNode, msg) {
						if(treeNode==null){
							var treeObj = $.fn.zTree.getZTreeObj(treeId);
							var nodes = treeObj.getNodes();
							if (nodes.length>0) {
								treeObj.expandNode(nodes[0],true,false,false);
							}
						}
					}
				}
			};
	    	$.fn.zTree.init($("#configCompanyTree"), setting);
      });  	
    	
    	$("#submitCompanyButton").click(function(){
    		var companyId=$("[name=companyId]").val();
    		document.getElementById("companyId").value = companyId;
    		document.getElementById("companyName").value = $("[name=companyName]").val();
    		 $('#addUserCom').modal('hide');
    	}); 
    	//模态框关闭
		 $("#modalClose").click(function(){
			 var oldId = $("[name=companyHiddenID]").val();
			 var oldname = $("[name=companyHidden]").val();
				$("[name=companyId]").val(oldId);
				$("[name=companyName]").val(oldname);
				$('#addUserCom').modal('hide');
			});
    	
    	
		 $("#addUserForm").bootstrapValidator({
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
		                       message: '姓名不为空'
		                   },
		                   stringLength: {
		                       min: 2,
		                       max: 16,
		                       message: '长度为2~16'
		                   },
		                   regexp: {
		                       regexp: /^[\u4e00-\u9fa5a-zA-Z0-9]+$/,
		                       message: '请输入正确格式(中文,数字或字母)'
		                   }
		                  
		               }
		           },
		           phone: {
		               validators: {
		                   regexp: {
		                       regexp: /^1[34578]\d{9}$/,
		                       message: '手机号格式不正确'
		                   }
		               }
		           },
		           email: {
		               validators: {
		                   emailAddress:{
		                       message: '邮箱格式不正确'
		                   }
		               }
		           }
		        }
			});
});

	
</script>