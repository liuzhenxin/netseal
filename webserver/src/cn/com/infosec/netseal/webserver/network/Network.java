package cn.com.infosec.netseal.webserver.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import cn.com.infosec.netseal.common.util.StringUtil;

/**
 * linux对网卡操作工具类
 */
public class Network {

	String lineSeparator = System.getProperty("line.separator");

	/**获取本机所有IP
	 * @return IP数组
	 */
	public static String[] allIP() {
		ArrayList<String> ips = new ArrayList<String>();
		try {
			Runtime runt = Runtime.getRuntime();
			Process p = runt.exec("ifconfig");
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String tmp = line.trim();
				if (tmp.startsWith("inet addr:")) {
					int start = tmp.indexOf(":");
					int end = tmp.indexOf(" ", start);
					String ip = tmp.substring(start + 1, end);
					if (!ip.equals("127.0.0.1") && !ip.equals("localhost")) {
						ips.add(ip);
					}
				}
			}
			ips.add("*");
			return (String[]) ips.toArray(new String[0]);
		} catch (Exception e) {
			return null;
		}
	}

	/**获取本机所有处于监听状态的端口
	 * @return 端口数组
	 */
	public static String[] getAllPorts() {
		Set<String> ports = new HashSet<String>();
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(WFWConstant.CHECK_PORT);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				String tmp = line.trim();
				if (tmp.startsWith("tcp") && tmp.endsWith("LISTEN")) {

					String str = tmp.substring(3, tmp.length() - 6);
					str = str.trim();
					int firstKongFlag = str.indexOf(" ");
					int kongFlag = str.lastIndexOf(" ");

					str = str.substring(firstKongFlag, kongFlag).trim();
					int nextKongFlag = str.indexOf(" ");
					str = str.substring(nextKongFlag).trim();
					int lastTag = str.lastIndexOf(":");
					String port = str.substring(lastTag + 1);
					ports.add(port);
				} else {
					continue;
				}
			}
		} catch (Exception e) {

		}
		return (String[]) ports.toArray(new String[0]);
	}

	/**获取本机所有处于监听状态的IP和端口
	 * @return map对象IP和端口
	 */
	public static Map<String, String> getAllIps() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(WFWConstant.CHECK_PORT);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				String tmp = line.trim();
				if (tmp.startsWith("tcp") && tmp.endsWith("LISTEN")) {

					String str = tmp.substring(3, tmp.length() - 6);
					str = str.trim();
					int firstKongFlag = str.indexOf(" ");
					int kongFlag = str.lastIndexOf(" ");

					str = str.substring(firstKongFlag, kongFlag).trim();
					int nextKongFlag = str.indexOf(" ");
					str = str.substring(nextKongFlag).trim();
					if (str.contains("::ffff:"))
						str = str.replace("::ffff:", "");
					String[] ip_port = str.split(":");
					map.put(ip_port[1], ip_port[0]);
				} else {
					continue;
				}
			}
		} catch (Exception e) {

		}
		return map;
	}


	/**设置网卡
	 * @param deviceName
	 * @param ip
	 * @param mask
	 * @param gateway
	 * @throws Exception
	 */
	public void setNetWorkCardInfo(String deviceName, String ip, String mask, String gateway) throws Exception {

		String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + "eth0";
		String newfile = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;

		FileOutputStream newfos = null;
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new Exception("network config file not found");
		} catch (IOException e) {
			throw new Exception("network config file read failed");
		}

		p.setProperty("IPADDR", ip);
		p.setProperty("NETMASK", mask);
		p.setProperty("GATEWAY", gateway);
		p.setProperty("DEVICE", deviceName);

		try {
			newfos = new FileOutputStream(newfile);
			p.store(newfos, "");

		} catch (FileNotFoundException e) {
			throw new Exception("network config file not found");
		} catch (IOException e) {
			throw new Exception("network config file write failed");
		} finally {
			try {
				if (newfos != null) {
					newfos.close();
				}
			} catch (Exception e) {

			}
		}

	}

	/**设置网卡
	 * @param deviceName
	 * @param ip
	 * @param mask
	 * @throws Exception
	 */
	public void setNetWorkCardInfoWithIpv6(String deviceName, String ip, String mask, String ipv6) throws Exception {

		String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX;
		String newfile = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;

		FileOutputStream newfos = null;
		BufferedReader br = null;
	
		StringBuffer sb = new StringBuffer();
		try {
			File f = new File(newfile);
			if (f.exists()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("IPADDR"))
						sb.append("IPADDR=").append(ip).append("\r\n");
					else if (line.startsWith("NETMASK"))
						sb.append("NETMASK=").append(mask).append("\r\n");
					else if (line.startsWith("DEVICE"))
						sb.append("DEVICE=").append(deviceName).append("\r\n");
					else if (line.startsWith("IPV6ADDR"))
						sb.append("IPV6ADDR=").append(ipv6).append("\r\n");
					else
						sb.append(line).append("\r\n");
				}
				newfos = new FileOutputStream(newfile);
			} else {
				String name = deviceName;
				//if (name.indexOf(":") > -1) {
					//int flag = name.indexOf(":");
					//name = name.substring(0, flag).trim();
					filename = filename + name;
					newfos = new FileOutputStream(filename);
					sb.append("IPADDR=").append(ip).append("\r\n");
					sb.append("NETMASK=").append(mask).append("\r\n");
					sb.append("DEVICE=").append(deviceName).append("\r\n");
					if(StringUtil.isNotBlank(ipv6)){
						sb.append("IPV6_AUTOCONFI=no").append("\r\n"); // 设置不使用默认IP地址
						sb.append("IPV6ADDR=").append(ipv6).append("\r\n");
					}
				//} else {
				//	throw new Exception("network config file not found");

				//}
			}
			newfos.write(sb.toString().getBytes());
			newfos.flush();
		} catch (FileNotFoundException e) {
			throw new Exception("network config file not found");
		} catch (IOException e) {
			throw new Exception("network config file read failed");
		} finally {
			if (newfos != null)
				try {
					newfos.close();
				} catch (IOException e) {
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
				}
		}
	
	}

	/**设置网卡 执行命令
	 * @param deviceName
	 * @param ip
	 * @param mask
	 * @param gateway
	 * @throws Exception
	 */
	public void setNetworkCardInfoNotConfig(String deviceName, String ip, String mask, String gateway) throws Exception {
		deviceName = deviceName.trim();
		String name = againstMapNetworkPort(deviceName);
		// String str = deviceName;
		// deviceName = "eth"+str.substring(4,str.length()).trim();
		// save eth config
		setNetWorkCardInfo(name, ip, mask, gateway);

		String shell = name + " " + ip + " netmask " + mask;
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		try {
			BufferedReader in = null;
			String[] commandLine = { WFWConstant.SHELL_PATH, "-c", WFWConstant.EXEIFCONFIG_SHELL + " " + shell };
			proc = runtime.exec(commandLine);
			proc.waitFor();
			if (proc.exitValue() != 0) {

				in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				String text = null;
				String errstr = "";
				while ((text = in.readLine()) != null) {
					errstr = text;
				}

			}

		} catch (Exception e) {

		}
	}

	/**设置网卡 执行命令
	 * @param deviceName
	 * @param ip
	 * @param mask
	 * @throws Exception
	 */
	public void setNetworkCardInfoNotConfigWithIpv6(String deviceName, String ip, String mask, String ipv6) throws Exception {
		deviceName = deviceName.trim();
		String name = deviceName;// againstMapNetworkPort(deviceName);
		
		// 是否含有ipv6地址
		if (StringUtil.isNotBlank(ipv6)){
			// 开启系统ipv6
			NetworkUtil.openIpv6();
			
			String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;
			IniConfigManager icm = new IniConfigManager(filename);
			icm.initInfo();
			
			// 查看 文件中是否配置ipv6信息
			String ipv6addr = icm.getItem("IPV6ADDR");
			if (StringUtil.isBlank(ipv6addr)) {
				Hashtable<String, String> ht = new Hashtable<String, String>();
				ht.put("IPV6INIT", "yes");
				ht.put("IPV6ADDR", "");
				ht.put("IPV6_DEFAULTGW", "");
				icm.addItem(ht);
			}
		}	
		
		setNetWorkCardInfoWithIpv6(name, ip, mask, ipv6);

		String shell = name + " " + ip + " netmask " + mask;
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		try {
			BufferedReader in = null;
			String[] commandLine = { WFWConstant.SHELL_PATH, "-c", WFWConstant.EXEIFCONFIG_SHELL + " " + shell };
			proc = runtime.exec(commandLine);
			proc.waitFor();
			if (proc.exitValue() != 0) {

				in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				String text = null;
				String errstr = "";
				while ((text = in.readLine()) != null) {
					errstr = text;
				}

			}

		} catch (Exception e) {

		}
		
	}

	/**获取路由IP
	 * @param deviceName
	 * @return
	 * @throws Exception
	 */
	public String getGateWayInfo(String deviceName) throws Exception {
		String gateWay = "";
		String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;
		String defaultConfigFile = WFWConstant.DEFAULTGATEWAYCONFIGFILE;
		IniConfigManager icm = new IniConfigManager(filename);
		icm.initInfo();
		gateWay = icm.getItem("GATEWAY");
		if (gateWay == null || gateWay == "") {
			File gate = new File(defaultConfigFile);
			if (gate.exists()) {
				Properties p = new Properties();
				try {
					try {
						p.load(new FileInputStream(defaultConfigFile));
					} catch (FileNotFoundException fe) {
						throw new Exception("default  network config file not found");
					}
				} catch (IOException ioe) {
					throw new Exception("default network config file read failed");
				}

				if (p.containsKey("GATEWAY")) {
					gateWay = p.getProperty("GATEWAY").toString().trim();
				}

			}
		}
		return gateWay;

	}

	/**获取路由IP
	 * @return
	 * @throws Exception
	 */
	public List<String> getGateWayInfo() throws Exception {
		List<String> list = new ArrayList<String>();
		String gateWay = "";
		String ipv6GateWay = "";
		String defaultConfigFile = WFWConstant.DEFAULTGATEWAYCONFIGFILE;
		File gate = new File(defaultConfigFile);
		if (gate.exists()) {
			Properties p = new Properties();
			try {
				try {
					p.load(new FileInputStream(defaultConfigFile));
				} catch (FileNotFoundException fe) {
					throw new Exception("default  network config file not found");
				}
			} catch (IOException ioe) {
				throw new Exception("default network config file read failed");
			}

			if (p.containsKey("GATEWAY")) {
				gateWay = p.getProperty("GATEWAY").toString().trim();
			}
			
			if (p.containsKey("IPV6_DEFAULTGW")) {
				ipv6GateWay = p.getProperty("IPV6_DEFAULTGW").toString().trim();
			}
			
			list.add(0, gateWay);
			list.add(1, ipv6GateWay);

		}
		return list;

	}

	/**保存路由IP
	 * @param gateway
	 * @param gatewayIpv6 
	 * @throws Exception
	 */
	private void storeGateWayInfo(String gateway, String gatewayIpv6) throws Exception {

		String filename = WFWConstant.DEFAULTGATEWAYCONFIGFILE;
		String newfile = WFWConstant.DEFAULTGATEWAYCONFIGFILEBAK;
		FileOutputStream newfos = null;
		if (gateway != null && !"".equals(gateway)) {
			gateway = gateway.trim();
		}

		Properties p = new Properties();
		try {
			p.load(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new Exception("network config file not found");
		} catch (IOException e) {
			throw new Exception("network config file read failed");
		}

		p.setProperty("GATEWAY", gateway);
		p.setProperty("IPV6_DEFAULTGW", gatewayIpv6);

		try {
			newfos = new FileOutputStream(newfile);
			p.store(newfos, "");

			File oldBakFile = new File(filename);
			oldBakFile.delete();
			File newBakFile = new File(newfile);
			newBakFile.renameTo(oldBakFile);
		} catch (FileNotFoundException e) {
			throw new Exception("network config file not found");
		} catch (IOException e) {
			throw new Exception("network config file write failed");
		} finally {
			try {
				if (newfos != null) {
					newfos.close();
				}
			} catch (Exception e) {

			}
		}

	}

	/**网关设置保存
	 * @param gateway 网关IP
	 * @param gatewayIpv6 
	 * @throws Exception
	 */
	public void setGageWayInfo(String gateway, String gatewayIpv6) throws Exception {
		if (gateway != null && !"".equals(gateway)) {
			gateway = gateway.trim();
		}
		
		if (gatewayIpv6 != null && !"".equals(gatewayIpv6)) {
			gatewayIpv6 = gateway.trim();
		}

		storeGateWayInfo(gateway, gatewayIpv6);
		String shellfile = gateway;

		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		try {
			BufferedReader in = null;
			String[] commandLine = { WFWConstant.SHELL_PATH, "-c", WFWConstant.GATEWAYCONFIG_SHELL + " " + shellfile };
			proc = runtime.exec(commandLine);
			proc.waitFor();
			if (proc.exitValue() != 0) {

				in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				String text = null;
				String errstr = "";
				while ((text = in.readLine()) != null) {
					errstr = text;
				}

			}

		} catch (Exception e) {

		}
		
		Process proc1 = null;
		String shellfileIpv6 = gateway;
		try {
			BufferedReader in = null;
			String[] commandLine = { WFWConstant.SHELL_PATH, "-c", WFWConstant.IPV6GATEWAYCONFIG_SHELL + " " + shellfileIpv6 };
			proc1 = runtime.exec(commandLine);
			proc1.waitFor();
			if (proc1.exitValue() != 0) {

				in = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
				String text = null;
				String errstr = "";
				while ((text = in.readLine()) != null) {
					errstr = text;
				}

			}

		} catch (Exception e) {

		}
	}

	public String againstMapNetworkPort(String deviceName) throws Exception {
		Properties p = null;
		deviceName = deviceName.trim();
		String port = "";
		String name = "";
		String portMappingPath = WebUICFG.getMappingNetworkPortPath() + "eth.txt";
		p = new Properties();
		try {
			p.load(new FileInputStream(portMappingPath));

			Enumeration e = p.keys();
			while (e.hasMoreElements()) {
				name = (String) e.nextElement();
				if (name != null && !"".equals(name)) {
					if (p.containsKey(name)) {
						port = p.getProperty(name).toString().trim();
						if (port.equals(deviceName)) {
							return name;
						} else {
							continue;
						}
					}
				}
			}

		} catch (FileNotFoundException fe) {
			throw new Exception("the mapping file called eth.txt of the device not found");
		} catch (IOException ioe) {
			throw new Exception("eth.txt file read failed");
		}
		return name;
	}

	public boolean activateNetworkCard(String deviceName) {
		String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;
		IniConfigManager icm = new IniConfigManager(filename);
		icm.setItem("ONBOOT", "yes");
		return true;
	}

	public boolean deactivateNetworkCard(String deviceName) {
		String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;
		IniConfigManager icm = new IniConfigManager(filename);
		icm.setItem("ONBOOT", "no");
		return true;
	}

	public NetworkCard getNCardInfoFromFile(String deviceName) throws Exception {
		String filename = WFWConstant.NETWORKCARDFILEPATH + WFWConstant.NETWORKCARDFILEPERFIX + deviceName;
		File file = new File(filename);
		if (file.exists()) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(filename));
			} catch (FileNotFoundException e) {
				// TODO 自动生成 catch 块
				throw new Exception("network config file not found");
			} catch (IOException e) {
				// TODO 自动生成 catch 块
				throw new Exception("network config file read failed");
			}

			String name = "";
			String IP = "";
			String mask = "";
			String gateway = "";
			
			String IPV6 = "";
			String IPV6GW = "";

			if (p.containsKey("DEVICE")) {
				name = p.getProperty("DEVICE").toString().trim();
			}

			if (p.containsKey("IPADDR")) {
				IP = p.getProperty("IPADDR").toString().trim();
			}

			if (p.containsKey("NETMASK")) {
				mask = p.getProperty("NETMASK").toString().trim();
			}

			if (p.containsKey("GATEWAY")) {
				gateway = p.getProperty("GATEWAY").toString().trim();
			}
			
			if (p.containsKey("IPV6ADDR")) {
				IPV6 = p.getProperty("IPV6ADDR").toString().trim();
			}
			
			if (p.containsKey("IPV6_DEFAULTGW")) {
				IPV6GW = p.getProperty("IPV6_DEFAULTGW").toString().trim();
			}

			NetworkCard nc = new NetworkCard();
			nc.setName(name);
			nc.setIp(IP);
			nc.setMask(mask);
			nc.setGateway(gateway);
			
			nc.setIpv6(IPV6);
			nc.setIpv6Geteway(IPV6GW);

			return nc;
		} else {
			return null;
		}
	}


	/** 获取网卡信息 
	 * @param is 命令ifconfig -a返回流数据
	 * @return
	 */
	public Vector<NetworkCard> getInfoFromBuffer(InputStream is) {
		// File f = new File("D:/ip.txt");
		
		String section = "";
		NetworkCard card = new NetworkCard();
		Vector<NetworkCard> cardList = new Vector<NetworkCard>();
		try {
			 //FileInputStream fis = new FileInputStream(f);
			 //BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String temp = reader.readLine().trim();

			while (temp != null) {
				temp = temp.trim();
				if (!temp.equalsIgnoreCase("")) {
					section = section + temp + " ";

				} else {
					if (!"".equalsIgnoreCase(section)) {
						card = getNetworkCardInfo(section);
						if (card != null) {
							cardList.add(card);
						}
					}
					section = "";
				}
				temp = reader.readLine();
			}
			// if (!"".equalsIgnoreCase(section))
			// {
			// System.out.println("section is :" + section);
			// card = (NetworkCard)getNetworkCardInfo(section);
			// if (card != null)
			// {
			// cardList.addElement(card);
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cardList;
	}

	public NetworkCard getNetworkCardInfo(String section) {
		NetworkCard netCard = new NetworkCard();
		// String name = "";
		String mappedName = "";
		String ip = "";
		// String gateway = "";
		String bcast = "";
		String mask = "";
		
		String ipv6 = "";
		
		int status = -1;
		int l = section.indexOf("Link encap");
		String head = section.substring(0, l).trim();
		if (section.startsWith("eth")) {
			int loop1 = section.indexOf("inet addr:");
			int loop2 = section.indexOf("Bcast:");
			int loop3 = section.indexOf("Mask:");

			int loop4 = section.indexOf(" inet6 addr: ");

			int loop5 = section.indexOf(" UP ");
			int loop6 = section.indexOf(" BROADCAST ");
			if (loop1 != -1) {
				ip = section.substring(loop1 + 10, loop2);

			} else {
				ip = "";
			}

			if (loop2 != -1) {
				bcast = section.substring(loop2 + 6, loop3);

			} else {
				bcast = "";
			}

			if (loop3 != -1) {
				if (loop4 == -1) {
					if (loop5 == -1) {
						mask = section.substring(loop3 + 5, loop6);
					} else {
						mask = section.substring(loop3 + 5, loop5);
					}
				} else {
					ipv6 = section.substring(loop4 + 12, loop5);
					mask = section.substring(loop3 + 5, loop4);
				}
			} else {
				mask = "";
			}
			if (loop5 == -1) {
				status = 0;
			} else {
				status = 1;
			}

			// try {
			// gateway = this.getGateWayInfo(bak);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			try {
				//mappedName = mapNetworkPort(head);
				mappedName = head;
			} catch (Exception e) {
				e.printStackTrace();
			}

			netCard.setName(mappedName);
			netCard.setIp(ip);
			netCard.setBcast(bcast);
			netCard.setMask(mask);
			netCard.setStatus(status);
			
			netCard.setIpv6(ipv6);
			
		} else {

			netCard = null;
		}

		return netCard;
	}

	

	/**获取网卡信息
	 * @return
	 */
	public Vector<NetworkCard> getNetWorkCardInfo() {
		Vector<NetworkCard> cardList = new Vector<NetworkCard>();
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		Network net = new Network();
		try {
			String[] commandLine = { WFWConstant.SHELL_PATH, "-c", WFWConstant.IFCONFIG_SHELL };
			proc = runtime.exec(commandLine);
			proc.waitFor();
			if (proc.exitValue() != 0) {

				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				String text = null;
				String errstr = "";
				while ((text = in.readLine()) != null) {
					errstr = text;
				}

			}

			cardList = (Vector<NetworkCard>) net.getInfoFromBuffer(proc.getInputStream());
			
			//cardList = (Vector<NetworkCard>) net.getInfoFromBuffer(null);
		} catch (Exception e) {

		}

		return cardList;

	}
	public HashMap<String,String> getHostNames() throws Exception {
		String filename = WFWConstant.HOSTSFILE;
		File f = new File(filename);
		HashMap<String,String> map = new HashMap<String,String>();
		if (f.exists()) {
			String line = "";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (!line.startsWith("#")) {
						String[] hostips = splitByBlankChar(line);
						String tmp = "";
						if (hostips != null) {
							int length = hostips.length;

							if (length <= 1) {
								continue;

							} else {
								tmp = hostips[hostips.length - 1];
							}
							map.put(hostips[0], tmp);

						}
					} else {
						continue;
					}
				}
			} catch (FileNotFoundException e) {
				throw new Exception("hosts file not found");
			} catch (IOException e) {
				throw new Exception("hosts file read failed");
			}

		}

		return map;
	}

	public String getHosts(String filename) throws Exception {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line = "";
		File file = new File(filename);
		if (file.exists()) {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#")) {
						sb.append(line).append("\r\n");
					}
				}
				return sb.toString();

			} catch (FileNotFoundException e) {
				throw new Exception("hosts file not found");
			} catch (IOException ioe) {
				throw new Exception("hosts file read failed");
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (Exception e) {

				}
			}

		}
		return sb.toString();
	}

	public void writeToHosts(String hoststr) throws Exception {
		String[] hostarr = null;
		FileOutputStream fos = null;
		String hostfile = WFWConstant.HOSTSFILE;
		if (hoststr != null && !"".equals(hoststr)) {
			hostarr = hoststr.split("\r\n");
		}

		try {
			fos = new FileOutputStream(hostfile);

			if (hostarr != null) {
				for (int i = 0; i < hostarr.length; i++) {
					fos.write((hostarr[i] + "\r\n").getBytes("GBK"));
				}
			}
		} catch (Exception ie) {
			throw new Exception("不能写入文件"+ hostfile +", 请确定是否有权限");
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {

			}
		}

	}
	private static String[] splitByBlankChar(String str) {

		if ((str == null) || str.equals(""))

			return new String[0];

		str = str.trim();

		return str.split("( |\\t)++");

	}
	public ArrayList<String> getNetWorkCardNameList() {
		ArrayList<String> nameList = new ArrayList<String>();
		Vector cardList = getNetWorkCardInfo();

		for (int i = 0; i < cardList.size(); i++) {
			NetworkCard card = (NetworkCard) cardList.get(i);
			nameList.add(card.getName());
		}
		return nameList;
	}
	public static void main(String[] args) throws IOException {
		
	}
}
