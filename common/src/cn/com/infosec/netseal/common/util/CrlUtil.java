package cn.com.infosec.netseal.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class CrlUtil {

	private static final BigInteger TEN = BigInteger.TEN;
	private static List<List> certSnList = new ArrayList<List>();

	static {
		Security.addProvider(new InfosecProvider());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());

		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
		certSnList.add(new ArrayList<BigInteger>());
	}

	/**
	 * 将证书编号存入内存中
	 * 
	 * @param certSn
	 */
	private synchronized static void store(BigInteger certSn) {
		if (certSn == null)
			return;

		List list = certSnList.get(certSn.mod(TEN).intValue());
		list.add(certSn);
	}

	/**
	 * 查看证书是否作废
	 * 
	 * @param certSn
	 * @return
	 */
	public static boolean contain(BigInteger certSn) {
		if (certSn == null)
			return false;

		String path = Constants.CRL_TO_PATH + calcFileName(certSn);
		return FileUtil.contain(path, StringUtil.padLeft(certSn.toByteArray(), Constants.LENGTH_TWENTY));
	}

	/**
	 * 查看证书是否在特定时间之前已作废
	 * 
	 * @param certSn
	 * @param time
	 * @return
	 */
	public static boolean containInDate(BigInteger certSn, long time) {
		if (certSn == null)
			return false;

		String path = Constants.CRL_TO_PATH + calcFileName(certSn);
		return FileUtil.containInDate(path, StringUtil.padLeft(certSn.toByteArray(), Constants.LENGTH_TWENTY), time);
	}

	/**
	 * 清空内存
	 */
	private synchronized static void clear() {
		for (int i = 0; i < certSnList.size(); i++) {
			certSnList.get(i).clear();
		}
	}

	/**
	 * 从crl中取得sn保存到文件
	 */
	public static void storeSnAndDate() {
		// 清空內存
		clear();

		File file = new File(Constants.CRL_FROM_PATH);
		File[] files = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(Constants.CRL_SUFFIX);
			}
		});

		for (File f : files) {
			transAndStore(f.getPath());
		}
	}

	/**
	 * 获取CRL文件中证书编号
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private static void transAndStore(String filePath) {
		byte[] data = FileUtil.getFile(filePath);

		X509CRL x509crl = null;
		try {
			CertificateFactory crlFact = CertificateFactory.getInstance("X.509", "INFOSEC");
			x509crl = (X509CRL) crlFact.generateCRL(new ByteArrayInputStream(data));

			Object[] os = x509crl.getRevokedCertificates().toArray();
			for (int i = 0; i < os.length; i++) {
				BigInteger sn = ((X509CRLEntry) os[i]).getSerialNumber();
				byte[] snData = StringUtil.padLeft(sn.toByteArray(), Constants.LENGTH_TWENTY);
				byte[] timeData = HexUtil.long2Byte(((X509CRLEntry) os[i]).getRevocationDate().getTime());

				byte[] allData = new byte[snData.length + timeData.length];
				System.arraycopy(snData, 0, allData, 0, snData.length);
				System.arraycopy(timeData, 0, allData, snData.length, timeData.length);

				FileUtil.storeFile(Constants.CRL_TO_PATH + calcFileName(sn), allData, true);
			}
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CERT_DATA_INVAILD, "get sn from crl error, " + e.getMessage());
		}
	}

	/**
	 * 根据sn计算文件名
	 * 
	 * @param sn
	 * @return
	 */
	private static String calcFileName(BigInteger sn) {
		byte[] data = sn.toByteArray();
		byte[] bs = new byte[data.length - 1];
		System.arraycopy(data, 0, bs, 0, bs.length);
		return HexUtil.byte2Hex(bs) + Constants.CRL_SUFFIX;
	}

	public static void main(String[] args) {
		byte[] snData = StringUtil.padLeft(BigInteger.TEN.toByteArray(), Constants.LENGTH_TWENTY);
		byte[] timeData = HexUtil.long2Byte(System.currentTimeMillis());

		byte[] allData = new byte[snData.length + timeData.length];
		System.arraycopy(snData, 0, allData, 0, snData.length);
		System.arraycopy(timeData, 0, allData, snData.length, timeData.length);

		System.out.println(HexUtil.byte2Hex(snData));
		System.out.println(HexUtil.byte2Hex(timeData));
		System.out.println(HexUtil.byte2Hex(allData));
	}
}
