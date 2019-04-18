package cn.com.infosec.netseal.webserver.service.sysUser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import cn.com.infosec.netseal.common.dao.role.RoleDaoImpl;
import cn.com.infosec.netseal.common.dao.sysUser.SysUserDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.entity.po.Role;
import cn.com.infosec.netseal.common.entity.po.SysUser;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.util.MobileTokenUtil;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.TreeNode;

@Service
public class SysUserServiceImpl extends BaseService {

	@Autowired
	private SysUserDaoImpl sysUserDao;
	@Autowired
	private CompanyDaoImpl companyDao;
	@Autowired
	protected CertDaoImpl certDao;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	protected RoleDaoImpl roleDao;
	@Autowired
	protected CountDaoImpl countDao;

	/**
	 * 新增加管理员用户
	 * 
	 * @param sysUserVO
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertSysUser(SysUserVO sysUserVO) throws Exception {
		// 判断证书是否超限
		long limit = ConfigUtil.getInstance().getCheckCertNumLimit();
		Count count = new Count();
		count.setName(Constants.CERT_NUM);
		count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
		List<Count> countList = countDao.getCount(count);
		if (countList.size() == 0)
			throw new WebDataException("计数表数据在库中不存在");

		if (countList.size() >= 2)
			throw new WebDataException("计数表数据在库中不唯一");

		Count c = countList.get(0);
		// 校验证书统计表信息
		isModify(c, "证书计数表中" + c.getName(), "Count which name is " + c.getName());

		if (c.getNum() < limit) {
			SysUser sysUser = new SysUser();
			sysUser.setName(sysUserVO.getName());
			sysUser.setCompanyId(sysUserVO.getCompanyId());
			// 同一个单位中姓名唯一
			List<SysUser> tempSysUser = sysUserDao.getSysUser(sysUser);
			if (tempSysUser != null && tempSysUser.size() > 0)
				throw new WebDataException("同一个单位中姓名已存在");

			BeanUtils.copyProperties(sysUserVO, sysUser);

			// 校验单位信息
			Company com = companyDao.getCompany(sysUserVO.getCompanyId());
			isModify(com, "所选单位 " + com.getName(), "Company which id is " + com.getId() + " and name is " + com.getName());

			sysUser.setPassword(CryptoHandler.hashEnc64(Constants.SYS_USER_DEFAULT_PWD));
			sysUser.setStatus(1);
			sysUser.setFailedNum(0);
			sysUser.setChangePass(0);
			sysUser.setGenerateTime(DateUtil.getCurrentTime());
			sysUser.setMac("");
			sysUser.setUpdateTime(DateUtil.getCurrentTime());
			String tokenSn = sysUserVO.getTokenSn();
			String tokenActiveCode = sysUserVO.getTokenActiveCode();
			if(StringUtil.isNotBlank(tokenSn) && StringUtil.isNotBlank(tokenActiveCode)){
				String tokenSeed = MobileTokenUtil.genSeed(tokenSn, tokenActiveCode);
				sysUser.setTokenSeed(tokenSeed);
			}
			sysUserDao.insertSysUser(sysUser);
			// 注册证书
			if (StringUtil.isBlank(sysUserVO.getCertDn()))
				throw new WebDataException("证书信息为空,不允许注册");

			String certDn = sysUserVO.getCertDn();
			String certSn = sysUserVO.getCertSn();

			// 证书DN长度限制
			byte[] bytes = certDn.getBytes();
			if (bytes.length > 300)
				throw new WebDataException("证书主题长度不超过300个字节");

			List<Cert> tempCertList = certDao.getCert(certDn);
			if (tempCertList.size() > 0)
				throw new WebDataException("存在相同的证书,不允许注册");

			byte[] data = sysUserVO.getX509Cert().getBytes();

			// 验证证书
			certChainService.verifyCert(data);

			// 添加证书
			CertData certData = new CertData();
			certData.setData(data);
			certData.setGenerateTime(sysUser.getGenerateTime());
			certData.setUpdateTime(sysUser.getUpdateTime());
			Long certDataId = certDataDao.insertCertData(certData);

			String certPath = Constants.CERT_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.CERT_SUFFIX;
			Cert cert = new Cert();
			cert.setCertDn(certDn);
			cert.setCertSn(certSn);
			cert.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
			cert.setCertPath(certPath);
			cert.setCertUsage(Constants.USAGE_SIGNATURE);
			cert.setNotBefor(0L);
			cert.setNotAfter(0L);
			cert.setCertDataId(certDataId);
			cert.setSysUserId(sysUser.getId());
			cert.setUserId(-1L);
			cert.setGenerateTime(sysUser.getGenerateTime());
			cert.setUpdateTime(sysUser.getUpdateTime());
			certDao.insertCert(cert);

			c.setNum(c.getNum() + 1);
			int n = countDao.updateCountPlus(c, limit);
			if (n == 0)
				throw new WebDataException("证书数量超限,无法注册");

			// 将证书写入到目录文件中
			FileUtil.storeFile(certPath, data);
		} else {
			throw new WebDataException("证书数量超限, 无法注册");
		}

	}

	/**
	 * 删除管理员用户
	 * 
	 * @param account
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteSysUser(String ids) throws Exception {
		List<Cert> certList = new ArrayList<Cert>();
		String idList[] = ids.split(Constants.SPLIT_1);
		for (int i = 0; i < idList.length; i++) {
			Long sysUserId = Long.valueOf(idList[i].trim());

			List<Cert> tempCertList = certDao.getCertBySysUserId(sysUserId);
			certList.addAll(tempCertList);

			int r = sysUserDao.deleteSysUser(sysUserId);
			if (r == 0)
				throw new WebDataException("操作失败");
			idDeleteDao.insertIDDelete(sysUserId, Constants.TABLE_SEAL_SYS_USER);
		}
		int deleteCertNum = 0;
		// 删除证书
		for (Cert cert : certList) {
			certDao.deleteCert(cert.getId());
			certDataDao.deleteCertData(cert.getCertDataId());
			// 增加删除记录
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

	/**
	 * 查询管理员用户
	 * 
	 * @param account
	 * @return
	 */
	public List<SysUserVO> getSysUser(String account) {
		List<SysUser> sysUserList = sysUserDao.getSysUser(account);
		List<SysUserVO> sysUserVOList = new ArrayList<SysUserVO>();
		for (SysUser sysUser : sysUserList) {
			SysUserVO sysUserVO = new SysUserVO();
			BeanUtils.copyProperties(sysUser, sysUserVO);
			sysUserVOList.add(sysUserVO);
		}
		return sysUserVOList;
	}

