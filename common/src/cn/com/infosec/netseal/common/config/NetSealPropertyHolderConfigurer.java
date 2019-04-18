package cn.com.infosec.netseal.common.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.util.Base64;

public class NetSealPropertyHolderConfigurer extends PropertyPlaceholderConfigurer {
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		try {
			String encode = props.getProperty("jdbc.password.encode");
			String value = props.getProperty("jdbc.password");
			if ("true".equalsIgnoreCase(encode))
				props.setProperty("jdbc.password", new String(Base64.decode(value)));
			else
				// 密码加密
				ConfigUtil.getInstance().saveDBPassword();

			encode = props.getProperty("listener.business.trustpwd.encode");
			value = props.getProperty("listener.business.trustpwd");
			if ("true".equalsIgnoreCase(encode))
				props.setProperty("listener.business.trustpwd", new String(Base64.decode(value)));
			else
				// 密码加密
				ConfigUtil.getInstance().saveBusinessTrustPassword();

			encode = props.getProperty("listener.business.keypwd.encode");
			value = props.getProperty("listener.business.keypwd");
			if ("true".equalsIgnoreCase(encode))
				props.setProperty("listener.business.keypwd", new String(Base64.decode(value)));
			else
				// 密码加密
				ConfigUtil.getInstance().saveBusinessKeyPassword();

			encode = props.getProperty("ldap.password.encode");
			value = props.getProperty("ldap.password");
			if ("true".equalsIgnoreCase(encode))
				props.setProperty("ldap.password", new String(Base64.decode(value)));
			else
				// 密码加密
				ConfigUtil.getInstance().saveLdapPassword();

		} catch (Exception e) {
			System.out.println("property place holder configurer init error, " + e.getMessage());
			LoggerUtil.errorlog("property place holder configurer init error, ", e);
		}

		super.processProperties(beanFactoryToProcess, props);
	}

}
