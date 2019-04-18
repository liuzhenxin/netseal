package cn.com.infosec.netseal.webserver.service.company;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.company.CompanyDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.sysUser.SysUserDaoImpl;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.entity.vo.CompanyVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.TreeIdUtil;
import cn.com.infosec.netseal.webserver.util.TreeNode;

@Service
public class CompanyServiceImpl extends BaseService {
	@Autowired
	protected CompanyDaoImpl companyDao;
	@Autowired
	protected TemplateDaoImpl templateDao;
	@Autowired
	protected SealDaoImpl sealDao;
	@Autowired
	protected UserDaoImpl userDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	private SysUserDaoImpl sysUserDao;
	
	/**
	 * 添加单位
	 * 
	 * @param company
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertCompany(CompanyVO companyVO) throws Exception {
		Company company = new Company();
		BeanUtils.copyProperties(companyVO,company);
		
		// 校验单位信息
		List<Company> comList = companyDao.getCompanys("000");
		for (Company comp : comList) 
			isModify(comp, "机构单位" + comp.getName(), "Company which id is" + comp.getId() + " and name is " + comp.getName());
		
		List<Company> tempCompany = companyDao.getCompany(companyVO.getName(),companyVO.getPid());
		if( tempCompany.size() > 0 )
			throw new WebDataException("添加单位失败,单位名称重复");
		
		// 更新上级单位为父节点
		Company c = companyDao.getCompany(company.getPid());
		c.setUpdateTime(DateUtil.getCurrentTime());
		c.setIsParent(1);
		int r = companyDao.updateCompanyIsParent(c);
		if (r == 0) 
			throw new WebDataException("添加单位失败,更新上级单位失败");
		
		List<String> treeIdList = companyDao.subCompanyTreeId(company.getPid());
		
		// 生成treeId
		String treeId = TreeIdUtil.genTreeId(treeIdList);
		if (treeId == null) {
			Company pCompany = companyDao.getCompany(company.getPid());
			treeId = pCompany.getTreeId() + "000";
		}
		company.setTreeId(treeId);
		company.setGenerateTime(DateUtil.getCurrentTime());
		company.setUpdateTime(DateUtil.getCurrentTime());
		companyDao.insertCompany(company);
	}

	/**
	 * 根据id查询单位信息
	 * 
	 * @param id
	 * @return
	 */
	public CompanyVO getCompany(Long id) {
		Company company = companyDao.getCompany(id);
		if(company == null)
			return null;
		CompanyVO companyVO = new CompanyVO();
		BeanUtils.copyProperties(company, companyVO);
		return companyVO;
	}

	/**
	 * 删除单位
	 * 
	 * @param id
	 * @return 父单位
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CompanyVO deleteCompany(Long id) throws Exception {
		Company company = companyDao.getCompany(id);
		// 1.如果存在印模不允许删除
		List<Template> templateList = templateDao.getTemplateByCompanyId(id);
		if (templateList.size() > 0) 
			throw new WebDataException("存在印模不允许删除");
		
		// 2.如果存在印章不允许删除
		/*Seal seal = new Seal();
		seal.setCompanyId(company.getId());
		List<Seal> sealList = sealDao.getSeals(seal);
		if (sealList.size() > 0) {
			throw new WebDataException("存在印章不允许删除");
		}*/
		Long pid = company.getPid();
		// 3.如果存在子单位不允许删除
		List<Company> subCompanyList = companyDao.getCompanys(id);
		if (subCompanyList.size() > 0) 
			throw new WebDataException("存在子单位不允许删除");
		
		// 4.存在下属人员不允许删除
		String treeId = company.getTreeId();
		int count = userDao.countUserByCompany(treeId);
		if (count > 0) 
			throw new WebDataException("存在签章人,不允许删除");
		
		int sysCount = sysUserDao.countSysUserByCompany(treeId);
		if (sysCount > 0) 
			throw new WebDataException("存在管理员,不允许删除");
		
		// 5.设置父节点
		subCompanyList = companyDao.getCompanys(pid);
		if (subCompanyList.size() == 1) {
			Company c = companyDao.getCompany(pid);
			c.setIsParent(0);
			c.setUpdateTime(DateUtil.getCurrentTime());
			
			companyDao.updateCompanyIsParent(c);
		}
		// 6.删除单位
		companyDao.deleteCompany(id);
		//增加删除记录
		idDeleteDao.insertIDDelete(id,Constants.TABLE_SEAL_COMPANY);
		
		company = companyDao.getCompany(pid);
		CompanyVO companyVO = new CompanyVO();
		BeanUtils.copyProperties(company, companyVO);		
		return companyVO;
	}

	/**
	 * 修改单位信息
	 * 
	 * @param company
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCompany(CompanyVO companyVO) throws Exception {
		List<Company> tempCompany = companyDao.getCompany(companyVO.getName(),companyVO.getPid());
		
		Company companyCur = companyDao.getCompany(companyVO.getId());
		// 校验信息
		isModify(companyCur, "所选单位 " + companyCur.getName(), "Company which id is " + companyCur.getId() + " and name is " + companyCur.getName());
		
		if(tempCompany.size()> 0 && !(companyVO.getName().equals(companyCur.getName())) )
			throw new WebDataException("修改单位失败,单位名称重复");
		
		// 如果修改单位名称
		if (!companyCur.getName().equals(companyVO.getName())) {
			// 如该单位下已有用户制发了电子印章，则不允许用户管理员修改该单位名称
			// 1.如果已制发过印章不允许修改单位名称
			int sealNum=sealDao.countSealByCompany(companyVO.getId());
			if (sealNum > 0) 
				throw new WebDataException("已制发过印章不允许修改单位名称");
			
		}
		// 2.修改单位信息
		Company company=new Company();
		BeanUtils.copyProperties(companyCur, company);
		company.setUpdateTime(DateUtil.getCurrentTime());
		company.setName(companyVO.getName());
		company.setRemark(companyVO.getRemark());
		int r = companyDao.updateCompany(company);
		if (r == 0) 
			throw new WebDataException("修改单位信息失败");
		
	}

	/**
	 * 查询单位集合
	 * 
	 * @param companyVO
	 * @return
	 */
	public List<TreeNode> findCompanyByPid(Long pid) {
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		List<Company> list = companyDao.getCompanys(pid);
		
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
	 * 查询单位节点
	 * @param Id
	 * @return
	 */
	public List<TreeNode> findCompanyById(Long Id) {
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		Company c = companyDao.getCompany(Id);
		
		TreeNode node = new TreeNode();
		node.setId(String.valueOf(c.getId()));
		node.setName(c.getName());
		node.setTitle(c.getRemark());
		if (c.getIsParent() == 1) 
			node.setIsParent(true);
		
		treeList.add(node);
		
		return treeList;
	}
}
