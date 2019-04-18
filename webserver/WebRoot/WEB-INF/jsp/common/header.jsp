<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="taglibs.jsp"%>

<a href="javascript:loadUrl('${ctx }/sysUser/sysIndexList.do')" class="logo" title="系统基本信息">
      <!-- mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini fa fa-info fa-2x"></span>
      <!-- logo for regular state and mobile devices -->
      <span class="logo-lg info-logo clearfix">
      	<img src="${ctx }/img/logo.jpg" class="pull-left" style="margin-left:-10px; padding-top:17px;"/>
      </span>
    </a>

    <!-- Header Navbar -->
    <nav class="navbar navbar-static-top" role="navigation" style="height:50px;">
      <!-- Sidebar toggle button
      <a href="#" class="fa fa-reorder fa-lg" data-toggle="push-menu" role="button" id="reorder">
        <span class="sr-only"></span>
      </a>-->
      
      <h3 class="fr netseal" style="font-family: 'Source Sans Pro',sans-serif;">电子签章系统(NetSeal)&nbsp;&nbsp;&nbsp;</h3>
      <!-- Navbar Right Menu -->
      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav" style="margin:0px; margin-right:5px;">
          <!-- Messages: style can be found in dropdown.less-->
          
          <!-- User Account Menu -->
          <li class="dropdown user user-menu" id="dropdown">
            <!-- Menu Toggle Button -->
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" style=" height:50px;" title="更改密码或注销">
              <!-- hidden-xs hides the username on small devices so only the image appears. -->
              <span class="hidden-xs user-font fa fa-user fa-lg" ></span>
              <span class="" style="color:rgb(115,135,156)">当前用户：${sessionScope.sysUser.name}(${sessionScope.sysUser.roleName})</span>
            </a>
            <ul class="dropdown-menu" style="width:235px;" id="dropdownMenu">
              <!-- The user image in the menu -->
              <li class="user-header">

                <p>
                ${sessionScope.sysUser.name}(${sessionScope.sysUser.roleName})
                  <small>欢迎登录！</small>
                </p>
              </li>
              <!-- Menu Footer-->
              <li class="user-footer" id="userFooter">
                <div class="pull-left">
                  <a href="javascript:loadUrl('${ctx }/sysUser/toUpdateSysUserPwd.do?account=${sessionScope.sysUser.account }')" class="btn btn-default btn-flat" id="changepwd">更改密码</a>
                </div>
                <div class="pull-right">
                  <a href="${ctx }/sysUser/logout.do" class="btn btn-default btn-flat" id="logout">注销</a>
                </div>
                <!-- 
                <div class="pull-right">
                  <a href="javascript:;" class="btn btn-default btn-flat" id="close">关闭</a>
                </div>
                 -->
              </li>
            </ul>
          </li>
          <!-- Control Sidebar Toggle Button -->
        </ul>
      </div>
    </nav>
    
	
 
<script type="text/javascript">   
   $(function() {		
	$('#dropdown').click(function(){
		if($('.dropdown-menu').css('display') == 'block'){
			$('.dropdown-menu').css('display','none');
		}else{
			$('.dropdown-menu').css('display','block');
		}
	});
	$('.dropdown-menu').mouseleave(function(){
		$('.dropdown-menu').css('display','none');
	})
  });	
</script>