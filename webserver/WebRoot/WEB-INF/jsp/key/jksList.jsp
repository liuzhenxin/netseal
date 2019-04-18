<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-xs-12">
				<div class="x_panel">
				  <div class="x_title" id="title">
				  		<input type="button" value="证书请求" class="btn btn-primary requestCert"  style="padding-left:15px; float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm8"/>
				  		<input type="button" value="恢复" onclick="recoveryKey()" class="btn btn-primary" id="recovery" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm7"/>
				  		<input type="button" onclick="downloadKey()" value="备份下载" class="btn btn-primary" id="download" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm11"/>
				  		<input type="button" value="删除" onclick="delKey()" class="btn btn-primary" id="delKeyButton" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
				  		<input type="button" value="证书下载" onclick="downLoadKeyCert()" class="btn btn-primary" id="downLoadKeyCert" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
				  		
				  		<form id="addCertChainForm" class="form-horizontal" action="${ctx }/key/keyJksImpCert.do" 
				  		onkeydown="if(event.keyCode==13){return false;}" method="post" enctype="multipart/form-data">
				  			<input type="button" value="导入证书" class="btn btn-primary importCert"  style=" height:34px; padding-left:15px; float:right; margin-left:10px;"/>
		  					<div class="col-xs-2 pull-right">
							  <input id="certChain" name="certFile" type="file" class="form-control input-sm col-xs-2" style="cursor: pointer;">
							  <input name="id" type="hidden" id="importId"/>
							</div>
								  		
			  			</form>
			  			
					<div class="clearfix"></div>
				  </div>
				  <div class="table-responsive  sys-tab artic">
						<article></article>
				  </div>
				  <div class="x_content">
					
					<div id="serverCertShow" style="display: block;">
						<table id="certlinkconfigT" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%">
						  <thead>
							<tr>
								<th width="1"><input id="check_all" class="js-checkbox-all" type="checkbox" /></th>
								<th>证书主题</th>
								<th>颁发者主题</th>
								<th>生效时间</th>
								<th>失效时间</th>
								<th>密钥用法</th>
								<th>导入时间</th>
							</tr>
						  </thead>
						  <tbody>
							<c:forEach items="${page.result}" var="key">
								<c:choose>
									<c:when test="${key.sealMac == true}">
										<tr>
											<th width="1"><input name="checkboxt" id="scKid" class="input_checked" type="checkbox" value="${key.id }"></th>
											<td width="20%">${key.certDn }</td>
											<td width="25%">${key.certIssueDn }</td>
											<td width="15%">${key.notBeforCn }</td>
											<td width="15%">${key.notAfterCn }</td>
											<td width="10%">${key.certUsageCn }</td>
											<td width="14%">${key.updateTimeCn }</td>
										</tr>
									</c:when>
									<c:when test="${key.sealMac == false}">
										<tr>
											<th width="1"><input name="checkboxt" id="scKid" class="input_checked" type="checkbox" value="${key.id }"></th>
											<td class="errorMac" width="20%">${key.certDn }</td>
											<td class="errorMac" width="25%">${key.certIssueDn }</td>
											<td class="errorMac" width="15%">${key.notBeforCn }</td>
											<td class="errorMac" width="15%">${key.notAfterCn }</td>
											<td class="errorMac" width="10%">${key.certUsageCn }</td>
											<td class="errorMac" width="14%">${key.updateTimeCn }</td>
										</tr>
									</c:when>
								</c:choose>
							</c:forEach>
						  </tbody>
						</table>
					<div class="text-right" id="serverCertPage"></div>
					</div>
				</div>
			</div>
		</div>
		</div>
	</section>
  </div>	
</div>

<script type="text/javascript">
	$(function() {
		inputTipText(); //初始化Input的灰色提示信息
		$(".artic").hide();
		
		//---复选框样式
		icheck(".js-checkbox-all");
		
		//分页
		laypage({
 			cont : 'serverCertPage',
 			skip : true,//跳转页面选项
 			pages : '${page.totalPage}', //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
			curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
 				var pageNo = '${page.pageNo}'; // 当前页(后台获取到的)
 				return pageNo ? pageNo : 1; // 返回当前页码值
 			}(),
 			jump : function(e, first) { //触发分页后的回调
 				if (!first) { //一定要加此判断，否则初始时会无限刷新
 					loadUrl(showPage("tab1","${ctx }/key/keyJksList.do?pageNo=" + e.curr));
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
	$(".requestCert").click(function() {
		$.ajax({
			type : "get",
			dataType : "json",
			url : "${ctx }/key/keyJksCsr.do",
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
					$(".requestCert").attr("disabled", "disabled");
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

	//导入证书
	$(".importCert").click(function() {
		ids = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					ids = $(this).val();
				}
				index++;
			}
		});
		if(index == 0){
			layer.alert( "请选择导入证书对象",{icon:0});
			return;
		} 
		
		$("#importId").val(ids);
		
		var value = $("[name=certFile]:file").val();
		if (value == "") {
			layer.alert( "请先选择文件", {icon:0});
			return;
		}

		var form = $("#addCertChainForm");
		form.ajaxSubmit({
			success : function(data) { 
				if (data == "ok") {
					layer.alert( "导入成功", {icon:1});
					$(".importCert").attr("disabled", "disabled");
					loadUrl("${ctx }/key/keyList.do");
				} else{
					layer.alert( data, {icon:2});
					loadUrl("${ctx }/key/keyList.do");
				}
			},
			error : function() {
				layer.alert( "导入失败", {icon:2});
				loadUrl("${ctx }/key/keyList.do");
			}
		});
	});
	
	//删除密钥
	function delKey(){
		ids = "";
		var index = 0;
		$("[id='scKid']:checkbox").each(function(){
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
				url : "${ctx }/key/delKey.do",
				type : "get",
				data : "id=" + ids,
				dataType : "json",
				success : function(data) {
					if (data.success){
						layer.alert( data.message, {icon:1});
						loadUrl("${ctx }/key/keyList.do");
					}else{
						layer.alert( data.message, {icon:2});
						loadUrl("${ctx }/key/keyList.do");
					}
					
				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
					loadUrl("${ctx }/key/keyList.do");
				}
			}); 
		});
	}
	
	//密钥恢复
	function recoveryKey(){
		loadTab("${ctx }/key/toRecoveryKey.do", "tab1");
	}
	
	//密钥下载
	function downloadKey(){
		var ids = "";
		var index = 0;
		$("[id='scKid']:checkbox").each(function(){
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
		
		var value = confirm("确定下载密钥?");
		if(!value){
			return false;
		}
		
		loadTab("${ctx }/key/toDownloadKey.do?id=" + ids, "tab1");
		
	}
	
	// 证书下载
	function downLoadKeyCert(){
		var ids = "";
		var index = 0;
		$("[id='scKid']:checkbox").each(function(){
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
					loadUrl("${ctx }/key/keyList.do");
				}
				
			},
			error : function() {
				layer.alert( "请求失败", {icon:2});
				loadUrl("${ctx }/key/keyList.do");
			}
		}); 
	}
	
</script>