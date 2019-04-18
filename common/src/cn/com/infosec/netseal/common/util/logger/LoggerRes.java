package cn.com.infosec.netseal.common.util.logger;

public class LoggerRes {

	private boolean UseRemoteLogger = false;

	private String LogFile = "log/acces.log";

	private int MaxBackupIndex = 10;

	private String MaxFileSize = "10MB";
	private String RemoteLoggerIP = null;
	private int RemoteLoggerPort = 0;
	private int reconnectTime = 0;
	private String Header = null;
	private String LoggerName = null;

	private String hostIP = "";
	private String facility = "local0";

	public void setLoggerName(String _LoggerName) {
		LoggerName = _LoggerName;
	}

	public String getLoggerName() {
		return LoggerName;

	}

	public void setUseRemoteLogger() {
		UseRemoteLogger = true;
	}

	public void setHeader(String _header) {
		Header = _header;
	}

	String getHeader() {
		return Header;
	}

	public boolean IsUseRemoteLogger() {
		return UseRemoteLogger;
	}

	public void setLogFile(String _logFilePath) {
		LogFile = _logFilePath;
	}

	public String getLogFile() {
		return LogFile;
	}

	public void setMaxBackupIndex(int _Index) {
		MaxBackupIndex = _Index;
	}

	int getMaxBackUpIndex() {
		return MaxBackupIndex;
	}

	public void setMaxFileSize(String _MaxFileSize) {
		MaxFileSize = _MaxFileSize;
	}

	String getMaxFileSize() {
		return MaxFileSize + "MB";
	}

	public void setRemoteLoggerIP(String _RemoteLoggerIP) {
		RemoteLoggerIP = _RemoteLoggerIP;
	}

	String getRemoteLoggerIP() {
		return RemoteLoggerIP;
	}

	public void setRemoteLoggerPort(int _Port) {
		RemoteLoggerPort = _Port;
	}

	int getRemoteLoggerPort() {
		return RemoteLoggerPort;
	}

	public void setReconnectTime(int reconnectTime) {
		this.reconnectTime = reconnectTime;
	}

	int getReconnectTime() {
		return reconnectTime;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}
}
