<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<section class="content-header">
	        	<div class="headercontainer">
		        	<ul style="background-color: transparent;" class="breadcrumb location">
		      			<li><a style="text-decoration:none;cursor:default">用户管理/签章人管理/用户信息查询</a></li>
		    		</ul>
	        	</div>
</section>
	  
<section class="content">
	<div class="main">
		<div class="table-responsive sys-tab" align="center">
		 <form  class="form-horizontal"  action="" method="post">
				<div class="form-group">
					<label for="accountE" class="col-sm-3 control-label">姓名</label>
					<div class="col-sm-3">
						<input  name="account" readonly="readonly" class="form-control input-sm" type="text" value="${user.name }"/>
					</div>
				</div>
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">单位</label>
					<div class="col-sm-3">
						<input  name="name" readonly="readonly" class="form-control input-sm" type="text" value="${user.companyName }"/>
					</div>
				</div>	
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">手机</label>
					<div class="col-sm-3">
						<input  name="name" readonly="readonly" class="form-control input-sm" type="text" value="${user.phone }"/>
					</div>
				</div>	
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">邮箱</label>
					<div class="col-sm-3">
						<input  name="name" readonly="readonly" class="form-control input-sm" type="text" value="${user.email }"/>
					</div>
				</div>	
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">注册时间</label>
					<div class="col-sm-3">
						<input  name="name" readonly="readonly" class="form-control input-sm" type="text" value="${generateTimeCN }"/>
					</div>
				</div>
				<div class="form-group">
					<label for="name" class="col-sm-3 control-label">已注册证书</label>
					<div class="col-sm-3">
						<select class="form-control input-sm selectCertDN">
							<c:forEach items="${listCert }" var="CertDN">
								<option>${CertDN.certDN }</option>
							</c:forEach>
						</select>
					</div>
				</div>														
				<div class="form-group">
			        <div class="col-sm-11">
			           <button type="button" id="back" class="btn btn-primary btn-sm">返回</button>
			        </div>
			    </div>	
			</form>
			</div>

		</div>
</section>

<script type="text/javascript">
	$(function() {
		
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/userManage/userList.do");
		});
	});


</script>