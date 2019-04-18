package cn.com.infosec.netsigninterface.util;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

import cn.com.infosec.jce.oscca.OID;
import cn.com.infosec.jce.provider.JCESM2PrivateKey;

public class OIDUtil {
	
	public static final String OID_MD2 = "1.2.840.113549.2.2";
	
	public static final String OID_MD5 = "1.2.840.113549.2.5";
	
	public static final String OID_SHA1 = "1.3.14.3.2.26";
	
	public static final String OID_SHA224 = "2.16.840.1.101.3.4.2.4";
	
	public static final String OID_SHA256 = "2.16.840.1.101.3.4.2.1";
	
	public static final String OID_SHA384 = "2.16.840.1.101.3.4.2.2";
	
	public static final String OID_SHA512 = "2.16.840.1.101.3.4.2.3";
	
	public static final String OID_RSA = "1.2.840.113549.1.1.1";
	
	public static final String OID_MD2withRSA = "1.2.840.113549.1.1.2";
	
	public static final String OID_MD5withRSA = "1.2.840.113549.1.1.4";
	
	public static final String OID_SHA1withRSA = "1.2.840.113549.1.1.5";
	
	public static final String OID_SHA256withRSA = "1.2.840.113549.1.1.11";
	
	public static String getSignatureAlgName( String oid ) {
		if( oid.equals( OID_SHA1withRSA ) )
			return "SHA1withRSA";
		if( oid.equals( OID_MD2withRSA ) )
			return "MD2withRSA";
		if( oid.equals( OID_MD5withRSA ) )
			return "MD5withRSA";
		if( oid.equals( OID_SHA256withRSA ) )
			return "SHA256withRSA";
		if( oid.equals( OID.OID_SM3withSM2_N ) )
			return "SM3withSM2";
		if( oid.equals( OID.OID_SHA256withSM2_N ) )
			return "SHA256withSM2" ;
		if( oid.equals( OID.OID_SHA1withSM2_N) )
			return "SHA1withSM2" ;
		if( oid.equals( OID.OID_SHA1withRSA_N ) )
			return "SHA1withRSA";
		if( oid.equals( OID.OID_SHA256withRSA_N ) )
			return "SHA256withRSA";
		if( oid.equals( OID.OID_SM3withRSA_N ) )
			return "SM3withRSA";
		return oid ;
	}
	
	public static String getEncryptAlgName( String oid ) {
		if( oid.equals( OID_RSA ) )
			return "RSA";
		if( ( oid.indexOf( OID.OID_SM2 ) > -1 ) || ( oid.indexOf( OID.OID_SM2_N ) > -1 ) )
			return "SM2";
		if( oid.indexOf( OID.OID_RSA_N ) > -1 )
			return "RSA";
		return oid;
	}
	
	public static String getDigestAlgName( String oid ) {
		if( oid.equals( OID_SHA1 ) )
			return "SHA1";
		if( ( oid.indexOf( OID.OID_SM3 ) > -1 ) || ( oid.indexOf( OID.OID_SM3_N ) > -1 ) )
			return "SM3";
		if( oid.equals( OID_SHA256 ) )
			return "SHA256";
		if( oid.equals( OID_MD5 ) )
			return "MD5";
		if( oid.equals( OID_SHA512 ) )
			return "SHA512";
		if( oid.equals( OID_MD2 ) )
			return "MD2";
		if( oid.equals( OID_SHA224 ) )
			return "SHA224";
		if( oid.equals( OID_SHA384 ) )
			return "SHA384";
		if( oid.indexOf( OID.OID_SHA1_N ) > -1 )
			return "SHA1";
		if( oid.indexOf( OID.OID_SHA256_N ) > -1 )
			return "SHA256";
		return oid;
	}
	
	public static String getEncryptAlgOID( PrivateKey prik ) throws NoSuchAlgorithmException {
		if( prik instanceof RSAPrivateKey )
			return OID_RSA;
		if( prik instanceof JCESM2PrivateKey )
			return OID.OID_SM2_1_N;
		throw new NoSuchAlgorithmException( "Unknown algorithm: " + prik.getClass() );
	}
	
	public static String getDigestAlgOID( String digestAlg ) throws NoSuchAlgorithmException {
		digestAlg = digestAlg.toUpperCase();
		if( digestAlg.equals( "SHA1" ) )
			return OID_SHA1;
		if( digestAlg.equals( "SM3" ) )
			return OID.OID_SM3_N;
		if( digestAlg.equals( "SHA256" ) )
			return OID_SHA256;
		if( digestAlg.equals( "MD5" ) )
			return OID_MD5;
		if( digestAlg.equals( "SHA224" ) )
			return OID_SHA224;
		if( digestAlg.equals( "SHA384" ) )
			return OID_SHA384;
		if( digestAlg.equals( "SHA512" ) )
			return OID_SHA512;
		if( digestAlg.equals( "MD2" ) )
			return OID_MD2;
		throw new NoSuchAlgorithmException( "Unknown algorithm: " + digestAlg );
	}
}
