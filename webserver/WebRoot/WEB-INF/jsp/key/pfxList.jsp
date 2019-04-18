<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
				<div class="x_panel">
					<div class="x_title" id="title1">
				  		<div class="col-xs-12">
				  			<div class="col-xs-4">
						  		<input type="button" value="恢复" onclick="recoveryPfxKey()" class="btn btn-primary" id="recovery" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm7"/>
						  		<input type="button" onclick="downloadKey()" value="备份下载" class="btn btn-primary" id="download" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm11"/>	
						  		<input type="button" value="删除"  onclick="delPfxKey()"  class="btn btn-primary" id="delete" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm12"/>
						  		<input type="button" value="证书下载"  onclick="downLoadKeyCert()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm12"/>					  									  		
				  			</div>
				  			<div class="col-xs-2"></div>
				  			<div class="col-xs-6">
					  			<form id="addCertChainPfxForm" class="form-horizontal" action="${ctx }/key/keyPfxImpCert.do" 
									onkeydown="if(event.keyCode==13){return false;}" method="post" enctype="multipart/form-data">
									<div class="col-xs-4" >
						  				<input id="certChain" name="certFilePfx" type="file" class="form-control input-sm col-xs-2" style="cursor: pointer;"/>
						  			</div>
						  			<div class="col-xs-2" style="text-align: right;">
						  				<label for="pwds" style="margin-top: 5px;">文件密码:</label>
						  			</div>
									<div class="col-xs-4 form-group">
										<input id="pwds" type="password" name="pwd"  maxlength="15" class="form-control input-sm col-xs-1" style="font-size:16px;"/>
							  		</div>	
						  			<div class="col-xs-2" >
						  				<input type="button" value="导入证书" class="btn btn-primary importCertPfx"/>
						  			</div>
							    </form>
						    </div>
						</div>
					<div class="clearfix"></div>
				  </div>
				  	<div class="x_content">
						<div id="table-box" style="display: block;">
							<table id="datatable-responsive1" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
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
												<th width="1"><input name="checkboxt" id="spKid" class="input_checked" type="checkbox" value="${key.id }"></th>
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
												<th width="1"><input name="checkboxt" id="spKid" class="input_checked" type="checkbox" value="${key.id }"></th>
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
							<div class="text-right" id="serverCertpfxPage"></div>
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
		
		//---复选框样式
		icheck(".js-checkbox-all");
		
		//分页
		laypage({
 			cont : 'serverCertpfxPage',
 			skip : true,//跳转页面选项
 			pages : '${page.totalPage}', //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
			curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
 				var pageNo = '${page.pageNo}'; // 当前页(后台获取到的)
 				return pageNo ? pageNo : 1; // 返回当前页码值
 			}(),
 			jump : function(e, first) { //触发分页后的回调
 				if (!first) { //一定要加此判断，否则初始时会无限刷新
 					loadUrl(showPage("tab2","${ctx }/key/keyPfxList.do?pageNo=" + e.curr));
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

	$(".importCertPfx").click(function() {
		var value = $("[name=certFilePfx]:file").val();
		if (value == "") {
			layer.alert( "请先选择文件", {icon:0});
			return;
		}
		
		value = $(".grayTips").val();
		if(value == "" || value == "文件密码"){
			layer.alert( "密码不能为空", {icon:0});
			return;
		}

		var form = $("#addCertChainPfxForm");
		form.ajaxSubmit({
			success : function(data) {
				if (data=="ok") {
					layer.alert( "导入成功", {icon:1});
					$(".importCert").attr("disabled", "disabled");
					loadTab("${ctx }/key/keyPfxList.do", "tab2"); // ajax加载页面
				} else{
					layer.alert( data, {icon:2});
					loadTab("${ctx }/key/keyPfxList.do", "tab2"); // ajax加载页面
				}
			},
			error : function() {
				layer.alert( "导入失败",{icon:2});
				loadTab("${ctx }/key/keyPfxList.do", "tab2"); // ajax加载页面
			}
		});
	});
	
	//删除密钥证书
	function delPfxKey(){
		ids = "";
		var index = 0;
		$("[id='spKid']:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					ids = $(this).val();
				}else{
					ids= ids + ";" + $(this).val();
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
					if (data.success) {
						layer.alert( "删除成功", {icon:1});
						loadTab("${ctx }/key/keyPfxList.do", "tab2");
					} else {
						layer.alert( data.message, {icon:2});
						loadTab("${ctx }/key/keyPfxList.do", "tab2");
					}
				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
					loadTab("${ctx }/key/keyPfxList.do", "tab2");
				}
			}); 
		});
	}
	
	
	//密钥恢复
	function recoveryPfxKey(){
		loadTab("${ctx }/key/toRecoveryKey.do", "tab2");
	}
	
	//密钥下载
	function downloadKey(){
		ids = "";
		var index = 0;
		$("[id='spKid']:checkbox").each(function(){
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
		
		loadTab("${ctx }/key/toDownloadKey.do?id="+ids, "tab2");
		
		
	}
	
	// 证书下载
	function downLoadKeyCert(){
		var ids = "";
		var index = 0;
		$("[id='spKid']:checkbox").each(function(){
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