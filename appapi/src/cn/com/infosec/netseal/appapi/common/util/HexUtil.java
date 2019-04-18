package cn.com.infosec.netseal.appapi.common.util;

public class HexUtil {

	public static byte[] hex2Byte(String hex) {
		byte[] bs = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length() / 2; i++) {
			String temp = hex.substring(i * 2, i * 2 + 2);
			bs[i] = Integer.valueOf(temp, 16).byteValue();
		}
		return bs;
	}

	public static String byte2Hex(byte[] bs) {
		String hex = "";
		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			String temp = Integer.toHexString(b & 0xff);
			if (temp.length() == 1)
				temp = "0" + temp;

			hex += temp;
		}
		return hex;
	}

	/**
	 * 整型占4个字节
	 * 
	 * @param bs
	 *            4个字节
	 * @return
	 */
	public static int byte2Int(byte[] bs) {
		int i = 0;
		i = i | bs[0] & 0xff;
		i = i << 8 | bs[1] & 0xff;
		i = i << 8 | bs[2] & 0xff;
		i = i << 8 | bs[3] & 0xff;

		return i;
	}

	public static byte[] int2Byte(int i) {
		byte[] bs = new byte[4];
		bs[3] = (byte) (i & 0xff);
		bs[2] = (byte) (i >>> 8 & 0xff);
		bs[1] = (byte) (i >>> 16 & 0xff);
		bs[0] = (byte) (i >>> 24 & 0xff);

		return bs;
	}

}
