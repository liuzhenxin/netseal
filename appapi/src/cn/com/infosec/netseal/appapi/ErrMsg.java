package cn.com.infosec.netseal.appapi;

import java.util.Hashtable;

public class ErrMsg {
	private static Hashtable<Integer, String> container = new Hashtable<Integer, String>();

	static {
		container.put(ErrCode.NO_ERROR_DESC, "没有对应的错误描述");

		container.put(ErrCode.API_PROPERTY_IS_NULL, "API属性未设置");
		container.put(ErrCode.NO_SERVER_IP, "没有服务IP");
		container.put(ErrCode.SERVER_PORT_ERROR, "服务端口错误");
		container.put(ErrCode.SSL_STORE_ERROR, "SSL 文件、密码错误");
		container.put(ErrCode.CONNECT_FAILED, "创建连接失败");

		container.put(ErrCode.NETWORK_IO_ERROR, "网络错误");
		container.put(ErrCode.PROTOCOL_ERROR, "协议错误");
		container.put(ErrCode.UNKNOWN_ERROR, "未知错误");
		container.put(ErrCode.RESPONSE_IS_NULL, "响应为空");
		container.put(ErrCode.RESPONSE_DATA_IS_NULL, "响应数据为空");

		container.put(ErrCode.API_PARAM_IS_NULL, "响应参数值为NULL");
		container.put(ErrCode.API_PARAM_IS_EMPTY, "响应参数值为空字符串");
		container.put(ErrCode.API_PARAM_LEN_EXCEED, "请求参数值长度非法");
		container.put(ErrCode.API_PARAM_INVAILD, "响应参数值非法");
		container.put(ErrCode.API_ENCODING_BASE64_ERROR, "BASE64转码发生错误");
		container.put(ErrCode.CONNECT_GET_TIMEOUT, "获取连接（从池中）超时");

		container.put(ErrCode.OPEN_FM_DEVICE_ERROR, "打开加密设备错误");
		container.put(ErrCode.LOAD_ALG_ERROR, "加载算法文件错误");
		container.put(ErrCode.SET_SSL_ERROR, "配置SSL错误");
	}

	public static String getErrMsg(int errorNum) {
		String errorMsg = container.get(errorNum);
		if (errorMsg == null)
			return container.get(ErrCode.NO_ERROR_DESC);
		else
			return errorMsg;
	}
}