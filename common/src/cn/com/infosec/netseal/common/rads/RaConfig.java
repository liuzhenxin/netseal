package cn.com.infosec.netseal.common.rads;

import java.io.FileInputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netcert.rads61.CertManager;
import cn.com.infosec.netcert.rads61.SysProperty;
import cn.com.infosec.netcert.rads61.exception.CAException;
import cn.com.infosec.netcert.rads61.exception.RAException;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.config.NetCertCaVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.rads.map.CertMap;

public class RaConfig {

	public static List<NetCertCaVO> ca_61_List;

	static {
		Security.addProvider(new InfosecProvider());
	}

	private RaConfig() {
	}

	public static void init() throws Exception {
		if (ca_61_List == null)
			return;

		// 加载配置文件
		CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");

		for (int i = 0; i < ca_61_List.size(); i++) {
			NetCertCaVO ncc = ca_61_List.get(i);
			try {
				resetCertManager(cf, ncc);
				LoggerUtil.systemlog("init " + ncc.getCertType() + " rads config ok.");
			} catch (Exception e) {
				System.out.println("init " + ncc.getCertType() + " rads config error, " + e.getMessage());
				LoggerUtil.errorlog("init " + ncc.getCertType() + " rads config error, ", e);
			}
		}
	}

	public static List<NetCertCaVO> getCa_61_List() {
		return ca_61_List;
	}

	public static void setCa_61_List(List<NetCertCaVO> ca_61_List) {
		RaConfig.ca_61_List = ca_61_List;
	}

	public static void resetCertManager(NetCertCaVO netCertCa) throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
		resetCertManager(cf, netCertCa);
	}

	public static void resetCertManager(CertificateFactory cf, NetCertCaVO netCertCa) throws Exception {
		String absolutePath = Constants.RADS_DATA_PATH;
		SysProperty sp = new SysProperty();
		sp.setTransIP(netCertCa.getTransIP());
		sp.setTransPort(netCertCa.getTransPort());
		sp.setHsmName(netCertCa.getHsmName());
		sp.setChanelEncryptName(netCertCa.getChanelEncryptName());
		if ("ssl".equalsIgnoreCase(netCertCa.getChanelEncryptName())) {
			sp.setSSL_TrustStore(absolutePath + netCertCa.getTrustStore());
			if (StringUtils.isNotBlank(netCertCa.getTrustPassword())) {
				sp.setSSL_TrustStorePass(netCertCa.getTrustPassword());
			}
		}
		if (StringUtils.isBlank(netCertCa.getHsmName())) {
			sp.setKeyIdx(absolutePath + netCertCa.getKeyIdx());
		} else {
			sp.setKeyIdx(netCertCa.getKeyIdx());
		}
		sp.setHsmName(netCertCa.getHsmName());
		sp.setPwd(netCertCa.getPwd());
		sp.setSignAlgName(netCertCa.getSignAlgName());
		if (StringUtils.isNotBlank(netCertCa.getSignCert())) {
			sp.setSignCert((X509Certificate) cf.generateCertificate(new FileInputStream(absolutePath + netCertCa.getSignCert())));
		}
		/*
		 * if(StringUtils.isNotBlank(netCertCa.getCaResponseCert())){ sp.setCaResponseCert((X509Certificate) cf.generateCertificate(new FileInputStream(absolutePath +netCertCa.getCaResponseCert())));
		 * }
		 */
		sp.setProtocolName(netCertCa.getProtocolName());
		sp.setCountry(netCertCa.getCountry());
		CertManager.resetCertManager(netCertCa.getCertType(), sp);
	}

	/**
	 * 测试 rads
	 * 
	 * @param netCertCa
	 * @return
	 * @throws Exception
	 */
	public static boolean testCertManager(NetCertCaVO netCertCa) throws Exception {
		boolean res = false;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509", "INFOSEC");
			resetCertManager(cf, netCertCa);

			CertMap certMap = new CertMap();
			certMap.put(CertMap.TEMPLATE, "ee_sign");
			certMap.put(CertMap.CERTDN, "cn=infosec");
			certMap.put(CertMap.CAID, netCertCa.getCertType());
			CertOperate certopr = new CertOperate(certMap);
			certopr.searchCert();
			res = true;
		} catch (RAException rae) {
			throw new WebDataException(rae.getErrorMsg() + "(" + rae.getErrorNum() + ")");
		} catch (CAException cae) {
			throw new WebDataException(cae.getErrorMsg() + "(" + cae.getErrorNum() + ")");
		}

		return res;
	}

	public static void removeCertManager(String name) throws Exception {
		CertManager.removeCertManager(name);
	}

}
