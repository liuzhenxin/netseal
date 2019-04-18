package cn.com.infosec.netseal.common.algorithm.sm2;

public class SM2Util {

	public static boolean decrypt(final byte[] privkey, final byte[] ct, byte[] pt) {
		return SM2.decrypt(privkey, ct, pt);
	}

	public static byte[] encrypt(final byte[] pubkey, final byte[] msg) {
		return SM2.encrypt(pubkey, msg);
	}

	public static void genKeyPair(byte[] pubkey, byte[] privkey) {
		new SM2().genKeyPair(pubkey, privkey);
	}
}
