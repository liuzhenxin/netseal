package cn.com.infosec.netseal.common.algorithm.gm;

import java.util.Arrays;

public class SM4Impl {
	final static private int fk[] = { 0xA3B1BAC6, 0x56AA3350, 0x677D9197, 0xB27022DC };
	final static private byte[] Sbox = { (byte) 0xd6, (byte) 0x90, (byte) 0xe9, (byte) 0xfe, (byte) 0xcc, (byte) 0xe1, (byte) 0x3d, (byte) 0xb7, (byte) 0x16, (byte) 0xb6, (byte) 0x14, (byte) 0xc2,
			(byte) 0x28, (byte) 0xfb, (byte) 0x2c, (byte) 0x05, (byte) 0x2b, (byte) 0x67, (byte) 0x9a, (byte) 0x76, (byte) 0x2a, (byte) 0xbe, (byte) 0x04, (byte) 0xc3, (byte) 0xaa, (byte) 0x44,
			(byte) 0x13, (byte) 0x26, (byte) 0x49, (byte) 0x86, (byte) 0x06, (byte) 0x99, (byte) 0x9c, (byte) 0x42, (byte) 0x50, (byte) 0xf4, (byte) 0x91, (byte) 0xef, (byte) 0x98, (byte) 0x7a,
			(byte) 0x33, (byte) 0x54, (byte) 0x0b, (byte) 0x43, (byte) 0xed, (byte) 0xcf, (byte) 0xac, (byte) 0x62, (byte) 0xe4, (byte) 0xb3, (byte) 0x1c, (byte) 0xa9, (byte) 0xc9, (byte) 0x08,
			(byte) 0xe8, (byte) 0x95, (byte) 0x80, (byte) 0xdf, (byte) 0x94, (byte) 0xfa, (byte) 0x75, (byte) 0x8f, (byte) 0x3f, (byte) 0xa6, (byte) 0x47, (byte) 0x07, (byte) 0xa7, (byte) 0xfc,
			(byte) 0xf3, (byte) 0x73, (byte) 0x17, (byte) 0xba, (byte) 0x83, (byte) 0x59, (byte) 0x3c, (byte) 0x19, (byte) 0xe6, (byte) 0x85, (byte) 0x4f, (byte) 0xa8, (byte) 0x68, (byte) 0x6b,
			(byte) 0x81, (byte) 0xb2, (byte) 0x71, (byte) 0x64, (byte) 0xda, (byte) 0x8b, (byte) 0xf8, (byte) 0xeb, (byte) 0x0f, (byte) 0x4b, (byte) 0x70, (byte) 0x56, (byte) 0x9d, (byte) 0x35,
			(byte) 0x1e, (byte) 0x24, (byte) 0x0e, (byte) 0x5e, (byte) 0x63, (byte) 0x58, (byte) 0xd1, (byte) 0xa2, (byte) 0x25, (byte) 0x22, (byte) 0x7c, (byte) 0x3b, (byte) 0x01, (byte) 0x21,
			(byte) 0x78, (byte) 0x87, (byte) 0xd4, (byte) 0x00, (byte) 0x46, (byte) 0x57, (byte) 0x9f, (byte) 0xd3, (byte) 0x27, (byte) 0x52, (byte) 0x4c, (byte) 0x36, (byte) 0x02, (byte) 0xe7,
			(byte) 0xa0, (byte) 0xc4, (byte) 0xc8, (byte) 0x9e, (byte) 0xea, (byte) 0xbf, (byte) 0x8a, (byte) 0xd2, (byte) 0x40, (byte) 0xc7, (byte) 0x38, (byte) 0xb5, (byte) 0xa3, (byte) 0xf7,
			(byte) 0xf2, (byte) 0xce, (byte) 0xf9, (byte) 0x61, (byte) 0x15, (byte) 0xa1, (byte) 0xe0, (byte) 0xae, (byte) 0x5d, (byte) 0xa4, (byte) 0x9b, (byte) 0x34, (byte) 0x1a, (byte) 0x55,
			(byte) 0xad, (byte) 0x93, (byte) 0x32, (byte) 0x30, (byte) 0xf5, (byte) 0x8c, (byte) 0xb1, (byte) 0xe3, (byte) 0x1d, (byte) 0xf6, (byte) 0xe2, (byte) 0x2e, (byte) 0x82, (byte) 0x66,
			(byte) 0xca, (byte) 0x60, (byte) 0xc0, (byte) 0x29, (byte) 0x23, (byte) 0xab, (byte) 0x0d, (byte) 0x53, (byte) 0x4e, (byte) 0x6f, (byte) 0xd5, (byte) 0xdb, (byte) 0x37, (byte) 0x45,
			(byte) 0xde, (byte) 0xfd, (byte) 0x8e, (byte) 0x2f, (byte) 0x03, (byte) 0xff, (byte) 0x6a, (byte) 0x72, (byte) 0x6d, (byte) 0x6c, (byte) 0x5b, (byte) 0x51, (byte) 0x8d, (byte) 0x1b,
			(byte) 0xaf, (byte) 0x92, (byte) 0xbb, (byte) 0xdd, (byte) 0xbc, (byte) 0x7f, (byte) 0x11, (byte) 0xd9, (byte) 0x5c, (byte) 0x41, (byte) 0x1f, (byte) 0x10, (byte) 0x5a, (byte) 0xd8,
			(byte) 0x0a, (byte) 0xc1, (byte) 0x31, (byte) 0x88, (byte) 0xa5, (byte) 0xcd, (byte) 0x7b, (byte) 0xbd, (byte) 0x2d, (byte) 0x74, (byte) 0xd0, (byte) 0x12, (byte) 0xb8, (byte) 0xe5,
			(byte) 0xb4, (byte) 0xb0, (byte) 0x89, (byte) 0x69, (byte) 0x97, (byte) 0x4a, (byte) 0x0c, (byte) 0x96, (byte) 0x77, (byte) 0x7e, (byte) 0x65, (byte) 0xb9, (byte) 0xf1, (byte) 0x09,
			(byte) 0xc5, (byte) 0x6e, (byte) 0xc6, (byte) 0x84, (byte) 0x18, (byte) 0xf0, (byte) 0x7d, (byte) 0xec, (byte) 0x3a, (byte) 0xdc, (byte) 0x4d, (byte) 0x20, (byte) 0x79, (byte) 0xee,
			(byte) 0x5f, (byte) 0x3e, (byte) 0xd7, (byte) 0xcb, (byte) 0x39, (byte) 0x48 };
	final static private int SM4ROUND = 32;
	final static private int CK[] = { 0x00070e15, 0x1c232a31, 0x383f464d, 0x545b6269, 0x70777e85, 0x8c939aa1, 0xa8afb6bd, 0xc4cbd2d9, 0xe0e7eef5, 0xfc030a11, 0x181f262d, 0x343b4249, 0x50575e65,
			0x6c737a81, 0x888f969d, 0xa4abb2b9, 0xc0c7ced5, 0xdce3eaf1, 0xf8ff060d, 0x141b2229, 0x30373e45, 0x4c535a61, 0x686f767d, 0x848b9299, 0xa0a7aeb5, 0xbcc3cad1, 0xd8dfe6ed, 0xf4fb0209,
			0x10171e25, 0x2c333a41, 0x484f565d, 0x646b7279 };

