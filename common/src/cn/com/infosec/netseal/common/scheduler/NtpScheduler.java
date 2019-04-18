package cn.com.infosec.netseal.common.scheduler;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.ExecSh;
import cn.com.infosec.netseal.common.util.StringUtil;

public class NtpScheduler extends BaseScheduler {

	// 全局变量，需要将一下参数设置成可配置项
	private String ntpIp = "127.0.0.1";

	public NtpScheduler() {
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.setInitialDelay(60);
		this.setInterval(configUtil.getNtpInterval());
		this.setNtpIp(configUtil.getNtpIp());
	}

	public String getNtpIp() {
		return ntpIp;
	}

	public void setNtpIp(String ntpIp) {
		this.ntpIp = ntpIp;
	}

	public void run() {
		LoggerUtil.debuglog("ntp scheduler begin...");
		try {
			String result = ExecSh.exec("ntpdate " + ntpIp);
			if (result.indexOf("adjust time server") == -1 && result.indexOf("step time server") == -1)
				throw new Exception(result);
		} catch (Exception e) {
			LoggerUtil.errorlog("ntp sync error, " + e.getMessage());
		}
		LoggerUtil.debuglog("ntp scheduler end...");
	}

	/**
	 * 执行同步时间脚本，true表示成功, false表示失败
	 * 
	 * @return
	 */
	public boolean execSynTime() {
		try {
			if (StringUtil.isBlank(ntpIp)) {
				LoggerUtil.errorlog("ntp sync error, ntp ip is null");
				return false;
			}

			String result = ExecSh.exec("ntpdate " + ntpIp);
			if (result.indexOf("adjust time server") == -1 && result.indexOf("step time server") == -1)
				return false;
		} catch (Exception e) {
			LoggerUtil.errorlog("ntp sync error, " + e.getMessage());
		}

		return true;
	}
}
