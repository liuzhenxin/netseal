<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
				  <div class="x_content" id="tab1">
	<table id="datatable-responsive1" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
		<thead>
			<tr>
				<th>文件名</th>
				<th>修改时间</th>
				<th>大小(KB)</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.result}" var="accessLog">
				<tr>
					<td>${accessLog.fileName }</td>
					<td>${accessLog.fileTimeCn }</td>
					<td>${accessLog.fileSize }</td>
					<td><a href="javascript:viewLog('${accessLog.fileName }')">查看</a>
						<a
						href="${ctx }/log/downloadFile.do?fileName=${accessLog.fileName }">下载</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="text-right" id="accessLogFilePage"></div>
	    </div>
		</div>
	  </div>
	</div>
</section>


<script type="text/javascript">
	$(function() {
		laypage({
			cont : 'accessLogFilePage',
			skip : true,//跳转页面选项
			pages : '${page.totalPage}',
			curr : function() {
				var pageNo = '${page.pageNo}';
				return pageNo ? pageNo : 1;
			}(),
			jump : function(e, first) {
				if (!first) {
					$('#tab2')
							.load(
									"${ctx }/log/accessLogFileList.do?pageNo="
											+ e.curr);
				}
			}
		});
	});
	function viewLog(fileName) {
		$('#tab2').load("${ctx }/log/viewFile.do?tab=tab2&fileName=" + fileName);
	}
</script>

