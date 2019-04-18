<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">网络配置</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /    DNS修改</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			DNS修改</a>
	</li>
</ul>
		

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12"> 
			<div class="x_panel">
				<form id="networkDNSEditForm" action="${ctx }/system/network/networkDNSEditSave.do" method="post" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px;" >

				  <div class="form-group">
					<label class="control-label col-xs-3" >DNS服务器 
					</label>
					<div class="col-xs-6">
					  <input  id="dnsIp1" name="dnsIp" class="form-control input-sm" value="${dnsIp}"  type="text" class="form-control col-xs-6">
						</div>
					  </div>
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
								  <button type="button" id="networkDNSEditSave" class="btn btn-primary col-md-offset-3" >保存</button>
								  <button class="btn btn-primary col-md-offset-3" type="button" id="returnButton" >返回</button>
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
			loadUrl("${ctx }/system/network/networkDNSConfig.do");
			
		});
		 $("#networkDNSEditSave").click(function(){
			 var form = $("#networkDNSEditForm");
			 form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				form.ajaxSubmit({
					success : function(data) {
						if (data.success) {
							layer.alert( data.message, {icon:1});
							loadUrl("${ctx }/system/network/networkDNSConfig.do");
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
		 $("#networkDNSEditForm").bootstrapValidator({			
				fields : {
					dnsIp1 : {
						validators : {
							notEmpty : {
								message : 'DNS服务器不能为空'
							},					
		                    regexp: {
		                        regexp: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/,
		                        message: 'DNS服务器格式不正确'
		                    }
						}
					}
				}
			});
		 
	});
	
	
</script>