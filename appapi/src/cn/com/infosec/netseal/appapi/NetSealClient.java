package cn.com.infosec.netseal.appapi;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import cn.com.infosec.netseal.appapi.common.communication.UnProtocolCommunicator;
import cn.com.infosec.netseal.appapi.common.communication.message.Request;
import cn.com.infosec.netseal.appapi.common.communication.message.Response;
import cn.com.infosec.netseal.appapi.common.communication.message.impl.bytes.ByteRequest;
import cn.com.infosec.netseal.appapi.common.define.Constants;
import cn.com.infosec.netseal.appapi.common.util.CertUtil;
import cn.com.infosec.netseal.appapi.common.util.FileUtil;
import cn.com.infosec.netseal.appapi.common.util.KeyStoreUtil;
import cn.com.infosec.netseal.appapi.common.util.NetSignUtil;
import cn.com.infosec.netseal.appapi.common.util.SSLSocketFactoryUtil;
import cn.com.infosec.netseal.appapi.common.util.StringUtil;
import cn.com.infosec.netseal.appapi.common.util.socketpool.NewSocketConnectionPool;

public class NetSealClient {

	private static SysProperty pro;
	private static PrintWriter logWriter;
	private static Object logSync = new Object();

	private static boolean isSSL;
	private static boolean isEnvelope;

	private static X509Certificate serverCert;
	private static String clientCertDn;
	private static PrivateKey priKey;

	/**
	 * 设置输出记录器
	 * 
	 * @param out
	 *            记录输出流
	 */
	public final static void setLogWriter(PrintWriter out) {
		synchronized (logSync) {
			logWriter = out;
		}
	}

	/**
	 * 返回输出流
	 * 
	 * @return API所使用的输出流
	 */
	public static java.io.PrintWriter getLogWriter() {
		synchronized (logSync) {
			return logWriter;
		}
	}

	/**
	 * 设置验证API配置信息
	 * 
	 * @param property
	 *            系统属性
	 * @throws AppApiException
	 *             如果属性为null则抛出异常
	 */
	public final static void setAPIProperty(SysProperty property) throws AppApiException {
		if (property == null) {
			int errno = ErrCode.API_PROPERTY_IS_NULL;
			String msg = "API Property is null";
			println("errno=" + errno + ", errmsg=" + msg);
			throw new AppApiException(errno, ErrMsg.getErrMsg(errno));
		}

		NetSealClient.pro = property;

		// SSL 配置
		String truststore = pro.getTrustStore().trim();
		String truststorepass = pro.getTrustStorePwd().trim();
		String keystore = pro.getClientStore().trim();
		String keypass = pro.getClientStorePwd().trim();

		// 数字信封配置
		String serverCertPath = pro.getServerCertPath();
		String clientCertPath = pro.getClientCertPath();
		String clientKeyPath = pro.getClientKeyPath();
		String clientKeyPwd = pro.getClientKeyPwd();

		SSLSocketFactory ssf = null;
		if (truststore.length() > 0) { // 配置了TrustStore,使用SSL通讯
			isSSL = true;
			Properties pro = new Properties();
			pro.setProperty("javax.net.ssl.trustStore", truststore);
			pro.setProperty("javax.net.ssl.trustStorePassword", truststorepass);
			if (keystore.length() > 0) { // 使用双向SSL
				pro.setProperty("javax.net.ssl.keyStore", keystore);
				pro.setProperty("javax.net.ssl.keyStorePassword", keypass);
			}

			try {
				SSLSocketFactoryUtil ssfu = new SSLSocketFactoryUtil();
				ssf = ssfu.getSSLSocketFactory(pro);
			} catch (Exception e) {
				int errno = ErrCode.SSL_STORE_ERROR;
				String msg = "ssl store file or password is invalid";
				println("errno=" + errno + ",errmsg=" + msg);
				println(e);
				throw new AppApiException(errno, ErrMsg.getErrMsg(errno));
			}
		} else if (serverCertPath.length() > 0) { // 配置了 envelope cert path, 使用数字信封通讯
			isEnvelope = true;
			try {
				// 初始化算法文件
				NetSignUtil.initialize();

				serverCert = CertUtil.parseCert(FileUtil.getFile(serverCertPath)).getX509Cert();
				if (StringUtil.isNotBlank(clientKeyPath) && StringUtil.isNotBlank(clientKeyPath))
					priKey = KeyStoreUtil.loadKey(clientKeyPwd, clientKeyPath);
				clientCertDn = CertUtil.parseCert(FileUtil.getFile(clientCertPath)).getCertDn();
			} catch (Exception e) {
				int errno = ErrCode.LOAD_ALG_ERROR;
				String msg = e.getMessage();
				println("errno=" + errno + ", errmsg=" + msg);
				throw new AppApiException(errno, ErrMsg.getErrMsg(errno));
			}
		}

		Properties pro1 = new Properties();
		pro1.setProperty(NewSocketConnectionPool.SERVER_IP, property.getServerIP());
		pro1.setProperty(NewSocketConnectionPool.SERVER_PORT, String.valueOf(property.getPort()));

		pro1.setProperty(NewSocketConnectionPool.MAX_CONN_NUMBER, String.valueOf(property.getMaxConn()));
		pro1.setProperty(NewSocketConnectionPool.MIN_CONN_NUMBER, String.valueOf(property.getMinConn()));
		pro1.setProperty(NewSocketConnectionPool.READ_TIMEOUT, String.valueOf(property.getReadTimeout()));
		pro1.setProperty(NewSocketConnectionPool.GETCONN_TIMEOUT, String.valueOf(property.getGetConnTimeout()));

		pro1.setProperty(NewSocketConnectionPool.CHECK_INTERVAL, String.valueOf(property.getRunIntervalTime()));
		pro1.setProperty(NewSocketConnectionPool.UNUSED_TIMEOUT, String.valueOf(property.getConnUnUsedTimeout()));

		NewSocketConnectionPool.setProperties(Constants.API, pro1, ssf);
	}

