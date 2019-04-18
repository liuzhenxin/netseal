package cn.com.infosec.netsigninterface;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import cn.com.infosec.asn1.DERConstructedSequence;
import cn.com.infosec.asn1.DERConstructedSet;
import cn.com.infosec.asn1.DERInputStream;
import cn.com.infosec.asn1.DERObject;
import cn.com.infosec.asn1.DERObjectIdentifier;
import cn.com.infosec.asn1.DEROctetString;
import cn.com.infosec.asn1.DERPrintableString;
import cn.com.infosec.asn1.DERTaggedObject;
import cn.com.infosec.netsigninterface.util.ConsoleLogger;

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
 * @author unascribed
 * @version 1.0
 */

public class CRLDPDEC {
	private final static String cn = "2.5.4.3";

	// CRLDistPoint cr=new CRLDistPoint();
	public CRLDPDEC() {
	}

	public static String getExtern( byte[] a ) {

		String externvalue = null;
		try {
			ByteArrayInputStream bint = new ByteArrayInputStream( a );
			DERInputStream dint = new DERInputStream( bint );
			DEROctetString doct = ( DEROctetString ) dint.readObject();
			byte[] tmp = doct.getOctets();
			bint = new ByteArrayInputStream( tmp );
			dint = new DERInputStream( bint );
			DERObject dobj = dint.readObject();
			Class dc = dobj.getClass();
			Class c1 = dc.forName( dc.getName() );
			Method m = c1.getMethod( "getString" , null );

			String tmp1 = ( String ) m.invoke( dobj , null );
			externvalue = tmp1;

		} catch ( Exception ex ) {
			ConsoleLogger.logException( ex );
		}
		return externvalue;
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
						// System.out.print("get crl dp="+crldp);
					}

				}
			}
			// else
			// {
			// System.out.println("crl dp type is url");
			// // System.out.println(new String(
			// ((DEROctetString)dert2.getObject()).getOctets()));
			// // System.out.println( dert2.getObject());
			// }
		}
		return crldp;

	}
	// public static void main(String[] args){
	// try{
	// java.security.cert.CertificateFactory cf=
	// java.security.cert.CertificateFactory.getInstance("X.509");
	// java.security.cert.X509Certificate
	// xc509=(java.security.cert.X509Certificate)cf.generateCertificate(new
	// FileInputStream("c://rca.cer"));
	// byte[] a =
	// xc509.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
	// FileOutputStream fout=new FileOutputStream("c://hycert.txt");
	// fout.write(a);
	// fout.close();
	// System.out.println("getdp="+CRLDPNEWDEC.getcrldp(a));
	// getcrldp()
	// }catch(Exception ex){
	// ConsoleLogger.logException( ex ) ;
	// }
	// }
}
