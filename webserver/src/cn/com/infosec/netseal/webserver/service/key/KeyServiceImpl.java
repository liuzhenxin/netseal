package cn.com.infosec.netseal.webserver.service.key;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.csrData.CsrDataDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.keyData.KeyDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.CsrData;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.KeyData;
import cn.com.infosec.netseal.common.entity.vo.KeyVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.Page;

@Service
public class KeyServiceImpl extends BaseService {

	@Autowired
	private KeyDaoImpl keyDao;
	@Autowired
	private KeyDataDaoImpl keyDataDao;
	@Autowired
	private CsrDataDaoImpl csrDataDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;

	/**
	 * 查询
	 * 
	 * @param page
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Page<KeyVO> searchCert(Page<KeyVO> page, KeyVO keyVO) throws Exception {
		Key key = new Key();
		BeanUtils.copyProperties(keyVO, key);

		int total = keyDao.searchTotal(key);
		int start = page.getStart();
		int end = page.getEnd();
		List<Key> list = keyDao.searchByPage(key, start, end);
		List<KeyVO> keyVoList = new ArrayList<KeyVO>();
		for (Key k : list) {
			KeyVO kVo = new KeyVO();
			BeanUtils.copyProperties(k, kVo);
			// 校验信息
			isModify(k, kVo);

			keyVoList.add(kVo);
		}
		page.setTotalNo(total);
		page.setResult(keyVoList);
		return page;
	}

	/**
	 * 增
	 * 
	 * @param key
	 */
	public void insertKey(KeyVO keyVO) {
		Key key = new Key();
		BeanUtils.copyProperties(keyVO, key);
		keyDao.insertKey(key);
	}

	/**
	 * 查询
	 * 
	 * @return
	 */
	public List<KeyVO> getKey() {
		List<Key> keyList = keyDao.getKeys();
		List<KeyVO> keyVOList = new ArrayList<KeyVO>();
		for (Key key : keyList) {
			KeyVO keyVO = new KeyVO();
			BeanUtils.copyProperties(key, keyVO);
			keyVOList.add(keyVO);
		}
		return keyVOList;
	}

	/**
	 * 查询
	 * 
	 * @param key
	 * @return
	 */
	public List<Key> getKeys(Key key) {
		return keyDao.getkeylList(key);
	}

	public List<Key> getKeys(String certDn) {
		Key key = new Key();
		key.setCertDn(certDn);
		key.setCertUsage(Constants.USAGE_SIGNATURE);
		return keyDao.getkeylList(key);
	}

	/**
	 * 查询所有已导入的证书
	 * 
	 * @return
	 */
	public List<KeyVO> getCheckKey(KeyVO kVO) {
		Key k = new Key();
		BeanUtils.copyProperties(kVO, k);
		k.setCertUsage(k.getCertUsage());
		k.setCertDn(Constants.DEFAULT_UNKNOWN_STRING);
		List<Key> keyList = keyDao.getCheckKey(k);
		List<KeyVO> keyVOList = new ArrayList<KeyVO>();
		for (Key key : keyList) {
			KeyVO keyVO = new KeyVO();
			BeanUtils.copyProperties(key, keyVO);
			keyVOList.add(keyVO);
		}
		return keyVOList;
	}

	/**
	 * 根据id查询密钥
	 * 
	 * @return
	 * @throws WebDataException
	 */
	public KeyVO getKey(Long ID) throws WebDataException {
		Key key = keyDao.getKey(ID);
		if (key == null)
			throw new WebDataException("密钥在数据库中不存在");

		KeyVO keyVO = new KeyVO();
		BeanUtils.copyProperties(key, keyVO);
		return keyVO;
	}

