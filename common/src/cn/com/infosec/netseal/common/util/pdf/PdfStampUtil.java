package cn.com.infosec.netseal.common.util.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import cn.com.infosec.jce.provider.InfosecProvider;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.vo.asn1.X509CertEnvelope;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.CertUtil;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.GenStampUtil;
import cn.com.infosec.netseal.common.util.KeyStoreUtil;
import cn.com.infosec.netseal.common.util.OidUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.logger.LoggerConfig;
import cn.com.infosec.netseal.itextpdf.text.BadElementException;
import cn.com.infosec.netseal.itextpdf.text.Document;
import cn.com.infosec.netseal.itextpdf.text.Image;
import cn.com.infosec.netseal.itextpdf.text.Rectangle;
import cn.com.infosec.netseal.itextpdf.text.pdf.AcroFields;
import cn.com.infosec.netseal.itextpdf.text.pdf.PdfContentByte;
import cn.com.infosec.netseal.itextpdf.text.pdf.PdfName;
import cn.com.infosec.netseal.itextpdf.text.pdf.PdfPKCS7;
import cn.com.infosec.netseal.itextpdf.text.pdf.PdfReader;
import cn.com.infosec.netseal.itextpdf.text.pdf.PdfSignatureAppearance;
import cn.com.infosec.netseal.itextpdf.text.pdf.PdfStamper;
import cn.com.infosec.netsign.frame.config.ExtendedConfig;

public class PdfStampUtil {

	static {
		Security.addProvider(new InfosecProvider());
	}

