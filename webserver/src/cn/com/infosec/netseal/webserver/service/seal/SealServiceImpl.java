package cn.com.infosec.netseal.webserver.service.seal;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.company.CompanyDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.sealData.SealDataDaoImpl;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.SealData;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.entity.vo.KeyVO;
import cn.com.infosec.netseal.common.entity.vo.SealVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.GenSealUtil;
import cn.com.infosec.netseal.common.util.OidUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ofd.OfdStampUtil;
import cn.com.infosec.netseal.common.util.pdf.PdfStampUtil;
import cn.com.infosec.netseal.common.util.photo.PhotoUitl;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.webserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.util.Base64;

@Service
public class SealServiceImpl extends BaseService {

	@Autowired
	private SealDaoImpl sealDao;
	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;
	@Autowired
	private PhotoDataDaoImpl photoDataDao;
	@Autowired
	private SealDataDaoImpl sealDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	private CompanyDaoImpl companyDao;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private KeyDaoImpl keyDao;
	

	/**
	 * 添加印章
	 * 
	 * @param sealVO
	 * @throws Exception
	 */
	public void insertSeal(SealVO sealVO) throws Exception {
		Seal seal = new Seal();
		BeanUtils.copyProperties(sealVO, seal);
		sealDao.insertSeal(seal);
	}

	/**
	 * 获取印章数据
	 * 
	 * @param id
	 * @return
	 */
	public SealVO getSeal(Long id) {
		Seal seal = sealDao.getSeal(id);
		if (seal == null)
			return null;
		SealVO sealVO = new SealVO();
		BeanUtils.copyProperties(seal, sealVO);
		User user = seal.getUser();
		if (user != null) {
			sealVO.setUserName(user.getName());
			Company company = user.getCompany();
			if (company != null)
				sealVO.setCompanyName(company.getName());

		}
		Cert cert = seal.getCert();
		if (cert != null) {
			sealVO.setCertDn(cert.getCertDn());
			sealVO.setCertSn(cert.getCertSn());
		}

		return sealVO;
	}

	/**
	 * 查列表
	 * 
	 * @param seal
	 * @return
	 */
	public List<Seal> getSeal(Seal seal) {
		return sealDao.getSeals(seal);
	}

	/**
	 * 删除印章
	 * 
	 * @param ids
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteSeal(String ids) throws Exception {
		List<String> sealPathList = new ArrayList<String>();
		List<String> photoPathList = new ArrayList<String>();
		if (StringUtil.isBlank(ids))
			throw new WebDataException("请求参数为空");

		String[] temp = ids.split(",");
		if (temp.length > 0) {
			for (int i = 0; i < temp.length; i++) {
				Long id = Long.parseLong(temp[i].trim());
				Seal seal = sealDao.getSeal(id);

				int r = sealDao.deleteSeal(id);
				if (r == 0)
					throw new WebDataException("操作失败");

				sealPathList.add(seal.getSealPath());
				photoPathList.add(seal.getPhotoPath());

				// 增加删除记录
				idDeleteDao.insertIDDelete(id, Constants.TABLE_SEAL_SEAL);
				// 删除印章数据
				sealDataDao.deleteSealData(seal.getSealDataId());
				// 增加删除记录
				idDeleteDao.insertIDDelete(seal.getSealDataId(), Constants.TABLE_SEAL_SEAL_DATA);
				// 删除印章图片数据
				Long photoDataId = seal.getPhotoDataId();
				if (!photoDataId.equals(-1L)) {
					photoDataDao.deletePhotoData(photoDataId);
					idDeleteDao.insertIDDelete(photoDataId, Constants.TABLE_SEAL_PHOTO_DATA);
				}
			}
		}
		// 删除印章文件
		for (String sealPath : sealPathList) {
			FileUtil.deleteFile(sealPath);
			sealPath = sealPath.substring(0, sealPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
			FileUtil.deleteEmptyDir(sealPath);// 删除空文件夹
		}
		// 删除印章图片文件
		for (String photoPath : photoPathList) {
			if (StringUtil.isNotBlank(photoPath)) {
				FileUtil.deleteFile(photoPath);
				photoPath = photoPath.substring(0, photoPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
				FileUtil.deleteEmptyDir(photoPath);// 删除空文件夹
			}
		}
	}

	/**
	 * 改
	 * 
	 * @param seal
	 */
	public int updateSealCount(Long id, Long updateTime) {
		Seal s = sealDao.getSeal(id);
		s.setUsedCount(s.getUsedCount() + 1);
		s.setUpdateTime(updateTime);

		return sealDao.updateSealCount(s);
	}

