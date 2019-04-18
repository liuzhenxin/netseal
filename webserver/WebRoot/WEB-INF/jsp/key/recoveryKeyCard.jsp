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
							<div id="table-box" style="display: block;">
								<div class="col-md-5 col-sm-5 col-xs-5">
									<button id="f1f1" onclick="javascript:getDatas('f1')" class="btn btn-primary" data-toggle="modal" data-target="#myModal" style="margin-left: 20px;">UKey1密钥恢复</button>
									<button id="f2f2" onclick="javascript:getDatas('f2')" class="btn btn-primary" data-toggle="modal" data-target="#myModal" style="margin-left: 20px;">UKey2密钥恢复</button>
									<button id="f3f3" onclick="javascript:getDatas('f3')" class="btn btn-primary" data-toggle="modal" data-target="#myModal" style="margin-left: 20px;">UKey3密钥恢复</button>
								</div>
								<div class="col-md-4 col-sm-4 col-xs-4">
							  		<form id="addRecoverCertForm" style="margin:0px;display:inline;" action="${ctx }/key/recoverKeyToDBAndCard.do" 
							  		onkeydown="if(event.keyCode==13){return false;}" method="post" enctype="multipart/form-data">
							  			<div class="col-xs-3" style="margin-top: 5px;">
											<label>选择证书:</label>
										</div>
					  					<div class="col-xs-9">
											<input placeholder="选择证书" id="recoverCert" name="recoverCert" type="file" class="form-control input-sm col-xs-3" style="cursor: pointer;">
											<input name="id" type="hidden" id="recoverKeyId"/>
										</div>
										
										<input id="f1" name="file1" type="hidden" value="">
										<input id="f2" name="file2" type="hidden" value="">
										<input id="f3" name="file3" type="hidden" value="">
						  			</form>
						  		</div>
								
								<div class="col-md-3 col-sm-3 col-xs-3">
									<input id="recoverKeyData" style="margin-left: 10px;" type="button" value="合成" class="btn btn-primary col-xs-5">
								</div>
								
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
                <h4 class="modal-title" id="myModalLabel">加密卡密钥恢复</h4>
            </div>
            <input id="ids" value="" type="hidden" />
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="recoverKeyForm" action="" method="post" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
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
											<button id="recoverKey" type="button" class="btn btn-primary col-md-offset-1">恢复</button>
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
	});
	
	
	// 获取数据
	function  getDatas(data) {
		$("#ids").val(data);
	}
	
	// 恢复
	$("#recoverKey").click(function() {
		// 检查Ukey
		var form = $("#recoverKeyForm");
		form.bootstrapValidator('validate');
		if (!form.data('bootstrapValidator').isValid()) {
			$("#recoverKeyForm").bootstrapValidator('validate');
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
		
		// 读取Ukey数据
		try{
  			//Enroll.sm_skf_writeDataEx("keyFile01", keyData); // 写入key
  			var read=Enroll.sm_skf_readDataEx("keyFile01");
  			if (read == ""){
  				throw '未读取到Ukey中密钥数据';
  			}
  		}catch(e){
  			layer.alert("恢复失败,"+e,{icon:2});
  			return;
  		}
		var tip = "恢复完成";
		layer.alert(tip, {icon:1});
		
		var id = $("#ids").val();
		$("#"+id).val(read);
		$("#"+id+id).attr("disabled","disabled"); 
		// 返回到页面
		closeModes();
	});
	
	//模态框关闭
	$("#closeMode").click(function() {
		closeModes();
	});
	
	// 关闭
	function closeModes(){
		$("#certDn_E").val('');
		$("#pins").val('');
		$("#myModal").modal('hide');
	}
	
	// 合成密钥
	$("#recoverKeyData").click(function() {
		var k1 = $("#f1").val();
		var k2 = $("#f2").val();
		var k3 = $("#f3").val();
		var cFile = $("[name=recoverCert]:file").val();
		if(k1 == ""){
			layer.alert("未获取到usbkey1恢复数据", {icon:2});
  			return false;
		}
		if(k2 == ""){
			layer.alert("未获取到usbkey2恢复数据", {icon:2});
  			return false;
		}
		if(k3 == ""){
			layer.alert("未获取到usbkey3恢复数据", {icon:2});
  			return false;
		}
		
		if(k1 === k2 || k1 === k3 || k2 === k3){
			layer.alert("存在相同恢复数据,无法恢复", {icon:2});
  			return false;
		}
		
		if (cFile == "") {
			layer.alert("证书不能为空", {icon:2});
  			return false;
		}
		
		$.ajax({
			type : "post",
			dataType : "json",
			data:{"file1":k1,"file2":k2,"file3":k3},
			url: '${ctx }/key/recoverKeyCard.do',
			success:function(data){ 
				if (data.existCard){
					layer.confirm("加密卡中存在其他密钥, 是否删除?",{btn:["删除","取消"]},function(){
				   		var kid = data.keyRecoveryId;
				   		var priKey = data.keyPri;
				   		var pubKey = data.keyPub;
				   		$.ajax({
							type : "post",
							data : {"kid" : kid, "keyPri" : priKey, "keyPub" : pubKey},
							url : "${ctx }/key/recoverKeyToCard.do",
							success : function(data) {
								if (data == "ok"){
									layer.alert( "恢复成功", {icon:1});
									loadTab("${ctx }/key/keySm2CardList.do", "tab4");
								} else {
									layer.alert( data, {icon:2});
									loadTab("${ctx }/key/keySm2CardList.do", "tab4");
								}
							},
							error : function() {
								layer.alert( "请求失败", {icon:2});
								loadTab("${ctx }/key/keySm2CardList.do", "tab4");
							}
						});
					});
				} else if (data.notInDB) {
					var form=$("#addRecoverCertForm");
					form.ajaxSubmit({
						success:function(data){
							if(data == 'ok'){		
								layer.alert("恢复成功",{icon:1});
								loadTab("${ctx }/key/keySm2CardList.do", "tab4");
							} else{
								layer.alert("请求失败,"+data,{icon:2});
								loadTab("${ctx }/key/keySm2CardList.do", "tab4");
							}
						    },error:function(){
						    	layer.alert("请求失败",{icon:2});
						    	loadTab("${ctx }/key/keySm2CardList.do", "tab4");
						    }
						});
			     } else if (data.InCard) {
					layer.alert( "恢复成功", {icon:1});
					loadTab("${ctx }/key/keySm2CardList.do", "tab4");
			     } else {
			    	 layer.alert("请求失败" + data, {icon:2});
			    	 loadTab("${ctx }/key/keySm2CardList.do", "tab4");
			     }
		    },error:function(){
		    	layer.alert("请求失败",{icon:2});
		    	loadTab("${ctx }/key/keySm2CardList.do", "tab4");
		    }
		 }); 
		
	});
	
	$("#recoverKeyForm").bootstrapValidator({
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
	
</script>