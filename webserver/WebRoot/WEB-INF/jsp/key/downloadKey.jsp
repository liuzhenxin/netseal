<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<br />
<div id="myTabContent" class="tab-content" style="margin-top: 20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="x_panel" id="downloadKey">
						<form id="downloadKeyForm" action="${ctx }/key/backupKey.do" method="post" data-parsley-validate=""
							class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
							<input id="downloadId" name="id" value="${id }" class="form-control input-sm" type="hidden" />
							<div class="form-group">
								<label for="pwd" class="control-label col-xs-3">压缩密钥密码 </label>
								<div class="col-xs-6">
									<input id="down_pwd" name="pwd" type="password" class="form-control col-xs-6">
								</div>
							</div>
							<div class="ln_solid"></div>
							<div class="form-group">
								<div class="col-xs-6 col-md-offset-3">
									<button type="button" class="btn btn-primary col-md-offset-3" id="downloadButton">下载</button>
									<button class="btn btn-primary col-md-offset-3" type="button" id="down_back">返回</button>
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
	$("#down_back").click(function() {
		loadUrl("${ctx }/key/keyList.do");
	});
	$(function(){
		//下载
		$("#downloadButton").click(function() {
			$('#downloadKeyForm').bootstrapValidator('validate');
			if ($('#downloadKeyForm').data('bootstrapValidator').isValid()) {
				var form = $("#downloadKeyForm");
				form.ajaxSubmit({
					success : function(data) {
						 if (data.success) {
							window.location.href="${ctx }/key/downloadKey.do?zipPath="+data.message;
						} else {
							layer.alert(data.message, {icon : 2});
							loadUrl("${ctx }/key/keyList.do");
						} 
					},
					error : function() {
						layer.alert(data, {icon : 2});
						loadUrl("${ctx }/key/keyList.do");
					}
				});
			}
		});
		
       $("#downloadKeyForm").bootstrapValidator({fields:{
           pwd: {
               validators: {
                   notEmpty: {
                       message: '密码不能为空'
                   },
                   stringLength: {
                       min: 8,
                       max: 8,
                       message: '请输入8位长度密码'
                   },
                   regexp: {
                       regexp: /^[0-9]*$/,
                       message: '请输入正确格式(只能是数字)'
                   },
                  
               }
           }
          }
       });
	});
		
</script>