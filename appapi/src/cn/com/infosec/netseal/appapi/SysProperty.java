package cn.com.infosec.netseal.appapi;

/**
 * API系统属性类
 */
public class SysProperty {
	private String serverIP;
	private int port;

	private String trustStore = "";
	private String trustStorePwd = "";
	private String clientStore = "";
	private String clientStorePwd = "";

	private int maxConn = 0;
	private int minConn = 0;

	private int getConnTimeout = 0; // 单位：秒
	private int readTimeout = 0;

	private int unUsedTimeout = 0; // 单位:秒
	private int threadCheckIntervalTime = 0; // 检测线程运行的间隔时间

	private String serverCertPath = ""; // 数字信封的加密证书
	private String clientCertPath = ""; // 数字信封通知服务器用证书创建信封返回
	private String clientKeyPath = "";// 数字信封的密钥文件路径
	private String clientKeyPwd = "";// 数字信封的密钥文件密码

	/**
	 * 使用验证服务器IP和端口构造系统属性类对象
	 * 
	 * @param ip
	 *            验证服务器IP
	 * @param port
	 *            验证服务器端口
	 */
	public SysProperty(String ip, int port) {
		if (ip == null)
			serverIP = "";
		else
			serverIP = ip;
		this.port = port;
	}

	/**
	 * 获取验证服务器端口设置
	 * 
	 * @return 验证服务器端口
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 设置验证服务器端口
	 * 
	 * @param port
	 *            验证服务器端口
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 获取验证服务器IP地址设置
	 * 
	 * @return 验证服务器IP地址设置
	 */
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * 设置验证服务器IP地址
	 * 
	 * @param serverIP
	 *            验证服务器IP地址
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	/**
	 * 设置Socket连接池大小
	 * 
	 * 0 默认值，表示不使用连接池，每次与服务器通讯都建立新的Socket
	 * 
	 * @param maxconn
	 *            Socket连接池大小
	 */
	public void setMaxConn(int maxconn) {
		this.maxConn = maxconn;
	}

	/**
	 * 获取Socket连接池的大小
	 * 
	 * @return Socket连接池的大小
	 */
	public int getMaxConn() {
		return maxConn;
	}

	/**
	 * 获取用于SSL通讯的客户端store文件,里面保存有客户端证书和密钥
	 * 
	 * @return store文件名
	 */
	public final String getClientStore() {
		return clientStore;
	}

	/**
	 * 设置客户端store文件, 里面保存有客户端证书及通讯密钥, 用于双向SSL通讯
	 * 
	 * @param clientStore
	 *            store文件名
	 */
	public final void setClientStore(String clientStore) {
		this.clientStore = clientStore;
	}

	/**
	 * 获取用于SSL通讯的客户端store文件的密码
	 * 
	 * @return store 客户端store文件密码
	 */
	public final String getClientStorePwd() {
		return clientStorePwd;
	}

	/**
	 * 设置客户端store文件的密码
	 * 
	 * @param clientStorePwd
	 *            密码
	 */
	public final void setClientStorePwd(String clientStorePwd) {
		this.clientStorePwd = clientStorePwd;
	}

	/**
	 * 返回客户端信任的store文件，里面保存客户端信任的CA证书，用于SSL通讯
	 * 
	 * @return store文件名
	 */
	public final String getTrustStore() {
		return trustStore;
	}

	/**
	 * 设置客户端API信任的store文件
	 * 
	 * @param trustStore
	 *            信任的store文件名
	 */
	public final void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * 
	 * 返回客户端信任的store文件的密码
	 * 
	 * @return 客户端信任的store文件的密码
	 */
	public String getTrustStorePwd() {
		return trustStorePwd;
	}

	/**
	 * 设置客户端信任的store文件的密码
	 * 
	 * @param trustStorePwd
	 *            客户端信任的store文件的密码
	 */
	public void setTrustStorePwd(String trustStorePwd) {
		this.trustStorePwd = trustStorePwd;
	}

	/**
	 * 获取通讯超时设置(单位:秒)
	 * 
	 * @return 通讯超时
	 */
	public int getTimeout() {
		return getConnTimeout;
	}

