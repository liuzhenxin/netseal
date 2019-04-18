<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">管理员管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    管理员管理    /    管理员详情</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
		管理员详情</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
				  <div class="x_content">
					<br>
					<form id="demo-form4" class="form-horizontal form-label-left" style="margin-top:20px;" >
							<div class="form-group">
								<label class="control-label col-xs-3" >用户名 
								</label>
								<div class="col-xs-5">
								  <input class="form-control col-xs-5" type="text" value="${sysUser.account }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								  
								</div>
						    </div>
							<div class="form-group">
								<label class="control-label col-xs-3" >姓名 
								</label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.name }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
							<div class="form-group">
								<label class="control-label col-xs-3" >角色 
								</label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.roleName }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
							<div class="form-group">
								<label class="control-label col-xs-3" >单位 
								</label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.companyName }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
							<div class="form-group">
								<label class="control-label col-xs-3" >状态 
								</label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.statusCn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
							<div class="form-group">
								<label class="control-label col-xs-3" >注册时间 
								</label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.generateTimeCn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
							<div class="form-group">
								<label class="control-label col-xs-3" >证书主题 </label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.certDn }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
					  		<div class="form-group">
								<label class="control-label col-xs-3" >手机令牌 </label>
								<div class="col-xs-5">
								   <input class="form-control col-xs-5" type="text" value="${sysUser.tokenSeed }" style="border:none; border-bottom: 1px solid #444; background:none;" readonly/>
								</div>
						    </div>
					 
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
									  <button type="button" class="btn btn-primary col-md-offset-3" id="back" >返回</button>
								</div>
							</div>
						</div>

					</form>
					
					
				  </div>
				</div>
			  </div>
			</div>
		</section>
	</div>
</div>





<script type="text/javascript">
	$(function() {
		
		//返回
		$("#back").click(function() {
			loadUrl("${ctx }/sysUser/sysUserList.do");
		});
	});


</script>