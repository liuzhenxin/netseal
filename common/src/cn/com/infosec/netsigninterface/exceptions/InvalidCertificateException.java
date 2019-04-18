package cn.com.infosec.netsigninterface.exceptions;

/**
 * 证书无效
 * <p>
 * Title: InvalidCertificateException
 * </p>
 * <p>
 * Description: 提供的证书错误，包括证书被作废和证书不在有效期、证书不被信任
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

public class InvalidCertificateException extends Exception {
	public InvalidCertificateException() {
		super();
	}

	public InvalidCertificateException( String msg) {
		super( msg );
	}
}