package cn.com.infosec.netseal.appserver.processor;

import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.appserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ofd.OfdStampUtil;

@Component("VerifyOfdStampProcessor")
@Scope("prototype")
public class VerifyOfdStampProcessor extends BaseProcessor {

	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private KeyServiceImpl keyService;

	public VerifyOfdStampProcessor() {
		super("VerifyOfdStampProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String ofdData = getValue(reqdata, Constants.OFD_DATA);
		String ofdCheckSealDate = getValue(reqdata, Constants.OFD_CHECK_SEAL_DATE);
		String ofdCheckCertDate = getValue(reqdata, Constants.OFD_CHECK_CERT_DATE);

		// 检查数据值有效性
		checkParamValue(Constants.OFD_DATA, ofdData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
		checkParamValue(Constants.OFD_CHECK_SEAL_DATE, ofdCheckSealDate, Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);
		checkParamValue(Constants.OFD_CHECK_CERT_DATE, ofdCheckCertDate, Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);

		// OFD文件数据
		byte[] ofdDataBs = StringUtil.base64Decode(ofdData);
		
		// 获取根证集合
		Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();

		// 获取服务器加密卡签名证书
		List<Key> signKeyList = keyService.getCardKeys();

		// 验证PDF签章
		boolean result = OfdStampUtil.verifyOfdStamp(ofdDataBs, rootHt, signKeyList, Boolean.parseBoolean(ofdCheckSealDate), Boolean.parseBoolean(ofdCheckCertDate));
		pro.setProperty(Constants.RESULT, getValue(String.valueOf(result)));
		return getSucceedResponse();
	}
}
