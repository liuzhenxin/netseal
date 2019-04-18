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

	<form name="free" action="pdfStampTemplateOK.jsp" method="post">
		<table width="60%" border="1" cellpadding="2" class="top"
			cellspacing="1" align="center" bgcolor="#00CCCC">
			<tr bgcolor="#CAEEFF">
				<td align=center colspan="2"><b>PDF模板盖章</b></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">PDF模板名称：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_TEMPLATE_NAME"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">PDF模板文本域值：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_TEMPLATE_FIELD"><br>
				(json字符串,格式为{"name1":"value1","name2":"value2"})</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">服务器证书DN：</td>
				<td style="width:80%"><input size="50" type="text" name="SERVER_CERT_DN"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">证书DN：</td>
				<td style="width:80%"><input size="50" type="text" name="CERT_DN"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">页号：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_PAGENUM"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">业务编号：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_BIZNUM"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">手写章图片路径：</td>
				<td style="width:80%"><input size="50" type="text" name="PHOTO_DATA">(部署Demo系统上的路径，印章类型为手写章时有效)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align="center" colspan="2">-----关键字盖章:-----</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">关键字：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_KEYWORDS"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align="center" colspan="2">-----坐标盖章:-----</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">左下角x坐标值：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_X"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">左下角y坐标值：</td>
				<td style="width:80%"><input size="50" type="text" name="PDF_Y"></td>
			</tr>		
			<tr bgcolor="#CAEEFF">
				<td align="center" colspan="2">-----骑缝盖章:-----</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">骑缝章：</td>
				<td style="width:80%">
					<input type="radio" name="PDF_QFZ" value="0" id="left"><label for="left">左骑缝</label>
					<input type="radio" name="PDF_QFZ" value="1" id="right"><label for="right">右骑缝</label>
				</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align="center" colspan="2">-----二维码:-----</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">二维码内容：</td>
				<td style="width:80%"><input size="50" type="text" name="BARCODE_CONTENT">(非空时二维码有效)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">左下角x坐标值：</td>
				<td style="width:80%"><input size="50" type="text" name="BARCODE_X"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">左下角y坐标值：</td>
				<td style="width:80%"><input size="50" type="text" name="BARCODE_Y"></td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">二维码宽：</td>
				<td style="width:80%"><input size="50" type="text" name="BARCODE_WIDTH">(为空时默认120)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right sytle="width:25%">页号：</td>
				<td style="width:80%"><input size="50" type="text" name="BARCODE_PAGENUM">(为空时默认1)</td>
			</tr>
			
			<tr bgcolor="#CAEEFF">
				<td colspan="2" align=center>-----盖章后PDF文件输出路径-----</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td align=right style="width:25%">盖章后PDF文件输出路径：</td>
				<td style="width:80%"><input size="50" type="text" name="pdfStampPath" value="f:/temp/stamp.pdf">(部署Demo系统上的路径)</td>
			</tr>
			<tr bgcolor="#CAEEFF">
				<td colspan=2 align=center><input type="submit" value="提交">
				</td>
			</tr>
		</table><p>&nbsp;</p>
	</form>

</body>
</html>