	/**
	 * 执行操作
	 * 
	 * @param req
	 *            请求体
	 * @return 响应体
	 * @throws AppApiException
	 *             API异常
	 * @throws AppServerException
	 *             服务器异常
	 */
	private static final Response execute(Request req) throws AppApiException, AppServerException {
		if (pro == null) {
			int errno = ErrCode.API_PROPERTY_IS_NULL;
			throw new AppApiException(errno, ErrMsg.getErrMsg(errno));
		}
		Socket s = null;
		try {
			s = NewSocketConnectionPool.getInstance(Constants.API).getConnection();
		} catch (Exception e) {
			int errCode = ErrCode.CONNECT_FAILED;
			String errMsg = "create connect to server error";

			println("errCode=" + errCode + ", errMsg=" + errMsg);
			println(e);
			throw new AppApiException(errCode, ErrMsg.getErrMsg(errCode));
		}

		if (s == null) {
			int errCode = ErrCode.CONNECT_GET_TIMEOUT;
			String errMsg = "get conn(is null) from pool timeout";

			println("errCode=" + errCode + ", errMsg=" + errMsg);
			throw new AppApiException(errCode, ErrMsg.getErrMsg(errCode));
		}

		Response res = null;
		UnProtocolCommunicator comm = new UnProtocolCommunicator(s);
		try {
			if (StringUtil.isNotBlank(clientCertDn))
				req.getData().setProperty(Constants.CERT_DN_CLIENT, clientCertDn);

			res = comm.sendAndReceive(req, isEnvelope, serverCert, priKey);
		} catch (Exception e) {
			int errCode = ErrCode.NETWORK_IO_ERROR;
			String errMsg = "read/write error occured";
			println("errCode=" + errCode + ", errMsg=" + errMsg);
			println(e);
			throw new AppApiException(errCode, ErrMsg.getErrMsg(errCode));
		} finally {
			try {
				comm.close();
			} catch (Exception e) {
			}
		}

		if (res == null) {
			println("response is null");
			int errorNo = ErrCode.RESPONSE_IS_NULL;
			throw new AppApiException(errorNo, ErrMsg.getErrMsg(errorNo));
		}

		if (res.getData() == null) {
			println("response data is null");
			int errorNo = ErrCode.RESPONSE_DATA_IS_NULL;
			throw new AppApiException(errorNo, ErrMsg.getErrMsg(errorNo));
		}

		int errCode = res.getErrCode();
		String errMsg = res.getErrMsg();
		if (errCode != 0)
			throw new AppServerException(errCode, errMsg);
		return res;
	}

