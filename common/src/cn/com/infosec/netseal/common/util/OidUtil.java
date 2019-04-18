package cn.com.infosec.netseal.common.util;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class OidUtil {

	public static final String OID_MD2 = "1.2.840.113549.2.2";

	public static final String OID_MD5 = "1.2.840.113549.2.5";

	public static final String OID_SHA1 = "1.3.14.3.2.26";

	public static final String OID_SHA224 = "2.16.840.1.101.3.4.2.4";

	public static final String OID_SHA256 = "2.16.840.1.101.3.4.2.1";

	public static final String OID_SHA384 = "2.16.840.1.101.3.4.2.2";

	public static final String OID_SHA512 = "2.16.840.1.101.3.4.2.3";

	public static final String OID_RSA = "1.2.840.113549.1.1.1";

	public static final String OID_MD2withRSA = "1.2.840.113549.1.1.2";

	public static final String OID_MD5withRSA = "1.2.840.113549.1.1.4";

	public static final String OID_SHA1withRSA = "1.2.840.113549.1.1.5";

	public static final String OID_SHA256withRSA = "1.2.840.113549.1.1.11";

	public static final String OID_SM3withSM2 = "1.2.156.10197.1.501";

	public static String getHashAlg(String signAlgOid) {
		if (OidUtil.OID_SHA1withRSA.equals(signAlgOid))
			return Constants.SHA1;
		else if (OidUtil.OID_SHA256withRSA.equals(signAlgOid))
			return Constants.SHA256;
		else if (OidUtil.OID_SM3withSM2.equals(signAlgOid))
			return Constants.SM3;
		else
			throw new NetSealRuntimeException(ErrCode.UNKNOWN_ALGORITHM, "unknown sign oid: " + signAlgOid);
	}

	public static String getSignAlg(String signAlgOid) {
		if (OidUtil.OID_SHA1withRSA.equals(signAlgOid))
			return Constants.SHA1_RSA;
		else if (OidUtil.OID_SHA256withRSA.equals(signAlgOid))
			return Constants.SHA256_RSA;
		else if (OidUtil.OID_SM3withSM2.equals(signAlgOid))
			return Constants.SM3_SM2;
		else
			throw new NetSealRuntimeException(ErrCode.UNKNOWN_ALGORITHM, "unknown sign oid: " + signAlgOid);
	}

	public static String getSymAlg(String signAlgOid) {
		if (OidUtil.OID_SHA1withRSA.equals(signAlgOid))
			return Constants.AES;
		else if (OidUtil.OID_SHA256withRSA.equals(signAlgOid))
			return Constants.AES;
		else if (OidUtil.OID_SM3withSM2.equals(signAlgOid))
			return Constants.SM4;
		else
			throw new NetSealRuntimeException(ErrCode.UNKNOWN_ALGORITHM, "unknown sign oid: " + signAlgOid);
	}
}
