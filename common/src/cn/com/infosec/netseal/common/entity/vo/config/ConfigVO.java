package cn.com.infosec.netseal.common.entity.vo.config;

public class ConfigVO {

	private String dbType;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private String initialSize;// 初始化连接数
	private String minIdle;// 最小空闲连接
	private String maxActive;// 最大连接数
	private String maxWait;// 最大等待时间

	private String logFileSize;// 日志文件大小
	private String logFileNum;// 日志文件个数
	private String syslogIp;
	private String syslogFacility;
	private String logToFile;
	private String logToDB;
	private String logToSyslog;

	private String tablePrefixId; // 表前缀id
	private String logFilterPattern; // 日志过滤正则表达式
	private String transCertDn; // 证书DN符号转化
	private String cryptoMode;// 加密方式 SOFT

	private String ldapContextFactory;
	private String ldapUrl;
	private String ldapSecurityAuthentication;
	private String ldapBaseDn;
	private String ldapAccount;
	private String ldapPassword;
	private String ldapFilter;
	private int ldapInterval;

	private String listenerSelfIp; // 管理绑定IP
	private int listenerSelfPort; // 管理端口
	private String listenerBusinessIp; // 业务绑定IP
	private int listenerBusinessPort; // 业务端口

	private boolean checkMac;
	private String checkStatus;// 系统状态

	private String networkCard; // license绑定网口
	private String wsUrl; // WebService地址
	private long serverKeyId; // 服务器制章使用的证书
	private String gmOid; // 国密OID值

	private int checkSocketNumLimit;
	private int checkCertNumLimit;

	private boolean cacheCertDb;
	private boolean cacheSealDb;
	private boolean cacheSealFile;

	private String ntpIp;
	private int ntpInterval;

	private String tsaRsaUrl;
	private String tsaRsaUsername;
	private String tsaRsaUserpwd;
	private String tsaRsaPolicy;
	private boolean tsaRsaUsetsa;
	
	private String tsaSM2Url;
	private String tsaSM2Username;
	private String tsaSM2Userpwd;
	private String tsaSM2Policy;
	private boolean tsaSM2Usetsa;
	
	public String getTsaRsaUrl() {
		return tsaRsaUrl;
	}

	public void setTsaRsaUrl(String tsaRsaUrl) {
		this.tsaRsaUrl = tsaRsaUrl;
	}

	public String getTsaRsaUsername() {
		return tsaRsaUsername;
	}

	public void setTsaRsaUsername(String tsaRsaUsername) {
		this.tsaRsaUsername = tsaRsaUsername;
	}

	public String getTsaRsaUserpwd() {
		return tsaRsaUserpwd;
	}

	public void setTsaRsaUserpwd(String tsaRsaUserpwd) {
		this.tsaRsaUserpwd = tsaRsaUserpwd;
	}

	public String getTsaRsaPolicy() {
		return tsaRsaPolicy;
	}

	public void setTsaRsaPolicy(String tsaRsaPolicy) {
		this.tsaRsaPolicy = tsaRsaPolicy;
	}

	public boolean isTsaRsaUsetsa() {
		return tsaRsaUsetsa;
	}

	public void setTsaRsaUsetsa(boolean tsaRsaUsetsa) {
		this.tsaRsaUsetsa = tsaRsaUsetsa;
	}

	public String getTsaSM2Url() {
		return tsaSM2Url;
	}

	public void setTsaSM2Url(String tsaSM2Url) {
		this.tsaSM2Url = tsaSM2Url;
	}

	public String getTsaSM2Username() {
		return tsaSM2Username;
	}

	public void setTsaSM2Username(String tsaSM2Username) {
		this.tsaSM2Username = tsaSM2Username;
	}

	public String getTsaSM2Userpwd() {
		return tsaSM2Userpwd;
	}

	public void setTsaSM2Userpwd(String tsaSM2Userpwd) {
		this.tsaSM2Userpwd = tsaSM2Userpwd;
	}

	public String getTsaSM2Policy() {
		return tsaSM2Policy;
	}

	public void setTsaSM2Policy(String tsaSM2Policy) {
		this.tsaSM2Policy = tsaSM2Policy;
	}

	public boolean isTsaSM2Usetsa() {
		return tsaSM2Usetsa;
	}

	public void setTsaSM2Usetsa(boolean tsaSM2Usetsa) {
		this.tsaSM2Usetsa = tsaSM2Usetsa;
	}

	

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(String initialSize) {
		this.initialSize = initialSize;
	}

	public String getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(String minIdle) {
		this.minIdle = minIdle;
	}

	public String getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(String maxActive) {
		this.maxActive = maxActive;
	}

	public String getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(String maxWait) {
		this.maxWait = maxWait;
	}

	public String getLogFileSize() {
		return logFileSize;
	}

	public void setLogFileSize(String logFileSize) {
		this.logFileSize = logFileSize;
	}

	public String getLogFileNum() {
		return logFileNum;
	}

	public void setLogFileNum(String logFileNum) {
		this.logFileNum = logFileNum;
	}

	public String getSyslogIp() {
		return syslogIp;
	}

	public void setSyslogIp(String syslogIp) {
		this.syslogIp = syslogIp;
	}

	public String getSyslogFacility() {
		return syslogFacility;
	}

