package cn.com.infosec.netseal.common.util.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SyslogAppender;

public class SyslogLogger extends AbstractLogger {

	public SyslogLogger(LoggerRes res) throws LoggerException {
		createDebuglogger(res);
	}

	public void Log(String msg) {
		logger.info(msg);
	}

	private void createDebuglogger(LoggerRes loggerres) throws LoggerException {

		if (loggerres.IsUseRemoteLogger()) {
			throw new LoggerException("Now We Can not Implement The Remote Logger");
		}

		logger = Logger.getLogger(loggerres.getLoggerName());
		logger.removeAllAppenders();

		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss S} [%t] %m%n");

		SyslogAppender da = null;
		try {
			da = new SyslogAppender();
			da.setSyslogHost(loggerres.getHostIP());
			da.setLayout(layout);
			da.setFacility(loggerres.getFacility());
			// da.setThreshold(Priority.DEBUG);
			logger.addAppender(da);
			logger.setLevel(Level.INFO);
		} catch (Exception ex) {
			throw new LoggerException(ex);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("111111111");
		LoggerRes res = new LoggerRes();
		res.setLoggerName("NetSeal_syslog");
		res.setHostIP("10.20.87.55");
		res.setFacility("local7");
		SyslogLogger logger = new SyslogLogger(res);
		logger.Log("NetSeal .................................");
		System.out.println("2222222222");
	}

}
