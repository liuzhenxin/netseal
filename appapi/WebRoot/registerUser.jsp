<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

	<form name="free" action="registerUserOK.jsp" method="post">
		<table width="60%" border="1" cellpadding="2" class="top" cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>注册签章人</b></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">签章人名称：</td>
				<td style="width:80%"><input size="50" type="text" name="USER_NAME"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">证书路径：</td>
				<td style="width:80%"><input size="50" type="text" name="CERT_DATA">(本地路径,多个路径以英文逗号分隔)</td> 
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">机构名称：</td>
				<td style="width:80%"><input size="50" type="text" name="COMPANY_NAME">(,分隔)</td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td colspan=2 align=center><input type="submit" value="提交"></td>
			</tr>
		</table>
	</form>

</body>
</html>
