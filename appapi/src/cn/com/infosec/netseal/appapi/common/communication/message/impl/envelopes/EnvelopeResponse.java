package cn.com.infosec.netseal.appapi.common.communication.message.impl.envelopes;

import java.security.PrivateKey;
import java.util.Enumeration;
import java.util.Properties;

import cn.com.infosec.netseal.appapi.common.communication.message.Response;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.bytes.ByteResponse;
import cn.com.infosec.netseal.appapi.common.util.NetSignUtil;

public class EnvelopeResponse extends Response {

	private ByteResponse bRes;

	public EnvelopeResponse() {
	}

	/**
	 * 解数字信封
	 * 
	 * @param envelopeData
	 * @param hsmId
	 * @return
	 * @throws Exception
	 */
	public ByteResponse unenvelopeResponse(byte[] envelopeData, PrivateKey priKey) throws Exception {
		envelopeData = NetSignUtil.decomposeEnvelopeData(envelopeData, null, priKey);
		bRes = new ByteResponse(envelopeData);
		setErrCode(bRes.getErrCode());
		setErrMsg(bRes.getErrMsg());
		setData(bRes.getData());

		return bRes;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" errCode:" + getErrCode());
		sb.append(" errMsg:" + getErrMsg());

		Properties pro = getData();
		if (pro != null) {
			Enumeration keys = pro.keys();
			while (keys.hasMoreElements()) {
				String name = (String) keys.nextElement();
				String value = pro.getProperty(name);
				sb.append(" " + name + ":" + value);
			}
		}

		return sb.toString();
	}

	public ByteResponse getbRes() {
		return bRes;
	}

	public void setbRes(ByteResponse bRes) {
		this.bRes = bRes;
	}

}
