package cn.com.infosec.netsigninterface.demo;

import cn.com.infosec.asn1.x509.X509Name;
import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.netsign.crypto.util.CryptoUtil;
import cn.com.infosec.netsign.crypto.util.PKCS10CertificationRequest;
import cn.com.infosec.netsign.frame.config.ExtendedConfig;
import cn.com.infosec.oscca.SDFJNI;
import cn.com.infosec.oscca.sm2.SM2;
import cn.com.infosec.oscca.sm2.SM2PrivateKey;
import cn.com.infosec.oscca.sm2.SM2PublicKey;

public class TestGenSM2Keypair {

	public static void main(String[] args) throws Exception {
		SDFJNI.init();

		// 生成SM2的密钥对
		String subject = "CN=NetSeal";
		cn.com.infosec.oscca.sm2.SM2PublicKey pubk = new cn.com.infosec.oscca.sm2.SM2PublicKey();
		cn.com.infosec.oscca.sm2.SM2PrivateKey prik = new cn.com.infosec.oscca.sm2.SM2PrivateKey();
		SDFJNI.generateSM2SignKeyPair(pubk, prik);

		// String p10 = PKCS10Utils.genP10(subject, pubk, prik);

		PKCS10CertificationRequest p10request = new PKCS10CertificationRequest(ExtendedConfig.getDefaultSM2P10Alg(),
				new X509Name(subject), (SM2PublicKey) pubk, (SM2PrivateKey) prik, "1234567812345678".getBytes());

		System.out.println("===123");
		System.out.println(CryptoUtil.createbase64csr(p10request));

		System.out.println(HexUtil.byte2Hex(pubk.getX()));
		System.out.println(HexUtil.byte2Hex(pubk.getY()));
		System.out.println(HexUtil.byte2Hex(prik.getD()));
		
		System.out.println(SM2.checkKeyPair(prik.getEncoded(), pubk.getX(), pubk.getY()));
	}

}
