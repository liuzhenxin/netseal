/**
 * 页面头部，利用转换工具：http://tool.chinaz.com/Tools/Html_Js.aspx 将HTML转换为js文件
 */
var name = "";
$.getScript('js/logout.js', function(){});
$.getScript('js/jquery.cookie.js', function(){
    var nameKey = "loginName";
    var name_tmp = $.cookie(nameKey);
    if(name_tmp){
        name = name_tmp;
    }else{
        var url=location.search;
        var i=url.indexOf('?');
        if(i != -1){
            name = url.substring(i+1);
            var paras = name.split("&");
            for(var i = 0; i < paras.length; i++){
                var tmp = paras[i];
                var index = tmp.indexOf(nameKey);
                if( index > -1){
                    name = tmp.substring(index+nameKey.length+1);
                    $.cookie(nameKey, name);
                    break;
                }
            }
        }
    }
    buildNav();
});


function buildNav(){
    $('body').prepend('<nav class="navbar navbar-default header" role="navigation">'
        +'<div class="container-fluid" style="width:75%">'
        +'<div class="nav navbar-header" style="min-width: 71%">'
        +'<a class="navbar-brand" href="/sso/index.jsp">'
        +'<img class="img-responsive" style="margin-top:-7px;" src="img/CenSign.png" />'
        +'</a>'
        +' <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target="#navCommon" aria-expanded="false" >'
        +'<span class="sr-only" >Toggle navigation</span>'
        +'<span class="icon-bar"></span>'
        +'<span class="icon-bar"></span>'
        +'<span class="icon-bar"></span>'
        +'</button>'
        +'<p class="navbar-text" style="padding-left:45%">平台配置</p>'
        +'</div>'
        +'<div class="collapse navbar-collapse" id="navCommon">'
        +'<ul class="nav navbar-nav navbar-right">'
        +'<li class="dropdown">'
        +'<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">'
        +'<span class="glyphicon glyphicon-user">'+name+'</span><span class="caret"></span>'
        +'</a>'
        +'<ul class="dropdown-menu">'
        +'<li><a href="#" onclick=\"toLogout()\">退出</a></li>'
        +'</ul>'
        +'</li>'
        +'<li style="margin-top:1%">'
        +'<a href="/sso/index.jsp">'
        +'<span class="glyphicon glyphicon-home">首页</span>'
        +'</a>'
        +'</li>'
        +'</ul>'
        +'</div>'
        +'</div>'
       
        +'</nav>'
        );
    $('body').css("padding-bottom", "100px");
    $('body').append("<div class=\'navbar navbar-default navbar-fixed-bottom\' style=\'width:100%;min-height: 22px;background-color:#f8f8f8;text-align:center;\'><span class=\'text-center\' style=\'padding-top:3px;\'>&copy;Infosec Corporation, ALL Rights Reserved</span></div>");
}