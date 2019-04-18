package cn.com.infosec.netseal.common.util.logger;

public class LoggerException extends Exception {

	/**
	 * 
	 */
	public LoggerException() {
		super();
	}

	/**
	 * @param message
	 */
	public LoggerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LoggerException(Throwable cause) {
		super(cause.getMessage());
	}

	public static void main(String[] args) {
	}
}
