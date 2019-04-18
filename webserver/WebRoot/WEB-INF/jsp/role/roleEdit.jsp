<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">角色管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    角色管理    /    权限变更</i>
	</li>
</ul>	
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			权限变更</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top: 20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="main">
							<div class="inner-title"></div>
							<form id="editRoleMenuForm" class="form-horizontal"
								action="${ctx }/role/editRoleMenu.do?id=${role.id }"
								method="post">
								<div class="text-center">
									<%-- <label class="control-label">角色编号</label> <span
										class="top-btn form-control-static">${role.id }</span> <label
										class="top-btn control-label">角色名称</label> --%><label 
										class="top-btn form-control-static">${role.name }</label>
								</div>


								<div id="table1" style="">
									<table class="table table-bordered  table-striped table-hover">
										<thead>
											<tr>
												<th>主菜单</th>
												<th>子菜单</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="menu" items="${allMune}">
												<c:if test="${menu.pid==-1}">
													<c:choose>
														<c:when test="${menu.sealMac == true}">
															<tr>
																<td>
																	<label class="checkbox-inline"> 
																		<input style="padding-top: 3px;" type="checkbox" class="menuparent" name="menuIds" ${menu.checked ? 'checked': '' } value="${menu.id}">
																		<span>${menu.name }</span>
																	</label>
																</td>
																<td><c:forEach var="menu2" items="${allMune}">
																		<c:if test="${menu.id==menu2.pid}">
																			<label class="checkbox-inline"> 
																				<input style="padding-top: 3px;" name="menuIds" type="checkbox" ${menu2.checked ? 'checked': '' } value="${menu2.id }"> 
																				<span>${menu2.name }</span>
																			</label>
																		</c:if>
																	</c:forEach></td>
															</tr>
														</c:when>
														<c:when test="${menu.sealMac == false}">
															<tr>
																<td>
																	<label class="checkbox-inline"> 
																	<input style="padding-top: 3px;" type="checkbox" class="menuparent" name="menuIds" ${menu.checked ? 'checked': '' } value="${menu.id}">
																		<span class="errorMac">${menu.name }</span>
																	</label>
																</td>
																<td>
																	<c:forEach var="menu2" items="${allMune}">
																		<c:if test="${menu.id==menu2.pid}">
																			<label class="checkbox-inline"> 
																			<input style="padding-top: 3px;" name="menuIds" type="checkbox" ${menu2.checked ? 'checked': '' } value="${menu2.id }"> 
																				<span class="errorMac">${menu2.name }</span>
																			</label>
																		</c:if>
																	</c:forEach>
																</td>
															</tr>
														</c:when>
													</c:choose>
												</c:if>
											</c:forEach>
										</tbody>
									</table>
									<div class="form-actions">
										<div class="row">
											<div class="col-md-offset-3 col-xs-6">
												<button id="reset" type="reset" class="btn btn-primary col-md-offset-2">重置</button>
												<button id="submitRoleMenu" class="btn btn-primary col-md-offset-2" type="button">确定</button>
												<button id="back" type="button" class="btn btn-primary col-md-offset-2" id="return">返回</button>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</div>


<script type="text/javascript">
$(function(){
	
	//授权
	 $("#submitRoleMenu").click(function(){		
		 var form=$("#editRoleMenuForm");
		 form.ajaxSubmit({
			 success:function(data){
			     if(data.success){
			    	 layer.alert(data.message,{icon:1});
			    	 var url="${ctx }/role/roleList.do";
			         loadUrl(url);
			      } else {
			    	  layer.alert(data.message,{icon:2});
			      } 
			    },error:function(){
			    	layer.alert("请求失败",{icon:2});
			    }
		 });
	});
	
	
	 $(".menuparent").click(function(){
		
		$.each($(this).parent().parent().parent().find("input"), function(i,e) {
			e.checked=$(this).parent().parent().parent().find("input").first()[0].checked;
		});	
		
	});
	
	$("[name=menuIds]").click(function(){
		var cks= $(this).parent().parent().parent().find("input");
		$.each(cks,function(i,e){
			if(e.checked){
				cks[0].checked=true;
			}
		});
		var nocheck=$(this).parent().parent().parent().find("input:checked");
		if(nocheck.length==1){
			cks[0].checked=false;						
		}
		
	}); 
	//返回
	$("#back").click(function() {
		loadUrl("${ctx }/role/roleList.do");
	});
	
	
});

</script>


