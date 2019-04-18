package cn.com.infosec.netseal.common.algorithm.gm;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

import cn.com.infosec.asn1.ASN1EncodableVector;
import cn.com.infosec.asn1.ASN1InputStream;
import cn.com.infosec.asn1.ASN1Sequence;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERSequence;
import cn.com.infosec.math.ec.ECPoint;
import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.util.BigIntegers;

/**
 * @author wangzb
 * 
 */
public class SM2Impl {

	public static final int SM2_SIZE = SM2GMParameter.SM2_SIZE;
	// private static final BigInteger gmp = SM2GMParameter.gmp;
	// private static final BigInteger gma = SM2GMParameter.gma;
	// private static final BigInteger gmb = SM2GMParameter.gmb;
	// private static final ECCurve gmec256 = SM2GMParameter.gmec256;
	// private static final BigInteger gmgx = SM2GMParameter.gmgx;
	// private static final BigInteger gmgy = SM2GMParameter.gmgy;
	private static final ECPoint gmg = SM2GMParameter.gmg;
	private static final BigInteger gmn = SM2GMParameter.gmn;

	/* SM2 Testing vector */
	// private static final int SM2_SIZE = SM2TestParameter.SM2_SIZE;
	// private static final BigInteger gmp = SM2TestParameter.gmp;
	// private static final BigInteger gma = SM2TestParameter.gma;
	// private static final BigInteger gmb = SM2TestParameter.gmb;
	// private static final ECCurve gmec256 = SM2TestParameter.gmec256;
	// private static final BigInteger gmgx = SM2TestParameter.gmgx;
	// private static final BigInteger gmgy = SM2TestParameter.gmgy;
	// private static final ECPoint gmg = SM2TestParameter.gmg;
	// private static final BigInteger gmn = SM2TestParameter.gmn;

	private final SecureRandom random = getRandom();

	private SecureRandom getRandom() {
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (Exception exp) {
			random = new SecureRandom();
		}
		return random;
	}

