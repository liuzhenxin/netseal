package cn.com.infosec.netseal.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.infosec.asn1.DERUTCTime;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyyMMdd");

	public static String getDate(Long time) {
		if (time == null)
			return "";
		return sdf.format(new Date(time));
	}

	public static boolean checkDateValid(String str) {
		try {
			Date date = sdf.parse(str);
			return str.equals(sdf.format(date));
		} catch (ParseException e) {
			throw new NetSealRuntimeException(ErrCode.DATE_FORMAT_INVAILD, e.getMessage());
		}
	}

	public static String getDateDir() {
		return sdf_1.format(new Date()) + "/";
	}

	public static String getDate() {
		return sdf_1.format(new Date());
	}

	public static String getDateTime() {
		return sdf.format(new Date());
	}

	public static String getDateTime(Date date) {
		return sdf.format(date);
	}

	public static long getLongTime(String date) {
		try {
			return sdf.parse(date).getTime();
		} catch (ParseException e) {
			throw new NetSealRuntimeException(ErrCode.DATE_FORMAT_INVAILD, e.getMessage());
		}
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * current 比 limit 时间值晚 返回true
	 * 
	 * @param current
	 * @param limit
	 * @return
	 */
	public static boolean checkDateLimitAfter(String current, String limit) {
		try {
			Date date_1 = sdf.parse(current);
			Date date_2 = sdf.parse(limit);
			return date_1.after(date_2);
		} catch (ParseException e) {
			throw new NetSealRuntimeException(ErrCode.DATE_FORMAT_INVAILD, e.getMessage());
		}
	}

	/**
	 * current 比 limit 时间值早 返回 true
	 * 
	 * @param current
	 * @param limit
	 * @return
	 */
	public static boolean checkDateLimitBefor(String current, String limit) {
		try {
			Date date_1 = sdf.parse(current);
			Date date_2 = sdf.parse(limit);
			return date_1.before(date_2);
		} catch (ParseException e) {
			throw new NetSealRuntimeException(ErrCode.DATE_FORMAT_INVAILD, e.getMessage());
		}
	}

	/**
	 * isfj derUtcTime 类getDate 时间为2050年时会转成1950 差100年
	 * 
	 * @param date
	 * @return
	 */
	public static Date transUtcTimeDate(Date date) {
		if (date.getTime() < 0) {
			Date d = new Date();
			d.setYear(date.getYear() + 100);
			d.setMonth(date.getMonth());
			d.setDate(date.getDate());
			d.setHours(date.getHours());
			d.setMinutes(date.getMinutes());
			d.setSeconds(date.getSeconds());
			return d;
		} else
			return date;
	}

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse("2050-12-12 10:10:10");
		System.out.println(date);

		DERUTCTime utc = new DERUTCTime(date);
		System.out.println(transUtcTimeDate(utc.getDate()));
	}

}
