<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">印章管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">印章管理    /    印章下载</i>
	</li>
</ul>	
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			印章下载</a>
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
					<form id="sealDownLoadForm" class="form-horizontal form-label-left" action="" method="post" style="margin-top:20px;" >
						 <input id="sealId" name="sealId" type="hidden" value="${seal.id }" />
		 				 <input id="certSn" name="certSn" type="hidden" value="${seal.certSn }" />
						<div class="form-group">
							<label class="control-label col-xs-3">印章名称 
							</label>
							<div class="col-xs-6">
							  <input type="text"   value="${seal.name }" class="form-control col-xs-6">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">签章人 
							</label>
							<div class="col-xs-6">
							  <input type="text"  value="${seal.userName }" class="form-control col-xs-6">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">证书主题 
							</label>
							<div class="col-xs-6">
							  <input type="text" value="${seal.certDn }"  class="form-control col-xs-6">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">单位名称 
							</label>
							<div class="col-xs-6">
							  <input type="text" value="${seal.companyName }"  class="form-control col-xs-6">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">印章生成时间 
							</label>
							<div class="col-xs-6">
							  <input type="text" value="${seal.generateTimeCn }" class="form-control col-xs-6" style="cursor: pointer;">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">印章起止时间 
							</label>
							<div class="col-xs-6">
							  <input type="text" value="${seal.notBeforCn }~${seal.notAfterCn }" class="form-control col-xs-6" style="cursor: pointer;">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">最大签章次数 
							</label>
							<div class="col-xs-6">
							  <input type="text" value="${seal.usedLimitCn }" class="form-control col-xs-6">
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-xs-3">输入签章PIN码 
							</label>
							<div class="col-xs-6">
							  <input id="pin" name="pin" type="password"  class="form-control col-xs-6">
							</div>
						</div>
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
									  <button type="button" class="btn btn-primary col-md-offset-3" id="sealDownLoad" >下载</button>
									  <button type="button" class="btn btn-primary col-md-offset-3" id="back" >返回</button>
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
function enumCerts(){//所有可用于签名的证书
	var ret = new Array();
	Enroll.sm_skf_setAllCertSelected(true);
	var Count = Enroll.sm_skf_getCountOfCert();
	for(var i = 0; i < Count; i++){
		var Cert = VBATOA(Enroll.sm_skf_getCertInfo(i));
		var c7=Cert[10];
		if(c7=='AT_SIGNATURE'){
			ret.push(Cert);
		}
	}
	return ret;
}
function readCertSignature(certs,certSn){//返回选择的证书
	var Count = certs.length;
	for(var i = 0; i < Count; i++){
		var rSN=certs[i][9];
		 if (certSn == rSN){
			 return certs[i];
		 }
	}
	return null;
}

function selectKeyAndVerify(Provider, Serial, Pin){//验证pin
	try{
		Enroll.sm_skf_useDevice(Provider, Serial, true);
		return Enroll.sm_skf_VerifyPin(Pin);
	}catch(e){
		return false;
	}
	
}
	$(function() {
		//--表单验证---修改
		$("#sealDownLoadForm").bootstrapValidator({
			fields: {
                pin: {
                    validators: {
                        notEmpty: {
                            message: 'PIN码不能为空'
                        }, stringLength: {
                            min: 1,
                            max: 50,
                            message: 'PIN长度为1~50'
                        }
                    }
                }
             }
		});
		
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/sealManage/sealList.do");
		});
		//下载
		$("#sealDownLoad").click(function() {
			
			var form = $("#sealDownLoadForm");
			form.bootstrapValidator('validate');
			if (!form.data('bootstrapValidator').isValid()) {
				$("#sealDownLoadForm").bootstrapValidator('validate');
				return;
			}
			
			Enroll.isShowErrorWindow(false);
			var certs = enumCerts();//所有可用于签名的证书	
			
			var certSn=$("#certSn").val();
			var cert=readCertSignature(certs,certSn);
			if(cert==null){
				layer.alert("没有读取到签章人证书",{icon:2});
				return;
			}
			//验证pin
			var CMBProvider=cert[0];
			var CMBSerial=cert[2];
			var pin=$("#pin").val();
			var res=selectKeyAndVerify(CMBProvider, CMBSerial, pin);
			if(!res){
				layer.alert("验证U_KEY失败,PIN码不正确",{icon:2});
				return; 
			}
			  
		  	var c2=cert[5];//主题
		  	 $.ajax({
					type : "post",
					dataType : "json",
					data:{"certDN":c2,"sealId":$("#sealId").val()},
					url : "${ctx }/sealManage/sealDownLoad.do",
					 success:function(data){
						 if(data.success){
					  		var X509Cert_Base64=data.sealData;
					  		try{
					  			Enroll.sm_skf_writeDataEx("SKFFile01",X509Cert_Base64);
					  			//var read=Enroll.sm_skf_readDataEx("SKFFile01");
						  		//alert(read);
					  		}catch(e){
					  			layer.alert("下载失败,"+e,{icon:2});
					  			return;
					  		}
						  	layer.alert("下载成功",{icon:1});
						  	
						 }else{
							 layer.alert(data.message,{icon:2});
						 }
				    },error:function(){
				    	layer.alert( "验证证书DN错误", {icon:2});
				    }
			 });
		});
	});


</script>