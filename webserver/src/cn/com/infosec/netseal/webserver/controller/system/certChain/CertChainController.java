package cn.com.infosec.netseal.webserver.controller.system.certChain;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.CertChain;
import cn.com.infosec.netseal.common.entity.vo.CertChainVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.webserver.aop.SealLog;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.util.Page;

@RequestMapping(value = "/system/certChain")
@Controller
public class CertChainController extends BaseController{

	@Autowired
	private CertChainServiceImpl certChainService;
	
	@Autowired
	private  HttpServletRequest request; 
	
	/**
	 * 证书链配置
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "certChainConfig")
	public String certChainConfig(Page<CertChainVO> page, CertChainVO certChainVO) throws Exception {
		page = certChainService.searchCertChain(page, certChainVO);
		request.setAttribute("page", page);
		return "system/certChainConfig";
	}

	/**
	 * 证书链查询的局部
	 * 
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/certChainConfigSearch")
	public Map<String, Object> certChainConfigSearch(Page<CertChainVO> page, CertChainVO certChainVO) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		page = certChainService.searchCertChain(page, certChainVO);
		modelMap.put("page", page);
		return modelMap;
	}

	/**
	 * 导入证书(链)
	 * 
	 * @param request
	 * @param certChain
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "addCertChain")
	@SealLog(optype = Constants.LOG_OPTYPE_ADDCERTCHAIN)
	public Map<String, Object> addCertChain(HttpServletRequest request, CertChainVO certChainVO, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String path = Constants.CERT_PATH;
		String filePath = "";
		try {
			// 导入证书
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("certChain");
				if (file != null) {
					String fileOriName = file.getOriginalFilename();

					String fileOriginalName = path + DateUtil.getDateDir() + FileUtil.getFileName() + "." + FileUtil.getFileSuffix(fileOriName);
					File targetFile = new File(fileOriginalName);
					if (!targetFile.exists())
						targetFile.mkdirs();

					file.transferTo(targetFile);
					// 上传文件夹路径
					filePath = targetFile.getPath();
					// 获取文件后缀名
					String name = FileUtil.getFileSuffix(filePath);
					if (name.equals("cer")) {
						// 解析证书
						addCertChain(certChainVO, resultMap, path, filePath, response);
					} else if (name.equals("p7b")) {
						// 解析证书链
						addCertChains(certChainVO, resultMap, path, filePath, response);
					} else
						throw new WebDataException("文件类型错误");
				}
			}

		} catch (WebDataException ex) {
			response.getWriter().write(ex.getMessage());
			FileUtil.deleteFile(filePath);
		} catch (Exception e) {
			response.getWriter().write(e.getMessage());
		} finally {
			if (!"".equals(filePath))
				FileUtil.deleteFile(filePath);
		}
		return null;
	}

	/**
	 * 导入证书(.cer)
	 * 
	 * @param certChainVO
	 * @param resultMap
	 * @param path
	 * @param filePath
	 * @param response
	 * @throws Exception
	 */
	private void addCertChain(CertChainVO certChainVO, Map<String, Object> resultMap, String path, String filePath, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		X509CertEnvelope certVO = CertUtil.parseCert(FileUtil.getFile(filePath));
		String fileName = FileUtil.getFileName();
		FileUtil.storeFile(path + DateUtil.getDateDir() + fileName + ".cer", certVO.getEncoded());
		String certPaths = path + DateUtil.getDateDir() + fileName + ".cer";
		// 判断证书是否在有效期
		Date notBefore = certVO.getNotBefore();
		Date notAfter = certVO.getNotAfter();
		Date current = new Date();
		if (notBefore.after(current) || notAfter.before(current)) {
			FileUtil.deleteFile(certPaths);
			throw new WebDataException("证书不在有效期内");
		}

		// 判断证书是否存在
		String certDNs = certVO.getCertDn();
		String certIssueDNs = certVO.getCertIssueDn();

		List<CertChain> getCertChainList = certChainService.getCertChain(certDNs);
		if (getCertChainList.size() == 0) {
			// 导入证书
			certChainVO.setCertDn(certDNs);
			certChainVO.setCertIssueDn(certIssueDNs);
			certChainVO.setCertPath(certPaths);
			certChainVO.setNotBefor(notBefore.getTime());
			certChainVO.setNotAfter(notAfter.getTime());
			certChainVO.setGenerateTime(DateUtil.getCurrentTime());
			certChainVO.setUpdateTime(DateUtil.getCurrentTime());

			if (certDNs.equals(certIssueDNs)) {// 证书自签
				certChainVO.setPid(-1L);// (根)证书父级ID 默认值-1
			} else {
				// 获取该证书的颁发者证书
				List<CertChain> getIssueCertList = certChainService.getCertChain(certIssueDNs);
				if (getIssueCertList.size() > 0) {// 该证书的颁发者证书存在
					CertChain getIssueCert = getIssueCertList.get(0);
					certChainVO.setPid(getIssueCert.getId());
				} else {
					certChainVO.setPid(-1L); // 颁发者证书不存在 先默认 -1
				}
			}
			// 保存DB
			certChainService.addCertChainDB(certChainVO, certDNs);
			response.getWriter().write("ok");
		} else {
			FileUtil.deleteFile(certPaths);
			throw new WebDataException("已存在该证书");
		}
	}

