package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.util.StringUtil;

public class CsrDataVO extends BaseVO {

	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getMac() {
		return "";
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append(" ");
		sb.append(StringUtil.base64Encode(data));
		sb.append(" ");
		sb.append(getGenerateTime());
		return sb.toString();
	}
}
