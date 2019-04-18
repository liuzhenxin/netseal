package cn.com.infosec.netseal.common.util;

import java.util.Hashtable;

public class CountMsgUtil {
	private static Hashtable<String, String> container = new Hashtable<String, String>();

	static {
		container.put("NO_DESC", "没有对应的操作类型");

		container.put("AddOperateLog", "添加交易日志");
		container.put("AddPrinter", "文档打印设置");
		container.put("GetPrinter", "获取打印份数");
		container.put("GetSeal", "获取印章");
		container.put("PdfStamp", "PDF盖章");
		container.put("PdfStampTemplate", "PDF模板盖章");
		container.put("OfdStamp", "OFD盖章");
		container.put("RegisterUser", "签章人注册");
		container.put("RequestSeal", "印章申请");
		container.put("UpdateSealPhoto", "更新印章图片");
		container.put("VerifyCert", "验证证书");
		container.put("VerifyPdfStamp", "验PDF章");
		container.put("VerifyOfdStamp", "验OFD章");
		
		container.put("GetSystemPro", "获取系统属性");
		container.put("Shutdown", "关闭服务");
		container.put("ReloadConfig", "重载配置");
	}

	public static String getCountMsg(String optype) {
		String countMsg = container.get(optype);
		if (countMsg == null)
			return container.get("NO_DESC");
		else
			return countMsg;
	}

}
