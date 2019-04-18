package cn.com.infosec.netseal.appapi.common.util;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import cn.com.infosec.jce.oscca.OSCCAMessageDigest;
import cn.com.infosec.jce.oscca.SM2;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.appapi.common.define.Constants;
import cn.com.infosec.nscrypto.netsign.crypto.util.AlgorithmUtil;
import cn.com.infosec.nscrypto.netsign.crypto.util.PKCS7EnvelopedData;
import cn.com.infosec.nscrypto.oscca.sm2.SM2PrivateKey;
import cn.com.infosec.nscrypto.oscca.sm2.SM2PublicKey;
import cn.com.infosec.nsderutil.DERSegment;

public class NetSignUtil {

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 鍥藉瘑瑁哥
	 * 
	 * @param plainText
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static byte[] sign(byte[] plainText, SM2PublicKey pubKey, SM2PrivateKey priKey, byte[] id) throws Exception {
		byte[] digest = OSCCAMessageDigest.SM3Digest(id, pubKey.getX(), pubKey.getY(), plainText);
		return SM2.signHash(digest, priKey.getD());
	}

	/**
	 * 鍥藉瘑楠岃８绛�
	 * 
	 * @param plainText
	 * @param signedText
	 * @param id
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static boolean verify(byte[] plainText, byte[] signedText, SM2PublicKey pubKey, byte[] id) throws Exception {
		byte[] digest = OSCCAMessageDigest.SM3Digest(id, pubKey.getX(), pubKey.getY(), plainText);
		signedText = formatSignedMsg(signedText);

		byte[] kbs = new byte[64];
		System.arraycopy(pubKey.getX(), 0, kbs, 0, 32);
		System.arraycopy(pubKey.getY(), 0, kbs, 32, 32);

		return SM2.verifyHash(digest, signedText, kbs);
	}

	/**
	 * 鏍硅瘉楠岀鍚�
	 * 
	 * @param cert
	 * @param rootKey
	 * @param sm2CertID
	 * @throws Exception
	 */
	public static void verifySM2Cert(X509Certificate cert, PublicKey rootKey) throws Exception {
		byte[] tbs = cert.getTBSCertificate();
		byte[] signed = cert.getSignature();
		if (!SignatureUtil.verify(tbs, signed, rootKey, "SM3", Constants.GM_OID.getBytes())) {
			throw new SignatureException("Certificate verify failed");
		}
	}

	private static byte[] formatSignedMsg(byte[] signed) throws Exception {
		if (signed.length == 64)
			return signed;
		while (signed[0] == 0) {
			byte[] tmp = new byte[signed.length - 1];
			System.arraycopy(signed, 1, tmp, 0, tmp.length);
			signed = tmp;
		}
		if (signed[0] != 0x30)
			throw new SignatureException("Bad signature structon");
		byte[] signedf = new byte[64];
		try {
			DERSegment ds = new DERSegment(signed);
			ds = ds.getInnerDERSegment();
			byte[] tmp = ds.nextDERSegment().getInnerData();

			System.arraycopy(tmp, tmp.length - 32, signedf, 0, 32);
			tmp = ds.nextDERSegment().getInnerData();
			System.arraycopy(tmp, tmp.length - 32, signedf, 32, 32);
			return signedf;
		} catch (Exception e) {
			throw new SignatureException(e.toString());
		}
	}

	/**
	 * 楠岃瘉CA绛惧彂璇佷功鍚岀閽ユ槸鍚﹀尮閰�
	 * 
	 * @param pubKey
	 * @param priKey
	 * @return
	 * @throws Exception
	 */
	public static boolean checkSm2KeyPair(SM2PublicKey pubKey, SM2PrivateKey priKey) throws Exception {
		return cn.com.infosec.nscrypto.oscca.sm2.SM2.checkKeyPair(priKey.getEncoded(), pubKey.getX(), pubKey.getY());
	}

	/**
	 * 瑙ｆ瀽璇佷功
	 * 
	 * @param cert
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate getCert(byte[] cert) throws Exception {
		ByteArrayInputStream in = null;
		// 鍒ゆ柇鏄惁涓篸er缂栫爜璇佷功
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
		return (X509Certificate) cf.generateCertificate(in);
	}

	/**
	 * 鐢熸垚鏁板瓧淇″皝
	 * 
	 * @param plain
	 * @param cert
	 * @param symAlg
	 * @return
	 * @throws Exception
	 */
	public static byte[] composeEnvelopeData(byte[] plain, X509Certificate cert) throws Exception {
		PKCS7EnvelopedData p7Env = new PKCS7EnvelopedData();
		byte[] envdata = p7Env.encrypt(plain, cert, OidUtil.getSymAlg(cert.getSigAlgOID()), "INFOSEC", cert.getPublicKey());
		return envdata;
	}

	/**
	 * 瑙ｅ瘑鏁板瓧淇″皝
	 * 
	 * @param envelopeData
	 * @param cert
	 * @param prik
	 * @return
	 * @throws Exception
	 */
	public static byte[] decomposeEnvelopeData(byte[] envelopeData, X509Certificate cert, PrivateKey prik) throws Exception {
		PKCS7EnvelopedData p7Env = new PKCS7EnvelopedData();
		byte[] decData = p7Env.decrypt(envelopeData, cert, prik, "INFOSEC");
		return decData;
	}

	public static void initialize() throws Exception {
		AlgorithmUtil.initialize("soft");
	}

	public static void main(String[] args) throws Exception, Exception {
		initialize();
		// String keyPath = "F:/temp/envelope/key/jks/netseal.jks";
		// String certPath = "F:/temp/envelope/key/jks/netseal.cer";

		String keyPath = "F:/temp/envelope/key/sm2/netseal.pri";
		String certPath = "F:/temp/envelope/key/sm2/netseal.cer";

		byte[] env = composeEnvelopeData("123".getBytes(), CertUtil.parseCert(FileUtil.getFile(certPath)).getX509Cert());
		FileUtil.storeFile("f:/temp/envelope/env.asn1", env);
		System.out.println("success...");
	}

}