	/**
	 * 更新印章信息
	 * 
	 * @param sealVO
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSeal(SealVO sealVO) throws Exception {
		Seal s = sealDao.getSeal(sealVO.getId());

		if (s != null) {
			// 校验信息
			isModify(s, "所选印章" + s.getName(), "Seal which id is " + s.getId() + " and name is " + s.getName());

			s.setUsedLimit(sealVO.getUsedLimit());
			s.setTransparency(sealVO.getTransparency());
			s.setUpdateTime(DateUtil.getCurrentTime());

			int r = sealDao.updateSeal(s);
			if (r == 0)
				throw new WebDataException("更新失败");

		} else {
			throw new WebDataException("更新失败,印章不存在");
		}
	}

	/**
	 * 产生印章信息(印章之前有审核)
	 * 
	 * @param sealVO
	 * @throws Exception
	 */
	public Properties genAuditSeal(SealVO sealVO, Long sysUserId, byte[] fileByte) throws Exception {
		Seal seal = sealDao.getSeal(sealVO.getId());
		// 获取管理员证书
		List<CertVO> certVOList = certService.getCertBySysUserId(sysUserId);
		if (certVOList.size() == 0)
			throw new WebDataException("该管理员未绑定证书");

		if (certVOList.size() >= 2)
			throw new WebDataException("该管理员证书绑定不唯一");

		CertVO certVO = certVOList.get(0);
		// 校验证书信息
		if (!certService.isModify(certVO.getId()))
			throw new WebDataException("管理员所绑定证书信息被篡改");
		// 管理员证书数据
		List<Cert> sysUserCertList = certDao.getCertBySysUserId(sysUserId);
		if (sysUserCertList == null || sysUserCertList.size() == 0)
			throw new WebDataException("管理员证书不存在");

		Cert sysUserCert = sysUserCertList.get(0);
		// 校验管理员证书信息
		certService.isModify(sysUserCert, "当前管理员所注册的证书 " + sysUserCert.getCertDn(), "Cert which id is " + sysUserCert.getId() + " and cert_dn is " + sysUserCert.getCertDn());
		Cert cert = certDao.getCert(seal.getCertId());
		if (cert == null)
			throw new WebDataException("签章人证书不存在");
		// 校验签章人证书
		certService.isModify(cert, "签章人所注册的证书 " + cert.getCertDn(), "Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());
		byte[] sysUserCertData = certService.getCertData(sysUserCert.getCertPath(), sysUserCert.getCertDataId());
		byte[] userCertData = certService.getCertData(cert.getCertPath(), cert.getCertDataId());

		Integer isAuthCertGenSeal = seal.getIsAuthCertGenSeal();// 审核是否验证书
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

		// 得到证书摘要算法
		byte[] certData = certService.getCertData(certVO.getCertPath(), certVO.getCertDataId());
		X509CertEnvelope certs = CertUtil.parseCert(certData);
		String hashAlg = OidUtil.getHashAlg(certs.getSigAlgOID()).toLowerCase();
		// 产生图片数据
		byte[] photoDataBs = null;
		if (fileByte == null) {
			photoDataBs = PhotoUitl.convertBGColor(FileUtil.getFile(seal.getPhotoPath()), (int) (sealVO.getTransparency() * 2.55));
		} else {
			photoDataBs = PhotoUitl.convertBGColor(fileByte, (int) (sealVO.getTransparency() * 2.55));
		}

		FileUtil.deleteFile(seal.getPhotoPath());
		PhotoData photoData = photoDataDao.getPhotoData(seal.getPhotoDataId());
		photoData.setUpdateTime(DateUtil.getCurrentTime());
		photoData.setData(photoDataBs);
		int updateNum = photoDataDao.updatePhotoData(photoData);
		if (updateNum > 0) {
			FileUtil.storeFile(seal.getPhotoPath(), photoDataBs);
		} else {
			throw new WebDataException("更新失败");
		}

		// 产生印章数据
		List<byte[]> certDataList = keyService.getSignCertData();
		byte[] sealData = GenSealUtil.prepareSealData2Sign(seal, certDataList, sysUserCertData, userCertData, photoDataBs);

		Properties sealInfo = new Properties();
		sealInfo.put("generateTime", seal.getGenerateTime());
		sealInfo.put("sealData", Base64.encode(sealData));
		sealInfo.put("hashAlg", hashAlg);
		return sealInfo;
	}

	/**
	 * 更新印章信息(印章之前有审核)
	 * 
	 * @param sealVO
	 * @param signData
	 * @param toSignData
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSeal(SealVO sealVO, String signData, String toSignData) throws Exception {
		Seal seal = sealDao.getSeal(sealVO.getId());
		// 删除旧印章文件
		FileUtil.deleteFile(seal.getSealPath());

		byte[] data = GenSealUtil.writeSigned2SealData(StringUtil.base64Decode(toSignData), StringUtil.base64Decode(signData), seal.getSealPath());
		SealData sealData = sealDataDao.getSealData(seal.getSealDataId());
		sealData.setUpdateTime(DateUtil.getCurrentTime());
		sealData.setData(data);
		int sealNum = sealDataDao.updateSealData(sealData);
		if (sealNum < 1) {
			throw new WebDataException("更新失败");
		}

		updateSeal(sealVO);

	}

	/**
	 * 更新印章信息(印章之前没有有审核)
	 * 
	 * @param sealVO
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSeal(SealVO sealVO, Long sysUserId, byte[] fileByte) throws Exception {
		Seal seal = sealDao.getSeal(sealVO.getId());
		// 删除旧印章文件
		FileUtil.deleteFile(seal.getSealPath());
		// 获取服务器密钥
		ConfigUtil config = ConfigUtil.getInstance();
		long checkId = config.getSignKeyId();
		if (checkId == -1)
			throw new WebDataException("未指定服务器签名证书,请设定");

		Key key = keyDao.getKey(checkId);
		if (key == null)
			throw new WebDataException("服务器证书不存在");

		// 校验服务器证书信息
		isModify(key, "服务器证书:" + key.getCertDn(), "Key which id is " + key.getId() + " and cert_dn is " + key.getCertDn());

		// 初始化密钥文件
		byte[] keyData = keyService.getKeyData(key);
		Integer isAuthCertGenSeal = seal.getIsAuthCertGenSeal();
		Cert cert = certDao.getCert(seal.getCertId());
		if (cert.getCertDn() == null)
			throw new WebDataException("签章人证书不存在");
		// 校验签章人证书信息
		isModify(cert, "所选证书 " + cert.getCertDn(), "Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());
		String userCertPath = cert.getCertPath();
		byte[] serverCertData = certService.getCertData(key.getCertPath(), key.getCertDataId());
		byte[] userCertData = certService.getCertData(userCertPath, cert.getCertDataId());
		if (isAuthCertGenSeal != null && isAuthCertGenSeal == 1) {// 审核验证书
			// 验证服务器证书
			try {
				certChainService.verifyCert(serverCertData);
			} catch (WebDataException e) {
				throw new WebDataException("服务器" + e.getMessage());
			}
			// 验证签章人证书
			try {
				certChainService.verifyCert(userCertData);
			} catch (WebDataException e) {
				throw new WebDataException("签章人" + e.getMessage());
			}
		}
		// 产生图片数据
		byte[] photoDataBs = null;
		if (fileByte == null) {
			photoDataBs = PhotoUitl.convertBGColor(FileUtil.getFile(seal.getPhotoPath()), (int) (sealVO.getTransparency() * 2.55));
		} else {
			photoDataBs = PhotoUitl.convertBGColor(fileByte, (int) (sealVO.getTransparency() * 2.55));
		}

		FileUtil.deleteFile(seal.getPhotoPath());
		PhotoData photoData = photoDataDao.getPhotoData(seal.getPhotoDataId());
		photoData.setUpdateTime(DateUtil.getCurrentTime());
		photoData.setData(photoDataBs);
		int updateNum = photoDataDao.updatePhotoData(photoData);
		if (updateNum > 0) {
			FileUtil.storeFile(seal.getPhotoPath(), photoDataBs);
		} else {
			throw new WebDataException("更新失败");
		}
		List<byte[]> certDataList = keyService.getSignCertData();
		byte[] data = GenSealUtil.genSealData(seal, certDataList, key, keyData, serverCertData, userCertData, photoDataBs, ConfigUtil.getInstance().getGmOid().getBytes());
		SealData sealData = sealDataDao.getSealData(seal.getSealDataId());
		sealData.setUpdateTime(DateUtil.getCurrentTime());
		sealData.setData(data);
		int sealNum = sealDataDao.updateSealData(sealData);
		if (sealNum < 1)
			throw new WebDataException("更新失败");

		updateSeal(sealVO);
	}

	/**
	 * 更新印章状态
	 * 
	 * @param ids
	 * @param status
	 * @throws Exception
	 */
	public void updateStatus(String ids, int status) throws Exception {
		String id[] = ids.split(Constants.SPLIT_1);
		long sysTime = DateUtil.getCurrentTime();
		String macs = "";
		for (int i = 0; i < id.length; i++) {
			Seal s = sealDao.getSeal(Long.parseLong(id[i]));
			// 校验信息
			isModify(s, "所选印章" + s.getName(), "Seal which id is " + s.getId() + " and name is " + s.getName());

			s.setStatus(status);
			s.setUpdateTime(sysTime);
			macs = s.calMac(); // 重新计算MAC值

			int r = sealDao.updateStatus(Long.parseLong(id[i]), status, sysTime, macs);
			if (r == 0)
				throw new WebDataException("更新失败");
		}
	}

	/**
	 * 修改印章中证书ID
	 * 
	 * @param userId
	 *            用户id
	 * @param preCertId
	 *            原来证书id
	 * @param certId
	 *            新证书id
	 * @return
	 */
	public void updateCertId(Long userId, Long preCertId, Long certId) throws Exception {
		sealDao.updateCertId(userId, preCertId, certId);
	}

	/**
	 * 获取印章集合
	 * 
	 * @param sealVO
	 * @return
	 */
	public List<SealVO> getSeals(SealVO sealVO) {
		Seal seal = new Seal();
		BeanUtils.copyProperties(sealVO, seal);
		List<Seal> sealList = sealDao.getSeals(seal);
		List<SealVO> sealVOList = new ArrayList<SealVO>();
		for (Seal s : sealList) {
			SealVO sVO = new SealVO();
			BeanUtils.copyProperties(s, sVO);
			sealVOList.add(sVO);
		}
		return sealVOList;
	}

	/**
	 * 获取非手写章集合
	 * 
	 * @param sealVO
	 * @return
	 */
	public List<SealVO> getNoHandSeals(SealVO sealVO) {
		Seal seal = new Seal();
		BeanUtils.copyProperties(sealVO, seal);
		List<Seal> sealList = sealDao.getSeals(seal);
		List<SealVO> sealVOList = new ArrayList<SealVO>();
		for (Seal s : sealList) {
			SealVO sVO = new SealVO();
			BeanUtils.copyProperties(s, sVO);
			if (s.getType() != 3)
				sealVOList.add(sVO);
		}
		return sealVOList;
	}

	/**
	 * 根据印模id获取印章
	 * 
	 * @param id
	 * @return
	 */
	public List<SealVO> getSealByTemplateId(Long id) {
		Seal seal = new Seal();
		seal.setTemplateId(id);
		List<Seal> sealList = sealDao.getSeals(seal);
		List<SealVO> sealVOList = new ArrayList<SealVO>();
		for (Seal s : sealList) {
			SealVO sVO = new SealVO();
			BeanUtils.copyProperties(s, sVO);
			sealVOList.add(sVO);
		}
		return sealVOList;
	}

	/**
	 * 获取签章人下的印章集合
	 * 
	 * @param userId
	 * @return
	 */
	public List<SealVO> getSealByUserId(Long userId) {
		Seal seal = new Seal();
		seal.setUserId(userId);
		List<Seal> sealList = sealDao.getSeals(seal);
		List<SealVO> sealVOList = new ArrayList<SealVO>();
		for (Seal s : sealList) {
			SealVO sVO = new SealVO();
			BeanUtils.copyProperties(s, sVO);
			sealVOList.add(sVO);
		}
		return sealVOList;
	}

	/**
	 * 分页查询印章数据
	 * 
	 * @param page
	 * @param sealVO
	 * @return
	 * @throws Exception
	 */
	public Page<SealVO> searchSeal(Page<SealVO> page, SealVO sealVO, Long cid) throws Exception {
		Seal seal = new Seal();
		BeanUtils.copyProperties(sealVO, seal);
		String u_Name = sealVO.getUserName();
		String certDN = sealVO.getCertDn();
		if (StringUtil.isNotBlank(u_Name)) {
			User u = new User();
			u.setName(u_Name);
			seal.setUser(u);
		}
		if (StringUtil.isNotBlank(certDN)) {
			Cert c = new Cert();
			c.setCertDn(certDN);
			seal.setCert(c);
		}
		Company com = companyDao.getCompany(cid);
		String treeID = com.getTreeId();

		int total = sealDao.searchTotal(seal, treeID);

		int start = page.getStart();
		int end = page.getEnd();
		List<Seal> sealList = sealDao.searchByPage(seal, start, end, treeID);
		List<SealVO> sealVOList = new ArrayList<SealVO>();
		for (Seal s : sealList) {
			SealVO sVO = new SealVO();
			BeanUtils.copyProperties(s, sVO);
			// 校验信息
			isModify(s, sVO);

			User user = s.getUser();
			if (user != null) {
				sVO.setUserName(user.getName());
				Company company = user.getCompany();
				if (company != null)
					sVO.setCompanyName(company.getName());

			}

			Cert cert = s.getCert();
			if (cert != null)
				sVO.setCertDn(cert.getCertDn());

			sealVOList.add(sVO);
		}
		page.setTotalNo(total);
		page.setResult(sealVOList);
		return page;
	}

	/**
	 * 获取印章数量
	 * 
	 * @return
	 */
	public int getSealCount(Long startTime) {
		return sealDao.getSealCount(startTime);
	}

	/**
	 * 获取制发印章的用户数量
	 * 
	 * @return
	 */
	public int getSealUserCount() {
		return sealDao.getSealUserCount();
	}

	/**
	 * 校验印章数据信息
	 * 
	 * @param userCertDN
	 * @param sealId
	 * @param sysUserId
	 * @return
	 * @throws Exception
	 */
	public SealVO sealDownLoadVerfy(String userCertDN, Long sealId, Long sysUserId) throws Exception {
		Seal seal = sealDao.getSeal(sealId);
		if (seal == null)
			throw new WebDataException("印章不存在");

		// 校验信息
		isModify(seal, "所选印章" + seal.getName(), "Seal which id is " + sealId + " and name is " + seal.getName());

		Integer isDownload = seal.getIsDownload();// 是否支持下载
		if (isDownload == null || isDownload == 0)
			throw new WebDataException("印章不支持下载");

		Integer isAuthCertDownload = seal.getIsAuthCertDownload();
		if (isAuthCertDownload != null && isAuthCertDownload == 1) {// 下载验证证书
			Cert cert = certDao.getCert(seal.getCertId());
			if (cert.getCertDn() == null)
				throw new WebDataException("签章人证书不存在");

			// 校验证书信息
			isModify(cert, "生成印章的证书 " + cert.getCertDn(), "Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());

			if (!cert.getCertDn().equals(userCertDN))
				throw new WebDataException("签章人证书DN不匹配");

			try {
				byte[] certData = certService.getCertData(cert.getCertPath(), cert.getCertDataId());
				// 1.验证签章人证书
				certChainService.verifyCert(userCertDN, certData);
			} catch (WebDataException e) {
				throw new WebDataException("签章人" + e.getMessage());
			}
			// 2.管理员证书
			List<Cert> sysUserCertList = certDao.getCertBySysUserId(sysUserId);
			if (sysUserCertList == null || sysUserCertList.size() == 0)
				throw new WebDataException("管理员证书不存在");

			Cert sysUserCert = sysUserCertList.get(0);
			// 校验信息
			isModify(sysUserCert, "当前管理员证书 " + sysUserCert.getCertDn(), "Cert which id is " + sysUserCert.getId() + " and cert_dn is " + cert.getCertDn());

			try {
				byte[] sysUserCertData = certService.getCertData(sysUserCert.getCertPath(), sysUserCert.getCertDataId());
				certChainService.verifyCert(sysUserCertData);
			} catch (WebDataException e) {
				throw new WebDataException("管理员" + e.getMessage());
			}
		}
		SealVO sealVO = new SealVO();
		BeanUtils.copyProperties(seal, sealVO);
		return sealVO;
	}

	/**
	 * 获取印章数据
	 * 
	 * @param sealPath
	 * @param sealDataId
	 * @return
	 * @throws Exception
	 */
	public byte[] getSealData(String sealPath, Long sealDataId) throws Exception {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(sealPath)) {
			data = FileUtil.getFile(sealPath);
		} else {
			SealData sealData = sealDataDao.getSealData(sealDataId);
			if (sealData == null)
				throw new WebDataException("数据库中印章数据不存在");

			// 校验印章数据信息
			isModify(sealData, "印章数据id为 : " + sealData.getId(), "SealData which id is " + sealDataId);

			data = sealData.getData();
			FileUtil.storeFile(sealPath, data);
		}

		return data;
	}

	/**
	 * PDF批签
	 * 
	 * @param ids
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void pdfBatchStamp(String ids, String name, String type, String keyWord, String X, String Y, String align) throws Exception {

		// 获取印章
		Seal s = new Seal();
		s.setName(name);
		List<Seal> sealList = sealDao.getSeals(s);
		if (sealList.size() == 0)
			throw new WebDataException("印章不存在");
		if (sealList.size() > 1)
			throw new WebDataException("印章不唯一");
		Seal seal = sealList.get(0);
		// 校验印章数据完整性
		isModify(seal, "印章", "Seal which id is " + seal.getId() + " and name is " + name);

		long currentTime = System.currentTimeMillis();
		long notbefor = seal.getNotBefor();
		long notafter = seal.getNotAfter();
		int usedLimit = seal.getUsedLimit();
		int usedCount = seal.getUsedCount();
		int status = seal.getStatus();

		// 是否过期
		if (currentTime < notbefor || currentTime > notafter)
			throw new WebDataException("印章已过期");

		// 盖章次数
		if (usedLimit == -1 || (usedCount >= usedLimit && usedLimit != 0))
			throw new WebDataException("印章使用次数超限不能盖章");

		// 印章状态
		if (status != 1)
			throw new WebDataException("印章状态未停用");

		// 获取指定签名密钥
		ConfigUtil config = ConfigUtil.getInstance();
		long signKeyId = config.getSignKeyId();
		if (signKeyId == -1)
			throw new WebDataException("未指定签名密钥");

		KeyVO signKey = keyService.getKey(signKeyId);
		if (signKey == null)
			throw new WebDataException("签名密钥数据在库中不存在");

		// 校验数据完整性
		if (!keyService.isModify(signKeyId))
			throw new WebDataException("指定签名密钥数据被篡改");

		// 初始化服务器证书数据 获取密钥数据
		Key key = keyDao.getKey(signKeyId);
		byte[] signKeyData = keyService.getKeyData(key);

		// 校验服务器证书
		certChainService.verifyCert(FileUtil.getFile(key.getCertPath()));

		// 印章图片
		byte[] photoData = photoDataService.getPhotoData(seal.getPhotoPath(), seal.getPhotoDataId());
		byte[] sealData = getSealData(seal.getSealPath(), seal.getSealDataId());
		byte[] keyCertData = FileUtil.getFile(key.getCertPath());

		// 获取根证集合
		Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();
		// 获取服务器加密卡签名证书
		List<Key> signKeyList = keyService.getCardKeys();

		byte[] pdfDataBs;
		String[] temp = ids.split(Constants.SPLIT_2);
		if (temp.length > 0) {
			for (int i = 0; i < temp.length; i++) {
				String dirPath = Constants.PDF_PATH + temp[i]; // 每个文件夹
				String[] listFileName = FileUtil.listFileName(dirPath);
				// 是否已存在文件夹
				String oldStampPath = Constants.PDF_STAMP_PATH + temp[i] + "_stamp";
				File stampedFile = new File(oldStampPath);
				if (stampedFile.exists())
					FileUtil.deleteDir(oldStampPath);

				for (String pdfPath : listFileName) { // 每个PDF文件
					try {
						String sf = Constants.SPOT + FileUtil.getFileSuffix(pdfPath);
						if (sf.equals(Constants.PDF_SUFFIX)) {
							pdfDataBs = FileUtil.getFile(dirPath + Constants.SPLIT_DIR + pdfPath);
							String base64PD = StringUtil.base64Encode(pdfDataBs);
							if (base64PD.length() > Constants.LENGTH_3MB_B64)
								throw new WebDataException("PDF文件大于3MB");

							byte[] pdfDataStampBs = null;
							if (type.equals("1")) { // 关键字盖章
								pdfDataStampBs = PdfStampUtil.pdfStampByText(pdfDataBs, photoData, keyWord, "", key, signKeyData, keyCertData, sealData, rootHt, signKeyList);
							} else if (type.equals("2")) { // 坐标盖章
								pdfDataStampBs = PdfStampUtil.pdfStampByCoordinate(pdfDataBs, photoData, Float.parseFloat(X), Float.parseFloat(Y), "", key, signKeyData, keyCertData, sealData, rootHt,
										signKeyList);
							} else if (type.equals("3")) // 骑缝章
								pdfDataStampBs = PdfStampUtil.pdfStampByQfz(pdfDataBs, photoData, "", key, signKeyData, keyCertData, sealData, rootHt, signKeyList, Integer.parseInt(align));

							else
								throw new WebDataException("未指定盖章方式");

							pdfPath = pdfPath.substring(0, pdfPath.lastIndexOf(Constants.SPOT));

							String stampPdfPath = oldStampPath + Constants.SPLIT_DIR + pdfPath + "_stamp.pdf";
							FileUtil.storeFile(stampPdfPath, pdfDataStampBs);
						}
					} catch (Exception e) {
						LoggerUtil.errorlog(pdfPath + "stamp error, " + e.getMessage());
						FileUtil.appendLog(oldStampPath + Constants.SPLIT_DIR + "stamp_error.log", pdfPath + " stamp error, " + e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * OFD文件批
	 * 
	 * @param ids
	 * @param name
	 * @param type
	 * @param keyWord
	 * @param X
	 * @param Y
	 * @param align
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void ofdBatchStamp(String ids, String name, String type, String keyWord, String X, String Y, String align) throws Exception {

		// 获取印章
		Seal s = new Seal();
		s.setName(name);
		List<Seal> sealList = sealDao.getSeals(s);
		if (sealList.size() == 0)
			throw new WebDataException("印章不存在");
		if (sealList.size() > 1)
			throw new WebDataException("印章不唯一");
		Seal seal = sealList.get(0);
		// 校验印章数据完整性
		isModify(seal, "印章", "Seal which id is " + seal.getId() + " and name is " + name);

		long currentTime = System.currentTimeMillis();
		long notbefor = seal.getNotBefor();
		long notafter = seal.getNotAfter();
		int usedLimit = seal.getUsedLimit();
		int usedCount = seal.getUsedCount();
		int status = seal.getStatus();

		// 是否过期
		if (currentTime < notbefor || currentTime > notafter)
			throw new WebDataException("印章已过期");

		// 盖章次数
		if (usedLimit == -1 || (usedCount >= usedLimit && usedLimit != 0))
			throw new WebDataException("印章使用次数超限不能盖章");

		// 印章状态
		if (status != 1)
			throw new WebDataException("印章状态未停用");

		// 获取指定签名密钥
		ConfigUtil config = ConfigUtil.getInstance();
		long signKeyId = config.getSignKeyId();
		if (signKeyId == -1)
			throw new WebDataException("未指定签名密钥");

		KeyVO signKey = keyService.getKey(signKeyId);
		if (signKey == null)
			throw new WebDataException("签名密钥数据在库中不存在");

		// 校验数据完整性
		if (!keyService.isModify(signKeyId))
			throw new WebDataException("指定签名密钥数据被篡改");

		// 初始化服务器证书数据 获取密钥数据
		Key key = keyDao.getKey(signKeyId);
		byte[] signKeyData = keyService.getKeyData(key);

		// 校验服务器证书
		certChainService.verifyCert(FileUtil.getFile(key.getCertPath()));

		// 印章图片
		byte[] photoData = photoDataService.getPhotoData(seal.getPhotoPath(), seal.getPhotoDataId());
		byte[] sealData = getSealData(seal.getSealPath(), seal.getSealDataId());
		byte[] keyCertData = FileUtil.getFile(key.getCertPath());

		// 获取根证集合
		Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();
		// 获取服务器加密卡签名证书
		List<Key> signKeyList = keyService.getCardKeys();

		byte[] ofdDataBs;
		String[] temp = ids.split(Constants.SPLIT_2);
		if (temp.length > 0) {
			for (int i = 0; i < temp.length; i++) {
				String dirPath = Constants.OFD_PATH + temp[i]; // 每个文件夹
				String[] listFileName = FileUtil.listFileName(dirPath);
				// 是否已存在文件夹
				String oldStampPath = Constants.OFD_STAMP_PATH + temp[i] + "_stamp";
				File stampedFile = new File(oldStampPath);
				if (stampedFile.exists())
					FileUtil.deleteDir(oldStampPath);

				for (String ofdPath : listFileName) { // 每个PDF文件
					try {
						String sf = Constants.SPOT + FileUtil.getFileSuffix(ofdPath);
						if (sf.equals(Constants.OFD_SUFFIX)) {
							ofdDataBs = FileUtil.getFile(dirPath + Constants.SPLIT_DIR + ofdPath);
							String base64PD = StringUtil.base64Encode(ofdDataBs);
							if (base64PD.length() > Constants.LENGTH_3MB_B64)
								throw new WebDataException("PDF文件大于3MB");

							byte[] ofdStampData = null;
							try {
								if (type.equals("1")) { // 关键字盖章
									ofdStampData = OfdStampUtil.ofdStampByText(ofdDataBs, keyWord, "", key, signKeyData, keyCertData, sealData, rootHt, signKeyList);
								} else if (type.equals("2")) { // 坐标盖章
									ofdStampData = OfdStampUtil.ofdStampByCoordinate(ofdDataBs, photoData, Integer.parseInt(X), Integer.parseInt(Y), "", key, signKeyData, keyCertData, sealData,
											rootHt, signKeyList);
								} else if (type.equals("3")) {// 骑缝章
									ofdStampData = OfdStampUtil.ofdStampByQfz(ofdDataBs, "", key, signKeyData, keyCertData, sealData, rootHt, signKeyList, Integer.parseInt(align));
								} else
									throw new WebDataException("未指定盖章方式");
							} catch (Exception e) {
								throw new Exception(e.getMessage());
							}

							ofdPath = ofdPath.substring(0, ofdPath.lastIndexOf(Constants.SPOT));
							String stampPdfPath = oldStampPath + Constants.SPLIT_DIR + ofdPath + "_stamp.ofd";
							FileUtil.storeFile(stampPdfPath, ofdStampData);

						}
					} catch (Exception e) {
						LoggerUtil.errorlog(ofdPath + "stamp error, " + e.getMessage());
						FileUtil.appendLog(oldStampPath + Constants.SPLIT_DIR + "stamp_error.log", ofdPath + " stamp error, " + e.getMessage());
					}
				}
			}
		}
	}

}
