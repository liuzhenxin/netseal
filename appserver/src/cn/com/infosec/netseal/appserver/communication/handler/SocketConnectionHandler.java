package cn.com.infosec.netseal.appserver.communication.handler;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.Properties;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.listener.ServerListener;
import cn.com.infosec.netseal.appserver.manager.ApplicationContextManager;
import cn.com.infosec.netseal.common.communication.Communicator;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.communication.message.impl.bytes.ByteRequest;
import cn.com.infosec.netseal.common.communication.message.impl.bytes.ByteResponse;
import cn.com.infosec.netseal.common.communication.message.impl.envelopes.EnvelopeRequest;
import cn.com.infosec.netseal.common.communication.message.impl.envelopes.EnvelopeResponse;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.keyData.KeyDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.CounterManager;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.resource.ErrMsg;

/**
 * Socket 连接处理器
 */
public class SocketConnectionHandler implements Runnable {

	private String channel;
	private Communicator comm;
	private ServerListener listener;

	public SocketConnectionHandler(Communicator comm, String channel, ServerListener listener) {
		this.comm = comm;
		this.channel = channel;
		this.listener = listener;
	}

	/**
	 * 处理请求,发送响应信息
	 */
	public void run() {
		while (true) {
			Response res = null;
			Request req = null;
			ByteResponse bRes = null;

			// 接收请求数据
			try {
				byte[] data = comm.recv();
				// 解析数字信封
				if (listener.isEnvelope()) {
					EnvelopeRequest eReq = new EnvelopeRequest();
					KeyDaoImpl keyDao = ApplicationContextManager.getBean("keyDaoImpl", KeyDaoImpl.class);
					KeyDataDaoImpl keyDataDao = ApplicationContextManager.getBean("keyDataDaoImpl", KeyDataDaoImpl.class);
					req = eReq.unenvelopeRequest(data, keyDao, keyDataDao);
				} else
					req = new ByteRequest(data);
				req.setClientHost(listener.getClientHost());
			} catch (EOFException e) {
				comm.close();
				decrement();
				break;
			} catch (SocketTimeoutException e) {
				comm.close();
				decrement();
				break;
			} catch (Throwable ex) {
				LoggerUtil.errorlog("get request data error", ex);
				comm.close();
				decrement();
				break;
			}

			// 将请求包内容记录在debug日志
			LoggerUtil.debuglog("Request message:[" + req.toString() + "]");

			// 取得处理器
			BaseProcessor p = null;
			try {
				req.setChannel(channel);
				p = ApplicationContextManager.getBean(req.getType() + "Processor", BaseProcessor.class);
			} catch (Exception e) {
				LoggerUtil.errorlog("get processor error", e);
				bRes = constructErrorResponse(ErrCode.REQUEST_TYPE_INVALID);
			}

			// 处理请求数据
			if (p != null) {
				try {
					res = p.process(req);
					if (res == null) {
						bRes = constructErrorResponse(ErrCode.RESPONSE_ISNULL);
					} else {
						bRes = new ByteResponse(res);
					}
				} catch (Throwable e) {
					LoggerUtil.errorlog("processor error ", e);
					bRes = constructErrorResponse(ErrCode.PROCESS_REQUEST_ERROR);
				}
			}

			// 将返回客户端的响应信息记录在debug日志
			if (bRes != null)
				LoggerUtil.debuglog("Response message:[" + bRes.toString() + "]");

			// 发送响应信息
			try {
				byte[] data = bRes.getBytes();
				// 构建数字信封
				if (listener.isEnvelope()) {
					Properties reqdata = req.getData();
					String certDn = reqdata.getProperty(Constants.CERT_DN_CLIENT);
					EnvelopeResponse eRes = new EnvelopeResponse();
					CertDaoImpl certDao = ApplicationContextManager.getBean("certDaoImpl", CertDaoImpl.class);
					CertDataDaoImpl certDataDao = ApplicationContextManager.getBean("certDataDaoImpl", CertDataDaoImpl.class);
					data = eRes.envelopeResponse(certDn, data, certDao, certDataDao);
				}
				comm.send(data);
			} catch (Throwable ex) {
				comm.close();
				LoggerUtil.errorlog("send response error", ex);
				decrement();
				break;
			}

			if (req != null && req.getType() != null) {
				if ((req.getType().startsWith("Shutdown")) && (res != null && res.getErrCode() == 0)) {
					System.out.println("prepared to stop server...");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					System.out.println("server stopped.");
					System.exit(0);
				}
			}
		}

	}

	private ByteResponse constructErrorResponse(int errorNum) {
		ByteResponse bRes = new ByteResponse();
		String message = ErrMsg.getErrMsg(errorNum);
		bRes.setErrCode(errorNum);
		bRes.setErrMsg(message);
		return bRes;
	}

	private void decrement() {
		CounterManager.decrement(Constants.SOCKET_NUM);
	}
}