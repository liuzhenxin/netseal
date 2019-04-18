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
							<div class="col-md-4 col-sm-4 col-xs-4">
								<input type="button" value="删除"  onclick="delOfd()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="返回"  onclick="back()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
							</div>
							<div class="clearfix"></div>
						</div>
						<input type="hidden" value="${folder }" id="folder"/>
						<div class="table-responsive">
							<table class="table table-striped table-bordered dt-responsive nowrap">
								<thead>
									<tr>
										<th width="1"><input id="checkboxChange" class="js-checkbox-all" type="checkbox"></th>
										<th>文件名</th>
										<th>修改时间</th>
										<th>大小(KB)</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${files }" var="files">
										<tr>
											<td width="1"><input id="ido" name="checkboxt" class="input_checked" type="checkbox" value="${files.fileName }"></td>
											<td>${files.fileName }</td>
											<td>${files.fileTimeCn }</td>
											<td>${files.fileSize }</td>
										</tr>
									</c:forEach>
								</tbody>

							</table>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		//---复选框样式
		icheck(".js-checkbox-all");
	});

	//文件删除
	function delOfd(){
		var ids = "";
		var index = 0;
		$("[id='ido']:checkbox").each(function() {
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
			layer.alert( "请选择删除对象", {icon:0});
			return;
		}
		
		layer.confirm("确定要删除文件?",{btn:["确定","取消"]},function(){
			var folder = $("#folder").val();
			$.ajax({
				url :"${ctx }/sealManage/ofdStamp/delOfd.do",
				type : "post",
				data : {"ids":ids,"folder":folder},
				dataType : "json",
				success : function(data) {
					if(data.success){
						layer.alert("删除成功",{icon:1});
						loadTab("${ctx }/sealManage/ofdStamp/viewOfdFolder.do?ids=" + folder, "tab1");
					}else{
						layer.alert(data.message,{icon:2});
						loadTab("${ctx }/sealManage/ofdStamp/viewOfdFolder.do?ids=" + folder, "tab1");
					}
				},
				error : function() {
					layer.alert("请求失败",{icon:2});
					loadTab("${ctx }/sealManage/ofdStamp/viewOfdFolder.do?ids=" + folder, "tab1");
				}
			});
		});
	}
	
	// 返回
	function back(){
		loadTab("${ctx }/sealManage/ofdStamp/ofdStampManage.do", "tab1");
	}
	
</script>