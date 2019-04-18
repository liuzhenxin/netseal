package cn.com.infosec.netseal.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERBoolean;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DEREncodable;
import cn.com.infosec.asn1.DERIA5String;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DERObject;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERTaggedObject;
import cn.com.infosec.asn1.DERUTCTime;
import cn.com.infosec.asn1.DERUTF8String;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.vo.asn1.SealVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;

public class GenSealUtil {

	/**
	 * 服务器签名-生成电子印章
	 * 
	 * @param seal
	 * @param sysUser
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static byte[] genSealData(Seal seal, List<byte[]> keyCertDataList, Key key, byte[] keyData, byte[] keyCertData, byte[] userCertData, byte[] photoData, byte[] id) {
		try {
			DERConstructedSequence SESeal = new DERConstructedSequence();

			// 1. 印章信息
			DERConstructedSequence esealInfo = new DERConstructedSequence();
			// 1.1 头信息
			DERConstructedSequence header = new DERConstructedSequence();
			header.addObject(new DERIA5String(Constants.GEN_SEAL_HEAD_ID));
			header.addObject(new DERInteger(Constants.GEN_SEAL_HEAD_VERSION));
			header.addObject(new DERIA5String(Constants.GEN_SEAL_HEAD_VID));
			esealInfo.addObject(header);

			// 1.2 电子印章标识
			X509CertEnvelope cert = CertUtil.parseCert(userCertData);
			esealInfo.addObject(new DERIA5String(cert.getCertSn()));

			// 1.3 印章属性信息
			DERConstructedSequence property = new DERConstructedSequence();
			property.addObject(new DERInteger(seal.getType() == 3 ? 2 : seal.getType()));
			property.addObject(new DERUTF8String(seal.getName()));

			DERConstructedSequence certList = new DERConstructedSequence();
			// 1.3.1 添加服务器证书
			for (byte[] certData : keyCertDataList)
				certList.addObject(new DEROctetString(CertUtil.parseCert(certData).getEncoded()));
			// 1.3.2 添加签章人证书
			certList.addObject(new DEROctetString(CertUtil.parseCert(userCertData).getEncoded()));

			property.addObject(certList);
			property.addObject(new DERUTCTime(new Date(seal.getGenerateTime())));
			property.addObject(new DERUTCTime(new Date(seal.getNotBefor())));
			property.addObject(new DERUTCTime(new Date(seal.getNotAfter())));
			esealInfo.addObject(property);

			// 1.4 图片数据
			DERConstructedSequence picture = new DERConstructedSequence();
			byte[] picture_bs = new byte[0];
			int width = 0, height = 0;

			// 手写印章没有图片
			if (photoData != null) {
				List list = FileUtil.getImagePro(photoData);
				picture_bs = (byte[]) list.get(0);
				width = (int) ((Integer) list.get(1) / Constants.GEN_SEAL_DPI * 25.4);
				height = (int) ((Integer) list.get(2) / Constants.GEN_SEAL_DPI * 25.4);
			}

			picture.addObject(new DERIA5String(FileUtil.getFileSuffix(seal.getPhotoPath())));
			picture.addObject(new DEROctetString(picture_bs));
			picture.addObject(new DERInteger(new Integer(width)));
			picture.addObject(new DERInteger(new Integer(height)));
			esealInfo.addObject(picture);

			// 2. 签名信息
			// 获取签名OID
			X509CertEnvelope certVO = CertUtil.parseCert(keyCertData);
			String signOid = certVO.getSigAlgOID();
			String signAlg = OidUtil.getSignAlg(signOid);

			DERConstructedSequence signInfo = new DERConstructedSequence();
			DEROctetString sign_cert = new DEROctetString(certVO.getEncoded());
			DERObjectIdentifier sign_oid = new DERObjectIdentifier(signOid);

			signInfo.addObject(sign_cert);
			signInfo.addObject(sign_oid);

			// 2.1 构建签名数据
			DERConstructedSequence toSign = new DERConstructedSequence();
			toSign.addObject(esealInfo);
			toSign.addObject(sign_cert);
			toSign.addObject(sign_oid);

			// 2.2 进行签名
			PrivateKey priKey = KeyStoreUtil.loadKey(StringUtil.getString(StringUtil.base64Decode(key.getKeyPwd())), key.getKeyMode(), keyData);
			byte[] signedData = CryptoHandler.sign(certVO.getPublicKey(), priKey, toSign.getEncoded(), key.getKeyMode(), key.getHsmId(), signAlg, id);
			signInfo.addObject(new DERBitString(signedData));

			SESeal.addObject(esealInfo);
			SESeal.addObject(signInfo);

			// 3. 保存文件
			FileUtil.storeFile(seal.getSealPath(), SESeal.getEncoded());
			return SESeal.getEncoded();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("gen seal data error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("gen seal data error", e);
			throw new NetSealRuntimeException(ErrCode.GEN_STAMP_ERROR, "gen seal data error, " + e.getMessage());
		}
	}

	/**
	 * 产生待签名的电子印章（将数据送入UKEY中签名）
	 * 
	 * @param seal
	 * @param sysUserCertPath
	 * @param userCertPath
	 * @return
	 * @throws Exception
	 */
	public static byte[] prepareSealData2Sign(Seal seal, List<byte[]> keyCertDataList, byte[] sysUserCertData, byte[] userCertData, byte[] photoData) throws Exception {
		try {
			// 1. 印章信息
			DERConstructedSequence seal_info = new DERConstructedSequence();
			// 1.1 头信息
			DERConstructedSequence head_seq = new DERConstructedSequence();
			head_seq.addObject(new DERIA5String(Constants.GEN_SEAL_HEAD_ID));
			head_seq.addObject(new DERInteger(Constants.GEN_SEAL_HEAD_VERSION));
			head_seq.addObject(new DERIA5String(Constants.GEN_SEAL_HEAD_VID));
			seal_info.addObject(head_seq);

			// 1.2 电子印章标识
			X509CertEnvelope cert = CertUtil.parseCert(userCertData);
			seal_info.addObject(new DERIA5String(cert.getCertSn()));

			// 1.3 印章属性信息
			DERConstructedSequence property_info = new DERConstructedSequence();
			property_info.addObject(new DERInteger(seal.getType()));
			property_info.addObject(new DERUTF8String(seal.getName()));

			DERConstructedSequence certList = new DERConstructedSequence();
			// 1.3.1 添加服务器证书
			for (byte[] certData : keyCertDataList)
				certList.addObject(new DEROctetString(CertUtil.parseCert(certData).getEncoded()));
			// 1.3.2 添加签章人证书
			certList.addObject(new DEROctetString(CertUtil.parseCert(userCertData).getEncoded()));

			property_info.addObject(certList);
			property_info.addObject(new DERUTCTime(new Date(seal.getGenerateTime())));
			property_info.addObject(new DERUTCTime(new Date(seal.getNotBefor())));
			property_info.addObject(new DERUTCTime(new Date(seal.getNotAfter())));
			seal_info.addObject(property_info);

			// 1.4 图片数据
			DERConstructedSequence picture_info = new DERConstructedSequence();
			byte[] picture_bs = new byte[0];
			int width = 0, height = 0;

			if (photoData != null) {
				List list = FileUtil.getImagePro(photoData);
				picture_bs = (byte[]) list.get(0);
				width = (int) ((Integer) list.get(1) / Constants.GEN_SEAL_DPI * 25.4);
				height = (int) ((Integer) list.get(2) / Constants.GEN_SEAL_DPI * 25.4);
			}
			picture_info.addObject(new DERIA5String(FileUtil.getFileSuffix(seal.getPhotoPath())));
			picture_info.addObject(new DEROctetString(picture_bs));
			picture_info.addObject(new DERInteger(new Integer(width)));
			picture_info.addObject(new DERInteger(new Integer(height)));
			seal_info.addObject(picture_info);

			// 2. 签名信息
			// 获取签名OID
			X509CertEnvelope certVO = CertUtil.parseCert(sysUserCertData);
			String signOid = certVO.getSigAlgOID();

			DERConstructedSequence sign_info = new DERConstructedSequence();
			DEROctetString sign_cert = new DEROctetString(certVO.getEncoded());
			DERObjectIdentifier sign_oid = new DERObjectIdentifier(signOid);

			sign_info.addObject(sign_cert);
			sign_info.addObject(sign_oid);

			// 2.1 构建签名数据
			DERConstructedSequence to_sign = new DERConstructedSequence();
			to_sign.addObject(seal_info);
			to_sign.addObject(sign_cert);
			to_sign.addObject(sign_oid);

			return to_sign.getEncoded();
		} catch (Exception e) {
			LoggerUtil.errorlog("prepare seal data 2 sign error", e);
			throw new WebDataException("生成待签名电子印章错误");
		}
	}

