<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="cn.com.infosec.netseal.appapi.*"%>
<%@ page isErrorPage="true"%>
<%
	int err = 0;
	String message = "";
	if (exception instanceof AppApiException) {
		AppApiException ex = (AppApiException) exception;
		err = ex.getErrCode();
		message = "errMsg=" + ex.getErrMsg();
	}

	if (exception instanceof AppServerException) {
		AppServerException ex = (AppServerException) exception;
		err = ex.getErrCode();
		message = "errMsg=" + ex.getErrMsg();
	} else {
		exception.printStackTrace();
		message = "errMsg=" + exception.getMessage();
	}
%>
<html>
<head>
<title>信安世纪签章API演示系统</title>
<link rel="stylesheet" type="text/css" href="css.css">
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
	bgcolor="#FFFFFF">
	<table width="100%" border="0" cellspacing="4" cellpadding="2">
		<tr bgcolor="#336699">
			<td>
				<div align="center" class="hei14">
					<b><font color="#FFFFFF">信安世纪签章API演示系统</font></b>
				</div>
			</td>
		</tr>
	</table>
	<p>&nbsp;</p>
	<table width="500" border="0" cellpadding="2" cellspacing="1"
		class="top" align="center" bgcolor="#00CCCC">
		<tr bgcolor="#CAEEFF">
			<td colspan="18">
				<div align="left">
					发生错误：
					<%
					if (err != 0)
						out.print("errCode=" + err + "   ");
					out.print(message);
				%>
				</div>
			</td>
		</tr>
	</table>
	<br>
</body>
</html>
