<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">统计</h2>
</div>
<ul class="breadcrumb" style="">
	<li><i class="fa fa-home"></i> <i
		style="color:rgb(42,63,84); font-style:normal;" id="bread">统计 /
			印章统计</i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab"
		style="height:30px; line-height:3px;" id="tabTitle"> 印章统计</a></li>
</ul>


<div id="myTabContent" class="tab-content" style="margin-top:20px;">

	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<!--  
						<div class="x_title">
							<div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix"
								id="input_box" style="width:334px; margin:0px; float:right;  ">
								<div class="input-group"
									style="margin:0px; margin-right:0px; float:left;">
									<input id="searchTime" type="text"
										class="form-control select2-search__field" type="search"
										value="" style="border-color: rgb(230,233,237); width:240px;">
									<span class="input-group-btn pull-left" style="width:54px;">
										<button class="btn btn-primary" type="button"
											style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
									</span>
								</div>
							</div>
							<select class="form-control" id="select"
								style="width:150px; float:right;">
								<option>生效时间(查找格式:yyyy-MM-dd)</option>
							</select>
							<div class="clearfix"></div>
						</div>
						-->
						<div class="main">
							<form id="demo-form" class="form-horizontal form-label-left"
								style="margin-top:20px;">
								<div class="form-group">
									<label class="control-label col-xs-3">印章数量 </label>
									<div id="countS" class="col-xs-8">
										<p id="countId" class="col-xs-8 showdetail">${sealNo }</p>
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
function searchAccount() {
	var  firstSearch =$("#searchTime").val();
	if (firstSearch == "")
		return true;
	
	var r = firstSearch.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
	if (r != null) {//判断输入格式是否正确  yyyy-MM-dd
		var url = "${ctx }/count/sealCountSearch.do?searchTime="+ firstSearch;
		$.ajax({
			type : "get",
			dataType : "json",
			url : url,
			success : function(jsonResult) {
				$("#searchTime").val("");
				//遍历一个数组or集合
				var ta = jsonResult.sealNo;
				if (ta == "") {
					layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
					//追加html文本
					$("#countId").remove();
					$("#countS").html("<p>" + '' + "</p>");
				} else {
					//追加html文本
					$("#countId").remove();
					$("#countS").html("<p>" + ta + "</p>");
				}
			},
			error : function() {
				layer.alert( "查询错误,请输入正确格式		yyyy-MM-dd", {icon:2});
				//追加html文本
				$("#countId").remove();
				$("#countS").html("<p>" + '' + "</p>");
			}
		});

	}else{
		layer.alert( "请输入正确格式:yyyy-MM-dd", {icon:2});
		//追加html文本
		$("#countId").remove();
		$("#countS").html("<p>" + '' + "</p>");
	}

}
</script>


