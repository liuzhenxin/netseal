/*
 * 创建日期 2005-7-13
 *
 *   要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package cn.com.infosec.netseal.common.util.p10;

/**
 * @author hyoffice
 * 
 * 要更改此生成的类型注释的模板，请转至 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.util.Hashtable;

import cn.com.infosec.asn1.ASN1InputStream;
import cn.com.infosec.asn1.ASN1Sequence;
import cn.com.infosec.asn1.ASN1Set;
import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROutputStream;
import cn.com.infosec.asn1.pkcs.CertificationRequest;
import cn.com.infosec.asn1.pkcs.CertificationRequestInfo;
import cn.com.infosec.asn1.x509.AlgorithmIdentifier;
import cn.com.infosec.asn1.x509.SubjectPublicKeyInfo;
import cn.com.infosec.asn1.x509.X509Name;

public class PKCS10CertificationRequest extends CertificationRequest {

	private static Hashtable algorithms = new Hashtable();

	static {
		algorithms.put("MD2WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
		algorithms.put("MD2WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
		algorithms.put("MD5WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
		algorithms.put("MD5WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
		algorithms.put("RSAWITHMD5", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
		algorithms.put("SHA1WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
		algorithms.put("SHA1WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
		algorithms.put("SHA256WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.11"));
		algorithms.put("RSAWITHSHA1", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
		algorithms.put("RIPEMD160WITHRSAENCRYPTION", new DERObjectIdentifier("1.3.36.3.3.1.2"));
		algorithms.put("RIPEMD160WITHRSA", new DERObjectIdentifier("1.3.36.3.3.1.2"));
		algorithms.put("SHA1WITHDSA", new DERObjectIdentifier("1.2.840.10040.4.3"));
		algorithms.put("DSAWITHSHA1", new DERObjectIdentifier("1.2.840.10040.4.3"));
		algorithms.put("SHA1WITHECDSA", new DERObjectIdentifier("1.2.840.10045.4.1"));
		algorithms.put("ECDSAWITHSHA1", new DERObjectIdentifier("1.2.840.10045.4.1"));
	}

	public PKCS10CertificationRequest(X509Name subject, PublicKey key, ASN1Set attributes, PrivateKey signingKey, String provider) throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException, SignatureException {
		this(null, subject, key, attributes, signingKey, provider);
		// this(a)
	}

	/**
	 * create a PKCS10 certfication request using the named provider.
	 */
	public PKCS10CertificationRequest(String signatureAlgorithm, X509Name subject, PublicKey key, ASN1Set attributes, PrivateKey signingKey, String provider) throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException, SignatureException {

		if (signatureAlgorithm == null) {
			if (((RSAPublicKey) key).getModulus().bitLength() <= 1024) {
				signatureAlgorithm = "SHA1WITHRSA";
			} else {
				signatureAlgorithm = "SHA256WITHRSA";
			}
		}

		DERObjectIdentifier sigOID = (DERObjectIdentifier) algorithms.get(signatureAlgorithm.toUpperCase());

		if (sigOID == null) {
			throw new IllegalArgumentException("Unknown signature type requested");
		}

		if (subject == null) {
			throw new IllegalArgumentException("subject must not be null");
		}

		if (key == null) {
			throw new IllegalArgumentException("public key must not be null");
		}

		this.sigAlgId = new AlgorithmIdentifier(sigOID, null);

		byte[] bytes = key.getEncoded();
		ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
		ASN1InputStream dIn = new ASN1InputStream(bIn);

		try {
			this.reqInfo = new CertificationRequestInfo(subject, new SubjectPublicKeyInfo((ASN1Sequence) dIn.readObject()), attributes);
		} catch (IOException e) {
			throw new IllegalArgumentException("can't encode public key");
		}

		Signature sig = null;

		try {
			sig = Signature.getInstance(sigAlgId.getObjectId().getId(), provider);
		} catch (NoSuchAlgorithmException e) {
			sig = Signature.getInstance(signatureAlgorithm, provider);
		}

		sig.initSign(signingKey);

		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			DEROutputStream dOut = new DEROutputStream(bOut);

			dOut.writeObject(reqInfo);

			sig.update(bOut.toByteArray());
		} catch (Exception e) {
			throw new SecurityException("exception encoding TBS cert request - " + e);
		}

		this.sigBits = new DERBitString(sig.sign());
	}

}
