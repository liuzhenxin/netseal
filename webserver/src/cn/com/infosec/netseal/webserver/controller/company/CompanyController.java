package cn.com.infosec.netseal.webserver.controller.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.CompanyVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.company.CompanyServiceImpl;
import cn.com.infosec.netseal.webserver.util.TreeNode;

/**
 *
 */
@RequestMapping(value = "/company")
@Controller
public class CompanyController extends BaseController{

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private CompanyServiceImpl companyService;
	@Autowired
	private HttpSession httpSession;
	
	/**
	 * 单位管理
	 * 
	 * @param company
	 * @return
	 */
	@RequestMapping(value = "manageCompany")
	public String manageCompany(CompanyVO company) {
		return "company/manageCompany";
	}

	/**
	 * 配置companyTree
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeCompany", produces = "application/json;charset=UTF-8")
	public List<TreeNode> treeCompany(Long id) {
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		
		if (id != null) {
			treeList = companyService.findCompanyByPid(id);
		} else {
			SysUserVO sysVo = (SysUserVO) httpSession.getAttribute("sysUser");
			treeList = companyService.findCompanyById(sysVo.getCompanyId());
		}
		
		return treeList;
	}

	/**
	 * 显示companyTree
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "viewCompany")
	public String viewCompany(Long id) {

		CompanyVO companyVO = companyService.getCompany(id);
		request.setAttribute("company", companyVO);

		return "company/companyView";
	}

	/**
	 * 添加跳转
	 * 
	 * @return
	 */
	@RequestMapping(value = "toAddCompany")
	public String toAddCompany(Long pid) {
		request.setAttribute("pid", pid);

		return "company/companyAdd";
	}

	/**
	 * 添加单位
	 * 
	 * @param company
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value = "addCompany")
	@SealLog(optype = Constants.LOG_OPTYPE_ADDCOMPANY)
	public ModelAndView addCompany(CompanyVO companyVO) throws Exception {

		try {
			companyService.insertCompany(companyVO);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("添加单位成功"));
	}

	/**
	 * 修改跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "toEditCompany")
	public String toEditCompany(Long id) {
		CompanyVO companyVO = companyService.getCompany(id);
		request.setAttribute("company", companyVO);
		return "company/companyEdit";
	}

	/**
	 * 修改单位信息
	 * 
	 * @param company
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "editCompany")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITCOMPANY)
	public ModelAndView editCompany(CompanyVO companyVO) throws Exception {
		try {
			companyService.updateCompany(companyVO);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("修改单位成功"));
	}

	/**
	 * 删除单位树节点
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "delCompany")
	@SealLog(optype = Constants.LOG_OPTYPE_DELCOMPANY)
	public ModelAndView delCompany(Long id) throws Exception {
		if (id == null || id == 0) {
			return getModelAndView(getErrMap("此节点不能删除"));
		} else {
			try {
				CompanyVO companyVO = companyService.deleteCompany(id);
				Properties extMsg = new Properties();
				extMsg.put("isParent", companyVO.getIsParent());
				return getModelAndView(getSuccMap("删除单位成功", extMsg));
			} catch (WebDataException e) {
				return getModelAndView(getErrMap(e));
			} catch (Exception e) {
				throw e;
			}
		}
	}

}
