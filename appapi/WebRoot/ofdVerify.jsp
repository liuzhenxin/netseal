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

	<form name="free" action="ofdVerifyOK.jsp" method="post">
		<table width="60%" border="1" cellpadding="2" class="top" cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>OFD验章</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">OFD文件：</td>
				<td style="width:80%"><input size="50" type="text" name="OFD_DATA">(本地路径)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">是否验证印章有效期：</td>
				<td style="width:80%">
					<input type="radio" checked name="OFD_CHECK_SEAL_DATE" value="true" id="leftS"><label for="leftS">是</label>
					<input type="radio" name="OFD_CHECK_SEAL_DATE" value="false" id="rightS"><label for="rightS">否</label>
				</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">是否验证证书有效期：</td>
				<td style="width:80%">
					<input type="radio" checked name="OFD_CHECK_CERT_DATE" value="true" id="leftC"><label for="leftC">是</label>
					<input type="radio" name="OFD_CHECK_CERT_DATE" value="false" id="rightC"><label for="rightC">否</label>
				</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td colspan=2 align=center><input type="submit" value="提交"></td>
			</tr>
		</table>
	</form>

</body>
</html>
