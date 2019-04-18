package cn.com.infosec.netseal.common.scheduler;

import java.lang.management.ManagementFactory;

import javax.management.ObjectName;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.jmx.JmxScalarVO;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.CounterManager;

public class JmxScheduler extends BaseScheduler {
	private JmxScalarVO scalar = new JmxScalarVO();

	public JmxScheduler() throws Exception {
		this.setInitialDelay(60);
		this.setInterval(1);
		ManagementFactory.getPlatformMBeanServer().registerMBean(scalar, new ObjectName("cn.com.infosec:type=NetSeal"));
	}

	public void run() {
		LoggerUtil.debuglog("jmx scheduler begin...");
		try {
			long dealSuccNum = CounterManager.getKeyValueAndClear(Constants.DEAL_S_NUM).longValue();
			long dealFailNum = CounterManager.getKeyValueAndClear(Constants.DEAL_F_NUM).longValue();
			long dealTotalNum = dealSuccNum + dealFailNum;

			scalar.setDealTotalNum(dealTotalNum);
			scalar.setDealSuccNum(dealSuccNum);
			scalar.setDealFailNum(dealFailNum);
		} catch (Exception e) {
			LoggerUtil.errorlog("get jmx scalar deal num error", e);
			throw new NetSealRuntimeException("get jmx scalar deal num error, " + e.getMessage());
		}
		LoggerUtil.debuglog("jmx scheduler end...");
	}
}
