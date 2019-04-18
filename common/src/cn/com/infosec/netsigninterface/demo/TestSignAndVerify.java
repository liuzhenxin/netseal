package cn.com.infosec.netsigninterface.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

import cn.com.infosec.jce.oscca.OSCCAMessageDigest;
import cn.com.infosec.jce.oscca.SM2;
import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.netsign.der.util.DERSegment;
import cn.com.infosec.netsigninterface.SignatureUtil;

public class TestSignAndVerify {

	public static byte[] sign(byte[] plainText, byte[] id) throws Exception {
		String x = "14121a51c923efdbf46a8daefc5a06a11a05aaf62ba155e2218c8855772e1d86";
		String y = "f2931c42f744073465c8bb813daadb31d1dc7b5bcb5ab39b458012990f180bed";
		String d = "e7f5fb3f91b4fc592ab16003f306ddf19cda43289837e5b83c7c024d58955e5f";

		byte[] digest = OSCCAMessageDigest.SM3Digest(id, HexUtil.hex2Byte(x), HexUtil.hex2Byte(y), plainText);
		return SM2.signHash(digest, HexUtil.hex2Byte(d));
	}

	public static void verifySM2Cert(X509Certificate cert, PublicKey rootKey, byte[] sm2CertID) throws Exception {
		byte[] tbs = cert.getTBSCertificate();
		byte[] signed = cert.getSignature();
		if (!SignatureUtil.verify(tbs, signed, rootKey, "SM3", sm2CertID)) {
			throw new SignatureException("Certificate verify failed.");
		}
	}

	public static boolean verify(byte[] plainText, byte[] signedText, byte[] id)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		String x = "14121a51c923efdbf46a8daefc5a06a11a05aaf62ba155e2218c8855772e1d86";
		String y = "f2931c42f744073465c8bb813daadb31d1dc7b5bcb5ab39b458012990f180bed";
		String d = "e7f5fb3f91b4fc592ab16003f306ddf19cda43289837e5b83c7c024d58955e5f";

		byte[] digest = OSCCAMessageDigest.SM3Digest(id, HexUtil.hex2Byte(x), HexUtil.hex2Byte(y), plainText);
		signedText = formatSignedMsg(signedText);
		System.out.println(HexUtil.byte2Hex(signedText));
		byte[] kbs = new byte[64];
		System.arraycopy(HexUtil.hex2Byte(x), 0, kbs, 0, 32);
		System.arraycopy(HexUtil.hex2Byte(y), 0, kbs, 32, 32);
		return SM2.verifyHash(digest, signedText, kbs);
	}

	private static byte[] formatSignedMsg(byte[] signed) throws SignatureException {
		if (signed.length == 64)
			return signed;
		while (signed[0] == 0) {
			byte[] tmp = new byte[signed.length - 1];
			System.arraycopy(signed, 1, tmp, 0, tmp.length);
			signed = tmp;
		}
		if (signed[0] != 0x30)
			throw new SignatureException("Bad signature structon");
		byte[] signedf = new byte[64];
		try {
			DERSegment ds = new DERSegment(signed);
			ds = ds.getInnerDERSegment();
			byte[] tmp = ds.nextDERSegment().getInnerData();
			System.out.println(HexUtil.byte2Hex(tmp));
			System.arraycopy(tmp, tmp.length - 32, signedf, 0, 32);
			tmp = ds.nextDERSegment().getInnerData();
			System.arraycopy(tmp, tmp.length - 32, signedf, 32, 32);
			return signedf;
		} catch (Exception e) {
			throw new SignatureException(e.toString());
		}
	}

	public static void main(String[] args) throws Exception {
		byte[] signed = sign("123456".getBytes(), "1234567812345678".getBytes());
		FileOutputStream fous = new FileOutputStream(new File("f:/temp/signed"));
		fous.write(signed);
		fous.close();
		boolean result = verify("123456".getBytes(), signed, "1234567812345678".getBytes());
		System.out.println(result);
	}

}
