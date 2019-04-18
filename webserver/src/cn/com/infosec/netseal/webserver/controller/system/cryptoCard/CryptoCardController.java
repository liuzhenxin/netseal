package cn.com.infosec.netseal.webserver.controller.system.cryptoCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUserInfo;
import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;

@RequestMapping(value = "/system/card")
@Controller
public class CryptoCardController extends BaseController{
	/**
	 * 进入加密卡用户管理
	 * @throws Exception 
	 */
	@RequestMapping(value = "cardUserManage")
	public String cardUserManage(HttpServletRequest request) throws Exception {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			// 取得加密卡内用户信息
			List<FishManUserInfo> userList = FishManUtil.listFishmanUser();
			List<FishManUserInfo> newUserList = new ArrayList<FishManUserInfo>();
			if (userList != null && userList.size() > 0) {
				for (FishManUserInfo cardUser : userList) {
					if ("管理员".equals(cardUser.getUserType())) {
						newUserList.add(cardUser);
						if (cardUser.isCurrentUser()) {
							request.setAttribute("currentUser", cardUser);
						}
					}
				}
			}
			request.setAttribute("userList", newUserList);
		} else {
			request.setAttribute("msg", "此功能只支持Linux操作系统使用");
		}
		return "system/card/cardUserManage";
	}

	/**
	 * 进入加密卡用户登陆
	 */
	@RequestMapping(value = "toLoginCardUser")
	public String toLoginCardUser(HttpServletRequest request) {
		
		return "system/card/cardUserLogin";
	}

	/**
	 * 加密卡用户登陆
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_CARDUSERLOGIN)
	@RequestMapping(value = "loginCardUser")
	public ModelAndView loginCardUser(String pin) {
		boolean success = true;
		String message = "";
		int[]  retry = new int[1];
		try {
			FishManUtil.login(pin, retry);
			message = "密卡用户登录成功";
		} catch (Exception e) {
			success = false;
			message = "pin码错误, 还可尝试 " + retry[0] + " 次";
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg)); 

	}

	/**
	 * 进入加密卡用户PIN码修改
	 */
	@RequestMapping(value = "toEditCardUser")
	public String toEditCardUser(String userId, HttpServletRequest request) {
		request.setAttribute("userId", userId);
		return "system/card/cardUserEdit";
	}

	/**
	 * 加密卡用户PIN码修改
	 */
	@RequestMapping(value = "editCardUser")
	public ModelAndView editCardUser(String userId, String oldPin,String newPin) {

		boolean success = true;
		String message = "";
		int[]  retry = new int[1];
		try {
			FishManUtil.modifyPin(oldPin,newPin, retry);
			message = "加密卡用户"+userId+"修改pin码成功";
		} catch (Exception e) {
			success = false;
			message = "修改pin码失败, 还可尝试 " + retry[0] + " 次";
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg)); 
	}

	/**
	 * 加密卡用户注销
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_CARDUSERLOGOUT)
	@RequestMapping(value = "cardUserLogout")
	public ModelAndView cardUserLogout(String userNum) {
		boolean success = true;
		String message = "";
		List<String> idList = Arrays.asList(userNum.split(","));
		int size = idList.size();
		int i = -1;
		try {
			for(i=0;i<size;i++) {
				int[] idArray= new int[]{Integer.parseInt(idList.get(i))};
				FishManUtil.logoutUser(idArray);
			}
			message = "用户注销成功";
		} catch (Exception e) {
			success = false;
			if(i !=-1) {
				message = "用户"+idList.get(i)+"注销失败";
			}else {
				message = "用户注销失败";
			}
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg)); 

	}
	
	
}
