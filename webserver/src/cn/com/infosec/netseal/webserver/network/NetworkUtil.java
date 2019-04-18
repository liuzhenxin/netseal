package cn.com.infosec.netseal.webserver.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import cn.com.infosec.netseal.common.util.ExecSh;
import cn.com.infosec.netseal.common.util.StringUtil;

public class NetworkUtil {

	private HashMap map = new HashMap();

	public NetworkUtil() {
	}

	public NetworkUtil(HashMap map) {
		this.map = map;
	}

	/**
	 * 开启IPv6
	 * 
	 * @return
	 */
	public static void openIpv6(){
		String defaultConfigFile = WFWConstant.DEFAULTGATEWAYCONFIGFILE;
		IniConfigManager icmConf = new IniConfigManager(defaultConfigFile);
		icmConf.initInfo();
		String ipv6_Net = icmConf.getItem("NETWORKING_IPV6");
		if (StringUtil.isNotBlank(ipv6_Net)) {
			Hashtable<String, String> ht = new Hashtable<String, String>();
			ht.put("NETWORKING_IPV6", "yes");
			icmConf.addItem(ht);
		}
	}
	
	public static boolean isNetMask(String netmask) {
		boolean f = true;
		StringBuffer sb = new StringBuffer();
		String[] elem = new String[4];
		elem = netmask.split("\\.");
		int count = 0;
		for (int i = 0; i < elem.length; i++) {
			int num = Integer.parseInt(elem[i]);
			if (num == 255) {
				count++;
			}
			String temp = Integer.toBinaryString(num);
			if (temp.length() < 8) {
				for (int k = temp.length(); k < 8; k++) {
					temp = "0" + temp;
				}

			}
			// System.out.println(temp.toString());

			sb.append(temp);
		}
		if (count == 4) {
			return false;
		}
		// System.out.println(sb.toString());
		int len = sb.length();
		// System.out.println("len is :"+len);
		int locate = sb.indexOf("0");
		// System.out.println("locate is :"+locate);
		String stemp = sb.substring(locate);
		// System.out.println(stemp);
		for (int j = 0; j < stemp.length(); j++) {
			String t = stemp.substring(j, j + 1);
			if ("1".equalsIgnoreCase(t))
				f = false;
		}

		return f;
	}

	public static int countNetMaskByONE(String netmask) {
		int total = 0;
		StringBuffer sb = new StringBuffer();
		String[] elem = new String[4];
		elem = netmask.split("\\.");
		int count = 0;
		for (int i = 0; i < elem.length; i++) {
			int num = Integer.parseInt(elem[i]);
			String temp = Integer.toBinaryString(num);
			sb.append(temp);
		}

		int len = sb.length();
		String stemp = sb.substring(0, len);
		for (int j = 0; j < len; j++) {
			String t = stemp.substring(j, j + 1);
			if ("1".equalsIgnoreCase(t))
				total++;
		}

		return total;
	}

