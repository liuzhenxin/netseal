package cn.com.infosec.netseal.appapi.common.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cn.com.infosec.netseal.appapi.common.exceptions.ProtocolLengthException;
import cn.com.infosec.netseal.appapi.common.util.HexUtil;
import cn.com.infosec.netseal.appapi.common.util.socketpool.SocketConnection;

/**
 * 网络通信基础类, 用来在网络上收发数据.
 * 
 */
public abstract class BasicCommunicator implements Communicator {

	public Socket socket;
	protected DataOutputStream out;
	protected DataInputStream input;

	/**
	 * 构造函数
	 * 
	 * @param s
	 *            服务器与客户端的Socket连接
	 */
	public BasicCommunicator(Socket s) {
		socket = s;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
		}
	}

	/**
	 * 发送二进制数据
	 */
	public void send(byte[] data) throws IOException {
		byte[] bs = HexUtil.int2Byte(data.length);
		byte[] sData = new byte[bs.length + data.length];

		System.arraycopy(bs, 0, sData, 0, bs.length);
		System.arraycopy(data, 0, sData, bs.length, data.length);
		out.write(sData);
		out.flush();
	}

	public void reSend(byte[] data) throws IOException {
		if (!(socket instanceof SocketConnection)) {
			throw new IOException("reSend error, sslsocket is " + socket);
		}

		// 关闭旧的连接，创建新的连接
		SocketConnection sc = (SocketConnection) socket;
		if (out != null) {
			out.close();
			out = null;
		}
		if (input != null) {
			input.close();
		}
		sc.reCreateConn(); // 关闭旧的,并创建新的
		out = new DataOutputStream(socket.getOutputStream());
		input = new DataInputStream(socket.getInputStream());

		send(data);
	}

	/**
	 * 读取数据开头4个字节,取得接受数据的长度.
	 * 
	 * @return 数据长度
	 * @throws IOException
	 */
	public int recvLength() throws IOException {
		int len = input.readInt();
		if ((len <= 0) || (len > Communicator.maxRequestLength))
			throw new ProtocolLengthException("recv length is over limit, len is " + len);

		return len;
	}

	public byte[] recv() throws IOException {
		int len = recvLength();

		byte[] allData = new byte[len];
		byte[] data = null;
		int rlen = len;

		while (rlen > 0) {
			if (rlen > READ_BLOCK_LENGTH)
				data = new byte[READ_BLOCK_LENGTH];
			else
				data = new byte[rlen];

			int alen = input.read(data);
			System.arraycopy(data, 0, allData, len - rlen, alen);
			rlen -= alen;
		}

		return allData;
	}

	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
		}
	}

	// 关闭坏的连接
	public void closeBadSocket() {
		try {
			if (socket instanceof SocketConnection) {
				SocketConnection sc = (SocketConnection) socket;
				sc.closeSocket();
			}
		} catch (Exception e) {
		}
	}

}
