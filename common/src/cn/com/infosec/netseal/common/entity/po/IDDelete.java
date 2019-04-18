package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class IDDelete extends Base {

	private String tableName; // 表名
	private Long deleteId;// 删除的id

	public Long getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(tableName));
			baos.write(calMac(deleteId));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
			
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}
	
	public String  toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:");
		sb.append(getId());
		sb.append(" tableName:");
		sb.append(tableName);
		sb.append(" deleteId:");
		sb.append(deleteId);
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:");
		sb.append(getMac());
		
		return sb.toString();
	}
	
}
