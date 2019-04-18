package cn.com.infosec.netseal.common.util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

import cn.com.infosec.asn1.x509.X509Name;
import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.p10.CryptoUtil;
import cn.com.infosec.netseal.common.util.p10.JKSFile;
import cn.com.infosec.netseal.common.util.p10.PKCS10CertificationRequest;
import cn.com.infosec.x509.X509V1CertificateGenerator;

public class P10Util {

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 生成P10请求
	 * 
	 * @param subject
	 * @param keyLable
	 * @param keyStrore
	 * @return 返回P10请求串
	 * @throws Exception
	 */
	public static String genP10(JKSFile jks, String csrPath, String jksPath) throws Exception {
		KeyPair kp = generateKeyPair(Constants.RSA_KEY_SIZE);
		if (kp == null)
			throw new Exception("Generate keypair failed");

		String certDN = Constants.P10_DN + String.valueOf(System.currentTimeMillis());
		X509Certificate cert = generateCert(certDN, kp.getPublic(), kp.getPrivate(), "SHA1withRSA");
		X509Certificate[] certs = new X509Certificate[] { cert };
		jks.prepareP10(kp.getPrivate(), Constants.P10_LABEL, certs, jksPath);

		PKCS10CertificationRequest p10reqest = new PKCS10CertificationRequest(new X509Name(certDN), kp.getPublic(), null, kp.getPrivate(), "INFOSEC");
		String p10 = CryptoUtil.createbase64csr(p10reqest);

		FileUtil.storeFile(csrPath, p10.getBytes());
		return p10;
	}

	private static X509Certificate generateCert(String subject, PublicKey publicKey, PrivateKey privateKey, String signatureType) throws Exception {
		X509Name dn = new X509Name(subject);
		int iValidity = 365;
		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
		certGen.setIssuerDN(dn);
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + ((long) iValidity * 24 * 60 * 60 * 1000)));
		certGen.setSubjectDN(dn);
		certGen.setPublicKey(publicKey);
		certGen.setSignatureAlgorithm(signatureType.toString());
		certGen.setSerialNumber(BigInteger.ONE);
		X509Certificate cert = certGen.generateX509Certificate(privateKey);
		return cert;
	}

	private static KeyPair generateKeyPair(int keySize) throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "INFOSEC");
		SecureRandom ran = SecureRandom.getInstance("SHA1PRNG");
		kpg.initialize(keySize, ran);
		return kpg.generateKeyPair();
	}

	public static String[] importP10(X509Certificate cert, JKSFile jks, String jksPath) throws Exception {
		String[] results = new String[2];

		X509Certificate[] certs = new X509Certificate[] { cert };
		results = jks.importP10(certs, jksPath);

		return results;
	}

	public static void main(String[] args) throws Exception {
		// JKSFile jks = new JKSFile();
		// String p10 = genP10(jks);
		// System.out.println(p10);
		/*
		 * FileInputStream fins = new FileInputStream("f:/temp/1.cer"); CertificateFactory cf = CertificateFactory.getInstance("X509", "INFOSEC"); X509Certificate cert = (X509Certificate)
		 * cf.generateCertificate(fins); fins.close(); System.out.println(cert);
		 */
		// importP10(cert, jks);

	}
}
