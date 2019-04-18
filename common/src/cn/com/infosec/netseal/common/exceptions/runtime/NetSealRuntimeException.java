package cn.com.infosec.netseal.common.exceptions.runtime;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 */

public class NetSealRuntimeException extends RuntimeException {

	private int errNum;
	private String errMsg;

	public NetSealRuntimeException() {
		super();
	}

	public NetSealRuntimeException(String msg) {
		super(msg);
		this.errMsg = msg;
	}

	public NetSealRuntimeException(int errNum, String msg) {
		super(msg);
		this.errNum = errNum;
		this.errMsg = msg;
	}

	public int getErrNum() {
		return errNum;
	}

	public void setErrNum(int errNum) {
		this.errNum = errNum;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getMessage() {
		return errMsg;
	}

}