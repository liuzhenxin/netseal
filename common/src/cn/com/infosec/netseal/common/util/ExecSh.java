package cn.com.infosec.netseal.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import cn.com.infosec.netseal.common.log.LoggerUtil;

public class ExecSh {

	public static String exec(String command) {
		BufferedReader br = null;
		String tmp, result = "";
		try {
			Process pro = Runtime.getRuntime().exec(command);
			pro.waitFor();

			br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			while ((tmp = br.readLine()) != null) {
				result += tmp;
			}

			if (StringUtil.isBlank(result)) {
				br = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
				while ((tmp = br.readLine()) != null) {
					result += tmp;
				}
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("exec " + command + " error,", e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception e1) {
				}
		}

		return result;
	}

	public static String exec(String command, String workBench) throws Exception {
		BufferedReader br = null;
		String tmp, result = "";
		try {
			Process pro = Runtime.getRuntime().exec(command, null, new File(workBench));
			pro.waitFor();

			br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			while ((tmp = br.readLine()) != null) {
				result += tmp;
			}

			if (StringUtil.isBlank(result)) {
				br = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
				while ((tmp = br.readLine()) != null) {
					result += tmp;
				}
			}
		} catch (Exception e) {
			LoggerUtil.errorlog("exec " + command + ", work bench " + workBench + " error,", e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception e1) {
				}
		}

		return result;
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]).append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		System.out.println(ExecSh.exec(sb.toString()));
	}
}
