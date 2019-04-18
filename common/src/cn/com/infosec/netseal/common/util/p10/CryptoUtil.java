/*
 * 创建日期 2005-7-14
 *
 *   要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package cn.com.infosec.netseal.common.util.p10;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERObject;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DEROutputStream;
import cn.com.infosec.crypto.CryptoException;
import cn.com.infosec.util.Base64;

/**
 * @author hyoffice
 * 
 *         要更改此生成的类型注释的模板，请转至 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class CryptoUtil {

	public static boolean debug = false;

	static byte JNI_RSA_KEY_NAME = 0x10;

	static byte JNI_RSA_PLAIN_DATA = 0x20;

	static byte JNI_RSA_SIGNATURE = 0x40;

	static byte JNI_RSA_KEY_PARM_N = 0x01;

	static byte JNI_RSA_KEY_PARM_KE = 0x02;

	static byte JNI_RSA_ENCHASH = 0x41;

	static byte JNI_RSA_DECHASH = 0x42;

	static byte JNI_SYM_ENCSECKEY = 0x43;

	static byte JNI_SYM_ENC_DATA = 0x44;

	static byte JNI_SYM_ALG_IV = 0x45;

	final static String cn = "2.5.4.3";

	private static final String BEGIN_CERT_REQ = "-----BEGIN CERTIFICATE REQUEST-----";

	/** End certificate signing request */
	private static final String END_CERT_REQ = "-----END CERTIFICATE REQUEST-----";

	/** The maximum length of lines in certificate signing requests */
	private static final int CERT_REQ_LINE_LENGTH = 76;

	private static final long DATE_2050 = 2524579200000l;

	public static final byte[] DERNULL = new byte[] { (byte) 05, (byte) 00 };

	public static final byte[] DERTRUE = new byte[] { (byte) 01, (byte) 01, (byte) 0xff };

	/**
	 * 
	 */
	public CryptoUtil() {
		super();
		// 自动生成构造函数存根
	}

	public static String getExtern(byte[] a) throws Exception {
		String externvalue = null;
		try {
			ByteArrayInputStream bint = new ByteArrayInputStream(a);
			DERInputStream dint = new DERInputStream(bint);
			DEROctetString doct = (DEROctetString) dint.readObject();
			byte[] tmp = doct.getOctets();
			bint = new ByteArrayInputStream(tmp);
			dint = new DERInputStream(bint);
			DERObject dobj = dint.readObject();
			Class dc = dobj.getClass();
			Class c1 = dc.forName(dc.getName());
			Method m = c1.getMethod("getString", null);
			String tmp1 = (String) m.invoke(dobj, null);
			externvalue = tmp1;
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return externvalue;
	}

	private static byte[] cnid = new byte[] { 0x55, 0x04, 0x03 };

	public static ArrayList getcrldp(byte[] a) throws IOException {
		ArrayList cnlist = new ArrayList();
		if ((a != null) && (a.length > 0)) {
			for (int i = 0, ilength = a.length; i < ilength; i++) {
				boolean match = false;
				for (int j = 0, jlength = cnid.length; j < jlength; j++) {
					if ((i + j) < ilength) {
						if (a[i + j] == cnid[j]) {
							match = true;
						} else {
							match = false;
							i += j;
							break;
						}
					}
				}
				if (match) {
					i += 4;
					int clength = 0xff & a[i];
					i += 1;
					byte[] cnbs = new byte[clength];
					System.arraycopy(a, i, cnbs, 0, clength);
					String cnstr = new String(cnbs);
					// System.out.println( cnstr );
					cnlist.add(cnstr);
					i += clength;
				}
			}
		}
		return cnlist;
	}

	/*
	 * public static String getcrldp( byte[] a ) throws IOException { if( a == null ) return null; String crldp = null; ByteArrayInputStream bint = new ByteArrayInputStream( a ); DERInputStream dint =
	 * new DERInputStream( bint ); DEROctetString doct = ( DEROctetString ) dint.readObject(); byte[] tmp = doct.getOctets(); bint = new ByteArrayInputStream( tmp ); dint = new DERInputStream( bint );
	 * // CRLDistPoint crl=CRLDistPoint.getInstance(dint); DERConstructedSequence dconstr = ( DERConstructedSequence ) ( dint .readObject() ); //System.out.print( "de=" + dconstr.size() ); for( int i
	 * = 0 ; i < dconstr.size() ; i++ ) { DERConstructedSequence dconstr0 = ( DERConstructedSequence ) ( dconstr .getObjectAt( 0 ) );
	 * 
	 * DERTaggedObject dert = ( DERTaggedObject ) dconstr0.getObjectAt( 0 ); DERTaggedObject dert1 = ( DERTaggedObject ) dert.getObject();
	 * 
	 * DERTaggedObject dert2 = ( DERTaggedObject ) dert1.getObject(); if( dert2.getTagNo() == 4 ) { DERConstructedSequence dconstr2 = ( DERConstructedSequence ) dert2 .getObject(); DERConstructedSet
	 * dconsettmp; DERConstructedSequence dconsequencetmp; for( int j = 0 ; j < dconstr2.size() ; j++ ) { dconsettmp = ( DERConstructedSet ) dconstr2.getObjectAt( j ); dconsequencetmp = (
	 * DERConstructedSequence ) dconsettmp .getObjectAt( 0 ); if( cn.equals( ( ( DERObjectIdentifier ) dconsequencetmp .getObjectAt( 0 ) ).getId() ) ) { crldp = ( ( DERString )
	 * dconsequencetmp.getObjectAt( 1 ) ) .getString().toLowerCase(); } } } // else // { // System.out.println("crl dp type is url"); // // System.out.println(new String( //
	 * ((DEROctetString)dert2.getObject()).getOctets())); // // System.out.println( dert2.getObject()); // } } return crldp;
	 * 
	 * }
	 */

	public static boolean compereDN(String DN1, String JKSDN) {
		if ((DN1 == null) || (JKSDN == null))
			return false;
		if (DN1.equals(JKSDN))
			return true;
		else {
			String[] temp = JKSDN.split(",");
			String turnDN = temp[temp.length - 1].trim();

			for (int i = (temp.length - 2); i >= 0; i--) {
				turnDN = turnDN + "," + temp[i].trim();
			}
			if (DN1.equals(turnDN))
				return true;

		}
		return false;
	}

	public static boolean compereBytes(byte[] bytes1, byte[] bytes2) {
		if (bytes1 == bytes2)
			return true;
		else {
			if ((bytes1 == null) || (bytes2 == null))
				return false;
			else if (bytes1.length != bytes2.length)
				return false;
			else {
				for (int i = 0, length = bytes1.length; i < length; i++) {
					if (bytes1[i] != bytes2[i])
						return false;
				}
				return true;
			}
		}
	}

	public static String trimDN(String dn) {
		String[] temp = dn.split(",");
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < temp.length; i++) {
			buffer.append(temp[i].trim() + ",");
		}
		String value = buffer.toString();
		return value.substring(0, value.length() - 1);
	}

	public static String turnDN(String DN) {
		String[] temp = DN.split(",");
		String turnDN = temp[temp.length - 1].trim();

		for (int i = (temp.length - 2); i >= 0; i--) {
			turnDN = turnDN + "," + temp[i].trim();
		}
		return turnDN;
	}

	public static String createbase64csr(PKCS10CertificationRequest csr) throws Exception {
		// Get Base 64 encoding of CSR
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DEROutputStream deros = new DEROutputStream(baos);
		deros.writeObject(csr.getDERObject());
		String sTmp = new String(Base64.encode(baos.toByteArray()));

		// CSR is bounded by a header and footer
		String sCsr = BEGIN_CERT_REQ + "\n";

		// Limit line lengths between header and footer
		for (int iCnt = 0; iCnt < sTmp.length(); iCnt += CERT_REQ_LINE_LENGTH) {
			int iLineLength;

			if ((iCnt + CERT_REQ_LINE_LENGTH) > sTmp.length()) {
				iLineLength = (sTmp.length() - iCnt);
			} else {
				iLineLength = CERT_REQ_LINE_LENGTH;
			}

			sCsr += sTmp.substring(iCnt, (iCnt + iLineLength)) + "\n";
		}

		// Footer
		sCsr += END_CERT_REQ + "\n";
		return sCsr;
	}

	public static byte[] constructHardData(byte tag, byte[] sourcedata) {
		int intLen = sourcedata.length;
		byte[] bytelen = getDataLength(intLen);
		byte[] result = new byte[5 + intLen];
		result[0] = tag;
		result[1] = bytelen[0];
		result[2] = bytelen[1];
		result[3] = bytelen[2];
		result[4] = bytelen[3];
		for (int i = 0; i < intLen; i++)
			result[5 + i] = sourcedata[i];
		return result;
	}

	public static byte[] getDataLength(int intLen) {
		byte[] result = new byte[4];
		result[0] = (byte) (0xff & (intLen >> 24));
		result[1] = (byte) (0xff & (intLen >> 16));
		result[2] = (byte) (0xff & (intLen >> 8));
		result[3] = (byte) (0xff & (intLen >> 0));
		return result;
	}

	public static PublicKey getPublicKey(byte[] pubkeyN, byte[] pubkeyE) throws Exception {
		try {

			// 根据PKCS1标准，N和Ｅ必须为正数，
			BigInteger biN = new BigInteger(1, pubkeyN);
			BigInteger biE = new BigInteger(1, pubkeyE);

			RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(biN, biE);
			KeyFactory fact;
			fact = KeyFactory.getInstance("RSA", "INFOSEC");
			PublicKey pubKey = fact.generatePublic(pubKeySpec);
			return pubKey;
		} catch (Exception ex) {
			String msg = "Can not convert PublicKey( N & E) bytearray to PublicKey object";
			throw new Exception(msg, ex);
		}

	}

	public static byte[] clearHardData(byte[] sourcedata) {
		byte[] byteLen = { sourcedata[1], sourcedata[2], sourcedata[3], sourcedata[4] };
		int intLen = getDataLength(byteLen);
		byte[] result = new byte[intLen];
		for (int i = 0; i < intLen; i++)
			result[i] = sourcedata[5 + i];
		return result;
	}

	public static byte[] constructHardPublicKey(PublicKey pk) throws CryptoException {
		// 将N , E 两个部分分离开
		// 格式：0x01 + length(4) + dataN + 0x02 + length(4) + dataE
		// PublicKey pk = CryptoUtil.getPublicKey("RSA", pubkey);

		RSAPublicKey rpk = (RSAPublicKey) pk;
		byte[] pk_n = rpk.getModulus().toByteArray();
		byte[] pk_e = rpk.getPublicExponent().toByteArray();

		int intLen_N = pk_n.length;
		int intLen_E = pk_e.length;

		byte[] bytePkn = getDataLength(intLen_N);
		byte[] bytePke = getDataLength(intLen_E);

		/**
		 * 长度 N的tag(1位) + N的长度描述(4位) + N的长度 + E的tag(1位) + E的长度描述(4位) + E的长度
		 */
		int totallen = 1 + 4 + intLen_N + 1 + 4 + intLen_E;

		byte[] resultPubkey = new byte[totallen];
		resultPubkey[0] = JNI_RSA_KEY_PARM_N;
		resultPubkey[1] = bytePkn[0];
		resultPubkey[2] = bytePkn[1];
		resultPubkey[3] = bytePkn[2];
		resultPubkey[4] = bytePkn[3];

		for (int i = 0; i < intLen_N; i++)
			resultPubkey[5 + i] = pk_n[i];
		/**
		 * JNI_RSA_KEY_PARM_KE N的tag下标(1位)+长度描述(4位)+ N长度 + E的tag描述(1位)
		 */
		int Pos_E = 0 + 4 + intLen_N + 1;
		resultPubkey[Pos_E] = JNI_RSA_KEY_PARM_KE;
		resultPubkey[Pos_E + 1] = bytePke[0];
		resultPubkey[Pos_E + 2] = bytePke[1];
		resultPubkey[Pos_E + 3] = bytePke[2];
		resultPubkey[Pos_E + 4] = bytePke[3];

		int curPos_E = Pos_E + 5;
		for (int i = 0; i < intLen_E; i++)
			resultPubkey[curPos_E + i] = pk_e[i];

		return resultPubkey;
	}

	public static int getDataLength(byte[] len) {
		BigInteger bi = new BigInteger(len);
		String result = bi.toString(10);
		return Integer.valueOf(result).intValue();
	}

	public static byte[][] splitHardPublicKey(byte[] hardpubkey) {
		byte[] byteLen_N = { hardpubkey[1], hardpubkey[2], hardpubkey[3], hardpubkey[4] };
		int intLen_N = getDataLength(byteLen_N);

		// System.out.println("pubkey_N length :" + intLen_N);

		// N的tag下标(1位)+长度描述(4位)+N + E的tag描述(1位)
		int Pos = 0 + 4 + intLen_N + 1;

		byte[] byteLen_E = { hardpubkey[Pos + 1], hardpubkey[Pos + 2], hardpubkey[Pos + 3], hardpubkey[Pos + 4] };
		int intLen_E = getDataLength(byteLen_E);

		// System.out.println("pubkey_E length :" + intLen_E);

		byte[] pubkey_N = new byte[intLen_N];
		byte[] pubkey_E = new byte[intLen_E];

		System.arraycopy(hardpubkey, 5, pubkey_N, 0, intLen_N);
		System.arraycopy(hardpubkey, Pos + 5, pubkey_E, 0, intLen_E);

		// try{
		// System.out.println("pubkey byte length =" + pubkey_N.length ) ;
		// FileOutputStream fo = new FileOutputStream("d:\\temp\\pubkey_N.dat");
		// fo.write(pubkey_N) ;
		// fo.close() ;
		// }catch(Exception e){}
		//

		return new byte[][] { pubkey_N, pubkey_E };
	}

	public static void debug(byte[] bs) {
		if (!debug || (bs == null))
			return;
		for (int i = 0, length = bs.length; i < length; i++) {
			int x = ((int) bs[i]) & 0xff;
			if (x > 15)
				System.out.print(Integer.toString(x, 16) + " ");
			else
				System.out.print("0" + Integer.toString(x, 16) + " ");
			if ((i + 1) % 16 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
	}

	public static void debug(String title, byte[] bs) {
		debug(title + ":");
		debug(bs);
	}

	public static void debug(byte[] bs, String file) {
		if (!debug)
			return;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bs);
			out.flush();
		} catch (Exception e) {
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void debug(String msg) {
		if (!debug)
			return;
		System.out.println(msg);
	}

	public static int bytes2Int(byte[] bytes, boolean desc) {
		if (bytes == null)
			return 0;
		if (bytes.length < 4) {
			byte[] tmp = new byte[] { 0, 0, 0, 0 };
			if (!desc)
				System.arraycopy(bytes, 0, tmp, 4 - bytes.length, bytes.length);
			else
				System.arraycopy(bytes, 0, tmp, 0, bytes.length);
			bytes = tmp;
		}
		if (!desc)
			return ((bytes[0] << 24) | ((bytes[1] << 16) & 0xff0000) | ((bytes[2] << 8) & 0xff00) | (bytes[3] & 0xff));
		else
			return ((bytes[3] << 24) | ((bytes[2] << 16) & 0xff0000) | ((bytes[1] << 8) & 0xff00) | (bytes[0] & 0xff));
	}

	public static byte[] getDERInnerData(byte[] der) {
		int ab = der[1] & 0xff;
		int length = 0;
		int dataIndex = 0;
		if (ab >= 0x80) {
			int lengthOfLength = ab - 0x80;
			byte[] lengthBytes = new byte[lengthOfLength];
			System.arraycopy(der, 2, lengthBytes, 0, lengthOfLength);
			length = generateInt(lengthBytes);
			dataIndex = 2 + lengthOfLength;
		} else {
			length = (int) ab;
			dataIndex = 2;
		}
		byte[] data = new byte[length];
		System.arraycopy(der, dataIndex, data, 0, length);
		return data;
	}

	public static byte[] getDERInnerData(byte[] der, int index) {
		int derStart = 0;
		int length = 0;
		int headLength = 2;
		for (int i = 0; i < index; i++) {
			int ab = der[derStart + 1] & 0xff;
			if (ab > 0x79) {
				int lengthOfLength = ab - 0x80;
				headLength = 2 + lengthOfLength;
				byte[] lengthBytes = new byte[lengthOfLength];
				System.arraycopy(der, derStart + 2, lengthBytes, 0, lengthOfLength);
				length = generateInt(lengthBytes);
			} else
				length = (int) ab;
			if (i != (index - 1))
				derStart = derStart + length + headLength;
		}
		byte[] bs = new byte[length];
		System.arraycopy(der, derStart + headLength, bs, 0, length);
		return bs;
	}

	public static int generateInt(byte[] bytes) {
		int tr = 0;
		for (int i = bytes.length - 1; i > -1; i--) {
			int x = ((int) bytes[bytes.length - 1 - i] & 0xff) << (i * 8);
			tr += x;
		}
		return tr;
	}

	public static byte[] generateDERCode(int type, byte[] content) {
		int length = content.length;
		byte[] lengthBs = null;
		if (length >= 0x80) {
			byte[] intBs = int2Bytes(length);
			lengthBs = new byte[1 + intBs.length];
			lengthBs[0] = (byte) (0x80 + intBs.length);
			System.arraycopy(intBs, 0, lengthBs, 1, intBs.length);
		} else {
			lengthBs = new byte[1];
			lengthBs[0] = (byte) length;
		}
		byte[] all = new byte[1 + lengthBs.length + content.length];
		all[0] = (byte) type;
		System.arraycopy(lengthBs, 0, all, 1, lengthBs.length);
		System.arraycopy(content, 0, all, 1 + lengthBs.length, content.length);
		return all;
	}

	public static byte[] int2Bytes(int i) {
		int l = 0;
		if (i <= 0xff)
			l = 1;
		else if (i <= 0xffff)
			l = 2;
		else if (i <= 0xffffff)
			l = 3;
		else
			l = 4;
		byte[] bs = new byte[l];
		for (int x = 0; x < l; x++) {
			bs[x] = (byte) (i >> ((l - 1 - x) * 8));
		}
		return bs;
	}

	public static byte[] connect(byte[] a, byte[] b) {
		byte[] tmp = new byte[a.length + b.length];
		System.arraycopy(a, 0, tmp, 0, a.length);
		System.arraycopy(b, 0, tmp, a.length, b.length);
		return tmp;
	}

	public static byte[] date2ASN1(Date date) {
		if (date.getTime() < DATE_2050) {
			SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
			String time = format.format(date) + "Z";
			return generateDERCode(0x17, time.getBytes());
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String time = format.format(date) + "Z";
			return generateDERCode(0x18, time.getBytes());
		}
	}

	public static byte[] oid2ASN1(String oid) {
		String[] pieces = oid.split("\\.");
		byte[] oidbs = null;
		byte b = (byte) (Integer.parseInt(pieces[0]) * 40 + Integer.parseInt(pieces[1]));
		oidbs = new byte[1];
		oidbs[0] = b;
		for (int i = 2; i < pieces.length; i++) {
			int num = Integer.parseInt(pieces[i]);
			int pow = maxPow128(num, 0);
			byte[] id = new byte[pow + 1];
			genid(pow, num, id);
			byte[] tmp = new byte[oidbs.length + id.length];
			System.arraycopy(oidbs, 0, tmp, 0, oidbs.length);
			System.arraycopy(id, 0, tmp, oidbs.length, id.length);
			oidbs = tmp;
		}
		return oidbs;
	}

	private static int maxPow128(int num, int pow) {
		if (num % (Math.pow(128, pow)) > Math.pow(128, ((pow - 1) > 0) ? (pow - 1) : 1))
			return maxPow128(num, pow + 1);
		else {
			if ((pow == 0) && (num / 128 > 0))
				return 1;
			return pow;
		}
	}

	private static void genid(int pow, int num, byte[] id) {
		if (pow == 0)
			id[id.length - 1] = (byte) num;
		else
			id[id.length - 1 - pow] = (byte) (0x80 + num / Math.pow(128, pow));
		if ((pow - 1) >= 0)
			genid(pow - 1, (int) (num % Math.pow(128, pow)), id);
	}

	public static X509Certificate generateCertificate(byte[] cert) throws Exception {
		ByteArrayInputStream in = null;
		// 判断是否为der编码证书
		if (cert[0] == 0x30) {
			int tl = ((int) (cert[1] & 0xff)) - 128;
			if (tl > 0) {
				byte[] ltmp = new byte[tl];
				System.arraycopy(cert, 2, ltmp, 0, tl);
				int length = new BigInteger(ltmp).intValue();
				if ((length > 0) && (length == (cert.length - 2 - tl))) {
					in = new ByteArrayInputStream(cert);
				} else
					throw new CertificateException("Illegal length: " + length);
			} else
				throw new CertificateException("Illegal code: 30 " + ((cert[1] & 0xff)));
		} else {
			String head = "-----BEGIN CERTIFICATE-----";
			String tail = "-----END CERTIFICATE-----";
			String b64Cert = new String(cert);
			if (b64Cert.indexOf(head) > -1) {
				b64Cert = b64Cert.replaceFirst(head, "").replaceFirst(tail, "");
			}
			byte[] certTmp = Base64.decode(b64Cert.trim());
			in = new ByteArrayInputStream(certTmp);
		}
		CertificateFactory cf = CertificateFactory.getInstance("X.509FX", "INFOSEC");
		return (X509Certificate) cf.generateCertificate(in);
	}

	public static void main(String[] args) throws Exception {
		String DN1 = "c=cn,ou=infosec,ou=randd,cn=hy";
		String DN2 = "cn=hy,ou=randd,ou=infosec,c=cn";
		System.out.println(turnDN(DN1));
		compereDN(DN1, DN2);
	}
}
