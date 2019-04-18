package cn.com.infosec.netseal.common.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.common.dao.accessLog.AccessLogDaoImpl;
import cn.com.infosec.netseal.common.dao.manageLog.ManageLogDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.AccessLog;
import cn.com.infosec.netseal.common.entity.po.ManageLog;
import cn.com.infosec.netseal.common.util.ManageLoggerMsgUtil;
import cn.com.infosec.netseal.common.util.logger.AccessLogger;
import cn.com.infosec.netseal.common.util.logger.DebugLogger;
import cn.com.infosec.netseal.common.util.logger.ErrorLogger;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;
import cn.com.infosec.netseal.common.util.logger.LoggerException;
import cn.com.infosec.netseal.common.util.logger.LoggerRes;
import cn.com.infosec.netseal.common.util.logger.ManageLogger;
import cn.com.infosec.netseal.common.util.logger.SyslogLogger;
import cn.com.infosec.netseal.common.util.logger.SystemLogger;

/**
 * 日志记录
 */
@Component
public class LoggerUtil {

	private static DebugLogger debugLog;
	private static ManageLogger manageLog;
	private static SystemLogger systemLog;
	private static AccessLogger accessLog;
	private static ErrorLogger errorLog;
	private static SyslogLogger sysLog;

	@Autowired
	protected AccessLogDaoImpl accessLogDao;
	@Autowired
	protected ManageLogDaoImpl manageLogDao;

	/**
	 * 记录调试日志
	 * 
	 * @param
	 */
	private static Object objLockDebug = new Object();

	public static void debuglog(String s) {
		if (LoggerConfig.toFilePriv("debug")) {
			synchronized (objLockDebug) {
				if (debugLog == null || LoggerConfig.getMapValue(Constants.DEBUG)) {
					LoggerRes res = new LoggerRes();
					res.setLogFile(LoggerConfig.getLogPath() + "debug.log");
					res.setLoggerName("debug");
					res.setMaxFileSize(String.valueOf(LoggerConfig.getFileSize()));
					res.setMaxBackupIndex(LoggerConfig.getFileNumber());
					try {
						debugLog = new DebugLogger(res);
						LoggerConfig.setMapValue(Constants.DEBUG, true);
					} catch (LoggerException e) {
						e.printStackTrace();
					}
				}
			}
			if (debugLog != null)
				debugLog.Log(s);
		}
		// 发送syslog
		if (LoggerConfig.toSyslogPriv("debug"))
			syslog("DEBUG: " + s);
	}