	/**
	 * 插入图片
	 * 
	 * @param pageNumber
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @throws Exception
	 */
	public static byte[] pdfAddImage(byte[] pdfData, byte[] jpgData, int pageNumber, float x, float y, float w, float h) {
		PdfReader reader = null;
		PdfStamper stamper = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			reader = new PdfReader(pdfData);
			stamper = new PdfStamper(reader, baos);
			int totalPageNum = reader.getNumberOfPages();
			if (pageNumber > totalPageNum || pageNumber < 1)
				throw new NetSealRuntimeException(ErrCode.PAGE_NUM_OVER_LIMIT_IN_PDF, "page number over limit in pdf");

			PdfContentByte content = stamper.getOverContent(pageNumber);

			if (content == null)
				throw new NetSealRuntimeException("page number " + pageNumber + " out of range");

			Image img = Image.getInstance(jpgData);
			if (w > 0)
				img.scaleAbsoluteWidth(w);
			if (h > 0)
				img.scaleAbsoluteHeight(h);

			x = (x > 0) ? x : 0;
			y = (y > 0) ? y : 0;

			img.setAbsolutePosition(x, y);
			content.addImage(img);
			stamper.close();

			return baos.toByteArray();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("pdf add image error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("pdf add image error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "pdf add photo error, " + e.getMessage());
		} finally {
			try {
				if (stamper != null)
					stamper.close();
			} catch (Exception e) {
			}

			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 坐标签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param pageNum
	 * @param zx
	 * @param zy
	 * @param ux
	 * @param uy
	 * @param biznum
	 * @param pwd
	 * @return
	 */
	public static byte[] pdfStampByCoordinate(byte[] pdfData, byte[] photoData, int pageNum, float pdfX, float pdfY, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			// 加载密钥
			PrivateKey priKey = KeyStoreUtil.loadKey(key.getKeyPwdPlain(), key.getKeyMode(), keyData);

			// 构造证书对象
			X509Certificate cert = CertUtil.parseCert(keyCertData).getX509Cert();
			Certificate[] certChain = new Certificate[] { cert };

			reader = new PdfReader(pdfData);
			int totalPageNum = reader.getNumberOfPages();
			if (pageNum > totalPageNum || pageNum < 1)
				throw new NetSealRuntimeException(ErrCode.PAGE_NUM_OVER_LIMIT_IN_PDF, "page number over limit in pdf");

			Document document = new Document(reader.getPageSize(1));
			float width = document.getPageSize().getWidth();
			float height = document.getPageSize().getHeight();

			if (pdfX < 0 || pdfY < 0) {
				throw new NetSealRuntimeException(ErrCode.PAGE_XY_OVER_LIMIT_IN_PDF, "coordinate over limit in pdf, not less than 0");
			}
			if (pdfX > width || pdfY > height) {
				throw new NetSealRuntimeException(ErrCode.PAGE_XY_OVER_LIMIT_IN_PDF, "coordinate over limit in pdf, not more than max value(width:" + width + " or height:" + height + ")");
			}
			stamper = PdfStamper.createSignature(reader, new ByteArrayOutputStream(), '\0', null, true);
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setSubFilter(new PdfName("adbe.pkcs7.detached"));
			sap.setPdfDigestAlg("SHA1");
			sap.setProvider("INFOSEC");
			sap.setCrypto(priKey, certChain, null, PdfSignatureAppearance.WINCER_SIGNED);

			// 单位 磅 1英寸=72磅
			List listImg = FileUtil.getImagePro(photoData);
			float widthP = ((int) listImg.get(1) / Constants.GEN_SEAL_DPI * 72f);
			float heightP = ((int) listImg.get(2) / Constants.GEN_SEAL_DPI * 72f);
			float ux = pdfX + widthP;
			float uy = pdfY + heightP;
			sap.setVisibleSignature(new Rectangle(pdfX, pdfY, ux, uy), pageNum, StringUtil.isBlank(biznum) ? "NetSeal" + DateUtil.getCurrentTime() : biznum);
			sap.setLayer2Text(" ");
			sap.setAcro6Layers(true);

			Image img = Image.getInstance(photoData);
			sap.setImage(img);

			// 根据证书得到HASH算法
			X509CertEnvelope certVO = CertUtil.parseCert(keyCertData);
			String digestAlg = OidUtil.getHashAlg(certVO.getSigAlgOID());
			sap.setPdfDigestAlg(digestAlg);

			// 设置时间戳服务器
			ConfigUtil config = ConfigUtil.getInstance();
			HashMap<String, String> hmRsa = new HashMap<String, String>();
			hmRsa.put("url", config.getTsaRsaUrl()); // 10.20.84.22 192.168.0.205
			hmRsa.put("username", config.getTsaRsaUsername());
			hmRsa.put("userpwd", config.getTsaRsaUserpwd());
			hmRsa.put("policy", config.getTsaRsaPolicy());
			hmRsa.put("usetsa", String.valueOf(config.getTsaRsaUsetsa()));
			sap.setTsaparas(hmRsa);

			if (Constants.SM3.equalsIgnoreCase(digestAlg)) {
				HashMap<String, String> hmSM2 = new HashMap<String, String>();
				hmSM2.put("url", config.getTsaSM2Url()); // 10.20.84.22 192.168.0.205
				hmSM2.put("username", config.getTsaSM2Username());
				hmSM2.put("userpwd", config.getTsaSM2Userpwd());
				hmSM2.put("policy", config.getTsaSM2Policy());
				hmSM2.put("usetsa", String.valueOf(config.getTsaSM2Usetsa()));
				sap.setTsaparas(hmSM2);
				
				sap.setId(ConfigUtil.getInstance().getGmOid().getBytes());
				sap.setSubFilter(new PdfName("adbe.x509.seal"));
				sap.setPdfDigestAlg("SM3");
				// 计算签章数据
				byte[] stampData = GenStampUtil.genStampData(pdfData, sealData, key, keyData, keyCertData, rootHt, signKeyList, sap.getId());
				sap.setStampData(stampData);
			}

			byte[] signedResult = stamper.indictClose();
			return signedResult;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("pdf stamp by coordinate error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("pdf stamp by coordinate error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (stamper != null)
					stamper.close();
			} catch (Exception e) {
			}

			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 坐标盖章 最后一页
	 * 
	 * @param pdfData
	 * @param photoData
	 * @param pdfX
	 * @param pdfY
	 * @param biznum
	 * @param key
	 * @param keyData
	 * @param keyCertData
	 * @param sealData
	 * @param rootHt
	 * @param signKeyList
	 * @return
	 */
	public static byte[] pdfStampByCoordinate(byte[] pdfData, byte[] photoData, float pdfX, float pdfY, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			// 加载密钥
			PrivateKey priKey = KeyStoreUtil.loadKey(key.getKeyPwdPlain(), key.getKeyMode(), keyData);

			// 构造证书对象
			X509Certificate cert = CertUtil.parseCert(keyCertData).getX509Cert();
			Certificate[] certChain = new Certificate[] { cert };

			reader = new PdfReader(pdfData);
			int totalPageNum = reader.getNumberOfPages();

			Document document = new Document(reader.getPageSize(1));
			float width = document.getPageSize().getWidth();
			float height = document.getPageSize().getHeight();

			if (pdfX < 0 || pdfY < 0) {
				throw new NetSealRuntimeException(ErrCode.PAGE_XY_OVER_LIMIT_IN_PDF, "coordinate over limit in pdf, not less than 0");
			}
			if (pdfX > width || pdfY > height) {
				throw new NetSealRuntimeException(ErrCode.PAGE_XY_OVER_LIMIT_IN_PDF, "coordinate over limit in pdf, not more than max value(width:" + width + " or height:" + height + ")");
			}
			stamper = PdfStamper.createSignature(reader, new ByteArrayOutputStream(), '\0', null, true);
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setSubFilter(new PdfName("adbe.pkcs7.detached"));
			sap.setPdfDigestAlg("SHA1");
			sap.setProvider("INFOSEC");
			sap.setCrypto(priKey, certChain, null, PdfSignatureAppearance.WINCER_SIGNED);

			// 单位 磅 1英寸=72磅
			List listImg = FileUtil.getImagePro(photoData);
			float widthP = ((int) listImg.get(1) / Constants.GEN_SEAL_DPI * 72f);
			float heightP = ((int) listImg.get(2) / Constants.GEN_SEAL_DPI * 72f);
			float ux = pdfX + widthP;
			float uy = pdfY + heightP;
			sap.setVisibleSignature(new Rectangle(pdfX, pdfY, ux, uy), totalPageNum, StringUtil.isBlank(biznum) ? "NetSeal" + DateUtil.getCurrentTime() : biznum);
			sap.setLayer2Text(" ");
			sap.setAcro6Layers(true);

			Image img = Image.getInstance(photoData);
			sap.setImage(img);

			// 根据证书得到HASH算法
			X509CertEnvelope certVO = CertUtil.parseCert(keyCertData);
			String digestAlg = OidUtil.getHashAlg(certVO.getSigAlgOID());
			sap.setPdfDigestAlg(digestAlg);

			// 设置时间戳服务器
			ConfigUtil config = ConfigUtil.getInstance();
			HashMap<String, String> hmRsa = new HashMap<String, String>();
			hmRsa.put("url", config.getTsaRsaUrl()); // 10.20.84.22 192.168.0.205
			hmRsa.put("username", config.getTsaRsaUsername());
			hmRsa.put("userpwd", config.getTsaRsaUserpwd());
			hmRsa.put("policy", config.getTsaRsaPolicy());
			hmRsa.put("usetsa", String.valueOf(config.getTsaRsaUsetsa()));
			sap.setTsaparas(hmRsa);

			if (Constants.SM3.equalsIgnoreCase(digestAlg)) {
				HashMap<String, String> hmSM2 = new HashMap<String, String>();
				hmSM2.put("url", config.getTsaSM2Url()); // 10.20.84.22 192.168.0.205
				hmSM2.put("username", config.getTsaSM2Username());
				hmSM2.put("userpwd", config.getTsaSM2Userpwd());
				hmSM2.put("policy", config.getTsaSM2Policy());
				hmSM2.put("usetsa", String.valueOf(config.getTsaSM2Usetsa()));
				sap.setTsaparas(hmSM2);
				
				sap.setId(ConfigUtil.getInstance().getGmOid().getBytes());
				sap.setSubFilter(new PdfName("adbe.x509.seal"));
				sap.setPdfDigestAlg("SM3");
				// 计算签章数据
				byte[] stampData = GenStampUtil.genStampData(pdfData, sealData, key, keyData, keyCertData, rootHt, signKeyList, sap.getId());
				sap.setStampData(stampData);
			}

			byte[] signedResult = stamper.indictClose();
			return signedResult;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("pdf stamp by coordinate error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("pdf stamp by coordinate error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (stamper != null)
					stamper.close();
			} catch (Exception e) {
			}

			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 关键字签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param page
	 * @param keywords
	 * @param key
	 * @param pwd
	 * @return
	 */
	public static byte[] pdfStampByText(byte[] pdfData, byte[] photoData, int pageNum, String keywords, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			reader = new PdfReader(pdfData);
			int totalPageNum = reader.getNumberOfPages();
			if (pageNum > totalPageNum || pageNum < 1)
				throw new NetSealRuntimeException(ErrCode.PAGE_NUM_OVER_LIMIT_IN_PDF, "page number over limit in pdf");

			List listImg = FileUtil.getImagePro(photoData);
			// 单位 磅 1英寸=72磅
			float width = ((int) listImg.get(1) / Constants.GEN_SEAL_DPI * 72f);
			float height = ((int) listImg.get(2) / Constants.GEN_SEAL_DPI * 72f);

			float llx = 0, lly = 0;
			List<com.itextpdf.awt.geom.Rectangle2D.Float> list = PdfStampXY.getCoordinate(pdfData, pageNum, keywords);
			if (list.size() != 0) {
				com.itextpdf.awt.geom.Rectangle2D.Float coordinate = list.get(0);
				llx = coordinate.x - width * 0.5f;
				lly = coordinate.y - height * 0.5f;
			} else {
				throw new NetSealRuntimeException(ErrCode.KEYWORD_NOT_EXIST_IN_PDF, "keywords not exits in pdf");
			}

			return pdfStampByCoordinate(pdfData, photoData, pageNum, llx, lly, biznum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("pdf stamp by text error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("pdf stamp by text error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (stamper != null)
					stamper.close();
			} catch (Exception e) {
			}

			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 关键字签章, 最后一次出现关键字的位置签章
	 * 
	 * @param pdfData
	 * @param jpgData
	 * @param keywords
	 * @param biznum
	 * @param key
	 * @return
	 */
	public static byte[] pdfStampByText(byte[] pdfData, byte[] photoData, String keywords, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData,
			Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList) {
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			reader = new PdfReader(pdfData);
			int totalPageNum = reader.getNumberOfPages();

			List listImg = FileUtil.getImagePro(photoData);
			// 单位 磅 1英寸=72磅
			float width = ((int) listImg.get(1) / Constants.GEN_SEAL_DPI * 72f);
			float height = ((int) listImg.get(2) / Constants.GEN_SEAL_DPI * 72f);

			float llx = 0, lly = 0;
			List<com.itextpdf.awt.geom.Rectangle2D.Float> list = null;
			int pageNum = totalPageNum;
			for (; pageNum > 0; pageNum--) {
				list = PdfStampXY.getCoordinate(pdfData, pageNum, keywords);
				if (list.size() != 0) {
					com.itextpdf.awt.geom.Rectangle2D.Float coordinate = list.get(0);
					llx = coordinate.x - width * 0.5f;
					lly = coordinate.y - height * 0.5f;

					break;
				}
			}
			if (list == null || list.size() == 0) {
				throw new NetSealRuntimeException(ErrCode.KEYWORD_NOT_EXIST_IN_PDF, "keywords not exits in pdf");
			}

			return pdfStampByCoordinate(pdfData, photoData, pageNum, llx, lly, biznum, key, keyData, keyCertData, sealData, rootHt, signKeyList);
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("pdf stamp by text error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("pdf stamp by text error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (stamper != null)
					stamper.close();
			} catch (Exception e) {
			}

			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 验签章
	 * 
	 * @param pdfPath
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyPdfStamp(byte[] pdfData, Hashtable<String, X509Certificate> rootHt, List<Key> signKeyList, boolean checkSealDate, boolean checkCertDate) {
		PdfReader reader = null;

		try {
			reader = new PdfReader(pdfData);
			AcroFields af = reader.getAcroFields();
			ArrayList names = af.getSignatureNames();

			boolean result = false;
			for (int i = 0, size = names.size(); i < size; i++) {
				String signName = (String) names.get(i);

				PdfPKCS7 pdfp7 = null;
				try {
					pdfp7 = af.verifySignature(signName, ExtendedConfig.getVerifyProvider(), ConfigUtil.getInstance().getGmOid().getBytes());
				} catch (Exception e) {
					throw new NetSealRuntimeException(signName + " verify signature error, " + e.getMessage());
				}

				try {
					result = pdfp7.verify(rootHt, signKeyList, checkSealDate, checkCertDate);
				} catch (Exception e) {
					throw new NetSealRuntimeException(signName + " pkcs7 verify error, " + e.getMessage());
				}

				if (!result)
					return result;
			}
			return result;

		} catch (Exception e) {
			LoggerUtil.errorlog("verify stamp error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_VERIFY_ERROR, "verify pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取证书数据
	 * 
	 * @param pdfData
	 * @return
	 */
	public static List<byte[]> getCertListFromPdf(byte[] pdfData) {
		List<byte[]> list = new ArrayList<byte[]>();
		PdfReader reader = null;
		try {
			reader = new PdfReader(pdfData);
			AcroFields af = reader.getAcroFields();
			ArrayList names = af.getSignatureNames();

			boolean result = false;
			for (int i = 0, size = names.size(); i < size; i++) {
				String signName = (String) names.get(i);

				PdfPKCS7 pdfp7 = null;
				try {
					pdfp7 = af.verifySignature(signName, ExtendedConfig.getVerifyProvider());
				} catch (Exception e) {
					throw new NetSealRuntimeException(signName + " verify signature error, " + e.getMessage());
				}

				// 获取证书
				list.add(pdfp7.getSigningCertificate().getEncoded());
			}

			return list;
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_VERIFY_ERROR, "verify pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 0: text完全包含keyword 1: keyword 包含text 2: text后面部分 包含keyword的前面部分 -1： text keyword 不相同
	 * 
	 * @param keyword
	 * @param text
	 * @return
	 */
	private static int match(String keyword, String text) {
		if (text.contains(keyword))
			return 0;
		else if (keyword.contains(text))
			return 1;
		else if (isPartContains(keyword, text)) {
			return 2;
		} else
			return -1;
	}

	/**
	 * text 包含 keyword的前一部分
	 * 
	 * @param keyword
	 * @param text
	 * @return
	 */
	private static boolean isPartContains(String keyword, String text) {
		char[] texts = text.toCharArray();
		char[] keywords = keyword.toCharArray();

		int i = 0;
		boolean result = false;
		for (int j = 0; j < texts.length; j++) {
			if (texts[j] == keywords[i]) {
				result = true;
				i++;
				if (i == keywords.length)
					return result;
			} else {
				if (result)
					return false;
			}
		}

		return result;
	}

	/**
	 * 切割图片
	 * 
	 * @param imgPath
	 *            原始图片路径
	 * @param n
	 *            切割份数
	 * @return itextPdf的Image[]
	 * @throws IOException
	 * @throws BadElementException
	 */
	private static Image[] subImages(byte[] photoData, int n) throws Exception {
		Image[] nImage = new Image[n];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(photoData));
		int h = img.getHeight();
		int w = img.getWidth();

		int sw = w / n;
		for (int i = 0; i < n; i++) {
			BufferedImage subImg;
			if (i == n - 1) {// 最后剩余部分
				subImg = img.getSubimage(i * sw, 0, w - i * sw, h);
			} else {// 前n-1块均匀切
				subImg = img.getSubimage(i * sw, 0, sw, h);
			}

			ImageIO.write(subImg, "png", out);
			nImage[i] = Image.getInstance(out.toByteArray());
			out.flush();
			out.reset();
		}
		return nImage;
	}

	/**
	 * 盖骑缝章
	 * 
	 * @param pdfData
	 * @param photoData
	 * @param biznum
	 * @param key
	 * @param keyData
	 * @param keyCertData
	 * @param sealData
	 * @param rootHt
	 * @param signKeyList
	 * @param align
	 * @return
	 */
	public static byte[] pdfStampByQfz(byte[] pdfData, byte[] photoData, String biznum, Key key, byte[] keyData, byte[] keyCertData, byte[] sealData, Hashtable<String, X509Certificate> rootHt,
			List<Key> signKeyList, int align) {
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			// 加载密钥
			PrivateKey priKey = KeyStoreUtil.loadKey(key.getKeyPwdPlain(), key.getKeyMode(), keyData);

			// 构造证书对象
			X509Certificate cert = CertUtil.parseCert(keyCertData).getX509Cert();
			Certificate[] certChain = new Certificate[] { cert };

			reader = new PdfReader(pdfData);
			stamper = PdfStamper.createSignature(reader, new ByteArrayOutputStream(), '\0', null, true);

			Document document = new Document(reader.getPageSize(1));
			float width = document.getPageSize().getWidth();
			float height = document.getPageSize().getHeight();

			int totalPageNum = reader.getNumberOfPages();
			if (totalPageNum < 2 || totalPageNum > 32)
				throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "PDF pages are not between 2 ~ 32");

			Image[] nImage = subImages(photoData, totalPageNum);// 生成骑缝章切割图片

			float x = 0, y = 0;
			float widthSplit = 0, heightSplit = 0;
			for (int n = 1; n <= totalPageNum; n++) {
				PdfContentByte over = stamper.getOverContent(n);// 设置在第几页打印印章
				Image img = nImage[n - 1];// 选择图片

				// 单位 磅 1英寸=72磅
				float imgW = ((int) img.getWidth() / Constants.GEN_SEAL_DPI * 72f);
				float imgH = ((int) img.getHeight() / Constants.GEN_SEAL_DPI * 72f);
				img.scaleAbsolute(imgW, imgH);// 控制图片大小

				if (Constants.QFZ_ALIGN_LEFT == align)
					x = 0;
				else if (Constants.QFZ_ALIGN_RIGHT == align)
					x = width - imgW;
				else
					throw new Exception("align value is invalid, value is " + align);

				y = height / 2 - imgH / 2;
				widthSplit = imgW;
				heightSplit = imgH;

				img.setAbsolutePosition(x, y);// 控制图片位置
				over.addImage(img);
			}

			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setSubFilter(new PdfName("adbe.pkcs7.detached"));
			sap.setPdfDigestAlg("SHA1");
			sap.setProvider("INFOSEC");
			sap.setCrypto(priKey, certChain, null, PdfSignatureAppearance.WINCER_SIGNED);
			sap.setVisibleSignature(new Rectangle(x, y, x + widthSplit, y + heightSplit), totalPageNum, StringUtil.isBlank(biznum) ? "NetSeal" + DateUtil.getCurrentTime() : biznum);
			sap.setLayer2Text(" ");
			sap.setAcro6Layers(true);

			// 根据证书得到HASH算法
			X509CertEnvelope certVO = CertUtil.parseCert(keyCertData);
			String digestAlg = OidUtil.getHashAlg(certVO.getSigAlgOID());
			sap.setPdfDigestAlg(digestAlg);
			
			// 设置时间戳服务器
			ConfigUtil config = ConfigUtil.getInstance();
			HashMap<String, String> hmRsa = new HashMap<String, String>();
			hmRsa.put("url", config.getTsaRsaUrl()); // 10.20.84.22 192.168.0.205
			hmRsa.put("username", config.getTsaRsaUsername());
			hmRsa.put("userpwd", config.getTsaRsaUserpwd());
			hmRsa.put("policy", config.getTsaRsaPolicy());
			hmRsa.put("usetsa", String.valueOf(config.getTsaRsaUsetsa()));
			sap.setTsaparas(hmRsa);

			if (Constants.SM3.equalsIgnoreCase(digestAlg)) {
				HashMap<String, String> hmSM2 = new HashMap<String, String>();
				hmSM2.put("url", config.getTsaSM2Url()); // 10.20.84.22 192.168.0.205
				hmSM2.put("username", config.getTsaSM2Username());
				hmSM2.put("userpwd", config.getTsaSM2Userpwd());
				hmSM2.put("policy", config.getTsaSM2Policy());
				hmSM2.put("usetsa", String.valueOf(config.getTsaSM2Usetsa()));
				sap.setTsaparas(hmSM2);
				
				sap.setId(ConfigUtil.getInstance().getGmOid().getBytes());
				sap.setSubFilter(new PdfName("adbe.x509.seal"));
				sap.setPdfDigestAlg("SM3");
				// 计算签章数据
				byte[] stampData = GenStampUtil.genStampData(pdfData, sealData, key, keyData, keyCertData, rootHt, signKeyList, sap.getId());
				sap.setStampData(stampData);
			}

			byte[] signedResult = stamper.indictClose();
			return signedResult;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("pdf stamp by coordinate error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("pdf stamp by coordinate error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "pdf stamp error, " + e.getMessage());
		} finally {
			try {
				if (stamper != null)
					stamper.close();
			} catch (Exception e) {
			}

			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		LoggerConfig.init();
		String sm2KeyPath = "F:/temp/pdf/key/sm2/netseal.pri";
		String sm2CertPath = "F:/temp/pdf/key/sm2/netseal.cer";
		String sm2KeyPwd = "68683556";

		String rsaKeyPath = "F:/temp/pdf/key/pfx/netseal.pfx";
		String rsaCertPath = "F:/temp/pdf/key/pfx/netseal.cer";
		String rsaKeyPwd = "11111111";

		String keyPath = sm2KeyPath;
		String certPath = sm2CertPath;
		String keyPwd = sm2KeyPwd;

		Hashtable<String, X509Certificate> rootHt = new Hashtable<String, X509Certificate>();
		File f = new File("f:/temp/ca");
		File[] fs = f.listFiles();
		X509Certificate root = null;
		for (int i = 0; i < fs.length; i++) {
			root = CertUtil.parseCert(FileUtil.getFile(fs[i].getAbsolutePath())).getX509Cert();
			rootHt.put(root.getSubjectDN().getName(), root);
		}

		Key key = new Key();
		key.setKeyPwd(StringUtil.base64Encode(keyPwd));
		key.setKeyMode(keyPath.substring(keyPath.indexOf(".")));
		key.setHsmId(0);

		Seal seal = new Seal();
		seal.setId(1L);
		seal.setName("testSeal");
		seal.setType(1);
		long time = System.currentTimeMillis();
		seal.setGenerateTime(time);
		seal.setNotBefor(time);
		seal.setNotAfter(time + 24 * 3600 * 1000L);

		byte[] ofdData = FileUtil.getFile("f:/temp/pdf/1.pdf");
		byte[] keyData = FileUtil.getFile(keyPath);
		byte[] keyCertData = FileUtil.getFile(certPath);
		byte[] photoData = FileUtil.getFile("f:/temp/seal.png");
		byte[] sealData = FileUtil.getFile("f:/temp/seal/rsa.seal");
		byte[] stampData = pdfStampByCoordinate(ofdData, photoData, 1, 0, 0, "", key, keyData, keyCertData, sealData, rootHt, null);
		FileUtil.storeFile("f:/temp/pdf/rsa_seal_stamp.pdf", stampData);

		// ofdStampByQfz(pdfData, null, key, keyData, certData, sealData, rootHt, null, Constants.QFZ_ALIGN_LEFT);

		boolean result = verifyPdfStamp(stampData, rootHt, null, true, true);
		System.out.println(result);
	}

}
