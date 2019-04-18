package cn.com.infosec.netseal.appserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.appserver.listener.SocketListener;
import cn.com.infosec.netseal.appserver.manager.ApplicationContextManager;
import cn.com.infosec.netseal.appserver.manager.ListenerManager;
import cn.com.infosec.netseal.appserver.manager.SelfManager;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.count.CountDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.LicenseManager;
import cn.com.infosec.netseal.common.manager.SchedulerManager;
import cn.com.infosec.netseal.common.scheduler.JmxScheduler;
import cn.com.infosec.netseal.common.util.ExecSh;
import cn.com.infosec.netseal.common.util.NetSignUtil;
import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUtil;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;

public class AppServer {
	static {
		// 指定JCE实现提供者 InfoSec
		java.security.Security.addProvider(new InfosecProvider());
	}

	private static final String[] options = { "-start", "-stop", "-testserver", "-version", "-reloadconf" };

	private static final String[] optiondescs = { "start server", "stop server", "test server", "print product version", "reload config" };

	private static void showUsage() {
		System.out.println("Usage: AppSever [-options]");
		System.out.println();
		System.out.println("where options include:");
		for (int i = 0; i < options.length; i++) {
			System.out.println("  " + options[i] + ":" + optiondescs[i]);
		}
	}

	private static boolean checkOption(String[] args) {
		if (args != null && args.length > 0) {
			String option = args[0];
			for (int i = 0; i < options.length; i++) {
				if (option.trim().equalsIgnoreCase(options[i])) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		int len = args.length;
		if ((len == 0)) {
			showUsage();
			return;
		}

		if (!checkOption(args)) {
			showUsage();
			return;
		}

		try {
			if (args[0].equalsIgnoreCase("-start")) {
				System.out.println("prepared to start server...");

				// 设置系统根目录
				LoggerConfig.init();
				System.out.println("log config init ok.");

				// 检测系统依赖
				if (!rpmCheck()) {
					return;
				}

				// 启动监听
				ApplicationContextManager.getBean("listenerManager", ListenerManager.class).startService();
				System.out.println("start server listener ok.");

				// 加载License
				ApplicationContextManager.getBean("LicenseManager", LicenseManager.class).loadLicense();
				System.out.println("load license ok.");

				// 启动统计交易数任务
				JmxScheduler js = new JmxScheduler();
				SchedulerManager.setScheduler(Constants.SCHEDULER_JMX, js);
				SchedulerManager.start(Constants.SCHEDULER_JMX);
				System.out.println("start jmx scheduler ok.");

				// 添加数据到证书统计表
				countCertNum2DB();
				System.out.println("calc cert num to counter table(db) ok.");

				// 打开加密卡
				openDevice();

				// 加载算法文件
				NetSignUtil.initialize();

				Thread.sleep(2000);
				System.out.println("start at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				System.out.println("server started.");
				return;
			}
		} catch (Throwable ex) {
			System.out.println("server start error");
			ex.printStackTrace();

			// 关闭加密卡
			closeDevice();
			LoggerUtil.errorlog("server start error", ex);
			System.exit(0);
		}

		if (args[0].equalsIgnoreCase("-version")) {
			System.out.println(Constants.VERISON);
			return;
		}

		try {
			if (args[0].equalsIgnoreCase("-testserver")) {
				// 加载管理端口信息
				int managePort = ApplicationContextManager.getBean("selfListener", SocketListener.class).getPort();
				String result = SelfManager.testServer("127.0.0.1", managePort);
				System.out.println(result);
				return;
			}

			if (args[0].equalsIgnoreCase("-stop")) {
				int managePort = ApplicationContextManager.getBean("selfListener", SocketListener.class).getPort();
				SelfManager.stopServer("127.0.0.1", managePort);
				System.out.println("server will stop in a few seconds...");
				return;
			}

			if (args[0].equalsIgnoreCase("-reloadconf")) {
				int managePort = ApplicationContextManager.getBean("selfListener", SocketListener.class).getPort();
				boolean result = SelfManager.reloadConf("127.0.0.1", managePort);
				if (result)
					System.out.println("reload config success");
				return;
			}
		} catch (Exception ex) {
			System.out.println("run shell command error, because " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
	}

	/**
	 * 打开加密卡
	 */
	private static void openDevice() {
		String os = System.getProperties().getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			// 打开加密卡设备
			try {
				FishManUtil.openDevice();
			} catch (Exception e) {
				System.out.println("open fm crypto card error, " + e.getMessage());
			}
		}
	}

	/**
	 * 关闭加密卡
	 */
	private static void closeDevice() {
		String os = System.getProperties().getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			// 关闭加密卡设备
			try {
				FishManUtil.closeDevice();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 统计当前服务器生成的证书数量, 添加到license控制表
	 */
	private static void countCertNum2DB() {
		String serverId = ConfigUtil.getInstance().getTablePrefixId();
		int certNum = ApplicationContextManager.getBean("certDaoImpl", CertDaoImpl.class).searchTotal(serverId);

		Count count = new Count();
		count.setName(Constants.CERT_NUM);
		count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
		count.setNum(certNum);
		List<Count> cList = ApplicationContextManager.getBean("countDaoImpl", CountDaoImpl.class).getCount(count);
		if (cList.size() == 0) {
			ApplicationContextManager.getBean("countDaoImpl", CountDaoImpl.class).insert(count);
		} else {
			ApplicationContextManager.getBean("countDaoImpl", CountDaoImpl.class).updateNum(count);
		}
	}

	/**
	 * RPM依赖包检测
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean rpmCheck() throws Exception {
		System.out.println("rpm check...");

		// windows系统不检查rpm安装
		String os = System.getProperties().getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("win") > -1) {
			System.out.println("rpm no check");
			return true;
		}

		String rpmCheckPath = Constants.RPM_CHECK_PATH;
		String result = ExecSh.exec(rpmCheckPath + "rpm_must.ini");

		if (result.indexOf("not installed") > -1) {
			System.out.println("must install the RPM package is not installed, as follow");
			System.out.println(result);
			return false;
		}

		result = ExecSh.exec(rpmCheckPath + "rpm_option.ini");
		if (result.indexOf("not installed") > -1) {
			System.out.println("option install the RPM package is not installed, but these packages are not must, as follow");
			System.out.println(result);
		}

		return true;
	}
}
