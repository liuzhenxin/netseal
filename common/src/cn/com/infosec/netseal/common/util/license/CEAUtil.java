package cn.com.infosec.netseal.common.util.license;

public class CEAUtil {
	long[][] ExtendKey;
	int CEA_ROTATE;
	int CEA_MPI_LONG;

	/**
	 */
	public CEAUtil() {
		super();
		CEA_ROTATE = 6;
		CEA_MPI_LONG = 2;
		ExtendKey = new long[2 * CEA_ROTATE + 4][2];

	}

	/**
	 * 
	 * @param s1
	 *            long[]
	 * @param s2
	 *            long[]
	 * @param res
	 *            long[]
	 * @param length
	 *            int
	 */
	protected void Add(long[] s1, long[] s2, long[] res, int length) {
		int i;
		long c;

		c = 0;
		for (i = length - 1; i >= 0; i--) {
			res[i] = s1[i] + s2[i] + c;
			c = (res[i] & 0xff00000000L) >>> 32;
			res[i] = res[i] & 0xffffffffL;
		}
	}

	/**
	 * 
	 * @param source
	 *            byte[]
	 * @param result
	 *            int[]
	 * @param length
	 *            int
	 */
	public void ByteToInt(byte[] source, int[] result, int length) {
		int i;

		for (i = 0; i < length; i++) {
			if (source[i] >= 0) {
				result[i] = source[i];
			} else {
				result[i] = source[i] + 256;
			}
		}
	}

	/**
	 * 
	 * @param cipher
	 *            byte[]
	 * @param plain
	 *            byte[]
	 */
	public void CEADecrypt(byte[] cipher, byte[] plain) {
		long[] A;
		long[] B;
		long[] Temp1;
		long[] Temp2;
		int[] Temp;
		int i;
		int shift;
		long Bit1, Bit2;

		A = new long[2];
		B = new long[2];
		Temp1 = new long[2];
		Temp2 = new long[2];
		Temp = new int[16];
		ByteToInt(cipher, Temp, 16);
		A[0] = ((long) Temp[0] << 24) | ((long) Temp[1] << 16) | ((long) Temp[2] << 8) | Temp[3];
		A[1] = ((long) Temp[4] << 24) | ((long) Temp[5] << 16) | ((long) Temp[6] << 8) | Temp[7];
		B[0] = ((long) Temp[8] << 24) | ((long) Temp[9] << 16) | ((long) Temp[10] << 8) | Temp[11];
		B[1] = ((long) Temp[12] << 24) | ((long) Temp[13] << 16) | ((long) Temp[14] << 8) | Temp[15];
		Add(A, ExtendKey[0], A, CEA_MPI_LONG);
		Add(B, ExtendKey[1], B, CEA_MPI_LONG);
		for (i = 1; i <= CEA_ROTATE; i++) {
			Xor(A, B, Temp1, CEA_MPI_LONG);
			shift = (int) (B[1] & 0x3f);
			LShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Add(Temp2, ExtendKey[2 * i], A, CEA_MPI_LONG);
			Xor(B, A, Temp1, CEA_MPI_LONG);
			shift = (int) (A[1] & 0x3f);
			LShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Add(Temp2, ExtendKey[2 * i + 1], B, CEA_MPI_LONG);
		}
		Xor(A, ExtendKey[2 * CEA_ROTATE + 2], Temp1, CEA_MPI_LONG);
		Copy(Temp1, A, CEA_MPI_LONG);
		Xor(B, ExtendKey[2 * CEA_ROTATE + 3], Temp1, CEA_MPI_LONG);
		Copy(Temp1, B, CEA_MPI_LONG);
		LShift(A, Temp1, 1, CEA_MPI_LONG);
		LShift(B, Temp2, 1, CEA_MPI_LONG);
		Bit1 = Temp1[1] & 0x01;
		Bit2 = Temp2[1] & 0x01;
		Temp1[1] = Temp1[1] & 0xfffffffeL;
		Temp1[1] = Temp1[1] | Bit2;
		Temp2[1] = Temp2[1] & 0xfffffffeL;
		Temp2[1] = Temp2[1] | Bit1;
		Bit1 = Temp1[0] & 0x80000000L;
		Bit2 = (Temp1[0] & 0x40000000L) << 1;
		Bit1 = Bit1 ^ Bit2;
		Temp1[0] = Temp1[0] & 0x7fffffffL;
		Temp1[0] = Temp1[0] | Bit1;
		Copy(Temp1, A, CEA_MPI_LONG);
		Copy(Temp2, B, CEA_MPI_LONG);
		for (i = CEA_ROTATE; i >= 1; i--) {
			Sub(B, ExtendKey[2 * i + 1], Temp1, CEA_MPI_LONG);
			shift = (int) (A[1] & 0x3f);
			RShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Xor(Temp2, A, B, CEA_MPI_LONG);
			Sub(A, ExtendKey[2 * i], Temp1, CEA_MPI_LONG);
			shift = (int) (B[1] & 0x3f);
			RShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Xor(Temp2, B, A, CEA_MPI_LONG);
		}
		Sub(B, ExtendKey[1], Temp1, CEA_MPI_LONG);
		Temp[8] = (int) ((Temp1[0] >>> 24) & 0xff);
		Temp[9] = (int) ((Temp1[0] >>> 16) & 0xff);
		Temp[10] = (int) ((Temp1[0] >>> 8) & 0xff);
		Temp[11] = (int) (Temp1[0] & 0xff);
		Temp[12] = (int) ((Temp1[1] >>> 24) & 0xff);
		Temp[13] = (int) ((Temp1[1] >>> 16) & 0xff);
		Temp[14] = (int) ((Temp1[1] >>> 8) & 0xff);
		Temp[15] = (int) (Temp1[1] & 0xff);

		Sub(A, ExtendKey[0], Temp1, CEA_MPI_LONG);
		Temp[0] = (int) ((Temp1[0] >>> 24) & 0xff);
		Temp[1] = (int) ((Temp1[0] >>> 16) & 0xff);
		Temp[2] = (int) ((Temp1[0] >>> 8) & 0xff);
		Temp[3] = (int) (Temp1[0] & 0xff);
		Temp[4] = (int) ((Temp1[1] >>> 24) & 0xff);
		Temp[5] = (int) ((Temp1[1] >>> 16) & 0xff);
		Temp[6] = (int) ((Temp1[1] >>> 8) & 0xff);
		Temp[7] = (int) (Temp1[1] & 0xff);
		IntToByte(Temp, plain, 16);
	}

