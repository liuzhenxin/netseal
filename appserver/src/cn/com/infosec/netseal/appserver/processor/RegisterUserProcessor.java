package cn.com.infosec.netseal.appserver.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.appserver.service.company.CompanyServiceImpl;
import cn.com.infosec.netseal.appserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

/**
 * 注册签章人
 */
@Component("RegisterUserProcessor")
@Scope("prototype")
public class RegisterUserProcessor extends BaseProcessor {

	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private CompanyServiceImpl companyService;

	public RegisterUserProcessor() {
		super("RegisterUserProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String userName = getValue(reqdata, Constants.USER_NAME);
		String certData = getValue(reqdata, Constants.CERT_DATA);
		String companyName = getValue(reqdata, Constants.COMPANY_NAME);

		// 检查数据值有效性
		checkParamValue(Constants.USER_NAME, userName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.CERT_DATA, certData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
		checkParamValue(Constants.COMPANY_NAME, companyName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_TWO_HUNDRED);

		// 校验机构
		String[] companyNameArr = companyName.split(Constants.SPLIT_1);
		boolean isMatch = false;
		long companyID = 0;

		for (int i = 0; i < companyNameArr.length; i++) {
			isMatch = false;
			List<Company> companyList = companyService.getSubCompanys(companyID);
			for (int j = 0; j < companyList.size(); j++) {
				Company company = companyList.get(j);
				if (companyNameArr[i].equals(company.getName())) {
					isMatch = true;
					companyID = company.getId();
					break;
				}
			}
			if (!isMatch)
				throw new NetSealRuntimeException(ErrCode.COMPANY_TREE_NOT_EXIST_IN_DB, "company tree data not exist");
		}
		Company company = companyService.getCompany(companyID);
		if (company == null)
			throw new NetSealRuntimeException(ErrCode.COMPANY_NOT_EXIST_IN_DB, "company data not exist");

		companyService.isModify(company);
		// 校验用户名
		List<User> userList = userService.getUser(userName, companyID);

		if (userList.size() > 1)
			throw new NetSealRuntimeException(ErrCode.USER_NOT_UNIQUE_IN_DB, "the number of user is more than one");

		List<Cert> certListReg = new ArrayList<Cert>();
		boolean insertUser = false;// 是否是新用户
		long currentTime = DateUtil.getCurrentTime();
		// 处理多个证书
		String[] certDataAll = certData.split(Constants.SPLIT_1);
		for (String tempCertData : certDataAll) {
			byte[] data = StringUtil.base64Decode(tempCertData);
			X509CertEnvelope certEnv = CertUtil.parseCert(data);

			String certDn = certEnv.getCertDn();
			String certIssueDn = certEnv.getCertIssueDn();
			String certSn = certEnv.getCertSn();

			checkParamValue(Constants.CERT_DN, certDn, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
			checkParamValue(Constants.CERT_ISSUE_DN, certIssueDn, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);

			List<Cert> certList = certService.getCertAll(certDn);
			if (certList.size() > 0)
				throw new NetSealRuntimeException(ErrCode.CERT_ALREADY_IN_USED, "cert already in used");

			if (certSn != null)
				certSn = certSn.toUpperCase();

			Cert cert = new Cert();
			// 密钥用法
			boolean isCheckSignCert = false;
			boolean isCheckEncCert = false;
			boolean[] usage = certEnv.getKeyUsage();
			if (usage != null && usage[0]) {// 签名证书
				isCheckSignCert = true;
				cert.setCertUsage(Constants.USAGE_SIGNATURE);
			}
			if (usage != null && usage[2] && usage[3]) {// 加密证书
				isCheckEncCert = true;
				if (isCheckSignCert)// 两种用途都是
					cert.setCertUsage(Constants.USAGE_SIGN_ENC);
				else
					cert.setCertUsage(Constants.USAGE_ENCRYPT);
			}
			if (usage == null || (!isCheckSignCert && !isCheckEncCert)) {// 不是签名 又不是加密证书
				throw new NetSealRuntimeException(ErrCode.UNKNOWN_USAGE, "cert usage unknown");
			}
			// 判断证书重复
			for (Cert tempCert : certListReg) {
				String tempCertDn = tempCert.getCertDn();
				Integer tempUsage = tempCert.getCertUsage();
				if (tempCertDn.equals(certDn)) {
					if (tempUsage.equals(cert.getCertUsage())) {// 两个同样用途的证书
						throw new NetSealRuntimeException(ErrCode.CERT_USAGE_HAS_EXIST, "cert usage has exist");
					} else if (tempUsage == Constants.USAGE_SIGN_ENC) {// 签名加密证书
						throw new NetSealRuntimeException(ErrCode.CERT_USAGE_HAS_EXIST, "cert usage has exist");
					}
				}
			}

			// 验证证书有效性
			certChainService.verifyCert(data, isCheckSignCert, isCheckEncCert, true);
			cert.setCertSn(certSn);
			cert.setCertDn(certDn);
			cert.setCertIssueDn(certIssueDn);
			cert.setGenerateTime(currentTime);
			cert.setUpdateTime(currentTime);
			CertData cd = new CertData();
			cd.setGenerateTime(currentTime);
			cd.setUpdateTime(currentTime);
			cd.setData(StringUtil.base64Decode(tempCertData));
			cert.setCertData(cd);

			certListReg.add(cert);
		}

		User user;
		// 新增签章人
		if (userList.size() == 0) {
			user = new User();
			user.setName(userName);
			user.setPassword(Constants.SYS_USER_DEFAULT_PWD);
			user.setCompanyId(companyID);
			user.setGenerateTime(currentTime);
			user.setUpdateTime(currentTime);
			insertUser = true;
		} else {// 已存在,追加证书
			user = userList.get(0);
		}

		// 保存数据
		userService.insertUser(user, certListReg, insertUser);

		return getSucceedResponse();
	}
}
