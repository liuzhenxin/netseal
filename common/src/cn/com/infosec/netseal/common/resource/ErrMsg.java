package cn.com.infosec.netseal.common.resource;

import java.util.Hashtable;

public class ErrMsg {
	private static Hashtable<Integer, String> container = new Hashtable<Integer, String>();

	static {
		container.put(ErrCode.NO_ERROR_DESC, "没有对应的错误描述");

		container.put(ErrCode.REQUEST_ISNULL, "请求为空");
		container.put(ErrCode.REQUEST_DATA_ISNULL, "请求数据为空");
		container.put(ErrCode.REQUEST_TYPE_INVALID, "请求类型非法");
		container.put(ErrCode.RESPONSE_ISNULL, "响应数据为空");
		container.put(ErrCode.PARAM_VALUE_ISNULL, "请求中参数值为NULL");
		container.put(ErrCode.PARAM_VALUE_ISEMPTY, "请求中参数值为空字符串");
		container.put(ErrCode.PARAM_VALUE_INVALID, "请求中参数值非法");
		container.put(ErrCode.PARAM_VALUE_LEN_OVER_LIMIT, "请求中参数值超限");
		container.put(ErrCode.METHOD_PARAM_VALUE_INVALID, "方法中参数值非法");

		container.put(ErrCode.SEAL_HAS_EXIST_IN_DB, "印章在库中已存在");
		container.put(ErrCode.TEMPATE_HAS_EXIST_IN_DB, "印模在库中已存在");
		container.put(ErrCode.PRINTER_HAS_EXIST_IN_DB, "文档打印数据在库中已存在");
		container.put(ErrCode.SEAL_HAS_EXIST_BY_CERT, "此证书已生成印章");
		container.put(ErrCode.REQUEST_HAS_EXIST_BY_CERT, "此证书已生成请求");
		container.put(ErrCode.USER_HAS_EXIST_IN_DB, "用户在库中已存在");
		container.put(ErrCode.REQUEST_HAS_EXIST_IN_DB, "印章申请在库中已存在");

		container.put(ErrCode.SEAL_NOT_EXIST_IN_DB, "印章在库中不存在");
		container.put(ErrCode.TEMPATE_NOT_EXIST_IN_DB, "印模在库中不存在");
		container.put(ErrCode.PRINTER_NOT_EXIST_IN_DB, "文档打印数据在库中不存在");
		container.put(ErrCode.USER_NOT_EXIST_IN_DB, "用户不存在");
		container.put(ErrCode.CERT_NOT_EXIST_IN_DB, "用户证书不存在");
		container.put(ErrCode.KEY_NOT_EXIST_IN_FILE, "密钥在JKS文件中不存在");
		container.put(ErrCode.COMPANY_NOT_EXIST_IN_DB, "机构不存在");
		container.put(ErrCode.COMPANY_TREE_NOT_EXIST_IN_DB, "此机构树不存在");
		container.put(ErrCode.CERT_DATA_NOT_EXIST_IN_DB, "证书数据不存在");
		container.put(ErrCode.PHOTO_DATA_NOT_EXIST_IN_DB, "图片数据不存在");
		container.put(ErrCode.SEAL_DATA_NOT_EXIST_IN_DB, "印章数据不存在");
		container.put(ErrCode.CERT_CHAIN_NOT_EXIST_IN_DB, "证书链不存在");
		container.put(ErrCode.KEY_NOT_EXIST_IN_DB, "密钥在数据库中不存在");
		container.put(ErrCode.KEY_DATA_NOT_EXIST_IN_DB, "密钥数据在数据库中不存在");
		container.put(ErrCode.ROOT_CERT_DN_NOT_FOUND, "证书颁发者DN不存在");
		container.put(ErrCode.GET_SCHEDULER_IS_NULL, "获取计划任务为空");
		container.put(ErrCode.NETWORK_CARD_GET_MAC_NOT_FOUND, "没有找到指定的网卡");
		container.put(ErrCode.KEYWORD_NOT_EXIST_IN_PDF, "关键字在PDF文件中不存在");
		container.put(ErrCode.CSR_DATA_NOT_EXIST_IN_DB, "证书申请数据不存在");
		container.put(ErrCode.PDF_TEMPLATE_NOT_EXIST_IN_DB, "PDF模板不存在");
		container.put(ErrCode.PDF_TEMPLATE_DATA_NOT_EXIST_IN_DB, "PDF模板数据不存在");
		container.put(ErrCode.NOT_FOUND_STAMP_PARAM, "没有找到签章参数");
		container.put(ErrCode.NOT_FOUND_DATA_TO_STAMP, "没有找到待签章数据");

		container.put(ErrCode.VERIFY_PASSWORD_FAILED, "验证密码失败");
		container.put(ErrCode.ROOT_CERT_VERIFY_SIGN_FAILED, "根证 验证书签名失败");
		container.put(ErrCode.PDF_STAMP_VERIFY_FAILED, "PDF验签章失败");
		container.put(ErrCode.NO_LICENSE_PERMISSION, "没有license许可");
		container.put(ErrCode.USER_NO_PERMISSION_TEMPLATE, "用户没有使用印模的权限");
		container.put(ErrCode.COMPANY_NO_PERMISSION_TEMPLATE, "机构没有使用印模的权限");
		container.put(ErrCode.SEAL_NOT_SUPPORT_DOWNLOAD, "印章不支持下载");
		container.put(ErrCode.SEAL_OVER_USED_LIMIT, "印章使用次数超限不能盖章");
		container.put(ErrCode.CERT_GEN_SEAL_NUM_ERROR, "证书产生的印章不唯一");
		container.put(ErrCode.CERT_NOT_UNIQUE_IN_DB, "证书在库中不唯一");
		container.put(ErrCode.USER_NOT_UNIQUE_IN_DB, "用户（相同机构相同证书）在库中不唯一");
		container.put(ErrCode.TEMPATE_NOT_UNIQUE_IN_DB, "印模在库中不唯一");
		container.put(ErrCode.CERT_NUM_OVER_LIMIT, "证书数量超限");
		container.put(ErrCode.SERVERCERT_NOT_SET, "未指定服务器签名密钥");
		container.put(ErrCode.PAGE_NUM_OVER_LIMIT_IN_PDF, "页码超出PDF文件范围");
		container.put(ErrCode.PAGE_XY_OVER_LIMIT_IN_PDF, "坐标超出PDF文件范围");
		container.put(ErrCode.PDF_TEMPLATE_NOT_UNIQUE_IN_DB, "PDF模板在库中不唯一");
		container.put(ErrCode.PRINT_NOT_UNIQUE_IN_DB, "文档打印数据在库中不唯一");

		container.put(ErrCode.ENCODING_NOT_SUPPORT, "编码类型不支持");
		container.put(ErrCode.DATE_FORMAT_INVAILD, "日期格式非法");
		container.put(ErrCode.DATE_EXCEED_LIMIT, "日期不在有效期内");
		container.put(ErrCode.CERT_HAS_REVOKED, "证书已作废");
		container.put(ErrCode.CERT_DN_NOT_MATCH, "证书DN不匹配");
		container.put(ErrCode.NAME_NOT_MATCH, "名称不匹配");
		container.put(ErrCode.STATUS_DISABLE, "状态为停用");
		container.put(ErrCode.CERT_INVALID, "证书无效");
		container.put(ErrCode.CERT_ALREADY_IN_USED, "证书已被注册,请删除再注册");
		container.put(ErrCode.SEAL_FORMAT_INVAILD, "电子印章格式非法");
		container.put(ErrCode.STAMP_FORMAT_INVAILD, "电子签章格式非法");
		container.put(ErrCode.CERT_USAGE_SIGN_FALSE, "证书密钥用法不包含签名");
		container.put(ErrCode.CERT_USAGE_ENC_FALSE, "证书密钥用法不包含加密");
		container.put(ErrCode.UNKNOWN_ALGORITHM, "系统未知算法");
		container.put(ErrCode.CERT_NOT_IN_TRUST, "证书不被信任");
		container.put(ErrCode.MAC_INVALID, "信息被篡改");
		container.put(ErrCode.UNKNOWN_USAGE, "未知证书密钥用法");
		container.put(ErrCode.CERT_USAGE_HAS_EXIST, "用户相同证书用途不唯一");

		container.put(ErrCode.INNER_OPERATION_ERROR, "发生内部错误");
		container.put(ErrCode.PROCESS_REQUEST_ERROR, "处理请求发生错误");
		container.put(ErrCode.ENCODING_BASE64_ERROR, "BASE64转码发生错误");
		container.put(ErrCode.FILE_OPERATION_ERROR, "操作文件发生错误");
		container.put(ErrCode.DATABASE_OPERATION_ERROR, "与数据库交互发生错误");
		container.put(ErrCode.ROOT_CERT_VERIFY_SIGN_ERROR, "根证 验证书签名发生错误");
		container.put(ErrCode.PDF_STAMP_ERROR, "PDF签章发生错误");
		container.put(ErrCode.PDF_STAMP_VERIFY_ERROR, " PDF验签章发生错误");
		container.put(ErrCode.CERT_SIGN_ERROR, "签名发生错误");
		container.put(ErrCode.CERT_SIGN_VERIFY_ERROR, "验签发生错误");
		container.put(ErrCode.PDF_INSERT_PHOTO_ERROR, "PDF插入图片发生错误");
		container.put(ErrCode.CERT_DATA_INVAILD, "构建证书对象发生错误,证书数据非法");
		container.put(ErrCode.GEN_SEAL_ERROR, " 生成印章发生错误");
		container.put(ErrCode.LOAD_KEY_FROM_FILE_ERROR, "从文件中加载密钥发生错误");
		container.put(ErrCode.GEN_BARCODE_ERROR, "产生二编码图片发生错误");
		container.put(ErrCode.GET_DATA_ERROR, "获取数据发生错误");
		container.put(ErrCode.GEN_SEAL_PHOTO_ERROR, "产生印章图片发生错误");
		container.put(ErrCode.GET_JMX_DATA_ERROR, " 获取JMX数据发生错误");
		container.put(ErrCode.NETWORK_CARD_GET_MAC_ERROR, "获取网卡MAC错误");
		container.put(ErrCode.OPERATE_LICENSE_ERROR, "操作License发生错误");
		container.put(ErrCode.STORE_KEY_TO_FILE_ERROR, "保存密钥到文件发生错误");
		container.put(ErrCode.CALC_MAC_ERROR, "计算MAC发生错误");
		container.put(ErrCode.GEN_STAMP_ERROR, "生成签章发生错误");
		container.put(ErrCode.VERIFY_SEAL_ERROR, "验证印章发生错误");
		container.put(ErrCode.VERIFY_STAMP_ERROR, "验证签章发生错误");
		container.put(ErrCode.LOAD_CONF_ERROR, "加载配置文件错误");
		container.put(ErrCode.RELOAD_CONF_ERROR, "重新加载系统配置错误");
		container.put(ErrCode.OFD_STAMP_ERROR, "OFD签章发生错误");
		container.put(ErrCode.OFD_STAMP_VERIFY_ERROR, "OFD验签章发生错误");
		container.put(ErrCode.OFD_WATERMARK_ERROR, "OFD添加水印发生错误");
		container.put(ErrCode.UPDATE_SEAL_PHOTO_ERROR, "更新印章图片发生错误");
		container.put(ErrCode.OFD_OPERATE_ERROR, "操作OFD文件发生错误");
		container.put(ErrCode.OFD_STAMP_CERT_ERROR, "OFD签章仅支持国密证书");
		container.put(ErrCode.OFD_STAMP_HANDSTAMP_ERROR, "手写签章不支持OFD签章");
	}

	public static String getErrMsg(int errorNum) {
		String errorMsg = container.get(errorNum);
		if (errorMsg == null)
			return container.get(ErrCode.NO_ERROR_DESC);
		else
			return errorMsg;
	}
}