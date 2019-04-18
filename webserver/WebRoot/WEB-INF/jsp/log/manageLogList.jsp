<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

	    
<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
				
				 <div class="x_title">
				<div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
				  <div class="input-group" style="margin:0px; margin-right:0px; float:left;">
					<input  id="searchAccount"  type="text" class="form-control select2-search__field" type="search"   value="" style="border-color: rgb(230,233,237); width:240px;">
					<span class="input-group-btn pull-left" style="width:54px;">
					  <button class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
					</span>
				  </div>
				  <div class="input-group" style="margin:0px; margin-right:20px;  float:left; display: none;">
					<input id="searchOptype" type="text" class="form-control select2-search__field" type="search"  value="" style="border-color: rgb(230,233,237); width:240px; float:left;">
					<span class="input-group-btn pull-left" style="width:54px;">
					  <button class="btn btn-primary" type="button" style="color:#fff; float:left;" onclick="javascript:searchAccount()">搜索</button>
					</span>
				  </div>
				  <div class="input-group" style="margin:0px; margin-right:20px;  float:left; display: none;">
					<input id="searchClientHost" type="text" class="form-control select2-search__field" type="search"  value="" style="border-color: rgb(230,233,237); width:240px; float:left;">
					<span class="input-group-btn pull-left" style="width:54px;">
					  <button class="btn btn-primary " type="button" style=" color:#fff; float:left;" onclick="javascript:searchAccount()">搜索</button>
					</span>
				  </div>
				</div>
				
				 
				 <select class="form-control" id="select" style="width:150px; float:right;">
				  <option>用户名</option>
				  <option>操作类型</option>
				  <option>客户IP</option>
				 </select>
				<div class="clearfix"></div>
		  </div>
				  <div class="x_content" id="tab1">	    
	     
		<table id="logTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
		    <thead>
		    	<tr>
		        	<th>用户名</th>
		         	<th>操作类型</th>
		         	<th>客户IP</th>
		        	<th>操作时间</th>
		         	<th>返回码</th>
		        </tr>
		    </thead>
		    <tbody>
			    <c:forEach items="${page.result}" var="manageLog">
					<c:choose>
						<c:when test="${manageLog.sealMac == true}">
							<tr>
						        <td>${manageLog.account }</td>
						        <td>${manageLog.opType }</td>
						        <td>${manageLog.clientHost }</td>
						        <td>${manageLog.generateTimeCn }</td>
						        <td>${manageLog.returnCode }</td>
					       </tr>
						</c:when>
						<c:when test="${manageLog.sealMac == false}">
							<tr>
						        <td class="errorMac">${manageLog.account }</td>
						        <td class="errorMac">${manageLog.opType }</td>
						        <td class="errorMac">${manageLog.clientHost }</td>
						        <td class="errorMac">${manageLog.generateTimeCn }</td>
						        <td class="errorMac">${manageLog.returnCode }</td>
					       </tr>
						</c:when>
					</c:choose>
			    </c:forEach>
		    </tbody>
		</table>
		<div class="text-right" id="manageLogPage"></div>
	 </div>
			 </div>
			 </div>
		</div>
</section>	




<script type="text/javascript">

$(function(){

	page('manageLogPage','${page.totalPage}','${page.pageNo}',"${ctx }/log/manageLogList.do?pageNo=",'#tab1');
	var oS = document.getElementById('select');
	var oB = document.getElementById('input_box');
	var aO = oS.children;
	var aI = oB.getElementsByTagName('div');
	
	oS.onchange = function(){
		for(var i=0;i<aO.length;i++){
			var selectValue = $('#select').val();
			if(selectValue==aO[i].value){
				for(var j=0;j<aI.length;j++){
					aI[j].style.display = 'none';
				}
				aI[aO[i].index].style.display = 'block';
			}
		}

	}
});

