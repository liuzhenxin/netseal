package cn.com.infosec.netseal.appserver.processor;

import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import cn.com.infosec.netseal.appserver.service.certChain.CertChainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.pdf.PdfStampUtil;

@Component("VerifyPdfStampProcessor")
@Scope("prototype")
public class VerifyPdfStampProcessor extends BaseProcessor {

	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private KeyServiceImpl keyService;

	public VerifyPdfStampProcessor() {
		super("VerifyPdfStampProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String pdfSealData = getValue(reqdata, Constants.PDF_DATA);
		String pdfCheckSealDate = getValue(reqdata, Constants.PDF_CHECK_SEAL_DATE);
		String pdfCheckCertDate = getValue(reqdata, Constants.PDF_CHECK_CERT_DATE);

		// 检查数据值有效性
		checkParamValue(Constants.PDF_DATA, pdfSealData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
		checkParamValue(Constants.PDF_CHECK_SEAL_DATE, pdfCheckSealDate, Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);
		checkParamValue(Constants.PDF_CHECK_CERT_DATE, pdfCheckCertDate, Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);

		// 获取PDF文档数据
		byte[] data = StringUtil.base64Decode(pdfSealData);

		// 获取根证集合
		Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();

		// 获取服务器加密卡签名证书
		List<Key> signKeyList = keyService.getCardKeys();

		// 验证PDF签章
		boolean result = PdfStampUtil.verifyPdfStamp(data, rootHt, signKeyList, Boolean.parseBoolean(pdfCheckSealDate), Boolean.parseBoolean(pdfCheckCertDate));
		pro.setProperty(Constants.RESULT, getValue(String.valueOf(result)));
		return getSucceedResponse();
	}
}
