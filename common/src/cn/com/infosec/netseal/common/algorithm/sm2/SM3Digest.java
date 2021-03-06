package cn.com.infosec.netseal.common.algorithm.sm2;

public class SM3Digest {
	public static final int DIGEST_LENGTH = 32;

	private int[] H = new int[8];
	private final byte msg[] = new byte[64];
	private final boolean debug = false;
	private int currentPos;
	private long currentLen;

	public SM3Digest() {
		reset();
	}

	public final int getDigestLength() {
		return 32;
	}

	public final void reset() {
		// 初始值
		H[7] = 0x7380166F;
		H[6] = 0x4914B2B9;
		H[5] = 0x172442D7;
		H[4] = 0xDA8A0600;
		H[3] = 0xA96F30BC;
		H[2] = 0x163138AA;
		H[1] = 0xE38DEE4D;
		H[0] = 0xB0FB0E4E;

		for (int i = 0; i < msg.length; i++)
			msg[i] = 0;

		currentPos = 0;
		currentLen = 0;
	}

	public final void update(byte[] b, int off, int len) {
		for (int i = off; i < (off + len); i++) {
			update(b[i]);
		}
	}

	public final void update(byte[] b) {
		for (int i = 0; i < b.length; i++)
			update(b[i]);
	}

	public final void update(byte b) {
		// System.out.println(currentPos + "->" + b);
		msg[currentPos++] = b;
		currentLen += 8;
		if (currentPos == 64) {
			perform();
			currentPos = 0;
		}
	}

