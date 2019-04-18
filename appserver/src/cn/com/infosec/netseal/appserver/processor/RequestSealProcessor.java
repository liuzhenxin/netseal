package cn.com.infosec.netseal.appserver.processor;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.com.infosec.netseal.appserver.base.BaseProcessor;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.company.CompanyServiceImpl;
import cn.com.infosec.netseal.appserver.service.request.RequestServiceImpl;
import cn.com.infosec.netseal.appserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.appserver.service.template.TemplateServiceImpl;
import cn.com.infosec.netseal.appserver.service.user.UserServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.dao.userTemplate.UserTemplateDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.entity.po.UserTemplate;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;

/**
 * 申请印章
 */
@Component("RequestSealProcessor")
@Scope("prototype")
public class RequestSealProcessor extends BaseProcessor {

	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private TemplateServiceImpl templateService;
	@Autowired
	private CompanyServiceImpl companyService;
	@Autowired
	private RequestServiceImpl requestService;
	@Autowired
	protected UserTemplateDaoImpl userTemplateDao;

	public RequestSealProcessor() {
		super("RequestSealProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String sealName = getValue(reqdata, Constants.SEAL_NAME);
		String templateName = getValue(reqdata, Constants.TEMPLATE_NAME);
		String certDN = getValue(reqdata, Constants.CERT_DN);
		String photoDataB64 = getValue(reqdata, Constants.PHOTO_DATA);

		// 检查数据值有效性
		checkParamValue(Constants.SEAL_NAME, sealName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.TEMPLATE_NAME, templateName, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.CERT_DN, certDN, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
		checkParamValue(Constants.PHOTO_DATA, photoDataB64, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_20KB_B64); // 20KB

		certDN = CertUtil.transCertDn(certDN);
		
		List<Cert> certList = certService.getCert(certDN, Constants.USAGE_SIGNATURE);
		List<Cert> certListEnc = certService.getCert(certDN, Constants.USAGE_SIGN_ENC);
		if (certList.size() == 0 && certListEnc.size() == 0)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_EXIST_IN_DB, "user cert not exist");
		if (certList.size() >= 2 || certListEnc.size() >= 2)
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "the number of cert is more than one");
		Cert cert = certList.get(0);
		// 校验证书信息
		certService.isModify(cert);

		User user = userService.getUser(cert.getUserId());
		if (user == null)
			throw new NetSealRuntimeException(ErrCode.USER_NOT_EXIST_IN_DB, "user not exist in db");
		// 校验用户信息
		userService.isModify(user);

		List<cn.com.infosec.netseal.common.entity.po.Request> requestList = requestService.getRequesList(sealName);
		// 已存在申请
		if (requestList != null && requestList.size() > 0)
			throw new NetSealRuntimeException(ErrCode.REQUEST_HAS_EXIST_IN_DB, "seal request has exist in db");
		// 校验印章名
		Seal seal = sealService.getSeal(sealName);
		if (seal != null)
			throw new NetSealRuntimeException(ErrCode.SEAL_HAS_EXIST_IN_DB, "seal data already exist");

		// 校验印模名
		List<Template> templateList = templateService.getTemplate(templateName);
		if (templateList == null || templateList.size() == 0)
			throw new NetSealRuntimeException(ErrCode.TEMPATE_NOT_EXIST_IN_DB, "template data not exist");
		// 印模在库中个数大于1
		if (templateList.size() > 1)
			throw new NetSealRuntimeException(ErrCode.TEMPATE_NOT_UNIQUE_IN_DB, "template in db is not unique, num is " + templateList.size());

		Template template = templateList.get(0);
		// 校验印模信息
		templateService.isModify(template);

		// 校验印模状态
		if (template.getStatus() == 0)
			throw new NetSealRuntimeException(ErrCode.STATUS_DISABLE, "template status is disable");

		// 印模已过期
		if (DateUtil.getCurrentTime() > template.getNotAfter())
			throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "template not in the period of validity");

		// 校验证书DN
		if (!userService.checkUserCertDn(user, certDN))
			throw new NetSealRuntimeException(ErrCode.CERT_DN_NOT_MATCH, "user cert DN is not match regedit cert DN");

		// 一张证书只能生成一次
		Seal tempSeal = new Seal();
		tempSeal.setCertId(cert.getId());
		List<Seal> sealList = sealService.getSeal(tempSeal);
		if (sealList != null && sealList.size() > 0)
			throw new NetSealRuntimeException(ErrCode.SEAL_HAS_EXIST_BY_CERT, "seal has been generated by this certDN");

		// 一张证书只能申请一次
		cn.com.infosec.netseal.common.entity.po.Request tempRequest = requestService.getRequestByCert(cert.getId());
		if (tempRequest != null)
			throw new NetSealRuntimeException(ErrCode.REQUEST_HAS_EXIST_BY_CERT, "request has been generated by this certDN");

		// 校验使用印模权限
		if (template.getCompanyId() != 0) {
			List<Long> list = companyService.getSubCompanysId(template.getCompanyId());
			if (!list.contains(user.getCompanyId()))
				throw new NetSealRuntimeException(ErrCode.COMPANY_NO_PERMISSION_TEMPLATE, "company no permission to use template");
		}

		// 校验使用印模权限
		List<UserTemplate> userTemplateList = userTemplateDao.getUserTemplate(template.getId());
		boolean isPermission = false;
		for (UserTemplate tempUT : userTemplateList) {

			if (!(tempUT.calMac()).equals(tempUT.getMac()))
				throw new NetSealRuntimeException(ErrCode.MAC_INVALID, "user_template info had been tampered with, the user_template id is " + tempUT.getId());

			Long userId = tempUT.getUserId();
			if (userId == 0 || userId.equals(user.getId())) {
				isPermission = true;
				break;
			}
		}
		if (!isPermission)
			throw new NetSealRuntimeException(ErrCode.USER_NO_PERMISSION_TEMPLATE, "user no permission to use template");

		// // 图片不为空
		// if (StringUtil.isNotBlank(photoDataB64)) {
		// // 转换图片背景色
		// byte[] bs =
		// SealPicUtil.convertBackgroundColor(StringUtil.base64Decode(photoDataB64));
		// photoDataB64 = StringUtil.base64Encode(bs);
		// }

		// 创建POJO类
		cn.com.infosec.netseal.common.entity.po.Request request = new cn.com.infosec.netseal.common.entity.po.Request();
		request.setName(sealName);
		request.setTemplateId(template.getId());
		request.setTransparency(template.getTransparency());
		request.setUserId(user.getId());
		request.setCertId(cert.getId());
		request.setNotAfter(template.getNotAfter());
		request.setNotBefor(template.getNotBefor());

		// 保存数据
		requestService.insertRequest(request, photoDataB64, template, cert);
		return getSucceedResponse();
	}
}
