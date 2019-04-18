<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">日志配置</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;">日志管理    /    日志配置</i>
	</li>
</ul>	
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			日志配置</a>
	</li>
</ul>

<div id="form" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
						<form id="logConfigForm"  action="${ctx }/logConfig/logConfigSave.do" method="post" class="form-horizontal bv-form" >
								<div class="sys-tab">
									<div class="form-group">
										<label class="control-label col-xs-3" >文件大小 
										</label>
										<div class="col-xs-6">
										  <input id="logFileSize" name="logFileSize" type="text"  value="${config.logFileSize}" class="form-control col-xs-6">(单位MB,值1-100)
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3" >文件数量 
										</label>
										<div class="col-xs-6">
										  <input id="logFileNum" name="logFileNum" type="text" class="form-control col-xs-6" value="${config.logFileNum}">(值1-100)
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3" >syslog日志IP 
										</label>
										<div class="col-xs-6">
										  <input id="syslogIp" name="syslogIp" type="text" class="form-control col-xs-6" value="${config.syslogIp}">
										</div>
									</div>
									<div class="form-group">
										<label class="control-label col-xs-3" >syslog日志facility 
										</label>
										<div class="col-xs-6">
										  <input  id="syslogFacility" name="syslogFacility" type="text" class="form-control col-xs-6" value="${config.syslogFacility}">
										</div>
									</div>
									
									<div class="form-group">
										<label for="name" class="control-label col-xs-3">syslog日志</label>
										<div class=" col-xs-6" style=" margin-top:10px;">
											<input name="logToSyslog" type="checkbox" value="system" id="sys">
											<label for="sys">系统日志</label>				
											<input name="logToSyslog" type="checkbox" value="access" id="acc">
											<label for="acc">访问日志</label>					
											<input name="logToSyslog" type="checkbox" value="manage" id="man">
											<label for="man">管理日志</label>					
											<input name="logToSyslog" type="checkbox" value="error" id="err">
											<label for="err">错误日志</label>					
											<input name="logToSyslog" type="checkbox" value="debug" id="deb">
											<label for="deb">调试日志</label>
										</div>
									</div>
									<div class="form-group">
										<label for="name" class="col-sm-3 control-label">文件</label>
										<div class=" col-xs-6" style=" margin-top:10px;">
											<input name="logToFile" type="checkbox" value="system" id="syst">
											<label for="syst">系统日志</label>				
											<input name="logToFile" type="checkbox" value="access" id="acce">
											<label for="acce">访问日志</label>					
											<input name="logToFile" type="checkbox" value="manage" id="mana">
											<label for="mana">管理日志</label>					
											<input name="logToFile" type="checkbox" value="error" id="erro">
											<label for="erro">错误日志</label>					
											<input name="logToFile" type="checkbox" value="debug" id="debu">
											<label for="debu">调试日志</label>	
										</div>
									</div>
									<div class="form-group">
										<label for="name" class="col-sm-3 control-label">数据库</label>
										<div class=" col-xs-6" style=" margin-top:10px;">						
											<input name="logToDB" type="checkbox" value="access" id="acces">
											<label for="acces">访问日志</label>					
											<input name="logToDB" type="checkbox" value="manage" id="manag">
											<label for="manag">管理日志</label>					
										</div>
									</div>


									<div class="form-actions">
										<div class="row">
											<div class="col-md-offset-4 col-xs-6">
												  <button id="submitLogConfigButton"  type="button" class="btn btn-primary col-md-offset-3" >提交</button>
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
	var form=$("#logConfigForm");
	//--表单验证---修改
	$(form).bootstrapValidator({
		fields : {
			logFileSize: {//日志配置---文件大小
                validators: {
                    notEmpty: {
                        message: '文件大小不能为空'
                    },
                    numeric: {message: '文件大小只能输入数字'},
                    between: {
                        min: 1,
                        max: 100,
                        message: '大小为1 - 100'
                    },
                    regexp: {
                    	regexp: "^[1-9][0-9]*$",
                        message: '必须为大于0的正整数'
                    }
                }
            },
            logFileNum: {//日志配置---文件数量
                validators: {
                    notEmpty: {
                        message: '文件数量不能为空'
                    },
                    numeric: {message: '文件数量只能输入数字'},
                    between: {
                        min: 1,
                        max: 100,
                        message: '文件数量1 - 100'
                    },
                    regexp: {
                    	regexp: "^[1-9][0-9]*$",
                        message: '必须为大于0的正整数'
                    }
                }
            }
		}
	 });

	$("#submitLogConfigButton").click(function(){
		 form.bootstrapValidator('validate');
		if (form.data('bootstrapValidator').isValid()) {
		 form.ajaxSubmit({
			 success:function(data){
			     if(data.success){		        	
				 	layer.alert(data.message,{icon:1});
		        	var url="${ctx }/logConfig/logConfig.do";
		        	loadUrl(url);
			      }else{
			    	  layer.alert(data.message,{icon:2});
			      }
			    	
			    },error:function(){
			    	layer.alert("请求失败",{icon:2});
			    }
			 
			 
		 });
		}
		
	});
	
	var logToSyslogs='${config.logToSyslog}';
	var tmpB=logToSyslogs.split(",");
	for (var j=0;j<tmpB.length ;j++ ){
		$("input[name='logToSyslog'][value='" + tmpB[j] + "']").iCheck('check');
	}
		
	var logToFiles='${config.logToFile}';
	var tmpB=logToFiles.split(",");
	for (var j=0;j<tmpB.length ;j++ ){
		$("input[name='logToFile'][value='" + tmpB[j] + "']").iCheck('check');
	}
	
	var logToDBs='${config.logToDB}';
	var tmpB=logToDBs.split(",");
	for (var j=0;j<tmpB.length ;j++ ){
		$("input[name='logToDB'][value='" + tmpB[j] + "']").iCheck('check');
	}
	
});


</script>