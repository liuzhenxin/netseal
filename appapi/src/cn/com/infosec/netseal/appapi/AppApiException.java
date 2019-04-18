package cn.com.infosec.netseal.appapi;

/**
 * 验证API端发生的异常，包括连接不上验证服务器、网络故障等
 */
public class AppApiException extends Exception {

	private int errCode;
	private String errMsg;

	/**
	 * 构造函数
	 */
	AppApiException() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param msg
	 *            错误信息
	 */
	AppApiException(String errMsg) {
		super(errMsg);
	}

	/**
	 * 构造函数
	 * 
	 * @param err
	 *            错误编号
	 * @param msg
	 *            错误信息
	 */
	public AppApiException(int errCode, String errMsg) {
		super(errMsg);
		this.errCode = errCode;
	}

	/**
	 * 返回错误编号
	 * 
	 * @return 错误编号
	 */
	public int getErrCode() {
		return errCode;
	}

	/**
	 * 返回错误信息
	 * 
	 * @return 错误信息
	 */
	public String getErrMsg() {
		return errMsg;
	}
}
