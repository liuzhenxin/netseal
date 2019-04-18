package cn.com.infosec.netseal.common.util;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Enumeration;

import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERTaggedObject;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.algorithm.sm3.SM3Util;
import cn.com.infosec.netseal.common.algorithm.sm4.SM4Util;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.oscca.sm2.SM2PrivateKey;

public class KeyStoreUtil {

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 获取密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadKey(String keyPwd, String keyMode, byte[] keyData) {
		KeyStore ks = null;
		PrivateKey priKey = null;

		try {
			if (".jks".equals(keyMode)) {
				ks = KeyStore.getInstance("JKS");
				ks.load(new ByteArrayInputStream(keyData), keyPwd.toCharArray());
			} else if (".pfx".equals(keyMode)) {
				ks = KeyStore.getInstance("PKCS12");
				ks.load(new ByteArrayInputStream(keyData), keyPwd.toCharArray());
			} else {
				return loadSm2Key(keyPwd, keyData);
			}

			Enumeration en = ks.aliases();
			String keyAlias = null;
			while (en.hasMoreElements()) {
				String alias = (String) en.nextElement();
				// if (Constants.TO_SEAL_LABEL.equals(alias)) {
				// keyAlias = alias;
				// break;
				// }
				keyAlias = alias;
				break;
			}

			if (keyAlias == null)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_FILE, "no key entry in file");

			priKey = (RSAPrivateKey) ks.getKey(keyAlias, keyPwd.toCharArray());
			if (priKey == null)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_FILE, "no key entry in file");

			return priKey;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.LOAD_KEY_FROM_FILE_ERROR, "load key in file error, " + e.getMessage());
		}
	}

	/**
	 * 获取国密密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	private static SM2PrivateKey loadSm2Key(String keyPwd, byte[] keyData) {
		try {
			byte[] hash = SM3Util.hash(keyPwd.getBytes());
			byte[] key = new byte[16];
			System.arraycopy(hash, 0, key, 0, key.length);

			byte[] priKeyHash = new byte[32];
			System.arraycopy(keyData, 0, priKeyHash, 0, priKeyHash.length);

			byte[] priKeyEnc = new byte[keyData.length - priKeyHash.length];
			System.arraycopy(keyData, keyData.length - priKeyEnc.length, priKeyEnc, 0, priKeyEnc.length);

			// 解密出私钥原文
			byte[] priKeyData = SM4Util.decrypt(key, priKeyEnc);

			if (!Arrays.equals(priKeyHash, SM3Util.hash(priKeyData)))
				throw new Exception("sm2 priKey hash not match");

			SM2PrivateKey priKey = new SM2PrivateKey();
			priKey.setD(priKeyData);

			return priKey;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.LOAD_KEY_FROM_FILE_ERROR, "load sm2 key in file error, " + e.getMessage());
		}
	}

	/**
	 * 加密保存密钥
	 * 
	 * @param priKeyData
	 */
	public static void storeSm2Key(byte[] priKeyData, String keyPath) {
		try {
			byte[] hash = SM3Util.hash(Constants.KEY_PWD.getBytes());
			byte[] key = new byte[16];
			System.arraycopy(hash, 0, key, 0, key.length);

			byte[] enc = SM4Util.encrypt(key, priKeyData);
			byte[] dataHash = SM3Util.hash(priKeyData);

			FileUtil.storeFile(keyPath + Constants.PRI_KEY, dataHash);
			FileUtil.storeFile(keyPath + Constants.PRI_KEY, enc, true);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.STORE_KEY_TO_FILE_ERROR, "store sm2 key to file error, " + e.getMessage());
		}
	}

	/**
	 * 加密卡保存密钥
	 * 
	 * @param priKeyData
	 * @param keyPath
	 */
	public static void storeSm2CardKey(byte[] priKeyData, String keyPath) {
		try {
			byte[] hash = SM3Util.hash(Constants.KEY_PWD.getBytes());
			byte[] key = new byte[16];
			System.arraycopy(hash, 0, key, 0, key.length);

			byte[] enc = SM4Util.encrypt(key, priKeyData);
			byte[] dataHash = SM3Util.hash(priKeyData);

			FileUtil.storeFile(keyPath + Constants.PRI_CARD_KEY, dataHash);
			FileUtil.storeFile(keyPath + Constants.PRI_CARD_KEY, enc, true);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.STORE_KEY_TO_FILE_ERROR, "store sm2 Card key to file error, " + e.getMessage());
		}
	}

	/**
	 * 保存密钥pem格式
	 * 
	 * @param priKeyData
	 */
	public static void storePemSm2Key(byte[] pubKeyData, byte[] priKeyData, String keyPath) {
		try {
			DERConstructedSequence seq = new DERConstructedSequence();
			seq.addObject(new DERInteger(1));
			seq.addObject(new DEROctetString(priKeyData));
			seq.addObject(new DERTaggedObject(true, 0, new DERObjectIdentifier("1.2.156.10197.1.301")));
			seq.addObject(new DERTaggedObject(true, 1, new DERBitString(pubKeyData)));
			byte[] pemData = seq.getDEREncoded();

			StringBuffer pemSB = new StringBuffer("-----BEGIN EC PRIVATE KEY-----\n");
			String pemBase64 = Base64.encode(pemData);
			pemSB.append(pemBase64);
			pemSB.append("\n-----END EC PRIVATE KEY-----");

			FileUtil.storeFile(keyPath + Constants.PEM_KEY, pemSB.toString().getBytes());
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.STORE_KEY_TO_FILE_ERROR, "store sm2 key to file error, " + e.getMessage());
		}
	}

	/**
	 * 获取证书
	 * 
	 * @return
	 */
	public static byte[] getCertFromPfx(String filePwd, String pfxPath) {
		KeyStore ks = null;
		byte[] data = null;

		try {
			data = FileUtil.getFile(pfxPath);
			ks = KeyStore.getInstance("PKCS12");
			ks.load(new ByteArrayInputStream(data), filePwd.toCharArray());

			Enumeration en = ks.aliases();
			String keyAlias = null;
			while (en.hasMoreElements()) {
				String alias = (String) en.nextElement();
				// if (Constants.TO_SEAL_LABEL.equals(alias)) {
				// keyAlias = alias;
				// break;
				// }
				keyAlias = alias;
				break;
			}

			if (keyAlias == null)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_FILE, "no cert entry in jks file");

			X509Certificate x509 = (X509Certificate) ks.getCertificate(keyAlias);
			if (x509 == null)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_FILE, "no cert entry in jks file");

			return x509.getEncoded();
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.LOAD_KEY_FROM_FILE_ERROR, "load rsa key in file error, " + e.getMessage());
		}
	}

}
