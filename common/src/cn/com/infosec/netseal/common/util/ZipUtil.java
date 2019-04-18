package cn.com.infosec.netseal.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * 通过Java的Zip输入输出流实现压缩和解压文件
 * 
 * 
 */
public class ZipUtil {

	/**
	 * 压缩
	 * 
	 * @param filePath
	 * @param zipPath
	 * @param pwd
	 * @throws Exception
	 */
	public static void zip(String filePath, String zipPath, String pwd) {
		try {
			ZipFile zipFile = new ZipFile(zipPath);
			ArrayList fileList = new ArrayList();
			setFileToList(filePath, fileList);

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			if (StringUtil.isNotBlank(pwd)) {
				parameters.setEncryptFiles(true);
				parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
				parameters.setPassword(pwd);
			}
			zipFile.addFiles(fileList, parameters);
		} catch (ZipException e) {
			throw new NetSealRuntimeException(ErrCode.FILE_OPERATION_ERROR, e.getMessage());
		}
	}
	
	/**
	 *	压缩文件夹
	 * 
	 * @param dirPath
	 * @param zipPath
	 * @param pwd
	 */
	public static void zipDir(String dirPath, String zipPath, String pwd) {
		try {
			ZipFile zipFile = new ZipFile(zipPath);

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			if (StringUtil.isNotBlank(pwd)) {
				parameters.setEncryptFiles(true);
				parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
				parameters.setPassword(pwd);
			}
			File df = new File(dirPath);
			zipFile.addFolder(df, parameters);
		} catch (ZipException e) {
			throw new NetSealRuntimeException(ErrCode.FILE_OPERATION_ERROR, e.getMessage());
		}
	}
	

	/**
	 * 解压
	 * 
	 * @param zipPath
	 * @param filePath
	 * @param pwd
	 * @throws Exception
	 */
	public static void unZip(String zipPath, String filePath, String pwd) {
		try {
			ZipFile zipFile = new ZipFile(zipPath);
			zipFile.setFileNameCharset("GBK"); 
			if (StringUtil.isBlank(pwd)) {
				zipFile.extractAll(filePath);
			} else {
				if (zipFile.isEncrypted())
					zipFile.setPassword(pwd);

				List fileHeaderList = zipFile.getFileHeaders();
				for (Object fileHeader : fileHeaderList)
					zipFile.extractFile((FileHeader) fileHeader, filePath);
			}
		} catch (ZipException e) {
			throw new NetSealRuntimeException(ErrCode.FILE_OPERATION_ERROR, e.getMessage());
		}
	}

	/**
	 * 递归
	 * 
	 * @param filePath
	 * @param fileList
	 */
	private static void setFileToList(String filePath, ArrayList fileList) {
		File file = new File(filePath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files)
				setFileToList(f.getPath(), fileList);
		} else
			fileList.add(file);
	}

	public static void main(String[] args) {
		String zipPath = "f:/temp/zip.zip";
		String filePath = "F:/temp/pdf_stamp/A_stamp/";

		ZipUtil.zipDir(filePath, zipPath, "");
		System.out.println("ok");
	}
}