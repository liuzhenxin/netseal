package cn.com.infosec.netseal.common.rads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import cn.com.infosec.netcert.framework.resource.PropertiesKeysRes;
import cn.com.infosec.netcert.rads61.CertManager;
import cn.com.infosec.netcert.rads61.exception.CAException;
import cn.com.infosec.netcert.rads61.exception.RAException;
import cn.com.infosec.netcert.rads61.resource.CustomExtValue;
import cn.com.infosec.netcert.rads61.resource.QueryResult;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.rads.map.CertMap;
import cn.com.infosec.util.Base64;

/**
 * RADS接口操作
 *
 */
public class CertOperate {	
	
	private CertManager manager;
	private CertMap map;

	public CertOperate(CertMap map) throws RAException {
		manager = null;
		this.map = null;
		manager = CertManager.getInstance(map.get(CertMap.CAID));
		this.map = map;
	}

	/**
	 * 注册用户
	 * @return
	 * @throws CAException 
	 * @throws RAException 
	 */
	public String applyUser() throws RAException, CAException{
		Properties properties = new Properties();
		if(StringUtils.isNotBlank(map.get(CertMap.TELEPHONE))) {
			
			properties.setProperty(PropertiesKeysRes.TELEPHONE, map.get(CertMap.TELEPHONE));
		}
		
		if(StringUtils.isNotBlank(map.get(CertMap.MAIL))) {
			
			properties.setProperty(PropertiesKeysRes.EMAIL, map.get(CertMap.MAIL));
		}
		
		if(StringUtils.isNotBlank(map.get(CertMap.NAME))) {
			
			properties.setProperty(PropertiesKeysRes.USERNAME, map.get(CertMap.NAME));
		}
		
		Properties resluts = manager.applyUser(properties);
		
		return resluts.getProperty(PropertiesKeysRes.UUID);
	}
		
	
	/**
	 * 证书申请
	 * @return
	 * @throws RAException
	 * @throws CAException
	 */
	public CertMap requestCert() throws RAException, CAException{
		
		Properties properties = new Properties();
		properties.setProperty(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
		properties.setProperty(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		properties.setProperty(PropertiesKeysRes.VALIDITYLEN, map.get(CertMap.VALIDITYLEN));
		properties.setProperty(PropertiesKeysRes.UUID, map.get(CertMap.UUID));
		
		Properties twoCodeAndSerial = null;

		List<CustomExtValue> listAll = new ArrayList<CustomExtValue>();
		if(StringUtils.isNotBlank(map.get(CertMap.USERNO))){
			
			CustomExtValue ext = new CustomExtValue();
			ext.setValue(map.get(CertMap.USERNO));
			listAll.add(ext);
		}
		
		if( StringUtils.isNotBlank(map.get(CertMap.POST))){
			
			CustomExtValue ext = new CustomExtValue();
			//ext.setOid(Constants.Extend.POST);	
			ext.setValue(map.get(CertMap.POST));
			listAll.add(ext);
		}
		
		if(listAll != null && listAll.size()>0){
    		//根据扩展项的名字获得扩展值	
			Map<String, List<CustomExtValue>> hashMap = new HashMap<String, List<CustomExtValue>>();
			for (CustomExtValue customExtValue: listAll) {
				String oid = customExtValue.getOid();
				if (!hashMap.containsKey(oid)) {
					List<CustomExtValue> listExtValue = new ArrayList<CustomExtValue>();
					listExtValue.add(customExtValue);
					hashMap.put(oid, listExtValue);
				} else {
					List<CustomExtValue> list = hashMap.get(oid);
					hashMap.remove(oid);
					list.add(customExtValue);
					hashMap.put(oid, list);
				}
			}
			twoCodeAndSerial = manager.requestCert(properties, hashMap);
		}else
			twoCodeAndSerial = manager.requestCert(properties);
		
		String refer = twoCodeAndSerial.getProperty(PropertiesKeysRes.REFNO);
		String auth = twoCodeAndSerial.getProperty(PropertiesKeysRes.AUTHCODE);
		map.put(CertMap.REFNO, refer);
		map.put(CertMap.AUTHCODE, auth);
		map.put(CertMap.CERTSN, refer);
		return map;
	}

	public CertMap downloadCert(String certType) throws RAException, CAException {
		
		Properties properties = new Properties();
		properties.setProperty(PropertiesKeysRes.REFNO, map.get(CertMap.REFNO));
		properties.setProperty(PropertiesKeysRes.AUTHCODE, map.get(CertMap.AUTHCODE));
		properties.setProperty(PropertiesKeysRes.PUBLICKEY, map.get(CertMap.PUBLICKEY));
		
		if(Constants.NETCERT_CA_RSA.equalsIgnoreCase(certType)){
			properties.setProperty(PropertiesKeysRes.RETURNTYPE, "P7CERT");
			if(StringUtils.isNotBlank(map.get(CertMap.RSA_TMP_PUB_KEY))){
				properties.setProperty(PropertiesKeysRes.RSA_TMP_PUB_KEY, map.get(CertMap.RSA_TMP_PUB_KEY));
			}
			if(StringUtils.isNotBlank(map.get("KMC_KEYLEN"))){
				properties.setProperty(PropertiesKeysRes.KMC_KEYLEN, map.get("KMC_KEYLEN"));
			}
			properties.setProperty(PropertiesKeysRes.RETSYMALG, "RC4");
			
		} else if(Constants.NETCERT_CA_SM2.equalsIgnoreCase(certType)){
			properties.setProperty(PropertiesKeysRes.RETURNTYPE, "CERT");
			properties.setProperty(PropertiesKeysRes.RETSYMALG, "SM4");
			properties.setProperty(PropertiesKeysRes.KMC_KEYLEN, map.get("KMC_KEYLEN"));
		}
		
		Properties results = manager.downCert(properties);
		String p7data = results.getProperty("P7DATA");
		if(StringUtils.isNotBlank(results.getProperty(PropertiesKeysRes.P7DATA_ENC))){
		
			String P7DATA_ENC=	results.getProperty(PropertiesKeysRes.P7DATA_ENC);
			String ENCPRIVATEKEY=	results.getProperty(PropertiesKeysRes.ENCPRIVATEKEY);
			String TEMPUKEK=	results.getProperty(PropertiesKeysRes.TEMPUKEK);
			map.put("P7DATAENC", P7DATA_ENC);
			map.put("ENCPRIVATEKEY", ENCPRIVATEKEY);
			map.put("TEMPUKEK", TEMPUKEK);
			
			//X509Certificate encCert = generateCert(P7DATA_ENC);
			//map.put("ENCNOTBEFORE", encCert.getNotBefore().getTime()+"");
			//map.put("ENCNOTAFTER", encCert.getNotAfter().getTime()+"");
			//map.put("ENCCERTSN", encCert.getSerialNumber().toString(16).toUpperCase());
		}	
		
		map.put(CertMap.P7DATA, p7data);
		
		//X509Certificate cert = generateCert(p7data);
		//map.put(CertMap.NOTBEFORE, cert.getNotBefore().getTime()+"");
		//map.put(CertMap.NOTAFTER, cert.getNotAfter().getTime()+"");
		return map;
			
	}
	
	public void reloadCertMap(CertMap map){
		this.map = map;
	}
	
	private X509Certificate generateCert(String p7){
		
		X509Certificate p7cert = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
			
		    p7cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decode(p7)));
		} catch (CertificateException e) {
			throw new RuntimeException("generating p7 occurs exception.", e);
		} catch (IOException e) {
			throw new RuntimeException("generating p7 occurs exception.", e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("cann't find INFOSEC provider.", e);
		}
		
		return p7cert;

	}
	
