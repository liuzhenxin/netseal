package cn.com.infosec.netseal.common.manager;

import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;

public class JmxManager {

	private static JmxManager jmxManager = null;
	private static JMXConnector jmxConn = null;
	private static MBeanServerConnection serverConn = null;

	private JmxManager() {
	}

	public synchronized static JmxManager getInstance() throws Exception {
		if (jmxManager == null) {
			jmxManager = new JmxManager();
			try {
				jmxConn = JMXConnectorFactory.connect(new JMXServiceURL(Constants.JMX_URL), null);
				serverConn = jmxConn.getMBeanServerConnection();
			} catch (Exception e) {
				LoggerUtil.errorlog("jmx manager get instance error,", e);
				throw e;
			}

		}
		return jmxManager;
	}

	/**
	 * 获取JMX 属性
	 * 
	 * @param objName
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	public synchronized Object getAttribute(String objName, String attribute) throws Exception {
		Object obj = null;
		try {
			obj = serverConn.getAttribute(new ObjectName(objName), attribute);
		} catch (Exception e) {
			LoggerUtil.errorlog("jmx manager get attribute error,", e);
			closeConn(jmxConn);
			reCreateConn();
			throw e;
		}

		return obj;
	}

	/**
	 * 获取混合JMX 属性
	 * 
	 * @param objName
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	public synchronized MemoryUsage getCompositeAttribute(String objName, String attribute) throws Exception {
		MemoryUsage heapMemoryUsage = null;
		try {
			heapMemoryUsage = MemoryUsage.from((CompositeDataSupport) serverConn.getAttribute(new ObjectName(objName), attribute));
		} catch (Exception e) {
			LoggerUtil.errorlog("jmx manager get composite attribute error,", e);
			closeConn(jmxConn);
			reCreateConn();
			throw e;
		}

		return heapMemoryUsage;
	}

	/**
	 * 重建连接
	 * 
	 * @throws Exception
	 */
	private void reCreateConn() throws Exception {
		try {
			jmxConn = JMXConnectorFactory.connect(new JMXServiceURL(Constants.JMX_URL), null);
			serverConn = jmxConn.getMBeanServerConnection();
		} catch (Exception e) {
			LoggerUtil.errorlog("jmx manager recreate conn error,", e);
			throw e;
		}

	}

	/**
	 * 关闭连接
	 * 
	 * @param jmxConn
	 */
	private void closeConn(JMXConnector jmxConn) {
		if (jmxConn != null)
			try {
				jmxConn.close();
			} catch (Exception e1) {
			}
	}

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LoggerConfig.init();
		Object obj = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "SocketNum");
		System.out.println("SocketNum: " + obj);

		obj = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "DealSuccNum");
		System.out.println("dealSuccNum: " + obj);

		obj = JmxManager.getInstance().getAttribute(Constants.JMX_INFOSEC_OBJ_NAME, "DealFailNum");
		System.out.println("dealFailNum: " + obj);

		obj = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "PoolingCount");
		System.out.println("PoolingCount: " + obj);

		obj = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "PoolingPeak");
		System.out.println("PoolingPeak: " + obj);

		Date d = (Date) JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "PoolingPeakTime");
		System.out.println("PoolingPeakTime: " + sdf.format(d));

		obj = JmxManager.getInstance().getAttribute(Constants.JMX_DRUID_OBJ_NAME, "ActiveCount");
		System.out.println("ActiveCount: " + obj);

		// 堆内存
		MemoryUsage heapMemoryUsage = JmxManager.getInstance().getCompositeAttribute(Constants.JMX_MEMORY_OBJ_NAME, "HeapMemoryUsage");
		System.out.println("堆内存:" + heapMemoryUsage.getMax() / 1024 / 1024 + "MB, 使用:" + heapMemoryUsage.getUsed() / 1024 / 1024 + "MB,");

		// 栈内存
		MemoryUsage nonheapMemoryUsage = JmxManager.getInstance().getCompositeAttribute(Constants.JMX_MEMORY_OBJ_NAME, "NonHeapMemoryUsage");
		System.out.println("栈内存使用:" + nonheapMemoryUsage.getUsed() / 1024 / 1024 + "MB");

		// String jmxUrl = "service:jmx:rmi:///jndi/rmi://10.20.87.55:8999/jmxrmi";
		// // connect JMX  
		// JMXServiceURL url = new JMXServiceURL(jmxUrl);
		// JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		// MBeanServerConnection serverConn = jmxc.getMBeanServerConnection();
		//
		// ObjectName heapObjName = new ObjectName("java.lang:type=Memory");
		// // 堆内存
		// MemoryUsage heapMemoryUsage = MemoryUsage.from((CompositeDataSupport) serverConn.getAttribute(heapObjName, "HeapMemoryUsage"));
		// System.out.println("堆内存:" + heapMemoryUsage.getMax() / 1024 / 1024 + "MB, 使用:" + heapMemoryUsage.getUsed() / 1024 / 1024 + "MB,");
		//
		// // 栈内存
		// MemoryUsage nonheapMemoryUsage = MemoryUsage.from((CompositeDataSupport) serverConn.getAttribute(heapObjName, "NonHeapMemoryUsage"));
		// System.out.println("栈内存使用:" + nonheapMemoryUsage.getUsed() / 1024 / 1024 + "MB");
		//
		// jmxc.close();

		// // PermGen内存
		// ObjectName permObjName = new ObjectName("java.lang:type=MemoryPool,name=Perm Gen");
		// MemoryUsage permGenUsage = MemoryUsage.from((CompositeDataSupport) serverConn.getAttribute(permObjName, "Usage"));
		// long committed = permGenUsage.getCommitted();//  持久堆大小  
		// long used = heapMemoryUsage.getUsed();
		// System.out.println("perm gen:" + (int) used * 100 / committed + "%");//  持久堆使用率  

	}
}