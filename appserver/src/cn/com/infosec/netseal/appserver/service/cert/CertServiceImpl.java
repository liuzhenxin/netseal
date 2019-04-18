package cn.com.infosec.netseal.appserver.service.cert;

import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;

@Service
public class CertServiceImpl extends BaseService {

	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private CertDataDaoImpl certDataDao;

	private Hashtable<String, List<Cert>> ht = new Hashtable<String, List<Cert>>();

	public List<Cert> getCert(String certDn) {
		return certDao.getCert(certDn);
	}

	public List<Cert> getCert(String certDn, Integer certUsage) {
		return certDao.getCert(certDn, certUsage);
	}

	public List<Cert> getCertAll(String certDn) {
		return certDao.getCertAll(certDn);
	}

	/**
	 * 获取证书数据
	 * 
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public byte[] getCertData(String certPath, long certDataId) {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(certPath)) {
			data = FileUtil.getFile(certPath);
		} else {
			CertData certData = certDataDao.getCertData(certDataId);
			if (certData == null)
				throw new NetSealRuntimeException(ErrCode.CERT_DATA_NOT_EXIST_IN_DB, "cert data is not exist in db, cert data ID is " + certDataId);

			// 校验证书数据信息
			isModify(certData, "the data of CertData which id is " + certData.getId());

			data = certData.getData();
			FileUtil.storeFile(certPath, data);
		}
		return data;
	}

	/**
	 * 证书总数
	 * 
	 * @return
	 */
	public Integer searchTotal(String serverId) {
		return certDao.searchTotal(serverId);
	}

	/**
	 * 校验信息
	 * 
	 * @param cert
	 * @param msg
	 */
	public void isModify(Cert cert) {
		isModify(cert, "the data of Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());
	}
}
