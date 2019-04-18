package cn.com.infosec.netseal.webserver.util;

import java.util.Properties;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class CheckValue {
	public static void checkParamValue(String name, String value, String type, long lenLimit) throws Exception {

		switch (type) {
		case Constants.PARAM_TYPE_STRING_NOT_NULL:
			if (value == null)
				throw new WebDataException(name + " value is null");

			if ("".equals(value.trim()))
				throw new WebDataException(name + " value is empty");

			if (getBytes(value).length > lenLimit)
				throw new WebDataException(name + " value len is over limit " + lenLimit);
			break;

		case Constants.PARAM_TYPE_STRING_NULLABLE:
			if (value != null && !"".equals(value))
				if (getBytes(value).length > lenLimit)
					throw new WebDataException(name + " value len is over limit " + lenLimit);
			break;

		case Constants.PARAM_TYPE_INT:
			try {
				Integer.parseInt(value);
			} catch (Exception e) {
				throw new WebDataException(name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_LONG:
			try {
				Long.parseLong(value);
			} catch (Exception e) {
				throw new WebDataException(name + " value is invalid");
			}
			break;

		case Constants.PARAM_TYPE_DATE:
			try {
				if (!DateUtil.checkDateValid(value))
					throw new Exception();
			} catch (Exception e) {
				throw new WebDataException(name + " value is invalid");
			}

		default:
			break;
		}
	}
	
	public static byte[] getBytes(String key) {
		return StringUtil.getBytes(key);
	}

	public static String getValue(String key) {
		return StringUtil.parseStringWithDefault(key, Constants.DEFAULT_STRING);
	}

	public static String getValue(Properties reqdata, String key) {
		return StringUtil.parseStringWithDefault(reqdata.getProperty(key), Constants.DEFAULT_STRING);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
