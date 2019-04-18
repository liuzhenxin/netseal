package cn.com.infosec.netseal.common.util;

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

import cn.com.infosec.asn1.ASN1Object;
import cn.com.infosec.asn1.ASN1Sequence;
import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DEREncodable;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERSequence;
import cn.com.infosec.asn1.x509.X509Name;
import cn.com.infosec.jce.oscca.OSCCAMessageDigest;
import cn.com.infosec.jce.oscca.SM2;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.algorithm.gm.SM2Impl;
import cn.com.infosec.netseal.common.algorithm.gm.SM4Impl;
import cn.com.infosec.netseal.common.algorithm.sm3.SM3Util;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netsign.crypto.util.AlgorithmUtil;
import cn.com.infosec.netsign.crypto.util.CryptoUtil;
import cn.com.infosec.netsign.crypto.util.PKCS10CertificationRequest;
import cn.com.infosec.netsign.crypto.util.PKCS7EnvelopedData;
import cn.com.infosec.netsign.der.util.DERSegment;
import cn.com.infosec.netsign.frame.config.ExtendedConfig;
import cn.com.infosec.netsigninterface.SignatureUtil;
import cn.com.infosec.oscca.SDFJNI;
import cn.com.infosec.oscca.sm2.SM2PrivateKey;
import cn.com.infosec.oscca.sm2.SM2PublicKey;

public class NetSignUtil {

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 国密裸签
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
	 * 国密验裸签
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
	 * 根证验签名
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
	 * 产生SM2证书请求
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String genSm2P10(String subject, String keyPath) throws Exception {
		if (FileUtil.checkPath(keyPath + Constants.CSR))
			return new String(FileUtil.getFile(keyPath + Constants.CSR));

		SDFJNI.init();
		// 生成SM2的密钥对
		cn.com.infosec.oscca.sm2.SM2PublicKey pubk = new cn.com.infosec.oscca.sm2.SM2PublicKey();
		cn.com.infosec.oscca.sm2.SM2PrivateKey prik = new cn.com.infosec.oscca.sm2.SM2PrivateKey();
		SDFJNI.generateSM2SignKeyPair(pubk, prik);

		byte[] hash = SM3Util.hash(Constants.KEY_PWD.getBytes());
		byte[] key = new byte[16];
		System.arraycopy(hash, 0, key, 0, key.length);

		// 保存私钥
		KeyStoreUtil.storeSm2Key(prik.getD(), keyPath);

		// 产生证书请求
		PKCS10CertificationRequest p10request = new PKCS10CertificationRequest(ExtendedConfig.getDefaultSM2P10Alg(), new X509Name(subject), pubk, prik, Constants.GM_OID.getBytes());
		String p10 = CryptoUtil.createbase64csr(p10request);
		FileUtil.storeFile(keyPath + Constants.CSR, p10.getBytes());

		return p10;
	}

	/**
	 * 验证CA签发证书同私钥是否匹配
	 * 
	 * @param pubKey
	 * @param priKey
	 * @return
	 * @throws Exception
	 */
	public static boolean checkSm2KeyPair(SM2PublicKey pubKey, SM2PrivateKey priKey) throws Exception {
		return cn.com.infosec.oscca.sm2.SM2.checkKeyPair(priKey.getEncoded(), pubKey.getX(), pubKey.getY());
	}

	/**
	 * 解析密钥对保护数据格式
	 * 
	 * @param envelope
	 */
	public static byte[] parseKeyPairProtectData(byte[] envelope, PrivateKey priKey) throws Exception {
		// 结构拆解
		ASN1Sequence env = (ASN1Sequence) ASN1Object.fromByteArray(envelope);

		// 1. 算法标识
		DEREncodable obj0 = env.getObjectAt(0);
		if (!(obj0 instanceof DERSequence))
			throw new Exception("format is invaild, obj(0) is not sequence");

		DEREncodable obj0_0 = ((DERSequence) obj0).getObjectAt(0);
		if (!(obj0_0 instanceof DERObjectIdentifier))
			throw new Exception("format is invaild, obj(0).(0) is not object identifier");

		// 2. 对称密钥密文
		DEREncodable obj1 = env.getObjectAt(1);
		if (!(obj1 instanceof DERSequence))
			throw new Exception("format is invaild, obj(1) is not sequence");

		DEREncodable obj1_0 = ((DERSequence) obj1).getObjectAt(0);
		if (!(obj1_0 instanceof DERInteger))
			throw new Exception("format is invaild, obj(1).(0) is not integer");

		DEREncodable obj1_1 = ((DERSequence) obj1).getObjectAt(1);
		if (!(obj1_1 instanceof DERInteger))
			throw new Exception("format is invaild, obj(1).(1) is not integer");

		DEREncodable obj1_2 = ((DERSequence) obj1).getObjectAt(2);
		if (!(obj1_2 instanceof DEROctetString))
			throw new Exception("format is invaild, obj(1).(2) is not octect string");

		DEREncodable obj1_3 = ((DERSequence) obj1).getObjectAt(3);
		if (!(obj1_3 instanceof DEROctetString))
			throw new Exception("format is invaild, obj(1).(3) is not octect string");

		// SM2 公钥
		DEREncodable obj2 = env.getObjectAt(2);
		if (!(obj2 instanceof DERBitString))
			throw new Exception("format is invaild, obj(2) is not bit string");

		// SM2私钥密文
		DEREncodable obj3 = env.getObjectAt(3);
		if (!(obj3 instanceof DERBitString))
			throw new Exception("format is invaild, obj(3) is not bit string");

		byte[] priKeyData = new byte[32];
		byte[] sm2Cipher = ((DERSequence) obj1).getDEREncoded();
		byte[] symKey = new SM2Impl().decrypt(sm2Cipher, ((SM2PrivateKey) priKey).getD());
		if (symKey == null)
			throw new Exception("decrypt sym key fail");

		byte[] tempKeyData = new SM4Impl().sm4_ecb_decrypt(((DERBitString) obj3).getBytes(), symKey, false);
		if (tempKeyData.length == 32)
			priKeyData = tempKeyData;
		else if (tempKeyData.length > 32)
			System.arraycopy(tempKeyData, 32, priKeyData, 0, 32);
		else if (tempKeyData.length < 32)
			throw new Exception("Invalid private key data");

		return priKeyData;
	}

	/**
	 * 解析证书
	 * 
	 * @param cert
	 * @return
	 * @throws Exception
	 */
	public static X509Certificate getCert(byte[] cert) throws Exception {
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
		return (X509Certificate) cf.generateCertificate(in);
	}

	/**
	 * 生成数字信封
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
	 * 解密数字信封
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

	public static void main(String[] args) throws Exception {
		initialize();
//		String keyPath = "F:/temp/envelope/key/jks/netseal.jks";
//		String certPath = "F:/temp/envelope/key/jks/netseal.cer";
		
		String keyPath = "F:/temp/envelope/key/sm2/netseal.pri";
		String certPath = "F:/temp/envelope/key/sm2/netseal.cer";

		byte[] env = composeEnvelopeData("123".getBytes(), CertUtil.parseCert(FileUtil.getFile(certPath)).getX509Cert());
		FileUtil.storeFile("f:/temp/envelope/env.asn1", env);
		
		PrivateKey prik = KeyStoreUtil.loadKey("68683556", ".pri", FileUtil.getFile(keyPath));
		byte[] plain = decomposeEnvelopeData(env, null, prik);
		System.out.println(HexUtil.byte2Hex(plain));
	}

}
