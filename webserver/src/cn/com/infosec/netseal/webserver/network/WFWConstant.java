package cn.com.infosec.netseal.webserver.network;

public interface WFWConstant {
	public final static String CONFIG_FILE = "web.properties";
	public final static String WEBUICFG_SERVERHOME = "NetSignServerDIR";
	public final static String TOMCAT_CONFIG_PATH = "TomcatConfigPath";
	public final static String MAPPING_NETWORKPORT_PATH="mappingNetworkPortPath";
	public final static String WEBUILOG_PATH = "webuiLogPath";
	public final static String SHELL_PATH = "/bin/sh";
	public final static int 	  ROUTE_NET = 0;
	public final static int 	  ROUTE_HOST = 1;
	 
	public final static String IFCONFIG_SHELL = "/sbin/ifconfig";
	public final static String EXEIFCONFIG_SHELL = "/sbin/ifconfig";
	public final static String GATEWAYCONFIG_SHELL = "/sbin/route add default gw";
	public final static String IPV6GATEWAYCONFIG_SHELL = "/sbin/route -A inet6 add default gw";
	public final static String DEFAULTGATEWAYCONFIGFILE = "/etc/sysconfig/network";
	public final static String DEFAULTGATEWAYCONFIGFILEBAK = "/etc/sysconfig/networkbak";
	public final static String NETWORKCARDFILEPATH = "/etc/sysconfig/network-scripts/";
	public final static String NETWORKCARDFILEPERFIX = "ifcfg-";
	public final static String DNSCFGFile = "/etc/resolv.conf";
	public final static String HOSTSFILE = "/etc/hosts";
	
	//调用的脚本
	/*public final static String DATE_SHELL= " date ";
	public final static String DATE_SET_ERROR= "date: invalid date";
	public final static String HWCLOCK_SHELL= "hwclock -w";
	public final static String RESTART_NETWORK_SHELL= "/etc/init.d/network restart";
	public final static String RESTART_SYSTEM_SHELL= "reboot";
	public final static String SHUTDOWN_SYSTEM_SHELL= "shutdown now -h";*/
	
	
	//HA 配置相关的项目
	public final static String HACF_FILE = "/etc/ha.d/ha.cf";
	public final static String HARESOURCES_FILE = "/etc/ha.d/haresources";
	public final static String HEARTBEAT_SHELL = "/etc/init.d/heartbeat";
	public final static String HA_STOPSTATUS= "heartbeat is stopped.";
	public final static String HA_STARTSTATUS= "heartbeat OK";
	public final static String HA_LOG_PATH= "/var/log/";
	
	//端口健康检查
	public final static String CHECK_PORT = "netstat -an |grep tcp";
	public final static String CHECK_CONFIG_PORT = "cat";
	
}
