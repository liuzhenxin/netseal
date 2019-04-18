<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

			<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">授信证书配置</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;">系统管理    /    授信证书配置</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
						授信证书配置</a>
				</li>
			</ul>
			
			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
							  <div class="x_title clearfix">
							  		<form id="addCertChainForm" class="form-horizontal pull-left" action="${ctx }/system/certChain/addCertChain.do" 
											onkeydown="if(event.keyCode==13){return false;}" method="post" enctype="multipart/form-data">
						  			
										<div class="col-xs-8 pull-left">
										  <input id="certChain" name="certChain" type="file" class="form-control input-sm col-xs-8" style="cursor: pointer;">
										</div>
								  		<input type="button" value="导入证书" class="btn btn-primary" id="addCertChain" style=" float:left; margin-left:10px;"/>
							  		
							  		</form>
							  		<input type="button" value="删除" class="btn btn-primary" id="delCert"  onclick="judgeCheckbox2('删除所选证书','${ctx }/system/certChain/delCert.do');" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm9"/>
							  		<div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
									  <div class="input-group" style="margin:0px; margin-right:0px; float:right;">
										<input id="sel_menu1" type="text" class="form-control select2-search__field" type="search"   value="" style="border-color: rgb(230,233,237); width:240px;">
										<span class="input-group-btn pull-left" style="width:54px;">
										  <button class="btn btn-primary" id="sel_menu2" type="button" style="color:#fff;">搜索</button>
										</span>
									  </div>
									</div>
									
								  
								  <select class="form-control" id="select" style="width:150px; float:right;">
									  <option>证书主题</option>
								  </select>
								  
								  <div class="clearfix"></div>
							  </div>
							  
							  <div class="x_content">
								<br>
								<div id="table-box" style="display: block;">
									<table id="certlinkconfigT" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
									  <thead>
										<tr>
											<th width="1"><input id="check_all" class="js-checkbox-all" type="checkbox" /></th>
											<th>证书主题</th>
											<th>颁发者主题</th>
											<th>生效时间</th>
											<th>失效时间</th>
											<th>导入时间</th>
										</tr>
									  </thead>
									  <tbody>
									 	 <c:forEach items="${page.result}" var="certChain">
									 	 	<c:choose>
												<c:when test="${certChain.sealMac == true}">
													<tr>
														<th width="1">
															<input name="checkboxt" class="input_checked" type="checkbox" value="${certChain.id }" />
														</th>
														<td width="30%">${certChain.certDn }</td>
														<td width="25%">${certChain.certIssueDn }</td>
														<td width="15%">${certChain.notBeforCn }</td>
														<td width="15%">${certChain.notAfterCn }</td>
														<td width="15%">${certChain.generateTimeCn }</td>
												    </tr>
												</c:when>
												<c:when test="${certChain.sealMac == false}">
													<tr>
														<th width="1">
															<input name="checkboxt" class="input_checked" type="checkbox" value="${certChain.id }" />
														</th>
														<td class="errorMac" width="30%">${certChain.certDn }</td>
														<td class="errorMac" width="25%">${certChain.certIssueDn }</td>
														<td class="errorMac" width="15%">${certChain.notBeforCn }</td>
														<td class="errorMac" width="15%">${certChain.notAfterCn }</td>
														<td class="errorMac" width="15%">${certChain.generateTimeCn }</td>
												    </tr>
												</c:when>
											</c:choose>
										  </c:forEach>
									  </tbody>
									</table>
									<div class="text-right" id="certChainPage"></div>
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
		
		page('certChainPage', '${page.totalPage}', '${page.pageNo}',"${ctx }/system/certChain/certChainConfig.do?pageNo=");
		//---复选框样式
		icheck(".js-checkbox-all");
		//条件查询
		$("#sel_menu2").click(
				function searchAccount() {
					var url = "";
					var requestUserName =$("#sel_menu1").val();
					//查询条件是用户名 
					url = "${ctx }/system/certChain/certChainConfigSearch.do";
					$.ajax({
								type : "post",
								dataType : "json",
								data: {"certDn":requestUserName},
								url : url,
								success : function(jsonResult) {
									$("#certlinkconfigT tr:not(:first)").remove();
									$("#certChainPage").empty();
									$("#sel_menu1").val("");
									//遍历一个数组or集合
									var ta = jsonResult.page.result;
									if (ta == "") {
										layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
									} else {
										$.each(ta,function(i, item) {
											//追加html文本
											if (item.sealMac == true) {
												$("#certlinkconfigT tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td width='30%'>"
														+item.certDn+"</td><td width='25%'>"+ item.certIssueDn+ "</td><td width='15%'>"+ item.notBeforCn+ "</td><td width='15%'>"+ item.notAfterCn+ "</td><td width='15%'>"+ item.generateTimeCn+ "</td></tr>");
											} else {
												$("#certlinkconfigT tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td class='errorMac' width='30%'>"
														+item.certDn+"</td><td class='errorMac' width='25%'>"+ item.certIssueDn+ "</td><td class='errorMac' width='15%'>"+ item.notBeforCn+ "</td><td class='errorMac' width='15%'>"+ item.notAfterCn+ "</td><td class='errorMac' width='15%'>"+ item.generateTimeCn+ "</td></tr>");
											}
											
											
											});
											icheck(".js-checkbox-all");
										/* ----分页--开始 */
										//分页
										 laypage({
											cont : 'certChainPage',
											skip : true,//跳转页面选项
											pages : jsonResult.page.totalPage, //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
											
											curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
												var pageNo = jsonResult.page.pageNo; // 当前页(后台获取到的)
												return pageNo ? pageNo : 1; // 返回当前页码值
											}(),
											jump : function(e, first) { //触发分页后的回调  
													if (!first) { //一定要加此判断，否则初始时会无限刷新
															$.ajax({
																type : "post",
																dataType : "json",
																url : "${ctx }/system/certChain/certChainConfigSearch.do",
																data: {"certDn":requestUserName,"pageNo":e.curr},
																success : function(jsonResult) {
																	$("#certlinkconfigT tr:not(:first)").remove();
																	//遍历一个数组or集合
																	var ta = jsonResult.page.result;
																	if (ta == "") {
																		layer.alert( "此条件查询结果为空,请确认查询正确",  {icon:0});
																	} else {
																		$.each(ta,function(i, item) {
																			//追加html文本
																			if (item.sealMac == true) {
																				$("#certlinkconfigT tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td width='30%'>"
																						+item.certDn+"</td><td width='25%'>"+ item.certIssueDn+ "</td><td width='15%'>"+ item.notBeforCn+ "</td><td width='15%'>"+ item.notAfterCn+ "</td><td width='15%'>"+ item.generateTimeCn+ "</td></tr>");
																			} else {
																				$("#certlinkconfigT tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td class='errorMac' width='30%'>"
																						+item.certDn+"</td><td class='errorMac' width='25%'>"+ item.certIssueDn+ "</td><td class='errorMac' width='15%'>"+ item.notBeforCn+ "</td><td class='errorMac' width='15%'>"+ item.notAfterCn+ "</td><td class='errorMac' width='15%'>"+ item.generateTimeCn+ "</td></tr>");
																			}
																			
																			
																			});
																			icheck(".js-checkbox-all");  
																		}
																},
																error : function() {
																	layer.alert( "查询错误",  {icon:2});
																}
															});
													}
											}
										});
										/* ---分页---结束 */
										}
								},
								error : function() {
									layer.alert( "查询错误",  {icon:2});
								}
							});
				}
		
		)
		
		//导入证书
		$("#addCertChain").click(function(){
			//判断是否有选择证书(链)
			if($("#certChain").val()){
				var form=$("#addCertChainForm");
				 form.ajaxSubmit({
					 success:function(data){
					     if(data.indexOf("ok")!=-1){		
					    	layer.alert("导入成功",{icon:1});
					    	loadUrl("${ctx }/system/certChain/certChainConfig.do");
					      }else {		
					    	layer.alert(data,{icon:2});
					    	loadUrl("${ctx }/system/certChain/certChainConfig.do");
						  }
					},error:function(){
				    	layer.alert(data,{icon:2});
				    	loadUrl("${ctx }/system/certChain/certChainConfig.do");
				    }
				 });
			}else{
				layer.alert("请选择证书(链)",{icon:2});
			}
		});
		
		
	});
	
	//删除
	function judgeCheckbox2(title,url) {
	ids = "";
	var index = 0;
	$("[name=checkboxt]:checkbox").each(function(){
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
		layer.alert( "请至少选择一条要操作的记录",{icon:0});
		return;
	} 
	//---开始之前的判断结束--可以单独抽取出来
	layer.confirm("确定要"+title+"?",{btn:["确定","取消"]},function(){
				$.ajax({
					url : url,
					type : "get",
					data : "id=" + ids,
					dataType : "json",
					success : function(data) {
						if(data.success){
							layer.alert(data.message,{icon:1});
							loadUrl("${ctx }/system/certChain/certChainConfig.do");
						}else{
							layer.alert(data.message,{icon:2});
							loadUrl("${ctx }/system/certChain/certChainConfig.do");
						}
					},
					error : function() {
						layer.alert("请求失败",{icon:2});
					}
				});
			});
		
	} 

	
</script>