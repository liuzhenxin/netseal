package cn.com.infosec.netseal.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;

import cn.com.infosec.asn1.DERBitString;
import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DEREncodable;
import cn.com.infosec.asn1.DERIA5String;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERInteger;
import cn.com.infosec.asn1.DERObject;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERUTCTime;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.vo.asn1.SealVO;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class GenStampUtil {

	/**
	 * 服务器生成电子签章
	 * 
	 * @param plain
	 * @param sealData
	 * @param key
	 * @param keyData
	 * @param keyCertData
	 * @param rootCertHt
	 * @param signList
	 * @param id
	 * @return
	 */
	public static byte[] genStampData(byte[] plain, byte[] sealData, Key key, byte[] keyData, byte[] keyCertData, Hashtable<String, X509Certificate> rootCertHt, List<Key> signKeyList, byte[] id) {
		DERInputStream din = null;
		try {
			din = new DERInputStream(new ByteArrayInputStream(sealData));
			DERObject eseal = din.readObject();
			din.close();
			din = null;

			// 1. 验证签章人证书
			X509CertEnvelope certVO = CertUtil.parseCert(keyCertData);

			X509Certificate rootCert = rootCertHt.get(certVO.getCertIssueDn());
			if (rootCert == null)
				throw new NetSealRuntimeException(ErrCode.CERT_NOT_IN_TRUST, "cert root not in trust chain, cert root dn is " + certVO.getCertIssueDn());
			CertUtil.verifyCert(keyCertData, rootCert.getEncoded(), null, true, false, true);

			// 2. 验证电子印章
			SealVO sealVo = GenSealUtil.verifySealData(sealData);
			GenSealUtil.verifySealData(sealVo, rootCertHt, signKeyList, id, true, true);

			// 3. 对比印章中的签章人列表
			boolean result = false;
			DERConstructedSequence certList = sealVo.getCertList();
			Enumeration en = certList.getObjects();
			while (en.hasMoreElements()) {
				DEROctetString certInSeal = (DEROctetString) en.nextElement();
				if (Arrays.equals(certVO.getEncoded(), CertUtil.parseCert(certInSeal.getOctets()).getEncoded())) {
					result = true;
					break;
				}
			}
			if (ConfigUtil.getInstance().getCheckSealUserCertList() && !result)
				throw new NetSealRuntimeException(ErrCode.GEN_STAMP_ERROR, "cert not in seal cert list, cert dn is " + certVO.getCertDn());

			// 4. 组成待签名原文、签名、形成电子签章数据
			// 摘要算法、签名算法
			String hashAlg = OidUtil.getHashAlg(certVO.getSigAlgOID());
			String signAlg = OidUtil.getSignAlg(certVO.getSigAlgOID());
			byte[] hash = CryptoHandler.hash(plain, certVO.getPublicKey(), hashAlg, id);

			DERConstructedSequence ses_signature = new DERConstructedSequence();
			// 4.1 待电子签章数据
			DERConstructedSequence tosign = new DERConstructedSequence();
			tosign.addObject(new DERInteger(Constants.GEN_SEAL_HEAD_VERSION));
			tosign.addObject(eseal);
			tosign.addObject(new DERBitString(new DERUTCTime(new Date())));
			tosign.addObject(new DERBitString(hash));
			tosign.addObject(new DERIA5String("plain len:" + plain.length + " bytes"));
			tosign.addObject(new DEROctetString(certVO.getEncoded()));
			tosign.addObject(new DERObjectIdentifier(certVO.getSigAlgOID()));

			// 4.2 进行签名
			PrivateKey priKey = KeyStoreUtil.loadKey(key.getKeyPwdPlain(), key.getKeyMode(), keyData);
			byte[] signedData = CryptoHandler.sign(certVO.getPublicKey(), priKey, tosign.getEncoded(), key.getKeyMode(), key.getHsmId(), signAlg, id);

			// 4.3 形成电子签章数据
			DERBitString signature = new DERBitString(signedData);
			ses_signature.addObject(tosign);
			ses_signature.addObject(signature);

			return ses_signature.getEncoded();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("gen stamp data error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("gen stamp data error", e);
			throw new NetSealRuntimeException(ErrCode.GEN_STAMP_ERROR, "generate stamp data error, " + e.getMessage());
		} finally {
			try {
				if (din != null)
					din.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 验证电子签章
	 * 
	 * @param seal
	 * @param plain
	 * @param userCertPath
	 * @param key
	 * @return
	 */
	public static void verifyStampData(byte[] plain, byte[] digest, byte[] stampData, Hashtable<String, X509Certificate> rootCertHt, List<Key> signKeyList, byte[] id, boolean checkSealDate,
			boolean checkCertDate) {
		DERInputStream din = null;
		try {
			din = new DERInputStream(new ByteArrayInputStream(stampData));
			DERObject ses_signature_obj = din.readObject();
			din.close();
			din = null;

			// 1. ==============================================电子签章数据==============================================
			if (!(ses_signature_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "ses_signature is not sequence");
			DERConstructedSequence ses_signature = (DERConstructedSequence) ses_signature_obj;
			// 1.1 电子签章数据分为2部分
			if (ses_signature.size() != 2)
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "ses_signature's child element is not 2");

			// 2. ==============================================待电子签章数据==============================================
			DEREncodable toSign_obj = ses_signature.getObjectAt(0);
			if (!(toSign_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "toSign is not sequence");
			DERConstructedSequence toSign = (DERConstructedSequence) toSign_obj;

			// 2.2 电子签章分为7部分
			if (toSign.size() != 7)
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "toSign's child element is not 7");

			// 2.3 版本信息
			DEREncodable version_obj = toSign.getObjectAt(0);
			if (!(version_obj instanceof DERInteger))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "version is not integer");
			// 2.4 电子印章
			DEREncodable esseal_obj = toSign.getObjectAt(1);
			if (!(esseal_obj instanceof DERConstructedSequence))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "esseal is not sequence");
			DERConstructedSequence esseal = (DERConstructedSequence) esseal_obj;
			// 2.5 签章时间信息
			DEREncodable timeInfo_obj = toSign.getObjectAt(2);
			if (!(timeInfo_obj instanceof DERBitString))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "timeInfo is not bit string");
			DERBitString timeInfo = (DERBitString) timeInfo_obj;
			din = new DERInputStream(new ByteArrayInputStream(timeInfo.getBytes()));
			DERObject derTime = din.readObject();
			din.close();
			din = null;

			Date signedDate = null;
			byte[] messageImprint = null;
			if (derTime instanceof DERUTCTime)
				signedDate = DERUTCTime.getInstance(derTime).getDate();
			else if (derTime instanceof DERConstructedSequence) {
				byte[] tsTokenBs = DERConstructedSequence.getInstance(derTime).getDEREncoded();
				TimeStampToken tsToken = new TimeStampToken(new CMSSignedData(tsTokenBs));
				TimeStampTokenInfo tstInfo = tsToken.getTimeStampInfo();
				signedDate = tstInfo.getGenTime();
				messageImprint = tstInfo.getMessageImprintDigest();
			} else
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "timeInfo's child is not utctime or timestamptoken");
			signedDate = DateUtil.transUtcTimeDate(signedDate);

			// 2.6 原文摘要
			DEREncodable dataHash_obj = toSign.getObjectAt(3);
			if (!(dataHash_obj instanceof DERBitString))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "dataHash is not bit string");
			// 2.7 原文属性信息
			DEREncodable propertyInfo_obj = toSign.getObjectAt(4);
			if (!(propertyInfo_obj instanceof DERIA5String))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "propertyInfo is not ia5 string");
			// 2.8 签章人签名证书
			DEREncodable cert_obj = toSign.getObjectAt(5);
			if (!(cert_obj instanceof DEROctetString))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "cert is not octet string");
			DEROctetString cert = (DEROctetString) cert_obj;
			// 2.9 签名算法标识
			DEREncodable signatureAlgorithm_obj = toSign.getObjectAt(6);
			if (!(signatureAlgorithm_obj instanceof DERObjectIdentifier))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "signatureAlgorithm is not object identifier");
			DERObjectIdentifier signatureAlgorithm = (DERObjectIdentifier) signatureAlgorithm_obj;

			// 3. ==============================================电子签章中签名值==============================================
			DEREncodable signature_obj = ses_signature.getObjectAt(1);
			if (!(signature_obj instanceof DERBitString))
				throw new NetSealRuntimeException(ErrCode.STAMP_FORMAT_INVAILD, "signature is not bit string");
			DERBitString signature = (DERBitString) signature_obj;
			X509CertEnvelope certEnv = CertUtil.parseCert(cert.getOctets());
			String signAlg = OidUtil.getSignAlg(signatureAlgorithm.getId());

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

			if (!CryptoHandler.verify(certEnv.getPublicKey(), toSign.getEncoded(), signature.getBytes(), keyMode, hsmId, signAlg, id))
				throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "verify stamp signature result is false");

			// 4. ==============================================签章证书有效性==============================================
			X509Certificate rootCert = rootCertHt.get(certEnv.getCertIssueDn());
			if (rootCert == null)
				throw new NetSealRuntimeException(ErrCode.CERT_NOT_IN_TRUST, "cert root not in trust chain, cert root dn is " + certEnv.getCertIssueDn());
			CertUtil.verifyCert(certEnv.getEncoded(), rootCert.getEncoded(), signedDate);

			// 5. ==============================================签章时间有效性==============================================
			Date notBefore = certEnv.getNotBefore();
			Date notAfter = certEnv.getNotAfter();
			if (signedDate.before(notBefore) || signedDate.after(notAfter))
				throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "signed date not in the period of validity of cert");

			// 6. ==============================================验证原文摘要==============================================
			if (plain != null) {
				// 摘要算法
				String hashAlg = OidUtil.getHashAlg(signatureAlgorithm.getId());
				byte[] hash = CryptoHandler.hash(plain, certEnv.getPublicKey(), hashAlg, id);

				byte[] dataHash = ((DERBitString) dataHash_obj).getBytes();
				if (!Arrays.equals(hash, dataHash))
					throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "calc hash compare fail");
			}

			// 6.1 直接对比摘要
			if (digest != null) {
				byte[] dataHash = ((DERBitString) dataHash_obj).getBytes();
				if (!Arrays.equals(digest, dataHash))
					throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "hash compare fail");

				if (messageImprint != null)
					if (!Arrays.equals(digest, dataHash))
						throw new NetSealRuntimeException(ErrCode.VERIFY_SEAL_ERROR, "message imprint compare fail");
			}

			// 7. ==============================================验证印章有效性==============================================
			SealVO sealVo = GenSealUtil.verifySealData(esseal.getEncoded());
			GenSealUtil.verifySealData(sealVo, rootCertHt, signKeyList, id, checkSealDate, checkCertDate);

			notBefore = DateUtil.transUtcTimeDate(sealVo.getValidStart().getDate());
			notAfter = DateUtil.transUtcTimeDate(sealVo.getValidEnd().getDate());
			if (signedDate.before(notBefore) || signedDate.after(notAfter))
				throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "signed date not in the period of validity of seal");

			// 7.1 验证签章人列表
			boolean result = false;
			DERConstructedSequence certList = sealVo.getCertList();
			Enumeration en = certList.getObjects();
			while (en.hasMoreElements()) {
				DEROctetString certInSeal = (DEROctetString) en.nextElement();
				if (Arrays.equals(certEnv.getEncoded(), CertUtil.parseCert(certInSeal.getOctets()).getEncoded())) {
					result = true;
					break;
				}
			}

			if (ConfigUtil.getInstance().getCheckSealUserCertList() && !result)
				throw new NetSealRuntimeException(ErrCode.VERIFY_STAMP_ERROR, "cert not in seal cert list, cert dn is " + certEnv.getCertDn());
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("verify stamp data error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("verify stamp data error", e);
			throw new NetSealRuntimeException(ErrCode.GEN_STAMP_ERROR, "verify stamp data error, " + e.getMessage());
		} finally {
			try {
				if (din != null)
					din.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// Seal seal = new Seal();
		// seal.setId(1L);
		// seal.setName("testSeal");
		// seal.setType(1);
		// long time = System.currentTimeMillis();
		// seal.setGenerateTime(time);
		// seal.setNotBefor(time);
		// seal.setNotAfter(time + 24 * 3600 * 1000L);
		// seal.setPhotoPath("F:/temp/seal.png");
		// seal.setSealPath("f:/temp/stamp.asn1");
		//
		// SealData sealData = new SealData();
		// sealData.setData(FileUtil.getFileNoCheckPath("f:/temp/seal.seal"));
		// seal.setSealData(sealData);
		//
		// Key key = new Key();
		// key.setKeyPath("F:/temp/pdf/key/toseal.pfx");
		// key.setCertPath("F:/temp/pdf/key/toseal.cer");
		// key.setKeyPwd(StringUtil.base64Encode("11111111"));
		// key.setKeyMode(".pfx");
		// key.setHsmId(0);
		//
		// byte[] stampData = genStampData("123".getBytes(), seal, key, null);
		// FileUtil.storeFileNoCheckPath("f:/temp/stamp.asn1", stampData);

		Hashtable<String, X509Certificate> ht = new Hashtable<String, X509Certificate>();
		X509Certificate root = CertUtil.parseCert(FileUtil.getFile("F:/temp/appSM2ID_SUBCA.cer")).getX509Cert();
		X509Certificate root1 = CertUtil.parseCert(FileUtil.getFile("F:/temp/pdf/key/toseal.cer")).getX509Cert();
		ht.put(root.getSubjectDN().getName(), root);
		ht.put(root1.getSubjectDN().getName(), root1);

		byte[] plain = FileUtil.getFile("f:/temp/2018032310009792.pdf");
		verifyStampData(plain, null, FileUtil.getFile("f:/temp/stamp4Pdf.asn1"), ht, null, "1234567811111111".getBytes(), true, true);

	}

}