	/**
	 * 记录调试日志, 包括出错堆栈信息
	 * 
	 * @param log
	 * @param tr
	 */
	public static void debuglog(String log, Throwable tr) {
		if (!LoggerConfig.toFilePriv("debug"))
			return;

		if (tr != null) {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bao);
			tr.printStackTrace(out);
			debuglog(log + "\r\n" + bao.toString());
		} else
			debuglog(log);
	}

	/**
	 * 记录系统日志
	 * 
	 * @param s
	 */
	private static Object objLockSystem = new Object();

	public static void systemlog(String s) {
		if (LoggerConfig.toFilePriv("system")) {
			synchronized (objLockSystem) {
				if (systemLog == null || LoggerConfig.getMapValue(Constants.SYSTEM)) {
					LoggerRes res = new LoggerRes();
					res.setLogFile(LoggerConfig.getLogPath() + "system.log");
					res.setLoggerName("system");
					res.setMaxFileSize(String.valueOf(LoggerConfig.getFileSize()));
					res.setMaxBackupIndex(LoggerConfig.getFileNumber());
					try {
						systemLog = new SystemLogger(res);
						LoggerConfig.setMapValue(Constants.SYSTEM, true);
					} catch (LoggerException e) {
						e.printStackTrace();
					}
				}
			}

			if (systemLog != null)
				systemLog.Log(s);
		}
		// 发送syslog
		if (LoggerConfig.toSyslogPriv("system"))
			syslog("SYSTEM: " + s);
	}

	/**
	 * 记录系统日志,带异常堆栈
	 * 
	 * @param log
	 * @param tr
	 */
	public static void systemlog(String log, Throwable tr) {
		if (!LoggerConfig.toFilePriv("system"))
			return;

		if (tr != null) {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bao);
			tr.printStackTrace(out);
			systemlog(log + "\r\n" + bao.toString());
		} else
			systemlog(log);
	}

	private static Object objLockError = new Object();

	public static void errorlog(String s) {
		if (LoggerConfig.toFilePriv("error")) {
			synchronized (objLockError) {
				if (errorLog == null || LoggerConfig.getMapValue(Constants.ERROR)) {
					LoggerRes res = new LoggerRes();
					res.setLogFile(LoggerConfig.getLogPath() + "error.log");
					res.setLoggerName("error");
					res.setMaxFileSize(String.valueOf(LoggerConfig.getFileSize()));
					res.setMaxBackupIndex(LoggerConfig.getFileNumber());
					try {
						errorLog = new ErrorLogger(res);
						LoggerConfig.setMapValue(Constants.ERROR, true);
					} catch (LoggerException e) {
						e.printStackTrace();
					}
				}
			}
			errorLog.Log(s);
		}
		// 发送syslog
		if (LoggerConfig.toSyslogPriv("error"))
			syslog("ERROR: " + s);
	}

	/**
	 * 记录错误日志,带异常堆栈
	 * 
	 * @param log
	 * @param tr
	 */
	public static void errorlog(String log, Throwable tr) {
		if (!LoggerConfig.toFilePriv("error"))
			return;

		if (tr != null) {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bao);
			tr.printStackTrace(out);
			errorlog(log + "\r\n" + tr.getMessage() + "\r\n" + bao.toString());
		} else
			errorlog(log);
	}

	private static Object objLockSyslog = new Object();

	public static void syslog(String s) {
		synchronized (objLockSyslog) {
			if (sysLog == null || LoggerConfig.getMapValue(Constants.SYSLOG)) {
				LoggerRes res = new LoggerRes();
				res.setHostIP(LoggerConfig.getHostIP());
				res.setFacility(LoggerConfig.getFacility());
				res.setLoggerName("syslog");
				try {
					sysLog = new SyslogLogger(res);
					LoggerConfig.setMapValue(Constants.SYSLOG, true);
				} catch (LoggerException e) {
					e.printStackTrace();
				}
			}
		}

		sysLog.Log(s);
	}

	/**
	 * 记录syslog日志,带异常堆栈
	 * 
	 * @param log
	 * @param tr
	 */
	public static void syslog(String log, Throwable tr) {
		if (tr != null) {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bao);
			tr.printStackTrace(out);
			syslog(log + "\r\n" + bao.toString());
		} else
			syslog(log);
	}

	/**
	 * 记录访问日志 包含 数据库
	 * 
	 * @param accesslog
	 * @throws Exception
	 */

	public void accesslog(AccessLog log) throws Exception {
		if (log == null)
			return;

		accesslog(log.toString());
		// to DB
		if (LoggerConfig.toDBPriv("access")) {
			accessLogDao.insertAccessLog(log);

		}
	}

	/**
	 * 记录访问日志
	 * 
	 * @param s
	 */
	private static Object objLockAccess = new Object();

	public static void accesslog(String s) {
		if (LoggerConfig.toFilePriv("access")) {
			synchronized (objLockAccess) {
				if (accessLog == null || LoggerConfig.getMapValue(Constants.ACCESS)) {
					LoggerRes res = new LoggerRes();
					res.setLogFile(LoggerConfig.getLogPath() + "access.log");
					res.setLoggerName("access");
					res.setMaxFileSize(String.valueOf(LoggerConfig.getFileSize()));
					res.setMaxBackupIndex(LoggerConfig.getFileNumber());
					try {
						accessLog = new AccessLogger(res);
						LoggerConfig.setMapValue(Constants.ACCESS, true);
					} catch (LoggerException e) {
						e.printStackTrace();
					}
				}
			}
			accessLog.Log(s);
		}
		// 发送syslog
		if (LoggerConfig.toSyslogPriv("access"))
			syslog("ACCESS:" + s);
	}

	/**
	 * 记录管理日志 包含 数据库
	 * 
	 * @param manageLog
	 * @throws Exception
	 */
	public void managelog(ManageLog log) throws Exception {
		if (log == null)
			return;
		managelog(log.toString());
		// to DB
		if (LoggerConfig.toDBPriv("manage")) {
			log.setErrMsg(Constants.DEFAULT_STRING);
			log.setOpType(ManageLoggerMsgUtil.getManageLoggerMsg(log.getOpType()));
			manageLogDao.insertManageLog(log);
		}
	}

	private static Object objLockManage = new Object();

	/**
	 * 记录管理日志
	 * 
	 * @param s
	 */
	public static void managelog(String s) {
		if (LoggerConfig.toFilePriv("manage")) {
			synchronized (objLockManage) {
				if (manageLog == null || LoggerConfig.getMapValue(Constants.MANAGE)) {
					LoggerRes res = new LoggerRes();
					res.setLogFile(LoggerConfig.getLogPath() + "manage.log");
					res.setLoggerName("manage");
					res.setMaxFileSize(String.valueOf(LoggerConfig.getFileSize()));
					res.setMaxBackupIndex(LoggerConfig.getFileNumber());
					try {
						manageLog = new ManageLogger(res);
						LoggerConfig.setMapValue(Constants.MANAGE, true);
					} catch (LoggerException e) {
						e.printStackTrace();
					}
				}
			}

			manageLog.Log(s);
		}
		// 发送syslog
		if (LoggerConfig.toSyslogPriv("manage"))
			syslog("MANAGE: " + s);
	}
}
