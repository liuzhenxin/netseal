package cn.com.infosec.netsigninterface.util;

import java.security.Security;
import java.util.Hashtable;

import cn.com.infosec.jce.provider.InfosecProvider;

/**
 * <p>
 * Title: Crls 对象，用来存储crl
 * </p>
 * <p>
 * Description: 使用说明:
 * </p>
 * <p>
 * X509CRL crl;
 * </p>
 * <p>
 * String dirpath="D://jp//PROJECT//NetsignServer//NetsignDemo//data";
 * </p>
 * </p> Crls crls=new Crls(dirpath);</p>
 * <p>
 * Hashtable ct= crls.getCRLs();
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class Crls {
	static {
		Security.addProvider( new InfosecProvider() );
	}

	private static Hashtable crls = new Hashtable();

	/**
	 *构造函数
	 * 
	 * @param crldirpath
	 *          crl存放路径
	 */
	public Crls() {

	}

	public void setCRLs( Hashtable crlnew ) {
		crls.clear();
		crls = crlnew;
	}

	/**
	 * 获得crl对象集合
	 * 
	 * @return Hashtable 一个 crl的键值对 键--crl文件名 值--crl对象
	 */
	public Hashtable getCRLs() {

		return crls;
	}

}