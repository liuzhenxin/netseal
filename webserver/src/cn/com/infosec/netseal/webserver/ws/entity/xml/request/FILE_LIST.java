package cn.com.infosec.netseal.webserver.ws.entity.xml.request;

public class FILE_LIST {
	private TREE_NODE TREE_NODE;

	public TREE_NODE getTREE_NODE() {
		return TREE_NODE;
	}

	public void setTREE_NODE(TREE_NODE tREE_NODE) {
		TREE_NODE = tREE_NODE;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<TREE_NODE>").append(TREE_NODE).append("</TREE_NODE>");
		return sb.toString();
	}

}
