<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div class="title clearfix">
<h2 style="font-size:34px; font-weight:bold;">印章管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">印章管理    /    印章修改</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			印章修改</a>
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
				<form id="editSealForm" data-parsley-validate="" class="form-horizontal form-label-left" action=""  method="post" novalidate style="margin-top:20px;" enctype="multipart/form-data" autocomplete="off" >
					<input id="certSn" name="certSn" type="hidden" value="${seal.certSn }">
						<input id="adminCertSn" name="adminCertSn" type="hidden" value="${adminCertSn }">
					<input id="sealId" name="id" type="hidden" value="${seal.id }">
					<div class="form-group">
						<label class="control-label col-xs-3">签章人 
						</label>
						<div class="col-xs-6">
						  <input readonly="readonly" class="form-control " type="text" value="${seal.userName }" />
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-xs-3">证书DN 
						</label>
						<div class="col-xs-6">
						  <input readonly="readonly" class="form-control " type="text" value="${seal.certDn }" />
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-xs-3">印章名称 
						</label>
						<div class="col-xs-6">
						  <input id="name" name="name" class="form-control " type="text" value="${seal.name }" readonly="readonly"/>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-xs-3">最大签章次数 
						</label>
						<div class="col-xs-6">
						  <input id="usedLimit" name="usedLimit" class="form-control" type="text" value="${seal.usedLimit }" /> (0-999,0为无限制,-1为不能进行盖章)
						</div>
					</div>
					
						<div class="form-group" id="files">
							<label class="control-label col-xs-3">图片 </label>
							<div class="col-xs-6" >
								<input id="photoPath" name="photoPath" type="hidden" class="form-control" value="${seal.photoPath}" />
								<input id="photoFile" name="photoFile" type="file" class="form-control input-sm" />
								<a href="#" data-toggle="modal" data-target="#myModal">查看</a>
							</div>
						</div>
					
				  
					<div class="form-group" id="alpha">
						<label class="control-label col-xs-3">图片透明度</label>
						<div class="col-xs-6" style="margin-top:6px;">
						  <div id="sliderParent" style="padding: 6px;background-color: gray;display: inline-block;">  </div>
						    <input id="transparency" name="transparency" type="hidden"   value="${seal.transparency }" > 
					     </div>
				    </div>
					
					
					<div class="form-group">
						<label class="control-label col-xs-3" >申请是否审核
						</label>
						<div class="col-xs-6">
						  <input id="isAuditReqCn"  class="form-control " type="text" value="${seal.isAuditReqCn}"  readonly="readonly"/>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-xs-3" >制章是否验证书
						</label>
						<div class="col-xs-6">
						  <input id="isAuthCertGenSealCn"  class="form-control " type="text" value="${seal.isAuthCertGenSealCn}"  readonly="readonly"/>	
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-xs-3" >下载是否验证书 
						</label>
						<div class="col-xs-6">
						   <input id="isAuthCertDownloadCn"  class="form-control " type="text" value="${seal.isAuthCertDownloadCn}"  readonly="readonly"/>		
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-xs-3" >印章是否可下载
						</label>
						<div class="col-xs-6">
						 	<input id="isDownloadCn"  class="form-control " type="text" value="${seal.isDownloadCn}"  readonly="readonly"/>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-xs-3">启用日期 
						</label>
						<div class="col-xs-6">
						  <input id="notBeforCn" name="notBeforCn" class="form-control " type="text" value="${seal.notBeforCn }"  readonly="readonly"/>
						</div>
					</div>
					
					<div class="form-group">
						<label class="control-label col-xs-3">终止日期 
						</label>
						<div class="col-xs-6">
						  <input id="notAfterCn" name="notAfterCn" class="form-control " type="text" value="${seal.notAfterCn }"  readonly="readonly"/>
							</div>
						</div>
					<div class="form-group" id="div_pin">
						<label for="after" class="col-xs-3 control-label">输入PIN码</label>
						<div class="col-xs-6">
								<input id="pin" name="pin" class="form-control input-sm col-xs-6" type="password" />
						</div>
					</div>
					  
					  
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
									  <button id="submitEditSealButton" type="button" class="btn btn-primary col-md-offset-3" >确定</button>
									  <button onclick="javascript:loadUrl('${ctx }/sealManage/sealList.do')" type="button" class="btn btn-primary col-md-offset-3" id="return" >返回</button>
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

