package cn.com.infosec.netsigninterface.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 按照文件的后缀名进行过滤
 * <p>
 * Title: FileExtensionFilter
 * </p>
 * <p>
 * Description: 实现java.io.FilenameFilter接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Infosec
 * </p>
 * 
 * @author lixiangfeng
 * @version 1.0
 */
public class FileExtensionFilter implements FilenameFilter {
	private String _fileNameExtendtion;

	/**
	 * 构造新的文件过滤器
	 * 
	 * @param fileNameExtendtion
	 *          文件后缀名，例如 *.pfx
	 */
	public FileExtensionFilter( String fileNameExtendtion) {
		this._fileNameExtendtion = fileNameExtendtion;
	}

	public boolean accept( File dir , String name ) {
		if( name.endsWith( _fileNameExtendtion ) )
			return true;
		return false;
	}
}