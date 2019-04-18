package cn.com.infosec.netseal.webserver.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *配置管理类
 */
public class IniConfigManager {

	String fileName;
	Hashtable iniInfoHash;
	int active;
	String lineSeparator;
	public int errorLineNum = -1;

	public IniConfigManager() {
		lineSeparator = System.getProperty("line.separator");
		iniInfoHash = new Hashtable();
	}

	public IniConfigManager(String fileName) {
		iniInfoHash = new Hashtable();
		lineSeparator = System.getProperty("line.separator");
		this.fileName = fileName;
	}

	public String getItem(String key) {
		String value = "";
		// if ((iniInfoHash == null)||(iniInfoHash.isEmpty()))
		// {
		boolean flag = initInfo();
		// }
		if (flag == true) {
			if (iniInfoHash.containsKey(key)) {
				value = iniInfoHash.get(key).toString();
			}
		} else {
			value = "";
		}
		return value;
	}

	public boolean setItem(String key, String value) {
		if ((iniInfoHash == null) || (iniInfoHash.isEmpty())) {
			if (initInfo()) {

			} else {
				return false;
			}
		}

		if ((iniInfoHash != null) && (!iniInfoHash.isEmpty())) {
			if (iniInfoHash.containsKey(key)) {
				iniInfoHash.put(key, value);
				setItem(iniInfoHash);
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;

	}

	public boolean addItem(String key, String value) {
		if ((iniInfoHash == null) || (iniInfoHash.isEmpty())) {
			if (initInfo()) {

			} else {
				return false;
			}

		}

		if (iniInfoHash != null) {
			iniInfoHash.put(key, value);
			addItem(iniInfoHash);
		} else {
			return false;
		}
		return true;
	}

	public synchronized boolean addItem(Hashtable itemHash) {
		Enumeration itemKeys = itemHash.keys();
		String newContent = "";
		DataOutputStream dout = null;
		FileInputStream fis = null;

		InputStreamReader isr = null;

		BufferedReader lnr = null;
		Hashtable hash = (Hashtable) itemHash.clone();
		try {
			fis = new FileInputStream(fileName);

			isr = new InputStreamReader(fis, "ISO-8859-1");
			lnr = new BufferedReader(isr);

			String line = lnr.readLine();
			String key = "";
			String value = "";
			while (line != null) {
				int locate = line.trim().indexOf("=");
				if (line.trim().indexOf("=") != -1) {
					if (!line.trim().startsWith("=")) {
						key = line.trim().substring(0, locate);
						if (itemHash.containsKey(key.trim())) {
							newContent = newContent
									+ key
									+ "="
									+ itemHash.get(key.trim()).toString()
											.trim() + lineSeparator;
							hash.remove(key);
						} else {
							newContent = newContent + line + lineSeparator;
						}

					} else {
						newContent = newContent + line + lineSeparator;
					}
				} else {
					newContent = newContent + line + lineSeparator;
				}
				line = lnr.readLine();
			}
			if ((hash != null) && (!hash.isEmpty())) {
				Enumeration enm = hash.keys();
				while (enm.hasMoreElements()) {
					String keyname = enm.nextElement().toString().trim();
					String templine = keyname + "="
							+ hash.get(keyname).toString().trim();
					newContent = newContent + templine + lineSeparator;
				}
			}

			fis.close();
			isr.close();
			lnr.close();

			dout = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(fileName)));

			dout.writeBytes(newContent);

			dout.close();
		} catch (FileNotFoundException fnfe) {
			return false;
		} catch (IOException ioe) {
			return false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (isr != null) {
					isr.close();
				}

				if (lnr != null) {
					lnr.close();
				}

				if (dout != null) {
					dout.close();
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public synchronized boolean setItem(Hashtable itemHash) {
		Enumeration itemKeys = itemHash.keys();
		String newContent = "";
		DataOutputStream dout = null;
		FileInputStream fis = null;

		InputStreamReader isr = null;

		BufferedReader br = null;
		try {
			fis = new FileInputStream(fileName);

			isr = new InputStreamReader(fis, "ISO-8859-1");
			br = new BufferedReader(isr);

			String line = br.readLine();

			String key = "";
			String value = "";
			while (line != null) {
				int locate = line.trim().indexOf("=");
				if (line.trim().indexOf("=") != -1) {
					if (!line.trim().startsWith("=")) {

						key = line.trim().substring(0, locate);
						if (itemHash.containsKey(key.trim())) {
							newContent = newContent
									+ key
									+ "="
									+ itemHash.get(key.trim()).toString()
											.trim() + lineSeparator;
						} else {
							newContent = newContent + line + lineSeparator;
						}

					} else {
						newContent = newContent + line + lineSeparator;
					}
				} else {
					newContent = newContent + line + lineSeparator;
				}

				line = br.readLine();
			}
			fis.close();
			isr.close();
			br.close();

			dout = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(fileName)));

			dout.writeBytes(newContent);

			dout.close();
		} catch (FileNotFoundException fnfe) {
			return false;
		} catch (IOException ioe) {
			return false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (isr != null) {
					isr.close();
				}

				if (br != null) {
					br.close();
				}

				if (dout != null) {
					dout.close();
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public boolean initInfo() {
		iniInfoHash = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(fileName);

			br = new BufferedReader(fr);

			String line = br.readLine();

			String key = "";
			String value = "";
			iniInfoHash = new Hashtable();
			// int num = 0;
			while (line != null) {
				// num ++;
				// System.out.println("line is " + line);
				int locate = line.trim().indexOf("=");
				if (line.trim().indexOf("=") != -1) {
					if (!line.trim().startsWith("=")) {
						if (line.trim().endsWith("=")) {
							key = line.trim().substring(0, locate).trim();
							value = "";
							iniInfoHash.put(key, value);
						} else {
							key = line.trim().substring(0, locate).trim();
							value = line
									.trim()
									.substring(locate + 1, line.trim().length())
									.trim();
							iniInfoHash.put(key, value);
						}
					} else {
						iniInfoHash.put(new Boolean(true), line);
					}
				} else {
					iniInfoHash.put(new Boolean(true), line);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException fnfe) {
			return false;

		} catch (IOException ioe) {
			return false;
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
				if (br != null) {
					br.close();
				}

			} catch (IOException ioe) {
				return false;
			}

		}
		return true;
	}

	public Hashtable getInfoHash(String startStr) {
		Hashtable hash = new Hashtable();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader lnr = null;
		try {

			fis = new FileInputStream(fileName);

			isr = new InputStreamReader(fis, "GBK");

			lnr = new BufferedReader(isr);
			String line = lnr.readLine();
			String key = "";
			String value = "";
			int startFlag = -1;
			int endFlag = -1;
			while (line != null) {
				String temp = line.trim();
				if (temp.startsWith(startStr)) {
					startFlag = 1;
					endFlag = 0;
				}

				if ((temp.startsWith("[")) && (temp.endsWith("]"))
						&& !temp.equalsIgnoreCase(startStr)) {
					endFlag = 1;
					startFlag = 0;
				}
				if ((startFlag == 1) && (endFlag == 0)) {
					// System.out.println("line is " + line);
					if ((line.indexOf("=") != -1)
							&& (line.lastIndexOf("=") == line.indexOf("="))) {
						StringTokenizer st = new StringTokenizer(line, "=");
						if ((!line.trim().startsWith("="))
								&& (!line.trim().endsWith("="))) {
							while (st.hasMoreTokens()) {
								key = st.nextToken().trim();
								value = st.nextToken().trim();
							}
							hash.put(key, value);
							Enumeration keys = hash.keys();
							while (keys.hasMoreElements()) {
								String serviceName = keys.nextElement()
										.toString();
								String port = hash.get(serviceName).toString();
							}
						}

					}
				}

				line = lnr.readLine();
			}
			isr.close();
			lnr.close();
			fis.close();
		} catch (Exception fe) {
			try {
				if (fis != null) {
					fis.close();
				}
				if (lnr != null) {
					lnr.close();
				}
				if (isr != null) {
					isr.close();
				}

			} catch (Exception e) {

			}

		}

		return hash;
	}

	public static void main(String[] args) throws IOException {
		IniConfigManager icm = new IniConfigManager("D:/aa.txt");
		// String temp = icm.getItem("status");
		// icm.setItem("qqq","bbb");
		// icm.addItem("ttt","fff");
		// System.out.println("temp is :" + temp);
	}
}
