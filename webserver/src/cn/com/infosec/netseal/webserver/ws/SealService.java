package cn.com.infosec.netseal.webserver.ws;

import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.dao.certData.CertDataDaoImpl;
import cn.com.infosec.netseal.common.dao.csrData.CsrDataDaoImpl;
import cn.com.infosec.netseal.common.dao.keyData.KeyDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.entity.po.CsrData;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.KeyData;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.vo.CertVO;
import cn.com.infosec.netseal.common.entity.vo.KeyVO;
import cn.com.infosec.netseal.common.entity.vo.SysUserVO;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.BarcodeUtil;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ofd.OfdStampUtil;
import cn.com.infosec.netseal.common.util.pdf.PdfStampUtil;
import cn.com.infosec.netseal.webserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.webserver.service.certChain.CertChainServiceImpl;
import cn.com.infosec.netseal.webserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.webserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.webserver.service.seal.SealServiceImpl;
import cn.com.infosec.netseal.webserver.service.sysUser.SysUserServiceImpl;
import cn.com.infosec.netseal.webserver.ws.entity.json.JsonRequest;
import cn.com.infosec.netseal.webserver.ws.entity.json.JsonResponse;

@WebService(endpointInterface = "cn.com.infosec.netseal.webserver.ws.SealInterface", serviceName = "SealService")
@Component
public class SealService implements SealInterface {

	@Autowired
	private SysUserServiceImpl sysUserService;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private SealServiceImpl sealService;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertDataDaoImpl certDataDao;
	@Autowired
	private KeyDataDaoImpl keyDataDao;
	@Autowired
	private CsrDataDaoImpl csrDataDao;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;

	public String pdfStamp(String request) {
		try {
			// 记录DEBUG日志
			LoggerUtil.debuglog("json request: " + request);

			// 解析Json报文
			JsonRequest req = JSON.parseObject(request, JsonRequest.class);
			String userId = req.getUserId();
			String userPwd = req.getUserPwd();
			String pdfData = req.getPdfData();
			String ofdData = req.getOfdData();
			String certDnServer = req.getCertDnServer();
			String certDn = req.getCertDn();
			String keywords = req.getKeywords();
			String x = req.getX();
			String y = req.getY();
			String pageNum = req.getPageNum();
			String qfz = req.getQfz();
			String bizNum = req.getBiznum();

			// 二维码参数
			String barcodeX = req.getBarcodeX();
			String barcodeY = req.getBarcodeY();
			String barcodeWidth = req.getBarcodeWidth();
			String barcodeContent = req.getBarcodeContent();
			String barcodePageNum = req.getBarcodePageNum();

			byte[] data2Stamp = null;
			// 检查数据值有效性
			if (StringUtil.isNotBlank(pdfData)) {
				checkParamValue(Constants.PDF_DATA, pdfData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
				data2Stamp = StringUtil.base64Decode(pdfData);
			} else if (StringUtil.isNotBlank(ofdData)) {
				checkParamValue(Constants.OFD_DATA, ofdData, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);
				data2Stamp = StringUtil.base64Decode(ofdData);
			} else
				throw new NetSealRuntimeException(ErrCode.NOT_FOUND_DATA_TO_STAMP, "not found data to stamp");

			// 关键字签章、坐标签章、骑缝签章
			if (StringUtil.isNotBlank(keywords)) {
				checkParamValue("KEYWORDS", keywords, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);
				if (StringUtil.isNotBlank(pageNum))
					checkParamValue("PAGENUM", pageNum, Constants.PARAM_TYPE_INT);
			} else if (StringUtil.isNotBlank(x) && StringUtil.isNotBlank(y)) {
				checkParamValue("X", x, Constants.PARAM_TYPE_FLOAT);
				checkParamValue("Y", y, Constants.PARAM_TYPE_FLOAT);
				checkParamValue("PAGENUM", pageNum, Constants.PARAM_TYPE_INT);
			} else if (StringUtil.isNotBlank(qfz)) {
				checkParamValue("QFZ", qfz, Constants.PARAM_TYPE_INT);
			} else
				throw new NetSealRuntimeException(ErrCode.NOT_FOUND_STAMP_PARAM, "not found stamp param(keywords、xy、qfz)");

			checkParamValue(Constants.CERT_DN_SERVER, certDnServer, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_THREE_HUNDRED);
			checkParamValue(Constants.CERT_DN, certDn, Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_THREE_HUNDRED);
			checkParamValue(Constants.PDF_BIZNUM, bizNum, Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_FIFTY);
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

			List<SysUserVO> sysUserVOList = sysUserService.getSysUser(userId);
			if (sysUserVOList.size() == 0)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "user is not exist");
			if (sysUserVOList.size() >= 2)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "user is not the only one");

			SysUserVO sysUserVO = sysUserVOList.get(0);
			if (!CryptoHandler.hashEnc64(StringUtil.base64Decode(userPwd)).equals(sysUserVO.getPassword()))
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "pwd is wrong");

