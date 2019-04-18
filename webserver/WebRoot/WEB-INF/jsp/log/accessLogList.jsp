<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
	<section class="content">
	<div class="row">
		<div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
				<div class="x_content" id="">
					<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
						<tr>
							<th>操作类型</th>
							<th>客户IP</th>
							<th>操作时间</th>
							<th>返回码</th>
							<th>错误描述</th>
						</tr>
						<c:forEach items="${page.result}" var="accessLog">
							<c:choose>
								<c:when test="${accessLog.sealMac == true}">
									<tr>
										<td>${accessLog.optype }</td>
										<td>${accessLog.clientHost }</td>
										<td>${accessLog.generateTimeCn }</td>
										<td>${accessLog.returnCode }</td>
										<td>${accessLog.errMsg }</td>
									</tr>
								</c:when>
								<c:when test="${accessLog.sealMac == false}">
									<tr>
										<td class="errorMac">${accessLog.optype }</td>
										<td class="errorMac">${accessLog.clientHost }</td>
										<td class="errorMac">${accessLog.generateTimeCn }</td>
										<td class="errorMac">${accessLog.returnCode }</td>
										<td class="errorMac">${accessLog.errMsg }</td>
									</tr>
								</c:when>
							</c:choose>
						</c:forEach>
					</table>
					<div class="text-right" id="accessLogPage"></div>
				</div>
			</div>
		</div>
	</div>
</section>

<script type="text/javascript">


$(function(){
	
     page('accessLogPage','${page.totalPage}','${page.pageNo}',"${ctx }/log/accessLogList.do?pageNo=","#tab1");
});
</script>

