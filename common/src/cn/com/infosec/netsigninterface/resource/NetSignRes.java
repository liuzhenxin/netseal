package cn.com.infosec.netsigninterface.resource;

/**
 * <p>
 * Title:NetSignRes
 * </p>
 * <p>
 * Description: 描述相关的资源说明
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:infosec
 * </p>
 * 
 * @author huangyong
 * @version 1.0
 */

public class NetSignRes {

	public void NetSignRes() {
	}

	// 版本
	public static final String PRODUCT_VERSION = "NetSignAPI(JAVA) v1.8.100.1 Build201309121600";

	// 下载CRL出现的异常
	public static final String INCORRECT_LDAP_HOST = "LDAP服务器地址错误";

	public static final String INCORRECT_LDAP_PORT = "LDAP端口错误";

	public static final String CANNOT_CONNECTTO_LDAP = "不能连接到LDAP";

	public static final String CRL_PATH_ERROR = "下载CRL过程中出现错误,请检查保存CRL的目录是否存在";

	public static final String BASEDN_NOT_SET = "没有设置下载CRL的BaseDN";

	public static final String SEARCHFILTER_NOT_SET = "没有设置下载CRL的过滤条件";

	public static final String PROPERTYNAME_NOT_SET = "没有设置下载CRL的属性名";

	public static final String PATH_NOT_SET = "没有下载CRL的存放目录";

	public static final String PATH_NOT_FOUND = "下载CRL的存放目录设置错误，可能是目录不存在";

	public static final String SEARCHCRL_EXCEPTION = "搜索CRL错误";

	public static final String INTEVAL_NOT_SET = "没有设置CRL下载和更新CRL列表的时间间隔";

	public static final String INVALID_INTERVAL = "CRL下载时间间隔设置错误，应该为大于零的数值（秒）";

	public static final String CANNOT_START_DOWNTHREAD = "CRL下载线程没有启动";

	public static final String CRLFILE_READONLY = "CRL文件只读,无法更新";

	public static final String CANNOT_READ_CRL_DIRECTORY = "不能读取指定的CRL的目录";

	public static final String CANNOT_WRITE_CRL_DIRECTORY = "不能写入指定存放CRL的目录";

	// 设置系统属性出现的异常
	public static final String INPUTSTREAM_OF_PFX_IS_NULL = "用来产生keystore的 pfx 输入流不能为空";

	public static final String PFX_PRIKEY_PASSWORD_IS_NULL = "用来产生keystore的私钥保护口令不能为空";

	public static final String SERVERKEYSTORES_IS_NULL = "在设置系统属性时,KeyStore对象数组不能为空";

	public static final String TRUSTCERTS_IS_NULL = "在设置系统属性时,信任的证书列表不能为空";

	public static final String INVALID_TRUSTCERTS_PATH = "信任证书路径错误";

	// 签名、验签名出现的异常
	public static final String PLAINDATA_IS_NULL = "明文数据不能为空";

	public static final String P7DATA_IS_NULL = "签名数据不能为空";

	public static final String PLAINDATATOSIGNISNULL = "待签名数据为空";

	public static final String NO_PLAIN_IN_P7DATA = "PKCS7数据包中没有签名原文";

	public static final String NETSIGN_VERIFY_ERROR = "验签名未通过";

	public static final String DECRYPT_DN_IS_NULL = "解密数字信封的证书DN不能为空";

	public static final String P7ENVDATA_IS_NULL = "数字信封数据为空";

	public static final String SERVERKEYSTORE_FOR_DN_NOTFOUND = "DN对应的证书没有找到";

	public static final String DN_FOR_SIGN_IS_NULL = "进行服务器端签名的证书DN不能为空";

	public static final String CERTIFICATE_DATA_IS_NULL = "制作数字信封的证书不能为空";

	public static final String PRIVATEKEY_NOT_FOUND = "不能在ServerKeystore中找到私钥";

	public static final String CERTIFICATE_NOT_FOUND = "不能在ServerKeystore中找到证书";

	public static final String CERTIFICATE_CHAIN_NOT_FOUND = "不能在ServerKeystore中找到证书链";

	// 证书错误信息
	public static final String CERTIFICATE_ERROR = "证书错误";

	public static final String CERTIFICATE_REVOKED = "证书已被作废";

	public static final String CERTNOTTRUSTERROR = "证书不被信任";
}