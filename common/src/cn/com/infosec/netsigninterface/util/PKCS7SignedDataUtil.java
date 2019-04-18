package cn.com.infosec.netsigninterface.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.util.Base64;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.netsign.der.util.PKCS7SignedData;
import cn.com.infosec.netsign.der.util.PKCS7SignedDataGenerater;
import cn.com.infosec.netsigninterface.SignatureUtil;

public class PKCS7SignedDataUtil {

	public static byte[] generatePKCS7SignedData(byte[] plainText, PrivateKey prik, String digestAlg, byte[] id, X509Certificate signCert, X509Certificate[] certChain, boolean isDetached,
			boolean isCertChain) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CertificateEncodingException, IOException {
		byte[] signed = SignatureUtil.sign(plainText, prik, digestAlg, id, signCert);
		String digestAlgOid = OIDUtil.getDigestAlgOID(digestAlg);
		String encryptAlgOid = OIDUtil.getEncryptAlgOID(prik);
		PKCS7SignedData p7sdp = new PKCS7SignedData();
		if (!isDetached)
			p7sdp.setContent(plainText);
		p7sdp.setDigestAlgOid(digestAlgOid);
		p7sdp.setDigestEncryptionAlgOid(encryptAlgOid);
		p7sdp.setSignCertIssuerSubjectDer(signCert.getIssuerX500Principal().getEncoded());
		p7sdp.setSignCertSN(signCert.getSerialNumber());
		p7sdp.setSignature(signed);
		if (isCertChain)
			p7sdp.setCerts(certChain);
		else
			p7sdp.setCerts(new Certificate[] { signCert });
		PKCS7SignedDataGenerater p7sdg = new PKCS7SignedDataGenerater(p7sdp);
		return p7sdg.generatePKCS7SignedData();
	}

	public static void main(String[] args) throws Exception {
		String keyPath = "f:/temp/pdf/key/pfx/netseal.pfx";
		String certPath = "f:/temp/pdf/key/pfx/netseal.cer";

		// PrivateKey prik = KeyStoreUtil.loadKey("11111111", ".pfx", FileUtil.getFile(keyPath));
		X509Certificate signCert = CertUtil.parseCert(FileUtil.getFile("f:/temp/sign/2.cer")).getX509Cert();

		// byte[] signed = generatePKCS7SignedData("12345678".getBytes(), prik, "SHA1", null, signCert, null, false, false);
		// FileUtil.storeFile("f:/temp/sign/attachSign.asn1", signed);

		byte[] signed = Base64.decode(
				"MIIEdwYJKoZIhvcNAQcCoIIEaDCCBGQCAQExCzAJBgUrDgMCGgUAMCIGCSqGSIb3DQEHAaAVBBNPPUluZm9zZWMsQ049c2VhbF8yoIIDMDCCAywwggIUoAMCAQICBTtTpCDiMA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNVBAYTAmNuMSYwJAYDVQQKDB1JTkZPU0VDIFRlY2hub2xvZ2llcyBTSEEyNTZJRDEUMBIGA1UEAwwLYXBwU0hBMjU2SUQwHhcNMTgwMTAyMDUxOTU4WhcNMjAwOTI4MDUxOTU4WjAjMRAwDgYDVQQKDAdJbmZvc2VjMQ8wDQYDVQQDDAZzZWFsXzIwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAK+0NG/0bRCleyBEMHSbC/Nqw7iFx/TL7Kdte6sCmx+RZwb7MMUYZEDAFfkNpTcrPwOPcNQc+zRdBH6MJx6dXPumR2DvMcbQX5gi2nYac3yNygeyv671ixrUewJvhFagT3GwtdTRQZ4cnM93R/nc857wKfUZ5fSv9kC4szRv2IgRAgMBAAGjgcIwgb8wHwYDVR0jBBgwFoAUftjUt+YkKC1TcaPnLkuqwKPcuxgwCQYDVR0TBAIwADBlBgNVHR8EXjBcMFqgWKBWpFQwUjENMAsGA1UEAwwEY3JsNjEMMAoGA1UECwwDY3JsMSYwJAYDVQQKDB1JTkZPU0VDIFRlY2hub2xvZ2llcyBTSEEyNTZJRDELMAkGA1UEBhMCY24wCwYDVR0PBAQDAgeAMB0GA1UdDgQWBBSrUWH3xDRKfQm/oulwDgIYhTsjXzANBgkqhkiG9w0BAQsFAAOCAQEAalRcUhpIVHxLHlgBAPF27QXDj6XiEGPhca9hJYAM2/h5naD+JItlT86XuJu0ownDzCw7ApDPf/i3jrrHoL3zZFuMsixsE73BiwXR0/1s/p4wXgTDbc3Y2GXqPmHwkGC+lvd6Z8C1NbxCwAQXuyW3bNMVo09nNsERvzeBc8d9VFkQ9q+q0n1kbAw2aCwjjV1mJQQnVtiaxEOlB9W0GajihsrpulbekGMHQzTC4peR79AL5g0g69gyxr2p5sV4AUP+kGQgZYgeDp6XPqA11mn53ZkaDkZ9tcIO8UgedLXgXE7nPVFJvHTOerm8FsFcgHLHCdHuxTLZ3B4QVkInTmEJyjGB+TCB9gIBATBUMEsxCzAJBgNVBAYTAmNuMSYwJAYDVQQKDB1JTkZPU0VDIFRlY2hub2xvZ2llcyBTSEEyNTZJRDEUMBIGA1UEAwwLYXBwU0hBMjU2SUQCBTtTpCDiMAkGBSsOAwIaBQAwDQYJKoZIhvcNAQEBBQAEgYAt5s79I5sGXTqiwKzD3c1f+V2hVKPIBCLbKxJMZjKeHKGF1uFpkJugrxURx/d5x2/C8jLKAy0MBr4daewk41yEileiKoaPABmiyiTSCZXdz8GNIuORmkWsCEt3Dp/Z59vkSZucrRxPg9OuvMu6k9zLJd24M5B42xK96HZTzOF4Jg==");

		cn.com.infosec.pkcs.PKCS7SignedData p7 = new cn.com.infosec.pkcs.PKCS7SignedData(signed);
		byte[] signautre = p7.getSignature();
		System.out.println(HexUtil.byte2Hex(signautre));

		byte[] plain = HexUtil.hex2Byte("4f3d496e666f7365632c434e3d7365616c5f32");
		System.out.println(new String(plain));

		// boolean result = SignatureUtil.verify(plain, signautre, signCert.getPublicKey(), "SHA1", null);
		// System.out.println(result);

		boolean result = CryptoHandler.verify(signCert.getPublicKey(), new String(plain).getBytes(), signautre, ".pfx", 0, "SHA1withRSA", null);
		System.out.println(result);
	}

}
