package cn.com.infosec.netseal.common.entity.vo;

public class AccessLogCountVO extends BaseVO{
	private String optype;
	private Integer count;
	
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
}
