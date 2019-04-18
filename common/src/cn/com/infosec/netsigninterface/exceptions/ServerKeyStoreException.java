package cn.com.infosec.netsigninterface.exceptions;

/**
 * ServerKeyStore错误
 * <p>
 * Title: ServerKeyStoreException
 * </p>
 * <p>
 * Description: 在产生和使用ServerKeyStore过程中发生错误
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Infosec
 * </p>
 * 
 * @author huangyong
 * @version 1.0
 */

public class ServerKeyStoreException extends Exception {
	public ServerKeyStoreException() {
		super();
	}

	public ServerKeyStoreException( String msg) {
		super( msg );
	}

}
