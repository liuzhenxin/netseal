package cn.com.infosec.netseal.appapi.common.communication.message;

import java.util.Properties;

import cn.com.infosec.netseal.appapi.common.define.Constants;
import cn.com.infosec.netseal.appapi.common.util.HexUtil;

/**
 * 从客户端发给Server的请求
 */
public class Request {

	/**
	 * 请求数据，包括请求的类型和请求数据 在此版本中，数据都是字符串数据， 所以使用Properties作为数据容器
	 */
	private Properties data = new Properties();
	private String channel = "";
	private String type = "";
	private String clientHost = "";

	public Request() {
	}

	/**
	 * 返回请求信息
	 * 
	 * @return
	 */
	public Properties getData() {
		return data;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * 设置请求信息
	 * 
	 * @param reqData
	 */
	public void setData(Properties data) {
		this.data = data;
	}

	/**
	 * 设置请求类型, Server根据请求类型的分发给相应的处理器
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	/**
	 * @return Returns the clientHost.
	 */
	public String getClientHost() {
		return clientHost;
	}

	/**
	 * @param clientHost
	 *            The clientHost to set.
	 */
	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public byte[] getBytes() throws Exception {
		throw new Exception("not support method getBytes()");
	}

	public byte[] getBytes(String str) throws Exception {
		return str.getBytes(Constants.UTF_8);
	}

	public byte[] int2Bytes(int i) {
		return HexUtil.int2Byte(i);
	}

	public int bytes2Int(byte[] bs) {
		return HexUtil.byte2Int(bs);
	}

}