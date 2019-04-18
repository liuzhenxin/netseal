package cn.com.infosec.netseal.webserver.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.UserCertRequestVO;
import cn.com.infosec.netseal.common.entity.vo.UserVO;
import cn.com.infosec.netseal.common.entity.vo.config.NetCertCaVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.request.RequestServiceImpl;
import cn.com.infosec.netseal.webserver.service.user.UserCertRequestServiceImpl;
import cn.com.infosec.netseal.webserver.service.user.UserServiceImpl;

/**
 * 签章人申请证书
 * 
 */
@RequestMapping(value = "/userCertReuqest")
@Controller
public class UserCertRequestController extends BaseController {

	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private UserCertRequestServiceImpl userCertRequestService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private RequestServiceImpl requestService;

	/**
	 * 申请证书跳转
	 * 
	 * @return
	 */
	@RequestMapping(value = "toRequestCert")
	public String toRequestCert(Long userId) {
		try {
			UserVO userVO = userService.getUser(userId);
			
			httpRequest.setAttribute("user", userVO);
			
			NetCertCaVO netCertCaRSA = ConfigUtil.getInstance().getNetCertCaRSA();
			List<String> templateList = netCertCaRSA.getTemplateCnList();
			httpRequest.setAttribute("templateList", templateList);
			
		} catch (Exception e) {
			LoggerUtil.errorlog("to request seal error", e);
		}
		return "userManage/requestCert";
	}
	/** 获取证书模板
	 * @param certType ca证书类型
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTemplateList")
	public ModelAndView getTemplateList(String certType) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			List<String> templateList = null;
			ConfigUtil config = ConfigUtil.getInstance();
			if(Constants.NETCERT_CA_RSA.equals(certType)){
				NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
				templateList = netCertCaRSA.getTemplateCnList();
			}else if(Constants.NETCERT_CA_SM2.equals(certType)){
				NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
				templateList = netCertCaSM2.getTemplateCnList();
			}
			resultMap.put("templateList", templateList);
			resultMap.put("success", true);
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(resultMap);
	}
	/**
	 * 申请证书 
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "/requestCert")
	@SealLog(optype = Constants.LOG_OPTYPE_ADDUSER)
	public ModelAndView requestCert(UserCertRequestVO userCertRequestVO) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
			try {
				UserCertRequestVO requestCert = userCertRequestService.requestCert(userCertRequestVO);
				resultMap.put("requestCertId", requestCert.getId());
				resultMap.put("success", true);
				resultMap.put("message", "申请两码成功");
			} catch (WebDataException e) {
				resultMap.put("message", e.getMessage());
			} catch (Exception e) {
				throw e;
			}

		
		return getModelAndView(resultMap);

	}
	
	@RequestMapping(value = "toDownloadCert")
	public String toDownloadCert(Long requestCertId) {
		try {
			UserCertRequestVO userCertRequestVO = userCertRequestService.getUserCertRequest(requestCertId);
			if(userCertRequestVO!=null){
				UserVO userVO = userService.getUser(userCertRequestVO.getUserId());
				httpRequest.setAttribute("user", userVO);
			}
			httpRequest.setAttribute("userCertRequest", userCertRequestVO);
					
			
		} catch (Exception e) {
			LoggerUtil.errorlog("to request seal error", e);
		}
		return "userManage/downloadCert";
	}
	/**
	 * 证书下载
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadCert")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITUSER)
	public ModelAndView downloadCert(UserCertRequestVO userCertRequestVO) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			userCertRequestVO = userCertRequestService.downloadCert(userCertRequestVO);
			resultMap.put("userCertRequest", userCertRequestVO);
			resultMap.put("success", true);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(resultMap);

	}







}
