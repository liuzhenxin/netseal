<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top: 20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="x_panel" id="recoverKey">
						<form id="recoverKeyForm" action="${ctx }/key/recoveryKey.do" method="post" autocomplete="off" enctype="multipart/form-data"
							data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
							<div class="form-group">
								<label for="recoverKeyFile" class="control-label col-xs-3">
									选择恢复密钥文件
								</label>
								<div class="col-xs-6">
									<input id="recoverKeyFile" name="recoverKeyFile" type="file" class="form-control col-xs-6">
								</div>
							</div>
							<div class="form-group">
								<label for="pwd" class="control-label col-xs-3">
									请输入恢复密钥密码
								</label>
								<div class="col-xs-6">
									<input id="pwd" name="pwd" type="password" class="form-control col-xs-6">
								</div>
							</div>
							<div class="form-group">
								<label for="pfxpwd" class="control-label col-xs-3">
									请输入PFX文件密码(可选)
								</label>
								<div class="col-xs-6">
									<input id="pfxpwd" name="pfxpwd" type="password" class="form-control col-xs-6">
								</div>
							</div>
							<div class="ln_solid"></div>
							<div class="form-group">
								<div class="col-xs-6 col-md-offset-3">
									<button type="button" id="recoverButton" class="btn btn-primary col-md-offset-3">恢复</button>
									<button class="btn btn-primary col-md-offset-3" type="button" id="recoBack" style="" id="return">返回</button>
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
	//返回
	$("#recoBack").click(function() {
		loadUrl("${ctx }/key/keyList.do");
	});
	$(function(){
		$("#recoverButton").click(function() {
			$('#recoverKeyForm').bootstrapValidator('validate');
			if ($('#recoverKeyForm').data('bootstrapValidator').isValid()) {
				var form = $("#recoverKeyForm");
				form.ajaxSubmit({
					success : function(data) { 
						if (data == "ok") {
							layer.alert( "恢复成功", {icon:1});
							loadUrl("${ctx }/key/keyList.do");
						} else{
							layer.alert( data, {icon:2});
							loadUrl("${ctx }/key/keyList.do");
						}
					},
					error : function() {
						layer.alert( "恢复失败", {icon:2});
						loadUrl("${ctx }/key/keyList.do");
					}
				});
			}  
		});
		
		
       $("#recoverKeyForm").bootstrapValidator({fields:{
    	   recoverKeyFile: {
               validators: {
                   notEmpty: {
                       message: '文件不能为空'
                   }
               }
           },
           pwd: {
               validators: {
                   notEmpty: {
                       message: '密码不能为空'
                   },
                   stringLength: {
                       min: 8,
                       max: 8,
                       message: '密码长度8位'
                   },
                   regexp: {
                       regexp: /^[0-9]*$/,
                       message: '请输入正确格式(只能是数字)'
                   },
                  
               }
           },
           pfxpwd :{
        	   validators: {
        	   stringLength: {
                   min: 0,
                   max: 15,
                   message: '最多15位'
               }, 
           	}
           }
          }
       });
	});
		
</script>