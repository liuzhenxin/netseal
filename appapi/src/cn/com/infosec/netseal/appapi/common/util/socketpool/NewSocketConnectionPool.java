package cn.com.infosec.netseal.appapi.common.util.socketpool;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import cn.com.infosec.netseal.appapi.common.util.StringUtil;

/**
 * 新的Socket连接池
 * 
 * 支持 1. 自动关闭连接数(维护线程) 2. 多个连接池 3. SSL 4.
 * 
 */
public class NewSocketConnectionPool {

	public static final String DEFAULT = "DEFAULT";

	// 使用静态对象实现保存的多个连接池
	private static HashMap htPools = new HashMap();

	/**
	 * 设置配置参数,构造一个新的连接池,同时将原先的关闭池关闭
	 * 
	 * @param poolName
	 * @param prop
	 */
	public synchronized static void setProperties(String poolName, Properties prop, SSLSocketFactory ssf) {

		Object obj = htPools.get(poolName);
		if (obj != null) {
			return;
		}

		NewSocketConnectionPool newPool = new NewSocketConnectionPool(poolName, prop, ssf);
		htPools.put(poolName, newPool);
	}

	/**
	 * 获取一个连接池对象
	 * 
	 * @param poolName
	 *            连接池的名称
	 * 
	 * @return
	 */
	public static NewSocketConnectionPool getInstance(String poolName) {
		Object obj = htPools.get(poolName);
		if (obj == null || !(obj instanceof NewSocketConnectionPool))
			return null;
		else
			return (NewSocketConnectionPool) obj;
	}

	// 配置参数
	public final static String SERVER_IP = "ip"; //
	public final static String SERVER_PORT = "port";
	public final static String MAX_CONN_NUMBER = "maxconn"; // 最大连接数与最小连接数
	public final static String MIN_CONN_NUMBER = "minconn";
	public final static String GETCONN_TIMEOUT = "getconntimeout"; // 获取连接数等待时间
	public final static String READ_TIMEOUT = "readtimeout"; // 读超时时间

	// 以下是Socket池维护线程的配置
	public final static String UNUSED_TIMEOUT = "unusedtimeout"; // 长时间未用超时时间,超过这个时间,则关闭连接池
	public final static String CHECK_INTERVAL = "intervaltime"; // 检测线程的运行间隔

	private Properties prop = null;

	private String poolName = null;

	private String host = null;
	private int port = 0;

	private int maxNumConn = 100;
	private int minNumConn = 100;

	private int getConnTimeout = 0; // 单位：秒
	private int readTimeount = 0;

	private int unUsedTimeount = 0; // 单位:秒
	private int threadCheckIntervalTime = 0; // 检测线程运行的间隔时间

	private boolean isSSL = false;
	private SSLSocketFactory customSsf = null; // 自定义的SSLSocket创建工厂 2009.9.7

	private List freeSocketList = new LinkedList();
	private ArrayList allSocketList = new ArrayList();
	private Object syncObj = new Object();

	private MaintainSocketPoolThread threadMaintainPool = null;

	// --- 方法
	public NewSocketConnectionPool(String poolName, Properties prop, SSLSocketFactory ssf) {

		// 1. 属性赋值
		this.poolName = poolName;

		// 2. 解析配置参数
		if (prop.containsKey(SERVER_IP)) {
			host = prop.getProperty(SERVER_IP);
		}
		port = StringUtil.parseStringWithDefault(prop.getProperty(SERVER_PORT), port);

		maxNumConn = StringUtil.parseStringWithDefault(prop.getProperty(MAX_CONN_NUMBER), maxNumConn);
		minNumConn = StringUtil.parseStringWithDefault(prop.getProperty(MIN_CONN_NUMBER), minNumConn);

		getConnTimeout = StringUtil.parseStringWithDefault(prop.getProperty(GETCONN_TIMEOUT), getConnTimeout) * 1000;
		readTimeount = StringUtil.parseStringWithDefault(prop.getProperty(READ_TIMEOUT), readTimeount) * 1000;

		unUsedTimeount = StringUtil.parseStringWithDefault(prop.getProperty(UNUSED_TIMEOUT), unUsedTimeount) * 1000;
		threadCheckIntervalTime = StringUtil.parseStringWithDefault(prop.getProperty(CHECK_INTERVAL), threadCheckIntervalTime) * 1000;

		if (ssf != null) {
			this.isSSL = true;
			this.customSsf = ssf;
		} else
			this.isSSL = false;

		// 3. 初始化连接池
		for (int i = 1; i <= maxNumConn; i++) {
			SocketConnection sc = new SocketConnection(this, i);
			freeSocketList.add(sc);
			allSocketList.add(sc);
		}

		// 4. 启动检测线程
		if (unUsedTimeount > 0 && threadCheckIntervalTime > 0) {
			// 创建Socket池维护线程,并启动
			threadMaintainPool = new MaintainSocketPoolThread(this);
			Thread t = new Thread(threadMaintainPool);
			t.start();
		}

		SocketPoolShutDownHook spsd = new SocketPoolShutDownHook(this);
		Runtime.getRuntime().addShutdownHook(spsd);

	}

