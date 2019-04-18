package cn.com.infosec.netseal.common.util.photo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import cn.com.infosec.netseal.common.entity.vo.stamp.StampVO;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

/**
 * 图章绘制 正方形
 * 
 */
public class SquareUtil {

	private String format = "png";
	private String name;// 图章名称
	private int width = 100;// 图章宽度
	private int height = 100;// 图章高度
	private String fontType = "宋体";// 图章字体
	/**
	 * 图章名称颜色
	 */
	private Color nameColor = Color.RED;
	/**
	 * 图章名称字体信息
	 */
	private int nameFontSize = 24;
	/**
	 * 图章名称字体信息
	 */
	private Font nameFont;
	/**
	 * 边框线宽
	 */
	private float borderWidth = 4F;
	/**
	 * 边框颜色
	 */
	private Color borderColor = Color.RED;

	public SquareUtil(StampVO stampVO) {

		if (stampVO.getWidth() != null) {
			width = stampVO.getWidth();
			height = width;
		}

		if (stampVO.getNameFontSize() != null)
			nameFontSize = stampVO.getNameFontSize();

		if (StringUtil.isNotBlank(stampVO.getFontType()))
			fontType = stampVO.getFontType();

		nameFont = new Font(fontType, Font.BOLD, nameFontSize);

		String tmpNname = stampVO.getName();
		init(tmpNname);

	}

	private void init(String name) {

		if (name == null)
			this.name = "";

		this.name = name;
	}

	public byte[] genStampData(String name) {
		init(name);
		return genStampData();
	}

	public byte[] genStampData() {

		int fix = 5;// 宽高修正,如果宽高就为图片宽高,可能边框线被切割
		BufferedImage bi = new BufferedImage(width + fix * 2, height + fix * 2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.translate(fix, fix);
		this.draw(g2d);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, format, baos);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.GEN_SEAL_PHOTO_ERROR, "gen seal photo error, " + e.getMessage());
		}
		return baos.toByteArray();
	}

	public void draw(Graphics2D g2d) {
		// 把绘制起点挪到圆中心点
		g2d.translate(width / 2, height / 2);

		Stroke stroke = g2d.getStroke();// 旧的线性

		// 绘制图章边框
		g2d.setColor(borderColor);
		g2d.setStroke(new BasicStroke(borderWidth));
		g2d.drawRect(-width / 2, -height / 2, width, height);
		g2d.setStroke(stroke);

		// 绘制图章名称
		FontMetrics fm = g2d.getFontMetrics();

		// 绘制图章单位
		g2d.setFont(nameFont);
		g2d.setColor(nameColor);
		fm = g2d.getFontMetrics();
		int w = fm.stringWidth(name);// 名称宽度
		int h = fm.getHeight();// 字高度
		int y = fm.getAscent() - h / 2 - 5;

		width = (int) (width - borderWidth * 2);
		int oneW = w / name.length();// 每个字宽度
		int textNum = width / oneW;// 每行字数
		int line = w / width;// 行数
		if (w % width > 0)
			line += 1;
		// System.out.println("fm.getAscent()="+fm.getAscent()+" w="+w+" width="+width+"
		// oneW="+oneW+" textNum="+textNum+" line="+line+" y="+y);
		for (int i = 0; i < line; i++) {
			String subName = "";
			if (((i + 1) * textNum) >= name.length())
				subName = name.substring(i * textNum);
			else
				subName = name.substring(i * textNum, (i + 1) * textNum);
			int check = 0;
			if (i > 0)
				check = 10;
			g2d.drawString(subName, -(oneW * subName.length()) / 2,
					-width / 2 + (i + 1) * width / (line + 1) + y + check);
		}
	}

	public static void main(String[] args) {
		StampVO stampVO = new StampVO();
		stampVO.setWidth(210);
		stampVO.setNameFontSize(55);
		stampVO.setName("测试专用章电子签章");// 章电子签章系统测试
		SquareUtil seal = new SquareUtil(stampVO);

		byte[] data = seal.genStampData();

		FileUtil.storeFile("e://seal.png", data);

	}
}