<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp"%>

        <section class="content">
            <div class="row">
              <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="x_panel">
                    <div class="main">
                    
                    <div class="x_title" id="x_title1" >
                         <input type="button" value="添加" class="btn btn-primary" class="btn btn-primary" style=" float:left;margin-left:10px;" data-toggle="modal" data-target="#addTempModal"/>
                         <input type="button" value="删除" class="btn btn-primary" id="delCertTemplateButton" style=" float:left; margin-left:10px;" data-toggle="modal"  data-target=".bs-example-modal-sm"/>
                    
                        <div class="col-md-3 col-sm-3 col-xs-12 form-group  clearfix" id="input_box" style="width:334px; margin:0px; float:right;  ">
                          <div class="input-group" style="margin:0px; margin-right:0px; float:left;">
                            <input  id="searchName" name="searchUserText" type="text" class="form-control select2-search__field" type="search"   value="" style="border-color: rgb(230,233,237); width:240px;">
                            <span class="input-group-btn pull-left" style="width:54px;">
                              <button  name="searchUser" class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
                            </span>
                          </div>
                          
                          <div class="input-group" style="margin:0px; margin-right:0px; float:left;display: none;">
                            <select class="form-control" id="searchCa" style="width:240px; ">
                                      <option>全部</option>
                                      <option value="rsa_ca">RSA</option>
                                      <option value="sm2_ca">SM2</option>
                                  </select>
                            <span class="input-group-btn pull-left" style="width:54px;">
                              <button  name="searchUser" class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
                            </span>
                          </div>
                          
                          <div class="input-group" style="margin:0px; margin-right:0px; float:left;display: none;">
                            <select class="form-control" id="searchType" style="width:240px;">
                                  <option>全部</option>
                                  <option>单证</option>
                                  <option>双证</option>
                             </select>
                            <span class="input-group-btn pull-left" style="width:54px;">
                              <button  name="searchUser" class="btn btn-primary" type="button" style="color:#fff;" onclick="javascript:searchAccount()">搜索</button>
                            </span>
                          </div>
                          
                        </div>
                        
                       
                      
                      <select class="form-control" id="select" style="width:150px; float:right;">
                          <option>模板名称</option>
                          <option>CA类型</option>
                          <option>模板类型</option>
                      </select>
                    
                    
                     <div class="clearfix"></div>
                   </div>
                   
                   <div id="table-box1">
                       <table id="tempDataTable" class="table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%" style="">
                         <thead>
                           <tr>
                               <th width="1"><input id="check_all" class="input_checked js-checkbox-all" type="checkbox" /></th>
                               <th>名称</th>
                               <th>类型</th>
                           </tr>
                         </thead>
                         <tbody>
                          <c:forEach items="${page.result }" var="temp">
                           <tr>
                               <th width="1">
                                   <input name="checkboxt" class="input_checked" type="checkbox" value="${temp.name }">
                               </th>
                               <td width="50%">${temp.name}</td>
                               <td width="48%">${temp.type}</td>
                           </tr>
                           </c:forEach>
                         </tbody>
                       </table>
                        <div class="text-right" id="templatePage"></div>
                   </div>
                </div>
            </div>
        </div>  
    </div>
</section>


<div class="modal fade" id="addTempModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">添加模板</h4>
            </div>
            <div id="myTabContent" class="tab-content" style="margin-top: 20px;">
                <div class="tab-pane fade in active" id="home">
                    <section class="content">
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <form id="certTemplateConfigForm" action="${ctx }/system/rads/rsaCaConfigSave.do" method="post" data-parsley-validate="" class="form-horizontal form-label-left" novalidate style="margin-top: 20px;">
                                        <div class="form-group">
                                            <label class="control-label col-xs-3">模板名称:</label>
                                            <div class="col-xs-6">
                                              <input type="text" id="template" class="form-control col-xs-6"  name="template" value="">
                                            </div>
                                        </div>
                                        
                                        <div class="form-group">
                                           <label class="control-label col-xs-3">CA类型:</label>
                                           <div class="col-xs-6">
                                             <select class="form-control " id="certType" name="certType">
                                               <option value="rsa_ca">RSA</option>
                                               <option value="sm2_ca">SM2</option>
                                            </select>
                                           </div>
                                         </div>
                                        <div class="form-group">
                                            <label class="control-label col-xs-3">模板类型:</label>
                                            <div class="col-xs-6">
                                             <select class="form-control " id="isDCert" name="isDCert">
                                                <option value="0">单证</option>
                                                <option value="1">双证</option>
                                             </select>
                                            </div>
                                        </div>
                                        
                                        <br>
                                        <div class="form-group">
                                            <button id="addCertTemplateBtn" type="button" class="btn btn-primary col-md-offset-4">确定</button>
                                            <button id="closeFolderMode" type="button" class="btn btn-primary col-md-offset-1">关闭</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
<script type="text/javascript">

$(document).ready(
        function() {
        //===分页===
        page('templatePage', '${page.totalPage}', '${page.pageNo}','${ctx }/system/rads/certTemplateConfig.do?pageNo=','#tab3');
        //---复选框样式
        icheck(".js-checkbox-all");
    });
