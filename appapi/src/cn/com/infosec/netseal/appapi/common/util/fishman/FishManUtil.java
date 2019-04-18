package cn.com.infosec.netseal.appapi.common.util.fishman;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.com.infosec.netseal.appapi.common.algorithm.sm2.SM2;
import cn.com.infosec.netseal.appapi.common.util.HexUtil;

public class FishManUtil {

	public static final int TYPE = fm_jni_api.FM_DEV_TYPE_PCIE_1_0X;
	public static final int FLAG = fm_jni_api.FM_OPEN_MULTITHREAD;

	/**
	 * 打开设备
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void openDevice() throws Exception {
		int retCode = fm_jni_api.FM_CPC_JNI_OpenDevice(new byte[] { 0 }, TYPE, FLAG);
		if (retCode != 0) {
			throw new Exception("open fish man device error, retCode = " + retCode);
		}
	}

	/**
	 * 关闭设备
	 * 
	 * @throws Exception
	 */
	public static void closeDevice() throws Exception {
		int retCode = fm_jni_api.FM_CPC_JNI_CloseDevice();
		if (retCode != 0) {
			throw new Exception("close fish man device error, retCode = " + retCode);
		}
	}

	/**
	 * 用户登录
	 * 
	 * @param pin
	 * @param retry
	 *            PIN码错误时, 剩余尝试次数
	 * @return
	 * @throws Exception
	 */
	public static void login(String pin, int[] retry) throws Exception {
		int[] user_refer = new int[1];
		int retCode = fm_jni_api.FM_CPC_JNI_USER_Login(0, pin.getBytes(), pin.length(), user_refer, retry);
		if (retCode != 0) {
			throw new Exception("user login error, retCode = " + retCode);
		}
	}

	/**
	 * 修改用户PIN码
	 * 
	 * @param oldPin
	 * @param newPin
	 * @param retry
	 *            PIN码错误时, 剩余尝试次数
	 * @return
	 * @throws Exception
	 */
	public static void modifyPin(String oldPin, String newPin, int[] retry) throws Exception {
		int retCode = fm_jni_api.FM_CPC_JNI_USER_ChangePin(1, oldPin.getBytes(), oldPin.length(), newPin.getBytes(), newPin.length(), retry);
		if (retCode != 0) {
			throw new Exception("modify pin error, retCode = " + retCode);
		}
	}

	/**
	 * 根据用户编号注销
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void logoutUser(int[] i) throws Exception {
		int retCode = fm_jni_api.FM_CPC_JNI_USER_Logout(i);
		if (retCode != 0) {
			throw new Exception("user logout error, retCode = " + retCode);
		}
	}

	/**
	 * 显示卡内用户
	 * 
	 * @param bs
	 * @return
	 * @throws Exception
	 */
	public static List<FishManUserInfo> listFishmanUser() throws Exception {
		List<FishManUserInfo> list = new ArrayList<FishManUserInfo>();
		byte[] bs = new byte[256];

		int retCode = fm_jni_api.FM_CPC_JNI_USER_GetInfo(bs);
		if (retCode != 0) {
			throw new Exception("list fishman user error, retCode = " + retCode);
		}

		if (HexUtil.byte2Int(bs) == 0)
			return list;

		byte[] bs_20 = new byte[20];
		byte[] bs_16 = new byte[16];

		for (int i = 0; i < 6; i++) {
			System.arraycopy(bs, i * 20, bs_20, 0, 20);
			System.arraycopy(bs_20, 0, bs_16, 0, 16);

			boolean isCurrent = bs_20[16] < 0 ? true : false;
			String userType = (bs_20[16] & 0x0f) == 1 ? "管理员" : "操作员";
			String userStatus = (bs_20[17] & 0xff) == 1 ? "已登录" : "未登录";

			FishManUserInfo fm = new FishManUserInfo();
			fm.setUserID(new String(bs_16));
			fm.setUserStatus(userStatus);
			fm.setUserType(userType);
			fm.setIsCurrentUser(isCurrent);

			list.add(fm);

		}

		return list;
	}

