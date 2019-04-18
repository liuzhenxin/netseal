package cn.com.infosec.netseal.appapi.common.util.socketpool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * 池化的Socket
 * 
 * 
 */
public class SocketConnection extends Socket implements Comparable {

	NewSocketConnectionPool pool = null;
	String name = "";
	volatile Socket socket = null;
	volatile boolean isUsed = false; // 是否在被使用
	volatile long lastUsedTime = 0; // 最后一个使用的时间

	public SocketConnection(NewSocketConnectionPool pool, int i) {
		this.pool = pool;
		this.name = "PooledSocket-" + i;
	}

	public synchronized void setUsedStatus() throws Exception {
		if (this.socket == null) {
			reCreateConn();
		}

		this.isUsed = true;
		this.lastUsedTime = System.currentTimeMillis();

	}

	/**
	 * 关闭长时间不使用的连接池
	 * 
	 * @return
	 */
	public synchronized boolean closeNoLongTimeNoUsed() {
		if (this.isUsed || this.socket == null)
			return false;
		if (this.pool.getUnUsedTimeount() <= 0)
			return false;

		long noUsedTime = System.currentTimeMillis() - this.lastUsedTime;
		if (noUsedTime > pool.getUnUsedTimeount()) {
			closeSocket();
			return true;
		}
		return false;
	}

	/**
	 * -1 表示 当前对象，小于指定对象
	 */
	public int compareTo(Object obj) {

		// 当前对象在用，排在前面
		if (this.isUsed)
			return -1;

		// 指定对象为空， 排到后面
		if (obj == null || !(obj instanceof SocketConnection))
			return -1;

		SocketConnection sc = (SocketConnection) obj;
		if (sc.isUsed)
			return 1; // 指定对象使用中，放到前面

		// 当前对象有效，而指定对象无效，指定对象放到后面
		if (this.socket == null && sc.socket == null)
			return 0;
		if (this.socket == null)
			return 1;
		if (sc.socket == null)
			return -1;

		// 哪个时间长，哪个放到后面
		if (sc.lastUsedTime > this.lastUsedTime)
			return 1;
		if (sc.lastUsedTime == this.lastUsedTime)
			return 0;
		return -1;

	}

	public synchronized void checkConn() throws Exception {
		if (this.socket != null)
			return;
		reCreateConn();
	}

	/**
	 * 检查并重新创建连接
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 * 
	 */
	public synchronized void reCreateConn() throws IOException {

		closeSocket();
		Socket tmpSocket = null;

		if (pool.isSSL()) {
			SSLSocketFactory factory = null;
			if (pool.getCustomSsf() == null)
				factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			else {
				factory = pool.getCustomSsf();
			}
			tmpSocket = (SSLSocket) factory.createSocket(pool.getHost(), pool.getPort());
		} else {
			tmpSocket = new Socket(pool.getHost(), pool.getPort());
		}
		tmpSocket.setSoTimeout(pool.getReadTimeount());
		tmpSocket.setTcpNoDelay(true);
//		tmpSocket.setSoLinger(true, 0);

		this.socket = tmpSocket;
	}

