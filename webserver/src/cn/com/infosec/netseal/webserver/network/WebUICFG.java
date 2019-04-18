package cn.com.infosec.netseal.webserver.network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WebUICFG {
	private static String cfgFilePath = "";
	private static Properties p = null;
	public static String appServer = "";
	private static String serverHome = "";
	private static String webuiLogPath = "";
	private static String tomcatConfigPath = "";
	private static String mappingNetworkPortPath = "";
	private static boolean parsed;

	public WebUICFG() {

	}

	public WebUICFG(String fileName) {
		WebUICFG.cfgFilePath = fileName;
	}

	public void load() throws Exception {
		if (!parsed) {
			p = new Properties();
			try {
				p.load(new FileInputStream(cfgFilePath));
				if (p.containsKey(WFWConstant.WEBUICFG_SERVERHOME)) {
					serverHome = p.getProperty(WFWConstant.WEBUICFG_SERVERHOME).toString().trim();
				}

				if (p.containsKey(WFWConstant.TOMCAT_CONFIG_PATH)) {
					tomcatConfigPath = p.getProperty(WFWConstant.TOMCAT_CONFIG_PATH).toString().trim();
				}

				if (p.containsKey(WFWConstant.WEBUILOG_PATH)) {
					webuiLogPath = p.getProperty(WFWConstant.WEBUILOG_PATH).toString().trim();
				}

				if (p.containsKey(WFWConstant.MAPPING_NETWORKPORT_PATH)) {
					mappingNetworkPortPath = p.getProperty(WFWConstant.MAPPING_NETWORKPORT_PATH).toString().trim();
				}

			} catch (FileNotFoundException fne) {
				throw new Exception("web.properties file not Found");

			} catch (IOException ioe) {
				throw new Exception("read and write web.properties file failed");
			}
			parsed = true;
		}
	}

	public static void store() throws Exception {
		p = new Properties();
		try {
			p.setProperty(WFWConstant.WEBUICFG_SERVERHOME, serverHome);
			p.setProperty(WFWConstant.TOMCAT_CONFIG_PATH, tomcatConfigPath);
			p.setProperty(WFWConstant.WEBUILOG_PATH, webuiLogPath);
			p.setProperty(WFWConstant.MAPPING_NETWORKPORT_PATH, mappingNetworkPortPath);
			p.store(new FileOutputStream(cfgFilePath), null);

		} catch (FileNotFoundException fne) {
			throw new Exception("web.properties file not Found");

		} catch (IOException ioe) {
			throw new Exception("read and write web.properties file failed");

		}
	}

	public static String getCfgFilePath() {

		return cfgFilePath;
	}

	public static String getServerHome() {

		return serverHome;
	}

	public static void setServerHome(String serverHome) {

		WebUICFG.serverHome = serverHome;
	}

	public static String getTomcatConfigPath() {
		return tomcatConfigPath;
	}

	public static String getWebuiLogPath() {
		return webuiLogPath;
	}

	public static String getMappingNetworkPortPath() {
		return mappingNetworkPortPath;
	}

	public static boolean isParsed() {
		return parsed;
	}

	public static void setParsed(boolean parsed) {
		WebUICFG.parsed = parsed;
	}

}
