package cn.com.infosec.netseal.webserver.util;

import java.util.ArrayList;
import java.util.List;

public class TreeIdUtil {
	public static String genTreeId(List<String> treeIdList) {
		if (treeIdList == null || treeIdList.size() == 0) {
			return null;
		}
		// 编码前缀
		String preCode = treeIdList.get(0).substring(0,treeIdList.get(0).length() - 3);
		// 存储已有编码后缀
		List<Integer> cList = new ArrayList<Integer>();
		for (String codeStr : treeIdList) {
			codeStr = codeStr.substring(codeStr.length() - 3, codeStr.length());
			cList.add(Integer.valueOf(codeStr));
		}
		for (int i = 0; i < 1000; i++) {
			if (!cList.contains(i)) {
				// 返回编码
				return preCode + String.format("%03d", i);
			}
		}
		return null;
	}
	/**返回当前和上级treeId
	 * @param treeId
	 * @return
	 */
	public static List<String> pTreeIdList(String treeId) {
		List<String> treeIdList=new ArrayList<String>();
		if(treeId!=null && treeId.length()>0){
			for(int i=3;i<=treeId.length();i+=3){
				treeIdList.add(treeId.substring(0,i));
			}
		}
		
		return treeIdList;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> treeIdList = new ArrayList<String>();
		//treeIdList.add("1111222200030");
		//String code = TreeIdUtil.genTreeId(treeIdList);
		//System.out.println(code);
		
		treeIdList = TreeIdUtil.pTreeIdList("000001002");
		
		System.out.println(treeIdList.toString());
		
	}
}
