<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
        <!-- Main content -->
        
<section class="content">						
<div class="row">
  <div class="col-md-12 col-sm-12 col-xs-12">
	<div class="x_panel">
				 
	
<div class="table-responsive">
  <table class="table table-striped table-bordered dt-responsive nowrap" >
    <thead>
      <tr>
        <th>文件名</th>
        <th>修改时间</th>
        <th>大小(KB)</th>
        <th>操作</th>
       </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.result}" var="debugLog">
      <tr>
        <td>${debugLog.fileName }</td>
        <td>${debugLog.fileTimeCn }</td>
        <td>${debugLog.fileSize }</td>
        <td>
        <a href="javascript:viewLog('${debugLog.fileName }')">查看</a>
        <a href="${ctx }/log/downloadFile.do?fileName=${debugLog.fileName }">下载</a>
        </td>
       </tr>
    </c:forEach>
    </tbody>
    
</table>
 <div class="text-right" id="debugLogFilePage"></div>
	</div>          	
 </div>
	  </div>
	</div>
</section>

<script type="text/javascript">
$(function(){
		laypage({
			cont : 'debugLogFilePage',
			skip : true,//跳转页面选项
			pages : '${page.totalPage}',
			curr : function() {
				var pageNo = '${page.pageNo}';
				return pageNo ? pageNo : 1;
			}(),
			jump : function(e, first) {
				if (!first) {
					$('#tab1').load("${ctx }/log/debugLogFileList.do?pageNo=" + e.curr);
				}
			}
		});
});
function viewLog(fileName){
	$('#tab1').load("${ctx }/log/viewFile.do?tab=tab1&fileName=" +fileName);
}

</script>

