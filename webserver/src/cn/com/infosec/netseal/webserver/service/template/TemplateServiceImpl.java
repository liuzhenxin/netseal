package cn.com.infosec.netseal.webserver.service.template;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.company.CompanyDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.dao.request.RequestDaoImpl;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserDaoImpl;
import cn.com.infosec.netseal.common.dao.userTemplate.UserTemplateDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.po.Request;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.po.UserTemplate;
import cn.com.infosec.netseal.common.entity.vo.CompanyVO;
import cn.com.infosec.netseal.common.entity.vo.TemplateVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.service.company.CompanyServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.TreeNode;

@Service
public class TemplateServiceImpl extends BaseService {
	@Autowired
	protected TemplateDaoImpl templateDao;
	@Autowired
	protected CompanyDaoImpl companyDao;
	@Autowired
	private RequestDaoImpl requestDao;
	@Autowired
	private PhotoDataDaoImpl photoDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	@Autowired
	protected UserTemplateDaoImpl userTemplateDao;
	@Autowired
	protected UserDaoImpl userDao;
	@Autowired
	protected CompanyServiceImpl companyService;

	/**
	 * 添加印模
	 * 
	 * @param template
	 * @param notBeforTime
	 * @param notAfterTime
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertTemplate(TemplateVO templateVO, PhotoData photoData) throws Exception {
		Template template = new Template();
		BeanUtils.copyProperties(templateVO, template);

		// 校验单位信息
		Company com = companyDao.getCompany(templateVO.getCompanyId());
		isModify(com, "所选单位" + com.getName(), "Company which id is " + com.getId() + " and name is " + com.getName());

		if (templateVO.getNotBefor() > templateVO.getNotAfter()) 
			throw new WebDataException("印模启用日期不能大于结束日期");

		template.setStatus(1);
		template.setGenerateTime(DateUtil.getCurrentTime());
		Long photoId = 0L;
		// 保存图片
		if (photoData != null) {
			photoId = photoDataDao.insertPhotoData(photoData);
		    template.setPhotoDataId(photoId);
		}else {
			template.setPhotoDataId(-1L);
		}
		templateDao.insertTemplate(template);
		// 增加关联记录
		String userIds = templateVO.getUserIds();
		if (StringUtil.isNotBlank(userIds)) {
			String[] ids = userIds.split(Constants.SPLIT_1);
			for (String tempId : ids) {
				Long userId = Long.valueOf(tempId.trim());
				UserTemplate userTemplate = new UserTemplate();
				userTemplate.setUserId(userId);
				userTemplate.setTemplateId(template.getId());
				userTemplate.setGenerateTime(template.getGenerateTime());
				userTemplate.setUpdateTime(template.getUpdateTime());
				userTemplateDao.insertUserTemplate(userTemplate);
			}

		}

	}

	/**
	 * 删除印模
	 * 
	 * @param id
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteTemplate(String ids) throws Exception {
		List<String> pathList = new ArrayList<String>();
		if (ids == null || "".equals(ids.trim()))
			throw new WebDataException("请求失败");

		String[] temp = ids.split(Constants.SPLIT_1);
		if (temp.length > 0) {
			ids = temp[0];
			for (int i = 0; i < temp.length; i++) {
				Long id = Long.parseLong(temp[i].trim());

				Template t = templateDao.getTemplate(id);
				if(StringUtil.isNotBlank(t.getPhotoPath()))
					pathList.add(t.getPhotoPath());
				Request request = new Request();
				request.setTemplateId(id);
				List<Request> requestList = requestDao.getRequests(request);

				if (requestList.size() > 0)
					throw new WebDataException("所选印模已有印章申请,无法删除");

				int r = templateDao.deleteTemplate(id);
				if (r == 0)
					throw new WebDataException("操作失败");

				// 增加删除记录
				idDeleteDao.insertIDDelete(id, Constants.TABLE_SEAL_TEMPLATE);

				// 删除图片数据
				if (!t.getPhotoDataId().equals(-1L)) {
					photoDataDao.deletePhotoData(t.getPhotoDataId());
					// 增加删除记录
					idDeleteDao.insertIDDelete(t.getPhotoDataId(), Constants.TABLE_SEAL_PHOTO_DATA);
				}
				// 删除用户关联记录
				userTemplateDao.deleteUserTemplate(id);
				// 增加删除记录
				idDeleteDao.insertIDDelete(id, Constants.TABLE_SEAL_USER_TEMPLATE);
				
			}

			// 删除图片
			for (String path : pathList) {
				FileUtil.deleteFile(path);
			}
		}
	}

	/**
	 * 获取印模信息
	 * 
	 * @param id
	 * @return
	 */
	public TemplateVO getTemplate(Long id) {
		Template template = templateDao.getTemplate(id);
		if (template == null)
			return null;
		TemplateVO templateVO = new TemplateVO();
		BeanUtils.copyProperties(template, templateVO);
		return templateVO;
	}

