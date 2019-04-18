package cn.com.infosec.netseal.common.communication;

import java.net.Socket;

import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.communication.message.impl.bytes.ByteResponse;

/**
 * 通讯服务提供者，封装通讯操作
 * <p>
 * Title: 通讯服务提供者
 * </p>
 */
public class UnProtocolCommunicator extends BasicCommunicator {

	public UnProtocolCommunicator(Socket s) {
		super(s);
	}

	/**
	 * 发送请求，接收响应
	 * 
	 * @param req
	 *            请求
	 * @return 响应
	 * @throws Exception
	 */
	public Response sendAndReceive(Request req) throws Exception {
		// 当接收数据失败后，直接关闭错误的连接，不再放回池中
		try {
			send(req.getBytes());
		} catch (Throwable e) {
			// 发送请求数据失败,重新发一次
			reSend(req.getBytes());
		}

		byte[] bs = null;
		try {
			bs = recv();
		} catch (Exception e) {
			// 关闭读数据发生错误的连接
			closeBadSocket();
			throw e;
		}

		Response res = new ByteResponse(bs);
		return res;
	}

}