<!-- 查看印模图片模态框 -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel" style="color:#3c8dbc;font-size:20px;">图片</h4>
			</div>
			<div class="modal-body">
				<div align="center">
					<img src="${ctx }/sealManage/viewPhoto.do?id=${seal.id }&time=<%=System.currentTimeMillis()%>" />
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>




<script type="text/javascript">

var  type = ${seal.type};
if(type == 3){
	document.getElementById("alpha").style.display="none";//隐藏
	document.getElementById("files").style.display="none";//隐藏
}else{
	document.getElementById("alpha").style.display="";
	document.getElementById("files").style.display="";
}


var option = {
    color: '#337ab7',
    width: '400px',
    progress: 0.3,
    handleSrc: '${ctx }/img/slider_handle.png',
    isCustomText: false
};

var num = parseInt($("#transparency").val());
   
$('#sliderParent')
    .jackWeiSlider(option)
    // .setText('2018-4-5 02:39:00')
    .setProgress(num/100)
    .setOnStartDragCallback(function () {
        //console.log('start');
    })
    .setOnDragCallback(function (p) {
       // console.log(p);
    })
    .setOnStopDragCallback(function () {
       // console.log('stop');
    });
    
   
var Enroll;

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
		var rSn=certs[i][9];//证书序列号
		if(rSn==certSn){
			return certs[i];
		}
	}
}


//判断是否是rsa证书,如果是rsa证书返回rsa证书,否则返回null

function isRsaCert(cert){
	if(cert[11] == "1.2.156.10197.1.501")
        return null;
    else {
        var ret = null;
        var Count = Enroll.rsa_csp_getCountOfCert();
        for (var i = 0; i < Count; i++) {
            var tempCert = VBATOA(Enroll.rsa_csp_getCertInfo(i));
            var tc3 = tempCert[3]; //证书颁发者
            var tc6 = tempCert[6]; //证书序列号
            var c6 = cert[6]; //证书颁发者
            var c9 = cert[9]; //证书序列号
            if ((tc3 == c6) && (tc6 == c9)) {
                ret = tempCert;
                break;
            }
        }
        return ret;
    } 
}


