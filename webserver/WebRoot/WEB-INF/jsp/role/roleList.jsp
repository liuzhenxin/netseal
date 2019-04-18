<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="title clearfix">
	<h2 style="font-size:34px; font-weight:bold;">角色管理</h2>
</div>
		<ul class="breadcrumb" style="">
	<li>
		<i class="fa fa-home"></i>
		<i style="color:rgb(42,63,84); font-style:normal;" id="bread">用户管理    /    角色管理</i>
	</li>
</ul>		
<ul id="myTab" class="nav nav-tabs" style="height:30px;">
	<li class="active"><a href="#home" data-toggle="tab" style="height:30px; line-height:3px;" id="tabTitle">
			角色管理</a>
	</li>
</ul>

<div id="myTabContent" class="tab-content" style="margin-top:20px;">
	<div class="tab-pane fade in active" id="home">
		<section class="content">						
			<div class="row">
			  <div class="col-md-12 col-sm-12 col-xs-12">
				<div class="x_panel">
			<div class="main">
				<div class="inner-title">

				</div>
				<div class="table-responsive">
					<table id="datatable-responsive" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
						<thead>
							<tr>
								<th>角色编号</th>
								<th>角色名称</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
								<c:forEach items="${list}" var="rlist">
									<tr>
										<td>${rlist.id }</td>
										<td>${rlist.name }</td>
										<td>
											<a href="javascript:loadUrl('${ctx }/role/toEditRoleMenu.do?id=${rlist.id }')">授权</a>
										</td>
									</tr>
								</c:forEach>

						</tbody>
					</table>
				</div>
				
				
				
			</div>
			</div>
			</div>	
			</div>
		</section>
	</div>
</div>

