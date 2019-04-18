/*
 *  主要对页面id为linkArea的进行初始化，依赖jquery，在使用该js前导入jquery.js
 * 
 */

function tip(data){
	alert(data);
}

function initLinks(){
	var pages = {
		/*"首页":[],*/
		"时间配置":[{"name" : "系统时间","link":"systime.do","img":"img/time.png","stat":true},
		           {"name" : "NTP配置","link":"ntp.do","img":"img/NTP.png","stat":true}
		          ],
		"网络配置":[{"name":"网口配置","link":"network.do","img":"img/eth.png","stat":true},
		        {"name":"bond配置","link":"bond.do","img":"img/eth.png","stat":true},
				{"name":"DNS配置","link":"dns.do","img":"img/dns.png","stat":true},
		        {"name":"静态路由配置","link":"route.do","img":"img/static.png","stat":true},
		        {"name":"网关配置","link":"gateway.do","img":"img/gateway.png","stat":true}
		        /*{"name":"HA配置","link":"ha.do","img":"img/HA.png","stat":true},*/
		        /*{"name":"SNMP配置","link":"snmp.do","img":"img/SNMP.png","stat":true}*/
		        ],
        "设备配置":[{"name":"设备管理","link":"device.do","img":"img/equipment.png","stat":true},
				{"name":"分组管理","link":"group.do","img":"img/group.png","stat":true}],
		"平台监控":[{"name":"平台监控","link":"monitor.do","img":"img/monitor.png","stat":true}],
		"平台管理":[{"name":"平台日志","link":"log.do","img":"img/log.png","stat":true},
				{"name":"平台配置","link":"sysManager.do","img":"img/config.png","stat":true}
				]
	};
	var area = $("#linkArea");
	area.empty();
	area.append(function(){
		var tmp = "";
		for(var key in pages){
			tmp += "<li class='brand'><h5>"+key+"</h5></li>";
			for(var i = 0; i < pages[key].length; i++){
				var page = pages[key][i];
				var style = page["stat"] ? "": "class='disabled'";
				tmp += "<li "+style+"><a href='"+page["link"]+"'><img class='pull-left' style='width: 1.2em; height: 1.2em;margin-right:0.5em;vertical-align: middle;fill: currentColor;overflow: hidden;' src='"+
				page["img"]+"'/>"+page["name"]+"</a></li>";
			}
		}
		return tmp;
	});
	
}		
$(document).ready(function() {
	initLinks();
})