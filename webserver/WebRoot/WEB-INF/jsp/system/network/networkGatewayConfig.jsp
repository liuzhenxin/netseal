<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
			<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">网络配置</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-home"></i>
					<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    / 网络配置  /   网关设置</i>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
						网关设置</a>
				</li>
			</ul>
  			
  			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12"> 
							<div class="x_panel">
							
								<form id="networkGatewayConfigForm"  action="${ctx }/system/network/networkGatewayConfigSave.do" method="post"  data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;" >

								  <div class="form-group">
									<label class="control-label col-xs-3" >IPV4默认网关 
									</label>
									<div class="col-xs-6">
									  <input type="text" id="gatewayIp" name="gatewayIp" value="${gatewayIp}" required="required" class="form-control col-xs-6">
									</div>
								  </div>
								  <div class="form-group">
									<label class="control-label col-xs-3" >IPV6默认网关 
									</label>
									<div class="col-xs-6">
									  <input type="text" id="gatewayIpv6" name="gatewayIpv6" value="${gatewayIpv6}" required="required" class="form-control col-xs-6">
									</div>
								  </div>
								  <div class="ln_solid"></div>
									<div class="form-actions">
										<div class="row">
											<div class="col-md-offset-3 col-xs-6">
											  <button  id="networkGatewayConfigSave" type="button"  class="btn btn-primary col-md-offset-3" >保存</button>
											  <button class="btn btn-primary col-md-offset-3" type="button" id="returnButton">返回</button>
											</div>
										</div>
									</div>
								</form>
							
							</div>
						  </div>
						</div>
					</section>
				</div>
			</div>





<script type="text/javascript">
	$(function() {	
		$("#returnButton").click(function(){
			loadUrl("${ctx }/system/network/networkList.do");
			
		});
		 $("#networkGatewayConfigSave").click(function(){
			 var form = $("#networkGatewayConfigForm");
			 form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				form.ajaxSubmit({
					success : function(data) {
						if (data.success) {
							layer.alert( data.message, {icon:1});
							loadUrl("${ctx }/system/network/networkList.do");
						} else {
							layer.alert( data.message,{icon:2});
						}

					},
					error : function() {
						layer.alert( "请求失败", {icon:2});
					}
				});
			} else {
				form.bootstrapValidator('validate');
			}
			
		});
		 
		 $("#networkGatewayConfigForm").bootstrapValidator({			
				fields : {
					gatewayIp : {
						validators : {
							notEmpty : {
								message : '默认网关IP不能为空'
							},					
		                    regexp: {
		                        regexp: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/,
		                        message: '默认网关IP地址格式不正确'
		                    }
						}
					}
				}
			});
	});

	
</script>



