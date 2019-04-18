package cn.com.infosec.netseal.webserver.ws.entity.xml.request;

public class BASE_DATA {
	private String SYS_ID;
	private String USER_ID;
	private String USER_PSD;

	public String getSYS_ID() {
		return SYS_ID;
	}

	public void setSYS_ID(String sYS_ID) {
		SYS_ID = sYS_ID;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getUSER_PSD() {
		return USER_PSD;
	}

	public void setUSER_PSD(String uSER_PSD) {
		USER_PSD = uSER_PSD;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<SYS_ID>").append(SYS_ID).append("</SYS_ID>");
		sb.append("<USER_ID>").append(USER_ID).append("</USER_ID>");
		sb.append("<USER_PSD>").append(USER_PSD).append("</USER_PSD>");
		return sb.toString();
	}

}
