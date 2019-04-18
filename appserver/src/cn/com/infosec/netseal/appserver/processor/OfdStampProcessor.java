package cn.com.infosec.netseal.appserver.processor;

import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.appserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.appserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.OidUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ofd.OfdStampUtil;

/**
 * PDF盖章
 *
 */
@Component("OfdStampProcessor")
@Scope("prototype")
public class OfdStampProcessor extends BaseProcessor {

	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertServiceImpl certService;

	public OfdStampProcessor() {
		super("OfdStampProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String ofdData64 = getValue(reqdata, Constants.OFD_DATA);
		String serverCertDN = getValue(reqdata, Constants.CERT_DN_SERVER);
		String certDN = getValue(reqdata, Constants.CERT_DN);
		String ofdPageNum = getValue(reqdata, Constants.OFD_PAGENUM);
		String ofdX = getValue(reqdata, Constants.OFD_X);
		String ofdY = getValue(reqdata, Constants.OFD_Y);
		String ofdKeywords = getValue(reqdata, Constants.OFD_KEYWORDS);
		String ofdBizNum = getValue(reqdata, Constants.OFD_BIZNUM);
		String ofdQfz = getValue(reqdata, Constants.OFD_QFZ);

		// 检查数据值有效性
		// 关键字签章、坐标签章、骑缝签章
		if (StringUtil.isNotBlank(ofdKeywords)) {
			checkParamValue(Constants.OFD_KEYWORDS, ofdKeywords, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);
			if (StringUtil.isNotBlank(ofdPageNum))
				checkParamValue(Constants.OFD_PAGENUM, ofdPageNum, Constants.PARAM_TYPE_INT);
		} else if (StringUtil.isNotBlank(ofdX) && StringUtil.isNotBlank(ofdY)) {
			checkParamValue(Constants.PDF_X, ofdX, Constants.PARAM_TYPE_FLOAT);
			checkParamValue(Constants.PDF_Y, ofdY, Constants.PARAM_TYPE_FLOAT);
			checkParamValue(Constants.OFD_PAGENUM, ofdPageNum, Constants.PARAM_TYPE_INT);
		} else if (StringUtil.isNotBlank(Constants.OFD_QFZ)) {
			checkParamValue(Constants.OFD_QFZ, ofdQfz, Constants.PARAM_TYPE_INT);
		} else
			throw new NetSealRuntimeException(ErrCode.NOT_FOUND_STAMP_PARAM, "not found stamp param(keywords、xy、qfz)");

		checkParamValue(Constants.OFD_DATA, ofdData64, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
		checkParamValue(Constants.CERT_DN_SERVER, serverCertDN, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_THREE_HUNDRED);
		checkParamValue(Constants.CERT_DN, certDN, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
		checkParamValue(Constants.OFD_BIZNUM, ofdBizNum, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);

		// OFD文件数据
		byte[] ofdData = StringUtil.base64Decode(ofdData64);

		Key key = null;
		if (StringUtil.isNotBlank(serverCertDN)) {
			serverCertDN = CertUtil.transCertDn(serverCertDN);
			// 获取服务器密钥
			List<Key> list = keyService.getKeys(serverCertDN);
			if (list == null || list.size() == 0)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "key is not exist in db, server cert dn is " + serverCertDN);
			key = list.get(0);
		} else {
			// 获取指定签名密钥
			ConfigUtil config = ConfigUtil.getInstance();
			long signKeyId = config.getSignKeyId();
			if (signKeyId == -1)
				throw new NetSealRuntimeException(ErrCode.SERVERCERT_NOT_SET, "key is not set ");

			key = keyService.getKey(signKeyId);
			if (key == null)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "key is not exist in db, server cert dn is " + serverCertDN);
		}
		// 校验密钥信息
		keyService.isModify(key);

		// 初始化服务器证书数据
		keyService.getKeyData(key);

		// 得到证书摘要算法
		byte[] certData = certService.getCertData(key.getCertPath(), key.getCertDataId());
		X509CertEnvelope certs = CertUtil.parseCert(certData);
		String hashAlg = OidUtil.getHashAlg(certs.getSigAlgOID()).toLowerCase();
		if (!Constants.SM3.equalsIgnoreCase(hashAlg))
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_CERT_ERROR, "ofd stamp only support sm2 key cert");

		// 校验服务器证书
		certChainService.verifyCert(FileUtil.getFile(key.getCertPath()));
		// 查询证书
		certDN = CertUtil.transCertDn(certDN);
		List<Cert> certList = certService.getCert(certDN, Constants.USAGE_SIGNATURE);
		List<Cert> certListEnc = certService.getCert(certDN, Constants.USAGE_SIGN_ENC);
		if (certList.size() == 0 && certListEnc.size() == 0)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_EXIST_IN_DB, "user cert not exist");
		if (certList.size() >= 2 || certListEnc.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "the number of cert is more than one");

