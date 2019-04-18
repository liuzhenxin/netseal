package cn.com.infosec.netseal.webserver.service;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.entity.po.Base;
import cn.com.infosec.netseal.common.entity.vo.BaseVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;

public class BaseService {
	
	/**
	 * 
	 * @param base
	 * 			对象信息
	 * @param msg
	 * 			提示信息
	 * @throws Exception
	 */
	public void isModify(Base base, String msg, String errlog) throws Exception{
		ConfigUtil config = ConfigUtil.getInstance();
		boolean checkMac = config.getCheckMac();
		if (checkMac){
			if (!(base.calMac()).equals(base.getMac())) {
				LoggerUtil.errorlog("the data of " + errlog + " had been tampered with" + "<br/>");
				throw new WebDataException(msg + "信息被篡改,操作失败");
			}
		}
	}
	
	/**
	 * 
	 * @param base
	 * 			对象信息
	 * @return
	 * @throws Exception
	 */
	public boolean isModify(Base base) throws Exception {
		ConfigUtil config = ConfigUtil.getInstance();
		boolean checkMac = config.getCheckMac();
		if (checkMac){
			if (base.calMac().equals(base.getMac())) 
				return true;
			
			return false;
		}
		return true;
	}
	
	/**
	 * 校验信息
	 * 
	 * @param base
	 * @param baseVO
	 * @throws Exception
	 */
	public void isModify(Base base, BaseVO baseVO) throws Exception{
		if (isModify(base)) 
			baseVO.setSealMac(true);
		else 
			baseVO.setSealMac(false);
	}
	
}
