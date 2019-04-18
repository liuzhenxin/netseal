package cn.com.infosec.netseal.common.crypto;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;

import cn.com.infosec.jce.oscca.OSCCAMessageDigest;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.jce.provider.JCESM2PublicKey;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.Base64;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.oscca.sm2.SM2PublicKey;

public class CryptoHandler {
	// 存储不同的加密方式
	private static Hashtable<String, CryptoInterface> ht = new Hashtable<String, CryptoInterface>();

	static {
		// 指定JCE实现提供者 InfoSec
		java.security.Security.addProvider(new InfosecProvider());

		ht.put(Constants.SOFT_MODE, new SoftCryptoImpl());
		ht.put(Constants.HARD_MODE, new HardCryptoImpl());
	}

	private CryptoHandler() {
	}

	private static CryptoInterface getCryptoImpl(String keyMode) {
		if (StringUtil.isNotBlank(keyMode)) {
			if (keyMode.equalsIgnoreCase(Constants.JKS_SUFFIX) || keyMode.equalsIgnoreCase(Constants.PFX_SUFFIX) || keyMode.equalsIgnoreCase(Constants.PRI_SUFFIX))
				return ht.get(Constants.SOFT_MODE);
			else if (keyMode.equalsIgnoreCase(Constants.KEY_SUFFIX))
				return ht.get(Constants.HARD_MODE);
			else
				throw new NetSealRuntimeException("key mode unknown, mode is: " + keyMode);
		} else
			throw new NetSealRuntimeException("key mode unknown, mode is: " + keyMode);
	}

	/**
	 * 硬件实现的初试化环境
	 * 
	 */
	public static void init(String keyMode) throws Exception {
		getCryptoImpl(keyMode).init();
	}

	/**
	 * 硬件实现的释放环境
	 * 
	 * @throws Exception
	 */
	public static void free(String keyMode) throws Exception {
		getCryptoImpl(keyMode).free();
	}

	/**
	 * 使用对称密钥算法加密数据
	 * 
	 * @param key
	 *            对称算法密钥
	 * @param plaindata
	 *            明文
	 * @return 密文
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] key, byte[] plaindata, String keyMode, int hsmId) throws Exception {
		return getCryptoImpl(keyMode).encrypt(key, plaindata, hsmId);
	}

	/**
	 * 使用对称密钥算法对密文解密
	 * 
	 * @param key
	 *            对称算法密钥
	 * @param encdata
	 *            密文
	 * @return 明文
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] key, byte[] encdata, String keyMode, int hsmId) throws Exception {
		return getCryptoImpl(keyMode).decrypt(key, encdata, hsmId);
	}

	/**
	 * 应用公钥加密
	 * 
	 * @param pubkey
	 *            RSA公钥
	 * @param value
	 *            被加密的数据
	 * @return 密文
	 * @throws Exception
	 */

	public static byte[] encryptWithPubkey(PublicKey pubKey, byte[] value, String keyMode, int hsmId) throws Exception {
		return getCryptoImpl(keyMode).encryptWithPubkey(pubKey, value, hsmId);
	}

	/**
	 * 使用私钥解密
	 * 
	 * @param priKey
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptWithPriKey(PrivateKey priKey, byte[] value, String keyMode, int hsmId) throws Exception {
		return getCryptoImpl(keyMode).decryptWithPrikey(priKey, value, hsmId);
	}

	/**
	 * 对明文进行签名操作
	 * 
	 * @param priKey
	 *            签名的私钥(PrivateKey bytearray )
	 * @param plain
	 *            明文
	 * @return 签名密文
	 * @throws Exception
	 */
	public static byte[] sign(PublicKey pubKey, PrivateKey priKey, byte[] plain, String keyMode, int hsmId, String signAlg, byte[] id) throws Exception {
		return getCryptoImpl(keyMode).sign(pubKey, priKey, plain, hsmId, signAlg, id);
	}

	/**
	 * 对签名进行验证
	 * 
	 * @param pubkey
	 *            验证签名的公钥(PublicKey bytearray )
	 * @param plain
	 *            明文
	 * @param signed
	 *            签名密文
	 * @return 验证是否通过
	 * @throws Exception
	 */
	public static boolean verify(PublicKey pubKey, byte[] plain, byte[] signed, String keyMode, int hsmId, String signAlg, byte[] id) throws Exception {
		return getCryptoImpl(keyMode).verify(pubKey, plain, signed, hsmId, signAlg, id);
	}

	/**
	 * 根证验证书签名
	 * 
	 * @param cert
	 * @param rootPubkey
	 * @return
	 * @throws Exception
	 */
	public static void verifyCert(X509Certificate cert, PublicKey rootPubKey, String keyMode, int hsmId, String signAlg) throws Exception {
		getCryptoImpl(keyMode).verifyCert(cert, rootPubKey, hsmId, signAlg);
	}

