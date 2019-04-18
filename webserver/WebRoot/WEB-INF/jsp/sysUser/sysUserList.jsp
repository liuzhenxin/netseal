<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<style>

.inlineIe {
	display: inline-block;
	display: inline;
	zoom: 1;
}
</style>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">管理员管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    管理员管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			管理员管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="x_panel" id="myTabContent1">
							<div class="x_title clearfix" style="position:relative;" id="topbar">
							
						  		<input type="button" value="添加管理员" onclick="javascript:loadUrl('${ctx }/sysUser/toAddSysUser.do')"  class="btn btn-primary" id="addadmin" style="float:left;"/>
						  		<input onclick="judgeCheckbox('重置该用户密码','${ctx }/sysUser/resetSysUserPwd.do','${ctx }/sysUser/sysUserList.do')" type="button" value="重置密码" class="btn btn-primary" id="resetpwd" style="float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm"/>
						  		<input type="button" value="修改" class="btn btn-primary" id="modSys" style="  float:left; margin-left:10px;"/>
						  		<input type="button" value="解锁" class="btn btn-primary" id="clearSys" style="  float:left; margin-left:10px;"/>
						  		<input onclick="judgeCheckbox('删除该用户','${ctx }/sysUser/delSysUser.do','${ctx }/sysUser/sysUserList.do')" type="button" value="删除" class="btn btn-primary" id="delete" style="  float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm1"/>
						  			
						  		
							  <div class="form-group clearfix" id="input_box" style="float:right; margin-left:10px;">
								  <div class="input-group" style="margin:0px; float:left;">
									<input id="userNameText" type="text" class="form-control select2-search__field" type="search"   value="" style="border-color: rgb(230,233,237); width:240px;">
									<span class="input-group-btn pull-left" style="width:54px;">
									  <button  id="userNameBut" class="btn btn-primary" type="button" style="color:#fff;">搜索</button>
									</span>
								  </div>
								</div>
								
							  
							  <select class="form-control col-xs-3" id="select" style="width:150px; float:right;">
								  <option>姓名</option>
							  </select>
							
							<div class="clearfix"></div>
						  </div>
						  <div class="x_content">
						  
							<div id="table-box" style="display: block;">
								<table id="sysUserTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
								  <thead>
									<tr>
										<th width="1"><input id="check_all" class="js-checkbox-all" type="checkbox" /></th>
										<th>用户名</th>
										<th>姓名</th>
			 							<th>角色</th>
										<th>单位名称</th>
										<th>状态</th>
									</tr>
								  </thead>
								  <tbody>
								  <c:forEach items="${page.result}" var="sysuser">
									<c:choose>
										<c:when test="${sysuser.sealMac == true}">
											<tr>
												<th width="1"><input name="checkboxt" class="input_checked" type="checkbox" value="${sysuser.id }"></th>
												<td><a href="javascript:loadUrl('${ctx }/sysUser/getSysUser.do?id=${sysuser.id }')">${sysuser.account }</a></td>
												<td>${sysuser.name }</td>
												<td>${sysuser.roleName }</td>
												<td>${sysuser.companyName }</td>
												<td>${sysuser.statusCn }</td>
												
											</tr>
										</c:when>
										<c:when test="${sysuser.sealMac == false}">
											<tr>
												<th width="1" style="color: red; font-size: bold;"><input name="checkboxt" class="input_checked" type="checkbox" value="${sysuser.id }"></th>
												<td><a class="errorMac" href="javascript:loadUrl('${ctx }/sysUser/getSysUser.do?id=${sysuser.id }')">${sysuser.account }</a></td>
												<td class="errorMac">${sysuser.name }</td>
												<td class="errorMac">${sysuser.roleName }</td>
												<td class="errorMac">${sysuser.companyName }</td>
												<td class="errorMac">${sysuser.statusCn }</td>
											</tr>
										</c:when>
									</c:choose>
								  </c:forEach>
								  
								  </tbody>
								</table>
								<div class="text-right" id="SysUserPage"></div>
							</div>
							
						  </div>
						</div>
				</div>		
			</div>
		</section>
    </div>
</div>	
	

<script type="text/javascript">