	final public static int SM4_BAD_KEY = 2;
	final public static int SM4_SUCCESS = 0;
	final public static int SM4_BLOCK_SIZE = 16;

	private int trans0(int A) {
		return ((Sbox[((A) >>> 24) & 0xFF] << 24) & 0xFF000000) + ((Sbox[((A) >>> 16) & 0xFF] << 16) & 0xFF0000) + ((Sbox[((A) >>> 8) & 0xFF] << 8) & 0xFF00) + (Sbox[(A) & 0xFF] & 0xFF);
	}

	private int ROL(int x, int y) {
		return ((((x) << (y)) | (((x) & 0xFFFFFFFF) >>> (32 - (y))))) & 0xFFFFFFFF;
	}

	private int trans1(int x_param) {
		int temp = 0;

		temp = trans0(x_param);
		return (temp ^ ROL(temp, 2) ^ ROL(temp, 10) ^ ROL(temp, 18) ^ ROL(temp, 24));
	}

	private int trans2(int input) {
		int temp = 0;

		temp = trans0(input);
		return temp ^ ROL(temp, 13) ^ ROL(temp, 23);
	}

	private int ansio2i(byte[] xarray, int offset) {
		return (((((xarray)[4 * (offset)] & 0xFF) << 24) & 0xFF000000) | ((((xarray)[(4 * (offset)) + 1] & 0xFF) << 16) & 0xFF0000) | ((((xarray)[(4 * (offset)) + 2] & 0xFF) << 8) & 0xFF00)
				| (((xarray)[(4 * (offset)) + 3] & 0xFF)) & 0xFF);
	}

