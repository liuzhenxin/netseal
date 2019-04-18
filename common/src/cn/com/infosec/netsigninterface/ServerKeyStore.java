package cn.com.infosec.netsigninterface;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import cn.com.infosec.netsigninterface.exceptions.ServerKeyStoreException;

/**
 * <p>
 * Title: Server端签名所使用的 KeyStore 的接口
 * </p>
 * <p>
 * Description: 目前由 PKCS12KeyStore 类实现
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
	 * 得到KeyStore中入口证书的DN
	 * 
	 * @return 证书链
	 * @throws ServerKeyStoreException
	 */
	public String getCertDN() throws ServerKeyStoreException;

	/**
	 * 得到KeyStore中的证书链
	 * 
	 * @return 证书链
	 * @throws ServerKeyStoreException
	 */
	public X509Certificate[] getCertChain() throws ServerKeyStoreException;

	/**
	 * 得到KeyStore中，证书的私钥
	 * 
	 * @return 取出的私钥
	 * @throws ServerKeyStoreException
	 */
	public PrivateKey getPrivateKey() throws ServerKeyStoreException;
	
	public X509Certificate getSignCert() ;
	
}
