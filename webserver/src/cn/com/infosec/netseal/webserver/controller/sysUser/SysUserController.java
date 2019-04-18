package cn.com.infosec.netseal.webserver.controller.sysUser;

import java.io.File;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.dao.cert.CertDaoImpl;
import cn.com.infosec.netseal.common.dao.count.CountDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.entity.vo.CompanyVO;
import cn.com.infosec.netseal.common.entity.vo.MenuVO;
import cn.com.infosec.netseal.common.entity.vo.RoleVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.entity.vo.config.ConfigVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.ExecSh;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.NetWorkUtil;
import cn.com.infosec.netseal.common.util.OidUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.license.LicenseChecker;
import cn.com.infosec.netseal.common.util.license.LicenseInfo;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.network.Network;
import cn.com.infosec.netseal.webserver.network.ha.HAInfo;
import cn.com.infosec.netseal.webserver.network.ha.HAManager;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.company.CompanyServiceImpl;
import cn.com.infosec.netseal.webserver.service.menu.MenuServiceImpl;
import cn.com.infosec.netseal.webserver.service.role.RoleServiceImpl;
import cn.com.infosec.netseal.webserver.service.sysUser.SysUserServiceImpl;
import cn.com.infosec.netseal.webserver.service.system.SystemServiceImpl;
import cn.com.infosec.netseal.webserver.util.MobileTokenUtil;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.netseal.webserver.util.SessionUtil;
import cn.com.infosec.netseal.webserver.util.TreeNode;