	private void sm4_key_exp(byte omk[], int rk[]) {
		int mk[] = new int[4], k[] = new int[4];
		int i = 0;

		mk[0] = ansio2i(omk, 0);
		mk[1] = ansio2i(omk, 1);
		mk[2] = ansio2i(omk, 2);
		mk[3] = ansio2i(omk, 3);

		k[0] = mk[0] ^ fk[0];
		k[1] = mk[1] ^ fk[1];
		k[2] = mk[2] ^ fk[2];
		k[3] = mk[3] ^ fk[3];
		for (i = 0; i < SM4ROUND; i++) {
			k[i & 3] ^= trans2(k[(i + 1) & 3] ^ k[(i + 2) & 3] ^ k[(i + 3) & 3] ^ CK[i]);

			rk[i] = k[i & 3];
			// System.out.println("\tinternal rk[" + i + "]="
			// + String.format("%08x", rk[i]));
		}
	}

	private void sm4_encrypt_rk(byte p_block[], int rk[], byte c_block[]) {
		sm4_encrypt_rk(p_block, 0, rk, c_block, 0);
	}

	private void sm4_encrypt_rk(byte p_block[], int p_offset, int rk[], byte c_block[], int c_offset) {
		int temp_block[] = new int[4];
		int i = 0;

		temp_block[0] = ((p_block[p_offset] << 24) & 0xFF000000) | ((p_block[1 + p_offset] << 16) & 0xFF0000) | ((p_block[2 + p_offset] << 8) & 0xFF00) | ((p_block[3 + p_offset]) & 0xFF);
		temp_block[1] = ((p_block[4 + p_offset] << 24) & 0xFF000000) | ((p_block[5 + p_offset] << 16) & 0xFF0000) | ((p_block[6 + p_offset] << 8) & 0xFF00) | ((p_block[7 + p_offset]) & 0xFF);
		temp_block[2] = ((p_block[8 + p_offset] << 24) & 0xFF000000) | ((p_block[9 + p_offset] << 16) & 0xFF0000) | ((p_block[10 + p_offset] << 8) & 0xFF00) | ((p_block[11 + p_offset]) & 0xFF);
		temp_block[3] = ((p_block[12 + p_offset] << 24) & 0xFF000000) | ((p_block[13 + p_offset] << 16) & 0xFF0000) | ((p_block[14 + p_offset] << 8) & 0xFF00) | ((p_block[15 + p_offset]) & 0xFF);
		for (i = 0; i < SM4ROUND; i++)
			temp_block[i & 3] ^= trans1(temp_block[(i + 1) & 3] ^ temp_block[(i + 2) & 3] ^ temp_block[(i + 3) & 3] ^ rk[i]);
		c_block[c_offset] = (byte) ((temp_block[3] >>> 24) & 0xFF);
		c_block[1 + c_offset] = (byte) ((temp_block[3] >>> 16) & 0xFF);
		c_block[2 + c_offset] = (byte) ((temp_block[3] >>> 8) & 0xFF);
		c_block[3 + c_offset] = (byte) (temp_block[3] & 0xFF);
		c_block[4 + c_offset] = (byte) ((temp_block[2] >>> 24) & 0xFF);
		c_block[5 + c_offset] = (byte) ((temp_block[2] >>> 16) & 0xFF);
		c_block[6 + c_offset] = (byte) ((temp_block[2] >>> 8) & 0xFF);
		c_block[7 + c_offset] = (byte) (temp_block[2] & 0xFF);
		c_block[8 + c_offset] = (byte) ((temp_block[1] >>> 24) & 0xFF);
		c_block[9 + c_offset] = (byte) ((temp_block[1] >>> 16) & 0xFF);
		c_block[10 + c_offset] = (byte) ((temp_block[1] >>> 8) & 0xFF);
		c_block[11 + c_offset] = (byte) (temp_block[1] & 0xFF);
		c_block[12 + c_offset] = (byte) ((temp_block[0] >>> 24) & 0xFF);
		c_block[13 + c_offset] = (byte) ((temp_block[0] >>> 16) & 0xFF);
		c_block[14 + c_offset] = (byte) ((temp_block[0] >>> 8) & 0xFF);
		c_block[15 + c_offset] = (byte) (temp_block[0] & 0xFF);
	}

