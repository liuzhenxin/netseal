package cn.com.infosec.netseal.common.resource;

public class ErrCode {
	// 没有对应的错误描述
	public static final int NO_ERROR_DESC = 80000000;

	// 1. ========请求响应参数类型========
	// 请求为空
	public static final int REQUEST_ISNULL = 80010001;
	// 请求数据为空
	public static final int REQUEST_DATA_ISNULL = 80010002;
	// 请求类型非法
	public static final int REQUEST_TYPE_INVALID = 80010003;
	// 响应数据为空
	public static final int RESPONSE_ISNULL = 80010004;
	// 请求中参数值为NULL
	public static final int PARAM_VALUE_ISNULL = 80010005;
	// 请求中参数值为空字符串
	public static final int PARAM_VALUE_ISEMPTY = 80010006;
	// 请求中参数值非法
	public static final int PARAM_VALUE_INVALID = 80010007;
	// 请求中参数值超限
	public static final int PARAM_VALUE_LEN_OVER_LIMIT = 80010008;
	// 方法中参数值非法
	public static final int METHOD_PARAM_VALUE_INVALID = 80010009;

	// 2. ========数据存在类型========
	// 印章在库中已存在
	public static final int SEAL_HAS_EXIST_IN_DB = 80020001;
	// 印模在库中已存在
	public static final int TEMPATE_HAS_EXIST_IN_DB = 80020002;
	// 文档打印数据在库中已存在
	public static final int PRINTER_HAS_EXIST_IN_DB = 80020003;
	// 此证书已生成印章
	public static final int SEAL_HAS_EXIST_BY_CERT = 80020004;
	// 此证书已生成请求
	public static final int REQUEST_HAS_EXIST_BY_CERT = 80020005;
	// 用户在库中已存在
	public static final int USER_HAS_EXIST_IN_DB = 80020006;
	// 印章申请在库中已存在
	public static final int REQUEST_HAS_EXIST_IN_DB = 80020007;

	// 3. ========数据不存在类型========
	// 印章在库中不存在
	public static final int SEAL_NOT_EXIST_IN_DB = 80030001;
	// 印模在库中不存在
	public static final int TEMPATE_NOT_EXIST_IN_DB = 80030002;
	// 文档打印数据在库中不存在
	public static final int PRINTER_NOT_EXIST_IN_DB = 80030003;
	// 用户不存在
	public static final int USER_NOT_EXIST_IN_DB = 80030004;
	// 用户证书不存在
	public static final int CERT_NOT_EXIST_IN_DB = 80030005;
	// 密钥在JKS文件中不存在
	public static final int KEY_NOT_EXIST_IN_FILE = 80030006;
	// 机构不存在
	public static final int COMPANY_NOT_EXIST_IN_DB = 80030007;
	// 此机构树不存在
	public static final int COMPANY_TREE_NOT_EXIST_IN_DB = 80030008;
	// 证书数据不存在
	public static final int CERT_DATA_NOT_EXIST_IN_DB = 80030009;
	// 图片数据不存在
	public static final int PHOTO_DATA_NOT_EXIST_IN_DB = 80030010;
	// 印章数据不存在
	public static final int SEAL_DATA_NOT_EXIST_IN_DB = 80030011;
	// 证书链不存在
	public static final int CERT_CHAIN_NOT_EXIST_IN_DB = 80030012;
	// 密钥在数据库中不存在
	public static final int KEY_NOT_EXIST_IN_DB = 80030013;
	// 密钥数据在数据库中不存在
	public static final int KEY_DATA_NOT_EXIST_IN_DB = 80030014;
	// 证书颁发者DN不存在
	public static final int ROOT_CERT_DN_NOT_FOUND = 8003015;
	// 获取计划任务为空
	public static final int GET_SCHEDULER_IS_NULL = 80030016;
	// 没有找到指定的网卡
	public static final int NETWORK_CARD_GET_MAC_NOT_FOUND = 80030017;
	// 关键字在PDF文件中不存在
	public static final int KEYWORD_NOT_EXIST_IN_PDF = 80030018;
	// 证书申请数据不存在
	public static final int CSR_DATA_NOT_EXIST_IN_DB = 80030019;
	// PDF模板不存在
	public static final int PDF_TEMPLATE_NOT_EXIST_IN_DB = 80030020;
	// PDF模板数据不存在
	public static final int PDF_TEMPLATE_DATA_NOT_EXIST_IN_DB = 80030021;
	// 没有找到签章参数
	public static final int NOT_FOUND_STAMP_PARAM = 80030022;
	// 没有找到待签章数据
	public static final int NOT_FOUND_DATA_TO_STAMP = 80030023;

