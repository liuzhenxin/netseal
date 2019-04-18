package cn.com.infosec.netseal.common.util.baes64;

/**
 * Title:       字符解码抽象类
 * Description: sun.misc.Base64Decoder中的 BASE64 解码实现代码
 * Copyright:   Infosec Copyright (c) 2003
 * Company:     Infosec
 * @version 2.0
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class MyCharacterDecoder
{

    public MyCharacterDecoder()
    {
    }

	/**
	 * 每一个解码元素(块)的字节数,在编码时，并非一个字符转化为一个字符，所以要定义解码元素
	 * @return              每一个解码元素的字节数
	 * 虚函数，具体的实现在子类中，如 MyBASE64Decoder
	 */
    protected abstract int bytesPerAtom();

	/**
	 * 每一行的字节数
	 * @return              每一行的字节数
	 * 虚函数，具体的实现在子类中，如 MyBASE64Decoder
	 * 编码时和解码时的每行字节数不同
	 */
    protected abstract int bytesPerLine();

	/**
	 * 对数据的前缀进行解码，并送到输出流
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    protected void decodeBufferPrefix(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
    }

	/**
	 * 对数据的后缀进行解码，并送到输出流
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    protected void decodeBufferSuffix(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
    }

	/**
	 * 对数据行的前缀进行解码，并送到输出流
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @return              每一行的长度
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    protected int decodeLinePrefix(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        return bytesPerLine();
    }

	/**
	 * 对数据行的后缀进行解码，并送到输出流
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    protected void decodeLineSuffix(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
    }

	/**
	 * 对数据流中的元素进行解码，并送到输出流，解码的方式在子类中实现
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @param i             元素的位置
	 * @throws IOException  在解码过程中发生 I/O 错误
	 * 调用了在子类中实现的方法，以实现不同的解码方式
	 */
    protected void decodeAtom(InputStream inputstream, OutputStream outputstream, int i)
        throws IOException
    {
        throw new StreamExhausted();
    }

	/**
	 * 将数据流中的数据读到字节数组中
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param abyte0        用来返回数据的字节数组
	 * @param i             数据在数组中的起始位置
	 * @param j             读取数据的字节数
	 * @return              实际读取的长度
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    protected int readFully(InputStream inputstream, byte abyte0[], int i, int j)
        throws IOException
    {
        for(int k = 0; k < j; k++)
        {
            int l = inputstream.read();
            if(l == -1)
                return k != 0 ? k : -1;
            abyte0[k + i] = (byte)l;
        }

        return j;
    }

	/**
	 * 对输入流进行解码，并送到输出流，解码的方式在子类中实现
	 * @param inputstream   包含了要进行解码的数据输入流
	 * @param outputstream  输出解码结果的输出流
	 * @throws IOException  在解码过程中发生 I/O 错误
	 * 调用了在子类中实现的方法，以实现不同的解码方式
	 */
    public void decodeBuffer(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        int j = 0;
        decodeBufferPrefix(inputstream, outputstream);
        try
        {
            do
            {
                int k = decodeLinePrefix(inputstream, outputstream);
                int i;
                for(i = 0; i + bytesPerAtom() < k; i += bytesPerAtom())
                {
                    decodeAtom(inputstream, outputstream, bytesPerAtom());
                    j += bytesPerAtom();
                }

                if(i + bytesPerAtom() == k)
                {
                    decodeAtom(inputstream, outputstream, bytesPerAtom());
                    j += bytesPerAtom();
                } else
                {
                    decodeAtom(inputstream, outputstream, k - i);
                    j += k - i;
                }
                decodeLineSuffix(inputstream, outputstream);
            } while(true);
        }
        catch( StreamExhausted ex  )
        {
            decodeBufferSuffix(inputstream, outputstream);
        }
    }

	/**
	 * 对输入的字符串进行解码，解码的方式在子类中实现
	 * @param s             要进行解码的字符串
	 * @return              解码结果
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    public byte[] decodeBuffer(String s)
        throws IOException
    {
        byte[] abyte0 = s.getBytes();

        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        decodeBuffer(((InputStream) (bytearrayinputstream)), ((OutputStream) (bytearrayoutputstream)));
        return bytearrayoutputstream.toByteArray();
    }

	/**
	 * 对输入流中的数据进行解码，解码的方式在子类中实现
	 * @param inputstream   包含了要进行解码数据的输入流
	 * @return              解码结果
	 * @throws IOException  在解码过程中发生 I/O 错误
	 */
    public byte[] decodeBuffer(InputStream inputstream)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        decodeBuffer(inputstream, ((OutputStream) (bytearrayoutputstream)));
        return bytearrayoutputstream.toByteArray();
    }
}