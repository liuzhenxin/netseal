package cn.com.infosec.netsigninterface;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import cn.com.infosec.jce.oscca.OSCCAMessageDigest;
import cn.com.infosec.jce.oscca.SM2;
import cn.com.infosec.jce.provider.JCESM2PrivateKey;
import cn.com.infosec.jce.provider.JCESM2PublicKey;
import cn.com.infosec.netsign.der.util.DERSegment;

public class SignatureUtil {
	
	public static byte[] sign( byte[] plainText , PrivateKey prik , String digestAlg , byte[] id ,
			X509Certificate signCert ) throws NoSuchAlgorithmException , NoSuchProviderException , InvalidKeyException ,
			SignatureException {
		if( prik instanceof RSAPrivateKey ) {
			Signature sig = Signature.getInstance( digestAlg + "withRSA" , "INFOSEC" );
			sig.initSign( prik );
			sig.update( plainText );
			return sig.sign();
		} else if( prik instanceof JCESM2PrivateKey ) {
			JCESM2PublicKey pubk = ( JCESM2PublicKey ) signCert.getPublicKey();
			byte[] digest = null;
			if( digestAlg.toUpperCase().equals( "SM3" ) ) {
				digest = OSCCAMessageDigest.SM3Digest( id , pubk.getX() , pubk.getY() , plainText );
			} else {
				digest = OSCCAMessageDigest.SHADigest( digestAlg , id , pubk.getX() , pubk.getY() , plainText );
			}
			return SM2.signHash( digest , ( ( JCESM2PrivateKey ) prik ).getD() );
		} else
			throw new InvalidKeyException( "Unsupport private key type: " + prik.getClass() );
	}
	
	public static boolean verify( byte[] plainText , byte[] signedText , PublicKey pubk , String digestAlg , byte[] id ) throws NoSuchAlgorithmException , NoSuchProviderException , InvalidKeyException ,
			SignatureException {
		if( pubk instanceof RSAPublicKey ) {
			Signature sig = Signature.getInstance( digestAlg + "withRSA" , "INFOSEC" );
			sig.initVerify( pubk );
			sig.update( plainText );
			return sig.verify( signedText );
		} else if( pubk instanceof JCESM2PublicKey ) {
			JCESM2PublicKey sm2Pubk = ( JCESM2PublicKey ) pubk ;
			byte[] digest = null;
			if( digestAlg.toUpperCase().equals( "SM3" ) ) {
				digest = OSCCAMessageDigest.SM3Digest( id , sm2Pubk.getX() , sm2Pubk.getY() , plainText );
			} else {
				digest = OSCCAMessageDigest.SHADigest( digestAlg , id , sm2Pubk.getX() , sm2Pubk.getY() , plainText );
			}
			signedText = formatSignedMsg( signedText );
			byte[] kbs = new byte[ 64 ];
			System.arraycopy( sm2Pubk.getX() , 0 , kbs , 0 , 32 );
			System.arraycopy( sm2Pubk.getY() , 0 , kbs , 32 , 32 );
			return SM2.verifyHash( digest , signedText , kbs ) ;
		} else
			throw new InvalidKeyException( "Unsupport public key type: " + pubk.getClass() );
	}
	
	private static byte[] formatSignedMsg( byte[] signed ) throws SignatureException {
		if( signed.length == 64 )
			return signed;
		while ( signed[ 0 ] == 0 ) {
			byte[] tmp = new byte[ signed.length - 1 ];
			System.arraycopy( signed , 1 , tmp , 0 , tmp.length );
			signed = tmp;
		}
		if( signed[ 0 ] != 0x30 )
			throw new SignatureException( "Bad signature structon" );
		byte[] signedf = new byte[ 64 ];
		try {
			DERSegment ds = new DERSegment( signed );
			ds = ds.getInnerDERSegment();
			byte[] tmp = ds.nextDERSegment().getInnerData();
			System.arraycopy( tmp , tmp.length - 32 , signedf , 0 , 32 );
			tmp = ds.nextDERSegment().getInnerData();
			System.arraycopy( tmp , tmp.length - 32 , signedf , 32 , 32 );
			return signedf;
		} catch ( Exception e ) {
			throw new SignatureException( e.toString() );
		}
	}
	
}
