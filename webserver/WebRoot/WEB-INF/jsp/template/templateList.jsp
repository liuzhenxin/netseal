<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
<h2 style="font-size:34px; font-weight:bold;">印模管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">印模管理    /    印模管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			印模管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			  <div class="x_title" id="topbar">
			  		<input id="addTemplate" onclick="addTemplate()" type="button" value="添加" class="btn btn-primary"  style=" float:left;"/>
			  		<input id="startTemplateButton" onclick="modTemplate()" type="button" value="修改" class="btn btn-primary"  style=" float:left;margin-left:10px;"/>
			  		<input type="button" value="启用" class="btn btn-primary" id="startTemplateButton" onclick="startTemplate()" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm"/>
			  		<input type="button" value="停用" class="btn btn-primary" id="stopTemplateButton"  onclick="stopTemplate()" style="float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm11"/>
			  		<input type="button" value="删除" class="btn btn-primary" id="delete"  onclick="judgeCheckbox('删除所选印模','${ctx }/template/delTemplate.do','${ctx }/template/templateList.do')"style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm1"/>
				
				  <div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
					  <div class="input-group" style="margin:0px; margin-right:0px; float:left;">
						<input  id="requestUserName" type="text" class="form-control select2-search__field" type="search"  value="" style="border-color: rgb(230,233,237); width:240px;">
						<span class="input-group-btn pull-left" style="width:54px;">
						  <button class="btn btn-primary" onclick="javascript:searchAccount()" type="button" style="color:#fff;">搜索</button>
						</span>
					  </div>
				 </div>
					
				  
				  <select class="form-control" id="select" style="width:150px; float:right;">
					  <option>印模名称</option>
				  </select>
				
				<div class="clearfix"></div>
			  </div>
			  <div class="x_content">
				<br>
				<div id="templateShow" style="display: block;">
					<table id="templateTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
					  <thead>
						<tr>
							<th width="1"><input id="check_all" class="js-checkbox-all" type="checkbox" /></th>
							<th>印模名称</th>
							<th>单位名称</th>
							<th>印模类型</th>
							<th>状态</th>
							<th>启用日期</th>
							<th>结束日期</th>
							<th>注册日期</th>
						</tr>
					  </thead>
					  <tbody>
				  		<c:forEach items="${page.result}" var="template">
				  			<c:choose>
								<c:when test="${template.sealMac == true}">
									<tr>
										<th width="1"><input name="checkboxt" class="input_checked" type="checkbox" value="${template.id }"></th>
										<td><a href="javascript:loadUrl('${ctx }/template/viewTemplate.do?id=${template.id }')">${template.name }</a></td>
										<td>${template.companyName }</td>
										<td>${template.typeCn }</td>
										<td>${template.statusCn }</td>
										<td>${template.notBeforCn }</td>
										<td>${template.notAfterCn }</td>
										<td>${template.generateTimeCn }</td>
									</tr>
								</c:when>
								<c:when test="${template.sealMac == false}">
									<tr>
										<th width="1"><input name="checkboxt" class="input_checked" type="checkbox" value="${template.id }"></th>
										<td><a class="errorMac" href="javascript:loadUrl('${ctx }/template/viewTemplate.do?id=${template.id }')">${template.name }</a></td>
										<td class="errorMac">${template.companyName }</td>
										<td class="errorMac">${template.typeCn }</td>
										<td class="errorMac">${template.statusCn }</td>
										<td class="errorMac">${template.notBeforCn }</td>
										<td class="errorMac">${template.notAfterCn }</td>
										<td class="errorMac">${template.generateTimeCn }</td>
									</tr>
								</c:when>
							</c:choose>
						</c:forEach>
					  </tbody>
					</table>
					 <div class="text-right" id="templatePage"></div>
				</div>
				  </div>
				</div>
			  </div>
			</div>
		</section>
	</div>
</div>	




<script type="text/javascript">
$(document).ready(
	function() {
	//===分页===
	page('templatePage', '${page.totalPage}', '${page.pageNo}',"${ctx }/template/templateList.do?pageNo=");
	//---复选框样式
	icheck(".js-checkbox-all");
	
	
});