	/**
	 * 将UKEY签名值加入到电子印章中
	 * 
	 * @param sealPath
	 * @param sealData
	 * @throws Exception
	 */
	public static byte[] writeSigned2SealData(byte[] toSign, byte[] signedData, String savePath) throws Exception {
		DERInputStream din = null;
		try {
			din = new DERInputStream(new ByteArrayInputStream(toSign));
			DERObject toSign_obj = din.readObject();
			din.close();
			din = null;

			DERConstructedSequence SESeal = new DERConstructedSequence();
			// 电子印章数据
			if (!(toSign_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "toSign is not sequence");
			DERConstructedSequence toSignSeq = (DERConstructedSequence) toSign_obj;

			int toSignSize = toSignSeq.getSize();
			if (toSignSize != 3)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "to sign's sub element must 3");

			DEREncodable sealInfo_obj = toSignSeq.getObjectAt(0);
			if (!(sealInfo_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "to sign(seal info) is not sequence");

			DEREncodable signCert_obj = toSignSeq.getObjectAt(1);
			if (!(signCert_obj instanceof DEROctetString))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "to sign(sign cert) is not octet string");

			DEREncodable signOid_obj = toSignSeq.getObjectAt(2);
			if (!(signOid_obj instanceof DERObjectIdentifier))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "to sign(sign oid) is not object identifier");

			DERConstructedSequence signInfo = new DERConstructedSequence();
			signInfo.addObject((DEROctetString) signCert_obj);
			signInfo.addObject((DERObjectIdentifier) signOid_obj);
			signInfo.addObject(new DERBitString(signedData));

			SESeal.addObject((DERConstructedSequence) sealInfo_obj);
			SESeal.addObject(signInfo);

			// 3. 保存文件
			FileUtil.storeFile(savePath, SESeal.getEncoded());
			return SESeal.getEncoded();
		} catch (Exception e) {
			LoggerUtil.errorlog("write signed 2 seal data error", e);
			throw new WebDataException("合成电子印章错误");
		} finally {
			try {
				if (din != null)
					din.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 验证电子印章格式
	 * 
	 * @param seal
	 * @param plain
	 * @param userCertPath
	 * @param key
	 * @return
	 */
	public static SealVO verifySealData(byte[] SealData) {
		DERInputStream din = null;
		try {
			din = new DERInputStream(new ByteArrayInputStream(SealData));
			DERObject SESeal_obj = din.readObject();
			din.close();
			din = null;

			// 电子印章数据
			if (!(SESeal_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "SESeal is not sequence");
			DERConstructedSequence SESeal = (DERConstructedSequence) SESeal_obj;
			// 电子签章数据分为2部分
			if (SESeal.size() != 2)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "SESeal's child element len is not 2");

			// 1. ==============================================印章信息 ==============================================
			DEREncodable esealInfo_obj = SESeal.getObjectAt(0);
			if (!(esealInfo_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "esealInfo is not sequence");
			DERConstructedSequence esealInfo = (DERConstructedSequence) esealInfo_obj;

			// 印章信息分为4部分 或 5部分
			if (esealInfo.size() != 4 && esealInfo.size() != 5)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "esealInfo's child element len is not 4 and 5");

			// 1.1 ==============================================头信息==============================================
			DEREncodable header_obj = esealInfo.getObjectAt(0);
			if (!(header_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "header is not sequence");
			DERConstructedSequence header = (DERConstructedSequence) header_obj;
			if (header.size() != 3)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "header's child element len is not 3");
			// 1.1.1 电子印章数据标识
			DEREncodable ID_obj = header.getObjectAt(0);
			if (!(ID_obj instanceof DERIA5String))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "ID is not ia5string");
			// 1.1.2 电子印章数据版本号标识
			DEREncodable version_obj = header.getObjectAt(1);
			if (!(version_obj instanceof DERInteger))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "version is not integer");
			// 1.1.3 电子印章厂商ID
			DEREncodable Vid_obj = header.getObjectAt(2);
			if (!(Vid_obj instanceof DERIA5String))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "Vid is not ia5string");

			// 1.2 ==============================================电子印章标识==============================================
			DEREncodable esID_obj = esealInfo.getObjectAt(1);
			if (!(esID_obj instanceof DERIA5String))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "esID is not ia5string");

