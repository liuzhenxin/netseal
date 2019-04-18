package cn.com.infosec.netseal.common.util.p7;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERConstructedSet;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERObject;
import cn.com.infosec.asn1.DERSet;
import cn.com.infosec.asn1.DERTaggedObject;
import cn.com.infosec.asn1.pkcs.ContentInfo;
import cn.com.infosec.asn1.pkcs.IssuerAndSerialNumber;
import cn.com.infosec.asn1.pkcs.PKCSObjectIdentifiers;
import cn.com.infosec.netseal.common.util.CertUtil;

public class PKCS7EnvelopedData implements PKCSObjectIdentifiers {

	public static String getCertSn(byte[] envelopeData) throws Exception {

		ByteArrayInputStream bais = new ByteArrayInputStream(envelopeData);
		DERInputStream din = new DERInputStream(bais);
		DERObject pkcs;
		DERConstructedSequence seq = null;

		try {
			pkcs = din.readObject();
		} catch (IOException ex) {
			throw new SecurityException("can't decode PKCS7EnvlopedData object");
		}

		if (!(pkcs instanceof DERConstructedSequence)) {
			throw new SecurityException("Not a valid PKCS#7 object - not a sequence");
		}

		// envelopedData
		ContentInfo content = ContentInfo.getInstance(pkcs);

		// if (!content.getContentType().equals(envelopedData)) {
		// throw new SecurityException("Not a valid PKCS#7 envloped-data object - wrong header " + content.getContentType().getId());
		// }

		seq = (DERConstructedSequence) pkcs;

		DERConstructedSequence env = (DERConstructedSequence) DERConstructedSequence.getInstance((DERTaggedObject) seq.getObjectAt(1), true);

		DERConstructedSet ds = (DERConstructedSet) DERSet.getInstance(env.getObjectAt(1));
		DERConstructedSequence recpientInfos = (DERConstructedSequence) DERConstructedSequence.getInstance(ds.getObjectAt(0));

		IssuerAndSerialNumber isAndSN = IssuerAndSerialNumber.getInstance(recpientInfos.getObjectAt(1));
		String issuer = isAndSN.getName().toString();

		// String sn = isAndSN.getCertificateSerialNumber().getValue().toString(16).toUpperCase();
		String sn = CertUtil.transCertSn(isAndSN.getCertificateSerialNumber().getValue().toString(16)).toUpperCase();
		return sn;
	}

	// public static void main(String[] args) throws Exception {
	// String sn = PKCS7EnvelopedData.getCertSn(FileUtil.getFile("f:/temp/envelope.asn1"));
	// System.out.println(sn);
	// X509Certificate cert = CertUtil.parseCert(FileUtil.getFile("f:/temp/pdf/sm2/netseal.cer")).getX509Cert();
	// System.out.println(cert.getSerialNumber().toString(16));
	// }

}