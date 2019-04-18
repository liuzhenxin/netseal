<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<form id="addCompanyForm" action="${ctx }/company/addCompany.do" method="post" >
<input id="pid" type="hidden" name="pid" value="${pid }"/>
<div class="form-group col-xs-12">
	<label class="control-label col-xs-2" style="padding-top:7px;">名称 
	</label>
	<div class="col-xs-10">
	  <input id="name" name="name" type="text" class="form-control col-xs-10" value="">
	</div>
</div>

<div class="form-group col-xs-12">
	<label class="control-label col-xs-2">备注 
	</label>
	<div class="col-xs-10">
	  <textarea id="remark" name="remark" class="form-control col-xs-10" style="height:100px; resize:none;" ></textarea>
	</div>
</div>

<input id="submitCompanyButton" type="button" value="提交" class="btn btn-primary" style="float:left; margin-top:10px;">

</form>


<script type="text/javascript">

$(function(){
	var form=$("#addCompanyForm");
	//表单验证
	$(form).bootstrapValidator({
		fields : {
			name: {
	            validators: {
	                notEmpty: {
	                    message: '不能为空'
	                },
	                stringLength: {
	                    min: 2,
	                    max: 16,
	                    message: '长度为2~16'
	                },
	                regexp: {
	                    regexp: /^[\u4e00-\u9fa5a-zA-Z0-9]+$/,
	                    message: '请输入正确格式(中文,数字或字母)'
	                }
	               
	            }
	        },
	        remark: {
	            validators: {
	                stringLength: {
	                    min: 0,
	                    max: 66,
	                    message: '长度0~66'
	                }
	            }
	        }
		}
	 });
	$("#submitCompanyButton").click(function(){
		$(form).bootstrapValidator('validate');
		 
		 if ($(form).data('bootstrapValidator').isValid()) {
			 form.ajaxSubmit({
				 success:function(data){
				     if(data.success){
				    	 layer.alert("单位添加成功",{icon:1});
			        	var treeObj = $.fn.zTree.getZTreeObj("companyTree");
			        	var nodes = treeObj.getSelectedNodes();
			        	if (nodes.length>0) {
			        		if (!nodes[0].isParent) {
			        			nodes[0].isParent=true;
			        		}
			        		treeObj.reAsyncChildNodes(nodes[0], "refresh");
			        	}
			        	var url="${ctx }/company/viewCompany.do?id="+$("#pid").val();
			    		$("#companyContent").load(url);
				      }else if(data.success==false && data.message.indexOf("名称重复")>0){
				    	  layer.alert(data.message,{icon:2});
				      }else{
				    	 layer.alert(data.message,{icon:2});
				     }
				    	
				    },error:function(data){
				    	layer.alert("请求失败",{icon:2});
				    }
			 });
		 }else {
			$(form).bootstrapValidator('validate');
		}
	});
})
</script>