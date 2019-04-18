package cn.com.infosec.netseal.webserver.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.Image;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.entity.vo.RequestVO;
import cn.com.infosec.netseal.common.entity.vo.SealVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.entity.vo.TemplateVO;
import cn.com.infosec.netseal.common.entity.vo.UserVO;
import cn.com.infosec.netseal.common.exceptions.ClientException;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.request.RequestServiceImpl;
import cn.com.infosec.netseal.webserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.webserver.service.template.TemplateServiceImpl;
import cn.com.infosec.netseal.webserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.SessionUtil;
import cn.com.infosec.netseal.webserver.util.TreeNode;

/**
 * 签章人管理
 * 
 */
@RequestMapping(value = "/userManage")
@Controller
public class UserManageController extends BaseController {

	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private RequestServiceImpl requestService;
	@Autowired
	private TemplateServiceImpl templateServiceImpl;
	/**
	 * 添加用户跳转
	 * 
	 * @return
	 */
	@RequestMapping(value = "toAddUser")
	public String toAddUser() {

		return "userManage/userAdd";
	}

	/**
	 * 增 添加用户 签章人
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "/addUser")
	@SealLog(optype = Constants.LOG_OPTYPE_ADDUSER)
	public ModelAndView addUser(UserVO userVO) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<UserVO> userVoList = userService.getUser(userVO.getName(), userVO.getCompanyId());
		if (userVoList.size() == 0) {
			try {
				userService.insertUser(userVO);
				resultMap.put("success", true);
			} catch (WebDataException e) {
				resultMap.put("message", e.getMessage());
			} catch (ClientException e) {
				resultMap.put("message", e.getMessage());
			} catch (Exception e) {
				throw e;
			}

		} else
			resultMap.put("message", "同一个单位中用户已存在");

		return getModelAndView(resultMap);

	}

	/**
	 * 删除
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/delUser")
	@SealLog(optype = Constants.LOG_OPTYPE_DELUSER)
	public ModelAndView delUser(String id) throws Exception {
		try {
			userService.deleteUsers(id);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("删除签章人成功"));
	}

	/**
	 * 修改用户跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "toEditUser")
	public String toEditUser(Long id) {
		UserVO user = userService.getUser(id);
		httpRequest.setAttribute("user", user);

		List<CertVO> certList = certService.getCertByUserId(id);
		httpRequest.setAttribute("certList", certList);

		return "userManage/userEdit";
	}

	/**
	 * 修改用户
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/editUser")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITUSER)
	public ModelAndView editUser(UserVO userVO) throws Exception {
		try {
			userService.updateUser(userVO);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("操作成功"));

	}

	/**
	 * 用户列表
	 * 
	 * @param page
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userList")
	public String userList(Page<UserVO> page, UserVO userVO, HttpServletRequest request) throws Exception {
		SysUserVO sessionSysUser = (SysUserVO) request.getSession().getAttribute("sysUser");
		page = userService.searchUser(page, userVO, sessionSysUser.getCompanyId());
		httpRequest.setAttribute("page", page);
		httpRequest.setAttribute("user", userVO);

		return "userManage/userList";
	}

	/**
	 * 查询分页
	 * 
	 * @param page
	 * @param sysUser
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userSearch")
	public Map<String, Object> sysUserSearch(Page<UserVO> page, UserVO userVO, HttpServletRequest request) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SysUserVO sessionSysUser = (SysUserVO) request.getSession().getAttribute("sysUser");
		page = userService.searchUser(page, userVO, sessionSysUser.getCompanyId());
		modelMap.put("page", page);
		return modelMap;
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "viewUser")
	public String viewUser(Long id) {

		UserVO user = userService.getUser(id);
		httpRequest.setAttribute("user", user);

		String generateTimeCN = DateUtil.getDate(user.getGenerateTime());
		httpRequest.setAttribute("generateTimeCN", generateTimeCN);
		return "userManage/userShow";
	}

	/**
	 * 设置单位树
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "configCompanyTree", produces = "application/json;charset=UTF-8")
	public List<TreeNode> configCompanyTree(Long id) {
		List<TreeNode> treeList = new ArrayList<TreeNode>();
		
		if (id != null) 
			treeList = userService.getCompanyTreeByPid(id);
		else {
			SysUserVO sysVo = (SysUserVO) httpSession.getAttribute("sysUser");
			treeList = userService.getCompanyTreeById(sysVo.getCompanyId());
		}
		
		return treeList;
	}

	/**
	 * 进入印章申请
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "toRequestSeal")
	public String toRequestSeal(Long userId) {
		try {
			UserVO u = userService.getUser(userId);
			// 获取有权限的印模
			List<TemplateVO> templateList = requestService.getRequestTemplate(userId, u.getCompanyId());

			httpRequest.setAttribute("templateList", templateList);
			httpRequest.setAttribute("user", u);

			List<CertVO> certList = certService.getCertByUserId(userId, Constants.USAGE_SIGNATURE);
			List<CertVO> certListEnc = certService.getCertByUserId(userId, Constants.USAGE_SIGN_ENC);
			certList.addAll(certListEnc);
			httpRequest.setAttribute("certList", certList);

		} catch (Exception e) {
			LoggerUtil.errorlog("to request seal error", e);
		}
		return "userManage/requestSeal";
	}

	/**
	 * 印章申请
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_REQUESTSEAL)
	@RequestMapping(value = "requestSeal")
	public ModelAndView requestSeal(HttpServletRequest request,RequestVO requestVO, String userId, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		try {
			// 判断印章名称是否重复
			SealVO sealVO = new SealVO();
			sealVO.setName(requestVO.getName());
			List<SealVO> sealList = sealService.getSeals(sealVO);

			RequestVO reqVO = new RequestVO();
			reqVO.setName(requestVO.getName());
			List<RequestVO> requestVOs = requestService.getRequests(reqVO);

			if (sealList.size() > 0 || requestVOs.size() > 0) {
				throw new WebDataException("印章名称重复");
			}

			requestVO.setGenerateTime(DateUtil.getCurrentTime());
			requestVO.setUpdateTime(DateUtil.getCurrentTime());
			requestVO.setUsedLimit(0);
			Long sysUserId = SessionUtil.getSysUserId(httpSession);
			
			byte[] fileByte = null;
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if(multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("photoFile");
				if(file != null && file.getSize() > 0) {
					fileByte = file.getBytes();
					String photoType = "." + FileUtil.getFileSuffix(file.getOriginalFilename());
					if(!photoType.equals(Constants.PHOTO_SUFFIX)) {
						throw new WebDataException("图片格式错误,请选择" + Constants.PHOTO_SUFFIX + "文件");
					}
					
					try {
						Image.getInstance(file.getBytes());
					}catch(Exception e) {
						throw new WebDataException("印模图片无效");
					}
					
					long fileSize = file.getSize();
					if(fileSize / 1024 > Constants.PHOTO_SIZE) {
						throw new WebDataException("印模图片不超过" + Constants.PHOTO_SIZE + "KB");
					}
					
				}
			}
			
			
			requestService.insertRequest(requestVO, sysUserId, userId,fileByte);
			response.getWriter().write("ok");
		} catch (WebDataException e) {
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			throw e;
		}
		
		return null;
	}

	/**
	 * 删除证书
	 * 
	 * @param certDN
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DELCERT)
	@RequestMapping(value = "delCert")
	public ModelAndView delCert(String certDN, Long userId) throws Exception {
		try {
			userService.deleteCert(certDN, userId);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("删除证书成功"));
	}
	
	/**
	 * 印模类型
	 * @param 
	 * @return
	 * @throws
	 */
	@RequestMapping(value = "getTemplate")
	public ModelAndView getTemplate(Long templateId) throws Exception {
			TemplateVO templateVO = templateServiceImpl.getTemplate(templateId);
			Properties extMsg = new Properties();
			extMsg.put("type", templateVO.getType());
			return getModelAndView(getSuccMap(extMsg));
	}

}
