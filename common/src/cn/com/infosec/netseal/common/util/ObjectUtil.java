package cn.com.infosec.netseal.common.util;

public class ObjectUtil {

	public static Integer toInteger(Object obj) {
		if (obj == null)
			return null;
		else
			return Integer.valueOf(obj.toString());
	}

	public static Long toLong(Object obj) {
		if (obj == null)
			return null;
		else
			return Long.valueOf(obj.toString());
	}

}
