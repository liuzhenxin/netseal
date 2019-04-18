package cn.com.infosec.netsigninterface;

import java.io.ByteArrayInputStream;

import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERConstructedSet;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERPrintableString;
import cn.com.infosec.asn1.DERTaggedObject;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 黄勇 获得crl发布点。根据 DistributionPonits::= SEQUENCE SIZE (1..MAX) OF
 *         DistributionPoint DistributionPoint ::= SEQUENCE { distributionPoint
 *         [0] DistributionPointName OPTIONAL, reasons [1] ReasonFlags OPTIONAL,
 *         cRLIssuer [2] GeneralNames OPTIONAL }
 * 
 *         DistributionPointName ::= CHOICE { fullName [0] GeneralNames,
 *         nameRelativeToCRLIssuer [1] RelativeDistinguishedName }
 * 
 * 
 *         GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName
 * 
 *         GeneralName ::= CHOICE { otherName [0] AnotherName, rfc822Name [1]
 *         IA5String, dNSName [2] IA5String, x400Address [3] ORAddress,
 *         directoryName [4] Name, ediPartyName [5] EDIPartyName,
 *         uniformResourceIdentifier [6] IA5String, iPAddress [7] OCTET STRING,
 *         registeredID [8] OBJECT IDENTIFIER }
 * 
 * @version 1.0
 */

public class CRLDPNEWDEC {
	private final static String cn = "2.5.4.3";

	// CRLDistPoint cr=new CRLDistPoint();
	public CRLDPNEWDEC() {
	}

	public static String getcrldp( byte[] a ) throws Exception {
		String crldp = null;
		ByteArrayInputStream bint = new ByteArrayInputStream( a );
		DERInputStream dint = new DERInputStream( bint );
		DEROctetString doct = ( DEROctetString ) dint.readObject();
		byte[] tmp = doct.getOctets();
		bint = new ByteArrayInputStream( tmp );
		dint = new DERInputStream( bint );
		// CRLDistPoint crl=CRLDistPoint.getInstance(dint);
		DERConstructedSequence dconstr = ( DERConstructedSequence ) ( dint.readObject() );
		// System.out.print("de="+dconstr.size());
		for( int i = 0 ; i < dconstr.size() ; i++ ) {
			DERConstructedSequence dconstr0 = ( DERConstructedSequence ) ( dconstr.getObjectAt( 0 ) );

			DERTaggedObject dert = ( DERTaggedObject ) dconstr0.getObjectAt( 0 );
			DERTaggedObject dert1 = ( DERTaggedObject ) dert.getObject();

			DERTaggedObject dert2 = ( DERTaggedObject ) dert1.getObject();
			if( dert2.getTagNo() == 4 ) {
				DERConstructedSequence dconstr2 = ( DERConstructedSequence ) dert2.getObject();
				DERConstructedSet dconsettmp;
				DERConstructedSequence dconsequencetmp;
				for( int j = 0 ; j < dconstr2.size() ; j++ ) {
					dconsettmp = ( DERConstructedSet ) dconstr2.getObjectAt( j );
					dconsequencetmp = ( DERConstructedSequence ) dconsettmp.getObjectAt( 0 );
					if( cn.equals( ( ( DERObjectIdentifier ) dconsequencetmp.getObjectAt( 0 ) ).getId() ) ) {
						crldp = ( ( DERPrintableString ) dconsequencetmp.getObjectAt( 1 ) ).getString().toLowerCase();
					}

				}
			} else {
				// System.out.println("crl dp type is url");
				// System.out.println(new String(
				// ((DEROctetString)dert2.getObject()).getOctets()));

			}
		}
		return crldp;

	}
	// public static void main(String[] args){
	// try{
	// java.security.cert.CertificateFactory cf=
	// java.security.cert.CertificateFactory.getInstance("X.509");
	// java.security.cert.X509Certificate
	// xc509=(java.security.cert.X509Certificate)cf.generateCertificate(new
	// FileInputStream("c://shecatest.cer"));
	// byte[] a =
	// xc509.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
	// FileOutputStream fout=new FileOutputStream("c://hycert.txt");
	// fout.write(a);
	// fout.close();
	// System.out.println("getdp="+CRLDPNEWDEC.getcrldp(a));
	// getcrldp()
	// }catch(Exception ex){
	// }
	// }
}
