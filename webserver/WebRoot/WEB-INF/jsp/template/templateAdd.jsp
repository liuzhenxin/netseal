<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
      <h2 style="font-size:34px; font-weight:bold;">添加印模</h2></div>
<ul class="breadcrumb" style="">
  <li>
    <i class="fa fa-home"></i>
    <i style="color:rgb(42,63,84); font-style:normal;">印模管理    /    添加印模</i></li>
</ul>
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
  <li class="active">
    <a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">添加印模</a></li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
    <div class="tab-pane fade in active" id="home">
<section class="content">						
	<div class="row" id="templateAddShow">
	  <div class="col-md-12 col-sm-12 col-xs-12">
		<div class="x_panel">
			<div class="main">
			  <form id="addTemplateForm"  class="form-horizontal bv-form" autocomplete="off" action="${ctx }/template/addTemplate.do"
			  method="post" enctype="multipart/form-data">
				<button type="submit" class="bv-hidden-submit" style="display: none; width: 0px; height: 0px;"></button>
		   <div class="sys-tab">

		  <div class="form-group">
			<label class="control-label col-xs-3">印模名称 
			</label>
			<div class="col-xs-6">
			  <input id="name" name="name" type="text" name="name"  class="form-control col-xs-6">
			</div>
		  </div>
		  <div class="form-group">
			<label class="control-label col-xs-3">印模类型</label>
			<div class="col-xs-6">
			  <select class="form-control input-sm col-xs-6" name="type">
				<option value="1">单位公章</option>
				<option value="2">个人章</option>
				<option value="3">手写章</option>
			  </select>
			</div>
		  </div>
		  <div class="form-group">
			<label class="control-label col-xs-3">选择单位</label>
			<div class="col-xs-6">
			  <div id="gender" class="btn-group" data-toggle="buttons">
			  <div class="input-group">
				<input id="companyName" name="companyName"   readonly type="text" class="form-control">
				<input id="companyId" name="companyId" class="form-control" type="hidden" />
				<span class="input-group-btn">
					<button type="button" class="btn btn-primary" id="selectCom" data-toggle="modal" data-target="#addTemplateCom" >选择单位</button>
				</span>
			  </div>
				  
			  </div>
			</div>
		  </div>
		  <div class="form-group" id="photo">
			<label class="control-label col-xs-3">印模图片</label>
			<div class="col-xs-6">
			  <input id="photoFile" name="photoFile" type="file" class="form-control input-sm"></div>
		  </div>
		  <div class="form-group" id="alpha">
			<label class="control-label col-xs-3">图片透明度</label>
			<div class="col-xs-6" style="margin-top:6px;">
			  <div id="sliderParent" style="padding: 6px;background-color: gray;display: inline-block;">  </div>
			    <input id="transparency" name="transparency" type="hidden"   class="form-control col-xs-6"> 
		    </div>
		   </div>
		  <div class="form-group">
			<label for="isAuditReq" class="control-label col-xs-3">申请是否审核</label>
			<div class="col-xs-6">
			   <ul class="ui-choose" >
                    <li class="selected">是</li>
                    <li>否</li>
                   <input type="hidden" name="isAuditReq" id="isAuthCertGenSeal1" value="1" >  
	           </ul>
		  </div>
		  </div>
		  <div class="form-group">
			<label class="control-label col-xs-3">制章是否验证书</label>
			<div class="col-xs-6">
			  <ul class="ui-choose" >
                    <li class="selected">是</li>
                    <li>否</li>
                   <input type="hidden" name="isAuthCertGenSeal" id="isAuthCertGenSeal1" value="1" >  
	           </ul>
		  </div>
		  </div>
		  <div class="form-group">
			<label class="control-label col-xs-3">下载是否验证书</label>
			<div class="col-xs-6">
			   <ul class="ui-choose" id="uc_01">
                 <li class="selected">是</li>
                  <li>否</li>
                 <input type="hidden" name="isAuthCertDownload"  value="1" >  
	           </ul>
			 </div>
			  
		  </div>
		  <div class="form-group">
			<label class="control-label col-xs-3">印章是否可下载</label>
			 <div class="col-xs-6">
			 
			  <ul class="ui-choose" id="uc_01">
                    <li class="selected">是</li>
                    <li>否</li>
                   <input type="hidden" name="isDownload" id="isAuthCertGenSeal1" value="1" >  
	           </ul>
			  </div>
		  </div>
		   
		  <div class="form-group">
			<label class="control-label col-xs-3">启用日期 
			</label>
			<div class="col-xs-6">
			  <input id="befor" name="notBeforCn"  type="text"  placeholder="请点击进行日期选择" readOnly class="form-control col-xs-6" style="cursor: pointer;">
			</div>
		  </div>
		  <div class="form-group">
			<label class="control-label col-xs-3">结束日期 
			</label>
			<div class="col-xs-6">
			  <input id="after" name="notAfterCn"  type="text" placeholder="请点击进行日期选择" readOnly   class="form-control col-xs-6"  style="cursor: pointer;">
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">用户列表</label>
					<div class="col-xs-6">
					  <div id="gender" class="btn-group" data-toggle="buttons">

					  <div class="input-group">
					    
						<input id="userNames"  readonly name="userNames" type="text"  class="form-control">
						<input id="userIds"   class="form-control" readonly name="userIds" type="hidden" />
						<span class="input-group-btn">
							<button type="button" class="btn btn-primary" onclick="javascript:selectUsers()">选择</button>
						</span>
					  </div>
					  </div>
					</div>
				  </div>
				  <div class="form-group">
					<label class="control-label col-xs-3">备注 
					</label>
					<div class="col-xs-6">
					  <input id="remark" type="text" name="remark" class="form-control col-xs-6">
					</div>
				  </div>
					<div class="form-actions">
						<div class="row">
							<div class="col-md-offset-4 col-xs-6">
								  <button id="submitTemplateButton"  type="button" class="btn btn-primary col-md-offset-1" >提交</button>
								  <button  onclick="javascript:loadUrl('${ctx }/template/templateList.do')" type="button" class="btn btn-primary col-md-offset-2"  id="return">返回</button>
							</div>
						</div>
					</div>
				</div>
			  </form>
			</div>
		</div>
	  </div>
	</div>
	<%@include file="templateConfigUser.jsp"%>
	<!--单位模态框  -->
	  <div  id="addTemplateCom"   class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog modal-lg"  style="width:500px; height:300px; position:absolute; left:50%; top:50%; margin-left:-250px; margin-top:-150px;">
		  <form id="editPrintNumForm" class="form-horizontal bv-form" action="${ctx }/printer/EditPrintNum.do" method="post">
	         <input id="comvalue" type="hidden"/>
		  <div class="modal-content">
			<div class="modal-header">
			  <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span>
			  </button>
			  <h4 class="modal-title" id="myModalLabel">选择用户单位</h4>
			</div>
			<div class="modal-body">
			  <div id="configCompanyTree" class="ztree" style="margin-top:20px; overflow-y: hidden;"></div>
			</div>
			<div class="modal-footer">
			  <button id="modalClose" type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			  <button id="submitCompanyButton" type="button" class="btn btn-primary" data-dismiss="modal" >确定</button>
			</div>
		  </div>
		  </form>
		</div>
	  </div>
  </section>
    </div>
