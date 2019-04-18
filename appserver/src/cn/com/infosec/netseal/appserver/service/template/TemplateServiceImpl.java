package cn.com.infosec.netseal.appserver.service.template;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.util.StringUtil;

@Service
public class TemplateServiceImpl extends BaseService{

	@Autowired
	private TemplateDaoImpl templateDao;

	public List<Template> getTemplate(String name) {
		if (StringUtil.isBlank(name))
			return null;
		List<Template> list = templateDao.getTemplate(name);
		return list;
	}
	
	/**
	 * 校验信息
	 * @param template
	 */
	public void isModify (Template template) {
		isModify(template, "the data of Template which id is " + template.getId() + " and name is " + template.getName());
	}
}
