package cn.com.infosec.netseal.appserver.processor;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.company.CompanyServiceImpl;
import cn.com.infosec.netseal.appserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.appserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;

/**
 * 更新印章图片
 */
@Component("UpdateSealPhotoProcessor")
@Scope("prototype")
public class UpdateSealPhotoProcessor extends BaseProcessor {

	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private CompanyServiceImpl companyService;

	public UpdateSealPhotoProcessor() {
		super("UpdateSealPhotoProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String userName = getValue(reqdata, Constants.USER_NAME);
		String certDN = getValue(reqdata, Constants.CERT_DN);
		String companyName = getValue(reqdata, Constants.COMPANY_NAME);
		String photoDataB64 = getValue(reqdata, Constants.PHOTO_DATA);

		// 检查数据值有效性
		checkParamValue(Constants.USER_NAME, userName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.CERT_DN, certDN, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
		checkParamValue(Constants.COMPANY_NAME, companyName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_TWO_HUNDRED);
		checkParamValue(Constants.PHOTO_DATA, photoDataB64, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_20KB_B64); // 20KB
		
		certDN = CertUtil.transCertDn(certDN);

		// 校验机构(1)
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

		// 校验机构(2)
		Company company = companyService.getCompany(companyID);
		if (company == null)
			throw new NetSealRuntimeException(ErrCode.COMPANY_NOT_EXIST_IN_DB, "company data not exist");
		
		// 校验信息
		companyService.isModify(company);

		// 查询证书
		List<Cert> certList = certService.getCert(certDN, Constants.USAGE_SIGNATURE);
		List<Cert> certListEnc = certService.getCert(certDN, Constants.USAGE_SIGN_ENC);
		if (certList.size() == 0 && certListEnc.size() == 0)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_EXIST_IN_DB, "user cert not exist");
		if (certList.size() >= 2 || certListEnc.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "the number of cert is more than one");

		Cert cert = certList.get(0);
		// 校验证书信息
		certService.isModify(cert);

		// 查询签章人
		List<User> userList = userService.getUser(userName, companyID, cert.getId());
		if (userList.size() == 0)
			throw new NetSealRuntimeException(ErrCode.USER_NOT_EXIST_IN_DB, "user not exist in db");
		
		if (userList.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.USER_NOT_UNIQUE_IN_DB, "the number of user is more than one");
		
		User user = userList.get(0);
		// 校验签章人信息
		userService.isModify(user);

		Seal s = new Seal();
		s.setUserId(user.getId());
		s.setCertId(cert.getId());

		List<Seal> sealList = sealService.getSeal(s);
		if (sealList.size() == 0)
			throw new NetSealRuntimeException(ErrCode.SEAL_NOT_EXIST_IN_DB, "seal not exist in db");
		
		else if (sealList.size() == 1) {
			// // 转换图片背景色
			// byte[] bs = SealPicUtil.convertBackgroundColor(StringUtil.base64Decode(photoDataB64));
			// photoDataB64 = StringUtil.base64Encode(bs);

			// 保存数据
			Seal seal = sealList.get(0);
			// 校验印章信息
			sealService.isModify(seal);
			
			if (seal.getType() == 3)
				throw new NetSealRuntimeException(ErrCode.UPDATE_SEAL_PHOTO_ERROR, "Handwritten seal cannot update images.");

			sealService.updateSeal(seal, cert, photoDataB64, company);
			return getSucceedResponse();
		} else
			throw new NetSealRuntimeException(ErrCode.CERT_GEN_SEAL_NUM_ERROR, "the number of seal produced by the cert is more than one");
	}

}
