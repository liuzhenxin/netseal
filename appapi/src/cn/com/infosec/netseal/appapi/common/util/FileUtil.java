package cn.com.infosec.netseal.appapi.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import javax.imageio.ImageIO;

import cn.com.infosec.netseal.appapi.common.define.Constants;

public class FileUtil {

	/**
	 * 校验文件路径
	 * 
	 * @param filePath
	 * @throws Exception 
	 */
	public static boolean checkPath(String filePath) throws Exception {
		if (StringUtil.isBlank(filePath))
			throw new Exception("path invaild, path is " + filePath);

		File file = new File(filePath);
		if (file.isFile())
			FilePathChecker.checkFileSuffix(filePath);

		return file.exists();
	}

	/**
	 * 根据文件路径创建绝对路径
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static void createDir(String filePath) {
		String dirPath = filePath.substring(0, filePath.lastIndexOf(Constants.SPLIT_DIR));

		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
			// file.setExecutable(true, false);
			// file.setReadable(true, false);
			// file.setWritable(true, false);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists())
			file.delete();
	}

	/**
	 * 删除目录
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static void deleteDir(String dirPath) {
		File file = new File(dirPath);
		File[] fs = file.listFiles();
		if (fs == null)
			return;

		for (File f : fs) {
			if (f.isFile())
				deleteFile(f.getPath());

			if (f.isDirectory())
				deleteDir(f.getPath());
		}

		deleteFile(dirPath);
	}

	/**
	 * 删除目录
	 * 
	 * @param dirPath目录
	 * @param excludePath
	 *            排除目录或文件的路径
	 */
	public static void deleteDir(String dirPath, String excludePath) {
		File excludeFile = new File(excludePath);
		File dirFile = new File(dirPath);

		File[] fs = dirFile.listFiles();
		if (fs == null)
			return;

		for (File f : fs) {
			if (!f.getAbsolutePath().equals(excludeFile.getAbsolutePath())) {
				if (f.isDirectory()) {
					deleteDir(f.getPath(), excludePath);
				}
				f.delete();
			}
		}
	}

	/**
	 * 删除文件及所在目录
	 * 
	 * @param path
	 */
	public static void deleteFileAndDir(String filePath) {
		File file = new File(filePath);
		deleteDir(file.getParent());
	}

	
	/**
	 * 获取文件数据
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] getFile(String filePath) throws Exception {
		File f = new File(filePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			byte[] bs = new byte[fis.available()];
			fis.read(bs);

			return bs;
		} catch (Exception e) {
			throw new Exception("get file data error, " + e.getMessage() + " file path is " + filePath);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}
	}


	/**
	 * 保存文件
	 * 
	 * @param filePath
	 * @param data
	 * @throws Exception
	 */
	public static String storeFile(String filePath, byte[] data) throws Exception {
		createDir(filePath);

		File f = new File(filePath);
		if (!f.exists())
			try {
				f.createNewFile();
				// f.setExecutable(true, false);
				// f.setReadable(true, false);
				// f.setWritable(true, false);
			} catch (Exception e) {
				throw new Exception("create file error, " + e.getMessage() + " file path is " + filePath);
			}

		if (data != null) {
			FileOutputStream fous = null;
			try {
				fous = new FileOutputStream(f);
				fous.write(data);
				fous.flush();
			} catch (Exception e) {
				throw new Exception("store file error, " + e.getMessage() + " file path is " + filePath);
			} finally {
				try {
					if (fous != null)
						fous.close();
				} catch (Exception e) {
				}
			}
		}
		return filePath;
	}

	/**
	 * 保存文件
	 * 
	 * @param filePath
	 * @param data
	 * @throws Exception
	 */
	public static String storeFile(String filePath, byte[] data, boolean append) throws Exception {
		createDir(filePath);

		File f = new File(filePath);
		if (!f.exists())
			try {
				f.createNewFile();
				// f.setExecutable(true, false);
				// f.setReadable(true, false);
				// f.setWritable(true, false);
			} catch (Exception e) {
				throw new Exception("create file error, " + e.getMessage() + " file path is " + filePath);
			}

		if (data != null) {
			FileOutputStream fous = null;
			try {
				fous = new FileOutputStream(f, append);
				fous.write(data);
				fous.flush();
			} catch (Exception e) {
				throw new Exception("store file error, " + e.getMessage() + " file path is " + filePath);
			} finally {
				try {
					if (fous != null)
						fous.close();
				} catch (Exception e) {
				}
			}
		}
		return filePath;
	}

	/**
	 * 产生图片
	 * 
	 * @param path
	 * @param file
	 * @param content
	 * @throws Exception 
	 */
	public static void createImage(String filePath, String content) throws Exception {
		File file = new File(filePath);
		try {
			Font font = new Font("Serif", Font.BOLD, 10);
			BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) bi.getGraphics();
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, 100, 100);
			g2.setPaint(Color.BLACK);

			FontRenderContext context = g2.getFontRenderContext();
			Rectangle2D bounds = font.getStringBounds(content, context);
			double x = (100 - bounds.getWidth()) / 2;
			double y = (100 - bounds.getHeight()) / 2;
			double ascent = -bounds.getY();
			double baseY = y + ascent;

			g2.drawString(content, (int) x, (int) baseY);
			ImageIO.write(bi, "jpg", file);
		} catch (Exception e) {
			throw new Exception("create image file error, " + e.getMessage() + " file path is " + filePath);
		}
	}

	/**
	 * 产生文件名
	 * 
	 * @return
	 */
	public static String getFileName() {
		byte[] bs = new byte[20];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(bs);
		return HexUtil.byte2Hex(bs);
	}

	


	public static void main(String[] args) {
		deleteFileAndDir("f:/temp/test/test1/1.txt");
	}

}
