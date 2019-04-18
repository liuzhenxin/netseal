package cn.com.infosec.netseal.webserver.ws.entity.xml.request;

import javax.xml.bind.annotation.XmlElement;

public class TREE_NODE {
	private String FILE_NO;
	private String IS_CODEBAR;
	private String CODEBAR_TYPE;

	private String CODEBAR_DATA;
	private String X_COORDINATE;
	private String Y_COORDINATE;
	
	private String IS_PICTURE;
	private String PICTURE_DATA;
	private String PICTURE_X;
	private String PICTURE_Y;

	private String RULE_TYPE;
	private String RULE_NO;
	private String RULE_INFO;

	private String CERT_NAME;
	private String SEAL_NAME;
	private String CJ_TYPE;

	private String MODEL_NAME;
	private String REQUEST_TYPE;
	private String FILE_PATH;
	private String DOC_TYPE;

	private String FTP_ADDRESS;
	private String FTP_PORT;
	private String FTP_USER;
	private String FTP_PWD;
	private String SAVE_PATH;
	private String AREA_SEAL;

	private String AREA_DATA;
	private String APP_DATA;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FILE_NO>").append(FILE_NO).append("</FILE_NO>");
		sb.append("<IS_CODEBAR>").append(IS_CODEBAR).append("</IS_CODEBAR>");
		sb.append("<CODEBAR_TYPE>").append(CODEBAR_TYPE).append("</CODEBAR_TYPE>");

		sb.append("<CODEBAR_DATA>").append(CODEBAR_DATA).append("</CODEBAR_DATA>");
		sb.append("<X_COORDINATE>").append(X_COORDINATE).append("</X_COORDINATE>");
		sb.append("<Y_COORDINATE>").append(Y_COORDINATE).append("</Y_COORDINATE>");

		sb.append("<IS_PICTURE>").append(IS_PICTURE).append("</IS_PICTURE>");
		sb.append("<PICTURE_DATA>").append(PICTURE_DATA).append("</PICTURE_DATA>");
		sb.append("<PICTURE_X>").append(PICTURE_X).append("</PICTURE_X>");
		sb.append("<PICTURE_Y>").append(PICTURE_Y).append("</PICTURE_Y>");

		sb.append("<RULE_TYPE>").append(RULE_TYPE).append("</RULE_TYPE>");
		sb.append("<RULE_NO>").append(RULE_NO).append("</RULE_NO>");
		sb.append("<RULE_INFO>").append(RULE_INFO).append("</RULE_INFO>");

		sb.append("<CERT_NAME>").append(CERT_NAME).append("</CERT_NAME>");
		sb.append("<SEAL_NAME>").append(SEAL_NAME).append("</SEAL_NAME>");
		sb.append("<CJ_TYPE>").append(CJ_TYPE).append("</CJ_TYPE>");

		sb.append("<MODEL_NAME>").append(MODEL_NAME).append("</MODEL_NAME>");
		sb.append("<REQUEST_TYPE>").append(REQUEST_TYPE).append("</REQUEST_TYPE>");
		sb.append("<FILE_PATH>").append(FILE_PATH).append("</FILE_PATH>");
		sb.append("<DOC_TYPE>").append(DOC_TYPE).append("</DOC_TYPE>");

		sb.append("<FTP_ADDRESS>").append(FTP_ADDRESS).append("</FTP_ADDRESS>");
		sb.append("<FTP_PORT>").append(FTP_PORT).append("</FTP_PORT>");
		sb.append("<FTP_USER>").append(FTP_USER).append("</FTP_USER>");
		sb.append("<FTP_PWD>").append(FTP_PWD).append("</FTP_PWD>");
		sb.append("<SAVE_PATH>").append(SAVE_PATH).append("</SAVE_PATH>");
		sb.append("<AREA_SEAL>").append(AREA_SEAL).append("</AREA_SEAL>");

