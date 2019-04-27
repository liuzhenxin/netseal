package cn.com.infosec.netseal.webserver.controller.system.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.NetWorkUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.license.LicenseChecker;
import cn.com.infosec.netseal.common.util.license.LicenseInfo;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.manager.ServiceManager;

/**
 *
 */
@RequestMapping(value = "/system/license")
@Controller
public class LicenseController  extends BaseController {

	@Autowired
	private HttpServletRequest request;
	
	/**
	 * License文件详情显示
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "licenseShow")
	public String licenseShow() {
		String licensePath = Constants.LICENSE_PATH + Constants.LICENSE;
		byte[] licBytes = FileUtil.getFile(licensePath);// 读取License文件
		byte[] entlic = StringUtil.base64Decode(licBytes);
		ConfigUtil config = ConfigUtil.getInstance();
		LicenseInfo li = LicenseChecker.checkLicenseNoDelete(NetWorkUtil.getHostMac(config.getNetworkCard()), entlic);
		if (li != null) {
			String strTimeStart = DateUtil.getDateTime(li.getValidTime_start());
			String strTimeEnd = DateUtil.getDateTime(li.getValidTime_end());
			request.setAttribute("strTimeStart", strTimeStart);
			request.setAttribute("strTimeEnd", strTimeEnd);
			request.setAttribute("li", li);
		}
		return "system/license/licenseShow";
	}

	/**
	 * 下载license.li文件
	 * 
	 * @param
	 * @return
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNLICENSE)
	@RequestMapping(value = "downloadLicense")
	public void downloadLicense(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=NetSeal_" + DateUtil.getDate() + "_bak_" + Constants.LICENSE);

		FileInputStream fis = null;
		OutputStream os = null;

		String filePath = Constants.LICENSE_PATH + Constants.LICENSE;
		try {
			fis = new FileInputStream(filePath);
			os = response.getOutputStream();

			int length = 0;
			byte[] b = new byte[1024];
			while ((length = fis.read(b)) > 0) {
				os.write(b, 0, length);
			}

		} catch (Exception e) {
			LoggerUtil.errorlog("download file " + filePath + " error, ", e);
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
	 * 转到更新License
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping("/toUpdateLicense")
	public String toUpdateLicense() {
		File f = new File(Constants.LICENSE_PATH + Constants.LICENSE_APP);
		request.setAttribute("FileNames", f.getName());
		return "system/license/licenseUpload";
	}

	/**
	 * 上传License文件
	 * 
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/uploadLicense")
	public void uploadLicense(HttpServletRequest request, @RequestParam("licenseFiles") MultipartFile file, HttpServletResponse response, ConfigUtil config, HttpSession httpSession)
			throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String uploadPath = Constants.TMP_PATH + FileUtil.getFileName() + "/";
		File f = new File(uploadPath);
		if (!f.exists())
			f.mkdirs();

		String filePath = uploadPath + Constants.LICENSE;

		if (file.isEmpty()) {
			response.getWriter().write("文件不能为空"); // 上传文件为空
			return;
		}
		if (!file.getOriginalFilename().endsWith(Constants.LICENSE_SUFFIX)) {
			response.getWriter().write("文件扩展名必须为" + Constants.LICENSE_SUFFIX);
			return;
		}
		try {
			file.transferTo(new File(filePath));

			byte[] licBytes = FileUtil.getFile(filePath);// 读取License文件
			byte[] entlic = StringUtil.base64Decode(licBytes);
			LicenseInfo i = LicenseChecker.checkLicenseNoDelete(NetWorkUtil.getHostMac(config.getNetworkCard()), entlic);
			if (i != null) {
				httpSession.setAttribute("licenseInfo", i);
				httpSession.setAttribute("filePath", filePath);
				response.getWriter().write("ok");
			} else {
				response.getWriter().write("验证文件出错,详细信息查看日志"); // 文件不匹配
				if (filePath != null) 
					FileUtil.deleteFileAndDir(filePath);
				
			}
		} catch (Exception e) {
			response.getWriter().write("导入出错,详细信息查看日志");
			LoggerUtil.errorlog("导入出错", e);
			if (filePath != null) 
				FileUtil.deleteFileAndDir(filePath);

		}

	}

	/**
	 * 更新License查看
	 * 
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "/licenseUploadView")
	public String licenseUploadView(HttpSession httpSession) {
		LicenseInfo li = (LicenseInfo) httpSession.getAttribute("licenseInfo");
		String strTimeStart = DateUtil.getDateTime(li.getValidTime_start());
		String strTimeEnd = DateUtil.getDateTime(li.getValidTime_end());
		request.setAttribute("strTimeStart", strTimeStart);
		request.setAttribute("strTimeEnd", strTimeEnd);
		request.setAttribute("li", li);
		return "system/license/licenseUploadView";
	}

	/**
	 * 删除License文件
	 * 
	 * @return
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DELLICENSE)
	@RequestMapping(value = "/deleteLicense")
	public String deleteLicense(HttpSession httpSession) {
		String path = (String) httpSession.getAttribute("filePath");
		if (path != null) {
            FileUtil.deleteFileAndDir(path);
        }
		
		return licenseShow();
	}

	/**
	 * 保存License文件
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveLicense")
	@SealLog(optype = Constants.LOG_OPTYPE_SAVELICENSE)
	public ModelAndView saveLicense(HttpSession httpSession) throws Exception {
		boolean success = true;
		String message = "";
		String path = (String) httpSession.getAttribute("filePath");
		try {
			File tmpLicense = new File(path);
			String tmpLicenseParent = tmpLicense.getParent();
			if (!tmpLicense.exists()) {

				throw new WebDataException("license文件失效, 请重新上传");
			}


			// 检查license数据有效性
			ConfigUtil config = ConfigUtil.getInstance();
			byte[] licenseData = FileUtil.getFile(path);
			byte[] entlic = StringUtil.base64Decode(licenseData);
			LicenseInfo i = LicenseChecker.checkLicenseNoDelete(NetWorkUtil.getHostMac(config.getNetworkCard()), entlic);
			if (i == null) {
				throw new Exception();
			}

			config.setLicenseConfig(i.getMaxCertNum(), i.getMaxThred());
			// 删除原来的
			String strNewPath = Constants.LICENSE_PATH + Constants.LICENSE;
			FileUtil.deleteFile(strNewPath);
			// 将License文件重命名并移动
			File file = new File(strNewPath);
			tmpLicense.renameTo(file);
			boolean result = ServiceManager.reloadConfig();
			if (!result) {
                throw new WebDataException("更新成功,同步配置出错,重启可同步生效,详细信息查看日志");
            }
			message = "更新成功, 实时生效";
			FileUtil.deleteDir(tmpLicenseParent);
		} catch (WebDataException e) {
			if (path != null) {
				FileUtil.deleteFileAndDir(path);
			}
			success = false;
			message = e.getMessage();
		} catch (Exception e) {
			if (path != null) {
				FileUtil.deleteFileAndDir(path);
			}
			success = false;
			message = "文件保存失败, 详细信息查看日志";
			LoggerUtil.errorlog("更新License文件保存失败", e);
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

}
