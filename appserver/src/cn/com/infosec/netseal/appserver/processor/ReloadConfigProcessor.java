package cn.com.infosec.netseal.appserver.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.NetWorkUtil;
import cn.com.infosec.netseal.common.util.license.LicenseChecker;
import cn.com.infosec.netseal.common.util.license.LicenseInfo;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;

/**
 * 重置系统配置 实时生效
 *
 */
@Component("ReloadConfigProcessor")
@Scope("prototype")
public class ReloadConfigProcessor extends BaseProcessor {

	public ReloadConfigProcessor() {
		super("ReloadConfigProcessor");
	}

	public Response process(Request req) {
		boolean result = false;
		// 检查请求数据
		//checkReqDataInvalid(req);

		ConfigUtil config = ConfigUtil.getInstance();
		// 重新加载配置文件
		config.reload();

		// 重置日志
		LoggerConfig.reset();

		LicenseInfo i = LicenseChecker.checkLicense(NetWorkUtil.getHostMac(config.getNetworkCard()));
		if (i == null) {
			throw new NetSealRuntimeException(ErrCode.OPERATE_LICENSE_ERROR, "license info is null");
		}
		config.setLicenseConfig(i.getMaxCertNum(), i.getMaxThred());
		result = true;

		pro.setProperty(Constants.RESULT, getValue(String.valueOf(result)));
		return getSucceedResponse();
	}

}
