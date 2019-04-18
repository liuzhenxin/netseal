package cn.com.infosec.netseal.common.util.license;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.UUID;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.NetWorkUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class LicenseChecker {


	static {
		Security.addProvider(new InfosecProvider());
	}

	private static byte[] hash(byte[] plain) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		return md.digest(plain);
	}

	private static byte[] int2bytes(int data) {
		byte[] bs = new byte[4];
		bs[0] = (byte) ((data >>> 24) & 0xFF);
		bs[1] = (byte) ((data >>> 16) & 0xFF);
		bs[2] = (byte) ((data >>> 8) & 0xFF);
		bs[3] = (byte) ((data >>> 0) & 0xFF);
		return bs;
	}

	private static int bytes2int(byte[] data) {
		BigInteger bi = new BigInteger(data);
		return bi.intValue();
	}

	private static LicenseInfo parse(byte[] b) throws Exception {
		LicenseInfo lic = new LicenseInfo();

		int LEN_BYTES = 4;
		int MSG_BYTES = 1;

		byte[] length = new byte[4];
		byte[] msgType = new byte[1];
		byte[] value = null;
		byte[] leftBytes = null;

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHssmm'Z'");

		System.arraycopy(b, 0, length, 0, LEN_BYTES);
		int licLen = bytes2int(length);
		if (b.length != (licLen + 4))
			throw new Exception("parse licence Error: content length NOT correct");

		int leftBytesLen = licLen;
		leftBytes = new byte[leftBytesLen];
		System.arraycopy(b, LEN_BYTES, leftBytes, 0, leftBytesLen);

		while (leftBytesLen > 0) {
			System.arraycopy(leftBytes, 0, msgType, 0, MSG_BYTES);
			System.arraycopy(leftBytes, MSG_BYTES, length, 0, LEN_BYTES);
			int valueLen = bytes2int(length);
			value = new byte[valueLen];
			System.arraycopy(leftBytes, MSG_BYTES + LEN_BYTES, value, 0, valueLen);

			if ('S' == msgType[0])
				lic.setSoftwareName(new String(value));
			else if ('I' == msgType[0])
				lic.setBindMsg(new String(value));
			else if ('M' == msgType[0])
				lic.setMaxThred(bytes2int(value));
			else if ('C' == msgType[0])
				lic.setMaxCertNum(bytes2int(value));
			else if ('B' == msgType[0])
				lic.setValidTime_start(df.parse(new String(value)));
			else if ('E' == msgType[0])
				lic.setValidTime_end(df.parse(new String(value)));
			// else if('V'==msgType[0])
			// validMonth = bytes2int(value);
			else if ('R' == msgType[0])
				lic.setLicenceExtension(new String(value));
			else if ('F' == msgType[0])
				lic.setFeatures(new String(value));

			int readLen = MSG_BYTES + LEN_BYTES + valueLen;
			leftBytesLen = leftBytes.length - readLen;
			byte[] tmp = new byte[leftBytesLen];
			System.arraycopy(leftBytes, readLen, tmp, 0, leftBytesLen);
			leftBytes = tmp;
		}

		return lic;
	}

	/**
	 */
	private static String genLicenseApp(String bindMsg) throws Exception {
		String guid = getGuid();
		if (!FileUtil.checkPath(Constants.LICENSE_PATH + Constants.LICENSE_APP)) {
			byte[] hashkey = hash(guid.getBytes());
			CEAUtil cea = new CEAUtil();

			byte[] bs = (bindMsg + "&&NetSeal").getBytes();
			int iplength = bs.length;
			byte[] bs_iplength = int2bytes(iplength);

			int datalen = guid.length() + 4 + iplength;
			byte[] plain = new byte[datalen];
			System.arraycopy(guid.getBytes(), 0, plain, 0, guid.getBytes().length);
			System.arraycopy(bs_iplength, 0, plain, guid.getBytes().length, 4);
			System.arraycopy(bs, 0, plain, guid.getBytes().length + 4, iplength);
			String sdlen = String.valueOf(datalen);
			byte[] bsdlen = new byte[16];
			for (int i = 0; i < bsdlen.length; i++) {
				bsdlen[i] = 0x00;
			}
			System.arraycopy(sdlen.getBytes(), 0, bsdlen, 0, sdlen.length());
			byte[] out1 = new byte[16];
			cea.Encrypt(bsdlen, out1, bsdlen.length, hashkey, 16);

			byte[] out2 = new byte[plain.length];
			cea.Encrypt(plain, out2, plain.length, hashkey, 16);

			byte[] encdata = new byte[1000];
			int n = 16 + datalen;
			byte[] bs_datalen = int2bytes(n);
			System.arraycopy(bs_datalen, 0, encdata, 0, 4);
			System.arraycopy(out1, 0, encdata, 4, 16);
			System.arraycopy(out2, 0, encdata, 20, plain.length);

			byte[] licenceApp = new byte[guid.getBytes().length + plain.length + 20];
			System.arraycopy(guid.getBytes(), 0, licenceApp, 0, guid.getBytes().length);
			System.arraycopy(encdata, 0, licenceApp, guid.getBytes().length, plain.length + 20);

			FileUtil.storeFile(Constants.LICENSE_PATH + Constants.LICENSE_APP, licenceApp);
		}
		return guid;
	}

	private static String getGuid() throws Exception {
		if (!FileUtil.checkPath(Constants.LICENSE_PATH + Constants.LICENSE_GUID)) {
			String guid = UUID.randomUUID().toString();
			FileUtil.storeFile(Constants.LICENSE_PATH + Constants.LICENSE_GUID, guid.getBytes());
			return guid;
		} else {
			byte[] bs = FileUtil.getFile(Constants.LICENSE_PATH + Constants.LICENSE_GUID);
			String guid = new String(bs).trim();
			return guid;
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public static LicenseInfo checkLicense(String bindMsg) {
		try {
			if (StringUtil.isBlank(bindMsg))
				throw new Exception("bound message should not be empty");

			if (!FileUtil.checkPath(Constants.LICENSE_PATH + Constants.LICENSE)) {
				genLicenseApp(bindMsg);
				return null;
			}

			byte[] data = FileUtil.getFile(Constants.LICENSE_PATH + Constants.LICENSE);
			byte[] entlic = StringUtil.base64Decode(data);

			return checkLicense(bindMsg, entlic);
		} catch (Exception e) {
			LoggerUtil.errorlog("check license error, ", e);
			return null;
		}

	}

	/**
	 * 
	 * @param bindMsg
	 * @param licBytes
	 * @return
	 * @throws Exception
	 */
	public static LicenseInfo checkLicense(String bindMsg, byte[] licBytes) {
		try {
			if (StringUtil.isBlank(bindMsg))
				throw new Exception("bound message should not be empty");

			LicenseInfo lic = null;
			byte[] sdlen = new byte[16];
			for (int i = 0; i < sdlen.length; i++) {
				sdlen[i] = 0x00;
			}

			byte[] encsdlen = new byte[16];
			System.arraycopy(licBytes, 4, encsdlen, 0, 16);

			String guid = genLicenseApp(bindMsg);
			byte[] hashkey = hash(guid.getBytes());

			CEAUtil cea = new CEAUtil();
			int ou2 = cea.Decrypt(encsdlen, sdlen, 16, hashkey, 16);

			byte[] encdata = new byte[licBytes.length - 20];
			byte[] signeddata = new byte[licBytes.length - 20];
			System.arraycopy(licBytes, 20, encdata, 0, licBytes.length - 20);

			CEAUtil cea2 = new CEAUtil();
			int ou3 = cea2.Decrypt(encdata, signeddata, encdata.length, hashkey, 16);

			byte[] m = new byte[4];
			System.arraycopy(signeddata, 0, m, 0, 4);
			int n = bytes2int(m);

			m = new byte[4];
			System.arraycopy(signeddata, 4 + n, m, 0, 4);
			int l = bytes2int(m);

			byte[] plain = new byte[n];
			System.arraycopy(signeddata, 4, plain, 0, n);
			byte[] signature = new byte[l];
			System.arraycopy(signeddata, 4 + 4 + n, signature, 0, l);
			LicenseSignedData p7sd = new LicenseSignedData(signature);
			p7sd.update(plain, 0, n);

			if (p7sd.verify()) {
				lic = parse(plain);
				String msg = lic.getBindMsg();
				if (msg.equals(bindMsg))
					lic.setGuid(guid);
				else {
					FileUtil.deleteFile(Constants.LICENSE_PATH + Constants.LICENSE);
					throw new Exception("bind msg not match license");
				}

			} else {
				FileUtil.deleteFile(Constants.LICENSE_PATH + Constants.LICENSE);
				throw new Exception("verify signature false");
			}
			long curTime = DateUtil.getCurrentTime();
			if (curTime < lic.getValidTime_start().getTime() || curTime > lic.getValidTime_end().getTime()) {
				FileUtil.deleteFile(Constants.LICENSE_PATH + Constants.LICENSE);
				throw new Exception("not in the period of validity");
			}

			return lic;
		} catch (Exception e) {
			LoggerUtil.errorlog("check license error, ", e);
			return null;
		}

	}
	/**
	 * 
	 * @param bindMsg
	 * @param licBytes
	 * @return
	 * @throws Exception
	 */
	public static LicenseInfo checkLicenseNoDelete(String bindMsg, byte[] licBytes) {
		try {
			if (StringUtil.isBlank(bindMsg))
				throw new Exception("bound message should not be empty");
			
			LicenseInfo lic = null;
			byte[] sdlen = new byte[16];
			for (int i = 0; i < sdlen.length; i++) {
				sdlen[i] = 0x00;
			}
			
			byte[] encsdlen = new byte[16];
			System.arraycopy(licBytes, 4, encsdlen, 0, 16);
			
			String guid = genLicenseApp(bindMsg);
			byte[] hashkey = hash(guid.getBytes());
			
			CEAUtil cea = new CEAUtil();
			int ou2 = cea.Decrypt(encsdlen, sdlen, 16, hashkey, 16);
			
			byte[] encdata = new byte[licBytes.length - 20];
			byte[] signeddata = new byte[licBytes.length - 20];
			System.arraycopy(licBytes, 20, encdata, 0, licBytes.length - 20);
			
			CEAUtil cea2 = new CEAUtil();
			int ou3 = cea2.Decrypt(encdata, signeddata, encdata.length, hashkey, 16);
			
			byte[] m = new byte[4];
			System.arraycopy(signeddata, 0, m, 0, 4);
			int n = bytes2int(m);
			
			m = new byte[4];
			System.arraycopy(signeddata, 4 + n, m, 0, 4);
			int l = bytes2int(m);
			
			byte[] plain = new byte[n];
			System.arraycopy(signeddata, 4, plain, 0, n);
			byte[] signature = new byte[l];
			System.arraycopy(signeddata, 4 + 4 + n, signature, 0, l);
			LicenseSignedData p7sd = new LicenseSignedData(signature);
			p7sd.update(plain, 0, n);
			
			if (p7sd.verify()) {
				lic = parse(plain);
				String msg = lic.getBindMsg();
				if (msg.equals(bindMsg))
					lic.setGuid(guid);
				else {
					throw new Exception("bind msg not match license, bind card:" +bindMsg +" license card:" + msg);
				}
				
			} else {
				throw new Exception("verify signature false");
			}
			long curTime = DateUtil.getCurrentTime();
			if (curTime < lic.getValidTime_start().getTime() || curTime > lic.getValidTime_end().getTime()) {
				throw new Exception("not in the period of validity");
			}
			
			return lic;
		} catch (Exception e) {
			LoggerUtil.errorlog("check license error, ", e);
			return null;
		}
		
	}

	public static void main(String[] args) throws Exception {
		LicenseInfo i = checkLicense(NetWorkUtil.getHostMac(ConfigUtil.getInstance().getNetworkCard()));
		System.out.println(i);
	}
}
