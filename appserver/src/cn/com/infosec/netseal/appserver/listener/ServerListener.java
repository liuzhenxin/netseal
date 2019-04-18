package cn.com.infosec.netseal.appserver.listener;

/**
 * Title: 抽象的服务器监听器 Description: 抽象的服务器监听器
 */

public abstract class ServerListener implements Runnable {

	protected String name;
	protected String ip;
	protected int port;
	protected int backlog;
	protected int timeout; // 单位 秒

	// SSL 相关配置
	protected boolean ssl = false;
	protected boolean authClient = false;
	protected String truststore;
	protected String trustpwd;
	protected String keystore;
	protected String keypwd;

	// 对端属性
	protected String clientHost;
	protected volatile boolean started = false;

	// 数字信封配置
	protected boolean envelope = false;

	public void start() {
		started = true;
	}

	public void shutDown() {
		started = false;
	}

	public boolean isStarted() {
		return started;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean getAuthClient() {
		return authClient;
	}

	public void setAuthClient(boolean authClient) {
		this.authClient = authClient;
	}

	public String getTruststore() {
		return truststore;
	}

	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	public String getTrustpwd() {
		return trustpwd;
	}

	public void setTrustpwd(String trustpwd) {
		this.trustpwd = trustpwd;
	}

	public String getKeystore() {
		return keystore;
	}

	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public String getKeypwd() {
		return keypwd;
	}

	public void setKeypwd(String keypwd) {
		this.keypwd = keypwd;
	}

	public String getClientHost() {
		return clientHost;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public boolean isEnvelope() {
		return envelope;
	}

	public void setEnvelope(boolean envelope) {
		this.envelope = envelope;
	}

}