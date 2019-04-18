package cn.com.infosec.netseal.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * 通过Java的Zip输入输出流实现压缩和解压文件
 * 
 * 
 */
public class ZipNoPwdUtil {

	/**
	 * 压缩文件
	 * 
	 * @param filePath
	 *            待压缩的文件路径
	 * @return 压缩后的文件
	 */
	public static File zip(String filePath) {
		File target = null;
		File source = new File(filePath);
		if (source.exists()) {
			// 压缩文件名=源文件名.zip
			String zipName = source.getName() + ".zip";
			target = new File(source.getParent(), zipName);
			if (target.exists())
				target.delete(); // 删除旧的文件

			FileOutputStream fos = null;
			ZipOutputStream zos = null;
			try {
				fos = new FileOutputStream(target);
				zos = new ZipOutputStream(fos);
				// 添加对应的文件Entry
				addEntry("", source, zos);
			} catch (IOException e) {
				throw new NetSealRuntimeException(ErrCode.FILE_OPERATION_ERROR, e.getMessage());
			} finally {
				IOUtil.closeQuietly(zos, fos);
			}
		}
		return target;
	}

	/**
	 * 扫描添加文件Entry
	 * 
	 * @param base
	 *            基路径
	 * 
	 * @param source
	 *            源文件
	 * @param zos
	 *            Zip文件输出流
	 * @throws IOException
	 */
	private static void addEntry(String base, File source, ZipOutputStream zos) throws IOException {
		// 按目录分级，形如：/aaa/bbb.txt
		String entry = base + source.getName();
		if (source.isDirectory()) {
			for (File file : source.listFiles())
				// 递归列出目录下的所有文件，添加文件Entry
				addEntry(entry + Constants.SPLIT_DIR, file, zos);
		} else {
			FileInputStream fis = null;
			try {
				byte[] buffer = new byte[1024 * 10];
				fis = new FileInputStream(source);
				int read = 0;
				zos.putNextEntry(new ZipEntry(entry));
				while ((read = fis.read(buffer, 0, buffer.length)) != -1)
					zos.write(buffer, 0, read);
				zos.closeEntry();
			} catch (IOException e) {
				throw new NetSealRuntimeException(ErrCode.FILE_OPERATION_ERROR, e.getMessage());
			} finally {
				IOUtil.closeQuietly(fis);
			}
		}
	}

	/**
	 * 解压文件
	 * 
	 * @param filePath
	 *            压缩文件路径
	 */
	public static void unzip(String filePath) {
		File source = new File(filePath);
		if (source.exists()) {
			ZipInputStream zis = null;
			FileOutputStream bos = null;
			try {
				zis = new ZipInputStream(new FileInputStream(source));
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
					File target = new File(Constants.ROOT_PATH, entry.getName());
					if (!target.getParentFile().exists()) {
						// 创建文件父目录
						target.getParentFile().mkdirs();
					}
					// 写入文件
					bos = new FileOutputStream(target);
					int read = 0;
					byte[] buffer = new byte[1024 * 10];
					while ((read = zis.read(buffer, 0, buffer.length)) != -1)
						bos.write(buffer, 0, read);
					bos.flush();
				}
				zis.closeEntry();
			} catch (IOException e) {
				throw new NetSealRuntimeException(ErrCode.FILE_OPERATION_ERROR, e.getMessage());
			} finally {
				IOUtil.closeQuietly(zis, bos);
			}
		}
	}

	public static void main(String[] args) {
		File file = ZipNoPwdUtil.zip(Constants.KEY_PATH);
		ZipNoPwdUtil.unzip(Constants.ROOT_PATH + "key.zip");
	}
}