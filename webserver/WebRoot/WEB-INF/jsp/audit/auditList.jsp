<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_32" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_32.cab#version=6.30.73.1"></OBJECT>
<OBJECT classid="clsid:9A10AF0C-A21B-4835-A276-7272405973C0" id="infosecEnroll_64" width="0" height="0" style="display:none" codebase="${ctx}/cab/SKFCSPEnroll_64.cab#version=6.30.73.1"></OBJECT>
<input id="adminCertSn" name="adminCertSn" type="hidden" value="${adminCertSn }">

<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">印章审核</h2>
</div>
		<ul class="breadcrumb" style="">
<li>
	<i class="fa fa-home"></i>
	<i style="color:rgb(42,63,84); font-style:normal;">印章管理    /    印章审核</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;">
			印章审核</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
<div class="tab-pane fade in active" id="home">
	<section class="content">						
		<div class="row">
		  <div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
			  <div class="x_title">
			  		<input type="button" value="印章审核" class="btn btn-primary" id="auditButton" style=" float:left;" data-toggle="modal"  data-target="#myModal"/>
		  		    <input type="button" value="删除" class="btn btn-primary" id="deleteButton" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm1"/>
			<div class="clearfix"></div>
		  </div>
		  <div class="x_content">
			<br>
			<div id="table-box" style="display: block;">
				<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
				  <thead>
					<tr>
					<th width="1"><input id="checkboxChange" class="js-checkbox-all " type="checkbox" /></th>
						<th>印章名称</th>
						<th>签章人</th>
						<th>单位名称</th>
						<th>申请时间</th>
						<th>印章启用日期</th>
						<th>印章结束日期</th>
						<th>备注</th>
					</tr>
				  </thead>
				  <tbody>
					<c:forEach items="${page.result}" var="request">
						<c:choose>
							<c:when test="${request.sealMac == true}">
								<tr>
									<th width="1"><input name="checkboxt" class="input_checked" type="checkbox" value="${request.id }"></th>
									<td><a href="javascript:loadUrl('${ctx }/audit/requestView.do?id=${request.id}')">${request.name }</a></td>
									<td>${request.userName }</td>
									<td>${request.companyName }</td>
									<td>${request.generateTimeCn }</td>
									<td>${request.notBeforCn }</td>
									<td>${request.notAfterCn }</td>
									<td>${request.remark }</td>
								</tr>
							</c:when>
							<c:when test="${request.sealMac == false}">
								<tr>
									<th width="1"><input name="checkboxt" class="input_checked" type="checkbox" value="${request.id }"></th>
									<td><a class="errorMac" href="javascript:loadUrl('${ctx }/audit/requestView.do?id=${request.id}')">${request.name }</a></td>
									<td class="errorMac">${request.userName }</td>
									<td class="errorMac">${request.companyName }</td>
									<td class="errorMac">${request.generateTimeCn }</td>
									<td class="errorMac">${request.notBeforCn }</td>
									<td class="errorMac">${request.notAfterCn }</td>
									<td class="errorMac">${request.remark }</td>
								</tr>
							</c:when>
						</c:choose>
					</c:forEach>
				</tbody>
				</table>
				<div class="text-right" id="requestPage"></div>
			    </div>
				  </div>
				</div>
			  </div>
			</div>
		</section>
	</div>
</div>	






<script type="text/javascript">
function VBATOA(v){
	var tmp = new VBArray(v);
	return tmp.toArray();
} 

function enumCerts(){//所有可用于签名的证书
	var ret = new Array();
	// sm2 cert
	var Count = Enroll.sm_skf_getCountOfCert();
	for (var i = 0; i < Count; i++) {
		var Cert = VBATOA(Enroll.sm_skf_getCertInfo(i));
		var c10 = Cert[10];
		if (c10 == 'AT_SIGNATURE')
			ret.push(Cert);
	}
	// rsa cert
	var Count1 = Enroll.rsa_csp_getCountOfCert();
	for (var i = 0; i < Count1; i++) {
		var Cert = VBATOA(Enroll.rsa_csp_getCertInfo(i));
		var c0 = Cert[0];
		var c7 = Cert[7];
		if (c0.indexOf('Microsoft') != 0 && c7 == 'AT_SIGNATURE')
			ret.push(Cert);
	}
	return ret;
}
function readCertSignature(certs,certSn){//返回选择的证书
	var rSn;
	var Count = certs.length;
	for (var i = 0; i < Count; i++) {
		if (certs[i].length == 12)
			rSn = certs[i][9]; //证书序列号
		else
			rSn = certs[i][6];

		if (rSn == certSn)
			return certs[i];
	}
	return null;
}

	$(function() {
		var ver = navigator.platform;
		if('Win32'==ver)
			Enroll=  document.getElementById('infosecEnroll_32');
		else
			Enroll = document.getElementById('infosecEnroll_64');
		
		
		//分页
		page('requestPage', '${page.totalPage}', '${page.pageNo}', "${ctx }/audit/auditList.do?pageNo=");
// 		laypage({
// 			cont : 'requestPage',
// 			skip : true,//跳转页面选项
// 			pages : '${page.totalPage}', //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
// 			curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
// 				var pageNo = '${page.pageNo}'; // 当前页(后台获取到的)
// 				return pageNo ? pageNo : 1; // 返回当前页码值
// 			}(),
// 			jump : function(e, first) { //触发分页后的回调
// 				if (!first) { //一定要加此判断，否则初始时会无限刷新
// 					loadUrl("${ctx }/audit/requestList.do?pageNo=" + e.curr);
// 				}
// 			}
// 		});
		//---复选框样式
		icheck(".js-checkbox-all");
		
		 $("#deleteButton").click(function() {
			 var ids = "";
				$("[name=checkboxt]:checkbox:checked").each(function() {
					ids += $(this).val() + ",";
				});
				if (ids == "") {
					layer.alert( "请选择印章申请删除", {icon:0});
					return;
				}
				
			layer.confirm("确定要删除印章申请吗?",{btn:["确定","取消"]},function(){
					$.ajax({
						url : "${ctx }/audit/delRequest.do",
						type : "get",
						data : "id=" + ids,
						dataType : "json",
						success : function(data) {
							if (data.success) {
								layer.alert( "删除成功", {icon:1});
							} else {
								layer.alert( "删除失败", {icon:2});
							}
							loadUrl("${ctx }/audit/auditList.do");
						},
						error : function() {
							layer.alert( "请求失败", {icon:2});
							loadUrl("${ctx }/audit/auditList.do");
						}
					});
				});
		 });
		 $("#auditButton").click(function() {
			 var id="";
			 var index = 0;
			 $("[name=checkboxt]:checkbox:checked").each(function(){
					if (this.checked) {
						if (index == 0) {
							id = $(this).val();
						} 
 
						index++;
					}
			 });
			 if(index == 0 || index>1){
				 layer.alert( "请选择要审核的一条记录", {icon:0});
				 return;
			 }else{
				 var certs = enumCerts();//所有可用于签名的证书
				 var adminCertSn=$("#adminCertSn").val();
				 cert=readCertSignature(certs,adminCertSn);
				 if(cert==null){
					layer.alert("没有读取到管理员证书,请插入U_KEY",{icon:2});
					return;
				}
				 
				 
			 loadUrl("${ctx }/audit/toAuditSeal.do?id="+id); 
			 }
		 });
		 $("#checkboxChange").click(function() {
			 $("[name=checkboxt]:checkbox").prop("checked",this.checked);			 			
		 });
	});

	
</script>