	/**
	 * 
	 * @param plain
	 *            byte[]
	 * @param cipher
	 *            byte[]
	 */
	public void CEAEncrypt(byte[] plain, byte[] cipher) {
		long[] A;
		long[] B;
		long[] Temp1;
		long[] Temp2;
		int[] Temp;
		int i;
		int shift;
		long Bit1, Bit2;

		A = new long[2];
		B = new long[2];
		Temp1 = new long[2];
		Temp2 = new long[2];
		Temp = new int[16];
		ByteToInt(plain, Temp, 16);
		A[0] = ((long) Temp[0] << 24) | ((long) Temp[1] << 16) | ((long) Temp[2] << 8) | Temp[3];
		A[1] = ((long) Temp[4] << 24) | ((long) Temp[5] << 16) | ((long) Temp[6] << 8) | Temp[7];
		B[0] = ((long) Temp[8] << 24) | ((long) Temp[9] << 16) | ((long) Temp[10] << 8) | Temp[11];
		B[1] = ((long) Temp[12] << 24) | ((long) Temp[13] << 16) | ((long) Temp[14] << 8) | Temp[15];
		Add(A, ExtendKey[0], A, CEA_MPI_LONG);
		Add(B, ExtendKey[1], B, CEA_MPI_LONG);
		for (i = 1; i <= CEA_ROTATE; i++) {
			Xor(A, B, Temp1, CEA_MPI_LONG);
			shift = (int) (B[1] & 0x3f);
			LShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Add(Temp2, ExtendKey[2 * i], A, CEA_MPI_LONG);
			Xor(B, A, Temp1, CEA_MPI_LONG);
			shift = (int) (A[1] & 0x3f);
			LShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Add(Temp2, ExtendKey[2 * i + 1], B, CEA_MPI_LONG);
		}
		LShift(A, Temp1, 63, CEA_MPI_LONG);
		LShift(B, Temp2, 63, CEA_MPI_LONG);
		Bit1 = Temp1[0] & 0x80000000L;
		Bit2 = Temp2[0] & 0x80000000L;
		Temp1[0] = Temp1[0] & 0x7fffffffL;
		Temp1[0] = Temp1[0] | Bit2;
		Temp2[0] = Temp2[0] & 0x7fffffffL;
		Temp2[0] = Temp2[0] | Bit1;
		Bit1 = Temp1[0] & 0x40000000L;
		Bit2 = (Temp1[0] & 0x20000000L) << 1;
		Bit1 = Bit1 ^ Bit2;
		Temp1[0] = Temp1[0] & 0xbfffffffL;
		Temp1[0] = Temp1[0] | Bit1;
		Xor(Temp1, ExtendKey[2 * CEA_ROTATE + 2], A, CEA_MPI_LONG);
		Xor(Temp2, ExtendKey[2 * CEA_ROTATE + 3], B, CEA_MPI_LONG);
		for (i = CEA_ROTATE; i >= 1; i--) {
			Sub(B, ExtendKey[2 * i + 1], Temp1, CEA_MPI_LONG);
			shift = (int) (A[1] & 0x3f);
			RShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Xor(Temp2, A, B, CEA_MPI_LONG);
			Sub(A, ExtendKey[2 * i], Temp1, CEA_MPI_LONG);
			shift = (int) (B[1] & 0x3f);
			RShift(Temp1, Temp2, shift, CEA_MPI_LONG);
			Xor(Temp2, B, A, CEA_MPI_LONG);
		}
		Sub(B, ExtendKey[1], Temp1, CEA_MPI_LONG);
		Temp[8] = (int) ((Temp1[0] >>> 24) & 0xff);
		Temp[9] = (int) ((Temp1[0] >>> 16) & 0xff);
		Temp[10] = (int) ((Temp1[0] >>> 8) & 0xff);
		Temp[11] = (int) (Temp1[0] & 0xff);
		Temp[12] = (int) ((Temp1[1] >>> 24) & 0xff);
		Temp[13] = (int) ((Temp1[1] >>> 16) & 0xff);
		Temp[14] = (int) ((Temp1[1] >>> 8) & 0xff);
		Temp[15] = (int) (Temp1[1] & 0xff);

		Sub(A, ExtendKey[0], Temp1, CEA_MPI_LONG);
		Temp[0] = (int) ((Temp1[0] >>> 24) & 0xff);
		Temp[1] = (int) ((Temp1[0] >>> 16) & 0xff);
		Temp[2] = (int) ((Temp1[0] >>> 8) & 0xff);
		Temp[3] = (int) (Temp1[0] & 0xff);
		Temp[4] = (int) ((Temp1[1] >>> 24) & 0xff);
		Temp[5] = (int) ((Temp1[1] >>> 16) & 0xff);
		Temp[6] = (int) ((Temp1[1] >>> 8) & 0xff);
		Temp[7] = (int) (Temp1[1] & 0xff);
		IntToByte(Temp, cipher, 16);
	}