	// 4. ========权限、验证、唯一性校验类型========
	// 验证密码失败
	public static final int VERIFY_PASSWORD_FAILED = 80040001;
	// 根证 验证书签名失败
	public static final int ROOT_CERT_VERIFY_SIGN_FAILED = 80040002;
	// PDF验签章失败
	public static final int PDF_STAMP_VERIFY_FAILED = 80040003;
	// 没有license许可
	public static final int NO_LICENSE_PERMISSION = 80040004;
	// 用户没有使用印模的权限
	public static final int USER_NO_PERMISSION_TEMPLATE = 80040005;
	// 机构没有使用印模的权限
	public static final int COMPANY_NO_PERMISSION_TEMPLATE = 80040006;
	// 印章不支持下载
	public static final int SEAL_NOT_SUPPORT_DOWNLOAD = 80040007;
	// 印章使用次数超限不能盖章
	public static final int SEAL_OVER_USED_LIMIT = 80040008;
	// 证书产生的印章不唯一
	public static final int CERT_GEN_SEAL_NUM_ERROR = 80040009;
	// 证书在库中不唯一
	public static final int CERT_NOT_UNIQUE_IN_DB = 80040010;
	// 用户（相同机构相同证书）在库中不唯一
	public static final int USER_NOT_UNIQUE_IN_DB = 80040011;
	// 印模在库中不唯一
	public static final int TEMPATE_NOT_UNIQUE_IN_DB = 80040012;
	// 证书数量超限
	public static final int CERT_NUM_OVER_LIMIT = 80040013;
	// 未指定服务器签名密钥
	public static final int SERVERCERT_NOT_SET = 80040014;
	// 页码超出PDF文件范围
	public static final int PAGE_NUM_OVER_LIMIT_IN_PDF = 80040015;
	// 坐标超出PDF文件范围
	public static final int PAGE_XY_OVER_LIMIT_IN_PDF = 80040016;
	// PDF模板在库中不唯一
	public static final int PDF_TEMPLATE_NOT_UNIQUE_IN_DB = 80040017;
	// 文档打印数据在库中不唯一
	public static final int PRINT_NOT_UNIQUE_IN_DB = 80040018;

	// 5. ========策略校验类型========
	// 编码类型不支持
	public static final int ENCODING_NOT_SUPPORT = 80050001;
	// 日期格式非法
	public static final int DATE_FORMAT_INVAILD = 80050002;
	// 日期不在有效期内
	public static final int DATE_EXCEED_LIMIT = 80050003;
	// 证书已作废
	public static final int CERT_HAS_REVOKED = 80050004;
	// 证书DN不匹配
	public static final int CERT_DN_NOT_MATCH = 80050005;
	// 名称不匹配
	public static final int NAME_NOT_MATCH = 80050006;
	// 状态为停用
	public static final int STATUS_DISABLE = 80050007;
	// 证书无效
	public static final int CERT_INVALID = 80050008;
	// 证书已被注册,请删除再注册
	public static final int CERT_ALREADY_IN_USED = 80050009;
	// 电子印章格式非法
	public static final int SEAL_FORMAT_INVAILD = 80050010;
	// 电子签章格式非法
	public static final int STAMP_FORMAT_INVAILD = 80050011;
	// 证书密钥用法不包含签名
	public static final int CERT_USAGE_SIGN_FALSE = 80050012;
	// 证书密钥用法不包含加密
	public static final int CERT_USAGE_ENC_FALSE = 80050013;
	// 系统未知算法
	public static final int UNKNOWN_ALGORITHM = 80050014;
	// 证书不被信任
	public static final int CERT_NOT_IN_TRUST = 80050015;
	// 信息被篡改
	public static final int MAC_INVALID = 80050015;
	// 未知证书密钥用法
	public static final int UNKNOWN_USAGE = 80050016;
	// 用户相同证书用途不唯一
	public static final int CERT_USAGE_HAS_EXIST = 80050017;
	

