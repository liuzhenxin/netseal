package cn.com.infosec.netseal.appserver.listener;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;

import cn.com.infosec.netseal.appserver.communication.handler.SocketConnectionHandler;
import cn.com.infosec.netseal.common.communication.Communicator;
import cn.com.infosec.netseal.common.communication.UnProtocolCommunicator;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.CounterManager;
import cn.com.infosec.netseal.common.util.SSLSocketFactoryUtil;

/**
 * Title: 普通Socket的监听器 Description: 普通Socket的监听器
 */
public class SocketListener extends ServerListener {

	private static ExecutorService exes = Executors.newCachedThreadPool();
	private ServerSocket server = null;

	/**
	 * 启动管理服务监听
	 */
	public void run() {
		try {
			if (isSsl()) {
				keystore = Constants.SSL_PATH + keystore;
				Properties pro = new Properties();
				pro.setProperty("javax.net.ssl.keyStore", keystore);
				pro.setProperty("javax.net.ssl.keyStorePassword", new String(keypwd));

				if (authClient) {
					truststore = Constants.SSL_PATH + truststore;
					pro.setProperty("javax.net.ssl.trustStore", truststore);
					pro.setProperty("javax.net.ssl.trustStorePassword", new String(trustpwd));

				}
				SSLSocketFactoryUtil ssfu = new SSLSocketFactoryUtil();
				ServerSocketFactory ssf = ssfu.getSSLServerSocketFactory(pro);

				if (ip == null || "".equals(ip.trim()))
					server = ssf.createServerSocket(port, backlog);
				else
					server = ssf.createServerSocket(port, backlog, InetAddress.getByName(ip));
				((SSLServerSocket) server).setNeedClientAuth(authClient);
			} else {
				if (ip == null || "".equals(ip.trim()))
					server = new ServerSocket(port, backlog);
				else
					server = new ServerSocket(port, backlog, InetAddress.getByName(ip));
			}

			start();
			System.out.println("started on channel:" + name + " port:" + port + " (ip:" + ip + ") ssl:" + ssl + " authclient:" + authClient);
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