package cn.com.infosec.netseal.webserver.service.request;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.audit.AuditDaoImpl;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.company.CompanyDaoImpl;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.dao.request.RequestDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.sealData.SealDataDaoImpl;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserDaoImpl;
import cn.com.infosec.netseal.common.dao.userTemplate.UserTemplateDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.po.Request;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.SealData;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.po.UserTemplate;
import cn.com.infosec.netseal.common.entity.vo.RequestVO;
import cn.com.infosec.netseal.common.entity.vo.TemplateVO;
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
import cn.com.infosec.netseal.webserver.util.CommonUtil;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.TreeIdUtil;

@Service
public class RequestServiceImpl extends BaseService {

	@Autowired
	protected RequestDaoImpl requestDao;
	@Autowired
	protected AuditDaoImpl auditDao;
	@Autowired
	private SealDaoImpl sealDao;
	@Autowired
	protected TemplateDaoImpl templateDao;
	@Autowired
	private CompanyDaoImpl companyDao;
	@Autowired
	private UserDaoImpl userDao;
	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private PhotoDataDaoImpl photoDataDao;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	protected UserTemplateDaoImpl userTemplateDao;
	@Autowired
	private KeyDaoImpl keyDao;
	@Autowired
	private SealDataDaoImpl sealDataDao;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;

	/**
	 * 添加印章申请
	 * 
	 * @param requestVO
	 * @param sysUserId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertRequest(RequestVO requestVO, Long sysUserId, String userId,byte[] fileByte) throws Exception {
		User user = userDao.getUser(Long.parseLong(userId));
		if (user == null)
			throw new WebDataException("获取签章人信息失败");

		// 校验用户信息是否被篡改
		isModify(user, "所选签章人 " + user.getName(), "User which id is " + user.getId() + " and name is " + user.getName());

		Company comp = companyDao.getCompany(user.getCompanyId());
		// 校验单位信息
		isModify(comp, "签章人所属单位 " + comp.getName(), "Company which id is " + comp.getId() + " and name is " + comp.getName());

		Template template = templateDao.getTemplate(requestVO.getTemplateId());
		if (template == null)
			throw new WebDataException("印模不存在");

		// 校验印模信息
		isModify(template, "所选印模 " + template.getName(), "Template which id is " + template.getId() + " and name is " + template.getName());

		long nowTime = DateUtil.getCurrentTime();
		if (nowTime > template.getNotAfter())
			throw new WebDataException("印模已过期");

		long before = CommonUtil.timeStrStart(requestVO.getNotBeforCn());
		long after = CommonUtil.timeStrEnd(requestVO.getNotAfterCn());
		if (before < template.getNotBefor() || after > template.getNotAfter())
			throw new WebDataException("印章起止时间不在印模起止时间内");

		Request tempReq = new Request();
		tempReq.setCertId(requestVO.getCertId());
		List<Request> requestList = requestDao.getRequests(tempReq);
		if (requestList.size() > 0)
			throw new WebDataException("该证书存在印章申请,正在审核中");

		Seal tempSeal = new Seal();
		tempSeal.setCertId(requestVO.getCertId());
		List<Seal> sealList = sealDao.getSeals(tempSeal);
		if (sealList != null && sealList.size() > 0)
			throw new WebDataException("该证书已申请生成印章");

		Cert cert = certDao.getCert(requestVO.getCertId());
		if (cert.getCertDn() == null)
			throw new WebDataException("签章人证书不存在");

		// 校验签章人证书信息
		isModify(cert, "所选证书 " + cert.getCertDn(), "Cert which id is " + cert.getId() + " and cert_dn is " + cert.getCertDn());

		String userCertPath = cert.getCertPath();
		long currentTime = DateUtil.getCurrentTime();
		Long photoDataId = template.getPhotoDataId();
		int width = 0;
		int height = 0;
		PhotoData photoData = new PhotoData();
		String photoPath = "";
		byte[] photoDataBs = null;
		if(!photoDataId.equals(-1L)) {
			if(fileByte == null) {  
				  photoDataBs = photoDataService.getPhotoData(template.getPhotoPath(), photoDataId);
				}else {
				  photoDataBs =	fileByte;
				}
			// 保存图片数据
			photoPath = Constants.PHOTO_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.PHOTO_SUFFIX;
			photoData.setGenerateTime(currentTime);
			photoData.setData(photoDataBs);
			photoData.setUpdateTime(currentTime);
			Long photoId = photoDataDao.insertPhotoData(photoData);
			requestVO.setPhotoDataId(photoId);
			requestVO.setPhotoPath(photoPath);
			
			try {
				List list = FileUtil.getImagePro(photoDataBs);
				width = (int) list.get(1);
				height = (int) list.get(2);
			} catch (Exception e) {
				throw new WebDataException("读取印章图片出错");
			}
		} else {
			requestVO.setPhotoDataId(-1L);
			requestVO.setPhotoPath("");
		}
		requestVO.setNotBefor(before);
		requestVO.setNotAfter(after);
		// 审核
		if (template.getIsAuditReq() != null && template.getIsAuditReq() == 0) { // 申请不需要审核,生成印章
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

			Integer isAuthCertGenSeal = template.getIsAuthCertGenSeal();// 审核是否验证书
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
			
			String sealPath = Constants.SEAL_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.SEAL_SUFFIX;
			try {
				// 1. 产生印章数据
				Seal seal = new Seal();
				BeanUtils.copyProperties(requestVO, seal);
				seal.setStatus(1);
				seal.setType(template.getType());
				seal.setIsDownload(template.getIsDownload());
				seal.setPhotoHigh(height);
				seal.setPhotoWidth(width);
				seal.setIsAuthCertDownload(template.getIsAuthCertDownload());
				seal.setIsAuditReq(template.getIsAuditReq());
				seal.setIsAuthCertGenSeal(template.getIsAuthCertGenSeal());
				if(fileByte == null) {
					seal.setTransparency(template.getTransparency());
				}else {
					seal.setTransparency(requestVO.getTransparency());
				}
				seal.setSealPath(sealPath);
				seal.setUpdateTime(DateUtil.getCurrentTime());
				seal.setPhotoData(photoData);

				// 2. 生成印章
				List<byte[]> certDataList = keyService.getSignCertData();
				byte[] data = GenSealUtil.genSealData(seal, certDataList, key, keyData, serverCertData, userCertData, photoDataBs, ConfigUtil.getInstance().getGmOid().getBytes());

				// 3.生成印章数据记录
				SealData sealData = new SealData();
				sealData.setGenerateTime(seal.getGenerateTime());
				sealData.setData(data);
				sealData.setUpdateTime(seal.getUpdateTime());
				Long sealId = sealDataDao.insertSealData(sealData);

				// 4.保存印章
				seal.setSealDataId(sealId);
				seal.setAuditId(Long.valueOf(-1));
				seal.setUsedCount(0);
				seal.setDownloadTime(0L);
				seal.setSysUserId(-1L);
				seal.setUserId(cert.getUserId());
				sealDao.insertSeal(seal);
				// 5. 保存图片文件
				if(StringUtil.isNotBlank(photoPath))
					FileUtil.storeFile(photoPath, photoDataBs);
			} catch (Exception e) {
				FileUtil.deleteFile(sealPath);
				if(StringUtil.isNotBlank(photoPath))
					FileUtil.deleteFile(photoPath);
				LoggerUtil.errorlog("gen seal error", e);
				throw new WebDataException("生成印章失败");
			}

		} else {
			// 需要审核
			Request request = new Request();
			BeanUtils.copyProperties(requestVO, request);
			if(fileByte == null) {
				request.setTransparency(template.getTransparency());
			}
			requestDao.insertRequest(request);
		}
	}

	/**
	 * 获取印章申请信息
	 * 
	 * @param id
	 * @return
	 */
	public RequestVO getRequest(Long id) {
		Request request = requestDao.getRequest(id);
		RequestVO requestVO = new RequestVO();
		BeanUtils.copyProperties(request, requestVO);
		return requestVO;
	}

