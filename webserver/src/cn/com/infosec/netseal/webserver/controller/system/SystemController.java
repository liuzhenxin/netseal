package cn.com.infosec.netseal.webserver.controller.system;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.count.CountDaoImpl;
import cn.com.infosec.netseal.common.dao.pdfTemplate.PdfTemplateDaoImpl;
import cn.com.infosec.netseal.common.dao.pdfTemplateData.PdfTemplateDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.entity.po.PdfTemplate;
import cn.com.infosec.netseal.common.entity.vo.PdfTemplateVO;
import cn.com.infosec.netseal.common.entity.vo.config.ConfigVO;
import cn.com.infosec.netseal.common.entity.vo.report.ReportVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.manager.SchedulerManager;
import cn.com.infosec.netseal.common.scheduler.BaseScheduler;
import cn.com.infosec.netseal.common.scheduler.CrlScheduler;
import cn.com.infosec.netseal.common.scheduler.NtpScheduler;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ZipUtil;
import cn.com.infosec.netseal.common.util.pdf.PdfStampTemplate;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.manager.ServiceManager;
import cn.com.infosec.netseal.webserver.network.Network;
import cn.com.infosec.netseal.webserver.network.NetworkUtil;
import cn.com.infosec.netseal.webserver.network.ha.HAInfo;
import cn.com.infosec.netseal.webserver.network.ha.HAManager;
import cn.com.infosec.netseal.webserver.service.pdfTemplate.PdfTemplateServiceImpl;
import cn.com.infosec.netseal.webserver.service.system.SystemServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;

/**
 *
 */
