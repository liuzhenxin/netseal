package cn.com.infosec.netseal.appapi.common.util;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Enumeration;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.appapi.common.algorithm.sm3.SM3Util;
import cn.com.infosec.netseal.appapi.common.algorithm.sm4.SM4Util;
import cn.com.infosec.netseal.appapi.common.define.Constants;
import cn.com.infosec.nscrypto.oscca.sm2.SM2PrivateKey;

public class KeyStoreUtil {

	static {
		Security.addProvider(new InfosecProvider());
	}

	private static String keyPwd = Constants.KEY_PWD;

	/**
	 * 获取密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey loadKey(String keyPwd, String keyPath) throws Exception {
		KeyStore ks = null;
		byte[] data = null;
		PrivateKey priKey = null;

		try {
			String sufName = keyPath.substring(keyPath.lastIndexOf(".", keyPath.length()));
			if (".jks".equals(sufName)) {
				data = FileUtil.getFile(keyPath);
				ks = KeyStore.getInstance("JKS");
				ks.load(new ByteArrayInputStream(data), keyPwd.toCharArray());
			} else if (".pfx".equals(sufName)) {
				data = FileUtil.getFile(keyPath);
				ks = KeyStore.getInstance("PKCS12");
				ks.load(new ByteArrayInputStream(data), keyPwd.toCharArray());
			} else {
				return loadSm2Key(keyPwd, keyPath);
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
				throw new Exception("no key entry in file");

			priKey = (RSAPrivateKey) ks.getKey(keyAlias, keyPwd.toCharArray());
			if (priKey == null)
				throw new Exception("no key entry in file");

			return priKey;
		} catch (Exception e) {
			throw new Exception("load key in file error, " + e.getMessage());
		}
	}

	/**
	 * 加密保存密钥
	 * 
	 * @param priKeyData
	 * @throws Exception
	 */
	public static void storeSm2Key(byte[] priKeyData, String keyPath) throws Exception {
		try {
			byte[] hash = SM3Util.hash(keyPwd.getBytes());
			byte[] key = new byte[16];
			System.arraycopy(hash, 0, key, 0, key.length);

			byte[] enc = SM4Util.encrypt(key, priKeyData);
			byte[] dataHash = SM3Util.hash(priKeyData);

			FileUtil.storeFile(keyPath + Constants.PRI_KEY, dataHash);
			FileUtil.storeFile(keyPath + Constants.PRI_KEY, enc, true);
		} catch (Exception e) {
			throw new Exception("store sm2 key to file error, " + e.getMessage());
		}
	}

	/**
	 * 获取证书
	 * 
	 * @return
	 * @throws Exception
	 */
	public static byte[] getCertFromPfx(String filePwd, String pfxPath) throws Exception {
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
				throw new Exception("no cert entry in jks file");

			X509Certificate x509 = (X509Certificate) ks.getCertificate(keyAlias);
			if (x509 == null)
				throw new Exception("no cert entry in jks file");

			return x509.getEncoded();
		} catch (Exception e) {
			throw new Exception("load rsa key in file error, " + e.getMessage());
		}
	}

	/**
	 * 获取国密密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static SM2PrivateKey loadSm2Key(String keyPwd, String keyPath) throws Exception {
		try {
			byte[] data = FileUtil.getFile(keyPath);

			byte[] hash = SM3Util.hash(keyPwd.getBytes());
			byte[] key = new byte[16];
			System.arraycopy(hash, 0, key, 0, key.length);

			byte[] priKeyHash = new byte[32];
			System.arraycopy(data, 0, priKeyHash, 0, priKeyHash.length);

			byte[] priKeyEnc = new byte[data.length - priKeyHash.length];
			System.arraycopy(data, data.length - priKeyEnc.length, priKeyEnc, 0, priKeyEnc.length);

			// 解密出私钥原文
			byte[] priKeyData = SM4Util.decrypt(key, priKeyEnc);

			if (!Arrays.equals(priKeyHash, SM3Util.hash(priKeyData)))
				throw new Exception("sm2 priKey hash not match");

			SM2PrivateKey priKey = new SM2PrivateKey();
			priKey.setD(priKeyData);

			return priKey;
		} catch (Exception e) {
			throw new Exception("load sm2 key in file error, " + e.getMessage());
		}
	}

}