	/**
	 * 设置通讯超时时间(单位:秒),包括获取连接超时（GetConnTimout）和等待响应包超时（ReadTimeout）。
	 * 
	 * 0 为默认值，表示没有超时设置
	 */
	public void setTimeout(int seconds) {
		this.getConnTimeout = seconds;
		this.readTimeout = seconds;
	}

	/**
	 * 获取连接超时设置(单位:秒)
	 * 
	 * @return 连接超时
	 */
	public int getGetConnTimeout() {
		return getConnTimeout;
	}

	/**
	 * 设置连接超时(单位:秒)
	 * 
	 * @param getConnTimeout
	 *            连接超时
	 */
	public void setGetConnTimeout(int getConnTimeout) {
		this.getConnTimeout = getConnTimeout;
	}

	/**
	 * 获取等待服务器响应结果超时（单位:秒)
	 * 
	 * @return 等待接口超时
	 */
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * 设置读取响应信息超时（单位:秒)
	 * 
	 * @param readTimeount
	 *            等待服务器响应结果超时
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * 
	 * 获取维护线程收集间隔(单位:秒)
	 * 
	 * @return 时间间隔
	 */
	public int getRunIntervalTime() {
		return threadCheckIntervalTime;
	}

	/**
	 * 
	 * 设置池维护线程运行的时间间隔，0表示不运行(单位：秒）
	 * 
	 * @param threadCheckIntervalTime
	 *            时间间隔
	 */
	public void setRunIntervalTime(int intervalInSeconds) {
		this.threadCheckIntervalTime = intervalInSeconds;
	}

	/**
	 * 
	 * 获取Socket空闲超时（单位：秒）。 Socket空闲时间超过这个值后，将被维护线程关闭。
	 * 
	 * @return Socket空闲超时
	 */
	public int getConnUnUsedTimeout() {
		return unUsedTimeout;
	}

	/**
	 * 设置Socket连接未使用的超时时间，如果某个Socket连接，处于空闲状态的时间超过这个值，则将被池维护线程关闭， 但是如果池中已有连接数小于最小连接数，则不会关闭。
	 * 
	 * @param unUsedTimeount
	 *            Socket空闲超时
	 */
	public void setConnUnUsedTimeout(int unUsedTimeout) {
		this.unUsedTimeout = unUsedTimeout;
	}

	/**
	 * 
	 * 获取Socket池最小连接数。对于最小连接数以外的Socket连接，如果超过空闲时间，将被维护线程关闭。
	 * 
	 * @return 最小连接数
	 */
	public int getMinConn() {
		return minConn;
	}

	/**
	 * 设置Socket池最小连接数。对于最小连接数以外的Socket连接，如果超过空闲时间，将被维护线程关闭。
	 * 
	 * @param minConn
	 *            最小连接数
	 */
	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	/**
	 * 获取使用数字信封的服务器证书路径
	 * 
	 * @return
	 */
	public String getServerCertPath() {
		return serverCertPath;
	}

	/**
	 * 设置使用数字信封的服务器证书路径
	 * 
	 * @return
	 */
	public void setServerCertPath(String serverCertPath) {
		this.serverCertPath = serverCertPath;
	}

	/**
	 * 获取通知服务器用证书创建信封返回
	 * 
	 * @return
	 */
	public String getClientCertPath() {
		return clientCertPath;
	}

	/**
	 * 设置通知服务器用证书创建信封返回
	 * 
	 * @return
	 */
	public void setClientCertPath(String clientCertPath) {
		this.clientCertPath = clientCertPath;
	}

	/**
	 * 获取数字信封的密钥文件路径
	 * 
	 * @return
	 */
	public String getClientKeyPath() {
		return clientKeyPath;
	}

	/**
	 * 设置数字信封的密钥文件路径
	 * 
	 * @return
	 */
	public void setClientKeyPath(String clientKeyPath) {
		this.clientKeyPath = clientKeyPath;
	}

	/**
	 * 获取数字信封的密钥文件密码
	 * 
	 * @return
	 */
	public String getClientKeyPwd() {
		return clientKeyPwd;
	}

	/**
	 * 设置数字信封的密钥文件密码
	 * 
	 * @return
	 */
	public void setClientKeyPwd(String clientKeyPwd) {
		this.clientKeyPwd = clientKeyPwd;
	}

}
