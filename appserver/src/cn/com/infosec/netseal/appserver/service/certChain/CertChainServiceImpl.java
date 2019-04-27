package cn.com.infosec.netseal.appserver.service.certChain;

import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.common.dao.certChain.CertChainDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.CertChain;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;

@Service
public class CertChainServiceImpl extends BaseService {

	@Autowired
	private CertChainDaoImpl certChainDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private CertServiceImpl certService;

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public List<CertChain> getCertChain(String CertDN) {
		return certChainDao.getCertChain(CertDN);
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
			X509CertEnvelope certVO = CertUtil.parseCert(certData.getData());
			ht.put(certChain.getCertDn(), certVO.getX509Cert());
		}
		return ht;
	}

	public void verifyCert(String certDN, byte[] certData) {
		try {
			// 创建证书对象
			X509CertEnvelope certVO = CertUtil.parseCert(certData);
			String subjectDN = certVO.getCertDn();

			// 判断DN
			if (!subjectDN.equals(certDN))
				throw new NetSealRuntimeException(ErrCode.CERT_DN_NOT_MATCH, "cert file dn not match dn in db");

			verifyCert(certData);
		} catch (NetSealRuntimeException ex) {
			throw ex;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ROOT_CERT_VERIFY_SIGN_ERROR, e.getMessage());
		}
	}

	/**
	 * 验证签名证书
	 * 
	 * @param certData
	 */
	public void verifyCert(byte[] certData) {
		verifyCert(certData, true, false, true);
	}

	/**
	 * 验证签名证书
	 * 
	 * @param certData
	 */
	public void verifyCert(byte[] certData, boolean checkCertDate) {
		verifyCert(certData, true, false, checkCertDate);
	}

	public void verifyCert(byte[] certData, boolean checkSignCert, boolean checkEncCert, boolean checkCertDate) {
		try {
			// 创建证书对象
			X509CertEnvelope certVO = CertUtil.parseCert(certData);

			// 查看根证书
			String rootCertDN = certVO.getCertIssueDn();

			List<CertChain> chainlList = getCertChain(rootCertDN);
			if (chainlList.size() == 0)
				throw new NetSealRuntimeException(ErrCode.ROOT_CERT_DN_NOT_FOUND, "root cert not be trusted, root dn is " + rootCertDN);
			if (chainlList.size() > 1)
				throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "the number of cert is more than one");
			CertChain chain = chainlList.get(0);
			// 校验根证信息
			isModify(chain, "the data of CertChain which id is " + chain.getId() + " and cert_dn is " + chain.getCertDn());

			// 获取根证
			byte[] rootCertData = certService.getCertData(chain.getCertPath(), chain.getCertDataId());

			// 检查证书有效性
			CertUtil.verifyCert(certData, rootCertData, null, checkSignCert, checkEncCert, checkCertDate);

		} catch (NetSealRuntimeException ex) {
			throw ex;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ROOT_CERT_VERIFY_SIGN_ERROR, e.getMessage());
		}
	}

}
