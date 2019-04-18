package cn.com.infosec.netseal.common.entity.vo;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.util.StringUtil;

public class FileVO extends BaseVO {
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName());
		sb.append(" [");
		sb.append("fileName:");
		sb.append(StringUtil.parseStringWithDefault(getFileName(), Constants.DEFAULT_STRING));
		sb.append(" fileSize:");
		sb.append(getFileSize());
		sb.append(" fileTime:");
		sb.append(getFileTime());
		sb.append("]");

		return sb.toString();
	}
}
