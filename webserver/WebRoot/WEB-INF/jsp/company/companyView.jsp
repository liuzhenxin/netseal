<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
<div class="form-group col-xs-12" >
	<label class="control-label col-xs-2" style="padding-top:7px;">名称 
	</label>
	<div class="col-xs-10">
	  <input type="text" class="form-control col-xs-10"  id="companyName" value="${company.name}" readonly>
	</div>
</div>
<div class="form-group col-xs-12">
	<label class="control-label col-xs-2" >备注
	</label>
	<div class="col-xs-10">
	  <textarea class="form-control col-xs-10" style="height:100px; resize:none;" readonly  >${company.remark}</textarea>
	</div>
</div>
