package cn.com.infosec.netseal.webserver.service.user;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netcert.framework.resource.PropertiesKeysRes;
import cn.com.infosec.netcert.rads61.exception.CAException;
import cn.com.infosec.netcert.rads61.exception.RAException;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserCertRequestDaoImpl;
import cn.com.infosec.netseal.common.dao.user.UserDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.po.UserCertRequest;
import cn.com.infosec.netseal.common.entity.vo.UserCertRequestVO;
import cn.com.infosec.netseal.common.entity.vo.config.NetCertCaVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.rads.CertOperate;
import cn.com.infosec.netseal.common.rads.map.CertMap;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;

@Service
public class UserCertRequestServiceImpl extends BaseService {
	@Autowired
	protected UserDaoImpl userDao;
	@Autowired
	protected UserCertRequestDaoImpl userCertReuqestDao;
	@Autowired
  	private IDDeleteDaoImpl idDeleteDao;

	/**
	 * 申请证书
	 * 
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public UserCertRequestVO requestCert(UserCertRequestVO userCertRequestVO) throws Exception {
		UserCertRequest userCertRequest= new UserCertRequest();
		BeanUtils.copyProperties(userCertRequestVO, userCertRequest);
				
		String certType = userCertRequestVO.getCertType();
		ConfigUtil config = ConfigUtil.getInstance();
		NetCertCaVO netCertCa = null;
		if(Constants.NETCERT_CA_RSA.equals(certType)){//申请rsa证书
			netCertCa = config.getNetCertCaRSA();
		}else if(Constants.NETCERT_CA_SM2.equals(certType)){//申请sm2证书
			netCertCa = config.getNetCertCaSM2();
		}else{//无效的申请
			throw new WebDataException("无效的申请");
		}
		try{
			CertMap certMap = new CertMap();
			List<UserCertRequest> userCertRequestList = userCertReuqestDao.userCertRequestList(userCertRequest);
			if(userCertRequestList==null || userCertRequestList.size()==0){
				int isGenUuid = netCertCa.getIsGenUuid();
				if (isGenUuid == 1) {// ca生成uuid
					//向ca注册用户
					User user = userDao.getUser(userCertRequestVO.getUserId());
					
					certMap.put(CertMap.NAME, user.getName());
					certMap.put(CertMap.CAID, userCertRequestVO.getCertType());
					CertOperate certopr = new CertOperate(certMap);
					String userUuid = certopr.applyUser();
					userCertRequest.setUserUuid(userUuid);
				} else {
					userCertRequest.setUserUuid(String.valueOf(userCertRequestVO.getUserId()));
				}
				//向ca申请证书
				certMap.clear();
				certMap.put(CertMap.CAID, userCertRequestVO.getCertType());
				certMap.put(CertMap.CERTDN, userCertRequestVO.getCertDn());
				String temp = userCertRequestVO.getCertTemplate();
				temp = temp.substring(0, temp.length() - 3);
				certMap.put(CertMap.TEMPLATE, temp);
				certMap.put(CertMap.VALIDITYLEN, String.valueOf(userCertRequestVO.getValidityLen()));
				certMap.put(CertMap.UUID, userCertRequest.getUserUuid());
				
				CertOperate certopr = new CertOperate(certMap);
				certMap = certopr.requestCert();
				String refno = certMap.get(CertMap.REFNO);
				String authCode = certMap.get(CertMap.AUTHCODE);
				userCertRequest.setCertRefno(refno);
				userCertRequest.setCertAuthCode(authCode);
	
				userCertRequest.setStatus(0);
				userCertRequest.setUpdateTime(DateUtil.getCurrentTime());
				userCertRequest.setGenerateTime(DateUtil.getCurrentTime());
				userCertReuqestDao.insertUserCertRequest(userCertRequest);
			}else{
				userCertRequest = userCertRequestList.get(0);
			}
			
			BeanUtils.copyProperties(userCertRequest, userCertRequestVO);
		}catch(RAException rae){
			LoggerUtil.errorlog("downloadCert RAException:", rae);
			throw new WebDataException(rae.getErrorMsg() + "("+rae.getErrorNum()+")");
		}catch(CAException cae){
			LoggerUtil.errorlog("downloadCert CAException:", cae);
			throw new WebDataException(cae.getErrorMsg() + "("+cae.getErrorNum()+")");
		}
		return userCertRequestVO;
	}

	/**
	 * 证书下载
	 * 
	 * @throws Exception
	 */
	public UserCertRequestVO downloadCert(UserCertRequestVO userCertRequestVO) throws Exception {
			
		UserCertRequest ucr = userCertReuqestDao.getUserCertRequest(userCertRequestVO.getId());
		try{
			CertMap certMap = new CertMap();
			certMap.put(CertMap.CAID, ucr.getCertType());
			certMap.put(CertMap.REFNO, ucr.getCertRefno());
			certMap.put(CertMap.AUTHCODE, ucr.getCertAuthCode());
			certMap.put(CertMap.PUBLICKEY, userCertRequestVO.getP10());
			certMap.put(CertMap.RSA_TMP_PUB_KEY, userCertRequestVO.getTmpPubKey());
			certMap.put(PropertiesKeysRes.KMC_KEYLEN, userCertRequestVO.getKeyLen());
			
			CertOperate certopr = new CertOperate(certMap);
			certMap = certopr.downloadCert(ucr.getCertType());
			
			String signCert = certMap.get(PropertiesKeysRes.P7DATA);  //签名证书返回内容
		    String encCert = certMap.get(PropertiesKeysRes.P7DATA_ENC);  //加密证书返回内容
		    String encPri = certMap.get(PropertiesKeysRes.ENCPRIVATEKEY);    //加密证书私钥
		    String ukek = certMap.get(PropertiesKeysRes.TEMPUKEK);      //ukek
			
		   /* System.out.println("[1]: "+signCert);
		  	 System. out.println("[2]: "+encCert);
		  	 System. out.println("[3]: "+encPri);
		  	 System. out.println("[4]: "+ukek);*/
		  	 
		  	userCertRequestVO.setSignCert(signCert);
		  	userCertRequestVO.setEncCert(encCert);
		  	userCertRequestVO.setEncPri(encPri);
		  	userCertRequestVO.setUkek(ukek);
		  	//成功返回证书后,删除用户证书请求记录
		  	userCertReuqestDao.deleteUserCertRequest(userCertRequestVO.getId());
		  	// 增加删除记录
		  	idDeleteDao.insertIDDelete(userCertRequestVO.getId(), Constants.TABLE_SEAL_USER_CERT_REQUEST);
		}catch(RAException rae){
			LoggerUtil.errorlog("downloadCert RAException:", rae);
			throw new WebDataException(rae.getErrorMsg() + "("+rae.getErrorNum()+")");
		}catch(CAException cae){
			LoggerUtil.errorlog("downloadCert CAException:", cae);
			throw new WebDataException(cae.getErrorMsg() + "("+cae.getErrorNum()+")");
		}
	  	 return userCertRequestVO;
	}
	
	public UserCertRequestVO getUserCertRequest(Long id) {
		UserCertRequest userCertRequest = userCertReuqestDao.getUserCertRequest(id);
		if (userCertRequest == null)
			return null;
		UserCertRequestVO userCertRequestVO= new UserCertRequestVO();
		BeanUtils.copyProperties(userCertRequest, userCertRequestVO);
		return userCertRequestVO;
	}
}
