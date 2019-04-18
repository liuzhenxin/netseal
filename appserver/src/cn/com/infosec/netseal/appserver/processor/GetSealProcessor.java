package cn.com.infosec.netseal.appserver.processor;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.OidUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Component("GetSealProcessor")
@Scope("prototype")
public class GetSealProcessor extends BaseProcessor {

	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private CertServiceImpl certService;

	public GetSealProcessor() {
		super("GetSealProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();
		String certDN = getValue(reqdata, Constants.CERT_DN);
		String signData = getValue(reqdata, Constants.SIGN_DATA);

		// 检查数据值有效性
		checkParamValue(Constants.CERT_DN, certDN, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
		certDN = CertUtil.transCertDn(certDN);

		// 查询证书
		List<Cert> certList = certService.getCert(certDN, Constants.USAGE_SIGNATURE);
		List<Cert> certListEnc = certService.getCert(certDN, Constants.USAGE_SIGN_ENC);
		if (certList.size() == 0 && certListEnc.size() == 0)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_EXIST_IN_DB, "user cert not exist");
		if (certList.size() >= 2 || certListEnc.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "the number of cert is more than one");

		// 查询印章
		Seal seal = new Seal();
		Cert cert = certList.get(0);
		// 校验信息
		certService.isModify(cert);

		// 验证客户端签名
		boolean isCheckSignData = ConfigUtil.getInstance().getCheckSealSignData();
		if (isCheckSignData) {
			checkParamValue(Constants.SIGN_DATA, signData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THOUSAND);
			String certPath = cert.getCertPath();
			byte[] certData = certService.getCertData(certPath, cert.getCertDataId());
			X509CertEnvelope x509cert = CertUtil.parseCert(certData);
			String signAlg = OidUtil.getSignAlg(x509cert.getSigAlgOID());

			try {
				if (!CryptoHandler.verify(x509cert.getPublicKey(), StringUtil.getBytes(certDN), StringUtil.base64Decode(signData), Constants.PFX_SUFFIX, 0, signAlg,
						ConfigUtil.getInstance().getGmOid().getBytes()))
					throw new NetSealRuntimeException("the sign data verify wrong");
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.CERT_SIGN_VERIFY_ERROR, "the sign data verify wrong");
			}
		}

		seal.setCertId(cert.getId());
		List<Seal> sealList = sealService.getSeal(seal);
		if (sealList.size() == 0)
			throw new NetSealRuntimeException(ErrCode.SEAL_NOT_EXIST_IN_DB, "seal info not exist");
		if (sealList.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.CERT_GEN_SEAL_NUM_ERROR, "the number of seal produced by the cert is more than one");

		seal = sealList.get(0);
		// 校验印章信息
		sealService.isModify(seal);

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

		byte[] sealData = sealService.getSealData(seal.getId());

		// // 更新印章使用次数
		// if (seal.getUsedLimit() != 0)
		// if (sealService.updateSealCount(seal.getId(), currentTime) == 0)
		// throw new NetSealRuntimeException(ErrCode.SEAL_OVER_USED_LIMIT, "seal use times exceed the limit");

		pro.setProperty(Constants.SEAL_DATA, getValue(StringUtil.base64Encode(sealData)));
		return getSucceedResponse();

	}
}