	/**
	 * 删除密钥
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteKey(String id) throws Exception {
		List<String> pathList = new ArrayList<String>();
		if (StringUtil.isNotBlank(id)) {
			String ids[] = id.split(Constants.SPLIT_2);
			for (int i = 0; i < ids.length; i++) {
				if (ids[i] != null) {
					ConfigUtil config = ConfigUtil.getInstance();
					long delkeyId = Long.valueOf(ids[i]);
					long signKeyId = config.getSignKeyId();
					// 如果为指定签名或加密密钥, 不允许删除
					if (delkeyId == signKeyId)
						throw new WebDataException("指定签名密钥,不能删除");

					long encKeyId = config.getEncrpKeyId();
					// 如果为指定签名或加密密钥, 不允许删除
					if (delkeyId == encKeyId)
						throw new WebDataException("指定加密密钥,不能删除");

					Key key = keyDao.getKey(delkeyId);
					// 删除备份该密钥备份文件
					String backupFilepath = key.getBackupPath();
					if (backupFilepath != null)
						pathList.add(backupFilepath);

					int r = keyDao.deleteKey(key.getId());
					if (r == 0)
						throw new WebDataException("删除操作失败");

					keyDataDao.deleteKeyData(key.getKeyDataId());
					csrDataDao.deleteCsrData(key.getCsrDataId());
					certDataDao.deleteCertData(key.getCertDataId());

					// 增加删除记录
					idDeleteDao.insertIDDelete(key.getKeyDataId(), Constants.TABLE_SEAL_KEY_DATA);
					idDeleteDao.insertIDDelete(key.getCsrDataId(), Constants.TABLE_SEAL_CSR_DATA);
					idDeleteDao.insertIDDelete(key.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
				}
			}
			// 删除文件
			for (String path : pathList) {
				FileUtil.deleteFileAndDir(path);
			}
		}

	}

	/**
	 * 更新密钥信息
	 * 
	 * @return
	 */
	public int updateKey(KeyVO keyVO) {
		Key key = new Key();
		BeanUtils.copyProperties(keyVO, key);
		return keyDao.updateKey(key);
	}

