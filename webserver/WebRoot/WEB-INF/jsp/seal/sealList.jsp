<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<!-- Main content -->
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">印章管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="breadcrumbSeal">印章管理    /    印章管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			印章管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
				  <div class="x_title" id="x_title">
				  		<input onclick="sealDownLoadView()" type="button" value="印章下载" class="btn btn-primary" id="sealDownload" style=" float:left;"/>
				  		<input onclick="startSeal()" type="button" value="启用" class="btn btn-primary" id="start" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm"/>
				  		<input onclick="stopSeal()" type="button" value="停用" class="btn btn-primary"  style="float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm11" id="stop"/>
				  		<input onclick="moduser()" type="button" value="修改" class="btn btn-primary" id="revise" style=" float:left; margin-left:10px;"/>
				  		
				  		<input onclick="delSeal()" type="button" value="删除" class="btn btn-primary" id="delete" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm1"/>
				  		
					
					  <div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
						  <div class="input-group" style="margin:0px; margin-right:0px; float:left;">
							<input id="searchName" type="text" class="form-control select2-search__field" type="search"   value="" style="border-color: rgb(230,233,237); width:240px;">
							<span class="input-group-btn pull-left" style="width:54px;">
							  <button class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
							</span>
						  </div>
						  <div class="input-group" style="margin:0px; margin-right:20px;  float:left; display: none;">
							<input id="searchUserAccount" type="text" class="form-control select2-search__field" type="search"  value="" style="border-color: rgb(230,233,237); width:240px; float:left;">
							<span class="input-group-btn pull-left" style="width:54px;">
							  <button class="btn btn-primary" type="button" style="color:#fff; float:left;" onclick="javascript:searchAccount()">搜索</button>
							</span>
						  </div>
						  <div class="input-group" style="margin:0px; margin-right:20px;  float:left; display: none;">
							<input id="searchCerDn" type="text" class="form-control select2-search__field" type="search" value="" style="border-color: rgb(230,233,237); width:240px; float:left;">
							<span class="input-group-btn pull-left" style="width:54px;">
							  <button class="btn btn-primary " type="button" style=" color:#fff; float:left;" onclick="javascript:searchAccount()">搜索</button>
							</span>
						  </div>
						</div>
						
					  
					  <select class="form-control" id="select" style="width:150px; float:right;">
						  <option>印章名称</option>
						  <option>签章人</option>
						  <option>证书主题</option>
					  </select>
					
					<div class="clearfix"></div>
				  </div>
				  <div class="x_content">
					<br>
					<div id="table-box" style="display: block;">
						<table id="sysUserTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
						  <thead>
							<tr>
								<th width="1"><input id="checkboxChange" class="js-checkbox-all" type="checkbox"></th>
								<th>印章名称</th>
								<th>签章人</th>
								<th>印章类型</th>
								<th>证书主题</th>
								<th>状态</th>
								<th>最大签章次数</th>
								<th>生成时间</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.result}" var="seal">
								<c:choose>
									<c:when test="${seal.sealMac == true}">
										<tr>
											<th width="1"><input id="id" name="checkboxt" class="input_checked" type="checkbox" value="${seal.id }"></th>
											<td><a href="javascript:loadUrl('${ctx }/sealManage/sealShow.do?id=${seal.id }')">${seal.name }</a></td>
											<td>${seal.userName }</td>
											<td>${seal.typeCn }</td>
											<td>${seal.certDn }</td>
											<td>${seal.statusCn }</td>
											<td>${seal.usedLimitCn }</td>
											<td>${seal.generateTimeCn }</td>
										</tr>
									</c:when>
									<c:when test="${seal.sealMac == false}">
										<tr>
											<th width="1"><input id="id" name="checkboxt" class="input_checked" type="checkbox" value="${seal.id }"></th>
											<td><a class="errorMac" href="javascript:loadUrl('${ctx }/sealManage/sealShow.do?id=${seal.id }')">${seal.name }</a></td>
											<td class="errorMac">${seal.userName }</td>
											<td class="errorMac">${seal.typeCn }</td>
											<td class="errorMac">${seal.certDn }</td>
											<td class="errorMac">${seal.statusCn }</td>
											<td class="errorMac">${seal.usedLimitCn }</td>
											<td class="errorMac">${seal.generateTimeCn }</td>
										</tr>
									</c:when>
								</c:choose>
							</c:forEach>
						 </tbody>
						</table>
						<div class="text-right" id="sealtPage"></div>
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
		
		
		//===分页===
		page('sealtPage', '${page.totalPage}', '${page.pageNo}',"${ctx }/sealManage/sealList.do?pageNo=");
		//---复选框样式
		icheck(".js-checkbox-all");
		
		});
		//印章启用方法
		function startSeal() {
			var ids = "";
			$("[id='id']:checkbox:checked").each(function() {
				ids += $(this).val() + ",";
			});
			if (ids == "") {
				layer.alert( "请选择印章启用", {icon:0});
				return;
			}
			$.ajax({
				url : "${ctx }/sealManage/updateSealStatus.do",
				type : "get",
				data : "id=" + ids + "&status=1",
				dataType : "json",
				success : function(data) {
					if (data.success) {
						layer.alert( "操作成功", {icon:1});
						loadUrl("${ctx }/sealManage/sealList.do");
					} else {
						layer.alert( data.message, {icon:2});
					}

				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
				}
			});
		};
		//印章停用方法
		function stopSeal()  {
			var ids = "";
			$("[id='id']:checkbox:checked").each(function() {
				ids += $(this).val() + ",";
			});
			if (ids == "") {
				layer.alert( "请选择印章停用", {icon:0});
				return;
			}
			$.ajax({
				url : "${ctx }/sealManage/updateSealStatus.do",
				type : "get",
				data : "id=" + ids + "&status=0",
				dataType : "json",
				success : function(data) {
					if (data.success) {
						layer.alert( "操作成功", {icon:1});
						loadUrl("${ctx }/sealManage/sealList.do");
					} else {
						layer.alert( data.message, {icon:2});
					}

				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
				}
			});
		};
		//印章删除方法
		function delSeal() {
			var ids = "";
			$("[id='id']:checkbox:checked").each(function() {
				ids += $(this).val() + ",";
			});
			if (ids == "") {
				layer.alert( "请选择印章删除", {icon:0});
				return;
			}
		
		layer.confirm("确定要删除该印章吗?",{btn:["确定","取消"]},function(){
				$.ajax({
					url : "${ctx }/sealManage/delSeal.do",
					type : "get",
					data : "id=" + ids,
					dataType : "json",
					success : function(data) {
						if (data.success) {
							layer.alert( "删除成功", {icon:1});
							loadUrl("${ctx }/sealManage/sealList.do");
						} else {
							layer.alert( "删除失败", {icon:2});
							loadUrl("${ctx }/sealManage/sealList.do");
						}
					},
					error : function() {
						layer.alert( "请求失败", {icon:2});
						loadUrl("${ctx }/sealManage/sealList.do");
					}
				});
			});
		};
		
		
		/*
		$("#checkboxChange").click(function() {
			$("[name=id]:checkbox").prop("checked", this.checked);
		}); */
		
	
	function editSeal(id) {
		loadUrl("${ctx }/sealManage/toEditSeal.do?id=" + id);
	}
	//修改
	function moduser() {
		var id = "";
		var index = 0;
		$("[id='id']:checkbox").each(
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
			return;
		}else{
			loadUrl("${ctx }/sealManage/toEditSeal.do?id=" + id);
			//$("#useredit").load("${ctx }/userManage/toEditUser.do?id="+id);
			
		}

	}
	//印章下载
	function sealDownLoadView() {
		var id = "";
		var index = 0;
		$("[id='id']:checkbox").each(
				function() {
					if (this.checked) {
						if (index == 0) {
							id = $(this).val();
						} 
						index++;
					}
				});
		
		if (index == 0 || index>1) {
			layer.alert( "请选择要下载的一条记录", {icon:0});
			return;
		}else{
			loadUrl("${ctx }/sealManage/sealDownLoadView.do?id=" + id);
			
		}

	}
	
	//根据条件搜索
	function searchAccount() {
		var url = "${ctx }/sealManage/sealSearch.do";
		var firstSearch =$("#searchName").val();
		var secondSearch =$("#searchUserAccount").val();
		var thirdSearch =$("#searchCerDn").val();
		//查询条件是用户名 
		$.ajax({
			type : "post",
			dataType : "json",
			url : url,
			data: {"name":firstSearch,"userName":secondSearch,"certDn":thirdSearch}, 
			success : function(jsonResult) {
				$("#sysUserTable tr:not(:first)").remove();
				$("#sealtPage").empty();
				$("#searchName").val("");
				$("#searchUserAccount").val("");
				$("#searchCerDn").val("");
				//遍历一个数组or集合
				var ta = jsonResult.page.result;
				if (ta == "") {
					layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
				} else {
					$.each(ta,function(i, item) {
		 				//追加html文本
		 				if (item.sealMac == true) {
		 					$("#sysUserTable tbody").append("<tr><td><input id='id' name='checkboxt' type='checkbox' value='"+item.id+"''></td><td><a href='javascript:loadUrl(\"${ctx }/sealManage/sealShow.do?id="+
									item.id+"\") ' >"+item.name+"</a></td><td>"+item.userName+"</td><td>"+item.typeCn+"</td><td>"+item.certDn+"</td><td>"+item.statusCn+"</td><td >"+
									item.usedLimitCn+"</td><td>"+item.generateTimeCn+"</td></tr>");
						} else {
							$("#sysUserTable tbody").append("<tr><td><input id='id' name='checkboxt' type='checkbox' value='"+item.id+"''></td><td><a class='errorMac' href='javascript:loadUrl(\"${ctx }/sealManage/sealShow.do?id="+
									item.id+"\") ' >"+item.name+"</a></td><td class='errorMac'>"+item.userName+"</td><td class='errorMac'>"+item.typeCn+"</td><td class='errorMac'>"+item.certDn+"</td><td class='errorMac'>"+item.statusCn+"</td><td class='errorMac'>"+
									item.usedLimitCn+"</td><td class='errorMac'>"+item.generateTimeCn+"</td></tr>");
						}
		 			});
					icheck(".js-checkbox-all");
					/* ----分页--开始 */
					//分页
					 laypage({
						cont : 'sealtPage',
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
											url : "${ctx }/sealManage/sealSearch.do",
											data: {"name":firstSearch,"userName":secondSearch,"certDn":thirdSearch,"pageNo":e.curr},
											success : function(jsonResult) {
												$("#sysUserTable tr:not(:first)").remove();
												//遍历一个数组or集合
												var ta = jsonResult.page.result;
												if (ta == "") {
													layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
												} else {
													$.each(ta,function(i, item) {
														//追加html文本
														if (item.sealMac == true) {
										 					$("#sysUserTable tbody").append("<tr><td><input id='id' name='checkboxt' type='checkbox' value='"+item.id+"''></td><td><a href='javascript:loadUrl(\"${ctx }/sealManage/sealShow.do?id="+
																	item.id+"\") ' >"+item.name+"</a></td><td>"+item.userName+"</td><td>"+item.typeCn+"</td><td>"+item.certDn+"</td><td>"+item.statusCn+"</td><td >"+
																	item.usedLimitCn+"</td><td>"+item.generateTimeCn+"</td></tr>");
														} else {
															$("#sysUserTable tbody").append("<tr><td><input id='id' name='checkboxt' type='checkbox' value='"+item.id+"''></td><td><a class='errorMac' href='javascript:loadUrl(\"${ctx }/sealManage/sealShow.do?id="+
																	item.id+"\") ' >"+item.name+"</a></td><td class='errorMac'>"+item.userName+"</td><td class='errorMac'>"+item.typeCn+"</td><td class='errorMac'>"+item.certDn+"</td><td class='errorMac'>"+item.statusCn+"</td><td class='errorMac'>"+
																	item.usedLimitCn+"</td><td class='errorMac'>"+item.generateTimeCn+"</td></tr>");
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
	
</script>