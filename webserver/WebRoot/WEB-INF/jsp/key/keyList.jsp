<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>

	<div class="title clearfix">
		<h2 style="font-size:34px; font-weight:bold;">服务器证书配置</h2>
	</div>
	<ul class="breadcrumb" style="">
		 <li>
			<i class="fa fa-home"></i>
			<i style="color:rgb(42,63,84); font-style:normal;" id="bread">系统管理    /    服务器证书配置</i>
		</li>
	</ul>	
	<div class="x_panel">
		<div class="form-group col-lg-12 col-md-12 col-xs-12 col-sm-12">
			<div class="form-group col-lg-6 col-md-6 col-xs-6 col-sm-6">
				<div class="col-lg-2 col-md-2 col-xs-2 col-sm-2 center">
					<label style="margin-top: 5px" class="control-label">签名密钥  :</label>
				</div>
				<div class="col-sm-4">
					<select class="form-control  input-sm" id="sigKeyId">
						<option value="-1"> -- 请选择密钥 -- </option>
						<c:forEach items="${sigKeyList }" var="klist">
							<option value="${klist.id }"  ${klist.id == serverSigKeyId ? 'selected':''}>${klist.certDn }</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-lg-1 col-md-1 col-xs-1 col-sm-1 center">
					<input id="setSignatureKey" class="btn btn-primary btn-sm" type="button" value="确定">
				</div>
			</div>
			
			<div class="form-group col-lg-6 col-md-6 col-xs-6 col-sm-6">
				<div class="col-lg-2 col-md-2 col-xs-2 col-sm-2 center">
					<label style="margin-top: 5px" class="control-label">加密密钥  :</label>
				</div>
				<div class="col-sm-4">
					<select class="form-control  input-sm" id="encKeyId">
						<option value="-1"> -- 请选择密钥 -- </option>
						<c:forEach items="${encKeyList }" var="enclist">
							<option value="${enclist.id }"  ${enclist.id == serverEncKeyId ? 'selected':''}>${enclist.certDn }</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-lg-1 col-md-1 col-xs-1 col-sm-1 center">
					<input id="setEncryptionKey" class="btn btn-primary btn-sm" type="button" value="确定">
				</div>
			</div>
		</div>
	</div>
	<br />
	 <ul id="maintab" class="nav nav-tabs" style="height:30px;">
		<li class="active"><a href="#tab1"  onclick='showPage("tab1","${ctx }/key/keyJksList.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
				<strong>RSA</strong>(JKS)</a>
		</li>
		<li class=""><a href="#tab2"  onclick='showPage("tab2","${ctx }/key/keyPfxList.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle1">
				<strong>RSA</strong>(PFX)</a>
		</li>
		<li class=""><a href="#tab3"  onclick='showPage("tab3","${ctx }/key/keySm2List.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle2">
				<strong>SM2</strong></a>
		</li>
		<li class=""><a href="#tab4"  onclick='showPage("tab4","${ctx }/key/keySm2CardList.do")' data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle3">
				<strong>SM2</strong>(加密卡)</a>
		</li>
	 </ul>
	 <div id="myTabContent" class="tab-content" style="margin-top:20px;">
			<div class="tab-pane fade in active" id="tab1"></div>
			<div class="tab-pane fade in" id="tab2"></div>
			<div class="tab-pane fade in" id="tab3"></div>
			<div class="tab-pane fade in" id="tab4"></div>
	 </div>


<script type="text/javascript">
function showPage(tabId, url) {
	$('#maintab a[href="#' + tabId + '"]').tab('show');
	$('#' + tabId).html('页面加载中，请稍后...');
	$('#' + tabId).load(url); // ajax加载页面

}
$(function() {
	$('#tab1').load("${ctx }/key/keyJksList.do");
	
	//设置签名密钥
	$("#setSignatureKey").click(function() {
		var ids = $('#sigKeyId').val();
		if (ids == -1) {
			layer.alert( '请选择签名密钥', {icon:0});
			return;
		}
		 $.ajax({
			url : "${ctx }/key/setSignKey.do",
			type : "get",
			data : "id=" + ids,
			dataType : "json",
			success : function(data) {
				if (data.success) {
					layer.alert( data.message, {icon:1});
				} else{
					layer.alert( data.message, {icon:2});
				}
			},
			error : function() {
				layer.alert( "请求失败", {icon:2});
			}
		}); 
	});
	
	//设置加密密钥
	$("#setEncryptionKey").click(function() {
		var ids = $('#encKeyId').val();
		if (ids == -1) {
			layer.alert( '请选择加密密钥', {icon:0});
			return;
		}
		 $.ajax({
			url : "${ctx }/key/setEncKey.do",
			type : "get",
			data : "id=" + ids,
			dataType : "json",
			success : function(data) {
				if (data.success) {
					layer.alert( data.message, {icon:1});
				} else{
					layer.alert( data.message, {icon:2});
				}
			},
			error : function() {
				layer.alert( "请求失败", {icon:2});
			}
		}); 
	});
	
});	
</script>

