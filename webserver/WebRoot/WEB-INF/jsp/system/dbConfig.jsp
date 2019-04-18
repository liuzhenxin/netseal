<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">数据库配置</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;">系统管理    /    数据库配置</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
						数据库配置</a>
				</li>
			</ul>
			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
								<div class="main">
									<form id="configForm"  action="" method="post"  data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;" >
										<div class="form-group">
											<label for="name" class="control-label col-xs-3">驱动名称</label>
											<div class="col-xs-6">
												<input class="form-control" type="text" name="driverClassName" value="${config.driverClassName }">
											</div>
											<div class="col-sm-1"><a id="configDriverTip" href="#">样例</a></div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">连接地址 
											</label>
											<div class="col-xs-6">
											  <input type="text"  name="url" required value="${config.url }" class="form-control col-xs-6">
											</div>
											<div class="col-sm-1"><a id="configUrlTip" href="#"><strong>ipv4</strong>样例</a></div>
											<div class="col-sm-1"><a id="configUrlTip6" href="#"><strong>ipv6</strong>样例</a></div>
										</div>

										<div class="form-group">
											<label class="control-label col-xs-3">用户名 
											</label>
											<div class="col-xs-6">
											  <input type="text"  name="username"  required value="${config.username }" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">密码 
											</label>
											<div class="col-xs-6">
											  <input type="password" name="password"  required value="${config.password }" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">初始化连接数 
											</label>
											<div class="col-xs-6">
											  <input type="text"  name="initialSize"  required value="${config.initialSize }" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">最小连接数 
											</label>
											<div class="col-xs-6">
											  <input type="text"  name="minIdle"  required value="${config.minIdle }" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">最大连接数 
											</label>
											<div class="col-xs-6">
											  <input type="text"  name="maxActive"  required value="${config.maxActive }" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">最大等待时间 
											</label>
											<div class="col-xs-6">
											  <input type="text"  name="maxWait"  required value="${config.maxWait }" class="form-control col-xs-6">
											</div>
											<div class="col-sm-1">(毫秒)</div>
										</div>

									  <div class="ln_solid"></div>
										<div class="form-actions">
											<div class="row">
												<div class="col-md-offset-3 col-xs-6">
											  <button id="saveConfigBtn" type="button" class="btn btn-primary col-md-offset-3" data-toggle="modal"  data-target=".bs-example-modal-sm11">保存</button>
											  <button id="testConfigBtn" type="button" class="btn btn-primary col-md-offset-3"  data-toggle="modal"  data-target=".bs-example-modal-sm10">测试DB连接</button>
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
function numberCompare(){
	var  initialSize = parseInt( $("input[name='initialSize']").val());
	var  minIdle = parseInt($("input[name='minIdle']").val());
	var  maxActive= parseInt($("input[name='maxActive']").val());
	var msg="";
	if((maxActive<1)|| (initialSize > maxActive)||(minIdle > maxActive)){
		msg +="最大连接数不小于1,且初始化连接数与最小连接数不能大于最大连接数";
	}
	if(msg){
		layer.alert(msg,{icon:2});
		return false;
	}
	
	return true;
}


	$(function() {
		$("#configDriverTip").click(function() {
			var tip="样例:<br>";
			tip+="oracle驱动名称：<b>oracle.jdbc.driver.OracleDriver</b><br>";
			tip+="mysql驱动名称：<b>com.mysql.jdbc.Driver</b><br>";
			tip+="DB2驱动名称：<b>com.ibm.db2.jcc.DB2Driver</b><br>";
			tip+="SQLserver驱动名称：<b>com.microsoft.sqlserver.jdbc.SQLServerDriver</b><br>";
			layer.alert(tip, {icon:0,title:'驱动名称',area:'600px'});
		});
		$("#configUrlTip").click(function() {
			var tip="红色内容需要修改<br>";
			tip+="oracle连接地址：<b>jdbc:oracle:thin:@<font color='red'>IP</font>:<font color='red'>PORT</font>:<font color='red'>SERVICENAME</font></b><br>";
			tip+="mysql连接地址：<b>jdbc:mysql://<font color='red'>IP</font>:<font color='red'>PORT</font>/<font color='red'>TABLE_NAME</font></b><br>";
			tip+="DB2连接地址：<b>jdbc:db2://<font color='red'>IP</font>:<font color='red'>PORT</font>/<font color='red'>TABLE_NAME</font></b><br>";
			tip+="SQLserver连接地址：<b>jdbc:sqlserver://<font color='red'>IP</font>:<font color='red'>PORT</font>;databaseName=<font color='red'>TABLE_NAME</font></b><br>";
			layer.alert(tip, {icon:0,title:'连接地址样例',area:'650px'});
		});
		$("#configUrlTip6").click(function() {
			var tip="红色内容需要修改<br>";
			tip+="oracle连接地址：<b>jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=[<font color='red'>IP</font>])(PORT=<font color='red'>PORT</font>))(CONNECT_DATA=(SERVICE_NAME=<font color='red'>SERVICENAME</font>)))</b><br>";
			tip+="mysql连接地址：<b>jdbc:mysql://address=(protocol=tcp)(host=<font color='red'>IP</font>)(port=<font color='red'>PORT</font>)/<font color='red'>TABLE_NAME</font></b><br>";
			tip+="DB2连接地址：<b>jdbc:db2://[<font color='red'>IP</font>]:<font color='red'>PORT</font>/<font color='red'>TABLE_NAME</font></b><br>";
			tip+="SQLserver连接地址：<b>jdbc:sqlserver://<font color='red'>IP</font>:<font color='red'>PORT</font>;databaseName=<font color='red'>TABLE_NAME</font></b><br>";
			layer.alert(tip, {icon:0,title:'连接地址样例',area:'1200px'});
		});
		$("#saveConfigBtn").click(function() {
			var form = $("#configForm");
			$('#configForm').bootstrapValidator('validate');
			numberCompare();
			if ($('#configForm').data('bootstrapValidator').isValid() && numberCompare()) {
				form.attr("action", "${ctx }/system/dbConfigSave.do");
				form.ajaxSubmit({
					success : function(data) {
						if (data.success) {
							layer.alert( data.message, {icon:1});
							/* layer.confirm(data.message, {btn:["确定"]}, function(){location.reload();}); */
						} else {
							layer.alert( data.message,{icon:2});
						}
					},
					error : function() {
						layer.alert( "请求失败", {icon:2});
					}
				});	
			} else {
				$('#configForm').bootstrapValidator('validate');
			}

		});
		$("#testConfigBtn").click(function() {
			
			
			var form = $("#configForm");
			$('#configForm').bootstrapValidator('validate');
			
			if ($('#configForm').data('bootstrapValidator').isValid() && numberCompare()) {
				form.attr("action", "${ctx }/system/dbConfigTest.do");
				form.ajaxSubmit({
					success : function(data) {
						if (data.success) {
							layer.alert( data.message, {icon:1});
						} else {
							layer.alert( data.message,{icon:2});
						}

					},
					error : function() {
						layer.alert( "请求失败", {icon:2});
					}
				});
			} else {
				$('#configForm').bootstrapValidator('validate');
			}

		});
	//----表单验证---超级管理员页面
		$("#configForm").bootstrapValidator({			
			fields : {
				driverClassName : {
					validators : {
						notEmpty : {
							message : '连接地址不能为空'
						}
					}
				},
				url : {
					validators : {
						notEmpty : {
							message : '连接地址不能为空'
						},
						stringLength : {
							min : 1,
							max : 150,
							message : '连接地址长度为1~150'
						}
					}
				},
				username : {
					validators : {
						notEmpty : {
							message : '用户名名不能为空'
						},
						stringLength : {
							min : 1,
							max : 50,
							message : '用户名长度为1~50'
						},
						regexp: {
	                         regexp: /^[-_a-zA-Z0-9@]+$/,
	                         message: '用户名只能由字母数字下划线和@组成'
	                    }
					}
				},
				password : {
					validators : {
						notEmpty : {
							message : '密码不能为空'
						},
						stringLength : {
							min : 1,
							max : 50,
							message : '密码长度为1~50'
						}
					}
				},
				maxActive : {
					validators : {
						notEmpty : {
							message : '最大连接数不能为空'
						},
						stringLength : {
							min : 1,
							max : 10,
							message : '最大连接数长度为1~10'
						},
						regexp : {
							regexp : /^[0-9]*$/,
							message : '最大连接数只能由数字组成'
						}
					}
				},				
				minIdle : {
					validators : {
						notEmpty : {
							message : '最小连接数不能为空'
						},
						stringLength : {
							min : 1,
							max : 10,
							message : '最小连接数长度为1~10'
						},
						regexp : {
							regexp : /^[0-9]*$/,
							message : '最小连接数只能由数字组成'
						}

					}
				},
				initialSize : {
					validators : {
						notEmpty : {
							message : '初始化连接数不能为空'
						},
						stringLength : {
							min : 1,
							max : 10,
							message : '初始化连接数长度为1~10'
						},
						regexp : {
							regexp : /^[0-9]*$/,
							message : '初始化连接数只能由数字组成'
						}

					}
				},
				maxWait : {
					validators : {
						notEmpty : {
							message : '最大等待时间不能为空'
						},
						stringLength : {
							min : 1,
							max : 10,
							message : '最大等待时间长度为1~10'
						},
						regexp : {
							regexp : /^[0-9]*$/,
							message : '最大等待时间只能由数字组成'
						}
					}
				}
			}
		});
	});
	
	
	
</script>