	/**
	 * 查询单条管理员用户
	 * 
	 * @param id
	 * @return
	 */
	public SysUserVO getSysUser(Long id) {
		SysUser sysUser = sysUserDao.getSysUser(id);
		if (sysUser == null)
			return null;

		SysUserVO sysUserVO = new SysUserVO();
		BeanUtils.copyProperties(sysUser, sysUserVO);
		Company company = sysUser.getCompany();
		if (company != null) {
			sysUserVO.setCompanyId(company.getId());
			sysUserVO.setCompanyName(company.getName());
		}

		Role role = sysUser.getRole();
		if (role != null) {
			sysUserVO.setRoleId(role.getId());
			sysUserVO.setRoleName(role.getName());
		}
		if(StringUtil.isNotBlank(sysUserVO.getTokenSeed())){
			sysUserVO.setTokenSeed("已注册");
		} else{
			sysUserVO.setTokenSeed("没有注册");
		}
		return sysUserVO;
	}

	/**
	 * 修改管理员用户
	 * 
	 * @param sysUserVO
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSysUserNoCert(SysUserVO sysUserVO) throws Exception {
		SysUser sysUser = sysUserDao.getSysUser(sysUserVO.getId());
		// 校验信息
		isModify(sysUser, "所选管理员 " + sysUser.getAccount(), "SysUser which id is " + sysUser.getId() + " and account is " + sysUser.getAccount());

		if (StringUtil.isNotBlank(sysUserVO.getName()))
			sysUser.setName(sysUserVO.getName());
		if (StringUtil.isNotBlank(sysUserVO.getPassword()))
			sysUser.setPassword(sysUserVO.getPassword());
		if (null != sysUserVO.getRoleId())
			sysUser.setRoleId(sysUserVO.getRoleId());
		if (null != sysUserVO.getStatus())
			sysUser.setStatus(sysUserVO.getStatus());
		if (null != sysUserVO.getFailedNum())
			sysUser.setFailedNum(sysUserVO.getFailedNum());
		if (null != sysUserVO.getChangePass())
			sysUser.setChangePass(sysUserVO.getChangePass());
		if (null != sysUserVO.getCompanyId())
			sysUser.setCompanyId(sysUserVO.getCompanyId());
		if (null != sysUserVO.getChangePass())
			sysUser.setChangePass(sysUserVO.getChangePass());

		sysUser.setUpdateTime(DateUtil.getCurrentTime());
		int r = sysUserDao.updateSysUser(sysUser);
		if (r == 0)
			throw new WebDataException("操作失败");
	}

	/**
	 * 修改管理员用户信息 及证书信息
	 * 
	 * @param sysUserVO
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSysUser(SysUserVO sysUserVO) throws Exception {
		SysUser sysUser = sysUserDao.getSysUser(sysUserVO.getId());
		// 校验信息
		isModify(sysUser, "所选管理员 " + sysUser.getAccount(), "SysUser which id is " + sysUser.getId() + " and account is " + sysUser.getAccount());

		if (StringUtil.isNotBlank(sysUserVO.getName()))
			sysUser.setName(sysUserVO.getName());
		if (StringUtil.isNotBlank(sysUserVO.getPassword()))
			sysUser.setPassword(sysUserVO.getPassword());
		if (null != sysUserVO.getRoleId())
			sysUser.setRoleId(sysUserVO.getRoleId());
		if (null != sysUserVO.getStatus())
			sysUser.setStatus(sysUserVO.getStatus());
		if (null != sysUserVO.getFailedNum())
			sysUser.setFailedNum(sysUserVO.getFailedNum());
		if (null != sysUserVO.getChangePass())
			sysUser.setChangePass(sysUserVO.getChangePass());
		if (null != sysUserVO.getCompanyId()) {
			Company comp = companyDao.getCompany(sysUserVO.getCompanyId());
			// 校验信息
			isModify(comp, "所选单位" + comp.getName(), "Company which id is " + comp.getId() + " and name is " + comp.getName());

			sysUser.setCompanyId(sysUserVO.getCompanyId());
		}
		
		String tokenSn = sysUserVO.getTokenSn();
		String tokenActiveCode = sysUserVO.getTokenActiveCode();
		if(StringUtil.isNotBlank(tokenSn) && StringUtil.isNotBlank(tokenActiveCode)){
			String tokenSeed = MobileTokenUtil.genSeed(tokenSn, tokenActiveCode);
			sysUser.setTokenSeed(tokenSeed);
		}
		sysUser.setUpdateTime(DateUtil.getCurrentTime());
		int r = sysUserDao.updateSysUser(sysUser);
		if (r == 0)
			throw new WebDataException("操作失败");

		String curCertDn = "";
		String curCertSn = "";
		Cert curCert = null;
		List<Cert> certList = certDao.getCertBySysUserId(sysUserVO.getId());
		if (certList != null && certList.size() > 0) {
			curCert = certList.get(0);
			curCertDn = curCert.getCertDn();
			curCertSn = curCert.getCertSn();
		}
		// 注册证书
		if (StringUtil.isNotBlank(sysUserVO.getCertDn())) {
			String certDn = sysUserVO.getCertDn();
			String certSn = sysUserVO.getCertSn();

			// 证书DN长度限制
			byte[] bytes = certDn.getBytes();
			if (bytes.length > 300)
				throw new WebDataException("证书主题长度不超过300个字节");

			List<Cert> tempCertList = certDao.getCert(certDn);
			if (!certDn.equals(curCertDn) && tempCertList.size() > 0)
				throw new WebDataException("此证书已被他人注册,不允许注册");

			// 判断读取的证书是否为原证书
			if (certSn.equals(curCertSn))
				throw new WebDataException("该管理员已注册该证书");

			byte[] data = sysUserVO.getX509Cert().getBytes();
			Cert cert = new Cert();
			// 验证证书
			certChainService.verifyCert(data);
			CertData certData = new CertData();
			certData.setData(data);
			certData.setGenerateTime(sysUser.getGenerateTime());
			certData.setUpdateTime(sysUser.getUpdateTime());
			Long certDataId = certDataDao.insertCertData(certData);

			String certPath = Constants.CERT_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.CERT_SUFFIX;
			cert.setCertDn(certDn);
			cert.setCertSn(certSn);
			cert.setCertPath(certPath);
			cert.setNotBefor(0L);
			cert.setNotAfter(0L);
			cert.setCertDataId(certDataId);
			cert.setSysUserId(sysUserVO.getId());
			cert.setUserId(-1L);
			cert.setCertUsage(Constants.USAGE_SIGNATURE);
			cert.setGenerateTime(sysUser.getGenerateTime());
			cert.setUpdateTime(sysUser.getUpdateTime());
			cert.setCertIssueDn(Constants.DEFAULT_UNKNOWN_STRING);
			// 删除历史注册的证书信息
			if (curCert != null) {
				certDao.deleteCert(curCert.getId());
				certDataDao.deleteCertData(curCert.getCertDataId());
				// 增加删除记录
				idDeleteDao.insertIDDelete(curCert.getId(), Constants.TABLE_SEAL_CERT);
				idDeleteDao.insertIDDelete(curCert.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
			}

			certDao.insertCert(cert);
			// 将证书写入到目录文件中
			FileUtil.storeFile(certPath, data);
			// 删除历史注册的证书文件
			if (curCert != null)
				FileUtil.deleteFile(curCert.getCertPath());

		}
	}

	/**
	 * 查询全部
	 * 
	 * @return
	 */
	public List<SysUser> getSysUser() {
		return sysUserDao.getSysUsers();
	}