	/**
	 * 对明文进行摘要算法操作
	 * 
	 * @param plain
	 *            明文
	 * @return 摘要密文
	 * @throws Exception
	 */
	public static byte[] hash(byte[] plain, String keyMode) throws Exception {
		return getCryptoImpl(keyMode).hash(plain);
	}

	/**
	 * 产生非对称密钥对
	 * 
	 * @param length
	 *            密钥长度
	 * @return byte[0]为公钥，byte[1]为私钥
	 * @throws Exception
	 */
	public static List<byte[]> genKeyPair(String keyMode, int hsmId) throws Exception {
		return getCryptoImpl(keyMode).genKeyPair(hsmId);
	}

	/**
	 * 产生对称密钥
	 * 
	 * @param length
	 *            密钥长度
	 * @return 对称密钥
	 * @throws Exception
	 */
	public static byte[] genSecretKey(String keyMode, int hsmId) throws Exception {
		return getCryptoImpl(keyMode).genSecretKey(hsmId);
	}

	/**
	 * 产生一个指定长度的随机数
	 * 
	 * @param len
	 *            指定的长度
	 * @return 随机数
	 */
	public static byte[] genRandom(int len, String keyMode) throws Exception {
		return getCryptoImpl(keyMode).genRandom(len);
	}

	/**
	 * 转BASE64字符串
	 * 
	 * @param plain
	 * @return
	 */
	public static String hashEnc64(String plain, String keyMode) {
		String ret = "";
		try {
			ret = Base64.encode(getCryptoImpl(keyMode).hash(plain.getBytes(Constants.UTF_8)));
		} catch (Exception e) {
			LoggerUtil.errorlog("crypto handler hash and encode base64 error", e);
			throw new NetSealRuntimeException(e.getMessage());
		}
		return ret;
	}

	/**
	 * 转BASE64字符串
	 * 
	 * @param plain
	 * @return
	 */
	public static String hashEnc64(byte[] plain, String keyMode) {
		String ret = "";
		try {
			ret = Base64.encode(getCryptoImpl(keyMode).hash(plain));
		} catch (Exception e) {
			LoggerUtil.errorlog("crypto handler hash and encode base64 error", e);
			throw new NetSealRuntimeException(e.getMessage());
		}
		return ret;
	}

	/**
	 * 对明文进行摘要算法操作
	 * 
	 * @param plain
	 *            明文
	 * @return 摘要密文
	 * @throws Exception
	 */
	public static byte[] hash(byte[] plain) throws Exception {
		return hash(plain, null, "SM3", null);
	}

	/**
	 * 对明文进行摘要算法操作
	 * 
	 * @param plain
	 *            明文
	 * @return 摘要密文
	 * @throws Exception
	 */
	public static byte[] hash(byte[] plain, PublicKey pubKey, String hashAlg, byte[] id) throws Exception {
		switch (hashAlg) {
		case "SHA1":
			MessageDigest md = MessageDigest.getInstance("SHA1", "INFOSEC");
			md.update(plain);
			return md.digest();
		case "SHA256":
			MessageDigest md256 = MessageDigest.getInstance("SHA256", "INFOSEC");
			md256.update(plain);
			return md256.digest();
		case "SM3":
			byte[] x = null, y = null;
			if (id != null) {
				if (pubKey instanceof SM2PublicKey) {
					x = ((SM2PublicKey) pubKey).getX();
					y = ((SM2PublicKey) pubKey).getY();
				} else if (pubKey instanceof JCESM2PublicKey) {
					x = new SM2PublicKey(pubKey.getEncoded()).getX();
					y = new SM2PublicKey(pubKey.getEncoded()).getY();
				} else
					throw new Exception("translate sm2 public key error");
			}
			return OSCCAMessageDigest.SM3Digest(id, x, y, plain);
		default:
			throw new Exception("unknown hash alg: " + hashAlg);
		}

	}

	/**
	 * 转BASE64字符串
	 * 
	 * @param plain
	 * @return
	 */
	public static String hashEnc64(String plain) {
		String ret = "";
		try {
			ret = Base64.encode(hash(plain.getBytes(Constants.UTF_8)));
		} catch (Exception e) {
			LoggerUtil.errorlog("crypto handler hash and encode base64 error", e);
			throw new NetSealRuntimeException(e.getMessage());
		}
		return ret;
	}

	/**
	 * 转BASE64字符串
	 * 
	 * @param plain
	 * @return
	 */
	public static String hashEnc64(byte[] plain) {
		String ret = "";
		try {
			ret = Base64.encode(hash(plain));
		} catch (Exception e) {
			LoggerUtil.errorlog("crypto handler hash and encode base64 error", e);
			throw new NetSealRuntimeException(e.getMessage());
		}
		return ret;
	}

}