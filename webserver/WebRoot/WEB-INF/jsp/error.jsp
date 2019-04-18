<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>error</title>
</head>
<body>
<div class="container" style=";margin:5% 3%;background-color:#3c8dbc;padding:5%;height:330px">

 <div style="float:right;width:60%;" >
    <div class=" col-md-5 ">
		<div style="color:#e9f3ff;font-size:52px;font-weight:bold">SORRY!</div><hr/>
        <div style="color:crimson;font-size:16px;font-weight:bold">无法找到您要查询的页面,请重试</div><br/>
        <div style="font-size:15px;">${exception}</div><br/>
        <div>
         <a href="../index.jsp"><input type="button" class="btn btn-primary" value="返回首页" /></a>
        </div>
	</div>
  </div>
  <div align="center" style="display:inline;width:40%;float:right" class="row">
     <span style="font-size:120px;color:#e9f3ff">404</span>
  </div>
</div>
</body>
</html>