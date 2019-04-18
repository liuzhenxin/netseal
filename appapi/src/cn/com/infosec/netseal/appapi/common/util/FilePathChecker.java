package cn.com.infosec.netseal.appapi.common.util;

import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.netseal.appapi.ErrCode;
import cn.com.infosec.netseal.appapi.common.define.Constants;

/**
 * 文件路径校验工具
 *
 */
public class FilePathChecker {

	private static List<String> list = new ArrayList<String>();

	static {
		list.add(Constants.PHOTO_SUFFIX);
		list.add(Constants.PDF_SUFFIX);
		list.add(Constants.CERT_SUFFIX);
		list.add(Constants.CERT_CHAIN_SUFFIX);
		list.add(Constants.SEAL_SUFFIX);
		list.add(Constants.STAMP_SUFFIX);
		list.add(Constants.TMP_SUFFIX);
		list.add(Constants.PFX_SUFFIX);
		list.add(Constants.JKS_SUFFIX);
		list.add(Constants.PRI_SUFFIX);
		list.add(Constants.KEY_SUFFIX);
		list.add(Constants.CRL_SUFFIX);
		list.add(Constants.CSR_SUFFIX);
		list.add(Constants.ZIP_SUFFIX);
		list.add(Constants.LICENSE_SUFFIX);
		list.add(Constants.LOG_SUFFIX);
	}

	public static void checkFileSuffix(String filePath) throws Exception {
		// 校验文件名后缀
		boolean checkSuffix = false;
		List<String> suffixList = FilePathChecker.list;
		for (int i = 0; i < suffixList.size(); i++) {
			if (filePath.endsWith(suffixList.get(i))) {
				checkSuffix = true;
				break;
			}
		}

		if (!checkSuffix)
			throw new Exception("file suffix is invalid, file path is " + filePath);
	}
}
