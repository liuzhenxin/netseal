package cn.com.infosec.netseal.webserver.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;

public class CommonUtil {
	
	/**时间格式转成long
	 * 开始时间
	 * @param times
	 * @return
	 * @throws ParseException
	 */
	public static long timeStrStart(String times) throws ParseException {
		if (times!=null && !"".equals(times)) {
			String timesadd=times+" 00:00:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long d= sdf.parse(timesadd).getTime();
			return d ;
		}
		return 0;
	}
	/**时间格式转成long
	 * 结束时间
	 * @param times
	 * @return
	 * @throws ParseException
	 */
	public static long timeStrEnd(String times) throws ParseException {
		if (times!=null && !"".equals(times)) {
			String timesadd=times+" 23:59:59";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    long d= sdf.parse(timesadd).getTime();
			return d ;
		}
		return 0;
	}
	
	
	
	
	
	public static String trimTheDN(String oldDN) {
		String newDN = "";
		String temp = "";
		oldDN = oldDN.trim();

		StringTokenizer st1 = new StringTokenizer(oldDN, "=");
		int i = 0;
		while (st1.hasMoreTokens()) {
			String s = st1.nextToken();
			if (i == 0) {
				temp = String.valueOf(String.valueOf(temp)).concat(
						String.valueOf(String.valueOf(s.trim())));
			} else {
				temp = String.valueOf(String.valueOf(new StringBuffer(String
						.valueOf(String.valueOf(temp))).append("=").append(
						s.trim())));
			}

			i += 1;
		}

		StringTokenizer st2 = new StringTokenizer(temp, ",");
		int j = 0;
		while (st2.hasMoreTokens()) {
			String s = st2.nextToken();
			if (j == 0) {
				newDN = String.valueOf(String.valueOf(newDN)).concat(
						String.valueOf(String.valueOf(s.trim())));
			} else {
				newDN = String.valueOf(String.valueOf(new StringBuffer(String
						.valueOf(String.valueOf(newDN))).append(",").append(
						s.trim())));
			}

			j += 1;
		}
		return newDN;
	}

	public static boolean isEmail(String email) {
		int locate_1 = 0;
		int locate_2 = 0;
		if (email.length() == 0) {
			return false;
		}

		locate_1 = email.indexOf("@");
		locate_2 = email.lastIndexOf("@");
		if (locate_2 == -1) {
			return false;
		}
		if (locate_1 != locate_2) {
			return false;
		}

		if ((locate_1 == 0) || (locate_1 == email.length())) {
			return false;
		}

		String header = "";
		String tail = "";

		header = email.substring(0, locate_1);
		tail = email.substring(locate_1 + 1, email.length());

		if (header.length() == 0) {
			return false;
		}

		if (tail.length() == 0) {
			return false;
		}

		if ((tail.startsWith(".")) || (tail.endsWith("."))) {
			return false;
		}

		int locate_3 = 0;
		locate_3 = tail.indexOf(".");
		if (locate_3 != -1) {
			if ((locate_3 == 0) || (locate_3 == tail.length())) {
				return false;
			}

			int locate_4 = 0;
			locate_4 = tail.indexOf("..");
			if (locate_4 != -1) {
				return false;
			}

		}

		return true;
	}

	public static boolean isIP(String ipString) {
		if (ipString == null)
			return false;
		int len = ipString.length();
		StringTokenizer st = null;
		Vector list = new Vector();
		if ((len < 7) || (len > 15)) {
			return false;
		}

		ipString = ipString.trim();
		if ((ipString.startsWith(".")) || (ipString.endsWith("."))) {
			return false;
		}

		int index = ipString.indexOf("..");
		if (index != -1) {
			return false;
		}
		st = new StringTokenizer(ipString, ".");
		while (st.hasMoreElements()) {
			String temp = st.nextElement().toString().trim();
			list.add(temp);
		}
		if (list.isEmpty()) {
			return false;
		}

		int num = list.size();
		if (num != 4) {
			return false;
		}

		for (int loop = 0; loop < num; loop++) {
			String item = list.elementAt(loop).toString().trim();
			try {
				int itemnum = Integer.parseInt(item);
				if ((itemnum >= 0) && (itemnum <= 255))
					continue;
				return false;
			} catch (NumberFormatException nfe) {
				int i;
				return false;
			}

		}

		return true;
	}

