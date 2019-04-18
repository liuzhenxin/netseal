<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
  <h2 style="font-size:34px; font-weight:bold;">印模管理</h2></div>
<ul class="breadcrumb" style="">
<li>
  <i class="fa fa-home"></i>
  <i style="color:rgb(42,63,84); font-style:normal;">印模管理    /    印模信息修改</i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active">
  <a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">印模信息修改</a></li>
</ul>
<div id="templateEditShow" class="tab-content" style="margin-top:20px;" >
    <div class="tab-pane fade in active" id="home">
<section class="content">						
	<div class="row">
	  <div class="col-md-12 col-sm-12 col-xs-12">
		<div class="x_panel" >
		
		  <div class="main" >
			<form  class="form-horizontal bv-form" id="editTemplateForm"  autocomplete="off" action="${ctx }/template/editTemplate.do" method="post" enctype="multipart/form-data" style="margin-top:10px;">
				<input name="id" id="id" type="hidden" value="${template.id }" />
			  <div class="form-group">
				<label class="control-label col-xs-3">印模名称 
				</label>
				<div class="col-xs-6">
				  <input id="name" name="name" type="text" class="form-control col-xs-6" value="${template.name }" >
				</div>
			  </div>
			  <div class="form-group">
				<label class="control-label col-xs-3">印模类型</label>
				<div class="col-xs-6">
				  <select class="form-control  col-xs-6" name="type">
					<option value="1" ${template.type ==1 ? 'selected':'' }>单位公章</option>
					<option value="2" ${template.type ==2 ? 'selected':'' }>个人章</option>
					<option value="3" ${template.type ==3 ? 'selected':'' }>手写章</option>
				  </select>
				</div>
			  </div>
			  <div class="form-group">
				<label class="control-label col-xs-3">选择单位</label>
				<div class="col-xs-6">
				  <div id="gender" class="btn-group" data-toggle="buttons">

				  <div class="input-group">
					<input id="companyName" name="companyName" readonly class="form-control " type="text" value="${template.companyName }" /> 
					<input id="companyId" name="companyId" class="form-control" type="hidden" value="${template.companyId }" />
					 <span class="input-group-btn">
						<button type="button" class="btn btn-primary "  id="selectCom" data-toggle="modal" data-target="#editTemplateCom">选择</button>
					</span>
				  </div>
				  </div>
				</div>
			  </div>
			  
			 
			  <div class="form-group" id="photo">
				<label class="control-label col-xs-3">印模图片</label>
				<div class="col-xs-6">
					<input id="photoPath" name="photoPath" type="hidden" class="form-control" value="${template.photoPath}" />
					<input id="photoFile" name="photoFile" type="file" class="form-control input-sm" value="${template.photoPath}" />
					<span>
						<c:if test="${template.photoPath!=null}">
							<a href="#" data-toggle="modal" data-target="#myModal">查看</a>
						</c:if>
						<c:if test="${template.photoPath==null}">无</c:if>
					</span>
			  	</div>
			  	</div>
			  
			  	
			  	
			  	  <div class="form-group" id="alpha">
					<label class="control-label col-xs-3">图片透明度</label>
					<div class="col-xs-6" style="margin-top:6px;">
					  <div id="sliderParent" style="padding: 6px;background-color: gray;display: inline-block;">  </div>
					    <input id="transparency" name="transparency" type="hidden"   value="${template.transparency }" > 
				    </div>
			     </div>
			   
			    
			    
			  <div class="form-group">
				<label for="isAuditReq" class="control-label col-xs-3">申请是否审核</label>
				<div class="col-xs-6">
				  <ul class="ui-choose" >
                    <li ${template.isAuditReq ==1 ? 'class="selected"':'' }>是</li>
                    <li ${template.isAuditReq ==0 ? 'class="selected"':'' }>否</li>
                   <input type="hidden" name="isAuditReq"  value="${template.isAuditReq }" >  
	              </ul>
				  
				  </div>
			  </div>
			  <div class="form-group">
				<label class="control-label col-xs-3">制章是否验证书</label>
				<div class="col-xs-6">
				 <!--  <input type="radio" name="isAuthCertGenSeal" id="isAuthCertGenSeal1" value="1" checked="">
				  <label>是</label>
				  <input type="radio" name="isAuthCertGenSeal" id="isAuthCertGenSeal0" value="0">
				  <label>否</label> -->
				  
				  <ul class="ui-choose" >
                    <li ${template.isAuthCertGenSeal ==1 ? 'class="selected"':'' }>是</li>
                    <li ${template.isAuthCertGenSeal ==0 ? 'class="selected"':'' }>否</li>
                   <input type="hidden" name="isAuthCertGenSeal"  value="${template.isAuthCertGenSeal }" >  
	              </ul>
				  
				  </div>
			  </div>
			  <div class="form-group">
				<label class="control-label col-xs-3">下载是否验证书</label>
				<div class="col-xs-6">
				  <!-- <input type="radio" name="isAuthCertDownload" id="isAuthCertGenSeal1" value="1" checked="">
				  <label>是</label>
				  <input type="radio" name="isAuthCertDownload" id="isAuthCertGenSeal0" value="0">
				  <label>否</label> -->
				  <ul class="ui-choose" >
                    <li ${template.isAuthCertDownload ==1 ? 'class="selected"':'' }>是</li>
                    <li ${template.isAuthCertDownload ==0 ? 'class="selected"':'' }>否</li>
                   <input type="hidden" name="isAuthCertDownload"  value="${template.isAuthCertDownload }" >  
	              </ul>
				  
				  
				  </div>
			  </div>
			  <div class="form-group">
				<label for="isAuthCertGenSeal" class="control-label col-xs-3">印章是否可下载</label>
				<div class="col-xs-6">
				 <!--  <input type="radio" name="isDownload" id="isAuthCertGenSeal1" value="1" checked="">
				  <label>是</label>
				  <input type="radio" name="isDownload" id="isAuthCertGenSeal0" value="0">
				  <label>否</label> -->
				  
				  <ul class="ui-choose" >
                    <li ${template.isDownload ==1 ? 'class="selected"':'' }>是</li>
                    <li ${template.isDownload ==0 ? 'class="selected"':'' }>否</li>
                   <input type="hidden" name="isDownload"  value="${template.isDownload }" >  
	              </ul>
				  </div>
			  </div>
			  
			
			  
			  <div class="form-group">
				<label class="control-label col-xs-3">启用日期 
				</label>
				<div class="col-xs-6">
				  <input id="befor" name="notBeforCn"  type="text" class="form-control col-xs-6" style="cursor: pointer;" value="${template.notBeforCn }">
				</div>
			  </div>
			  <div class="form-group">
				<label class="control-label col-xs-3">结束日期 
				</label>
				<div class="col-xs-6">
				  <input id="after" name="notAfterCn" type="text"  class="form-control col-xs-6" style="cursor: pointer;" value="${template.notAfterCn }">
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">用户列表</label>
					<div class="col-xs-6">
					  <div id="gender" class="btn-group" data-toggle="buttons">

					  <div class="input-group">
						<input id="userIds" name="userIds" readonly class="form-control " type="hidden" value="${template.userIds }"/>
						<input id="userNames" class="form-control" readonly name="userNames" type="text" value="${template.userNames }" />
						<span class="input-group-btn">
							<button type="button" class="btn btn-primary" id="showUserList" onclick="javascript:selectUsers()">选择</button>
						</span>
					  </div>
					  </div>
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">备注 
					</label>
					<div class="col-xs-6">
					  <input id="remark" name="remark"  type="text" class="form-control col-xs-6" value="${template.remark }">
					</div>
				  </div>
					<div class="form-actions">
						<div class="row">
							<div class="col-md-offset-4 col-xs-6">
								  <button  id="submitTemplateButton" type="button" class="btn btn-primary col-md-offset-1" >提交</button>
								  <button  id="returnTemplateButton" type="button" class="btn btn-primary col-md-offset-3" id="return" >返回</button>
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

	
	<!-- 单位模态框 -->
	<div class="modal fade" id="editTemplateCom" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog modal-lg"  style="width:500px; height:300px; position:absolute; left:50%; top:50%; margin-left:-250px; margin-top:-150px;">
	    <form id="editPrintNumForm" class="form-horizontal" action="${ctx }/printer/EditPrintNum.do" method="post">
	    	<input id="comvalue" type="hidden"/>
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	                <h4 class="modal-title" id="myModalLabel">选择用户单位</h4>
	            </div>
	            <div class="modal-body">
	               <div class="tree-dialog-content">
	                   <ul id="configCompanyTree" class="ztree"></ul>
                   </div>
	            </div>
	            <div class="modal-footer">
	                <button type="button"  id="modalClose" class="btn btn-default" data-dismiss="modal">关闭</button>
	                <button type="button" id="submitCompanyButton"  class="btn btn-primary">确定</button>
	            </div>
	        </div>
	       </form>
		</div>
	</div>
	<%@include file="templateConfigUser.jsp"%>
		<!-- 查看印模图片模态框 -->	
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	   		<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel" style="color:#3c8dbc;font-size:20px;"> 印模图片 </h4>
		            </div>
		            <div class="modal-body">
				        <div align="center">
	                    	<img src="${ctx }/template/viewPhoto.do?id=${template.id }&time=${template.updateTime }"/> 
				        </div>
					</div>
					<div class="modal-footer">
		            	<button type="button"   class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
				</div>
	   		</div><!-- /.modal -->
		</div>
		