	/**
	 * 导入证书链(.p7b)
	 * 
	 * @param certChainVO
	 * @param resultMap
	 * @param path
	 * @param filePath
	 * @param response
	 * @throws Exception
	 */
	private void addCertChains(CertChainVO certChainVO, Map<String, Object> resultMap, String path, String filePath, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		List<X509CertEnvelope> certList = null;
		certList = CertUtil.parseCertChain(FileUtil.getFile(filePath));

		for (int i = 0; i < certList.size(); i++) {
			String fileName = FileUtil.getFileName();
			// 将证书链分解成证书
			FileUtil.storeFile(path + DateUtil.getDateDir() + fileName + ".cer", certList.get(i).getEncoded());
			String certPaths = path + DateUtil.getDateDir() + fileName + ".cer";

			// 判断证书是否在有效期
			X509CertEnvelope certVO = CertUtil.parseCert(FileUtil.getFile(certPaths));
			Date notBefore = certVO.getNotBefore();
			Date notAfter = certVO.getNotAfter();
			Date current = new Date();
			if (notBefore.after(current) || notAfter.before(current)) {
				FileUtil.deleteFile(certPaths);
				throw new WebDataException("证书不在有效期内");
			}
			String certDN = certList.get(i).getCertDn();
			String certIssueDN = certList.get(i).getCertIssueDn();

			// 判断证书是否存在
			List<CertChain> getCertChainList = certChainService.getCertChain(certDN);
			if (getCertChainList.size() == 0) {
				certChainVO.setCertDn(certDN);
				certChainVO.setCertIssueDn(certIssueDN);
				certChainVO.setCertPath(certPaths);
				certChainVO.setNotBefor(notBefore.getTime());
				certChainVO.setNotAfter(notAfter.getTime());
				certChainVO.setGenerateTime(System.currentTimeMillis());
				certChainVO.setUpdateTime(DateUtil.getCurrentTime());
				// 判断证书是否自签
				if (certDN.equals(certIssueDN)) { // 证书自签
					certChainVO.setPid(-1L);// (根)证书父级ID 默认值-1
				} else {
					// 获取颁发者证书
					List<CertChain> getIssueCertList = certChainService.getCertChain(certIssueDN);
					CertChain getIssueCert = getCertChainList.get(0);
					if (getIssueCertList.size() > 0) {
						certChainVO.setPid(getIssueCert.getId());
					} else {
						certChainVO.setPid(-1L); // 颁发者证书不存在 先默认 -1
					}
				}
				// 保存DB
				certChainService.addCertChainsDB(certChainVO, certDN);

				response.getWriter().write("ok");
			} else {
				FileUtil.deleteFile(certPaths);
				throw new WebDataException("已存在该证书");
			}
		}
	}

	/**
	 * 删除授信证书
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "delCert")
	@SealLog(optype = Constants.LOG_OPTYPE_DELCERTCHAIN)
	public ModelAndView delCert(String id) throws Exception {
		try {
			certChainService.deleteCertChain(id);
			return this.getModelAndView(getSuccMap("删除成功"));
		} catch (WebDataException ex) {
			return this.getModelAndView(getErrMap(ex));
		} catch (Exception e) {
			return this.getModelAndView(getErrMap("删除失败"));
		}
	}
	
	
	
}
