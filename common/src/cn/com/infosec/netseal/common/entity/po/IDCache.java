package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class IDCache extends Base {
	private String tableName; // 表名
	private long lastId = 0; // 上批结束ID
	private long curId = 0; // 当前ID
	private long endId = 0; // 本批最大ID
	private int batchId = 1000; // 批数量

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public long getLastId() {
		return lastId;
	}

	public void setLastId(long lastId) {
		this.lastId = lastId;
	}

	public long getCurId() {
		return curId;
	}

	public void setCurId(long curId) {
		this.curId = curId;
	}

	public long getEndId() {
		return endId;
	}

	public void setEndId(long endId) {
		this.endId = endId;
	}

	public int getBatchId() {
		return batchId;
	}

	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	
	public String calMac() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(tableName));
			baos.write(calMac(lastId));
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		
		return CryptoHandler.hashEnc64(baos.toByteArray());
		
	}

}
