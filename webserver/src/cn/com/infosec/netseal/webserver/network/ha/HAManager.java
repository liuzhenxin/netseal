package cn.com.infosec.netseal.webserver.network.ha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.webserver.network.LogFileItem;
import cn.com.infosec.netseal.webserver.network.NetworkUtil;
import cn.com.infosec.netseal.webserver.network.WFWConstant;
import cn.com.infosec.netseal.webserver.network.WebUICFG;

public class HAManager {

	public static String[] getPorts() {
		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			proc = rt.exec("ls /dev/");
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("") || !line.startsWith("ttyS"))
					continue;
				sb.append(line).append(",");
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1).split(",");
		}
		return null;
	}

	/** 读取/etc/ha.d/ha.cf和haresources文件信息
	 * @return
	 * @throws Exception
	 */
	public static HAInfo getHAInfo() throws Exception {
		HAInfo info = new HAInfo();
		BufferedReader in = null;
		try {
			// 处理ha.cf文件。
			in = new BufferedReader(new FileReader(WFWConstant.HACF_FILE));
			 //in = new BufferedReader(new FileReader("f:/ha.cf"));
			String line = "";
			ArrayList nodelist = new ArrayList();
			String logFile = "";
			String debugFile = "";
			String HADevice = "";
			String pingip = "";
			String baud = "";
			String serialportname = "";
			while ((line = in.readLine()) != null) {
				line = line.trim();
				// System.out.println(line);
				if (line.startsWith("node")) {
					nodelist.add(line.substring(4, line.length()).trim());
					// System.out.println("node:"+line.substring(4,
					// line.length()).trim());
				}

				if (line.startsWith("logfile")) {

					logFile = line.substring(7, line.length()).trim();
					// System.out.println(logFile);
				}
				if (line.startsWith("debugfile")) {
					debugFile = line.substring(9, line.length()).trim();
				}

				if (line.startsWith("ucast")) {
					HADevice = line.substring(5, line.length()).trim();
					
					// System.out.println(HADevice);
				}
				if (line.startsWith("ping ")) {
					pingip = line.substring(5).trim();
				}
				if (line.startsWith("baud "))
					baud = line.substring(5).trim();
				if (line.startsWith("serial ") && line.contains("# Linux")) {
					int x = line.indexOf("ttyS");
					if (x > -1)
						serialportname = line.substring(x, x + 5);
				}
			}
			if(nodelist.size()<2)
				throw new Exception("HA 配置文件中节点配置有错误！");
			// 处理haresources文件。
			// 主节点设备名 IPaddr::虚IP/24/eth0 SendArp::虚IP/eth0 shell
			in = new BufferedReader(new FileReader(WFWConstant.HARESOURCES_FILE));
			// in = new BufferedReader(new FileReader("F:/haresources"));
			
			String mainNodeDeviceName = "";
			String vmIP = "";
			int netMaskValue = 0;
			String IPDevice = "";
			Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
			try {
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if(line.startsWith("#"))
						continue;
					Matcher matcher = pattern.matcher(line);
					 if (matcher.find()) {  
						vmIP = matcher.group(0);  
			            int ipIndexOf=line.indexOf(vmIP);
			            mainNodeDeviceName=line.substring(0,ipIndexOf).trim();
			            String suffix=line.substring(ipIndexOf+vmIP.length()+1);
			            String[] tempArray=suffix.split("/");
			            netMaskValue=Integer.parseInt(tempArray[0].trim());
			            IPDevice = tempArray[1].trim();			            
			        }
				}
			} catch (Exception e) {
				throw new Exception("HA 配置文件格式有错误！");
			}				

			info.setMainNodeDeviceName(mainNodeDeviceName);
			info.setVmIP(vmIP);
			info.setNetMaskValue(netMaskValue);
			info.setIPDevice(IPDevice);
			info.setHADevice(HADevice);
			info.setNodeList(nodelist);
			info.setLogFile(logFile);
			info.setDebugFile(debugFile);
			info.setPingIp(pingip);
			info.setBaudRate(baud);
			info.setSerialportname(serialportname);
		} catch (FileNotFoundException e) {			
			throw new Exception("HA 配置文件不存在！");

		} catch (IOException ioe) {			
			throw new Exception("HA 配置文件读取失败");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {					
					throw new Exception("HA 配置文件读取失败");
				}
			}
		}
		return info;
	}

	public static String againstMapNetworkPort(String deviceName) throws Exception {
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

	public static HAInfo setHAInfo(HAInfo info) throws Exception {
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			// 处理ha.cf文件。
			File f = new File(WFWConstant.HACF_FILE);
			File fbak = new File(WFWConstant.HACF_FILE + ".bak");
			in = new BufferedReader(new FileReader(f));
			out = new BufferedWriter(new FileWriter(fbak));

			// File f = new File("F:/ha.cf");
			// File fbak = new File("F:/ha.cf" + ".bak");
			// in = new BufferedReader(new FileReader(f));
			// out = new BufferedWriter(new FileWriter(fbak));

			String line = "";
			String lineSeparator = System.getProperty("line.separator");
			ArrayList nodeList = new ArrayList();
			nodeList = info.getNodeList();
			String HADevice = info.getHADevice();
			String pingip = info.getPingIp();
			String baud = info.getBaudRate();
			String serialportname = info.getSerialportname();
			boolean hasping = false;
			boolean iswebport = false;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("ucast") || line.startsWith("#ucast")) {
					if (HADevice.equals(""))
						out.write("#" + line + lineSeparator);
					else
						out.write("ucast " + HADevice + lineSeparator);
				} else if (line.startsWith("ping ") || line.startsWith("#ping ")) {
					if (!pingip.equals("")) {
						out.write("ping " + pingip + lineSeparator);
						hasping = true;
					} else {
						if (line.startsWith("#"))
							out.write(line + lineSeparator);
						else
							out.write("#" + line + lineSeparator);
					}
				} else if (line.contains("respawn hacluster")) {
					if (hasping) {
						line = line.startsWith("#") ? line.substring(1) : line;
						out.write(line + lineSeparator);
					} else {
						line = line.startsWith("#") ? line : "#" + line;
						out.write(line + lineSeparator);
					}
				} else if (line.startsWith("#baud") || line.startsWith("baud")) {
					if (!baud.equals("")) {
						out.write("baud " + baud + lineSeparator);
						iswebport = true;
					} else {
						if (line.startsWith("#"))
							out.write(line + lineSeparator);
						else
							out.write("#" + line + lineSeparator);
					}
				} else if (line.contains("serial") && line.contains("/dev/ttyS")) {
					if (iswebport) {
						out.write("serial /dev/" + serialportname + "\t# Linux" + lineSeparator);
					} else {
						line = line.startsWith("#") ? line : "#" + line;
						out.write(line + lineSeparator);
					}
				} else {
					if (!line.startsWith("node")) {
						out.write(line + lineSeparator);
					}
				}

			}

			Iterator it = nodeList.iterator();
			while (it.hasNext()) {
				String nodeName = it.next().toString();
				out.write("node " + nodeName + lineSeparator);
			}
			in.close();
			out.close();
			f.renameTo(new File(WFWConstant.HACF_FILE + ".bakup"));
			fbak.renameTo(new File(WFWConstant.HACF_FILE));

			// 处理haresources文件。
			// 主节点设备名 IPaddr::虚IP/24/eth0 SendArp::虚IP/eth0 shell
			File f1 = new File(WFWConstant.HARESOURCES_FILE);
			File fbak1 = new File(WFWConstant.HARESOURCES_FILE + ".bak");
			in = new BufferedReader(new FileReader(f1));
			out = new BufferedWriter(new FileWriter(fbak1));
			
			String mainNodeDeviceName = info.getMainNodeDeviceName();
			String vmIP = info.getVmIP();
			int netMaskValue = info.getNetMaskValue();
			String IPDevice = info.getIPDevice().trim();
			//String netmask = info.getNetmask();
			//NetSeal 10.20.87.155/24/eth0 netsealscript.dat
			Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if(line.startsWith("#"))
					out.write(line + lineSeparator);
				else{
					Matcher matcher = pattern.matcher(line);
					 if (matcher.find()) {
						 int p = line.lastIndexOf(" ");
						String netseal_shell = "";
						if (p != -1) {
							netseal_shell = line.substring(p + 1, line.length()).trim();
							String newItem = mainNodeDeviceName + " " + vmIP + "/" + netMaskValue + "/" + IPDevice +":0 " + netseal_shell;
							out.write(newItem + lineSeparator);
						}else {
							throw new Exception("HA haresources配置文件格式错误");
						}
						
			        }
				}
								
			}
			f1.delete();
			fbak1.renameTo(new File(WFWConstant.HARESOURCES_FILE));


		} catch (FileNotFoundException e) {
			throw new Exception("HA 配置文件不存在");

		} catch (IOException ioe) {
			throw new Exception("HA 配置文件读取失败");
		} finally {
			try {
				if (in != null) {
					in.close();
				}

				if (out != null) {
					out.close();
				}
			} catch (Exception e) {

			}
		}
		return info;
	}

	public static boolean startServer() throws Exception {
		boolean f = false;

		String shell = WFWConstant.HEARTBEAT_SHELL + " start";
		File shellFile = new File(WFWConstant.HEARTBEAT_SHELL);
		if (shellFile.exists()) {
			NetworkUtil.blockCommandLine(shell);
			// Util.echoCommandLine(shell);
			if (queryServerStatus() == true)
				f = true;
		} else {
			throw new Exception("HA 服务的脚本文件不存在");
		}
		return f;
	}

	public static boolean stopServer() throws Exception {
		boolean f = false;
		String shell = WFWConstant.HEARTBEAT_SHELL + " stop";
		File shellFile = new File(WFWConstant.HEARTBEAT_SHELL);
		if (shellFile.exists()) {
			NetworkUtil.blockCommandLine(shell);
			if (queryServerStatus() == false)
				f = true;
		} else {
			throw new Exception("HA 服务的脚本文件不存在");
		}
		return f;
	}

	public static boolean queryServerStatus() throws Exception {

		String shell = WFWConstant.HEARTBEAT_SHELL + " status";
		File shellFile = new File(WFWConstant.HEARTBEAT_SHELL);
		if (shellFile.exists()) {
			String echo = NetworkUtil.echoCommandLine(shell);
			if (echo.startsWith(WFWConstant.HA_STARTSTATUS))
				return true;
			if (echo.startsWith(WFWConstant.HA_STOPSTATUS))
				return false;
		} else {
			throw new Exception("HA 服务的脚本文件不存在");
		}
		return false;
	}

	public static LogFileItem getLogInfo() throws Exception {
		LogFileItem item = new LogFileItem();
		HAInfo info = null;
		try {
			info = getHAInfo();
		} catch (Exception e) {
			throw e;
		}

		if (info != null) {
			String logFile = info.getLogFile().trim();
			File f = new File(logFile);
			if (f.exists()) {
				Date lastModify = new Date(f.lastModified());

				double size = f.length();

				item.setFileName(logFile);
				item.setLastModify(lastModify);
				item.setSize(size);
			} else {
				throw new Exception("HA 日志文件" + logFile + " 不存在");
			}
		}

		return item;
	}

	public static ArrayList getAllLogInfo() {
		ArrayList list = new ArrayList();
		HAInfo info = null;
		try {
			info = getHAInfo();
		} catch (Exception e) {
		}
		if (info != null) {
			String logFile = info.getLogFile().trim();
			String logPath = logFile.substring(0, logFile.lastIndexOf("/") + 1);
			String debugFile= info.getDebugFile();
			File file = new File(logPath);
			File flist[] = file.listFiles();

			for (int i = 0; i < flist.length; i++) {
				File f = flist[i];
				String name = f.getAbsolutePath();
				if (f.isFile() && (name.equals(logFile) || (name.startsWith(logFile) || name.startsWith(debugFile)))) {
					LogFileItem item = new LogFileItem();
					Date lastModify = new Date(f.lastModified());

					double size = f.length();
					item.setFileName(name);
					item.setLastModify(lastModify);
					item.setSize(size);
					list.add(item);

				}
			}
		}
		list = sort(list);
		return list;
	}

	private static ArrayList sort(ArrayList list) {

		for (int i = 0; i < list.size(); i++) {
			LogFileItem lfiMax = (LogFileItem) list.get(i);
			Date maxdate = lfiMax.getLastModify();

			for (int j = i + 1; j < list.size(); j++) {
				LogFileItem lfi = (LogFileItem) list.get(j);
				Date d = lfi.getLastModify();
				if (d.after(maxdate)) {
					lfiMax = (LogFileItem) list.get(i);
					LogFileItem lfitemp = new LogFileItem();
					lfitemp.setFileName(lfiMax.getFileName());
					lfitemp.setSize(lfiMax.getSize());
					lfitemp.setLastModify(lfiMax.getLastModify());

					lfi = (LogFileItem) list.get(j);
					LogFileItem lfitemp2 = new LogFileItem();
					lfitemp2.setFileName(lfi.getFileName());
					lfitemp2.setSize(lfi.getSize());
					lfitemp2.setLastModify(lfi.getLastModify());

					list.set(j, lfitemp);
					list.set(i, lfitemp2);

					// for (int m=0;m<list.size();m++)
					// {
					// LogFileItem lg = (LogFileItem)list.get(m);
					// System.out.print(lg.getFileName());
					// System.out.print(",");

					// System.out.print(lg.getLastModify().toLocaleString());
					// System.out.println();
					// }
					lfiMax = (LogFileItem) list.get(i);
					maxdate = lfiMax.getLastModify();

				}
			}

			// System.out.println("now max is: " +
			// ((LogFileItem)list.get(i)).getLastModify().toLocaleString());
			// for (int m=0;m<list.size();m++)
			// {
			// LogFileItem lg = (LogFileItem)list.get(m);
			// System.out.print(lg.getFileName());
			// System.out.print(",");

			// System.out.print(lg.getLastModify().toLocaleString());
			// System.out.println();
			// }
		}

		return list;
	}
}
