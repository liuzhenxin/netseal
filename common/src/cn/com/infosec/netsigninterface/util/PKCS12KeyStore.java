package cn.com.infosec.netsigninterface.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netsigninterface.ServerKeyStore;
import cn.com.infosec.netsigninterface.exceptions.ServerKeyStoreException;
import cn.com.infosec.netsigninterface.resource.NetSignRes;

/**
 * <p>
 * Title: PKCS12KeyStore
 * </p>
 * <p>
 * Description: 用来对p12z证书格式进行读取
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:infosec
 * </p>
 * 
 * @author huangyong
 * @version 1.0
 */

public class PKCS12KeyStore implements ServerKeyStore {

	private KeyStore store;

	private String sKeyEntry;

	private String sCertEntry;
	
	private X509Certificate signCert ;
	
	private PrivateKey prik ;
	
	private String dn ;
	
	private X509Certificate[] certChain ;
	
	static {
		Security.addProvider( new InfosecProvider() );
	}

	/**
	 * 构造PKCS12KeyStore
	 * 
	 * @param is
	 *          包含了keystore的输入流
	 * @param password
	 *          口令
	 * @throws ServerKeyStoreException
	 *           产生keystore错误则抛出此异常
	 */
	public PKCS12KeyStore( InputStream is , char[] password) throws ServerKeyStoreException {
		try {

			store = KeyStore.getInstance( "PKCS12" , "INFOSEC" );
			store.load( is , password );

			Enumeration en = store.aliases();
			String pKeyName = null;
			String pCertName = null;
			while ( en.hasMoreElements() ) {
				String n = ( String ) en.nextElement();
				if( store.isKeyEntry( n ) ) {
					pKeyName = n;
					// pCertName = n;
				}
				if( store.isCertificateEntry( n ) ) {
					pCertName = n;
				}
			}
			if( pKeyName == null && pCertName == null )
				throw new ServerKeyStoreException( "PKCS12 Key Entry Not found" );
			sCertEntry = pCertName;
			sKeyEntry = pKeyName;
			
			initCertDN() ;
			initCertChain() ;
			initPrivateKey() ;
		} catch ( IOException ex ) {
			throw new ServerKeyStoreException( ex.getMessage() );
		} catch ( NoSuchAlgorithmException ex ) {
			throw new ServerKeyStoreException( ex.getMessage() );
		} catch ( CertificateException ex ) {
			throw new ServerKeyStoreException( ex.getMessage() );
		} catch ( NoSuchProviderException ex ) {
			throw new ServerKeyStoreException( ex.getMessage() );
		} catch ( KeyStoreException ex ) {
			throw new ServerKeyStoreException( ex.getMessage() );
		}
	}

	/**
	 * 得到证书DN
	 * 
	 * @return
	 * @throws ServerKeyStoreException
	 */
	private void initCertDN() throws ServerKeyStoreException {
		dn = null;
		try {
			signCert = ( X509Certificate ) store.getCertificate( sKeyEntry );
			if( signCert == null )
				signCert = ( X509Certificate ) store.getCertificate( sCertEntry );
		} catch ( KeyStoreException ex ) {
			throw new ServerKeyStoreException( ex.getMessage() );
		}
		if( signCert == null )
			throw new ServerKeyStoreException( "在ServerKeystore中不能找到证书" );
		dn = signCert.getSubjectDN().toString();
	}

	/**
	 * 取得证书链
	 * 
	 * @return 证书链
	 * @throws ServerKeyStoreException
	 *           在取证书链过程中出现错误，则抛出此异常
	 */
	private void initCertChain() throws ServerKeyStoreException {
		java.security.cert.Certificate[] certs1 = null;
		try {
			certs1 = store.getCertificateChain( sKeyEntry );
			if( certs1 == null )
				certs1 = store.getCertificateChain( sCertEntry );
		} catch ( Exception ex ) {
			ConsoleLogger.logException( ex );
			throw new ServerKeyStoreException( ex.getMessage() );
		}
		if( certs1 == null )
			throw new ServerKeyStoreException( NetSignRes.CERTIFICATE_CHAIN_NOT_FOUND );
		certChain = new X509Certificate[ certs1.length ];
		for( int i = 0 ; i < certs1.length ; i++ )
			certChain[ i ] = ( X509Certificate ) certs1[ i ];
	}

	/**
	 * 取得私钥
	 * 
	 * @return keystore 中保存的私钥
	 * @throws ServerKeyStoreException
	 *           在取私钥过程中出现错误则抛出此异常
	 */
	private void initPrivateKey() throws ServerKeyStoreException {
		try {
			prik = ( PrivateKey ) ( store.getKey( sKeyEntry , "".toCharArray() ) );
			if( prik == null )
				throw new ServerKeyStoreException( NetSignRes.PRIVATEKEY_NOT_FOUND );
		} catch ( UnrecoverableKeyException ex ) {
			ConsoleLogger.logException( ex );
			throw new ServerKeyStoreException( ex.getMessage() );
		} catch ( NoSuchAlgorithmException ex ) {
			ConsoleLogger.logException( ex );
			throw new ServerKeyStoreException( ex.getMessage() );
		} catch ( KeyStoreException ex ) {
			ConsoleLogger.logException( ex );
			throw new ServerKeyStoreException( ex.getMessage() );
		}
	}

	public static void main( String[] args ) throws Exception {
		Security.addProvider( new InfosecProvider() );
		KeyStore keyStore = KeyStore.getInstance( "PKCS12" , "INFOSEC" );
		keyStore.load( new FileInputStream( "c:\\shsign.pfx" ) , "12345678".toCharArray() );
	}

	public String getCertDN() throws ServerKeyStoreException {
		return dn ;
	}

	public X509Certificate[] getCertChain() throws ServerKeyStoreException {
		return certChain ;
	}

	public PrivateKey getPrivateKey() throws ServerKeyStoreException {
		return prik ;
	}

	public X509Certificate getSignCert() {
		return signCert ;
	}
}