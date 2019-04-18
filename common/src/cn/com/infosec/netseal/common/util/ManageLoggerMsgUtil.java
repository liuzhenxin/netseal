package cn.com.infosec.netseal.common.util;

import java.util.Hashtable;

import cn.com.infosec.netseal.common.define.Constants;

public class ManageLoggerMsgUtil {
	private static Hashtable<String, String> container = new Hashtable<String, String>();
	
	static {
		container.put(Constants.LOG_OPTYPE_LOGCONFIG, "保存日志配置");
		container.put(Constants.LOG_OPTYPE_ADDCOMPANY, "添加单位");
		container.put(Constants.LOG_OPTYPE_EDITCOMPANY, "修改单位信息");
		container.put(Constants.LOG_OPTYPE_DELCOMPANY, "删除单位树节点");
		container.put(Constants.LOG_OPTYPE_SYSUSERLOGIN, "登录");
		container.put(Constants.LOG_OPTYPE_SYSUSERLOGOUT, "登出");
		container.put(Constants.LOG_OPTYPE_ADDSYSUSER, "添加用户");
		container.put(Constants.LOG_OPTYPE_EDITSYSUSER, "修改管理员信息");
		container.put(Constants.LOG_OPTYPE_DELSYSUSER, "删除管理员");
		container.put(Constants.LOG_OPTYPE_RESETSYSUSERPWD, "重置管理员密码");
		container.put(Constants.LOG_OPTYPE_UPDATESYSUSERPWD, "修改管理员密码");
		container.put(Constants.LOG_OPTYPE_ADDUSER, "增添加签章人");
		container.put(Constants.LOG_OPTYPE_EDITUSER, "修改签章人");
		container.put(Constants.LOG_OPTYPE_DELUSER, "删除签章人");
		container.put(Constants.LOG_OPTYPE_EDITROLE, "角色授权");
		container.put(Constants.LOG_OPTYPE_ADDTEMPLATE, "制作印模");
		container.put(Constants.LOG_OPTYPE_EDITTEMPLATE, "修改印模信息");
		container.put(Constants.LOG_OPTYPE_DELTEMPLATE, "删除印模");
		container.put(Constants.LOG_OPTYPE_UPDATETEMPLATESTATUS, "修改印模状态");
		container.put(Constants.LOG_OPTYPE_REQUESTSEAL, "印章申请");
		container.put(Constants.LOG_OPTYPE_AUDITSEAL, "印章审核");
		container.put(Constants.LOG_OPTYPE_DELREQUEST, "删除印章申请");
		container.put(Constants.LOG_OPTYPE_DELSEAL, "删除印章");
		container.put(Constants.LOG_OPTYPE_UPDATESEALSTATUS, "修改印章状态");
		container.put(Constants.LOG_OPTYPE_EDITSEAL, "修改印章");
		container.put(Constants.LOG_OPTYPE_SERVERJKSCERTREQUEST, "JKS密钥证书请求");
		container.put(Constants.LOG_OPTYPE_SERVERSM2CERTREQUEST, "SM2证书请求");  
		container.put(Constants.LOG_OPTYPE_SERVERSM2CARDCERTREQUEST, "加密卡SM2证书请求");  
		container.put(Constants.LOG_OPTYPE_SERVERJKSCERTIMPORT, "JKS证书导入");
		container.put(Constants.LOG_OPTYPE_SERVERPFXCERTIMPORT, "PFX证书导入");
		container.put(Constants.LOG_OPTYPE_SERVERSM2SIGNCERTIMPORT, "SM2签名证书导入");
		container.put(Constants.LOG_OPTYPE_SERVERSM2ENCCERTIMPORT, "SM2加密证书导入");
		container.put(Constants.LOG_OPTYPE_SERVERSM2CARDCERTIMPORT, "加密卡SM2证书导入");
		container.put(Constants.LOG_OPTYPE_DELKEY, "删除密钥");
		container.put(Constants.LOG_OPTYPE_BACKUPSKEY, "下载密钥");
		container.put(Constants.LOG_OPTYPE_RECOVERYKEY, "密钥恢复");
		container.put(Constants.LOG_OPTYPE_ADDCERTCHAIN, "导入证书(链)");
		container.put(Constants.LOG_OPTYPE_DELCERTCHAIN, "删除授信证书");
		container.put(Constants.LOG_OPTYPE_ADDPDFTEMPLATE, "增加PDF模板");
		container.put(Constants.LOG_OPTYPE_DELPDFTEMPLATE, "删除PDF模板");
		container.put(Constants.LOG_OPTYPE_CRLMANAGESAVE, "保存CRL配置");
		container.put(Constants.LOG_OPTYPE_CRLMANAGEOPER, "修改CRL状态");
		container.put(Constants.LOG_OPTYPE_DBCONFIGSAVE, "保存数据库配置");
		container.put(Constants.LOG_OPTYPE_SAVELICENSE, "保存License文件");
		container.put(Constants.LOG_OPTYPE_CHECKREPORT, "巡检报告");
		container.put(Constants.LOG_OPTYPE_HACONFIGSAVE, "HA配置保存");
		container.put(Constants.LOG_OPTYPE_HASERVICEUPDATE, "启动/停止HA服务");
		container.put(Constants.LOG_OPTYPE_HOSTSCONFIGSAVE, "HOSTS配置");
		container.put(Constants.LOG_OPTYPE_TSACONFIGSAVE, "保存时间戳配置");
		container.put(Constants.LOG_OPTYPE_SERVERSM2CARDENCIMPORT, "加密证书导入");
		container.put(Constants.LOG_OPTYPE_DOWNSERVERCERT, "下载服务器证书");
		container.put(Constants.LOG_OPTYPE_DOWNLOGFILE, "下载日志");
		container.put(Constants.LOG_OPTYPE_DOWNSEAL, "下载印章");
		container.put(Constants.LOG_OPTYPE_CARDUSERLOGOUT, "加密卡用户注销");
		container.put(Constants.LOG_OPTYPE_CARDUSERLOGIN, "加密卡用户登录");
		container.put(Constants.LOG_OPTYPE_DOWNHAFILE, "下载HA日志");
		container.put(Constants.LOG_OPTYPE_DOWNLICENSE, "下载License文件");
		container.put(Constants.LOG_OPTYPE_DELLICENSE, "删除License文件");
		container.put(Constants.LOG_OPTYPE_DOWNCHECKREPORT, "下载巡检报告");
		container.put(Constants.LOG_OPTYPE_SYNCNTP, "NTP同步");
		container.put(Constants.LOG_OPTYPE_DOWNPDFTEMPLATE, "下载PDF模板");
		container.put(Constants.LOG_OPTYPE_SAVESYSCONFIG, "保存系统基本信息配置");
		container.put(Constants.LOG_OPTYPE_DELCERT, "删除证书");
		container.put(Constants.LOG_OPTYPE_GENSTAMP, "生成图章");
		container.put(Constants.LOG_OPTYPE_SYSUSERCLEAR, "管理员解锁");
		
	}

	public static String getManageLoggerMsg(String logOptype) {
		String ManageLoggerMsg = container.get(logOptype);
		if (ManageLoggerMsg == null)
			return "没有对应的操作描述";
		else
			return ManageLoggerMsg;
	}
	
	
	public static void main(String[] args) {
		System.out.println(ManageLoggerMsgUtil.getManageLoggerMsg("1111"));
	}
}
