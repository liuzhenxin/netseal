package cn.com.infosec.netseal.common.exceptions;

import java.io.IOException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 */

public class ProtocolLengthException extends IOException {

	public ProtocolLengthException() {
		super();
	}

	public ProtocolLengthException(String msg) {
		super(msg);
	}

}