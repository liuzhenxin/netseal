/*
 * Created on 2005-3-1
 *
 *  
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cn.com.infosec.netsigninterface.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩&解压缩byte数组
 * @author zhoupeng
 * @version 1.0
 */
public class GZipUtil {
	/**
	 * 压缩byte数组，并且返回压缩后的数据
	 * @param _bytes 需要被压缩的数据
	 * @return 压缩后的数据
	 * @throws IOException
	 */
	public static byte[] zip(byte[] _bytes) throws IOException {
		BufferedOutputStream bufOut = null;		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		bufOut = new BufferedOutputStream(
				new GZIPOutputStream(byteOut));
		bufOut.write(_bytes);
		bufOut.close();
		return byteOut.toByteArray();		
	}
	
	/**
	 * 解压缩byte数组，并且返回解压缩后的数据
	 * @param _bytes 需要被解压缩的数据
	 * @return 解压缩后的数据
	 * @throws IOException
	 */
	public static byte[] unzip(byte[] _bytes) throws IOException {
		ByteArrayOutputStream byteOut = null;
		BufferedInputStream bufIn = null;
		try {
			bufIn = new BufferedInputStream(
					new GZIPInputStream(
					new ByteArrayInputStream(_bytes)));
			byteOut = new ByteArrayOutputStream(1024);
			byte[] byteBuf = new byte[1024];
			int len = 0;
			while ((len = bufIn.read(byteBuf, 0, byteBuf.length)) != -1) {
				byteOut.write(byteBuf, 0, len);
			}			
			return byteOut.toByteArray();
		} finally {	
			try {
				if (byteOut != null)
					byteOut.close();
				if (bufIn != null)
					bufIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			byte[] zippedBytes = zip("Test".getBytes());
			byte[] unzippedBytes = unzip(zippedBytes);
			zippedBytes = zip(unzippedBytes);
			System.out.println(new String(unzip(zippedBytes)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