//根据条件搜索
function searchAccount() {
	var url = "";
	var requestUserName =$("#requestUserName").val();
	
	//查询条件是用户名 
	url = "${ctx }/template/templateSearch.do";
	$.ajax({
		type : "post",
		dataType : "json",
		data: {"name":requestUserName},
		url : url,
		success : function(jsonResult) {
		$("#requestUserName").val("");	
		$("#templateTable tr:not(:first)").remove();
		$("#templatePage").empty();
		//遍历一个数组or集合
		var ta = jsonResult.page.result;
		if (ta == "") {
			layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
		} else {
			$.each(ta,function(i, item) {
				//追加html文本
				if (item.sealMac == true) {
					$("#templateTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
							+"'></th><td><a href="+'javascript:loadUrl('+"'${ctx }/template/viewTemplate.do?id="+item.id+"') >"
							+item.name+"</a></td><td>"+item.companyName+"</td><td>"+item.typeCn+"</td><td>"+item.statusCn
							+"</td><td>"+item.notBeforCn+"</td><td>"+item.notAfterCn+"</td><td>"+item.generateTimeCn+ "</td></tr>");
				} else {
					$("#templateTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
							+"'></th><td><a class='errorMac' href="+'javascript:loadUrl('+"'${ctx }/template/viewTemplate.do?id="+item.id+"') >"
							+item.name+"</a></td><td class='errorMac'>"+item.companyName+"</td><td class='errorMac'>"+item.typeCn+"</td><td class='errorMac'>"+item.statusCn
							+"</td><td class='errorMac'>"+item.notBeforCn+"</td><td class='errorMac'>"+item.notAfterCn+"</td><td class='errorMac'>"+item.generateTimeCn+ "</td></tr>");
				}
				
				});
				icheck(".js-checkbox-all");
						/* ----分页--开始 */
						//分页
						 laypage({
							cont : 'templatePage',
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
												url : "${ctx }/template/templateSearch.do",
												data: {"name":requestUserName,"pageNo":e.curr},
												success : function(jsonResult) {
													$("#templateTable tr:not(:first)").remove();
													//遍历一个数组or集合
													var ta = jsonResult.page.result;
													if (ta == "") {
														layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
													} else {
														$.each(ta,function(i, item) {
															//追加html文本
															$("#templateTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
																	+"'></th><td><a href="+'javascript:loadUrl('+"'${ctx }/template/viewTemplate.do?id="+item.id+"') >"
																	+item.name+"</a></td><td>"+item.companyName+"</td><td>"+item.typeCn+"</td><td>"+item.statusCn
																	+"</td><td>"+item.notBeforCn+"</td><td>"+item.notAfterCn+"</td><td>"+item.generateTimeCn+ "</td></tr>");
															});															icheck(".js-checkbox-all");
														}
												},
												error : function() {
													layer.alert( "查询错误", {icon:2});
												}
											});
									}
							}
						}); 
						/* ---分页---结束 */
						}
				},
				error : function() {
					layer.alert( "查询错误", {icon:2});
				}
			});
}


//启用印模
function startTemplate() {
	
	var ids = "";
	$("[name=checkboxt]:checkbox:checked").each(function() {
		ids += $(this).val() + ",";
	});
	if (ids == "") {
		layer.alert( "请选择印模启用", {icon:0});
		return;
	}
	$.ajax({
		url : "${ctx }/template/updateTemplateStatus.do",
		type : "get",
		data : "id=" + ids + "&status=1",
		dataType : "json",
		success : function(data) {
			if (data.success) {
				layer.alert( "操作成功", {icon:1});
				loadUrl("${ctx }/template/templateList.do");
			} else {
				layer.alert( data.message, {icon:2});
			}

		},
		error : function() {
			layer.alert( "请求失败", {icon:2});
		}
	});
};

//印模停用方法
function stopTemplate()  {
	var ids = "";
	$("[name=checkboxt]:checkbox:checked").each(function() {
		ids += $(this).val() + ",";
	});
	if (ids == "") {
		layer.alert( "请选择印模停用", {icon:0});
		return;
	}
	$.ajax({
		url : "${ctx }/template/updateTemplateStatus.do",
		type : "get",
		data : "id=" + ids + "&status=0",
		dataType : "json",
		success : function(data) {
			if (data.success) {
				layer.alert( "操作成功", {icon:1});
				loadUrl("${ctx }/template/templateList.do");
			} else {
				layer.alert( data.message, {icon:2});
			}

		},
		error : function() {
			layer.alert( "请求失败", {icon:2});
		}
	});
};

//添加  /template/toAddTemplate.do
function addTemplate(){
	loadUrl("${ctx }/template/toAddTemplate.do");
}
//修改
function modTemplate() {
	var id = -1;
	var index = 0;
	$("[name=checkboxt]:checkbox").each(function() {
		if (this.checked) {
			index++;
		}
	});

	if (index == 0 || index > 1) {
		layer.alert( "请选择要修改的一条记录", {icon:0});
		
		return;
	}else{
		$("[name=checkboxt]:checkbox").each(function() {
			if (this.checked) {
				id=	$(this).val();
				loadUrl("${ctx }/template/toEditTemplate.do?id="+id);
			}
		});
		
		
	}
}






/* 
 function updateTemplate(id,status){
	if(status==1){
		status=0;
	}else if(status==0){
		status=1;
	}
	$.ajax({
	    url:"${ctx }/template/updateTemplateStatus.do",
	    type:"get",
	    data:"id="+id+"&status="+status,
	    dataType:"json",
	    success:function(data){
	        if(data.success){
	        	swal("",data.message,"success");
	        	loadUrl("${ctx }/template/templateList.do");
	        }
	    	
	    },error:function(){
	    	swal("",data.message,"success");
	    }
	 });
	
}  */
</script>
