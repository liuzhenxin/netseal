package cn.com.infosec.netseal.webserver.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import cn.com.infosec.netseal.common.rads.RaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.count.CountDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.entity.vo.config.NetCertCaVO;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.LicenseManager;
import cn.com.infosec.netseal.common.manager.SchedulerManager;
import cn.com.infosec.netseal.common.scheduler.CrlScheduler;
import cn.com.infosec.netseal.common.scheduler.IDDeleteScheduler;
import cn.com.infosec.netseal.common.scheduler.NtpScheduler;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUtil;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;

@Component
public class WebContextManager {

	@Autowired
	private CountDaoImpl countDao;
	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private LicenseManager licenseManager;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;

	static {
		// 指定JCE实现提供者 infosec
		java.security.Security.addProvider(new InfosecProvider());
	}

	@PostConstruct
	public void init() {
		loggerInit();
		loadLicense();
		startCrlScheduler();
		startNtpScheduler();
		startIdDeleteScheduler();
		countCertNum2DB();
		radsInit();
		openDevice();
	}

	@PreDestroy
	public void destroy() {
		DestroyScheduler();
		closeDevice();
	}

	/**
	 * 配置日志 (必须第一个执行)
	 */
	public void loggerInit() {
		try {
			LoggerConfig.init();
			LoggerUtil.systemlog("logger config init ok.");
		} catch (Exception e) {
			System.out.println("logger config init error, " + e.getMessage());
		}
	}

