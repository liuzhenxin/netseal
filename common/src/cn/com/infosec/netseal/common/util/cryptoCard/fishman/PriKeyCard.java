package cn.com.infosec.netseal.common.util.cryptoCard.fishman;

import java.util.HashMap;

import cn.com.infosec.oscca.sm2.SM2PrivateKey;

/**
 * 存储私钥信息到内存
 * 内存中私钥信息与加密卡私钥信息一一匹配
 * 
 * @author Administrator
 *
 */
public class PriKeyCard {
	public static HashMap<Integer, SM2PrivateKey> keyCardMap = new HashMap<>();
	
	// 添加
	public synchronized static void addPriMap (int hkid, SM2PrivateKey sm2Pri) {
		keyCardMap.put(hkid, sm2Pri);
	}
	
	// 移除
	public synchronized static void removePriMap (int hkid) {
		keyCardMap.remove(hkid);
	}

	public static HashMap<Integer, SM2PrivateKey> getKeyCardMap() {
		return keyCardMap;
	}

	public static void setKeyCardMap(HashMap<Integer, SM2PrivateKey> keyCardMap) {
		PriKeyCard.keyCardMap = keyCardMap;
	}
	

}
