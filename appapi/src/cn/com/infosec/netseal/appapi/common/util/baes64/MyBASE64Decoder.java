package cn.com.infosec.netseal.appapi.common.util.baes64;

/**
 * Title:       BASE64 解码
 * Description: sun.misc.Base64Decoder中的 BASE64 解码实现，在cn.com.infosec.rads.util.Base64中使用
 * Copyright:   Infosec Copyright (c) 2003
 * Company:     Infosec
 * @version 1.0
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyBASE64Decoder extends MyCharacterDecoder
{

    public MyBASE64Decoder()
    {
        decode_buffer = new byte[4];
    }

	/**
	 * BASE64算法中每一个解码元素的字节数
	 * @return              每一个解码元素的字节数
	 * BASE64算法是将三个字节的数据转化为四个字节，所以编码块长度为3，解码块长度为4
	 */
    protected int bytesPerAtom()
    {
        return 4;
    }

	/**
	 * 每一行的字节数
	 * @return              每一行的字节数
	 */
    protected int bytesPerLine()
    {
        return 72;
    }

	/**
	 * 对数据的前缀进行解码，并送到输出流
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @param i             解码位
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    protected void decodeAtom(InputStream inputstream, OutputStream outputstream, int i)
        throws IOException
    {
        byte byte0 = -1;
        byte byte1 = -1;
        byte byte2 = -1;
        byte byte3 = -1;
        int j;
        if(i < 2)
            throw new FormatException("BASE64Decoder: Not enough bytes for an atom.");
        do
        {
            j = inputstream.read();
            if(j == -1)
                throw new StreamExhausted();

        } while(j == 10 || j == 13);
        decode_buffer[0] = (byte)j;
        j = readFully(inputstream, decode_buffer, 1, i - 1);
        if(j == -1)
            throw new StreamExhausted();

        if(i > 3 && decode_buffer[3] == 61)
            i = 3;
        if(i > 2 && decode_buffer[2] == 61)
            i = 2;
        switch(i)
        {
        case 4: // '\004'
            byte3 = pem_convert_array[decode_buffer[3] & 0xff];
            // fall through

        case 3: // '\003'
            byte2 = pem_convert_array[decode_buffer[2] & 0xff];
            // fall through

        case 2: // '\002'
            byte1 = pem_convert_array[decode_buffer[1] & 0xff];
            byte0 = pem_convert_array[decode_buffer[0] & 0xff];
            // fall through

        default:
            switch(i)
            {
            case 2: // '\002'
                outputstream.write((byte)(byte0 << 2 & 0xfc | byte1 >>> 4 & 3));
                break;

            case 3: // '\003'
                outputstream.write((byte)(byte0 << 2 & 0xfc | byte1 >>> 4 & 3));
                outputstream.write((byte)(byte1 << 4 & 0xf0 | byte2 >>> 2 & 0xf));
                break;

            case 4: // '\004'
                outputstream.write((byte)(byte0 << 2 & 0xfc | byte1 >>> 4 & 3));
                outputstream.write((byte)(byte1 << 4 & 0xf0 | byte2 >>> 2 & 0xf));
                outputstream.write((byte)(byte2 << 6 & 0xc0 | byte3 & 0x3f));
                break;
            }
            break;
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
    private static final byte pem_convert_array[];
    byte decode_buffer[];

	//初史化在解码中使用的数组
    static
    {
        pem_convert_array = new byte[256];
        for(int i = 0; i < 255; i++)
            pem_convert_array[i] = -1;

        for(int j = 0; j < pem_array.length; j++)
            pem_convert_array[pem_array[j]] = (byte)j;

    }
}