<script type="text/javascript">


//将所有.ui-choose实例化
$('.ui-choose').ui_choose();

$('.ui-choose').each(function(){
	var uc_01 = $(this).data('ui-choose'); // 取回已实例化的对象
	var uc  = $(this);
	uc_01.click = function(index, item) {
	    if(index==0){
	    	uc.find("input").val(1);
	    }else{
	    	uc.find("input").val(0);
	    }
	  
	}
	
});


var option = {
        color: '#337ab7',
        width: '400px',
        progress: 0.3,
        handleSrc: '${ctx }/img/slider_handle.png',
        isCustomText: false
    };
    
    var num = parseInt($("#transparency").val());
       
    $('#sliderParent')
        .jackWeiSlider(option)
        // .setText('2018-4-5 02:39:00')
        .setProgress(num/100)
        .setOnStartDragCallback(function () {
            //console.log('start');
        })
        .setOnDragCallback(function (p) {
           // console.log(p);
        })
        .setOnStopDragCallback(function () {
           // console.log('stop');
        });





//下拉框联动图片事件
$("select[name='type']").change(function(){
	 var optValue = $("select[name='type']").val();
	 if(optValue == 3){
		document.getElementById("photo").style.display="none";//隐藏
		document.getElementById("alpha").style.display="none";//隐藏
	 }else{
		document.getElementById("photo").style.display="";
		document.getElementById("alpha").style.display="";
	 }
})