	private void sm4_decrypt_rk(byte c_block[], int rk[], byte p_block[]) {
		sm4_decrypt_rk(c_block, 0, rk, p_block, 0);
	}

	private void sm4_decrypt_rk(byte c_block[], int c_offset, int rk[], byte p_block[], int p_offset) {
		int temp_block[] = new int[4];
		int i = 0;

		temp_block[0] = ((c_block[c_offset] << 24) & 0xFF000000) | ((c_block[1 + c_offset] << 16) & 0xFF0000) | ((c_block[2 + c_offset] << 8) & 0xFF00) | ((c_block[3 + c_offset]) & 0xFF);
		temp_block[1] = ((c_block[4 + c_offset] << 24) & 0xFF000000) | ((c_block[5 + c_offset] << 16) & 0xFF0000) | ((c_block[6 + c_offset] << 8) & 0xFF00) | ((c_block[7 + c_offset]) & 0xFF);
		temp_block[2] = ((c_block[8 + c_offset] << 24) & 0xFF000000) | ((c_block[9 + c_offset] << 16) & 0xFF0000) | ((c_block[10 + c_offset] << 8) & 0xFF00) | ((c_block[11 + c_offset]) & 0xFF);
		temp_block[3] = ((c_block[12 + c_offset] << 24) & 0xFF000000) | ((c_block[13 + c_offset] << 16) & 0xFF0000) | ((c_block[14 + c_offset] << 8) & 0xFF00) | ((c_block[15 + c_offset]) & 0xFF);
		for (i = 0; i < SM4ROUND; i++)
			temp_block[i & 3] ^= trans1(temp_block[(i + 1) & 3] ^ temp_block[(i + 2) & 3] ^ temp_block[(i + 3) & 3] ^ rk[(31 - i)]);
		p_block[p_offset] = (byte) ((temp_block[3] >>> 24) & 0xFF);
		p_block[1 + p_offset] = (byte) ((temp_block[3] >>> 16) & 0xFF);
		p_block[2 + p_offset] = (byte) ((temp_block[3] >>> 8) & 0xFF);
		p_block[3 + p_offset] = (byte) (temp_block[3] & 0xFF);
		p_block[4 + p_offset] = (byte) ((temp_block[2] >>> 24) & 0xFF);
		p_block[5 + p_offset] = (byte) ((temp_block[2] >>> 16) & 0xFF);
		p_block[6 + p_offset] = (byte) ((temp_block[2] >>> 8) & 0xFF);
		p_block[7 + p_offset] = (byte) (temp_block[2] & 0xFF);
		p_block[8 + p_offset] = (byte) ((temp_block[1] >>> 24) & 0xFF);
		p_block[9 + p_offset] = (byte) ((temp_block[1] >>> 16) & 0xFF);
		p_block[10 + p_offset] = (byte) ((temp_block[1] >>> 8) & 0xFF);
		p_block[11 + p_offset] = (byte) (temp_block[1] & 0xFF);
		p_block[12 + p_offset] = (byte) ((temp_block[0] >>> 24) & 0xFF);
		p_block[13 + p_offset] = (byte) ((temp_block[0] >>> 16) & 0xFF);
		p_block[14 + p_offset] = (byte) ((temp_block[0] >>> 8) & 0xFF);
		p_block[15 + p_offset] = (byte) (temp_block[0] & 0xFF);

	}

