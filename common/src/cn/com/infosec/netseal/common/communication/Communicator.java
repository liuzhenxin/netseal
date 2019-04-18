package cn.com.infosec.netseal.common.communication;

import cn.com.infosec.netseal.common.define.Constants;

/**
 * <p>
 * Title: 通讯器接口
 * </p>
 * <p>
 * Description: 封装系统各部件之间的通信
 * </p>
 */

public interface Communicator {

	long maxRequestLength = Constants.LENGTH_5MB;
	long maxResponseLength = Constants.LENGTH_5MB;

	int READ_BLOCK_LENGTH = 1024;
	int DATALENGTH_BLOCK_LENGTH = 16;

	/**
	 * 向网络中发送数据
	 */
	void send(byte[] data) throws Exception;

	/**
	 * 从网络中获取数据长度
	 * 
	 * @param length
	 * @return
	 * @throws Exception
	 */
	int recvLength() throws Exception;

	/**
	 * 从网络接收一段数据
	 */
	byte[] recv() throws Exception;

	/**
	 * 关闭连接
	 */
	void close();

}
