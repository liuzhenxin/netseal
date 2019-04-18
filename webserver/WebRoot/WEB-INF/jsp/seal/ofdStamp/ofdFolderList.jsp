<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top: 20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="x_panel">
						<div class="x_title" id="x_title">
							<div class="col-md-10 col-sm-10 col-xs-10">
								<input id="allO" type="button" value="全选" class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="新建"  class="btn btn-primary" style=" float:left;margin-left:10px;" data-toggle="modal" data-target="#addOfdFolderFormModel"/>
								<input type="button" value="删除"  onclick="delOfdFolder()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="查看"  onclick="viewOfdFolder()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="文件上传"  class="btn btn-primary" style=" float:left;margin-left:10px;" data-toggle="modal" data-target="#uploadOfdModal"/>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<div id="table-box" style="display: block;">
								<c:forEach items="${files }" var="files">
									<div style="float: left; margin: 10px;" align="center">
										<img id="timg" src="${ctx }/img/folder.jpg" width="80px" height="80px"/>
										<p>
											<input id="iOdir" name="checkboxt" class="input_checked" type="checkbox" value="${files }">
											${files }
										</p>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</div>

<div class="modal fade" id="addOfdFolderFormModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">新建文件夹</h4>
            </div>
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="addOfdFolderForm" action="${ctx }/sealManage/ofdStamp/addOfdFolder.do" method="post" data-parsley-validate="" 
									class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
										<div class="form-group" id="">
											<label class="control-label col-xs-3">文件夹名称</label>
											<div class="col-xs-6" style="margin-top: 7px">
												<input type="text" class="form-control input-xs" name="folderName" id="folderName">
											</div>
										</div>
										
										<br>
										<div class="form-group">
											<button id="addOfdFolder" type="button" class="btn btn-primary col-md-offset-4">确定</button>
											<button id="closeOfdFolderMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</section>
				</div>
			</div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<div class="modal fade" id="uploadOfdModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">上传OFD文件</h4>
            </div>
            <input id="ids" value="" type="hidden" />
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="uploadOfdForm" action="${ctx }/sealManage/ofdStamp/uploadOfdFile.do" method="post" autocomplete="off" enctype="multipart/form-data"
										data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
										<input type="hidden" name="ids" id="upOfdId">
										<div class="form-group">
											<label class="control-label col-xs-3">
												OFD文件包(.zip)
											</label>
											<div class="col-xs-6">
												<input id="ofdFile" name="ofdFile" type="file" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label for="pwd" class="control-label col-xs-3">
												压缩密码(*可选)
											</label>
											<div class="col-xs-6">
												<input id="pwd" name="pwd" type="password" class="form-control col-xs-6">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-1"></label>
											<label for="pwd" class="control-label col-xs-8" style="text-align: center; color: red">
												注: zip包中单个文件大小不超过3M, 文件夹数量需少于100<br>
												名字重复的ofd文件会被覆盖
											</label>
										</div>
										<br>
										<div class="form-group">
											<button id="uploadOfdFile" type="button" class="btn btn-primary col-md-offset-4">上传</button>
											<button id="closeUpOfdMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</section>
				</div>
			</div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
<script type="text/javascript">

	//全选
	$(function(){
		$("#allO").click(function() {
			if ($(".input_checked").prop("checked")) {//先判断该checkbox是否已经被先中。
				$(".input_checked").removeAttr("checked", false);
			} else {
				$(".input_checked").prop("checked", true);
			}
		});
	});

	//模态框关闭
	$("#closeOfdFolderMode").click(function() {
		$("#folderName").val('');
		$("#addOfdFolderFormModel").modal('hide');
	});
	
	$(function(){
		$("#addOfdFolderForm").bootstrapValidator({
			fields: {
				folderName: {
	                validators: {
	                    notEmpty: {
	                        message: '文件夹不能为空'
	                    },
	                   
	                    regexp: {
	                        regexp: /^[a-zA-Z0-9_]{0,}$/,
	                        message: '只能为字母,数字或下划线'
	                    }
	                   
	                }
	            }
	         }
	 	});
		
		// 新增文件夹 
		$("#addOfdFolder").click(function(){
			var form=$("#addOfdFolderForm");
			form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				form.ajaxSubmit({
					success:function(data){
						if(data.success){
							$(".modal-backdrop").remove();
					    	layer.alert("新建文件夹成功",{icon:1});
					    	loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
						} else {
							layer.alert( data.message, {icon:2});
						}
					},error:function(){
						layer.alert("请求失败",{icon:2});
					}
				}); 
			 }else{
				 form.bootstrapValidator('validate');
			 }
		});

	});
	
	
	// 删除文件夹
	function delOfdFolder(){
		var ids = "";
		var index = 0;
		$("[id='iOdir']:checkbox").each(function() {
			if (this.checked) {
				if (index == 0) {
					ids = $(this).val();
				} else{
					ids= ids + ";" + $(this).val();
				}
				index++;
			}
		});
		
		if (index == 0) {
			layer.alert( "请选择要删除的文件夹", {icon:0});
			return;
		}
		
		
		layer.confirm("确定要删除文件夹及文件?",{btn:["确定","取消"]},function(){
			$.ajax({
				url :"${ctx }/sealManage/ofdStamp/delOfdFolder.do",
				type : "post",
				data: "ids=" + ids,
				dataType : "json",
				success : function(data) {
					if(data.success){
						layer.alert("删除成功",{icon:1});
						loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
					}else{
						layer.alert(data.message,{icon:2});
						loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
					}
				},
				error : function() {
					layer.alert("请求失败",{icon:2});
					loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
				}
			});
		});
	}
	
	// 查看文件夹
	function viewOfdFolder(){
		var ids = "";
		var index = 0;
		$("[id='iOdir']:checkbox").each(function() {
			if (this.checked) {
				if (index == 0) {
					ids = $(this).val();
				} else{
					ids= ids + ";" + $(this).val();
				}
				index++;
			}
		});
		if (index == 0  || index > 1) {
			layer.alert( "请选择一个文件夹", {icon:0});
			return;
		}
		
		loadTab("${ctx }/sealManage/ofdStamp/viewOfdFolder.do?ids=" + ids, "tab1");
	}
	
	// ofd文件上传
	//模态框关闭
	$("#closeUpOfdMode").click(function() {
		$("#ofdFile").val('');
		$("#pwd").val('');
		$("#uploadOfdModal").modal('hide');
	});
	
	// 上传ofd文件
	$("#uploadOfdFile").click(function(){
		var ids = "";
		var index = 0;
		$("[id='iOdir']:checkbox").each(function() {
			if (this.checked) {
				if (index == 0) {
					ids = $(this).val();
				}
				index++;
			}
		});
		
		if (index == 0  || index > 1) {
			layer.alert( "请选择一个文件夹", {icon:0});
			return;
		}
		
		var ofdFile = $('#ofdFile').val();
		if (!ofdFile){
			layer.alert( "请选择ofd文件压缩包", {icon:0});
			return;
		}
		
		$("#upOfdId").val(ids);
		var form = $("#uploadOfdForm");
		form.ajaxSubmit({
			success : function(data) { 
				if (data == "ok"){
					$(".modal-backdrop").remove();
					layer.alert("上传成功", {icon:1});
					loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
				} else {
					$(".modal-backdrop").remove();
					layer.alert(data, {icon:2});
					loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
				}
			},
			error : function() {
				$(".modal-backdrop").remove();
				layer.alert( "上传失败", {icon:2});
				loadUrl("${ctx }/sealManage/ofdStamp/ofdStampManage.do");
			}
		});
	});
	
</script>