	/**
	 * 
	 * @param source
	 *            long[]
	 * @param result
	 *            long[]
	 * @param length
	 *            int
	 */
	public void Copy(long[] source, long[] result, int length) {
		int i;
		for (i = 0; i < length; i++) {
			result[i] = source[i];
		}
	}

	/**
	 * 
	 * @return int
	 * @param in
	 *            byte[]
	 * @param out
	 *            byte[]
	 * @param in_len
	 *            int
	 * @param key
	 *            byte[]
	 * @param key_len
	 *            int
	 */
	public int Decrypt(byte[] in_data, byte[] out_data, int in_len, byte[] key, int key_len) {
		byte[] in_temp;
		byte[] out_temp;
		int i, n, j, k;
		int remainder;

		if (in_len < 16) {
			return -1;
		}
		in_temp = new byte[16];
		out_temp = new byte[16];

		init(key, key_len);
		remainder = in_len % 16;
		n = in_len / 16;
		if (remainder == 0) {
			for (i = 0; i < n; i++) {
				k = i * 16;
				for (j = 0; j < 16; j++) {
					in_temp[j] = in_data[k];
					k++;
				}
				CEADecrypt(in_temp, out_temp);
				k = i * 16;
				for (j = 0; j < 16; j++) {
					out_data[k] = out_temp[j];
					k++;
				}
			}
		} else {
			for (i = 0; i < n - 1; i++) {
				k = i * 16;
				for (j = 0; j < 16; j++) {
					in_temp[j] = in_data[k];
					k++;
				}
				CEADecrypt(in_temp, out_temp);
				k = i * 16;
				for (j = 0; j < 16; j++) {
					out_data[k] = out_temp[j];
					k++;
				}
			}

			k = (n - 1) * 16 + remainder;
			for (j = 0; j < 16; j++) {
				in_temp[j] = in_data[k];
				k++;
			}
			CEADecrypt(in_temp, out_temp);
			k = n * 16;
			for (j = 0; j < remainder; j++) {
				out_data[k] = out_temp[j];
				k++;
			}
			k = (n - 1) * 16;
			for (j = 0; j < remainder; j++) {
				in_temp[j] = in_data[k];
				k++;
			}
			for (j = remainder; j < 16; j++) {
				in_temp[j] = out_temp[j];
			}
			CEADecrypt(in_temp, out_temp);
			k = (n - 1) * 16;
			for (j = 0; j < 16; j++) {
				out_data[k] = out_temp[j];
				k++;
			}
		}
		return 0;
	}

