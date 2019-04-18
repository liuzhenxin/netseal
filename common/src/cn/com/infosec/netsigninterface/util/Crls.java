package cn.com.infosec.netsigninterface.util;

import java.security.Security;
import java.util.Hashtable;

import cn.com.infosec.jce.provider.InfosecProvider;

/**
 * <p>
 * Title: Crls ���������洢crl
 * </p>
 * <p>
 * Description: ʹ��˵��:
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
	 *���캯��
	 * 
	 * @param crldirpath
	 *          crl���·��
	 */
	public Crls() {

	}

	public void setCRLs( Hashtable crlnew ) {
		crls.clear();
		crls = crlnew;
	}

	/**
	 * ���crl���󼯺�
	 * 
	 * @return Hashtable һ�� crl�ļ�ֵ�� ��--crl�ļ��� ֵ--crl����
	 */
	public Hashtable getCRLs() {

		return crls;
	}

}