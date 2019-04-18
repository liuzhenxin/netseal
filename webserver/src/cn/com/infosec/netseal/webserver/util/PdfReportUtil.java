package cn.com.infosec.netseal.webserver.util;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cn.com.infosec.netseal.common.entity.vo.ScalarVO;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

public class PdfReportUtil {

	// 添加中文字体
	private static BaseFont bfChinese;

	// 设置字体样式
	private static Font firsetTitleFont; // 一级标题
	private static Font secondTitleFont; // 二级标题
	private static Font textFont; // 正常
	private static Font redTextFont; // 正常,红色
	private static Font boldFont; // 加粗
	private static Font redBoldFont; // 加粗,红色
	// private static Font underlineFont; // 下划线斜体
	// private static Image img; // 图片

	public static Document getInstance(String filePath,String reportNumber) throws Exception {
		bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		firsetTitleFont = new Font(bfChinese, 22, Font.BOLD); // 一级标题
		secondTitleFont = new Font(bfChinese, 15, Font.BOLD); // 二级标题

		textFont = new Font(bfChinese, 11, Font.NORMAL); // 正常
		redTextFont = new Font(bfChinese, 11, Font.NORMAL, BaseColor.RED); // 正常,红色

		boldFont = new Font(bfChinese, 11, Font.BOLD); // 加粗
		redBoldFont = new Font(bfChinese, 11, Font.BOLD, BaseColor.RED); // 加粗,红色

		// underlineFont = new Font(bfChinese, 11, Font.UNDERLINE); // 下划线斜体
		// img = Image.getInstance(Constants.PHOTO_PATH+"list.png"); // 手指图片

		Document doc = new Document();
		PdfWriter.getInstance(doc, new FileOutputStream(filePath));

		doc.addTitle("产品巡检");
		doc.addAuthor("电子签章系统");
		doc.addSubject("产品巡检");
		doc.addKeywords("产品巡检");
		doc.addCreator("电子签章系统");
		doc.open();

		// 段落
		Paragraph p1 = new Paragraph();
		// 短语
		Phrase ph1 = new Phrase();
		// 块
		Chunk c1 = new Chunk("报告编号：", boldFont);
		Chunk c2 = new Chunk(reportNumber, textFont);
		ph1.add(c1);
		ph1.add(c2);
		p1.add(ph1);
		doc.add(p1);

		p1 = new Paragraph("产品巡检", firsetTitleFont);
		p1.setLeading(100);
		p1.setAlignment(Element.ALIGN_CENTER);
		doc.add(p1);

		p1 = new Paragraph("(电子签章)", secondTitleFont);
		p1.setLeading(20);
		p1.setAlignment(Element.ALIGN_CENTER);
		doc.add(p1);

		p1 = new Paragraph("北京信安世纪科技股份有限公司", secondTitleFont);
		p1.setLeading(315);
		p1.setAlignment(Element.ALIGN_CENTER);
		doc.add(p1);
		String genDate = DateUtil.getDateTime(new Date());
		p1 = new Paragraph(genDate, secondTitleFont);
		p1.setLeading(30);
		p1.setAlignment(Element.ALIGN_CENTER);
		doc.add(p1);
		doc.newPage();

		p1 = new Paragraph("目录", secondTitleFont);
		p1.setLeading(100);
		p1.setAlignment(Element.ALIGN_CENTER);
		doc.add(p1);

		addCatalogContent(doc, "1   系统概况", "3");
		addCatalogContent(doc, "2   产品信息", "3");
		addCatalogContent(doc, "3   运行状况", "3");
		addCatalogContent(doc, "4    巡检结论", "4");
		doc.newPage();

		return doc;
	}

	private static void addCatalogContent(Document doc, String content, String pageNum) throws DocumentException {
		while (content.length() < 170)
			content += ".";

		Paragraph p1 = new Paragraph(content + pageNum, boldFont);
		p1.setLeading(30);
		p1.setAlignment(Element.ALIGN_LEFT);
		doc.add(p1);
	}