		// 查询印章
		Seal seal = new Seal();
		Cert cert = certList.get(0);

		// 校验证书信息
		certService.isModify(cert);

		seal.setCertId(cert.getId());
		List<Seal> sealList = sealService.getSeal(seal);
		if (sealList.size() == 0)
			throw new NetSealRuntimeException(ErrCode.SEAL_NOT_EXIST_IN_DB, "seal info not exist");
		if (sealList.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.CERT_GEN_SEAL_NUM_ERROR, "the number of seal produced by the cert is more than one");

		seal = sealList.get(0);
		// 校验印章信息
		sealService.isModify(seal);

		// 手写章不支持OFD盖章
		if (seal.getType() == 3)
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_HANDSTAMP_ERROR, "handwritten seal does not support ofd stamp.");

		long currentTime = System.currentTimeMillis();
		long notbefor = seal.getNotBefor();
		long notafter = seal.getNotAfter();
		int usedLimit = seal.getUsedLimit();
		int usedCount = seal.getUsedCount();
		int status = seal.getStatus();

		if (currentTime < notbefor || currentTime > notafter)
			throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "seal date is invaild");

		if (usedLimit == -1 || (usedCount >= usedLimit && usedLimit != 0))
			throw new NetSealRuntimeException(ErrCode.SEAL_OVER_USED_LIMIT, "seal use times exceed the limit");

		if (status != 1)
			throw new NetSealRuntimeException(ErrCode.STATUS_DISABLE, "seal status disable");

		byte[] sealData = sealService.getSealData(seal.getSealPath(), seal.getSealDataId());
		byte[] keyData = FileUtil.getFile(key.getKeyPath());
		byte[] keyCertData = FileUtil.getFile(key.getCertPath());

		// 获取根证集合
		Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();
		// 获取服务器加密卡签名证书
		List<Key> signKeyList = keyService.getCardKeys();

		// OFD盖章
		byte[] ofdStampData = null;
		// 1. 关键字盖章
		if (StringUtil.isNotBlank(ofdKeywords)) {
			if (StringUtil.isNotBlank(ofdPageNum))
				ofdStampData = OfdStampUtil.ofdStampByText(ofdData, Integer.parseInt(ofdPageNum), ofdKeywords, ofdBizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
			else
				ofdStampData = OfdStampUtil.ofdStampByText(ofdData, ofdKeywords, ofdBizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
		}
		// 2. 坐标签章
		else if (StringUtil.isNotBlank(ofdX) && StringUtil.isNotBlank(ofdY))
			ofdStampData = OfdStampUtil.ofdStampByCoordinate(ofdData, Integer.parseInt(ofdPageNum), Float.parseFloat(ofdX), Float.parseFloat(ofdY), ofdBizNum, key, keyData, keyCertData, sealData,
					rootHt, signKeyList);
		// 3. 骑缝签章
		else if (StringUtil.isNotBlank(ofdQfz))
			ofdStampData = OfdStampUtil.ofdStampByQfz(ofdData, ofdBizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList, Integer.parseInt(ofdQfz));

		// 更新印章使用次数
		if (seal.getUsedLimit() != 0)
			if (sealService.updateSealCount(seal.getId(), currentTime) == 0)
				throw new NetSealRuntimeException(ErrCode.SEAL_OVER_USED_LIMIT, "seal use times exceed the limit");

		pro.setProperty(Constants.OFD_DATA_STAMP, getValue(StringUtil.base64Encode(ofdStampData)));

		return getSucceedResponse();
	}
}
