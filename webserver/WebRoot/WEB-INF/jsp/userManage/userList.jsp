<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">签章人管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    签章人管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			签章人管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
				<div class="x_title" id="x_title">
			  		<input type="button" value="添加用户" class="btn btn-primary" id="addUser" style=" float:left;"/>
		  		<input type="button" value="印章申请" class="btn btn-primary" id="modSys" onclick="requestSeal()" style=" float:left; margin-left:10px;" />
		  		<input type="button" value="修改" class="btn btn-primary" id="modSys" onclick="moduser()" style=" float:left; margin-left:10px;"/>
		  		<input type="button" value="删除" class="btn btn-primary" id="delete"  onclick="judgeCheckbox('删除该用户以及该用户下的印章和证书吗','${ctx }/userManage/delUser.do','${ctx }/userManage/userList.do');"  style=" float:left; margin-left:10px;" />
		  		<input type="button" value="证书申请下载" class="btn btn-primary" id="requestCert" style=" float:left; margin-left:10px;" />
				
				<div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
				  <div class="input-group" style="margin:0px; margin-right:0px; float:left;">
					<input  id="searchName" name="searchUserText" type="text" class="form-control select2-search__field" type="search"   value="" style="border-color: rgb(230,233,237); width:240px;">
					<span class="input-group-btn pull-left" style="width:54px;">
					  <button  name="searchUser" class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
					</span>
				  </div>
				  <div class="input-group" style="margin:0px; margin-right:20px;  float:left; display: none;">
					<input id="searchDn" name="searchText" type="text" class="form-control select2-search__field" type="search"  value="" style="border-color: rgb(230,233,237); width:240px; float:left;">
					<span class="input-group-btn pull-left" style="width:54px;">
					  <button  name="searchUser" class="btn btn-primary" type="button" style="color:#fff; float:left;" onclick="javascript:searchAccount()">搜索</button>
					</span>
				  </div>
				</div>
				
			  
			  <select class="form-control" id="select" style="width:150px; float:right;">
				  <option>姓名</option>
				  <option>证书主题</option>
			  </select>
				<div class="clearfix"></div>
		  </div>
		  
		  <div class="x_content">
			<br>
			<div id="table-box" style="display: block;">
				<table id="userTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
				  <thead>
					<tr>
						<th width="1"><input id="check_all" class="input_checked js-checkbox-all" type="checkbox" /></th>
						<th width="18%">姓名</th>
						<th width="18%">单位名称</th>
						<th width="35%">证书主题</th>
						<th width="10%">密钥用法</th>
						<th width="18%">注册时间</th>
					</tr>
				  </thead>
				  <tbody>
				  	<c:forEach items="${page.result}" var="user">
				  		<c:choose>
							<c:when test="${user.sealMac == true}">
								<tr>
									<th width="1">
										<input name="checkboxt" class="input_checked" type="checkbox" value="${user.id }">
									</th>
									<td width="18%" style="word-break:break-all;">${user.name }</td>
									<td width="18%" style="word-break:break-all;" >${user.companyName }</td>
									<td width="35%" style="word-break:break-all;">${user.certDn }</td>
									<td width="10%" style="word-break:break-all;">${user.certUsageCN }</td>
									<td width="18%" style="word-break:break-all;">${user.generateTimeCn }</td>
								 </tr>
							</c:when>
							<c:when test="${user.sealMac == false}">
								<tr>
									<th width="1">
										<input name="checkboxt" class="input_checked" type="checkbox" value="${user.id }">
									</th>
									<td width="18%" class="errorMac" style="word-break:break-all;">${user.name }</td>
									<td width="18%" class="errorMac" style="word-break:break-all;" >${user.companyName }</td>
									<td width="35%" class="errorMac" style="word-break:break-all;">${user.certDn }</td>
									<td width="10%" class="errorMac" style="word-break:break-all;">${user.certUsageCN }</td>
									<td width="18%" class="errorMac" style="word-break:break-all;">${user.generateTimeCn }</td>
								 </tr>
							</c:when>
						</c:choose>
					 </c:forEach>
						  </tbody>
						</table>
						<div class="text-right" id="userPage"></div>
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
			
	/*  validate("#addUserForm");   */
	//===分页===
	page('userPage', '${page.totalPage}', '${page.pageNo}',"${ctx }/userManage/userList.do?pageNo=");
	//---复选框样式
	icheck(".js-checkbox-all");
	//条件框js
	var oS = document.getElementById('select');
	var oB = document.getElementById('input_box');
	var aO = oS.children;
	var aI = oB.getElementsByTagName('div');
	
	oS.onchange = function(){
		for(var i=0;i<aO.length;i++){
			var selectValue = $('#select').val();
			if(selectValue==aO[i].value){
				for(var j=0;j<aI.length;j++){
					aI[j].style.display = 'none';
				}
				aI[aO[i].index].style.display = 'block';
			}
		}

	};
    
   //添加按钮 
   $("#addUser").click(function(){
	   loadUrl("${ctx }/userManage/toAddUser.do");
   }); 
   $("#requestCert").click(function(){
	   var id = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function() {
			if (this.checked) {
				if (index == 0) {
					id = $(this).val();
				}
				index++;
			}
		});
		
		if (index == 0 || index>1) {
			layer.alert( "请选择一条记录",{icon:0});
			return;
		}else{
			loadUrl("${ctx }/userCertReuqest/toRequestCert.do?userId="+id);
		}
   }); 
    
});


