package cn.com.infosec.netseal.common.algorithm.gmsm3;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GMOTPImp {

	public String genPUK(byte[] seed, String pur, int pukSize) {
		String ID = HexUtil.byte2Hex(pur.getBytes());
		while (ID.length() < 32)
			ID += "0";

		String F = HexUtil.byte2Hex(seed) + ID;

		byte[] msg = HexUtil.hex2Byte(F);
		byte[] hash = SM3.SM3Digest(msg);

		BigInteger bi = BigInteger.ZERO;
		for (int i = 0; i < 8; i++) {
			byte[] ii = new byte[4];
			System.arraycopy(hash, i * 4, ii, 0, 4);
			bi = bi.add(new BigInteger(1, ii));
		}

		BigInteger modv = (BigInteger.valueOf(2)).pow(32);
		BigInteger od = bi.mod(modv);

		int myod = od.mod(BigInteger.valueOf(10).pow(pukSize)).intValue();

		String result = new Integer(myod).toString();
		while (result.length() < pukSize)
			result = "0" + result;

		return result;
	}

	public byte[] genTik() throws Exception {
		SecureRandom randomGen = new SecureRandom();
		byte[] tik = new byte[32];
		randomGen.nextBytes(tik);
		return tik;
	}

	public byte[] genSeed(byte[] tik, String tac) {
		String F = HexUtil.byte2Hex(tik) + HexUtil.byte2Hex(tac.getBytes());
		byte[] msg = HexUtil.hex2Byte(F);
		byte[] hash = SM3.SM3Digest(msg);

		return hash;
	}

	public String genACC(byte[] tik, String tac, int accSize) {
		byte[] seed = genSeed(tik, tac);

		String ID = HexUtil.byte2Hex(tac.getBytes());
		while (ID.length() < 32)
			ID += "0";

		String F = HexUtil.byte2Hex(seed) + ID;
		byte[] msg = HexUtil.hex2Byte(F);
		byte[] hash = SM3.SM3Digest(msg);

		BigInteger bi = BigInteger.ZERO;
		for (int i = 0; i < 8; i++) {
			byte[] ii = new byte[4];
			System.arraycopy(hash, i * 4, ii, 0, 4);
			bi = bi.add(new BigInteger(1, ii));
		}

		BigInteger modv = (BigInteger.valueOf(2)).pow(32);
		BigInteger od = bi.mod(modv);
		int myod = od.mod(BigInteger.valueOf(10).pow(accSize)).intValue();

		String result = new Integer(myod).toString();
		while (result.length() < accSize)
			result = "0" + result;

		return result;
	}

	
	public String genChallengePassword(byte[] seed, String challenge, long timeFactor, byte[] eventFactor, int returnDigits) {
		// 时间转换
		byte[] bArray = new byte[8];
		for (int i = bArray.length - 1; i >= 0; i--) {
			bArray[i] = (byte) (timeFactor & 0xff);
			timeFactor >>= 8;
		}

		String timeHex = HexUtil.byte2Hex(bArray);
		while (timeHex.length() < 16)
			timeHex = "0" + timeHex;

		String result = null;
		String K = HexUtil.byte2Hex(seed);
		String T0 = timeHex;
		String C = "00000000";
		if (eventFactor != null)
			C = HexUtil.byte2Hex(eventFactor);

		String Q = challenge;
		String hexQ = HexUtil.byte2Hex(Q.getBytes());

		String ID = T0 + C + hexQ;
		while (ID.length() < 32)
			ID += "0";

		String F = K + ID;

		byte[] msg = HexUtil.hex2Byte(F);
		byte[] hash = SM3.SM3Digest(msg);

		BigInteger bi = BigInteger.ZERO;
		for (int i = 0; i < 8; i++) {
			byte[] ii = new byte[4];
			System.arraycopy(hash, i * 4, ii, 0, 4);
			bi = bi.add(new BigInteger(1, ii));
		}

		BigInteger modv = (BigInteger.valueOf(2)).pow(32);
		BigInteger od = bi.mod(modv);
		int myod = od.mod(BigInteger.valueOf(10).pow(returnDigits)).intValue();

		result = new Integer(myod).toString();
		while (result.length() < returnDigits)
			result = "0" + result;

		return result;
	}

	/**
	 * 普通时间型口令	 
	 * @param seed
	 * @param timeFactor
	 * @param eventFactor
	 * @param returnDigits
	 * @return
	 */
	public String genTimePassword(byte[] seed, long timeFactor, byte[] eventFactor, int returnDigits) {
		// 时间转换
		byte[] bArray = new byte[8];
		for (int i = bArray.length - 1; i >= 0; i--) {
			bArray[i] = (byte) (timeFactor & 0xff);
			timeFactor >>= 8;
		}

		String timeHex = HexUtil.byte2Hex(bArray);

		while (timeHex.length() < 16)
			timeHex = "0" + timeHex;

		String result = null;
		String K = HexUtil.byte2Hex(seed);
		String T0 = timeHex;

		String C = "";
		if (eventFactor != null)
			C = HexUtil.byte2Hex(eventFactor);

		String ID = T0 + C;
		while (ID.length() < 32)
			ID += "0";

		String F = K + ID;
		byte[] msg = HexUtil.hex2Byte(F);
		byte[] hash = SM3.SM3Digest(msg);

		BigInteger bi = BigInteger.ZERO;
		for (int i = 0; i < 8; i++) {
			byte[] ii = new byte[4];
			System.arraycopy(hash, i * 4, ii, 0, 4);
			bi = bi.add(new BigInteger(1, ii));
		}

		BigInteger modv = (BigInteger.valueOf(2)).pow(32);
		BigInteger od = bi.mod(modv);
		int myod = od.mod(BigInteger.valueOf(10).pow(returnDigits)).intValue();

		result = new Integer(myod).toString();
		while (result.length() < returnDigits)
			result = "0" + result;

		return result;
	}

}
