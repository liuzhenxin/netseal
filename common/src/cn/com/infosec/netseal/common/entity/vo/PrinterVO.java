package cn.com.infosec.netseal.common.entity.vo;

/**
 * 打印控制
 */
public class PrinterVO extends BaseVO {

	private String name;
	private String sealName;
	private Integer sealType;
	private String userName;
	private Integer printNum;
	private Integer printedNum;
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSealName() {
		return sealName;
	}

	public void setSealName(String sealName) {
		this.sealName = sealName;
	}

	public Integer getSealType() {
		return sealType;
	}

	public void setSealType(Integer sealType) {
		this.sealType = sealType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public Integer getPrintedNum() {
		return printedNum;
	}

	public void setPrintedNum(Integer printedNum) {
		this.printedNum = printedNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
