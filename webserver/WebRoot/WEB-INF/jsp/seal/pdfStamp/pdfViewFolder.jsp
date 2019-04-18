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
								<input type="button" value="返回"  onclick="back()"  class="btn btn-primary" style=" float:left; margin-left:10px;" data-target=".bs-example-modal-sm12"/>
							</div>
							<div class="clearfix"></div>
						</div>
						<div class="table-responsive">
							<table class="table table-striped table-bordered dt-responsive nowrap">
								<thead>
									<tr>
										<th>文件名</th>
										<th>批签时间</th>
										<th>大小(KB)</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${files }" var="files">
										<tr>
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
//返回
function back(){
	loadTab("${ctx }/sealManage/pdfStamp/toDownloadPdf.do","tab3");
}
</script>