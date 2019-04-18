package cn.com.infosec.netsigninterface;

import java.io.InputStream;
import java.security.InvalidParameterException;

import cn.com.infosec.netsigninterface.exceptions.ServerKeyStoreException;
import cn.com.infosec.netsigninterface.resource.NetSignRes;
import cn.com.infosec.netsigninterface.util.PKCS12KeyStore;

/**
 * ServerKeyStore工厂类 用于产生Server端签名和制作数字信封的KeyStore
 * 
 * @author lixiangfeng
 * @version 1.0
 * @stereotype factory
 */
public class ServerKeyStoreFactory {
	/**
	 * 生成一个PKCS12ServerKeyStore对象
	 * 
	 * @param PKCS12Store
	 *          包含有PKCS12ServerKeyStore对象的输入流
	 * @param password
	 *          私钥口令
	 * @return 生成的PKCS12ServerKeyStore对象
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