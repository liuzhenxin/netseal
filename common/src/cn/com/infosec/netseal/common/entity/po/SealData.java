package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.StringUtil;

public class SealData extends Base {

	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String calMac()  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(data));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (IOException e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(getId());
		sb.append(" data:");
		sb.append(StringUtil.base64Encode(data));
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:").append(getMac());
		return sb.toString();
	}
}
