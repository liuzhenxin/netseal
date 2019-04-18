package cn.com.infosec.netseal.common.entity.vo;

public class ScalarVO {

	private String value;
	private boolean isRed;
	private boolean isBgGray;
	private boolean isBold;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isRed() {
		return isRed;
	}

	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}

	public boolean isBgGray() {
		return isBgGray;
	}

	public void setBgGray(boolean isBgGray) {
		this.isBgGray = isBgGray;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}

}
