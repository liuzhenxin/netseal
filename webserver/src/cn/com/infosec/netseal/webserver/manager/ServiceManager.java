package cn.com.infosec.netseal.webserver.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.log.LoggerUtil;

public class ServiceManager {
	private static String appServerPath = Constants.APPSERVER_PATH;
	private static String command = "AppServer.sh";
	private static String command2 = "AppServer2.sh";
	
	/** 重置配置
	 * @return
	 * @throws Exception
	 */
	public static boolean reloadConfig() throws Exception {
		String result = runCommand("-reloadconf", command2);
		if(result.indexOf("success")>-1)
			return true;
		else
			return false;
	}
	
	private static String runCommand(String arg, String command) {
		StringBuffer sb = new StringBuffer();
		String arg0 = appServerPath + command + " " + arg;
		try {
			Process p = Runtime.getRuntime().exec(arg0, null, new File(appServerPath));
			System.out.println(arg0);

			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				sb.append(line);				
			}
			input.close();
		} catch (Exception e) {			
			LoggerUtil.errorlog("run command "+ arg0 + " error", e);
			return e.getMessage();
		}
		return sb.toString();
	}
}
