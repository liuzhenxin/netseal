package cn.com.infosec.netseal.common.entity.vo.asn1;

import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERIA5String;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;

public class StampVO {
	private DERConstructedSequence SES_Signature;
	private DERConstructedSequence toSign;
	private DERBitString signature;

	private DERInteger version;
	private DERConstructedSequence SESeal;
	private DERBitString timeInfo;
	private DERBitString dataHash;
	private DERIA5String propertyInfo;
	private DEROctetString cert;
	private DERObjectIdentifier signatureAlgorithm;

	public DERConstructedSequence getSES_Signature() {
		return SES_Signature;
	}

	public void setSES_Signature(DERConstructedSequence sES_Signature) {
		SES_Signature = sES_Signature;
	}

	public DERConstructedSequence getToSign() {
		return toSign;
	}

	public void setToSign(DERConstructedSequence toSign) {
		this.toSign = toSign;
	}

	public DERBitString getSignature() {
		return signature;
	}

	public void setSignature(DERBitString signature) {
		this.signature = signature;
	}

	public DERInteger getVersion() {
		return version;
	}

	public void setVersion(DERInteger version) {
		this.version = version;
	}

	public DERConstructedSequence getSESeal() {
		return SESeal;
	}

	public void setSESeal(DERConstructedSequence sESeal) {
		SESeal = sESeal;
	}

	public DERBitString getTimeInfo() {
		return timeInfo;
	}

	public void setTimeInfo(DERBitString timeInfo) {
		this.timeInfo = timeInfo;
	}

	public DERBitString getDataHash() {
		return dataHash;
	}

	public void setDataHash(DERBitString dataHash) {
		this.dataHash = dataHash;
	}

	public DERIA5String getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(DERIA5String propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public DEROctetString getCert() {
		return cert;
	}

	public void setCert(DEROctetString cert) {
		this.cert = cert;
	}

	public DERObjectIdentifier getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(DERObjectIdentifier signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public byte[] getBytes() throws Exception {
		DERConstructedSequence ses_signature = new DERConstructedSequence();

		DERConstructedSequence tosign = new DERConstructedSequence();
		tosign.addObject(getVersion());
		tosign.addObject(getSESeal());
		tosign.addObject(getTimeInfo());
		tosign.addObject(getDataHash());
		tosign.addObject(getPropertyInfo());
		tosign.addObject(getCert());
		tosign.addObject(getSignatureAlgorithm());

		ses_signature.addObject(tosign);
		ses_signature.addObject(getSignature());

		return ses_signature.getEncoded();
	}
}