	/**
	 * 关闭连接
	 */
	public void release() {
		System.out.println("pool release (name:" + this.poolName + ")");
		// 1. 关闭所有连接
		for (int i = 0; i < allSocketList.size(); i++) {
			SocketConnection conn = (SocketConnection) allSocketList.get(i);
			conn.closeSocket();
		}

		allSocketList.clear();
		freeSocketList.clear();

		// 2. 停止维护线程
		if (threadMaintainPool != null)
			threadMaintainPool.setRun(false); // 关闭线程

	}

	public boolean isPooled() {
		return (maxNumConn > 0);
	}

	/**
	 * 获取一个新的连接
	 * 
	 * @return
	 */
	public Socket getConnection() throws Exception {

		// 1. 不使用连接池
		if (!isPooled()) {
			SocketConnection sc = new SocketConnection(this, 0);
			sc.setUsedStatus();
			return sc;
		}

		// 2. 获取可用连接
		long expiredTime = System.currentTimeMillis() + this.getConnTimeout;
		SocketConnection sc = null;
		while (sc == null) {
			synchronized (syncObj) {
				// log("freeSocketList.size():" + freeSocketList.size());
				// 1. 获取连接
				if (freeSocketList.size() > 0) {
					sc = (SocketConnection) freeSocketList.remove(0);
					break;
				}

				// 2. 没有取到连接, 等待
				if (this.getConnTimeout <= 0) {
					try {
						syncObj.wait();
					} catch (Exception e) {
					}
				} else {
					long remainTimeInMillSec = expiredTime - System.currentTimeMillis();
					if (remainTimeInMillSec <= 0)
						break;
					else
						try {
							syncObj.wait(remainTimeInMillSec);
						} catch (Exception e) {
						}
				}
			}
		}

		// 3. 等待超时
		if (sc == null) {
			return null;
		}

		try {
			// 4. 检查连接状态
			sc.setUsedStatus();
		} catch (Throwable e) {
			synchronized (syncObj) {
				freeSocketList.add(sc);
				try {
					syncObj.notifyAll();
				} catch (Exception e3) {
				}
			}
			throw new Exception(e.getMessage());
		}

		return sc;
	}

	/**
	 * 将一个连接放回池中
	 * 
	 * @param socket
	 */
	public void freeConnection(Socket socket) {

		if (!isPooled()) {
			try {
				if (socket instanceof SocketConnection) {
					SocketConnection sc = (SocketConnection) socket;
					sc.closeSocket();
				} else
					socket.close();
			} catch (Throwable e) {
			}
			return; // 不用放到池中
		}

		synchronized (syncObj) {
			freeSocketList.add(0, socket);
			try {
				syncObj.notifyAll();
			} catch (IllegalMonitorStateException e) {
			}
		}
	}

	/**
	 * @return the customSsf
	 */
	public SSLSocketFactory getCustomSsf() {
		return customSsf;
	}

	/**
	 * @param customSsf
	 *            the customSsf to set
	 */
	public void setCustomSsf(SSLSocketFactory customSsf) {
		this.customSsf = customSsf;
	}

	/**
	 * @return the isSSL
	 */
	public boolean isSSL() {
		return isSSL;
	}

	/**
	 * @param isSSL
	 *            the isSSL to set
	 */
	public void setSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the maxNumConn
	 */
	public int getMaxNumConn() {
		return maxNumConn;
	}

	/**
	 * @param maxNumConn
	 *            the maxNumConn to set
	 */
	public void setMaxNumConn(int maxNumConn) {
		this.maxNumConn = maxNumConn;
	}

	/**
	 * @return the minNumConn
	 */
	public int getMinNumConn() {
		return minNumConn;
	}

	/**
	 * @param minNumConn
	 *            the minNumConn to set
	 */
	public void setMinNumConn(int minNumConn) {
		this.minNumConn = minNumConn;
	}

	/**
	 * @return the poolName
	 */
	public String getPoolName() {
		return poolName;
	}

	/**
	 * @param poolName
	 *            the poolName to set
	 */
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the readTimeount
	 */
	public int getReadTimeount() {
		return readTimeount;
	}

	/**
	 * @param readTimeount
	 *            the readTimeount to set
	 */
	public void setReadTimeount(int readTimeount) {
		this.readTimeount = readTimeount;
	}

	/**
	 * @return the threadCheckIntervalTime
	 */
	public int getThreadCheckIntervalTime() {
		return threadCheckIntervalTime;
	}

	/**
	 * @param threadCheckIntervalTime
	 *            the threadCheckIntervalTime to set
	 */
	public void setThreadCheckIntervalTime(int threadCheckIntervalTime) {
		this.threadCheckIntervalTime = threadCheckIntervalTime;
	}

	/**
	 * @return the unUsedTimeount
	 */
	public int getUnUsedTimeount() {
		return unUsedTimeount;
	}

	/**
	 * @param unUsedTimeount
	 *            the unUsedTimeount to set
	 */
	public void setUnUsedTimeount(int unUsedTimeount) {
		this.unUsedTimeount = unUsedTimeount;
	}

	public ArrayList getAllSocketList() {
		return allSocketList;
	}

	public void setAllSocketList(ArrayList allSocketList) {
		this.allSocketList = allSocketList;
	}

	public List getFreeSocketList() {
		return freeSocketList;
	}

	public void setFreeSocketList(List freeSocketList) {
		this.freeSocketList = freeSocketList;
	}
}
