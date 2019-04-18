package cn.com.infosec.netseal.common.util.logger;

import org.apache.log4j.Logger;

public abstract class AbstractLogger {
	protected Logger logger = null;

	// private static String logFilterPattern=ConfigUtil.getInstance().getLogFilterPattern();
	/**
	 * 
	 */
	public AbstractLogger() {
		super();
	}

	public abstract void Log(String msg);

	// public boolean filterString(String msg){
	// try{
	// if (!Pattern.matches(logFilterPattern, msg)) {
	// return false;
	// } else {
	// return true;
	// }
	//
	// }catch(Exception e){
	// return false;
	// }
	// }
}
