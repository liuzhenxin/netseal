<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

	<section class="content" >						
		<div class="row" >
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
				${viewDetails}
		   </div>
		   <div class="form-actions">
				<div class="col-md-offset-3 col-xs-6">
					<input class="btn btn-primary col-md-offset-6" id="back" type="button" value="返回">
				</div>
			 </div>
			</div>
		</div>
	</section>

	
<script type="text/javascript">
$("#back").click(function(){
	$('#tab2').load("${ctx }/sysUser/sysVersion.do");
});
</script>