	/**
	 * 删除未审核的印章记录
	 * 
	 * @param ids
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteRequest(String ids) throws Exception {
		List<String> photoPathList = new ArrayList<String>();
		if (ids == null || "".equals(ids.trim())) {
			return;
		}
		String[] temp = ids.split(",");
		if (temp.length > 0) {
			ids = temp[0];
			for (int i = 0; i < temp.length; i++) {
				Long id = Long.parseLong(temp[i].trim());

				Request request = requestDao.getRequest(id);
				int r = requestDao.deleteRequest(id);
				if (r == 0)
					throw new WebDataException("操作失败");
				if(!request.getPhotoDataId().equals(-1L)) {
					photoPathList.add(request.getPhotoPath());
					photoDataDao.deletePhotoData(request.getPhotoDataId());
				}
			}
		}

		for (String photoPath : photoPathList)
			FileUtil.deleteFile(photoPath);

	}

	/**
	 * 修改印章申请记录信息
	 * 
	 * @param requestVO
	 * @throws Exception
	 */
	public void updateRequest(RequestVO requestVO) throws Exception {
		Request request = new Request();
		BeanUtils.copyProperties(requestVO, request);
		int r = requestDao.updateRequest(request);
		if (r == 0)
			throw new WebDataException("更新失败");

	}

	/**
	 * 查询集合
	 * 
	 * @param requestVO
	 * @return
	 */

	public List<RequestVO> getRequests(RequestVO requestVO) {
		Request request = new Request();
		BeanUtils.copyProperties(requestVO, request);
		List<Request> rList = requestDao.getRequests(request);
		List<RequestVO> rVOList = new ArrayList<RequestVO>();
		for (Request r : rList) {
			RequestVO reqVO = new RequestVO();
			BeanUtils.copyProperties(r, reqVO);
			rVOList.add(reqVO);
		}
		return rVOList;

	}

