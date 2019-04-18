package cn.com.infosec.netseal.common.config;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.lang.StringUtils;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.config.ConfigVO;
import cn.com.infosec.netseal.common.entity.vo.config.NetCertCaVO;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;
import cn.com.infosec.util.Base64;

public class ConfigUtil {
	private static final String propertiesPath = Constants.CONF_PATH + "config.properties";
	private static ConfigUtil instance;

	private Configuration properties;
	private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
	private String defaultValue = "";
	private int checkSocketNumLimit = Constants.DEFAULT_UNKNOWN_INT;
	private int checkCertNumLimit = Constants.DEFAULT_UNKNOWN_INT;

	private ConfigUtil() {
	}

	public static synchronized ConfigUtil getInstance() {
		if (instance == null) {
			ConfigUtil config = new ConfigUtil();
			try {
				config.load();
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.LOAD_CONF_ERROR, e.getMessage());
			}
			instance = config;
		}
		return instance;
	}

	public synchronized void reload() {
		try {
			instance.load();
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.RELOAD_CONF_ERROR, e.getMessage());
		}
	}

	private synchronized void load() {
		try {
			// 读取属性文件 .properties
			Parameters params = new Parameters();
			builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);
			builder.configure(params.properties().setFileName(propertiesPath).setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
			properties = builder.getConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetSealRuntimeException(ErrCode.LOAD_CONF_ERROR, e.getMessage());
		}
	}

	public synchronized void saveDBConfig(ConfigVO configVO) throws Exception {
		properties.setProperty("jdbc.driverClassName", configVO.getDriverClassName());
		properties.setProperty("jdbc.url", configVO.getUrl());
		properties.setProperty("jdbc.username", configVO.getUsername());
		properties.setProperty("jdbc.password", Base64.encode(configVO.getPassword()));
		properties.setProperty("jdbc.password.encode", true);
		properties.setProperty("dataSource.dbType", getDbTypeFromUrl(configVO.getUrl()));
		properties.setProperty("dataSource.initialSize", configVO.getInitialSize());
		properties.setProperty("dataSource.minIdle", configVO.getMinIdle());
		properties.setProperty("dataSource.maxActive", configVO.getMaxActive());
		properties.setProperty("dataSource.maxWait", configVO.getMaxWait());
		builder.save();
	}

	public synchronized void saveSignKeyId(Long keyId) throws Exception {
		properties.setProperty("crypto.sign.keyId", keyId);
		builder.save();
	}

	public synchronized void saveEncryptKeyId(Long keyId) throws Exception {
		properties.setProperty("crypto.encrypt.keyId", keyId);
		builder.save();
	}

	public synchronized void saveLogConfig(ConfigVO configVO) throws Exception {
		properties.setProperty("log.file_size", configVO.getLogFileSize());
		properties.setProperty("log.file_num", configVO.getLogFileNum());
		properties.setProperty("log.syslog.ip", configVO.getSyslogIp());
		properties.setProperty("log.syslog.facility", configVO.getSyslogFacility());
		properties.setProperty("log.toFile", configVO.getLogToFile());
		properties.setProperty("log.toDB", configVO.getLogToDB());
		properties.setProperty("log.toSyslog", configVO.getLogToSyslog());
		builder.save();

		// 重置日志
		LoggerConfig.reset();
	}

	public synchronized void saveCheckStatus(String checkStatus) throws Exception {
		properties.setProperty("check.status", checkStatus);
		builder.save();
	}

	/**
	 * 加密保存数据库密码
	 * 
	 * @throws Exception
	 */
	public synchronized void saveDBPassword() throws Exception {
		properties.setProperty("jdbc.password", Base64.encode(this.getPassword()));
		properties.setProperty("jdbc.password.encode", true);
		builder.save();
	}

	/**
	 * 加密保存LDAP密码
	 * 
	 * @throws Exception
	 */
	public synchronized void saveLdapPassword() throws Exception {
		properties.setProperty("ldap.password", Base64.encode(this.getLdapPassword()));
		properties.setProperty("ldap.password.encode", true);
		builder.save();
	}

	/**
	 * 加密保存JKS密码
	 * 
	 * @throws Exception
	 */
	public synchronized void saveBusinessTrustPassword() throws Exception {
		properties.setProperty("listener.business.trustpwd", Base64.encode(this.getBusinessTrustPassword()));
		properties.setProperty("listener.business.trustpwd.encode", true);
		builder.save();
	}

	/**
	 * 加密保存JKS密码
	 * 
	 * @throws Exception
	 */
	public synchronized void saveBusinessKeyPassword() throws Exception {
		properties.setProperty("listener.business.keypwd", Base64.encode(this.getBusinessKeyPassword()));
		properties.setProperty("listener.business.keypwd.encode", true);
		builder.save();
	}

	public synchronized void saveLdapConfig(ConfigVO configVO) throws Exception {
		properties.setProperty("ldap.context_factory", configVO.getLdapContextFactory());
		properties.setProperty("ldap.url", configVO.getLdapUrl());
		properties.setProperty("ldap.security_authentication", configVO.getLdapSecurityAuthentication());
		properties.setProperty("ldap.base_dn", configVO.getLdapBaseDn());
		properties.setProperty("ldap.account", configVO.getLdapAccount());
		properties.setProperty("ldap.password", Base64.encode(this.getLdapPassword()));
		properties.setProperty("ldap.password.encode", true);
		properties.setProperty("ldap.filter", configVO.getLdapFilter());
		properties.setProperty("ldap.interval", configVO.getLdapInterval());
		builder.save();
	}

	/**
	 * 保存系统基本配置
	 * 
	 * @throws Exception
	 */
	public synchronized void saveSysConfigInit(ConfigVO configVO) throws Exception {
		properties.setProperty("table.prefix.id", configVO.getTablePrefixId());
		properties.setProperty("network.card", configVO.getNetworkCard());
		properties.setProperty("ws.url", configVO.getWsUrl());
		builder.save();
	}

	/**
	 * 保存ntp配置
	 */
	public synchronized void saveNtpConfig(ConfigVO configVO) throws Exception {
		properties.setProperty("ntp.ip", configVO.getNtpIp());
		properties.setProperty("ntp.initDelay", configVO.getNtpInterval());
		properties.setProperty("ntp.interval", configVO.getNtpInterval());
		builder.save();
	}

	/**
	 * 保存时间戳配置
	 */
	public synchronized void saveTsaRsaConfig(ConfigVO configVO) throws Exception {
		properties.setProperty("tsa.rsa.url", configVO.getTsaRsaUrl());
		properties.setProperty("tsa.rsa.username", configVO.getTsaRsaUsername());
		properties.setProperty("tsa.rsa.userpwd", configVO.getTsaRsaUserpwd());
		properties.setProperty("tsa.rsa.policy", configVO.getTsaRsaPolicy());
		properties.setProperty("tsa.rsa.usetsa", configVO.isTsaRsaUsetsa());
		builder.save();
	}

	public synchronized void saveTsaSM2Config(ConfigVO configVO) throws Exception {
		properties.setProperty("tsa.sm2.url", configVO.getTsaSM2Url());
		properties.setProperty("tsa.sm2.username", configVO.getTsaSM2Username());
		properties.setProperty("tsa.sm2.userpwd", configVO.getTsaSM2Userpwd());
		properties.setProperty("tsa.sm2.policy", configVO.getTsaSM2Policy());
		properties.setProperty("tsa.sm2.usetsa", configVO.isTsaSM2Usetsa());
		builder.save();
	}

	/**
	 * 设置License配置
	 * 
	 * @param checkCertNumLimit
	 * @param checkSocketNumLimit
	 */
	public synchronized void setLicenseConfig(int checkCertNumLimit, int checkSocketNumLimit) {
		this.checkCertNumLimit = checkCertNumLimit;
		this.checkSocketNumLimit = checkSocketNumLimit;
	}

	public String getDriverClassName() {
		return properties.getString("jdbc.driverClassName", defaultValue);
	}

	public String getUrl() {
		return properties.getString("jdbc.url", defaultValue);
	}

	public String getUsername() {
		return properties.getString("jdbc.username", defaultValue);
	}

	public String getPassword() {
		String password = properties.getString("jdbc.password", defaultValue);
		String encode = properties.getString("jdbc.password.encode");
		if ("true".equalsIgnoreCase(encode))
			try {
				password = new String(Base64.decode(password));
			} catch (IOException e) {
				LoggerUtil.errorlog("jdbc password base64 decode error, ", e);
			}
		return password;
	}

	public String getMaxActive() {
		return properties.getString("dataSource.maxActive", defaultValue);
	}

	public String getMinIdle() {
		return properties.getString("dataSource.minIdle", defaultValue);
	}

	public String getInitialSize() {
		return properties.getString("dataSource.initialSize", defaultValue);
	}

	public String getMaxWait() {
		return properties.getString("dataSource.maxWait", defaultValue);
	}

	public String getCryptoMode() {
		return properties.getString("crypto.mode", defaultValue);
	}

	public String getLogFileSize() {
		return properties.getString("log.file_size", defaultValue);
	}

	public String getLogFileNum() {
		return properties.getString("log.file_num", defaultValue);
	}

	public String getSyslogIp() {
		return properties.getString("log.syslog.ip", defaultValue);
	}

	public String getSyslogFacility() {
		return properties.getString("log.syslog.facility", defaultValue);
	}

	public String getLogToFile() {
		List<Object> list = properties.getList("log.toFile");
		String logToFile = StringUtils.join(list.toArray(), ",");
		return logToFile;
	}

	public String getLogToDB() {
		List<Object> list = properties.getList("log.toDB");
		String logToDB = StringUtils.join(list.toArray(), ",");
		return logToDB;
	}

	public String getLogToSyslog() {
		List<Object> list = properties.getList("log.toSyslog");
		String logToSyslog = StringUtils.join(list.toArray(), ",");
		return logToSyslog;
	}

	public String getTablePrefixId() {
		String tablePrefixId = properties.getString("table.prefix.id", defaultValue);
		return tablePrefixId;
	}

	public String getLogFilterPattern() {
		return properties.getString("log.filter.pattern", defaultValue);
	}

	public String getCheckStatus() {
		return properties.getString("check.status", "0");
	}

	public boolean getCheckMac() {
		return properties.getBoolean("check.mac", false);
	}

	public boolean getCheckSealSignData() {
		return properties.getBoolean("check.seal.signData", false);
	}

	public String getTransCertDn() {
		List<Object> list = properties.getList("trans.cert_dn");
		String transCertDn = StringUtils.join(list.toArray(), ",");
		return transCertDn;
	}

	public String getLdapContextFactory() {
		return properties.getString("ldap.context_factory", defaultValue);
	}

	public String getLdapUrl() {
		return properties.getString("ldap.url", defaultValue);
	}

	public String getLdapSecurityAuthentication() {
		return properties.getString("ldap.security_authentication", defaultValue);
	}

	public String getLdapBaseDn() {
		List<Object> list = properties.getList("ldap.base_dn");
		String ldapBaseDn = StringUtils.join(list.toArray(), ",");
		return ldapBaseDn;
	}

	public String getLdapAccount() {
		List<Object> list = properties.getList("ldap.account");
		String ldapAccount = StringUtils.join(list.toArray(), ",");
		return ldapAccount;
	}

	public String getLdapPassword() {
		String ldapPassword = properties.getString("ldap.password", defaultValue);
		String encode = properties.getString("ldap.password.encode");
		if ("true".equalsIgnoreCase(encode))
			try {
				ldapPassword = new String(Base64.decode(ldapPassword));
			} catch (IOException e) {
				LoggerUtil.errorlog("ldap password base64 decode error, ", e);
			}
		return ldapPassword;
	}

	public String getBusinessTrustPassword() {
		String trustPassword = properties.getString("listener.business.trustpwd", defaultValue);
		String encode = properties.getString("listener.business.trustpwd.encode");
		if ("true".equalsIgnoreCase(encode))
			try {
				trustPassword = new String(Base64.decode(trustPassword));
			} catch (IOException e) {
				LoggerUtil.errorlog("ldap password base64 decode error, ", e);
			}
		return trustPassword;
	}

	public String getBusinessKeyPassword() {
		String keyPassword = properties.getString("listener.business.keypwd", defaultValue);
		String encode = properties.getString("listener.business.keypwd.encode");
		if ("true".equalsIgnoreCase(encode))
			try {
				keyPassword = new String(Base64.decode(keyPassword));
			} catch (IOException e) {
				LoggerUtil.errorlog("ldap password base64 decode error, ", e);
			}
		return keyPassword;
	}

	public String getLdapFilter() {
		List<Object> list = properties.getList("ldap.filter");
		String ldapFilter = StringUtils.join(list.toArray(), ",");
		return ldapFilter;
	}

	public int getLdapInterval() {
		return properties.getInt("ldap.interval", 0);
	}

	public String getNetworkCard() {
		return properties.getString("network.card", defaultValue);
	}

	public String getListenerSelfIp() {
		return properties.getString("listener.self.ip", defaultValue);
	}

	public int getListenerSelfPort() {
		return properties.getInt("listener.self.port", 0);
	}

	public String getListenerBusinessIp() {
		return properties.getString("listener.business.ip", defaultValue);
	}

	public int getListenerBusinessPort() {
		return properties.getInt("listener.business.port", 0);
	}

	public long getSignKeyId() {
		return properties.getLong("crypto.sign.keyId", 0);
	}

	public long getEncrpKeyId() {
		return properties.getLong("crypto.encrypt.keyId", 0);
	}

	public String getDbType() {
		return properties.getString("dataSource.dbType", defaultValue);
	}

	public String getWsUrl() {
		return properties.getString("ws.url", defaultValue);
	}

	public String getGmOid() {
		return properties.getString("crypto.gm.oid", Constants.GM_OID);
	}

	public String getNtpIp() {
		return properties.getString("ntp.ip", defaultValue);
	}

	public int getNtpInterval() {
		return properties.getInt("ntp.interval", 0);
	}

	public String getTsaRsaUrl() {
		return properties.getString("tsa.rsa.url", defaultValue);
	}

	public String getTsaRsaUsername() {
		return properties.getString("tsa.rsa.username", defaultValue);
	}

	public String getTsaRsaUserpwd() {
		return properties.getString("tsa.rsa.userpwd", defaultValue);
	}

	public String getTsaRsaPolicy() {
		return properties.getString("tsa.rsa.policy", defaultValue);
	}

	public boolean getTsaRsaUsetsa() {
		return properties.getBoolean("tsa.rsa.usetsa", false);
	}

	public String getTsaSM2Url() {
		return properties.getString("tsa.sm2.url", defaultValue);
	}

	public String getTsaSM2Username() {
		return properties.getString("tsa.sm2.username", defaultValue);
	}

	public String getTsaSM2Userpwd() {
		return properties.getString("tsa.sm2.userpwd", defaultValue);
	}

	public String getTsaSM2Policy() {
		return properties.getString("tsa.sm2.policy", defaultValue);
	}

	public boolean getTsaSM2Usetsa() {
		return properties.getBoolean("tsa.sm2.usetsa", false);
	}

	public int getCheckSocketNumLimit() {
		return this.checkSocketNumLimit;
	}

	public int getCheckCertNumLimit() {
		return this.checkCertNumLimit;
	}

	public boolean getCheckSealUserCertList() {
		return properties.getBoolean("check.seal.userCertList", false);
	}

	public int getTimeWindowLength() {
		return properties.getInt("mobileToken.timeWindowLength", 0);
	}

	public Long getIDDeleteInterval() {
		return properties.getLong("IDDelete.interval", 0L);
	}

	public Long getIDDeleteTime() {
		return properties.getLong("IDDelete.time", 0L);
	}

	public NetCertCaVO getNetCertCaRSA() {
		NetCertCaVO netCertCaVO = new NetCertCaVO();
		netCertCaVO.setCertType("rsa_ca");
		String hsmName = properties.getString("rsa_ca.hsmname", defaultValue);
		String transIP = properties.getString("rsa_ca.transIP", defaultValue);
		int transPort = properties.getInt("rsa_ca.transPort", 0);
		String keyIdx = properties.getString("rsa_ca.keyIdx", defaultValue);
		String pwd = properties.getString("rsa_ca.pwd", defaultValue);
		String signAlgName = properties.getString("rsa_ca.signAlgName", defaultValue);
		String signCert = properties.getString("rsa_ca.signCert", defaultValue);
		String chanelEncryptName = properties.getString("rsa_ca.chanelEncryptName", defaultValue);
		String trustStore = properties.getString("rsa_ca.ssl_trustStore", defaultValue);
		String trustPassword = properties.getString("rsa_ca.ssl_trustPassword", defaultValue);
		String protocolName = properties.getString("rsa_ca.protocolname", defaultValue);
		String country = properties.getString("rsa_ca.country", defaultValue);
		List templateList = properties.getList("rsa_ca.template");
		String isGenUuid = properties.getString("rsa_ca.isGenUuid", defaultValue);

		netCertCaVO.setHsmName(hsmName);
		netCertCaVO.setTransIP(transIP);
		netCertCaVO.setTransPort(transPort);
		netCertCaVO.setKeyIdx(keyIdx);
		netCertCaVO.setPwd(pwd);
		netCertCaVO.setSignAlgName(signAlgName);
		netCertCaVO.setSignCert(signCert);
		netCertCaVO.setChanelEncryptName(chanelEncryptName);
		netCertCaVO.setTrustStore(trustStore);
		netCertCaVO.setTrustPassword(trustPassword);
		netCertCaVO.setProtocolName(protocolName);
		netCertCaVO.setCountry(country);
		netCertCaVO.setTemplateList(templateList);
		if ("true".equalsIgnoreCase(isGenUuid))
			netCertCaVO.setIsGenUuid(1);
		else
			netCertCaVO.setIsGenUuid(0);
		return netCertCaVO;
	}

	public NetCertCaVO getNetCertCaSM2() {
		NetCertCaVO netCertCaVO = new NetCertCaVO();
		netCertCaVO.setCertType("sm2_ca");
		String hsmName = properties.getString("sm2_ca.hsmname", defaultValue);
		String transIP = properties.getString("sm2_ca.transIP", defaultValue);
		int transPort = properties.getInt("sm2_ca.transPort", 0);
		String keyIdx = properties.getString("sm2_ca.keyIdx", defaultValue);
		String pwd = properties.getString("sm2_ca.pwd", defaultValue);
		String signAlgName = properties.getString("sm2_ca.signAlgName", defaultValue);
		String signCert = properties.getString("sm2_ca.signCert", defaultValue);
		String chanelEncryptName = properties.getString("sm2_ca.chanelEncryptName", defaultValue);
		String trustStore = properties.getString("sm2_ca.ssl_trustStore", defaultValue);
		String trustPassword = properties.getString("sm2_ca.ssl_trustPassword", defaultValue);
		String protocolName = properties.getString("sm2_ca.protocolname", defaultValue);
		String country = properties.getString("sm2_ca.country", defaultValue);
		List templateList = properties.getList("sm2_ca.template");
		String isGenUuid = properties.getString("sm2_ca.isGenUuid", defaultValue);

		netCertCaVO.setHsmName(hsmName);
		netCertCaVO.setTransIP(transIP);
		netCertCaVO.setTransPort(transPort);
		netCertCaVO.setKeyIdx(keyIdx);
		netCertCaVO.setPwd(pwd);
		netCertCaVO.setSignAlgName(signAlgName);
		netCertCaVO.setSignCert(signCert);
		netCertCaVO.setChanelEncryptName(chanelEncryptName);
		netCertCaVO.setTrustStore(trustStore);
		netCertCaVO.setTrustPassword(trustPassword);
		netCertCaVO.setProtocolName(protocolName);
		netCertCaVO.setCountry(country);
		netCertCaVO.setTemplateList(templateList);
		if ("true".equalsIgnoreCase(isGenUuid))
			netCertCaVO.setIsGenUuid(1);
		else
			netCertCaVO.setIsGenUuid(0);
		return netCertCaVO;
	}

	public synchronized void saveNetCertCaRSA(NetCertCaVO netCertCaVO) throws Exception {
		properties.setProperty("rsa_ca.transIP", netCertCaVO.getTransIP());
		properties.setProperty("rsa_ca.transPort", netCertCaVO.getTransPort());
		properties.setProperty("rsa_ca.keyIdx", netCertCaVO.getKeyIdx());
		properties.setProperty("rsa_ca.pwd", netCertCaVO.getPwd());
		properties.setProperty("rsa_ca.signAlgName", netCertCaVO.getSignAlgName());
		properties.setProperty("rsa_ca.signCert", netCertCaVO.getSignCert());
		properties.setProperty("rsa_ca.chanelEncryptName", netCertCaVO.getChanelEncryptName());
		properties.setProperty("rsa_ca.ssl_trustStore", netCertCaVO.getTrustStore());
		properties.setProperty("rsa_ca.ssl_trustPassword", netCertCaVO.getTrustPassword());
		if (netCertCaVO.getIsGenUuid() == 1)
			properties.setProperty("rsa_ca.isGenUuid", true);
		else
			properties.setProperty("rsa_ca.isGenUuid", false);

		builder.save();
	}

	public synchronized void saveNetCertCaSM2(NetCertCaVO netCertCaVO) throws Exception {
		properties.setProperty("sm2_ca.transIP", netCertCaVO.getTransIP());
		properties.setProperty("sm2_ca.transPort", netCertCaVO.getTransPort());
		properties.setProperty("sm2_ca.keyIdx", netCertCaVO.getKeyIdx());
		properties.setProperty("sm2_ca.pwd", netCertCaVO.getPwd());
		properties.setProperty("sm2_ca.signAlgName", netCertCaVO.getSignAlgName());
		properties.setProperty("sm2_ca.signCert", netCertCaVO.getSignCert());
		properties.setProperty("sm2_ca.chanelEncryptName", netCertCaVO.getChanelEncryptName());
		properties.setProperty("sm2_ca.ssl_trustStore", netCertCaVO.getTrustStore());
		properties.setProperty("sm2_ca.ssl_trustPassword", netCertCaVO.getTrustPassword());
		if (netCertCaVO.getIsGenUuid() == 1)
			properties.setProperty("sm2_ca.isGenUuid", true);
		else
			properties.setProperty("sm2_ca.isGenUuid", false);

		builder.save();
	}

	public synchronized void saveNetCertCaTemplate(NetCertCaVO netCertCaVO) throws Exception {
		String certType = netCertCaVO.getCertType();
		if (Constants.NETCERT_CA_RSA.equals(certType)) {
			properties.setProperty("rsa_ca.template", netCertCaVO.getTemplate());
		} else if (Constants.NETCERT_CA_SM2.equals(certType)) {
			properties.setProperty("sm2_ca.template", netCertCaVO.getTemplate());
		}
		builder.save();
	}

	private String getDbTypeFromUrl(String url) {
		if (StringUtil.isBlank(url))
			return "";

		String urls[] = url.split(":");

		switch (urls[1].toLowerCase()) {
		case "oracle":
			return "oracle";
		case "db2":
			return "db2";
		case "microsoft":
			return "sqlserver";
		case "sqlserver":
			return "sqlserver";
		case "mysql":
			return "mysql";
		default:
			return "";
		}
	}

	public static void main(String[] args) {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);
		builder.configure(params.properties().setFileName("d:/photo/jdbc.properties").setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
		try {
			Configuration config = builder.getConfiguration();
			config.setProperty("dbcp.removeAbandonedTimeout", "#000000");

			builder.save();
			String dbHost = config.getString("jdbc.driverClassName");
			System.out.println(dbHost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
