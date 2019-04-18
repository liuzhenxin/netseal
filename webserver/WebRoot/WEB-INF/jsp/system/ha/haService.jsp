<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-xs-12">
							<div class="x_panel">
		<div class="x_title" id="x_title">
  		<input class="btn btn-primary" id="updateStatusButton" type="button" value="">
  		<div class="clearfix"></div>
	  </div>
		
		
			<form id="configForm" action="" method="post" class="form-horizontal" autocomplete="off" >
			<input id="status" type="hidden" value="${status }"/>
					<div class="form-group">
						<label for="name" class="control-label col-xs-3"></label>
						<div class="col-xs-6" style="text-align:center;">
							&nbsp;HA服务状态:&nbsp;${message }						
						</div>
					</div>
					<div class="form-group">
					<label for="name" class="control-label col-xs-3"></label>
					<div class="col-xs-6" style="text-align:center;">						
						<font color="red"><span id="tipInfo"></span></font>
					</div>
					</div>
				
			</form>
			
			</div>
			  </div>
			</div>
		  </section>
	    </div>
  	  </div>  


<script type="text/javascript">
$(function(){
	var status=$("#status").val();
	var statusCN="启用/停用";
	if(status=="start"){
		statusCN="停用";
		
	}
	if(status=="stop"){
		statusCN="启用";
	}
	
	$("#updateStatusButton").val(statusCN);
	
	$("#updateStatusButton").click(function(){
		if(status==""){
			layer.alert("不能执行当前操作",{icon:2});
			return;
		}
		if(status=="start"){
			status="stop";
		}else{
			status="start";
		}
		$("#tipInfo").html(statusCN+"已执行...");
		var url="${ctx }/system/ha/haServiceUpdate.do";		
		$.ajax({
		    url:url,
		    type:"get",
		    data:"status="+status,
		    dataType:"json",
		    success:function(data){
		        if(data.success){	
		        	layer.alert(data.message,{icon:1});
		        }else{
		        	layer.alert(data.message,{icon:2});
		        }
		    	
		    },error:function(){
		    	//layer.alert("请求失败",{icon:2});
		    }
		 });
		
	});
});
</script>

