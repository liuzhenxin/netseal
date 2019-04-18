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
import cn.com.infosec.netseal.appserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.appserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.communication.message.Response;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.BarcodeUtil;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.pdf.PdfStampUtil;

/**
 * PDF盖章
 *
 */
@Component("PdfStampProcessor")
@Scope("prototype")
public class PdfStampProcessor extends BaseProcessor {

	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;

	public PdfStampProcessor() {
		super("PdfStampProcessor");
	}

	public Response process(Request req) {

		// 检查请求数据
		checkReqDataInvalid(req);
		Properties reqdata = req.getData();

		String pdfData64 = getValue(reqdata, Constants.PDF_DATA);
		String serverCertDN = getValue(reqdata, Constants.CERT_DN_SERVER);
		String certDN = getValue(reqdata, Constants.CERT_DN);
		String photoData64 = getValue(reqdata, Constants.PHOTO_DATA);
		String pdfPageNum = getValue(reqdata, Constants.PDF_PAGENUM);
		String pdfX = getValue(reqdata, Constants.PDF_X);
		String pdfY = getValue(reqdata, Constants.PDF_Y);
		String pdfKeywords = getValue(reqdata, Constants.PDF_KEYWORDS);
		String pdfBizNum = getValue(reqdata, Constants.PDF_BIZNUM);
		String pdfQfz = getValue(reqdata, Constants.PDF_QFZ);

		// 二维码参数
		String barcodeX = getValue(reqdata, Constants.PDF_BARCODE_X);
		String barcodeY = getValue(reqdata, Constants.PDF_BARCODE_Y);
		String barcodeWidth = getValue(reqdata, Constants.PDF_BARCODE_WIDTH);
		String barcodeContent = getValue(reqdata, Constants.PDF_BARCODE_CONTENT);
		String barcodePageNum = getValue(reqdata, Constants.PDF_BARCODE_PAGENUM);

		// 检查数据值有效性
		// 关键字签章、坐标签章、骑缝签章
		if (StringUtil.isNotBlank(pdfKeywords)) {
			checkParamValue(Constants.PDF_KEYWORDS, pdfKeywords, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);
			if (StringUtil.isNotBlank(pdfPageNum))
				checkParamValue(Constants.PDF_PAGENUM, pdfPageNum, Constants.PARAM_TYPE_INT);
		} else if (StringUtil.isNotBlank(pdfX) && StringUtil.isNotBlank(pdfY)) {
			checkParamValue(Constants.PDF_X, pdfX, Constants.PARAM_TYPE_FLOAT);
			checkParamValue(Constants.PDF_Y, pdfY, Constants.PARAM_TYPE_FLOAT);
			checkParamValue(Constants.PDF_PAGENUM, pdfPageNum, Constants.PARAM_TYPE_INT);
		} else if (StringUtil.isNotBlank(Constants.PDF_QFZ)) {
			checkParamValue(Constants.PDF_QFZ, pdfQfz, Constants.PARAM_TYPE_INT);
		} else
			throw new NetSealRuntimeException(ErrCode.NOT_FOUND_STAMP_PARAM, "not found stamp param(keywords、xy、qfz)");

		checkParamValue(Constants.PDF_DATA, pdfData64, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
		checkParamValue(Constants.CERT_DN_SERVER, serverCertDN, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_THREE_HUNDRED);
		checkParamValue(Constants.CERT_DN, certDN, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
		checkParamValue(Constants.PDF_BIZNUM, pdfBizNum, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);
		checkParamValue(Constants.PDF_BARCODE_CONTENT, barcodeContent, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);

		// 二维码内容不为空
		if (StringUtil.isNotBlank(barcodeContent)) {
			checkParamValue(Constants.PDF_BARCODE_X, barcodeX, Constants.PARAM_TYPE_FLOAT);
			checkParamValue(Constants.PDF_BARCODE_Y, barcodeY, Constants.PARAM_TYPE_FLOAT);
			checkParamValue(Constants.PDF_BARCODE_CONTENT, barcodeContent, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_FIFTY);

			if (StringUtil.isNotBlank(barcodeWidth))
				checkParamValue(Constants.PDF_BARCODE_WIDTH, barcodeWidth, Constants.PARAM_TYPE_FLOAT);
			else
				barcodeWidth = "120";

			if (StringUtil.isNotBlank(barcodePageNum))
				checkParamValue(Constants.PDF_BARCODE_PAGENUM, barcodePageNum, Constants.PARAM_TYPE_INT);
			else
				barcodePageNum = "1";
		}

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

		// 校验服务器证书
		certChainService.verifyCert(FileUtil.getFile(key.getCertPath()));

		byte[] pdfData = StringUtil.base64Decode(pdfData64);
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

		// 印章图片
		byte[] photoData = null;
		if (seal.getType() == 3) {
			checkParamValue(Constants.PHOTO_DATA, photoData64, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_20KB_B64);
			photoData = StringUtil.base64Decode(photoData64);
		} else
			photoData = photoDataService.getPhotoData(seal.getPhotoPath(), seal.getPhotoDataId());

		byte[] sealData = sealService.getSealData(seal.getSealPath(), seal.getSealDataId());
		byte[] keyData = FileUtil.getFile(key.getKeyPath());
		byte[] keyCertData = FileUtil.getFile(key.getCertPath());

		// 生成二维码图片 插入二维码到PDF文件
		if (StringUtil.isNotBlank(barcodeContent)) {
			byte[] barcodeBs = BarcodeUtil.genImage(barcodeContent);
			float tempBarcodeWidth = Float.parseFloat(barcodeWidth);
			pdfData = PdfStampUtil.pdfAddImage(pdfData, barcodeBs, Integer.parseInt(barcodePageNum), Float.parseFloat(barcodeX), Float.parseFloat(barcodeY), tempBarcodeWidth, tempBarcodeWidth);
		}

		// 获取根证集合
		Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();
		// 获取服务器加密卡签名证书
		List<Key> signKeyList = keyService.getCardKeys();

		// PDF盖章
		byte[] pdfDataStampBs = null;
		// 1. 关键字盖章
		if (StringUtil.isNotBlank(pdfKeywords)) {
			if (StringUtil.isNotBlank(pdfPageNum))
				pdfDataStampBs = PdfStampUtil.pdfStampByText(pdfData, photoData, Integer.parseInt(pdfPageNum), pdfKeywords, pdfBizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
			else
				pdfDataStampBs = PdfStampUtil.pdfStampByText(pdfData, photoData, pdfKeywords, pdfBizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
		}
		// 2. 坐标签章
		else if (StringUtil.isNotBlank(pdfX) && StringUtil.isNotBlank(pdfY))
			pdfDataStampBs = PdfStampUtil.pdfStampByCoordinate(pdfData, photoData, Integer.parseInt(pdfPageNum), Float.parseFloat(pdfX), Float.parseFloat(pdfY), pdfBizNum, key, keyData, keyCertData,
					sealData, rootHt, signKeyList);
		// 3. 骑缝签章
		else if (StringUtil.isNotBlank(pdfQfz))
			pdfDataStampBs = PdfStampUtil.pdfStampByQfz(pdfData, photoData, pdfBizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList, Integer.parseInt(pdfQfz));

		// 更新印章使用次数
		if (seal.getUsedLimit() != 0)
			if (sealService.updateSealCount(seal.getId(), currentTime) == 0)
				throw new NetSealRuntimeException(ErrCode.SEAL_OVER_USED_LIMIT, "seal use times exceed the limit");

		pro.setProperty(Constants.PDF_DATA_STAMP, getValue(StringUtil.base64Encode(pdfDataStampBs)));
		return getSucceedResponse();
	}
}