	/*
	 * public static int sm4_encrypt(byte p_block[], byte key[], int key_len_bytes, byte c_block[]) { int rk[] = new int[32];
	 * 
	 * if (key_len_bytes != 16) return SM4_BAD_KEY; sm4_key_exp(key, rk); sm4_encrypt_rk(p_block, rk, c_block); return SM4_SUCCESS; }
	 * 
	 * public static int sm4_decrypt(byte c_block[], byte key[], int key_len_bytes, byte p_block[]) { int rk[] = new int[32];
	 * 
	 * if (key_len_bytes != 16) return SM4_BAD_KEY; sm4_key_exp(key, rk); sm4_decrypt_rk(c_block, rk, p_block); return SM4_SUCCESS; }
	 */
	/**
	 * 密钥长度128bits。只接受padding好的原文
	 * 
	 * @param in
	 * @param in_len_bytes
	 * @param key
	 * @param key_len_bytes
	 * @param ivec
	 * @return
	 */
	public byte[] sm4_cbc_encrypt(byte[] in, final byte[] key, final byte ivec[], boolean isNeedPadding) {
		int i, j, numBlocks, pc = 0;
		byte[] tr = new byte[16];
		byte[] iv = new byte[16];
		int rk[] = new int[32];
		byte[] out;

		// int m = in.length % SM4_BLOCK_SIZE;
		// byte[] out = new byte[in.length + SM4_BLOCK_SIZE - m];
		// byte[] out = new byte[in.length];

		// pc = in.length & 15;
		// numBlocks = (in.length - pc) >>> 4;
		// pc = 16 - pc;
		// numBlocks = (in.length) >>> 4;

		int n = in.length & 15;
		numBlocks = (in.length - n) >>> 4;

		if (isNeedPadding) {
			pc = 16 - n;
			int m = in.length % SM4_BLOCK_SIZE;
			out = new byte[in.length + SM4_BLOCK_SIZE - m];
		} else {
			out = new byte[in.length];
		}

		sm4_key_exp(key, rk);

		/* copy ivec to iv */
		for (j = 0; j < SM4_BLOCK_SIZE; j++) {
			iv[j] = ivec[j];
		}

		for (i = 0; i < numBlocks; i++) {
			/* IV xor plaintext */
			for (j = 0; j < SM4_BLOCK_SIZE; j++) {
				tr[j] = (byte) (in[j + i * SM4_BLOCK_SIZE] ^ iv[j]);
			}
			/* sm4 encryption */
			sm4_encrypt_rk(tr, rk, tr);
			/* output ciphertext and assign new iv */
			for (j = 0; j < SM4_BLOCK_SIZE; j++) {
				out[j + i * SM4_BLOCK_SIZE] = tr[j];
				iv[j] = tr[j];
			}
		}

		if (isNeedPadding) {
			/* process last block */
			j = in.length - numBlocks * SM4_BLOCK_SIZE;
			if (pc == 0)
				pc = 16;
			for (i = 0; i < j; i++)
				tr[i] = in[numBlocks * SM4_BLOCK_SIZE + i];
			for (i = j; i < 16; i++)
				tr[i] = (byte) pc;
			for (i = 0; i < 16; i++)
				tr[i] ^= iv[i];
			sm4_encrypt_rk(tr, 0, rk, out, numBlocks * SM4_BLOCK_SIZE);
		}

		return out;
	}

	/**
	 * 密钥长度128bits。不负责padding
	 * 
	 * @param in
	 * @param in_len_bytes
	 * @param key
	 * @param key_len_bytes
	 * @param ivec
	 * @return
	 */
	public byte[] sm4_cbc_decrypt(final byte[] in, final byte[] key, final byte ivec[], boolean withPadding) {
		int i, j, numBlocks;
		byte tr[] = new byte[16], iv[] = new byte[16];
		int rk[] = new int[32];
		byte[] out = new byte[in.length];

		numBlocks = in.length >>> 4;

		sm4_key_exp(key, rk);

		for (i = 0; i < SM4_BLOCK_SIZE; i++)
			iv[i] = ivec[i];

		for (i = 0; i < numBlocks; i++) {
			for (j = 0; j < SM4_BLOCK_SIZE; j++)
				tr[j] = in[j + i * SM4_BLOCK_SIZE];
			sm4_decrypt_rk(tr, rk, tr);
			for (j = 0; j < SM4_BLOCK_SIZE; j++) {
				out[j + i * SM4_BLOCK_SIZE] = (byte) (tr[j] ^ iv[j]);
				iv[j] = in[j + i * SM4_BLOCK_SIZE];
			}
		}

		// padding
		if (withPadding) {
			int padLen = out[out.length - 1];
			byte[] ret = new byte[out.length - padLen];
			System.arraycopy(out, 0, ret, 0, ret.length);
			return ret;
		}

		return out;
	}

