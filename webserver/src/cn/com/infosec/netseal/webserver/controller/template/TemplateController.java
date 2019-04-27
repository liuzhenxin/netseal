package cn.com.infosec.netseal.webserver.controller.template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.vo.RequestVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.entity.vo.TemplateVO;
import cn.com.infosec.netseal.common.entity.vo.UserVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.photo.PhotoUitl;
import cn.com.infosec.netseal.itextpdf.text.Image;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.webserver.service.request.RequestServiceImpl;
import cn.com.infosec.netseal.webserver.service.template.TemplateServiceImpl;
import cn.com.infosec.netseal.webserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.webserver.util.CommonUtil;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.TreeNode;

/**
 * 印模
 */
@RequestMapping(value = "/template")
@Controller
public class TemplateController  extends BaseController{

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private TemplateServiceImpl templateService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private RequestServiceImpl requestService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;
	@Autowired
	private HttpSession httpSession;

	/**
	 * 制作印模跳转
	 * 
	 * @return
	 */
	@RequestMapping(value = "toAddTemplate")
	public String toAddTemplate(TemplateVO templateVO) {

		return "template/templateAdd";
	}

	/**
	 * 制作印模
	 * 
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_ADDTEMPLATE)
	@RequestMapping(value = "addTemplate")
	public ModelAndView addTemplate(HttpServletRequest request, TemplateVO templateVO, HttpServletResponse response) throws Exception {
		// Map<String, Object> resultMap = new HashMap<String, Object>();
		response.setContentType("text/html;charset=utf-8");
		String path = Constants.PHOTO_PATH;
		String fileName = path + DateUtil.getDateDir() + "/" + FileUtil.getFileName() + Constants.PHOTO_SUFFIX;
		PhotoData photoData = null;
		try {

			String tName = templateVO.getName();
			List<TemplateVO> tempList = templateService.getTemplateByName(tName);
			if (tempList.size() > 0){

				throw new WebDataException("印模名称重复");
			}

			long notBefor = CommonUtil.timeStrStart(templateVO.getNotBeforCn());
			long notAfter = CommonUtil.timeStrEnd(templateVO.getNotAfterCn());

			long nowTime = System.currentTimeMillis();
			if (nowTime > notAfter){

				throw new WebDataException("过期印模,无法添加");
			}

			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());

			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("photoFile");
				
				if (file !=null && file.getSize() > 0) {
					byte[] fileByte = PhotoUitl.convertBGColor(file.getBytes(),(int) (templateVO.getTransparency()*2.55));
					String photoType = "." + FileUtil.getFileSuffix(file.getOriginalFilename());
					if (!photoType.equals(Constants.PHOTO_SUFFIX)) {

						throw new WebDataException("图片格式错误,请选择" + Constants.PHOTO_SUFFIX + "文件");
					}

					try {
						Image.getInstance(file.getBytes());
					} catch (Exception e) {
						throw new WebDataException("印模图片无效");
					}
					long fileSize = file.getSize();
					if (fileSize / 1024 <= Constants.PHOTO_SIZE) {
						FileUtil.storeFile(fileName, fileByte);
						templateVO.setPhotoPath(fileName);
						templateVO.setIsPhoto(1);
						
						// 图片数据
						photoData = new PhotoData();
						photoData.setGenerateTime(DateUtil.getCurrentTime());
						photoData.setData(fileByte); 
						photoData.setUpdateTime(DateUtil.getCurrentTime());

					} else {
						throw new WebDataException("印模图片不超过" + Constants.PHOTO_SIZE + "KB");
					}
				}else {
					templateVO.setIsPhoto(0);
				}
			}
			templateVO.setUpdateTime(DateUtil.getCurrentTime());
			templateVO.setNotBefor(notBefor);
			templateVO.setNotAfter(notAfter);
			templateService.insertTemplate(templateVO, photoData);
			
			response.getWriter().write("ok");
		} catch (WebDataException ex) {
			FileUtil.deleteFile(fileName);
			response.getWriter().write(ex.getMessage());
		} catch (Exception e) {
			FileUtil.deleteFile(fileName);
			throw e;
		}
		return null;
	}

	/**
	 * 删除印模
	 * 
	 * @return
	 * @throws Exception 
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DELTEMPLATE)
	@RequestMapping(value = "delTemplate")
	public ModelAndView delTemplate(String id) throws Exception {
		try {
			templateService.deleteTemplate(id);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}

		return getModelAndView(getSuccMap("印模删除成功"));
	}

	/**
	 * 修改跳转
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "toEditTemplate")
	public String toEditTemplate(Long id) throws Exception {
		TemplateVO templateVO = templateService.getTemplateWithUser(id);

		request.setAttribute("template", templateVO);
		return "template/templateEdit";
	}

	/**
	 * 修改印模信息
	 * 
	 * @param request
	 * @param template
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_EDITTEMPLATE)
	@RequestMapping(value = "editTemplate")
	public ModelAndView editTemplate(HttpServletRequest request, TemplateVO templateVO, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		// Map<String, Object> resultMap = new HashMap<String, Object>();
		String path = Constants.PHOTO_PATH;
		String fileName = path + DateUtil.getDateDir() + "/" + FileUtil.getFileName() + Constants.PHOTO_SUFFIX;
		try {
			PhotoData photoData = null;
			// 存在申请中的印章，印模不允许修改
			List<RequestVO> list = requestService.requestList(templateVO.getId());
			if (list != null && list.size() > 0) {
				/*
				 * resultMap.put("success", false); resultMap.put("message", "存在申请中的印章,印模不允许修改");
				 */
				response.getWriter().write("存在申请中的印章,印模不允许修改");
				return null;
			}
			long notBefor = CommonUtil.timeStrStart(templateVO.getNotBeforCn());
			long notAfter = CommonUtil.timeStrEnd(templateVO.getNotAfterCn());
			long nowTime = System.currentTimeMillis();
			if (nowTime > notAfter){

				throw new WebDataException("过期印模,修改失败");
			}

			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());

			if (multipartResolver.isMultipart(request)) { 
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("photoFile");
				
				TemplateVO template =templateService.getTemplate(templateVO.getId());
				boolean  flag = (template.getTransparency()).equals(templateVO.getTransparency()); 
				if ((file != null && file.getSize() > 0)||(!flag)&&(templateVO.getType()!=3)) {
 				    byte[] fileByte =null;
					if(file != null && file.getSize() > 0) {
						 String photoType = "." + FileUtil.getFileSuffix(file.getOriginalFilename());
						if (!photoType.equals(Constants.PHOTO_SUFFIX)) {

							throw new WebDataException("图片格式错误,请选择" + Constants.PHOTO_SUFFIX + "文件");
						}

						try {
							Image.getInstance(file.getBytes());
						} catch (Exception e) {
							throw new WebDataException("印模图片无效");
						}
						
						long fileSize = file.getSize();
						if (fileSize / 1024 > Constants.PHOTO_SIZE){

							throw new WebDataException("印模图片不超过" + Constants.PHOTO_SIZE + "KB");
						}
						fileByte = PhotoUitl.convertBGColor(file.getBytes(),(int) (templateVO.getTransparency()*2.55));
						
							
					}else {
						 fileByte = PhotoUitl.convertBGColor(FileUtil.getFile(templateVO.getPhotoPath()),(int) (templateVO.getTransparency()*2.55));
					}
						FileUtil.storeFile(fileName, fileByte);
						templateVO.setPhotoPath(fileName);
						templateVO.setIsPhoto(1);
						
						// 图片数据
						photoData = new PhotoData();
						photoData.setGenerateTime(DateUtil.getCurrentTime());
						photoData.setUpdateTime(DateUtil.getCurrentTime());
						photoData.setData(fileByte);
				}else {
					 templateVO.setIsPhoto(0);
				}
			}
			templateVO.setUpdateTime(DateUtil.getCurrentTime());
			templateVO.setNotBefor(notBefor);
			templateVO.setNotAfter(notAfter);
			templateVO.setGenerateTime(DateUtil.getCurrentTime());
			templateVO.setUpdateTime(DateUtil.getCurrentTime());
			templateService.updateTemplate(templateVO, photoData);
			/*
			 * resultMap.put("success", true); resultMap.put("message", "编辑印模成功");
			 */
			response.getWriter().write("ok");
		} catch (WebDataException e) {
			FileUtil.deleteFile(fileName);
			/*
			 * resultMap.put("success", false); resultMap.put("message", e.getMessage());
			 */
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			FileUtil.deleteFile(fileName);
			throw e;
		}
		return null;
	}


	/**
	 * 印模列表
	 * 
	 * @param page
	 * @param template
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "templateList")
	public String templateList(Page<TemplateVO> page, TemplateVO templateVO) throws Exception {
		SysUserVO sessionSysUser = (SysUserVO) request.getSession().getAttribute("sysUser");
		page = templateService.searchTemplate(page, templateVO, sessionSysUser.getCompanyId());
		request.setAttribute("page", page);

		return "template/templateList";
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
	@RequestMapping(value = "/templateSearch")
	public ModelAndView templateSearch(Page<TemplateVO> page, TemplateVO templateVO) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SysUserVO sessionSysUser = (SysUserVO) request.getSession().getAttribute("sysUser");
		page = templateService.searchTemplate(page, templateVO, sessionSysUser.getCompanyId());
		resultMap.put("page", page);
		return new ModelAndView(new MappingJacksonJsonView(), resultMap);
	}

	/**
	 * 查看印模图片
	 * 
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "viewPhoto")
	public void viewPhoto(Long id, HttpServletRequest request, HttpServletResponse response) {
		if (id == null) {
			LoggerUtil.errorlog("下载文件为空");
			return;
		}

		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=photo" + id.intValue());
		response.setHeader("Set-Cookie", "cookiename=cookievalue; path=/; Domain=domainvaule; Max- age=seconds; HttpOnly");

		TemplateVO template = templateService.getTemplate(id);
		if (StringUtil.isNotBlank(template.getPhotoPath())) {
			Long PhotoDataId = template.getPhotoDataId();
			OutputStream os = null;
			try {
				os = response.getOutputStream();
				if(PhotoDataId != -1L) {
				byte[] photoData = photoDataService.getPhotoData(template.getPhotoPath(), PhotoDataId);
				os.write(photoData, 0, photoData.length);
				}else {
					response.getWriter().write("手写章没有图片");
				}
			} catch (Exception e) {
				LoggerUtil.errorlog("查看印模图片错误", e);
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * 印模信息详情跳转
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/viewTemplate")
	public String viewTemplate(Long id) throws Exception {
		TemplateVO templateVO = templateService.getTemplateWithUser(id);

		request.setAttribute("template", templateVO);

		return "template/templateView";
	}

	/**
	 * 修改印模状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 * @throws Exception 
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_UPDATETEMPLATESTATUS)
	@RequestMapping(value = "updateTemplateStatus")
	public ModelAndView updateTemplateStatus(String id, Integer status) throws Exception {
		if (id != null) {
			id = StringUtils.join(id.split(","), ",");
		}
		try {
			templateService.updateTemplateStatus(id, status);

		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("修改印模操作成功"));
	}

	/**
	 * 选取单位跳转
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "toConfigCompany")
	public String toConfigCompany(Long id) throws Exception {
		if (id != null) {
			TemplateVO templateVO = templateService.getTemplate(id);
			request.setAttribute("template", templateVO);
		}
		return "template/templateCompanyConfig";
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
		
		if (id != null) {
			treeList = templateService.configCompanyTree(id);
		} else {
			SysUserVO sysVo = (SysUserVO) httpSession.getAttribute("sysUser");
			treeList = templateService.configCompanyTreeById(sysVo.getCompanyId());
		}
		
		return treeList;
	}

	/**
	 * 公司下用户列表
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "configUserList", produces = "application/json;charset=UTF-8")
	public Page<UserVO> configUserList(Page<UserVO> page, UserVO userVO, Long cid) {
		// page = userService.searchUser(page, user);
		page = userService.searchUserPageByCompanyId(page, userVO, cid);// 单位下所有的员工
		return page;
	}

	/**
	 * 查询分页
	 * 
	 * @param page
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/SearchUserByCompanyId")
	public Map<String, Object> SearchUserByCompanyId(Page<UserVO> page, UserVO userVO, Long cid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		page = userService.searchUserPageByCompanyId(page, userVO, cid);// 单位下所有的员工
		modelMap.put("page", page);
		return modelMap;
	}

}
