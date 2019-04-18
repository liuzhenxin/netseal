package cn.com.infosec.netseal.webserver.controller.audit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.entity.vo.RequestVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.entity.vo.TemplateVO;
import cn.com.infosec.netseal.common.entity.vo.UserVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.Base64;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.OidUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.audit.AuditServiceImpl;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.request.RequestServiceImpl;
import cn.com.infosec.netseal.webserver.service.template.TemplateServiceImpl;
import cn.com.infosec.netseal.webserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.SessionUtil;

/**
 * 印章申请审核
 */
@RequestMapping(value = "/audit")
@Controller
public class AuditController extends BaseController {
	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private AuditServiceImpl auditService;
	@Autowired
	private RequestServiceImpl requestService;
	
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private TemplateServiceImpl templateService;

	/**
	 * 进入印章审核
	 * 
	 * @param requestAudit
	 * @return
	 */
	@RequestMapping(value = "toAuditSeal")
	public String toAuditSeal(Long id) throws Exception {
		RequestVO requestVO = requestService.getRequest(id);
		httpRequest.setAttribute("request", requestVO);
		// 用户证书
		if (requestVO != null) {
			CertVO certVO = certService.getCert(requestVO.getCertId());
			if (certVO != null) 
				requestVO.setCertSn(certVO.getCertSn());
		}
		// 管理员证书
		SysUserVO sysUserVO = SessionUtil.getSysUser(httpSession);
		List<CertVO> certList = certService.getCertBySysUserId(sysUserVO.getId());
		if (certList != null && certList.size() > 0) {
			CertVO certVO = certList.get(0);
			httpRequest.setAttribute("adminCertSn", certVO.getCertSn());
		}
		return "audit/auditSeal";
	}

	/**
	 * 印章审核, 首先获取印章签名数据
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "auditSealSign")
	public ModelAndView auditSealSign(RequestVO requestVO) throws Exception {
		SysUserVO sysUser = SessionUtil.getSysUser(httpSession);
		Seal seal = new Seal();
		try {
			RequestVO tempRequest = requestService.getRequest(requestVO.getId());
			tempRequest.setSysUserId(sysUser.getId());
			
			// 获取管理员证书
			List<CertVO> certVOList = certService.getCertBySysUserId(sysUser.getId());
			if (certVOList.size() == 0)
				throw new WebDataException("该管理员未绑定证书");

			if (certVOList.size() >= 2)
				throw new WebDataException("该管理员证书绑定不唯一");

			CertVO certVO = certVOList.get(0);
			// 校验证书信息
			if (!certService.isModify(certVO.getId()))
				throw new WebDataException("管理员所绑定证书信息被篡改");

			// 得到证书摘要算法
			byte[] certData = certService.getCertData(certVO.getCertPath(), certVO.getCertDataId());
			X509CertEnvelope cert = CertUtil.parseCert(certData);
			String hashAlg = OidUtil.getHashAlg(cert.getSigAlgOID()).toLowerCase();
			
			byte[] sealData = auditService.auditSealSign(tempRequest, seal);
			Properties extMsg = new Properties();
			extMsg.put("generateTime", seal.getGenerateTime());
			extMsg.put("sealData", Base64.encode(sealData));
			extMsg.put("hashAlg", hashAlg);
			return getModelAndView(getSuccMap(extMsg));
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}


	}

	/**
	 * 印章审核, 签名后的印章数据生成印章
	 * 
	 * @param requestAudit
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_AUDITSEAL)
	@RequestMapping(value = "auditSeal")
	public ModelAndView auditSeal(RequestVO requestVO, String signData, String toSignData) throws Exception {
		SysUserVO sysUser = SessionUtil.getSysUser(httpSession);
		try {
			RequestVO tempRequest = requestService.getRequest(requestVO.getId());
			tempRequest.setUsedLimit(requestVO.getUsedLimit());
			tempRequest.setRemark(requestVO.getRemark());
			tempRequest.setSysUserId(sysUser.getId());
			tempRequest.setGenerateTime(requestVO.getGenerateTime());

			auditService.auditSeal(tempRequest, signData, toSignData);
			return getModelAndView(getSuccMap());
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 删除印章申请
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DELREQUEST)
	@RequestMapping(value = "delRequest")
	public ModelAndView delRequest(String id) throws Exception {
		try {
			requestService.deleteRequest(id);
			return getModelAndView(getSuccMap("印章删除成功"));
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 审核列表
	 * 
	 * @param page
	 * @param requestAudit
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "auditList")
	public String requestAuditList(Page<RequestVO> page, RequestVO requestVO) throws Exception {
		// requestVO.setStatus(0);// 末审核
		SysUserVO sysUserVO = SessionUtil.getSysUser(httpSession);
		page = requestService.searchRequest(page, requestVO, sysUserVO.getCompanyId());
		httpRequest.setAttribute("page", page);
		// 管理员证书
		List<CertVO> certList = certService.getCertBySysUserId(sysUserVO.getId());
		if (certList != null && certList.size() > 0) {
			CertVO certVO = certList.get(0);
			httpRequest.setAttribute("adminCertSn", certVO.getCertSn());
		}
		return "audit/auditList";
	}

	/**
	 * 单项ID查询
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "requestView")
	public String requestView(Long id) {
		RequestVO requestVO = requestService.getRequest(id);
		httpRequest.setAttribute("request", requestVO);

		UserVO user = userService.getUser(requestVO.getUserId());// 获取签章人信息
		if (user != null) {
			httpRequest.setAttribute("uName", user.getName());
			httpRequest.setAttribute("companyName", user.getCompanyName());
		}
		CertVO certVO = certService.getCert(requestVO.getCertId());// 获取证书信息
		if (certVO != null) {
			httpRequest.setAttribute("certDn", certVO.getCertDn());
		}
		TemplateVO templateVO = templateService.getTemplate(requestVO.getTemplateId());
		if (templateVO != null) {
			requestVO.setTemplateName(templateVO.getName());
		}
		return "audit/requestShow";
	}

	// 查看图片
	@RequestMapping(value = "viewPhoto")
	public void viewPhoto(Long id, HttpServletRequest request, HttpServletResponse response) {
		if (id == null) {
			LoggerUtil.errorlog("查看图片为空");
			return;
		}

		RequestVO requestVO = requestService.getRequest(id);
		if (StringUtil.isNotBlank(requestVO.getPhotoPath())) { 
			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=photo" + id.intValue());
			response.setHeader("Set-Cookie", "cookiename=cookievalue; path=/; Domain=domainvaule; Max- age=seconds; HttpOnly");
			InputStream inputStream = null;
			OutputStream os = null;
			try {
				os = response.getOutputStream();
				byte[] b = requestService.getRequestPhoto(id);
				os.write(b, 0, b.length);

			} catch (Exception e) {
				LoggerUtil.errorlog("查看申请图片错误", e);
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

}
