package cn.com.infosec.netseal.webserver.util;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

	// public static final String getServerName() {
	// String serverName = "UnkownServerName";
	// try {
	// InetAddress address = InetAddress.getLocalHost();
	// serverName = address.getHostAddress();
	// } catch (UnknownHostException e) {
	// e.printStackTrace();
	// return serverName;
	// }
	// return serverName;
	// }
	//
	// public static final String getServerName(HttpServletRequest request) {
	// String serverName = request.getServerName();
	// if (("127.0.0.1".equals(serverName)) || ("localhost".equals(serverName))) {
	// try {
	// InetAddress address = InetAddress.getLocalHost();
	// serverName = address.getHostAddress();
	// } catch (UnknownHostException e) {
	// e.printStackTrace();
	// return serverName;
	// }
	// }
	// return serverName;
	// }

	public static String getClientHost(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getRemoteAddr();
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		return ip;
	}

	// public static final String getAbsoluteUrl(HttpServletRequest request,String servletPath) {
	// String serverName = getServerName(request);
	// String path = request.getContextPath() + servletPath;
	// StringBuffer urlBuff = new StringBuffer(request.getScheme());
	// urlBuff.append("://");
	// String port = ":" + String.valueOf(request.getServerPort());
	// urlBuff.append(serverName).append(port).append(path);
	// return urlBuff.toString();
	// }
}