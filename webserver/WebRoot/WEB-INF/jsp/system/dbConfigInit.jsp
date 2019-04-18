<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<%@ include file="../common/resource.jsp"%>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>NetSeal电子签章管理系统</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

</head>
<body class="hold-transition login-page" style="background-color: rgb(42,63,84); overflow-y: hidden;">
<div class="login-box">
  <div class="login-logo" style="font-size:30px; color:aqua;">
    <p style="color:rgb(205,210,215); font-size:20px;">第三步、初始化数据连接</p>
  </div>
  <!-- /.login-logo -->
  <div class="login-box-body" style="width:1000px; position:absolute; left: 50%; top:50%; margin-top:-200px; margin-left:-500px;">

    <div class="x_panel">
    <div class="main">
       	<div class="form-group">
			<p style="text-align: center; color:red;">保存成功后，需要重启生效！</p>
		</div><br>
		<form id="configForm" class="form-horizontal form-label-left" action="" method="post"  style="margin-top:20px;" >
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
				  <input type="text"  name="url" value="${config.url }" class="form-control col-xs-6">
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
				  <input type="password" name="password"   required value="${config.password }" class="form-control col-xs-6">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-xs-3">初始化连接数 
				</label>
				<div class="col-xs-6">
				  <input type="text" name="initialSize"  value="${config.initialSize }" class="form-control col-xs-6">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-xs-3">最小连接数 
				</label>
				<div class="col-xs-6">
				  <input type="text" name="minIdle" value="${config.minIdle }" class="form-control col-xs-6">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-xs-3">最大连接数 
				</label>
				<div class="col-xs-6">
				  <input type="text" name="maxActive"  required value="${config.maxActive }" class="form-control col-xs-6">
				</div>
			</div>
			
			<div class="form-group">
				<label class="control-label col-xs-3">最大等待时间 
				</label>
				<div class="col-xs-6">
				  <input type="text"  name="maxWait" value="${config.maxWait }" class="form-control col-xs-6">
				</div>
				<div class="col-sm-1">(毫秒)</div>
			</div>

		  <div class="ln_solid"></div>
			<div class="form-actions">
				<div class="row">

				  <button id="backBtn" type="button" class="btn btn-primary col-md-offset-2">返回上一步</button>
				  <button id="saveConfigBtn" type="button" class="btn btn-primary col-md-offset-2">保存</button>
				  <button id="testConfigBtn" class="btn btn-primary col-md-offset-2" type="button">测试DB连接</button>

				</div>
			</div>

		</form>

	</div>
	</div>

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
			tip+="oracle连接地址：<b>jdbc:oracle:thin:@<font color='red'>10.20.61.53</font>:<font color='red'>1521</font>:<font color='red'>orcl</font></b><br>";
			tip+="mysql连接地址：<b>jdbc:mysql://<font color='red'>10.20.61.53</font>:<font color='red'>3306</font>/<font color='red'>netseal</font></b><br>";
			tip+="DB2连接地址：<b>jdbc:db2://<font color='red'>10.20.61.53</font>:<font color='red'>50000</font>/<font color='red'>NETSEAL</font></b><br>";
			tip+="SQLserver连接地址：<b>jdbc:sqlserver://<font color='red'>10.20.61.53</font>:<font color='red'>1433</font>;databaseName=<font color='red'>NETSEAL</font></b><br>";
			layer.alert(tip, {icon:0,title:'连接地址样例',area:'650px'});
		});
		$("#configUrlTip6").click(function() {
			var tip="红色内容需要修改<br>";
			tip+="oracle连接地址：<b>jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=[<font color='red'>IP</font>])(PORT=<font color='red'>PORT</font>))(CONNECT_DATA=(SERVICE_NAME=<font color='red'>SERVICENAME</font>)))</b><br>";
			tip+="mysql连接地址：<b>jdbc:mysql://address=(protocol=tcp)(host=<font color='red'>IP</font>)(port=<font color='red'>PORT</font>)/<font color='red'>TABLE_NAME</font></b><br>";
			tip+="DB2连接地址：<b>jdbc:db2://<font color='red'>10.20.61.53</font>:<font color='red'>50000</font>/<font color='red'>NETSEAL</font></b><br>";
			tip+="SQLserver连接地址：<b>jdbc:sqlserver://<font color='red'>10.20.61.53</font>:<font color='red'>1433</font>;databaseName=<font color='red'>NETSEAL</font></b><br>";
			layer.alert(tip, {icon:0,title:'连接地址样例',area:'1200px'});
		});
		$("#saveConfigBtn").click(function() {
			var form = $("#configForm");
			$('#configForm').bootstrapValidator('validate');
			if ($('#configForm').data('bootstrapValidator').isValid() && numberCompare()) {
				form.attr("action", "${ctx }/sysUser/dbConfigInitSave.do");
				form.ajaxSubmit({
					success : function(data) {
						if(data.success){
							layer.alert( data.message, {icon:1});
							location.reload();
						}else{
							layer.alert( data.message, {icon:2});
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
		$("#backBtn").click(function() {
			window.location.href="${ctx }/sysUser/sysConfigInit.do";
		});
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
							message : '最小连接不能为空'
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
</body>
</html>