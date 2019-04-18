package cn.com.infosec.netseal.common.util.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class AccessLogger extends AbstractLogger {

	public AccessLogger(LoggerRes p) throws LoggerException {
		createAccesslogger(p);
	}

	private void createAccesslogger(LoggerRes loggerres) throws LoggerException {

		if (loggerres.IsUseRemoteLogger()) {
			throw new LoggerException("Now We Can not Implement The Remote Logger");
		}

		logger = Logger.getLogger(loggerres.getLoggerName());
		logger.removeAllAppenders();

		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss S} [%t] %m%n");

		RollingFileAppender da = null;
		try {
			da = new RollingFileAppender(layout, loggerres.getLogFile());
			da.setMaxBackupIndex(loggerres.getMaxBackUpIndex());
			da.setMaxFileSize(loggerres.getMaxFileSize());
			logger.addAppender(da);
			logger.setLevel(Level.INFO);
		} catch (Exception ex) {
			throw new LoggerException(ex);
		}
	}

	public void Log(String msg) {
		logger.info(msg);
	}

	public static void main(String[] args) throws Exception {
		LoggerRes res = new LoggerRes();
		res.setLogFile("access6.log");
		res.setLoggerName("Testhy");
		res.setMaxBackupIndex(3);
		res.setMaxFileSize("1kb");
		res.setHeader("#hytest");

		AccessLogger logger = new AccessLogger(res);
		for (int i = 0; i < 1000; i++)
			logger.Log("Sign ok");
	}
}