	/**
	 * 修改用户
	 * @return
	 * @throws RAException
	 * @throws CAException
	 */
	public boolean modifyUser() throws RAException, CAException{
		
		Properties properties = new Properties();
		
		properties.setProperty(PropertiesKeysRes.UUID, map.get(CertMap.UUID));
		
		if(StringUtils.isNotBlank(map.get(CertMap.TELEPHONE))){
			
			properties.setProperty(PropertiesKeysRes.TELEPHONE, map.get(CertMap.TELEPHONE));
		}
		
		if(StringUtils.isNotBlank(map.get(CertMap.MAIL))){
			
			properties.setProperty(PropertiesKeysRes.EMAIL, map.get(CertMap.MAIL));
		}
		
		if(StringUtils.isNotBlank(map.get(CertMap.NAME))) {
			
			properties.setProperty(PropertiesKeysRes.USERNAME, map.get(CertMap.NAME));
		}
		
		return manager.modifyUser(properties);

	}
	
	public CertMap updateCert() throws RAException, CAException{
		
		Properties properties = new Properties();
		if(StringUtils.isNotBlank(map.get(CertMap.CERTSN))){
			
			properties.put(PropertiesKeysRes.CERTSN, map.get(CertMap.CERTSN));
		}else {
			
			properties.put(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
			properties.put(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		}
        if (map.get(CertMap.VALIDITYLEN) == null) {
			properties.put(PropertiesKeysRes.TP, "2");
			properties.put(PropertiesKeysRes.NOTBEFORE, map.get(CertMap.NOTBEFORE));
			properties.put(PropertiesKeysRes.NOTAFTER, map.get(CertMap.NOTAFTER));

		}else{
			properties.put(PropertiesKeysRes.VALIDITYLEN, map.get(CertMap.VALIDITYLEN));
		}
		Properties results = manager.updateCert(properties);
		
		String refer = results.getProperty(PropertiesKeysRes.REFNO);
		String auth = results.getProperty(PropertiesKeysRes.AUTHCODE);
		map.put(CertMap.REFNO, refer);
		map.put(CertMap.AUTHCODE, auth);
		map.put(CertMap.CERTSN, refer);
		//map.put(CertMap.STATUS, Constants.CertStatusRes.NOTDOWNLOAD);
		
		return map;
	}

	public CertMap reget2Code()throws Exception{
		
		Properties properties = new Properties();
		
		if(StringUtils.isNotBlank(map.get(CertMap.CERTSN))){
			
			properties.put(PropertiesKeysRes.CERTSN, map.get(CertMap.CERTSN));
		}else {
			
			properties.put(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
			properties.put(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		}
	       
		Properties results = manager.get2Code(properties);
		String refer = results.getProperty(PropertiesKeysRes.REFNO);
		String auth = results.getProperty(PropertiesKeysRes.AUTHCODE);
		map.put(CertMap.REFNO, refer);
		map.put(CertMap.AUTHCODE, auth);
		map.put(CertMap.CERTSN, refer);
		//map.put(CertMap.STATUS, Constants.CertStatusRes.NOTDOWNLOAD);
	    return map;
	}
	
	/**
	 * 证书查询
	 * @return
	 * @throws RAException
	 * @throws CAException
	 */
	public Properties [] searchCert() throws RAException, CAException{
		
		Properties properties = new Properties();
			
		properties.put(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
		properties.put(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		
		QueryResult queryResult = manager.searchCert(properties);
		
		return  queryResult.getPs();
		
	}

	public boolean revokeCert()throws Exception{
		
		Properties properties = new Properties();
		if(StringUtils.isNotBlank(map.get(CertMap.CERTSN))){
			
			properties.put(PropertiesKeysRes.CERTSN, map.get(CertMap.CERTSN));
		}else {
			
			properties.put(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
			properties.put(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		}
		properties.put(PropertiesKeysRes.REVOKEREASON, String.valueOf(0));
		return manager.revokeCert(properties);
	}	
	
	
    public boolean lockCert()throws Exception{
		
		Properties properties = new Properties();
		if(!StringUtils.isBlank(map.get(CertMap.CERTSN)))
			properties.put(PropertiesKeysRes.CERTSN, map.get(CertMap.CERTSN));
		
		properties.put(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
		properties.put(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		return manager.lockCert(properties);
	}	
    
    public boolean unlockCert()throws Exception{
		
		Properties properties = new Properties();
		if(!StringUtils.isBlank(map.get(CertMap.CERTSN)))
			properties.put(PropertiesKeysRes.CERTSN, map.get(CertMap.CERTSN));
		
		properties.put(PropertiesKeysRes.TEMPLATENAME, map.get(CertMap.TEMPLATE));
		properties.put(PropertiesKeysRes.SUBJECTDN, map.get(CertMap.CERTDN));
		return manager.unlockCert(properties);
	}
}