			// 1.3 ==============================================印章属性信息==============================================
			DEREncodable property_obj = esealInfo.getObjectAt(2);
			if (!(property_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "property is not sequence");
			DERConstructedSequence property = (DERConstructedSequence) property_obj;
			if (property.size() != 6)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "property's child element len is not 6");
			// 1.3.1 印章类型
			DEREncodable type_obj = property.getObjectAt(0);
			if (!(type_obj instanceof DERInteger))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "type is not integer");
			// 1.3.2 印章名称
			DEREncodable name_obj = property.getObjectAt(1);
			if (!(name_obj instanceof DERUTF8String))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "name is not utf8string");
			// 1.3.3 签章人证书列表
			DEREncodable certList_obj = property.getObjectAt(2);
			if (!(certList_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "certList is not sequence");
			DERConstructedSequence certList = (DERConstructedSequence) certList_obj;
			Enumeration en = certList.getObjects();
			while (en.hasMoreElements()) {
				DEREncodable object = (DEREncodable) en.nextElement();
				if (!(object instanceof DEROctetString))
					throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "certList's child element(cert) is not octetstring");
			}
			// 1.3.4 印章制作日期
			DEREncodable createDate_obj = property.getObjectAt(3);
			if (!(createDate_obj instanceof DERUTCTime))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "createDate is not utctime");
			// 1.3.5 印章有效起始日期
			DEREncodable validStart_obj = property.getObjectAt(4);
			if (!(validStart_obj instanceof DERUTCTime))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "validStart is not utctime");
			DERUTCTime validStart = (DERUTCTime) validStart_obj;
			// 1.3.6 印章有效终址日期
			DEREncodable validEnd_obj = property.getObjectAt(5);
			if (!(validEnd_obj instanceof DERUTCTime))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "validEnd is not utctime");
			DERUTCTime validEnd = (DERUTCTime) validEnd_obj;

			// 1.4 ==============================================电子印章图片数据==============================================
			DEREncodable picture_obj = esealInfo.getObjectAt(3);
			if (!(picture_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "picture is not sequence");
			DERConstructedSequence picture = (DERConstructedSequence) picture_obj;
			if (picture.size() != 4)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "picture's child element len is not 4");
			// 1.4.1 图片类型
			DEREncodable type_obj_p = picture.getObjectAt(0);
			if (!(type_obj_p instanceof DERIA5String))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "picture type is not ia5string");
			// 1.4.2 图片数据
			DEREncodable data_obj = picture.getObjectAt(1);
			if (!(data_obj instanceof DEROctetString))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "picture data is not octetstring");
			// 1.4.3 图片宽度
			DEREncodable width_obj = picture.getObjectAt(2);
			if (!(width_obj instanceof DERInteger))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "width is not integer");
			// 1.4.4 图片高度
			DEREncodable height_obj = picture.getObjectAt(3);
			if (!(height_obj instanceof DERInteger))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "height is not integer");

			// 1.5 ==============================================自定义数据==============================================
			if (esealInfo.size() == 5) {
				DEREncodable extDatas_obj = esealInfo.getObjectAt(4);
				if (!(extDatas_obj instanceof DERTaggedObject))
					throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "extDatas is not explicit sequence");

				DERTaggedObject extDatas_tag = (DERTaggedObject) extDatas_obj;
				if (!extDatas_tag.isExplicit())
					throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "extDatas is not explicit sequence");

				if (!(extDatas_tag.getObject() instanceof DERConstructedSequence))
					throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "extDatas is not explicit sequence");

				DERConstructedSequence extDatas = (DERConstructedSequence) extDatas_tag.getObject();

				en = extDatas.getObjects();
				while (en.hasMoreElements()) {
					DEREncodable object = (DEREncodable) en.nextElement();
					if (!(object instanceof DERConstructedSequence))
						throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "extDatas's child element(ExtData) is not sequence");

					DERConstructedSequence extData = (DERConstructedSequence) object;
					if (extData.size() != 3)
						throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "ExtData's child element len is not 3");

					if (!(extData.getObjectAt(0) instanceof DERObjectIdentifier))
						throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "ExtData's child element(extnID) is not objectidentifier");

					if (!(extData.getObjectAt(1) instanceof DERBoolean))
						throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "ExtData's child element(critical) is not boolean");

					if (!(extData.getObjectAt(2) instanceof DEROctetString))
						throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "ExtData's child element(extnValue) is not octetstring");
				}
			}

			// 2. ==============================================印章签名信息==============================================
			DEREncodable signInfo_obj = SESeal.getObjectAt(1);
			if (!(signInfo_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "signInfo is not sequence");
			DERConstructedSequence signInfo = (DERConstructedSequence) signInfo_obj;
			// 印章信息分为3部分
			if (signInfo.size() != 3)
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "signInfo's child element len is not 3");
			// 2.1 制章人签名证书
			DEREncodable cert_obj = signInfo.getObjectAt(0);
			if (!(cert_obj instanceof DEROctetString))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "cert is not octetstring");
			// 2.2 签名算法标识
			DEREncodable signatureAlorithm_obj = signInfo.getObjectAt(1);
			if (!(signatureAlorithm_obj instanceof DERObjectIdentifier))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "signatureAlorithm is not objectidentifier");
			// 2.3 制章人签名值
			DEREncodable signData_obj_s = signInfo.getObjectAt(2);
			if (!(signData_obj_s instanceof DERBitString))
				throw new NetSealRuntimeException(ErrCode.SEAL_FORMAT_INVAILD, "signData is not bitstring");

			// 3. ==============================================组成对象==============================================
			SealVO sealVo = new SealVO();
			sealVo.setId((DERIA5String) ID_obj);
			sealVo.setVersion((DERInteger) version_obj);
			sealVo.setVid((DERIA5String) Vid_obj);
			sealVo.setEsId((DERIA5String) esID_obj);
			sealVo.setType((DERInteger) type_obj);
			sealVo.setName((DERUTF8String) name_obj);
			sealVo.setCertList((DERConstructedSequence) certList_obj);
			sealVo.setCreateDate((DERUTCTime) createDate_obj);
			sealVo.setValidStart((DERUTCTime) validStart_obj);
			sealVo.setValidEnd((DERUTCTime) validEnd_obj);
			sealVo.setPhotoType((DERIA5String) type_obj_p);
			sealVo.setPhotoData((DEROctetString) data_obj);
			sealVo.setPhotoWidth((DERInteger) width_obj);
			sealVo.setPhotoHeight((DERInteger) height_obj);

			if (esealInfo.size() == 5) {
				DEREncodable extDatas_obj = esealInfo.getObjectAt(4);
				DERTaggedObject extDatas_tag = (DERTaggedObject) extDatas_obj;
				DERObject extDatas = extDatas_tag.getObject();
				sealVo.setExtDatas((DERConstructedSequence) extDatas);
			}

			sealVo.setSignCert((DEROctetString) cert_obj);
			sealVo.setSignOid((DERObjectIdentifier) signatureAlorithm_obj);
			sealVo.setSignData((DERBitString) signData_obj_s);

			sealVo.setEsealInfo(esealInfo);
			sealVo.setSignInfo(signInfo);

			return sealVo;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("verify seal data error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("verify seal data error", e);
			throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "verify seal data error, " + e.getMessage());
		} finally {
			try {
				if (din != null)
					din.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 验证电子印章
	 * 
	 * @param seal
	 * @param plain
	 * @param userCertPath
	 * @param key
	 * @return
	 */
	public static void verifySealData(SealVO sealVo, Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList, byte[] id, boolean checkSealDate, boolean checkCertDate) {
		try {
			// 1. 获取签名属性
			DEROctetString cert = sealVo.getSignCert();
			DERObjectIdentifier signatureAlorithm = sealVo.getSignOid();
			DERBitString signData = sealVo.getSignData();

			// 2. 验证签名
			DERConstructedSequence toSign = new DERConstructedSequence();
			toSign.addObject(sealVo.getEsealInfo());
			toSign.addObject(cert);
			toSign.addObject(signatureAlorithm);

			byte[] certData = cert.getOctets();
			X509CertEnvelope certEnv = CertUtil.parseCert(certData);
			String signAlg = OidUtil.getSignAlg(signatureAlorithm.getId());

			// 加密卡签名的数据使用加密卡验签
			String keyMode = Constants.PFX_SUFFIX;
			int hsmId = 0;
			if (signKeyList != null) {
				for (Key key : signKeyList) {
					if (key.getCertDn().equals(certEnv.getCertDn()) && key.getCertSn().equals(certEnv.getCertSn())) {
						keyMode = key.getKeyMode();
						hsmId = key.getHsmId();
						break;
					}
				}
			}

			if (!CryptoHandler.verify(certEnv.getPublicKey(), toSign.getEncoded(), signData.getBytes(), keyMode, hsmId, signAlg, id))
				throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "verify seal signature result is false");

			// 3. 验证制章证书有效性
			X509Certificate rootCert = rootHt.get(certEnv.getCertIssueDn());
			if (rootCert == null)
				throw new NetSealRuntimeException(ErrCode.CERT_NOT_IN_TRUST, "cert root not in trust chain, cert root dn is " + certEnv.getCertIssueDn());
			CertUtil.verifyCert(certData, rootCert.getEncoded(), null, true, false, checkCertDate);

			// 4. 验有效期
			// 4.1 验产生印章时间在证书有效期内
			Date genSealDate = DateUtil.transUtcTimeDate(sealVo.getCreateDate().getDate());
			Date notBefore = CertUtil.parseCert(certData).getNotBefore();
			Date notAfter = CertUtil.parseCert(certData).getNotAfter();
			if (genSealDate.before(notBefore) || genSealDate.after(notAfter))
				throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "gen seal date not in the period of cert date validity");

			// 4.2 验印章有效期
			if (checkSealDate) {
				Date currentDate = new Date();
				notBefore = DateUtil.transUtcTimeDate(sealVo.getValidStart().getDate());
				notAfter = DateUtil.transUtcTimeDate(sealVo.getValidEnd().getDate());
				if (currentDate.before(notBefore) || currentDate.after(notAfter))
					throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "seal date not in the period of validity of current date");
			}
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("verify seal data error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("verify seal data error", e);
			throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "verify seal data error, " + e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		LoggerConfig.init();
		long time = System.currentTimeMillis();

		Seal seal = new Seal();
		seal.setId(1L);
		seal.setName("testSeal");
		seal.setType(1);
		seal.setGenerateTime(time);
		seal.setNotBefor(time);
		seal.setNotAfter(time + 30 * 24 * 3600 * 1000L);
		seal.setSealPath("f:/temp/v3/seal.asn1");

		List<byte[]> certDataList = new ArrayList<byte[]>();
		// certDataList.add(FileUtil.getFile("F:/temp/appSM2ID_SUBCA.cer"));

		Key key = new Key();
		key.setKeyPwd(StringUtil.base64Encode("68683556"));
		key.setKeyMode(".jks");
		key.setHsmId(0);
		byte[] keyData = FileUtil.getFile("F:/temp/pdf/key/jks/netseal.jks");
		byte[] keyCertData = FileUtil.getFile("F:/temp/pdf/key/jks/netseal.cer");
		byte[] userCertData = FileUtil.getFile("F:/temp/pdf/key/pfx/netseal.cer");
		byte[] photoData = FileUtil.getFile("F:/temp/seal.png");
		certDataList.add(keyCertData);

		byte[] data = genSealData(seal, certDataList, key, keyData, keyCertData, userCertData, photoData, "1234567812345678".getBytes());
		FileUtil.storeFile("f:/temp/ye/seal.asn1", data);

		String base64 = Base64.encode(FileUtil.getFile("f:/temp/ye/seal.asn1"));
		System.out.println(base64);

		FileUtil.storeFile("f:/temp/ye/111.asn1", Base64.decode(base64));

		// Hashtable<String, X509Certificate> ht = new Hashtable<String, X509Certificate>();
		// X509Certificate root = CertUtil.parseCert(FileUtil.getFile("F:/temp/v3/appSM2ID.cer")).getX509Cert();
		// X509Certificate root1 = CertUtil.parseCert(FileUtil.getFile("F:/temp/v3/appSM2ID_SUBCA.cer")).getX509Cert();
		// X509Certificate root2 = CertUtil.parseCert(FileUtil.getFile("F:/temp/pdf/key/jks/netseal.cer")).getX509Cert();
		// ht.put(root.getSubjectDN().getName(), root);
		// ht.put(root1.getSubjectDN().getName(), root1);
		// ht.put(root2.getSubjectDN().getName(), root2);
		//
		// // // SealVO sealVo = GenSealUtil.verifySealData(FileUtil.getFile("f:/temp/v3/47c22adc5a0f62f51ce46c65b937aacc6fa0150b.seal"));
		// // // GenSealUtil.verifySealData(sealVo, ht, "1234567812345678".getBytes());
		// //
		// // byte[] sysUserCertData = FileUtil.getFile("F:/temp/pdf/sm2/netseal.cer");
		// // byte[] userCertData = FileUtil.getFile("F:/temp/pdf/sm2/netseal.cer");
		// // byte[] photoData = FileUtil.getFile("F:/temp/seal.png");
		// // List<byte[]> certList = new ArrayList<byte[]>();
		// // certList.add(sysUserCertData);
		// // byte[] toSign = GenSealUtil.prepareSealData2Sign(seal, certList, sysUserCertData, userCertData, photoData);
		// //
		// // String signAlg = OidUtil.getSignAlg(CertUtil.parseCert(sysUserCertData).getX509Cert().getSigAlgOID());
		// // PrivateKey priKey = KeyStoreUtil.loadKey(key.getKeyPwdPlain(), key.getKeyMode(), keyData);
		// // byte[] signedData = CryptoHandler.sign(CertUtil.parseCert(sysUserCertData).getPublicKey(), priKey, toSign, key.getKeyMode(), key.getHsmId(), signAlg, "1234567812345678".getBytes());
		// //
		// // byte[] sealData = GenSealUtil.writeSigned2SealData(toSign, signedData, "f:/temp/v3/seal.seal");
		//
		// SealVO sealVo = GenSealUtil.verifySealData(FileUtil.getFile("f:/temp/v3/seal.asn1"));
		// GenSealUtil.verifySealData(sealVo, ht, null, "1234567812345678".getBytes(), true, true);

	}

}