	/**
	 * 查询分页
	 * 
	 * @param page
	 * @param sysUser
	 * @return
	 */
	/**
	 * @param page
	 * @param sysUserVO
	 * @return
	 * @throws Exception
	 */
	public Page<SysUserVO> searchSysUser(Page<SysUserVO> page, SysUserVO sysUserVO, String treeId) throws Exception {
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(sysUserVO, sysUser);
		int total = sysUserDao.searchTotal(sysUser, treeId);

		int start = page.getStart();
		int end = page.getEnd();
		List<SysUser> list = sysUserDao.searchByPage(sysUser, start, end, treeId);
		List<SysUserVO> sysUserVOlist = new ArrayList<SysUserVO>();
		for (SysUser tempSysUser : list) {
			SysUserVO tempSysUserVO = new SysUserVO();
			BeanUtils.copyProperties(tempSysUser, tempSysUserVO);
			Role role = tempSysUser.getRole();
			if (role != null) {
				tempSysUserVO.setRoleId(role.getId());
				tempSysUserVO.setRoleName(role.getName());
			}
			Company company = tempSysUser.getCompany();
			if (company != null) {
				tempSysUserVO.setCompanyId(company.getId());
				tempSysUserVO.setCompanyName(company.getName());
			}
			// 校验信息
			isModify(tempSysUser, tempSysUserVO);

			sysUserVOlist.add(tempSysUserVO);
		}
		page.setTotalNo(total);
		page.setResult(sysUserVOlist);
		return page;
	}

