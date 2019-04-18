<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<title>信安世纪签章API演示系统</title>
<link rel="stylesheet" type="text/css" href="css.css">
</head>
<body bgcolor="#FFFFFF">
	<table width="100%" border="0" cellspacing="4" cellpadding="2">
		<tr bgcolor="#336699">
			<td>
				<div align="center" class="hei14">
					<b><font color="#FFFFFF">信安世纪签章API演示系统</font> </b>
				</div>
			</td>
		</tr>
	</table>
	<p>&nbsp;</p>

	<form name="free" action="addOperLogOK.jsp" method="post">
		<table width="60%" border="1" cellpadding="2" class="top"
			cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>添加交易日志</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">帐号：</td>
				<td style="width:80%"><input size="50" type="text" name="OP_LOG_ACCOUNT"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">类型：</td>
				<td style="width:80%"><input size="50" type="text" name="OP_LOG_TYPE"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">时间：</td>
				<td style="width:80%"><input size="50" type="text" name="OP_LOG_TIME">(2017-09-09 09:50:50)</td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">错误代码：</td>
				<td style="width:80%"><input size="50" type="text" name="OP_LOG_RETURN_CODE"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">错误描述：</td>
				<td style="width:80%"><input size="50" type="text" name="OP_LOG_ERR_MSG"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td colspan=2 align=center><input type="submit" value="提交">
				</td>
			</tr>
		</table>
	</form>

</body>
</html>