$(function(){
	
	 
	var ver = navigator.platform;
	if('Win32'==ver)
		Enroll=  document.getElementById('infosecEnroll_32');
	else
		Enroll = document.getElementById('infosecEnroll_64');
	
	var isAuditReq = ${seal.isAuditReq};
	if( isAuditReq == 1){
		var certs = enumCerts();//所有可用于签名的证书
		var adminCertSn=$("#adminCertSn").val();
		var cert=readCertSignature(certs,adminCertSn);
		 if(cert!=null){
			 var retCert=isRsaCert(cert);
			 if(retCert!=null){//管理员为rsa证书,隐藏输入pin密码框
				 $("#div_pin").hide();
			 }
	}
	}else{
		$("#div_pin").hide();
	}
	 
	
	$("#editSealForm").bootstrapValidator({
		fields: {
			usedLimit: {
                validators: {
                    notEmpty: {
                        message: '最大签章次数不能为空'
                    },
                   
                    regexp: {
                        regexp: /^(\-?(1)|[0-9]{1,3})$/,
                        message: '最大签章次数范围为(-1)~999'
                    }
                   
                }
            }
         }
 	});

	$("#submitEditSealButton").click(function(){
		 var isAuditReq = ${seal.isAuditReq };
		 var type = ${seal.type};
		 var oldTransparency = ${seal.transparency };
		 var form=$("#editSealForm");
		 form.bootstrapValidator('validate');
		 var num = $(".jws-text").html();
	     num = num.substr(0,num.length-1);
	     if(oldTransparency != num ){
	     	$("#transparency").val(num);
	     }
		 if (form.data('bootstrapValidator').isValid()) {
			 //没有调整透明度
			 if((oldTransparency == num && !$('#photoFile').val()) || (type == 3)){
			 form.ajaxSubmit({
				 type: 'post', 
		         url: '${ctx }/sealManage/editSeal.do',
				 success:function(data){
				     if(data=="ok"){	
				    	layer.alert("操作成功",{icon:1});
			        	var url="${ctx }/sealManage/sealList.do";
			        	loadUrl(url);
				      }else{
				    	  layer.alert("修改失败",{icon:2});
					  }
				    	
				    },error:function(){
				    	layer.alert("请求失败",{icon:2});
				    }
			 }); 
			 }else{
				if(isAuditReq == 0) {//调整透明度且不需要审核
					 form.ajaxSubmit({
						 type: 'post', 
				         url: '${ctx }/sealManage/editNotAuditSeal.do',
						 success:function(data){
						     if(data ==  "ok"){	
						    	layer.alert("操作成功",{icon:1});
					        	var url="${ctx }/sealManage/sealList.do";
					        	loadUrl(url);
						      }else{
						    	  layer.alert("修改失败",{icon:2});
								}
						    	
						    },error:function(){
						    	layer.alert("请求失败",{icon:2});
						    }
					 }); 
					
				}else{ //调整透明度且需要审核 
					var certs = enumCerts();//所有可用于签名的证书
					var adminCertSn=$("#adminCertSn").val();
					var cert=readCertSignature(certs,adminCertSn);
					 if(cert==null){
						layer.alert("没有读取到管理员证书",{icon:2});
						return;
					}	
					
					//第一次提交 签名
						form.ajaxSubmit({
							 type: 'post', 
					         url: '${ctx }/sealManage/genAuditSeal.do',
					        // data:{"id":$("#sealId").val(), "usedLimit":$("#usedLimit").val(),
							//		"transparency":$("#transparency").val()},
							success:function(data){
								data = JSON.parse(data);
								if(data.success){
									var signData = "";
									var toSignData = data.sealData;
									var hashAlg = data.hashAlg;
							    	//对印章数据签名				        
									if(retCert != null){//rsa证书
										Enroll.rsa_csp_setProvider(retCert[0]);
										try {
											signData = Enroll.rsa_csp_signDataOfBytesBase64(retCert[1], toSignData, hashAlg);
										} catch(e){
											layer.alert("证书签名错误 " + e.message, {icon:2});
											return;
										}
									}else{//sm证书
										var pin = $("#pin").val();
										if(pin.length == 0){
											layer.alert("国密证书PIN码不能为空",{icon:2});
											return;
										}
										Enroll.sm_skf_setDevice(cert[0],cert[1],cert[3]);
										var res = Enroll.sm_skf_VerifyPin(pin);
										if(!res){
											layer.alert("验证管理员U_KEY失败,PIN码不正确",{icon:2});
											return; 
										}
										try {
											signData = Enroll.sm_skf_signDataOfBytesBase64(cert[4], toSignData, "", true);
										} catch(e){
											layer.alert("证书签名错误 " + e.message,{icon:2});
											return;
										}
									}
								  	
									//第二次提交审核
									$.ajax({
										type : "post",
										dataType : "json",
										data:{"signData":signData,"id":$("#sealId").val(), "toSignData": toSignData,"usedLimit":$("#usedLimit").val(),
											"transparency":$("#transparency").val()},
										url: "${ctx }/sealManage/editAuditSeal.do",
										success:function(data){
											if(data.success){
												var url="${ctx }/sealManage/sealList.do";
												loadUrl(url);
											}else{
												layer.alert("修改失败",{icon:2});
											}
										},error:function(){
											layer.alert("请求失败",{icon:2});
										}
									}); 
								}else{
									layer.alert("修改失败",{icon:2});
								}
							    	
							},error:function(){
								layer.alert("请求失败",{icon:2});
							}
						});					
					
				}
			 }
			 
		 }else{
			 form.bootstrapValidator('validate');
		 }
		 
	});
});


</script>