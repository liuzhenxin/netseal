<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
        <!-- Main content -->
<div class="title clearfix">
			<h2 style="font-size:34px; font-weight:bold;">网络配置</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /  网络配置 / 网口设置</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			网口设置</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
					<div class="main">
					<form id="networkCardConfigForm"   action="${ctx }/system/network/networkCardConfigSave.do" method="post"   data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top:20px; " >

					  <div class="form-group">
						<label class="control-label col-xs-3" >网口名 
						</label>
						<div class="col-xs-6">
						  <input id="name" name="name" type="text" id="first-name" required="required" class="form-control col-xs-6" value="${networkCard.name}" readonly="readonly">
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-5" >IPV4</label>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-3" >IP地址 
						</label>
						<div class="col-xs-6">
						  <input type="text" id="ip" name="ip" required="required" class="form-control col-xs-6" value="${networkCard.ip}">
						</div>
					  </div>
					  <div class="form-group">
						<label for="middle-name" class="control-label col-xs-3">子网掩码 </label>
						<div class="col-xs-6">
						  <input id="mask"  name="mask" class="form-control col-xs-6" type="text" name="middle-name" value="${networkCard.mask}">
						</div>
					  </div>
					  <div class="form-group">
						<label class="control-label col-xs-5" >IPV6</label>
					  </div>
					   <div class="form-group">
						<label class="control-label col-xs-3" >IP地址 
						</label>
						<div class="col-xs-6">
						  <input type="text" id="ipv6" name="ipv6" required="required" class="form-control col-xs-6" value="${networkCard.ipv6}">
						</div>
					  </div>
					  <div class="ln_solid"></div>
						<div class="form-actions">
							<div class="row">
								<div class="col-md-offset-3 col-xs-6">
								  <button id="networkCardConfigSave" type="button" class="btn btn-primary col-md-offset-3" >保存</button>
								  <button class="btn btn-primary col-md-offset-3" type="button" id="returnButton">返回</button>
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
		$("#returnButton").click(function(){
			loadUrl("${ctx }/system/network/networkList.do");
			
		});
		 $("#networkCardConfigSave").click(function(){
			 var form = $("#networkCardConfigForm");
			 form.bootstrapValidator('validate');
			if (form.data('bootstrapValidator').isValid()) {
				layer.confirm("配置成功后,需要重启电子签章服务,重新登录系统<br>配置失败时有信息提示",{btn:["确定","取消"]},function(){
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
				});
			} else {
				form.bootstrapValidator('validate');
			}
			
		}); 
	});
	$("#networkCardConfigForm").bootstrapValidator({			
		fields : {
			ip : {
				validators : {
					notEmpty : {
						message : 'IP地址不能为空'
					},					
                    regexp: {
                        regexp: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/,
                        message: 'IP地址不正确'
                    }
				}
			},
			ipv6 : {
				validators : {
                    regexp: {
                        regexp: /^([\da-fA-F]{1,4}:){7}[\da-fA-F]{1,4}$/,
                        message: 'IPv6地址不正确'
                    }
				}
			},
			mask : {
				validators : {
					notEmpty : {
						message : '子网掩码不能为空'
					},					
                    regexp: {
                        regexp: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}$/,
                        message: '子网掩码不正确'
                    }
				}
			}
		}
		});
</script>