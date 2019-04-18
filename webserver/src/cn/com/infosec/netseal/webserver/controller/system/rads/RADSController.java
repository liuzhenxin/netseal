package cn.com.infosec.netseal.webserver.controller.system.rads;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.config.NetCertCaVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.rads.RaConfig;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.util.Page;

@RequestMapping(value = "/system/rads")
@Controller
public class RADSController extends BaseController {
	@Autowired
	private HttpServletRequest httpRequest;
	@Autowired
	private HttpSession httpSession;

	/**
	 * rads配置
	 */
	@RequestMapping(value = "radsConfig")
	public String radsConfig() {
		return "system/rads/radsConfig";
	}

	/**
	 * rsa ca
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "rsaCaConfig")
	public String rsaCaConfig(HttpServletRequest request) throws Exception {
		ConfigUtil config = ConfigUtil.getInstance();
		NetCertCaVO netCertCa = config.getNetCertCaRSA();
		httpRequest.setAttribute("netCertCa", netCertCa);
		return "system/rads/rsaCaConfig";
	}

	/**
	 * sm2 ca
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "sm2CaConfig")
	public String sm2CaConfig(HttpServletRequest request) throws Exception {
		ConfigUtil config = ConfigUtil.getInstance();
		NetCertCaVO netCertCa = config.getNetCertCaSM2();
		httpRequest.setAttribute("netCertCa", netCertCa);

		return "system/rads/sm2CaConfig";
	}

	/**
	 * 证书模板 tab
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "certTemplateConfig")
	public String certTemplateConfig(Page<Map<String, String>> page) throws Exception {
		ConfigUtil config = ConfigUtil.getInstance();
		int start = page.getStart();
		int end = page.getEnd();
		List<Map<String, String>> templateList = new LinkedList<Map<String, String>>();
		List<Map<String, String>> templateListNew = new LinkedList<Map<String, String>>();
		NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
		List<String> rsaTemplateCnList = netCertCaRSA.getTemplateCnList();
		if (rsaTemplateCnList != null)
			for (String name : rsaTemplateCnList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", name);
				map.put("type", "RSA");
				templateList.add(map);
			}
		NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
		List<String> sm2TemplateCnList = netCertCaSM2.getTemplateCnList();
		if (sm2TemplateCnList != null)
			for (String name : sm2TemplateCnList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", name);
				map.put("type", "SM2");
				templateList.add(map);
			}

		for (int i = 0; i < templateList.size(); i++) {
			if (i >= start && i < end) {
				templateListNew.add(templateList.get(i));
			}
		}

		page.setTotalNo(templateList.size());
		page.setResult(templateListNew);
		httpRequest.setAttribute("page", page);
		return "system/rads/certTemplateConfig";
	}

	/**
	 * rsa ca参数配置保存
	 * 
	 * @param request
	 * @param netCertCa
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "rsaCaConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_RADSCONFIGSAVE)
	public ModelAndView rsaCaConfigSave(HttpServletRequest request, HttpServletResponse response, NetCertCaVO netCertCa) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		long time = DateUtil.getCurrentTime();
		String rsa = "rsa/" + time + "/";
		String rsaPath = Constants.RADS_DATA_PATH + rsa;
		String tmp = "tmp/" + time + "/";
		String fileName = "ra";
		String tmpPath = Constants.RADS_DATA_PATH + tmp;
		FileUtil.createDir(rsaPath);
		FileUtil.createDir(tmpPath);
		NetCertCaVO tmpNcc = new NetCertCaVO();// 测试信息
		try {
			BeanUtils.copyProperties(netCertCa, tmpNcc);
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile signCertFile = multiRequest.getFile("signCertFile");
				MultipartFile keyIdxFile = multiRequest.getFile("keyIdxFile");
				MultipartFile trustStoreFile = multiRequest.getFile("trustStoreFile");
				if (signCertFile != null) {
					long fileSize = signCertFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名证书不能超过200KB");
					String signCertFilePath = fileName + "." + FileUtil.getFileSuffix(signCertFile.getOriginalFilename());
					signCertFile.transferTo(new File(tmpPath + signCertFilePath));
					netCertCa.setSignCert(rsa + signCertFilePath);
					tmpNcc.setSignCert(tmp + signCertFilePath);
				}
				// rsa 签名密钥
				if (keyIdxFile != null) {
					long fileSize = keyIdxFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名密钥不能超过200KB");
					keyIdxFile.transferTo(new File(tmpPath + fileName + "." + FileUtil.getFileSuffix(keyIdxFile.getOriginalFilename())));
					netCertCa.setKeyIdx(rsa + fileName);
					tmpNcc.setKeyIdx(tmp + fileName);
				}
				if (trustStoreFile != null) {
					long fileSize = trustStoreFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("ssl信任库不能超过200KB");
					String keyIdxFilePath = "trustStore." + FileUtil.getFileSuffix(trustStoreFile.getOriginalFilename());
					trustStoreFile.transferTo(new File(tmpPath + keyIdxFilePath));
					netCertCa.setTrustStore(rsa + keyIdxFilePath);
					tmpNcc.setTrustStore(tmp + keyIdxFilePath);
				}
			}

			ConfigUtil config = ConfigUtil.getInstance();
			NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
			tmpNcc.setHsmName(netCertCaRSA.getHsmName());
			tmpNcc.setProtocolName(netCertCaRSA.getProtocolName());
			tmpNcc.setCountry(netCertCaRSA.getCountry());
			tmpNcc.setCertType("testRSACa");
			boolean res = RaConfig.testCertManager(tmpNcc);
			if (res) {
				FileUtil.copyDirNoCheck(tmpPath, rsaPath);
				config.saveNetCertCaRSA(netCertCa);
				// 重置rads
				netCertCa.setCertType(Constants.NETCERT_CA_RSA);
				RaConfig.resetCertManager(config.getNetCertCaRSA());
				FileUtil.deleteDir(Constants.RADS_DATA_PATH + "rsa", rsaPath);
				response.getWriter().write("ok");
			}
		} catch (WebDataException e) {
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				RaConfig.removeCertManager(tmpNcc.getCertType());
				FileUtil.deleteDir(Constants.RADS_DATA_PATH + "tmp");
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * sm2 ca参数配置保存
	 * 
	 * @param request
	 * @param netCertCa
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "sm2CaConfigSave")
	@SealLog(optype = Constants.LOG_OPTYPE_RADSCONFIGSAVE)
	public ModelAndView sm2CaConfigSave(HttpServletRequest request, HttpServletResponse response, NetCertCaVO netCertCa) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		long time = DateUtil.getCurrentTime();
		String sm2 = "sm2/" + time + "/";
		String sm2Path = Constants.RADS_DATA_PATH + sm2;
		String tmp = "tmp/" + time + "/";
		String fileName = "ra";
		String tmpPath = Constants.RADS_DATA_PATH + tmp;
		FileUtil.createDir(sm2Path);
		FileUtil.createDir(tmpPath);
		NetCertCaVO tmpNcc = new NetCertCaVO();// 测试信息
		try {
			BeanUtils.copyProperties(netCertCa, tmpNcc);
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile signCertFile = multiRequest.getFile("signCertFile");
				MultipartFile keyIdxFile = multiRequest.getFile("keyIdxFile");
				MultipartFile keyIdx2File = multiRequest.getFile("keyIdx2File");
				MultipartFile trustStoreFile = multiRequest.getFile("trustStoreFile");
				if (signCertFile != null) {
					long fileSize = signCertFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名证书不能超过200KB");
					String signCertFilePath = fileName + "." + FileUtil.getFileSuffix(signCertFile.getOriginalFilename());
					signCertFile.transferTo(new File(tmpPath + signCertFilePath));
					netCertCa.setSignCert(sm2 + signCertFilePath);
					tmpNcc.setSignCert(tmp + signCertFilePath);
				}
				// sm2 签名密钥
				if (keyIdxFile != null && keyIdx2File != null) {
					long fileSize = keyIdxFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名密钥不能超过200KB");
					fileSize = keyIdx2File.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名密钥不能超过200KB");
					keyIdxFile.transferTo(new File(tmpPath + fileName + "." + FileUtil.getFileSuffix(keyIdxFile.getOriginalFilename())));
					keyIdx2File.transferTo(new File(tmpPath + fileName + "." + FileUtil.getFileSuffix(keyIdx2File.getOriginalFilename())));
					netCertCa.setKeyIdx(sm2 + fileName);
					tmpNcc.setKeyIdx(tmp + fileName);
				}
				if (trustStoreFile != null) {
					long fileSize = trustStoreFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("ssl信任库不能超过200KB");
					String keyIdxFilePath = "trustStore." + FileUtil.getFileSuffix(trustStoreFile.getOriginalFilename());
					trustStoreFile.transferTo(new File(tmpPath + keyIdxFilePath));
					netCertCa.setTrustStore(sm2 + keyIdxFilePath);
					tmpNcc.setTrustStore(tmp + keyIdxFilePath);
				}
			}

			ConfigUtil config = ConfigUtil.getInstance();
			NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
			tmpNcc.setHsmName(netCertCaSM2.getHsmName());
			tmpNcc.setProtocolName(netCertCaSM2.getProtocolName());
			tmpNcc.setCountry(netCertCaSM2.getCountry());
			tmpNcc.setCertType("testSM2Ca");
			boolean res = RaConfig.testCertManager(tmpNcc);
			if (res) {
				FileUtil.copyDirNoCheck(tmpPath, sm2Path);
				config.saveNetCertCaSM2(netCertCa);
				// 重置rads
				netCertCa.setCertType(Constants.NETCERT_CA_SM2);
				RaConfig.resetCertManager(config.getNetCertCaSM2());
				FileUtil.deleteDir(Constants.RADS_DATA_PATH + "sm2", sm2Path);
				response.getWriter().write("ok");
			}
		} catch (WebDataException e) {
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				RaConfig.removeCertManager(tmpNcc.getCertType());
				FileUtil.deleteDir(Constants.RADS_DATA_PATH + "tmp");
			} catch (Exception e) {
			}
		}
		return null;
	}

	@RequestMapping(value = "sm2CaConfigTest")
	public ModelAndView sm2CaConfigTest(HttpServletRequest request, HttpServletResponse response, NetCertCaVO netCertCa) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		boolean success = false;
		long time = DateUtil.getCurrentTime();
		String tmp = "tmp/" + time + "/";
		String fileName = "ra";
		String sm2Path = Constants.RADS_DATA_PATH + tmp;
		FileUtil.createDir(sm2Path);
		try {
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile signCertFile = multiRequest.getFile("signCertFile");
				MultipartFile keyIdxFile = multiRequest.getFile("keyIdxFile");
				MultipartFile keyIdx2File = multiRequest.getFile("keyIdx2File");
				MultipartFile trustStoreFile = multiRequest.getFile("trustStoreFile");
				if (signCertFile != null) {
					long fileSize = signCertFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名证书不能超过200KB");
					String signCertFilePath = fileName + "." + FileUtil.getFileSuffix(signCertFile.getOriginalFilename());
					signCertFile.transferTo(new File(sm2Path + signCertFilePath));
					netCertCa.setSignCert(tmp + signCertFilePath);
				}
				// sm2 签名密钥
				if (keyIdxFile != null && keyIdx2File != null) {
					long fileSize = keyIdxFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名密钥不能超过200KB");
					fileSize = keyIdx2File.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名密钥不能超过200KB");
					keyIdxFile.transferTo(new File(sm2Path + fileName + "." + FileUtil.getFileSuffix(keyIdxFile.getOriginalFilename())));
					keyIdx2File.transferTo(new File(sm2Path + fileName + "." + FileUtil.getFileSuffix(keyIdx2File.getOriginalFilename())));
					netCertCa.setKeyIdx(tmp + fileName);
				}
				if (trustStoreFile != null) {
					long fileSize = trustStoreFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("ssl信任库不能超过200KB");
					String keyIdxFilePath = "trustStore." + FileUtil.getFileSuffix(trustStoreFile.getOriginalFilename());
					trustStoreFile.transferTo(new File(sm2Path + keyIdxFilePath));
					netCertCa.setTrustStore(tmp + keyIdxFilePath);
				}
			}

			ConfigUtil config = ConfigUtil.getInstance();
			NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
			netCertCa.setHsmName(netCertCaSM2.getHsmName());
			netCertCa.setProtocolName(netCertCaSM2.getProtocolName());
			netCertCa.setCountry(netCertCaSM2.getCountry());
			success = RaConfig.testCertManager(netCertCa);
			if (success)
				response.getWriter().write("ok");
			else
				response.getWriter().write("测试失败");

		} catch (WebDataException e) {
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				RaConfig.removeCertManager(netCertCa.getCertType());
				FileUtil.deleteDir(Constants.RADS_DATA_PATH + "tmp", sm2Path);
			} catch (Exception e) {
			}
		}

		return null;
	}

	@RequestMapping(value = "rsaCaConfigTest")
	public ModelAndView rsaCaConfigTest(HttpServletRequest request, HttpServletResponse response, NetCertCaVO netCertCa) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		boolean success = false;
		long time = DateUtil.getCurrentTime();
		String tmp = "tmp/" + time + "/";
		String fileName = "ra";
		String rsaPath = Constants.RADS_DATA_PATH + tmp;
		FileUtil.createDir(rsaPath);
		try {
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile signCertFile = multiRequest.getFile("signCertFile");
				MultipartFile keyIdxFile = multiRequest.getFile("keyIdxFile");
				MultipartFile trustStoreFile = multiRequest.getFile("trustStoreFile");
				if (signCertFile != null) {
					long fileSize = signCertFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名证书不能超过200KB");
					String signCertFilePath = fileName + "." + FileUtil.getFileSuffix(signCertFile.getOriginalFilename());
					signCertFile.transferTo(new File(rsaPath + signCertFilePath));
					netCertCa.setSignCert(tmp + signCertFilePath);
				}
				// rsa签名密钥
				if (keyIdxFile != null) {
					long fileSize = keyIdxFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("签名密钥不能超过200KB");
					keyIdxFile.transferTo(new File(rsaPath + fileName + "." + FileUtil.getFileSuffix(keyIdxFile.getOriginalFilename())));
					netCertCa.setKeyIdx(tmp + fileName);
				}
				if (trustStoreFile != null) {
					long fileSize = trustStoreFile.getSize();
					if (fileSize / 1024 > 200)
						throw new WebDataException("ssl信任库不能超过200KB");
					String keyIdxFilePath = "trustStore." + FileUtil.getFileSuffix(trustStoreFile.getOriginalFilename());
					trustStoreFile.transferTo(new File(rsaPath + keyIdxFilePath));
					netCertCa.setTrustStore(tmp + keyIdxFilePath);
				}
			}

			ConfigUtil config = ConfigUtil.getInstance();
			NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
			netCertCa.setHsmName(netCertCaRSA.getHsmName());
			netCertCa.setProtocolName(netCertCaRSA.getProtocolName());
			netCertCa.setCountry(netCertCaRSA.getCountry());
			success = RaConfig.testCertManager(netCertCa);
			if (success)
				response.getWriter().write("ok");
			else
				response.getWriter().write("测试失败");

		} catch (WebDataException e) {
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				RaConfig.removeCertManager(netCertCa.getCertType());
				FileUtil.deleteDir(Constants.RADS_DATA_PATH + "tmp", rsaPath);
			} catch (Exception e) {
			}
		}

		return null;
	}

	/**
	 * 删除证书模板
	 * 
	 * @param certType
	 * @param templateCn
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "delCertTemplate")
	@SealLog(optype = Constants.LOG_OPTYPE_RADSCONFIGSAVE)
	public ModelAndView delCertTemplate(String certType, String id) throws Exception {
		boolean success = true;
		String message = "";
		Properties extMsg = new Properties();
		try {
			List<String> templateList = null;
			List<String> templateListRSA = null;
			List<String> templateListSM2 = null;
			ConfigUtil config = ConfigUtil.getInstance();
			NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
			templateListRSA = netCertCaRSA.getTemplateList();
			NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
			templateListSM2 = netCertCaSM2.getTemplateList();

			String idList[] = id.split(Constants.SPLIT_1);
			for (int i = 0; i < idList.length; i++) {
				String templateCn = idList[i].trim();
				String sub = templateCn.substring(templateCn.length() - 3);
				String template = null;
				if (sub.equals("_单证")) {
					template = templateCn.substring(0, templateCn.length() - 3) + "_s";
				} else if (sub.equals("_双证")) {
					template = templateCn.substring(0, templateCn.length() - 3) + "_d";
				}
				for (String temp : templateListRSA) {
					if (temp.equals(template)) {
						templateListRSA.remove(temp);
						break;
					}
				}
				for (String temp : templateListSM2) {
					if (temp.equals(template)) {
						templateListSM2.remove(temp);
						break;
					}
				}
			}
			NetCertCaVO netCertCa = new NetCertCaVO();
			netCertCa.setCertType(Constants.NETCERT_CA_RSA);
			netCertCa.setTemplate(StringUtils.join(templateListRSA.toArray(), ","));
			config.saveNetCertCaTemplate(netCertCa);
			netCertCa.setCertType(Constants.NETCERT_CA_SM2);
			netCertCa.setTemplate(StringUtils.join(templateListSM2.toArray(), ","));
			config.saveNetCertCaTemplate(netCertCa);
			message = "删除证书模板成功";
		} catch (Exception e) {
			success = false;
			message = "删除证书模板错误";
			LoggerUtil.errorlog(message, e);
		}

		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));

	}

	/**
	 * 添加证书模板
	 * 
	 * @param netCertCa
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "addCertTemplate")
	@SealLog(optype = Constants.LOG_OPTYPE_RADSCONFIGSAVE)
	public ModelAndView addCertTemplate(NetCertCaVO netCertCa) throws Exception {
		boolean success = true;
		String message = "";
		Properties extMsg = new Properties();
		try {
			String certType = netCertCa.getCertType();
			List<String> templateList = null;
			ConfigUtil config = ConfigUtil.getInstance();
			if (Constants.NETCERT_CA_RSA.equals(certType)) {
				NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
				templateList = netCertCaRSA.getTemplateList();
			} else if (Constants.NETCERT_CA_SM2.equals(certType)) {
				NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
				templateList = netCertCaSM2.getTemplateList();
			}
			String template = netCertCa.getTemplate();
			String templateCn = template;
			Integer isDCert = netCertCa.getIsDCert();
			if (isDCert != null && isDCert.intValue() == 0) {
				template += "_s";
				templateCn += "_单证";
			} else if (isDCert != null && isDCert.intValue() == 1) {
				template += "_d";
				templateCn += "_双证";
			}
			templateList.add(template);

			extMsg.put("template", templateCn);
			netCertCa.setTemplate(StringUtils.join(templateList.toArray(), ","));
			config.saveNetCertCaTemplate(netCertCa);
			message = "添加证书模板成功";
		} catch (Exception e) {
			success = false;
			message = "添加证书模板错误";
			LoggerUtil.errorlog(message, e);
		}

		extMsg.put("success", success);
		extMsg.put("message", message);
		return getModelAndView(getMap(extMsg));
	}

	/**
	 * 获取证书模板
	 * 
	 * @param certType
	 *            ca证书类型
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTemplateList")
	public ModelAndView getTemplateList(String certType, String searchName, String isDCert, Page<Map<String, String>> page) throws Exception {
		int start = page.getStart();
		int end = page.getEnd();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			List<Map<String, String>> templateList = new LinkedList<>();
			List<String> templateListRSA = new ArrayList<>();
			List<String> templateListSM2 = new ArrayList<>();
			ConfigUtil config = ConfigUtil.getInstance();
			if (Constants.NETCERT_CA_RSA.equals(certType)) {
				NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
				templateListRSA = netCertCaRSA.getTemplateCnList();
				for (String temp : templateListRSA) {
					if (StringUtil.isNotBlank(searchName)) {
						if ("全部".equals(isDCert)) {
							if (temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "RSA");
								templateList.add(map);
							}
						} else {
							if (temp.endsWith(isDCert) && temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "RSA");
								templateList.add(map);
							}
						}
					} else {
						if ("全部".equals(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "RSA");
							templateList.add(map);
						} else if (temp.endsWith(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "RSA");
							templateList.add(map);
						}
					}
				}
			} else if (Constants.NETCERT_CA_SM2.equals(certType)) {
				NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
				templateListSM2 = netCertCaSM2.getTemplateCnList();
				for (String temp : templateListSM2) {
					if (StringUtil.isNotBlank(searchName)) {
						if ("全部".equals(isDCert)) {
							if (temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "SM2");
								templateList.add(map);
							}
						} else {
							if (temp.endsWith(isDCert) && temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "SM2");
								templateList.add(map);
							}
						}
					} else {
						if ("全部".equals(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "SM2");
							templateList.add(map);
						} else if (temp.endsWith(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "SM2");
							templateList.add(map);
						}
					}
				}
			} else {
				NetCertCaVO netCertCaRSA = config.getNetCertCaRSA();
				templateListRSA = netCertCaRSA.getTemplateCnList();
				NetCertCaVO netCertCaSM2 = config.getNetCertCaSM2();
				templateListSM2 = netCertCaSM2.getTemplateCnList();
				for (String temp : templateListRSA) {
					if (StringUtil.isNotBlank(searchName)) {
						if ("全部".equals(isDCert)) {
							if (temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "RSA");
								templateList.add(map);
							}
						} else {
							if (temp.endsWith(isDCert) && temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "RSA");
								templateList.add(map);
							}
						}
					} else {
						if ("全部".equals(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "RSA");
							templateList.add(map);
						} else if (temp.endsWith(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "RSA");
							templateList.add(map);
						}
					}
				}

				for (String temp : templateListSM2) {
					if (StringUtil.isNotBlank(searchName)) {
						if ("全部".equals(isDCert)) {
							if (temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "SM2");
								templateList.add(map);
							}
						} else {
							if (temp.endsWith(isDCert) && temp.contains(searchName)) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("name", temp);
								map.put("type", "SM2");
								templateList.add(map);
							}
						}
					} else {
						if ("全部".equals(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "SM2");
							templateList.add(map);
						} else if (temp.endsWith(isDCert)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", temp);
							map.put("type", "SM2");
							templateList.add(map);
						}
					}
				}

			}
			List<Map<String, String>> templateListNew = new LinkedList<Map<String, String>>();
			for (int i = 0; i < templateList.size(); i++) {
				if (i >= start && i < end) {
					templateListNew.add(templateList.get(i));
				}
			}
			page.setTotalNo(templateList.size());
			page.setResult(templateListNew);
			// httpRequest.setAttribute("page", page);
			resultMap.put("page", page);
			resultMap.put("success", true);
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(resultMap);
	}
}