//时间控件
var start = {
elem: '#befor',
format: 'YYYY-MM-DD',
istoday : false,
choose: function(datas){
  end.min = datas;
  end.start = datas;
}
};
var end = {
elem: '#after',
format:"YYYY-MM-DD",
istoday : false,
choose: function(datas){
	start.max = datas;
}
};
laydate(start);
laydate(end); 

$(function(){
	
	 var optValue = $("select[name='type']").val();
	 if(optValue == 3){
		document.getElementById("photo").style.display="none";//隐藏
		document.getElementById("alpha").style.display="none";//隐藏
	 }else{
		document.getElementById("photo").style.display="";
		document.getElementById("alpha").style.display="";
	 }
	
	 //   validate("#editTemplateForm"); //--表单验证---修改
	 $("#editTemplateForm").bootstrapValidator({
	         message: 'This value is not valid',
	         feedbackIcons: {
	             valid: 'glyphicon glyphicon-ok',
	             invalid: 'glyphicon glyphicon-remove',
	             validating: 'glyphicon glyphicon-refresh'
	         },
			fields: {
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
                        },
                       
                    }
                },
	            remark:{
	            	validators:{
	            		stringLength: {
	                        min: 0,
	                        max: 66,
	                        message: '长度为0-66'
	                    },
	                    
	            	}
	            },
	         }
		});

	//返回
	$("#returnTemplateButton").click(function(){
		loadUrl("${ctx }/template/templateList.do");
	});
	
	
	$("#submitTemplateButton").click(function(){	
		 var form=$("#editTemplateForm");
		 $('#editTemplateForm').bootstrapValidator('validate');
			if($('#editTemplateForm').data('bootstrapValidator').isValid()) {
				var before =  document.getElementById("befor").value;
				var after =  document.getElementById("after").value;
				if(!before  || before=="请点击进行日期选择"){
					layer.alert( "请选取印模起始时间", {icon:2});
				}else if($("select[name='type']").val()!=3 && !$('#photoFile').val() && !$('#photoPath').val()){
					layer.alert( "请输入印模图片", {icon:2});
				}else if(!after || after=="请点击进行日期选择"){
					layer.alert( "请选取印模截止时间", {icon:2});  
				}else{
					var num = $(".jws-text").html();
			        num = num.substr(0,num.length-1);
			        if(optValue!=3){
				    	 $("#transparency").val(num);
				    }else{
				    	 $("#transparency").val(100);
				    }
				 	form.ajaxSubmit({
					 	success:function(data){
					     if(data=='ok'){
					    	 layer.alert("印模修改成功", {icon:1});
					    	 var url="${ctx }/template/templateList.do";
					         loadUrl(url);
					      }else if(data==undefined){
					    	  layer.alert( "请求失败", {icon:2});
					      }else{
					    	  layer.alert( data, {icon:2});
					      }
					    },error:function(){
					    	layer.alert( "请求失败", {icon:2});
					    }
				 });
				}
			} else {
				$('#editTemplateForm').bootstrapValidator('validate');
			}
	});
});