@RequestMapping(value = "/sysUser")
@Controller
public class SysUserController extends BaseController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private SysUserServiceImpl sysUserService;
	@Autowired
	private RoleServiceImpl roleService;
	@Autowired
	private MenuServiceImpl menuService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private SystemServiceImpl systemService;
	@Autowired
	private CompanyServiceImpl companyService;
	@Autowired
	private com.alibaba.druid.pool.DruidDataSource dataSource;
	@Autowired
	private CountDaoImpl countDao;
	@Autowired
	private CertDaoImpl certDao;

	/**
	 * 登录跳转
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toLogin")
	public String toLogin(@ModelAttribute("msg") String msg) throws Exception {
		ConfigUtil config = ConfigUtil.getInstance();
		// 系统状态
		String status = config.getCheckStatus();
		if ("0".equals(status)) { // 系统基础配置
			return sysConfigInit();
		} else if ("1".equals(status)) { // 检查License文件
			return checkLicense();
		} else if ("2".equals(status)) {// DB配置
			request.setAttribute("config", config);
			return "system/dbConfigInit";
		} else if ("3".equals(status)) {// 系统用户登录
			request.setAttribute("msg", msg);
			return "login";
		} else {
			LoggerUtil.errorlog("系统加载错误,状态码" + status);
			return "error";
		}
	}

	/**
	 * 检查License
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping("/checkLicense")
	public String checkLicense() {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			LicenseInfo i = LicenseChecker.checkLicense(NetWorkUtil.getHostMac(config.getNetworkCard()));
			if (i == null) {
				File f = new File(Constants.LICENSE_PATH + Constants.LICENSE_APP);
				request.setAttribute("FileNames", f.getName());
				return "sysUser/licenseUpload";
			} else {
				config.saveCheckStatus("2");
				return "redirect:/sysUser/toLogin";
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("check license error, ", e);
		}
		request.setAttribute("exception", "验证license, 网卡network.card参数配置错误,请检查config.properties文件");
		return "error";
	}

	/**
	 * 下载LicenseApp文件
	 * 
	 * @param
	 * @return
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNLICENSE)
	@RequestMapping(value = "downLicenseApp")
	public void downLicenseGuid(HttpServletRequest request, HttpServletResponse response) {
		String fileName = Constants.LICENSE_APP;
		String filePath = Constants.LICENSE_PATH + Constants.LICENSE_APP;
		download(filePath, fileName, request, response);
	}

	/**
	 * 上传License文件
	 * 
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/uploadLicense")
	public void uploadLicense(HttpServletRequest request, @RequestParam("licenseFiles") MultipartFile file, HttpServletResponse response, HttpSession httpSession) throws IOException {
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
			ConfigUtil config = ConfigUtil.getInstance();
			byte[] licBytes = FileUtil.getFile(filePath);// 读取License文件
			byte[] entlic = StringUtil.base64Decode(licBytes);
			LicenseInfo i = LicenseChecker.checkLicense(NetWorkUtil.getHostMac(config.getNetworkCard()), entlic);
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
	 * 读取License文件详情跳转
	 * 
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "/viewLicense")
	public String toSaveLicense(HttpSession httpSession) {
		LicenseInfo li = (LicenseInfo) httpSession.getAttribute("licenseInfo");
		String strTimeStart = DateUtil.getDateTime(li.getValidTime_start());
		String strTimeEnd = DateUtil.getDateTime(li.getValidTime_end());
		request.setAttribute("strTimeStart", strTimeStart);
		request.setAttribute("strTimeEnd", strTimeEnd);
		request.setAttribute("li", li);
		return "sysUser/licenseView";
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
		return "redirect:/sysUser/toLogin";
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
		String path = (String) httpSession.getAttribute("filePath");
		try {
			File fold = new File(path);
			String tmpLicenseParent = fold.getParent();
			String strNewPath = Constants.LICENSE_PATH;
			File file = new File(strNewPath + Constants.LICENSE);
			fold.renameTo(file);// 将License文件重命名并移动
			FileUtil.deleteDir(tmpLicenseParent);
			ConfigUtil config = ConfigUtil.getInstance();
			LicenseInfo i = LicenseChecker.checkLicense(NetWorkUtil.getHostMac(config.getNetworkCard()));
			if (i != null) {
				config.setLicenseConfig(i.getMaxCertNum(), i.getMaxThred());
				config.saveCheckStatus("2");
			} else {
				throw new Exception();
			}
			FileUtil.deleteDir(tmpLicenseParent);
		} catch (WebDataException e) {
			if (path != null)
				FileUtil.deleteFileAndDir(path);
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			if (path != null)
				FileUtil.deleteFileAndDir(path);
			throw e;
		}

		return getModelAndView(getSuccMap());
	}

	/**
	 * 数据库配置保存跳转
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "dbConfigInitSave")
	@SealLog(optype = Constants.LOG_OPTYPE_DBCONFIGSAVE)
	public ModelAndView dbConfigInitSave(ConfigVO configVO) throws Exception {
		try {
			boolean result = systemService.testDB(configVO);
			if (result) {
				ConfigUtil config = ConfigUtil.getInstance();
				config.saveDBConfig(configVO);
				config.saveCheckStatus("3");

				/*
				 * dataSource.restart(); dataSource.setDriver(null); dataSource.setDbType(null);
				 * 
				 * dataSource.setDriverClassName(config.getDriverClassName()); dataSource.setUrl(config.getUrl()); dataSource.setUsername(config.getUsername());
				 * dataSource.setPassword(config.getPassword()); dataSource.setInitialSize(Integer.parseInt(config.getInitialSize())); dataSource.setMinIdle(Integer.parseInt(config.getMinIdle()));
				 * dataSource.setMaxActive(Integer.parseInt(config.getMaxActive())); dataSource.setMaxWait(Long.parseLong(config.getMaxWait()));
				 * 
				 * // 配置对应数据库中证书表数量 try { // 查询当前服务器生成的证书数量 String serverId = ConfigUtil.getInstance().getTablePrefixId(); int certNum = certDao.searchTotal(serverId);
				 * 
				 * Count count = new Count(); count.setName(Constants.CERT_NUM); count.setLocation(serverId); count.setNum(certNum); List<Count> cList = countDao.getCount(count);
				 * 
				 * if (cList.size() == 0) { countDao.insert(count); } else { countDao.updateNum(count); } LoggerUtil.systemlog("count cert num 2 DB ok."); } catch (Exception e) { System.out.println(
				 * "count cert num 2 DB error, " + e.getMessage()); LoggerUtil.errorlog("count cert num 2 DB error, ", e); }
				 */
			} else
				throw new WebDataException("配置错误,不能连接到数据库");
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("配置成功,重启服务生效"));
	}

	/**
	 * 登录, 普通登录或手机令牌登录
	 * 
	 * @param loginType
	 * @param account
	 * @param password
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@SealLog(optype = Constants.LOG_OPTYPE_SYSUSERLOGIN)
	public String login(Integer loginType, String account, String password, HttpSession httpSession, RedirectAttributes model) throws Exception {
		request.setAttribute("loginType", loginType);
		LicenseInfo i = LicenseChecker.checkLicense(NetWorkUtil.getHostMac(ConfigUtil.getInstance().getNetworkCard()));
		// 没有license文件、文件过期、验签名失败 被删除了
		if (i == null) {
			File f = new File(Constants.LICENSE_PATH + Constants.LICENSE_APP);
			request.setAttribute("FileNames", f.getName());
			return "sysUser/licenseUpload";
		}

		if (account == null || password == null) {
			request.setAttribute("msg", "请填写用户名和密码");
			return "login";
		}
		request.setAttribute("account", account);

		List<SysUserVO> sysUserVOList = sysUserService.getSysUser(account);
		if (sysUserVOList.size() == 0) {
			request.setAttribute("msg", "用户不存在");
			return "login";
		}
		if (sysUserVOList.size() >= 2) {
			request.setAttribute("msg", "用户在库中不唯一");
			return "login";
		}

		SysUserVO sysUserVO = sysUserVOList.get(0);
		if (sysUserVO.getFailedNum() == 6) {
			request.setAttribute("msg", "密码错误次数已达到上限,请解锁后继续尝试.");
			return "login";
		}

		// 校验管理员信息
		if (!sysUserService.isModify(sysUserVO.getId())) {
			request.setAttribute("msg", "管理员信息被篡改,无法登陆");
			return "login";
		}
		// 管理员状态
		if (sysUserVO.getStatus() == 0) {
			request.setAttribute("msg", "用户已停用,无法登陆");
			return "login";
		}
		// 验证密码
		boolean verifyRes = false;
		if (loginType != null && loginType.intValue() == 1) {
			if (CryptoHandler.hashEnc64(password).equals(sysUserVO.getPassword())) {
				verifyRes = true;
			}
		} else if (loginType != null && loginType.intValue() == 3) {// 手机令牌
			String seed = sysUserVO.getTokenSeed();
			if (StringUtil.isBlank(seed)) {
				request.setAttribute("msg", "用户没有绑定手机令牌");
				return "login";
			}
			verifyRes = MobileTokenUtil.verifyPassword(password, seed);
		} else {
			request.setAttribute("msg", "不存在的登录类型");
			return "login";
		}
		if (verifyRes) {// 登录成功
			// 第一次登录，修改密码
			if (sysUserVO.getChangePass() != 1 && loginType.intValue() == 1) {
				request.setAttribute("account", account);
				return "loginFirst";
			}
			Long roleId = sysUserVO.getRoleId();
			RoleVO roleVO = roleService.getRole(roleId);
			if (roleVO != null) {
				String menuIds = roleVO.getMenuId();
				List<MenuVO> menuVOList = menuService.getMenuByIds(menuIds);
				request.setAttribute("menuList", menuVOList);
				sysUserVO.setRoleName(roleVO.getName());
			} else {
				request.setAttribute("msg", "用户未授权");
				return "login";
			}
			String version = Constants.VERISON;
			httpSession.setAttribute("sysUser", sysUserVO);
			httpSession.setAttribute("version", version);
			return "common/main";
		} else {
			sysUserVO.setFailedNum(sysUserVO.getFailedNum() + 1);
			sysUserService.updateSysUserNoCert(sysUserVO);
			Integer num = 6 - sysUserVO.getFailedNum();
			if (num == 0)
				model.addFlashAttribute("msg", "密码错误次数已达到上限,请寻求其他用户管理员的帮助.");
			else
				model.addFlashAttribute("msg", "密码错误,剩余登录次数：" + num);
			return "redirect:/sysUser/toLogin.do";
		}
	}

	/**
	 * 通过证书登录
	 * 
	 * @param account
	 * @param httpSession
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/loginByCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SYSUSERLOGIN)
	public ModelAndView loginByCert(String account) throws Exception {
		try {
			List<SysUserVO> sysList = sysUserService.getSysUser(account);
			if (sysList.size() == 0)
				throw new WebDataException("用户不存在");

			if (sysList.size() >= 2)
				throw new WebDataException("用户在库中不唯一");

			// 校验管理员信息
			SysUserVO sysVo = sysList.get(0);
			if (sysVo.getFailedNum() == 6) {
				throw new WebDataException("密码错误次数已达到上限,请寻求其他用户管理员的帮助.");
			}
			if (!sysUserService.isModify(sysVo.getId()))
				throw new WebDataException("管理员信息被篡改,无法登陆");

			// 管理员状态
			if (sysVo.getStatus() == 0)
				throw new WebDataException("用户已停用,无法登陆");

			Long roleId = sysVo.getRoleId();
			RoleVO roleVO = roleService.getRole(roleId);
			if (roleVO == null)
				throw new WebDataException("用户未授权");

			// 获取管理员证书
			List<CertVO> certVOList = certService.getCertBySysUserId(sysVo.getId());
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

			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("randomData", StringUtil.base64Encode(CryptoHandler.genRandom(20, Constants.PFX_SUFFIX)));
			resultMap.put("certSn", cert.getCertSn());
			resultMap.put("sysId", sysVo.getId());
			resultMap.put("hashAlg", hashAlg);
			resultMap.put("success", true);
			return getModelAndView(resultMap);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * 验证签名 登录
	 * 
	 * @param SignatureData
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/loginVerifySign")
	@SealLog(optype = Constants.LOG_OPTYPE_SYSUSERLOGIN)
	public ModelAndView loginVerifySign(String SignatureData, String plainData, long sysId) throws Exception {
		boolean success = false;
		String message = "";
		try {
			List<CertVO> certVOList = certService.getCertBySysUserId(sysId);
			CertVO certVO = certVOList.get(0);
			String certPath = certVO.getCertPath();
			byte[] certData = certService.getCertData(certPath, certVO.getCertDataId());
			X509CertEnvelope cert = CertUtil.parseCert(certData);
			String signAlg = OidUtil.getSignAlg(cert.getSigAlgOID());
			
			SysUserVO sysUserVO = sysUserService.getSysUser(sysId);
			
			// 验证签名信息
			if (CryptoHandler.verify(cert.getPublicKey(), StringUtil.base64Decode(plainData), StringUtil.base64Decode(SignatureData), Constants.PFX_SUFFIX, 0, signAlg,
					ConfigUtil.getInstance().getGmOid().getBytes())){
				success = true;
				httpSession.setAttribute("sysUser", sysUserVO);
			}
			else {
				sysUserVO.setFailedNum(sysUserVO.getFailedNum() + 1);
				sysUserService.updateSysUserNoCert(sysUserVO);
				Integer num = 6 - sysUserVO.getFailedNum();
				if (num == 0)
					message = "密码错误次数已达到上限,请寻求其他用户管理员的帮助.";
				else
					message = "签名信息验证失败,剩余登录次数：" + num;
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
	 * 登录到系统
	 * 
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_SYSUSERLOGIN)
	@RequestMapping(value = "/loginSign", method = RequestMethod.POST)
	public String loginSign(long sysId, HttpSession httpSession, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		LicenseInfo i = LicenseChecker.checkLicense(NetWorkUtil.getHostMac(ConfigUtil.getInstance().getNetworkCard()));
		// 没有license文件、文件过期、验签名失败 被删除了
		if (i == null) {
			File f = new File(Constants.LICENSE_PATH + Constants.LICENSE_APP);
			request.setAttribute("FileNames", f.getName());
			return "sysUser/licenseUpload";
		}

		String version = Constants.VERISON;
		// 第一次登录，修改密码
		/*if (sysUserVO.getChangePass() != 1) {
			request.setAttribute("account", sysUserVO.getAccount());
			return "loginFirst";
		}*/

		SysUserVO sysUserVO = sysUserService.getSysUser(sysId);
		
		Long roleId = sysUserVO.getRoleId();
		RoleVO roleVO = roleService.getRole(roleId);
		if (roleVO != null) {
			String menuIds = roleVO.getMenuId();
			List<MenuVO> menuVOList = menuService.getMenuByIds(menuIds);
			request.setAttribute("menuList", menuVOList);
			sysUserVO.setRoleName(roleVO.getName());
		}
		
		httpSession.setAttribute("version", version);

		return "common/main";
	}

	/**
	 * 系统基本信息 登录成功后系统首页面
	 * 
	 * @param httpRequest
	 * @return
	 */
	@RequestMapping(value = "/sysIndexList")
	public String sysIndexList(HttpServletRequest httpRe) {
		return "common/sysIndexList";
	}

	/**
	 * 系统基本信息 登录成功后系统首页面
	 * 
	 * @param httpRequest
	 * @return
	 */
	@RequestMapping(value = "/sysIndex")
	public String sysIndex(HttpServletRequest httpRequest) {
		try {
			LicenseInfo li = systemService.licenseInfo();
			if (li != null) {
				String certNum = li.getMaxCertNum() + "";
				String thredNum = li.getMaxThred() + "";
				String validTime = DateUtil.getDateTime(li.getValidTime_start()) + "	至	" + DateUtil.getDateTime(li.getValidTime_end());
				// 后台服务
				int businessPort = ConfigUtil.getInstance().getListenerBusinessPort();
				String ret = ExecSh.exec("netstat -anp |grep " + businessPort);
				String businessStatus = "正常";
				if (ret.length() == 0)
					businessStatus = "异常";

				httpRequest.setAttribute("validTime", validTime);
				httpRequest.setAttribute("certNum", certNum);
				httpRequest.setAttribute("thredNum", thredNum);
				httpRequest.setAttribute("businessPort", businessPort);
				httpRequest.setAttribute("businessStatus", businessStatus);
			}

			// 从WATCHDOG配置文件中获取设备信息
			if (FileUtil.checkPath(Constants.WATCHDOG_CONF_PATH + "config.txt")) {
				byte[] wdConfData = FileUtil.getFile(Constants.WATCHDOG_CONF_PATH + "config.txt");
				String wdConfStr = new String(wdConfData);
				String[] wdConfs = wdConfStr.split(Constants.SPLIT_3);

				httpRequest.setAttribute("deviceModel", wdConfs[0]);
				httpRequest.setAttribute("deviceSn", wdConfs[3]);
			} else {
				httpRequest.setAttribute("deviceModel", Constants.DEFAULT_UNKNOWN_STRING);
				httpRequest.setAttribute("deviceSn", Constants.DEFAULT_UNKNOWN_STRING);
			}

			httpRequest.setAttribute("verison", Constants.VERISON);
		} catch (Exception e) {

		}

		return "common/sysIndex";
	}

	/**
	 * 版本信息
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/sysVersion")
	public String sysVersion(HttpServletRequest httpRequest) throws Exception {
		File file = new File(Constants.ROOT_PATH);
		String filePath = file.getParent() + "/" + "updatelog.txt";
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			return "common/sysVersion";
		}
		List<Map<String, String>> updateLog = new ArrayList<Map<String, String>>();
		updateLog = sysUserService.getVersion(filePath);
		request.setAttribute("updateLogList", updateLog);

		return "common/sysVersion";
	}

	/**
	 * 版本信息详情
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/viewDetails")
	public String viewDetails(String version) throws Exception {
		File file = new File(Constants.ROOT_PATH);
		String filePath = file.getParent() + "/" + "updatelog.txt";
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			return "common/sysVersion";
		}
		String viewDetails = "";
		version = version.replaceAll(",", " ");
		viewDetails = sysUserService.getDetails(version, filePath);
		if (viewDetails != null && viewDetails.indexOf("\r\n") != -1) {
			viewDetails = viewDetails.replace("\r\n", "<br/>");
		}
		request.setAttribute("viewDetails", viewDetails);
		return "common/viewDetails";
	}

	/**
	 * 第一步 系统基础信息配置
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sysConfigInit")
	public String sysConfigInit() {
		try {
			Network network = new Network();
			ArrayList<String> netWorkNameList = network.getNetWorkCardNameList();
			try {
				String bindNetWorkName = "";
				HAInfo haInfo = HAManager.getHAInfo();
				String ipDevice = haInfo.getIPDevice();
				ipDevice = ipDevice.split(" ")[0].trim();
				ipDevice = ipDevice.substring(0, ipDevice.length() - 2);
				for (int i = 0; i < netWorkNameList.size(); i++) {
					String netWorkName = netWorkNameList.get(i).trim();
					if (StringUtil.isBlank(netWorkName)) {
						continue;
					}

					if (ipDevice.equals(netWorkName)) {
						bindNetWorkName = netWorkName;
						break;
					}
				}
				// 过滤掉HA虚拟网口
				if (StringUtil.isNotBlank(bindNetWorkName))
					netWorkNameList.remove(bindNetWorkName + ":0");

			} catch (Exception e) {
				LoggerUtil.errorlog(e.getMessage());
			}
			request.setAttribute("netWorkNameList", netWorkNameList);// 所有网口
			ConfigUtil config = ConfigUtil.getInstance();
			request.setAttribute("tablePrefixId", config.getTablePrefixId());
			request.setAttribute("networkCard", config.getNetworkCard());
			request.setAttribute("wsUrl", config.getWsUrl());
		} catch (Exception e) {

		}

		return "sysUser/sysConfigInit";
	}

	/**
	 * 第一步 系统基本信息配置 保存
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_SAVESYSCONFIG)
	@RequestMapping(value = "sysConfigInitSave")
	public ModelAndView sysConfigInitSave(ConfigVO configVO) throws Exception {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			config.saveSysConfigInit(configVO);
			config.saveCheckStatus("1");
			FileUtil.deleteDir(Constants.LICENSE_PATH);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("系统基本信息配置成功"));
	}

	/**
	 * 登出
	 * 
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "/logout")
	@SealLog(optype = Constants.LOG_OPTYPE_SYSUSERLOGOUT)
	public String logout(HttpSession httpSession, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		httpSession.invalidate();
		return "login";
	}

	/**
	 * 超时
	 * 
	 * @return
	 */
	@RequestMapping(value = "/timeout")
	public String timeout() {
		return "timeout";
	}

	/**
	 * 添加用户
	 * 
	 * @param sysUserVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/addSysUser")
	@SealLog(optype = Constants.LOG_OPTYPE_ADDSYSUSER)
	public ModelAndView addSysUser(SysUserVO sysUserVO) throws Exception {
		if (sysUserService.getSysUser(sysUserVO.getAccount()).size() == 0) {
			try {
				sysUserService.insertSysUser(sysUserVO);
			} catch (WebDataException e) {
				return getModelAndView(getErrMap(e));
			} catch (Exception e) {
				throw e;
			}
			return getModelAndView(getSuccMap());
		} else
			return getModelAndView(getErrMap("用户存在"));
	}

	/**
	 * 删除用户
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/delSysUser")
	@SealLog(optype = Constants.LOG_OPTYPE_DELSYSUSER)
	public ModelAndView delSysUser(String id) throws Exception {
		try {
			sysUserService.deleteSysUser(id);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("删除成功"));
	}

	/**
	 * 修改管理员信息跳转
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "toEditSysUser")
	public String toEditUser(Long id) {
		SysUserVO sysUserVO = sysUserService.getSysUser(id);
		request.setAttribute("sysUser", sysUserVO);
		// 查询所有角色
		List<RoleVO> list = roleService.getRole();
		request.setAttribute("roleList", list);

		// 用户证书
		List<CertVO> certList = certService.getCertBySysUserId(id);
		if (certList != null && certList.size() > 0) {
			request.setAttribute("certDn", certList.get(0).getCertDn());
		}
		return "sysUser/sysUserEdit";
	}

	/**
	 * 修改管理员信息
	 * 
	 * @param sysUser
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/editSysUser")
	@SealLog(optype = Constants.LOG_OPTYPE_EDITSYSUSER)
	public ModelAndView editSysUser(SysUserVO sysUserVO) throws Exception {
		try {
			sysUserService.updateSysUser(sysUserVO);
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("修改成功"));
	}

	/**
	 * 重置密码 123456
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/resetSysUserPwd")
	@SealLog(optype = Constants.LOG_OPTYPE_RESETSYSUSERPWD)
	public ModelAndView resetSysUserPwd(String id) throws Exception {
		try {
			SysUserVO sysUserVO = new SysUserVO();
			// 将用户进行拆分
			String singleId[] = id.split(Constants.SPLIT_1);
			for (int i = 0; i < singleId.length; i++) {
				String sysId = singleId[i].trim();
				sysUserVO.setId(Long.parseLong(sysId));
				sysUserVO.setPassword(CryptoHandler.hashEnc64(Constants.SYS_USER_DEFAULT_PWD));
				sysUserVO.setChangePass(0);
				sysUserService.updateSysUserNoCert(sysUserVO);
			}
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("重置成功"));
	}

	/**
	 * 修改密码跳转
	 * 
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/toUpdateSysUserPwd")
	public String toEditSysUserPwd(String account) {
		List<SysUserVO> sysUserVOList = sysUserService.getSysUser(account);
		SysUserVO sysUserVO = sysUserVOList.get(0);
		request.setAttribute("sysUser", sysUserVO);
		return "sysUser/sysUserEditPwd";
	}

	/**
	 * 修改密码
	 * 
	 * @param account
	 * @param password
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateSysUserPwd")
	@SealLog(optype = Constants.LOG_OPTYPE_UPDATESYSUSERPWD)
	public ModelAndView updateSysUserPwd(String account, String password) throws Exception {
		String newPwd = request.getParameter("newSysPwd");

		List<SysUserVO> sysUserVOList = sysUserService.getSysUser(account);

		SysUserVO sysUserVO = sysUserVOList.get(0);
		if (sysUserVO != null) {
			if (CryptoHandler.hashEnc64(password).equals(sysUserVO.getPassword())) {
				sysUserVO.setPassword(CryptoHandler.hashEnc64(newPwd));
				sysUserService.updateSysUserNoCert(sysUserVO);
			} else
				return getModelAndView(getErrMap("密码错误"));

		} else
			return getModelAndView(getErrMap("用户不存在"));

		return getModelAndView(getSuccMap());
	}

	/**
	 * 单条查询
	 * 
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/getSysUser")
	public String getSysUser(Long id) {
		SysUserVO sysUserVO = sysUserService.getSysUser(id);

		// 用户证书
		List<CertVO> certList = certService.getCertBySysUserId(id);
		if (certList != null && certList.size() > 0) {
			sysUserVO.setCertDn(certList.get(0).getCertDn());
		}
		request.setAttribute("sysUser", sysUserVO);
		return "sysUser/sysUserShow";
	}

	/**
	 * 用户列表
	 * 
	 * @param page
	 * @param sysUser
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sysUserList")
	public String sysuserList(Page<SysUserVO> page, SysUserVO sysUserVO) throws Exception {
		sysUserVO.setId(SessionUtil.getSysUserId(httpSession));

		SysUserVO sysVo = (SysUserVO) httpSession.getAttribute("sysUser");
		CompanyVO sysComp = companyService.getCompany(sysVo.getCompanyId());
		page = sysUserService.searchSysUser(page, sysUserVO, sysComp.getTreeId());
		request.setAttribute("page", page);
		request.setAttribute("sysUser", sysUserVO);

		// 查询所有角色
		List<RoleVO> list = roleService.getRole();
		request.setAttribute("list", list);

		return "sysUser/sysUserList";

	}

	/**
	 * 查询分页
	 * 
	 * @param page
	 * @param sysUserVO
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sysUserSearch")
	public Map<String, Object> sysUserSearch(Page<SysUserVO> page, SysUserVO sysUserVO) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sysUserVO.setId(SessionUtil.getSysUserId(httpSession));
		SysUserVO sysVo = (SysUserVO) httpSession.getAttribute("sysUser");
		CompanyVO sysComp = companyService.getCompany(sysVo.getCompanyId());
		page = sysUserService.searchSysUser(page, sysUserVO, sysComp.getTreeId());
		modelMap.put("page", page);
		return modelMap;
	}

	/**
	 * 设置机构
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setCompanyTree", produces = "application/json;charset=UTF-8")
	public List<TreeNode> setCompanyTree(Long id) {
		List<TreeNode> treeList = new ArrayList<TreeNode>();

		if (id != null)
			treeList = sysUserService.getCompanyTreeByPid(id);

		else {
			SysUserVO sysVo = (SysUserVO) httpSession.getAttribute("sysUser");
			treeList = sysUserService.getCompanyTreeById(sysVo.getCompanyId());
		}

		return treeList;
	}

	/**
	 * 第一次登录，修改密码
	 * 
	 * @param sys
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_UPDATESYSUSERPWD)
	@RequestMapping(value = "/loginFirst")
	public String loginFirst(String account, String currentPwd, String newPwd, String newPwd2) throws Exception {
		String msg = null;
		List<SysUserVO> sysUserVOList = sysUserService.getSysUser(account);

		SysUserVO sysUserVO = sysUserVOList.get(0);

		if (sysUserVO != null) {
			if (!sysUserService.isModify(sysUserVO.getId())) {
				msg = "管理员信息被篡改,操作失败";
				return "loginFirst";
			}

			if (sysUserVO.getPassword().equals(CryptoHandler.hashEnc64(currentPwd))) {
				if (newPwd.equals(newPwd2)) {
					sysUserVO.setChangePass(1);
					sysUserVO.setPassword(CryptoHandler.hashEnc64(newPwd));
					sysUserService.updateSysUserNoCert(sysUserVO);
					msg = "修改成功";
					request.setAttribute("msg", msg);
					return "login";
				} else
					msg = "确认新密码不一致";
			} else
				msg = "当前密码错误";
		} else
			msg = "用户不存在";

		request.setAttribute("account", sysUserVO.getAccount());
		request.setAttribute("msg", msg);
		return "loginFirst";
	}

	/**
	 * 显示二级菜单
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/sysUserLeft")
	public String sysUserLeft(Long pid) throws Exception {
		List<MenuVO> resMenuVOList = new ArrayList<MenuVO>();
		List<MenuVO> menuVOList = new ArrayList<MenuVO>();
		List<SysUserVO> sysUserVOList = sysUserService.getSysUser(SessionUtil.getSysUserAccount(httpSession));

		SysUserVO sysUserVO = sysUserVOList.get(0);

		// 校验管理员信息
		if (!sysUserService.isModify(sysUserVO.getId()))
			throw new Exception("管理员账户为:" + sysUserVO.getAccount() + "信息被篡改");

		if (sysUserVO != null) {
			Long roleId = sysUserVO.getRoleId();
			RoleVO roleVO = roleService.getRole(roleId);
			if (roleVO != null) {
				String menuIds = roleVO.getMenuId();
				if (pid != null) {
					menuVOList = menuService.getMenuByPid(pid);
				} else {// 默认第一个menu
					menuVOList = menuService.getMenuByIds(menuIds);
					if (menuVOList.size() > 0) {
						MenuVO menuVO = menuVOList.get(0);
						menuVOList = menuService.getMenuByPid(menuVO.getId());
					}

				}
				// 有权限的menu
				if (menuIds != null) {
					String[] ids = menuIds.split(Constants.SPLIT_1);
					for (MenuVO tempMenuVO : menuVOList) {
						for (String strId : ids) {
							long id = Long.parseLong(strId.trim());
							long tempId = tempMenuVO.getId();
							if (id == tempId) {
								resMenuVOList.add(tempMenuVO);
								break;
							}
						}
					}
				}
			}
			request.setAttribute("menuList", resMenuVOList);
		}
		return "sysUser/sysUserLeft";
	}

	/**
	 * 添加用户跳转
	 */
	@RequestMapping(value = "/toAddSysUser")
	public String toAddSysUser() {
		// 查询所有角色
		List<RoleVO> list = roleService.getRole();
		request.setAttribute("list", list);
		return "sysUser/sysUserAdd";
	}

	/**
	 * 用户解锁
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_SYSUSERCLEAR)
	@RequestMapping(value = "/clearSysUser")
	public ModelAndView cleraSysUser(String id) throws Exception {
		try {
			String singleId[] = id.split(Constants.SPLIT_1);
			for (int i = 0; i < singleId.length; i++) {
				SysUserVO sysUserVO = sysUserService.getSysUser(Long.parseLong(singleId[i].trim()));
				sysUserVO.setFailedNum(0);
				sysUserService.updateSysUserNoCert(sysUserVO);
			}
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("解锁成功"));

	}

}
