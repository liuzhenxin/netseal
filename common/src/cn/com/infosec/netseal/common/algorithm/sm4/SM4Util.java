package cn.com.infosec.netseal.common.algorithm.sm4;

import cn.com.infosec.netseal.common.exceptions.CryptoException;

public class SM4Util {

	public static byte[] encrypt(byte[] key, byte[] in) throws CryptoException {
		if (key == null || key.length != 16)
			throw new CryptoException("key length is not 16");

		byte[] out = new byte[in.length + (in.length % 16 == 0 ? 16 : 16 - (in.length % 16))];
		SM4.sm4_ecb_encrypt(in, in.length, out, key, key.length);

		return out;
	}

	public static byte[] decrypt(byte[] key, byte[] in) throws CryptoException {
		if (key == null || key.length != 16)
			throw new CryptoException("key length is not 16");

		byte[] out = new byte[in.length];
		SM4.sm4_ecb_decrypt(in, in.length, out, out.length, key, key.length);

		out = pkcs5PaddingClean(out);
		return out;
	}

	private static byte[] pkcs5PaddingClean(byte[] in) {
		byte[] cleanBlock = new byte[0];
		try {
			byte last = in[in.length - 1];
			cleanBlock = new byte[in.length - last];
			System.arraycopy(in, 0, cleanBlock, 0, cleanBlock.length);
		} catch (Exception e) {
		}

		return cleanBlock;
	}
}