	/**
	 * 根据印模id 查询印模和用户信息
	 * 
	 * @param templateId
	 * @return
	 * @throws Exception 
	 */
	public TemplateVO getTemplateWithUser(Long templateId) throws Exception {
		Template template = templateDao.getTemplate(templateId);
		TemplateVO templateVO = new TemplateVO();
		BeanUtils.copyProperties(template, templateVO);

		List<UserTemplate> userTemplateList = userTemplateDao.getUserTemplate(templateId);
		StringBuffer ids = new StringBuffer();
		StringBuffer names = new StringBuffer();
		for (UserTemplate tempUT : userTemplateList) {
			// 校验用户印模关联信息
			isModify(tempUT, "用户印模关联表表中user_id为: " + tempUT.getUserId() + "template_id为: " + tempUT.getTemplateId(),
					"UserTemplate which user_id is " + tempUT.getUserId() + " and template_id为: " + tempUT.getTemplateId());
			
			Long userId = tempUT.getUserId();
			if (userId.longValue() == 0) {
				ids.append(0);
				names.append("印模单位下所有用户");
				break;
			} else {
				User user = userDao.getUser(userId);
				if (user != null) {
					ids.append(user.getId() + ",");
					names.append(user.getName() + ",");
				}
			}

		}
		templateVO.setUserIds(StringUtils.join(ids.toString().split(","), ","));
		templateVO.setUserNames(StringUtils.join(names.toString().split(","), ","));
		CompanyVO companyVO = companyService.getCompany(templateVO.getCompanyId());
		templateVO.setCompanyId(companyVO.getId());
		templateVO.setCompanyName(companyVO.getName());

		return templateVO;
	}

	/**
	 * 更新印模信息
	 * 
	 * @param templateVO
	 * @param photoData
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTemplate(TemplateVO templateVO, PhotoData photoData) throws Exception {
		if (templateVO.getNotBefor() > templateVO.getNotAfter()) 
			throw new WebDataException("印模启用日期不能大于印模结束日期");
		
		Long id = templateVO.getId();
		Template t = templateDao.getTemplate(id);
		
		if (t == null) 
			throw new WebDataException("修改失败,印模不存在");
		String prePhotoPath = t.getPhotoPath();
		// 校验信息
		isModify(t, "所选印模" + t.getName(), "Template which id is " + t.getId() + " and name is " + t.getName());

		String tName = templateVO.getName();
		List<Template> tempTemplateList = templateDao.getTemplate(tName);
		if (tempTemplateList.size() > 0 && !t.getName().equals(tName)) 
			throw new WebDataException("印模名称重复");
		
		t.setName(templateVO.getName());
		t.setType(templateVO.getType());
		t.setCompanyId(templateVO.getCompanyId());
		t.setIsAuditReq(templateVO.getIsAuditReq());
		t.setIsAuthCertGenSeal(templateVO.getIsAuthCertGenSeal());
		t.setIsAuthCertDownload(templateVO.getIsAuthCertDownload());
		t.setIsDownload(templateVO.getIsDownload());
		t.setTransparency(templateVO.getTransparency());
		t.setRemark(templateVO.getRemark());
		t.setUpdateTime(DateUtil.getCurrentTime());
		t.setNotBefor(templateVO.getNotBefor());
		t.setNotAfter(templateVO.getNotAfter());
		Long photoId = t.getPhotoDataId();
		
		// 修改图片
		if (photoData != null || templateVO.getType()==3 ) {
			if (photoId != 0) {
				photoDataDao.deletePhotoData(photoId);
				// 增加删除记录
				idDeleteDao.insertIDDelete(photoId, Constants.TABLE_SEAL_PHOTO_DATA);
			}
			if(photoData != null) {
				photoData.setGenerateTime(DateUtil.getCurrentTime());
				photoData.setUpdateTime(DateUtil.getCurrentTime());
				// 保存印模图片
				photoId = photoDataDao.insertPhotoData(photoData);
				t.setPhotoDataId(photoId);
				t.setPhotoPath(templateVO.getPhotoPath());
			}else {
				t.setPhotoDataId(-1L);
				t.setPhotoPath("");
			}
		}

		int r = templateDao.updateTemplate(t);
		if (r == 0)
			throw new WebDataException("修改印模失败");

		// 修改用户关联记录
		userTemplateDao.deleteUserTemplate(id);
		// 增加删除记录
		idDeleteDao.insertIDDelete(photoId, Constants.TABLE_SEAL_USER_TEMPLATE);
		
		String userIds = templateVO.getUserIds();
		if (StringUtil.isNotBlank(userIds)) {
			String[] ids = userIds.split(Constants.SPLIT_1);
			for (String tempId : ids) {
				Long userId = Long.valueOf(tempId.trim());
				UserTemplate userTemplate = new UserTemplate();
				userTemplate.setUserId(userId);
				userTemplate.setTemplateId(templateVO.getId());
				userTemplate.setGenerateTime(DateUtil.getCurrentTime());
				userTemplate.setUpdateTime(DateUtil.getCurrentTime());
				userTemplateDao.insertUserTemplate(userTemplate);
			}

		}
		
		// 修改成功删除原来图片
		if ((photoData != null || templateVO.getType()==3) && StringUtil.isNotBlank(prePhotoPath))
			FileUtil.deleteFile(prePhotoPath);
	}

	/**
	 * 修改印模状态
	 * 
	 * @param ids
	 * @param status
	 * @throws Exception
	 */
	public void updateTemplateStatus(String ids, Integer status) throws Exception {
		String id[] = ids.split(Constants.SPLIT_1);
		long systime = DateUtil.getCurrentTime();
		String macs = "";
		for (int i = 0; i < id.length; i++) {
			Template t = templateDao.getTemplate(Long.parseLong(id[i]));
			// 校验信息
			isModify(t, "所选印模" + t.getName(), "Template which id is " + t.getId() + " and name is " + t.getName());

			t.setUpdateTime(systime);
			t.setStatus(status);
			macs = t.calMac(); // 重新计算MAC值

			int r = templateDao.updateTemplateStatus(Long.parseLong(id[i]), status, systime, macs);
			if (r == 0)
				throw new WebDataException("操作失败");
		}
	}

