package cn.com.infosec.netseal.common.util;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class JmxUtil {

	private static JMXConnector getConn(String jmxUrl) throws Exception {
		if (StringUtil.isBlank(jmxUrl))
			return null;

		JMXServiceURL serviceURL = new JMXServiceURL(jmxUrl);
		return JMXConnectorFactory.connect(serviceURL, null);
	}

	public static Object getAttr(String jmxUrl, String objName, String attrName) {
		if (StringUtil.isBlank(jmxUrl) || StringUtil.isBlank(objName) || StringUtil.isBlank(attrName))
			throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_ISNULL, "parameter is null");

		ObjectName obj = null;
		JMXConnector conn = null;
		try {
			obj = new ObjectName(objName);
			conn = getConn(jmxUrl);
			if (conn == null)
				throw new Exception("get jmx connector is null");

			MBeanServerConnection mbsc = conn.getMBeanServerConnection();
			return mbsc.getAttribute(obj, attrName);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.GET_JMX_DATA_ERROR, e.getMessage());
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}

	}

	public static void main(String[] args) throws Exception {
		String jmxUrl = "service:jmx:rmi:///jndi/rmi://10.20.87.55:8999/jmxrmi";
		String objName = "com.alibaba.druid:type=DruidDataSource,id=NetSeal";
		ObjectName threadObjName = new ObjectName(objName);

		JMXConnector connector = null;
		MBeanAttributeInfo[] mbAttributes = null;
		try {
			connector = getConn(jmxUrl);
			mbAttributes = connector.getMBeanServerConnection().getMBeanInfo(threadObjName).getAttributes();
		} catch (Exception e) {
			throw e;
		} finally {
			if (connector != null) {
				try {
					connector.close();
				} catch (Exception e) {
				}
			}
		}

		for (MBeanAttributeInfo mBeanAttributeInfo : mbAttributes) {
			String key = mBeanAttributeInfo.getName();
			String type = mBeanAttributeInfo.getType();
			try {
				System.out.println("key is " + key);
				System.out.println("value is " + getAttr(jmxUrl, objName, key));
				System.out.println("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
