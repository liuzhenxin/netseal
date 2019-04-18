package cn.com.infosec.netsigninterface.util;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Debugger {

	private static int debug = 10;

	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyyMMddHHmmss" );

	public static void debug( byte[] bs , int level ) {
		if( ( level >= debug ) || ( bs != null ) ) {
			for( int i = 0 , length = bs.length ; i < length ; i++ ) {
				int x = ( ( int ) bs[ i ] ) & 0xff;
				System.out.print( Integer.toString( x , 16 ) + " " );
				if( ( i + 1 ) % 32 == 0 )
					System.out.print( "\n" );
			}
			System.out.print( "\n" );
		}
	}

	public static void debug( String title , byte[] bs , int level ) {
		debug( title + ":" , level );
		debug( bs , level );
	}
	
	public static void debug( String title , byte[] bs ) {
		debug( title + ":" , 11 );
		debug( bs , 11 );
	}

	public static void debug( byte[] bs , String file , int level ) {
		if( ( level >= debug ) && ( bs != null ) ) {
			try {
				FileOutputStream out = new FileOutputStream( file );
				out.write( bs );
				out.flush();
				out.close();
			} catch ( Exception e ) {
				//ConsoleLogger.logException( e ) ;
			}
		}
	}

	public static void debug( String msg , int level ) {
		if( level >= debug ) {
			System.out.println( msg );
		}
	}

	public static void error( String msg , byte[] info , Exception e , int level ) {
		StringBuffer buf = new StringBuffer();
		buf.append( "============Error log of NetSign============\n" );
		buf.append( "Time:" + format.format( new Date() ) + "\n" );
		if( msg != null )
			buf.append( msg + "\n" );
		if( info != null ){
			BigInteger bi = new BigInteger( info ) ;
			String hex = bi.toString( 16 ) ;
			hex = ( hex.length()%2 == 0 ) ? hex : ( "0" + hex ) ;
			char[] chars = hex.toCharArray() ;
			int line = 48 ;
			for( int i = 0 , length = chars.length ; i < length ; i ++ ){
				buf.append( chars[i] ) ;
				buf.append( chars[i + 1 ] ) ;
				buf.append( " " ) ;
				i += 1 ;
				if( ( i + 1 )%line == 0 )
					buf.append( "\n" ) ;
			}
			buf.append( "\n" ) ;
		}
		if( e != null ) {
			buf.append( e.toString() + "\n" );
			StackTraceElement[] elements = e.getStackTrace();
			for( int i = 0 , length = elements.length ; i < length ; i++ ) {
				buf.append( elements[ i ].toString() + "\n" );
			}
		}
		buf.append( "============================================\n" ) ;
		debug( buf.toString() , level ) ;
	}

}
