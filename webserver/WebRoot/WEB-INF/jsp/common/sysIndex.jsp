<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>


<section class="content">
	<div class="row">
		<div class="col-md-12 col-sm-12 col-xs-12">
			<div class="x_panel">
				<div class="main">
					<form method="post" class="form-horizontal form-label-left"
						style="margin-top:20px;">
						<div class="form-group">
							<label class="control-label col-xs-3">名称</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">电子签章系统</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">版本号</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${version }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">设备型号</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${deviceModel }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">设备序号</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${deviceSn }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3"></label>
							<div class="col-xs-8"></div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">License有效期</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${validTime }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3"> 连接数限制</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${thredNum }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">证书数限制</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${certNum }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3"></label>
							<div class="col-xs-8"></div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">业务服务端口</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${businessPort }</p>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-3">业务服务状态</label>
							<div class="col-xs-8">
								<p class="col-xs-8 showdetail">${businessStatus }</p>
							</div>
						</div>
					</form>
				</div>

			</div>
		</div>
	</div>
</section>
	
<script type="text/javascript">
	$(function() {});
</script>