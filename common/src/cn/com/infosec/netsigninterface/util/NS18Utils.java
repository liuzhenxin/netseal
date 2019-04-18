package cn.com.infosec.netsigninterface.util;

public class NS18Utils {
	
	public static String turnDN( String DN ) {
		String[] temp = DN.split( "," );
		String turnDN = temp[ temp.length - 1 ].trim();

		for( int i = ( temp.length - 2 ) ; i >= 0 ; i-- ) {
			turnDN = turnDN + "," + temp[ i ].trim();
		}
		return turnDN;
	}
	
}
