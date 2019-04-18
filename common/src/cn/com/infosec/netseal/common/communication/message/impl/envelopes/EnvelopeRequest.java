package cn.com.infosec.netseal.common.communication.message.impl.envelopes;

import java.security.PrivateKey;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.impl.bytes.ByteRequest;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.keyData.KeyDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.KeyData;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.KeyStoreUtil;
import cn.com.infosec.netseal.common.util.NetSignUtil;
import cn.com.infosec.netseal.common.util.p7.PKCS7EnvelopedData;

@Component
public class EnvelopeRequest extends Request {
	private byte[] serverCert;
	private ByteRequest bReq;

	public EnvelopeRequest() {
	}

	/**
	 * 解数字信封
	 * 
	 * @param envelopeData
	 * @return
	 * @throws Exception
	 */
	public ByteRequest unenvelopeRequest(byte[] envelopeData, KeyDaoImpl keyDao, KeyDataDaoImpl keyDataDao) throws Exception {
		String certSn = PKCS7EnvelopedData.getCertSn(envelopeData);

		long envId = ConfigUtil.getInstance().getEncrpKeyId();
		Key key = keyDao.getKey(envId);
		if (key == null)
			throw new Exception("key is null from db, encrypt id is " + envId);

		if (!key.getCertSn().equals(certSn))
			throw new Exception("client cert sn not match, client cert sn is " + certSn + " server cert sn is " + key.getCertSn());

		byte[] data = null;
		if (!FileUtil.checkPath(key.getKeyPath())) {
			KeyData keyData = keyDataDao.getKeyData(key.getKeyDataId());
			if (keyData == null)
				throw new Exception("key data not exist in db, key id is " + key.getId());

			data = keyData.getData();
			FileUtil.storeFile(key.getKeyPath(), data);
		} else
			data = FileUtil.getFile(key.getKeyPath());

		PrivateKey priK = KeyStoreUtil.loadKey(key.getKeyPwdPlain(), key.getKeyMode(), data);
		byte[] plain = NetSignUtil.decomposeEnvelopeData(envelopeData, null, priK);

		bReq = new ByteRequest(plain);
		setType(bReq.getType());
		setData(bReq.getData());

		return bReq;
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

	public byte[] getEnvelopeCert() {
		return serverCert;
	}

	public void setEnvelopeCert(byte[] envelopeCert) {
		this.serverCert = envelopeCert;
	}

	public ByteRequest getbReq() {
		return bReq;
	}

	public void setbReq(ByteRequest bReq) {
		this.bReq = bReq;
	}

}
