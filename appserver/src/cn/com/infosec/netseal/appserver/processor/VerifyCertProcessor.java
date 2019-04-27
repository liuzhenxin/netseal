package cn.com.infosec.netseal.appserver.processor;

import java.util.Properties;

import cn.com.infosec.netseal.appserver.service.certChain.CertChainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.StringUtil;

@Component("VerifyCertProcessor")
@Scope("prototype")
public class VerifyCertProcessor extends BaseProcessor {

	@Autowired
	private CertChainServiceImpl certChainService;

	public VerifyCertProcessor() {
		super("VerifyCertProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String certData = getValue(reqdata, Constants.CERT_DATA);

		// 检查数据值有效性
		checkParamValue(Constants.CERT_DATA, certData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		// 证书解码
		byte[] data = StringUtil.base64Decode(certData);

		// 验证证书有效性
		certChainService.verifyCert(data);

		pro.setProperty(Constants.RESULT, "true");
		return getSucceedResponse();
	}
}