	public static boolean ifHasDigit(String temp) {
		for (int i = 0; i <= 9; i++) {
			String ch = String.valueOf(String.valueOf(i)).concat(" ");

			ch = ch.trim();
			if (temp.indexOf(ch) != -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean ifHasUppercase(String temp) {
		for (char ch = 'A'; ch <= 'Z'; ch = (char) (ch + '\001')) {
			String upper = String.valueOf(String.valueOf(ch)).concat("");
			if (temp.indexOf(upper) != -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean ifHasLowercase(String temp) {
		for (char ch = 'a'; ch <= 'z'; ch = (char) (ch + '\001')) {
			String lower = String.valueOf(String.valueOf(ch)).concat("");
			if (temp.indexOf(lower) != -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean ifHasLiteral(String temp) {
		String[] literals = { "!", "@", "#", "$", "%", "^", "&", "*", "(", ")",
				"<", ">", "?", "-", "+", "~" };
		int loop = literals.length;
		for (int i = 0; i < loop; i++) {
			String literal = literals[i];
			if (temp.indexOf(literal) != -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTheDayFromString(int location, String dayString) {
		String temp = dayString.substring(location - 1, location);

		if ("1".equalsIgnoreCase(temp)) {
			return true;
		}

		return !"0".equalsIgnoreCase(temp);
	}

	public static float getShortFloat(float f, int n) {
		String floatString = String.valueOf(String.valueOf(f)).concat("");
		int index = floatString.indexOf(".");
		if (index != -1) {
			int location = index + n + 1;
			int len = floatString.length();
			if (location >= len) {
				location = len;
			}
			floatString = floatString.substring(0, location);
		}

		return Float.parseFloat(floatString);
	}

	public static Vector listFile(String rp) {
		String listFileStr = "";

		File file = new File(rp);
		File[] list = file.listFiles();
		Vector fNameList = new Vector();

		for (int i = 0; i < list.length; i++) {
			try {
				if (list[i].isDirectory()) {
					continue;
				}

				fNameList.addElement(list[i].getName());
			} catch (Exception ex) {
				listFileStr = String.valueOf(String.valueOf(listFileStr))
						.concat(String.valueOf(String.valueOf(String
								.valueOf(String.valueOf(new StringBuffer(
										"Access deny：").append(
										list[i].getAbsolutePath()).append(
										"\r\n"))))));
			}

		}

		return fNameList;
	}

	public static boolean isFileExist(String fileName) {
		FileReader fr = null;
		LineNumberReader lnr = null;
		try {
			fr = new FileReader(fileName);
			lnr = new LineNumberReader(fr);
			lnr.readLine();
			lnr.close();
			fr.close();
		} catch (FileNotFoundException fnfe) {
			return false;
		} catch (IOException ioe) {
			int i;
			return false;
		} finally {
			if (lnr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					
				}
			}
		}
		return true;
	}

	public static byte[] getSHA(String originPassword) {
		byte[] digesta = null;

		if (originPassword == null) {
			return null;
		}

		try {
			MessageDigest alga = MessageDigest.getInstance("SHA-1");
			alga.update(originPassword.getBytes());

			digesta = alga.digest();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
		}

		return digesta;
	}

	public static byte[] getSHA(byte[] src) {
		byte[] digesta = null;

		if (src == null) {
			return null;
		}

		try {
			MessageDigest alga = MessageDigest.getInstance("SHA-1");
			alga.update(src);
			digesta = alga.digest();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
		}

		return digesta;
	}

	public static void main(String[] args) {

	}

}
