<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<%@include file="resource.jsp"%>

<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=9">
  <title>NetSeal电子签章管理系统</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <script type="text/javascript">
	$.ajaxSetup({aysnc: false, cache : false});
	
	var timer;
	function loadUrl(url) {
		loadTab(url, "content");		
	}
	
	function loadTab(url, id) {
		clearTimeout(timer);
		$("#"+id).load(url,function(responseTxt,statusTxt,xhr){
			if(xhr.readyState == 4){
				if(xhr.status == 404)
			    	layer.alert("请求失败, 找不到请求资源",{icon:2});
				if(xhr.status == 500)
			    	layer.alert("请求失败, 发生内部错误",{icon:2});
			}else{
				layer.alert("请求失败, 未完成响应",{icon:2});
			}
		});
	}
	
	
  </script>

</head>
<body class="hold-transition skin-blue sidebar-mini" style="margin:0px; padding:0px; position:relative; background-color: rgb(236,240,245);">
	<div class="wrapper" style="width:100%; position:absolute; top:0px; left:0px; background-color: rgb(236,240,245);">


	  <header class="main-header" id="top" style="margin:0px; z-index:1000; position:fixed;">
		<%@include file="header.jsp"%>
	  </header>
	  <aside class="main-sidebar" style="position:absolute; z-index:999; height:100%; position:fixed;" id="main">
		 <%@include file="left.jsp"%>
	  </aside>

	  <div class="content-wrapper" style="height:100%; overflow-x: hidden; overflow-y: auto;">
		<section class="content container-fluid" id="content" style="padding-top:50px; padding-bottom:50px; overflow-x: auto; overflow-y: auto;">

		</section>
	  </div>

	  <footer class="main-footer" id="bottom" style="width:100%;  z-index:9; position:fixed; left:0; bottom:0; margin-left:-1px;">
		<%@include file="footer.jsp"%>
	  </footer>

	</div>	

</body>
</html>