	public void setSyslogFacility(String syslogFacility) {
		this.syslogFacility = syslogFacility;
	}

	public String getLogToFile() {
		return logToFile;
	}

	public void setLogToFile(String logToFile) {
		this.logToFile = logToFile;
	}

	public String getLogToDB() {
		return logToDB;
	}

	public void setLogToDB(String logToDB) {
		this.logToDB = logToDB;
	}

	public String getLogToSyslog() {
		return logToSyslog;
	}

	public void setLogToSyslog(String logToSyslog) {
		this.logToSyslog = logToSyslog;
	}

	public String getTablePrefixId() {
		return tablePrefixId;
	}

	public void setTablePrefixId(String tablePrefixId) {
		this.tablePrefixId = tablePrefixId;
	}

	public String getLogFilterPattern() {
		return logFilterPattern;
	}

	public void setLogFilterPattern(String logFilterPattern) {
		this.logFilterPattern = logFilterPattern;
	}

	public String getTransCertDn() {
		return transCertDn;
	}

	public void setTransCertDn(String transCertDn) {
		this.transCertDn = transCertDn;
	}

	public String getCryptoMode() {
		return cryptoMode;
	}

	public void setCryptoMode(String cryptoMode) {
		this.cryptoMode = cryptoMode;
	}

	public String getLdapContextFactory() {
		return ldapContextFactory;
	}

	public void setLdapContextFactory(String ldapContextFactory) {
		this.ldapContextFactory = ldapContextFactory;
	}

	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getLdapSecurityAuthentication() {
		return ldapSecurityAuthentication;
	}

	public void setLdapSecurityAuthentication(String ldapSecurityAuthentication) {
		this.ldapSecurityAuthentication = ldapSecurityAuthentication;
	}

	public String getLdapBaseDn() {
		return ldapBaseDn;
	}

	public void setLdapBaseDn(String ldapBaseDn) {
		this.ldapBaseDn = ldapBaseDn;
	}

	public String getLdapAccount() {
		return ldapAccount;
	}

	public void setLdapAccount(String ldapAccount) {
		this.ldapAccount = ldapAccount;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getLdapFilter() {
		return ldapFilter;
	}

	public void setLdapFilter(String ldapFilter) {
		this.ldapFilter = ldapFilter;
	}

	public int getLdapInterval() {
		return ldapInterval;
	}

	public void setLdapInterval(int ldapInterval) {
		this.ldapInterval = ldapInterval;
	}

	public String getListenerSelfIp() {
		return listenerSelfIp;
	}

	public void setListenerSelfIp(String listenerSelfIp) {
		this.listenerSelfIp = listenerSelfIp;
	}

	public int getListenerSelfPort() {
		return listenerSelfPort;
	}

	public void setListenerSelfPort(int listenerSelfPort) {
		this.listenerSelfPort = listenerSelfPort;
	}

	public String getListenerBusinessIp() {
		return listenerBusinessIp;
	}

	public void setListenerBusinessIp(String listenerBusinessIp) {
		this.listenerBusinessIp = listenerBusinessIp;
	}

	public int getListenerBusinessPort() {
		return listenerBusinessPort;
	}

	public void setListenerBusinessPort(int listenerBusinessPort) {
		this.listenerBusinessPort = listenerBusinessPort;
	}

	public boolean isCheckMac() {
		return checkMac;
	}

	public void setCheckMac(boolean checkMac) {
		this.checkMac = checkMac;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getNetworkCard() {
		return networkCard;
	}

	public void setNetworkCard(String networkCard) {
		this.networkCard = networkCard;
	}

	public String getWsUrl() {
		return wsUrl;
	}

	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}

	public long getServerKeyId() {
		return serverKeyId;
	}

	public void setServerKeyId(long serverKeyId) {
		this.serverKeyId = serverKeyId;
	}

	public String getGmOid() {
		return gmOid;
	}

	public void setGmOid(String gmOid) {
		this.gmOid = gmOid;
	}

	public int getCheckSocketNumLimit() {
		return checkSocketNumLimit;
	}

	public void setCheckSocketNumLimit(int checkSocketNumLimit) {
		this.checkSocketNumLimit = checkSocketNumLimit;
	}

	public int getCheckCertNumLimit() {
		return checkCertNumLimit;
	}

	public void setCheckCertNumLimit(int checkCertNumLimit) {
		this.checkCertNumLimit = checkCertNumLimit;
	}

	public boolean isCacheCertDb() {
		return cacheCertDb;
	}

	public void setCacheCertDb(boolean cacheCertDb) {
		this.cacheCertDb = cacheCertDb;
	}

	public boolean isCacheSealDb() {
		return cacheSealDb;
	}

	public void setCacheSealDb(boolean cacheSealDb) {
		this.cacheSealDb = cacheSealDb;
	}

	public boolean isCacheSealFile() {
		return cacheSealFile;
	}

	public void setCacheSealFile(boolean cacheSealFile) {
		this.cacheSealFile = cacheSealFile;
	}

	public String getNtpIp() {
		return ntpIp;
	}

	public void setNtpIp(String ntpIp) {
		this.ntpIp = ntpIp;
	}

	public int getNtpInterval() {
		return ntpInterval;
	}

	public void setNtpInterval(int ntpInterval) {
		this.ntpInterval = ntpInterval;
	}

	

}
