package cn.com.infosec.netseal.common.algorithm.sm3;

import java.security.SecureRandom;

import cn.com.infosec.util.Base64;

public class SM3Util {

	public static byte[] hash(byte[] plain) {
		SM3 sm3 = new SM3();
		sm3.update(plain);
		byte[] digest = new byte[32];
		sm3.digest(digest);
		return digest;
	}

	public static byte[] genRandom(int size) {
		SecureRandom sr = new SecureRandom();
		byte[] radom = new byte[size];
		sr.setSeed(System.currentTimeMillis());
		sr.nextBytes(radom);
		return radom;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Base64.encode(SM3Util.hash("admin".getBytes())));
	}
}
