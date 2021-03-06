package cn.com.infosec.netseal.common.util;

import java.math.BigInteger;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class StringUtil {

	/**
	 * 将指定字符串转换为整数类型, 若为空或者格式不正确,则使用默认值
	 * 
	 * @param sValue
	 * @param defaultValue
	 * @return
	 */
	public static int parseStringWithDefault(String sValue, int defaultValue) {
		if (sValue == null)
			return defaultValue;

		int v = defaultValue;
		try {
			v = Integer.parseInt(sValue.trim());
		} catch (Throwable e) {
		}
		return v;
	}

	public static String parseStringWithDefault(String sValue, String defaultValue) {
		if (sValue == null)
			return defaultValue;
		else
			return sValue.trim();
	}

	public static byte[] getBytes(String str) {
		byte[] bs = new byte[0];
		if (str != null)
			try {
				bs = str.getBytes(Constants.UTF_8);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.ENCODING_NOT_SUPPORT, e.getMessage());
			}
		return bs;
	}

	public static String base64Encode(String data) {
		try {
			return Base64.encode(data);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ENCODING_BASE64_ERROR, e.getMessage());
		}
	}

	public static String base64Encode(byte[] data) {
		try {
			return Base64.encode(data);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ENCODING_BASE64_ERROR, e.getMessage());
		}
	}

	public static byte[] base64Decode(String data) {
		try {
			return Base64.decode(data);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ENCODING_BASE64_ERROR, e.getMessage());
		}
	}

	public static byte[] base64Decode(byte[] data) {
		try {
			return Base64.decode(data);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ENCODING_BASE64_ERROR, e.getMessage());
		}
	}

	public static String getString(String str) {
		try {
			return new String(str.getBytes("iso8859-1"), Constants.UTF_8);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ENCODING_NOT_SUPPORT, e.getMessage());
		}
	}

	public static String getString(byte[] bs) {
		try {
			return new String(bs, Constants.UTF_8);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ENCODING_NOT_SUPPORT, e.getMessage());
		}
	}

	/**
	 * 去除字符串
	 * 
	 * @param src
	 * @param delStr
	 * @param addStr
	 * @return
	 */
	public static String getString(String src, String delStr, String addStr) {
		StringBuffer result = new StringBuffer();
		if (src == null)
			return result.toString();
		String[] tempArray = src.split(",");
		for (String tempStr : tempArray) {
			if (!tempStr.equals(delStr)) {
				result.append(tempStr).append(",");
			}
		}
		result.append(addStr);
		return result.toString();
	}

	/**
	 * 判断字符串为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() <= 0)
			return true;
		else
			return false;
	}

	/**
	 * 判断字符串不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		if (str != null && str.trim().length() > 0)
			return true;
		else
			return false;
	}

	/**
	 * 返回指定长度的数组, 不够左补0x00, 超过从左开始截取
	 * 
	 * @param bs
	 * @param len
	 * @return
	 */
	public static byte[] padLeft(byte[] bs, int len) {
		if (bs == null || len <= 0)
			return null;

		byte[] data = new byte[len];
		if (bs.length == data.length)
			return bs;

		if (bs.length < data.length) {
			System.arraycopy(bs, 0, data, data.length - bs.length, bs.length);
			return data;
		}

		System.arraycopy(bs, 0, data, 0, len);
		return data;
	}

	/**
	 * 返回指定长度的数组, 不够返回原始长度, 超过从左开始截取
	 * 
	 * @param bs
	 * @param len
	 * @return
	 */
	public static byte[] subLeft(byte[] bs, int len) {
		if (bs == null || len <= 0)
			return null;

		byte[] data = new byte[len];
		if (bs.length <= data.length)
			return bs;

		System.arraycopy(bs, 0, data, 0, len);
		return data;
	}

	/**
	 * 去掉补位
	 * 
	 * @param bs
	 * @return
	 */
	public static byte[] clearPad(byte[] bs) {
		return new BigInteger(bs).toByteArray();
	}

}
