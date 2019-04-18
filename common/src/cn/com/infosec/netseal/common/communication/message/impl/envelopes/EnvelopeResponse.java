package cn.com.infosec.netseal.common.communication.message.impl.envelopes;

import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.communication.message.impl.bytes.ByteResponse;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.NetSignUtil;

@Component
public class EnvelopeResponse extends Response {
	private ByteResponse bRes;

	public EnvelopeResponse() {
	}

	/**
	 * 组成数字信封
	 * 
	 * @param certDn
	 *            签章人证书
	 * @param plain
	 * @return
	 * @throws Exception
	 */
	public byte[] envelopeResponse(String certDn, byte[] plain, CertDaoImpl certDao, CertDataDaoImpl certDataDao) throws Exception {
		ByteResponse bRes = new ByteResponse(plain);
		plain = bRes.getBytes();

		List<Cert> certs = certDao.getCert(certDn);
		if (certs.size() == 0)
			throw new Exception("user cert 4 envelope is null, user cert dn is " + certDn);

		if (certs.size() > 1)
			throw new Exception("the number of user cert 4 envelope is more than one, user cert dn is " + certDn);
		Cert cert = certs.get(0);

		byte[] data = null;
		if (!FileUtil.checkPath(cert.getCertPath())) {
			CertData certData = certDataDao.getCertData(cert.getCertDataId());
			if (certData == null)
				throw new NetSealRuntimeException(ErrCode.CERT_DATA_NOT_EXIST_IN_DB, "cert data not exist in db, cert id is " + cert.getId());

			data = certData.getData();
			FileUtil.storeFile(cert.getCertPath(), data);
		} else
			data = FileUtil.getFile(cert.getCertPath());

		X509Certificate x509Cert = CertUtil.parseCert(data).getX509Cert();
		plain = NetSignUtil.composeEnvelopeData(plain, x509Cert);

		return plain;
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
