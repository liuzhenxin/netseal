package cn.com.infosec.netseal.common.communication.message;

import java.util.Properties;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.HexUtil;

/**
 * Server对客户端请求的应答
 */
public class Response {
	/**
	 * 错误代码
	 */
	protected int errCode = 0;

	/**
	 * 错误信息
	 */
	protected String errMsg = "OK";
	/**
	 * 响应数据
	 */
	protected Properties data = new Properties();

	/**
	 * 将响应信息编码为可以发送给请求者的数据形式
	 */
	public Response() {
	}

	public Properties getData() {
		return data;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setData(Properties data) {
		this.data = data;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	protected byte[] getBytes(String str) throws Exception {
		return str.getBytes(Constants.UTF_8);
	}

	protected byte[] int2Bytes(int i) {
		return HexUtil.int2Byte(i);
	}

	protected int bytes2Int(byte[] bs) {
		return HexUtil.byte2Int(bs);
	}

}