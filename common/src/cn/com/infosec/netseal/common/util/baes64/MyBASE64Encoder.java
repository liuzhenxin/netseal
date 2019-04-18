package cn.com.infosec.netseal.common.util.baes64;


/**
 * Title:       BASE64 编码
 * Description: sun.misc.Base64Decoder中的 BASE64 编码实现，在cn.com.infosec.rads.util.Base64中使用
 * Copyright:   Infosec Copyright (c) 2003
 * Company:     Infosec
 * @version 2.0
 */

import java.io.IOException;
import java.io.OutputStream;

public class MyBASE64Encoder extends MyCharacterEncoder
{

    public MyBASE64Encoder()
    {
    }

	/**
	 * BASE64编码算法中，每个编码元素(块)的长度
	 * @return                  每个编码元素(块)的长度
	 * BASE64算法是将三个字节的数据转化为四个字节，所以编码块长度为3，解码块长度为4
	 */
    protected int bytesPerAtom()
    {
        return 3;
    }

	/**
	 * BASE64编码算法中，每行的长度
	 * @return  每行的长度
	 * BASE64算法是将三个字节的数据转化为四个字节
	 */
    protected int bytesPerLine()
    {
        return 57;
    }

	/**
	 * 对元素进行BASE64编码
	 * @param outputstream 输出编码结果的输出流
	 * @param abyte0       要进行编码的数据
	 * @param i            编码位
	 * @param j            解码位
	 * @throws IOException
	 */

    protected void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j)
        throws IOException
    {
        if(j == 1)
        {
            byte byte0 = abyte0[i];
            int k = 0;
            boolean flag = false;
            outputstream.write(pem_array[byte0 >>> 2 & 0x3f]);
            outputstream.write(pem_array[(byte0 << 4 & 0x30) + (k >>> 4 & 0xf)]);
            outputstream.write(61);
            outputstream.write(61);
        } else
        if(j == 2)
        {
            byte byte1 = abyte0[i];
            byte byte3 = abyte0[i + 1];
            int l = 0;
            outputstream.write(pem_array[byte1 >>> 2 & 0x3f]);
            outputstream.write(pem_array[(byte1 << 4 & 0x30) + (byte3 >>> 4 & 0xf)]);
            outputstream.write(pem_array[(byte3 << 2 & 0x3c) + (l >>> 6 & 3)]);
            outputstream.write(61);
        } else
        {
            byte byte2 = abyte0[i];
            byte byte4 = abyte0[i + 1];
            byte byte5 = abyte0[i + 2];
            outputstream.write(pem_array[byte2 >>> 2 & 0x3f]);
            outputstream.write(pem_array[(byte2 << 4 & 0x30) + (byte4 >>> 4 & 0xf)]);
            outputstream.write(pem_array[(byte4 << 2 & 0x3c) + (byte5 >>> 6 & 3)]);
            outputstream.write(pem_array[byte5 & 0x3f]);
        }
    }

	//使用的字符，包括字母、数字，以及加号、减号
    private static final char pem_array[] = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', '+', '/'
    };

}