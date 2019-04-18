package cn.com.infosec.netseal.webserver.util;

import cn.com.infosec.netseal.common.algorithm.gmsm3.GMOTPImp;
import cn.com.infosec.netseal.common.algorithm.sm3.SM3;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.util.Base64;

public class MobileTokenUtil {

	/** 生成种子
	 * @param tokenSn 令牌序列号
	 * @param activeCode 令牌激活码
	 * @return
	 * @throws Exception
	 */
	public static String genSeed(String tokenSn,String activeCode) throws Exception {
		SM3 sm = new SM3();
		sm.reset();
		sm.update(tokenSn.getBytes());
		sm.update(activeCode.getBytes());
	    byte[] digest = new byte[32];
	    sm.digest(digest);
	  
	    String seed = Base64.encode(digest);
	    return seed;
	}
	
	
	/** 验证密码
	 * @param password
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyPassword(String password, String seed) throws Exception {
		//自动时间窗口长度
		int timeWindowLength = ConfigUtil.getInstance().getTimeWindowLength();
		int[] relateValue = new int[timeWindowLength * 2 + 1];
		int pos = 0;
		relateValue[pos++] = 0;
		for (int i = 1; i <= timeWindowLength; i++) {
			relateValue[pos++] = -i;
			relateValue[pos++] = i;
		}
		GMOTPImp gm = new GMOTPImp();
		long timeFactor = DateUtil.getCurrentTime() / 1000 / 60;
		byte[] seedData=Base64.decode(seed);
		for (int i = 0; i < relateValue.length; i++) {
			String genPassword = gm.genTimePassword(seedData, timeFactor + relateValue[i], null, password.length());		
			if(genPassword.equals(password))
				return true;
		}
		return false;
	}
		

	public static void main(String[] args) throws Exception {
		String genSeed = MobileTokenUtil.genSeed("688563", "0686226182275332");
		System.out.println(genSeed);
		boolean result = MobileTokenUtil.verifyPassword("49721937", genSeed);
		System.out.println(result);
	}

}
