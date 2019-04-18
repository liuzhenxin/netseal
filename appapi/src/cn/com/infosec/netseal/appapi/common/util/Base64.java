
package cn.com.infosec.netseal.appapi.common.util;

/**
 * Title:       BASE64 算法
 * Description: BASE64 编码/解码
 * Copyright:   Infosec Copyright (c) 2003
 * Company:     Infosec
 * @version 2.0
 */

//import sun.misc.*;
import java.io.IOException;
import java.util.Arrays;

import cn.com.infosec.netseal.appapi.common.util.baes64.MyBASE64Decoder;
import cn.com.infosec.netseal.appapi.common.util.baes64.MyBASE64Encoder;


public class Base64 {

	/**
	 * 对字符串进行BASE64编码
	 * @param s             要进行编码的字符串
	 * @return              进行BASE64编码的字节数组
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
	public static final String encode(String s) throws IOException {
		MyBASE64Encoder mbe = new MyBASE64Encoder();
		return mbe.encode(s.getBytes());
	}

	/**
	 * 对字节数组进行BASE64编码
	 * @param bs            要进行编码的字节数组
	 * @return              进行BASE64编码的字节数组
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
	public static final String encode(byte[] bs) throws IOException {
		MyBASE64Encoder mbe = new MyBASE64Encoder();
		return mbe.encode(bs);
	}

	/**
	 * 对字符串进行BASE64解码
	 * @param s             要进行解码的BASE64字符串
	 * @return              进行BASE64解码的结果字节数组
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
	public static final byte[] decode(String s) throws IOException {
		MyBASE64Decoder mbd = new MyBASE64Decoder();
		
//		StringBuffer sb = new StringBuffer(s);
//		StringBuffer snew = new StringBuffer();
//		int index = sb.length();
//		for (int i=0;i<index;i++){
//			char c = sb.charAt(i);
//			if (isBase64Char(c)){
//				snew.append(c);
//			}
//		}
//		
//		s = snew.toString();
		
		return mbd.decodeBuffer(s);
	}

	/**
	 * 对字节数组进行BASE64解码
	 * @param bs            要进行解码的字节数组
	 * @return              进行BASE64解码的结果字节数组
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
	public static final byte[] decode(byte[] bs) throws IOException {
		MyBASE64Decoder mbd = new MyBASE64Decoder();
		String s = new String(bs);
		return mbd.decodeBuffer(s);
	}
	
	
	private static boolean isBase64Char(char c){
		return (Arrays.binarySearch(pem_array, c) >=0) ? true:false;
	}

	//	使用的字符，包括字母、数字，以及加号、减号
	private static final char pem_array[] =
		{
			'A',
			'B',
			'C',
			'D',
			'E',
			'F',
			'G',
			'H',
			'I',
			'J',
			'K',
			'L',
			'M',
			'N',
			'O',
			'P',
			'Q',
			'R',
			'S',
			'T',
			'U',
			'V',
			'W',
			'X',
			'Y',
			'Z',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'o',
			'p',
			'q',
			'r',
			's',
			't',
			'u',
			'v',
			'w',
			'x',
			'y',
			'z',
			'0',
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9',
			'+',
			'/' };
	public static final void main(String args[]){
		String plainText = "12345678901234567890";
		try {
			String base64Text = encode(plainText);
			System.out.println(base64Text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	public static final void main( String args[] )
	{
		try
		{
			FileInputStream fileIn = new FileInputStream( "Base64Test.dat" );
			byte[] bs = new byte[ 2000 ];
			fileIn.read( bs, 0, 1999 );
			String ss = new String( bs );
			ss = "测试 test 字符串" + ss;
	
			String e = new String( Base64.encode( ss ) );
			String d = new String( Base64.decode( e )  );
	
			String sunSEncode = "";
			String sunSDecode = "";
	
		    BASE64Encoder b64Enc = new BASE64Encoder();
		    sunSEncode = b64Enc.encode( ss.getBytes() );
	
			sun.misc.BASE64Decoder bd = new BASE64Decoder();
			sunSDecode = new String( bd.decodeBuffer( sunSEncode ) );
	
			System.out.println( "Input   = [" + ss + "]" );
			System.out.println( "My Encoded = [" + e + "]" );
			System.out.println( "My Decoded = [" + d + "]" );
			System.out.println( "Sun Encoded = [" + sunSEncode + "]" );
			System.out.println( "Sun Encoded = [" + sunSDecode + "]" );
	
			String sMsg1 = "";
			String sMsg2 = "";
			if( e.equals( sunSEncode ) )
				sMsg1 = "BASE64 编码测试正确";
			else
				sMsg1 = "BASE64 编码测试错误";
			if( d.equals( sunSDecode ) )
				sMsg2 = "BASE64 解码测试正确";
			else
				sMsg2 = "BASE64 解码测试错误";
	
			System.out.println( sMsg1 );
			System.out.println( sMsg2 );
	
	
		}catch ( Exception x ){
		    x.printStackTrace();
	
		}
	}
	*/

}
