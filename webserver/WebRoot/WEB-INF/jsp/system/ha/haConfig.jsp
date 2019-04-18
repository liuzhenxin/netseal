<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-xs-12">
							<div class="x_panel" >
							
		
			<form id="haConfigForm" action="${ctx }/system/ha/haConfigSave.do" method="post" class="form-horizontal">
			<div class="form-group">
					<label class="control-label col-xs-3"><b>HA节点名称配置</b></label>
					<div class="col-xs-6">
						<font color="red">${message }</font>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-3">主机节点名称</label>
					<div class="col-xs-6">
						<select id="masterNodeName" name="mainNodeDeviceName" class="form-control input-sm">
						<c:forEach items="${hostNameMap}" var="entry">    
							<option value="${entry.value}">${entry.value}-${entry.key}</option>   
						</c:forEach>  
						</select>
					</div>
					<div class="col-xs-3">主备机节点名称配置格式:节点名称-对应IP</div>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-3">备机节点名称</label>
					<div class="col-xs-6">
						<select id="slaveNodeName" name="slaveNodeName" class="form-control input-sm">
						<c:forEach items="${hostNameMap}" var="entry">
							<option value="${entry.value}">${entry.value}-${entry.key}</option>   
						</c:forEach> 
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-3"><b>HA虚IP配置</b></label>
					<div class="col-xs-6">
						
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-3">虚IP地址</label>
					<div class="col-xs-6">
						<input class="form-control" type="text" id="vmIP" name="vmIP" value="${vmIP }">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-3">子网掩码</label>
					<div class="col-xs-6">
						<input class="form-control" type="text" id="netmask" name="netmask" value="${netMask }">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-xs-3">绑定网口名称</label>
					<div class="col-xs-6">
						<select id="bindNetWorkName" name="IPDevice" class="form-control input-sm">
						<c:forEach items="${netWorkNameList}" var="value">
							<option value="${value}">${value}</option>
						</c:forEach>
						</select>
					</div>
				</div>
				
				<div class="form-group">
					<label class="control-label col-xs-3">网口健康检查</label>
					<div class="col-xs-6">
						<select id="checkNetWorkName" name="HADevice" class="form-control input-sm">
						<c:forEach items="${netWorkNameList}" var="value">
							<option value="${value}">${value}</option>
						</c:forEach> 
						</select>
					</div>
				</div>
				
				<div class="form-actions">				
					<div class="row">
						<div class="col-md-offset-3 col-xs-6" style="text-aligen:center">
						  <button type="button" class="btn btn-primary col-md-offset-4"   id="haConfigSave">保存</button>
						</div>
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
	$("#masterNodeName").val("${masterNodeName}");
	$("#slaveNodeName").val("${slaveNodeName}");
	$("#bindNetWorkName").val("${bindNetWorkName}");
	$("#checkNetWorkName").val("${checkNetWorkName}");
	
	$("#haConfigForm").bootstrapValidator({       
		fields: {
			mainNodeDeviceName: {
               validators: {
                   notEmpty: {
                       message: '主机节点名称'
                   },
                   different: {//不能和备机节点名称相同
                       field: 'slaveNodeName',
                       message: '不能和备机节点名称相同'
                   }
                  
               }
           },
           slaveNodeName: {
               validators: {
                   notEmpty: {
                       message: '备机节点名称'
                   },
                   different: {//不能和主机节点名称相同
                       field: 'mainNodeDeviceName',
                       message: '不能和主机节点名称相同'
                   }
                  
               }
           },
           vmIP: {
               validators: {
                   notEmpty: {
                       message: '虚IP地址不能为空'
                   },					
                   regexp: {
                       regexp: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/,
                       message: '虚IP地址格式不正确'
                   }
                  
               }
           },
           netmask: {
               validators: {
                   notEmpty: {
                       message: '子网掩码不能为空'
                   },					
                   regexp: {
                       regexp: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/,
                       message: '子网掩码格式不正确'
                   }
                  
               }
           },
           IPDevice: {
               validators: {
                   notEmpty: {
                       message: '绑定网口名称不能为空'
                   }                  
               }
           },
           HADevice: {
               validators: {
                   notEmpty: {
                       message: '网口健康检查不能为空'
                   }                  
               }
           }
           
        }
	});
	$("#haConfigSave").click(function(){
		if("${message}"!=""){
			layer.alert("不能执行当前操作",{icon:2});
			return;
		}
		 var form=$("#haConfigForm");
		 form.bootstrapValidator('validate');
		 if (form.data('bootstrapValidator').isValid()) {
			 form.ajaxSubmit({
				 success:function(data){
				     if(data.success){	
				    	layer.alert(data.message,{icon:1});
			        	
				     }else{
						layer.alert( data.message, {icon:2});
					}
				    	
				    },error:function(){
				    	layer.alert("请求失败",{icon:2});
				    }
			 }); 
		 }else{
			 form.bootstrapValidator('validate');
		 }
		 
	});
});
</script>

