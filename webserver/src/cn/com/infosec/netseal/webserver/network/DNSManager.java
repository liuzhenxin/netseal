package cn.com.infosec.netseal.webserver.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 *DNS操作工具类
 */
public class DNSManager {
	private String dnsConfigFileName = "/etc/resolv.conf";

	private String lineSeperator = System.getProperty("line.separator");

	private Vector<String> nameServerList = null;

	public DNSManager() {
		dnsConfigFileName = WFWConstant.DNSCFGFile;
	}

	/**获取dns
	 * @return
	 * @throws Exception
	 */
	public Vector<String> getNameServerList() throws Exception {
		this.getNameServerInfo();
		return nameServerList;
	}

	private boolean getNameServerInfo() throws Exception {
		nameServerList = new Vector<String>();

		BufferedReader br = null;
		try {

			br = new BufferedReader(new FileReader(dnsConfigFileName));
			String line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (line.startsWith("nameserver")) {
					int i = line.indexOf(" ");
					if (i != -1) {
						String ip = line.substring(i, line.length()).trim();
						nameServerList.add(ip);
					}
				}
				line = br.readLine();
			}

			br.close();

		} catch (FileNotFoundException fnfe) {
			throw new Exception("dns config file not found");

		} catch (IOException ioe) {
			throw new Exception("dns config file IO exception");

		} finally {
			try {

				if (br != null) {
					br.close();
				}

			} catch (Exception e) {
				throw new Exception("dns config file IO exception");
			}
		}
		return true;
	}

	/**DNS设置保存
	 * @param list为DNS IP
	 * @throws Exception
	 */
	public synchronized void setNameServerInfo(Vector<String> list)
			throws Exception {
		if (list != null) {
			BufferedWriter bw = null;
			int num = list.size();
			String content = "";
			try {
				bw = new BufferedWriter(new FileWriter(dnsConfigFileName));
				for (int i = 0; i < num; i++) {
					String ip = list.get(i).toString();
					String line = "nameserver " + ip + lineSeperator;
					content = content + line;
				}
				bw.write(content);
				bw.close();
			} catch (FileNotFoundException fnfe) {
				throw new Exception("dns config file not found");

			} catch (IOException ioe) {
				throw new Exception("dns config file IO exception");

			} finally {
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
						// TODO 自动生成 catch 块
						throw new Exception("dns config file IO exception");
					}
				}
			}
		}
	}

	public void addNameServer(String ip) throws Exception {
		if ((nameServerList == null) || (nameServerList.isEmpty())) {
			this.getNameServerInfo();
		}

		if (nameServerList != null) {
			if (nameServerList.contains(ip)) {

			} else {
				nameServerList.add(ip);
				this.setNameServerInfo(nameServerList);
			}
		}
	}

	public void deleteNameServer(String ip) throws Exception {
		if ((nameServerList == null) || (nameServerList.isEmpty())) {
			this.getNameServerInfo();
		}

		if (nameServerList != null) {
			if (nameServerList.contains(ip)) {
				nameServerList.remove(ip);
				this.setNameServerInfo(nameServerList);
			}
		}
	}

	public void editNameServer(Vector<String> list) throws Exception {
		this.setNameServerInfo(list);
	}

	public static void main(String[] args) throws IOException {
		// DNSmanager dns = new DNSmanager();
		// dns.addNameServer("192.168.0.4");
		// dns.deleteNameServer("192.168.0.3");

	}
}
