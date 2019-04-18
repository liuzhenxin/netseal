package cn.com.infosec.netseal.common.algorithm.gm;

import java.math.BigInteger;

import cn.com.infosec.math.ec.ECCurve;
import cn.com.infosec.math.ec.ECPoint;
import cn.com.infosec.util.BigIntegers;

public class SM2Util {
	private static final int SM2_SIZE = SM2GMParameter.SM2_SIZE;
	private static final BigInteger gmp = SM2GMParameter.gmp;
	private static final BigInteger gma = SM2GMParameter.gma;
	private static final BigInteger gmb = SM2GMParameter.gmb;
	private static final ECCurve gmec256 = SM2GMParameter.gmec256;
	private static final BigInteger gmgx = SM2GMParameter.gmgx;
	private static final BigInteger gmgy = SM2GMParameter.gmgy;
	private static final byte[] sm2abgxgy = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFC, (byte) 0x28, (byte) 0xE9, (byte) 0xFA, (byte) 0x9E, (byte) 0x9D, (byte) 0x9F, (byte) 0x5E,
			(byte) 0x34, (byte) 0x4D, (byte) 0x5A, (byte) 0x9E, (byte) 0x4B, (byte) 0xCF, (byte) 0x65, (byte) 0x09, (byte) 0xA7, (byte) 0xF3, (byte) 0x97, (byte) 0x89, (byte) 0xF5, (byte) 0x15,
			(byte) 0xAB, (byte) 0x8F, (byte) 0x92, (byte) 0xDD, (byte) 0xBC, (byte) 0xBD, (byte) 0x41, (byte) 0x4D, (byte) 0x94, (byte) 0x0E, (byte) 0x93, (byte) 0x32, (byte) 0xC4, (byte) 0xAE,
			(byte) 0x2C, (byte) 0x1F, (byte) 0x19, (byte) 0x81, (byte) 0x19, (byte) 0x5F, (byte) 0x99, (byte) 0x04, (byte) 0x46, (byte) 0x6A, (byte) 0x39, (byte) 0xC9, (byte) 0x94, (byte) 0x8F,
			(byte) 0xE3, (byte) 0x0B, (byte) 0xBF, (byte) 0xF2, (byte) 0x66, (byte) 0x0B, (byte) 0xE1, (byte) 0x71, (byte) 0x5A, (byte) 0x45, (byte) 0x89, (byte) 0x33, (byte) 0x4C, (byte) 0x74,
			(byte) 0xC7, (byte) 0xBC, (byte) 0x37, (byte) 0x36, (byte) 0xA2, (byte) 0xF4, (byte) 0xF6, (byte) 0x77, (byte) 0x9C, (byte) 0x59, (byte) 0xBD, (byte) 0xCE, (byte) 0xE3, (byte) 0x6B,
			(byte) 0x69, (byte) 0x21, (byte) 0x53, (byte) 0xD0, (byte) 0xA9, (byte) 0x87, (byte) 0x7C, (byte) 0xC6, (byte) 0x2A, (byte) 0x47, (byte) 0x40, (byte) 0x02, (byte) 0xDF, (byte) 0x32,
			(byte) 0xE5, (byte) 0x21, (byte) 0x39, (byte) 0xF0, (byte) 0xA0 };

	// private static final ECPoint gmg = SM2GMParameter.gmg;
	// private static final BigInteger gmn = SM2GMParameter.gmn;

	public static byte[] getZA(byte[] id, byte[] pubkey) {
		byte[] msg = new byte[2 + id.length + 6 * SM2_SIZE];
		int pos = 0;
		System.arraycopy(getENTLa(id), 0, msg, pos, 2); // ENTLA
		pos += 2;
		System.arraycopy(id, 0, msg, pos, id.length);
		pos += id.length;

		// System.arraycopy(gma.toByteArray(), 0, msg, pos, SM2_SIZE);
		//
		// pos += SM2_SIZE;
		//
		// System.arraycopy(gmb.toByteArray(), 0, msg, pos, SM2_SIZE);
		// pos += SM2_SIZE;
		//
		// System.arraycopy(gmgx.toByteArray(), 0, msg, pos, SM2_SIZE);
		// pos += SM2_SIZE;
		//
		// System.arraycopy(gmgy.toByteArray(), 0, msg, pos, SM2_SIZE);
		// pos += SM2_SIZE;

		System.arraycopy(sm2abgxgy, 0, msg, pos, sm2abgxgy.length);
		pos += sm2abgxgy.length;

		if (pubkey.length == 2 * SM2_SIZE) // raw coordinate x||y
			System.arraycopy(pubkey, 0, msg, pos, pubkey.length);
		else if (pubkey.length == 1 + 2 * SM2_SIZE) // not compressed point
													// 04||x||y
			System.arraycopy(pubkey, 1, msg, pos, SM2_SIZE * 2);
		else if (pubkey.length == 1 + SM2_SIZE) {// compressed point (02 or 03
													// )||x
			System.arraycopy(pubkey, 1, msg, pos, SM2_SIZE);
			pos += SM2_SIZE;
			int yb = pubkey[0];
			pubkey[0] = 0;
			System.arraycopy(BigIntegers.asUnsignedByteArray(getY(new BigInteger(1, pubkey), yb)), 0, msg, pos, SM2_SIZE);
		}

		SM3Digest sm3 = new SM3Digest();
		sm3.update(msg);
		byte[] digest = new byte[SM3Digest.DIGEST_LENGTH];
		sm3.digest(digest);

		return digest;

	}

	private static BigInteger getBeta(BigInteger x) {
		BigInteger beta = null;
		BigInteger alpha = x.modPow(new BigInteger("3"), gmp).add(gma.multiply(x)).add(gmb).mod(gmp);
		BigInteger[] rs = gmp.divideAndRemainder(new BigInteger("8"));
		int reminder = rs[1].intValue();
		switch (reminder) {
		case 1:
			break;
		case 5:
			int z = alpha.modPow(rs[0].add(rs[0]).add(BigInteger.ONE), gmp).intValue();
			switch (z) {
			case 1:
				beta = alpha.modPow(rs[0].add(BigInteger.ONE), gmp);
				break;
			case -1:
				beta = alpha.add(alpha);
				beta = beta.multiply(beta.add(beta).modPow(rs[0], gmp));
				break;
			default:
				return null;
			}
			break;
		case 6:// p mod 4 = 3
		case 7:// p mod 4 = 3
				// y^2 =g mod p ?
			beta = alpha.modPow(rs[0].add(rs[0]).add(BigInteger.ONE), gmp);
			if (beta.multiply(beta).mod(gmp).compareTo(alpha) != 0)
				return null;

		}
		return beta;
	}

	static BigInteger getY(BigInteger x, int ypb) {
		BigInteger y = getBeta(x);
		if (y == null)
			return null;
		else {
			byte[] yb = BigIntegers.asUnsignedByteArray(y);
			// 若PC = 02，则令yP = 0；若PC = 03，则令yP = 1；
			if ((yb[yb.length - 1] & 0xFF) == (ypb - 2))
				return y;
			else
				return gmp.add(y.negate());
		}
	}

	/**
	 * @param p
	 *            ECPoint类型的SM2 椭圆曲线点
	 * @return 字节数组
	 */
	public static byte[] point2octect(ECPoint p) {
		byte[] pb = new byte[SM2_SIZE * 2 + 1];

		pb[0] = (byte) 0x04;
		int bisize = BigIntegers.asUnsignedByteArray(p.getX().toBigInteger()).length;
		for (int i = 0; i < SM2_SIZE - bisize; i++) {
			pb[1 + i] = 0;
		}
		System.arraycopy(BigIntegers.asUnsignedByteArray(p.getX().toBigInteger()), 0, pb, 1 + SM2_SIZE - bisize, bisize);
		bisize = BigIntegers.asUnsignedByteArray(p.getY().toBigInteger()).length;
		for (int i = 0; i < SM2_SIZE - bisize; i++) {
			pb[1 + SM2_SIZE + i] = 0;
		}

		System.arraycopy(BigIntegers.asUnsignedByteArray(p.getY().toBigInteger()), 0, pb, 1 + SM2_SIZE + SM2_SIZE - bisize, bisize);
		return pb;
	}

	/**
	 * @param p
	 *            [in] ECPoint类型的SM2 椭圆曲线点
	 * @param x
	 *            [out] x 坐标
	 * @param y
	 *            [out] y 坐标
	 */
	public static void point2octect(ECPoint p, byte[] x, byte[] y) {
		System.arraycopy(BigIntegers.asUnsignedByteArray(p.getX().toBigInteger()), 0, x, 0, SM2_SIZE);
		System.arraycopy(BigIntegers.asUnsignedByteArray(p.getY().toBigInteger()), 0, y, 0, SM2_SIZE);

	}

	/**
	 * @param pb
	 *            字节数组，长度为33的压缩椭圆曲线点字节数组，长度为65的非压缩椭圆曲线点字节数组或长度为64的裸坐标数组
	 * @return ECPoint类型的SM2椭圆曲线点
	 */
	public static ECPoint octect2point(byte[] pb) {
		byte[] x = new byte[SM2_SIZE];
		byte[] y = new byte[SM2_SIZE];
		int px, py;

		switch (pb.length) {
		case 33: // 1+SM2_SIZE
			px = 1;

			System.arraycopy(pb, px, x, 0, SM2_SIZE);
			BigInteger kx = new BigInteger(1, x);
			BigInteger ky = getY(kx, pb[0]);
			if (ky == null)
				return null;
			return gmec256.createPoint(kx, ky, false);

		case 65:// 1+2*SM2_SIZE
			px = 1;
			py = SM2_SIZE + 1;
			if (pb[0] != 4)
				return null;
			System.arraycopy(pb, px, x, 0, SM2_SIZE);
			System.arraycopy(pb, py, y, 0, SM2_SIZE);
			break;
		case 64: // 2*SM2_SIZE
			px = 0;
			py = SM2_SIZE;

			System.arraycopy(pb, px, x, 0, SM2_SIZE);
			System.arraycopy(pb, py, y, 0, SM2_SIZE);

			break;
		default:
			return null;
		}

		return gmec256.createPoint(new BigInteger(1, x), new BigInteger(1, y), false);
	}

	public static ECPoint octect2point(byte[] pb, int offset, int length) {
		byte[] x = new byte[SM2_SIZE];
		byte[] y = new byte[SM2_SIZE];
		int px, py;

		switch (length) {
		case 33: // 1+SM2_SIZE
			px = 1 + offset;

			System.arraycopy(pb, px, x, 0, SM2_SIZE);
			BigInteger kx = new BigInteger(1, x);
			BigInteger ky = getY(kx, pb[offset]);
			if (ky == null)
				return null;
			return gmec256.createPoint(kx, ky, false);

		case 65:// 1+2*SM2_SIZE
			px = 1 + offset;
			py = SM2_SIZE + 1 + offset;
			if (pb[0] != 4)
				return null;
			System.arraycopy(pb, px, x, 0, SM2_SIZE);
			System.arraycopy(pb, py, y, 0, SM2_SIZE);
			break;
		case 64: // 2*SM2_SIZE
			px = 0 + offset;
			py = SM2_SIZE + offset;

			System.arraycopy(pb, px, x, 0, SM2_SIZE);
			System.arraycopy(pb, py, y, 0, SM2_SIZE);

			break;
		default:
			return null;
		}

		return gmec256.createPoint(new BigInteger(1, x), new BigInteger(1, y), false);
	}

	static byte[] getENTLa(byte[] id) {
		byte[] la = new byte[2];
		int entlena = id.length * 8;
		la[1] = (byte) (entlena & 0xFF);
		la[0] = (byte) ((entlena >> 8) & 0xFF);
		// System.out.println(Integer.toString(la[0],
		// 16)+" "+Integer.toString(la[1]+256, 16));
		return la;
	}

	// bc version < 1.47
	// static byte[] derEncode(BigInteger r, BigInteger s) throws IOException {
	// DERInteger[] rs = new DERInteger[] { new DERInteger(r),
	// new DERInteger(s) };
	// return new DERSequence(rs).getEncoded(ASN1Encodable.DER);
	// }
	//
	// static BigInteger[] derDecode(byte[] encoding) throws IOException {
	// ASN1Sequence s = (ASN1Sequence) ASN1Object.fromByteArray(encoding);
	// return new BigInteger[] { ((DERInteger) s.getObjectAt(0)).getValue(),
	// ((DERInteger) s.getObjectAt(1)).getValue() };
	// }
	// bc 147 new style
	// public static byte[] derEncode(BigInteger r, BigInteger s)
	// throws IOException {
	// DERInteger[] rs = new DERInteger[] { new DERInteger(r),
	// new DERInteger(s) };
	// return new DERSequence(rs).getEncoded(ASN1Encoding.DER);
	// }
	//
	// public static BigInteger[] derDecode(byte[] encoding) throws IOException
	// {
	// ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(encoding);
	// return new BigInteger[] { ((ASN1Integer) s.getObjectAt(0)).getValue(),
	// ((ASN1Integer) s.getObjectAt(1)).getValue() };
	// }

	/**
	 * @param x2y2
	 *            --- the original source for key deriving
	 * @param keySize
	 *            -- the length of new key size (it's length of bytes, not length of bits)
	 * @return the new key
	 */
	public static byte[] kdf(final byte[] x2y2, final int keySize) {
		if (keySize < 1)
			return null;
		SM3Digest md = new SM3Digest();
		int mdLength = SM3Digest.DIGEST_LENGTH;
		int d = keySize / mdLength;
		int t = keySize % mdLength;

		byte[] result = new byte[keySize];
		byte[] last = new byte[mdLength];
		int ctr = 1;

		try {
			for (int i = 0; i < d; i++, ctr++) {
				byte[] ctrBytes = I2OSP(ctr);
				md.update(x2y2);
				md.update(ctrBytes);
				md.digest(result, i * mdLength);
			}
		} catch (Exception e) {
			// must not happen
			throw new RuntimeException("internal error");
		}

		if (t != 0) {
			// derive remaining key bytes
			byte[] ctrBytes = I2OSP(ctr);
			md.update(x2y2);
			md.update(ctrBytes);

			md.digest(last);
			System.arraycopy(last, 0, result, d * mdLength, t);
		}

		return result;
	}

	/**
	 * Convert an integer to an octet string of length 4 according to IEEE 1363, Section 5.5.3.
	 * 
	 * @param x
	 *            the integer to convert
	 * @return the converted integer
	 */
	public static byte[] I2OSP(int x) {
		byte[] result = new byte[4];
		result[0] = (byte) (x >>> 24);
		result[1] = (byte) (x >>> 16);
		result[2] = (byte) (x >>> 8);
		result[3] = (byte) x;
		return result;
	}

	public static void main(String args[]) {
		byte[] x2y2 = { (byte) 0x64, (byte) 0xD2, (byte) 0x0D, (byte) 0x27, (byte) 0xD0, (byte) 0x63, (byte) 0x29, (byte) 0x57, (byte) 0xF8, (byte) 0x02, (byte) 0x8C, (byte) 0x1E, (byte) 0x02,
				(byte) 0x4F, (byte) 0x6B, (byte) 0x02, (byte) 0xED, (byte) 0xF2, (byte) 0x31, (byte) 0x02, (byte) 0xA5, (byte) 0x66, (byte) 0xC9, (byte) 0x32, (byte) 0xAE, (byte) 0x8B, (byte) 0xD6,
				(byte) 0x13, (byte) 0xA8, (byte) 0xE8, (byte) 0x65, (byte) 0xFE, (byte) 0x58, (byte) 0xD2, (byte) 0x25, (byte) 0xEC, (byte) 0xA7, (byte) 0x84, (byte) 0xAE, (byte) 0x30, (byte) 0x0A,
				(byte) 0x81, (byte) 0xA2, (byte) 0xD4, (byte) 0x82, (byte) 0x81, (byte) 0xA8, (byte) 0x28, (byte) 0xE1, (byte) 0xCE, (byte) 0xDF, (byte) 0x11, (byte) 0xC4, (byte) 0x21, (byte) 0x90,
				(byte) 0x99, (byte) 0x84, (byte) 0x02, (byte) 0x65, (byte) 0x37, (byte) 0x50, (byte) 0x77, (byte) 0xBF, (byte) 0x78 };
		byte[] kdfr = kdf(x2y2, 19);
		for (int i = 0; i < kdfr.length; i++)
			System.out.print(String.format("%02X", kdfr[i]));
	}

}
