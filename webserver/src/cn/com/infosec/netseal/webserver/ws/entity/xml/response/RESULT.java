package cn.com.infosec.netseal.webserver.ws.entity.xml.response;

public class RESULT {
	private String RET_CODE;
	private String RET_MSG;
	private String PDF_STAMPED;

	public String getRET_CODE() {
		return RET_CODE;
	}

	public void setRET_CODE(String rET_CODE) {
		RET_CODE = rET_CODE;
	}

	public String getRET_MSG() {
		return RET_MSG;
	}

	public void setRET_MSG(String rET_MSG) {
		RET_MSG = rET_MSG;
	}

	public String getPDF_STAMPED() {
		return PDF_STAMPED;
	}

	public void setPDF_STAMPED(String pDF_STAMPED) {
		PDF_STAMPED = pDF_STAMPED;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(RET_CODE).append("|").append(RET_MSG).append("|").append(PDF_STAMPED);
		return sb.toString();
	}

}
