package cn.com.infosec.netseal.appapi.common.util.baes64;


/**
 * Title:       字符编码
 * Description: sun.misc.Base64Decoder中的 BASE64 编码实现代码
 * Copyright:   Infosec Copyright (c) 2003
 * Company:     Infosec
 * @version 2.0
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class MyCharacterEncoder
{

    public MyCharacterEncoder()
    {
    }

	/**
	 * 每一个编码元素的字节数
	 * @return              每一个编码元素的字节数
	 * 虚函数，具体的实现在子类中，如 MyBASE64Decoder
	 */
    protected abstract int bytesPerAtom();

	/**
	 * 每一行的字节数
	 * @return              每一行的字节数
	 * 虚函数，具体的实现在子类中，如 MyBASE64Decoder
	 */
    protected abstract int bytesPerLine();

	/**
	 * 对数据的前缀进行编码，并送到输出流
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    protected void encodeBufferPrefix(OutputStream outputstream)
        throws IOException
    {
        pStream = new PrintStream(outputstream);
    }

	/**
	 * 对数据的后缀进行编码，并送到输出流
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    protected void encodeBufferSuffix(OutputStream outputstream)
        throws IOException
    {
    }

	/**
	 * 对数据行的前缀进行编码，并送到输出流
	 * @param outputstream  输出编码结果的输出流
	 * @param i             行数
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    protected void encodeLinePrefix(OutputStream outputstream, int i)
        throws IOException
    {
    }

	/**
	 * 对数据行的后缀进行编码，并送到输出流
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    protected void encodeLineSuffix(OutputStream outputstream)
        throws IOException
    {
//        pStream.println();
    }

	/**
	 * 对数据流中的元素进行编码，并送到输出流，编码的方式在子类中实现
	 * @param outputstream  输出编码结果的输出流
	 * @param abyte0        保存编码数据的字节数组
	 * @param i             元素的位置
	 * @param j             元素的长度
	 * @throws IOException  在编码过程中发生 I/O 错误
	 * 调用了在子类中实现的方法，以实现不同的编码方式
	 */
    protected abstract void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j)
        throws IOException;

	/**
	 * 将数据流中的数据读到字节数组中
	 * @param inputstream   包含了要进行编码的数据输入流
	 * @param abyte0        用来返回数据的字节数组
	 * @return              实际读取的长度
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    protected int readFully(InputStream inputstream, byte abyte0[])
        throws IOException
    {
        for(int i = 0; i < abyte0.length; i++)
        {
            int j = inputstream.read();
            if(j == -1)
                return i;
            abyte0[i] = (byte)j;
        }

        return abyte0.length;
    }

	/**
	 * 对输入流进行编码，并送到输出流，编码的方式在子类中实现
	 * @param inputstream   包含了要进行编码的数据输入流
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 * 调用了在子类中实现的方法，以实现不同的编码方式
	 */
    public void encode(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        byte abyte0[] = new byte[bytesPerLine()];
        encodeBufferPrefix(outputstream);
        do
        {
            int j = readFully(inputstream, abyte0);
            if(j == 0)
                break;
            encodeLinePrefix(outputstream, j);
            for(int i = 0; i < j; i += bytesPerAtom())
                if(i + bytesPerAtom() <= j)
                    encodeAtom(outputstream, abyte0, i, bytesPerAtom());
                else
                    encodeAtom(outputstream, abyte0, i, j - i);

            if(j < bytesPerLine())
                break;
            encodeLineSuffix(outputstream);
        } while(true);
        encodeBufferSuffix(outputstream);
    }

	/**
	 * 对输入的字节数组进行编码
	 * @param abyte0        要进行编码的字节数组
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    public void encode(byte abyte0[], OutputStream outputstream)
        throws IOException
    {
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        encode(((InputStream) (bytearrayinputstream)), outputstream);
    }

	/**
	 * 对输入的字节数组进行编码
	 * @param abyte0        要进行编码的字节数组
	 * @return              编码结果字符串
	 * @throws IOException  在编码过程中发生 I/O 错误
	 */
    public String encode(byte abyte0[])
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        String s = null;
        try
        {
            encode(((InputStream) (bytearrayinputstream)), ((OutputStream) (bytearrayoutputstream)));
            s = bytearrayoutputstream.toString("8859_1");
        }
        catch(Exception exception)
        {
            throw new Error("ChracterEncoder::encodeBuffer internal error");
        }
        return s;
    }

	/**
	 * 对输入流进行编码，并送到输出流
	 * @param inputstream   包含了要进行编码的数据输入流
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 * 调用了在子类中实现的方法，以实现不同的编码方式
	 */
	public void encodeBuffer(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        byte abyte0[] = new byte[bytesPerLine()];
        encodeBufferPrefix(outputstream);
        int j;
        do
        {
            j = readFully(inputstream, abyte0);
            if(j == 0)
                break;
            encodeLinePrefix(outputstream, j);
            for(int i = 0; i < j; i += bytesPerAtom())
                if(i + bytesPerAtom() <= j)
                    encodeAtom(outputstream, abyte0, i, bytesPerAtom());
                else
                    encodeAtom(outputstream, abyte0, i, j - i);

            encodeLineSuffix(outputstream);
        } while(j >= bytesPerLine());
        encodeBufferSuffix(outputstream);
    }

	/**
	 * 对输入数据进行编码，并送到输出流
	 * @param abyte0        要进行编码的数据
	 * @param outputstream  输出编码结果的输出流
	 * @throws IOException  在编码过程中发生 I/O 错误
	 * 调用了在子类中实现的方法，以实现不同的编码方式
	 */
    public void encodeBuffer(byte abyte0[], OutputStream outputstream)
        throws IOException
    {
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        encodeBuffer(((InputStream) (bytearrayinputstream)), outputstream);
    }

	/**
	 * 对输入数据进行编码为字符串
	 * @param abyte0        要进行编码的数据
	 * @return              编码结果
	 * 调用了在子类中实现的方法，以实现不同的编码方式
	 */
    public String encodeBuffer(byte abyte0[])
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        try
        {
            encodeBuffer(((InputStream) (bytearrayinputstream)), ((OutputStream) (bytearrayoutputstream)));
        }
        catch(Exception exception)
        {
            throw new Error("ChracterEncoder::encodeBuffer internal error");
        }
        return bytearrayoutputstream.toString();
    }

    protected PrintStream pStream;
}