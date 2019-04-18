package cn.com.infosec.netseal.common.scheduler;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.CrlUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class CrlScheduler extends BaseScheduler {

	// 全局变量，需要将一下参数设置成可配置项
	private String context_factory = "com.sun.jndi.ldap.LdapCtxFactory";
	private String provider_url = "ldap://10.20.85.28:389";
	private String security_authentication = "simple";
	private String baseDn = "ou=crl100,o=icbc";
	private String account = "cn=admin,o=config";
	private String password = "novell";
	private String filter = "";

	public CrlScheduler() {
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.setInitialDelay(60);
		this.setInterval(configUtil.getLdapInterval());
	}

	/**
	 * 从ldap获取crl文件
	 */
	private void getCrlFromLdap() {
		LoggerUtil.debuglog("crl scheduler begin...");

		// 清空目录
		FileUtil.deleteDir(Constants.CRL_FROM_PATH);
		ConfigUtil configUtil = ConfigUtil.getInstance();
		// LDAP 配置
		provider_url = configUtil.getLdapUrl();
		context_factory = configUtil.getLdapContextFactory();
		security_authentication = configUtil.getLdapSecurityAuthentication();
		baseDn = configUtil.getLdapBaseDn();
		account = configUtil.getLdapAccount();
		password = configUtil.getLdapPassword();
		filter = configUtil.getLdapFilter();

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, context_factory);
		env.put(Context.PROVIDER_URL, provider_url);
		env.put(Context.SECURITY_AUTHENTICATION, Constants.LDAP_NONE);

		if (StringUtil.isNotBlank(account)) {
			env.put(Context.SECURITY_PRINCIPAL, account);
			env.put(Context.SECURITY_AUTHENTICATION, security_authentication);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}

		NamingEnumeration<?> en = null;
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// searchCtls.setTimeLimit(time_limit * 1000);

			if (StringUtil.isBlank(filter))
				en = ctx.search(baseDn, "objectClass=crldistributionpoint", searchCtls);
			else
				en = ctx.search(baseDn, filter, searchCtls);

			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					Attributes as = si.getAttributes();

					Attribute ar = as.get("certificateRevocationList;binary");
					FileUtil.storeFile(Constants.CRL_FROM_PATH + HexUtil.byte2Hex(si.getName().getBytes(Constants.UTF_8)) + Constants.CRL_SUFFIX, (byte[]) ar.get());
				}
			}

			// 清空目录
			FileUtil.deleteDir(Constants.CRL_TO_PATH);
			CrlUtil.storeSnAndDate();
		} catch (Exception e) {
			LoggerUtil.errorlog("get crl file from ldap error", e);
			throw new NetSealRuntimeException("get crl file from ldap error, " + e.getMessage());
		} finally {
			try {
				if (ctx != null)
					ctx.close();
			} catch (Exception e1) {
				LoggerUtil.errorlog("ldap close error ", e1);
			}
		}
		LoggerUtil.debuglog("crl scheduler end...");
	}

	/**
	 * 验证crl同步配置是否有效
	 * 
	 * @param configUtil
	 * @return
	 */
	public boolean authCrlFromLdap() {
		ConfigUtil configUtil = ConfigUtil.getInstance();
		// LDAP 配置
		provider_url = configUtil.getLdapUrl();
		if (StringUtil.isBlank(provider_url)) {
			LoggerUtil.errorlog("authCrlFromLdap, ldap url is null");
			return false;
		}

		context_factory = configUtil.getLdapContextFactory();
		security_authentication = configUtil.getLdapSecurityAuthentication();
		baseDn = configUtil.getLdapBaseDn();
		account = configUtil.getLdapAccount();
		password = configUtil.getLdapPassword();
		filter = configUtil.getLdapFilter();

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, context_factory);
		env.put(Context.PROVIDER_URL, provider_url);
		env.put(Context.SECURITY_AUTHENTICATION, Constants.LDAP_NONE);

		if (StringUtil.isNotBlank(account)) {
			env.put(Context.SECURITY_PRINCIPAL, account);
			env.put(Context.SECURITY_AUTHENTICATION, security_authentication);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}
		InitialDirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			return true;
		} catch (Exception e) {
			LoggerUtil.errorlog("authCrlFromLdap error ", e);
			return false;
		} finally {
			if (ctx != null)
				try {
					ctx.close();
				} catch (Exception e) {
					LoggerUtil.errorlog("close authCrlFromLdap error ", e);
				}
		}
	}

	public void run() {
		getCrlFromLdap();
	}

	public static void main(String args[]) {
		// LdapUtil auu = new LdapUtil();
		// String filter = "(&(cn=zhangqy)(objectClass=top))";
		// auu.getCrlFromLdap();

		// new CrlScheduler().getCrlFromLdap();
		// CrlUtil.storeSnFromCrl();
		// System.out.println("succ");
	}

}