	/**
	 * 分页查询印模信息
	 * 
	 * @param page
	 * @param templateVO
	 * @return
	 * @throws Exception 
	 */
	public Page<TemplateVO> searchTemplate(Page<TemplateVO> page, TemplateVO templateVO, Long cid) throws Exception {
		Template template = new Template();
		BeanUtils.copyProperties(templateVO, template);
		
		Company com = companyDao.getCompany(cid);
		String treeID = com.getTreeId();
		
		int total = templateDao.searchTotal(template, treeID);

		int start = page.getStart();
		int end = page.getEnd();
		List<Template> tlist = templateDao.searchByPage(template, start, end, treeID);
		List<TemplateVO> tVOlist = new ArrayList<TemplateVO>();
		for (Template t : tlist) {
			TemplateVO tVO = new TemplateVO();
			BeanUtils.copyProperties(t, tVO);

			// 校验信息
			isModify(t, tVO);

			tVOlist.add(tVO);
		}

		// 查询单位
		if (tlist != null && tlist.size() > 0) {
			for (TemplateVO t : tVOlist) {
				CompanyVO companyVO = companyService.getCompany(t.getCompanyId());
				t.setCompanyId(companyVO.getId());
				t.setCompanyName(companyVO.getName());
			}
		}

		page.setTotalNo(total);
		page.setResult(tVOlist);
		return page;
	}

	/**
	 * 获取印模单位tree
	 * 
	 * @param companyVO
	 * @param userVO
	 * @return
	 */
	public List<TreeNode> configCompanyTree(Long pid) {
		List<Company> list = companyDao.getCompanys(pid);
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		
		for (Company c : list) {
			TreeNode node = new TreeNode();
			node.setId(String.valueOf(c.getId()));
			node.setName(c.getName());
			node.setTitle(c.getRemark());
			if (c.getIsParent() == 1) {
				node.setIsParent(true);
			}
			treeList.add(node);
		}

		return treeList;
	}
	
	/**
	 * 通过Id查询
	 * 
	 * @param Id
	 * @return
	 */
	public List<TreeNode> configCompanyTreeById(Long Id) {
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
	 * 根据印模名称获查询印模集合
	 * 
	 * @param tName
	 * @return
	 */
	public List<TemplateVO> getTemplateByName(String tName) {
		List<Template> tlist = templateDao.getTemplate(tName);
		List<TemplateVO> tVOlist = new ArrayList<TemplateVO>();
		for (Template t : tlist) {
			TemplateVO tVO = new TemplateVO();
			BeanUtils.copyProperties(t, tVO);
			tVOlist.add(tVO);
		}
		return tVOlist;
	}

}
