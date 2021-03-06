package cn.com.infosec.netseal.appapi.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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

public class CertUtil {

	private static Properties pro = new Properties();

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 证书属性转义
	 * 
	 * @param certDN
	 * @return
	 */
	private static String transCertDn(String certDN) {
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
	private static String transCertSn(String certSn) {
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
	public static String removeSpace(String certDN) {
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

	public static X509CertEnvelope parseCert(byte[] data) throws Exception {
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
			throw new Exception("parse cert error, " + e.getMessage());
		}
	}

	public static List<X509CertEnvelope> parseCertChain(byte[] data) throws Exception {
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
			throw new Exception("parse cert error, " + e.getMessage());
		}
	}

}
