<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div class="title clearfix">
			<h2 style="font-size:34px; font-weight:bold;">签章人管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理   /    签章人管理    /    签章人修改</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			签章人修改</a>
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
				<form id="editUserForm" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;"  autocomplete="off" action="${ctx }/userManage/editUser.do" method="post" >
					    <input id="X509CertE" name="X509Cert" type="hidden"> 
						<input id="certSnE" name="certSn" type="hidden">
						
						<input id="X509CertenE" name="X509Certen" type="hidden">
				  		<input id="certenSnE" name="certenSn" type="hidden">
						
						<input   name="id" type="hidden" value="${user.id }" />
					  <div class="form-group">
						<label class="control-label col-xs-3">姓名 
						</label>
						<div class="col-xs-6">
						  <input type="text" class="form-control col-xs-6" readonly value="${user.name }">
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">用户单位 
						</label>
						<div class="col-xs-6">
						  <input class="form-control " readonly value="${user.companyName }" />
						  <input id="companyIdE" name="companyId" class="form-control input-sm" type="hidden" value="${user.companyId }" />
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">已注册证书 
						</label>
						<div class="col-xs-6">
						  <select class="form-control " id="certDn">
							<c:forEach items="${certList }" var="cert">
								<option>${cert.certDn }</option>
							</c:forEach>
						  </select>
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">证书主题 
						</label>
						<div class="col-xs-6">
						  <select id="certDn_E" name="certDn" class="form-control ">
						  </select>
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">手机 
						</label>
						<div class="col-xs-6">
						  <input id="phone" name="phone" class="form-control "
							type="text" value="${user.phone }" />
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3">邮箱 
						</label>
						<div class="col-xs-6">
						  <input id="email" name="email" class="form-control "
							type="text" value="${user.email }" />
						</div>
					  </div>
					  
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-8">
									  <button id="editUserReadCert"  type="button" class="btn btn-primary col-md-offset-1">读取证书</button>
									  <button id="deleteCert"  type="button" class="btn btn-primary col-md-offset-1">删除证书</button>
									  <button id="submitUserButton"  type="button" class="btn btn-primary col-md-offset-1">提交</button>
									  <button class="btn btn-primary col-md-offset-1" type="button" id="returnButton" onclick="javascript:loadUrl('${ctx }/userManage/userList.do')">返回</button>
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
	
	$(function(){
		//--表单验证---修改
		//validate("#editUserForm");
		$("#editUserForm").bootstrapValidator({
          
            fields: {
            	account: {
                    validators: {
                        notEmpty: {
                            message: '用户名不能为空'
                        },
                        stringLength: {
                            min: 2,
                            max: 16,
                            message: '用户名长度为2~16'
                        },
                        regexp: {
                            regexp: /^[\u4e00-\u9fa5a-zA-Z0-9]+$/,
                            message: '请输入正确格式(中文,数字或字母)'
                        },
                    }
                },
                phone: {
                    validators: {
                        regexp: {
                            regexp: /^1[34578]\d{9}$/,
                            message: '手机号格式不正确'
                        },
                    }
                },
                email: {
                    validators: {
                        emailAddress:{
                            message: '邮箱格式不正确'
                        }
                    }
                },
            }  
            });
		
		 var certsSign=new Array();
		 var certsEnc=new Array();
		 
		 //读取U_KEY
		  $("#editUserReadCert").click(function() {
			  var certDnAll="";
			  var certDn;			  
			  certsSign = enumCerts("AT_SIGNATURE");//所有可用于签名的证书
			  certsEnc = enumCerts("AT_KEYEXCHANGE");//所有可用于加密的证书
			  if(certsSign.length==0 && certsSign.length==0){
				  $("#certDn_E").empty();
				  layer.alert("没有读取到证书",{icon:2});
				  return;
			  }
			 $("#certDn_E").empty();
			for(var i = 0; i < certsSign.length; i++){
				if (certsSign[i].length == 12)
					certDn = certsSign[i][5]; //主题
				else
					certDn = certsSign[i][2];
				certDnAll +=certDn +";";
				$("#certDn_E").append("<option value='"+certDn+"'>"+certDn+"</option>");
			}	
			for(var i = 0; i < certsEnc.length; i++){
				if (certsEnc[i].length == 12)
					certDn = certsEnc[i][5]; //主题
				else
					certDn = certsEnc[i][2];
				if(certDnAll.indexOf(certDn) == -1){
					$("#certDn_E").append("<option value='"+certDn+"'>"+certDn+"</option>");
				}
			}
			  
		  });
		 
		
	//修改
	$("#submitUserButton").click(function() {
		var form = $("#editUserForm");
		form.bootstrapValidator('validate');
		if ($('#editUserForm').data('bootstrapValidator').isValid()) {
			var certDn=$('#certDn_E').val();
			 if(certDn!=null){//要注册证书
				certsSign = enumCerts("AT_SIGNATURE");//所有可用于签名的证书
				certsEnc = enumCerts("AT_KEYEXCHANGE");//所有可用于加密的证书
				var certSign=readCertSignature(certsSign,certDn);
				var certEnc =readCertSignature(certsEnc,certDn);
				if(certSign==null && certEnc == null ){
					layer.alert("没有读取到所选证书",{icon:2});
					return;
				}
				if(certSign!=null){
					if (certSign.length == 12) {
						var c4 = certSign[4]; //容器名
						Enroll.sm_skf_setDevice(certSign[0], certSign[1], certSign[3]);
						var exp = Enroll.sm_skf_exportSignX509Cert(c4);
						$("#X509CertE").val(exp);
						$("#certSnE").val(certSign[9]);
					} else {
						var c1 = certSign[1]; //容器名
						Enroll.rsa_csp_setProvider(certSign[0]);
						var exp = Enroll.rsa_csp_exportSignX509Cert(c1);
						$("#X509CertE").val(exp);
						$("#certSnE").val(certSign[6]);
					}
				}
			  	if(certEnc != null ){
			  		if (certEnc.length == 12) {
						var c4 = certEnc[4]; //容器名
						Enroll.sm_skf_setDevice(certEnc[0], certEnc[1], certEnc[3]);
						var exp = Enroll.sm_skf_exportEncX509Cert(c4);
						$("#X509CertenE").val(exp);
						$("#certenSnE").val(certEnc[9]);
					} else {
						var c1 = certEnc[1]; //容器名
						Enroll.rsa_csp_setProvider(certEnc[0]);
						var exp = Enroll.rsa_csp_exportEncX509Cert(c1);
						$("#X509CertenE").val(exp);
						$("#certenSnE").val(certEnc[6]);
					}
			  	}
			} 
			
			form.ajaxSubmit({
				success : function(data) {
					if (data.success) {
							layer.alert(data.message,{icon:1});
							loadUrl("${ctx }/userManage/userList.do");
						} else {
							if (typeof (data.message) == "undefined")
								layer.alert( "操作失败",{icon:2});
							else
								layer.alert(data.message,{icon:2});
						}
				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
					}
				});
			}
		});
	
	
	  //删除证书
	  $("#deleteCert").click(function(){
		  var certDN = $("#certDn").val();
		  var id = $("input[name='id']").val();
		  $.ajax({
			  url : "${ctx }/userManage/delCert.do",
			  type : "get",
			  data : "certDN="+certDN+"&userId="+id,
			  dataType : "json",
			  success : function(data){
				  if(data.success){
					  layer.alert(data.message, {icon:1});
					  loadUrl("${ctx }/userManage/toEditUser.do?id="+id);
				  }else{
					  layer.alert(data.message, {icon:2});
					  loadUrl("${ctx }/userManage/toEditUser.do?id="+id);
				    }
			  },
		 	  error : function(){
				  layer.alert("删除失败",{icon:2});
				  loadUrl("${ctx }/userManage/toEditUser.do?id="+id);
		 	  }
		  });
		  
	  });
	
	
	
	
	});
	</script>