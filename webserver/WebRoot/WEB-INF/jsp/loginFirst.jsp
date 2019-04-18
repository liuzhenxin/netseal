<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="common/taglibs.jsp"%>
<%@ include file="common/resource.jsp"%>

 <html>
	  <head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>NetSeal电子签章管理系统</title>
<script type="text/javascript">
	function submitButton() {
		var newSysPwd = $("#newPwd").val();
		var checkNewSysPwd = $("#newPwd2").val();
		if (newSysPwd == checkNewSysPwd) {
			$('[name=sysUserLoginUpdateForm]').bootstrapValidator('validate'); 
			if($('[name=sysUserLoginUpdateForm]').data('bootstrapValidator').isValid()){
				document.sysUserLoginUpdateForm.submit();
			}else{
			    $('[name=sysUserLoginUpdateForm]').bootstrapValidator('validate'); 
			}
		} else {
			layer.alert("新密码与确认密码不一致", {icon : 2});
		}
	}

	$(document).ready(function() {
	    $('[name=sysUserLoginUpdateForm]').bootstrapValidator({          
	            fields: {
	            	currentPwd: {
	                    validators: {
	                        notEmpty: {
	                            message: '当前密码不能为空'
	                        }
	                    }
	                },
	                newPwd: {
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
	 	                    	regexp: /^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9~!@#$%^&*()-+_])+$/,
	 	                        message: '新密码必须包含数字和字母,只能由数字、字母或特殊字符组成'
	 	                    }
	                   }
	                },
	                newPwd2: {
	                	validators: {
	                        notEmpty: {
	                            message: '确认新密码不能为空'
	                        },
	                        identical: {
	                        	field: 'newPwd',
	                        	message: '两次输入密码不一致'
	                        }
	                    }
	                }
	            }
	        }); 
	});
	$(document).ready(function(){
 	   $(".errormsg").show().delay(4000).hide(0);
    });
</script>
	  </head>  
      <body>
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
			<div id="myTabContent" class="tab-content" style="margin-top:100px;">
				<div class="tab-pane fade in active" id="home">
					<div class="col-xs-12">
					    
						<form id="demo-form" data-parsley-validate="" name="sysUserLoginUpdateForm" class="form-horizontal form-label-left x_panel" novalidate style="margin-top:20px;" action="${ctx }/sysUser/loginFirst.do" method="post" >
						  <input name="account" type="hidden" value="${account }" />
							<div class="form-group" >
								<label class="control-label col-xs-3" >当前密码 
								</label>
								<div class="col-xs-6" >
								  <input name="currentPwd" type="password"  class="form-control col-xs-6"  >
								</div>
							</div>
							<div class="form-group" >
								<label class="control-label col-xs-3" >新密码 
								</label>
								<div class="col-xs-6" >
								  <input name="newPwd" type="password" id="newPwd" required="" class="form-control col-xs-6"  >
								</div>
							</div>
							<div class="form-group" >
								<label class="control-label col-xs-3" >新密码确认 
								</label>
								<div class="col-xs-6" >
								  <input name="newPwd2" type="password"  id="newPwd2" class="form-control col-xs-6"  >
								</div>
							</div>
							
							<div class="form-group">
								<div class="col-sm-offset-5 col-md-3">
									<span class="errormsg" style="color:red">${msg }</span>
								</div>
							</div>
						  
						  <div class="ln_solid"></div>
						  <div class="form-group">
						  <div class="row">
								<div class="col-md-offset-3 col-xs-6">
							  <button type="button" class="btn btn-primary col-md-offset-3" onclick="submitButton()" >确定</button>
							  <button type="button" class="btn btn-primary col-md-offset-3"  id="return">返回</button>
						      </div>
							</div></div>
						</form>
		
					</div>
				</div>	
			</div>	
			
			
				<footer class="main-footer" id="" style="width:100%; position:fixed; left:0; bottom:0; margin:0; text-align:center;">
	     		 <span style="text-align:center;display:block;"><strong>Copyright &copy;信安世纪|</strong> All rights reserved. <a href="http://www.infosec.com.cn">关于我们</a></span>
			    </footer>
			
	<%-- <script src="${ctx }/js/jQuery-1.7.2.min.js"></script>
    <!-- Bootstrap -->
    <script src="${ctx }/js/bootstrap.min.js"></script> --%>
    
    <script>
		$('#return').click(function(){
			// $('#content').load('${ctx }/sysUser/toLogin.do');
			window.location.href = "${pageContext.request.contextPath}/sysUser/toLogin.do";
		})
	</script>
	
</body>
</html>