package cn.com.infosec.netseal.common.communication.message.impl.bytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import cn.com.infosec.netseal.common.communication.message.Request;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.ProtocolDataException;

public class ByteRequest extends Request {

	public ByteRequest() {
	}

	private static byte REQUEST_TAG = (byte) 0xF0;
	private static byte HEAD_TAG = (byte) 0xF1;
	private static byte BODY_TAG = (byte) 0xF2;

	/**
	 * 
	 * 格式：
	 * 
	 * @param bs
	 * @throws ProtocolDataException
	 * @throws IOException
	 */
	public ByteRequest(byte[] bs) throws Exception {

		String type = null;
		Properties pro = new Properties();
		byte[] len = new byte[4];
		byte[] tag = new byte[1];
		byte[] data = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(bs);

		// 请求标识符
		bin.read(tag);
		if (tag[0] != REQUEST_TAG)
			throw new ProtocolDataException(" format invalid (REQUEST_TAG)");

		// head部分
		bin.read(tag);
		if (tag[0] != HEAD_TAG)
			throw new ProtocolDataException(" format invalid (HEAD_TAG)");

		// type
		bin.read(len);
		int size = bytes2Int(len);
		data = new byte[size];

		bin.read(data);
		type = new String(data, Constants.UTF_8);

		// 主体(body)部分
		bin.read(tag);
		if (tag[0] != BODY_TAG)
			throw new ProtocolDataException(" format invalid (BODY_TAG)");

		bin.read(len);
		int length = bytes2Int(len); // get the size of items
		// parse item content
		for (int i = 0; i < length; i++) {
			String name = "";
			String value = "";

			bin.read(len);
			size = bytes2Int(len);
			data = new byte[size];
			bin.read(data);
			name = new String(data, Constants.UTF_8);

			bin.read(len);
			size = bytes2Int(len);
			data = new byte[size];
			bin.read(data);
			value = new String(data, Constants.UTF_8);

			pro.setProperty(name, value);
		}
		bin.close();

		setType(type);
		setData(pro);
	}

	public byte[] getBytes() throws Exception {
		String type = getType();
		Properties pro = getData();

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bout.write(new byte[] { REQUEST_TAG });
		bout.write(new byte[] { HEAD_TAG });

		bout.write(int2Bytes(getBytes(type).length));
		bout.write(getBytes(type));

		bout.write(new byte[] { BODY_TAG });
		bout.write(int2Bytes(pro.size()));

		Enumeration keys = pro.keys();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			String value = pro.getProperty(name);

			bout.write(int2Bytes(getBytes(name).length));
			bout.write(getBytes(name));

			bout.write(int2Bytes(getBytes(value).length));
			bout.write(getBytes(value));
		}

		byte[] bs = bout.toByteArray();
		bout.close();

		return bs;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" type:" + getType());
		Properties pro = getData();
		Enumeration keys = pro.keys();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			String value = pro.getProperty(name);
			sb.append(" " + name + ":" + value);
		}

		return sb.toString();
	}

}
