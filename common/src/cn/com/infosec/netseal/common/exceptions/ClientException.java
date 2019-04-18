package cn.com.infosec.netseal.common.exceptions;

/**web数据异常
 *
 */
public class ClientException extends Exception{

	private static final long serialVersionUID = -8148956236005836303L;

	public ClientException() {
		super();
	}

	public ClientException(String msg) {
		super(msg);
	}
}
