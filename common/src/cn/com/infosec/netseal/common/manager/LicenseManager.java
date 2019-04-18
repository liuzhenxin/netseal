package cn.com.infosec.netseal.common.manager;

import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.NetWorkUtil;
import cn.com.infosec.netseal.common.util.license.LicenseChecker;
import cn.com.infosec.netseal.common.util.license.LicenseInfo;

@Component("LicenseManager")
public class LicenseManager {

	/**
	 * 加载证书总数
	 */
	public void loadLicense() {
		// 加载license
		try {
			String networkCard = NetWorkUtil.getHostMac(ConfigUtil.getInstance().getNetworkCard());
			LicenseInfo i = LicenseChecker.checkLicense(networkCard);
			if (i == null)
				throw new Exception("licenseInfo obj is null");

			ConfigUtil.getInstance().setLicenseConfig(i.getMaxCertNum(), i.getMaxThred());
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.OPERATE_LICENSE_ERROR, e.getMessage());
		}
	}

}
