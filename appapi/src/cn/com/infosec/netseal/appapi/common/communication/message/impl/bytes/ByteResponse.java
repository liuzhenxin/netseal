package cn.com.infosec.netseal.appapi.common.communication.message.impl.bytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import cn.com.infosec.netseal.appapi.common.communication.message.Response;
import cn.com.infosec.netseal.appapi.common.define.Constants;
import cn.com.infosec.netseal.appapi.common.exceptions.ProtocolDataException;

public class ByteResponse extends Response {

	private static byte RESPONSE_TAG = (byte) 0xE0;
	private static byte HEAD_TAG = (byte) 0xE1;
	private static byte BODY_TAG = (byte) 0xE2;

	public ByteResponse() {
	}

	public ByteResponse(Response res) {
		this.setErrMsg(res.getErrMsg());
		this.setErrCode(res.getErrCode());
		this.setData(res.getData());
	}

	public ByteResponse(byte[] bs) throws Exception {

		// 自定义协议
		String errCode = null;
		String errMsg = null;
		byte[] tag = new byte[1];
		byte[] len = new byte[4];
		byte[] data = null;
		Properties pro = new Properties();

		// 标识符
		ByteArrayInputStream bin = new ByteArrayInputStream(bs);
		bin.read(tag);
		if (tag[0] != RESPONSE_TAG)
			throw new ProtocolDataException(" format invalid (RESPONSE_TAG)");

		// 头信息(head)
		bin.read(tag);
		if (tag[0] != HEAD_TAG)
			throw new ProtocolDataException(" format invalid (HEAD_TAG)");

		// error code
		bin.read(len);
		int size = bytes2Int(len);
		data = new byte[size];
		bin.read(data);
		errCode = new String(data, Constants.UTF_8);

		// error message
		bin.read(len);
		size = bytes2Int(len);
		data = new byte[size];
		bin.read(data);
		errMsg = new String(data, Constants.UTF_8);

		// 主体信息(body)
		bin.read(tag);
		if (tag[0] != BODY_TAG)
			throw new ProtocolDataException(" format invalid (BODY_TAG)");

		bin.read(len);
		int length = bytes2Int(len); // get the size of items
		// parse item content
		for (int j = 0; j < length; j++) {
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

		setErrCode(Integer.parseInt(errCode));
		setErrMsg(errMsg);
		setData(pro);
	}

	public byte[] getBytes() throws Exception {
		String errCode = String.valueOf(getErrCode());
		String errMsg = getErrMsg();
		Properties pro = getData();

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bout.write(new byte[] { RESPONSE_TAG }); // 0xE0 binary reponse tag
		bout.write(new byte[] { HEAD_TAG }); // 0xE1 head tag

		bout.write(int2Bytes(getBytes(errCode).length));
		bout.write(getBytes(errCode));

		bout.write(int2Bytes(getBytes(errMsg).length));
		bout.write(getBytes(errMsg));

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
		sb.append(" errCode:" + getErrCode());
		sb.append(" errMsg:" + getErrMsg());

		Properties pro = getData();
		if (pro != null) {
			Enumeration keys = pro.keys();
			while (keys.hasMoreElements()) {
				String name = (String) keys.nextElement();
				String value = pro.getProperty(name);
				sb.append(" " + name + ":" + value);
			}
		}

		return sb.toString();
	}

}