			// 获取服务器密钥
			Key key = new Key();
			if (StringUtil.isNotBlank(certDnServer)) {
				certDnServer = CertUtil.transCertDn(certDnServer);
				// 获取服务器密钥
				List<Key> list = keyService.getKeys(certDnServer);
				if (list == null || list.size() == 0)
					throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "key is not exist in db, server cert dn is " + certDnServer);
				key = list.get(0);
			} else {
				// 获取指定签名密钥
				ConfigUtil config = ConfigUtil.getInstance();
				long signKeyId = config.getSignKeyId();
				if (signKeyId == -1)
					throw new NetSealRuntimeException(ErrCode.SERVERCERT_NOT_SET, "key is not set ");

				KeyVO keyVo = keyService.getKey(signKeyId);
				if (keyVo == null)
					throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "key is not exist in db, server cert dn is " + certDnServer);
				BeanUtils.copyProperties(keyVo, key);
			}

			// 校验密钥信息
			keyService.isModify(key);

			// 初始化服务器证书数据
			initKeyFile(key);

			// 校验服务器证书
			certChainService.verifyCert(FileUtil.getFile(key.getCertPath()));
			// 查询证书
			certDn = CertUtil.transCertDn(certDn);
			List<CertVO> certList = certService.getCert(certDn, Constants.USAGE_SIGNATURE);
			List<CertVO> certListEnc = certService.getCert(certDn, Constants.USAGE_SIGN_ENC);
			if (certList.size() == 0 && certListEnc.size() == 0)
				throw new NetSealRuntimeException(ErrCode.CERT_NOT_EXIST_IN_DB, "user cert not exist");
			if (certList.size() >= 2 || certListEnc.size() >= 2)
				throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "the number of cert is more than one");

			// 查询印章
			Seal seal = new Seal();
			CertVO certVo = certList.get(0);
			Cert cert = new Cert();
			BeanUtils.copyProperties(certVo, cert);
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
			byte[] photoData = photoDataService.getPhotoData(seal.getPhotoPath(), seal.getPhotoDataId());
			byte[] sealData = sealService.getSealData(seal.getSealPath(), seal.getSealDataId());
			byte[] keyData = FileUtil.getFile(key.getKeyPath());
			byte[] keyCertData = FileUtil.getFile(key.getCertPath());

			// 生成二维码图片 插入二维码到PDF文件
			if (StringUtil.isNotBlank(barcodeContent) && StringUtil.isNotBlank(pdfData)) {
				byte[] barcodeBs = BarcodeUtil.genImage(barcodeContent);
				float tempBarcodeWidth = Float.parseFloat(barcodeWidth);
				data2Stamp = PdfStampUtil.pdfAddImage(data2Stamp, barcodeBs, Integer.parseInt(barcodePageNum), Float.parseFloat(barcodeX), Float.parseFloat(barcodeY), tempBarcodeWidth,
						tempBarcodeWidth);
			}

			// 获取根证集合
			Hashtable<String, X509Certificate> rootHt = certChainService.getCertChainHt();
			// 获取服务器加密卡签名证书
			List<Key> signKeyList = keyService.getCardKeys();

			byte[] stampData = null;
			// 1. PDF 盖章
			if (StringUtil.isNotBlank(pdfData)) {
				// 1.1 关键字盖章
				if (StringUtil.isNotBlank(keywords)) {
					if (StringUtil.isNotBlank(pageNum))
						stampData = PdfStampUtil.pdfStampByText(data2Stamp, photoData, Integer.parseInt(pageNum), keywords, bizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
					else
						stampData = PdfStampUtil.pdfStampByText(data2Stamp, photoData, keywords, bizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
				}
				// 1.2 坐标签章
				else if (StringUtil.isNotBlank(x) && StringUtil.isNotBlank(y))
					stampData = PdfStampUtil.pdfStampByCoordinate(data2Stamp, photoData, Integer.parseInt(pageNum), Float.parseFloat(x), Float.parseFloat(y), bizNum, key, keyData, keyCertData,
							sealData, rootHt, signKeyList);
				// 1.3 骑缝签章
				else if (StringUtil.isNotBlank(qfz))
					stampData = PdfStampUtil.pdfStampByQfz(data2Stamp, photoData, bizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList, Integer.parseInt(qfz));
			}
			// 2. OFD 盖章
			else if (StringUtil.isNotBlank(ofdData)) {
				// 2.1 关键字盖章
				if (StringUtil.isNotBlank(keywords)) {
					if (StringUtil.isNotBlank(pageNum))
						stampData = OfdStampUtil.ofdStampByText(data2Stamp, Integer.parseInt(pageNum), keywords, bizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
					else
						stampData = OfdStampUtil.ofdStampByText(data2Stamp, keywords, bizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
				}
				// 2.2 坐标签章
				else if (StringUtil.isNotBlank(x) && StringUtil.isNotBlank(y))
					stampData = OfdStampUtil.ofdStampByCoordinate(data2Stamp, Integer.parseInt(pageNum), Float.parseFloat(x), Float.parseFloat(y), bizNum, key, keyData, keyCertData, sealData, rootHt,
							signKeyList);
				// 2.3 骑缝签章
				else if (StringUtil.isNotBlank(qfz))
					stampData = OfdStampUtil.ofdStampByQfz(data2Stamp, bizNum, key, keyData, keyCertData, sealData, rootHt, signKeyList, Integer.parseInt(qfz));
			}

			// 更新印章使用次数
			if (seal.getUsedLimit() != 0)
				if (sealService.updateSealCount(seal.getId(), currentTime) == 0)
					throw new NetSealRuntimeException(ErrCode.SEAL_OVER_USED_LIMIT, "seal use times exceed the limit");

			String result = process("SUCCES", StringUtil.base64Encode(stampData));
			LoggerUtil.debuglog("json response: " + result);

			return result;
		} catch (Exception e) {
			String result = process("ERROR", e.getMessage());
			LoggerUtil.debuglog("json response: " + result);
			LoggerUtil.errorlog("webservice data stamp error, ", e);

			return result;
		}
	}

	private String process(String msg, String result) {
		JsonResponse res = new JsonResponse();
		res.setMsg(msg);
		res.setResult(result);

		return JSON.toJSONString(res);
	}

	/**
	 * 校验参数值有效性
	 * 
	 * @param name
	 * @param value
	 * @param type
	 */
	protected void checkParamValue(String name, String value, String type) {
		checkParamValue(name, value, type, Constants.DEFAULT_INT);
	}

	/**
	 * 校验参数值有效性
	 * 
	 * @param name
	 * @param value
	 * @param type
	 */
	private void checkParamValue(String name, String value, String type, long lenLimit) {
		switch (type) {
		case Constants.PARAM_TYPE_STRING_NOT_NULL:
			if (value == null)
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_ISNULL, name + " value is null");

			if ("".equals(value.trim()))
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_ISEMPTY, name + " value is empty");

			if (getBytes(value).length > lenLimit)
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_LEN_OVER_LIMIT, name + " value len is over limit " + lenLimit);
			break;

		case Constants.PARAM_TYPE_STRING_NULLABLE:
			if (StringUtil.isNotBlank(value))
				if (getBytes(value).length > lenLimit)
					throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_LEN_OVER_LIMIT, name + " value len is over limit " + lenLimit);
			break;

		case Constants.PARAM_TYPE_INT:
			try {
				Integer.parseInt(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_LONG:
			try {
				Long.parseLong(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_DATE:
			try {
				if (!DateUtil.checkDateValid(value))
					throw new Exception();
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_FLOAT:
			try {
				Float.parseFloat(value);
			} catch (Exception e) {
				throw new NetSealRuntimeException(ErrCode.PARAM_VALUE_INVALID, name + " value is invalid");
			}
			break;

		default:
			break;
		}
	}

	private byte[] getBytes(String key) {
		return StringUtil.getBytes(key);
	}

	private void initKeyFile(Key key) {
		if (!FileUtil.checkPath(key.getCertPath())) {
			CertData certData = certDataDao.getCertData(key.getCertDataId());
			if (certData == null)
				throw new NetSealRuntimeException(ErrCode.CERT_DATA_NOT_EXIST_IN_DB, "cert data is not exist in db, cert data id is " + key.getCertDataId());

			// 校验证书数据信息
			if (!certData.calMac().equals(certData.getMac()))
				throw new NetSealRuntimeException(ErrCode.MAC_INVALID, "cert data info had been tampered with, the cert_data id is " + certData.getId());

			byte[] data = certData.getData();
			FileUtil.storeFile(key.getCertPath(), data);
		}

		String keyPath = key.getKeyPath();
		if (StringUtil.isNotBlank(keyPath)) {
			if (!FileUtil.checkPath(keyPath)) {
				KeyData keyData = keyDataDao.getKeyData(key.getKeyDataId());
				if (keyData == null)
					throw new NetSealRuntimeException(ErrCode.KEY_DATA_NOT_EXIST_IN_DB, "key data is not exist in db, key data id is " + key.getKeyDataId());

				// 校验密钥数据信息
				if (!(keyData.calMac()).equals(keyData.getMac()))
					throw new NetSealRuntimeException(ErrCode.MAC_INVALID, "key data info had been tampered with, the key_data id is " + keyData.getId());

				byte[] data = keyData.getData();
				FileUtil.storeFile(keyPath, data);
			}
		}

		String csrPath = key.getCsrPath();
		if (StringUtil.isNotBlank(csrPath)) {
			if (!FileUtil.checkPath(csrPath)) {
				// KeyData keyData = keyDataDao.getKeyData(key.getKeyDataId());
				CsrData csrData = csrDataDao.getCsrData(key.getCsrDataId());
				if (csrData == null)
					throw new NetSealRuntimeException(ErrCode.KEY_DATA_NOT_EXIST_IN_DB, "key data is not exist in db, key data id is " + key.getKeyDataId());

				// 校验密钥数据信息
				if (!(csrData.calMac()).equals(csrData.getMac()))
					throw new NetSealRuntimeException(ErrCode.MAC_INVALID, "key data info had been tampered with, the key_data id is " + csrData.getId());

				byte[] data = csrData.getData();
				FileUtil.storeFile(csrPath, data);
			}
		}
	}

	public static void main(String[] args) {
		// JsonRequest req = new JsonRequest();
		// req.setUserId("test");
		// req.setUserPwd("123456");
		// req.setCertDn("C=cn,CN=test");
		// req.setKeywords("关键字");
		// System.out.println(JSON.toJSON(req));

		String json = "{\"keywords\":\"关键字\",\"userPwd\":\"123456\",\"userId\":\"test\",\"certDn\":\"C=cn,CN=test\",\"KEYWORDS\":\"123\"}";
		JsonRequest req = JSON.parseObject(json, JsonRequest.class);
		System.out.println(req.getKeywords());
	}

}