	/**
	 * 
	 * @return int
	 * @param in_data
	 *            byte[]
	 * @param out_data
	 *            byte[]
	 * @param in_len
	 *            int
	 * @param key
	 *            byte[]
	 * @param key_len
	 *            int
	 */
	public int Encrypt(byte[] in_data, byte[] out_data, int in_len, byte[] key, int key_len) {
		byte[] in_temp;
		byte[] out_temp;
		int i, n, j, k;
		int remainder;

		if (in_len < 16) {
			return -1;
		}
		in_temp = new byte[16];
		out_temp = new byte[16];

		init(key, key_len);
		remainder = in_len % 16;
		n = in_len / 16;

		for (i = 0; i < n; i++) {
			k = i * 16;
			for (j = 0; j < 16; j++) {
				in_temp[j] = in_data[k];
				k++;
			}
			CEAEncrypt(in_temp, out_temp);
			k = i * 16;
			for (j = 0; j < 16; j++) {
				out_data[k] = out_temp[j];
				k++;
			}
		}
		if (remainder != 0) {
			k = n * 16;
			for (j = 0; j < remainder; j++) {
				in_temp[j] = in_data[k];
				k++;
			}
			k = (n - 1) * 16 + remainder;
			for (j = remainder; j < 16; j++) {
				in_temp[j] = out_data[k];
				k++;
			}
			CEAEncrypt(in_temp, out_temp);
			k = (n - 1) * 16 + remainder;
			for (j = 0; j < 16; j++) {
				out_data[k] = out_temp[j];
				k++;
			}

		}
		return 0;
	}

