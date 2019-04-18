package cn.com.infosec.netsigninterface.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.x509.X509Extensions;
import cn.com.infosec.netsigninterface.exceptions.TrustCertException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class TrustConfig {
	public TrustConfig() {
	}

	private X509Certificate rootcert;

	private String rootcertdn;

	private String iscrldp;

	private String crldir;

	private String crldownloadinterval;

	private BigInteger subjectKID;

	public void setrootcert( X509Certificate cert ) throws TrustCertException {
		try {
			rootcert = cert;
			if( rootcert != null ) {
				rootcertdn = rootcert.getSubjectDN().getName();
			}
			// get subject key id
			byte[] subkid = getSubjectKeyId( rootcert );
			if( ( subkid == null ) || ( subkid.length == 0 ) ) {
				byte[] pubkey = getPublicKey( rootcert.getPublicKey() );
				subkid = getKid( pubkey , "sha1" );
			}
			subjectKID = new BigInteger( subkid );
		} catch ( NoSuchProviderException ex ) {
			ConsoleLogger.logException( ex );
			throw new TrustCertException( "Can not found infosec provider" );
		} catch ( Exception ex ) {
			ConsoleLogger.logException( ex );
			throw new TrustCertException( ex.getMessage() );
		}
	}

	public void setrootcert( String _rootcertdir ) throws TrustCertException {
		try {
			FileInputStream fis = new FileInputStream( _rootcertdir );
			CertificateFactory certFact = CertificateFactory.getInstance( "X.509" , "INFOSEC" );
			rootcert = ( X509Certificate ) certFact.generateCertificate( fis );
			fis.close();
			if( rootcert != null ) {
				rootcertdn = rootcert.getSubjectDN().getName();
			}
			// get subject key id
			byte[] subkid = getSubjectKeyId( rootcert );
			if( ( subkid == null ) || ( subkid.length == 0 ) ) {
				byte[] pubkey = getPublicKey( rootcert.getPublicKey() );
				subkid = getKid( pubkey , "sha1" );
			}
			subjectKID = new BigInteger( subkid );
		} catch ( NoSuchProviderException ex ) {
			ConsoleLogger.logException( ex );
			throw new TrustCertException( "Can not found infosec provider" );
		} catch ( Exception ex ) {
			ConsoleLogger.logException( ex );
			throw new TrustCertException( ex.getMessage() );
		}
		// rootcertdn=rootcert.getSubjectDN().getName();
	}

	private byte[] getSubjectKeyId( X509Certificate cert ) {
		byte[] subkid = rootcert.getExtensionValue( X509Extensions.SubjectKeyIdentifier.getId() );
		if( ( subkid == null ) || ( subkid.length == 0 ) )
			return subkid;
		else {
			byte[] tmp = new byte[ subkid.length - 4 ];
			System.arraycopy( subkid , 4 , tmp , 0 , tmp.length );
			return tmp;
		}
	}

	private byte[] getKid( byte[] key , String alg ) throws NoSuchAlgorithmException , NoSuchProviderException {
		MessageDigest d = MessageDigest.getInstance( alg , "INFOSEC" );
		return d.digest( key );
	}

	private byte[] getPublicKey( PublicKey pubKey ) throws IOException {
		byte[] pubkey = null;
		byte[] pubkeyinfo = pubKey.getEncoded();
		ByteArrayInputStream bIn;
		bIn = new ByteArrayInputStream( pubkeyinfo );
		DERInputStream dIn = new DERInputStream( bIn );
		DERConstructedSequence derseq = null;
		try {
			derseq = ( DERConstructedSequence ) dIn.readObject();
		} catch ( IOException ex ) {
			throw ex;
		}
		DERBitString bitpubkey = ( DERBitString ) derseq.getObjectAt( 1 );
		return bitpubkey.getBytes();
	}

	public BigInteger getSubjectKid() {
		return subjectKID;
	}

	public void setcrldownloadinterval( String _crldownloadinterval ) {
		crldownloadinterval = _crldownloadinterval;
	}

	public String getcrldownloadinterval() {
		return crldownloadinterval;
	}

	public void setcrldir( String _crldir ) {
		crldir = _crldir;
	}

	public void setiscrldp( String _crldp ) {
		iscrldp = _crldp;
	}

	public X509Certificate getrootcert() {
		return rootcert;
	}

	public String getrootcertdn() {
		return rootcertdn;
	}

	public String getcrldir() {
		return crldir;
	}

	public String iscrldp() {
		return iscrldp;
	}

}
