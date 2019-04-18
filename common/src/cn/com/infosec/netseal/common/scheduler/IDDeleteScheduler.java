package cn.com.infosec.netseal.common.scheduler;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;

public class IDDeleteScheduler extends BaseScheduler {
	private IDDeleteDaoImpl idDeleteDao;

	ConfigUtil configUtil = ConfigUtil.getInstance();

	public IDDeleteScheduler() {
		this.setInitialDelay(60);
		this.setInterval(configUtil.getIDDeleteInterval());
	}

	@Override
	public void run() {
		LoggerUtil.debuglog("idd scheduler begin...");
		try {
			Long time = DateUtil.getCurrentTime() - configUtil.getIDDeleteTime();
			idDeleteDao.deleteIDDeleteByTime(time);
		} catch (Exception e) {
			LoggerUtil.errorlog("delete data of SEAL_ID_DELETE error," + e.getMessage());
		}
		LoggerUtil.debuglog("idd scheduler end...");
	}

	public IDDeleteDaoImpl getIdDeleteDao() {
		return idDeleteDao;
	}

	public void setIdDeleteDao(IDDeleteDaoImpl idDeleteDao) {
		this.idDeleteDao = idDeleteDao;
	}

}