	/**
	 * 判断管理员、操作员登录 状态、个数
	 * 
	 * @return
	 * @throws Exception
	 */
	private static void checkUserLoginNum() throws Exception {
		int num = 0;
		boolean flag = false;

		List<FishManUserInfo> list = listFishmanUser();
		if (list.size() == 0)
			return;

		for (int i = 0; i < list.size(); i++) {
			FishManUserInfo fm = list.get(i);
			if ("操作员".equals(fm.getUserType()) && "已登录".equals(fm.getUserStatus()))
				flag = true;
			if ("管理员".equals(fm.getUserType()) && "已登录".equals(fm.getUserStatus()))
				num++;
		}

		if (num >= 3)
			flag = true;

		if (flag == false)
			throw new Exception("用户没有登录, 或登录个数不够");
	}

	/**
	 * 导入密钥
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static void importKey(int hKey, byte[] key) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		// 导入密钥
		retCode = fm_jni_api.FM_CPC_JNI_ImportKey(fm_jni_api.FM_ALG_SM4, key, key.length, new int[] { hKey });
		if (retCode != 0) {
			throw new Exception("fish man import key error, retCode = " + retCode);
		}
	}

	/**
	 * 导出密钥
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static byte[] exportKey(int hKey) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		byte[] key = new byte[16];
		retCode = fm_jni_api.FM_CPC_JNI_ExportKey(hKey, key, new int[] { key.length });
		if (retCode != 0) {
			throw new Exception("fish man export key error, retCode = " + retCode);
		}
		return key;
	}

	/**
	 * 导出密钥
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static List<byte[]> exportSM2Key(int hKey) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		List<byte[]> retList = new ArrayList<byte[]>();
		int retCode = 0;
		byte[] pPubkey = new byte[68];
		byte[] pPrikey = new byte[36];
		retCode = fm_jni_api.FM_CPC_JNI_ExportECCKeypair(hKey, pPubkey, pPrikey);
		if (retCode != 0) {
			throw new Exception("fish man export key error, retCode = " + retCode);
		}

		// 需要把公钥转换成信安SM2的格式 前边去掉4个字节的模长， 加上一个0x04 变成65个字节
		byte[] temp = new byte[65];
		temp[0] = (byte) 0x04;

		System.arraycopy(pPubkey, 4, temp, 1, 64);
		pPubkey = temp;

		// 私钥转换
		temp = new byte[32];
		System.arraycopy(pPrikey, 4, temp, 0, 32);
		pPrikey = temp;

		retList.add(pPubkey);
		retList.add(pPrikey);
		return retList;
	}

	/**
	 * 导入密钥
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static void importSM2Key(int hKey, List<byte[]> list) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		byte[] pPubkey = new byte[68];
		pPubkey[0] = (byte) 0x00;
		pPubkey[1] = (byte) 0x01;
		pPubkey[2] = (byte) 0x00;
		pPubkey[3] = (byte) 0x00;

		byte[] pPrikey = new byte[36];
		pPrikey[0] = (byte) 0x00;
		pPrikey[1] = (byte) 0x01;
		pPrikey[2] = (byte) 0x00;
		pPrikey[3] = (byte) 0x00;

		System.arraycopy(list.get(0), 1, pPubkey, 4, 64);
		System.arraycopy(list.get(1), 0, pPrikey, 4, 32);

		int retCode = 0;
		retCode = fm_jni_api.FM_CPC_JNI_ImportECCKeypair(new int[] { hKey }, pPubkey, pPrikey);
		if (retCode != 0) {
			throw new Exception("fish man export key error, retCode = " + retCode);
		}
	}

	/**
	 * SM4加密
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static byte[] encryptSM4(int hKey, byte[] in) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		in = pkcs5Padding(in);
		byte[] out = new byte[in.length];

		retCode = fm_jni_api.FM_CPC_JNI_Encrypt(hKey, fm_jni_api.FM_ALG_SM4, fm_jni_api.FM_ALGMODE_ECB, in, in.length, out, new int[] { out.length }, null, 0, null, 0);
		if (retCode != 0) {
			throw new Exception("fish man encrypt sm4 key error, retCode = " + retCode);
		}

		return out;
	}

	/**
	 * SM4解密
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static byte[] decryptSM4(int hKey, byte[] in) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		byte[] out = new byte[in.length];
		retCode = fm_jni_api.FM_CPC_JNI_Decrypt(hKey, fm_jni_api.FM_ALG_SM4, fm_jni_api.FM_ALGMODE_ECB, in, in.length, out, new int[] { out.length }, null, 0, null, 0);
		if (retCode != 0) {
			throw new Exception("fish man decrypt sm4 key error, retCode = " + retCode);
		}

		out = pkcs5PaddingClean(out);

		return out;
	}

	/**
	 * sm3
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static byte[] hashSM3(byte[] plain) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		// 初始化
		retCode = fm_jni_api.FM_CPC_JNI_SM3Init(null, null, 0);
		if (retCode != 0) {
			throw new Exception("fish man hash sm3 init error, retCode = " + retCode);
		}

		// 输入明文
		if (retCode == 0) {
			retCode = fm_jni_api.FM_CPC_JNI_SM3Update(plain, plain.length);
			if (retCode != 0) {
				throw new Exception("fish man hash sm3 update error, retCode = " + retCode);
			}
		}

		byte[] out = new byte[32];
		// 输出摘要
		if (retCode == 0) {
			retCode = fm_jni_api.FM_CPC_JNI_SM3Final(out, new int[] { out.length });
			if (retCode != 0) {
				throw new Exception("fish man hash sm3 final error, retCode = " + retCode);
			}
		}

		return out;
	}

	/**
	 * sm3
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static byte[] hashSM3(byte[] pubKey, byte[] id, byte[] plain) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		// 初始化
		retCode = fm_jni_api.FM_CPC_JNI_SM3Init(pubKey, id, id.length);
		if (retCode != 0) {
			throw new Exception("fish man hash sm3 init error, retCode = " + retCode);
		}

		// 输入明文
		if (retCode == 0) {
			retCode = fm_jni_api.FM_CPC_JNI_SM3Update(plain, plain.length);
			if (retCode != 0) {
				throw new Exception("fish man hash sm3 update error, retCode = " + retCode);
			}
		}

		byte[] out = new byte[32];
		// 输出摘要
		if (retCode == 0) {
			retCode = fm_jni_api.FM_CPC_JNI_SM3Final(out, new int[] { out.length });
			if (retCode != 0) {
				throw new Exception("fish man hash sm3 final error, retCode = " + retCode);
			}
		}

		return out;
	}

	/**
	 * 删除密钥
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static void delKey(int hKey) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;
		retCode = fm_jni_api.FM_CPC_JNI_DelKey(hKey);
		if (retCode != 0) {
			throw new Exception("fish man del key error, retCode = " + retCode);
		}

	}

	public static byte[] pkcs5Padding(byte[] in) {
		int numBlock = in.length % 16 == 0 ? 16 : 16 - (in.length % 16);

		byte[] block = new byte[numBlock];
		for (int i = 0; i < numBlock; i++) {
			block[i] = (byte) (numBlock & 0xff);
		}

		byte[] all = new byte[in.length + block.length];
		System.arraycopy(in, 0, all, 0, in.length);
		System.arraycopy(block, 0, all, in.length, block.length);

		return all;
	}

	public static byte[] pkcs5PaddingClean(byte[] in) {
		byte[] cleanBlock = new byte[0];
		try {
			byte last = in[in.length - 1];
			cleanBlock = new byte[in.length - last];
			System.arraycopy(in, 0, cleanBlock, 0, cleanBlock.length);
		} catch (Exception e) {
		}

		return cleanBlock;
	}

	/**
	 * 产生SM2密钥对, KEYID: 3
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<byte[]> genSM2Keypair(int hKey) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		List<byte[]> retList = new ArrayList<byte[]>();
		int retCode = 0;
		// 产生密钥对
		byte[] pPubkey = new byte[68];
		byte[] pPrikey = new byte[36];
		retCode = fm_jni_api.FM_CPC_JNI_GenECCKeypair(fm_jni_api.FM_ALG_SM2_1, new int[] { hKey }, pPubkey, pPrikey);
		if (retCode != 0) {
			throw new Exception("fish man device gen SM2 keypair error, retCode = " + retCode);
		}

		// 需要把公钥转换成信安SM2的格式 前边去掉4个字节的模长， 加上一个0x04 变成65个字节
		byte[] temp = new byte[65];
		temp[0] = (byte) 0x04;

		System.arraycopy(pPubkey, 4, temp, 1, 64);
		pPubkey = temp;

		// 私钥转换
		temp = new byte[32];
		System.arraycopy(pPrikey, 4, temp, 0, 32);
		pPrikey = temp;

		retList.add(pPubkey);
		retList.add(pPrikey);

		// 返回
		return retList;
	}

	/**
	 * 加密卡公钥加密SM2
	 * 
	 * @param hKey
	 * @param pu8InBuf
	 * @return
	 * @throws Exception
	 * @throws FMException
	 */
	public static byte[] encryptSM2(int hKey, byte[] in) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		// 私钥解密
		byte[] out = new byte[260];
		retCode = fm_jni_api.FM_CPC_JNI_ECCEncrypt(fm_jni_api.FM_ALG_SM2_1, hKey, in, in.length, null, out);
		if (retCode != 0) {
			throw new Exception("fish man device encrypt with SM2 public key error, retCode = " + retCode);
		}

