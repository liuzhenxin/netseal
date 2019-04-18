package cn.com.infosec.netseal.webserver.controller.stamp;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.stamp.StampVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.stamp.StampServiceImpl;

/**
 * 图章管理
 * 
 */
@RequestMapping(value = "/stamp")
@Controller
public class StampController extends BaseController {

	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private StampServiceImpl stampService;
	

	/**
	 * 生成图片跳转
	 * 
	 * @return
	 */
	@RequestMapping(value = "toGenStamp")
	public String toStampGen() {

		List<String> stampStyleList =new ArrayList<String>();
		stampStyleList.add("圆形");
		stampStyleList.add("椭圆形");
		stampStyleList.add("方形");
		stampStyleList.add("长方形");
		httpRequest.setAttribute("stampStyleList", stampStyleList);
		
		return "stamp/stampGen";
	}

	/**
	 * 制作图章文件
	 * 
	 * @param stampVO
	 * @param textFile
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/genStamp")
	@SealLog(optype = Constants.LOG_OPTYPE_GENSTAMP)
	public ModelAndView genStamp(StampVO stampVO, MultipartFile textFile, HttpServletResponse response, HttpSession httpSession) throws Exception {
//		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		response.setContentType("text/html;charset=utf-8");
		String stampPath = null;
		try {
			if (textFile.isEmpty()) 
				throw new WebDataException("文件不存在");
			
			if (!textFile.getOriginalFilename().toLowerCase().endsWith(Constants.TXT_SUFFIX)) 
				throw new WebDataException("文件类型必须为txt");
				
			try {
				stampPath = stampService.gemStamp(stampVO, textFile.getInputStream());
			} catch (Exception e) {
				LoggerUtil.errorlog("制作图章错误", e);
				throw new WebDataException(e.getMessage());
			}
			
			httpSession.setAttribute("stamp_Path", stampPath);
			response.getWriter().write("ok");
		} catch (WebDataException ex){
			response.getWriter().write(ex.getMessage());
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	
	/**
	 * 图章文件下载准备
	 * 
	 * @param stampPath
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/toDownLoadStamp")
	public String toDownLoadStamp(HttpSession httpSession) throws Exception {
		String stampPath = (String) httpSession.getAttribute("stamp_Path");
		httpRequest.setAttribute("stampPath", stampPath);
		
		return "stamp/stampDownLoadView";
	}
	
	
	/**
	 * 下载
	 * 
	 * @param stampPath
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downLoadStamp")
	public void downLoadStamp(String stampPath, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=" + stampPath);
		FileInputStream fis = null;
		OutputStream os = null;
		String zipFile = Constants.STAMP_PATH + DateUtil.getDateDir() + stampPath;
		try {
			os = response.getOutputStream();
			fis = new FileInputStream(zipFile);
			os = response.getOutputStream();
			int length = 0;
			byte[] b = new byte[1024];
			while ((length = fis.read(b)) > 0) {
				os.write(b, 0, length);
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("view stamp error, ", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
		
	}
	
	/**
	 * 预览图章
	 * 
	 * @param stampVO
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/viewStamp")
	public void viewStamp(StampVO stampVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");

		FileInputStream fis = null;
		OutputStream os = null;
		String company = stampVO.getCompany();
		if (StringUtil.isNotBlank(company)) {
			stampVO.setCompany(URLDecoder.decode(company, "utf-8"));
		}
		String name = stampVO.getName();
		if (StringUtil.isNotBlank(name)) {
			stampVO.setName(URLDecoder.decode(name, "utf-8"));
		}
		String fontType = stampVO.getFontType();
		if (StringUtil.isNotBlank(fontType)) {
			stampVO.setFontType(URLDecoder.decode(fontType, "utf-8"));
		}
		try {	
			os = response.getOutputStream();
			byte[] stampData = stampService.viewStamp(stampVO);
			os.write(stampData);
		} catch (Exception e) {
			LoggerUtil.errorlog("view stamp error, ", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
		
	}
	
}
