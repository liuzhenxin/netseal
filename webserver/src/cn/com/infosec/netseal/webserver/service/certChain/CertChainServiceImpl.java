package cn.com.infosec.netseal.webserver.service.certChain;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.certChain.CertChainDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.CertChain;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.vo.CertChainVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;

@Service
public class CertChainServiceImpl extends BaseService {

	@Autowired
	protected CertChainDaoImpl certChainDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	private CertServiceImpl certService;

	/**
	 * 添加授信证书
	 * 
	 * @param certChain
	 */
	public void insertCertChain(CertChain certChain) {
		certChainDao.insertCertChain(certChain);
	}

	/**
	 * 根据id查询授信证书
	 * 
	 * @param id
	 * @return
	 */
	public CertChain getCertChain(Long id) {
		try {
			return certChainDao.getCertChain(id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 根据certDN获取授信证书
	 * 
	 * @param CertDNs
	 * @return
	 */
	public List<CertChain> getCertChain(String CertDNs) {
		try {
			return certChainDao.getCertChain(CertDNs);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 根据certDN查询授信证书集合
	 * 
	 * @param certDn
	 * @return
	 */
	public List<CertChain> getIssueCert(String certDn) {
		return certChainDao.getCerts(certDn);
	}

	/**
	 * 证书链删除
	 * 
	 * @param id
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCertChain(String id) throws Exception {
		if (id != null && !"".equals(id)) {
			String certDn[] = id.split(Constants.SPLIT_2);
			for (int i = 0; i < certDn.length; i++) {
				if (certDn[i] != null) {
					CertChain c = certChainDao.getCertChain(Long.valueOf(certDn[i]));
					int r = certChainDao.deleteCertChain(c.getId());
					if (r == 0)
						throw new WebDataException("操作失败");

					certDataDao.deleteCertData(c.getCertDataId());
					// 增加删除记录
					idDeleteDao.insertIDDelete(c.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
					FileUtil.deleteFile(c.getCertPath());

				}
			}
		}

	}

	/**
	 * 修改授信证书信息
	 * 
	 * @param certChain
	 * @throws Exception
	 */
	public void updateCertChain(CertChain certChain) throws Exception {
		int r = certChainDao.updateCertChain(certChain);
		if (r == 0) {
			throw new WebDataException("更新失败");
		}
	}

	/**
	 * 修改授信证书pid
	 * 
	 * @param str
	 * @param num
	 * @throws Exception
	 */
	public void updateCertPid(String str, CertChain certChain) throws Exception {
		int r = certChainDao.updateCertPid(str, certChain);
		if (r == 0) {
			throw new WebDataException("更新失败");
		}
	}

	/**
	 * 查询授信证书集合
	 * 
	 * @return
	 */
	public List<CertChain> getCertChain() {
		return certChainDao.getCertChains();
	}

	/**
	 * 查询授信证书集合
	 * 
	 * @return
	 */
	public Hashtable<String, X509Certificate> getCertChainHt() {
		Hashtable<String, X509Certificate> ht = new Hashtable<String, X509Certificate>();
		List<CertChain> list = certChainDao.getCertChains();
		for (CertChain certChain : list) {
			CertData certData = certDataDao.getCertData(certChain.getCertDataId());
			X509Certificate x509Cert = CertUtil.parseCert(certData.getData()).getX509Cert();
			ht.put(certChain.getCertDn(), x509Cert);
		}
		return ht;
	}

	/**
	 * 证书链分页查询
	 * 
	 * @param page
	 * @param certChainVO
	 * @return
	 * @throws Exception 
	 */
	public Page<CertChainVO> searchCertChain(Page<CertChainVO> page, CertChainVO certChainVO) throws Exception {
		CertChain certChain = new CertChain();
		BeanUtils.copyProperties(certChainVO, certChain);

		int total = certChainDao.searchTotal(certChain);
		int start = page.getStart();
		int end = page.getEnd();
		List<CertChain> list = certChainDao.searchByPage(certChain, start, end);
		List<CertChainVO> certVoList = new ArrayList<CertChainVO>();
		for (CertChain certs : list) {
			CertChainVO chainVO = new CertChainVO();
			BeanUtils.copyProperties(certs, chainVO);
			// 校验信息
			isModify(certs, chainVO);
			
			certVoList.add(chainVO);
		}

		page.setTotalNo(total);
		page.setResult(certVoList);
		return page;
	}

	/**
	 * 验证授信证书信息
	 * 
	 * @param certDN
	 * @param certData
	 * @throws Exception
	 */
	public void verifyCert(String certDN, byte[] certData) throws Exception {
		try {
			// 创建证书对象
			X509CertEnvelope certEnv = CertUtil.parseCert(certData);
			String subjectDN = certEnv.getCertDn();

			// 判断DN
			if (!subjectDN.equals(certDN))
				throw new WebDataException("证书文件DN不匹配");

			verifyCert(certData);
		} catch (Exception e) {
			throw new WebDataException("验证证书发生错误," + e.getMessage());
		}
	}
	/**验证签名证书
	 * @param certData
	 * @throws Exception 
	 */
	public void verifyCert(byte[] certData) throws Exception {
		verifyCert(certData, true, false);
	}
	/**
	 * 校验授信证书
	 * 
	 * @param certData
	 * @throws Exception
	 */
	public void verifyCert(byte[] certData, boolean isCheckSignCert, boolean isCheckEncCert) throws Exception {
		try {
			// 创建证书对象
			X509CertEnvelope certEnv = CertUtil.parseCert(certData);

			// 查看根证书
			String rootCertDN = certEnv.getCertIssueDn();

			List<CertChain> chainList = getCertChain(rootCertDN);
			if (chainList.size() == 0)
				throw new WebDataException("证书颁发者DN不存在");
			if (chainList.size() >= 2)
				throw new WebDataException("证书颁发者在库中不唯一");
			CertChain chain = chainList.get(0);
			// 校验根证信息
			isModify(chain, "授信证书表" + chain.getCertDn(), "CertChain which id is " + chain.getId() + " and cert_dn is " + chain.getCertDn());

			// 获取根证
			byte[] rootCertData = certService.getCertData(chain.getCertPath(), chain.getCertDataId());

			// 检查证书有效性
			CertUtil.verifyCert(certData, rootCertData, null, isCheckSignCert, isCheckEncCert);
		} catch (WebDataException e) {
			LoggerUtil.errorlog(e.getMessage());
			throw new WebDataException(e.getMessage());
		} catch (Exception e) {
			LoggerUtil.errorlog("root cert verify sign error", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 获取授信证书序列号
	 * 
	 * @param certData
	 * @return
	 * @throws Exception
	 */
	public String getCertSerialNumber(byte[] certData) throws Exception {
		try {
			// 创建证书对象
			X509CertEnvelope certEnv = CertUtil.parseCert(certData);
			return certEnv.getCertSn();
		} catch (Exception e) {
			LoggerUtil.errorlog("get cert serial number error", e);
			throw new WebDataException(e.getMessage());
		}
	}

	/**
	 * 添加授信证书
	 * 
	 * @param certChainVO
	 * @param certDNs
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addCertChainDB(CertChainVO certChainVO, String certDNs) throws Exception {
		CertChain certChain = new CertChain();
		BeanUtils.copyProperties(certChainVO, certChain);

		CertData certData = new CertData();
		certData.setGenerateTime(System.currentTimeMillis());
		certData.setData(FileUtil.getFile(certChain.getCertPath()));
		certData.setUpdateTime(certChain.getUpdateTime());
		certDataDao.insertCertData(certData);

		certChain.setCertDataId(certData.getId());
		insertCertChain(certChain);

		// 判断该证书是否为数据库已存证书的颁发证书
		List<CertChain> isSonCertList = getIssueCert(certDNs);
		if (isSonCertList.size() > 0) {
			List<CertChain> thisCertList = getCertChain(certDNs);
			CertChain thisCert = thisCertList.get(0);
			CertChain upPidCertChain = isSonCertList.get(0);
			upPidCertChain.setPid(thisCert.getId());
			upPidCertChain.setUpdateTime(DateUtil.getCurrentTime());
			updateCertPid(certDNs, upPidCertChain);
		}
	}

	/**
	 * 添加授信证书链
	 * 
	 * @param certChainVO
	 * @param certDN
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addCertChainsDB(CertChainVO certChainVO, String certDN) throws Exception {
		CertChain certChain = new CertChain();
		BeanUtils.copyProperties(certChainVO, certChain);

		CertData certData = new CertData();
		certData.setGenerateTime(System.currentTimeMillis());
		certData.setData(FileUtil.getFile(certChain.getCertPath()));
		certData.setUpdateTime(certChain.getUpdateTime());
		certDataDao.insertCertData(certData);

		certChain.setCertDataId(certData.getId());
		insertCertChain(certChain);

		// 判断该证书是否为数据库已存证书的颁发证书
		List<CertChain> isSonCertList = getIssueCert(certDN);
		if (isSonCertList.size() > 0) {
			List<CertChain> thisCertList = getCertChain(certDN);
			CertChain thisCert = thisCertList.get(0);
			CertChain upPidCertChain = isSonCertList.get(0);
			upPidCertChain.setPid(thisCert.getId());
			upPidCertChain.setUpdateTime(DateUtil.getCurrentTime());
			updateCertPid(certDN, upPidCertChain);
		}
	}
}
