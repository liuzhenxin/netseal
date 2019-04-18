package cn.com.infosec.netseal.webserver.controller.log;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.AccessLogVO;
import cn.com.infosec.netseal.common.entity.vo.ManageLogVO;
import cn.com.infosec.netseal.common.entity.vo.OperateLogVO;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.accessLog.AccessLogServiceImpl;
import cn.com.infosec.netseal.webserver.service.manageLog.ManageLogServiceImpl;
import cn.com.infosec.netseal.webserver.service.operateLog.OperateLogServiceImpl;
import cn.com.infosec.netseal.webserver.util.LoggerFileUtil;
import cn.com.infosec.netseal.webserver.util.Page;

@RequestMapping(value = "/log")
@Controller
public class LogController extends BaseController {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private AccessLogServiceImpl accessLogService;
	@Autowired
	private ManageLogServiceImpl manageLogService;
	@Autowired
	private OperateLogServiceImpl operateLogService;

	/**
	 * 跳转页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "accessLog")
	public String accessLog() {
		return "log/accessLog";
	}

	/**
	 * 显示访问日志列表
	 * 
	 * @param page
	 * @param accessLog
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "accessLogList")
	public String accessLogList(Page<AccessLogVO> page, AccessLogVO accessLogVO) throws Exception {
		page = accessLogService.searchAccessLog(page, accessLogVO);
		request.setAttribute("page", page);
		return "log/accessLogList";
	}

	/**
	 * 根据条件查询访问日志
	 * 
	 * @param page
	 * @param accessLog
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "accessLogSearch")
	public Map<String, Object> accessLogSearch(Page<AccessLogVO> page, AccessLogVO accessLogVO) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		page = accessLogService.searchAccessLog(page, accessLogVO);
		modelMap.put("page", page);
		return modelMap;
	}

	/**
	 * 访问日志文件
	 * 
	 * @param page
	 * @param accessLog
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "accessLogFileList")
	public String accessLogFileList(Page<AccessLogVO> page, AccessLogVO accessLogVO) throws Exception {
		page = accessLogService.accessLogFileList(page, accessLogVO);
		request.setAttribute("page", page);
		return "log/accessLogFileList";
	}

	/**
	 * 跳转页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "manageLog")
	public String manageLog() {
		return "log/manageLog";
	}

	/**
	 * 管理日志列表
	 * 
	 * @param page
	 * @param manageLog
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "manageLogList")
	public String manageLogList(Page<ManageLogVO> page, ManageLogVO manageLogVO) throws Exception {
		page = manageLogService.searchManageLog(page, manageLogVO);
		request.setAttribute("page", page);
		return "log/manageLogList";
	}

	/**
	 * 根据条件查询管理日志
	 * 
	 * @param page
	 * @param manageLog
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "getmanageLogList")
	public Map<String, Object> manageLogListSearch(Page<ManageLogVO> page, ManageLogVO manageLogVO) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		page = manageLogService.searchManageLog(page, manageLogVO);
		modelMap.put("page", page);
		return modelMap;
	}

	/**
	 * 管理日志文件
	 * 
	 * @param page
	 * @param manageLog
	 * @return
	 */
	@RequestMapping(value = "manageLogFileList")
	public String manageLogFileList(Page<ManageLogVO> page, ManageLogVO manageLogVO) {
		page = manageLogService.manageLogFileList(page, manageLogVO);
		request.setAttribute("page", page);
		return "log/manageLogFileList";
	}

	/**
	 * 跳转页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "operateLog")
	public String operateLog() {
		return "log/operateLog";
	}

	/**
	 * 操作日志列表
	 * 
	 * @param page
	 * @param operateLog
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "operateLogList")
	public String operateLogList(Page<OperateLogVO> page, OperateLogVO operateLogVO) throws Exception {
		page = operateLogService.searchOperateLog(page, operateLogVO);
		request.setAttribute("page", page);
		return "log/operateLogList";
	}

	/**
	 * 根据条件查询操作日志
	 * 
	 * @param page
	 * @param operateLog
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "getoperateLogList")
	public Map<String, Object> operateLogListSearch(Page<OperateLogVO> page, OperateLogVO operateLogVO) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		page = operateLogService.searchOperateLog(page, operateLogVO);
		modelMap.put("page", page);
		return modelMap;
	}

	/**
	 * 查看日志列表的详细错误信息
	 * 
	 * @param page
	 * @param operateLog
	 * @return
	 * @throws ParseException
	 */

	@RequestMapping(value = "operateLogerrMsg")
	public String operateLogerrMsg(Page<OperateLogVO> page, OperateLogVO operateLogVO) throws Exception {
		page = operateLogService.searchOperateLog(page, operateLogVO);
		request.setAttribute("page", page);
		return "log/operateLogerrMsg";
	}