	/**
	 * 保存操作日志
	 * 
	 * @param pro
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static void addOperateLog(Properties pro) throws AppApiException, AppServerException {
		ByteRequest req = new ByteRequest();
		req.setType("AddOperateLog");

		Properties p = new Properties();
		p.setProperty(Constants.OP_LOG_ACCOUNT, handleParam(pro.getProperty(Constants.OP_LOG_ACCOUNT)));
		p.setProperty(Constants.OP_LOG_TYPE, handleParam(pro.getProperty(Constants.OP_LOG_TYPE)));
		p.setProperty(Constants.OP_LOG_TIME, handleParam(pro.getProperty(Constants.OP_LOG_TIME)));
		p.setProperty(Constants.OP_LOG_RETURN_CODE, handleParam(pro.getProperty(Constants.OP_LOG_RETURN_CODE)));
		p.setProperty(Constants.OP_LOG_ERR_MSG, handleParam(pro.getProperty(Constants.OP_LOG_ERR_MSG)));
		req.setData(p);

		execute(req);
	}

	/**
	 * 设置文档打印参数
	 * 
	 * @param pro
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static void addPrinter(Properties pro) throws AppApiException, AppServerException {
		ByteRequest req = new ByteRequest();
		req.setType("AddPrinter");

		Properties p = new Properties();
		p.setProperty(Constants.PRINTER_NAME, handleParam(pro.getProperty(Constants.PRINTER_NAME)));
		p.setProperty(Constants.SEAL_NAME, handleParam(pro.getProperty(Constants.SEAL_NAME)));
		p.setProperty(Constants.SEAL_TYPE, handleParam(pro.getProperty(Constants.SEAL_TYPE)));
		p.setProperty(Constants.USER_NAME, handleParam(pro.getProperty(Constants.USER_NAME)));
		p.setProperty(Constants.PRINTER_NUM, handleParam(pro.getProperty(Constants.PRINTER_NUM)));
		p.setProperty(Constants.PRINTER_LIMIT, handleParam(pro.getProperty(Constants.PRINTER_LIMIT)));
		p.setProperty(Constants.PRINTER_PWD, handleParam(pro.getProperty(Constants.PRINTER_PWD)));
		req.setData(p);

		execute(req);
	}

	/**
	 * 获取文档打印份数
	 * 
	 * @param pro
	 * @return
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static long getPrinter(Properties pro) throws AppApiException, AppServerException {
		ByteRequest req = new ByteRequest();
		req.setType("GetPrinter");

		Properties p = new Properties();
		p.setProperty(Constants.PRINTER_NAME, handleParam(pro.getProperty(Constants.PRINTER_NAME)));
		p.setProperty(Constants.PRINTER_PWD, handleParam(pro.getProperty(Constants.PRINTER_PWD)));
		req.setData(p);

		Response res = execute(req);
		String complete_num = getValue(res.getData(), Constants.PRINTER_NUM);

		checkResponeValue(Constants.PRINTER_NUM, complete_num, Constants.PARAM_TYPE_INT);

		return Integer.parseInt(complete_num);
	}

	/**
	 * 获取印章
	 * 
	 * @param pro
	 * @return
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static byte[] getSeal(Properties pro) throws AppApiException, AppServerException {
		ByteRequest req = new ByteRequest();
		req.setType("GetSeal");

		Properties p = new Properties();
		p.setProperty(Constants.CERT_DN, handleParam(pro.getProperty(Constants.CERT_DN)));
		p.setProperty(Constants.SIGN_DATA, handleParam(pro.getProperty(Constants.SIGN_DATA)));
		req.setData(p);

		Response res = execute(req);

		String sealData = getValue(res.getData(), Constants.SEAL_DATA);
		checkResponeValue(Constants.SEAL_DATA, sealData, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return StringUtil.base64Decode(sealData);

		// String sealID = getValue(res.getData(), Constants.SEAL_ID);
		// String sealName = getValue(res.getData(), Constants.SEAL_NAME);
		// String sealType = getValue(res.getData(), Constants.SEAL_TYPE);
		// String sealStatus = getValue(res.getData(), Constants.SEAL_STATUS);
		// String sealTempleID = getValue(res.getData(),
		// Constants.SEAL_TEMPLATE_ID);
		// String sealCompanyName = getValue(res.getData(),
		// Constants.SEAL_COMPANY_NAME);
		// String sealAccountR = getValue(res.getData(),
		// Constants.SEAL_ACCOUNT_R);
		// String sealAccountRCertDN = getValue(res.getData(),
		// Constants.SEAL_ACCOUNT_R_CERT_DN);
		// String sealUsedCount = getValue(res.getData(),
		// Constants.SEAL_USEDCOUNT);
		//
		// String sealUseLimit = getValue(res.getData(),
		// Constants.SEAL_USEDLIMIT);
		// String sealNotbefor = getValue(res.getData(),
		// Constants.SEAL_NOTBEFOR);
		// String sealNotafter = getValue(res.getData(),
		// Constants.SEAL_NOTAFTER);
		//
		// String sealPhotoPath = getValue(res.getData(),
		// Constants.SEAL_PHOTO_PATH);
		// String sealPhotoHigh = getValue(res.getData(),
		// Constants.SEAL_PHOTO_HIGH);
		// String sealPhotoWidth = getValue(res.getData(),
		// Constants.SEAL_PHOTO_WIDTH);
		// String sealGenerateTime = getValue(res.getData(),
		// Constants.SEAL_GENERATE_TIME);
		// String sealDownloadTime = getValue(res.getData(),
		// Constants.SEAL_DOWNLOAD_TIME);
		// String sealIsAuthCertDownload = getValue(res.getData(),
		// Constants.SEAL_IS_AUTH_CERT_DOWNLOAD);
		// String sealIsDownload = getValue(res.getData(),
		// Constants.SEAL_IS_DOWNLOAD);
		// String sealData = getValue(res.getData(), Constants.SEAL_DATA);
		//
		// checkParamValue(Constants.SEAL_ID, sealID, Constants.PARAM_TYPE_INT);
		// // checkParamValue(Constants.SEAL_NAME, sealName,
		// Constants.PARAM_TYPE_STRING_NULLABLE);
		// checkParamValue(Constants.SEAL_TYPE, sealType,
		// Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_STATUS, sealStatus,
		// Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_TEMPLATE_ID, sealTempleID,
		// Constants.PARAM_TYPE_INT);
		//
		// // checkParamValue(Constants.SEAL_COMPANY_NAME, sealCompanyName,
		// Constants.PARAM_TYPE_STRING_NULLABLE);
		// // checkParamValue(Constants.SEAL_ACCOUNT_R, sealAccountR,
		// Constants.PARAM_TYPE_STRING_NULLABLE);
		// // checkParamValue(Constants.SEAL_ACCOUNT_R_CERT_DN,
		// sealAccountRCertDN, Constants.PARAM_TYPE_STRING_NULLABLE);
		// checkParamValue(Constants.SEAL_USEDCOUNT, sealUsedCount,
		// Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_USEDLIMIT, sealUseLimit,
		// Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_NOTBEFOR, sealNotbefor,
		// Constants.PARAM_TYPE_LONG);
		// checkParamValue(Constants.SEAL_NOTAFTER, sealNotafter,
		// Constants.PARAM_TYPE_LONG);
		//
		// // checkParamValue(Constants.SEAL_PHOTO_PATH, sealPhotoPath,
		// Constants.PARAM_TYPE_STRING_NULLABLE);
		// checkParamValue(Constants.SEAL_PHOTO_HIGH, sealPhotoHigh,
		// Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_PHOTO_WIDTH, sealPhotoWidth,
		// Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_GENERATE_TIME, sealGenerateTime,
		// Constants.PARAM_TYPE_LONG);
		// checkParamValue(Constants.SEAL_DOWNLOAD_TIME, sealDownloadTime,
		// Constants.PARAM_TYPE_LONG);
		// checkParamValue(Constants.SEAL_IS_AUTH_CERT_DOWNLOAD,
		// sealIsAuthCertDownload, Constants.PARAM_TYPE_INT);
		// checkParamValue(Constants.SEAL_IS_DOWNLOAD, sealIsDownload,
		// Constants.PARAM_TYPE_INT);
		//
		// Seal seal = new Seal();
		// seal.setId(Integer.parseInt(sealID));
		// seal.setName(sealName);
		// seal.setType(Integer.parseInt(sealType));
		// seal.setStatus(Integer.parseInt(sealStatus));
		// seal.setSealTemplateId(Integer.parseInt(sealTempleID));
		// seal.setCompanyName(sealCompanyName);
		// seal.setAccountR(sealAccountR);
		// seal.setAccountRCertDN(sealAccountRCertDN);
		// seal.setUsedCount(Integer.parseInt(sealUsedCount));
		//
		// seal.setUsedLimit(Integer.parseInt(sealUseLimit));
		// seal.setNotAfter(Long.parseLong(sealNotbefor));
		// seal.setNotAfter(Long.parseLong(sealNotafter));
		//
		// seal.setPhotoPath(sealPhotoPath);
		// seal.setPhotoHigh(Integer.parseInt(sealPhotoHigh));
		// seal.setPhotoWidth(Integer.parseInt(sealPhotoWidth));
		// seal.setGenerateTime(Long.parseLong(sealGenerateTime));
		// seal.setDownloadTime(Long.parseLong(sealDownloadTime));
		// seal.setIsAuthCertDownload(Integer.parseInt(sealIsAuthCertDownload));
		// seal.setIsDownload(Integer.parseInt(sealIsDownload));
		//
		// if (!"".equals(sealData))
		// seal.setSealData(StringUtil.base64Decode(sealData));
		//
		// return seal;
	}

	// /**
	// * 获取印章状态
	// *
	// * @param pro
	// * @return
	// * @throws AppApiException
	// * @throws AppServerException
	// */
	// public static int getSealStatus(Properties pro) throws AppApiException,
	// AppServerException {
	// ByteRequest req = new ByteRequest();
	// req.setType("GetSealStatus");
	//
	// Properties p = new Properties();
	// p.setProperty(Constants.SEAL_NAME,
	// handleParam(pro.getProperty(Constants.SEAL_NAME)));
	// req.setData(p);
	//
	// Response res = execute(req);
	// String seal_status = getValue(res.getData(), Constants.SEAL_STATUS);
	//
	// checkParamValue(Constants.SEAL_STATUS, seal_status,
	// Constants.PARAM_TYPE_INT);
	//
	// return Integer.parseInt(seal_status);
	// }

