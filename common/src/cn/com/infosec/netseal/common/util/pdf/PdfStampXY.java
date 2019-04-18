package cn.com.infosec.netseal.common.util.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.awt.geom.Rectangle2D.Float;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import cn.com.infosec.netseal.common.util.HexUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class PdfStampXY {

	/**
	 * 获取坐标
	 * 
	 * @param pdfReader
	 * @param pageNum
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	public static List getCoordinate(final byte[] pdfData, final int pageNum, final String keyword) throws Exception {
		final List<String> textTmp = new ArrayList<String>();
		final List<Float> coordiTmp = new ArrayList<Float>();
		final List<Float> result = new ArrayList<Float>();
		if (StringUtil.isBlank(keyword))
			return result;

		PdfReader pdfReader = null;
		try {
			pdfReader = new PdfReader(pdfData);
			PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
			pdfReaderContentParser.processContent(pageNum, new RenderListener() {
				public void renderText(TextRenderInfo textRenderInfo) {
					String text = textRenderInfo.getText();
					if (textTmp.size() != 0)
						text = textTmp.get(0) + text;

					if (text != null) {
						// text 完全包含 keyword
						if (match(keyword, text) == 0) {
							textTmp.clear();
							result.clear();

							Float boundingRectange = textRenderInfo.getBaseline().getBoundingRectange();
							// 总宽度 (所需文字在同一数据块内)
							float totalW = boundingRectange.width;

							// 文字单独在数据块内
							if (coordiTmp.size() != 0)
								for (int i = 0; i < coordiTmp.size(); i++)
									totalW += coordiTmp.get(i).width;

							// 计算图章右移距离
							float perW = totalW / HexUtil.byte2Hex(StringUtil.getBytes(text)).length(); // 每个文字的宽度
							int index = HexUtil.byte2Hex(StringUtil.getBytes(text)).indexOf(HexUtil.byte2Hex(StringUtil.getBytes(keyword)));
							int keywordLen = HexUtil.byte2Hex(StringUtil.getBytes(keyword)).length();
							index += keywordLen / 2;

							if (coordiTmp.size() == 0)
								boundingRectange.x = boundingRectange.x + index * perW;
							else
								boundingRectange.x = coordiTmp.get(0).x + index * perW;

							result.add(boundingRectange);
							coordiTmp.clear();
							// keyword 包含 text 或 text 包含 keyword的前一部分
						} else if (match(keyword, text) == 1 || match(keyword, text) == 2) {
							textTmp.clear();
							textTmp.add(text);
							Float rectange = textRenderInfo.getBaseline().getBoundingRectange();
							coordiTmp.add(rectange);
						} else {
							textTmp.clear();
							coordiTmp.clear();
						}
					}
				}

				public void renderImage(ImageRenderInfo arg0) {
				}

				public void endTextBlock() {
				}

				public void beginTextBlock() {
				}
			});
		} catch (Exception e) {
			throw e;
		} finally {
			if (pdfReader != null)
				pdfReader.close();
		}

		return result;
	}

	private List getKeyWords(String filePath, final String keyword) {
		final List<String> textTmp = new ArrayList<String>();
		final List<Float> coordiTmp = new ArrayList<Float>();
		final List<Float> result = new ArrayList<Float>();

		try {
			PdfReader pdfReader = new PdfReader(filePath);
			int pageNum = pdfReader.getNumberOfPages();

			PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
			pdfReaderContentParser.processContent(pageNum, new RenderListener() {
				public void renderText(TextRenderInfo textRenderInfo) {
					String text = textRenderInfo.getText();
					if (textTmp.size() != 0)
						text = textTmp.get(0) + text;

					if (text != null) {
						// text 完全包含 keyword
						if (match(keyword, text) == 0) {
							textTmp.clear();
							result.clear();

							Float boundingRectange = textRenderInfo.getBaseline().getBoundingRectange();
							// 总宽度 (所需文字在同一数据块内)
							float totalW = boundingRectange.width;

							// 文字单独在数据块内
							if (coordiTmp.size() != 0)
								for (int i = 0; i < coordiTmp.size(); i++)
									totalW += coordiTmp.get(i).width;

							// 计算图章右移距离
							float perW = totalW / HexUtil.byte2Hex(StringUtil.getBytes(text)).length(); // 每个文字的宽度
							int index = HexUtil.byte2Hex(StringUtil.getBytes(text)).indexOf(HexUtil.byte2Hex(StringUtil.getBytes(keyword)));
							int keywordLen = HexUtil.byte2Hex(StringUtil.getBytes(keyword)).length();
							index += keywordLen / 2;

							if (coordiTmp.size() == 0)
								boundingRectange.x = boundingRectange.x + index * perW;
							else
								boundingRectange.x = coordiTmp.get(0).x + index * perW;

							result.add(boundingRectange);
							coordiTmp.clear();
							// keyword 包含 text 或 text 包含 keyword的前一部分
						} else if (match(keyword, text) == 1 || match(keyword, text) == 2) {
							textTmp.clear();
							textTmp.add(text);
							Float rectange = textRenderInfo.getBaseline().getBoundingRectange();
							coordiTmp.add(rectange);
						} else {
							textTmp.clear();
							coordiTmp.clear();
						}
					}
				}

				@Override
				public void renderImage(ImageRenderInfo arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void endTextBlock() {
					// TODO Auto-generated method stub

				}

				@Override
				public void beginTextBlock() {
					// TODO Auto-generated method stub

				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
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

	public static void main(String[] args) {
		List list = new PdfStampXY().getKeyWords("f:/temp/check.pdf", "签");
		System.out.println(list);
	}

}