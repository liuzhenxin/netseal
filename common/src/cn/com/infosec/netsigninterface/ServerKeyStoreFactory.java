package cn.com.infosec.netsigninterface;

import java.io.InputStream;
import java.security.InvalidParameterException;

import cn.com.infosec.netsigninterface.exceptions.ServerKeyStoreException;
import cn.com.infosec.netsigninterface.resource.NetSignRes;
import cn.com.infosec.netsigninterface.util.PKCS12KeyStore;

/**
 * ServerKeyStore������ ���ڲ���Server��ǩ�������������ŷ��KeyStore
 * 
 * @author lixiangfeng
 * @version 1.0
 * @stereotype factory
 */
public class ServerKeyStoreFactory {
	/**
	 * ����һ��PKCS12ServerKeyStore����
	 * 
	 * @param PKCS12Store
	 *          ������PKCS12ServerKeyStore�����������
	 * @param password
	 *          ˽Կ����
	 * @return ���ɵ�PKCS12ServerKeyStore����
	 * @throws ServerKeyStoreException
	 */
	public static ServerKeyStore generatePKCS12ServerKeyStore( InputStream PKCS12Store , char[] password )
			throws ServerKeyStoreException {
		if( PKCS12Store == null )
			throw new InvalidParameterException( NetSignRes.INPUTSTREAM_OF_PFX_IS_NULL );
		if( password == null )
			throw new InvalidParameterException( NetSignRes.PFX_PRIKEY_PASSWORD_IS_NULL );

		return new PKCS12KeyStore( PKCS12Store , password );
	}
}