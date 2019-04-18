package cn.com.infosec.netseal.appserver.service.company;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.company.CompanyDaoImpl;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

@Service
public class CompanyServiceImpl extends BaseService {

	@Autowired
	protected CompanyDaoImpl companyDao;

	public Company getCompany(long ID) {
		return companyDao.getCompany(ID);
	}

	public List<Long> getSubCompanysId(long id) {
		Company company = companyDao.getCompany(id);
		if (company == null)
			throw new NetSealRuntimeException(ErrCode.COMPANY_NOT_EXIST_IN_DB, "company data not exist in db");

		List<Company> companys = companyDao.getCompanys(company.getTreeId());
		List<Long> list = new ArrayList<Long>();

		for (Company comp : companys)
			list.add(comp.getId());

		return list;
	}

	public List<Company> getSubCompanys(Long pid) {
		return companyDao.getCompanys(pid);
	}
	
	/**
	 * 校验信息
	 * @param company
	 * @param msg
	 */
	public void isModify(Company company) {
		isModify(company, "the data of Company which id is " + company.getId() + " and name is " + company.getName());
	}
}
