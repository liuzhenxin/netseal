<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="clearfix"></div>
						<div class="x_content">
							<br>
							<div id="table-box" style="display: block;">
								<input id="keyIds" type="hidden" value="${keyId}">
								<table class="table table-striped table-bordered dt-responsive nowrap">
									<thead>
										<tr>
											<th>文件名</th>
											<th>操作</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td width="25%">usbkey1</td>
											<td width="24%"><button id="f11" onclick="javascript:getDatas('${str1}','f11')" class="btn btn-primary" data-toggle="modal" data-target="#myModal">备份</button></td>
										</tr>
										<tr>
											<td width="25%">usbkey2</td>
											<td width="24%"><button id="f22" onclick="javascript:getDatas('${str2}','f22')" class="btn btn-primary" data-toggle="modal" data-target="#myModal">备份</button></td>
										</tr>
										<tr>
											<td width="25%"><input id="f3" type="hidden" value="f3" />usbkey3</td>
											<td width="24%"><button  id="f33" onclick="javascript:getDatas('${str3}','f33')" class="btn btn-primary" data-toggle="modal" data-target="#myModal">备份</button></td>
										</tr>
										<tr>
											<td width="25%"><input id="f4" type="hidden" value="f4" />usbkey4</td>
											<td width="24%"><button  id="f44" onclick="javascript:getDatas('${str4}','f44')" class="btn btn-primary" data-toggle="modal" data-target="#myModal">备份</button></td>
										</tr>
										<tr>
											<td width="25%"><input id="f5" type="hidden" value="f5" />usbkey5</td>
											<td width="24%"><button id="f55" onclick="javascript:getDatas('${str5}','f55')" class="btn btn-primary" data-toggle="modal" data-target="#myModal">备份</button></td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</div>	

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">加密卡密钥备份</h4>
            </div>
            <input id="keyData" value="" type="hidden" />
            <input id="keyId" value="" type="hidden" />
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="backupKeyForm" action="" method data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
										<div class="form-group">
											<label class="control-label col-xs-3">选择证书</label>
											<div class="col-xs-6">
												<select id="certDn_E" name="certDn" class="form-control input-xs"></select>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">Pin </label>
											<div class="col-xs-6">
												<input id="pins" name="pin" class="form-control col-xs-6">
											</div>
										</div>
										<br>
										<div class="form-group">
											<input id="readCert" type="button" value="读取证书" class="btn btn-primary col-md-offset-3">
											<button id="backupSave" type="button" class="btn btn-primary col-md-offset-1">备份</button>
											<button id="closeMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</section>
				</div>
			</div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
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
	
	//返回选择的证书
	function readCertSignature(certs, certDN){
		var Count = certs.length;
		for(var i = 0; i < Count; i++){
			var c2 = certs[i][5];//主题
			if(c2 == certDN){
				return certs[i];
			}
		}
	}
	
	//验证pin
	function selectKeyAndVerify(Provider, Serial, Pin){//验证pin
		try{
			Enroll.sm_skf_useDevice(Provider, Serial, true);
			return Enroll.sm_skf_VerifyPin(Pin);
		}catch(e){
			return false;
		}
	}
	
	// 获取数据
	function  getDatas(data,id) {
		$("#keyData").val(data);
		$("#keyId").val(id);
	}
	
	// 是否已备份
	$(function(){
		
	});
	
	$(function() {	
		// 读取证书
		var certs=new Array();
		//读取U_KEY
		$("#readCert").click(function() {
		  	certs = enumCerts();//所有可用于签名的证书
			if(certs.length==0){
				$("#certDn_E").empty();
				layer.alert("没有读取到证书",{icon:2});
				return;
			}
			$("#certDn_E").empty();
			for(var i = 0; i < certs.length; i++){
				$("#certDn_E").append("<option value='"+certs[i][5]+"'>"+certs[i][5]+"</option>");		
			}
		});
		
		// 备份
		$("#backupSave").click(function() {
			var form = $("#backupKeyForm");
			form.bootstrapValidator('validate');
			if (!form.data('bootstrapValidator').isValid()) {
				$("#backupKeyForm").bootstrapValidator('validate');
				return;
			}
			
			var certDn = $("#certDn_E").val();
			var pin = $("#pins").val();
			if (certDn == null) {
				layer.alert("请选择证书",{icon:0});
				return;
			}
			if (pin == null) {
				layer.alert("请输入PIN码",{icon:0});
				return;
			}
			
			Enroll.isShowErrorWindow(false);
	        var certs = enumCerts();//所有可用于签名的证书
	        var certDn = $('#certDn_E').val();
	        
			var cert = readCertSignature(certs, certDn);
			if(cert == null){
				layer.alert("没有读取到所选证书",{icon:0});
				return;
			}
			
	        // 验证PIN
			var CMBProvider = cert[0];
			var CMBSerial = cert[2];
			var pin = $("#pins").val();
			var res = selectKeyAndVerify(CMBProvider, CMBSerial, pin);
			if(!res){
				layer.alert("验证U_KEY失败,PIN码不正确",{icon:2});
				return; 
			}
			// 获取备份数据
			var keyData = $("#keyData").val();
			try{
	  			Enroll.sm_skf_writeDataEx("keyFile01", keyData); // 写入key
	  			// var read=Enroll.sm_skf_readDataEx("keyFile01");
		  		// alert(read);
	  			var id = $("#keyId").val();
	  			$("#"+id).attr("disabled","disabled"); 
	  		}catch(e){
	  			layer.alert("备份失败,"+e,{icon:2});
	  			return;
	  		}
			var tip = "备份完成";
			layer.alert(tip, {icon:1});
			// 返回到页面
			closeMode();
		});
		
		
		//模态框关闭
		$("#closeMode").click(function() {
			closeMode();
		});
		
		// 关闭
		function closeMode(){
			$("#certDn_E").val('');
			$("#pins").val('');
			$("#myModal").modal('hide');
		}
		
		$("#backupKeyForm").bootstrapValidator({
			fields : {
				certDn : {
					validators : {
						notEmpty : {
							message : '证书不能为空'
						}
					}
				},
				pin : {
					validators : {
						notEmpty : {
							message : 'pin不能为空'
						},
						stringLength : {
							min : 1,
							max : 30,
							message : '长度为1~30'
						},
						regexp : {
							regexp : /^[0-9]+$/,
							message : '请输入正确格式(数字)'
						}

					},

				}
			}
		});
	});
</script>