	public synchronized void closeSocket() {
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (Throwable e) {
			}
			this.socket = null;
		}
	}

	/**
	 * @param bindpoint
	 * @throws IOException
	 * @see java.net.Socket#bind(java.net.SocketAddress)
	 */
	public void bind(SocketAddress bindpoint) throws IOException {
		socket.bind(bindpoint);
	}

	/**
	 * @throws IOException
	 * @see java.net.Socket#close()
	 */
	public void close() throws IOException {
		this.isUsed = false;
		this.lastUsedTime = System.currentTimeMillis();
		pool.freeConnection(this);
	}

	/**
	 * @param endpoint
	 * @param timeout
	 * @throws IOException
	 * @see java.net.Socket#connect(java.net.SocketAddress, int)
	 */
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		socket.connect(endpoint, timeout);
	}

	/**
	 * @param endpoint
	 * @throws IOException
	 * @see java.net.Socket#connect(java.net.SocketAddress)
	 */
	public void connect(SocketAddress endpoint) throws IOException {
		socket.connect(endpoint);
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return socket.equals(obj);
	}

	/**
	 * @return
	 * @see java.net.Socket#getChannel()
	 */
	public SocketChannel getChannel() {
		return socket.getChannel();
	}

	/**
	 * @return
	 * @see java.net.Socket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see java.net.Socket#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getKeepAlive()
	 */
	public boolean getKeepAlive() throws SocketException {
		return socket.getKeepAlive();
	}

	/**
	 * @return
	 * @see java.net.Socket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	/**
	 * @return
	 * @see java.net.Socket#getLocalPort()
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	/**
	 * @return
	 * @see java.net.Socket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getOOBInline()
	 */
	public boolean getOOBInline() throws SocketException {
		return socket.getOOBInline();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see java.net.Socket#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	/**
	 * @return
	 * @see java.net.Socket#getPort()
	 */
	public int getPort() {
		return socket.getPort();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getReceiveBufferSize()
	 */
	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}

	/**
	 * @return
	 * @see java.net.Socket#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getReuseAddress()
	 */
	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getSendBufferSize()
	 */
	public int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getSoLinger()
	 */
	public int getSoLinger() throws SocketException {
		return socket.getSoLinger();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getSoTimeout()
	 */
	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getTcpNoDelay()
	 */
	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}

	/**
	 * @return
	 * @throws SocketException
	 * @see java.net.Socket#getTrafficClass()
	 */
	public int getTrafficClass() throws SocketException {
		return socket.getTrafficClass();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return socket.hashCode();
	}

	/**
	 * @return
	 * @see java.net.Socket#isBound()
	 */
	public boolean isBound() {
		return socket.isBound();
	}

	/**
	 * @return
	 * @see java.net.Socket#isClosed()
	 */
	public boolean isClosed() {
		return socket.isClosed();
	}

	/**
	 * @return
	 * @see java.net.Socket#isConnected()
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}

	/**
	 * @return
	 * @see java.net.Socket#isInputShutdown()
	 */
	public boolean isInputShutdown() {
		return socket.isInputShutdown();
	}

	/**
	 * @return
	 * @see java.net.Socket#isOutputShutdown()
	 */
	public boolean isOutputShutdown() {
		return socket.isOutputShutdown();
	}

	/**
	 * @param data
	 * @throws IOException
	 * @see java.net.Socket#sendUrgentData(int)
	 */
	public void sendUrgentData(int data) throws IOException {
		socket.sendUrgentData(data);
	}

	/**
	 * @param on
	 * @throws SocketException
	 * @see java.net.Socket#setKeepAlive(boolean)
	 */
	public void setKeepAlive(boolean on) throws SocketException {
		socket.setKeepAlive(on);
	}

	/**
	 * @param on
	 * @throws SocketException
	 * @see java.net.Socket#setOOBInline(boolean)
	 */
	public void setOOBInline(boolean on) throws SocketException {
		socket.setOOBInline(on);
	}

	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.Socket#setReceiveBufferSize(int)
	 */
	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}

	/**
	 * @param on
	 * @throws SocketException
	 * @see java.net.Socket#setReuseAddress(boolean)
	 */
	public void setReuseAddress(boolean on) throws SocketException {
		socket.setReuseAddress(on);
	}

	/**
	 * @param size
	 * @throws SocketException
	 * @see java.net.Socket#setSendBufferSize(int)
	 */
	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}

	/**
	 * @param on
	 * @param linger
	 * @throws SocketException
	 * @see java.net.Socket#setSoLinger(boolean, int)
	 */
	public void setSoLinger(boolean on, int linger) throws SocketException {
		socket.setSoLinger(on, linger);
	}

	/**
	 * @param timeout
	 * @throws SocketException
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	/**
	 * @param on
	 * @throws SocketException
	 * @see java.net.Socket#setTcpNoDelay(boolean)
	 */
	public void setTcpNoDelay(boolean on) throws SocketException {
		socket.setTcpNoDelay(on);
	}

	/**
	 * @param tc
	 * @throws SocketException
	 * @see java.net.Socket#setTrafficClass(int)
	 */
	public void setTrafficClass(int tc) throws SocketException {
		socket.setTrafficClass(tc);
	}

	/**
	 * @throws IOException
	 * @see java.net.Socket#shutdownInput()
	 */
	public void shutdownInput() throws IOException {
		socket.shutdownInput();
	}

	/**
	 * @throws IOException
	 * @see java.net.Socket#shutdownOutput()
	 */
	public void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}

	/**
	 * @return
	 * @see java.net.Socket#toString()
	 */
	public synchronized String toString() {
		return "name:" + name + " isUsed:" + isUsed + " lastUseTime:" + lastUsedTime + " Socket:" + this.socket;
	}
}