	/**
	 * 打开加密卡设备
	 */
	public void openDevice() {
		String os = System.getProperties().getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			// 打开加密卡设备
			try {
				addLibraryDir(Constants.NATIVELIB_PATH);
				FishManUtil.openDevice();
				LoggerUtil.systemlog("open fm crypto card ok.");
			} catch (Exception e) {
				System.out.println("open fm crypto card error, " + e.getMessage());
				LoggerUtil.errorlog("open fm crypto card error, ", e);
				LoggerUtil.systemlog("open fm crypto card error.");
			}
		}
	}

	/**
	 * 初始化License
	 */
	public void loadLicense() {
		try {
			// 加载license
			licenseManager.loadLicense();
			LoggerUtil.systemlog("load license ok.");
		} catch (Exception e) {
			System.out.println("load license error, " + e.getMessage());
			LoggerUtil.errorlog("load license error, ", e);
			LoggerUtil.systemlog("load license error.");
		}

	}

	/**
	 * 启动CrlScheduler
	 */
	public void startCrlScheduler() {
		try {
			CrlScheduler bs = new CrlScheduler();
			boolean result = bs.authCrlFromLdap();
			if (result) {
				SchedulerManager.setScheduler(Constants.SCHEDULER_CRL, bs);
				SchedulerManager.start(Constants.SCHEDULER_CRL);
				LoggerUtil.systemlog("start crl scheduler ok.");
			} else {
				throw new Exception("can't start crl scheduler, test ldap fail");
			}
		} catch (Exception e) {
			System.out.println("start crl scheduler error, " + e.getMessage());
			LoggerUtil.errorlog("start crl scheduler error, ", e);
			LoggerUtil.systemlog("start crl scheduler error.");
		}
	}

	/**
	 * 启动NtpScheduler
	 */
	public void startNtpScheduler() {
		try {
			NtpScheduler bs = new NtpScheduler();
			boolean result = bs.execSynTime();
			if (result) {
				SchedulerManager.setScheduler(Constants.SCHEDULER_NTP, bs);
				SchedulerManager.start(Constants.SCHEDULER_NTP);
				LoggerUtil.systemlog("start ntp scheduler ok.");
			} else {
				throw new Exception("can't start ntp scheduler, test ntp fail");
			}
		} catch (Exception e) {
			System.out.println("start ntp scheduler error, " + e.getMessage());
			LoggerUtil.errorlog("start ntp scheduler error, ", e);
			LoggerUtil.systemlog("start ntp scheduler error.");
		}
	}

	/**
	 * 启动IddScheduler(删除SEAL_ID_DELETE表中30天之前的数据)
	 */
	public void startIdDeleteScheduler() {
		try {
			IDDeleteScheduler bs = new IDDeleteScheduler();
			bs.setIdDeleteDao(idDeleteDao);
			SchedulerManager.setScheduler(Constants.SCHEDULER_IDD, bs);
			SchedulerManager.start(Constants.SCHEDULER_IDD);
			LoggerUtil.systemlog("start id delete scheduler ok.");
		} catch (Exception e) {
			System.out.println("start id delete scheduler error, " + e.getMessage());
			LoggerUtil.errorlog("start id delete scheduler error, ", e);
			LoggerUtil.systemlog("start id delete scheduler error.");
		}
	}

	/**
	 * 统计当前服务器生成的证书数量, 添加到license控制表
	 */
	public void countCertNum2DB() {
		try {
			// 查询当前服务器生成的证书数量
			String serverId = ConfigUtil.getInstance().getTablePrefixId();
			int certNum = certDao.searchTotal(serverId);

			Count count = new Count();
			count.setName(Constants.CERT_NUM);
			count.setLocation(serverId);
			count.setNum(certNum);
			List<Count> cList = countDao.getCount(count);

			if (cList.size() == 0)
				countDao.insert(count);
			else
				countDao.updateNum(count);

			LoggerUtil.systemlog("count cert num 2 DB ok.");
		} catch (Exception e) {
			System.out.println("count cert num 2 DB error, " + e.getMessage());
			LoggerUtil.errorlog("count cert num 2 DB error, ", e);
			LoggerUtil.systemlog("count cert num 2 DB error.");
		}
	}

	/**
	 * 初始化RADS
	 */
	public void radsInit() {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			List<NetCertCaVO> netCertCaList = new ArrayList<NetCertCaVO>();

			// rsa_ca
			NetCertCaVO netCertCa = config.getNetCertCaRSA();
			if (StringUtil.isNotBlank(netCertCa.getTransIP()) && netCertCa.getTransPort() != 0)
				netCertCaList.add(netCertCa);

			// sm2_ca
			netCertCa = config.getNetCertCaSM2();
			if (StringUtil.isNotBlank(netCertCa.getTransIP()) && netCertCa.getTransPort() != 0){

				netCertCaList.add(netCertCa);
			}

			if (netCertCaList.size() > 0) {
				RaConfig.setCa_61_List(netCertCaList);
				RaConfig.init();
			}
			LoggerUtil.systemlog("init all rads config ok.");
		} catch (Exception e) {
			System.out.println("init rads config error, " + e.getMessage());
			LoggerUtil.errorlog("init rads config error, ", e);
			LoggerUtil.systemlog("init all rads config error.");
		}
	}

	/**
	 * 关闭scheduler任务
	 */
	public void DestroyScheduler() {
		try {
			List<String> keyList = SchedulerManager.getKeys();
			if (keyList.size() > 0) {
				for (String key : keyList) {
					boolean isRunning = SchedulerManager.isRunning(key);
					if (isRunning) {
						SchedulerManager.stopAndClear(key);
					}
				}
			}
			LoggerUtil.systemlog("destory all scheduler ok.");
		} catch (Exception e) {
			System.out.println("destory all scheduler error, " + e.getMessage());
			LoggerUtil.errorlog("destory all scheduler error, ", e);
			LoggerUtil.systemlog("destory all scheduler error.");
		}
	}

	/**
	 * 关闭加密卡设备
	 */
	public void closeDevice() {
		String os = System.getProperties().getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			// 关闭加密卡设备
			try {
				FishManUtil.closeDevice();
				LoggerUtil.systemlog("close fm crypto card ok.");
			} catch (Exception e) {
				System.out.println("close fm crypto card error, " + e.getMessage());
				LoggerUtil.errorlog("close fm crypto card error, ", e);
				LoggerUtil.systemlog("close fm crypto card error.");
			}
		}
	}

	/**
	 * 增加库路径
	 * 
	 * @param libraryPath
	 * @throws Exception
	 */
	private static void addLibraryDir(String libraryPath) throws Exception {
		Field field = ClassLoader.class.getDeclaredField("usr_paths");
		field.setAccessible(true);
		String[] paths = (String[]) field.get(null);
		for (int i = 0; i < paths.length; i++) {
			if (libraryPath.equals(paths[i])) {
				return;
			}
		}

		String[] tmp = new String[paths.length + 1];
		System.arraycopy(paths, 0, tmp, 0, paths.length);
		tmp[paths.length] = libraryPath;
		field.set(null, tmp);
	}

}
