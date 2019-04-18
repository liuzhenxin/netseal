package cn.com.infosec.netseal.common.entity.vo.jmx;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.manager.CounterManager;

public class JmxScalarVO implements JmxScalarVOMBean {

	private long dealSuccNum;
	private long dealFailNum;
	private long dealTotalNum;

	/**
	 * 打开的线程数量
	 * 
	 * @return
	 */
	public int getSocketNum() {
		return CounterManager.getKeyValue(Constants.SOCKET_NUM).intValue();
	}

	/**
	 * 成功交易数量
	 * 
	 * @return
	 */
	public long getDealSuccNum() {
		return dealSuccNum;
	}

	public void setDealSuccNum(long dealSuccNum) {
		this.dealSuccNum = dealSuccNum;
	}

	/**
	 * 失败交易数量
	 * 
	 * @return
	 */
	public long getDealFailNum() {
		return dealFailNum;
	}

	public void setDealFailNum(long dealFailNum) {
		this.dealFailNum = dealFailNum;
	}

	/**
	 * 总交易数量
	 * 
	 * @return
	 */
	public long getDealTotalNum() {
		return dealTotalNum;
	}

	public void setDealTotalNum(long dealTotalNum) {
		this.dealTotalNum = dealTotalNum;
	}

}