	/**
	 * 密钥长度128bits
	 * 
	 * @param in
	 * @param key
	 * @param isNeedPadding
	 *            是否在本函数内处理padding。true-由本函数打padding，false-原文必须为处理过的整齐的数据
	 * @return
	 */
	public byte[] sm4_ecb_encrypt(final byte[] in, final byte[] key, boolean isNeedPadding) {
		int i, j, numBlocks, pc = 0;
		byte[] tr = new byte[16];
		int rk[] = new int[32];
		byte[] out;

		int n = in.length & 15;
		numBlocks = (in.length - n) >>> 4;

		if (isNeedPadding) {
			pc = 16 - n;
			int m = in.length % SM4_BLOCK_SIZE;
			out = new byte[in.length + SM4_BLOCK_SIZE - m];
		} else {
			out = new byte[in.length];
		}

		sm4_key_exp(key, rk);

		for (i = 0; i < numBlocks; i++) {
			for (j = 0; j < SM4_BLOCK_SIZE; j++)
				tr[j] = in[i * SM4_BLOCK_SIZE + j];
			/* sm4 encryption */
			sm4_encrypt_rk(tr, 0, rk, out, (i * SM4_BLOCK_SIZE));
		}

		if (isNeedPadding) {
			/* process last block */
			j = in.length - numBlocks * SM4_BLOCK_SIZE;
			if (pc == 0)
				pc = 16;
			for (i = 0; i < j; i++)
				tr[i] = in[numBlocks * SM4_BLOCK_SIZE + i];
			for (i = j; i < 16; i++)
				tr[i] = (byte) pc;

			sm4_encrypt_rk(tr, 0, rk, out, numBlocks * SM4_BLOCK_SIZE);
		}

		return out;
	}

	/**
	 * 密钥长度128bits
	 * 
	 * @param in
	 * @param key
	 * @param isNeedPadding
	 *            是否在本函数内处理padding。true-由本函数打padding，false-原文必须为处理过的整齐的数据
	 * @return
	 */
	public byte[] sm4_ecb_decrypt(final byte[] in, final byte[] key, boolean isNeedPadding) {
		int i, j, numBlocks;
		byte[] tr = new byte[16];
		int[] rk = new int[32];
		byte[] out = new byte[in.length];

		numBlocks = in.length / 16;

		sm4_key_exp(key, rk);

		for (i = 0; i < numBlocks; i++) {
			for (j = 0; j < SM4_BLOCK_SIZE; j++)
				tr[j] = in[j + i * SM4_BLOCK_SIZE];
			sm4_decrypt_rk(tr, 0, rk, out, (i * SM4_BLOCK_SIZE));
		}

		if (isNeedPadding) {
			int padLen = out[out.length - 1];
			byte[] ret = new byte[out.length - padLen];
			System.arraycopy(out, 0, ret, 0, ret.length);
			return ret;
		} else
			return out;
	}

	/*
	 * private final static void validation() { byte[] key = { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0xfe, (byte) 0xdc, (byte)
	 * 0xba, (byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10 }; byte[] pt = new byte[16]; byte ct[] = new byte[16]; int rk[] = new int[32]; sm4_key_exp(key, rk); // for (int i = 0; i <
	 * 32; i++) // System.out.println("rk[" + i + "]=" + String.format("%08x", rk[i])); // long start, end; for (int i = 0; i < 16; i++) pt[i] = key[i]; sm4_encrypt_rk(pt, rk, ct); System.out .print(
	 * "\nCorrect Vector     :68 1e df 34 d2 06 96 5e 86 b3 e9 4f 53 6e 42 46\nVerification Vector:"); for (int i = 0; i < 16; i++) System.out.print(String.format("%02x ", ct[i])); sm4_decrypt_rk(ct,
	 * rk, pt); System.out.println("\nplaintext:"); for (int i = 0; i < 16; i++) System.out.print(String.format("%02x ", pt[i]));
	 * 
	 * for (int i = 0; i < 500000; i++) { sm4_encrypt_rk(pt, rk, ct); sm4_encrypt_rk(ct, rk, pt); } System.out .println(
	 * "\n1M iteration Vaildation vector:\n59 52 98 c7 c6 fd 27 1f 04 02 f8 04 c3 3d 3f 66"); for (int i = 0; i < 16; i++) System.out.print(String.format("%02x ", pt[i]));
	 * 
	 * System.out.println(); }
	 */

