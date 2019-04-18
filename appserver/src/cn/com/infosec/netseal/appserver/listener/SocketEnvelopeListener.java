package cn.com.infosec.netseal.appserver.listener;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.infosec.netseal.appserver.communication.handler.SocketConnectionHandler;
import cn.com.infosec.netseal.appserver.manager.ApplicationContextManager;
import cn.com.infosec.netseal.common.communication.Communicator;
import cn.com.infosec.netseal.common.communication.UnProtocolCommunicator;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.CounterManager;

/**
 * Title: 普通Socket的监听器 Description: 普通Socket的监听器
 */
public class SocketEnvelopeListener extends ServerListener {

	private static ExecutorService exes = Executors.newCachedThreadPool();
	private ServerSocket server = null;

	/**
	 * 启动管理服务监听
	 */
	public void run() {
		try {
			if (ip == null || "".equals(ip.trim()))
				server = new ServerSocket(port, backlog);
			else
				server = new ServerSocket(port, backlog, InetAddress.getByName(ip));
			start();

			// 检查是否配置了数字信封证书
			String msg = "";
			Long keyId = ConfigUtil.getInstance().getEncrpKeyId();
			Key key = ApplicationContextManager.getBean("keyDaoImpl", KeyDaoImpl.class).getKey(keyId);
			if (key == null)
				msg = "envelope disable(server key is null from db)";
			else
				msg = "envelope enable(server key is " + key.getCertDn() + ")";

			System.out.println("started on channel:" + name + " port:" + port + " (ip:" + ip + ") " + msg);
		} catch (Exception ex) {
			System.out.println("start channel:" + name + " error, because " + ex.getMessage());
			ex.printStackTrace();
			return;
		}

		while (isStarted()) {
			Socket incomingConnection = null;
			try {
				incomingConnection = server.accept();
				incomingConnection.setSoTimeout(timeout * 1000);
				incomingConnection.setTcpNoDelay(true);
				incomingConnection.setSoLinger(true, 0);

				String clientHost = incomingConnection.getInetAddress().getHostAddress();
				setClientHost(clientHost);
				handleConnection(incomingConnection);
			} catch (SocketException e) {
				if (isStarted())
					LoggerUtil.errorlog("accept client connection error, start is " + isStarted(), e);
			} catch (Throwable ex) {
				LoggerUtil.errorlog("accept client connection error(Throwable)", ex);
			}
		}
	}

	private void handleConnection(Socket connectionToHandle) throws Exception {
		if (CounterManager.isContinue(Constants.SOCKET_NUM, ConfigUtil.getInstance().getCheckSocketNumLimit())) {
			Communicator comm = new UnProtocolCommunicator(connectionToHandle);
			exes.execute(new SocketConnectionHandler(comm, name, this));
		} else {
			connectionToHandle.close();
			LoggerUtil.debuglog("connect num exceed limit, close socket is " + connectionToHandle);
		}
	}

	public void shutDown() {
		super.shutDown();
		try {
			server.close();
		} catch (Exception e) {
		}
	}

}