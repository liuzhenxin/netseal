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
								<input id="allOS" type="button" value="全选" class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
								<input id="stamp" type="button" value="批签" class="btn btn-primary" style=" float:left;margin-left:10px;" data-toggle="modal" data-target="#myModal"/>
								<input type="button" value="文件下载"  onclick="downloadOfd()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<div id="table-box" style="display: block;">
								<c:forEach items="${files }" var="files">
									<div style="float: left; margin: 10px;" align="center">
										<img id="timg" src="${ctx }/img/folder.jpg" width="80px" height="80px"/>
										<p>
											<input id="iOfdir" name="checkboxt" class="input_checked" type="checkbox" value="${files }">
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
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">OFD批签</h4>
            </div>
            <input id="ids" value="" type="hidden" />
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="x_panel">
									<form id="" action="" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
										<div class="form-group">
											<label class="control-label col-xs-3">印章名称</label>
											<div class="col-xs-6">
												<select class="form-control  input-sm" name="name" id="name">
													<option value="0">- 请选择 -</option>
													<c:forEach items="${sealList }" var="slist">
														<option value="${slist.name }">${slist.name }</option>
													</c:forEach>
												</select> 
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-xs-3">盖章类型</label>
											<div class="col-xs-6">
												<select class="form-control input-sm col-xs-6" name="type" id="types">
													<option value="0">- 请选择 -</option>
													<option value="1">关键字盖章</option>
													<option value="2">坐标盖章</option>
													<option value="3">骑缝盖章</option>
												</select>
											</div>
										</div>
										<div class="form-group" id="keyW">
											<label class="control-label col-xs-3">关键字</label>
											<div class="col-xs-6" style="margin-top: 7px">
												<input type="text" class="form-control input-xs" name="keyWord" id="keyWord">
											</div>
										</div>
										<div class="form-group" id="xyVal">
											<div class="form-group">
												<label class="control-label col-xs-3">X坐标</label>
												<div class="col-xs-6">
													<input type="text" class="form-control" id="X">
												</div>
											</div>
											<div class="form-group">
												<label class="control-label col-xs-3">Y坐标</label>
												<div class="col-xs-6">
													<input type="text" class="form-control" id="Y">
												</div>
											</div>
										</div>
										<div class="form-group" id="ofd_Qfz">
											<label class="control-label col-xs-3">骑缝位置</label>
											<div class="col-xs-6" style="margin-top: 7px">
												<input type="radio" name="OFD_QFZ" id="QFZL" value="0"><label for="QFZL">左骑缝</label>
												<input type="radio" name="OFD_QFZ" id="QFZR" value="1" style="margin-left: 20px;" ><label for="QFZR">右骑缝</label>
											</div>
										</div>
										<br>
										<div class="form-group">
											<button id="stampOfd" onclick="batchOfdStamp()" type="button" class="btn btn-primary col-md-offset-4">批签</button>
											<button id="closeMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
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

	$(function(){
		document.getElementById("keyW").style.display="none";
		document.getElementById("xyVal").style.display="none";//隐藏
		document.getElementById("ofd_Qfz").style.display = "none";//隐藏
	});
	
	//全选
	$(function(){
		$("#allOS").click(function() {
			if ($(".input_checked").prop("checked")) {//先判断该checkbox是否已经被先中。
				$(".input_checked").removeAttr("checked", false);
			} else {
				$(".input_checked").prop("checked", true);
			}
		});
	});

	$("#types").change(function() {
		var optValue = $("#types").val();
		if (optValue == 1) { //关键字盖章
			document.getElementById("keyW").style.display = "";
			document.getElementById("xyVal").style.display = "none";//隐藏
			document.getElementById("ofd_Qfz").style.display = "none";//隐藏
			$("#X").val('');
			$("#Y").val('');
			$("#QFZL").val('');
			$("#QFZR").val('');
		} else if (optValue == 2) { // 坐标盖章
			document.getElementById("xyVal").style.display = "";
			document.getElementById("keyW").style.display = "none";
			document.getElementById("ofd_Qfz").style.display = "none";//隐藏
			$("#xyVal").val('');
			$("#QFZL").val('');
			$("#QFZR").val('');
		} else { // 骑缝章
			document.getElementById("ofd_Qfz").style.display = "";
			document.getElementById("keyW").style.display = "none";
			document.getElementById("xyVal").style.display = "none";
			$("#xyVal").val('');
			$("#X").val('');
			$("#Y").val('');
		}
	});

	//模态框关闭
	$("#closeMode").click(function() {
		$("#X").val('');
		$("#Y").val('');
		$("#keyWord").val('');
		$("#QFZL").val('');
		$("#QFZR").val('');
		$("#myModal").modal('hide');
	});

	// 批签
	function batchOfdStamp() {
		var name = $("#name").val();
		if (name == 0) {
			layer.alert("请选择印章", {
				icon : 0
			});
			return;
		}

		var type = $("#types").val();
		if (type == 0) {
			layer.alert("请选择盖章类型", {icon : 0});
			return;
		}
		
		var ids = "";
		var index = 0;
		$("[id='iOfdir']:checkbox").each(function() {
			if (this.checked) {
				if (index == 0) {
					ids = $(this).val();
				} else {
					ids = ids + ";" + $(this).val();
				}
				index++;
			}
		});

		if (index == 0) {
			layer.alert("请选择批签文件夹", {icon : 0});
			return;
		}

		$("#ids").val(ids);
		var keyWord = $("#keyWord").val();
		$.ajax({
			url : "${ctx }/sealManage/ofdStamp/ofdBatchStamp.do",
			type : "post",
			data : {
				"ids" : ids,
				"name" : name,
				"type" : type,
				"keyWord" : keyWord,
				"X" : $("#X").val(),
				"Y" : $("#Y").val(),
				"QFZ" : $('input[name="OFD_QFZ"]:checked').val()
			},
			dataType : "json",
			success : function(data) {
				if (data.success) {
					$(".modal-backdrop").remove();
					layer.alert("批签成功", { icon : 1 });
					loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
				} else {
					$(".modal-backdrop").remove();
					layer.alert(data.message, { icon : 2 });
					loadTab("${ctx }/sealManage/ofdStamp/ofdBatchStampManage.do", "tab3");
				}
			},
			error : function() {
				$(".modal-backdrop").remove();
				layer.alert("请求失败", { icon : 2 });
				loadTab("${ctx }/sealManage/ofdStamp/ofdBatchStampManage.do", "tab3");
			}
		}); 
	}

	// 下载已盖章OFD文件
	function downloadOfd() {
		loadTab("${ctx }/sealManage/ofdStamp/toDownloadOfd.do", "tab3");
	}
</script>