	public static void main(String[] args) {
		// System.loadLibrary("libeay32");
		// System.loadLibrary("libGM_in_native");

		SM4Impl impl = new SM4Impl();
		byte[] key = { (byte) 0xe5, (byte) 0xb8, (byte) 0x7b, (byte) 0x4d, (byte) 0xf5, (byte) 0xe8, (byte) 0x48, (byte) 0x20, (byte) 0x54, (byte) 0x14, (byte) 0x48, (byte) 0xb1, (byte) 0x1f,
				(byte) 0x5a, (byte) 0xa9, (byte) 0x3f };
		// new byte[16];
		byte IV[] = { (byte) 0x12, (byte) 0xa4, (byte) 0xb7, (byte) 0x39, (byte) 0xee, (byte) 0x9a, (byte) 0xc2, (byte) 0x43, (byte) 0x97, (byte) 0x24, (byte) 0xd8, (byte) 0xc0, (byte) 0xf1,
				(byte) 0xa7, (byte) 0x39, (byte) 0x71 };

		byte[] p1k = new byte[1024];
		byte[] c1k = new byte[1040];
		byte[] p8k = new byte[8192];
		byte[] c8k = new byte[8208];
		long start, end;
		int max;
		max = 1024;
		// System.out.println(String.format("%1$02x ", 2));
		// System.out.println(Integer.toBinaryString(0xFF00FF00) + "\n"
		// + Integer.toBinaryString(ROL(0xFF00FF00, 13)));
		try {
			/*
			 * SecureRandom sr = SecureRandom.getInstance("SHA1PRNG"); for (int i = 0; i < max; i++) p1k[i] = (byte) (sr.nextInt() & 0xFF); for (int i = 0; i < 8192; i++) { p8k[i] = (byte)
			 * (sr.nextInt() & 0xFF); } max = 100000; start = System.currentTimeMillis(); for (int i = 0; i < max; i++) SM4.sm4_cbc_encrypt(p1k, 1024, c1k, key, SM4_BLOCK_SIZE, IV); end =
			 * System.currentTimeMillis(); System.out.println("1k bytes input, Throughput =" + String.format("%8.2f", (max * 1024 * 1.0 / 1024 / 1024 1000 / (end - start))) + " MBps"); start =
			 * System.currentTimeMillis(); for (int i = 0; i < max; i++) SM4.sm4_cbc_encrypt(p8k, 8192, c8k, key, SM4_BLOCK_SIZE, IV); end = System.currentTimeMillis(); System.out.println(
			 * "8k bytes input, Throughput =" + String.format("%8.2f", (max * 1024 * 8.0 / 1024 / 1024 1000 / (end - start))) + " MBps"); validation();
			 */
			byte[] data = new byte[16];
			for (int i = 0; i < data.length; i++)
				data[i] = (byte) i;

			// byte[] ecb_enc_out = sm4_ecb_encrypt(data, key, true);
			byte[] ecb_enc_out = impl.sm4_ecb_encrypt(data, key, true);
			// byte[] ecb_dec_out = sm4_ecb_decrypt(ecb_enc_out, key, false);
			byte[] ecb_dec_out = impl.sm4_ecb_decrypt(ecb_enc_out, key, true);
			System.out.println("ecb ok: " + Arrays.equals(data, ecb_dec_out));

			// byte[] cbc_enc_out = sm4_cbc_encrypt(data, key, IV, true);
			byte[] cbc_enc_out = impl.sm4_cbc_encrypt(data, key, IV, true);
			byte[] cbc_dec_out = impl.sm4_cbc_decrypt(cbc_enc_out, key, IV, true);
			System.out.println("cbc ok: " + Arrays.equals(data, cbc_dec_out));
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
	}
}
