<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-xs-12">
				<div class="x_panel">
				  <div class="x_title" id="title">
				  	<div class="col-md-12 col-sm-12 col-xs-12">
				  		<div class="col-md-7 col-sm-7 col-xs-7">
					  		<input type="button" value="证书请求" class="btn btn-primary requestSMCardCert"  style="padding-left:15px; float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm8"/>
					  		<input type="button" value="恢复" onclick="recoverySM2KeyCard()" class="btn btn-primary" id="recovery" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm7"/>
					  		<input type="button" onclick="backupKey()" value="备份" class="btn btn-primary" id="download" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm11"/>
					  		<input type="button" value="删除" onclick="delKey()" class="btn btn-primary" id="delKeyButton" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
					  		<input type="button" value="证书下载" onclick="downLoadKeyCert()" class="btn btn-primary" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
					  		<input type="button" value="导入加密证书"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target="#myModal"/>
					  	</div>
					  	<div class="col-md-5 col-sm-5 col-xs-5">
					  		<form id="addSMCardCertForm" style="margin:0px;display:inline;" action="${ctx }/key/keySm2CardImpSignCert.do" 
					  		onkeydown="if(event.keyCode==13){return false;}" method="post" enctype="multipart/form-data">
			  					<div class="col-xs-7">
									<input id="certChains" name="certFileSMCard" type="file" class="form-control input-sm col-xs-2" style="cursor: pointer;">
									<input name="id" type="hidden" id="importSMCId"/>
								</div>
								<input type="button" value="导入签名证书" class="btn btn-primary importSMCardCert"/>
				  			</form>
				  		</div>
					</div>
				<div class="clearfix"></div>
				</div>
				<div class="table-responsive  sys-tab artic">
					<article></article>
				 </div>
				  <div class="x_content">
					
					<div id="serverCertShow" style="display: block;">
						<table id="certlinkconfigT" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
						  <thead>
							<tr>
								<th width="1"><input id="check_all" class="js-checkbox-all" type="checkbox" /></th>
								<th>证书主题</th>
								<th>颁发者主题</th>
								<th>生效时间</th>
								<th>失效时间</th>
								<th>密钥用法</th>
								<th>密钥位置</th>
								<th>导入时间</th>
							</tr>
						  </thead>
						  <tbody>
							<c:forEach items="${page.result}" var="key">
								<c:choose>
										<c:when test="${key.sealMac == true}">
											<tr>
												<th width="1"><input name="checkboxt" id="smKCid" class="input_checked" type="checkbox" value="${key.id }"></th>
												<td width="20%">${key.certDn }</td>
												<td width="20%">${key.certIssueDn }</td>
												<td width="14%">${key.notBeforCn }</td>
												<td width="14%">${key.notAfterCn }</td>
												<td width="8%">${key.certUsageCn }</td>
												<td width="8%">${key.hsmId }</td>
												<td width="14%">${key.updateTimeCn }</td>
											</tr>
										</c:when>
										<c:when test="${key.sealMac == false}">
											<tr>
												<th width="1"><input name="checkboxt" id="smKCid" class="input_checked" type="checkbox" value="${key.id }"></th>
												<td class="errorMac" width="20%">${key.certDn }</td>
												<td class="errorMac" width="20%">${key.certIssueDn }</td>
												<td class="errorMac" width="14%">${key.notBeforCn }</td>
												<td class="errorMac" width="14%">${key.notAfterCn }</td>
												<td class="errorMac" width="8%">${key.certUsageCn }</td>
												<td class="errorMac" width="8%">${key.hsmId }</td>
												<td class="errorMac" width="14%">${key.updateTimeCn }</td>
											</tr>
										</c:when>
									</c:choose>
							</c:forEach> 
						  </tbody>
						</table>
					<div class="text-right" id="serverSMCertCardPage"></div>
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
                <h4 class="modal-title" id="myModalLabel">导入数字信封和加密证书</h4>
            </div>
            <input id="keyData" value="" type="hidden" />
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="addSMCardEnvForm" action="${ctx }/key/keySm2CardImpEncCert.do" method="post" data-parsley-validate=""
								      onkeydown="if(event.keyCode==13){return false;}"	class="form-horizontal form-label-left" novalidate style="margin-top: 20px;"
								      enctype="multipart/form-data">
									     <div class="form-group">
											<label class="control-label col-xs-3">选择数字信封</label>
											<input name="id" type="hidden" id="importSMCEnvId"/>
											<div class="col-xs-6">		
											  <input name="cardEnv" type="file" class="form-control input-sm col-xs-2" style="cursor: pointer;">
											</div>
										</div>
									     <div class="form-group">
											<label class="control-label col-xs-3">选择加密证书</label>
											<div class="col-xs-6">		
											  <input name="cardCert" type="file" class="form-control input-sm col-xs-2" style="cursor: pointer;">
											</div>
										</div>
										<br>
										<div class="form-group">
											<button id="confirm" type="button" class="btn btn-primary importSMCardEnv col-md-offset-4">确认</button>
											<button id="closeSM2CardMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
										</div>
									</form>
								</div>
							</div>
					</section>
				</div>
			</div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>