		// 返回加密密文
		return out;
	}

	/**
	 * 加密卡私钥解密SM2
	 * 
	 * @param hKey
	 * @param pu8InBuf
	 * @return
	 * @throws Exception
	 * @throws FMException
	 */
	public static byte[] decryptSM2(int hKey, byte[] in) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		// 解密前需要把信安格式，转换成加密卡可识别的格式
		// 信安格式 0x04 32个字节X 32个字节Y 32个字节M 加密数据 共97+明文长度 个字节
		// 加密卡格式 4字节模长0x03000000 32字节X 32字节 Y 160字节加密数据不足补0 32个字节M 共260个字节

		// 信安XY值
		byte[] infosec_xy = new byte[64];
		System.arraycopy(in, 1, infosec_xy, 0, infosec_xy.length);

		// 信安M值
		byte[] infosec_m = new byte[32];
		System.arraycopy(in, 65, infosec_m, 0, infosec_m.length);

		// 信安加密数据值
		byte[] infosec_data = new byte[in.length - 97];
		System.arraycopy(in, 97, infosec_data, 0, infosec_data.length);

		// 加密卡数据160字节，不够后补0
		byte[] fishman_data = new byte[160];
		System.arraycopy(infosec_data, 0, fishman_data, 0, infosec_data.length);

		byte[] fishman_in = new byte[260];
		// 此处的四个字节表示加密前明文的长度，因NETPASS里的公钥是16个字节 0x10 用表示，这里就直接写死了
		// 下边的out[0x10]也是.
		fishman_in[0] = (byte) 0x10;
		fishman_in[1] = (byte) 0x00;
		fishman_in[2] = (byte) 0x00;
		fishman_in[3] = (byte) 0x00;

		System.arraycopy(infosec_xy, 0, fishman_in, 4, infosec_xy.length);
		System.arraycopy(fishman_data, 0, fishman_in, 68, fishman_data.length);
		System.arraycopy(infosec_m, 0, fishman_in, 228, infosec_m.length);

		int retCode = 0;
		// 私钥解密
		byte[] out = new byte[0x10];
		int[] outLen = new int[] { out.length };
		retCode = fm_jni_api.FM_CPC_JNI_ECCDecrypt(fm_jni_api.FM_ALG_SM2_1, hKey, fishman_in, null, out, outLen);
		if (retCode != 0) {
			throw new Exception("fish man device decrypt with SM2 private key error, retCode = " + retCode);
		}

		// 返回解密明文
		return out;
	}

	/**
	 * 删除SM2密钥对
	 * 
	 * @param hKey
	 * @throws Exception
	 */
	public static void delSM2Keypair(int hKey) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		// 删除RAS密钥对
		retCode = fm_jni_api.FM_CPC_JNI_DelECCKeypair(hKey);
		if (retCode != 0) {
			throw new Exception("fish man device delete SM2 key pair error, retCode = " + retCode);
		}
	}

	/**
	 * 加密卡签名SM2
	 * 
	 * @param hKey
	 * @param pu8InBuf
	 * @return
	 * @throws Exception
	 * @throws FMException
	 */
	public static byte[] signSM2(int hKey, byte[] hash) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;

		// 私钥签名
		byte[] out = new byte[64];
		retCode = fm_jni_api.FM_CPC_JNI_ECCSign(hKey, hash, hash.length, null, out);
		if (retCode != 0) {
			throw new Exception("fish man device sign with SM2 private key error, retCode = " + retCode);
		}

		// 结构封装
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		System.arraycopy(out, 0, r, 0, r.length);
		System.arraycopy(out, 32, s, 0, s.length);
		byte[] signed = SM2.xy2sign(r, s);

		// 返回签名结构
		return signed;
	}

	/**
	 * 加密卡验签SM2
	 * 
	 * @param hKey
	 * @param pu8InBuf
	 * @return
	 * @throws Exception
	 * @throws FMException
	 */
	public static boolean verifySM2(int hKey, byte[] hash, byte[] signed) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		// 结构拆解
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		SM2.sign2xy(signed, r, s);
		byte[] sinedData = new byte[64];
		System.arraycopy(r, 0, sinedData, 0, r.length);
		System.arraycopy(s, 0, sinedData, 32, s.length);

		int retCode = 0;
		// 公钥验签
		retCode = fm_jni_api.FM_CPC_JNI_ECCVerify(hKey, null, hash, hash.length, signed);
		if (retCode != 0) {
			return false;
		}

		return true;
	}

	/**
	 * 加密卡产生SM4密钥
	 * 
	 * @param hKey
	 * @param pu8InBuf
	 * @return
	 * @throws Exception
	 * @throws FMException
	 */
	public static boolean genSM4Key(int hKey, byte[] key) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;
		// 公钥验签
		retCode = fm_jni_api.FM_CPC_JNI_GenKey(fm_jni_api.FM_ALG_SM4, 16, new int[] { hKey }, key);
		if (retCode != 0) {
			return false;
		}

		return true;
	}

	/**
	 * 产生随机数
	 * 
	 * @param seedLen
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public static byte[] genRandom(int seedLen, byte[] out) throws Exception {
		// 增加用户登录权限判断
		checkUserLoginNum();

		int retCode = 0;
		// 产生随机数
		retCode = fm_jni_api.FM_CPC_JNI_GenRandom(seedLen, out);
		if (retCode != 0) {
			throw new Exception("fish man gen random error, retCode = " + retCode);
		}

		return out;
	}


	public static void addLibraryDir(String libraryPath) throws Exception {
		Field field = ClassLoader.class.getDeclaredField("usr_paths");
		field.setAccessible(true);
		String[] paths = (String[]) field.get(null);
		for (int i = 0; i < paths.length; i++) {
			if (libraryPath.equals(paths[i])) {
				return;
			}
		}

		String[] tmp = new String[paths.length + 1];
		System.arraycopy(paths, 0, tmp, 0, paths.length);
		tmp[paths.length] = libraryPath;
		field.set(null, tmp);
	}

	public static void main(String[] args) {
		
	}

}
