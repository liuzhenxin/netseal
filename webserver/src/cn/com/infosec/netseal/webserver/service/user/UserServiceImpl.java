package cn.com.infosec.netseal.webserver.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.company.CompanyDaoImpl;
import cn.com.infosec.netseal.common.dao.count.CountDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.request.RequestDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.entity.po.Request;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.vo.SealVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.entity.vo.UserVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.TreeNode;

@Service
public class UserServiceImpl extends BaseService {
	@Autowired
	protected UserDaoImpl userDao;
	@Autowired
	protected CompanyDaoImpl companyDao;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private CertDaoImpl certDao;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	protected RequestDaoImpl requestDao;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	CountDaoImpl countDao;
	@Autowired
	private RequestDaoImpl requestDaoImpl;
	@Autowired
	private SealDaoImpl sealDaoImpl;
	@Autowired
	private HttpSession httpSession;
	

	/**
	 * 增加签章人
	 * 
	 * @param user
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertUser(UserVO userVO) throws Exception {
		
			User user = new User();
			BeanUtils.copyProperties(userVO, user);

			Company com = companyDao.getCompany(userVO.getCompanyId());
			
			// 校验单位信息
			isModify(com, "所选单位" + com.getName(), "Company which id is " + com.getId() + " and name is " + com.getName());
			
			user.setPassword(CryptoHandler.hashEnc64(Constants.SYS_USER_DEFAULT_PWD));// 新添加的用户默认密码：123456
			user.setGenerateTime(DateUtil.getCurrentTime());
			user.setUpdateTime(DateUtil.getCurrentTime());
			userDao.insertUser(user);

			// 注册证书
			if (StringUtil.isNotBlank(userVO.getCertDn())) {
				long limit = ConfigUtil.getInstance().getCheckCertNumLimit();
				Count count = new Count();
				count.setName(Constants.CERT_NUM);
				count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
				List<Count> cList = countDao.getCount(count);
				if (cList.size() == 0)
					throw new WebDataException("计数表数据不存在");
				if (cList.size() >= 2)
					throw new WebDataException("计数表数据不唯一");

				Count c = cList.get(0);
				// 校验证书统计表信息
				isModify(c, "证书计数表中" + c.getName(), "Count which name is " + c.getName());
				String certSn = userVO.getCertSn();
				String certenSn = userVO.getCertenSn();
				int certNUM = 0;
				if(StringUtil.isNotBlank(certSn)) 
					certNUM +=1;
				if(StringUtil.isNotBlank(certenSn)) 
					certNUM +=1;
				
				if ((c.getNum() + certNUM) <= limit) {
					String certDn = userVO.getCertDn();
					// 证书DN长度限制
					byte[] bytes = certDn.getBytes();
					if (bytes.length > 300)
						throw new WebDataException("证书主题长度不超过300个字节");
		
					List<Cert> tempCertList = certDao.getCert(certDn);
					if (tempCertList.size() > 0)
						throw new WebDataException("存在相同的证书主题,不允许注册");
					
					if(StringUtil.isNotBlank(certSn) && StringUtil.isNotBlank(certenSn) && certSn.equals(certenSn)) {
						//签名证书密钥用法
						byte[] data = userVO.getX509Cert().getBytes();
						X509CertEnvelope cert = CertUtil.parseCert(data);
						boolean[] usage = cert.getKeyUsage();
						//加密证书密钥用法
						byte[] dataS = userVO.getX509Certen().getBytes();
						X509CertEnvelope certen = CertUtil.parseCert(dataS);
						boolean[] usageen = certen.getKeyUsage();
						
						if(Arrays.equals(usage,usageen)) 
							certToDB(user, data, certSn, certDn, limit, c, Constants.USAGE_SIGN_ENC);
					} else {
						if (StringUtil.isNotBlank(certSn)) {
							byte[] data = userVO.getX509Cert().getBytes();
							certToDB(user, data, certSn, certDn, limit, c, Constants.USAGE_SIGNATURE);
						}
						if (StringUtil.isNotBlank(certenSn)) {
							byte[] dataS = userVO.getX509Certen().getBytes();
							certToDB(user, dataS, certenSn, certDn, limit, c, Constants.USAGE_ENCRYPT);
						}
					}
		
				} else {
					throw new WebDataException("证书数量超限,无法注册");
				}
		}

	}

	/**
	 * 证书存入数据库
	 * 
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void certToDB(User user, byte[] data, String certSn, String certDn, long limit, Count c, Integer certUsage) throws Exception {
		// 验证证书
		if(certUsage.equals(1)) {
			certChainService.verifyCert(data, true, false);
		}else if(certUsage.equals(2)) {
			certChainService.verifyCert(data, false, true);
		}else {
			certChainService.verifyCert(data, true, true);
		}

		CertData certData = new CertData();
		certData.setData(data);
		certData.setGenerateTime(user.getGenerateTime());
		certData.setUpdateTime(user.getUpdateTime());
		Long certDataId = certDataDao.insertCertData(certData);

		String certPath = Constants.CERT_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.CERT_SUFFIX;
		Cert cert = new Cert();
		cert.setCertDn(certDn);
		cert.setCertSn(certSn);
		cert.setCertUsage(certUsage);
		cert.setCertPath(certPath);
		cert.setNotBefor(0L);
		cert.setNotAfter(0L);
		cert.setCertDataId(certDataId);
		cert.setGenerateTime(user.getGenerateTime());
		cert.setUpdateTime(user.getUpdateTime());
		cert.setUserId(user.getId());
		cert.setSysUserId(-1L);
		cert.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
		certDao.insertCert(cert);

		c.setNum(c.getNum() + 1);
		int n = countDao.updateCountPlus(c, limit);
		if (n == 0)
			throw new WebDataException("证书数量超限,无法注册");

		// 将证书写入到目录文件中
		FileUtil.storeFile(certPath, data);
	}

	/**
	 * 删除用户
	 * 
	 * @param ids
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteUsers(String ids) throws Exception {
		List<Cert> certList = new ArrayList<Cert>();
		String idList[] = ids.split(Constants.SPLIT_1);
		int deleteCertNum = 0;
		for (int i = 0; i < idList.length; i++) {
			Long userId = Long.valueOf(idList[i].trim());
			// 正在申请印章 不能删除
			Request tempReq = new Request();
			tempReq.setUserId(userId);
			List<Request> requestList = requestDao.getRequests(tempReq);

			if (requestList.size() > 0)
				throw new WebDataException("用户正在申请印章,不允许删除");

			// 删除印章
			List<SealVO> list = sealService.getSealByUserId(userId);
			if (!list.isEmpty()) {
				String sealIds = "";
				for (int j = 0; j < list.size(); j++) {
					SealVO seal = list.get(j);
					sealIds += seal.getId() + ",";
				}
				/*
				 * SealVO seal = list.get(list.size() - 1); sealIds += seal.getId();
				 */

