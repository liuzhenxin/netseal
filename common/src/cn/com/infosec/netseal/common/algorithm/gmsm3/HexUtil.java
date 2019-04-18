package cn.com.infosec.netseal.common.algorithm.gmsm3;

public class HexUtil {
	public static byte[] hex2Byte(String hex) {
		byte[] bs = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length() / 2; ++i) {
			String temp = hex.substring(i * 2, i * 2 + 2);
			bs[i] = Integer.valueOf(temp, 16).byteValue();
		}
		return bs;
	}

	public static String byte2Hex(byte[] bs) {
		String hex = "";
		for (int i = 0; i < bs.length; ++i) {
			byte b = bs[i];
			String temp = Integer.toHexString(b & 0xFF);
			if (temp.length() == 1)
				temp = "0" + temp;

			hex = hex + temp;
		}
		return hex;
	}

	public static int byte2Int(byte[] bs) {
		int i = 0;
		i |= bs[0] & 0xFF;
		i = i << 8 | bs[1] & 0xFF;
		i = i << 8 | bs[2] & 0xFF;
		i = i << 8 | bs[3] & 0xFF;

		return i;
	}

	public static byte[] int2Byte(int i) {
		byte[] bs = new byte[4];
		bs[3] = (byte) (i & 0xFF);
		bs[2] = (byte) (i >>> 8 & 0xFF);
		bs[1] = (byte) (i >>> 16 & 0xFF);
		bs[0] = (byte) (i >>> 24 & 0xFF);

		return bs;
	}

	public static void main(String[] args) {
		byte[] bs = { 16, 32, 48, 64 };

		int i = byte2Int(bs);

		System.out.println(i);
		System.out.println(Integer.toBinaryString(i));

		bs = int2Byte(i);
		System.out.println(Integer.toBinaryString(bs[0] & 0xFF));
		System.out.println(Integer.toBinaryString(bs[1] & 0xFF));
		System.out.println(Integer.toBinaryString(bs[2] & 0xFF));
		System.out.println(Integer.toBinaryString(bs[3] & 0xFF));
	}
}