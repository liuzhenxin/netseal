package cn.com.infosec.netsigninterface.exceptions;

/**
 * ֤����Ч
 * <p>
 * Title: InvalidCertificateException
 * </p>
 * <p>
 * Description: �ṩ��֤����󣬰���֤�鱻���Ϻ�֤�鲻����Ч�ڡ�֤�鲻������
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