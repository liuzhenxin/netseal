package cn.com.infosec.netseal.common.util.ofd;

import java.io.File;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;
import cn.com.infosec.netseal.ofd.handler.OfdStampHandler;
import cn.com.infosec.netseal.ofd.handler.VerifyStampHandler;
import cn.com.infosec.netseal.ofd.signer.SignerByServer;

public class OfdStampUtil {

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 坐标签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param pageNum
	 * @param zx
	 * @param zy
	 * @param ux
	 * @param uy
	 * @param biznum
	 * @param pwd
	 * @return
	 */
	public static byte[] ofdStampByCoordinate(byte[] ofdData, int pageNum, float pdfX, float pdfY, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		try {
			OfdStampHandler handler = new OfdStampHandler();
			handler.setSigner(new SignerByServer(key, keyData, keyCertData, sealData, rootHt, null, ConfigUtil.getInstance().getGmOid().getBytes(), true, true));
			handler.setOfdData(ofdData);
			handler.setPageNo(pageNum);
			handler.setXY(pdfX, pdfY);
			return handler.handle();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("ofd stamp by coordinate error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("ofd stamp by coordinate error", e);
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_ERROR, "ofd stamp error, " + e.getMessage());
		}
	}

	/**
	 * 坐标签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param zx
	 * @param zy
	 * @param ux
	 * @param uy
	 * @param biznum
	 * @param pwd
	 * @return
	 */
	public static byte[] ofdStampByCoordinate(byte[] ofdData, byte[] photoData, float pdfX, float pdfY, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		try {
			OfdStampHandler handler = new OfdStampHandler();
			handler.setSigner(new SignerByServer(key, keyData, keyCertData, sealData, rootHt, null, ConfigUtil.getInstance().getGmOid().getBytes(), true, true));
			handler.setOfdData(ofdData);
			handler.setXY(pdfX, pdfY);
			return handler.handle();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("ofd stamp by coordinate error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("ofd stamp by coordinate error", e);
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_ERROR, "ofd stamp error, " + e.getMessage());
		}
	}

	/**
	 * 关键字签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param page
	 * @param keywords
	 * @param key
	 * @param pwd
	 * @return
	 */
	public static byte[] ofdStampByText(byte[] ofdData, int pageNum, String keywords, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		try {
			OfdStampHandler handler = new OfdStampHandler();
			handler.setSigner(new SignerByServer(key, keyData, keyCertData, sealData, rootHt, null, ConfigUtil.getInstance().getGmOid().getBytes(), true, true));
			handler.setOfdData(ofdData);
			handler.setPageNo(pageNum);
			handler.setText(keywords);
			return handler.handle();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("ofd stamp by text error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("ofd stamp by text error", e);
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_ERROR, "ofd stamp error, " + e.getMessage());
		}
	}

	/**
	 * 关键字签章, 最后一次出现关键字的位置签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param keywords
	 * @param biznum
	 * @param key
	 * @return
	 */
	public static byte[] ofdStampByText(byte[] ofdData, String keywords, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData, Hashtable<String, X509Certificate> rootHt,
			List<Key> signKeyList) {
		try {
			OfdStampHandler handler = new OfdStampHandler();
			handler.setSigner(new SignerByServer(key, keyData, keyCertData, sealData, rootHt, null, ConfigUtil.getInstance().getGmOid().getBytes(), true, true));
			handler.setOfdData(ofdData);
			handler.setText(keywords);
			return handler.handle();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("ofd stamp by text error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("ofd stamp by text error", e);
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_ERROR, "ofd stamp error, " + e.getMessage());
		}
	}

	/**
	 * 盖骑缝章
	 * 
	 * @param pdfData
	 * @param photoData
	 * @param biznum
	 * @param key
	 * @param keyData
	 * @param keyCertData
	 * @param sealData
	 * @param rootHt
	 * @param signKeyList
	 * @param align
	 * @return
	 */
	public static byte[] ofdStampByQfz(byte[] ofdData, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData, Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList,
			int align) {
		try {
			OfdStampHandler handler = new OfdStampHandler();
			handler.setSigner(new SignerByServer(key, keyData, keyCertData, sealData, rootHt, null, ConfigUtil.getInstance().getGmOid().getBytes(), true, true));
			handler.setOfdData(ofdData);
			handler.qfz(align);
			return handler.handle();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("ofd stamp by qfz error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("ofd stamp by qfz error", e);
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_ERROR, "ofd stamp error, " + e.getMessage());
		}
	}

	/**
	 * 验签章
	 * 
	 * @param pdfPath
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyOfdStamp(byte[] ofdData, Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList, boolean checkSealDate, boolean checkCertDate) {
		try {
			VerifyStampHandler handler = new VerifyStampHandler();
			handler.setSigner(new SignerByServer(null, null, null, null, rootHt, null, ConfigUtil.getInstance().getGmOid().getBytes(), checkSealDate, checkCertDate));
			handler.setOfdData(ofdData);
			handler.handle();
			return true;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("verify ofd stamp error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("verify ofd stamp error", e);
			throw new NetSealRuntimeException(ErrCode.OFD_STAMP_VERIFY_ERROR, "verify ofd stamp error, " + e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		LoggerConfig.init();
		String sm2KeyPath = "F:/temp/pdf/key/sm2/netseal.pri";
		String sm2CertPath = "F:/temp/pdf/key/sm2/netseal.cer";
		String sm2KeyPwd = "68683556";

		String rsaKeyPath = "F:/temp/pdf/key/pfx/netseal.pfx";
		String rsaCertPath = "F:/temp/pdf/key/pfx/netseal.cer";
		String rsaKeyPwd = "11111111";

		String keyPath = sm2KeyPath;
		String certPath = sm2CertPath;
		String keyPwd = sm2KeyPwd;

		Hashtable<String, X509Certificate> rootHt = new Hashtable<String, X509Certificate>();
		File f = new File("f:/temp/ca");
		File[] fs = f.listFiles();
		X509Certificate root = null;
		for (int i = 0; i < fs.length; i++) {
			root = CertUtil.parseCert(FileUtil.getFile(fs[i].getAbsolutePath())).getX509Cert();
			rootHt.put(root.getSubjectDN().getName(), root);
		}

		Key key = new Key();
		key.setKeyPwd(StringUtil.base64Encode(keyPwd));
		key.setKeyMode(keyPath.substring(keyPath.indexOf(".")));
		key.setHsmId(0);

		Seal seal = new Seal();
		seal.setId(1L);
		seal.setName("testSeal");
		seal.setType(1);
		long time = System.currentTimeMillis();
		seal.setGenerateTime(time);
		seal.setNotBefor(time);
		seal.setNotAfter(time + 24 * 3600 * 1000L);

		byte[] ofdData = FileUtil.getFile("f:/temp/ofd/2.ofd");
		byte[] keyData = FileUtil.getFile(keyPath);
		byte[] keyCertData = FileUtil.getFile(certPath);
		byte[] sealData = FileUtil.getFile("f:/temp/seal/sm2.seal");
		byte[] stampData = ofdStampByCoordinate(ofdData, 1, 0, 0, "", key, keyData, keyCertData, sealData, rootHt, null);
		FileUtil.storeFile("f:/temp/ofd/sm2_seal_stamp.ofd", stampData);

		// ofdStampByQfz(pdfData, null, key, keyData, certData, sealData, rootHt, null, Constants.QFZ_ALIGN_LEFT);

		boolean result = verifyOfdStamp(stampData, rootHt, null, true, true);
		System.out.println(result);
	}

}
