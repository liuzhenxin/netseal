package cn.com.infosec.netseal.common.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUtil;
import cn.com.infosec.oscca.sm2.SM2PublicKey;

public class HardCryptoImpl implements CryptoInterface {

	HardCryptoImpl() {
	}

	public void init() throws Exception {
	}

	public void free() throws Exception {
	}

	public byte[] encrypt(byte[] key, byte[] plain, int hsmId) throws Exception {
		return FishManUtil.encryptSM4(hsmId, plain);
	}

	public byte[] decrypt(byte[] key, byte[] enc, int hsmId) throws Exception {
		return FishManUtil.decryptSM4(hsmId, enc);
	}

	public byte[] encryptWithPubkey(PublicKey pubkey, byte[] value, int hsmId) throws Exception {
		return FishManUtil.encryptSM2(hsmId, value);
	}

	public byte[] decryptWithPrikey(PrivateKey prikey, byte[] value, int hsmId) throws Exception {
		return FishManUtil.decryptSM2(hsmId, value);
	}

	public byte[] sign(PublicKey pubkey, PrivateKey priKey, byte[] plain, int hsmId, String signAlg, byte[] id) throws Exception {
		SM2PublicKey pubSm2 = new SM2PublicKey(pubkey.getEncoded());
		byte[] hash = FishManUtil.hashSM3(pubSm2.getEncoded4ex(), id, plain);
		return FishManUtil.signSM2(hsmId, hash);
	}

	public boolean verify(PublicKey pubkey, byte[] plain, byte[] signed, int hsmId, String signAlg, byte[] id) throws Exception {
		SM2PublicKey pubSm2 = new SM2PublicKey(pubkey.getEncoded());
		byte[] hash = FishManUtil.hashSM3(pubSm2.getEncoded4ex(), id, plain);
		return FishManUtil.verifySM2(hsmId, hash, signed);
	}

	public void verifyCert(X509Certificate cert, PublicKey rootPubkey, int hsmId, String signAlg) throws Exception {
	}

	public byte[] hash(PublicKey pubkey, byte[] plain, int hsmId, byte[] id) throws Exception {
		return FishManUtil.hashSM3(pubkey.getEncoded(), id, plain);
	}

	public List<byte[]> genKeyPair(int hsmId) throws Exception {
		return FishManUtil.genSM2Keypair(hsmId);
	}

	public byte[] genSecretKey(int hsmId) throws Exception {
		byte[] key = new byte[16];
		FishManUtil.genSM4Key(hsmId, key);
		return key;
	}

	public byte[] genRandom(int len) throws Exception {
		byte[] out = new byte[len];
		FishManUtil.genRandom(len, out);
		return out;
	}

	public byte[] hash(byte[] plain) throws Exception {
		return FishManUtil.hashSM3(plain);
	}

}