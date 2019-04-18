<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">添加机构</h2>
		</div>
  		<ul class="breadcrumb" style="">
			<li>
				<i class="fa fa-home"></i>
				<i style="color:rgb(42,63,84); font-style:normal;" id="bread">组织机构管理    /    添加机构</i>
			</li>
		</ul>		
		<ul id="myTab" class="nav nav-tabs" style="height:30px;">
			<li class="active">
			   <a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">添加机构</a>
			</li>
		</ul>
		<div id="myTabContent" class="tab-content" style="margin-top:20px;">
			<div class="tab-pane fade in active" id="home">
				<section class="content clearfix">						
					<div class="row">
					  <div class="col-md-12 col-sm-12 col-xs-12">
						<div class="x_panel">
							<div style="width:300px; min-height:400px; border:1px solid rgb(221,221,221); float:left;">
							<ul id="companyTree" class="ztree col-sm-3"></ul>
							</div>
							<div style="float:left; margin-left:50px; width:400px; height:400px;">
								<input type="button" value="添加" class="btn btn-primary" id="addCompanyButton" style="float:left; ">
								<input type="button" value="修改" class="btn btn-primary" id="editCompanyButton" style="float:left; margin-left:10px;">
								<input type="button" value="删除" class="btn btn-primary" id="delCompanyButton" style="float:left; margin-left:10px;" data-toggle="modal">
								<input id="currentId" type="hidden" />

								<div id="companyContent" style="margin-top:60px;">
								</div>
							</div>
						</div>
					  </div>
					</div>
				</section>
			</div>
</div>
<script type="text/javascript">
	$(function() {
		var setting = {
			data : {
				key : {
					title : "title"
				}
			},
			view : {
				fontCss : getFontCss,
				showTitle : true
			},
			async : {
				enable : true,
				url : "${ctx }/company/treeCompany.do",
				autoParam : [ "id" ]
			},
			callback : {
				beforeClick : function(treeId, treeNode) {
					var id = treeNode.id;
					$("#currentId").val(id);
					var url = "${ctx }/company/viewCompany.do?id=" + id;
					$("#companyContent").load(url);
				},
				onAsyncSuccess : function(event, treeId, treeNode, msg) {
					if (treeNode == null) {
						var treeObj = $.fn.zTree.getZTreeObj(treeId);
						var nodes = treeObj.getNodes();
						if (nodes.length > 0) {
							treeObj.expandNode(nodes[0], true, true, true);
						}
					}
				}
			}
		};

		$.fn.zTree.init($("#companyTree"), setting);

		//添加
		$("#addCompanyButton").click(function() {
			var pid = $("#currentId").val();
			if (pid == "") {
				layer.alert("请选择上级单位",{icon:0});
				return;
			}
			var url = "${ctx }/company/toAddCompany.do?pid=" + pid;

			$("#companyContent").load(url);

		});
		//修改
		$("#editCompanyButton").click(function() {
			var id = $("#currentId").val();
			if (id == "") {
				layer.alert("请选择上级单位",{icon:0});
				return;
			}
			var url = "${ctx }/company/toEditCompany.do?id=" + id;

			$("#companyContent").load(url);

		});

		//删除
		$("#delCompanyButton").click(
				function() {
					var id = $("#currentId").val();
					if (id == "") {
						layer.alert("请选择上级单位",{icon:0});
						return;
					}
					layer.confirm("确定要删除该单位吗?",{btn:["确定","取消"]},function(){
						$.ajax({
							url : "${ctx }/company/delCompany.do",
							type : "get",
							data : "id=" + id,
							dataType : "json",
							success : function(data) {			
									if (data.success) {
										layer.alert(data.message,{icon:1});
										var treeObj = $.fn.zTree.getZTreeObj("companyTree");
										var node = treeObj.getNodeByParam("id", id,	null);
										var parentNode = node.getParentNode();
										if (data.isParent == 0) {
											parentNode.isParent = false;
											treeObj.updateNode(parentNode);
										}
										treeObj.reAsyncChildNodes(parentNode,"refresh");
										$("#companyContent").html("");
										$("#currentId").val("");
									}else{
										layer.alert(data.message,{icon:2});
									}
							},
							error : function() {
								layer.alert("请求失败",{icon:2});
							}
						});
					});
// 					swal({
// 						title: "确定要删除该单位吗?", 
// 						text: "", 
// 						type: "warning",
// 						showCancelButton: true,
// 						closeOnConfirm: false,
// 						confirmButtonText: "确定",
// 						confirmButtonColor: "#ec6c62"
// 						}, 
// 						function() {
// 							$.ajax({
// 								url : "${ctx }/company/delCompany.do",
// 								type : "get",
// 								data : "id=" + id,
// 								dataType : "json",
// 								success : function(data) {			
// 										if (data.success) {
// 											layer.alert(data.message,{icon:1});
// 											var treeObj = $.fn.zTree.getZTreeObj("companyTree");
// 											var node = treeObj.getNodeByParam("id", id,	null);
// 											var parentNode = node.getParentNode();
// 											if (data.isParent == 0) {
// 												parentNode.isParent = false;
// 												treeObj.updateNode(parentNode);
// 											}
// 											treeObj.reAsyncChildNodes(parentNode,"refresh");
// 											$("#companyContent").html("");
// 											$("#currentId").val("");
// 										}else{
// 											layer.alert(data.message,{icon:2});
// 										}
// 								},
// 								error : function() {
// 									layer.alert("请求失败",{icon:2});
// 								}
// 							});
// 						}
// 					);
					

				});
	});
	function getFontCss(treeId, node) {
		return node.fontCss ? node.fontCss : {};
	}
</script>