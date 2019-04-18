package cn.com.infosec.netseal.common.util.p10;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Enumeration;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.util.encoders.Base64;

public class JKSFile {
	//private String file = Constants.KEY_PATH + Constants.TO_SEAL_JKS;
	private String pwd = Constants.KEY_PWD;
	private KeyStore keyStore = null;

	public JKSFile(String jksPath) throws Exception {
		FileInputStream in = null;
		FileOutputStream fous = null;

		try {
			File f = new File(jksPath);
			if (!f.exists()) {
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(null, null);
				fous = new FileOutputStream(jksPath);
				ks.store(fous, pwd.toCharArray());
			}

			in = new FileInputStream(jksPath);
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(in, pwd.toCharArray());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fous != null)
					fous.close();
			} catch (Exception e) {
			}

			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 列出JKS文件中的所有别名
	 * 
	 * @return
	 */
	public String[] listAliases() throws KeyStoreException {
		Enumeration aliases = keyStore.aliases();
		if (aliases != null) {
			ArrayList list = new ArrayList();
			while (aliases.hasMoreElements()) {
				list.add(aliases.nextElement());
			}
			return (String[]) list.toArray(new String[0]);
		}
		return null;
	}

	/**
	 * 按别名删除实体
	 * 
	 * @param alias
	 * @return
	 */
	public boolean remove(String alias, String jksPath) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(jksPath);
			keyStore.deleteEntry(alias);
			if (pwd != null)
				keyStore.store(out, pwd.toCharArray());
			else
				keyStore.store(out, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

	// /**
	// * 导入pfx文件
	// *
	// * @param pfxFile
	// * @param pwd
	// * @return 返回字符数组str[0]:别名,str[1]:口令 ,str[2]sm2私钥,str[3]证书,str[4]算法名称,返回null代表导入失败
	// */
	// public String[] importPFX(String pfxFile, String pwd) {
	// FileInputStream in = null;
	// FileOutputStream out = null;
	// try {
	// // 解析pfx
	// KeyStore pfx = KeyStore.getInstance("PKCS12", provider);
	// in = new FileInputStream(pfxFile);
	// pfx.load(in, pwd.toCharArray());
	// Enumeration aliases = pfx.aliases();
	// String keyAlias = null;
	// String certAlias = null;
	// while (aliases.hasMoreElements()) {
	// String alias = (String) aliases.nextElement();
	// if (pfx.isCertificateEntry(alias))
	// certAlias = alias;
	// if (pfx.isKeyEntry(alias))
	// keyAlias = alias;
	// }
	// if (keyAlias == null) {
	// System.out.println("no key entry in the pfx file: " + pfxFile);
	// return null;
	// }
	// // 获取私钥
	// Key k = pfx.getKey(keyAlias, "".toCharArray());
	// // 获取公钥证书
	// X509Certificate cert = (X509Certificate) pfx.getCertificate(keyAlias);
	// if ((cert == null)) {
	// if (certAlias != null)
	// cert = (X509Certificate) pfx.getCertificate(certAlias);
	// if (cert == null) {
	// System.out.println("no certificate found in the pfx file: " + pfxFile);
	// return null;
	// }
	// }
	// // 用sun检查证书格式
	// testCertbySunJce(cert);
	//
	// // 获取证书链
	// Certificate[] certChain = pfx.getCertificateChain(keyAlias);
	// if (certChain == null)
	// certChain = pfx.getCertificateChain(certAlias);
	// if (certChain == null) {
	// System.out.println("no certificate chain found in the pfx file: " + pfxFile);
	// return null;
	// }
	// // 获取公钥
	// PublicKey pubk = cert.getPublicKey();
	//
	// String[] result = new String[5];
	// // 产生别名
	// result[0] = genAlias(cert);
	// // 检查pfx文件中公私钥对是否匹配
	// if (pubk instanceof RSAPublicKey) {
	// compareKey((RSAPrivateKey) k, (RSAPublicKey) pubk, result[0]);
	// } else {
	// System.out.println("no rsa key pair found in the pfx file: " + pfxFile);
	// return null;
	// }
	// if (result[0] == null || "".equals(result[0])) {
	// return null;
	// }
	// // 产生口令
	// result[1] = genPassword(result[0]);
	//
	// // RSA证书保存到jks文件中
	// if (!keyStore.containsAlias(result[0]))
	// keyStore.setKeyEntry(result[0], k, result[1].toCharArray(), certChain);
	// else {
	// RSAPrivateKey priK = (RSAPrivateKey) k;
	// RSAPublicKey pubK = (RSAPublicKey) cert.getPublicKey();
	// try {
	// compareKey(priK, pubK, result[0]);
	// keyStore.deleteEntry(result[0]);
	// keyStore.setKeyEntry(result[0], k, result[1].toCharArray(), certChain);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// out = new FileOutputStream(file);
	// if (storePwd != null)
	// keyStore.store(out, storePwd.toCharArray());
	// else
	// keyStore.store(out, null);
	//
	// result[3] = new String(Base64.encode(cert.getEncoded()));
	// result[4] = "RSA";
	// return result;
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// } finally {
	// try {
	// if (in != null)
	// in.close();
	// } catch (Exception e) {
	// }
	// try {
	// if (out != null)
	// out.close();
	// } catch (Exception e) {
	// }
	// }
	// }

	private static X509Certificate getCert(Key k, Certificate[] certs) {
		if (certs == null)
			return null;

		if (k != null) {
			RSAKey priK = (RSAKey) k;
			BigInteger priM = priK.getModulus();
			for (int i = 0, length = certs.length; i < length; i++) {
				X509Certificate cert = (X509Certificate) certs[i];
				RSAKey pubK = (RSAKey) cert.getPublicKey();
				if (priM.equals(pubK.getModulus())) {
					return cert;
				}
			}
		} else {
			if (certs.length == 1)
				return (X509Certificate) certs[0];
			for (int i = 0, lengthi = certs.length; i < lengthi; i++) {
				X509Certificate certi = (X509Certificate) certs[i];
				boolean find = true;
				for (int j = 0, lengthj = certs.length; i < lengthj; i++) {
					X509Certificate certj = (X509Certificate) certs[j];
					if (certi.getSubjectDN().equals(certj.getIssuerDN())) {
						find = false;
						break;
					}
				}
				if (find == true) {
					return certi;
				}
			}
		}
		return null;
	}

	public String[] importP10(Certificate[] certs, String jksPath) throws Exception {
		String keyLable = Constants.P10_LABEL;
		FileOutputStream out = null;

		try {
			Key k = keyStore.getKey(keyLable, pwd.toCharArray());
			if (k == null)
				throw new Exception("Can not find private key");

			String[] results = new String[2];
			X509Certificate cert = getCert(k, certs);
			if (cert == null)
				throw new Exception("Can not find certificate by Modulus from the private key");

			testCertbySunJce(cert);

			results[0] = keyLable;
			results[1] = Constants.KEY_PWD;

			keyStore.deleteEntry(keyLable);
			keyStore.setKeyEntry(results[0], k, results[1].toCharArray(), certs);

			out = new FileOutputStream(jksPath);
			if (pwd != null)
				keyStore.store(out, pwd.toCharArray());
			else
				keyStore.store(out, null);

			return results;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

	public void prepareP10(Key key, String keyLable, Certificate[] certs, String jksPath) throws Exception {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(jksPath);

			keyStore.setKeyEntry(keyLable, key, pwd.toCharArray(), certs);
			if (pwd != null)
				keyStore.store(out, pwd.toCharArray());
			else
				keyStore.store(out, null);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 导入JKS文件
	 * 
	 * @param jksFile
	 *            需要导入的JKS对象
	 * @param aliasAndPwd
	 *            二维数组，每项表示一对别名和口令
	 * @return 二维数组，每项表示一对别名和口令
	 */
	public String[][] importJKS(JKSFile jks, String[][] aliasAndPwd) throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}

	/**
	 * 导入cert文件
	 * 
	 * @param certFile
	 * @return 返回别名, 返回null代表导入失败
	 */
	public String[] importCert(String certFile, String jksPath) {
		try {
			X509Certificate cert = CertificateUtil.generateCertificate(certFile);
			if (cert == null) {
				return null;
			}
			return importCert(cert, jksPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] importCert(Certificate[] certs, String jksPath) throws Exception {
		X509Certificate cert = getCert(null, certs);
		return importCert(cert, jksPath);
	}

	private void compareKey(RSAPrivateKey prik, RSAPublicKey pubk, String alias) throws SecurityException {
		if (!prik.getModulus().equals(pubk.getModulus()))
			throw new SecurityException("The publickey of certificate is not for the privatekey,the alias of the certificate is :" + alias);
	}

	private void testCertbySunJce(Certificate cert) throws Exception {
		byte[] bs = cert.getEncoded();
		ByteArrayInputStream in = new ByteArrayInputStream(bs);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate c = cf.generateCertificate(in);
		if (c == null)
			throw new Exception("Parse certificate by sun jce failed");
	}

	/**
	 * 导入cert文件
	 * 
	 * @param certFile
	 * @return 返回别名, 返回null代表导入失败
	 * @throws Exception
	 */
	public String[] importCert(X509Certificate cert, String jksPath) throws Exception {
		if (cert == null)
			return null;
		testCertbySunJce(cert);

		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			String alias = genAlias(cert);
			if (alias == null || "".equals(alias)) {
				return null;
			}
			if (cert.getPublicKey() instanceof RSAPublicKey) {
				out = new FileOutputStream(jksPath);
				String entryType = "cert";
				if (keyStore.containsAlias(alias)) {
					if (keyStore.isCertificateEntry(alias))
						keyStore.setCertificateEntry(alias, cert);
					else {
						entryType = "key";
						String keypasswd = genPassword(alias);
						Key k = keyStore.getKey(alias, keypasswd.toCharArray());
						RSAPrivateKey priK = (RSAPrivateKey) k;
						RSAPublicKey pubK = (RSAPublicKey) cert.getPublicKey();
						try {
							compareKey(priK, pubK, alias);
							keyStore.deleteEntry(alias);
							keyStore.setKeyEntry(alias, k, keypasswd.toCharArray(), new Certificate[] { cert });
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					keyStore.setCertificateEntry(alias, cert);
				}

				if (pwd != null)
					keyStore.store(out, pwd.toCharArray());
				else
					keyStore.store(out, null);
				return new String[] { alias, entryType, null, "RSA" };
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

	public String importPrivateKey(String keylable, Key key, Certificate cert, String jksPath) {
		if ((keylable == null) || (key == null))
			return null;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(jksPath);
			String pwd = genPassword(keylable);
			Certificate[] chain = new Certificate[1];
			chain[0] = cert;
			keyStore.setKeyEntry(keylable, key, pwd.toCharArray(), chain);
			if (pwd != null)
				keyStore.store(out, pwd.toCharArray());
			else
				keyStore.store(out, null);
			return pwd;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 生成别名
	 * 
	 * @param cert
	 * @return
	 */
	public String genAlias(X509Certificate cert) {
		String alias = "";
		try {
			alias = cert.getSubjectDN().getName();
			alias = alias.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			alias = alias + ":" + CertificateUtil.getSubjectKid(cert);
		} catch (Exception e) {
			return null;
		}
		return alias;
	}

	/**
	 * 用别名生成口令
	 * 
	 * @param alias
	 * @return
	 */
	private String genPassword(String alias) {
		byte[] digest = null;
		try {
			MessageDigest dig = MessageDigest.getInstance("SHA1");
			digest = dig.digest(alias.getBytes("GBK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(Base64.encode(digest));
	}

	public RSAPrivateKey getPrivateKey(String alias, String pwd) {
		if (alias == null)
			return null;
		try {
			if (keyStore.containsAlias(alias) && keyStore.isKeyEntry(alias)) {
				if (pwd == null)
					pwd = genPassword(alias);
				return (RSAPrivateKey) keyStore.getKey(alias, pwd.toCharArray());
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public X509Certificate getCertificate(String alias) {
		if (alias == null)
			return null;
		try {
			if (keyStore.containsAlias(alias))
				return (X509Certificate) keyStore.getCertificate(alias);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Certificate[] getCertChain(String alias) {
		if (alias == null)
			return null;
		try {
			if (keyStore.containsAlias(alias))
				return keyStore.getCertificateChain(alias);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}*/

	public KeyStore getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	public String getStorePwd() {
		return pwd;
	}

	public void setStorePwd(String storePwd) {
		this.pwd = storePwd;
	}

}
