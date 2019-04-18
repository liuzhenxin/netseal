package cn.com.infosec.netseal.appapi.common.util;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

public class X509CertEnvelope {

	X509Certificate x509Cert;
	BigInteger sn;
	String certSn;
	String certDn;
	String certIssueDn;
	Date notBefore;
	Date notAfter;
	boolean[] keyUsage;
	String sigAlgOID;
	byte[] encoded;
	PublicKey publicKey;

	public X509Certificate getX509Cert() {
		return x509Cert;
	}

	public void setX509Cert(X509Certificate x509Cert) {
		this.x509Cert = x509Cert;
	}

	public BigInteger getSn() {
		return sn;
	}

	public void setSn(BigInteger sn) {
		this.sn = sn;
	}

	public String getCertSn() {
		return certSn;
	}

	public void setCertSn(String certSn) {
		this.certSn = certSn;
	}

	public String getCertDn() {
		return certDn;
	}

	public void setCertDn(String certDn) {
		this.certDn = certDn;
	}

	public String getCertIssueDn() {
		return certIssueDn;
	}

	public void setCertIssueDn(String certIssueDn) {
		this.certIssueDn = certIssueDn;
	}

	public Date getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	public Date getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Date notAfter) {
		this.notAfter = notAfter;
	}

	public boolean[] getKeyUsage() {
		return keyUsage;
	}

	public void setKeyUsage(boolean[] keyUsage) {
		this.keyUsage = keyUsage;
	}

	public String getSigAlgOID() {
		return sigAlgOID;
	}

	public void setSigAlgOID(String signAlgOid) {
		this.sigAlgOID = signAlgOid;
	}

	public byte[] getEncoded() {
		return encoded;
	}

	public void setEncoded(byte[] encoded) {
		this.encoded = encoded;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
