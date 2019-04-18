<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglibs.jsp"%>
  			
<div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
       <div class="menu_section">
        <ul class="nav side-menu" id="sidebarMenu">
        <c:forEach var="menu" items="${menuList}" varStatus="s">
        <c:if test="${menu.pid==-1}">
          <li class="nav-item"><a id="sysUser" class="clickMenu"><i class="${menu.img}"></i>${menu.name }<span class="fa fa-chevron-down"></span></a>
            <ul class="nav child_menu">
            <c:forEach var="menu2" items="${menuList}" varStatus="s2">
            	<c:if test="${menu.id==menu2.pid}">
              <li><a  class="leftMenus" href="javascript:loadUrl('${ctx }${menu2.url }')">${menu2.name }</a></li>
                </c:if>
            </c:forEach>
            </ul>
          </li>
          </c:if>
          </c:forEach>
          
        </ul>
      </div>
</div>  	
<script type="text/javascript">
$(function() {
	loadUrl("${ctx }/sysUser/sysIndexList.do");
	$('.nav-item>a').on('click',function(){
		if (!$('.nav').hasClass('nav-mini')) {
			if ($(this).next().css('display') == "none") {
				$('.nav-item').children('ul').slideUp(300);
				$(this).next('ul').slideDown(300);
				$(this).parent('li').addClass('active').siblings('li').removeClass('active');
			}else{
				$(this).next('ul').slideUp(300);
				$('.nav-item.active').removeClass('active');
			}
		}
	});
	
	$('.nav-item ul li a').click(function(){
		if($('.nav-item').hasClass('active')){
			$('.nav-item ul li a').each(function(){
				$(this).parent().css('background-color','rgb(42,63,84)')
			})
			$(this).parent().css('background-color','rgb(51,122,183)');
		}
	});
  });
</script>