	/**
	 * 获取机构
	 * 
	 * @param company
	 * @param sysUser
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
	 * 获取机构
	 * 
	 * @param company
	 * @param sysUser
	 * @return
	 */
	public List<TreeNode> getCompanyTreeById(Long id) {
		Company c = companyDao.getCompany(id);
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
	 * 校验管理员信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean isModify(long id) throws Exception {
		SysUser sys = sysUserDao.getSysUser(id);
		return isModify(sys);
	}
	
	/**
	 * 项目升级日志
	 * @throws Exception 
	 */
	public List<Map<String,String>> getVersion(String filePath) throws Exception{
		
		BufferedReader in = null;
		String line = "";
		int versionStart=0,versionEnd=0,dateStart=0, dateEnd=0;
		List<Map<String,String>> versionList = new ArrayList<Map<String,String>>();
		try {
			in = new BufferedReader(new FileReader(filePath));
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.startsWith("[setup]")) {
					versionStart =  StringUtils.ordinalIndexOf(line, "[", 2) + 1;
					versionEnd = StringUtils.ordinalIndexOf(line, "]", 2);
					dateStart = StringUtils.ordinalIndexOf(line, "[", 3) + 1;
					dateEnd = StringUtils.ordinalIndexOf(line, "]", 3)-4;
					Map<String,String> map = new HashMap<String,String>();
					map.put("version", line.substring(versionStart, versionEnd).trim());
					map.put("date", line.substring(dateStart, dateEnd).trim());
					versionList.add(map);
				}
			}
			
		}catch(IOException ioe) {
			throw new Exception("updatelog文件读取失败");
		}finally {
			if (in != null) {
				try {
					in.close();
				}catch(IOException e) {
					throw new Exception("updatelog文件读取失败");
			 }
		  }
		}
		return versionList;
	}
	
	/**
	 * 版本详情
	 * @throws Exception 
	 */
	public String getDetails(String version,String filePath) throws Exception {
		BufferedReader in = null;
		String line = "";
		StringBuffer details = new StringBuffer();
		details.append(version).append("\r\n");
		StringBuffer str = new StringBuffer("");
		StringBuffer strdate = new StringBuffer("");
		int datestart,index;
		try {
			in = new BufferedReader(new FileReader(filePath));
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(str.length()==0) {
					if(line.contains(version)){
						if(line.endsWith("]")) {
							if(line.contains("rollback")) {
								strdate.append("恢复： ");
							}
							if(line.contains("backup")) {
								strdate.append("备份： ");
							}
							datestart = StringUtils.ordinalIndexOf(line, "[", 3) + 1;
							strdate.append(line.substring(datestart,line.length()-5)).append("\r\n");
						}else {
							index = StringUtils.ordinalIndexOf(line, "[", 4) + 1;
							str.append(line.substring(index)).append("\r\n");
						}
					}
				}else {
					if(!line.contains("]")) {
						str.append(line).append("\r\n");
					}else{
						str.append(line.substring(0,line.length()-1)).append("\r\n");
						details.append(str);
						str.setLength(0);
					}
				}
			}
			details.append(strdate);
		}catch(IOException ioe) {
			throw new Exception("updatelog文件读取失败");
		}finally {
			if (in != null) {
				try {
					in.close();
				}catch(IOException e) {
					throw new Exception("updatelog文件读取失败");
			 }
		  }
		}
		
		
		return details.toString();
	}
	
	
	public static void main(String[] args) throws Exception {
		SysUserServiceImpl sysuser = new  SysUserServiceImpl();
		System.out.println(sysuser.getDetails("NetSeal 1.0.004.1 build201808101619","f:/temp/updatelog.txt"));
	}
}