	/**
	 * 跳转错误页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "errorLog")
	public String errorLog() {
		return "log/errorLog";
	}

	/**
	 * 错误日志文件
	 * 
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "errorLogFileList")
	public String errorLogFileList(Page<OperateLogVO> page) {
		File[] fs = LoggerFileUtil.logFile("error");
		int total = fs.length;
		int start = page.getStart();
		int end = page.getEnd();
		List<OperateLogVO> list = new ArrayList<OperateLogVO>();
		for (int i = 1; i <= fs.length; i++) {
			File f = fs[i - 1];
			if (f.isFile() && i >= start && i <= end) {
				OperateLogVO log = new OperateLogVO();
				log.setFileName(f.getName());
				log.setFilePath(f.getAbsolutePath());
				log.setFileSize(f.length() / 1024);
				log.setFileTime(f.lastModified());
				list.add(log);
			}
		}
		page.setTotalNo(total);
		page.setResult(list);

		request.setAttribute("page", page);
		return "log/errorLogFileList";
	}

	/**
	 * 跳转调试日志页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "debugLog")
	public String debugLog() {
		return "log/debugLog";
	}

	/**
	 * 错误日志文件
	 * 
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "debugLogFileList")
	public String debugLogFileList(Page<OperateLogVO> page) {
		File[] fs = LoggerFileUtil.logFile("debug");
		int total = fs.length;
		int start = page.getStart();
		int end = page.getEnd();
		List<OperateLogVO> list = new ArrayList<OperateLogVO>();
		for (int i = 1; i <= fs.length; i++) {
			File f = fs[i - 1];
			if (f.isFile() && i >= start && i <= end) {
				OperateLogVO log = new OperateLogVO();
				log.setFileName(f.getName());
				log.setFilePath(f.getAbsolutePath());
				log.setFileSize(f.length() / 1024);
				log.setFileTime(f.lastModified());
				list.add(log);
			}
		}
		page.setTotalNo(total);
		page.setResult(list);

		request.setAttribute("page", page);
		return "log/debugLogFileList";
	}

	/**
	 * 跳转系统日志页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "systemLog")
	public String systemLog() {
		return "log/systemLog";
	}

	/**
	 * 系统日志文件
	 * 
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "systemLogFileList")
	public String systemLogFileList(Page<OperateLogVO> page) {
		File[] fs = LoggerFileUtil.logFile("system");
		int total = fs.length;
		int start = page.getStart();
		int end = page.getEnd();
		List<OperateLogVO> list = new ArrayList<OperateLogVO>();
		for (int i = 1; i <= fs.length; i++) {
			File f = fs[i - 1];
			if (f.isFile() && i >= start && i <= end) {
				OperateLogVO log = new OperateLogVO();
				log.setFileName(f.getName());
				log.setFilePath(f.getAbsolutePath());
				log.setFileSize(f.length() / 1024);
				log.setFileTime(f.lastModified());
				list.add(log);
			}
		}
		page.setTotalNo(total);
		page.setResult(list);

		request.setAttribute("page", page);
		return "log/systemLogFileList";
	}

	/**
	 * 下载日志文件
	 * 
	 * @param fileName
	 * @param request
	 * @param response
	 * @throws Exception   
	 */ 
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNLOGFILE)
	@RequestMapping(value = "downloadFile")
	public void downloadFile(String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (fileName == null || "".equals(fileName))
			throw new Exception("文件名称为空");
		if (!Pattern.matches("[0-9A-Za-z.]+", fileName)) {
			throw new Exception("文件名称不是由数字、字母和点组成");
		}
		String filePath = Constants.LOG_PATH;
		File targetFile = new File(filePath);

		if (!targetFile.exists())
			throw new Exception("文件不存在");

		filePath = filePath + "/" + fileName;
		
		download(filePath, fileName, request, response);
	}

	/**
	 * 查看日志文件内容
	 * 
	 * @param fileName
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "viewFile")
	public String viewFile(String tab, String fileName, Page<String> page) throws Exception {
		request.setAttribute("fileName", fileName);
		request.setAttribute("tab", tab);// 显示tab页id

		if (StringUtil.isBlank(fileName))
			return "log/viewFile";

		String filePath = Constants.LOG_PATH;
		File targetFile = new File(filePath);

		if (!targetFile.exists())
			return "log/viewFile";

		filePath = filePath + "/" + fileName;
		page.setPageSize(100);
		int start = page.getStart();
		int end = page.getEnd();

		String ret = FileUtil.getSpecifyLine(filePath, start, end);
		int total = FileUtil.getFileLineNum(filePath);

		List<String> list = new ArrayList<String>();
		list.add(ret);
		page.setTotalNo(total);
		page.setResult(list);

		request.setAttribute("page", page);
		return "log/viewFile";
	}

}