//根据条件搜索
function searchAccount() {
	var url = "${ctx }/userManage/userSearch.do";
	var firstSearch = $("#searchName").val();
	var secondSearch =$("#searchDn").val();
	$.ajax({
		type : "post",
		dataType : "json",
		data: {"name":firstSearch,"certDn":secondSearch},  
		url : url,
		success : function(jsonResult) {
			$("#userTable tr:not(:first)").remove();
			$("#userPage").empty();
			$("#searchName").val("");
			$("#searchDn").val("");
			//遍历一个数组or集合
			var ta = jsonResult.page.result;
			if (ta == "") {
				layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
			} else {
				$.each(ta,function(i, item) {
					//追加html文本
					if (item.sealMac == true) {
						$("#userTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
								+"'></th><td>" + item.name + "</td><td name=" + item.companyID+">"+ item.companyName + "</td><td>"+ item.certDn
								+ "</td><td>"+ item.certUsageCN+"</td><td>"+item.generateTimeCn + "</td></tr>");
					} else {
						$("#userTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
								+"'></th><td class='errorMac'>" + item.name + "</td><td class='errorMac' name=" + item.companyID+">"+ item.companyName + "</td><td class='errorMac'>"+ item.certDn
								+ "</td><td class='errorMac'>"+ item.certUsageCN + "</td><td class='errorMac'>"+ item.generateTimeCn + "</td></tr>");
					}
					
					});
					icheck(".js-checkbox-all");
				/* ----分页--开始 */
				//分页
				 laypage({
					cont : 'userPage',
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
										url : "${ctx }/userManage/userSearch.do",
										data: {"name":firstSearch,"certDn":secondSearch,"pageNo":e.curr},
										success : function(jsonResult) {
											$("#userTable tr:not(:first)").remove();
											//遍历一个数组or集合
											var ta = jsonResult.page.result;
											if (ta == "") {
												layer.alert( "此条件查询结果为空,请确认查询正确",{icon:0});
											} else {
												$.each(ta,function(i, item) {
													//追加html文本
													if (item.sealMac == true) {
														$("#userTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
																+"'></th><td>" + item.name + "</td><td name=" + item.companyID+">"+ item.companyName + "</td><td>"+ item.certDn
																+ "</td><td>"+item.certUsageCN+"</td><td>"+ item.generateTimeCn + "</td></tr>");
													} else {
														$("#userTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id
																+"'></th><td class='errorMac'>" + item.name + "</td><td class='errorMac' name=" + item.companyID+">"+ item.companyName + "</td><td class='errorMac'>"+ item.certDn
																+"</td><td class='errorMac'>"+ item.certUsageCN + "</td><td class='errorMac'>"+ item.generateTimeCn + "</td></tr>");
													}
													
													});
													icheck(".js-checkbox-all");
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
	
	//修改
	function moduser() {
		var id = "";
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
			layer.alert( "请选择要修改的一条记录", {icon:0});
			//$("#useradd,#useredit").hide();
			//$("#userShow").show();
			return;
		}else{
			//$("#useredit").load("${ctx }/userManage/toEditUser.do?id="+id);
			loadUrl("${ctx }/userManage/toEditUser.do?id="+id);
			//console.log(1111);
			//shoHide('#useredit', '#userShow', '#useradd');
			
		}

	}
	//申请印章
	function requestSeal() {
		var id = "";
		var index = 0;
		var certUsage = "";
		$("[name=checkboxt]:checkbox").each(
				function() {
					if (this.checked) {
						if (index == 0) {
							id = $(this).val();
							certUsage = $(this).parent().nextAll().eq(3).html();
						}
						index++;
					}
				});
		
		if (index == 0 || index>1) {
			layer.alert( "请选择要申请印章的一条记录",{icon:0});
			//$("#useradd,#useredit").hide();
			//$("#userShow").show();
			return;
		}else if(certUsage.indexOf("签名") == -1){
			layer.alert( "该用户没有签名证书",{icon:0});
			return;
		}else{
			loadUrl("${ctx }/userManage/toRequestSeal.do?userId="+id);
					
		}

	}
	
</script>

