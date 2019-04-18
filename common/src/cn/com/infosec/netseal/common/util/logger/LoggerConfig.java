package cn.com.infosec.netseal.common.util.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;

public class LoggerConfig {

	private static String logPath = Constants.LOG_PATH;
	private static List<String> toFileList;
	private static List<String> toDBList;
	private static List<String> toSyslogList;
	private static HashMap<String, Boolean> toRestMap;

	private static int fileSize;
	private static int fileNumber;
	private static String hostIP;
	private static String Facility;

	public synchronized static void init() {
		toFileList = new ArrayList<String>();
		toDBList = new ArrayList<String>();
		toSyslogList = new ArrayList<String>();
		toRestMap = new HashMap<String, Boolean>();

		ConfigUtil config = ConfigUtil.getInstance();
		String toFile = config.getLogToFile();
		String toDB = config.getLogToDB();
		String toSyslog = config.getLogToSyslog();

		String[] toFiles = toFile.split(",");
		for (String string : toFiles)
			toFileList.add(string);

		String[] toDBs = toDB.split(",");
		for (String string : toDBs)
			toDBList.add(string);

		String[] toSyslogs = toSyslog.split(",");
		for (String string : toSyslogs) {
			toSyslogList.add(string);
		}

		fileSize = Integer.parseInt(ConfigUtil.getInstance().getLogFileSize());
		fileNumber = Integer.parseInt(ConfigUtil.getInstance().getLogFileNum());
		hostIP = ConfigUtil.getInstance().getSyslogIp();
		Facility = ConfigUtil.getInstance().getSyslogFacility();

		toRestMap.put(Constants.ACCESS, true);
		toRestMap.put(Constants.MANAGE, true);
		toRestMap.put(Constants.DEBUG, true);
		toRestMap.put(Constants.SYSTEM, true);
		toRestMap.put(Constants.ERROR, true);
		toRestMap.put(Constants.SYSLOG, true);
	}

	public synchronized static void reset() {
		toFileList.clear();
		toDBList.clear();
		toSyslogList.clear();

		ConfigUtil config = ConfigUtil.getInstance();
		String toFile = config.getLogToFile();
		String toDB = config.getLogToDB();
		String toSyslog = config.getLogToSyslog();

		String[] toFiles = toFile.split(",");
		for (String string : toFiles)
			toFileList.add(string);

		String[] toDBs = toDB.split(",");
		for (String string : toDBs)
			toDBList.add(string);

		String[] toSyslogs = toSyslog.split(",");
		for (String string : toSyslogs) {
			toSyslogList.add(string);
		}

		fileSize = Integer.parseInt(ConfigUtil.getInstance().getLogFileSize());
		fileNumber = Integer.parseInt(ConfigUtil.getInstance().getLogFileNum());
		hostIP = ConfigUtil.getInstance().getSyslogIp();
		Facility = ConfigUtil.getInstance().getSyslogFacility();

		toRestMap.put(Constants.ACCESS, true);
		toRestMap.put(Constants.MANAGE, true);
		toRestMap.put(Constants.DEBUG, true);
		toRestMap.put(Constants.SYSTEM, true);
		toRestMap.put(Constants.ERROR, true);
		toRestMap.put(Constants.SYSLOG, true);
	}

	public synchronized static void setMapValue(String key, boolean value) {
		toRestMap.put(key, value);
	}

	public synchronized static boolean getMapValue(String key) {
		return toRestMap.get(key);
	}

	public synchronized static String getLogPath() {
		return logPath;
	}

	public synchronized static int getFileSize() {
		return fileSize;
	}

	public synchronized static int getFileNumber() {
		return fileNumber;
	}

	public synchronized static String getHostIP() {
		return hostIP;
	}

	public synchronized static String getFacility() {
		return Facility;
	}

	public synchronized static boolean toFilePriv(String logMode) {
		return toFileList.contains(logMode);
	}

	public synchronized static boolean toDBPriv(String logMode) {
		return toDBList.contains(logMode);
	}

	public synchronized static boolean toSyslogPriv(String logMode) {
		return toSyslogList.contains(logMode);
	}

}