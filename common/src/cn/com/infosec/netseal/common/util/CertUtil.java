package cn.com.infosec.netseal.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import cn.com.infosec.asn1.ASN1Set;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERObject;
import cn.com.infosec.asn1.pkcs.ContentInfo;
import cn.com.infosec.asn1.pkcs.PKCSObjectIdentifiers;
import cn.com.infosec.asn1.pkcs.SignedData;
import cn.com.infosec.asn1.x509.X509CertificateStructure;
import cn.com.infosec.crypto.CryptoException;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.jce.provider.X509CertificateObject;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class CertUtil {

	private static Properties pro = new Properties();

	static {
		Security.addProvider(new InfosecProvider());

		// pro初始化
		String transCertDn = ConfigUtil.getInstance().getTransCertDn();
		String[] transCertDnArray = transCertDn.split(";");
		for (String tempTrans : transCertDnArray) {
			String[] tempTransX = tempTrans.split(",");
			if (tempTransX.length == 2) {
				pro.setProperty(tempTransX[0], tempTransX[1]);
			}
		}
	}

	/**
	 * 证书属性转义
	 * 
	 * @param certDN
	 * @return
	 */
	public static String transCertDn(String certDN) {
		certDN = removeSpace(certDN);

		Enumeration en = pro.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			certDN = certDN.replaceAll(key, pro.getProperty(key));
		}

		return certDN;
	}

	/**
	 * 证书属性转义 - 最高位为1时, 前补0x00
	 * 
	 * @param certSn
	 *            16进制
	 * @return
	 */
	public static String transCertSn(String certSn) {
		if (StringUtil.isNotBlank(certSn)) {
			char c = certSn.charAt(0);
			if (c >= '8')
				return "00" + certSn;
			else
				return certSn;
		}
		return certSn;
	}

	/**
	 * 去除证书DN中 逗号和等号两边的空格
	 * 
	 * @param certDN
	 * @return certDN
	 */
	private static String removeSpace(String certDN) {
		if (certDN == null)
			return "";

		StringBuffer sb = new StringBuffer();
		String[] items = certDN.split(",");
		for (int i = 0; i < items.length; i++) {
			String[] items2 = items[i].split("=");
			if (items2.length == 2) {
				sb.append(items2[0].trim()).append("=").append(items2[1].trim());
			} else {
				sb.append(items[i].trim());
			}
			if (i < items.length - 1)
				sb.append(",");
		}

		return sb.toString();
	}

	public static X509CertEnvelope parseCert(byte[] data) {
		try {
			X509Certificate x509Cert = NetSignUtil.getCert(data);
			X509CertEnvelope cert = new X509CertEnvelope();
			cert.setX509Cert(x509Cert);
			cert.setSn(x509Cert.getSerialNumber());
			cert.setCertSn(transCertSn(x509Cert.getSerialNumber().toString(16)).toUpperCase());
			cert.setCertDn(transCertDn(x509Cert.getSubjectDN().getName()));
			cert.setCertIssueDn(transCertDn(x509Cert.getIssuerDN().getName()));
			cert.setNotBefore(x509Cert.getNotBefore());
			cert.setNotAfter(x509Cert.getNotAfter());
			cert.setKeyUsage(x509Cert.getKeyUsage());
			cert.setSigAlgOID(x509Cert.getSigAlgOID());
			cert.setEncoded(x509Cert.getEncoded());
			cert.setPublicKey(x509Cert.getPublicKey());
			return cert;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CERT_DATA_INVAILD, "parse cert error, " + e.getMessage());
		}
	}

	public static List<X509CertEnvelope> parseCertChain(byte[] data) {
		try {
			DERInputStream din = new DERInputStream(new ByteArrayInputStream(data));
			List<X509CertEnvelope> certList = new ArrayList<X509CertEnvelope>();
			DERObject pkcs7;
			try {
				pkcs7 = din.readObject();
			} catch (IOException e) {
				throw new CryptoException("can't decode PKCS7SignedData object");
			}

			if (!(pkcs7 instanceof DERConstructedSequence)) {
				throw new CryptoException("Not a valid PKCS#7 object - not a sequence");
			}
			ContentInfo content = ContentInfo.getInstance(pkcs7);

			if (!content.getContentType().equals(PKCSObjectIdentifiers.signedData)) {
				throw new CryptoException("Not a valid PKCS#7 signed-data object - wrong header " + content.getContentType().getId());
			}
			SignedData signedData = SignedData.getInstance(content.getContent());

			if (signedData.getCertificates() != null) {
				Enumeration ec = ASN1Set.getInstance(signedData.getCertificates()).getObjects();
				while (ec.hasMoreElements()) {
					X509Certificate x509Cert = new X509CertificateObject(X509CertificateStructure.getInstance(ec.nextElement()));
					X509CertEnvelope certEnv = CertUtil.parseCert(x509Cert.getEncoded());
					certList.add(certEnv);
				}
			}

			return certList;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CERT_DATA_INVAILD, "parse cert error, " + e.getMessage());
		}
	}

	public static void verifyCert(byte[] certData, byte[] rootCertData) {
		verifyCert(certData, rootCertData, null);
	}

	public static void verifyCert(byte[] certData, byte[] rootCertData, Date stampDate) {
		verifyCert(certData, rootCertData, stampDate, true, false);
	}

	public static void verifyCert(byte[] certData, byte[] rootCertData, Date stampDate, boolean checkSignCert, boolean checkEncCert) {
		verifyCert(certData, rootCertData, stampDate, checkSignCert, checkEncCert, true);
	}

	public static void verifyCert(byte[] certData, byte[] rootCertData, Date stampDate, boolean checkSignCert, boolean checkEncCert, boolean checkCertDate) {
		try {
			// 创建证书对象
			X509CertEnvelope cert = parseCert(certData);

			BigInteger sn = cert.getSn();
			Date notBefore = cert.getNotBefore();
			Date notAfter = cert.getNotAfter();
			String subjectDN = cert.getCertDn();

			// 判断证书有效期
			Date current = new Date();
			if (checkCertDate) {
				if (notBefore.after(current) || notAfter.before(current))
					throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "cert date not in the period of validity, dn is " + subjectDN);
			}

			// 查看CRL
			if (stampDate == null) {
				if (CrlUtil.contain(sn))
					throw new NetSealRuntimeException(ErrCode.CERT_HAS_REVOKED, "cert has been revoked, dn is " + subjectDN);
			} else {
				if (CrlUtil.containInDate(sn, stampDate.getTime()))
					throw new NetSealRuntimeException(ErrCode.CERT_HAS_REVOKED, "cert has been revoked, dn is " + subjectDN);
			}

			// 查看密钥用法
			boolean[] usage = cert.getKeyUsage();
			if (checkSignCert)
				if (usage == null || !usage[0])
					throw new NetSealRuntimeException(ErrCode.CERT_USAGE_SIGN_FALSE, "cert usage [digitalSignature] is false or null");

			if (checkEncCert)
				if (usage == null || (!usage[2] && !usage[3]))
					throw new NetSealRuntimeException(ErrCode.CERT_USAGE_ENC_FALSE, "cert usage [keyEncipherment, dataEncipherment] is false or null");

			X509CertEnvelope rootCert = parseCert(rootCertData);
			sn = rootCert.getSn();
			notBefore = rootCert.getNotBefore();
			notAfter = rootCert.getNotAfter();
			String rootCertDN = rootCert.getCertDn();

			// 判断根证书日期
			if (notBefore.after(current) || notAfter.before(current))
				throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "not in the period of validity, root dn is " + rootCertDN);

			// 查看CRL
			if (CrlUtil.contain(sn))
				throw new NetSealRuntimeException(ErrCode.CERT_HAS_REVOKED, "root cert has been revoked, root dn is " + rootCertDN);

			String signAlg = OidUtil.getSignAlg(cert.getSigAlgOID());
			// 根证验签
			try {
				CryptoHandler.verifyCert(cert.getX509Cert(), rootCert.getPublicKey(), Constants.PFX_SUFFIX, 0, signAlg);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.ROOT_CERT_VERIFY_SIGN_FAILED, "verify cert sign failed, dn is " + subjectDN);
			}
		} catch (NetSealRuntimeException ex) {
			throw ex;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.ROOT_CERT_VERIFY_SIGN_ERROR, e.getMessage());
		}
	}

}
