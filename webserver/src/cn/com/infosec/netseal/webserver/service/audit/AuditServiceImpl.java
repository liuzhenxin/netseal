package cn.com.infosec.netseal.webserver.service.audit;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.audit.AuditDaoImpl;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.dao.request.RequestDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.sealData.SealDataDaoImpl;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Audit;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.po.Request;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.SealData;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.entity.vo.RequestVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.GenSealUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.webserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;

@Service
public class AuditServiceImpl extends BaseService {
	@Autowired
	protected AuditDaoImpl auditDao;
	@Autowired
	protected RequestDaoImpl requestDao;
	@Autowired
	private SealDaoImpl sealDao;
	@Autowired
	protected TemplateDaoImpl templateDao;
	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private SealDataDaoImpl sealDataDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private PhotoDataDaoImpl photoDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	private PhotoDataServiceImpl photoDataService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private KeyServiceImpl keyService;

	public Audit getAudit(Long id) {
		return auditDao.getAudit(id);
	}

	public void deleteAudit(Long id) throws Exception {
		auditDao.deleteAudit(id);
	}

	/**
	 * 删除未审核的印章记录
	 * 
	 * @param ids
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteAudit(String ids) throws Exception {
		if (ids == null || "".equals(ids.trim())) {
			return;
		}
		String[] temp = ids.split(",");
		if (temp.length > 0) {
			ids = temp[0];
			for (int i = 0; i < temp.length; i++) {
				Long id = Long.parseLong(temp[i].trim());
				// 判断印章是否审核通过
				/*
				 * Request request = requestDao.getRequest(id); if (request.getStatus() == 1) { throw new WebDataException("审核通过已生成印章,无法删除"); } else {
				 */
				int r = auditDao.deleteAudit(id);
				if (r == 0) {
					throw new WebDataException("操作失败");
					// }
				}
			}
		}
	}

	/**
	 * 印章审核
	 * 
	 * @param seal
	 * @param audit
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void auditSeal(RequestVO requestVO, String signedData, String toSignData) throws Exception {
		Cert cert = certDao.getCert(requestVO.getCertId());
		if (cert.getCertDn() == null)
			throw new WebDataException("签章人证书不存在");

		// 校验签章人证书信息
		isModify(cert, "签章人所注册的证书 " + cert.getCertDn(), "Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());

		List<Cert> sysUserCertList = certDao.getCertBySysUserId(requestVO.getSysUserId());
		if (sysUserCertList == null || sysUserCertList.size() == 0)
			throw new WebDataException("管理员证书不存在");

		Cert sysUserCert = sysUserCertList.get(0);
		// 校验管理员证书信息
		isModify(sysUserCert, "管理员所注册的证书 " + sysUserCert.getCertDn(), "Cert which id is " + sysUserCert.getId() + " and cert_dn is " + sysUserCert.getCertDn());

		// 审核通过 生成印章
		Template template = templateDao.getTemplate(requestVO.getTemplateId());
		if (template == null)
			throw new WebDataException("生成印章失败,印模不存在");

		// 校验印模信息
		isModify(template, "印章申请所使用印模 " + template.getName(), "Template which id is " + template.getId() + " and name is " + template.getName());

		long currentTime = DateUtil.getCurrentTime();
		
		String sealPath = Constants.SEAL_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.SEAL_SUFFIX;
		Request request = requestDao.getRequest(requestVO.getId());
		try {
			Seal seal = new Seal();
			BeanUtils.copyProperties(requestVO, seal);
			seal.setStatus(1);
			seal.setType(template.getType());
			seal.setIsDownload(template.getIsDownload());
			seal.setPhotoPath(requestVO.getPhotoPath());
			seal.setIsAuthCertDownload(template.getIsAuthCertDownload());
			seal.setIsAuditReq(template.getIsAuditReq());
			seal.setIsAuthCertGenSeal(template.getIsAuthCertGenSeal());
			seal.setTransparency(request.getTransparency());
			seal.setSealPath(sealPath);
			seal.setUpdateTime(currentTime);
			seal.setPhotoDataId(request.getPhotoDataId());

			// 1. 生成印章文件
			byte[] data = GenSealUtil.writeSigned2SealData(StringUtil.base64Decode(toSignData), StringUtil.base64Decode(signedData), sealPath);

			// 2.生成印章数据记录
			SealData sealData = new SealData();
			sealData.setGenerateTime(System.currentTimeMillis());
			sealData.setData(data);
			sealData.setUpdateTime(seal.getUpdateTime());
			Long sealId = sealDataDao.insertSealData(sealData);

			// 3.保存印章
			seal.setSealDataId(sealId);
			seal.setAuditId(Long.valueOf(-1));
			seal.setUsedCount(0);
			seal.setDownloadTime(0L);
			sealDao.insertSeal(seal);

			// 4. 删除申请记录
			requestDao.deleteRequest(requestVO.getId());

		} catch (Exception e) {
			FileUtil.deleteFile(sealPath);
			LoggerUtil.errorlog("gen seal error", e);
			throw new WebDataException("生成印章失败");
		}

	}

	/**
	 * 返回印章数据
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public byte[] auditSealSign(RequestVO requestVO, Seal seal) throws Exception {
		Request request = new Request();
		request.setCertId(requestVO.getCertId());
		List<Request> requestList = requestDao.getRequests(request);
		if (requestList != null && requestList.size() > 1)
			throw new WebDataException("相同的证书,存在" + requestList.size() + "条申请记录");

		// 校验印章申请信息是否被篡改
		request = requestDao.getRequest(requestVO.getId());
		isModify(request, "所选印章申请 " + request.getName(), "Request which id is " + request.getId() + " and name is " + request.getName());

		Seal tempSeal = new Seal();
		tempSeal.setCertId(requestVO.getCertId());
		List<Seal> sealList = sealDao.getSeals(tempSeal);
		if (sealList != null && sealList.size() > 1)
			throw new WebDataException("此证书已生成印章,不能生成多个印章");

		// 审核通过 生成印章
		Template template = templateDao.getTemplate(requestVO.getTemplateId());
		if (template == null)
			throw new WebDataException("生成印章失败,印模不存在");

		// 校验印模信息
		isModify(template, "印章申请所使用的印模 " + template.getName(), "Template which id is " + template.getId() + " and name is " + template.getName());

		Long notBefor = template.getNotBefor();
		Long notAfter = template.getNotAfter();
		if (requestVO.getNotBefor() < notBefor)
			throw new WebDataException("启用日期超出印模的启用日期");

		if (requestVO.getNotAfter() > notAfter)
			throw new WebDataException("终止日期超出印模的终止日期");

		String photoPath = requestVO.getPhotoPath();

		BeanUtils.copyProperties(requestVO, seal);

		seal.setGenerateTime(DateUtil.getCurrentTime());
		seal.setName(requestVO.getName());
		seal.setCertId(requestVO.getCertId());
		seal.setType(template.getType());
		seal.setPhotoPath(photoPath);

		// 1.签章人证书
		Cert cert = certDao.getCert(requestVO.getCertId());
		if (cert == null)
			throw new WebDataException("签章人证书不存在");
		// 校验签章人证书
		isModify(cert, "签章人所注册的证书 " + cert.getCertDn(), "Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());

		// 2.管理员证书
		List<Cert> sysUserCertList = certDao.getCertBySysUserId(requestVO.getSysUserId());
		if (sysUserCertList == null || sysUserCertList.size() == 0)
			throw new WebDataException("管理员证书不存在");

		Cert sysUserCert = sysUserCertList.get(0);
		// 校验管理员证书信息
		isModify(sysUserCert, "当前管理员所注册的证书 " + sysUserCert.getCertDn(), "Cert which id is " + sysUserCert.getId() + " and cert_dn is " + sysUserCert.getCertDn());

		byte[] sysUserCertData = certService.getCertData(sysUserCert.getCertPath(), sysUserCert.getCertDataId());
		byte[] userCertData = certService.getCertData(cert.getCertPath(), cert.getCertDataId());

		Integer isAuthCertGenSeal = template.getIsAuthCertGenSeal();// 审核是否验证书
		if (isAuthCertGenSeal != null && isAuthCertGenSeal == 1) {// 审核验证书
			// 3.验证管理员证书
			try {
				certChainService.verifyCert(sysUserCertData);
			} catch (WebDataException e) {
				throw new WebDataException("管理员" + e.getMessage());
			}
			// 4.验证签章人证书
			try {
				certChainService.verifyCert(userCertData);
			} catch (WebDataException e) {
				throw new WebDataException("签章人" + e.getMessage());
			}
		}
		// 5. 初始化数据
		byte[] photoDataBs = null;
		if(StringUtil.isNotBlank(photoPath))
		    photoDataBs = photoDataService.getPhotoData(photoPath, requestVO.getPhotoDataId());

		// 6.产生印章数据
		List<byte[]> certDataList = keyService.getSignCertData();
		byte[] sealData = GenSealUtil.prepareSealData2Sign(seal, certDataList, sysUserCertData, userCertData, photoDataBs);
		return sealData;
	}

	/**
	 * 分页显示印章申请信息
	 * 
	 * @param page
	 * @param audit
	 * @return
	 */
	public Page<Audit> searchAudit(Page<Audit> page, Audit audit) {
		int total = auditDao.searchTotal(audit);

		int start = page.getStart();
		int end = page.getEnd();
		List<Audit> list = auditDao.searchByPage(audit, start, end);

		page.setTotalNo(total);
		page.setResult(list);
		return page;
	}

	/**
	 * 获取证书序列号
	 * 
	 * @param certPath
	 * @param certDataId
	 * @return
	 * @throws Exception
	 */
	public String getCertSerialNumber(String certPath, Long certDataId) throws Exception {
		String certSn = "";
		try {
			byte[] certData = certService.getCertData(certPath, certDataId);
			certSn = certChainService.getCertSerialNumber(certData);
		} catch (Exception e) {
			throw new WebDataException(e.getMessage());
		}
		return certSn;
	}

}