		sb.append("<AREA_DATA>").append(AREA_DATA).append("</AREA_DATA>");
		sb.append("<APP_DATA>").append(APP_DATA).append("</APP_DATA>");
		return sb.toString();
	}

	public String getFILE_NO() {
		return FILE_NO;
	}

	public void setFILE_NO(String fILE_NO) {
		FILE_NO = fILE_NO;
	}

	public String getIS_CODEBAR() {
		return IS_CODEBAR;
	}

	public void setIS_CODEBAR(String iS_CODEBAR) {
		IS_CODEBAR = iS_CODEBAR;
	}

	public String getCODEBAR_TYPE() {
		return CODEBAR_TYPE;
	}

	public void setCODEBAR_TYPE(String cODEBAR_TYPE) {
		CODEBAR_TYPE = cODEBAR_TYPE;
	}

	public String getCODEBAR_DATA() {
		return CODEBAR_DATA;
	}

	public void setCODEBAR_DATA(String cODEBAR_DATA) {
		CODEBAR_DATA = cODEBAR_DATA;
	}

	@XmlElement(name = "X_COORDINATE")
	public String getX_COORDINATE() {
		return X_COORDINATE;
	}

	public void setX_COORDINATE(String x_COORDINATE) {
		X_COORDINATE = x_COORDINATE;
	}

	@XmlElement(name = "Y_COORDINATE")
	public String getY_COORDINATE() {
		return Y_COORDINATE;
	}

	public void setY_COORDINATE(String y_COORDINATE) {
		Y_COORDINATE = y_COORDINATE;
	}

	public String getIS_PICTURE() {
		return IS_PICTURE;
	}

	public void setIS_PICTURE(String iS_PICTURE) {
		IS_PICTURE = iS_PICTURE;
	}

	public String getPICTURE_DATA() {
		return PICTURE_DATA;
	}

	public void setPICTURE_DATA(String pICTURE_DATA) {
		PICTURE_DATA = pICTURE_DATA;
	}

	public String getPICTURE_X() {
		return PICTURE_X;
	}

	public void setPICTURE_X(String pICTURE_X) {
		PICTURE_X = pICTURE_X;
	}

	public String getPICTURE_Y() {
		return PICTURE_Y;
	}

	public void setPICTURE_Y(String pICTURE_Y) {
		PICTURE_Y = pICTURE_Y;
	}

	public String getRULE_TYPE() {
		return RULE_TYPE;
	}

	public void setRULE_TYPE(String rULE_TYPE) {
		RULE_TYPE = rULE_TYPE;
	}

	public String getRULE_NO() {
		return RULE_NO;
	}

	public void setRULE_NO(String rULE_NO) {
		RULE_NO = rULE_NO;
	}

	public String getRULE_INFO() {
		return RULE_INFO;
	}

	public void setRULE_INFO(String rULE_INFO) {
		RULE_INFO = rULE_INFO;
	}

	public String getCERT_NAME() {
		return CERT_NAME;
	}

	public void setCERT_NAME(String cERT_NAME) {
		CERT_NAME = cERT_NAME;
	}

	public String getSEAL_NAME() {
		return SEAL_NAME;
	}

	public void setSEAL_NAME(String sEAL_NAME) {
		SEAL_NAME = sEAL_NAME;
	}

	public String getCJ_TYPE() {
		return CJ_TYPE;
	}

	public void setCJ_TYPE(String cJ_TYPE) {
		CJ_TYPE = cJ_TYPE;
	}

	public String getMODEL_NAME() {
		return MODEL_NAME;
	}

	public void setMODEL_NAME(String mODEL_NAME) {
		MODEL_NAME = mODEL_NAME;
	}

	public String getREQUEST_TYPE() {
		return REQUEST_TYPE;
	}

	public void setREQUEST_TYPE(String rEQUEST_TYPE) {
		REQUEST_TYPE = rEQUEST_TYPE;
	}

	public String getFILE_PATH() {
		return FILE_PATH;
	}

	public void setFILE_PATH(String fILE_PATH) {
		FILE_PATH = fILE_PATH;
	}

	public String getDOC_TYPE() {
		return DOC_TYPE;
	}

	public void setDOC_TYPE(String dOC_TYPE) {
		DOC_TYPE = dOC_TYPE;
	}

	public String getFTP_ADDRESS() {
		return FTP_ADDRESS;
	}

	public void setFTP_ADDRESS(String fTP_ADDRESS) {
		FTP_ADDRESS = fTP_ADDRESS;
	}

	public String getFTP_PORT() {
		return FTP_PORT;
	}

	public void setFTP_PORT(String fTP_PORT) {
		FTP_PORT = fTP_PORT;
	}

	public String getFTP_USER() {
		return FTP_USER;
	}

	public void setFTP_USER(String fTP_USER) {
		FTP_USER = fTP_USER;
	}

	public String getFTP_PWD() {
		return FTP_PWD;
	}

	public void setFTP_PWD(String fTP_PWD) {
		FTP_PWD = fTP_PWD;
	}


	public String getSAVE_PATH() {
		return SAVE_PATH;
	}

	public void setSAVE_PATH(String sAVE_PATH) {
		SAVE_PATH = sAVE_PATH;
	}

	public String getAREA_SEAL() {
		return AREA_SEAL;
	}

	public void setAREA_SEAL(String aREA_SEAL) {
		AREA_SEAL = aREA_SEAL;
	}

	public String getAREA_DATA() {
		return AREA_DATA;
	}

	public void setAREA_DATA(String aREA_DATA) {
		AREA_DATA = aREA_DATA;
	}

	public String getAPP_DATA() {
		return APP_DATA;
	}

	public void setAPP_DATA(String aPP_DATA) {
		APP_DATA = aPP_DATA;
	}

	
}