				sealService.deleteSeal(sealIds);

			}

			int r = userDao.deleteUser(userId);
			if (r == 0) {
				throw new WebDataException("操作失败");
			}
			// 增加删除记录
			idDeleteDao.insertIDDelete(userId, Constants.TABLE_SEAL_USER);
			certList = certDao.getCertByUserId(userId);
			// 删除证书
			for (Cert cert : certList) {
				certDao.deleteCert(cert.getId());
				certDataDao.deleteCertData(cert.getCertDataId());
				// 增加删除记录
				idDeleteDao.insertIDDelete(cert.getId(), Constants.TABLE_SEAL_CERT);
				idDeleteDao.insertIDDelete(cert.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
				deleteCertNum++;
			}
		}
		// 更新证书计数
		Count count = new Count();
		count.setName(Constants.CERT_NUM);
		count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
		List<Count> cList = countDao.getCount(count);
		count = cList.get(0);
		count.setNum(count.getNum() - deleteCertNum);
		countDao.updateCountMinus(count);

		// 删除证书文件
		for (Cert cert : certList)
			FileUtil.deleteFile(cert.getCertPath());

	}

	/**
	 * 修改用户--整合
	 * 
	 * @param sys
	 * @return
	 * @throws WebDataException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateUser(UserVO user) throws Exception {
		User u = userDao.getUser(user.getId());
		// 校验信息
		isModify(u, "所选签章人" + u.getName(), "User which id is " + u.getId() + " and name is " + u.getName());

		long limit = ConfigUtil.getInstance().getCheckCertNumLimit();
		Count count = new Count();
		count.setName(Constants.CERT_NUM);
		count.setLocation(ConfigUtil.getInstance().getTablePrefixId());

		// 校验证书计数表信息
		List<Count> cList = countDao.getCount(count);
		if (cList.size() == 0)
			throw new WebDataException("计数表数据不存在");
		if (cList.size() >= 2)
			throw new WebDataException("计数表数据不唯一");

		Count c = cList.get(0);
		// 校验证书统计表信息
		isModify(c, "证书计数表中" + c.getName(), "Count which name is " + c.getName());

		if (StringUtil.isNotBlank(user.getName()))
			u.setName(user.getName());
		if (StringUtil.isNotBlank(user.getPassword()))
			u.setPassword(user.getPassword());
		if (StringUtil.isNotBlank(user.getEmail()))
			u.setEmail(user.getEmail());
		if (StringUtil.isNotBlank(user.getPhone()))
			u.setPhone(user.getPhone());
		if (null != user.getCompanyId()) {
			Company comp = companyDao.getCompany(user.getCompanyId());
			isModify(comp, "所选单位 " + comp.getName(), "Company which id is " + comp.getId() + " and name is " + comp.getName());

			u.setCompanyId(user.getCompanyId());
		}
		u.setUpdateTime(DateUtil.getCurrentTime());

		// 如果存在证书DN,注册证书
		if (StringUtil.isNotBlank(user.getCertDn())) {
			String certDn = user.getCertDn();
			String certSn = user.getCertSn();
			String certenSn = user.getCertenSn();

			// 证书DN长度限制
			byte[] bytes = certDn.getBytes();
			if (bytes.length > 300)
				throw new WebDataException("证书主题长度不超过300个字节");

			List<Cert> tempCertList = null;
			
			tempCertList = certDao.getCert(certDn);
			if (tempCertList.size() >= 2) 
				throw new WebDataException("证书主题为" + certDn + "的证书数据在库中不唯一");
			
			if (tempCertList.size() == 1 && !u.getId().equals(tempCertList.get(0).getUserId())) 
				throw new WebDataException("证书主题为" + certDn + "的证书已被他人注册,注册前需先删除该主题证书！");
			
			if (tempCertList.size() == 1 && u.getId().equals(tempCertList.get(0).getUserId())) 
				throw new WebDataException("该用户已注册证书主题为" + certDn + "的证书,注册前需先删除该主题证书！");

			if(StringUtil.isNotBlank(certSn)&&StringUtil.isNotBlank(certenSn)&&certSn.equals(certenSn)) {
				//签名证书密钥用法
				byte[] data = user.getX509Cert().getBytes();
				X509CertEnvelope cert = CertUtil.parseCert(data);
				boolean[] usage = cert.getKeyUsage();
				//加密证书密钥用法
				byte[] dataS = user.getX509Certen().getBytes();
				X509CertEnvelope certen = CertUtil.parseCert(dataS);
				boolean[] usageen = certen.getKeyUsage();
				
				if(Arrays.equals(usage,usageen)) 
					certToDB(u, data, certSn, certDn, limit, c, Constants.USAGE_SIGN_ENC);
				
			}else {
				if (StringUtil.isNotBlank(certSn)) {
					byte[] data = user.getX509Cert().getBytes();
					certToDB(u, data, certSn, certDn, limit, c, Constants.USAGE_SIGNATURE);
				}
				if (StringUtil.isNotBlank(certenSn)) {
					byte[] dataS = user.getX509Certen().getBytes();
					certToDB(u, dataS, certenSn, certDn, limit, c, Constants.USAGE_ENCRYPT); 
				}
			}

		}

		userDao.updateUser(u);
	}

	/**
	 * 根据id查询签章人信息
	 * 
	 * @param id
	 * @return
	 */
	public UserVO getUser(Long id) {
		try {
			User user = userDao.getUser(id);
			if (user == null)
				return null;
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			Company company = user.getCompany();
			if (company != null)
				userVO.setCompanyName(company.getName());

			return userVO;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查询指定单位下签章人
	 * 
	 * @param name
	 * @param companyID
	 * @return
	 */
	public List<UserVO> getUser(String name, Long companyID) {
		List<User> userList = userDao.getUser(name, companyID);
		List<UserVO> userVoList = new ArrayList<UserVO>();
		for (User user : userList) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			userVoList.add(userVO);
		}

		return userVoList;
	}

	/**
	 * 获取单位下所有的用户
	 * 
	 * @param page
	 * @param user
	 * @param cid
	 * @return
	 */
	public Page<UserVO> searchUserPageByCompanyId(Page<UserVO> page, UserVO userVO, Long cid) {
		User user = new User();
		BeanUtils.copyProperties(userVO, user);

		String c_Name = userVO.getCompanyName();
		if (c_Name != null) {
			Company c = new Company();
			c.setName(c_Name);
			user.setCompany(c);
		}

		Company c = companyDao.getCompany(cid);
		String treeID = c.getTreeId();

		int total = userDao.searchSonTotal(user, treeID);
		int start = page.getStart();
		int end = page.getEnd();

		List<User> list = userDao.searchUserPageByCompanyId(user, start, end, treeID);
		List<UserVO> userVOlist = new ArrayList<UserVO>();
		for (User tempUser : list) {
			UserVO tempUserVo = new UserVO();
			BeanUtils.copyProperties(tempUser, tempUserVo);
			Company company = tempUser.getCompany();
			if (company != null)
				tempUserVo.setCompanyName(company.getName());

			userVOlist.add(tempUserVo);
		}
		page.setTotalNo(total);
		page.setResult(userVOlist);
		return page;
	}

	/**
	 * 分页查询签章人
	 * 
	 * @param page
	 * @param userVO
	 * @return
	 * @throws Exception
	 */
	public Page<UserVO> searchUser(Page<UserVO> page, UserVO userVO, Long cid) throws Exception {
		User user = new User();
		BeanUtils.copyProperties(userVO, user);
		// 传入证书DN 条件搜索
		List<Cert> listCerts = new ArrayList<Cert>();
		Cert c = new Cert();
		String certDn = userVO.getCertDn();
		if (StringUtil.isNotBlank(certDn)) {
			c.setCertDn(certDn);
			listCerts.add(c);
			user.setCerts(listCerts);
		}
		Company com = companyDao.getCompany(cid);
		String treeID = com.getTreeId();

		int total = userDao.searchTotal(user, treeID);

		int start = page.getStart();
		int end = page.getEnd();
		List<User> list = userDao.searchByPage(user, start, end, treeID);
		List<UserVO> userVOlist = new ArrayList<UserVO>();
		for (User tempUser : list) {
			UserVO tempUserVo = new UserVO();
			BeanUtils.copyProperties(tempUser, tempUserVo);
			// 校验信息
			isModify(tempUser, tempUserVo);

			userVOlist.add(tempUserVo);
		}

		// 查询cert, 单位
		if (list != null && list.size() > 0)
			for (UserVO u : userVOlist) {
				Company company = companyDao.getCompany(u.getCompanyId());
				if (company != null) {
					u.setCompanyId(company.getId());
					u.setCompanyName(company.getName());
				}

				List<Cert> certList = certDao.getCertByUserId(u.getId());
				if (certList != null && certList.size() > 0) {
					u.setCertDn(certList.get(0).getCertDn());
					u.setCertUsageCN(u.getCertUsage(certList.get(0).getCertUsage()));
				}
				if (certList != null && certList.size() > 1) {
					for (int i = 1; i < certList.size(); i++) {
						u.setCertDn(u.getCertDn() + "<br>" + certList.get(i).getCertDn());
						u.setCertUsageCN(u.getCertUsageCN() + "<br>" + u.getCertUsage(certList.get(i).getCertUsage()));
					}
				}
			}
		page.setTotalNo(total);
		page.setResult(userVOlist);
		return page;
	}

	/**
	 * 查询签章人单位数据
	 * 
	 * @param companyVO
	 * @param userVO
	 * @return
	 */
	public List<TreeNode> getCompanyTreeByPid(Long pId) {
		List<Company> list = companyDao.getCompanys(pId);
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		for (Company c : list) {
			TreeNode node = new TreeNode();
			node.setId(String.valueOf(c.getId()));
			node.setName(c.getName());
			node.setTitle(c.getRemark());
			if (c.getIsParent() == 1)
				node.setIsParent(true);

			treeList.add(node);
		}

		return treeList;
	}
	
	/**
	 * 查询签章人单位数据
	 * 
	 * @param companyVO
	 * @param userVO
	 * @return
	 */
	public List<TreeNode> getCompanyTreeById(Long Id) {
		Company c = companyDao.getCompany(Id);
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		
		TreeNode node = new TreeNode();
		node.setId(String.valueOf(c.getId()));
		node.setName(c.getName());
		node.setTitle(c.getRemark());
		if (c.getIsParent() == 1)
			node.setIsParent(true);

		treeList.add(node);

		return treeList;
	}

	/**
	 * 获取签章人数量
	 * 
	 * @return
	 */
	public int getUserTotalCount() {
		return userDao.getTotalUsreCount();
	}

	/**
	 * 删除签章人证书
	 * 
	 * @param certDN
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCert(String certDN, Long userId) throws Exception {
		List<Cert> certList = certDao.getCertByDN(certDN, userId);
		int deleteCertNum = 0;
		for (Cert cert : certList) {
			Request request = new Request();
			request.setCertId(cert.getId());
			request.setUserId(userId);
			List<Request> requestList = requestDaoImpl.getRequests(request);
			if (requestList.size() > 0)
				throw new WebDataException("所选证书正在申请印章");
			Seal seal = new Seal();
			seal.setCertId(cert.getId());
			seal.setUserId(userId);
			List<Seal> sealList = sealDaoImpl.getSeals(seal);
			if (sealList.size() > 0)
				throw new WebDataException("所选证书已生成印章");
			// 删除证书
			certDao.deleteCert(cert.getId());
			certDataDao.deleteCertData(cert.getCertDataId());
			idDeleteDao.insertIDDelete(cert.getId(), Constants.TABLE_SEAL_CERT);
			idDeleteDao.insertIDDelete(cert.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
			deleteCertNum++;
		}

		// 更新证书计数
		Count count = new Count();
		count.setName(Constants.CERT_NUM);
		count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
		List<Count> cList = countDao.getCount(count);
		count = cList.get(0);
		count.setNum(count.getNum() - deleteCertNum);
		countDao.updateCountMinus(count);

		// 删除证书文件
		for (Cert cert : certList)
			FileUtil.deleteFile(cert.getCertPath());
	}

}
