package cn.com.infosec.netseal.common.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {

	private FTPClient ftp;
	private static final String ENCODE = "GBK";

	private FtpUtil() {
	}

	public FtpUtil(String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) throws Exception {
		ftp = new FTPClient();

		ftp.setConnectTimeout(10 * 1000);// 秒
		ftp.connect(ftpHost, ftpPort);// 连接FTP服务器

		ftp.setSoTimeout(30 * 1000); // 秒
		ftp.setSoLinger(true, 0);
		ftp.setTcpNoDelay(true);

		ftp.login(ftpUserName, ftpPassword);// 登陆FTP服务器

		if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			ftp.disconnect();
			ftp = null;
			throw new Exception("login failed");
		}

	}

	/**
	 * 下载文件从FTP服务器
	 * 
	 * @param ftpPath
	 * @param ftpName
	 * @param localFile
	 * @return
	 * @throws Exception
	 */
	public boolean download(String ftpPath, String ftpName, String localFile) throws Exception {
		FileUtil.createDir(localFile);

		OutputStream out = null;
		try {
			out = new FileOutputStream(localFile);
			ftp.setControlEncoding(ENCODE); // 中文支持
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			ftp.changeWorkingDirectory(ftpPath);
			boolean result = ftp.retrieveFile(ftpPath + convert(ftpName), out);

			out.flush();
			ftp.logout();

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}

			try {
				if (ftp != null)
					ftp.disconnect();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 上传文件到FTP服务器
	 * 
	 * @param ftpPath
	 * @param ftpName
	 * @param localFile
	 * @return
	 * @throws Exception
	 */
	public boolean upload(String ftpPath, String ftpName, String localFile) throws Exception {
		InputStream in = null;

		try {
			in = new FileInputStream(localFile);
			ftp.setControlEncoding(ENCODE); // 中文支持
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			ftp.changeWorkingDirectory(ftpPath);

			boolean result = ftp.storeFile(ftpPath + ftpName, in);
			ftp.logout();

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}

			try {
				if (ftp != null)
					ftp.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查FTP上文件是否存在
	 * 
	 * @param ftpPath
	 * @param ftpName
	 * @return
	 * @throws Exception
	 */
	public boolean checkExsitFile(String ftpPath, String ftpName) throws Exception {
		boolean result = false;
		try {
			ftp.setControlEncoding(ENCODE); // 中文支持
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			ftp.changeWorkingDirectory(ftpPath);

			FTPFile[] files = ftp.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals(ftpName)) {
					result = true;
					break;
				}
			}

			ftp.logout();
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (ftp != null)
					ftp.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String convert(String name) throws Exception {
		return new String(name.getBytes(ENCODE), "iso8859-1");
	}

	public static void main(String[] args) throws Exception {
		FtpUtil ftp = new FtpUtil("10.20.61.50", 21, "test", "test");

		boolean result = ftp.download("", "test.pdf", "f:/temp/test.pdf");
		// boolean result = ftp.upload("", "test.txt", "f:/temp/", "1.sql");

		// boolean result = ftp.checkExsitFile("", "test.pdf");
		System.out.println(result);
	}

}
