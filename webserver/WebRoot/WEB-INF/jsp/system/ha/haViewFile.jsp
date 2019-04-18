<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
	   
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content" style="overflow-x:auto;">						
						<div class="row" style="margin:0;">
						  <div class="col-xs-12">
							<div class="x_panel" >	   
	  	<table id="logPartTable" class="table table-striped table-bordered dt-responsive nowrap">
		     
			   <c:forEach items="${page.result}" var="logInfo">
			      <tr>
			        <td>${logInfo}</td>
			        
			       </tr>
			   </c:forEach>
		</table>
		<div class="text-right" id="viewFilePage"></div>
		
 </div>
			  </div>
			</div>
		  </section>
	    </div>
  	  </div>  		

<script type="text/javascript">

$(function(){
	
    page('viewFilePage','${page.totalPage}','${page.pageNo}',"${ctx }/system/ha/haViewFile.do?tab=${tab}&fileName=${fileName}&pageNo=",'#${tab}');
        
  });
  

</script>