<script type="text/javascript">
	$(function() {
		inputTipText(); //初始化Input的灰色提示信息
		$(".artic").hide();
		
		//---复选框样式
		icheck(".js-checkbox-all");
		
		//分页
		laypage({
 			cont : 'serverSMCertCardPage',
 			skip : true,//跳转页面选项
 			pages : '${page.totalPage}', //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
			curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
 				var pageNo = '${page.pageNo}'; // 当前页(后台获取到的)
 				return pageNo ? pageNo : 1; // 返回当前页码值
 			}(),
 			jump : function(e, first) { //触发分页后的回调
 				if (!first) { //一定要加此判断，否则初始时会无限刷新
 					loadUrl(showPage("tab4","${ctx }/key/keySm2CardList.do?pageNo=" + e.curr));
 				}
 			}
 		});
		
	});
	
	function inputTipText() {
		$(".grayTips") //所有样式名中含有grayTips的input     
		.each(function() {
			var oldVal = $(this).val(); //默认的提示性文本     
			$(this).css({
				"color" : "#888"
			}) //灰色     
			.focus(function() {
				if ($(this).val() != oldVal) {
					$(this).css({
						"color" : "#000"
					});
				} else {
					$(this).val("").css({
						"color" : "#888"
					});
				}
			}).blur(function() {
				if ($(this).val() == "") {
					$(this).val(oldVal).css({
						"color" : "#888"
					});
				}
			}).keydown(function() {
				$(this).css({
					"color" : "#000"
				});
			});

		});
	}
	
	//请求证书
	$(".requestSMCardCert").click(function() {
		$.ajax({
			type : "get",
			dataType : "json",
			url : "${ctx }/key/keySm2CardCsr.do",
			success : function(jsonResult) {
				//---请求成功---
				if (jsonResult.success) {
					var p10 = jsonResult.p10;
                    p10 = p10.replace("-----BEGIN CERTIFICATE REQUEST-----","");
                    p10 = p10.replace("-----END CERTIFICATE REQUEST-----","");
                    p10 = p10.split("\n")
                    $("article").append("-----BEGIN CERTIFICATE REQUEST-----");
                    $("article").append("<br/>");
                    for (var i = 0; i < p10.length - 1; i++) {
                        $("article").append(p10[i]);
                    }
                    $("article").append("<br/>");
                    $("article").append("-----END CERTIFICATE REQUEST-----");
					$(".requestSMCardCert").attr("disabled", "disabled");
					$(".artic").show();
				} else {
					layer.alert( jsonResult.msg, {icon:2});
				}
			},
			error : function() {
				layer.alert( "查询错误", {icon:2});
			}
		});
	});
	
	
	//模态框关闭
	$("#closeSM2CardMode").click(function() {
		$("input[name='certFileSMCardEnv']").val('');
		$("input[name='certFileSMCard']").val('');
		$("#myModal").modal('hide');
	});
	
	
	
	// 导入数字信封
	$(".importSMCardEnv").click(function() {
		ids = "";
		var index = 0;
		$("[id='smKCid']:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					ids = $(this).val();
				}
				index++;
			}
		});
		if(index != 1){
			layer.alert( "请选择一条操作对象",{icon:0});
			return;
		} 
		
		$("#importSMCEnvId").val(ids);
		
		var value = $("[name=cardEnv]:file").val();
		var values = $("[name=cardCert]:file").val();
		if (value == "" || values == "") {
			layer.alert( "请先选择文件", {icon:0});
			return;
		}

		var form = $("#addSMCardEnvForm");
		form.ajaxSubmit({
			success : function(data) { 
				if (data == "ok") {
					$(".modal-backdrop").remove();
					layer.alert( "导入成功", {icon:1});
					loadTab("${ctx }/key/keySm2CardList.do","tab4"); // ajax加载页面
				} else{
					$(".modal-backdrop").remove();
					layer.alert( data, {icon:2});
					loadTab("${ctx }/key/keySm2CardList.do", "tab4"); // ajax加载页面
				}
			},
			error : function() {
				$(".modal-backdrop").remove();
				layer.alert( "导入失败", {icon:2});
				loadTab("${ctx }/key/keySm2CardList.do", "tab4"); // ajax加载页面
			}
		});
	});
	
	//导入证书
	$(".importSMCardCert").click(function() {
		ids = "";
		var index = 0;
		$("[id='smKCid']:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					ids = $(this).val();
				}
				index++;
			}
		});
		if(index != 1){
			layer.alert( "请选择导入证书的一个对象",{icon:0});
			return;
		} 
		$("#importSMCId").val(ids);
		
		var value = $("[name=certFileSMCard]:file").val();
		if (value == "") {
			layer.alert( "请先选择文件", {icon:0});
			return;
		}

		var form = $("#addSMCardCertForm");
		form.ajaxSubmit({
			success : function(data) { 
				if (data == "ok") {
					layer.alert( "导入成功", {icon:1});
					loadTab("${ctx }/key/keySm2CardList.do","tab4"); // ajax加载页面
				} else{
					layer.alert( data, {icon:2});
					loadTab("${ctx }/key/keySm2CardList.do", "tab4"); // ajax加载页面
				}
			},
			error : function() {
				layer.alert( "导入失败", {icon:2});
				loadTab("${ctx }/key/keySm2CardList.do", ""); // ajax加载页面
			}
		});
	});

	
	//删除密钥
	function delKey(){
		ids = "";
		var index = 0;
		$("[id='smKCid']:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					ids = $(this).val();
				}else{
					ids = ids + ";" + $(this).val();
				}
				index++;
			}
		});
		if(index == 0){
			layer.alert( "请选择删除对象",{icon:0});
			return;
		} 
		
		layer.confirm("确定删除?",{btn:["确定","取消"]},function(){
			 $.ajax({
				url : "${ctx }/key/delKeyCard.do",
				type : "get",
				data : "id=" + ids,
				dataType : "json",
				success : function(data) {
					if (data.success){
						layer.alert( data.message, {icon:1});
						loadTab("${ctx }/key/keySm2CardList.do", "tab4");
					}else {
						layer.alert( data.message, {icon:2});
						loadTab("${ctx }/key/keySm2CardList.do", "tab4");
					}
				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
					loadTab("${ctx }/key/keySm2CardList.do", "tab4");
				}
			}); 
		});
	}
	
		
	//密钥恢复
	function recoverySM2KeyCard(){
		loadTab("${ctx }/key/toRecoveryKeyCard.do", "tab4");
	}
	
	//密钥备份
	function backupKey(){
		ids = "";
		var index = 0;
		$("[id='smKCid']:checkbox").each(function(){
			if(this.checked){
				if(index == 0 ){
					ids = $(this).val();
				}
				index++;
			}
		});
		if(index == 0 || index > 1){
			layer.alert( "请选择一条备份对象",{icon:0});
			return;
		} 
		
		loadTab("${ctx }/key/toBackupKeyCard.do?id="+ids,"tab4");
	}
	
	// 证书下载
	function downLoadKeyCert(){
		var ids = "";
		var index = 0;
		$("[id='smKCid']:checkbox").each(function(){
			if(this.checked){
				if(index == 0 ){
					ids = $(this).val();
				}
				index++;
			}
		});
		if(index == 0 || index > 1){
			layer.alert( "请选择一条下载对象",{icon:0});
			return;
		} 
		
		$.ajax({
			url : "${ctx }/key/toDownLoadKeyCert.do",
			type : "get",
			data : "id=" + ids,
			dataType : "json",
			success : function(data) {
				if (data.success){
					window.location.href="${ctx }/key/downLoadKeyCert.do?certPath=" + data.message;
				}else{
					layer.alert( data.message, {icon:2});
				}
				
			},
			error : function() {
				layer.alert( "请求失败", {icon:2});
			}
		}); 
	}
	
</script>