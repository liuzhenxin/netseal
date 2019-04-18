<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>
<div id="myTabContent" class="tab-content" style="margin-top:20px;">
				<div class="tab-pane fade in active" id="home">
					<section class="content">						
						<div class="row">
						  <div class="col-xs-12">
							<div class="x_panel" >

			<form id="hostsConfigForm" action="${ctx }/system/ha/hostsConfigSave.do" method="post" class="form-horizontal">
							
			<div class="form-group">
					<label for="name" class="col-xs-3 control-label">配置hosts文件</label>
					<div class="col-xs-6">
						<textarea class="form-control" id="hostsConfig" name="hostsConfig" rows="8">${hostsConfig }</textarea>
					</div>
				</div>
				
				<div class="form-actions">
					<div class="col-md-offset-3 col-xs-6">
						<input class="btn btn-primary col-md-offset-6" id="hostsConfigSave" type="button" value="保存">
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
$(function(){
	$("#hostsConfigForm").bootstrapValidator({
		fields: {
			hostsConfig: {
               validators: {
                   notEmpty: {
                       message: '不能为空'
                   }
                  
               }
           }
        }
	});
	$("#hostsConfigSave").click(function(){
		 var form=$("#hostsConfigForm");
		 form.bootstrapValidator('validate');
		 if (form.data('bootstrapValidator').isValid()) {
			 form.ajaxSubmit({
				 success:function(data){
				     if(data.success){	
				    	layer.alert(data.message,{icon:1});
			        	
				     }else{
						layer.alert( data.message, {icon:2});
					}
				    	
				    },error:function(){
				    	layer.alert("请求失败",{icon:2});
				    }
			 }); 
		 }else{
			 form.bootstrapValidator('validate');
		 }
		 
	});
	
	
});
</script>

