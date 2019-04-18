<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

<input type="hidden" id="companyId" name="companyId" value="${user.companyId }"/>
<input type="hidden" id="companyName" name="companyName"/>
	
<div>选择单位：</div>
<div class="tree-dialog-content">
	<ul id="configCompanyTree" class="ztree"></ul>
</div>

<div>
<input id="submitCompanyButton" type="button" value="确定">
</div>

<script type="text/javascript">
$(function(){
    
    var setting = {
			async: {
				enable: true,
				url:"${ctx }/userManage/configCompanyTree.do",
				autoParam:["id"]
			},
			callback: {
				beforeClick: function(treeId, treeNode) {
					var id=treeNode.id;
					$("#companyId").val(id);
					$("#companyName").val(treeNode.name);
				},
				onAsyncSuccess: function(event, treeId, treeNode, msg) {
					if(treeNode==null){
						var treeObj = $.fn.zTree.getZTreeObj(treeId);
						var nodes = treeObj.getNodes();
						if (nodes.length>0) {
							treeObj.expandNode(nodes[0],true,false,false);
						}
					}
				}
			}
		};
    	$.fn.zTree.init($("#configCompanyTree"), setting);
    	
    	$("#submitCompanyButton").click(function(){
    		var companyId=$("#companyId").val();
    		window.opener.document.getElementById("companyId").value = companyId;
    		window.opener.document.getElementById("companyName").value = $("#companyName").val();
    		window.close();
    	}); 
});

</script>