	private static ECPoint kmg(ECPoint p, BigInteger k) {

		return p.multiply(k);
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

			// System.out.println("KG:\n" + kg.toString() + "\n" + kg2.toString());
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

	/**
	 * 产生SM2密钥对函数 输入参数： 无 输出参数：
	 * 
	 * @param privkey
	 *            -- 私钥，长度为32的byte数组
	 * 
	 * @param pubKey
	 *            -- * 公钥，长度为65的byte数组 返回值: 无
	 */

	public void genKeyPair(byte[] privkey, byte[] pubkey) {
		BigInteger k = null;
		int nBitLength = gmn.bitLength();
		ECPoint kg;
		do {
			k = new BigInteger(nBitLength, random);
			k = k.mod(gmn);

			kg = gmg.multiply(k);
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

	public byte[] signHash(final byte[] msg_hash, final byte[] privkey) {
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

				ECPoint p = kmg(gmg, k); // old: gmg.multiply(k);//

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
	private boolean verifyHash(byte[] msg_hash, byte[] r, byte[] s, byte[] x, byte[] y) {
		byte[] signature = new byte[2 * SM2_SIZE];
		System.arraycopy(r, 0, signature, 0, SM2_SIZE);
		System.arraycopy(s, 0, signature, SM2_SIZE, SM2_SIZE);
		byte[] pubkey = new byte[1 + 2 * SM2_SIZE];
		pubkey[0] = 4;
		System.arraycopy(x, 0, pubkey, 1, SM2_SIZE);
		System.arraycopy(y, 0, pubkey, 1 + SM2_SIZE, SM2_SIZE);

		return verifyHash(msg_hash, signature, pubkey);
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
	public boolean verifyHash(final byte[] msg_hash, final byte[] signature, final byte[] pubkey) {
		// long start;
		// start = System.nanoTime();
		if (msg_hash.length != SM2_SIZE) {
			// System.out.println("msg_hash length is wrong!");
			return false;
		}
		/*
		 * 2013-12-17 签名结构长度判断条件有错误，应允许各种长度的签名结果，并废止以前的采用椭圆曲线点表示的签名结果
		 */
		/*
		 * 2013-12-17 if ((signature.length != 2 * SM2_SIZE) && (signature.length != 1 + 2 * SM2_SIZE) && (!(signature.length >= 5 + 2 * SM2_SIZE))) { // System.out.println(
		 * "signatuer length is wrong!\nsignature length=" // + signature.length); return false; }
		 */
		if (signature[1] != signature[3] + signature[signature[3] + 5] + 4)
			return false;
		if ((pubkey.length != 2 * SM2_SIZE) && (pubkey.length != (1 + 2 * SM2_SIZE))) {
			// System.out.println("public key length is wrong!");
			return false;
		}
		BigInteger e = new BigInteger(1, msg_hash);
		BigInteger r = null, s = null;
		/*
		 * 2013-12-17 if (signature.length >= 5 + 2 * SM2_SIZE) {
		 */
		byte[] x = new byte[32];
		byte[] y = new byte[32];
		sign2xy(signature, x, y);
		r = new BigInteger(1, x);
		s = new BigInteger(1, y);

		/*
		 * 2013-12-17 } else { byte[] signpart = new byte[SM2_SIZE]; // simple transfer signature to two BigIntegers, not support // compressed // point presentation int pos = 0; if (signature[0] == 4
		 * && signature.length == SM2_SIZE * 2 + 1) pos = 1;
		 * 
		 * System.arraycopy(signature, pos, signpart, 0, SM2_SIZE); r = new BigInteger(1, signpart); System.arraycopy(signature, pos + SM2_SIZE, signpart, 0, SM2_SIZE); s = new BigInteger(1,
		 * signpart); }
		 */
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

		ECPoint Q = SM2Util.octect2point(pubkey);

		ECPoint xy1 = gmg.multiply(s).add(Q.multiply(t));

		// R=(e'+x1') mod n
		BigInteger v = xy1.getX().toBigInteger().add(e).mod(gmn);
		// System.out.println("---3 elapsed " + (System.nanoTime() - start));

		return (v.compareTo(r) == 0);
	}

	/**
	 * @param msg
	 *            明文数据
	 * @param pubkey
	 *            加密公钥
	 * @return 密文数据，GM-0009格式规范
	 */
	public byte[] encrypt(final byte[] msg, final byte[] pubkey) throws IOException {
		ASN1EncodableVector derVec = new ASN1EncodableVector();

		BigInteger k = null;
		// byte[] c = new byte[97 + msg.length];
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
			kg = gmg.multiply(k);
			pbk = SM2Util.octect2point(pubkey);
			// A4
			pbk = pbk.multiply(k);
		} while (pbk.isInfinity());

		byte[] tmpib = BigIntegers.asUnsignedByteArray(kg.getX().toBigInteger());
		// c[0] = (byte) 04;
		// System.arraycopy(tmpib, 0, c, 33 - tmpib.length, tmpib.length);
		DERInteger kx;
		if ((tmpib[0] & 0x80) == 0x80) { // 首字节大于0x80时，增加符号字节0
			byte[] tmp = new byte[tmpib.length + 1];
			tmp[0] = 0;
			System.arraycopy(tmpib, 0, tmp, 1, tmpib.length);
			kx = new DERInteger(tmp);
		} else {
			kx = new DERInteger(tmpib);
		}

		tmpib = BigIntegers.asUnsignedByteArray(kg.getY().toBigInteger());
		// System.arraycopy(tmpib, 0, c, 65 - tmpib.length, tmpib.length);
		DERInteger ky;
		if ((tmpib[0] & 0x80) == 0x80) { // 首字节大于0x80时，增加符号字节0
			byte[] tmp = new byte[tmpib.length + 1];
			tmp[0] = 0;
			System.arraycopy(tmpib, 0, tmp, 1, tmpib.length);
			ky = new DERInteger(tmp);
		} else {
			ky = new DERInteger(tmpib);
		}

		byte[] xy2 = new byte[65];// point2octect(pbk);
		xy2[0] = (byte) 4;
		tmpib = BigIntegers.asUnsignedByteArray(pbk.getX().toBigInteger());
		System.arraycopy(tmpib, 0, xy2, 33 - tmpib.length, tmpib.length);
		tmpib = BigIntegers.asUnsignedByteArray(pbk.getY().toBigInteger());
		System.arraycopy(tmpib, 0, xy2, 65 - tmpib.length, tmpib.length);

		byte[] hash = new byte[32];
		SM3Digest md = new SM3Digest();
		md.update(ByteBuffer.allocate(32).put(xy2, 1, 32).array());// (x2);
		md.update(msg);
		md.update(ByteBuffer.allocate(32).put(xy2, 33, 32).array());// (y2);
		md.digest(hash);
		DEROctetString derHash = new DEROctetString(hash);

		byte[] t = kdf(ByteBuffer.allocate(64).put(xy2, 1, 64).array(), msg.length);// kdf(x2y2, msg.length);
		byte[] cipherText = new byte[msg.length]; // 密文
		for (int i = 0; i < msg.length; i++)
			cipherText[i] = (byte) (msg[i] ^ t[i]);
		DEROctetString derCipher = new DEROctetString(cipherText);

		derVec.add(kx);
		derVec.add(ky);
		derVec.add(derHash);
		derVec.add(derCipher);
		DERSequence seq = new DERSequence(derVec);
		byte[] c = seq.getEncoded();

		return c;
	}

	/**
	 * @param ct
	 *            密文数据，GM-0009格式规范
	 * @param privkey
	 *            私钥
	 * @return 正确解密返回明文,否则返回null
	 */
	public byte[] decrypt(final byte[] ct, final byte[] privkey) throws IOException {
		// check the length of parameters
		// if (ct.length < 98 || privkey.length != SM2_SIZE) {
		// return false;
		// }
		ASN1InputStream in = new ASN1InputStream(ct);
		ASN1Sequence seq = (ASN1Sequence) in.readObject();
		if (seq.size() != 4)
			return null;
		byte[] kx = ((DERInteger) seq.getObjectAt(0)).getValue().toByteArray();
		byte[] ky = ((DERInteger) seq.getObjectAt(1)).getValue().toByteArray();
		byte[] hash = ((DEROctetString) seq.getObjectAt(2)).getOctets();
		byte[] cipherText = ((DEROctetString) seq.getObjectAt(3)).getOctets();

		int ctl = cipherText.length;

		// int ctl = ct.length - 97;
		//
		// byte[] c2 = ByteBuffer.allocate(ctl).put(ct, 97, ctl).array();// new
		// // byte[ctl];
		// byte[] c3 = ByteBuffer.allocate(32).put(ct, 65, 32).array();// new
		// byte[SM2_SIZE];

		// B1 split c1 c3 c2

		// ECPoint c1p = SM2Util.octect2point(ByteBuffer.allocate(65).put(ct, 0, 65).array());// (c1);
		byte[] c1 = new byte[65];
		c1[0] = 4;
		if (kx.length <= 32)
			System.arraycopy(kx, 0, c1, 1 + (32 - kx.length), kx.length);
		else if (kx.length == 33 && kx[0] == 0) // 如果数据的第一个字节大于0x80，会在前面加0x0；这里要去掉；
			System.arraycopy(kx, 1, c1, 1, 32);
		else
			throw new IOException("Invalid point X");

		if (ky.length <= 32)
			System.arraycopy(ky, 0, c1, 1 + 32 + (32 - ky.length), ky.length);
		else if (ky.length == 33 && ky[0] == 0)
			System.arraycopy(ky, 1, c1, 1 + 32, 32);
		else
			throw new IOException("Invalid point Y");

		System.out.println(HexUtil.byte2Hex(c1));

		ECPoint c1p = SM2Util.octect2point(c1);// (c1);
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
		byte[] pt = new byte[ctl];
		for (int i = 0; i < ctl; i++) {
			pt[i] = (byte) (cipherText[i] ^ t[i]);
		}

		System.out.println(HexUtil.byte2Hex(xy));

		SM3Digest md = new SM3Digest();
		md.update(ByteBuffer.allocate(32).put(xy, 0, 32).array());
		md.update(pt);
		md.update(ByteBuffer.allocate(32).put(xy, 32, 32).array());
		byte[] u = new byte[SM3Digest.DIGEST_LENGTH];
		md.digest(u);
		// B6
		for (int i = 0; i < SM2_SIZE; i++) {
			if ((u[i] & 0xFF) != (hash[i] & 0xFF)) {
				return null;
			}
		}

		return pt;
	}

	/**
	 * @param id
	 *            签名者标识
	 * @param message
	 *            签名原文数据
	 * @param privkey
	 *            私钥
	 * @param pubkey
	 *            公钥
	 * @return 签名结果
	 * 
	 *         public static byte[] signgm(byte[] id, byte[] message, byte[] privkey, byte[] pubkey) { // ZA=H256(ENTLA||IDA||a|| b|| xG||yG||xA||yA) byte[] digest = new byte[SM3Digest.DIGEST_LENGTH];
	 *         SM3Digest sm3 = new SM3Digest(); sm3.update(SM2Util.getZA(id, pubkey));// ZA sm3.update(message); sm3.digest(digest); return signHash(digest, privkey); }
	 */

	/**
	 * @param id
	 *            签名者标识
	 * @param message
	 *            原文信息
	 * @param signature
	 *            签名结果
	 * @param pubkey
	 *            签名者公钥
	 * @return true 签名正确 false 签名错误
	 * 
	 *         public static boolean verifygm(byte[] id, byte[] message, byte[] signature, byte[] pubkey) { byte[] digest = new byte[SM3Digest.DIGEST_LENGTH]; SM3Digest sm3 = new SM3Digest();
	 *         sm3.update(SM2Util.getZA(id, pubkey));// ZA sm3.update(message); sm3.digest(digest); return verifyHash(digest, signature, pubkey); }
	 */

	/**
	 * @param id
	 *            签名者标识
	 * @param message
	 *            原文信息
	 * @param r
	 *            签名结果中的R
	 * @param s
	 *            签名结果中的S
	 * @param x
	 *            签名者公钥x坐标
	 * @param y
	 *            签名者公钥y坐标
	 * @return true 签名正确 false 签名错误
	 * 
	 *         public static boolean verifygm(byte[] id, byte[] message, byte[] r, byte[] s, byte[] x, byte[] y) { byte[] digest = new byte[SM3Digest.DIGEST_LENGTH]; byte[] pubkey = new byte[1 + 2 *
	 *         SM2_SIZE]; pubkey[0] = 4; System.arraycopy(x, 0, pubkey, 1, SM2_SIZE); System.arraycopy(y, 0, pubkey, 1 + SM2_SIZE, SM2_SIZE); SM3Digest sm3 = new SM3Digest();
	 *         sm3.update(SM2Util.getZA(id, pubkey));// ZA sm3.update(message); sm3.digest(digest); return verifyHash(digest, r, s, x, y); }
	 */

	/**
	 * the function of key derived function, generating the key from key material, the length of key is keySize
	 * 
	 * @param x2y2
	 *            key material
	 * @param keySize
	 *            the length of target key
	 * @return target key
	 */

	private byte[] kdf(final byte[] x2y2, final int keySize) {
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

	private ECPoint implShamirsTrick(JPECPoint P, BigInteger k, JPECPoint Q, BigInteger l) {
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

	private void sign2xy(byte[] sign, byte[] x, byte[] y) {
		int x_length, y_length;

		x_length = sign[3];
		y_length = sign[x_length + 5];
		if (x_length > 32)
			System.arraycopy(sign, 4 + x_length - 32, x, 0, SM2_SIZE);
		else
			System.arraycopy(sign, 4, x, 32 - x_length, x_length);
		if (y_length > 32)
			System.arraycopy(sign, 6 + x_length + y_length - 32, y, 0, SM2_SIZE);
		else
			System.arraycopy(sign, 6 + x_length, y, 32 - y_length, y_length);
	}

	private byte[] xy2sign(byte[] x, byte[] y) {
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

	public boolean checkKeyPair(byte[] privKey, byte[] pubKeyX, byte[] pubKeyY) {
		if ((0xff & privKey[0]) > 0x79) {
			byte[] tmp = new byte[privKey.length + 1];
			tmp[0] = 0;
			System.arraycopy(privKey, 0, tmp, 1, privKey.length);
			privKey = tmp;
		}

		BigInteger k = new BigInteger(privKey);
		ECPoint kg = gmg.multiply(k);
		byte[] x = new byte[32];
		byte[] y = new byte[32];
		byte[] temp = BigIntegers.asUnsignedByteArray(kg.getX().toBigInteger());
		System.arraycopy(temp, 0, x, Math.max(0, SM2_SIZE - temp.length), Math.min(temp.length, SM2_SIZE));
		// ConsoleLogger.logBinary( "dx" , x ) ;
		if (!Arrays.equals(x, pubKeyX))
			return false;
		temp = BigIntegers.asUnsignedByteArray(kg.getY().toBigInteger());
		System.arraycopy(temp, 0, y, Math.max(0, SM2_SIZE - temp.length), Math.min(temp.length, SM2_SIZE));
		// ConsoleLogger.logBinary( "dy" , y ) ;
		if (!Arrays.equals(y, pubKeyY))
			return false;
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// // System.loadLibrary("libeay32");
		// // System.loadLibrary("libGM_in_native");
		//
		// byte[] privKey = new byte[SM2_SIZE];
		// byte[] pubKey = new byte[65];
		// byte[] msg_hash = new byte[SM2_SIZE];
		// String str = "This is string to sign";
		// byte[] sign = new byte[72];
		// boolean result = false;
		// long start, end;
		// int max = 1;
		// SM3Digest sm3 = null;
		SM2Impl impl = new SM2Impl();
		//
		// // impl.genKeyPair(privKey, pubKey);
		// // System.out.println("pri: " + Base64.encode(privKey)+"\npuk: "+Base64.encode(pubKey));
		// privKey = Base64.decode("/TyA5WU3zlxBH5CL/pGTQL+KAfnSRg7Hr3lSf0HvU3A=");
		// pubKey = Base64.decode("BO7F6QS0Dry5YLJeejYTqekoefYtAVtpb0oDyRoJjQVAkCQFD87dibMfPopzwBUCHd+8mc4WX7JEcbkMjV3zKsE=");
		// System.out.println(HexUtil.byte2Hex(pubKey));
		//
		// for (int i = 0; i < 32; i++)
		// msg_hash[i] = (byte) (privKey[i] ^ pubKey[i + 1]);
		// start = System.currentTimeMillis();
		// for (int i = 0; i < max; i++) {
		// sm3 = new SM3Digest();
		// sm3.update(str.getBytes());
		// sm3.digest(msg_hash);
		// sign = impl.signHash(msg_hash, privKey);
		// }
		// end = System.currentTimeMillis();
		// System.out.println(max + " sm2sign operation(s) has elapsed " + (end - start) + " ms\n or " + (max * 1000.0 / (end - start)) + " ops");
		//
		// start = System.currentTimeMillis();
		// for (int i = 0; i < max; i++) {
		// sm3 = new SM3Digest();
		// sm3.update(str.getBytes());
		// sm3.digest(msg_hash);
		// result = impl.verifyHash(msg_hash, sign, pubKey);
		// }
		// end = System.currentTimeMillis();
		// System.out.println(max + " sm2verify operation(s) has elapsed " + (end - start) + " ms\n or " + (max * 1000.0 / (end - start)) + " ops");
		// if (!result)
		// System.out.println("SM2 Verification failed!");
		// else
		// System.out.println("SM2 verification is OK!");
		//
		// // test enc
		// start = System.currentTimeMillis();
		// byte[] ct = impl.encrypt(str.getBytes(), pubKey);
		// FileUtil.storeFile("f:/temp/ct.asn1", ct);
		// end = System.currentTimeMillis();
		// System.out.println("ct= " + Base64.encode(ct));
		// System.out.println(max + " sm2 encrypt operation(s) has elapsed " + (end - start) + " ms");
		//
		// // FileInputStream in = new FileInputStream("d:/tmp/sm2c/cipher.txt");
		// // byte[] ct = new byte[in.available()];
		// // in.read(ct);
		// // in.close();
		// // privKey = new byte[]{0x6C, (byte)0xC7, 0x65, 0x0D, 0x7D, 0x53, 0x64, (byte)0x8D, (byte)0x8F, 0x6D, (byte)0xFA, 0x33, 0x44, 0x07, (byte)0xA1, (byte)0xAD, 0x69, (byte)0xC9, 0x0F, 0x62,
		// 0x67,
		// // (byte)0x8C, 0x47, (byte)0xC7, (byte)0xAF, (byte)0xEE, (byte)0xEF, (byte)0xCD, 0x02, (byte)0xD2, 0x57, 0x65};
		// start = System.currentTimeMillis();
		// byte[] pt = impl.decrypt(ct, privKey);
		// // byte[] pt = (new SM2Impl()).decrypt(ct, privKey);
		// end = System.currentTimeMillis();
		// System.out.println(max + " sm2 dencrypt operation(s) has elapsed " + (end - start) + " ms");
		//
		// if (pt == null || pt.length == 0)
		// System.out.println("SM2 encryption/decryption failed!");
		// else
		// System.out.println("SM2 encryption/decryption is OK!\n" + (new String(pt)));
		//
		// // impl = new SM2Impl();
		// // ct = HexUtil.hex2Byte(
		// //
		// "307902210090957c1d132ee114b1f40301ed8a69a630af95d43e64b43e50e8ab443b8d70830220201ae0d3919c54693b7886d4a6d0865352abc51404b1a2a672cdf3b448fcebae0420253f1478337f747bb76dd22fe710c94252c67c790e1591b5d9ad3213151031d00410305c0e07722e681fbbec5916e084b385");
		// // byte[] plain = impl.decrypt(ct, HexUtil.hex2Byte("3382d6f5fd84d6d41ad72a320ecae9a9b82d6b4de9d4b1a936c4ceebd7dc7800"));
		// // System.out.println(HexUtil.byte2Hex(plain));

		impl = new SM2Impl();
		// byte[] keyEnc = impl.encrypt(HexUtil.hex2Byte("73461a6b914ab5c4da85c72c5b918a91"),
		// HexUtil.hex2Byte("040f59063032200dacb90deaa6f9ec7db23fcb06627a11b956e203ffb87bdfb20a280d0836ab09caa454f86a2295bd0df0b89fe10b8b4d3e1bfc5f3de6826b7fb7"));
		// FileUtil.storeFile("f:/temp/keyEnc.asn1", keyEnc);

		byte[] hash = new byte[32];
		SM3Digest md = new SM3Digest();
		md.update(HexUtil.hex2Byte("421199711697510104716d4fc089a40800ca18b4e76713521305fb57e537377c"));// (x2);
		md.update(HexUtil.hex2Byte("73461a6b914ab5c4da85c72c5b918a91"));
		md.update(HexUtil.hex2Byte("2b6ceec5a96b158f68e87665b8a3e2c36ef83e465df76bada831e9c1c0f277f2"));// (y2);
		md.digest(hash);
		System.out.println(HexUtil.byte2Hex(hash));
	}

}
