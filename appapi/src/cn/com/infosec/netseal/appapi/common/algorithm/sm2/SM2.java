package cn.com.infosec.netseal.appapi.common.algorithm.sm2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import cn.com.infosec.math.ec.ECCurve;
import cn.com.infosec.math.ec.ECPoint;
import cn.com.infosec.util.BigIntegers;

public class SM2 {
	final static int SM2_SIZE = 32;

	final static BigInteger gmp = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);
	final static BigInteger gma = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16);
	final static BigInteger gmb = new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16);

	final static ECCurve gmec256 = new ECCurve.Fp(gmp, gma, gmb);
	final static BigInteger gmgx = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
	final static BigInteger gmgy = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);

	final static ECPoint gmg = gmec256.createPoint(gmgx, gmgy, false);

	final static BigInteger gmn = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
	public static final byte[] defaultIdAndLength = { (byte) 0x00, (byte) 0x80, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x31,
			(byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38 };

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

	// public static final AlgorithmIdentifier sm2oid = new AlgorithmIdentifier(
	// new ASN1ObjectIdentifier("1.156.10197.1.301"));
	// public static final AlgorithmIdentifier ecPublicKeyOid = new
	// AlgorithmIdentifier(
	// new ASN1ObjectIdentifier("1.2.840.10045.2.1"));

	private static final SecureRandom random = getRandom();

	private static SecureRandom getRandom() {
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (Exception exp) {
			random = new SecureRandom();
		}
		return random;
	}

	static class SM2Holder {
		static SM2 instance = new SM2();
	}

	public static SM2 getInstance() {
		return SM2Holder.instance;
	}

	private static ECPoint kmg(ECPoint p, BigInteger k) {
		JPECPoint pp = new JPECPoint.Fp(gmec256, p.getX(), p.getY());
		return pp.multiply(k).toECPoint();
	}

	/*
	 * 产生SM2密钥对函数 输入参数： 无 输出参数：
	 * 
	 * @param privKey -- 私钥，长度为32的byte数组
	 * 
	 * @param pubKeyX -- 公钥X，长度为32的byte数组
	 * 
	 * @param pubKeyY -- 公钥Y，长度为32的byte数组 返回值: 无
	 */
	public void genKeyPair(byte[] privKey, byte[] pubKeyX, byte[] pubKeyY) {
		BigInteger k = null;
		int nBitLength = gmn.bitLength();
		ECPoint kg;
		do {
			k = new BigInteger(nBitLength, random);
			k = k.mod(gmn);
			kg = gmg.multiply(k);
			ECPoint kg2 = kmg(gmg, k);
			System.out.println("KG:\n" + kg.toString() + "\n" + kg2.toString());
		} while (kg.isInfinity());
		byte[] temp;
		// 规范化私钥和公钥的输出，数据结果均为32字节，不足32字节的都已经在前面补零了
		// 修订于2013-9-9
		for (int i = 0; i < 32; i++) {
			privKey[i] = 0;
			pubKeyX[i] = 0;
			pubKeyY[i] = 0;
		}
		temp = BigIntegers.asUnsignedByteArray(k);
		System.arraycopy(temp, 0, privKey, 32 - temp.length, temp.length);
		temp = BigIntegers.asUnsignedByteArray(kg.getX().toBigInteger());
		System.arraycopy(temp, 0, pubKeyX, 32 - temp.length, temp.length);
		temp = BigIntegers.asUnsignedByteArray(kg.getY().toBigInteger());
		System.arraycopy(temp, 0, pubKeyY, 32 - temp.length, temp.length);

	}

	// public void genKeyPair(ECPrivateKey privkey, ECPublicKey pubkey) {
	// BigInteger k = null;
	// int nBitLength = gmn.bitLength();
	// ECPoint kg;
	// do {
	// k = new BigInteger(nBitLength, random);
	// k = k.mod(gmn);
	// kg = SM2Constants.jg.multiply(k).toECPoint();// gmg.multiply(k);
	// } while (kg.isInfinity());
	// privkey = new ECPrivateKey(k);
	// byte[] pk = new byte[65];
	// pk[0] = 0;
	// byte[] temp = BigIntegers.asUnsignedByteArray(kg.getX().toBigInteger());
	// System.arraycopy(temp, 0, pk, 33 - temp.length, temp.length);
	// temp = BigIntegers.asUnsignedByteArray(kg.getY().toBigInteger());
	// System.arraycopy(temp, 0, pk, 65 - temp.length, temp.length);
	// SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo(sm2oid, pk);
	// ECParameterSpec ecps = new ECParameterSpec(new EllipticCurve(
	// new ECFieldFp(gmp), gma, gmb), gmg, gmn, 1);
	// ECPublicKeySpec epks = new ECPublicKeySpec(kg, ecps);
	// }

	/**
	 * 产生SM2密钥对函数 输入参数： 无 输出参数：
	 * 
	 * @param privkey
	 *            -- 私钥，长度为32的byte数组
	 * 
	 * @param pubKey
	 *            -- * 公钥，长度为65的byte数组 返回值: 无
	 */

	public void genKeyPair(byte[] pubkey, byte[] privkey) {

		BigInteger k = null;
		int nBitLength = gmn.bitLength();
		ECPoint kg;
		do {
			k = new BigInteger(nBitLength, random);
			k = k.mod(gmn);
			kg = SM2Constants.jg.multiply(k).toECPoint();// gmg.multiply(k);
		} while (kg.isInfinity());
		byte[] temp = BigIntegers.asUnsignedByteArray(k);
		for (int i = 0; i < 32; i++)
			privkey[i] = 0;
		System.arraycopy(temp, 0, privkey, SM2_SIZE - temp.length, temp.length);
		// 规范化私钥和公钥的输出，数据结果均为32字节，不足32字节的都已经在前面补零了
		// 修订于2013-9-9
		pubkey[0] = (byte) 0x04;// uncompressed point
		for (int i = 1; i < 65; i++)
			pubkey[i] = 0;
		temp = BigIntegers.asUnsignedByteArray(kg.getX().toBigInteger());
		System.arraycopy(temp, 0, pubkey, 33 - temp.length, temp.length);
		temp = BigIntegers.asUnsignedByteArray(kg.getY().toBigInteger());
		System.arraycopy(temp, 0, pubkey, 65 - temp.length, temp.length);
	}

	/**
	 * 签名函数 输入参数：
	 * 
	 * @param msg_hash
	 *            -- 待签名数据的Hash值， 长度为32的byte数组
	 * @param privKey
	 *            --用户私钥 长度为32的byte数组
	 * @return返回值: 签名结果,长度为71的byte数组
	 * 
	 */

	public static byte[] signHash(final byte[] msg_hash, final byte[] privkey) {
		if (msg_hash.length != SM2_SIZE || privkey.length != SM2_SIZE)
			return null;

		BigInteger e = new BigInteger(1, msg_hash);
		BigInteger r = null;
		BigInteger s = null;

		// 5.3.2
		do // generate s
		{
			BigInteger k = null;
			int nBitLength = gmn.bitLength();

			do // generate r
			{
				do {
					k = new BigInteger(nBitLength, random);
				} while (k.equals(BigInteger.ZERO) || k.compareTo(gmn) >= 0);
				// p=k[G]

				ECPoint p = kmg(gmg, k);// gmg.multiply(k);

				// 5.3.3
				BigInteger x = p.getX().toBigInteger();

				r = x.mod(gmn);
			} while (r.equals(BigInteger.ZERO) || (e.add(r).compareTo(gmn) == 0));

			BigInteger d = new BigInteger(1, privkey);
			// r=(e+x1) mod n
			r = e.add(r).mod(gmn);

			// s= (((1+dA)^(-1))(k-rdA) ) mod n
			s = BigInteger.ONE.add(d).modInverse(gmn).multiply(k.add(d.multiply(r).negate())).mod(gmn);

		} while (s.equals(BigInteger.ZERO));

		byte[] x = new byte[32];
		byte[] y = new byte[32];
		x = BigIntegers.asUnsignedByteArray(r);
		y = BigIntegers.asUnsignedByteArray(s);
		return xy2sign(x, y);
	}

	// 5.4 pg 29
	/**
	 * return true if the value r and s represent a OSCCA SM2 signature for the passed in message (for standard DSA the message should be a SM3 hash of the real message to be verified).
	 */
	/**
	 * @param msg_hash
	 *            原文信息杂凑值
	 * @param r
	 *            签名结果中的R
	 * @param s
	 *            签名结果中的S
	 * @param x
	 *            签名者公钥x坐标
	 * @param y
	 *            签名者公钥y坐标
	 * @return true 签名正确 false 签名错误
	 */
	public boolean verifyHash(byte[] msg_hash, byte[] r, byte[] s, byte[] x, byte[] y) {
		byte[] signature = new byte[2 * SM2_SIZE];
		System.arraycopy(r, 0, signature, 0, SM2_SIZE);
		System.arraycopy(s, 0, signature, SM2_SIZE, SM2_SIZE);
		byte[] pubkey = new byte[1 + 2 * SM2_SIZE];
		pubkey[0] = 4;
		System.arraycopy(x, 0, pubkey, 1, SM2_SIZE);
		System.arraycopy(y, 0, pubkey, 1 + SM2_SIZE, SM2_SIZE);

		return verifyHash(msg_hash, signature, pubkey);
	}

	private static ECPoint implShamirsTrick(JPECPoint P, BigInteger k, JPECPoint Q, BigInteger l) {
		int m = Math.max(k.bitLength(), l.bitLength());
		JPECPoint Z = P.add(Q);
		JPECPoint R = P.getInfinity();

		for (int i = m - 1; i >= 0; --i) {
			R = R.twice();

			if (k.testBit(i)) {
				if (l.testBit(i)) {
					R = R.add(Z);
				} else {
					R = R.add(P);
				}
			} else {
				if (l.testBit(i)) {
					R = R.add(Q);
				}
			}
		}

		return R.toECPoint();
	}

	/*
	 * 签名验证 输入参数:
	 * 
	 * @param msg_hash -- 待签名数据的Hash值， 长度为32的byte数组
	 * 
	 * @param signature -- 签名结果,长度为64的byte数组
	 * 
	 * @param pubkey -- 公钥，长度为64的byte数组 、长度为33的压缩椭圆曲线点 或长度为65的非压缩椭圆曲线点
	 * 
	 * @return 返回值：--true，签名正确, false --签名错误
	 */
	public static boolean verifyHash(final byte[] msg_hash, final byte[] signature, final byte[] pubkey) {
		// long start;
		// start = System.nanoTime();
		if (msg_hash.length != SM2_SIZE) {
			// System.out.println("msg_hash length is wrong!");
			return false;
		}
		if ((signature.length != 2 * SM2_SIZE) && (signature.length != 1 + 2 * SM2_SIZE) && (!(signature.length >= 5 + 2 * SM2_SIZE))) {
			// System.out.println("signatuer length is wrong!\nsignature length="
			// + signature.length);
			return false;
		}
		if ((pubkey.length != 2 * SM2_SIZE) && (pubkey.length != (1 + 2 * SM2_SIZE))) {
			// System.out.println("public key length is wrong!");
			return false;
		}
		BigInteger e = new BigInteger(1, msg_hash);
		BigInteger r = null, s = null;
		if (signature.length >= 5 + 2 * SM2_SIZE) {
			byte[] x = new byte[32];
			byte[] y = new byte[32];
			sign2xy(signature, x, y);
			r = new BigInteger(1, x);
			s = new BigInteger(1, y);
		} else {
			byte[] signpart = new byte[SM2_SIZE];
			// simple transfer signature to two BigIntegers, not support
			// compressed
			// point presentation
			int pos = 0;
			if (signature[0] == 4 && signature.length == SM2_SIZE * 2 + 1)
				pos = 1;

			System.arraycopy(signature, pos, signpart, 0, SM2_SIZE);
			r = new BigInteger(1, signpart);
			System.arraycopy(signature, pos + SM2_SIZE, signpart, 0, SM2_SIZE);
			s = new BigInteger(1, signpart);
		}
		// r in the range [1,n-1]
		if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(gmn) >= 0) {
			// System.out.println("Signature's R is wrong!");
			return false;
		}

		// s in the range [1,n-1]
		if (s.compareTo(BigInteger.ONE) < 0 || s.compareTo(gmn) >= 0) {
			// System.out.println("Signature's S is wrong!");
			return false;
		}
		// t=(r'+s') mod n
		BigInteger t = r.add(s).mod(gmn);

		ECPoint Q = octect2point(pubkey);

		ECPoint xy1 = implShamirsTrick(SM2Constants.jg, s, new JPECPoint.Fp(Q), t);
		// R=(e'+x1') mod n
		BigInteger v = xy1.getX().toBigInteger().add(e).mod(gmn);
		// System.out.println("---3 elapsed " + (System.nanoTime() - start));

		return (v.compareTo(r) == 0);
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

	public static void sign2xy(byte[] sign, byte[] x, byte[] y) {
		int i, x_delta = 0, y_delta = 0;
		if (sign[3] == 33)
			x_delta = 1;
		if (sign[37 + x_delta] == 33)
			y_delta = 1;
		for (i = 0; i < 32; i++) {
			x[i] = sign[4 + x_delta + i];
			y[i] = sign[38 + x_delta + y_delta + i];
		}
	}

	public static byte[] xy2sign(byte[] x, byte[] y) {
		// 30 len 02 len x 02 len y
		int x_delta = 0, y_delta = 0;
		if ((x.length == SM2_SIZE) && (x[0] & 0xFF) >= 0x80)
			x_delta = 1;
		if ((y.length == SM2_SIZE) && (y[0] & 0xFF) >= 0x80)
			y_delta = 1;
		byte[] sign = new byte[70 + x_delta + y_delta];
		sign[0] = 0x30;
		sign[1] = (byte) (68 + x_delta + y_delta);// 32+32+2+2+2+(x's 00) + (y's
													// 00)
		sign[2] = 2;
		sign[3 + x_delta] = 0; // 这一行必须在前面，否则如果x_delta＝0，将会把长度值给冲了
		sign[3] = (byte) (32 + x_delta);
		int pad_length = SM2_SIZE - x.length;// 解决长度不足的问题
		for (int i = 0; i < pad_length; i++)
			sign[4 + x_delta + i] = 0;
		for (int i = 0; i < x.length; i++)
			sign[4 + x_delta + i + pad_length] = x[i];
		sign[36 + x_delta] = 2;
		sign[38 + x_delta] = 0;
		sign[37 + x_delta] = (byte) (32 + y_delta);
		pad_length = SM2_SIZE - y.length;
		for (int i = 0; i < pad_length; i++)
			sign[38 + x_delta + y_delta + i] = 0;
		for (int i = 0; i < y.length; i++)
			sign[38 + x_delta + y_delta + i + pad_length] = y[i];
		return sign;
	}

	/**
	 * 签名函数 输入参数：
	 * 
	 * @param id
	 *            -- 签名者ID
	 * 
	 * @param data
	 *            -- 待签名数据
	 * @param privKey
	 *            --用户私钥 长度为32的byte数组
	 * @param pubKey
	 *            --用户公钥 长度为65的byte数组 ,如果pubkey为null,系统自动计算公钥
	 * @return返回值: 签名结果,长度为71的byte数组
	 * 
	 */

	public static byte[] signData(byte[] id, byte[] data, byte[] privKey, byte[] pubkey) {
		SM3Digest sm3 = new SM3Digest();
		int id_length;
		if (id == null) {
			sm3.update(defaultIdAndLength);
		} else {
			id_length = id.length;
			sm3.update((byte) (id_length >> 8));
			sm3.update((byte) (id_length & 0xFF));
			sm3.update(id);
		}
		sm3.update(sm2abgxgy);
		if (pubkey == null) {
			BigInteger d = new BigInteger(1, privKey);
			ECPoint pk = SM2Constants.jg.multiply(d).toECPoint();
			byte[] coordinate = new byte[32];
			byte[] temp = BigIntegers.asUnsignedByteArray(pk.getX().toBigInteger());
			System.arraycopy(temp, 0, coordinate, 32 - temp.length, temp.length);
			sm3.update(coordinate);
			temp = BigIntegers.asUnsignedByteArray(pk.getY().toBigInteger());
			System.arraycopy(temp, 0, coordinate, 32 - temp.length, temp.length);
			sm3.update(coordinate);
		} else {
			sm3.update(ByteBuffer.allocate(64).put(pubkey, 1, 64).array());
		}
		byte[] za = new byte[32];
		sm3.digest(za);
		sm3 = new SM3Digest();
		sm3.update(za);
		sm3.update(data);
		byte[] msg_hash = new byte[32];
		sm3.digest(msg_hash);
		return signHash(msg_hash, privKey);
	}

	/**
	 * singing data by privKey and default ID
	 * 
	 * @param data
	 *            to be signed data
	 * @param privKey
	 *            private key
	 * @return signature
	 */
	public static byte[] signData(byte[] data, byte[] privKey) {
		return signData(null, data, privKey, null);
	}

	/**
	 * singing data by privKey and default ID
	 * 
	 * @param data
	 *            --- to be signed data
	 * @param privKey
	 *            --- private key
	 * @param pubKey
	 *            --- public key
	 * @return --- signature
	 */

	public static byte[] signData(byte[] data, byte[] privKey, byte[] pubKey) {
		return signData(null, data, privKey, pubKey);
	}

	/*
	 * 签名验证 输入参数:
	 * 
	 * @param id -- 签名ID,如果id为null，则使用缺省ID进行运算
	 * 
	 * @param data -- 待签名数据
	 * 
	 * @param signature -- 签名结果,长度为64的byte数组
	 * 
	 * @param pubkey -- 公钥，长度为64的byte数组 、长度为33的压缩椭圆曲线点 或长度为65的非压缩椭圆曲线点
	 * 
	 * @return 返回值：--true，签名正确, false --签名错误
	 */
	public static boolean verifyData(byte[] id, byte[] data, byte[] signature, byte[] pubkey) {
		if (data == null || signature == null || pubkey == null)
			return false;

		SM3Digest sm3 = new SM3Digest();
		int id_length;
		if (id == null) {
			sm3.update(defaultIdAndLength);
		} else {
			id_length = id.length;
			sm3.update((byte) (id_length >> 8));
			sm3.update((byte) (id_length & 0xFF));
			sm3.update(id);
		}
		sm3.update(sm2abgxgy);

		sm3.update(ByteBuffer.allocate(64).put(pubkey, 1, 64).array());

		byte[] za = new byte[32];
		sm3.digest(za);
		sm3 = new SM3Digest();
		sm3.update(za);
		sm3.update(data);
		byte[] msg_hash = new byte[32];
		sm3.digest(msg_hash);
		return verifyHash(msg_hash, signature, pubkey);
	}

	/**
	 * verify signature using default ID
	 * 
	 * @param data
	 *            --- signed data
	 * @param signature
	 *            -- signature
	 * @param pubkey
	 *            --- public key
	 * @return the result of verification
	 */
	public static boolean verifyData(byte[] data, byte[] signature, byte[] pubkey) {
		return verifyData(null, data, signature, pubkey);
	}

	/**
	 * the function of key derived function, generating the key from key material, the length of key is keySize
	 * 
	 * @param x2y2
	 *            key material
	 * @param keySize
	 *            the length of target key
	 * @return target key
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
				byte[] ctrBytes = ByteBuffer.allocate(4).putInt(ctr).array(); // I2OSP(ctr);
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
			byte[] ctrBytes = ByteBuffer.allocate(4).putInt(ctr).array();// I2OSP(ctr);
			md.update(x2y2);
			md.update(ctrBytes);
			md.digest(last);
			System.arraycopy(last, 0, result, d * mdLength, t);
		}

		return result;
	}

	/**
	 * @param msg
	 *            明文数据
	 * @param pubkey
	 *            加密公钥
	 * @return 密文数据，长度为明文数据长度+97字节
	 */
	public static byte[] encrypt(final byte[] pubkey, final byte[] msg) {
		BigInteger k = null;
		byte[] c = new byte[97 + msg.length];
		int nBitLength = gmn.bitLength();
		SecureRandom random = null;
		// 优先使用SHA1PRNG随机数算法
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (Exception exp) {
			random = new SecureRandom();
		}
		ECPoint kg, pbk;
		do {
			// A1
			do {
				k = new BigInteger(nBitLength, random);
				k = k.mod(gmn);
			} while (k.compareTo(BigInteger.ZERO) <= 0);

			// A2
			kg = SM2Constants.jg.multiply(k).toECPoint();// gmg.multiply(k);
			pbk = octect2point(pubkey);
			// A4
			pbk = (new JPECPoint.Fp(pbk)).multiply(k).toECPoint();// pbk.multiply(k);
		} while (pbk.isInfinity());

		byte[] tmpib = BigIntegers.asUnsignedByteArray(kg.getX().toBigInteger());
		c[0] = (byte) 04;
		System.arraycopy(tmpib, 0, c, 33 - tmpib.length, tmpib.length);
		tmpib = BigIntegers.asUnsignedByteArray(kg.getY().toBigInteger());
		System.arraycopy(tmpib, 0, c, 65 - tmpib.length, tmpib.length);

		byte[] xy2 = new byte[65];// point2octect(pbk);
		xy2[0] = (byte) 4;
		tmpib = BigIntegers.asUnsignedByteArray(pbk.getX().toBigInteger());
		System.arraycopy(tmpib, 0, xy2, 33 - tmpib.length, tmpib.length);
		tmpib = BigIntegers.asUnsignedByteArray(pbk.getY().toBigInteger());
		System.arraycopy(tmpib, 0, xy2, 65 - tmpib.length, tmpib.length);

		byte[] t = kdf(ByteBuffer.allocate(64).put(xy2, 1, 64).array(), msg.length);// kdf(x2y2,
																					// msg.length);
		for (int i = 0; i < msg.length; i++)
			// c2
			// c[i + 65] = (byte) (msg[i] ^ t[i]); //c1||c2||c3
			c[i + 97] = (byte) (msg[i] ^ t[i]); // c1||c3||c2 strlen(c1||c3) =
												// 97

		SM3Digest md = new SM3Digest();
		md.update(ByteBuffer.allocate(32).put(xy2, 1, 32).array());// (x2);
		md.update(msg);
		md.update(ByteBuffer.allocate(32).put(xy2, 33, 32).array());// (y2);
		// md.digest(c, 65 + msg.length);// c1||c2
		md.digest(c, 65);// c1||c3
		return c;
	}

	/**
	 * @param ct
	 *            密文数据
	 * @param privkey
	 *            私钥
	 * @param pt
	 *            明文数据,长度为密文(ct)长度-97
	 * @return boolean类型, 正确解密返回true,并将解密结果放在pt(明文数据)中,pt长度为密文(ct)长度-97,否则返回false
	 */
	public static boolean decrypt(final byte[] privkey, final byte[] ct, byte[] pt) {
		// check the length of parameters
		if (ct.length < 98 || privkey.length != SM2_SIZE) {
			return false;
		}

		int ctl = ct.length - 97;

		byte[] c2 = ByteBuffer.allocate(ctl).put(ct, 97, ctl).array();// new
																		// byte[ctl];
		byte[] c3 = ByteBuffer.allocate(32).put(ct, 65, 32).array();// new
																	// byte[SM2_SIZE];

		// B1 split c1 c3 c2

		ECPoint c1p = octect2point(ByteBuffer.allocate(65).put(ct, 0, 65).array());// (c1);
		BigInteger d = new BigInteger(1, privkey);
		// B3
		c1p = c1p.multiply(d);

		byte[] tmpib = BigIntegers.asUnsignedByteArray(c1p.getX().toBigInteger());
		byte[] xy = new byte[64];
		System.arraycopy(tmpib, 0, xy, 32 - tmpib.length, tmpib.length);
		tmpib = BigIntegers.asUnsignedByteArray(c1p.getY().toBigInteger());
		System.arraycopy(tmpib, 0, xy, 64 - tmpib.length, tmpib.length);
		// B4
		byte[] t = kdf(xy, ctl);
		// B5
		for (int i = 0; i < ctl; i++) {
			pt[i] = (byte) (c2[i] ^ t[i]);
		}

		SM3Digest md = new SM3Digest();
		md.update(ByteBuffer.allocate(32).put(xy, 0, 32).array());
		md.update(pt);
		md.update(ByteBuffer.allocate(32).put(xy, 32, 32).array());
		byte[] u = new byte[SM3Digest.DIGEST_LENGTH];
		md.digest(u);
		// B6
		for (int i = 0; i < SM2_SIZE; i++) {
			if ((u[i] & 0xFF) != (c3[i] & 0xFF)) {
				return false;
			}
		}

		return true;
	}

	static byte[] readFile(String filename) {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(new File(filename)));
			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer, 0, size);
			return buffer;
		} catch (Exception e) {
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	static void verifykeypair(String folder) {
		File f = new File(folder);
		if (!f.isDirectory()) {
			return;
		} else {
			File fall[] = f.listFiles();
			String fname = null;
			for (int i = 0; i < fall.length; i++) {

				if (fall[i].getName().endsWith("pri")) {
					fname = fall[i].getName();
					byte[] prvkey = readFile(folder + "/" + fname);
					byte[] pubkey = readFile(folder + "/" + fname.substring(0, fname.length() - 3) + "pub");
					BigInteger prvkeyint = new BigInteger(1, prvkey);
					ECPoint pubkeyp = gmg.multiply(prvkeyint);
					byte[] pubkeype = pubkeyp.getEncoded();
					boolean equals = true;
					for (int j = 0; j < pubkey.length; j++) {
						if (pubkey[j] != pubkeype[j]) {
							equals = false;
							break;
						}
					}
					if (!equals) {
						System.out.println("\n" + fname + "keypair is not match!");
						for (int j = 0; j < prvkey.length; j++)
							System.out.print(String.format("%02X", prvkey[j]));

						System.out.println();
						for (int j = 0; j < pubkey.length; j++)
							System.out.print(String.format("%02x", pubkey[j]));
						System.out.println();
						System.out.println("04" + pubkeyp.getX().toBigInteger().toString(16) + "" + pubkeyp.getY().toBigInteger().toString(16));
						byte[] priv2 = new byte[32];
						for (int j = 0; j < 31; j++)
							priv2[j + 1] = prvkey[j];
						priv2[0] = 0;
						BigInteger prvkeyint2 = new BigInteger(1, priv2);
						byte[] pubkey2 = gmg.multiply(prvkeyint2).getEncoded();
						for (int j = 0; j < pubkey2.length; j++)
							System.out.print(String.format("%02X", pubkey2[j]));
						equals = true;
						for (int j = 0; j < pubkey.length; j++) {
							if (pubkey[j] != pubkey2[j]) {
								equals = false;
								break;
							}
						}
						if (equals) {
							// try {
							// FileOutputStream fos = new FileOutputStream(
							// new File(fname));
							// fos.write(priv2);
							// fos.close();
							// } catch (Exception ef) {
							// ef.printStackTrace();
							// }
							System.out.println("\nis  padding error!");
						}
					} else {
						System.out.println(fname + " keypair is OK!");
					}
				}
			}

		}
	}
}
