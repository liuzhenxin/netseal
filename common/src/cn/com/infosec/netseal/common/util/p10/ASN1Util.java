package cn.com.infosec.netseal.common.util.p10;

public class ASN1Util {

	public static int toInt( byte[] bs ){
		int ai = 0 ;
		for( int i = 0 , length = bs.length ; i < length ; i ++ ){
			int tmp = 0xff & bs[i] ;
			ai += tmp<<(bs.length-i-1)*8 ;
		}
		return ai ;
	}
	
	/**
	 * 获取ASN1串中的值
	 * @param bs
	 * @return
	 */
	public static byte[] getValue( byte[] bs ){
		//System.out.println( bs.length ) ;
		int length = 0 ;
		int off = 0 ;
		byte b =  bs[1] ;
		if( (0xff&b) >= 0x80 ){
			int l = (0xff&b)-(0xff&0x80) ;
			byte[] tmp = new byte[l] ;
			System.arraycopy( bs , 2 , tmp , 0 , l ) ;
			length = toInt(tmp) ;
			off = 2 + l ;
		}else{
			off = 2 ;
			length = b&0xff ;
		}
		if( bs[0] == 3 ){
			off += 1 ;
			length -= 1 ;
		}
		byte[] tmp = new byte[length] ;
		System.arraycopy( bs , off , tmp , 0 , length );
		return tmp ;
	}
	
	/**
	 * 获取ASN1串中的第index个对象
	 * @param bs
	 * @param index
	 * @return
	 */
	public static byte[] getObject( byte[] bs , int index ){
		int length = 0 ;
		int off = 0 ;
		int l = 0 ;
		for( int i = 0 ; i <= index ; i ++ ){
			off += l + length ;
			byte b =  bs[off+1] ;
			if( (0xff&b) >= 0x80 ){
				l = (0xff&b)-128 ;
				byte[] tmp = new byte[l] ;
				System.arraycopy( bs , off+2 , tmp , 0 , l ) ;
				length = toInt(tmp) ;
				l += 2 ;
			}else{
				length = b&0xff ;
				l = 2;
			}
		}
		byte[] tmp = new byte[length + l ] ;
		System.arraycopy( bs , off , tmp , 0 , length +l ) ;
		return tmp ;
	}
	
}
