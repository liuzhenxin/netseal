package cn.com.infosec.netseal.appserver.service.key;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.csrData.CsrDataDaoImpl;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.keyData.KeyDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.CsrData;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.KeyData;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Service
public class KeyServiceImpl extends BaseService {

	@Autowired
	private KeyDaoImpl keyDao;
	@Autowired
	private KeyDataDaoImpl keyDataDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private CsrDataDaoImpl csrDataDao;

	public Key getKey(long ID) {
		return keyDao.getKey(ID);
	}

	public List<Key> getKeys(String certDn) {
		Key key = new Key();
		key.setCertDn(certDn);
		key.setCertUsage(Constants.USAGE_SIGNATURE);
		return keyDao.getkeylList(key);
	}

	/**
	 * 获取加密卡密钥
	 * 
	 * @return
	 */
	public List<Key> getCardKeys() {
		Key key = new Key();
		key.setKeyMode(Constants.KEY_SUFFIX);
		key.setCertUsage(Constants.USAGE_SIGNATURE);
		List<Key> signCardKeyList = keyDao.getkeylList(key);

		// 检验数据完整性
		for (Key ks : signCardKeyList)
			isModify(ks);

		return signCardKeyList;
	}

	/**
	 * 本地不存在,从数据库读取,存储到本地
	 * 
	 * @param key
	 */
	public byte[] getKeyData(Key key) {
		if (key == null)
			throw new NetSealRuntimeException(ErrCode.METHOD_PARAM_VALUE_INVALID, "sync key data, params is null");

		// 密钥数据
		if (!FileUtil.checkPath(key.getKeyPath())) {
			KeyData keyData = keyDataDao.getKeyData(key.getKeyDataId());
			if (keyData == null)
				throw new NetSealRuntimeException(ErrCode.CERT_DATA_NOT_EXIST_IN_DB, "key data not exist in db, key id is " + key.getId());

			// 校验信息
			isModify(keyData, "the data of KeyData which id is " + keyData.getId());

			byte[] data = keyData.getData();
			FileUtil.storeFile(key.getKeyPath(), data);
		}

		// 证书数据
		if (StringUtil.isNotBlank(key.getCertPath())) {
			if (!FileUtil.checkPath(key.getCertPath())) {
				CertData certData = certDataDao.getCertData(key.getCertDataId());
				if (certData == null)
					throw new NetSealRuntimeException(ErrCode.CERT_DATA_NOT_EXIST_IN_DB, "cert data not exist in db, key id is " + key.getId());

				// 校验信息
				isModify(certData, "the data of CertData which id is " + certData.getId());

				byte[] data = certData.getData();
				FileUtil.storeFile(key.getCertPath(), data);
			}
		}

		// 证书请求数据
		if (StringUtil.isNotBlank(key.getCsrPath())) {
			if (!FileUtil.checkPath(key.getCsrPath())) {
				CsrData csrData = csrDataDao.getCsrData(key.getCsrDataId());
				if (csrData == null)
					throw new NetSealRuntimeException(ErrCode.CSR_DATA_NOT_EXIST_IN_DB, "csr data not exist in db, the csrdata id is " + key.getCsrDataId());

				// 校验信息
				isModify(csrData, "the data of CsrData which id is " + csrData.getId());

				byte[] data = csrData.getData();
				FileUtil.storeFile(key.getCsrPath(), data);
			}
		}

		return FileUtil.getFile(key.getKeyPath());
	}

	/**
	 * 获取所有可签名的服务器证书
	 * 
	 * @return
	 */
	public List<byte[]> getSignCertData() {
		List<byte[]> certDataList = new ArrayList<byte[]>();
		List<Key> getkeylList = keyDao.getKeySign();
		for (Key key : getkeylList) {
			String certPath = key.getCertPath();
			// 证书数据
			if (StringUtil.isNotBlank(certPath)) {
				byte[] data = new byte[0];
				if (FileUtil.checkPath(certPath)) {
					data = FileUtil.getFile(certPath);
				} else {
					CertData certData = certDataDao.getCertData(key.getCertDataId());
					if (certData != null) {
						// 校验数据信息
						isModify(certData, "the data of CertData which id is " + certData.getId());

						data = certData.getData();
						FileUtil.storeFile(certPath, data);
					}
				}
				if (data.length > 0)
					certDataList.add(data);
			}
		}
		return certDataList;
	}

	/**
	 * 校验信息
	 * 
	 * @param key
	 * @param msg
	 */
	public void isModify(Key key) {
		isModify(key, "the data of Key which id is " + key.getId() + " and cert_dn is " + key.getCertDn());
	}
}