function searchAccount(){   
    var searchName = $("#searchName").val();
    var certType = $("#searchCa").val();
    var isDCert = $("#searchType").val(); 
    
    $.ajax({
        url : "${ctx }/system/rads/getTemplateList.do",
        type : "post",
        data : {"searchName":searchName,"isDCert":isDCert,"certType":certType},
        dataType : "json",
        success : function(data) {
            if (data.success) { 
                $("#tempDataTable tr:not(:first)").remove();
                var tempList = data.page.result;
                if(tempList == ""){
                    layer.alert("此条件查询结果为空,请确认查询条件正确",{icon:0});
                }else{
                    $.each(tempList,function(i,item){
                        //追加文本
                        $("#tempDataTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.name
                                +"'></th><td width='50%'>" + item.name + "</td><td width='48%'>" + item.type + "</td></tr>");
                    });
                }
                icheck(".js-checkbox-all");
                /* ----分页--开始 */
                //分页
                 laypage({
                    cont : 'templatePage',
                    skip : true,//跳转页面选项
                    pages : data.page.totalPage, //可以叫服务端把总页数放在某一个隐藏域，再获取。假设我们获取到的是18
                    
                    curr : function() { //通过url获取当前页，也可以同上（pages）方式获取
                        var pageNo = data.page.pageNo; // 当前页(后台获取到的)
                        return pageNo ? pageNo : 1; // 返回当前页码值
                    }(),
                    jump : function(e, first) { //触发分页后的回调  
                            if (!first) { //一定要加此判断，否则初始时会无限刷新
                                    $.ajax({
                                        type : "post",
                                        dataType : "json",
                                        url : "${ctx }/system/rads/getTemplateList.do",
                                        data: {"searchName":searchName,"isDCert":isDCert,"certType":certType,"pageNo":e.curr},
                                        success : function(data) {
                                            $("#tempDataTable tr:not(:first)").remove();
                                            //遍历一个数组or集合
                                            var ta = data.page.result;
                                            if (ta == "") {
                                                layer.alert( "此条件查询结果为空,请确认查询正确", {icon:0});
                                            } else {
                                                $.each(ta,function(i, item) {
                                                    //追加html文本
                                                    $("#tempDataTable tbody").append("<tr><th><input name='checkboxt' class='input_checked' type='checkbox' value='"+item.name
                                                            +"'></th><td width='50%'>" + item.name + "</td><td width='48%'>" + item.type + "</td></tr>");
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
                
            }else{
                layer.alert("查询错误",{icon:2});
            }
        },
        error : function() {
            layer.alert("请求失败",{icon:2});
        }
    });
}



$(function(){   
    
    //---复选框样式
    icheck(".js-checkbox-all");
    
  //条件框js
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

    };
    
    
    
    
    //模态框关闭
    $("#closeFolderMode").click(function() {
        $("#template").val('');
        $("#addTempModal").modal('hide');
    });
    
    
    
    $("#addCertTemplateBtn").click(function(){
       var form = $("#certTemplateConfigForm");
       form.bootstrapValidator('validate');
       if(form.data('bootstrapValidator').isValid()){
          form.attr("action", "${ctx }/system/rads/addCertTemplate.do");
          form.ajaxSubmit({
              success:function(data){
                    if(data.success) {
                        $(".modal-backdrop").remove();
                        layer.alert(data.message, {icon:1});
                        $('#tab3').load("${ctx }/system/rads/certTemplateConfig.do");
                    } else {
                        $(".modal-backdrop").remove();
                        layer.alert(data.message, {icon:2});
                    }
                },
                error:function(){
                    $(".modal-backdrop").remove();
                    layer.alert("请求失败",{icon:2});
                }
              });
          }else{
              form.bootstrapValidator('validate');
          }
      });
     
      $("#delCertTemplateButton").click(function(){
          var id = "";
          var index = 0;
          $("[name=checkboxt]:checkbox:checked").each(function(){
              if(this.checked){
                  if(index == 0){
                      id = $(this).val();
                  }else{
                      id +=","+$(this).val()
                  }
                  index++;
              }
          });
          if(index == 0){
              layer.alert("请选择要删除的模板",{icon:0});
              return;
          }
          layer.confirm("确定要删除已有模板选项吗?",{btn:["确定","取消"]},function(){
              $.ajax({
                  url:"${ctx }/system/rads/delCertTemplate.do",
                  type:"post",
                  data:{"id":id},
                  dataType:"json",
                  success: function(data){
                      if(data.success) {
                          layer.alert(data.message, {icon:1});
                          $('#tab3').load("${ctx }/system/rads/certTemplateConfig.do");
                      }else{
                          layer.alert(data.message, {icon:2});
                      }
                  },
                  error:function(){
                      layer.alert("请求失败",{icon:2});
                  }
              });
          });
         });
     
      //表单验证
      $("#certTemplateConfigForm").bootstrapValidator({
          fields: {
              template:{
                  validators:{
                      notEmpty:{
                          message:'证书模板不能为空'
                      },
                        stringLength : {
                            min : 1,
                            max : 50,
                            message : '证书模板长度为1~50'
                        }
                  }
              }
              
              
          }
      });
      
  });
 
</script>