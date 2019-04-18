package cn.com.infosec.netseal.common.entity.vo.asn1;

import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERIA5String;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERUTCTime;
import cn.com.infosec.asn1.DERUTF8String;

public class SealVO {
	private DERIA5String id;
	private DERInteger version;
	private DERIA5String vid;
	private DERIA5String esId;
	private DERInteger type;
	private DERUTF8String name;
	private DERConstructedSequence certList;
	private DERUTCTime createDate;
	private DERUTCTime validStart;
	private DERUTCTime validEnd;
	private DERIA5String photoType;
	private DEROctetString photoData;
	private DERInteger photoWidth;
	private DERInteger photoHeight;
	private DERConstructedSequence extDatas;
	private DEROctetString signCert;
	private DERObjectIdentifier signOid;
	private DERBitString signData;

	private DERConstructedSequence esealInfo;
	private DERConstructedSequence signInfo;

	public DERIA5String getId() {
		return id;
	}

	public void setId(DERIA5String id) {
		this.id = id;
	}

	public DERInteger getVersion() {
		return version;
	}

	public void setVersion(DERInteger version) {
		this.version = version;
	}

	public DERIA5String getVid() {
		return vid;
	}

	public void setVid(DERIA5String vid) {
		this.vid = vid;
	}

	public DERIA5String getEsId() {
		return esId;
	}

	public void setEsId(DERIA5String esId) {
		this.esId = esId;
	}

	public DERInteger getType() {
		return type;
	}

	public void setType(DERInteger type) {
		this.type = type;
	}

	public DERUTF8String getName() {
		return name;
	}

	public void setName(DERUTF8String name) {
		this.name = name;
	}

	public DERConstructedSequence getCertList() {
		return certList;
	}

	public void setCertList(DERConstructedSequence certList) {
		this.certList = certList;
	}

	public DERUTCTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DERUTCTime createDate) {
		this.createDate = createDate;
	}

	public DERUTCTime getValidStart() {
		return validStart;
	}

	public void setValidStart(DERUTCTime validStart) {
		this.validStart = validStart;
	}

	public DERUTCTime getValidEnd() {
		return validEnd;
	}

	public void setValidEnd(DERUTCTime validEnd) {
		this.validEnd = validEnd;
	}

	public DERIA5String getPhotoType() {
		return photoType;
	}

	public void setPhotoType(DERIA5String photoType) {
		this.photoType = photoType;
	}

	public DEROctetString getPhotoData() {
		return photoData;
	}

	public void setPhotoData(DEROctetString photoData) {
		this.photoData = photoData;
	}

	public DERInteger getPhotoWidth() {
		return photoWidth;
	}

	public void setPhotoWidth(DERInteger photoWidth) {
		this.photoWidth = photoWidth;
	}

	public DERInteger getPhotoHeight() {
		return photoHeight;
	}

	public void setPhotoHeight(DERInteger photoHeight) {
		this.photoHeight = photoHeight;
	}

	public DERConstructedSequence getExtDatas() {
		return extDatas;
	}

	public void setExtDatas(DERConstructedSequence extDatas) {
		this.extDatas = extDatas;
	}

	public DEROctetString getSignCert() {
		return signCert;
	}

	public void setSignCert(DEROctetString signCert) {
		this.signCert = signCert;
	}

	public DERObjectIdentifier getSignOid() {
		return signOid;
	}

	public void setSignOid(DERObjectIdentifier signOid) {
		this.signOid = signOid;
	}

	public DERBitString getSignData() {
		return signData;
	}

	public void setSignData(DERBitString signData) {
		this.signData = signData;
	}

	public DERConstructedSequence getEsealInfo() {
		return esealInfo;
	}

	public void setEsealInfo(DERConstructedSequence esealInfo) {
		this.esealInfo = esealInfo;
	}

	public DERConstructedSequence getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(DERConstructedSequence signInfo) {
		this.signInfo = signInfo;
	}

}
