/*
 * Created on 2005-3-18
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package cn.com.infosec.netseal.appserver.manager;

import java.net.Socket;
import java.util.Properties;

import cn.com.infosec.netseal.common.communication.UnProtocolCommunicator;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.communication.message.impl.bytes.ByteRequest;
import cn.com.infosec.netseal.common.define.Constants;

public class SelfManager {

	public static void stopServer(String host, int port) throws Exception {
		UnProtocolCommunicator comm = null;
		try {
			comm = getComm(host, port);
			Request req = new ByteRequest();
			req.setType("Shutdown");
			Response res = comm.sendAndReceive(req);
			if (res.getErrCode() != 0)
				throw new Exception("stop server error: " + res.getErrMsg());
		} catch (Exception e) {
			throw e;
		} finally {
			if (comm != null)
				comm.close();
		}

	}

	public static String testServer(String host, int port) throws Exception {
		UnProtocolCommunicator comm = null;
		try {
			comm = getComm(host, port);
			Request req = new ByteRequest();
			req.setType("TestServer");
			Response res = comm.sendAndReceive(req);
			if (res.getErrCode() != 0)
				throw new Exception("test server error: " + res.getErrMsg());

			Properties data = res.getData();
			if (data == null)
				throw new Exception("invalid response data");
		} catch (Exception e) {
			throw e;
		} finally {
			if (comm != null)
				comm.close();
		}

		return "server is running"; // 注意，不要修改提示信息

	}

	public static boolean reloadConf(String host, int port) throws Exception {
		UnProtocolCommunicator comm = null;
		try {
			comm = getComm(host, port);
			Request req = new ByteRequest();
			req.setType("ReloadConfig");

			Response res = comm.sendAndReceive(req);
			if (res.getErrCode() != 0)
				throw new Exception("reload config file error: " + res.getErrMsg());

			Properties data = res.getData();
			if (data == null)
				throw new Exception("invalid response data");

			return Boolean.parseBoolean(data.getProperty(Constants.RESULT));
		} catch (Exception e) {
			throw e;
		} finally {
			if (comm != null)
				comm.close();
		}
	}

	private static UnProtocolCommunicator getComm(String host, int port) throws Exception {
		Socket s = new Socket(host, port);
		s.setSoTimeout(10000);
		s.setTcpNoDelay(true);
		return new UnProtocolCommunicator(s);
	}

}