$(function(){
	

	//修改按钮
	$("#modSys").click(function(){
		var index = 0;
		$("[name=checkboxt]:checkbox").each(
			function() {
				if (this.checked) {
					if (index == 0) {
						id = $(this).val();
					} 
					index++;
				}
		});
		
        if (index == 0 || index>1) {
        	layer.alert("请选择要修改的一条记录",{icon:0});
			return;
		} else {
			$("#myTabContent1").load("${ctx }/sysUser/toEditSysUser.do?id="+id);
		}
	});
	
	
	//解锁
	$("#clearSys").click(function(){
		var id = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					id = $(this).val();
				}else{
					id = id + "," + $(this).val();
				}
				index++;
			}
		});
		if(index == 0){
			layer.alert( "请至少选择一条要操作的记录",{icon:0});
			return;
		} 
		
		$.ajax({
			url :"${ctx }/sysUser/clearSysUser.do" ,
			type : "get",
			data : "id=" + id,
			dataType : "json",
			success : function(data) {
				if(data.success){
					layer.alert(data.message,{icon:1});
					loadUrl("${ctx }/sysUser/sysUserList.do");
				}else{
					layer.alert(data.message,{icon:2});
					loadUrl("${ctx }/sysUser/sysUserList.do");
				}
			},
			error : function() {
				layer.alert("请求失败",{icon:2});
			}
		});
		
	});
	
	
	//根据条件搜索
	$("#userNameBut").click(
	function searchAccount() {
		var url = "";
		var requestUserName = $("#userNameText").val();
		//查询条件是姓名 		
		url = "${ctx }/sysUser/sysUserSearch.do";
		$.ajax({
			type : "post",
			dataType : "json",
			data:{"name":requestUserName},
			url : url,
			success : function(jsonResult) {
				$("#sysUserTable tr:not(:first)").remove();
				$("#SysUserPage").empty();
				 $("#userNameText").val("");
				//遍历一个数组or集合
				var ta = jsonResult.page.result;
				if (ta == "") {
					layer.alert( "此条件查询结果为空,请确认查询正确",{icon:5});
				} else {
					$.each(ta,function(i, item) {
						//追加html文本
						if (item.sealMac == true) {
							$("#sysUserTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td><a href='javascript:loadUrl(\"${ctx }/sysUser/getSysUser.do?id="+item.id+"\") ' >"
									+item.account+"</a></td><td name="+item.status+">"+ item.name+ "</td><td name="+ item.roleID+">"+ item.roleName+ "</td><td name="+ item.companyID+">"+ item.companyName+ "</td></tr>");
						} else {
							$("#sysUserTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td><a class='errorMac' href='javascript:loadUrl(\"${ctx }/sysUser/getSysUser.do?id="+item.id+"\") ' >"
									+item.account+"</a></td><td class='errorMac' name="+item.status+">"+ item.name+ "</td><td class='errorMac' name="+ item.roleID+">"+ item.roleName+ "</td><td class='errorMac' name="+ item.companyID+">"+ item.companyName+ "</td></tr>");
						}
						
						
						});
						icheck(".js-checkbox-all");
					/* ----分页--开始 */
					//分页
					 laypage({
						cont : 'SysUserPage',
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
											url : "${ctx }/sysUser/sysUserSearch.do",
											data: {"name":requestUserName,"pageNo":e.curr},
											success : function(jsonResult) {
												$("#sysUserTable tr:not(:first)").remove();
												//遍历一个数组or集合
												var ta = jsonResult.page.result;
												if (ta == "") {
													layer.alert("此条件查询结果为空,请确认查询正确",{icon:5});
												} else {
													$.each(ta,function(i, item) {
														//追加html文本
														if (condition) {
															$("#sysUserTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td>"
																	+"<a href='javascript:loadUrl(\"${ctx }/sysUser/getSysUser.do?id="+item.id+"\") ' >"+item.account+"</a></td><td name="+item.status+">"+ item.name+ "</td><td name="+ item.roleID+">"+ item.roleName+ "</td><td name="+ item.companyID+">"+ item.companyName+ "</td></tr>");
														} else {
															$("#sysUserTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td><a class='errorMac' href='javascript:loadUrl(\"${ctx }/sysUser/getSysUser.do?id="+item.id+"\") ' >"
																	+item.account+"</a></td><td class='errorMac' name="+item.status+">"+ item.name+ "</td><td class='errorMac' name="+ item.roleID+">"+ item.roleName+ "</td><td class='errorMac' name="+ item.companyID+">"+ item.companyName+ "</td></tr>");
														}
														
													});
													icheck(".js-checkbox-all");
												}
											},
											error : function() {
												layer.alert("查询错误",{icon:2});
											}
										});
								}
						}
					});
					/* ---分页---结束 */
					}
			},
			error : function() {
				layer.alert("查询错误",{icon:2});
			}
		});
	});
	

	
})


$(document).ready(
			function() {
				//===分页===
				page('SysUserPage', '${page.totalPage}', '${page.pageNo}',
						"${ctx }/sysUser/sysUserList.do?pageNo=");
				//---复选框样式
				icheck(".js-checkbox-all");
				
			});


	
</script>






