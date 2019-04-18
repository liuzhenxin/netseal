<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
        <!-- Main content -->
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-xs-12">
							<div class="x_panel" style="overflow:hidden;">	

  <table class="table table-striped table-bordered dt-responsive nowrap">
    <thead>
      <tr>
        <th>文件名</th>
        <th>修改时间</th>
        <th>大小(KB)</th>
        <th>操作</th>
       </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.result}" var="haLog">
      <tr>
        <td>${haLog.fileName }</td>
        <td>${haLog.fileTimeCn }</td>
        <td>${haLog.fileSize }</td>
        <td>
        <a href="javascript:viewLog('${haLog.fileName }')">查看</a>
        <a href="${ctx }/system/ha/haDownFile.do?fileName=${haLog.fileName }">下载</a>
        </td>
       </tr>
    </c:forEach>
    </tbody>
    
</table>
<div class="text-right" id="haLogFilePage"></div>

                </div>
			  </div>
			</div>
		  </section>
	    </div>
  	  </div>  
         	


<script type="text/javascript">
$(function(){
		laypage({
			cont : 'haLogFilePage',
			skip : true,//跳转页面选项
			pages : '${page.totalPage}',
			curr : function() {
				var pageNo = '${page.pageNo}';
				return pageNo ? pageNo : 1;
			}(),
			jump : function(e, first) {
				if (!first) {
					$('#tab3').load("${ctx }/system/ha/haLog.do?pageNo=" + e.curr);
				}
			}
		});
});
function viewLog(fileName){
	$('#tab3').load("${ctx }/system/ha/haViewFile.do?tab=tab3&fileName=" +fileName);
}

</script>

