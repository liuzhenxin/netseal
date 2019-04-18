package cn.com.infosec.netseal.common.entity.vo.jmx;

public interface JmxScalarVOMBean {

	/**
	 * 打开的线程数量
	 * 
	 * @return
	 */
	public int getSocketNum();

	/**
	 * 交易数量
	 * 
	 * @return
	 */
	public long getDealTotalNum();

	/**
	 * 成功交易数量
	 * 
	 * @return
	 */
	public long getDealSuccNum();

	/**
	 * 失败交易数量
	 * 
	 * @return
	 */
	public long getDealFailNum();
}
