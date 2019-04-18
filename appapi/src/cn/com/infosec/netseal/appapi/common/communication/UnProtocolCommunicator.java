package cn.com.infosec.netseal.appapi.common.communication;

import java.net.Socket;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import cn.com.infosec.netseal.appapi.common.communication.message.Request;
import cn.com.infosec.netseal.appapi.common.communication.message.Response;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.bytes.ByteRequest;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.bytes.ByteResponse;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.envelopes.EnvelopeRequest;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.envelopes.EnvelopeResponse;

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
	public Response sendAndReceive(Request req, boolean isEnvelope, X509Certificate serverCert, PrivateKey priKey) throws Exception {
		Response res = null;

		if (isEnvelope) {
			EnvelopeRequest eReq = new EnvelopeRequest();
			eReq.setbReq((ByteRequest) req);
			eReq.setServerCert(serverCert);
			req = eReq;
		}

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

		if (isEnvelope) {
			EnvelopeResponse eRes = new EnvelopeResponse();
			bs = eRes.unenvelopeResponse(bs, priKey).getBytes();
		}

		res = new ByteResponse(bs);
		return res;
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