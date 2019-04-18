package cn.com.infosec.netseal.common.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.List;

import cn.com.infosec.jce.provider.JCESM2PublicKey;
import cn.com.infosec.netseal.common.algorithm.gm.SM2Impl;
import cn.com.infosec.netseal.common.algorithm.sm3.SM3Util;
import cn.com.infosec.netseal.common.algorithm.sm4.SM4Util;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.NetSignUtil;
import cn.com.infosec.oscca.sm2.SM2PrivateKey;
import cn.com.infosec.oscca.sm2.SM2PublicKey;

public class SoftCryptoImpl implements CryptoInterface {

	SoftCryptoImpl() {
	}

	public void init() throws Exception {
	}

	public void free() throws Exception {
	}

	public byte[] encrypt(byte[] key, byte[] plain, int hsmId) throws Exception {
		return SM4Util.encrypt(key, plain);
	}

	public byte[] decrypt(byte[] key, byte[] enc, int hsmId) throws Exception {
		return SM4Util.decrypt(key, enc);
	}

	public byte[] encryptWithPubkey(PublicKey pubKey, byte[] value, int hsmId) throws Exception {
		return null;
	}

	public byte[] decryptWithPrikey(PrivateKey priKey, byte[] value, int hsmId) throws Exception {
		if (priKey == null)
			throw new Exception("priKey is null");
		
		return new SM2Impl().decrypt(value, new SM2PrivateKey(priKey.getEncoded()).getD());
	}

	public byte[] sign(PublicKey pubKey, PrivateKey priKey, byte[] plain, int hsmId, String signAlg, byte[] id) throws Exception {
		if (pubKey == null)
			throw new Exception("pubKey is null");

		if (priKey == null)
			throw new Exception("priKey is null");

		switch (signAlg) {
		case Constants.SHA1_RSA:
			Signature sig = Signature.getInstance(Constants.SHA1_RSA, "INFOSEC");
			sig.initSign(priKey);
			sig.update(plain);
			return sig.sign();
		case Constants.SHA256_RSA:
			Signature sig256 = Signature.getInstance(Constants.SHA256_RSA, "INFOSEC");
			sig256.initSign(priKey);
			sig256.update(plain);
			return sig256.sign();
		case Constants.SM3_SM2:
			byte[] X = ((JCESM2PublicKey) pubKey).getX();
			byte[] Y = ((JCESM2PublicKey) pubKey).getY();

			SM2PublicKey pub = new SM2PublicKey();
			pub.setX(X);
			pub.setY(Y);

			return NetSignUtil.sign(plain, pub, (SM2PrivateKey) priKey, id);
		default:
			throw new Exception("sign alg unknown, alg is: " + signAlg);
		}

	}

	public boolean verify(PublicKey pubKey, byte[] plain, byte[] signed, int hsmId, String signAlg, byte[] id) throws Exception {
		if (pubKey == null)
			throw new Exception("pubKey is null");

		switch (signAlg) {
		case Constants.SHA1_RSA:
			Signature rsa = Signature.getInstance(Constants.SHA1_RSA, "INFOSEC");
			rsa.initVerify(pubKey);
			rsa.update(plain);
			return rsa.verify(signed);
		case Constants.SHA256_RSA:
			Signature rsa256 = Signature.getInstance(Constants.SHA256_RSA, "INFOSEC");
			rsa256.initVerify(pubKey);
			rsa256.update(plain);
			return rsa256.verify(signed);
		case Constants.SM3_SM2:
			SM2PublicKey pubSm2 = new SM2PublicKey(pubKey.getEncoded());
			return NetSignUtil.verify(plain, signed, pubSm2, id);
		default:
			throw new Exception("sign alg unknown, alg is: " + signAlg);
		}
	}

	public void verifyCert(X509Certificate cert, PublicKey rootPubKey, int hsmId, String signAlg) throws Exception {
		if (cert == null)
			throw new Exception("cert is null");

		if (rootPubKey == null)
			throw new Exception("rootPubKey is null");

		switch (signAlg) {
		case Constants.SHA1_RSA:
			cert.verify(rootPubKey);
			return;
		case Constants.SHA256_RSA:
			cert.verify(rootPubKey);
			return;
		case Constants.SM3_SM2:
			NetSignUtil.verifySM2Cert(cert, rootPubKey);
			return;
		default:
			throw new Exception("sign alg unknown, alg is: " + signAlg);
		}
	}

	public byte[] hash(byte[] plain) throws Exception {
		return SM3Util.hash(plain);
	}

	public List<byte[]> genKeyPair(int hsmId) throws Exception {
		return null;
	}

	public byte[] genSecretKey(int hsmId) throws Exception {
		return null;
	}

	public byte[] genRandom(int len) throws Exception {
		byte[] bs = new byte[len];
		SecureRandom random = new SecureRandom();
		random.nextBytes(bs);
		return bs;
	}

}