	/**
	 * 保存RSA密钥信息
	 * 
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public long serverCertRequestDB(String csrPath, String jksPath) throws Exception {
		byte[] csrData = FileUtil.getFile(csrPath);
		byte[] data = FileUtil.getFile(jksPath);

		// 保存keydata
		KeyData keyData = new KeyData();
		keyData.setData(data);
		keyData.setGenerateTime(DateUtil.getCurrentTime());
		keyData.setUpdateTime(DateUtil.getCurrentTime());
		keyDataDao.insertKeyData(keyData);

		// 保存csrdata
		CsrData csrDatas = new CsrData();
		csrDatas.setData(csrData);
		csrDatas.setGenerateTime(DateUtil.getCurrentTime());
		csrDatas.setUpdateTime(DateUtil.getCurrentTime());
		csrDataDao.insertCsrData(csrDatas);
		// 保存DB
		Key key = new Key();
		key.setCertDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertSn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertUsage(Constants.DEFAULT_INT);
		key.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCsrPath(csrPath);
		key.setCertPath(Constants.DEFAULT_STRING);
		key.setKeyPath(jksPath);
		key.setNotBefor(0L);
		key.setNotAfter(0L);
		key.setCertDataId(0L);
		key.setKeyDataId(keyData.getId());
		key.setCsrDataId(csrDatas.getId());
		key.setKeyMode(Constants.JKS_SUFFIX);
		key.setKeyPwd(StringUtil.base64Encode(Constants.KEY_PWD));
		key.setGenerateTime(DateUtil.getCurrentTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setStatus(1);
		key.setHsmId(-1);
		keyDao.insertKey(key);

		return key.getId();
	}

	/**
	 * 国密证书请求
	 * 
	 * @param csrPath
	 * @param priPath
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public long serverSMCertRequestDB(String csrPath, String priPath) throws Exception {
		byte[] csrData = FileUtil.getFile(csrPath);
		byte[] data = FileUtil.getFile(priPath);

		KeyData keyData = new KeyData();
		keyData.setData(data);
		keyData.setGenerateTime(DateUtil.getCurrentTime());
		keyData.setUpdateTime(DateUtil.getCurrentTime());
		keyDataDao.insertKeyData(keyData);

		// 保存csrdata
		CsrData csrDatas = new CsrData();
		csrDatas.setData(csrData);
		csrDatas.setGenerateTime(DateUtil.getCurrentTime());
		csrDatas.setUpdateTime(DateUtil.getCurrentTime());
		csrDataDao.insertCsrData(csrDatas);

		// 保存DB
		Key key = new Key();
		key.setCertDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertSn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertUsage(Constants.DEFAULT_INT);
		key.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCsrPath(csrPath);
		key.setCertPath(Constants.DEFAULT_STRING);
		key.setKeyPath(priPath);
		key.setNotBefor(0L);
		key.setNotAfter(0L);
		key.setCertDataId(0L);
		key.setKeyDataId(keyData.getId());
		key.setCsrDataId(csrDatas.getId());
		key.setKeyMode(Constants.PRI_SUFFIX);
		key.setKeyPwd(StringUtil.base64Encode(Constants.KEY_PWD));
		key.setGenerateTime(DateUtil.getCurrentTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setStatus(1);
		key.setHsmId(-1);
		keyDao.insertKey(key);

		return key.getId();
	}

	/**
	 * 加密卡密钥请求
	 * 
	 * @param csrPath
	 * @param priPath
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public long serverkeyCardRequestDB(String csrPath, String priPath, int hsmId) throws Exception {
		byte[] csrData = FileUtil.getFile(csrPath);
		byte[] data = FileUtil.getFile(priPath);

		KeyData keyData = new KeyData();
		keyData.setData(data);
		keyData.setGenerateTime(DateUtil.getCurrentTime());
		keyData.setUpdateTime(DateUtil.getCurrentTime());
		keyDataDao.insertKeyData(keyData);

		// 保存csrdata
		CsrData csrDatas = new CsrData();
		csrDatas.setData(csrData);
		csrDatas.setGenerateTime(DateUtil.getCurrentTime());
		csrDatas.setUpdateTime(DateUtil.getCurrentTime());
		csrDataDao.insertCsrData(csrDatas);

		// 保存DB
		Key key = new Key();
		key.setCertDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertSn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertUsage(Constants.DEFAULT_INT);
		key.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCsrPath(csrPath);
		key.setCertPath(Constants.DEFAULT_STRING);
		key.setKeyPath(priPath);
		key.setNotBefor(0L);
		key.setNotAfter(0L);
		key.setCertDataId(0L);
		key.setKeyDataId(keyData.getId());
		key.setCsrDataId(csrDatas.getId());
		key.setKeyMode(Constants.KEY_SUFFIX);
		key.setKeyPwd(StringUtil.base64Encode(Constants.KEY_PWD));
		key.setGenerateTime(DateUtil.getCurrentTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setStatus(1);
		key.setHsmId(hsmId);
		keyDao.insertKeyCard(key);

		return key.getId();
	}

	/**
	 * 保存密钥证书信息
	 * 
	 * @param data
	 * @param cert
	 * @throws WebDataException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void serverCertImportCertDB(byte[] data, X509Certificate cert, String certPath, long keyId) throws WebDataException {
		X509CertEnvelope certEnv = null;
		try {
			certEnv = CertUtil.parseCert(cert.getEncoded());
		} catch (Exception e) {
			throw new WebDataException("证书封装发生错误");
		}
		String certDN = certEnv.getCertDn();
		String certSn = certEnv.getCertSn();

		// 获取密钥用法
		int usage;
		try {
			usage = getCertUsage(cert);
		} catch (Exception e) {
			throw new WebDataException("无法获取证书密钥用法");
		}

		Key kc = new Key();
		kc.setCertDn(certDN);
		kc.setCertUsage(usage);
		List<Key> keyList = keyDao.getkeylList(kc);
		if (keyList.size() > 0)
			throw new WebDataException("密钥证书已存在");

		CertData certData = new CertData();
		certData.setGenerateTime(DateUtil.getCurrentTime());
		certData.setUpdateTime(DateUtil.getCurrentTime());
		certData.setData(data);
		certDataDao.insertCertData(certData);

		Key key = keyDao.getKey(keyId);
		key.setCertPath(certPath);
		key.setCertDn(cert.getSubjectDN().toString());
		key.setCertSn(certSn);
		key.setCertUsage(usage);
		key.setCertIssueDn(cert.getIssuerDN().toString());
		key.setNotBefor(cert.getNotBefore().getTime());
		key.setNotAfter(cert.getNotAfter().getTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setCertDataId(certData.getId());
		keyDao.updateKey(key);
	}

	/**
	 * 保存PFX密钥
	 * 
	 * @param pwd
	 * @param cert
	 * @throws WebDataException
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void serverCertPfxImportCertDB(String pwd, X509Certificate cert, String dirPath) throws WebDataException {
		X509CertEnvelope certEnv = null;
		try {
			certEnv = CertUtil.parseCert(cert.getEncoded());
		} catch (Exception e) {
			throw new WebDataException("证书封装发生错误");
		}
		String certDN = certEnv.getCertDn();
		String certSn = certEnv.getCertSn();

		// 获取证书密钥用法
		int usage;
		try {
			usage = getCertUsage(cert);
		} catch (Exception e) {
			throw new WebDataException("无法获取证书密钥用法");
		}

		Key kc = new Key();
		kc.setCertDn(certDN);
		kc.setCertUsage(usage);
		List<Key> keyList = keyDao.getkeylList(kc);
		if (keyList.size() > 0) {
			throw new WebDataException("密钥证书已存在");
		}

		byte[] data1 = FileUtil.getFile(dirPath + Constants.PFX);
		KeyData keyData = new KeyData();
		keyData.setData(data1);
		keyData.setGenerateTime(DateUtil.getCurrentTime());
		keyData.setUpdateTime(DateUtil.getCurrentTime());

		byte[] data2 = FileUtil.getFile(dirPath + Constants.CER);
		CertData certData = new CertData();
		certData.setData(data2);
		certData.setGenerateTime(DateUtil.getCurrentTime());
		certData.setUpdateTime(DateUtil.getCurrentTime());

		certDataDao.insertCertData(certData);
		keyDataDao.insertKeyData(keyData);

		// 保存DB
		Key key = new Key();
		key.setCertPath(dirPath + Constants.CER);
		key.setKeyPath(dirPath + Constants.PFX);
		key.setGenerateTime(DateUtil.getCurrentTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setCertDn(cert.getSubjectDN().toString());
		key.setCertSn(certSn);
		key.setCertUsage(usage);
		key.setCertIssueDn(cert.getIssuerDN().toString());
		key.setNotBefor(cert.getNotBefore().getTime());
		key.setNotAfter(cert.getNotAfter().getTime());
		key.setKeyPwd(StringUtil.base64Encode(pwd));
		key.setKeyMode(Constants.PFX_SUFFIX);
		key.setKeyDataId(keyData.getId());
		key.setCsrDataId(-1L);
		key.setCertDataId(certData.getId());
		key.setStatus(1);
		key.setHsmId(-1);
		keyDao.insertPfxKey(key);

	}

	/**
	 * 保存国密签名密钥证书信息
	 * 
	 * @param data
	 * @param cert
	 * @throws WebDataException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void serverSMCertImport(byte[] data, X509Certificate cert, String certPath, long keyId) throws WebDataException {
		X509CertEnvelope certEnv = null;
		try {
			certEnv = CertUtil.parseCert(cert.getEncoded());
		} catch (Exception e) {
			throw new WebDataException("证书封装发生错误");
		}
		String certDN = certEnv.getCertDn();
		String certSn = certEnv.getCertSn();

		// 获取证书签名算法
		int usage;
		try {
			usage = getCertUsage(cert);
		} catch (Exception e) {
			throw new WebDataException("无法获取证书密钥用法");
		}

		Key kc = new Key();
		kc.setCertDn(certDN);
		kc.setCertUsage(usage);
		List<Key> keyList = keyDao.getkeylList(kc);
		if (keyList.size() > 0)
			throw new WebDataException("密钥证书已存在");

		CertData certData = new CertData();
		certData.setGenerateTime(DateUtil.getCurrentTime());
		certData.setUpdateTime(DateUtil.getCurrentTime());
		certData.setData(data);
		certDataDao.insertCertData(certData);

		// 更新密钥信息
		Key key = keyDao.getKey(keyId);
		key.setCertDn(cert.getSubjectDN().toString());
		key.setCertSn(certSn);
		key.setCertUsage(usage);
		key.setCertIssueDn(cert.getIssuerDN().toString());
		key.setCertPath(certPath);
		key.setNotBefor(cert.getNotBefore().getTime());
		key.setNotAfter(cert.getNotAfter().getTime());
		key.setCertDataId(certData.getId());
		key.setUpdateTime(DateUtil.getCurrentTime());
		keyDao.updateKey(key);

	}

	/**
	 * 导入加密证书信息
	 * 
	 * @param dirPath
	 * @param certEnv
	 * @param id
	 * @param usage
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void importEncCertToDB(String dirPath, X509CertEnvelope certEnv, int hKey, int usage) {
		byte[] data = FileUtil.getFile(dirPath + Constants.PRI_CARD_KEY);
		byte[] csrData = FileUtil.getFile(dirPath + Constants.CSR);
		byte[] certData = FileUtil.getFile(dirPath + Constants.CER);

		KeyData keyData = new KeyData();
		keyData.setData(data);
		keyData.setGenerateTime(DateUtil.getCurrentTime());
		keyData.setUpdateTime(DateUtil.getCurrentTime());
		keyDataDao.insertKeyData(keyData);

		// 保存csrData
		CsrData csrDatas = new CsrData();
		csrDatas.setData(csrData);
		csrDatas.setGenerateTime(DateUtil.getCurrentTime());
		csrDatas.setUpdateTime(DateUtil.getCurrentTime());
		csrDataDao.insertCsrData(csrDatas);

		// 保存certData
		CertData certDatas = new CertData();
		certDatas.setGenerateTime(DateUtil.getCurrentTime());
		certDatas.setUpdateTime(DateUtil.getCurrentTime());
		certDatas.setData(certData);
		certDataDao.insertCertData(certDatas);

		// 保存DB
		Key key = new Key();
		key.setCertDn(certEnv.getCertDn());
		key.setCertSn(certEnv.getCertSn());
		key.setCertUsage(usage);
		key.setCertIssueDn(certEnv.getCertIssueDn());
		key.setCsrPath(dirPath + Constants.CSR);
		key.setCertPath(dirPath + Constants.CER);
		key.setKeyPath(dirPath + Constants.PRI_CARD_KEY);
		key.setNotBefor(certEnv.getX509Cert().getNotBefore().getTime());
		key.setNotAfter(certEnv.getX509Cert().getNotAfter().getTime());
		key.setCertDataId(certDatas.getId());
		key.setKeyDataId(keyData.getId());
		key.setCsrDataId(csrDatas.getId());
		key.setKeyMode(Constants.KEY_SUFFIX);
		key.setKeyPwd(StringUtil.base64Encode(Constants.KEY_PWD));
		key.setGenerateTime(DateUtil.getCurrentTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setStatus(1);

		// 加密卡位置
		key.setHsmId(hKey);
		keyDao.insertKeyCard(key);
	}

	/**
	 * 加密卡密钥恢复到数据库
	 * 
	 * @param keyPath
	 * @throws WebDataException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public long serverSMCardCertToDB(String dirPath, int hKey) {
		byte[] data = FileUtil.getFile(dirPath + Constants.PRI_CARD_KEY);
		byte[] csrData = FileUtil.getFile(dirPath + Constants.CSR);

		KeyData keyData = new KeyData();
		keyData.setData(data);
		keyData.setGenerateTime(DateUtil.getCurrentTime());
		keyData.setUpdateTime(DateUtil.getCurrentTime());
		keyDataDao.insertKeyData(keyData);

		// 保存csrData
		CsrData csrDatas = new CsrData();
		csrDatas.setData(csrData);
		csrDatas.setGenerateTime(DateUtil.getCurrentTime());
		csrDatas.setUpdateTime(DateUtil.getCurrentTime());
		csrDataDao.insertCsrData(csrDatas);

		// 保存DB
		Key key = new Key();
		key.setCertDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertSn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCertUsage(Constants.DEFAULT_INT);
		key.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
		key.setCsrPath(dirPath + Constants.CSR);
		key.setCertPath(Constants.DEFAULT_STRING);
		key.setKeyPath(dirPath + Constants.PRI_CARD_KEY);
		key.setNotBefor(0L);
		key.setNotAfter(0L);
		key.setCertDataId(0L);
		key.setKeyDataId(keyData.getId());
		key.setCsrDataId(csrDatas.getId());
		key.setKeyMode(Constants.KEY_SUFFIX);
		key.setKeyPwd(StringUtil.base64Encode(Constants.KEY_PWD));
		key.setGenerateTime(DateUtil.getCurrentTime());
		key.setUpdateTime(DateUtil.getCurrentTime());
		key.setStatus(1);

		// 加密卡位置
		key.setHsmId(hKey);
		keyDao.insertKeyCard(key);

		return key.getId();
	}

	/**
	 * 本地不存在,从数据库读取,存储到本地
	 * 
	 * @param key
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public byte[] getKeyData(Key key) throws Exception {
		if (key == null)
			throw new WebDataException("获取服务器密钥参数为空");

		// 密钥数据
		if (!FileUtil.checkPath(key.getKeyPath())) {
			KeyData keyData = keyDataDao.getKeyData(key.getKeyDataId());
			if (keyData != null) {
				// 校验keyData 信息
				isModify(keyData, "密钥数据表中id为" + keyData.getId(), "KetData which id is " + keyData.getId());

				byte[] data = keyData.getData();
				FileUtil.storeFile(key.getKeyPath(), data);
			}
		}

		// 证书数据
		if (StringUtil.isNotBlank(key.getCertPath())) {
			if (!FileUtil.checkPath(key.getCertPath())) {
				CertData certData = certDataDao.getCertData(key.getCertDataId());
				if (certData != null) {
					// 校验数据信息
					isModify(certData, "证书数据表中 id 为" + certData.getId(), "CertData which id is " + certData.getId());

					byte[] data = certData.getData();
					FileUtil.storeFile(key.getCertPath(), data);
				}
			}
		}

		// 证书请求数据
		if (StringUtil.isNotBlank(key.getCsrPath())) {
			if (!FileUtil.checkPath(key.getCsrPath())) {
				CsrData csrData = csrDataDao.getCsrData(key.getCsrDataId());
				if (csrData != null) {
					// 校验信息
					isModify(csrData, "证书请求表中id为" + csrData.getId(), "CsrData which id is " + csrData.getId());

					byte[] data = csrData.getData();
					FileUtil.storeFile(key.getCsrPath(), data);
				}
			}
		}

		return FileUtil.getFile(key.getKeyPath());
	}

	/**
	 * 获取所有可签名的服务器证书
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<byte[]> getSignCertData() throws Exception {
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
						isModify(certData, "证书数据表中 id 为" + certData.getId(), "CertData which id is " + certData.getId());

						data = certData.getData();
						FileUtil.storeFile(key.getCertPath(), data);
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
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean isModify(long id) throws Exception {
		Key key = keyDao.getKey(id);
		return isModify(key);
	}

	/**
	 * 获取加密卡密钥id
	 * 
	 * @return
	 * @throws WebDataException
	 */
	public synchronized int getKeyCardId() throws WebDataException {
		int id = 0;

		// 查询加密卡密钥数量
		Key temK = new Key();
		temK.setKeyMode(Constants.KEY_SUFFIX);
		int count = keyDao.searchTotal(temK);
		if (count < 64) {
			Key k = new Key();
			k.setKeyMode(Constants.KEY_SUFFIX);
			List<Key> keyList = keyDao.getkeylList(k);
			List<String> idList = new ArrayList<>();
			// 获取已存在加密卡密钥id集合
			if (keyList.size() > 0) {
				for (Key key : keyList)
					idList.add(key.getHsmId().toString());

				int lastId = keyList.get(0).getHsmId();
				// 匹配id
				for (Integer i = 0; i < 64; i++) {
					if (!idList.contains(i.toString())) {
						id = i;
						return id;
					} else {
						id = lastId + 1;
					}
				}
			}
		} else {
			throw new WebDataException("加密卡密钥已满");
		}

		return id;
	}

	/**
	 * 获取证书信息的算法(签名 / 加密)
	 * 
	 * @param cert
	 * @return
	 */
	public int getCertUsage(X509Certificate cert) {
		boolean[] usage = cert.getKeyUsage();
		int usages = 0;
		if (usage[0]) // 签名
			usages = 1;

		if (usage[2] || usage[3]) // 加密
			usages = 2;

		if (usage[0] && (usage[2] || usage[3]))
			usages = 3;

		return usages;
	}

	/**
	 * 获取加密卡密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Key> getCardKeys() throws Exception {
		Key key = new Key();
		key.setKeyMode(Constants.KEY_SUFFIX);
		key.setCertUsage(Constants.USAGE_SIGNATURE);
		List<Key> signCardKeyList = keyDao.getkeylList(key);

		// 检验数据完整性
		for (Key ks : signCardKeyList)
			isModify(ks, "密钥", "Key which id is " + ks.getId());

		return signCardKeyList;
	}

}
