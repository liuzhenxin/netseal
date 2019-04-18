package cn.com.infosec.netseal.appapi.common.communication.message.impl.envelopes;

import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;

import cn.com.infosec.netseal.appapi.common.communication.message.Request;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.bytes.ByteRequest;
import cn.com.infosec.netseal.appapi.common.util.NetSignUtil;

public class EnvelopeRequest extends Request {

	private X509Certificate serverCert;
	private ByteRequest bReq;

	public EnvelopeRequest() {
	}

	/**
	 * 组成数字信封
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] envelopeRequest() throws Exception {
		byte[] plain = bReq.getBytes();
		byte[] envelopeData = NetSignUtil.composeEnvelopeData(plain, serverCert);
		return envelopeData;
	}

	/**
	 * 客户端使用
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] getBytes() throws Exception {
		return envelopeRequest();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" type:" + getType());
		Properties pro = getData();
		Enumeration keys = pro.keys();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			String value = pro.getProperty(name);
			sb.append(" " + name + ":" + value);
		}

		return sb.toString();
	}

	public X509Certificate getServerCert() {
		return serverCert;
	}

	public void setServerCert(X509Certificate serverCert) {
		this.serverCert = serverCert;
	}

	public ByteRequest getbReq() {
		return bReq;
	}

	public void setbReq(ByteRequest bReq) {
		this.bReq = bReq;
	}

}
