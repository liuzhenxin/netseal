package cn.com.infosec.netseal.common.util.p10;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import cn.com.infosec.asn1.x509.X509Extensions;
import cn.com.infosec.util.encoders.Base64;

public class CertificateUtil {

	public static X509Certificate generateCertificate(String file) {
		if (file == null)
			return null;
		File f = new File(file);
		if (!f.exists())
			return null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			byte[] bs = new byte[in.available()];
			in.read(bs);
			return generateCertificate(bs);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
	}

	public static X509Certificate generateCertificate(byte[] cert) throws Exception {
		ByteArrayInputStream in = null;
		// 判断是否为der编码证书
		if (cert[0] == 0x30) {
			int tl = ((int) (cert[1] & 0xff)) - 128;
			if (tl > 0) {
				byte[] ltmp = new byte[tl];
				System.arraycopy(cert, 2, ltmp, 0, tl);
				int length = new BigInteger(ltmp).intValue();
				if ((length > 0) && (length == (cert.length - 2 - tl))) {
					in = new ByteArrayInputStream(cert);
				} else
					throw new CertificateException("Illegal length: " + length);
			} else
				throw new CertificateException("Illegal code: 30 " + ((cert[1] & 0xff)));
		} else {
			String head = "-----BEGIN CERTIFICATE-----";
			String tail = "-----END CERTIFICATE-----";
			String b64Cert = new String(cert);
			if (b64Cert.indexOf(head) > -1) {
				b64Cert = b64Cert.replaceFirst(head, "").replaceFirst(tail, "");
			}
			byte[] certTmp = Base64.decode(b64Cert.trim());
			in = new ByteArrayInputStream(certTmp);
		}
		CertificateFactory cf = CertificateFactory.getInstance("X.509FX", "INFOSEC");
		X509Certificate certi = (X509Certificate) cf.generateCertificate(in);
		try {
			certi.getSubjectDN().getName();
		} catch (Exception e) {
			return null;
		}
		return certi;
	}

	public static String getSubjectKid(X509Certificate cert) {
		byte[] subkid = cert.getExtensionValue(X509Extensions.SubjectKeyIdentifier.getId());
		if ((subkid == null) || (subkid.length == 0)) {
			return getSubjectKid(cert.getPublicKey());
		} else {
			byte[] tmp = new byte[subkid.length - 4];
			System.arraycopy(subkid, 4, tmp, 0, tmp.length);
			subkid = tmp;
		}
		String kidStr = "";
		for (int i = 0, length = subkid.length; i < length; i++) {
			String tmp = Integer.toHexString(0xff & subkid[i]);
			kidStr += (tmp.length() == 1) ? ("0" + tmp) : tmp;
		}
		return kidStr;
	}

