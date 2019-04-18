package cn.com.infosec.netseal.appserver.service;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.entity.po.Base;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class BaseService {
	
	/**
	 * 校验信息
	 * @param base
	 * @param msg
	 * @throws Exception
	 */
	public void isModify(Base base, String msg){
		ConfigUtil config = ConfigUtil.getInstance();
		boolean checkMac = config.getCheckMac();
		if (checkMac){
			String mac = null;
			try {
				mac = base.calMac();
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
			}
			
			if (!mac.equals(base.getMac())) 
				throw new NetSealRuntimeException(ErrCode.MAC_INVALID, msg + " had been tampered with");
		}
	}
	
}
