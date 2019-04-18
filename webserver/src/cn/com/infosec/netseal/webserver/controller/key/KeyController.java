package cn.com.infosec.netseal.webserver.controller.key;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.algorithm.shamir.ShamirUtil;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.vo.KeyVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.ExecSh;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.netseal.common.util.KeyStoreUtil;
import cn.com.infosec.netseal.common.util.NetSignUtil;
import cn.com.infosec.netseal.common.util.P10Util;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ZipUtil;
import cn.com.infosec.netseal.common.util.cryptoCard.fishman.FishManUtil;
import cn.com.infosec.netseal.common.util.p10.JKSFile;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.manager.ServiceManager;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;
import cn.com.infosec.oscca.sm2.SM2PrivateKey;
import cn.com.infosec.oscca.sm2.SM2PublicKey;

/**
 * 密钥管理
 *
 */
@RequestMapping(value = "/key")
@Controller
public class KeyController extends BaseController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;

	/**
	 * 服务器证书管理
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "keyList")
	public String keyList() throws Exception {
		// 初始化服务器文件
		initServerFile();

		// 查询所有导入的签名密钥证书
		KeyVO sigKvo = new KeyVO();
		sigKvo.setCertUsage(Constants.USAGE_SIGNATURE);
		List<KeyVO> sigKeyList = keyService.getCheckKey(sigKvo);
		request.setAttribute("sigKeyList", sigKeyList);

		ConfigUtil config = ConfigUtil.getInstance();
		request.setAttribute("serverSigKeyId", config.getSignKeyId());

		// 查询所有导入的签名密钥证书
		KeyVO encKvo = new KeyVO();
		encKvo.setCertUsage(Constants.USAGE_ENCRYPT);
		List<KeyVO> encKeyList = keyService.getCheckKey(encKvo);
		request.setAttribute("encKeyList", encKeyList);

		request.setAttribute("serverEncKeyId", config.getEncrpKeyId());

		return "key/keyList";
	}

	/**
	 * 服务器证书管理 JKS
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "keyJksList")
	public String keyJksList(Page<KeyVO> page, KeyVO keyVO) throws Exception {
		// 初始化服务器文件
		initServerFile();

		keyVO.setKeyMode(Constants.JKS_SUFFIX);
		page = keyService.searchCert(page, keyVO);
		request.setAttribute("page", page);
		return "key/jksList";
	}

	/**
	 * 服务器证书管理 PFX
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "keyPfxList")
	public String keyPfxList(Page<KeyVO> page, KeyVO keyVO) throws Exception {
		// 初始化服务器文件
		initServerFile();

		keyVO.setKeyMode(Constants.PFX_SUFFIX);
		page = keyService.searchCert(page, keyVO);
		request.setAttribute("page", page);
		return "key/pfxList";
	}

	/**
	 * 国密证书管理 SM2
	 * 
	 * @param page
	 * @param keyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "keySm2List")
	public String keySm2List(Page<KeyVO> page, KeyVO keyVO) throws Exception {
		// 初始化服务器文件
		initServerFile();

		keyVO.setKeyMode(Constants.PRI_SUFFIX);
		page = keyService.searchCert(page, keyVO);
		request.setAttribute("page", page);
		return "key/sm2List";
	}

	/**
	 * 国密加密卡管理 SM2
	 * 
	 * @param page
	 * @param keyVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "keySm2CardList")
	public String keySm2CardList(Page<KeyVO> page, KeyVO keyVO) throws Exception {
		// 初始化服务器文件
		initServerFile();

		keyVO.setKeyMode(Constants.KEY_SUFFIX);
		page = keyService.searchCert(page, keyVO);
		request.setAttribute("page", page);
		return "key/sm2CardList";
	}

	/**
	 * 设置签名密钥
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "setSignKey")
	public ModelAndView setSignKey(long id) throws Exception {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			KeyVO key = keyService.getKey(id);
			if (key == null)
				throw new WebDataException("密钥在数据库中不存在");

			// 校验信息
			if (!keyService.isModify(id))
				throw new WebDataException("所选密钥" + key.getCertDn() + "信息被篡改");

			if (Constants.KEY_SUFFIX.equals(key.getKeyMode())) {
				// 加密卡中是否含密钥
				int hKey = key.getHsmId();
				if (!FishManUtil.exiteSM2Key(hKey))
					throw new WebDataException("加密卡内没有密钥");

				List<byte[]> kmList = FishManUtil.exportSM2Key(hKey);
				byte[] pPri = kmList.get(1);

				// 加密卡密钥与数据库密钥是否匹配
				SM2PrivateKey sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(key.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(key.getKeyPath()));

				if (!Arrays.equals(pPri, sm2Pri.getD()))
					throw new WebDataException("加密卡没有对应密钥");
			}
			// 设置签名密钥Id
			config.saveSignKeyId(key.getId());

			// 修改OpenSSL配置文件
			File crudiniFile = new File(Constants.ROOT_PATH + "ns/config.ini");
			if (crudiniFile.exists()) {
				String dirPath = key.getCertPath();
				dirPath = dirPath.substring(0, dirPath.lastIndexOf("/")) + "/";
				if (Constants.KEY_SUFFIX.equals(key.getKeyMode())) {
					File pemFile = new File(dirPath + Constants.PEM_KEY);
					if (!pemFile.exists())
						throw new WebDataException("密钥文件(.pem)不存在,操作失败");
				}

				try {
					ExecSh.exec("./crudini --set config.ini server0 sigcert " + key.getCertPath(), Constants.ROOT_PATH + "ns/");
					ExecSh.exec("./crudini --set config.ini server0 sigkey " + dirPath + Constants.PEM_KEY, Constants.ROOT_PATH + "ns/");
					ExecSh.exec("./crudini --set config.ini server1 sigcert " + key.getCertPath(), Constants.ROOT_PATH + "ns/");
					ExecSh.exec("./crudini --set config.ini server1 sigkey " + dirPath + Constants.PEM_KEY, Constants.ROOT_PATH + "ns/");
				} catch (Exception e) {
					throw new WebDataException("OpenSSL配置文件设置签名密钥路径出错");
				}
			}

			boolean result = ServiceManager.reloadConfig();
			if (!result)
				throw new WebDataException("设置成功, 同步配置出错, 详细信息查看日志");

		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}

		return getModelAndView(getSuccMap("设置成功"));
	}

	/**
	 * 设置加密密钥
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "setEncKey")
	public ModelAndView setEncKey(long id) throws Exception {
		try {
			ConfigUtil config = ConfigUtil.getInstance();
			KeyVO key = keyService.getKey(id);
			if (key == null)
				throw new WebDataException("密钥在数据库中不存在");

			// 校验信息
			if (!keyService.isModify(id))
				throw new WebDataException("所选密钥" + key.getCertDn() + "信息被篡改");

			if (Constants.KEY_SUFFIX.equals(key.getKeyMode())) {
				// 加密卡中是否含密钥
				int hKey = key.getHsmId();
				if (!FishManUtil.exiteSM2Key(hKey))
					throw new WebDataException("加密卡内没有密钥");

				List<byte[]> kmList = FishManUtil.exportSM2Key(hKey);
				byte[] pPri = kmList.get(1);

				// 加密卡密钥与数据库密钥是否匹配
				SM2PrivateKey sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(key.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(key.getKeyPath()));

				if (!Arrays.equals(pPri, sm2Pri.getD())) // DB 加密卡密钥匹配
					throw new WebDataException("加密卡没有对应密钥");
			}
			// 设置加密密钥Id
			config.saveEncryptKeyId(key.getId());

			// 修改OpenSSL配置文件
			File crudiniFile = new File(Constants.ROOT_PATH + "ns/config.ini");
			if (crudiniFile.exists()) {
				String dirPath = key.getCertPath();
				dirPath = dirPath.substring(0, dirPath.lastIndexOf("/")) + "/";
				if (Constants.KEY_SUFFIX.equals(key.getKeyMode())) {
					File pemFile = new File(dirPath + Constants.PEM_KEY);
					if (!pemFile.exists())
						throw new WebDataException("密钥文件(.pem)不存在,操作失败");
				}
				try {
					ExecSh.exec("./crudini --set config.ini server0 enccert " + key.getCertPath(), Constants.ROOT_PATH + "ns/");
					ExecSh.exec("./crudini --set config.ini server0 enckey " + dirPath + Constants.PEM_KEY, Constants.ROOT_PATH + "ns/");
					ExecSh.exec("./crudini --set config.ini server1 enccert " + key.getCertPath(), Constants.ROOT_PATH + "ns/");
					ExecSh.exec("./crudini --set config.ini server1 enckey " + dirPath + Constants.PEM_KEY, Constants.ROOT_PATH + "ns/");
				} catch (Exception e) {
					throw new WebDataException("OpenSSL配置文件设置加密密钥路径出错");
				}
			}

			boolean result = ServiceManager.reloadConfig();
			if (!result)
				throw new WebDataException("设置成功, 同步配置出错, 详细信息查看日志");

		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("设置成功"));
	}

	/**
	 * JKS证书请求
	 * 
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keyJksCsr")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERJKSCERTREQUEST)
	public Map<String, Object> keyJksCsr() throws Exception {
		String dirPath = Constants.KEY_PATH + FileUtil.getFileName() + "/";
		String csrPath = dirPath + Constants.CSR;
		String certPath = dirPath + Constants.CER;
		String jksPath = dirPath + Constants.JKS;
		// 初始化服务器文件
		initServerFile();

		Map<String, Object> modelMap = new HashMap<String, Object>();
		File f = new File(csrPath);
		File cer = new File(certPath);
		if (cer.exists()) {
			modelMap.put("cer", true);
			modelMap.put("msg", "证书已经成功导入,请勿重复");
			modelMap.put("success", false);
			modelMap.put("p10", "");
			return modelMap;
		}
		if (f.exists()) {
			String a = getCsrFile(csrPath);

			modelMap.put("p10", a);
			modelMap.put("success", true);
		} else {
			if (!f.getParentFile().exists()) {// 判断目标文件所在的目录是否存在
				// 如果目标文件所在的文件夹不存在，则创建父文件夹
				if (!f.getParentFile().mkdirs()) {// 判断创建目录是否成功
					modelMap.put("success", false);
					modelMap.put("msg", "创建目标文件所在的目录失败！");
				}
			}
			try {
				JKSFile jks = new JKSFile(jksPath);
				String p10 = P10Util.genP10(jks, csrPath, jksPath);
				modelMap.put("success", true);
				modelMap.put("p10", p10);
			} catch (Exception e1) {
				modelMap.put("success", false);
				modelMap.put("msg", "P10产生失败");
				// 删除文件
				FileUtil.deleteDir(dirPath);
			}

			try {
				// 保存DB
				keyService.serverCertRequestDB(csrPath, jksPath);
			} catch (Exception e) {
				LoggerUtil.errorlog("产生证书请求失败", e);
				FileUtil.deleteFile(csrPath);
				FileUtil.deleteFile(jksPath);
				modelMap.put("success", false);
				modelMap.put("msg", "产生证书请求失败");
				// 删除文件
				FileUtil.deleteDir(dirPath);
			}
		}

		return modelMap;
	}

	/**
	 * JKS证书导入
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keyJksImpCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERJKSCERTIMPORT)
	public Map<String, Object> keyJksImpCert(HttpServletRequest request, @RequestParam("certFile") MultipartFile file, HttpServletResponse response, @RequestParam("id") String id) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		// 初始化服务器文件
		initServerFile();
		Long kId = Long.parseLong(id);
		KeyVO keyVo = keyService.getKey(kId);
		if (keyVo != null) {
			if (file.isEmpty()) {
				response.getWriter().write("文件未上传");
			} else {
				String jksPath = keyVo.getKeyPath();
				String certPath = jksPath.substring(0, jksPath.lastIndexOf(Constants.SPLIT_DIR)) + "/" + Constants.CER;
				try {
					// 只能上传cer后缀文件
					if (!file.getOriginalFilename().endsWith(Constants.CERT_SUFFIX)) {
						response.getWriter().write("文件格式错误");
					} else if (FileUtil.checkPath(certPath)) {
						response.getWriter().write("证书已经成功导入,请勿重复");
					} else {
						// 校验密钥信息
						if (!keyService.isModify(kId))
							throw new WebDataException("密钥信息被篡改,操作失败");

						file.transferTo(new File(certPath));
						// 生成证书对象, 导入证书
						byte[] data = FileUtil.getFile(certPath);

						X509CertEnvelope certEnv = CertUtil.parseCert(data);
						// 校验证书
						boolean isCheckSignCert = false;
						boolean isCheckEncCert = false;
						boolean[] usage = certEnv.getKeyUsage();
						if (usage != null && usage[0])// 签名证书
							isCheckSignCert = true;

						if (usage != null && usage[2] && usage[3]) // 加密证书
							isCheckEncCert = true;

						if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
							throw new WebDataException("未知证书密钥用法");

						certChainService.verifyCert(certEnv.getEncoded(), isCheckSignCert, isCheckEncCert);

						JKSFile jks = new JKSFile(jksPath);
						P10Util.importP10(certEnv.getX509Cert(), jks, jksPath);

						// 更新DB
						long keyId = keyVo.getId();
						keyService.serverCertImportCertDB(data, certEnv.getX509Cert(), certPath, keyId);
						response.getWriter().write("ok");
					}
				} catch (WebDataException e) {
					LoggerUtil.errorlog("导入失败", e);
					FileUtil.deleteFile(certPath);
					response.getWriter().write(e.getMessage());
				} catch (Exception e) {
					LoggerUtil.errorlog("导入失败", e);
					FileUtil.deleteFile(certPath);
					response.getWriter().write("导入失败");
				}
			}

		} else {
			response.getWriter().write("获取证书对象失败");
		}

		return null;
	}

	/**
	 * PFX证书导入
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keyPfxImpCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERPFXCERTIMPORT)
	public Map<String, Object> keyPfxImpCert(HttpServletRequest request, @RequestParam("certFilePfx") MultipartFile file, String pwd, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String dirPath = Constants.KEY_PATH + FileUtil.getFileName() + "/";
		String pfxPath = dirPath + Constants.PFX;
		String certPath = dirPath + Constants.CER;
		// 初始化服务器文件
		initServerFile();

		if (file.isEmpty()) {
			response.getWriter().write("文件未上传");
		} else {
			try {
				// 只能上传pfx后缀文件
				if (!file.getOriginalFilename().endsWith(Constants.PFX_SUFFIX)) {
					response.getWriter().write("文件格式错误");
				} else if (FileUtil.checkPath(pfxPath)) {
					response.getWriter().write("证书已经成功导入,请勿重复");
				} else {
					File impoPfx = new File(pfxPath);
					if (!impoPfx.exists()) {
						impoPfx.mkdirs();
					}
					file.transferTo(impoPfx);

					// 校验PFX 密码
					try {
						KeyStoreUtil.loadKey(pwd, Constants.PFX_SUFFIX, FileUtil.getFile(pfxPath));
					} catch (Exception e) {
						throw new WebDataException("PFX文件密码错误");
					}

					// 保存证书
					byte[] data = KeyStoreUtil.getCertFromPfx(pwd, pfxPath);

					FileUtil.storeFile(certPath, data);
					X509CertEnvelope certEnv = CertUtil.parseCert(data);
					// 校验证书
					boolean isCheckSignCert = false;
					boolean isCheckEncCert = false;
					boolean[] usage = certEnv.getKeyUsage();
					if (usage != null && usage[0])// 签名证书
						isCheckSignCert = true;

					if (usage != null && usage[2] && usage[3]) // 加密证书
						isCheckEncCert = true;

					if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
						throw new WebDataException("未知证书密钥用法");

					certChainService.verifyCert(data, isCheckSignCert, isCheckEncCert);

					// 保存DB
					keyService.serverCertPfxImportCertDB(pwd, certEnv.getX509Cert(), dirPath);

					response.getWriter().write("ok");
				}
			} catch (WebDataException e) {
				LoggerUtil.errorlog("导入失败", e);
				FileUtil.deleteDir(dirPath);
				response.getWriter().write(e.getMessage());
			} catch (Exception e) {
				LoggerUtil.errorlog("导入失败", e);
				FileUtil.deleteDir(dirPath);
				response.getWriter().write("导入失败");
			}
		}
		return null;
	}

	/**
	 * SM2证书请求
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keySm2Csr")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERSM2CERTREQUEST)
	public Map<String, Object> keySm2Csr() throws Exception {
		String dirPath = Constants.KEY_PATH + FileUtil.getFileName() + "/";
		String csrPath = dirPath + Constants.CSR;
		String certPath = dirPath + Constants.CER;
		// 初始化服务器文件
		initServerFile();

		Map<String, Object> modelMap = new HashMap<String, Object>();
		File f = new File(csrPath);
		File cer = new File(certPath);
		if (cer.exists()) {
			modelMap.put("cer", true);
			modelMap.put("msg", "证书已经成功导入,请勿重复");
			modelMap.put("success", false);
			modelMap.put("p10", "");
			return modelMap;
		}
		if (f.exists()) {
			String a = getCsrFile(csrPath);

			modelMap.put("p10", a);
			modelMap.put("success", true);
		} else {
			if (!f.getParentFile().exists()) {// 判断目标文件所在的目录是否存在
				// 如果目标文件所在的文件夹不存在，则创建父文件夹
				if (!f.getParentFile().mkdirs()) {// 判断创建目录是否成功
					modelMap.put("success", false);
					modelMap.put("msg", "创建目标文件所在的目录失败！");
				}
			}
			String certDN = Constants.P10_DN + String.valueOf(System.currentTimeMillis());
			String p10 = NetSignUtil.genSm2P10(certDN, dirPath);
			modelMap.put("success", true);
			modelMap.put("p10", p10);

			try {
				// 保存DB
				String priPath = dirPath + Constants.PRI_KEY;
				keyService.serverSMCertRequestDB(csrPath, priPath);
			} catch (Exception e) {
				LoggerUtil.errorlog("产生证书请求失败", e);
				FileUtil.deleteFile(csrPath);
				modelMap.put("success", false);
				modelMap.put("msg", "产生证书请求失败");
				// 删除文件
				FileUtil.deleteDir(dirPath);
			}
		}

		return modelMap;
	}

	/**
	 * SM2签名证书导入
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keySm2ImpCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERSM2SIGNCERTIMPORT)
	public Map<String, Object> keySm2ImpSignCert(@RequestParam("certFileSM") MultipartFile file, HttpServletResponse response, @RequestParam("id") String id)
			throws Exception {
		response.setContentType("text/html;charset=utf-8");
		// 初始化服务器文件
		initServerFile();
		Long kId = Long.parseLong(id);
		KeyVO keyVo = keyService.getKey(kId);
		if (keyVo != null) {
			if (file.isEmpty()) {
				response.getWriter().write("文件未上传");
			} else {
				String priPath = keyVo.getKeyPath();
				String dirPath = priPath.substring(0, priPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
				String certPath = dirPath + Constants.CER;

				// 只能上传cer后缀文件
				if (!file.getOriginalFilename().endsWith(Constants.CERT_SUFFIX)) {
					response.getWriter().write("文件格式错误");
				} else if (FileUtil.checkPath(certPath)) {
					response.getWriter().write("证书已经成功导入,请勿重复");
				} else {
					try {
						// 校验密钥信息
						if (!keyService.isModify(kId))
							throw new WebDataException("密钥信息被篡改,操作失败");

						file.transferTo(new File(certPath));
						// 生成证书对象, 导入证书
						byte[] data = file.getBytes();

						X509Certificate x509Cert = NetSignUtil.getCert(data);
						// 校验证书
						boolean isCheckSignCert = false;
						boolean isCheckEncCert = false;
						boolean[] usage = x509Cert.getKeyUsage();
						if (usage != null && usage[0])// 签名证书
							isCheckSignCert = true;

						if (usage != null && usage[2] && usage[3]) // 加密证书
							isCheckEncCert = true;

						if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
							throw new WebDataException("未知证书密钥用法");

						certChainService.verifyCert(x509Cert.getEncoded(), isCheckSignCert, isCheckEncCert);

						// 匹配证书
						String keyPath = keyVo.getKeyPath();
						String keyPwd = new String(StringUtil.base64Decode(keyVo.getKeyPwd()));
						SM2PublicKey sm2Pub = new SM2PublicKey(x509Cert.getPublicKey().getEncoded());
						SM2PrivateKey sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(keyPwd, Constants.PRI_SUFFIX, FileUtil.getFile(keyPath));
						if (!NetSignUtil.checkSm2KeyPair(sm2Pub, sm2Pri))
							throw new WebDataException("所导入的证书不匹配");

						FileUtil.storeFile(certPath, x509Cert.getEncoded());

						// 更新DB
						keyService.serverSMCertImport(data, x509Cert, certPath, kId);
						response.getWriter().write("ok");

					} catch (WebDataException e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFile(certPath);
						response.getWriter().write(e.getMessage());
					} catch (Exception e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFile(certPath);
						response.getWriter().write("导入失败");
					}
				}
			}

		} else {
			response.getWriter().write("获取证书对象失败");
		}

		return null;
	}

	/**
	 * 导入SM2加密证书
	 * 
	 * @param request
	 * @param env_file
	 * @param cert_file
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keyImpSM2EncCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERSM2ENCCERTIMPORT)
	public Map<String, Object> keySm2ImpEncCert(HttpServletRequest request, @RequestParam("SM2Env") MultipartFile env_file, @RequestParam("SM2Cert") MultipartFile cert_file,
			HttpServletResponse response, @RequestParam("id") String id) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		// 初始化服务器文件
		initServerFile();
		long kId = Long.parseLong(id);
		KeyVO keyVo = keyService.getKey(kId);
		if (keyVo != null) {
			if (env_file.isEmpty()) {
				response.getWriter().write("数字信封未上传");
			} else if (cert_file.isEmpty()) {
				response.getWriter().write("加密证书未上传");
			} else {
				String priPath = keyVo.getKeyPath();
				String dirPath = priPath.substring(0, priPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
				String envPath = dirPath + Constants.ENV;
				String certPath = dirPath + Constants.CER;

				if (FileUtil.checkPath(certPath)) {
					response.getWriter().write("证书已经成功导入,请勿重复");
				} else if (env_file.getOriginalFilename().endsWith(Constants.ENV_SUFFIX) && cert_file.getOriginalFilename().endsWith(Constants.CERT_SUFFIX)) {
					try {
						// 校验密钥信息
						if (!keyService.isModify(kId))
							throw new WebDataException("密钥信息被篡改,操作失败");

						env_file.transferTo(new File(envPath));
						// 生成证书对象, 导入证书
						byte[] envData = env_file.getBytes();

						SM2PrivateKey prik = (SM2PrivateKey) KeyStoreUtil.loadKey(keyVo.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(keyVo.getKeyPath()));
						byte[] encPriK = NetSignUtil.parseKeyPairProtectData(StringUtil.base64Decode(envData), prik);

						// 校验是否存在
						Key k = new Key();
						k.setKeyMode(Constants.KEY_SUFFIX);
						List<Key> keys = keyService.getKeys(k);
						SM2PrivateKey sm2Pri = new SM2PrivateKey();
						for (Key key : keys) {
							sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(key.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(key.getKeyPath()));
							if (Arrays.equals(encPriK, sm2Pri.getD()))
								throw new WebDataException("数字信封中私钥已存在");
						}

						// 数字信封中的私钥
						SM2PrivateKey env_prik = new SM2PrivateKey(encPriK);

						// 获取公钥信息
						cert_file.transferTo(new File(certPath));
						byte[] certData = cert_file.getBytes();

						X509Certificate x509Cert = NetSignUtil.getCert(certData);
						// 校验证书
						boolean isCheckSignCert = false;
						boolean isCheckEncCert = false;
						boolean[] usage = x509Cert.getKeyUsage();
						if (usage != null && usage[0])// 签名证书
							isCheckSignCert = true;

						if (usage != null && usage[2] && usage[3]) // 加密证书
							isCheckEncCert = true;

						if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
							throw new WebDataException("未知证书密钥用法");

						certChainService.verifyCert(x509Cert.getEncoded(), isCheckSignCert, isCheckEncCert);

						X509CertEnvelope x509CertEnv = CertUtil.parseCert(certData);
						SM2PublicKey pubk = new SM2PublicKey(x509CertEnv.getPublicKey().getEncoded());
						if (!NetSignUtil.checkSm2KeyPair(pubk, env_prik))
							throw new WebDataException("所导入的数字信封和加密证书不匹配");

						// 对比是否存在
						String certDN = x509CertEnv.getCertDn();
						int usa;
						try {
							usa = keyService.getCertUsage(x509CertEnv.getX509Cert());
						} catch (Exception e) {
							throw new WebDataException("无法获取证书密钥用法");
						}

						Key kc = new Key();
						kc.setCertDn(certDN);
						kc.setCertUsage(usa);

						List<Key> keyList = keyService.getKeys(kc);
						if (keyList.size() > 0)
							throw new WebDataException("密钥证书已存在");

						// 更新DB
						keyService.serverSMCertImport(certData, x509Cert, certPath, kId);

						FileUtil.deleteFile(envPath);
						response.getWriter().write("ok");

					} catch (WebDataException e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFile(certPath);
						FileUtil.deleteFile(envPath);
						response.getWriter().write(e.getMessage());
					} catch (Exception e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFile(certPath);
						FileUtil.deleteFile(envPath);
						response.getWriter().write("导入失败");
					}
				} else {
					response.getWriter().write("文件格式错误,请选择正确文件(.env .cer)");
				}
			}

		} else {
			response.getWriter().write("获取证书对象失败");
		}

		return null;
	}

	/**
	 * 加密卡证书请求
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keySm2CardCsr")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERSM2CARDCERTREQUEST)
	public Map<String, Object> keySm2CardCsr() throws Exception {
		String dirPath = Constants.KEY_PATH + FileUtil.getFileName() + "/";
		String csrPath = dirPath + Constants.CSR;
		String certPath = dirPath + Constants.CER;
		// 初始化服务器文件
		initServerFile();

		Map<String, Object> modelMap = new HashMap<String, Object>();
		File f = new File(csrPath);
		File cer = new File(certPath);
		if (cer.exists()) {
			modelMap.put("cer", true);
			modelMap.put("msg", "证书已经成功导入,请勿重复");
			modelMap.put("success", false);
			modelMap.put("p10", "");
			return modelMap;
		}
		if (f.exists()) {
			String a = getCsrFile(csrPath);

			modelMap.put("p10", a);
			modelMap.put("success", true);
		} else {
			if (!f.getParentFile().exists()) {// 判断目标文件所在的目录是否存在
				// 如果目标文件所在的文件夹不存在，则创建父文件夹
				if (!f.getParentFile().mkdirs()) {// 判断创建目录是否成功
					modelMap.put("success", false);
					modelMap.put("msg", "创建目标文件所在的目录失败！");
				}
			}
			try {
				// 获取id(加密卡位置)
				int hKey = keyService.getKeyCardId();

				// 产生密钥对
				List<byte[]> retList = FishManUtil.genSM2Keypair(hKey);
				SM2PublicKey pubk = new SM2PublicKey(retList.get(0));
				SM2PrivateKey prik = new SM2PrivateKey(retList.get(1));

				String certDN = Constants.P10_DN + String.valueOf(System.currentTimeMillis());
				String p10 = FishManUtil.genSM2P10(pubk, prik, certDN, dirPath);

				modelMap.put("success", true);
				modelMap.put("p10", p10);

				// 保存DB
				String priPath = dirPath + Constants.PRI_CARD_KEY;
				keyService.serverkeyCardRequestDB(csrPath, priPath, hKey);

				// PriKeyCard.addPriMap(id, prik); // 加密卡密钥信息备份到内存中
			} catch (WebDataException e) {
				LoggerUtil.errorlog("产生证书请求失败:" + e.getMessage(), e);
				FileUtil.deleteFile(csrPath);
				modelMap.put("success", false);
				modelMap.put("msg", "请求失败," + e.getMessage());
				// 删除文件
				FileUtil.deleteDir(dirPath);
			} catch (Exception e) {
				LoggerUtil.errorlog("产生证书请求失败", e);
				FileUtil.deleteFile(csrPath);
				modelMap.put("success", false);
				modelMap.put("msg", "产生证书请求失败");
				// 删除文件
				FileUtil.deleteDir(dirPath);
			}
		}

		return modelMap;
	}

	/**
	 * 加密卡签名证书导入
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keySm2CardImpSignCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERSM2CARDCERTIMPORT)
	public Map<String, Object> keySm2CardImpSignCert(@RequestParam("certFileSMCard") MultipartFile file, HttpServletResponse response, @RequestParam("id") String id)
			throws Exception {
		response.setContentType("text/html;charset=utf-8");
		// 初始化服务器文件
		initServerFile();
		long kId = Long.parseLong(id);
		KeyVO keyVo = keyService.getKey(kId);
		if (keyVo != null) {
			if (file.isEmpty()) {
				response.getWriter().write("文件未上传");
			} else {
				String priPath = keyVo.getKeyPath();
				String dirPath = priPath.substring(0, priPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
				String certPath = dirPath + Constants.CER;

				// 导入证书文件
				if (!file.getOriginalFilename().endsWith(Constants.CERT_SUFFIX)) {
					response.getWriter().write("文件格式错误,请选择正确文件(.cer)");
				} else if (FileUtil.checkPath(certPath)) {
					response.getWriter().write("证书已经成功导入,请勿重复");
				} else {
					try {
						// 校验密钥信息
						if (!keyService.isModify(kId))
							throw new WebDataException("密钥信息被篡改,操作失败");

						file.transferTo(new File(certPath));
						// 生成证书对象, 导入证书
						byte[] data = file.getBytes();

						X509Certificate x509Cert = NetSignUtil.getCert(data);
						// 校验证书
						boolean isCheckSignCert = false;
						boolean isCheckEncCert = false;
						boolean[] usage = x509Cert.getKeyUsage();
						if (usage != null && usage[0])// 签名证书
							isCheckSignCert = true;

						if (usage != null && usage[2] && usage[3]) // 加密证书
							isCheckEncCert = true;

						if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
							throw new WebDataException("未知证书密钥用法");

						certChainService.verifyCert(x509Cert.getEncoded(), isCheckSignCert, isCheckEncCert);

						// 匹配证书公钥和私钥
						String keyPath = keyVo.getKeyPath();
						String keyPwd = new String(StringUtil.base64Decode(keyVo.getKeyPwd()));
						SM2PublicKey sm2Pub = new SM2PublicKey(x509Cert.getPublicKey().getEncoded());
						SM2PrivateKey sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(keyPwd, Constants.PRI_SUFFIX, FileUtil.getFile(keyPath));
						if (!NetSignUtil.checkSm2KeyPair(sm2Pub, sm2Pri))
							throw new WebDataException("所导入的证书不匹配");

						FileUtil.storeFile(certPath, x509Cert.getEncoded());

						// 更新DB
						keyService.serverSMCertImport(data, x509Cert, certPath, kId);
						response.getWriter().write("ok");

					} catch (WebDataException e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFile(certPath);
						response.getWriter().write(e.getMessage());
					} catch (Exception e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFile(certPath);
						response.getWriter().write("导入失败");
					}
				}
			}
		} else {
			response.getWriter().write("获取证书对象失败");
		}

		return null;
	}

	/**
	 * 加密卡加密证书导入
	 * 
	 * @param request
	 * @param file
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/keySm2CardImpEncCert")
	@SealLog(optype = Constants.LOG_OPTYPE_SERVERSM2CARDENCIMPORT)
	public Map<String, Object> keySm2CardImpEncCert(HttpServletRequest request, @RequestParam("cardEnv") MultipartFile env_file, @RequestParam("cardCert") MultipartFile cert_file,
			HttpServletResponse response, @RequestParam("id") String id) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		// 初始化服务器文件
		initServerFile();
		long kId = Long.parseLong(id);
		KeyVO keyVo = keyService.getKey(kId);
		if (keyVo != null) {
			if (env_file.isEmpty()) {
				response.getWriter().write("数字信封未上传");
			} else if (cert_file.isEmpty()) {
				response.getWriter().write("加密证书未上传");
			} else {
				String priPath = keyVo.getKeyPath();
				String dirPath = priPath.substring(0, priPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
				String envPath = dirPath + Constants.ENV;
				String certPath = dirPath + Constants.CER;

				if (FileUtil.checkPath(certPath)) {
					response.getWriter().write("证书已经成功导入,请勿重复");
				} else if (env_file.getOriginalFilename().endsWith(Constants.ENV_SUFFIX) && cert_file.getOriginalFilename().endsWith(Constants.CERT_SUFFIX)) {
					try {
						// 校验密钥信息
						if (!keyService.isModify(kId))
							throw new WebDataException("密钥信息被篡改,操作失败");

						env_file.transferTo(new File(envPath));
						// 生成证书对象, 导入证书
						byte[] envData = env_file.getBytes();

						SM2PrivateKey prik = (SM2PrivateKey) KeyStoreUtil.loadKey(keyVo.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(keyVo.getKeyPath()));
						byte[] encPriK = NetSignUtil.parseKeyPairProtectData(StringUtil.base64Decode(envData), prik);

						// 校验是否存在
						Key k = new Key();
						k.setKeyMode(Constants.KEY_SUFFIX);
						List<Key> keys = keyService.getKeys(k);
						SM2PrivateKey sm2Pri = new SM2PrivateKey();
						for (Key key : keys) {
							sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(key.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(key.getKeyPath()));
							if (Arrays.equals(encPriK, sm2Pri.getD()))
								throw new WebDataException("数字信封中私钥已存在");
						}

						// 数字信封中的私钥
						SM2PrivateKey env_prik = new SM2PrivateKey(encPriK);

						// 获取公钥信息
						cert_file.transferTo(new File(certPath));
						byte[] certData = cert_file.getBytes();
						X509CertEnvelope x509CertEnv = CertUtil.parseCert(certData);
						SM2PublicKey pubk = new SM2PublicKey(x509CertEnv.getPublicKey().getEncoded());
						if (!NetSignUtil.checkSm2KeyPair(pubk, env_prik))
							throw new WebDataException("所导入的数字信封和加密证书不匹配");

						// 对比是否存在
						String certDN = x509CertEnv.getCertDn();
						int usa;
						try {
							usa = keyService.getCertUsage(x509CertEnv.getX509Cert());
						} catch (Exception e) {
							throw new WebDataException("无法获取证书密钥用法");
						}

						Key kc = new Key();
						kc.setCertDn(certDN);
						kc.setCertUsage(usa);

						List<Key> keyList = keyService.getKeys(kc);
						if (keyList.size() > 0)
							throw new WebDataException("密钥证书已存在");

						// 校验证书
						X509Certificate x509Cert = NetSignUtil.getCert(certData);
						boolean isCheckSignCert = false;
						boolean isCheckEncCert = false;
						boolean[] usage = x509Cert.getKeyUsage();
						if (usage != null && usage[0])// 签名证书
							isCheckSignCert = true;

						if (usage != null && usage[2] && usage[3]) // 加密证书
							isCheckEncCert = true;

						if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
							throw new WebDataException("未知证书密钥用法");

						certChainService.verifyCert(x509Cert.getEncoded(), isCheckSignCert, isCheckEncCert);

						// 删除原有数据 删除加密卡中密钥对
						delKeyCard(String.valueOf(kId));

						// 重新生成证书文件
						FileUtil.storeFile(certPath, x509CertEnv.getX509Cert().getEncoded());

						// 将新的密钥对导入加密卡
						int hKey = keyVo.getHsmId();
						List<byte[]> keyDataList = new ArrayList<>();
						keyDataList.add(0, pubk.getEncoded4ex());
						keyDataList.add(1, env_prik.getD());
						FishManUtil.importSM2Key(hKey, keyDataList);

						// 产生P10 保存文件
						String certDNs = Constants.P10_DN + String.valueOf(System.currentTimeMillis());
						FishManUtil.genSM2P10(pubk, env_prik, certDNs, dirPath);

						// 更新DB
						keyService.importEncCertToDB(dirPath, x509CertEnv, hKey, usa);

						FileUtil.deleteFile(envPath);
						response.getWriter().write("ok");

					} catch (WebDataException e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFileAndDir(dirPath);
						response.getWriter().write(e.getMessage());
					} catch (Exception e) {
						LoggerUtil.errorlog("导入失败", e);
						FileUtil.deleteFileAndDir(dirPath);
						response.getWriter().write("导入失败");
					}
				} else {
					response.getWriter().write("文件格式错误,请选择正确文件(.env .cer)");
				}
			}

		} else {
			response.getWriter().write("获取证书对象失败");
		}

		return null;
	}

	/**
	 * 国密加密卡备份列表
	 * 
	 * @param id
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_BACKUPSKEY)
	@RequestMapping(value = "toBackupKeyCard")
	public String toBackupKeyCard(long id) throws Exception {
		KeyVO keyVO = keyService.getKey(id);
		// 是否为完整密钥
		File f = new File(keyVO.getCertPath());
		if (!f.exists())
			throw new Exception("未导入密钥证书,无法备份");

		// 校验密钥信息
		if (!keyService.isModify(id))
			throw new Exception("密钥信息被篡改,操作失败");

		int hKey = keyVO.getHsmId();
		String[] strs;
		try {
			// 导出密钥
			List<byte[]> kmList = FishManUtil.exportSM2Key(hKey);
			byte[] Km = new byte[97];
			byte[] pPub = kmList.get(0);
			byte[] pPri = kmList.get(1);
			System.arraycopy(pPub, 0, Km, 0, pPub.length);
			System.arraycopy(pPri, 0, Km, pPub.length, pPri.length);

			if (HexUtil.byte2Int(Km) == 0)
				throw new Exception("未获取到加密卡密钥");

			// 分割密钥
			strs = ShamirUtil.generate(Km, 3, 5);

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		request.setAttribute("str1", strs[0]);
		request.setAttribute("str2", strs[1]);
		request.setAttribute("str3", strs[2]);
		request.setAttribute("str4", strs[3]);
		request.setAttribute("str5", strs[4]);
		request.setAttribute("keyId", id);

		return "key/backupKeyCard";
	}

	/**
	 * 进入国密加密卡恢复列表
	 */
	@RequestMapping(value = "toRecoveryKeyCard")
	public String toRecoveryKeyCard(Long id) {
		request.setAttribute("keyId", id);
		return "key/recoveryKeyCard";
	}

	/**
	 * 国密加密卡恢复
	 * 
	 * @throws Exception
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_RECOVERYKEY)
	@RequestMapping(value = "recoverKeyCard")
	public ModelAndView recoverKeyCard(HttpServletRequest request, String file1, String file2, String file3, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String dirPath = Constants.KEY_PATH + FileUtil.getFileName() + "/";
		try {
			long oldKid = -1;
			String[] fileStr = new String[3];
			fileStr[0] = file1;
			fileStr[1] = file2;
			fileStr[2] = file3;
			// 恢复分割的密钥
			byte[] keyData = ShamirUtil.recover(fileStr);

			if (HexUtil.byte2Int(keyData) == 0)
				throw new WebDataException("未获取到密钥");

			// 截取公钥私钥
			byte[] KmPub = new byte[65];
			System.arraycopy(keyData, 0, KmPub, 0, KmPub.length);
			byte[] KmPri = new byte[32];
			System.arraycopy(keyData, 65, KmPri, 0, KmPri.length);
			List<byte[]> keyDataList = new ArrayList<>();
			keyDataList.add(0, KmPub);
			keyDataList.add(1, KmPri);

			// 校验是否存在
			boolean inDB = false;
			boolean inCard = false;
			Key k = new Key();
			k.setKeyMode(Constants.KEY_SUFFIX);
			List<Key> keys = keyService.getKeys(k);
			SM2PrivateKey sm2Pri = new SM2PrivateKey();
			for (Key key : keys) {
				sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(key.getKeyPwdPlain(), Constants.PRI_SUFFIX, FileUtil.getFile(key.getKeyPath()));
				if (Arrays.equals(KmPri, sm2Pri.getD())) {// 存在DB
					inDB = true;
					oldKid = key.getId();
				}
			}

			KeyVO inDBKey = new KeyVO();
			if (oldKid != -1)
				inDBKey = keyService.getKey(oldKid);

			// 加密卡中是否含密钥
			if (inDB && FishManUtil.exiteSM2Key(inDBKey.getHsmId())) {
				List<byte[]> kmList = FishManUtil.exportSM2Key(inDBKey.getHsmId());
				byte[] pPri = kmList.get(1);
				if (Arrays.equals(KmPri, pPri)) // 存在加密卡
					inCard = true;
				else { // DB存在, 加密卡存在其他的密钥
					Properties extMsg = new Properties();
					extMsg.put("existCard", true);
					extMsg.put("keyPri", KmPri);
					extMsg.put("keyPub", KmPub);
					extMsg.put("keyRecoveryId", oldKid);
					return getModelAndView(getSuccMap(extMsg));
				}
			} else
				inCard = false; // 加密卡密钥为空

			if (inDB && inCard)
				throw new WebDataException("所恢复密钥已存在");

			if (!inDB) { // 均不存在
				Properties extMsg = new Properties();
				extMsg.put("notInDB", true);
				return getModelAndView(getSuccMap(extMsg));
			}

			if (inDB && !inCard) { // 存在于DB 不存在于加密卡
				// 生成.pem文件
				KeyVO oldk = keyService.getKey(oldKid);
				dirPath = oldk.getKeyPath();
				dirPath = dirPath.substring(0, dirPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
				KeyStoreUtil.storePemSm2Key(KmPub, KmPri, dirPath);

				// 导入加密卡
				FishManUtil.importSM2Key(oldk.getHsmId(), keyDataList);
				Properties extMsg = new Properties();
				extMsg.put("InCard", true);
				return getModelAndView(getSuccMap(extMsg));
			}
		} catch (WebDataException e) {
			FileUtil.deleteDir(dirPath);
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			FileUtil.deleteDir(dirPath);
			throw e;
		}

		return null;
	}
	
	/**
	 * 恢复密钥到数据库以及加密卡内
	 * 
	 * @param request
	 * @param file1
	 * @param file2
	 * @param file3
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "recoverKeyToDBAndCard")
	public ModelAndView recoverKeyToDBAndCard(HttpServletRequest request, String file1, String file2, String file3, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String dirPath = Constants.KEY_PATH + FileUtil.getFileName() + "/";
		try {
			// 获取没加密卡密钥位置
			int hKey = keyService.getKeyCardId();
			
			String[] fileStr = new String[3];
			fileStr[0] = file1;
			fileStr[1] = file2;
			fileStr[2] = file3;
			// 恢复分割的密钥
			byte[] keyData = ShamirUtil.recover(fileStr);

			if (HexUtil.byte2Int(keyData) == 0)
				throw new WebDataException("未获取到密钥");

			// 截取公钥私钥
			byte[] KmPub = new byte[65];
			System.arraycopy(keyData, 0, KmPub, 0, KmPub.length);
			byte[] KmPri = new byte[32];
			System.arraycopy(keyData, 65, KmPri, 0, KmPri.length);
			List<byte[]> keyDataList = new ArrayList<>();
			keyDataList.add(0, KmPub);
			keyDataList.add(1, KmPri);

			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("recoverCert");
				String fileType = Constants.SPOT + FileUtil.getFileSuffix(file.getOriginalFilename());
				if (!fileType.equals(Constants.CERT_SUFFIX))
					throw new WebDataException("请选择正确格式文件:" + Constants.CERT_SUFFIX);
				
				if (file != null && file.getSize() > 0) {
					// 导入密钥
					FishManUtil.importSM2Key(hKey, keyDataList);

					// 产生证书请求 保存文件
					String certDN = Constants.P10_DN + String.valueOf(System.currentTimeMillis());
					SM2PublicKey pubk = new SM2PublicKey(KmPub);
					SM2PrivateKey prik = new SM2PrivateKey(KmPri);
					// 产生P10,保存CSR文件
					FishManUtil.genSM2P10(pubk, prik, certDN, dirPath);

					// 恢复到数据库中
					long recoverKeyId = keyService.serverSMCardCertToDB(dirPath, hKey);
					// 导入证书
					keySm2CardImpSignCert(file, response, String.valueOf(recoverKeyId));
				}
				else
					throw new WebDataException("未获取到证书文件");
			}

			// 将密钥信息保存在内存
			// PriKeyCard.addPriMap(hKey, prik);
		} catch (WebDataException e) {
			FileUtil.deleteDir(dirPath);
			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			FileUtil.deleteDir(dirPath);
			throw e;
		}

		return null;
	}

	/**
	 * 恢复到加密卡中
	 * 
	 * @param keyPri
	 * @param keyPub
	 * @param kid
	 * @throws Exception
	 */
	@RequestMapping(value = "recoverKeyToCard")
	public ModelAndView recoverKeyToCard(byte[] keyPri, byte[] keyPub, long kid, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String dirPath = null;
		try {
			if (keyPri == null || keyPub == null)
				throw new WebDataException("未获取到密钥数据");

			// 生成.pem文件
			KeyVO oldk = keyService.getKey(kid);
			if (oldk == null)
				throw new WebDataException("未获取到DB密钥信息");

			dirPath = oldk.getKeyPath();
			dirPath = dirPath.substring(0, dirPath.lastIndexOf(Constants.SPLIT_DIR)) + "/";
			KeyStoreUtil.storePemSm2Key(keyPub, keyPri, dirPath);

			// 删除加密卡密钥信息
			FishManUtil.delKey(oldk.getHsmId());
			// 导入加密卡
			List<byte[]> keyDataList = new ArrayList<>();
			keyDataList.add(0, keyPub);
			keyDataList.add(1, keyPri);
			FishManUtil.importSM2Key(oldk.getHsmId(), keyDataList);
			
			response.getWriter().write("ok");

			// PriKeyCard.addPriMap(kid, new SM2PrivateKey(KmPri)); // 将密钥信息保存在内存
		} catch (WebDataException e) {
			if (dirPath != null)
				FileUtil.deleteFile(dirPath);

			response.getWriter().write(e.getMessage());
		} catch (Exception e) {
			if (dirPath != null)
				FileUtil.deleteFile(dirPath);

			throw e;
		}
		return null;
	}

	/**
	 * 删除密钥
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "delKey")
	@SealLog(optype = Constants.LOG_OPTYPE_DELKEY)
	public ModelAndView delKey(String id) throws Exception {
		try {
			List<String> pathList = new ArrayList<String>();
			if (StringUtil.isNotBlank(id)) {
				String ids[] = id.split(Constants.SPLIT_2);
				for (int i = 0; i < ids.length; i++) {
					if (ids[i] != null) {
						KeyVO key = keyService.getKey(Long.valueOf(ids[i]));
						// 添加删除文件路径
						String keyPath = key.getKeyPath();
						if (keyPath != null && !"".equals(keyPath))
							pathList.add(key.getKeyPath());
					}
				}
				keyService.deleteKey(id);
				// 删除文件
				for (String path : pathList) {
					FileUtil.deleteFileAndDir(path);
				}
			}
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("删除成功"));
	}

	/**
	 * 删除加密卡密钥
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "delKeyCard")
	@SealLog(optype = Constants.LOG_OPTYPE_DELKEY)
	public ModelAndView delKeyCard(String id) throws Exception {
		try {
			List<String> pathList = new ArrayList<String>();
			if (StringUtil.isNotBlank(id)) {
				String ids[] = id.split(Constants.SPLIT_2);

				// 添加密钥删除文件
				for (int i = 0; i < ids.length; i++) {
					if (ids[i] != null) {
						ConfigUtil config = ConfigUtil.getInstance();
						long delkeyId = Long.valueOf(ids[i]);
						
						// 如果为指定签名或加密密钥, 不允许删除
						long signKeyId = config.getSignKeyId();
						if (delkeyId == signKeyId)
							throw new WebDataException("指定签名密钥,不能删除");

						// 如果为指定签名或加密密钥, 不允许删除
						long encKeyId = config.getEncrpKeyId();
						if (delkeyId == encKeyId)
							throw new WebDataException("指定加密密钥,不能删除");
						
						KeyVO key = keyService.getKey(Long.valueOf(ids[i]));
						
						// 添加删除文件路径
						String keyPath = key.getKeyPath();
						if (keyPath != null && !"".equals(keyPath))
							pathList.add(key.getKeyPath());
						
						KeyVO kVo = keyService.getKey(delkeyId);
						FishManUtil.delSM2Keypair(kVo.getHsmId());
						// PriKeyCard.removePriMap(Integer.valueOf(ids[i])); // 移除内存中加密卡密钥信息
					}
				}
				
				// 删除数据库
				keyService.deleteKey(id);

				// 删除文件
				for (String path : pathList) {
					FileUtil.deleteFileAndDir(path);
				}
			}
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap("删除成功"));
	}

	/**
	 * 转到密钥恢复
	 */
	@RequestMapping(value = "toRecoveryKey")
	public String toRecoveryKey() {
		return "key/recoverKey";
	}

	/**
	 * 密钥恢复
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "recoveryKey")
	@SealLog(optype = Constants.LOG_OPTYPE_RECOVERYKEY)
	public ModelAndView recoveryKey(String pwd, String pfxpwd, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		String upzipPath = Constants.TMP_PATH;
		String copyPath = "";
		String zipPath = "";
		try {
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("recoverKeyFile");
				if (file != null) {
					String fileOriName = file.getOriginalFilename();
					if (!fileOriName.endsWith(Constants.ZIP_SUFFIX))
						throw new WebDataException("文件类型错误..");

					// 创建解压文件路径
					copyPath = Constants.TMP_PATH + FileUtil.getFileName() + "/";
					File copyFile = new File(copyPath);
					if (!copyFile.exists()) {
						copyFile.mkdirs();// 创建文件夹
					}

					String fileOriginalName = upzipPath + FileUtil.getFileName() + "/" + Constants.BACKUP_KEY;
					File targetFile = new File(fileOriginalName);
					if (!targetFile.exists()) {
						targetFile.mkdirs();
					}
					file.transferTo(targetFile);
					// 上传文件夹路径
					zipPath = targetFile.getPath();

					if (pwd != null && pwd.length() == 8) {
						try {
							ZipUtil.unZip(zipPath, copyPath, pwd);
						} catch (Exception we) {
							throw new WebDataException("文件解压失败,请确认文件校验密码");
						}

						String fileDir = Constants.KEY_PATH + FileUtil.getFileName() + "/";
						// 解压成功将解压的文件copy到key目录
						FileUtil.copyDir(copyPath, fileDir);

						String newCsrPath = fileDir + Constants.CSR;
						File csrFile = new File(newCsrPath);
						String newJksPath = fileDir + Constants.JKS;
						File jksFile = new File(newJksPath);
						String newCertPath = fileDir + Constants.CER;
						File certFile = new File(newCertPath);
						String newPfxPath = fileDir + Constants.PFX;
						File pfxFile = new File(newPfxPath);
						String SMPriPath = fileDir + Constants.PRI_KEY;
						File SMPriFile = new File(SMPriPath);
						String SMCsrPath = fileDir + Constants.CSR;
						File SMCsrFile = new File(SMCsrPath);
						String SMCerPath = fileDir + Constants.CER;
						File SMCerFile = new File(SMCerPath);

						// 重新插入恢复的key
						if (csrFile.exists() && jksFile.exists() && certFile.exists()) {
							long newKid = 0;
							try {
								// 重新读取插入新的数据
								newKid = keyService.serverCertRequestDB(newCsrPath, newJksPath);
								// 导入证书
								byte[] certData = FileUtil.getFile(newCertPath);
								X509CertEnvelope certEnv = CertUtil.parseCert(certData);
								// 校验证书
								boolean isCheckSignCert = false;
								boolean isCheckEncCert = false;
								boolean[] usage = certEnv.getKeyUsage();
								if (usage != null && usage[0])// 签名证书
									isCheckSignCert = true;

								if (usage != null && usage[2] && usage[3]) // 加密证书
									isCheckEncCert = true;

								if (usage == null || (usage != null && (!usage[0] && !usage[2] && !usage[3]))) // 非签名非加密
									throw new WebDataException("未知证书密钥用法");

								certChainService.verifyCert(certEnv.getEncoded(), isCheckSignCert, isCheckEncCert);

								JKSFile jks = new JKSFile(newJksPath);
								P10Util.importP10(certEnv.getX509Cert(), jks, newJksPath);
								// 更新DB
								keyService.serverCertImportCertDB(certData, certEnv.getX509Cert(), newCertPath, newKid);

							} catch (WebDataException e) {
								// 删除文件
								KeyVO delKey = keyService.getKey(newKid);
								keyService.deleteKey(String.valueOf(newKid));
								certDataDao.deleteCertData(delKey.getCertDataId());
								// 增加删除记录
								idDeleteDao.insertIDDelete(delKey.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
								FileUtil.deleteDir(fileDir);
								throw new WebDataException(e.getMessage());
							} catch (Exception e) {
								// 删除文件
								KeyVO delKey = keyService.getKey(newKid);
								keyService.deleteKey(String.valueOf(newKid));
								certDataDao.deleteCertData(delKey.getCertDataId());
								FileUtil.deleteDir(fileDir);
								throw new WebDataException("恢复失败");
							}

						} else if (certFile.exists() && pfxFile.exists()) {// pfx文件格式
							if (pfxpwd.length() == 0) {
								// 删除文件
								FileUtil.deleteDir(fileDir);
								throw new WebDataException("请输入PFX文件校验密码");
							}
							try {
								// 校验PFX 密码
								KeyStoreUtil.loadKey(pfxpwd, Constants.PFX_SUFFIX, FileUtil.getFile(newPfxPath));
							} catch (Exception e) {
								// 删除文件
								FileUtil.deleteDir(fileDir);
								throw new WebDataException("请确认PFX文件校验密码");
							}

							try {
								// 保存证书
								byte[] pfxData = KeyStoreUtil.getCertFromPfx(pfxpwd, newPfxPath);
								FileUtil.storeFile(fileDir + Constants.CER, pfxData);
								X509CertEnvelope certEnv = CertUtil.parseCert(pfxData);
								// 校验证书
								boolean isCheckSignCert = false;
								boolean isCheckEncCert = false;
								boolean[] usage = certEnv.getKeyUsage();
								if (usage != null && usage[0])// 签名证书
									isCheckSignCert = true;
								else if (usage != null && usage[2] && usage[3]) // 加密证书
									isCheckEncCert = true;
								else
									throw new NetSealRuntimeException(ErrCode.UNKNOWN_USAGE, "cert usage unknown");

								certChainService.verifyCert(certEnv.getEncoded(), isCheckSignCert, isCheckEncCert);

								// 保存DB
								keyService.serverCertPfxImportCertDB(pfxpwd, certEnv.getX509Cert(), fileDir);

							} catch (WebDataException e) {
								// 删除文件
								FileUtil.deleteDir(fileDir);
								throw new WebDataException(e.getMessage());
							} catch (Exception e) {
								// 删除文件
								FileUtil.deleteDir(fileDir);
								throw new WebDataException("恢复失败");
							}

						} else if (SMCerFile.exists() && SMCsrFile.exists() && SMPriFile.exists()) { // SM密钥恢复
							long newSMKId = 0;
							try {
								newSMKId = keyService.serverSMCertRequestDB(SMCsrPath, SMPriPath);
								// 导入证书
								byte[] certData = FileUtil.getFile(SMCerPath);
								X509Certificate x509Cert = NetSignUtil.getCert(certData);
								// 校验证书
								certChainService.verifyCert(x509Cert.getEncoded());

								// 匹配证书
								SM2PublicKey sm2Pub = new SM2PublicKey(x509Cert.getPublicKey().getEncoded());
								SM2PrivateKey sm2Pri = (SM2PrivateKey) KeyStoreUtil.loadKey(Constants.KEY_PWD, Constants.PRI_SUFFIX, FileUtil.getFile(SMPriPath));
								if (!NetSignUtil.checkSm2KeyPair(sm2Pub, sm2Pri))
									throw new WebDataException("所导入的证书不匹配");

								keyService.serverSMCertImport(certData, x509Cert, SMCerPath, newSMKId);
							} catch (WebDataException e) {
								// 删除文件
								KeyVO delKey = keyService.getKey(newSMKId);
								keyService.deleteKey(String.valueOf(newSMKId));
								certDataDao.deleteCertData(delKey.getCertDataId());
								// 增加删除记录
								idDeleteDao.insertIDDelete(delKey.getCertDataId(), Constants.TABLE_SEAL_CERT_DATA);
								FileUtil.deleteDir(fileDir);
								throw new WebDataException(e.getMessage());
							} catch (Exception e) {
								// 删除文件
								FileUtil.deleteDir(fileDir);
								throw new WebDataException("恢复失败");
							}
						} else {
							throw new WebDataException("所恢复文件数据缺失,无法恢复");
						}
						response.getWriter().write("ok");
					} else {
						throw new WebDataException("密码输入有误");
					}

				}
			}
		} catch (WebDataException ex) {
			response.getWriter().write(ex.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			// 删除copyPath中文件
			FileUtil.deleteDir(copyPath);
			FileUtil.deleteFileAndDir(zipPath);
		}

		return null;
	}

	/**
	 * 转到下载密钥文件
	 */
	@RequestMapping(value = "toDownloadKey")
	public String toDownloadKey(String id) {
		request.setAttribute("id", id);
		return "key/downloadKey";
	}

	/**
	 * 密钥备份
	 * 
	 * @param pwd
	 * @param id
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "backupKey")
	@SealLog(optype = Constants.LOG_OPTYPE_BACKUPSKEY)
	public ModelAndView backupKey(String pwd, String id, HttpServletResponse response) throws Exception {
		KeyVO kVo = keyService.getKey(Long.parseLong(id));
		if (kVo == null)
			return this.getModelAndView(getErrMap("获取密钥对象失败"));

		// 检验信息
		if (!keyService.isModify(kVo.getId()))
			return this.getModelAndView(getErrMap("所选密钥数据被篡改,操作失败"));

		// 密钥是否有备份
		String backupFilePath = kVo.getBackupPath();
		if (StringUtil.isNotBlank(backupFilePath))
			FileUtil.deleteFileAndDir(backupFilePath);// 如果存在则删除备份文件

		String keypath = kVo.getKeyPath();
		String filePath = keypath.substring(0, keypath.lastIndexOf(Constants.SPLIT_DIR));
		// 备份文件路径
		String backPath = Constants.BACKUP_PATH + FileUtil.getFileName() + "/";
		File file = new File(backPath);
		if (!file.exists()) {
			file.mkdirs();// 创建文件夹
		}

		String zipPath = backPath + Constants.BACKUP_KEY;

		try {
			if (pwd != null && pwd.length() == 8) {
				// 压缩文件
				ZipUtil.zip(filePath, zipPath, pwd);

				kVo.setBackupPath(zipPath);
				kVo.setUpdateTime(DateUtil.getCurrentTime());
				int r = keyService.updateKey(kVo);
				if (r == 0) {
					return this.getModelAndView(getErrMap("密钥路径有误"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.getModelAndView(getSuccMap(zipPath));

	}

	/**
	 * 密钥下载
	 * 
	 * @param zipPath
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "downloadKey")
	public void downloadKey(String zipPath, HttpServletRequest request, HttpServletResponse response) {
		// 下载文件
		String fileName = Constants.BACKUP_KEY;

		download(zipPath, fileName, request, response);
	}

	/**
	 * 证书下载前的准备
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "toDownLoadKeyCert")
	public ModelAndView toDownLoadKeyCert(long id) throws Exception {
		KeyVO kVo = keyService.getKey(id);
		if (kVo == null)
			return getModelAndView(getErrMap("获取密钥对象失败"));

		// 是否为已导入证书
		String certPath = kVo.getCertPath();
		File f = new File(certPath);
		if (!f.exists())
			return getModelAndView(getErrMap("证书未导入, 无法操作"));

		// 检验信息
		if (!keyService.isModify(kVo.getId()))
			return getModelAndView(getErrMap("所选密钥数据被篡改,操作失败"));

		return getModelAndView(getSuccMap(certPath));

	}

	/**
	 * 下载证书
	 * 
	 * @param certPath
	 * @param request
	 * @param response
	 */
	@SealLog(optype = Constants.LOG_OPTYPE_DOWNSERVERCERT)
	@RequestMapping(value = "downLoadKeyCert")
	public void downLoadKeyCert(String certPath, HttpServletRequest request, HttpServletResponse response) {
		// 下载文件
		String fileName = Constants.CER;

		download(certPath, fileName, request, response);
	}

	/**
	 * 初始化服务器文件
	 * 
	 * @throws Exception
	 */
	public void initServerFile() throws Exception {
		List<KeyVO> keyVOList = keyService.getKey();
		for (KeyVO keyVO : keyVOList) {
			Key key = new Key();
			BeanUtils.copyProperties(keyVO, key);
			keyService.getKeyData(key);
		}
	}

	/**
	 * 获取CSR证书文件信息
	 * 
	 * @param csrPath
	 * @return
	 */
	public String getCsrFile(String csrPath) {
		StringBuffer buffer = new StringBuffer();
		Reader reader = null;
		BufferedReader buffReader = null;
		try {
			// 1、创建流对象
			reader = new FileReader(csrPath);
			// 构建高效流对象
			buffReader = new BufferedReader(reader);
			// 2、读取一行字符串
			String line = buffReader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = buffReader.readLine();
			}
		} catch (Exception e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			if (buffReader != null) {
				try {
					buffReader.close();
				} catch (IOException e) {
				}
			}
		}
		return buffer.toString();
	}
}
