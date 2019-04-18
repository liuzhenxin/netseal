package cn.com.infosec.netseal.webserver.controller.seal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.alibaba.fastjson.JSONObject;

import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.entity.vo.SealVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.itextpdf.text.Image;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.webserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.webserver.service.template.TemplateServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.SessionUtil;
import cn.com.infosec.util.Base64;

/**
 * 印章管理
 */
@RequestMapping(value = "/sealManage")
@Controller
public class SealManageController extends BaseController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private TemplateServiceImpl templateService;
	@Autowired
	private CertDaoImpl certDao;

	/**
	 * 印章管理
	 * 
	 * @param seal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "sealList")
	public String sealList(Page<SealVO> page, SealVO sealVO, HttpServletRequest request) throws Exception {
		SysUserVO sessionSysUser = (SysUserVO) request.getSession().getAttribute("sysUser");
		page = sealService.searchSeal(page, sealVO, sessionSysUser.getCompanyId());
		request.setAttribute("page", page);
		request.setAttribute("seal", sealVO);
		return "seal/sealList";
	}

	/**
	 * 修改印章状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "updateSealStatus")
	@SealLog(optype = Constants.LOG_OPTYPE_UPDATESEALSTATUS)
	public ModelAndView updateSealStatus(String id, Integer status) throws Exception {
		if (id != null) {
			id = StringUtils.join(id.split(","), ",");
		}
		try {
			sealService.updateStatus(id, status);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}

	/**
	 * 删除印章
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "delSeal")
	@SealLog(optype = Constants.LOG_OPTYPE_DELSEAL)
	public ModelAndView delSeal(String id) throws Exception {
		try {
			sealService.deleteSeal(id);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}

	/**
	 * 修改印章跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "toEditSeal")
	public String toEditSeal(Long id) {
		SealVO sealVO = sealService.getSeal(id);
		request.setAttribute("seal", sealVO);
		//request.setAttribute("templateType", sealVO.getType());

		// 管理员证书
		SysUserVO sysUserVO = SessionUtil.getSysUser(httpSession);
		List<CertVO> certList = certService.getCertBySysUserId(sysUserVO.getId());
		if (certList != null && certList.size() > 0) {
			CertVO certVO = certList.get(0);
			request.setAttribute("adminCertSn", certVO.getCertSn());
		}

		return "seal/sealEdit";
	}

	/**
	 * 修改印章 不需要重新生成印章
	 * 
	 * @param request
	 * @param sealVO
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "editSeal")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITSEAL)
	public ModelAndView editSeal(HttpServletRequest request, SealVO sealVO, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html;charset=utf-8");
		try {
//			long notBefor = CommonUtil.timeStrStart(sealVO.getNotBeforCn());
//			long notAfter = CommonUtil.timeStrEnd(sealVO.getNotAfterCn());
//			sealVO.setNotAfter(notAfter);
//			sealVO.setNotBefor(notBefor);
			sealService.updateSeal(sealVO);
			response.getWriter().write("ok");
		} catch (WebDataException e) {
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * 修改印章 首先获取印章签名数据 (审核)
	 * 
	 * @param sealVO
	 * @return
	 */
	@RequestMapping(value = "genAuditSeal")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITSEAL)
	public ModelAndView genAuditSeal(HttpServletRequest request, SealVO sealVO, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html;charset=utf-8");
		SysUserVO sysUser = SessionUtil.getSysUser(httpSession);
		byte[] fileByte = null;

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			MultipartFile file = multiRequest.getFile("photoFile");
			if (file != null && file.getSize() > 0) {
				String photoType = "." + FileUtil.getFileSuffix(file.getOriginalFilename());
				if (!photoType.equals(Constants.PHOTO_SUFFIX))
					throw new WebDataException("图片格式错误,请选择" + Constants.PHOTO_SUFFIX + "文件");

				try {
					Image.getInstance(file.getBytes());
				} catch (Exception e) {
					throw new WebDataException("印章图片无效");
				}

				long fileSize = file.getSize();
				if (fileSize / 1024 > Constants.PHOTO_SIZE)
					throw new WebDataException("印章图片不超过" + Constants.PHOTO_SIZE + "KB");
				
				fileByte = file.getBytes();
			}
			
		}
		try {

			Properties sealInfo = sealService.genAuditSeal(sealVO, sysUser.getId(), fileByte);

			JSONObject json = new JSONObject();
			json.put("success","true");
			json.put("generateTime", sealInfo.get("generateTime"));
			json.put("sealData", sealInfo.get("sealData"));
			json.put("hashAlg", sealInfo.get("hashAlg"));
			response.getWriter().write(json.toString());
			return null;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 修改印章 签名后的印章数据生成印章(审核)
	 * 
	 * @param sealVO
	 * @return
	 */

	@RequestMapping(value = "editAuditSeal")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITSEAL)
	public ModelAndView editAuditSeal(SealVO sealVO, String signData, String toSignData, HttpServletResponse response)
			throws Exception {
		// response.setContentType("text/html;charset=utf-8");
		try {
			sealService.updateSeal(sealVO, signData, toSignData);
			return getModelAndView(getSuccMap());
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 修改印章 需要重新生成印章 (不审核)
	 * 
	 * @param sealVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "editNotAuditSeal")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITSEAL)
	public ModelAndView editNotAuditSeal(HttpServletRequest request,SealVO sealVO, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		byte[] fileByte = null;

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			MultipartFile file = multiRequest.getFile("photoFile");
			if (file != null && file.getSize() > 0) {
				String photoType = "." + FileUtil.getFileSuffix(file.getOriginalFilename());
				if (!photoType.equals(Constants.PHOTO_SUFFIX))
					throw new WebDataException("印章图片格式错误,请选择" + Constants.PHOTO_SUFFIX + "文件");

				try {
					Image.getInstance(file.getBytes());
				} catch (Exception e) {
					throw new WebDataException("印章图片无效");
				}

				long fileSize = file.getSize();
				if (fileSize / 1024 > Constants.PHOTO_SIZE)
					throw new WebDataException("印章图片不超过" + Constants.PHOTO_SIZE + "KB");
				
				fileByte = file.getBytes();
			}
			
		}
		try {
			Long sysUserId = SessionUtil.getSysUserId(httpSession);
			sealService.updateSeal(sealVO, sysUserId,fileByte);
			response.getWriter().write("ok");
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return null;

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
	@RequestMapping(value = "/sealSearch")
	public ModelAndView searchSeal(Page<SealVO> page, SealVO sealVO, HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SysUserVO sessionSysUser = (SysUserVO) request.getSession().getAttribute("sysUser");
		page = sealService.searchSeal(page, sealVO, sessionSysUser.getCompanyId());
		resultMap.put("page", page);
		return new ModelAndView(new MappingJacksonJsonView(), resultMap);
	}

	/**
	 * 查看单条详情
	 * 
	 * @param page
	 * @param sysUser
	 * @return
	 */
	@RequestMapping(value = "sealShow")
	public String sealShow(Long id) {
		SealVO sealVO = sealService.getSeal(id);
		request.setAttribute("seal", sealVO);

		// 查询该印章的印模信息
		// Template template = templateService.getTemplate(sealVO.getTemplateID());
		// request.setAttribute("template", template);
		return "seal/sealShow";
	}

	/**
	 * 印章下载显示跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "sealDownLoadView")
	public String sealDownLoadView(Long id) {

		SealVO sealVO = sealService.getSeal(id);
		request.setAttribute("seal", sealVO);

		return "seal/sealDownLoadView";
	}

	/**
	 * 印章下载
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNSEAL)
	@RequestMapping(value = "sealDownLoad")
	public ModelAndView sealDownLoad(String certDN, Long sealId) throws Exception {
		SysUserVO sysUser = SessionUtil.getSysUser(httpSession);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 验证证书
			certDN = CertUtil.transCertDn(certDN);
			SealVO sealVO = sealService.sealDownLoadVerfy(certDN, sealId, sysUser.getId());

			byte[] sealData = sealService.getSealData(sealVO.getSealPath(), sealVO.getSealDataId());
			resultMap.put("sealData", Base64.encode(sealData));
			resultMap.put("success", true);
		} catch (WebDataException e) {
			resultMap.put("success", false);
			resultMap.put("message", e.getMessage());
		} catch (Exception e) {
			throw e;
		}
		return this.getModelAndView(resultMap);
	}

	// 查看图片
	@RequestMapping(value = "viewPhoto")
	public void viewPhoto(Long id, HttpServletRequest request, HttpServletResponse response) {
		if (id == null) {
			LoggerUtil.errorlog("查看印章图片为空");
			return;
		}

		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=photo" + id.longValue());
		response.setHeader("Set-Cookie",
				"cookiename=cookievalue; path=/; Domain=domainvaule; Max- age=seconds; HttpOnly");

		SealVO seal = sealService.getSeal(id);
		if (StringUtil.isNotBlank(seal.getPhotoPath())) {
			InputStream inputStream = null;
			OutputStream os = null;
			try {
				os = response.getOutputStream();
				byte[] photoData = photoDataService.getPhotoData(seal.getPhotoPath(), seal.getPhotoDataId());
				os.write(photoData, 0, photoData.length);
			} catch (Exception e) {
				LoggerUtil.errorlog("查看印章图片错误", e);
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
