<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>


<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">加密卡用户管理</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;">用户管理    /    加密卡用户管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			加密卡用户管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			  <div class="x_title">
			  		<input type="button" value="登录" class="btn btn-primary" id="cardUserLogin" style=" float:left;"  />
		  		    <input type="button" value="注销" class="btn btn-primary" id="cardUserLogout" style=" float:left; margin-left:10px;" />
		  		   <!--  <input type="button" value="修改PIN" class="btn btn-primary" id="editPin" style=" float:left; margin-left:10px;" /> -->
		  		    <input type="hidden" id="userId" name="userId" value="${currentUser.userID }">
		  		    
			<div class="clearfix"></div>
		  </div>
		  <div class="x_content">
			<div id="table-box" style="display: block;">
				<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap">
				  <thead>
					<tr>
					<th width="1"><input id="checkboxChange" class="js-checkbox-all " type="checkbox" /></th>
						<th>编号</th>
						<th>名称</th>
						<th>状态</th>
						<th>类型</th>
						<th>当前用户</th>
					</tr>
				  </thead>
				   
				  <tbody>
				  <c:forEach items="${userList}" var="cardUser" varStatus="status">
						<tr>
							<th width="1"><input id="${status.count-1 }" name="checkboxt" class="input_checked" type="checkbox" value="${cardUser.userStatus }"></th>
							<td>${status.count-1}</td>
							<td>${cardUser.userID }</td>
							<td>${cardUser.userStatus }</td>
							<td>${cardUser.userType}</td>
							<td>${cardUser.currentUserCn }</td>
						</tr>
						</c:forEach>
				</tbody>				
				</table>
				<span><font color="red"><b>${msg }</b></font></span>
				<div class="text-right" id="requestPage"></div>
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
	icheck(".js-checkbox-all");
	$("#cardUserLogin").click(function(){
		loadUrl("${ctx }/system/card/toLoginCardUser.do?")
	});
	
	$("#cardUserLogout").click(function(){
		var id = "";
		var flag = true;
		$("[name=checkboxt]:checkbox").each(function() {
			if (this.checked) {
				var userStatus=	$(this).val();
				if(userStatus!="已登录"){
					flag = false;
					layer.alert("请选择已登录用户,进行注销");
					return ;
				}
				id += $(this).attr("id") + ",";
				
			}
		});
		if(flag){
			if (id  == "") {
				layer.alert( "请选择要注销的记录", {icon:0});
				return;
			}
			
			$.ajax({
				url:"${ctx }/system/card/cardUserLogout.do",
				type:"post",
				data:"userNum="+id,
				dataType:"json",
				success : function(data) {
					if (data.success) {
						layer.alert( "注销成功", {icon:1});
						loadUrl("${ctx }/system/card/cardUserManage.do");
					} else {
						layer.alert( "注销失败", {icon:2});
					}
					
				},
				error : function() {
					layer.alert( "请求失败", {icon:2});
				}
			});
		}
			
	});
	
	
	/* $("#editPin").click(function(){
		var userId= $("#userId").val();
		loadUrl("${ctx }/system/card/toEditCardUser.do?userId="+userId)
	
    }); */
	
});




</script>