@RequestMapping(value = "/system")
@Controller
public class SystemController extends BaseController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private CountDaoImpl countDao;
	@Autowired
	private SystemServiceImpl systemService;
	@Autowired
	private PdfTemplateServiceImpl pdfTemplateService;
	@Autowired
	private PdfTemplateDaoImpl pdfTemplateDao;
	@Autowired
	private PdfTemplateDataDaoImpl pdfTemplateDataDao;
	@Autowired
	private com.alibaba.druid.pool.DruidDataSource dataSource;
	@Autowired
	private CertDaoImpl certDao;

	/**
	 * CRL管理
	 * 
	 * @return
	 */
	@RequestMapping(value = "crlManage")
	public String crlManage() {
		ConfigUtil config = ConfigUtil.getInstance();
		request.setAttribute("config", config);
		boolean crlIsRunning = SchedulerManager.isRunning(Constants.SCHEDULER_CRL);
		request.setAttribute("crlIsRunning", crlIsRunning);
		return "system/crlManage";
	}

	/**
	 * 保存CRL配置
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "crlManageSave")
	@SealLog(optype = Constants.LOG_OPTYPE_CRLMANAGESAVE)
	public ModelAndView crlManageSave(ConfigVO configVO) {
		try {
			boolean authResult = systemService.authCrlFromLdap(configVO);
			if (authResult) {
				ConfigUtil.getInstance().saveLdapConfig(configVO);
				return this.getModelAndView(getSuccMap("配置成功,重启生效"));
			} else {
				return this.getModelAndView(getErrMap("配置信息错误,不能连接到ldap服务器"));
			}
		} catch (Exception e) {
			return this.getModelAndView(getErrMap("配置失败"));
		}
	}

	/**
	 * 修改CRL状态
	 * 
	 * @param operType
	 * @return
	 */
	@RequestMapping(value = "crlManageOper")
	@SealLog(optype = Constants.LOG_OPTYPE_CRLMANAGEOPER)
	public ModelAndView crlManageOper(String operType) {
		boolean success = true;
		String message = "";
		try {
			BaseScheduler bs = new CrlScheduler();
			SchedulerManager.setScheduler(Constants.SCHEDULER_CRL, bs);
			if ("0".equals(operType)) {// 停止
				SchedulerManager.stopAndClear(Constants.SCHEDULER_CRL);
				message = "停止成功";
			} else if ("1".equals(operType)) {// 启动
				boolean authResult = systemService.authCrlFromLdap();
				if (authResult) {
					SchedulerManager.start(Constants.SCHEDULER_CRL);
					message = "启动成功";
				} else {
					success = false;
					message = "配置信息错误,不能连接到ldap服务器,启动失败";
				}
			} else {
				success = false;
				message = "操作无效";
			}
		} catch (Exception e) {
			success = false;
			message = "操作失败";

		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 数据库配置
	 * 
	 * @return
	 */
	@RequestMapping(value = "dbConfig")
	public String dbConfig() {
		ConfigUtil config = ConfigUtil.getInstance();
		request.setAttribute("config", config);

		return "system/dbConfig";
	}

	/**
	 * 保存数据库配置
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "dbConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_DBCONFIGSAVE)
	public ModelAndView dbConfigSave(ConfigVO configVO) throws Exception {
		boolean success = true;
		String message = "";
		try {
			boolean result = systemService.testDB(configVO);
			if (result) {
				ConfigUtil config = ConfigUtil.getInstance();
				config.saveDBConfig(configVO);

				/*dataSource.restart();
				dataSource.setDriver(null);
				dataSource.setDbType(null);

				dataSource.setDriverClassName(config.getDriverClassName());
				dataSource.setUrl(config.getUrl());
				dataSource.setUsername(config.getUsername());
				dataSource.setPassword(config.getPassword());
				dataSource.setInitialSize(Integer.parseInt(config.getInitialSize()));
				dataSource.setMinIdle(Integer.parseInt(config.getMinIdle()));
				dataSource.setMaxActive(Integer.parseInt(config.getMaxActive()));
				dataSource.setMaxWait(Long.parseLong(config.getMaxWait()));
				
				// 配置对应数据库中证书表数量
				try {
					// 查询当前服务器生成的证书数量
					String serverId = ConfigUtil.getInstance().getTablePrefixId();
					int certNum = certDao.searchTotal(serverId);

					Count count = new Count();
					count.setName(Constants.CERT_NUM);
					count.setLocation(serverId);
					count.setNum(certNum);
					List<Count> cList = countDao.getCount(count);

					if (cList.size() == 0) {
						countDao.insert(count);
					} else {
						countDao.updateNum(count);
					}
					LoggerUtil.systemlog("count cert num 2 DB ok.");
				} catch (Exception e) {
					System.out.println("count cert num 2 DB error, " + e.getMessage());
					LoggerUtil.errorlog("count cert num 2 DB error, ", e);
				}
				*/
				message = "配置成功, 重启服务生效";
			} else {
				success = false;
				message = "配置错误,不能连接到数据库";
			}
		} catch (Exception e) {
			throw e;
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 数据库配置数据测试
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "dbConfigTest")
	public ModelAndView dbConfigTest(ConfigVO configVO) {
		boolean success = true;
		String message = "";
		boolean result = systemService.testDB(configVO);
		if (result) {
			message = "测试成功,能连接数据库";
		} else {
			success = false;
			message = "测试失败,不能连接数据库";
		}

		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 巡检报告
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "checkReport")
	@SealLog(optype = Constants.LOG_OPTYPE_CHECKREPORT)
	public String checkReport(HttpServletRequest request) {
		ReportVO report = new ReportVO();
		String nodeNameCN = "-";
		String ip = request.getLocalAddr();
		try {
			String hostName = NetworkUtil.getHostName();
			boolean haStatus = HAManager.queryServerStatus();
			HAInfo haInfo = HAManager.getHAInfo();
			if (haStatus) {
				HashMap<String, String> hostNameMap = new Network().getHostNames();
				for (String key : hostNameMap.keySet()) {
					if (hostNameMap.get(key).equals(hostName)) {
						ip = key;
						break;
					}
				}

			}

			// 取当前服务器名
			String mainNodeDeviceName = haInfo.getMainNodeDeviceName();

			if (mainNodeDeviceName.equals(hostName)) {
				nodeNameCN = "主机";
			} else
				nodeNameCN = "备机";
			report.setNodeNameCN(nodeNameCN);
		} catch (Exception e) {
		}

		// 取当前证书数量
		try {
			Count count = new Count();
			count.setName(Constants.CERT_NUM);
			count.setLocation(ConfigUtil.getInstance().getTablePrefixId());
			List<Count> cList = countDao.getCount(count);
			if (cList.size() > 0) {
				Count c = cList.get(0);
				report.setCertNum(c.getNum());
			}

			report.setIp(ip);
			systemService.genReport(report);// 生成巡检
			request.setAttribute("ip", ip);
			request.setAttribute("reportNumber", report.getReportNumber());
			request.setAttribute("version", report.getVersion());
			request.setAttribute("licenseNumber", report.getLicenseNumber());
			request.setAttribute("ipList", report.getIpList());
			request.setAttribute("checkList", report.getCheckList());
			request.setAttribute("resultList", report.getResultList());
			request.setAttribute("reportPath", report.getReportPath());
			request.setAttribute("nodeNameCN", nodeNameCN);
		} catch (Exception e) {
			LoggerUtil.errorlog("生成巡检报告错误", e);
			request.setAttribute("msg", "生成巡检报告错误");
		}

		return "system/checkReport";
	}

	/**
	 * 下载巡检报告
	 * 
	 * @param request
	 * @param response
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNCHECKREPORT)
	@RequestMapping(value = "downCheckReport")
	public void downCheckReport(HttpServletRequest request, HttpServletResponse response, String reportPath) {
		String filePath = Constants.REPORT_PATH + reportPath;
		File targetFile = new File(filePath);
		if (!targetFile.exists() || StringUtil.isBlank(reportPath)) {
			return;
		}

		String fileName = "NetSeal_CheckReport.pdf";

		download(filePath, fileName, request, response);
	}

	/**
	 * 进入时间配置
	 */
	@RequestMapping(value = "timeConfig")
	public String timeConfig() {
		return "system/timeConfig";
	}

	/**
	 * 进入NTP配置
	 */

	@RequestMapping(value = "ntpConfig")
	public String ntpConfig(HttpServletRequest request) {
		// todo ntp同步状态
		ConfigUtil config = ConfigUtil.getInstance();
		ConfigVO configVO = new ConfigVO();
		configVO.setNtpIp(config.getNtpIp());
		configVO.setNtpInterval(config.getNtpInterval());
		request.setAttribute("config", configVO);
		boolean ntpIsRunning = SchedulerManager.isRunning(Constants.SCHEDULER_NTP);
		request.setAttribute("ntpIsRunning", ntpIsRunning);
		return "system/ntpConfig";
	}

	/**
	 * 保存NTP配置 并立即同步
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 * 
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_SYNCNTP)
	@RequestMapping(value = "ntpConfigSave")
	public ModelAndView ntpConfigSave(ConfigVO configVO) throws Exception {
		try {
			NtpScheduler ntpScheduler = new NtpScheduler();
			ntpScheduler.setNtpIp(configVO.getNtpIp());
			boolean result = ntpScheduler.execSynTime();
			if (result)
				return getModelAndView(getSuccMap("立即同步成功"));
			else
				return getModelAndView(getErrMap("不能完成ntp同步, 立即同步失败"));
		} catch (Exception e) {
			LoggerUtil.errorlog("ntp同步错误", e);
			return getModelAndView(getErrMap("ntp同步错误"));
		}
	}

	/**
	 * 执行ntp操作 0停止 1启动
	 * 
	 * @param operType
	 * @return
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_SYNCNTP)
	@RequestMapping(value = "ntpConfigOper")
	public ModelAndView ntpConfigOper(ConfigVO configVO, String operType) {
		boolean success = true;
		String message = "";
		try {
			BaseScheduler bs = new NtpScheduler();
			SchedulerManager.setScheduler(Constants.SCHEDULER_NTP, bs);
			if ("0".equals(operType)) {// 停止
				SchedulerManager.stopAndClear(Constants.SCHEDULER_NTP);
				message = "停止成功";
			} else if ("1".equals(operType)) {// 启动
				NtpScheduler ntpScheduler = new NtpScheduler();
				ntpScheduler.setNtpIp(configVO.getNtpIp());
				ntpScheduler.setInterval(configVO.getNtpInterval());
				boolean result = ntpScheduler.execSynTime();
				if (result) {
					ConfigUtil config = ConfigUtil.getInstance();
					config.saveNtpConfig(configVO);

					SchedulerManager.setScheduler(Constants.SCHEDULER_NTP, ntpScheduler);
					SchedulerManager.start(Constants.SCHEDULER_NTP);
					message = "启动成功";
				} else {
					success = false;
					message = "不能完成ntp同步, 启动失败";
				}
			} else {
				success = false;
				message = "操作无效";
			}
		} catch (Exception e) {
			success = false;
			message = "ntp同步错误";
			LoggerUtil.errorlog("ntp同步错误", e);
		}
		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));

	}

	/**
	 * 进入 PDF模板管理
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "PDFTemplateManager")
	public String PDFTemplateManager(Page<PdfTemplateVO> page) throws Exception {
		// 初始化文件
		List<PdfTemplateVO> pdfTemplateVOList = pdfTemplateService.getPdfTemplates();
		for (PdfTemplateVO pdfTemplateVO : pdfTemplateVOList) {
			PdfTemplate pdfTemplate = new PdfTemplate();
			BeanUtils.copyProperties(pdfTemplateVO, pdfTemplate);
			pdfTemplateService.getPdfData(pdfTemplate);
		}

		// 查询分页
		page = pdfTemplateService.searchPdfTemplate(page);
		request.setAttribute("page", page);
		return "system/pdfTemplateManager";
	}

	/**
	 * 进入pdfTemplateAdd
	 */
	@RequestMapping(value = "pdfTemplateAdd")
	public String pdfTemplateAdd() throws Exception {
		return "system/pdfTemplateAdd";
	}

	/**
	 * 增加PdfTemplate
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "addPdfTemplate")
	@SealLog(optype = Constants.LOG_OPTYPE_ADDPDFTEMPLATE)
	public Map<String, Object> addPdfTemplate(HttpServletRequest request, HttpServletResponse response, PdfTemplateVO pdfTemplateVO) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String path = Constants.PDF_TEMPLATE_PATH;
		try {
			CommonsMultipartResolver commonMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			String fileOriName = "";
			String filePath = "";
			if (commonMultipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("pdfTemplate");

				if (file != null) {
					fileOriName = file.getOriginalFilename();
					if (FileUtil.getFileSuffix(fileOriName).equals("pdf")) {
						byte[] data = file.getBytes();
						String base64 = StringUtil.base64Encode(data);
						if (base64.length() > Constants.LENGTH_3MB_B64) {
							throw new WebDataException("模板文件不能大于3MB");
						}
						int countPdf = pdfTemplateDao.countPdfByName(pdfTemplateVO.getName());
						if (countPdf > 0)
							throw new WebDataException("模板名称重复");

						filePath = path + pdfTemplateVO.getName() + Constants.PDF_SUFFIX;
						File targetFile = new File(filePath);
						if (!targetFile.exists()) {
							targetFile.mkdirs();
						}
						file.transferTo(targetFile);

					} else {
						throw new WebDataException("文件类型错误");
					}
				}
			}

			try {
				// 保存DB
				pdfTemplateVO.setTemplatePath(filePath);
				pdfTemplateService.pdfTemplateDB(pdfTemplateVO);
			} catch (Exception e) {
				FileUtil.deleteFile(filePath);
				throw new WebDataException("文件导入失败");
			}
			response.getWriter().write("ok");
		} catch (WebDataException ex) {
			response.getWriter().write(ex.getMessage());
		} catch (Exception e) {
			response.getWriter().write(e.getMessage());
		}

		return null;
	}

	/**
	 * 删除PdfTemplate
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "delPdfTemplate")
	@SealLog(optype = Constants.LOG_OPTYPE_DELPDFTEMPLATE)
	public ModelAndView delPdfTemplate(String id) throws Exception {
		try {
			pdfTemplateService.deletePdfTemplate(id);
		} catch (WebDataException ex) {
			return getModelAndView(getErrMap(ex));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("删除成功"));
	}

	/**
	 * 下载前的准备
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "toDownloadPdfTemplate")
	public ModelAndView toDownloadPdfTemplate(String id) throws Exception {
		try {
			if (id == null || "".equals(id))
				throw new WebDataException("无效参数");

			PdfTemplate pdfTemplate = pdfTemplateDao.getPdfTemplate(Long.parseLong(id));
			if (pdfTemplate == null)
				throw new WebDataException("下载对象获取失败");

			// 校验信息
			pdfTemplateService.isModify(pdfTemplate, "所选Pdf模板" + pdfTemplate.getName(), "PdfTemplate which id is " + pdfTemplate.getId() + " and name is " + pdfTemplate.getName());

		} catch (WebDataException ex) {
			return getModelAndView(getErrMap(ex));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("ok"));
	}

	/**
	 * 下载PDF模板
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNPDFTEMPLATE)
	@RequestMapping(value = "downloadPdfTemplate")
	public void downloadPdfTemplate(String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PdfTemplate pdfTemplate = pdfTemplateDao.getPdfTemplate(Long.parseLong(id));
		String filePath = pdfTemplate.getTemplatePath();
		String fileName = new String(pdfTemplate.getName().getBytes("gb2312"), "ISO8859-1") + Constants.PDF_SUFFIX;

		download(filePath, fileName, request, response);

	}

	/**
	 * 进入输入项详情
	 */
	@RequestMapping(value = "templateDetails")
	public String templateDetails(Long id) throws Exception {
		PdfTemplate pdfTemplate = pdfTemplateDao.getPdfTemplate(id);

		List<String> list = PdfStampTemplate.getFieldName(FileUtil.getFile(pdfTemplate.getTemplatePath()));
		int size = list.size();
		String[] array = (String[]) list.toArray(new String[size]);
		request.setAttribute("name", pdfTemplate.getName());
		request.setAttribute("FieldName", Arrays.toString(array).replace("[", "").replace("]", ""));

		return "system/pdfTemplateDetails";
	}

	/**
	 * 时间戳配置RSA
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "tsaRsaConfig")
	public String tsaRsaConfig(HttpServletRequest request) {
		ConfigUtil config = ConfigUtil.getInstance();
		ConfigVO configVO = new ConfigVO();
		configVO.setTsaRsaUrl(config.getTsaRsaUrl());
		configVO.setTsaRsaUsername(config.getTsaRsaUsername());
		configVO.setTsaRsaUserpwd(config.getTsaRsaUserpwd());
		configVO.setTsaRsaPolicy(config.getTsaRsaPolicy());
		configVO.setTsaRsaUsetsa(config.getTsaRsaUsetsa());
		request.setAttribute("config", configVO);
		return "system/tsaRsaConfig";
	}

	/**
	 * 保存时间戳配置RSA
	 * 
	 * @param configVO
	 * @return
	 */
	@RequestMapping(value = "tsaRsaConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_TSACONFIGSAVE)
	public ModelAndView tsaRsaConfigSave(ConfigVO configVO) {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			config.saveTsaRsaConfig(configVO);
			boolean result = ServiceManager.reloadConfig();
			if (!result)
				throw new WebDataException("设置成功, 同步配置出错, 详细信息查看日志");
			return getModelAndView(getSuccMap("保存时间戳配置成功"));
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			LoggerUtil.errorlog("保存时间戳配置失败", e);
			return getModelAndView(getErrMap("保存时间戳配置失败"));
		}
	}

	/**
	 * 测试时间戳配置RSA
	 * 
	 * @param configVO
	 * @return
	 */
	@RequestMapping(value = "tsaRsaConfigTest")
	public ModelAndView tsaRsaConfigTest(ConfigVO configVO) {
		boolean success = false;
		String message = "";

		boolean isusetsa = configVO.isTsaRsaUsetsa();
		if (isusetsa) {
			boolean result = systemService.testTsaRsa(configVO);
			if (result) {
				success = true;
				message = "测试成功,能连接时间戳服务器";
			} else {
				message = "测试失败,不能连接时间戳服务器";
			}
		} else {
			message = "不使用时间戳,不进行测试";
		}

		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 时间戳配置SM2
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "tsaSM2Config")
	public String tsaSM2Config(HttpServletRequest request) {
		ConfigUtil config = ConfigUtil.getInstance();
		ConfigVO configVO = new ConfigVO();
		configVO.setTsaSM2Url(config.getTsaSM2Url());
		configVO.setTsaSM2Username(config.getTsaSM2Username());
		configVO.setTsaSM2Userpwd(config.getTsaSM2Userpwd());
		configVO.setTsaSM2Policy(config.getTsaSM2Policy());
		configVO.setTsaSM2Usetsa(config.getTsaSM2Usetsa());
		request.setAttribute("config", configVO);
		return "system/tsaSM2Config";
	}

	/**
	 * 保存时间戳配置SM2
	 * 
	 * @param configVO
	 * @return
	 */
	@RequestMapping(value = "tsaSM2ConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_TSACONFIGSAVE)
	public ModelAndView tsaSM2ConfigSave(ConfigVO configVO) {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			config.saveTsaSM2Config(configVO);
			boolean result = ServiceManager.reloadConfig();
			if (!result)
				throw new WebDataException("设置成功, 同步配置出错, 详细信息查看日志");
			return getModelAndView(getSuccMap("保存时间戳配置成功"));
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			LoggerUtil.errorlog("保存时间戳配置失败", e);
			return getModelAndView(getErrMap("保存时间戳配置失败"));
		}
	}

	/**
	 * 测试时间戳配置SM2
	 * 
	 * @param configVO
	 * @return
	 */
	@RequestMapping(value = "tsaSM2ConfigTest")
	public ModelAndView tsaSM2ConfigTest(ConfigVO configVO) {
		boolean success = false;
		String message = "";

		boolean isusetsa = configVO.isTsaSM2Usetsa();
		if (isusetsa) {
			boolean result = systemService.testTsaSM2(configVO);
			if (result) {
				success = true;
				message = "测试成功,能连接时间戳服务器";
			} else {
				message = "测试失败,不能连接时间戳服务器";
			}
		} else {
			message = "不使用时间戳,不进行测试";
		}

		Properties extMsg = new Properties();
		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 控件下载
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "downloadCab")
	public void downloadCab(HttpServletRequest request, HttpServletResponse response) {
		// 下载文件
		String fileName = "cab.zip";

		String filePath = Constants.ROOT_PATH + "tomcat/webapps/webserver/cab";
		String zipPath = Constants.TMP_PATH + DateUtil.getDateDir();
		File f = new File(zipPath);
		if (!f.exists())
			f.mkdirs();

		zipPath = zipPath + "cab.zip";
		// 压缩文件
		ZipUtil.zip(filePath, zipPath, "");

		download(zipPath, fileName, request, response);

		FileUtil.deleteFileAndDir(zipPath);
	}

}