	public static void blockCommandLine(String shell) {
		boolean flag = true;

		String echo = "";
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		String[] commandLine = { WFWConstant.SHELL_PATH, "-c", shell };

		BufferedReader br = null;
		try {
			proc = runtime.exec(commandLine);
			flag = true;
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			echo = br.readLine();
			if (echo == null) {
				br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				String echo1 = "";
				while ((echo1 = br.readLine()) != null) {
				}
			} else {
				while ((echo = br.readLine()) != null) {

				}
			}
			br.close();
		} catch (IOException ioe) {
			flag = false;

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
				}
			}
			proc.destroy();
			proc = null;

		}

	}

	public static boolean blockCommandLineForTomcat(String shell) {
		boolean flag = true;

		String echo = "";
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		String[] commandLine = { WFWConstant.SHELL_PATH, "-c", shell };

		String serverHome = WebUICFG.getServerHome();
		String configPath = serverHome + "bin/config";
		BufferedReader br = null;
		try {
			proc = runtime.exec(commandLine);
			flag = true;
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			echo = br.readLine();
			if (echo == null) {
				br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				flag = false;
				String echo1 = "";
				while ((echo1 = br.readLine()) != null) {
				}
				br.close();
			} else {
				while ((echo = br.readLine()) != null) {
				}

			}
		} catch (IOException ioe) {
			flag = false;

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
				}
			}
			proc.destroy();
			proc = null;

		}
		return flag;

	}

	public static String parseMaskValue(int value) {
		String netmask = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= 32; i++) {
			if (i > value) {
				sb.append("0");
			} else {
				sb.append("1");
			}
		}

		String stemp = sb.toString();
		// System.out.println(stemp);
		String ip[] = new String[4];
		int ipvalue[] = new int[4];
		for (int j = 0; j < 4; j++) {
			// System.out.println(j);
			ip[j] = stemp.substring(j * 8, (j * 8) + 8);
			// System.out.println(ip[j]);

			ipvalue[j] = Integer.valueOf(ip[j], 2).intValue();

			if (j < 3) {
				netmask = netmask + ipvalue[j] + ".";
			} else {
				netmask = netmask + ipvalue[j];
			}
		}

		// System.out.println(netmask);

		return netmask;
	}

	public static int getNetMaskValue(String netmask) {
		int value = 0;
		StringBuffer sb = new StringBuffer();
		String[] elem = new String[4];
		elem = netmask.split("\\.");
		for (int i = 0; i < elem.length; i++) {
			int num = Integer.parseInt(elem[i]);
			String temp = Integer.toBinaryString(num);
			sb.append(temp);
		}

		int len = sb.length();
		for (int j = 0; j < len; j++) {
			String t = sb.substring(j, j + 1);
			if ("1".equalsIgnoreCase(t))
				value++;
		}

		return value;
	}

	public static String echoCommandLine(String shell, String serverHome) {
		boolean flag = true;

		String echo = "";
		String echobak = "";
		BufferedReader br = null;
		// final Process proc = null;

		String[] commandLine = { WFWConstant.SHELL_PATH, "-c", shell };
		try {

			flag = true;
			Runtime runtime = Runtime.getRuntime();
			// long startTime = System.currentTimeMillis();
			// proc = runtime.exec(commandLine);

			final Process proc = Runtime.getRuntime().exec(shell, null, new File(serverHome));

			new Thread(new Runnable() {
				public void run() {
					try {
						InputStreamReader eisr = new InputStreamReader(proc.getErrorStream());
						BufferedReader ebr = new BufferedReader(eisr);

						String eoutStr = ebr.readLine();
						while (eoutStr != null) {
							eoutStr = ebr.readLine();
						}

					} catch (Exception e) {
					}
				}
			}).start();

			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			// String serverHome = WebUICFG.getServerHome();
			String configPath = serverHome + "bin/config";
			while ((echo = br.readLine()) != null) {
				echobak = echo;
				if (echo.equals(configPath)) {
				}

			}

			echo = echobak;

		} catch (IOException ioe) {
			flag = false;

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			// proc.destroy();
			// proc = null;
			return echo;
		}

	}

	public static String echoCommandLine(String shell) {
		boolean flag = true;

		String echo = "";
		String echobak = "";
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		String[] commandLine = { WFWConstant.SHELL_PATH, "-c", shell };
		BufferedReader br = null;
		try {
			long startTime = System.currentTimeMillis();
			proc = runtime.exec(commandLine);

			flag = true;

			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			echo = br.readLine();
			if (echo != null) {
				echobak = echo;
				while ((echo = br.readLine()) != null) {
				}
			} else {
				br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				if (br != null) {
					while ((echo = br.readLine()) != null) {
					}
				}
			}
			br.close();
			long endTime = System.currentTimeMillis();
			long time = endTime - startTime;
			echo = echobak;
		} catch (IOException ioe) {
			flag = false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			proc.destroy();
			proc = null;
			return echo;
		}

	}

	public Object getKey(Object value) {
		Object o = null;
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue().equals(value)) {
				o = entry.getKey();
				return o;
			}

		}
		return o;
	}

	public static String getHostName() throws Exception {
		return ExecSh.exec("uname -n");
	}

	public static void main(String[] args) throws Exception {
		System.out.println(NetworkUtil.isNetMask("255.255.255.128"));
	}

}
