package cn.com.infosec.netsigninterface.util;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogger {

	private static Date now;

	private static SimpleDateFormat format;

	public static boolean isPrintStackTrace = true;

	public static boolean isDebug = false;

	public static boolean isSave = false;
	
	static {
		now = new Date();
		format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	}

	public static void logException( Throwable e , String info ) {
		now.setTime( System.currentTimeMillis() );
		String time = format.format( now );
		System.out.println( "---------------NetSign(" + time + ")----------------" );
		System.out.println( info );
		System.out.println( "An exception catched: " + e.toString() );
		if( isPrintStackTrace ) {
			System.out.println( "Full stacktrace as below:" );
			e.printStackTrace();
		}
		System.out.println( "-----------------------------------------------------------" );
	}

	public static void logException( Throwable e ) {
		now.setTime( System.currentTimeMillis() );
		String time = format.format( now );
		System.out.println( "---------------NetSign(" + time + ")----------------" );
		System.out.println( "An exception catched: " + e.toString() );
		if( isPrintStackTrace ) {
			System.out.println( "Full stacktrace as below:" );
			e.printStackTrace();
		}
		System.out.println( "-----------------------------------------------------------" );
	}

	public static void logBinary( String title , byte[] bs ) {
		if( !isDebug )
			return;
		now.setTime( System.currentTimeMillis() );
		String time = format.format( now );
		System.out.println( "---------------NetSign(" + time + ")----------------" );
		int length = ( bs == null ) ? 0 : bs.length ; 
		System.out.println( title + "("+ length + "):" );
		for( int i = 0 ; i < length ; i++ ) {
			int x = ( ( int ) bs[ i ] ) & 0xff;
			if( x >= 16 )
				System.out.print( Integer.toString( x , 16 ) + " " );
			else
				System.out.print( "0" + Integer.toString( x , 16 ) + " " );
			if( ( i + 1 ) % 32 == 0 )
				System.out.print( "\n" );
		}
		System.out.print( "\n" );
		System.out.println( "-----------------------------------------------------------" );
	}
	
	public static void saveBinaryasHEX( String file , byte[] bs ) {
		if( !isDebug )
			return;
		now.setTime( System.currentTimeMillis() );
		String time = format.format( now );
		System.out.println( "---------------NetSign(" + time + ")----------------" );
		System.out.println( "Binary stream saved to file:" + file );
		FileOutputStream out = null;
		try {
			out = new FileOutputStream( file );
			out.write( "        0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31\n".getBytes() ) ;
			out.write( "      0|".getBytes() ) ;
			for( int i = 0 ; i < bs.length ; i++ ) {
				int x = ( ( int ) bs[ i ] ) & 0xff;
				if( x >= 16 )
					out.write( ( Integer.toString( x , 16 ) + " " ).getBytes() );
				else
					out.write( ( "0" + Integer.toString( x , 16 ) + " " ).getBytes() );
				if( ( i + 1 ) % 32 == 0 ){
					out.write( "\n".getBytes() );
					int s = (i/32+1)*32 ;
					int z = 7 - (s + "").length() ;
					for( int j = 0 ; j < z ; j ++ )
						out.write( (byte)0x20 ) ;
					out.write( (s+"|" ).getBytes() ) ;
				}
			}
			out.flush();
		} catch ( Exception e ) {
			logException( e );
		} finally {
			try {
				if( out != null )
					out.close();
			} catch ( Exception e ) {
			}
		}
		System.out.println( "-----------------------------------------------------------" );
	}

	public static void saveBinary( String file , byte[] bs ) {
		if( !isDebug )
			return;
		now.setTime( System.currentTimeMillis() );
		String time = format.format( now );
		System.out.println( "---------------NetSign(" + time + ")----------------" );
		System.out.println( "Binary stream saved to file:" + file );
		FileOutputStream out = null;
		try {
			out = new FileOutputStream( file );
			out.write( bs );
			out.flush();
		} catch ( Exception e ) {
			logException( e );
		} finally {
			try {
				if( out != null )
					out.close();
			} catch ( Exception e ) {
			}
		}
		System.out.println( "-----------------------------------------------------------" );
	}

	public static void logString( Object msg ) {
		if( !isDebug )
			return;
		now.setTime( System.currentTimeMillis() );
		String time = format.format( now );
		System.out.println( "---------------NetSign(" + time + ")----------------" );
		System.out.println( msg );
		System.out.println( "-----------------------------------------------------------" );
	}

}