//-----原来单开页面的的选择用户单位,现在将新开页面修改为模态框---代码挪用进来 selectCom
	$("#selectCom").click(function() {
		var setting = {
			async : {
				enable : true,
				url : "${ctx }/template/configCompanyTree.do",
				autoParam : [ "id" ]
			},
			callback : {
				beforeClick : function(treeId, treeNode) {
					var id = treeNode.id;
					$("[name=companyId]").val(id);
					//$("[name=companyName]").val(treeNode.name);
					$("#comvalue").val(treeNode.name);
				},
				onAsyncSuccess : function(event, treeId, treeNode, msg) {
					
					if (treeNode == null) {
						var treeObj = $.fn.zTree.getZTreeObj(treeId);
						var nodes = treeObj.getNodes();
						if (nodes.length > 0) {
							treeObj.expandNode(nodes[0], true, false, false);
						}
					}
				}
			}
		};
		$.fn.zTree.init($("#configCompanyTree"), setting);
	});

	$("#submitCompanyButton").click(
		function() {
			var companyId = $("[name=companyId]").val();
			$("#companyId").val(companyId);
			//$("#companyName").val($("[name=companyName]").val());
			$("[name=companyName]").val($("#comvalue").val());
			$("#userIds").val("");
			$("#userNames").val("");
			$('#editTemplateCom').modal('hide');
	});
	 //模态框关闭
	 $("#modalClose").click(function(){
		
			$('#editTemplateCom').modal('hide');
		});

	
function selectUsers(){
	if($("#companyId").val()){
		shoHide('#templateConfigUser','#templateEditShow','');
		var requestComId=$("#companyId").val();
		var url = "";
		url = "${ctx }/template/configUserList.do?cid=" + requestComId;
		$.ajax({
			type : "get",
			dataType : "json",
			url : url,
			success : function(jsonResult) {
				$("#comUserTable tr:not(:first)").remove();
				$("#userPage").empty();
				//遍历一个数组or集合
				var ta = jsonResult.result;
				if (ta == "") {
					layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
				} else {
					$.each(ta,function(i, item) {
					//追加html文本
					$("#comUserTable tbody").append("<tr><th name="+item.name+"><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td>"
							+ item.name+ "</td><td>"+ item.companyName+ "</td></tr>");
					});
					icheck(".js-checkbox-all");
					/* ----分页--开始 */
					//分页
					laypage({
						cont : 'userPage',
						skip : true,//跳转页面选项
						pages : jsonResult.totalPage, //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
									
						curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
							var pageNo = jsonResult.pageNo; // 当前页(后台获取到的)
							return pageNo ? pageNo : 1; // 返回当前页码值
						}(),
						jump : function(e, first) { //触发分页后的回调  
							if (!first) { //一定要加此判断，否则初始时会无限刷新
								$.ajax({
									type : "get",
									dataType : "json",
									url : "${ctx }/template/configUserList.do?cid=" + requestComId+"&pageNo="+ e.curr,
									success : function(jsonResult) {
										$("#comUserTable tr:not(:first)").remove();
										//遍历一个数组or集合
										var ta = jsonResult.result;
											if (ta == "") {
												layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
											} else {
												$.each(ta,function(i, item) {
													//追加html文本
													$("#comUserTable tbody").append("<tr><th name="+item.name+"><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.id+"'></th><td>"
															+ item.name+ "</td><td>"+ item.companyName+ "</td></tr>");
														});
												icheck(".js-checkbox-all");
											}
										},
									error : function() {
										layer.alert( "查询错误", {icon:2});
									}
								});
							}
					}
			}); 
					/* ---分页---结束 */
		}
	},
	error : function() {
		layer.alert( "查询错误", {icon:2});
	}
});
	}else{
		layer.alert("请选择单位",{icon:2});
	}
	
}


$(function(){
	$("#submitComButton").click(function() {
		var account = "";
		var userIds="";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					account = $(this).parent("th").attr("name");
					userIds = $(this).attr('value');
				}else{
					account = account + "," + $(this).parent("th").attr("name");
					userIds =  userIds +"," + $(this).attr('value');
				}
				index++;
			}
		});
		/* if(index == 0){
			layer.alert( "请选择用户", {icon:0});
			return;
		} */
		$("#userNames").val(account);
		$("#userIds").val(userIds);
		shoHide('#templateEditShow','#templateConfigUser','');
	});
	$("#closeCompanyButton").click(function() {
		shoHide('#templateEditShow','#templateConfigUser','');
		
	});

	$("#selectAllUser").click(function() {
		var account = "印模单位下所有用户" ;
		$("#userNames").val(account);
		$("#userIds").val(0);
		shoHide('#templateEditShow','#templateConfigUser','');
	});

});

	
</script>
