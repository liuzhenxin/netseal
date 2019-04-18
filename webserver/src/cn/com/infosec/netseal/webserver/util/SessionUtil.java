package cn.com.infosec.netseal.webserver.util;

import javax.servlet.http.HttpSession;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;

public class SessionUtil {

	public static SysUserVO getSysUser(HttpSession httpSession) {
		SysUserVO sysUserVO = (SysUserVO) httpSession.getAttribute("sysUser");
		return sysUserVO;
	}

	public static String getSysUserAccount(HttpSession httpSession) {
		SysUserVO sysUserVO = (SysUserVO) httpSession.getAttribute("sysUser");
		if (sysUserVO != null) {
			return sysUserVO.getAccount();
		}
		return Constants.DEFAULT_STRING;
	}

	public static long getSysUserId(HttpSession httpSession) {
		SysUserVO sysUserVO = (SysUserVO) httpSession.getAttribute("sysUser");
		if (sysUserVO != null && sysUserVO.getId()!=null) {
			return sysUserVO.getId();
		}
		return -1;
	}

}