	/**
	 * 查询申请印章集合
	 * 
	 * @param ids
	 * @return
	 */
	public List<RequestVO> getRequest(String ids) {
		if (ids == null || "".equals(ids.trim())) {
			return new ArrayList<RequestVO>();
		}
		String[] temp = ids.split(",");
		if (temp.length > 0) {
			ids = temp[0];
			for (int i = 1; i < temp.length; i++) {
				ids += "," + temp[i];
			}
		}
		List<Request> rList = requestDao.getRequests(ids);
		List<RequestVO> rVOList = new ArrayList<RequestVO>();
		for (Request r : rList) {
			RequestVO requestVO = new RequestVO();
			BeanUtils.copyProperties(r, requestVO);
			rVOList.add(requestVO);
		}
		return rVOList;
	}

	/**
	 * 分特显示印章申请信息
	 * 
	 * @param page
	 * @param requestVO
	 * @return
	 * @throws Exception
	 */
	public Page<RequestVO> searchRequest(Page<RequestVO> page, RequestVO requestVO, Long cid) throws Exception {
		Request request = new Request();
		BeanUtils.copyProperties(requestVO, request);
		Company com = companyDao.getCompany(cid);
		String treeID = com.getTreeId();
		int total = requestDao.searchTotal(request, treeID);

		int start = page.getStart();
		int end = page.getEnd();
		List<Request> list = requestDao.searchByPage(request, start, end, treeID);
		List<RequestVO> rVOList = new ArrayList<RequestVO>();
		for (Request r : list) {
			RequestVO rVO = new RequestVO();
			BeanUtils.copyProperties(r, rVO);

			// 校验信息
			isModify(r, rVO);

			// 签章人信息
			User u = r.getUser();
			if (u != null)
				rVO.setUserName(u.getName());

			// 单位信息
			// Template t = templateDao.getTemplate(r.getTemplateId());
			if (u != null) {
				Company c = companyDao.getCompany(u.getCompanyId());
				if (c != null)
					rVO.setCompanyName(c.getName());
			}

			rVOList.add(rVO);
		}
		page.setTotalNo(total);
		page.setResult(rVOList);
		return page;
	}

	/**
	 * 根据印模id查询印章申请
	 * 
	 * @param templateId
	 * @return
	 */
	public List<RequestVO> requestList(Long templateId) {
		Request request = new Request();
		request.setTemplateId(templateId);
		List<Request> list = requestDao.getRequests(request);
		List<RequestVO> rVOList = new ArrayList<RequestVO>();

		for (Request r : list) {
			RequestVO rVO = new RequestVO();
			BeanUtils.copyProperties(r, rVO);
			rVOList.add(rVO);
		}
		return rVOList;
	}

	/**
	 * 查询用户有权限的印模
	 * 
	 * @param curUserId
	 * @return
	 * @throws Exception
	 */
	public List<TemplateVO> getRequestTemplate(Long curUserId, Long companyId) throws Exception {
		List<TemplateVO> rlist = new ArrayList<TemplateVO>();
		if (companyId == -1) {
			User user = userDao.getUser(curUserId);
			if (user == null)
				return rlist;

			companyId = user.getCompanyId();
		}
		// 用户所属公司
		Company company = companyDao.getCompany(companyId);
		if (company == null)
			return rlist;

		// 可制作印章的印模 status=1
		List<String> treeIdList = TreeIdUtil.pTreeIdList(company.getTreeId());
		List<Template> list = templateDao.getUserTemplate(treeIdList);
		List<UserTemplate> utlist = userTemplateDao.getUserTemplate2(curUserId);

		// 找出用户有权限的印模
		if (list != null && list.size() > 0) {
			for (Template t : list) {
				for (UserTemplate ut : utlist) {
					Long userId = ut.getUserId();
					if (ut.getTemplateId().equals(t.getId()) && (userId.equals(curUserId) || userId == 0)) {
						TemplateVO tVo = new TemplateVO();
						BeanUtils.copyProperties(t, tVo);
						// 校验印模信息是否被篡改
						if (isModify(t))
							rlist.add(tVo);

						break;
					}
				}
			}
		}

		return rlist;
	}

	/**
	 * 读取申请图片,本地不存在,从数据库读取,存储到本地
	 * 
	 * @param requestId
	 * @return 申请图片数据
	 * @throws Exception
	 */
	public byte[] getRequestPhoto(Long requestId) {
		byte[] data = new byte[0];
		try {
			Request request = requestDao.getRequest(requestId);
			String photoPath = request.getPhotoPath();

			if (FileUtil.checkPath(photoPath)) {
				data = FileUtil.getFile(photoPath);
			} else {
				Long photoId = request.getPhotoDataId();
				PhotoData photoData = photoDataDao.getPhotoData(photoId);
				// 校验信息
				isModify(photoData, "图片数据表id为" + photoData.getId(), "PhotoData which id is " + photoData.getId());

				data = photoData.getData();
				FileUtil.storeFile(photoPath, data);
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("查看申请图片错误", e);
			return data;
		}
		return data;
	}

}
