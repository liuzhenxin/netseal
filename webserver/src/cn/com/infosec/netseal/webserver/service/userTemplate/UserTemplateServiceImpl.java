package cn.com.infosec.netseal.webserver.service.userTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.userTemplate.UserTemplateDaoImpl;
import cn.com.infosec.netseal.common.entity.po.UserTemplate;

@Service
public class UserTemplateServiceImpl {
	@Autowired
	protected UserTemplateDaoImpl userTemplateDao;

	/**
	 * 增
	 * 
	 * @param userTemplate
	 */
	public void insertUserTemplate(UserTemplate userTemplate) {

		userTemplateDao.insertUserTemplate(userTemplate);
	}

	/**
	 * 按印模id删
	 * 
	 * @param templateId
	 * @return
	 */
	public int deleteUserTemplate(Long templateId) {
		return userTemplateDao.deleteUserTemplate(templateId);
	}

	/**
	 * 按用户id删
	 * 
	 * @param userId
	 * @return
	 */
	public int deleteUserTemplate2(Long userId) {
		return userTemplateDao.deleteUserTemplate2(userId);
	}

	/**
	 * 查询全部
	 * 
	 * @return
	 */
	public List<UserTemplate> getUserTemplate() {
		return userTemplateDao.getUserTemplate();
	}

	/**
	 * 按印模id查询
	 * 
	 * @param templateId
	 * @return
	 */
	public List<UserTemplate> getUserTemplate(Long templateId) {
		return userTemplateDao.getUserTemplate(templateId);
	}

	/**
	 * 按用户id查询
	 * 
	 * @param userId
	 * @return
	 */
	public List<UserTemplate> getUserTemplate2(Long userId) {
		return userTemplateDao.getUserTemplate2(userId);
	}

}
