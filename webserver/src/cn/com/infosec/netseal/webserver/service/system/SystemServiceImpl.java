package cn.com.infosec.netseal.webserver.service.system;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.ScalarVO;
import cn.com.infosec.netseal.common.entity.vo.config.ConfigVO;
import cn.com.infosec.netseal.common.entity.vo.report.ReportVO;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.JmxManager;
import cn.com.infosec.netseal.common.manager.SchedulerManager;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.ExecSh;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.NetWorkUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.license.LicenseChecker;
import cn.com.infosec.netseal.common.util.license.LicenseInfo;
import cn.com.infosec.netseal.itextpdf.text.pdf.TSAClientBouncyCastle;
import cn.com.infosec.netseal.webserver.network.ha.HAInfo;
import cn.com.infosec.netseal.webserver.network.ha.HAManager;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.PdfReportUtil;
import cn.com.infosec.netseal.webserver.util.SigarUtil;

@Service
public class SystemServiceImpl extends BaseService {

	/**
	 * 数据库联通性(数据库连接状态)
	 * 
	 * @return
	 */
	public boolean testDB() {
		boolean result = false;
		Connection con = null;
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			Class.forName(config.getDriverClassName());
			con = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * 测试时间戳服务器连接状态RSA
	 * 
	 * @return
	 */
	public boolean testTsaRsa(ConfigVO configVO) {
		boolean result = false;
		try {
			TSAClientBouncyCastle tsaClient = null;
			String url = configVO.getTsaRsaUrl();
			String username = configVO.getTsaRsaUsername();
			String userpwd = configVO.getTsaRsaUserpwd();
			String policy = configVO.getTsaRsaPolicy();

			tsaClient = new TSAClientBouncyCastle(url, username, userpwd);
			if (StringUtil.isNotBlank(policy))
				tsaClient.setTsapolicy(policy);

			byte[] tsToken = tsaClient.getTimeStampToken(null, new byte[20]);
			if (tsToken != null)
				result = true;
		} catch (Exception e) {
			LoggerUtil.errorlog("test tsa error, ", e);
		}
		return result;
	}
	
	

	/**
	 * 测试时间戳服务器连接状态SM2
	 * 
	 * @return
	 */
	public boolean testTsaSM2(ConfigVO configVO) {
		boolean result = false;
		try {
			TSAClientBouncyCastle tsaClient = null;
			String url = configVO.getTsaSM2Url();
			String username = configVO.getTsaSM2Username();
			String userpwd = configVO.getTsaSM2Userpwd();
			String policy = configVO.getTsaSM2Policy();

			tsaClient = new TSAClientBouncyCastle(url, username, userpwd);
			if (StringUtil.isNotBlank(policy))
				tsaClient.setTsapolicy(policy);

			byte[] tsToken = tsaClient.getTimeStampToken(null, new byte[20]);
			if (tsToken != null)
				result = true;
		} catch (Exception e) {
			LoggerUtil.errorlog("test tsa error, ", e);
		}
		return result;
	}


	/**
	 * 测试数据库是否能连接
	 * 
	 * @return
	 */
	public boolean testDB(ConfigVO configVO) {
		boolean result = false;
		Connection con = null;
		try {
			Class.forName(configVO.getDriverClassName());
			String url = configVO.getUrl();
			if (url.contains("sqlserver")) { // SQLserver 连接方式
				String ip = url.substring(url.indexOf("//"), url.lastIndexOf(":")).replace("//", "");
				String port = url.substring(url.lastIndexOf(":"), url.lastIndexOf(";")).replace(":", "");
				String tab_name = url.substring(url.lastIndexOf("="), url.length()).replace("=", "");
				String new_url = url.substring(0, url.indexOf("//") + 2);
				
				Properties pr = new Properties();
				pr.put("user", configVO.getUsername());
				pr.put("password", configVO.getPassword());
				pr.put("serverName", ip);
				pr.put("portNumber", port);
				pr.put("instanceName ", tab_name);
				
				con = DriverManager.getConnection(new_url, pr);
			} else 
				con = DriverManager.getConnection(configVO.getUrl(), configVO.getUsername(), configVO.getPassword());
			
			result = true;
		} catch (Exception e) {
			result = false;
			LoggerUtil.errorlog("test db error, ", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * 获取License信息
	 * 
	 * @return
	 */
	public LicenseInfo licenseInfo() {
		ConfigUtil config = ConfigUtil.getInstance();
		String licensePath = Constants.LICENSE_PATH + Constants.LICENSE;
		byte[] licBytes = FileUtil.getFile(licensePath);// 读取License文件
		byte[] entlic = StringUtil.base64Decode(licBytes);
		return LicenseChecker.checkLicense(NetWorkUtil.getHostMac(config.getNetworkCard()), entlic);
	}

	/**
	 * 验证crl同步配置是否有效
	 * 
	 * @param configUtil
	 * @return
	 */
	public boolean authCrlFromLdap(ConfigVO configVO) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, configVO.getLdapContextFactory());
		env.put(Context.PROVIDER_URL, configVO.getLdapUrl());
		env.put(Context.SECURITY_AUTHENTICATION, Constants.LDAP_NONE);

		if (StringUtil.isNotBlank(configVO.getLdapAccount())) {
			env.put(Context.SECURITY_PRINCIPAL, configVO.getLdapAccount());
			env.put(Context.SECURITY_AUTHENTICATION, configVO.getLdapSecurityAuthentication());
			env.put(Context.SECURITY_CREDENTIALS, configVO.getLdapPassword());
		}

		InitialDirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			return true;
		} catch (Exception e) {
			LoggerUtil.errorlog("authCrlFromLdap error ", e);
			return false;
		} finally {
			if (ctx != null)
				try {
					ctx.close();
				} catch (Exception e) {
					LoggerUtil.errorlog("close authCrlFromLdap error ", e);
				}
		}
	}

	/**
	 * 验证crl同步配置是否有效
	 * 
	 * @param configUtil
	 * @return
	 */
	public boolean authCrlFromLdap() {
		ConfigUtil config = ConfigUtil.getInstance();
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, config.getLdapContextFactory());
		env.put(Context.PROVIDER_URL, config.getLdapUrl());
		env.put(Context.SECURITY_AUTHENTICATION, Constants.LDAP_NONE);

		if (StringUtil.isNotBlank(config.getLdapAccount())) {
			env.put(Context.SECURITY_PRINCIPAL, config.getLdapAccount());
			env.put(Context.SECURITY_AUTHENTICATION, config.getLdapSecurityAuthentication());
			env.put(Context.SECURITY_CREDENTIALS, config.getLdapPassword());
		}

		InitialDirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			return true;
		} catch (Exception e) {
			LoggerUtil.errorlog("authCrlFromLdap error ", e);
			return false;
		} finally {
			if (ctx != null)
				try {
					ctx.close();
				} catch (Exception e) {
					LoggerUtil.errorlog("close authCrlFromLdap error ", e);
				}
		}
	}

	/**
	 * 获取日志信息
	 * 
	 * @return
	 */
	private String checkLogFile() {
		StringBuffer checkLog = new StringBuffer();
		String accessPath = Constants.LOG_PATH + "access.log";
		String systemPath = Constants.LOG_PATH + "system.log";
		String managePath = Constants.LOG_PATH + "manage.log";
		String errorPath = Constants.LOG_PATH + "error.log";

		File file = new File(accessPath);
		if (file.exists())
			checkLog.append("存在access.log,");
		else
			checkLog.append("无access.log,");

		file = new File(systemPath);
		if (file.exists())
			checkLog.append("存在system.log,");
		else
			checkLog.append("无system.log,");
		file = new File(managePath);
		if (file.exists())
			checkLog.append("存在manage.log,");
		else
			checkLog.append("无manage.log,");
		file = new File(errorPath);
		if (file.exists())
			checkLog.append("存在error.log");
		else
			checkLog.append("无error.log");

		return checkLog.toString();
	}

	/**
	 * 生成巡检
	 * 
	 * @throws Exception
	 */
	public void genReport(ReportVO report) throws Exception {
		Document doc = null;
		try {
			Map<String, String> dataMap = SigarUtil.getInstance().getDataMap();
			Map<String, String> netInfoMap = SigarUtil.getInstance().getNetInfoMap();
			List<String> resultList = new ArrayList<String>();
			String dateDir = DateUtil.getDateDir();
			if (new File(Constants.REPORT_PATH).exists()) {
				// 删除昨天以后目录
				FileUtil.deleteDir(Constants.REPORT_PATH, Constants.REPORT_PATH + dateDir);
			}
			String path = dateDir + "NetSeal_CheckReport" + DateUtil.getCurrentTime() + ".pdf";
			report.setReportPath(path);
			File file = new File(Constants.REPORT_PATH + dateDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			String reportNumber = String.valueOf(DateUtil.getCurrentTime());
			report.setReportNumber(reportNumber);
			// 创建文档
			doc = PdfReportUtil.getInstance(Constants.REPORT_PATH + path, reportNumber);

			// 添加标题
			PdfReportUtil.addTitle(doc, "1. 系统概况");
			// 添加内容
			PdfReportUtil.addContent(doc, "信安电子签章系统巡检报告");

			// 添加标题 2. 产品信息
			PdfReportUtil.addTitle(doc, "2. 产品信息");
			List<String> titleList = new ArrayList<String>();
			titleList.add("服务器名称");
			titleList.add("产品型号");
			titleList.add("版本号");
			titleList.add("License信息");
			titleList.add("IP地址");
			LicenseInfo li = licenseInfo();
			List<List<ScalarVO>> contentList = new ArrayList<List<ScalarVO>>();
			List<ScalarVO> scalarVOList = new ArrayList<ScalarVO>();
			String version = Constants.VERISON;
			String licenseNumber = "";
			int maxcertNum = 0;
			int maxThred = 0;
			String validTime = "";
			if (li != null) {
				maxcertNum = li.getMaxCertNum();
				maxThred = li.getMaxThred();
				licenseNumber = "证书数限制" + maxcertNum + ",连接数限制" + maxThred;
				validTime = ",有效期" + DateUtil.getDateTime(li.getValidTime_start()) + " 至 " + DateUtil.getDateTime(li.getValidTime_end());
			}
			report.setVersion(version);
			report.setLicenseNumber(licenseNumber + validTime);
			PdfReportUtil.addScalarVO(scalarVOList, "电子签章系统", false, false, false);
			PdfReportUtil.addScalarVO(scalarVOList, "NetSeal", false, false, false);
			PdfReportUtil.addScalarVO(scalarVOList, version, false, false, false);
			PdfReportUtil.addScalarVO(scalarVOList, licenseNumber + validTime, false, false, false);
			PdfReportUtil.addScalarVO(scalarVOList, report.getIp(), false, false, false);

			contentList.add(scalarVOList);

			// 添加表格 产品信息
			PdfReportUtil.addTable(doc, "产品信息", titleList.size(), titleList, contentList);

			// 3. 详细运行状况
			PdfReportUtil.addTitle(doc, "3. 运行状况");
			titleList = new ArrayList<String>();

			contentList = new ArrayList<List<ScalarVO>>();
			scalarVOList = new ArrayList<ScalarVO>();
			PdfReportUtil.addScalarVO(scalarVOList, "机器名称", false, true, true);
			PdfReportUtil.addScalarVO(scalarVOList, "NetSeal", false, false, false);

			PdfReportUtil.addScalarVO(scalarVOList, "主/备机", false, true, true);
			PdfReportUtil.addScalarVO(scalarVOList, report.getNodeNameCN(), false, false, false);

			PdfReportUtil.addScalarVO(scalarVOList, "管理地址", false, true, true);
			PdfReportUtil.addScalarVO(scalarVOList, "https://" + report.getIp() + ":8443/webserver", false, false, false);

			contentList.add(scalarVOList);
			PdfReportUtil.addTable(doc, "系统信息", scalarVOList.size(), titleList, contentList);

			// =======端口和IP对应关系
			titleList = new ArrayList<String>();
			contentList = new ArrayList<List<ScalarVO>>();
			scalarVOList = new ArrayList<ScalarVO>();
			titleList.add("序号");
			titleList.add("网口");
			titleList.add("IP地址");
			int number = 1;
			List<String> ipList = new ArrayList<String>();
			for (String name : netInfoMap.keySet()) {
				StringBuffer item = new StringBuffer("<tr class=\"text-c\">");
				item.append(PdfReportUtil.addScalarVO(scalarVOList, String.valueOf(number++), false, true, true));
				item.append(PdfReportUtil.addScalarVO(scalarVOList, name, false, false, false));
				item.append(PdfReportUtil.addScalarVO(scalarVOList, netInfoMap.get(name), false, false, false));
				item.append("</tr>");
				ipList.add(item.toString());
			}
			report.setIpList(ipList);

			contentList.add(scalarVOList);
			PdfReportUtil.addTable(doc, "端口和IP对应关系", titleList.size(), titleList, contentList);

			// =======系统检测
			titleList = new ArrayList<String>();
			contentList = new ArrayList<List<ScalarVO>>();
			scalarVOList = new ArrayList<ScalarVO>();
			titleList.add("检测内容");
			titleList.add("检测方法");
			titleList.add("检测结果");
			titleList.add("判断标准");
			titleList.add("状况分析");
			List<String> checkList = new ArrayList<String>();
			// 检查系统时间
			StringBuffer item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "检查系统时间", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "date", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, DateUtil.getDate(DateUtil.getCurrentTime()), false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "漂移时间<1分钟", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// CPU使用率
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "CPU使用率", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "top", false, false, false));
			String cpuPer = dataMap.get("cpuPer");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, cpuPer + "%", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "<80%正常", false, false, false));
			String value = "正常";
			boolean isRed = false;
			double cpuPerD = Double.valueOf(cpuPer);
			if (cpuPerD >= 80) {
				value = "异常";
				isRed = true;
				resultList.add("CPU使用率" + cpuPer + "%大于等于80%,较高,存在异常.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());

			// 硬盘使用率
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "硬盘使用率", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "df -k", false, false, false));
			String usagePer = dataMap.get("usagePer");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, usagePer + "%", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "<80%正常", false, false, false));
			value = "正常";
			isRed = false;
			double usagePerD = Double.valueOf(usagePer);
			if (usagePerD >= 80) {
				value = "异常";
				isRed = true;
				resultList.add("硬盘使用率" + usagePer + "%大于等于80%,较高,存在异常.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// 内存使用率
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "内存使用率", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "top", false, false, false));
			String memPer = dataMap.get("memPer");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, memPer + "%", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "<80%正常", false, false, false));
			value = "正常";
			isRed = false;
			double memPerD = Double.valueOf(memPer);
			if (memPerD >= 80) {
				value = "异常";
				isRed = true;
				resultList.add("内存使用率" + memPer + "%大于等于80%,较高,存在异常.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// Swap分区使用率
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "Swap分区使用率", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "top", false, false, false));
			String swapPer = dataMap.get("swapPer");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, swapPer + "%", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "<50%正常", false, false, false));
			value = "正常";
			isRed = false;
			double swapPerD = Double.valueOf(swapPer);
			if (swapPerD >= 50) {
				value = "异常";
				isRed = true;
				resultList.add("Swap分区使用率" + swapPer + "%大于等于50%,较高,存在异常.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// 数据库联通性(数据库连接状态)
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "数据库联通性(数据库连接状态)", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "服务器与数据库建立连接", false, false, false));
			boolean testDB = testDB();
			value = "正常";
			isRed = false;
			String retCheck = "能建立数据库连接";
			if (!testDB) {
				value = "异常";
				isRed = true;
				retCheck = "不能建立数据库";
				resultList.add("数据库联通性,服务器不能与数据库建立连接,存在异常.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, retCheck, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, false, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// 后台服务状态
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "业务服务状态", false, true, true));
			int businessPort = ConfigUtil.getInstance().getListenerBusinessPort();
			String ret = ExecSh.exec("netstat -anp |grep " + businessPort);
			value = "正常";
			isRed = false;
			retCheck = "存在端口" + businessPort;
			if (ret.length() == 0) {
				value = "异常";
				isRed = true;
				retCheck = "不存在端口" + businessPort;
				resultList.add("业务服务状态,不存在端口" + businessPort + ",业务服务端口未被监听,存在异常.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "netstat -anp |grep " + businessPort, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, retCheck, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "业务服务端口被监听", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// 系统日志
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "系统日志", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "cd /opt/infosec/NetSeal/log目录下查看", false, false, false));
			String checkLog = checkLogFile();
			item.append(PdfReportUtil.addScalarVO(scalarVOList, checkLog, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append("</tr>");
			checkList.add(item.toString());

			// HA状态
			retCheck = "服务已停止";
			value = "异常";
			isRed = true;
			try {
				boolean serverStatus = HAManager.queryServerStatus();
				if (serverStatus) {
					retCheck = "服务正在运行";
					value = "正常";
					isRed = false;
				}
			} catch (Exception e) {
				retCheck = e.getMessage();
			}
			if (isRed)
				resultList.add("HA状态," + retCheck + ",存在异常.");
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "HA状态", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "ps -ef|grep heartbeat", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, retCheck, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "服务运行", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// HA虚拟IP
			HAInfo haInfo = null;
			String vmIp = "无";
			String haErrorInfo = "";
			try {
				haInfo = HAManager.getHAInfo();
				vmIp = "虚拟IP地址" + haInfo.getVmIP();

			} catch (Exception e) {
				haErrorInfo = e.getMessage();
				vmIp = haErrorInfo;
			}

			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "HA虚拟IP", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "cat /etc/ha.d/haresources文件", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, vmIp, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// 主备机切换日志
			String logPath = "";
			String logFile = "无日志文件";
			if (StringUtil.isBlank(haErrorInfo)) {
				logFile = "存在" + haInfo.getLogFile();
				logFile += "," + haInfo.getDebugFile() + "开头的日志文件";
				logPath = logFile.substring(0, logFile.lastIndexOf("/") + 1) + "目录查看";
			}
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "主备机切换日志", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, logPath, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, logFile, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// 数据库表空间
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "数据库表空间", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "客户手动检查", true, false, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "", false, false, false));
			item.append("</tr>");
			checkList.add(item.toString());
			// License状态
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "License状态", false, true, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "系统内部检查", false, false, true));
			long certNum = report.getCertNum();
			double cerNumPer = (double) certNum / (double) maxcertNum;
			String cerNumPerStr = String.format("%.2f", cerNumPer * 100);
			value = "正常";
			isRed = false;
			String resultInfo = "License状态,";
			if (cerNumPer * 100 >= 80) {
				value = "异常";
				isRed = true;
				resultInfo += "已使用证书数," + cerNumPerStr + "%大于等于80%,使用量较高;";
			}
			String socketNumPerStr = "";
			int socketNum = 0;
			try {
				Object obj = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "SocketNum");
				socketNum = Integer.parseInt(obj.toString().trim());
				double socketNumPer = (double) socketNum / (double) maxThred;
				socketNumPerStr = String.format("%.2f", socketNumPer * 100) + "%";
				if (socketNumPer * 100 >= 80) {
					value = "异常";
					isRed = true;
					resultInfo += "已使用线程数," + socketNumPerStr + "大于等于80%,使用量较高;";
				}
			} catch (Exception e) {
				value = "异常";
				isRed = true;
				resultInfo += "线程数不能正常监控";
			}
			if (isRed)
				resultList.add(resultInfo);
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "已使用证书数" + certNum + ",使用量" + cerNumPerStr + "%;已使用线程数" + socketNum + ",使用量" + socketNumPerStr, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, licenseNumber + ",使用量<80%", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));

			item.append("</tr>");
			checkList.add(item.toString());

			// CRL管理状态
			item = new StringBuffer("<tr class=\"text-c\">");
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "CRL管理状态", false, true, true));
			boolean crlIsRunning = SchedulerManager.isRunning(Constants.SCHEDULER_CRL);
			value = "正常";
			isRed = false;
			retCheck = "正在运行";
			if (!crlIsRunning) {
				value = "异常";
				isRed = true;
				retCheck = "停止运行";
				resultList.add("CRL管理状态,停止运行,如果使用CRL管理功能,检查配置信息是否正确.");
			}
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "系统内部检查", false, false, true));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, retCheck, false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, "正在运行", false, false, false));
			item.append(PdfReportUtil.addScalarVO(scalarVOList, value, isRed, false, false));
			item.append("</tr>");
			checkList.add(item.toString());

			contentList.add(scalarVOList);
			report.setCheckList(checkList);
			PdfReportUtil.addTable(doc, "系统检测", titleList.size(), titleList, contentList);

			if (resultList.size() == 0)
				resultList.add("无");

			// 4. 巡检结果总结
			PdfReportUtil.addTitle(doc, "4. 巡检结论");
			for (String result : resultList)
				PdfReportUtil.addContent(doc, result);

			report.setResultList(resultList);
		} finally {
			if (doc != null) {
				// 关闭流
				PdfReportUtil.close(doc);
			}
		}
	}

}
