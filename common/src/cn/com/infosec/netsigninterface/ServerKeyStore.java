package cn.com.infosec.netsigninterface;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import cn.com.infosec.netsigninterface.exceptions.ServerKeyStoreException;

/**
 * <p>
 * Title: Server��ǩ����ʹ�õ� KeyStore �Ľӿ�
 * </p>
 * <p>
 * Description: Ŀǰ�� PKCS12KeyStore ��ʵ��
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Infosec
 * </p>
 * 
 * @author lixiangfeng
 * @version 1.0
 * @stereotype interface
 */
public interface ServerKeyStore {
	/**
	 * �õ�KeyStore�����֤���DN
	 * 
	 * @return ֤����
	 * @throws ServerKeyStoreException
	 */
	public String getCertDN() throws ServerKeyStoreException;

	/**
	 * �õ�KeyStore�е�֤����
	 * 
	 * @return ֤����
	 * @throws ServerKeyStoreException
	 */
	public X509Certificate[] getCertChain() throws ServerKeyStoreException;

	/**
	 * �õ�KeyStore�У�֤���˽Կ
	 * 
	 * @return ȡ����˽Կ
	 * @throws ServerKeyStoreException
	 */
	public PrivateKey getPrivateKey() throws ServerKeyStoreException;
	
	public X509Certificate getSignCert() ;
	
}
