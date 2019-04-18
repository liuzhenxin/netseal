/**
 * 
 */
package cn.com.infosec.netseal.common.rads.map;

import java.util.HashMap;

/**
 * @ClassName: CertMap
 * @Description: TODO
 * @author xuchy
 * @version 6.1.001.1
 * @date 2013-4-7 下午05:33:44
 */

public class CertMap extends HashMap<String, String>{

	public static final String CERTDN = "CERTDN";
	public static final String CERTSN = "CERTSN";
	public static final String TEMPLATE = "TEMPLATE";
	public static final String NOTBEFORE = "NOTBEFORE";
	public static final String REFNO = "REFNO";
	public static final String AUTHCODE = "AUTHCODE";
	public static final String NOTAFTER = "NOTAFTER";
	public static final String STATUS = "STATUS";
	public static final String RSA_TMP_PUB_KEY = "RSA_TMP_PUB_KEY";
	public static final String PUBLICKEY = "PUBLICKEY";
	public static final String P7DATA = "P7DATA";
	public static final String VALIDITYLEN = "VALIDITYLEN";
	public static final String UUID = "UUID";
	public static final String CAID = "CAID";
	public static final String NAME = "NAME";
	public static final String TELEPHONE = "TELEPHONE";
	public static final String MAIL = "MAIL";
	public static final String USERSORT = "USERSORT";
	public static final String USERNO = "USERNO";
	public static final String POST = "POST";
	public static final String BSTR = "BSTR";
	public CertMap()
	{
		put("CERTDN", null);
		put("CERTSN", null);
		put("TEMPLATE", null);
		put("NOTBEFORE", "0");
		put("NOTAFTER", "0");
		put("STATUS", "-1");
		put("RSA_TMP_PUB_KEY", null);
		put("PUBLICKEY", null);
		put("P7DATA", null);
		put("REFNO", null);
		put("AUTHCODE", null);
		put("UUID", null);
		put("VALIDITYLEN", null);
		put("CAID", null);
		put("NAME", null);
		put("USERSORT", null);
		put("RAID", null);
		put("TELEPHONE", null);
		put("MAIL", null);
		put("USERNO", null);
		put("POST", null);
		put("BSTR",null);

	}
}