</div>
  

<script type="text/javascript">

 
$(function(){
	
	// 将所有.ui-choose实例化
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
	    $('#sliderParent')
	        .jackWeiSlider(option)
	        // .setText('2018-4-5 02:39:00')
	        .setProgress(1.0)
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
			$('#photoFile').val("");
			document.getElementById("photo").style.display="none";//隐藏
			document.getElementById("alpha").style.display="none";//隐藏
		 }else{
			document.getElementById("photo").style.display="";
			document.getElementById("alpha").style.display="";
		 }
	})
	
   // validate("#addTemplateForm"); 表单验证,若引用总体的验证,ie8只能判断第一个
	$("#addTemplateForm").bootstrapValidator({
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
	
	//时间控件开始
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
	
	//时间控件结束
	
	$("#submitComButton").click(function() {
		var account = "";
		var userid = "";
		var index = 0;
		$("[name=checkboxt]:checkbox").each(function(){
			if(this.checked){
				if(index == 0){
					account = $(this).parent("th").attr("name");
					userid = $(this).val();
				}else{
					account = account + "," + $(this).parent("th").attr("name");
					userid = userid + "," + $(this).val();
				}
				index++;
			}
		});
		 if(index == 0){
			//layer.alert( "请选择用户", {icon:0});
			//return;
		}
		$("#userNames").val(account);
		$("#userIds").val(userid);
		shoHide('#templateAddShow','#templateConfigUser','');
	});
	
	$("#closeCompanyButton").click(function() {
		$('#templateConfigUser').modal('hide');
		
	});
	
	$("#selectAllUser").click(function() {
		var account = "印模单位下所有用户" ;
		var userid = "0";
		$("#userNames").val(account);
		$("#userIds").val(userid);
		shoHide('#templateAddShow','#templateConfigUser','');
	});
});

	//-----原来单开页面的的选择用户单位,现在将新开页面修改为模态框---代码挪用进来
	$("#selectCom").click(function() {
		$('input, textarea').placeholder();
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
				var companyid = $("[name=companyId]").val();
				$("#companyId").val(companyid);
			//	$("#companyName").val($("[name=companyName]").val());
				$("[name=companyName]").val($("#comvalue").val());
				$("#userIds").val("");
				$("#userNames").val("");
				$('#addTemplateCom').modal('hide');
		});
		 //模态框关闭
		 $("#modalClose").click(function(){
		
				$('#addTemplateCom').modal('hide');
			});

		 $("#submitTemplateButton").click(function(){
			    var before =  document.getElementById("befor").value;
				var after =  document.getElementById("after").value;
				var optValue = $("select[name='type']").val();
				var num = $(".jws-text").html();
		        num = num.substr(0,num.length-1);
			   
			    if(optValue!=3){
			    	 $("#transparency").val(num);
			    }else{
			    	 $("#transparency").val(100);
			    }
				$('#addTemplateForm').bootstrapValidator('validate');
				if ($('#addTemplateForm').data('bootstrapValidator').isValid()) {
					if (!$('#companyName').val()) {
						layer.alert( "单位不能为空", {icon:2});
					}else if(optValue!=3 && !$('#photoFile').val()){
						layer.alert( "请输入印模图片", {icon:2});
					}
					else if(!before  || before=="请点击进行日期选择"){
						layer.alert( "请选取印模起始时间", {icon:2});
					}else if(!after || after=="请点击进行日期选择"){
						layer.alert( "请选取印模截止时间", {icon:2});
					}
					/* else if(!$('#userNames').val()){
						layer.alert( "请选择用户", {icon:2});
					} */else {
						var form=$("#addTemplateForm");
						form.ajaxSubmit({
							success:function(data){
							    if(data == 'ok'){		
							  	  	layer.alert("印模制作成功",{icon:1});
						        	var url="${ctx }/template/templateList.do";
						        	loadUrl(url);
							      }else if(data==undefined){
							    	  layer.alert( "请求失败", {icon:2});
							      }else{
							    	  layer.alert("请求失败,"+data,{icon:2});
							      }
							    },error:function(){
							    	layer.alert("请求失败",{icon:2});
							    }
						 });
					}
					
				}else {
					$('#addTemplateForm').bootstrapValidator('validate');
				}
		 });

	

	function selectUsers(){
		if($("#companyId").val()){
			shoHide('#templateConfigUser','#templateAddShow','');
			var requestComid=$("#companyId").val();
			var url = "";
			url = "${ctx }/template/configUserList.do?cid=" + requestComid;
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
										url : "${ctx }/template/configUserList.do?cid=" + requestComid+"&pageNo="+ e.curr,
										success : function(jsonResult) {
											$("#comUserTable tr:not(:first)").remove();
											//遍历一个数组or集合
											var ta = jsonResult.result;
												if (ta == "") {
													layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
												} else {
													$.each(ta,function(i, item) {
														//追加html文本
														$("#comUserTable tbody").append("<tr><th name="+item.name+"><input name='checkboxt' class='input_checked' type='checkbox' type='checkbox' value='"+item.id+"'></th><td>"
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

</script>