//根据条件搜索
function searchAccount(){
	//var param="account="+firstSearch+"&optype="+secondSearch+"&clientHost="+thirdSearch;
	var url ="${ctx }/log/getmanageLogList.do";
    var firstSearch= $("#searchAccount").val();
    var secondSearch= $("#searchOptype").val();
    var thirdSearch=$("#searchClientHost").val();
	  
	$.ajax({
		type:"post",
		dataType:"json",
		data:"account="+firstSearch+"&opType="+secondSearch+"&clientHost="+thirdSearch,
	    url:url,
	    success:function(jsonResult){
	    	$("#logTable tr:not(:first)").remove();
	    	$("#manageLogPage").empty();
	    	
	    	$("#searchAccount").val("");
	    	$("#searchOptype").val("");
	    	$("#searchClientHost").val("");
	    	
	    	//遍历一个数组or集合
	    	var ta = jsonResult.page.result;
	    	if(ta == ""){
	    		layer.alert("此条件查询结果为空,请确认查询正确",{icon:0});
	    	}else{
	    		$.each(ta,function(i,item){
	 				//追加html文本
	 				if (item.sealMac == true) {
	 					$("#logTable ").append("<tr><td>"+item.account+"</td><td>"+item.opType+
		 						"</td><td>"+item.clientHost+"</td><td>"+item.generateTimeCn+"</td><td>"+item.returnCode+"</td></tr>");
					} else {
						$("#logTable ").append("<tr><td class='errorMac'>"+item.account+"</td><td class='errorMac'>"+item.opType+
		 						"</td><td class='errorMac'>"+item.clientHost+"</td><td class='errorMac'>"+item.generateTimeCn+"</td><td class='errorMac'>"+item.returnCode+"</td></tr>");
					}
	 				
	 			});
	 	    	//page('manageLogPage',jsonResult.page.totalPage,jsonResult.page.pageNo,"${ctx }/log/manageLogList.do?"+param+"&pageNo=",'#tab1');
				/* ----分页--开始 */
				//分页
				 laypage({
					cont : 'manageLogPage',
					skip : true,//跳转页面选项
					pages : jsonResult.page.totalPage, //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
					
					curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
						var pageNo = jsonResult.page.pageNo; // 当前页(后台获取到的)
						return pageNo ? pageNo : 1; // 返回当前页码值
					}(),
					jump : function(e, first) { //触发分页后的回调  
							if (!first) { //一定要加此判断，否则初始时会无限刷新
									$.ajax({
										type : "post",
										dataType : "json",
										url : "${ctx }/log/getmanageLogList.do",
										data:"account="+firstSearch
										+"&opType="+secondSearch+"&clientHost="+thirdSearch+"&pageNo="+ e.curr,
										success : function(jsonResult) {
											$("#logTable tr:not(:first)").remove();
											//遍历一个数组or集合
											var ta = jsonResult.page.result;
											if (ta == "") {
												layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
											} else {
												$.each(ta,function(i, item) {
													//追加html文本
									 				if (item.sealMac == true) {
									 					$("#logTable ").append("<tr><td>"+item.account+"</td><td>"+item.opType+
										 						"</td><td>"+item.clientHost+"</td><td>"+item.generateTimeCn+"</td><td>"+item.returnCode+"</td></tr>");
													} else {
														$("#logTable ").append("<tr><td class='errorMac'>"+item.account+"</td><td class='errorMac'>"+item.opType+
										 						"</td><td class='errorMac'>"+item.clientHost+"</td><td class='errorMac'>"+item.generateTimeCn+"</td><td class='errorMac'>"+item.returnCode+"</td></tr>");
													}
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
	    error:function(){
	    	layer.alert("查询错误",{icon:2});
	    }
	});
}


//给搜索框绑定事件
function inputChange(){
	//搜索框绑定回车事件
	$("input[type='search']").on('keypress',function(event){
		if(event.keyCode == "13"){
			var value = $("input[type=search]").val();
					
		    $("#tab1").load("${ctx }/log/manageLogList.do?account="+value);
		   $("#sel_menu2").prop("disabled",true);
		    page('manageLogPage','${page.totalPage}','${page.pageNo}',"${ctx }/log/manageLogList.do?pageNo=",'#tab1');
	     }
	
	});
	//搜索框内容变化事件
	$("input[type=search]").on("keyup",function(event){
		
		var currentVal = $(this).val();
		if(event.keyCode != null){
			if(currentVal != lastVal){
				$("li.select2-results__option").each(function(index){
					var title = $(this).text();
					var array = title.split(":");
					if(array.length>0){
						title = array[0];
					}
					title += ":"+currentVal;
					$(this).text(title);
					$($("#sel_menu2 option")[index]).text(title);
					inputText = title;
				});
			}
		}
		lastVal = currentVal;
	});
}

//动态添加条件
	function addSearch(name){
		var addMap=[];
		//判断：true：下拉框添加搜索条件 false：根据收藏添加条件；
		if(name==20161118){
			var name=$("#showSearch option:selected").val();
		}
		if(name=="用户名"){
			
			if(exist(name)){
			addMap.push('<tr id=用户名><td class=condi2>用户名:<input name=requestUserName id=requestUserName  class="btn-sm form-input" >')
			addMap.push('<a name="用户名" style="margin-left:9px" class="hand form-input" onclick="del(this.name);">x</a>');
			addMap.push('</td></tr>');
			$("#add_Con").append(addMap.join(''));
			dataMap.push(name);
			size();
			addMap=[];
			}else{
				layer.alert("条件已存在，请重新选择","");
			}
			
			
		}else if(name=="操作类型"){
			
			if(exist(name)){
			addMap.push("<tr id=操作类型><td class=condi2>操作类型:<input class='btn-sm form-input'  name=approveUserName >")
			addMap.push('<a class="hand form-input" style="margin-left:9px" name="操作类型" onclick="return del(this.name);">x</a>');
			addMap.push('</td></tr>');
			$("#add_Con").append(addMap.join(''));
			dataMap.push(name);
			addMap=[];
			size();
			}else{
				layer.alert("条件已存在，请重新选择","")
			}
		
		}else if(name=="客户IP"){
			
			if(exist(name)){
			addMap.push("<tr id=客户IP><td class=condi1>客户IP:<input  class='btn-sm form-input'  name=approveObject >");
			addMap.push('<a class="hand form-input" style="margin-left:9px" name=客户IP onclick="return del(this.name);">x</a>');
			addMap.push('</td></tr>');
			$("#add_Con").append(addMap.join(''));
			addMap=[];
			dataMap.push(name);
			size();
			}else{
				layer.alert("条件已存在，请重新选择","")
			}
			
		}else if(name=="操作时间"){
			
			if(exist(name)){
				size();
				addMap.push("<tr id=操作时间><td class=condi1>操作时间:");
				addMap.push("<input type=text name=optimeStart  class='btn-sm form-input' form-input  id=optimeStart placeholder=起始>");
				addMap.push("<input type=text name=optimeEnd  class='btn-sm form-input'  id=optimeEnd placeholder=截止>");
				
				addMap.push('<a class="hand form-input" style="margin-left:9px" name=操作时间 onclick="return del(this.name);">x</a>');
				
				addMap.push("</td></tr>");
			$("#add_Con").append(addMap.join(''));
			dataMap.push(name);
			addMap=[];
			//时间控件
			   var start = {
			   elem: '#optimeStart',
			   format: 'YYYY-MM-DD',
			   choose: function(datas){
			      end.min = datas;
			      end.start = datas;
			   }
			};
			var end = {
				elem: '#optimeEnd',
				format:"YYYY-MM-DD",
				choose: function(datas){
					start.max = datas;
			   }
			};
			laydate(start);
			laydate(end); 
			}else{
				layer.alert("条件已存在，请重新选择","")
			}
			
		}
	}
	//判断条件是否已存在
	function exist(name){
		var bool=true;
		for(key in dataMap){
			if(dataMap[key]==name){
				bool=false;
			}
		}
		return bool;
	}
	//动态改变表格大小
	 function size(){
		 var bottom=$("#size").css("margin-bottom");
		 var bottomNum=parseInt(bottom);
		
		 if(sizeCount==0){
			var newBottom=bottomNum+24+"px";	
			$("#size").css("margin-bottom",newBottom); 
			 sizeCount++;
			 if(sizeLeng!=0){
				 sizeLeng--;
			 }
		 }else{
			 var newBottom=bottomNum+34+"px";
			 $("#size").css("margin-bottom",newBottom);
			 sizeCount++;
			 if(sizeLeng!=0){
				 sizeLeng--;
			 }
		 }
		
	}
	
	//点击查询按钮
		function search(){
			var requestUserName=$("#requestUserName").val();//--用户名-1
			var approveObject=$("input[name=approveObject]").val();//--客户IP-3
			var approveUserName=$("input[name=approveUserName]").val();//--操作类型-2
			var optimeStart=$("input[name=optimeStart]").val();//--操作时间-起始-4
			var optimeEnd=$("input[name=optimeEnd]").val();//--操作时间-截止-4
			if(!requestUserName){// 客户IP没有查询
				requestUserName = "";
			} 
				
				if(!approveObject){// 客户IP没有查询
					approveObject = "";
				} if(!approveUserName){//操作类型没有查询
					approveUserName = "";
				} if(!optimeStart || !optimeEnd){//操作时间没有查询 
					optimeStart = "";optimeEnd="";
				}
				
				url="${ctx }/log/getmanageLogList.do?account="+requestUserName
				+"&optype="+approveUserName+"&clientHost="+approveObject+"&optimeStart="+optimeStart+"&optimeEnd="+optimeEnd;
				url = url.replace("undefined", "null");
			
			
			
			$.ajax({
		 		type:"get",
		 		dataType:"json",
		 	    url:url,
		 	    success:function(jsonResult){
		 	    	$("#logTable tr:not(:first)").remove();
		 	    	$("#manageLogPage").empty();
		 	    	//遍历一个数组or集合
		 	    	var ta = jsonResult.page.result;
		 	    	if(ta == ""){
		 	    		layer.alert("此条件查询结果为空,请确认查询正确",{icon:0});
		 	    	}else{
		 	    		$.each(ta,function(i,item){
			 				//追加html文本
			 				$("#logTable ").append("<tr><td>"+item.account+"</td><td>"+item.optype+"</td><td>"+item.clientHost+"</td><td>"+item.optimeCN+"</td><td>"+item.returnCode+"</td></tr>");
			 			});
			 	    	page('manageLogPage',jsonResult.page.totalPage,jsonResult.page.pageNo,"${ctx }/log/manageLogList.do?pageNo=",'#tab1');
		 	    	}
		 			
		 	    },
		 	    error:function(){
		 	    	layer.alert("查询错误",{icon:2});
		 	    }
		 	});
			
		}
	//删除条件
		function del(ele){
			var index="";
			var bq="#"+ele;
			
			$(bq).remove();
			sizeSmal();
			for(key in dataMap){
				if(dataMap[key]==ele){
					var index=key;
				} 
			}
			dataMap.splice(index,1);
			//dataMap
		}
		//动态改变表格大小
		 function sizeSmal(){
			 var bottom=$("#size").css("margin-bottom");
			 var bottomNum=parseInt(bottom);
			
			 if(sizeLeng==0){
				var newBottom=bottomNum-24+"px";	
				$("#size").css("margin-bottom",newBottom); 
				 sizeCount--;
				 sizeLeng++;
			 }else{
				 var newBottom=bottomNum-34+"px";
				 $("#size").css("margin-bottom",newBottom);
				 sizeCount--;
				 sizeLeng++;
			 }
			
		}

</script>

