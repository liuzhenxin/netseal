package cn.com.infosec.netseal.webserver.network.ha;

import java.util.ArrayList;

public class HAInfo {

	private String mainNodeDeviceName = "";//主机节点名称
	private String vmIP = "";
	private int netMaskValue = 24;
	private String IPDevice = "";
	private String HADevice = "";
	private String netmask = "";
	private ArrayList nodeList = null;
	private String logFile = "";
	private String debugFile = "";
	private String pingIp = "";
	private String baudRate = "";
	private String serialportname = "";
	private String slaveNodeName = "";

	public HAInfo() {

	}

	public HAInfo(String mainNodeDeviceName, String vmIP, ArrayList nodeList, String logFile) {
		this.mainNodeDeviceName = mainNodeDeviceName;
		this.vmIP = vmIP;
		this.nodeList = (ArrayList) nodeList.clone();
		this.logFile = logFile;
	}

	public HAInfo(String mainNodeDeviceName, String vmIP, ArrayList nodeList, String logFile, String pingIp) {
		this(mainNodeDeviceName, vmIP, nodeList, logFile);
		this.pingIp = pingIp;
	}

	public String getMainNodeDeviceName() {
		return this.mainNodeDeviceName;
	}

	public void setMainNodeDeviceName(String mainNodeDeviceName) {
		this.mainNodeDeviceName = mainNodeDeviceName;
	}

	public String getVmIP() {
		return this.vmIP;
	}

	public void setVmIP(String vmIP) {
		this.vmIP = vmIP;
	}

	public int getNetMaskValue() {
		return this.netMaskValue;
	}

	public void setNetMaskValue(int netMaskValue) {
		this.netMaskValue = netMaskValue;
	}

	public String getIPDevice() {
		return this.IPDevice;
	}

	public void setIPDevice(String IPDevice) {
		this.IPDevice = IPDevice;
	}

	public String getHADevice() {
		return this.HADevice;
	}

	public void setHADevice(String HADevice) {
		this.HADevice = HADevice;
	}

	public ArrayList getNodeList() {
		return this.nodeList;
	}

	public void setNodeList(ArrayList nodeList) {
		this.nodeList = (ArrayList) nodeList.clone();
	}

	public String getLogFile() {
		return this.logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getDebugFile() {
		return debugFile;
	}

	public void setDebugFile(String debugFile) {
		this.debugFile = debugFile;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根

	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getPingIp() {
		return pingIp;
	}

	public void setPingIp(String pingIp) {
		this.pingIp = pingIp;
	}

	public String getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(String baudRate) {
		this.baudRate = baudRate;
	}

	public String getSerialportname() {
		return serialportname;
	}

	public void setSerialportname(String serialportname) {
		this.serialportname = serialportname;
	}

	public String getSlaveNodeName() {
		return slaveNodeName;
	}

	public void setSlaveNodeName(String slaveNodeName) {
		this.slaveNodeName = slaveNodeName;
	}

}
