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
								<input type="button" value="PDF文件上传"  class="btn btn-primary" style=" float:left;margin-left:10px;" data-toggle="modal" data-target="#uploadPdfModal"/>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<div id="table-box" style="display: block;">
								<c:forEach items="${files }" var="files">
									<div style="float: left; margin: 10px;" align="center">
										<img id="timg" src="${ctx }/img/folder.jpg" width="80px" height="80px"/>
										<p>
											<input id="iUdir" name="checkboxt" class="input_checked" type="checkbox" value="${files }">
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
<div class="modal fade" id="uploadPdfModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">上传PDF文件</h4>
            </div>
            <input id="ids" value="" type="hidden" />
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="uploadPdfForm" action="${ctx }/sealManage/pdfStamp/uploadPdfFile.do" method="post" autocomplete="off" enctype="multipart/form-data"
										data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
										<input type="hidden" name="ids" id="upPdfId">
										<div class="form-group">
											<label for="recoverKeyFile" class="control-label col-xs-3">
												PDF文件包(.zip)
											</label>
											<div class="col-xs-6">
												<input id="pdfFile" name="pdfFile" type="file" class="form-control col-xs-6">
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
										<br>
										<div class="form-group">
											<button id="uploadPdfFile" type="button" class="btn btn-primary col-md-offset-4">上传</button>
											<button id="closeUpPdfMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
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

	//模态框关闭
	$("#closeUpPdfMode").click(function() {
		$("#pdfFile").val('');
		$("#pwd").val('');
		$("#uploadPdfModal").modal('hide');
	});
	
	// 上传PDF文件
	$("#uploadPdfFile").click(function(){
		var ids = "";
		var index = 0;
		$("[id='iUdir']:checkbox").each(function() {
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
		
		var pdfFile = $('#pdfFile').val();
		if (!pdfFile){
			layer.alert( "请选择PDF文件压缩包", {icon:0});
			return;
		}
		
		$("#upPdfId").val(ids);
		var form = $("#uploadPdfForm");
		form.ajaxSubmit({
			success : function(data) { 
				if (data == "ok"){
					$(".modal-backdrop").remove();
					layer.alert("上传成功", {icon:1});
					loadTab("${ctx }/sealManage/pdfStamp/pdfUploadManage.do", "tab2");
				} else {
					$(".modal-backdrop").remove();
					layer.alert(data, {icon:2});
					loadTab("${ctx }/sealManage/pdfStamp/pdfUploadManage.do", "tab2");
				}
			},
			error : function() {
				$(".modal-backdrop").remove();
				layer.alert( "上传失败", {icon:2});
				loadTab("${ctx }/sealManage/pdfStamp/pdfUploadManage.do", "tab2");
			}
		});
	});
	
</script>