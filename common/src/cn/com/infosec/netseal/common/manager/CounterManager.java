package cn.com.infosec.netseal.common.manager;

import java.math.BigInteger;
import java.util.HashMap;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 维护系统中各种计数器
 * 
 * @author zhaojz
 *
 */
public class CounterManager {

	private static HashMap<String, BigInteger> ht = new HashMap<String, BigInteger>();

	static {
		ht.put(Constants.SOCKET_NUM, BigInteger.ZERO);
		ht.put(Constants.DEAL_S_NUM, BigInteger.ZERO);
		ht.put(Constants.DEAL_F_NUM, BigInteger.ZERO);
	}

	/**
	 * 设置key值
	 * 
	 * @param key
	 * @param value
	 */
	public static synchronized void setKeyValue(String key, BigInteger value) {
		ht.put(key, value);
	}

	/**
	 * 读取key值
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized BigInteger getKeyValue(String key) {
		return ht.get(key);
	}

	/**
	 * 读取key值 并且清零
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized BigInteger getKeyValueAndClear(String key) {
		BigInteger value = ht.get(key);
		ht.put(key, BigInteger.ZERO);
		return value;
	}

	/**
	 * 数量减一
	 * 
	 * @param key
	 */
	public static synchronized void decrement(String key) {
		BigInteger value = ht.get(key);
		if (value == null)
			throw new NetSealRuntimeException(ErrCode.METHOD_PARAM_VALUE_INVALID, "param value for method is invaild");

		if (value.intValue() > 0) {
			value = value.subtract(BigInteger.ONE);
			ht.put(key, value);
		}
	}

	/**
	 * 数量加一
	 * 
	 * @param key
	 * @param num
	 * @return
	 */
	public static synchronized void increment(String key) {
		BigInteger value = ht.get(key);
		if (value == null)
			throw new NetSealRuntimeException(ErrCode.METHOD_PARAM_VALUE_INVALID, "param value for method is invaild");

		value = value.add(BigInteger.ONE);
		ht.put(key, value);
	}

	/**
	 * 有权限，数量加一
	 * 
	 * @param key
	 * @param num
	 * @return
	 */
	public static synchronized boolean isContinue(String key, int num) {
		BigInteger value = ht.get(key);
		if (value == null)
			throw new NetSealRuntimeException(ErrCode.METHOD_PARAM_VALUE_INVALID, "param value for method is invaild");

		if (value.intValue() < num) {
			value = value.add(BigInteger.ONE);
			ht.put(key, value);
			return true;
		} else
			return false;
	}

}
