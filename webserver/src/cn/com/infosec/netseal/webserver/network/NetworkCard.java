package cn.com.infosec.netseal.webserver.network;

import java.io.IOException;

/**
 * 网卡信息
 */
public class NetworkCard {
	private String name;
	private String ip;
	private String mask;
	private String bcast;
	private String gateway;
	private int status;
	private String statusCn;
	
	private String ipv6;
	private String ipv6Geteway;

	public NetworkCard() {

	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getGateway() {
		if(gateway!=null)
			gateway=gateway.trim();
		return gateway;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if(name!=null)
			name=name.trim();
		return name;
	}

	public String getIp() {
		if(ip!=null)
			ip=ip.trim();
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMask(String mask) {		
		this.mask = mask;
	}

	public String getMask() {
		if(mask!=null)
			mask=mask.trim();
		return mask;
	}

	public void setBcast(String bcast) {
		this.bcast = bcast;
	}

	public String getBcast() {
		return this.bcast;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

	public String getStatusCn() {
		if(status==1){
			statusCn="是";
		}else if(status==0){
			statusCn="否";
		}else{
			statusCn="-";
		}
		return statusCn;
	}

	public void setStatusCn(String statusCn) {
		this.statusCn = statusCn;
	}
	
	public String getIpv6() {
		if(ipv6 != null)
			ipv6 = ipv6.trim();
		return ipv6;
	}

	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	public String getIpv6Geteway() {
		if(ipv6Geteway != null)
			ipv6Geteway = ipv6Geteway.trim();
		return ipv6Geteway;
	}

	public void setIpv6Geteway(String ipv6Geteway) {
		this.ipv6Geteway = ipv6Geteway;
	}

	public static void main(String[] args) throws IOException {
		// Vector v = new Vector();
		// Network n = new Network();
		// v = (Vector)n.getInfoFromBuffer();
		// if ((v != null )&&(!v.isEmpty()))
		// {
		// for (int i=0;i<v.size();i++)
		// {
		// Network m = new Network();
		// m = (Network)v.elementAt(i);
		// System.out.println("name is :" + m.getName());
		// System.out.println("ip is :" + m.getIP());
		// System.out.println("mark is :" + m.getMark());
		// System.out.println("gateway is :" + m.getGateway());
		// System.out.println("status is :" + m.getStatus());
		// }
		//
		// }
	}
}