	/**
	 * 申请印章
	 * 
	 * @param pro
	 * @return
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static void requestSeal(Properties pro) throws AppApiException, AppServerException {
		checkParamValue(Constants.PHOTO_DATA, pro.getProperty(Constants.PHOTO_DATA), Constants.PARAM_TYPE_STRING_NULLABLE, Constants.LENGTH_20KB_B64);

		ByteRequest req = new ByteRequest();
		req.setType("RequestSeal");

		Properties p = new Properties();
		p.setProperty(Constants.SEAL_NAME, handleParam(pro.getProperty(Constants.SEAL_NAME)));
		p.setProperty(Constants.TEMPLATE_NAME, handleParam(pro.getProperty(Constants.TEMPLATE_NAME)));
		p.setProperty(Constants.CERT_DN, handleParam(pro.getProperty(Constants.CERT_DN)));
		p.setProperty(Constants.PHOTO_DATA, handleParam(pro.getProperty(Constants.PHOTO_DATA)));
		req.setData(p);

		execute(req);
	}

	/**
	 * 注册签章人
	 * 
	 * @param pro
	 * @return
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static void registerUser(Properties pro) throws AppApiException, AppServerException {
		checkParamValue(Constants.CERT_DATA, pro.getProperty(Constants.CERT_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		ByteRequest req = new ByteRequest();
		req.setType("RegisterUser");

		Properties p = new Properties();
		p.setProperty(Constants.USER_NAME, handleParam(pro.getProperty(Constants.USER_NAME)));
		p.setProperty(Constants.COMPANY_NAME, handleParam(pro.getProperty(Constants.COMPANY_NAME)));
		p.setProperty(Constants.CERT_DATA, handleParam(pro.getProperty(Constants.CERT_DATA)));
		req.setData(p);

		execute(req);
	}

	/**
	 * 更新印章图片
	 * 
	 * @param pro
	 * @return
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static void updateSealPhoto(Properties pro) throws AppApiException, AppServerException {
		checkParamValue(Constants.PHOTO_DATA, pro.getProperty(Constants.PHOTO_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_20KB_B64);

		ByteRequest req = new ByteRequest();
		req.setType("UpdateSealPhoto");

		Properties p = new Properties();
		p.setProperty(Constants.USER_NAME, handleParam(pro.getProperty(Constants.USER_NAME)));
		p.setProperty(Constants.COMPANY_NAME, handleParam(pro.getProperty(Constants.COMPANY_NAME)));
		p.setProperty(Constants.CERT_DN, handleParam(pro.getProperty(Constants.CERT_DN)));
		p.setProperty(Constants.PHOTO_DATA, handleParam(pro.getProperty(Constants.PHOTO_DATA)));
		req.setData(p);

		execute(req);
	}

	/**
	 * 验证证书
	 * 
	 * @param pro
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static boolean verifyCert(Properties pro) throws AppApiException, AppServerException {
		checkParamValue(Constants.CERT_DATA, pro.getProperty(Constants.CERT_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		ByteRequest req = new ByteRequest();
		req.setType("VerifyCert");

		Properties p = new Properties();
		p.setProperty(Constants.CERT_DATA, handleParam(pro.getProperty(Constants.CERT_DATA)));
		req.setData(p);

		Response res = execute(req);

		String result = getValue(res.getData(), Constants.RESULT);
		checkResponeValue(Constants.RESULT, result, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return Boolean.valueOf(result);
	}

	/**
	 * PDF盖章
	 * 
	 * @param pro
	 * @return 签完章的pdf,按base64编码
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static String pdfStamp(Properties pro) throws AppApiException, AppServerException {
		checkParamValue(Constants.PDF_DATA, pro.getProperty(Constants.PDF_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		ByteRequest req = new ByteRequest();
		req.setType("PdfStamp");

		Properties p = new Properties();
		p.setProperty(Constants.PDF_DATA, handleParam(pro.getProperty(Constants.PDF_DATA)));
		p.setProperty(Constants.CERT_DN_SERVER, handleParam(pro.getProperty(Constants.CERT_DN_SERVER)));
		p.setProperty(Constants.CERT_DN, handleParam(pro.getProperty(Constants.CERT_DN)));
		p.setProperty(Constants.PHOTO_DATA, handleParam(pro.getProperty(Constants.PHOTO_DATA)));
		p.setProperty(Constants.PDF_PAGENUM, handleParam(pro.getProperty(Constants.PDF_PAGENUM)));
		p.setProperty(Constants.PDF_BIZNUM, handleParam(pro.getProperty(Constants.PDF_BIZNUM)));
		p.setProperty(Constants.PDF_KEYWORDS, handleParam(pro.getProperty(Constants.PDF_KEYWORDS)));
		p.setProperty(Constants.PDF_X, handleParam(pro.getProperty(Constants.PDF_X)));
		p.setProperty(Constants.PDF_Y, handleParam(pro.getProperty(Constants.PDF_Y)));
		p.setProperty(Constants.PDF_QFZ, handleParam(pro.getProperty(Constants.PDF_QFZ)));

		p.setProperty(Constants.PDF_BARCODE_X, handleParam(pro.getProperty(Constants.PDF_BARCODE_X)));
		p.setProperty(Constants.PDF_BARCODE_Y, handleParam(pro.getProperty(Constants.PDF_BARCODE_Y)));
		p.setProperty(Constants.PDF_BARCODE_WIDTH, handleParam(pro.getProperty(Constants.PDF_BARCODE_WIDTH)));
		p.setProperty(Constants.PDF_BARCODE_CONTENT, handleParam(pro.getProperty(Constants.PDF_BARCODE_CONTENT)));
		p.setProperty(Constants.PDF_BARCODE_PAGENUM, handleParam(pro.getProperty(Constants.PDF_BARCODE_PAGENUM)));

		req.setData(p);

		Response res = execute(req);

		String pdfStampBase64 = getValue(res.getData(), Constants.PDF_DATA_STAMP);
		checkResponeValue(Constants.PDF_DATA_STAMP, pdfStampBase64, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return pdfStampBase64;
	}

	/**
	 * PDF模板盖章
	 * 
	 * @param pro
	 * @return 签完章的PDF,按base64编码
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static String pdfStampTemplate(Properties pro) throws AppApiException, AppServerException {
		ByteRequest req = new ByteRequest();
		req.setType("PdfStampTemplate");

		Properties p = new Properties();
		p.setProperty(Constants.PDF_TEMPLATE_NAME, handleParam(pro.getProperty(Constants.PDF_TEMPLATE_NAME)));
		p.setProperty(Constants.PDF_TEMPLATE_FIELD, handleParam(pro.getProperty(Constants.PDF_TEMPLATE_FIELD)));
		p.setProperty(Constants.CERT_DN_SERVER, handleParam(pro.getProperty(Constants.CERT_DN_SERVER)));
		p.setProperty(Constants.CERT_DN, handleParam(pro.getProperty(Constants.CERT_DN)));
		p.setProperty(Constants.PHOTO_DATA, handleParam(pro.getProperty(Constants.PHOTO_DATA)));
		p.setProperty(Constants.PDF_PAGENUM, handleParam(pro.getProperty(Constants.PDF_PAGENUM)));
		p.setProperty(Constants.PDF_X, handleParam(pro.getProperty(Constants.PDF_X)));
		p.setProperty(Constants.PDF_Y, handleParam(pro.getProperty(Constants.PDF_Y)));
		p.setProperty(Constants.PDF_BIZNUM, handleParam(pro.getProperty(Constants.PDF_BIZNUM)));
		p.setProperty(Constants.PDF_KEYWORDS, handleParam(pro.getProperty(Constants.PDF_KEYWORDS)));
		p.setProperty(Constants.PDF_QFZ, handleParam(pro.getProperty(Constants.PDF_QFZ)));

		p.setProperty(Constants.PDF_BARCODE_X, handleParam(pro.getProperty(Constants.PDF_BARCODE_X)));
		p.setProperty(Constants.PDF_BARCODE_Y, handleParam(pro.getProperty(Constants.PDF_BARCODE_Y)));
		p.setProperty(Constants.PDF_BARCODE_WIDTH, handleParam(pro.getProperty(Constants.PDF_BARCODE_WIDTH)));
		p.setProperty(Constants.PDF_BARCODE_CONTENT, handleParam(pro.getProperty(Constants.PDF_BARCODE_CONTENT)));
		p.setProperty(Constants.PDF_BARCODE_PAGENUM, handleParam(pro.getProperty(Constants.PDF_BARCODE_PAGENUM)));

		req.setData(p);

		Response res = execute(req);

		String pdfStampBase64 = getValue(res.getData(), Constants.PDF_DATA_STAMP);
		checkResponeValue(Constants.PDF_DATA_STAMP, pdfStampBase64, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return pdfStampBase64;
	}

	/**
	 * OFD盖章
	 * 
	 * @param pro
	 * @return 签完章的OFD,按base64编码
	 * @throws Exception
	 */
	public static String ofdStamp(Properties pro) throws Exception {
		checkParamValue(Constants.OFD_DATA, pro.getProperty(Constants.OFD_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		ByteRequest req = new ByteRequest();
		req.setType("OfdStamp");

		Properties p = new Properties();
		p.setProperty(Constants.OFD_DATA, handleParam(pro.getProperty(Constants.OFD_DATA)));
		p.setProperty(Constants.CERT_DN_SERVER, handleParam(pro.getProperty(Constants.CERT_DN_SERVER)));
		p.setProperty(Constants.CERT_DN, handleParam(pro.getProperty(Constants.CERT_DN)));
		p.setProperty(Constants.OFD_PAGENUM, handleParam(pro.getProperty(Constants.OFD_PAGENUM)));
		p.setProperty(Constants.OFD_BIZNUM, handleParam(pro.getProperty(Constants.OFD_BIZNUM)));
		p.setProperty(Constants.OFD_KEYWORDS, handleParam(pro.getProperty(Constants.OFD_KEYWORDS)));
		p.setProperty(Constants.OFD_X, handleParam(pro.getProperty(Constants.OFD_X)));
		p.setProperty(Constants.OFD_Y, handleParam(pro.getProperty(Constants.OFD_Y)));
		p.setProperty(Constants.OFD_QFZ, handleParam(pro.getProperty(Constants.OFD_QFZ)));
		req.setData(p);

		Response res = execute(req);

		String ofdStampBase64 = getValue(res.getData(), Constants.OFD_DATA_STAMP);
		checkResponeValue(Constants.PDF_DATA_STAMP, ofdStampBase64, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return ofdStampBase64;
	}

	/**
	 * 验证PDF签章
	 * 
	 * @param pro
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static boolean verifyPdfStamp(Properties pro) throws AppApiException, AppServerException {
		checkParamValue(Constants.PDF_DATA, pro.getProperty(Constants.PDF_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		boolean checkSealDate = true;
		boolean checkCertDate = true;

		if (StringUtil.isNotBlank(pro.getProperty(Constants.PDF_CHECK_SEAL_DATE))) {
			checkParamValue(Constants.PDF_CHECK_SEAL_DATE, pro.getProperty(Constants.PDF_CHECK_SEAL_DATE), Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);
			checkSealDate = Boolean.parseBoolean(pro.getProperty(Constants.PDF_CHECK_SEAL_DATE));
		}

		if (StringUtil.isNotBlank(pro.getProperty(Constants.PDF_CHECK_CERT_DATE))) {
			checkParamValue(Constants.PDF_CHECK_CERT_DATE, pro.getProperty(Constants.PDF_CHECK_CERT_DATE), Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);
			checkCertDate = Boolean.parseBoolean(pro.getProperty(Constants.PDF_CHECK_CERT_DATE));
		}

		ByteRequest req = new ByteRequest();
		req.setType("VerifyPdfStamp");

		Properties p = new Properties();
		p.setProperty(Constants.PDF_DATA, handleParam(pro.getProperty(Constants.PDF_DATA)));
		p.setProperty(Constants.PDF_CHECK_SEAL_DATE, String.valueOf(checkSealDate));
		p.setProperty(Constants.PDF_CHECK_CERT_DATE, String.valueOf(checkCertDate));
		req.setData(p);

		Response res = execute(req);

		String result = getValue(res.getData(), Constants.RESULT);
		checkResponeValue(Constants.SEAL_STATUS, result, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return Boolean.valueOf(result);
	}

	/**
	 * 验证OFD签章
	 * 
	 * @param pro
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyOfdStamp(Properties pro) throws Exception {
		checkParamValue(Constants.OFD_DATA, pro.getProperty(Constants.OFD_DATA), Constants.PARAM_TYPE_STRING_NOT_NULL, Constants.LENGTH_3MB_B64);

		boolean checkSealDate = true;
		boolean checkCertDate = true;

		if (StringUtil.isNotBlank(pro.getProperty(Constants.OFD_CHECK_SEAL_DATE))) {
			checkParamValue(Constants.OFD_CHECK_SEAL_DATE, pro.getProperty(Constants.OFD_CHECK_SEAL_DATE), Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);
			checkSealDate = Boolean.parseBoolean(pro.getProperty(Constants.OFD_CHECK_SEAL_DATE));
		}

		if (StringUtil.isNotBlank(pro.getProperty(Constants.OFD_CHECK_CERT_DATE))) {
			checkParamValue(Constants.OFD_CHECK_CERT_DATE, pro.getProperty(Constants.OFD_CHECK_CERT_DATE), Constants.PARAM_TYPE_BOOLEAN, Constants.LENGTH_EIGHT);
			checkCertDate = Boolean.parseBoolean(pro.getProperty(Constants.OFD_CHECK_CERT_DATE));
		}

		ByteRequest req = new ByteRequest();
		req.setType("VerifyOfdStamp");

		Properties p = new Properties();
		p.setProperty(Constants.OFD_DATA, handleParam(pro.getProperty(Constants.OFD_DATA)));
		p.setProperty(Constants.OFD_CHECK_SEAL_DATE, String.valueOf(checkSealDate));
		p.setProperty(Constants.OFD_CHECK_CERT_DATE, String.valueOf(checkCertDate));
		req.setData(p);

		Response res = execute(req);

		String result = getValue(res.getData(), Constants.RESULT);
		checkResponeValue(Constants.SEAL_STATUS, result, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return Boolean.valueOf(result);
	}

	/**
	 * 获取系统属性
	 * 
	 * @param pro
	 * @throws AppApiException
	 * @throws AppServerException
	 */
	public static String getSystemPro(Properties pro) throws AppApiException, AppServerException {
		ByteRequest req = new ByteRequest();
		req.setType("GetSystemPro");

		Properties p = new Properties();
		p.setProperty(Constants.PRO_KEY, handleParam(pro.getProperty(Constants.PRO_KEY)));
		req.setData(p);

		Response res = execute(req);

		String result = getValue(res.getData(), Constants.RESULT);
		checkResponeValue(Constants.RESULT, result, Constants.PARAM_TYPE_STRING_NOT_NULL);

		return result;
	}

	private static String handleParam(String stringParameter) {
		if (stringParameter == null)
			return Constants.DEFAULT_STRING;
		else
			return stringParameter.trim();
	}

	private static void checkParamValue(String name, String value, String type, long lenLimit) throws AppApiException {
		if (Constants.PARAM_TYPE_STRING_NOT_NULL.equals(type)) {
			if (value == null)
				throw new AppApiException(ErrCode.API_PARAM_IS_NULL, name + " value is null");

			if ("".equals(value.trim()))
				throw new AppApiException(ErrCode.API_PARAM_IS_EMPTY, name + " value is empty");

			if (StringUtil.getBytes(value).length > lenLimit)
				throw new AppApiException(ErrCode.API_PARAM_LEN_EXCEED, name + " value len is over limit " + lenLimit);

		} else if (Constants.PARAM_TYPE_STRING_NULLABLE.equals(type)) {
			if (StringUtil.isNotBlank(value))
				if (StringUtil.getBytes(value).length > lenLimit)
					throw new AppApiException(ErrCode.API_PARAM_LEN_EXCEED, name + " value len is over limit " + lenLimit);

		} else if (Constants.PARAM_TYPE_INT.equals(type)) {
			try {
				Integer.parseInt(value);
			} catch (Exception e) {
				throw new AppApiException(ErrCode.API_PARAM_INVAILD, name + " value is invalid");
			}

		} else if (Constants.PARAM_TYPE_LONG.equals(type)) {
			try {
				Long.parseLong(value);
			} catch (Exception e) {
				throw new AppApiException(ErrCode.API_PARAM_INVAILD, name + " value is invalid");
			}
		} else if (Constants.PARAM_TYPE_BOOLEAN.equals(type)) {
			try {
				Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new AppApiException(ErrCode.API_PARAM_INVAILD, name + " value is invalid");
			}
		}
	}

	private static void checkResponeValue(String name, String value, String type) throws AppApiException {
		if (Constants.PARAM_TYPE_STRING_NOT_NULL.equals(type)) {
			if (value == null)
				throw new AppApiException(ErrCode.API_PARAM_IS_NULL, name + " value is null");

			if ("".equals(value.trim()))
				throw new AppApiException(ErrCode.API_PARAM_IS_EMPTY, name + " value is empty");
		} else if (Constants.PARAM_TYPE_STRING_NULLABLE.equals(type)) {
			if (value == null)
				throw new AppApiException(ErrCode.API_PARAM_IS_NULL, name + " value is null");

		} else if (Constants.PARAM_TYPE_INT.equals(type)) {
			try {
				Integer.parseInt(value);
			} catch (Exception e) {
				throw new AppApiException(ErrCode.API_PARAM_INVAILD, name + " value is invalid");
			}
		} else if (Constants.PARAM_TYPE_LONG.equals(type)) {
			try {
				Long.parseLong(value);
			} catch (Exception e) {
				throw new AppApiException(ErrCode.API_PARAM_INVAILD, name + " value is invalid");
			}
		}
	}

	private static String getValue(Properties reqdata, String key) {
		return StringUtil.parseStringWithDefault(reqdata.getProperty(key), Constants.DEFAULT_STRING);
	}

	private static void println(String message) {
		try {
			synchronized (logSync) {
				if (logWriter != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
					String s = sdf.format(new Date());
					logWriter.print(s);
					logWriter.print(" ");
					logWriter.println(message);
					logWriter.flush();
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
					String s = sdf.format(new Date());
					System.out.print(s);
					System.out.print(" ");
					System.out.println(message);
				}
			}
		} catch (Throwable e) {
		}
	}

	private static void println(Throwable th) {
		try {
			synchronized (logSync) {
				if (logWriter != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
					String s = sdf.format(new Date());
					logWriter.print(s);
					logWriter.print(" ");
					th.printStackTrace(logWriter);
					logWriter.flush();
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
					String s = sdf.format(new Date());
					System.out.print(s);
					System.out.print(" ");
					th.printStackTrace(System.out);
				}

			}
		} catch (Throwable e) {
		}
	}

	/**
	 * 增加库路径
	 * 
	 * @param libraryPath
	 * @throws Exception
	 */
	private static void addLibraryDir(String libraryPath) throws Exception {
		Field field = ClassLoader.class.getDeclaredField("usr_paths");
		field.setAccessible(true);
		String[] paths = (String[]) field.get(null);
		for (int i = 0; i < paths.length; i++) {
			if (libraryPath.equals(paths[i])) {
				return;
			}
		}

		String[] tmp = new String[paths.length + 1];
		System.arraycopy(paths, 0, tmp, 0, paths.length);
		tmp[paths.length] = libraryPath;
		field.set(null, tmp);
	}

}
