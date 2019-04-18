<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>


<section class="content">
	<div class="row">
		<div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			<div class="table-responsive">
				<table id="logTable" class="table table-striped table-bordered dt-responsive nowrap" >
				
			     <thead>
			    	<tr>
			        	<th>版本号</th>
			         	<th>时间</th>
			         	<th>操作</th>
			        </tr>
			    </thead>
			    <tbody>
			    	<c:forEach items="${updateLogList}" var="updateLog" >
					<tr>
				        <td>${updateLog.version }</td>
				        <td>${updateLog.date }</td>
				        <td>
				        <a href="javascript:viewLog('${updateLog.version }')">详情</a>
				        </td>
			       </tr>
			       </c:forEach>
			    </tbody>
			</table>
			</div>
			</div>
			</div>
		</div>
</section>
	
<script type="text/javascript">
function viewLog(version){
	
	$('#tab2').load("${ctx }/sysUser/viewDetails.do?version="+version.split(" ").join(','));
} 
</script>