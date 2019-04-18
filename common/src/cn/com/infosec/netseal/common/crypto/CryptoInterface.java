package cn.com.infosec.netseal.common.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * <p>Title: 加密接口模块</p>
 */

import java.util.List;

public interface CryptoInterface {

	/**
	 * 硬件实现的初试化环境
	 */
	public void init() throws Exception;

	/**
	 * 硬件实现的释放环境
	 */
	public void free() throws Exception;

	/**
	 * 使用对称密钥算法对明文加密
	 * 
	 * @param key
	 *            密钥
	 * @param plain
	 *            明文
	 * @return 密文
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] key, byte[] plain, int hsmId) throws Exception;

	/**
	 * 使用对称密钥算法对密文解密
	 * 
	 * @param key
	 *            密钥
	 * @param enc
	 *            密文
	 * @return 明文
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] key, byte[] enc, int hsmId) throws Exception;

	/**
	 * 公钥加密
	 * 
	 * @param pubkey
	 *            公钥
	 * @param value
	 *            原文
	 * @return 密文
	 * @throws Exception
	 */
	public byte[] encryptWithPubkey(PublicKey pubkey, byte[] value, int hsmId) throws Exception;

	/**
	 * 使用私钥解密
	 * 
	 * @param prikey
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public byte[] decryptWithPrikey(PrivateKey prikey, byte[] value, int hsmId) throws Exception;

	/**
	 * 对明文进行签名操作
	 * 
	 * @param priKey
	 *            签名的非对称算法的私钥
	 * @param plain
	 *            明文
	 * @return 签名密文
	 * @throws Exception
	 */
	public byte[] sign(PublicKey pubkey, PrivateKey priKey, byte[] plain, int hsmId, String signAlg, byte[] id) throws Exception;

	/**
	 * 对签名进行验证
	 * 
	 * @param pubkey
	 *            验证签名的公钥
	 * @param srcdata
	 *            明文
	 * @param signed
	 *            签名密文
	 * @return 验证是否通过
	 */
	public boolean verify(PublicKey pubkey, byte[] plain, byte[] signed, int hsmId, String signAlg, byte[] id) throws Exception;

	/**
	 * 根证验证书签名
	 * 
	 * @param cert
	 * @param rootPubkey
	 * @return
	 * @throws Exception
	 */
	public void verifyCert(X509Certificate cert, PublicKey rootPubkey, int hsmId, String signAlg) throws Exception;

	/**
	 * 对明文进行摘要算法操作
	 * 
	 * @param plain
	 *            明文
	 * @return 摘要密文
	 * @throws Exception
	 */
	public byte[] hash(byte[] plain) throws Exception;

	/**
	 * 产生非对称密钥对
	 * 
	 * @return [0]为公钥，[1]为私钥
	 * @throws Exception
	 */
	public List<byte[]> genKeyPair(int hsmId) throws Exception;

	/**
	 * 产生对称密钥
	 * 
	 * @param length
	 *            密钥长度
	 * @return 对称密钥
	 * @throws Exception
	 */
	public byte[] genSecretKey(int hsmId) throws Exception;

	/**
	 * 产生一个指定长度的随机数
	 * 
	 * @param len
	 *            指定的长度
	 * @return 随机数
	 */
	public byte[] genRandom(int len) throws Exception;

}