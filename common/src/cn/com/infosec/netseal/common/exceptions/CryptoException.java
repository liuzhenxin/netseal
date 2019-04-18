package cn.com.infosec.netseal.common.exceptions;

public class CryptoException extends Exception {

	public CryptoException() {
	}

	public CryptoException(String msg) {
		super(msg);
	}

	public CryptoException(short code, String message) {
		super("Error NO :" + code + "Message:" + message);
		this.code = code;
	}

	/** @serial */
	public short code;

	/**
	 * 空参数
	 */
	public static final short NULL_PARAMETER = 1;

	/**
	 * 不支持此操作
	 */
	public static final short UNSUPPORTED_OPERATION = 2;

	/**
	 * 对称算法加密数据时发生错误
	 */
	public static final short SYMMETRY_ENCRYPT_ERROR = 10;
	/**
	 * 对称算法解密数据时发生错误
	 */
	public static final short SYMMETRY_DECRYPT_ERROR = 11;
	/**
	 * 对数据签名时发生错误
	 */
	public static final short SIGN_ERROR = 12;

	/**
	 * 使用PKCS7标准对数据签名时发生错误
	 */
	public static final short PKCS7_SIGN_ERROR = 13;
	/**
	 * 创建数字信封时发生错误
	 */
	public static final short KMC_ENCAPSULATE_ERROR = 14;
	/**
	 * 使用DER标准对数据签名时发生错误
	 */
	public static final short DER_ENCAPSULATE_ERROR = 15;

	/**
	 * 将字节数组转换为私钥对象时发生错误
	 */

	public static final short CONVERSION_PRIVATEKEY_ERROR = 16;
	/**
	 * 将字节数组转换为公钥对象时发生错误
	 */

	public static final short CONVERSION_PUBLICKEY_ERROR = 17;
	/**
	 * 创建散列函数时发生错误
	 */
	public static final short HASH_ERROR = 18;
	/**
	 * 创建RSA密钥对时发生错误
	 */
	public static final short GNGENDER_RSA_KEYPAIR_ERROR = 19;
	/**
	 * 创建对称密钥时发生错误
	 */
	public static final short GNGENDER_SECRET_KEY_ERROR = 20;

	/**
	 * 应用RSA公钥加密对称密钥时发生错误
	 */
	public static final short ENCRYPT_SECRETKEY_ERROR = 21;
	/**
	 * 对数据签名验证时发生错误
	 */
	public static final short VERITY_ERROR = 22;
	/**
	 * 将字节数组转换为X509证书对象时发生错误
	 */
	public static final short CONVERSION_X509CERTIFICATE_ERROR = 23;
	/**
	 * 不支持此算法
	 */
	public static final short UNSUPPORTED_ALGORITHMIC = 50;

	/**
	 * 不能得到证书链
	 */
	public static final short CANNOT_GET_CERTCHAIN_ERROR = 24;

}