	public static void close(Document doc) {
		doc.close();
	}

	public static boolean addNewPage(Document doc) {
		return doc.newPage();
	}

	public static void addTitle(Document doc, String title) throws Exception {
		Paragraph p1 = new Paragraph(title, secondTitleFont);
		p1.setLeading(30);
		p1.setAlignment(Element.ALIGN_LEFT);
		doc.add(p1);
	}

	public static void addContent(Document doc, String content) throws Exception {
		Paragraph p1 = new Paragraph();
		p1.setLeading(30);
		p1.setFirstLineIndent(23);
		p1.setAlignment(Element.ALIGN_LEFT);
		Phrase ph1 = new Phrase();
		Chunk c1 = new Chunk(content, textFont);
		ph1.add(c1);
		p1.add(ph1);
		doc.add(p1);
	}

	public static void addTable(Document doc, String tableTitle, int col, List<String> titleList, List<List<ScalarVO>> contentList) throws Exception {
		if (StringUtil.isNotBlank(tableTitle)) {
			Paragraph p1 = new Paragraph();
			p1.setFirstLineIndent(23);
			p1.setSpacingBefore(10);
			p1.setSpacingAfter(10);
			Phrase ph1 = new Phrase();
			// Chunk c1 = new Chunk(img, 0, 0);
			Chunk c2 = new Chunk(tableTitle, boldFont);
			// ph1.add(c1);
			ph1.add(c2);
			p1.add(ph1);
			doc.add(p1);
		}
		PdfPTable table = new PdfPTable(col);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell cell = new PdfPCell();
		ScalarVO scalarVO = new ScalarVO();
		for (int i = 0; i < titleList.size(); i++) {
			cell = new PdfPCell(new Phrase(titleList.get(i), boldFont));
			cell.setMinimumHeight(25); // 设置单元&#26684;高度
			cell.setUseAscender(true); // 设置可以居中
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); // 设置水平居中
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // 设置垂直居中
			cell.setBackgroundColor(BaseColor.GRAY);

			table.addCell(cell);
		}

		for (int i = 0; i < contentList.size(); i++) {
			List<ScalarVO> innerList = contentList.get(i);
			for (int j = 0; j < innerList.size(); j++) {
				scalarVO = innerList.get(j);
				if (scalarVO.isRed() && scalarVO.isBold())
					cell = new PdfPCell(new Phrase(scalarVO.getValue(), redBoldFont));
				else if (scalarVO.isRed())
					cell = new PdfPCell(new Phrase(scalarVO.getValue(), redTextFont));
				else if (scalarVO.isBold())
					cell = new PdfPCell(new Phrase(scalarVO.getValue(), boldFont));
				else
					cell = new PdfPCell(new Phrase(scalarVO.getValue(), textFont));

				cell.setMinimumHeight(25); // 设置单元&#26684;高度
				cell.setUseAscender(true); // 设置可以居中
				cell.setHorizontalAlignment(Element.ALIGN_CENTER); // 设置水平居中
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // 设置垂直居中

				if (scalarVO.isBgGray())
					cell.setBackgroundColor(BaseColor.GRAY);
				table.addCell(cell);
			}
		}

		doc.add(table);
	}

	public static String addScalarVO(List<ScalarVO> scalarVOList, String value, boolean isRed, boolean isBgGray, boolean isBold) {
		ScalarVO scalarVO = new ScalarVO();
		scalarVO.setValue(value);
		scalarVO.setRed(isRed);
		scalarVO.setBgGray(isBgGray);
		scalarVO.setBold(isBold);
		scalarVOList.add(scalarVO);
		StringBuffer sb = new StringBuffer("<td>");
		if (isRed)
			sb.append("<font color=\"red\">").append(value).append("</font>");
		else
			sb.append(value);
		sb.append("</td>");
		return sb.toString();
	}

}
