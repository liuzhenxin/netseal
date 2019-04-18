package cn.com.infosec.netseal.common.util.logger;

import org.apache.log4j.Level;

public class LoggerLevel {

	public static Level getLevel(boolean result) {
		if (result)
			return Level.INFO;
		else
			return Level.OFF;
	}

}
