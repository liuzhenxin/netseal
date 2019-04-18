package cn.com.infosec.netsigninterface.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * �����ļ��ĺ�׺�����й���
 * <p>
 * Title: FileExtensionFilter
 * </p>
 * <p>
 * Description: ʵ��java.io.FilenameFilter�ӿ�
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
	 * �����µ��ļ�������
	 * 
	 * @param fileNameExtendtion
	 *          �ļ���׺�������� *.pfx
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