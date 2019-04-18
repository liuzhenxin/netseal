package cn.com.infosec.netseal.common.util.pdf;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.log.LoggerUtil;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;

public class PdfStampTemplate {

	public static List<String> getFieldName(byte[] pdfTemplateData) {
		PdfReader reader = null;
		List<String> list = new ArrayList<String>();
		try {
			reader = new PdfReader(pdfTemplateData);
			AcroFields fields = reader.getAcroFields();

			Map<String, Item> fieldMap = fields.getFields(); // pdf表单相关信息展示
			Set<String> set = fieldMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String fieldName = (String) it.next();
				if (fields.getFieldType(fieldName) == AcroFields.FIELD_TYPE_TEXT) {
					fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1, fieldName.lastIndexOf("["));
					list.add(fieldName);
				}
			}
			return list;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("create pdf by template error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("create pdf by template error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "create pdf by template error, " + e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	public static byte[] createPdfByTemplate(byte[] pdfTemplateData, Properties pro) {
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(pdfTemplateData);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			stamper = new PdfStamper(reader, bos);
			AcroFields fields = stamper.getAcroFields();

			// 使用中文字体 使用 AcroFields填充值的不需要在程序中设置字体，在模板文件中设置字体为中文字体 Adobe 宋体 std L
			fields.addSubstitutionFont(BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false));

			// 遍历给表单赋值
			for (Object o : pro.keySet()) {
				// 插入的数据都为字符类型
				fields.setField((String) o, pro.getProperty((String) o));
			}

			// 如果为false那么生成的PDF文件还能编辑，一定要设为true
			stamper.setFormFlattening(true);
			stamper.close();
			stamper = null;

			// 添加图片
			// String imgpath = "D:/n5.jpg";
			// int pageNo = s.getFieldPositions("img").get(0).page;
			// Rectangle signRect = s.getFieldPositions("img").get(0).position;
			// float x = signRect.getLeft();
			// float y = signRect.getBottom();
			// // 读图片
			// Image image = Image.getInstance(imgpath);
			// // 获取操作的页面
			// PdfContentByte under = ps.getOverContent(pageNo);
			// // 根据域的大小缩放图片
			// image.scaleToFit(signRect.getWidth(), signRect.getHeight());
			// // 添加图片
			// image.setAbsolutePosition(x, y);
			// under.addImage(image);

			return bos.toByteArray();
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("create pdf by template error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("create pdf by template error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "create pdf by template error, " + e.getMessage());
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

	public static List<Rectangle> getSignRectangle(byte[] pdfData) {
		PdfReader reader = null;
		List<Rectangle> retList = new ArrayList<Rectangle>();
		try {
			reader = new PdfReader(pdfData);
			AcroFields fields = reader.getAcroFields();
			ArrayList<String> list = fields.getSignatureNames();
			for (int i = 0; i < list.size(); i++) {
				String signName = list.get(i);
				List<FieldPosition> positionList = fields.getFieldPositions(signName);
				for (int j = 0; j < positionList.size(); j++)
					retList.add(positionList.get(j).position);
			}
			return retList;
		} catch (NetSealRuntimeException e) {
			LoggerUtil.errorlog("create pdf by template error", e);
			throw e;
		} catch (Exception e) {
			LoggerUtil.errorlog("create pdf by template error", e);
			throw new NetSealRuntimeException(ErrCode.PDF_STAMP_ERROR, "create pdf by template error, " + e.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		Properties pro = new Properties();
		pro.setProperty("id", "蓝");
		// pro.setProperty("qq", "252462807");
		// byte[] data = PdfStampTemplate.createPdfByTemplate(FileUtil.getFile("D:/download/edgeDownload/pdf template/PdfTemplate.pdf"), pro);
		// FileUtil.storeFile("D:/download/edgeDownload/pdf template/.pdf", data);

		byte[] data = PdfStampTemplate.createPdfByTemplate(FileUtil.getFile("f:/temp/v4/t.pdf"), pro);
		FileUtil.storeFile("f:/temp/v4/tt.pdf", data);

	}
}