	private static final String toHexString(byte[] b) {
		final String hexChar = "0123456789ABCDEF";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar.charAt((b[i] >> 4) & 0x0F));
			sb.append(hexChar.charAt(b[i] & 0x0F));
		}
		return sb.toString();
	}

	private final void putInt(byte[] b, int pos, int val) {
		b[pos] = (byte) (val >> 24);
		b[pos + 1] = (byte) (val >> 16);
		b[pos + 2] = (byte) (val >> 8);
		b[pos + 3] = (byte) val;
	}

	public final void digest(byte[] out) {
		digest(out, 0);
	}

	public final void digest(byte[] out, int off) {
		long l = currentLen;

		update((byte) 0x80);// 添加最后一个字节

		// 填充余下的零
		while (currentPos != 56)
			update((byte) 0);

		update((byte) (l >> 56));
		update((byte) (l >> 48));
		update((byte) (l >> 40));
		update((byte) (l >> 32));

		update((byte) (l >> 24));
		update((byte) (l >> 16));
		update((byte) (l >> 8));
		update((byte) l);

		putInt(out, off, H[7]);
		putInt(out, off + 4, H[6]);
		putInt(out, off + 8, H[5]);
		putInt(out, off + 12, H[4]);
		putInt(out, off + 16, H[3]);
		putInt(out, off + 20, H[2]);
		putInt(out, off + 24, H[1]);
		putInt(out, off + 28, H[0]);

		reset();
	}

	private final void perform() {
		int w[] = new int[68];
		int wd[] = new int[64];
		int vj[] = new int[8];
		// int vj1[] = new int[8];
		for (int j = 0; j < 16; j++) {
			w[j] = ((msg[j * 4] & 0xFF) << 24) | ((msg[j * 4 + 1] & 0xFF) << 16) | ((msg[j * 4 + 2] & 0xFF) << 8) | ((msg[j * 4 + 3] & 0xFF));
		}
		// 消息分组扩展
		for (int j = 16; j < 68; j++) {
			w[j] = p1((w[j - 16] ^ w[j - 9] ^ (rotl(w[j - 3], 15)))) ^ (rotl(w[j - 13], 7)) ^ w[j - 6];
		}
		for (int j = 0; j < 64; j++) {
			wd[j] = w[j] ^ w[j + 4];
		}
		// for (int j = 0; j < 68; j++) {
		// if ((j % 8) == 0) {
		// System.out.print("\n[" + (j / 8) + "] ");
		// }
		// System.out.print(String.format("%08X", w[j]).toUpperCase() + " ");
		//
		// }
		// 开始迭代
		for (int i = 0; i < 8; i++)
			vj[i] = H[i];

		final int T0_15 = 0x79CC4519;
		final int T16_63 = 0x7A879D8A;
		int ss1, ss2, tt1, tt2;

		// A->v[7] B->v[6] C->v[5] D->v[4] E->v[3] F->v[2] G->v[1] H->v[0]
		for (int j = 0; j < 64; j++) {
			if (debug) {
				// System.out.print("\ni=" + String.format("%02d ", j));
				// for (int i = 7; i >= 0; i--)
				// System.out.print(String.format("%08X", vj[i]).toUpperCase()
				// + " ");
			}
			/* 计算SS1 */
			if (j < 16)
				ss1 = rotl((rotl(vj[7], 12) + vj[3] + rotl(T0_15, j)), 7); // [(A<<12)+E+(Tj<<<j)]<<<7
			else
				ss1 = rotl((rotl(vj[7], 12) + vj[3] + rotl(T16_63, j)), 7);
			/* 计算SS2 */
			ss2 = ss1 ^ rotl(vj[7], 12);// SS1 xor (A<<<12)
			/* 计算TT1 */// [FF(A,B,C) +D +SS2] +wdj
			if (j < 16)
				tt1 = (FF_0_15(vj[7], vj[6], vj[5]) + vj[4] + ss2) + wd[j];
			else
				tt1 = (FF_16_63(vj[7], vj[6], vj[5]) + vj[4] + ss2) + wd[j];
			/* 计算TT2 */// [GG(E,F,G)+H+SS1]+wj
			if (j < 16)
				tt2 = GG_0_15(vj[3], vj[2], vj[1]) + vj[0] + ss1 + w[j];
			else
				tt2 = GG_16_63(vj[3], vj[2], vj[1]) + vj[0] + ss1 + w[j];
			/* 计算D */
			vj[4] = vj[5];// D=C
			/* 计算C */
			vj[5] = rotl(vj[6], 9);// C=B<<<9
			/* 计算B */
			vj[6] = vj[7];// B=A
			/* 计算A */
			vj[7] = tt1;// A=TT1
			/* 计算H */
			vj[0] = vj[1];// H=G
			/* 计算G */
			vj[1] = rotl(vj[2], 19);// G=F<<<19
			/* 计算F */
			vj[2] = vj[3];// F=E
			/* 计算E */
			vj[3] = p0(tt2);// E=P0(TT2)
		}// end for
			// if (debug) {
		// System.out.print("\ni=" + String.format("%02d ", 64));
		// for (int i = 7; i >= 0; i--)
		// System.out.print(String.format("%08X", vj[i]).toUpperCase()
		// + " ");
		// }
		// for (int i = 0; i < 8; i++)
		// vj1[i] = H[i] ^ vj[i];
		for (int i = 0; i < 8; i++)
			H[i] ^= vj[i];
		// if (debug) {
		// System.out.println();
		// for (int i = 7; i >= 0; i--)
		// System.out.print(String.format("%08X", H[i]).toUpperCase()
		// + " ");
		// }
		// ? currentPos=0;
	}

	private final int FF_0_15(int a, int b, int c) {
		return a ^ b ^ c;
	}

	private final int FF_16_63(int a, int b, int c) {
		return (a & b) | (a & c) | (b & c);
	}

	private final int GG_0_15(int a, int b, int c) {
		// return a^b^c;
		return FF_0_15(a, b, c);
	}

	private final int GG_16_63(int a, int b, int c) {
		return ((a & b) | ((~a) & c));
	}

	private final int rotl(int value, int bits) {
		// 2011.5.25 renhongwei 经和pony确认，下面这种实现是正确的，等同与Integer.rotateLeft(value,
		// bits)
		return ((value << bits) | (value >>> -bits));
		// return (i << distance) | (i >> (32 - bits) );
		// return Integer.rotateLeft(value, bits);
	}

	private final int p0(int x) {
		return x ^ rotl(x, 9) ^ rotl(x, 17);
	}

	private final int p1(int x) {
		return x ^ rotl(x, 15) ^ rotl(x, 23);
	}

	public static void main(String[] args) {

		byte[] vd = { (byte) 0x14, (byte) 0x0, (byte) 0x0, (byte) 0x0c, (byte) 0xf0, (byte) 0xe3, (byte) 0x6c, (byte) 0xca, (byte) 0x1f, (byte) 0xc0, (byte) 0x77, (byte) 0xee, (byte) 0xb5,
				(byte) 0x0b, (byte) 0x2c, (byte) 0x46 };

		byte[] message = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd".getBytes();

		byte[] mc = {};
		byte[] nc = { 0x72, 0x35, 0x09, 0x75 };
		byte[] n = {};
		byte[] digest = new byte[32];

		int i, max = 100, m_len = message.length;
		long t0, t1;
		t0 = System.nanoTime();// System.currentTimeMillis();//System.nanoTime();//
		SM3Digest sm3 = new SM3Digest();
		sm3.update(vd);
		sm3.digest(digest);
		for (i = 0; i < 32; i++)
			System.out.print(Integer.toHexString(digest[i] & 0xFF) + " ");
		System.out.println();
		sm3 = new SM3Digest();
		for (i = 0; i < max; i++)
			sm3.update(message);
		sm3.digest(digest);
		t1 = System.nanoTime();// System.currentTimeMillis();//System.nanoTime();//
		System.out.println("SM3 Throughput = " + (max * m_len * 1000000000.0 / (t1 - t0) / 1024 / 1024) + " MB/sec");
	}

}
