package cn.com.infosec.netseal.common.util;

import java.net.NetworkInterface;
import java.util.Enumeration;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class NetWorkUtil {

	public static String getHostMac(String networkCard) {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) en.nextElement();
				if (ni.getName().equalsIgnoreCase(networkCard) && ni.getHardwareAddress()!=null)
					return HexUtil.byte2Hex(ni.getHardwareAddress());
			}
			throw new NetSealRuntimeException(ErrCode.NETWORK_CARD_GET_MAC_NOT_FOUND, "network card " + networkCard + " not found");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetSealRuntimeException(ErrCode.NETWORK_CARD_GET_MAC_ERROR, "get network card mac error, " + e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		 //System.out.println(getHostMac("10.20.61.47"));
		 System.out.println(getHostMac("192.168.1.101"));
		//System.out.println(getHostMac("eth0"));
	}
}
