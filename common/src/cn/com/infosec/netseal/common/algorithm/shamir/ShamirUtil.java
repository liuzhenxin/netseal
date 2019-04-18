package cn.com.infosec.netseal.common.algorithm.shamir;

import java.math.BigInteger;
import java.util.ArrayList;

import cn.com.infosec.netcert.util.shamir.InfosecShamirSplit;
import cn.com.infosec.util.Base64;

public class ShamirUtil {
	private static int BlockSize = 32;

	/**
	 * 分割秘密
	 * 
	 * @param data
	 *            被分割的内容
	 * @param shamirM
	 *            最少恢复数
	 * @param shamirN
	 *            片断总数
	 * @return
	 * @throws Exception
	 */
	public static String[] generate(byte[] data, int shamirM, int shamirN) throws Exception {

		byte[] partBytes = null, restBytes = null;
		String restBase64 = null;
		String[] rslt = new String[shamirN];

		if (data.length > BlockSize) {
			partBytes = new byte[BlockSize];
			System.arraycopy(data, 0, partBytes, 0, partBytes.length);

			restBytes = new byte[data.length - BlockSize];
			System.arraycopy(data, BlockSize, restBytes, 0, restBytes.length);
		} else {
			partBytes = data;
		}

		byte longkek[] = new byte[partBytes.length + 1];
		longkek[0] = 1;
		for (int i = 1; i < longkek.length; i++) {
			longkek[i] = partBytes[i - 1];
		}
		BigInteger bigkek = new BigInteger(longkek);
		byte[][] nkek = new byte[shamirN][];
		InfosecShamirSplit.numBits = partBytes.length * 8;
		nkek = InfosecShamirSplit.InfosecShare(bigkek, shamirN, shamirM);

		if (restBytes != null && restBytes.length > 0)
			restBase64 = Base64.encode(restBytes);
		for (int i = 0; i < nkek.length; i++) {
			if (restBase64 != null) {
				rslt[i] = Base64.encode(nkek[i]) + "," + restBase64;
			} else {
				rslt[i] = Base64.encode(nkek[i]);
			}
		}
		return rslt;
	}

	/**
	 * 恢复被分割的内容
	 * 
	 * @param ss
	 *            片断的base64编码
	 * @return 被恢复的数据
	 */
	public static byte[] recover(String[] ss) throws Exception {
		boolean hasGotRest = false;
		byte[] rest = null, secret = null;
		ArrayList<byte[]> inputs = new ArrayList<byte[]>();

		for (String s : ss) {
			String[] tmp = s.split(",");
			if (tmp.length == 2 && !hasGotRest) {
				rest = Base64.decode(tmp[1]);
				hasGotRest = true;
			}
			inputs.add(Base64.decode(tmp[0]));
		}

		byte[][] tmp = new byte[inputs.size()][];
		for (int i = 0; i < inputs.size(); i++)
			tmp[i] = (byte[]) inputs.get(i);
		InfosecShamirSplit.numBits = BlockSize * 8;
		BigInteger bigkek = InfosecShamirSplit.InfosecRecover(tmp);
		byte[] longkek = bigkek.toByteArray();
		byte[] partKek = new byte[longkek.length - 1];
		for (int i = 1; i < longkek.length; i++) {
			partKek[i - 1] = longkek[i];
		}

		if (rest != null) {
			secret = new byte[partKek.length + rest.length];
			System.arraycopy(partKek, 0, secret, 0, partKek.length);
			System.arraycopy(rest, 0, secret, partKek.length, rest.length);
		} else {
			secret = new byte[partKek.length];
			System.arraycopy(partKek, 0, secret, 0, partKek.length);
		}

		return secret;
	}

	public static void main(String[] args) throws Exception {
		byte[] data = "1234567890123456".getBytes();
		String[] strs = ShamirUtil.generate(data, 2, 4);
		for (int i = 0; i < strs.length; i++) {
			System.out.println(strs[i]);
		}

		String[] strs1 = new String[2];
		strs1[0] = strs[0];
		strs1[1] = strs[1];

		byte[] plain = ShamirUtil.recover(strs1);
		System.out.println(new String(plain));
	}
}
