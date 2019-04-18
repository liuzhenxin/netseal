package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.StringUtil;

public class Base {

	// 继承属性
	private Long id;
	private Long generateTime;
	private Long updateTime;
	private String mac;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(Long generateTime) {
		this.generateTime = generateTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String calMac() throws Exception {
		return mac;
	}

	public byte[] calMac(Object obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (obj == null)
			baos.write(27);

		try {
			if (obj instanceof String) {
				String str = (String) obj;
				if (StringUtil.isBlank(str))
					baos.write(27);
				else
					baos.write(StringUtil.getBytes(str));
			}

			if (obj instanceof Long) {
				Long l = (Long) obj;
				baos.write(StringUtil.getBytes(l.toString()));
			}

			if (obj instanceof Integer) {
				Integer i = (Integer) obj;
				baos.write(StringUtil.getBytes(i.toString()));
			}

			if (obj instanceof byte[]) {
				byte[] bs = (byte[]) obj;
				baos.write(bs);
			}
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}

		return baos.toByteArray();
	}

}
