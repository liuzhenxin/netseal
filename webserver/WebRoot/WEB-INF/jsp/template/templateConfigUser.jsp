<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div id="templateConfigUser" class="tab-content" style="margin-top:20px; display:none">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
				  <div class="x_title" id="topbar1">
				  		<input id="selectAllUser" type="button" value="选择全体用户" class="btn btn-primary" style=" float:left;"/>
				  		<input id="submitComButton" type="button" value="确定" class="btn btn-primary"  style=" float:left; margin-left:10px;" />
				  		
				  		<input id="closeCompanyButton" type="button" value="取消" class="btn btn-primary" onclick="shoHide('#templateAddShow','#templateConfigUser','')"  style="float:left; margin-left:10px;"/>
				  		
					  <div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
						  <div class="input-group" style="margin:0px; margin-right:0px; float:left;">
							<input id="searchName" type="text" class="form-control select2-search__field" type="search"  value="" style="border-color: rgb(230,233,237); width:240px;">
							<span class="input-group-btn pull-left" style="width:54px;">
							  <button class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
							</span>
						  </div>
						  <div class="input-group" style="margin:0px; margin-right:20px;  float:left; display: none;">
							<input id="searchConpanyName" type="text" class="form-control select2-search__field" type="search" value="" style="border-color: rgb(230,233,237); width:240px; float:left;">
							<span class="input-group-btn pull-left" style="width:54px;">
							  <button class="btn btn-primary" type="button" style="color:#fff; float:left;" onclick="javascript:searchAccount()" >搜索</button>
							</span>
						  </div>
					</div>
						
					  
					  <select class="form-control" id="select" style="width:150px; float:right;"> 
						  <option>姓名</option>
						  <option>单位名称</option>
					  </select>
					
					<div class="clearfix"></div>
				  </div>
				  <div class="x_content">
					<div id="table-box1" style="">
						<table id="comUserTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
						  <thead>
							<tr>
								<th width="1"><input id="check_all" class="js-checkbox-all" type="checkbox" /></th>
								<th>姓名</th>
								<th>单位名称</th>
							</tr>
						  </thead>
						  <tbody>
							<c:forEach items="${page.result}" var="user">
								<tr>
									<th width="1" name="${user.name }"><input name="checkboxt" class="input_checked" type="checkbox" value="${user.id }"></th>
									<td>${user.name }</td>
									<td>${user.companyName }</td>
								</tr>
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


	$(document).ready(
		function() {
			//---复选框样式
			icheck(".js-checkbox-all");
			
			
		});
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

	}
	
	
	//根据条件搜索
	function searchAccount() {
		var requestComid=$("#companyId").val();
		var firstSearch=$("#searchName").val();
		var secondSearch=$("#searchConpanyName").val();
		console.log(firstSearch +";"+secondSearch+";"+requestComid);
		var url = "";
	    url="${ctx }/template/SearchUserByCompanyId.do";
		$.ajax({
			type : "post",
			dataType : "json",
			data: {"name":firstSearch,"companyName":secondSearch,"cid":requestComid},
			url : url,
			success : function(jsonResult) {
				$("#searchName").val("");
				$("#searchConpanyName").val("");
				$("#comUserTable tr:not(:first)").remove();
				$("#userPage").empty();
				//遍历一个数组or集合
				var ta = jsonResult.page.result;
				if (ta == "") {
					layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
				} else {
					$.each(ta,function(i, item) {
						//追加html文本
						$("#comUserTable tbody").append("<tr><th name="+item.name+"><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td>"
								+ item.name+ "</td><td>"+ item.companyName+ "</td></tr>");
						});
						icheck(".js-checkbox-all");
						//分页
						/* page('userPage',jsonResult.page.totalPage,jsonResult.page.pageNo,"${ctx }/template/SearchUserByCompanyId.do?pageNo="); */
						
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
										url :"${ctx }/template/SearchUserByCompanyId.do",
										data: {"name":firstSearch,"companyName":secondSearch,"cid":requestComid,"pageNo":e.curr},
										success : function(jsonResult) {
											$("#comUserTable tr:not(:first)").remove();
											//遍历一个数组or集合
											var ta = jsonResult.page.result;
											if (ta == "") {
												layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
											} else {
												$.each(ta,function(i, item) {
													//追加html文本
													$("#comUserTable tbody").append("<tr><th name="+item.name+"><input name='checkboxt' class='input_checked' type='checkbox'></th><td>"
															+ item.name+ "</td><td>"+ item.companyName+ "</td></tr>");
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
	
	
	
	/* $(function(){
		$("#submitComButton").click(function() {
			var account = "";
			var index = 0;
			$("[name=checkboxt]:checkbox").each(function(){
				if(this.checked){
					if(index == 0){
						account = $(this).parent().parent("th").attr("name");
					}else{
						account = account + "," + $(this).parent().parent("th").attr("name");
					}
					index++;
				}
			});
			if(index == 0){
				layer.alert( "请至少选择一条要操作的记录", {icon:0});
				return;
			}
			document.getElementById("userListId").value = account;
			shoHide('#templateAddShow','#templateConfigUser','')
		});
		$("#closeCompanyButton").click(function() {
			$('#templateConfigUser').modal('hide');
		});

		

	}); */
</script>