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
								<input id="allD" type="button" value="全选" class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="下载"  onclick="downloadOfd()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="删除"  onclick="delStampOfd()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="查看"  onclick="viewFolder()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input type="button" value="返回"  onclick="back()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<div id="table-box" style="display: block;">
								<c:forEach items="${stamOfd }" var="stamOfd">
									<div style="float: left;margin: 8px;" align="center">
										<img id="timg" src="${ctx }/img/folder.jpg" width="80px" height="80px"/>
										<p>
											<input id="iofd" name="checkboxt" class="input_checked" type="checkbox" value="${stamOfd }">
											${stamOfd }
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
<script type="text/javascript">
	//全选
	$(function(){
		$("#allD").click(function() {
			if ($(".input_checked").prop("checked")) {//先判断该checkbox是否已经被先中。
				$(".input_checked").removeAttr("checked", false);
			} else {
				$(".input_checked").prop("checked", true);
			}
		});
	});

	// OFD批量下载
	function downloadOfd(){
		var ids = "";
		var index = 0;
		$("[id='iofd']:checkbox").each(function() {
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
			layer.alert( "请选择下载对象", {icon:0});
			return;
		}
		
		$.ajax({
			url :"${ctx }/sealManage/ofdStamp/toDownloadStampOfd.do",
			type : "post",
			data : "ids=" + ids,
			dataType : "json",
			success : function(data) {
				if(data.success){
					window.location.href="${ctx }/sealManage/ofdStamp/downloadOfd.do?zipPath=" + data.message;
				}else{
					layer.alert(data.message,{icon:2});
					loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
				}
			},
			error : function() {
				layer.alert("请求失败",{icon:2});
				loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
			}
		});
	}
	
	// 文件删除
	function delStampOfd(){
		var ids = "";
		var index = 0;
		$("[id='iofd']:checkbox").each(function() {
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
		
		layer.confirm("确定要删除文件夹及文件?",{btn:["确定","取消"]},function(){	
			$.ajax({
				url :"${ctx }/sealManage/ofdStamp/delStampOfdFolder.do",
				type : "post",
				data : "ids=" + ids,
				dataType : "json",
				success : function(data) {
					if(data.success){
						layer.alert("删除成功",{icon:1});
						loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
					}else{
						layer.alert(data.message,{icon:2});
						loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
					}
				},
				error : function() {
					layer.alert("请求失败",{icon:2});
					loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
				}
			});
		});
	}
	
	// 查看文件夹
	function viewFolder(){
		var ids = "";
		var index = 0;
		$("[id='iofd']:checkbox").each(function() {
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
		
		loadTab("${ctx }/sealManage/ofdStamp/viewStampFolder.do?ids=" + ids, "tab3");
	}
	
	// 返回
	function back(){
		loadTab("${ctx }/sealManage/ofdStamp/ofdBatchStampManage.do", "tab3");
	}
	
</script>