	/**
	 * 
	 * @param key
	 *            byte[]
	 * @param keylength
	 *            int
	 */
	public void init(byte[] key, int keylength) {
		long[][] SrcKey;
		long[] A;
		long[] B;
		long[] Temp1;
		long[] Temp2;
		int i, j, k, n;
		int keycount;
		int[] temp;
		long t;

		SrcKey = new long[2 * CEA_ROTATE + 4][CEA_MPI_LONG];
		A = new long[CEA_MPI_LONG];
		B = new long[CEA_MPI_LONG];
		Temp1 = new long[CEA_MPI_LONG];
		Temp2 = new long[CEA_MPI_LONG];
		for (i = 0; i < CEA_MPI_LONG; i++) {
			A[i] = 0;
			B[i] = 0;
			Temp1[i] = 0;
			Temp2[i] = 0;
		}
		keycount = (keylength + (CEA_MPI_LONG * 4 - 1)) / (CEA_MPI_LONG * 4);
		temp = new int[keycount * (CEA_MPI_LONG * 4)];
		for (i = 0; i < keycount * (CEA_MPI_LONG * 4); i++) {
			temp[i] = 0;
		}
		ByteToInt(key, temp, keylength);
		n = 0;
		for (i = 0; i < keycount; i++) {
			for (j = 0; j < CEA_MPI_LONG; j++) {
				SrcKey[i][j] = 0;
				for (k = 0; k < 4; k++) {
					SrcKey[i][j] = (SrcKey[i][j] << 8) | (temp[n] & 0xff);
					n++;
				}
			}
		}
		ExtendKey[0][0] = 0xb7e15163L;
		ExtendKey[0][1] = 0x9e3779b9L;
		for (i = 1; i < 2 * CEA_ROTATE + 4; i++) {
			LShift(ExtendKey[i - 1], ExtendKey[i], i, CEA_MPI_LONG);
		}
		for (i = 0; i < 2 * CEA_ROTATE + 4; i++) {
			Add(A, B, Temp1, CEA_MPI_LONG);
			Add(ExtendKey[i], Temp1, Temp2, CEA_MPI_LONG);
			LShift(Temp2, ExtendKey[i], 5, CEA_MPI_LONG);
			Copy(ExtendKey[i], A, CEA_MPI_LONG);
			Add(A, B, Temp1, CEA_MPI_LONG);
			Add(Temp1, SrcKey[i % keycount], Temp2, CEA_MPI_LONG);
			t = Temp1[1] % 64;
			LShift(Temp2, SrcKey[i % keycount], (int) t, CEA_MPI_LONG);
			Copy(SrcKey[i % keycount], B, CEA_MPI_LONG);
		}
	}

	/**
	 * 
	 * @param source
	 *            int[]
	 * @param result
	 *            byte[]
	 * @param length
	 *            int
	 */
	public void IntToByte(int[] source, byte[] result, int length) {
		int i;

		for (i = 0; i < length; i++) {
			if (source[i] < 128) {
				result[i] = (byte) source[i];
			} else {
				result[i] = (byte) (source[i] - 256);
			}
		}
	}

	/**
	 * 
	 * @param source
	 *            long[]
	 * @param result
	 *            long[]
	 * @param count
	 *            int
	 * @param length
	 *            int
	 */
	public void LShift(long[] source, long[] result, int count, int length) {
		int shiftlong, shiftbit, rshiftbit;
		int i;
		long[] mid;
		mid = new long[length];
		count = count % (length * 32);
		shiftlong = count >>> 5;
		shiftbit = count & 0x1f;
		rshiftbit = 32 - shiftbit;
		for (i = 0; i < length; i++) {
			mid[i] = (source[shiftlong] << shiftbit) & 0xffffffffL;
			shiftlong++;
			if (shiftlong >= length) {
				shiftlong = 0;
			}
			mid[i] = mid[i] | ((source[shiftlong] >>> rshiftbit) & 0xffffffffL);
		}
		for (i = 0; i < length; i++) {
			result[i] = mid[i];
		}
	}

	/**
	 * 
	 * @param source
	 *            long[]
	 * @param result
	 *            long[]
	 * @param count
	 *            int
	 * @param length
	 *            int
	 */
	public void RShift(long[] source, long[] result, int count, int length) {
		count = count % (length * 32);
		count = length * 32 - count;
		count = count % (length * 32);
		LShift(source, result, count, length);
	}

	/**
	 * 
	 * @param s1
	 *            long[]
	 * @param s2
	 *            long[]
	 * @param result
	 *            long[]
	 * @param length
	 *            int
	 */
	public void Sub(long[] s1, long[] s2, long[] result, int length) {
		int i;
		long b;

		b = 0;
		for (i = length - 1; i >= 0; i--) {
			result[i] = s1[i] - s2[i] - b;
			if (result[i] >= 0) {
				b = 0;
			} else {
				result[i] = result[i] + 0x100000000L;
				b = 1;
			}
		}
	}

	/**
	 * 
	 * @param s1
	 *            long[]
	 * @param s2
	 *            long[]
	 * @param result
	 *            long[]
	 * @param length
	 *            int
	 */
	public void Xor(long[] s1, long[] s2, long[] result, int length) {
		int i;

		for (i = 0; i < length; i++) {
			result[i] = s1[i] ^ s2[i];
		}
	}
}
