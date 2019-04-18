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

	<form name="free" action="requestSealOK.jsp" method="post">
		<table width="60%" border="1" cellpadding="2" class="top" cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>申请印章</b></td>
			</tr>
			<!--  
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">帐号：</td> 
				<td style="width80%"><input size="50" type="text" name="ACCOUNT"> (签章人)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">密码：</td>
				<td style="width80%"><input size="50" type="text" name="PASSWORD"></td>
			</tr>
			-->
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">印章名称：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_NAME"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">印模名称：</td>
				<td style="width80%"><input size="50" type="text" name="TEMPLATE_NAME"></td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">图片：</td>
				<td style="width80%"><input size="50" type="text" name="TEMPLATE_PHOTO">(本地路径 不填取印模配置图片)</td> 
			</tr>
			<!--  
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">生效时间：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_NOTBEFOR">(2017-03-03 09:50:50)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">失效时间：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_NOTAFTER">(2017-03-04 09:50:50)</td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">签章人姓名：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_USER"></td>
			</tr>
			-->
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">证书DN：</td>
				<td style="width80%"><input size="50" type="text" name="CERT_DN"></td>
			</tr>
			<!-- <tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">机构名称：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_COMPANY_NAME">(,分隔)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">图章名称：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_PHOTO_NAME"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">图章正文：</td>
				<td style="width80%"><input size="50" type="text" name="SEAL_PHOTO_DATA_GEN_STR"></td>
			</tr> -->
			<tr bgcolor="#CAEEFF">
				<td colspan=2 align=center><input type="submit" value="提交"></td>
			</tr>
		</table>
	</form>

</body>
</html>