	public static String getSubjectKid(PublicKey pubk) {
		byte[] subkid = null;
		byte[] keyBs = pubk.getEncoded();
		keyBs = ASN1Util.getValue(keyBs);
		keyBs = ASN1Util.getObject(keyBs, 1);
		keyBs = ASN1Util.getValue(keyBs);
		try {
			MessageDigest dig = MessageDigest.getInstance("SHA1");
			subkid = dig.digest(keyBs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String kidStr = "";
		if (subkid != null) {
			for (int i = 0, length = subkid.length; i < length; i++) {
				String tmp = Integer.toHexString(0xff & subkid[i]);
				kidStr += (tmp.length() == 1) ? ("0" + tmp) : tmp;
			}
		}
		return kidStr;
	}

	// public static String getIssuerKid( X509Certificate cert ) {
	// byte[] issuerkid = cert.getExtensionValue( X509Extensions.AuthorityKeyIdentifier.getId() );
	// if( ( issuerkid == null ) || ( issuerkid.length == 0 ) )
	// return null ;
	// else {
	// issuerkid = getInnerValue( issuerkid );
	// if( ( issuerkid[ 0 ] & 0xff ) == 0x80 ) {
	// issuerkid = getInnerValue( issuerkid );
	// byte[] tmpkid = new byte[ issuerkid.length + 1 ] ;
	// System.arraycopy( issuerkid , 0 , tmpkid , 1 , issuerkid.length );
	// return new BigInteger( tmpkid ).toString( 16 ) ;
	// } else
	// return null ;
	// }
	// }
	//
	// private static byte[] getInnerValue( byte[] bs ) {
	// byte b = bs[ 1 ];
	// byte[] inner = null;
	// int start = 2;
	// int bint = b & 0xff;
	// if( bint < 128 ) {
	// inner = new byte[ bint ];
	// } else {
	// int l = bint - 128;
	// start += l;
	// byte[] bsLength = new byte[ l ];
	// System.arraycopy( bs , 2 , bsLength , 0 , l );
	// if( bsLength[0]<0 ){
	// byte[] tmp = new byte[ bsLength.length + 1 ];
	// System.arraycopy(bsLength, 0, tmp, 1 , bsLength.length) ;
	// bsLength = tmp ;
	// }
	// BigInteger bigi = new BigInteger( bsLength );
	// int dataLength = bigi.intValue();
	// inner = new byte[ dataLength ];
	// }
	// System.arraycopy( bs , start , inner , 0 , inner.length );
	// return inner;
	// }
	//
	//
	//

	//
	//
	//

	//
	// public static byte[] getPublicKey(byte[] cert) {
	// DERSegment seg = new DERSegment(cert);
	// seg = seg.getInnerDERSegment().getInnerDERSegment();
	// return seg.getDERSegment(6).getEncoded();
	// }
	//
	// public static byte[] generateSM2CertificateByP10( byte[] p10 , byte[] rootPriKey , byte[] baseCert )
	// throws NoSuchAlgorithmException , CryptoException,NoSuchProviderException {
	// byte[] cert = new byte[ baseCert.length ];
	// System.arraycopy( baseCert , 0 , cert , 0 , baseCert.length );
	// DERSegment seg = new DERSegment( p10 );
	// seg = seg.getInnerDERSegment().getInnerDERSegment().getDERSegment( 2 );
	// byte[] pubk = seg.getEncoded();
	// byte[] pubkhead = new byte[]{ 0x30 , 0x52 , 0x30 , 0xc , 0x6 , 0x7 , 0x2a , ( byte ) 0x86 , 0x48 , ( byte ) 0xce ,
	// 0x3d , 0x2 , 0x1 , 0x2 , 0x1 , 0x7 , 0x3 , 0x42 , 0x0 , 4 };
	// int pubkIndex = ByteArray.find( cert , pubkhead );
	// System.arraycopy( pubk , 0 , cert , pubkIndex , 84 );
	// seg = new DERSegment( cert );
	// seg = seg.getInnerDERSegment().getDERSegment( 0 );
	// byte[] tbs = seg.getEncoded();
	// byte[] signed = SDFJNI.SM2SignWithExternalKey( tbs , "SM3" , rootPriKey , null , null );
	// System.arraycopy( signed , 0 , cert , cert.length-64 , 64 ) ;
	// return cert ;
	// }
	//
	// public static String trimDN( String dn ) {
	// String[] temp = dn.split( "," );
	// StringBuffer buffer = new StringBuffer();
	//
	// for( int i = 0 ; i < temp.length ; i++ ) {
	// buffer.append( temp[ i ].trim() + "," );
	// }
	// String value = buffer.toString();
	// return value.substring( 0 , value.length() - 1 );
	// }
	//
	// public static String turnDN( String DN ) {
	// String[] temp = DN.split( "," );
	// String turnDN = temp[ temp.length - 1 ].trim();
	//
	// for( int i = ( temp.length - 2 ) ; i >= 0 ; i-- ) {
	// turnDN = turnDN + "," + temp[ i ].trim();
	// }
	// return turnDN;
	// }
	//
	// public static void main( String[] args ) throws Exception {
	// Security.addProvider( new cn.com.infosec.jce.provider.InfosecProvider() ) ;
	// FileInputStream in = new FileInputStream( "D:\\WORK\\myjava\\infosec\\certs\\cert\\rootcert\\ca_root_sm2_id.cer" );
	// byte[] bs = new byte[ in.available() ];
	// in.read( bs );
	// X509Certificate cert = generateCertificate( bs ) ;
	// String kid = getSubjectKid( cert.getPublicKey() ) ;
	// System.out.println( "kid:" + kid ) ;
	// }

}