	// 6. ========发生错误类型========
	// 发生内部错误
	public static final int INNER_OPERATION_ERROR = 80060001;
	// 处理请求发生错误
	public static final int PROCESS_REQUEST_ERROR = 80060002;
	// BASE64转码发生错误
	public static final int ENCODING_BASE64_ERROR = 80060003;
	// 操作文件发生错误
	public static final int FILE_OPERATION_ERROR = 80060004;
	// 与数据库交互发生错误
	public static final int DATABASE_OPERATION_ERROR = 80060005;
	// 根证 验证书签名发生错误
	public static final int ROOT_CERT_VERIFY_SIGN_ERROR = 80060006;
	// PDF签章发生错误
	public static final int PDF_STAMP_ERROR = 80060007;
	// PDF验签章发生错误
	public static final int PDF_STAMP_VERIFY_ERROR = 80060008;
	// 签名发生错误
	public static final int CERT_SIGN_ERROR = 80060009;
	// 验签发生错误
	public static final int CERT_SIGN_VERIFY_ERROR = 80060010;
	// PDF插入图片发生错误
	public static final int PDF_INSERT_PHOTO_ERROR = 80060011;
	// 构建证书对象发生错误,证书数据非法
	public static final int CERT_DATA_INVAILD = 80060012;
	// 生成印章发生错误
	public static final int GEN_SEAL_ERROR = 80060013;
	// 从文件中加载密钥发生错误
	public static final int LOAD_KEY_FROM_FILE_ERROR = 80060014;
	// 产生二编码图片发生错误
	public static final int GEN_BARCODE_ERROR = 80060015;
	// 获取数据发生错误
	public static final int GET_DATA_ERROR = 80060016;
	// 产生印章图片发生错误
	public static final int GEN_SEAL_PHOTO_ERROR = 80060017;
	// 获取JMX数据发生错误
	public static final int GET_JMX_DATA_ERROR = 80060018;
	// 获取网卡MAC错误
	public static final int NETWORK_CARD_GET_MAC_ERROR = 80060019;
	// 操作License发生错误
	public static final int OPERATE_LICENSE_ERROR = 80060020;
	// 保存密钥到文件发生错误
	public static final int STORE_KEY_TO_FILE_ERROR = 80060021;
	// 计算MAC发生错误
	public static final int CALC_MAC_ERROR = 80060022;
	// 生成签章发生错误
	public static final int GEN_STAMP_ERROR = 80060023;
	// 验证印章发生错误
	public static final int VERIFY_SEAL_ERROR = 80060024;
	// 验证签章发生错误
	public static final int VERIFY_STAMP_ERROR = 80060025;
	// 加载配置文件错误
	public static final int LOAD_CONF_ERROR = 80060026;
	// 重新加载系统配置错误
	public static final int RELOAD_CONF_ERROR = 80060027;
	// OFD签章发生错误
	public static final int OFD_STAMP_ERROR = 80060028;
	// OFD验签章发生错误
	public static final int OFD_STAMP_VERIFY_ERROR = 80060029;
	// OFD添加水印发生错误
	public static final int OFD_WATERMARK_ERROR = 80060030;
	// 更新印章图片发生错误
	public static final int UPDATE_SEAL_PHOTO_ERROR = 80060031;
	// 操作OFD文件发生错误
	public static final int OFD_OPERATE_ERROR = 80060032;
	// OFD签章仅支持国密证书
	public static final int OFD_STAMP_CERT_ERROR = 80060033;
	// 手写章不支持OFD签章
	public static final int OFD_STAMP_HANDSTAMP_ERROR = 80060034;

}