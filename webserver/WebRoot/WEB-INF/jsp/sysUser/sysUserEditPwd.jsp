<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
	<div class="title clearfix">
				<h2 style="font-size:34px; font-weight:bold;">更改密码</h2>
			</div>
	  		<ul class="breadcrumb" style="">
				<li>
					<i class="fa fa-user"></i>
					<a href="javascript:;" style="color:rgb(42,63,84)">更改密码</a>
				</li>
			</ul>		
			<ul id="myTab" class="nav nav-tabs" style="height:30px;">
				<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
						更改密码</a>
				</li>
			</ul>
			<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
				    <section class="content">						
						<div class="row">
						  <div class="col-md-12 col-sm-12 col-xs-12">
						    <div class="x_panel"> 
							  <div class="x_content">
					
						<form id="editSysPwdForm" class="form-horizontal form-label-left" action="${ctx }/sysUser/updateSysUserPwd.do?account=${sysUser.account }" style="margin-top:20px;" >
						  <input id="prePassword" name="prePassword" type="hidden" value="${sys.password }" />
							<div class="form-group">
								<label class="control-label col-xs-3">当前密码 
								</label>
								<div class="col-xs-6">
								  <input id="password" name="password" type="password" class="form-control col-xs-6" >
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">新密码 
								</label>
								<div class="col-xs-6">
								  <input id="newSysPwd" name="newSysPwd" type="password" class="form-control col-xs-6" >
								</div>
							</div>
							<div class="form-group">
								<label class="control-label col-xs-3">新密码确认 
								</label>
								<div class="col-xs-6">
								  <input id="checkNewSysPwd" name="checkNewSysPwd" type="password" class="form-control col-xs-6" >
								</div>
							</div>
						  
						  <div class="ln_solid"></div>
							<div class="form-actions">
								<div class="row">
									<div class="col-md-offset-3 col-xs-6">
									  <button type="button" id="submitSysPwdEdit" class="btn btn-primary col-md-offset-3">保存</button>
									  <button type="button" id="backButton" class="btn btn-primary col-md-offset-3">返回</button>
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
		$("#backButton").click(function() {
			loadUrl("${ctx }/sysUser/sysUserList.do");
		});
		
		 
		$('#editSysPwdForm').bootstrapValidator({           
            fields: {
            	password: {
                    validators: {
                        notEmpty: {
                            message: '当前密码不能为空'
                        },
                    }
                },
                newSysPwd: {
                    validators: {
                        notEmpty: {
                            message: '新密码不能为空'
                        },
                        stringLength: {
                            min: 6,
                            max: 30,
                            message: '长度为6~30'
                        },
	                    regexp: {
	                        regexp: /^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9~!@#$%^&*()-+_]{6,30})+$/,
	                        message: '新密码必须包含数字和字母,只能由数字、字母或特殊字符组成'
	                    }
                  }
                },
                checkNewSysPwd: {
                    validators: {
                        notEmpty: {
                            message: '确认密码不能为空'
                        },
                        identical: {
                        	field: 'newSysPwd',
                        	message: '两次输入密码不一致'
                        }
                    }
                }
            }
        });
		

		//密码修改
		$("#submitSysPwdEdit").click(function() {
			var newSysPwd = $("#newSysPwd").val();
			var checkNewSysPwd = $("#checkNewSysPwd").val();
			if (newSysPwd == checkNewSysPwd) {
				var form = $("#editSysPwdForm");
				if(form.data('bootstrapValidator').isValid()){
					form.ajaxSubmit({
						success : function(data) {
							if (data.success) {
								if (typeof (data.message) == "undefined")
									layer.alert( "操作成功", {icon:1});
								else
									layer.alert( data.message, {icon:1});
								var url = "${ctx }/sysUser/logout.do";
								window.location.href = url;
							} else {
								if (typeof (data.message) == "undefined")
									layer.alert( "操作失败", {icon:2});
								else
									layer.alert( data.message, {icon:2});
							}
						},
						error : function() {
							layer.alert( "请求失败",{icon:2});
						}
					});
				}else{
					form.bootstrapValidator('validate');
				}
			}else {
				layer.alert( "新密码与确认密码不一致", {icon:2});
			}
		}); 
	});
</script>