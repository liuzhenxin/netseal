package cn.com.infosec.netseal.appapi;

/**
 * 验证服务器发生错误而产生的异常信息
 */
public class AppServerException extends Exception {

	private int errCode;
	private String errMsg;

	/**
	 * 构造函数
	 */
	AppServerException() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param msg
	 *            错误信息
	 */
	AppServerException(String errMsg) {
		super(errMsg);
		this.errMsg = errMsg;
	}

	/**
	 * 构造函数
	 * 
	 * @param err
	 *            错误编号
	 * @param msg
	 *            错误信息
	 */
	AppServerException(int errCode, String errMsg) {
		super(errMsg);
		this.errCode = errCode;
		this.errMsg = errMsg;
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