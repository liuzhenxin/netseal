package cn.com.infosec.netseal.common.util.photo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import cn.com.infosec.netseal.common.entity.vo.stamp.StampVO;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

/**
 * 图章绘制 圆形
 * 
 */
public class CircleUtil {

	private String format = "png";
	private String company;// 图章所属单位
	private String name;// 图章名称
	private int width = 150;// 图章宽度
	private int height = 150;// 图章高度
	private String fontType = "宋体";// 图章字体
	/**
	 * 图章名称距中心点偏移量,按照y轴方向
	 */
	private int nameOffset = 50;
	/**
	 * 图章中心标志(默认为五角星)外接圆半径
	 */
	private float radius = 25;
	/**
	 * 图章所属单位的起始角度,以6点钟方向为中心,向两个方向平均扩展
	 */
	private float companyAngle = 120;
	/**
	 * 图章名称颜色
	 */
	private Color nameColor = Color.RED;

	/**
	 * 图章所属单位颜色
	 */
	private Color companyColor = Color.RED;
	private int nameFontSize = 14;
	/**
	 * 图章名称字体信息
	 */
	private Font nameFont;
	private int companyFontSize = 24;
	/**
	 * 图章所属单位字体信息
	 */
	private Font companyFont;
	/**
	 * 单位字体的宽度缩放比率(百分比).此参数可以使字体看起来瘦长
	 */
	private float companyScale = 0.7F;
	/**
	 * 边框线宽
	 */
	private float borderWidth = 4F;
	/**
	 * 边框颜色
	 */
	private Color borderColor = Color.RED;
	/**
	 * 图章标记(默认为五角星)线宽
	 */
	private float signBorderWidth = 3F;
	/**
	 * 图章标记颜色
	 */
	private Color signBorderColor = Color.RED;
	/**
	 * 图章标记填充颜色
	 */
	private Color signFillColor = Color.RED;

	public CircleUtil(StampVO stampVO) {

		if (stampVO.getWidth() != null) {
			width = stampVO.getWidth();
			height = width;
			radius = width / 6;
			nameOffset = width / 3;
		}
		if (stampVO.getCompanyFontSize() != null)
			companyFontSize = stampVO.getCompanyFontSize();
		if (stampVO.getNameFontSize() != null)
			nameFontSize = stampVO.getNameFontSize();

		if (StringUtil.isNotBlank(stampVO.getFontType()))
			fontType = stampVO.getFontType();

		companyFont = new Font(fontType, Font.BOLD, companyFontSize);
		nameFont = new Font(fontType, Font.BOLD, nameFontSize);

		String tmpCompany = stampVO.getCompany();
		String tmpNname = stampVO.getName();
		init(tmpNname, tmpCompany);

	}

	private void init(String name, String company) {
		if (company == null)
			this.company = "";

		if (name == null)
			this.name = "";

		this.name = name;
		this.company = company;
	}

	public byte[] genStampData(String name, String company) {
		init(name, company);
		return genStampData();
	}

	public byte[] genStampData() {
		int fix = 5;// 宽高修正,如果宽高就为图片宽高,可能边框线被切割
		BufferedImage bi = new BufferedImage(width + fix * 2, height + fix * 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width + fix * 2, height + fix * 2);
		g2d.translate(fix, fix);
		this.draw(g2d);

		bi = convert(bi);// 去白色背景

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
		// 填充五角星
		Polygon polygon = getPentaclePoints(radius);
		if (signFillColor != null) {
			g2d.setColor(signFillColor);
			g2d.fill(polygon);
		}

		// 绘制五角星边框
		g2d.setStroke(new BasicStroke(signBorderWidth));
		g2d.setColor(signBorderColor);
		g2d.draw(polygon);

		// 绘制图章边框
		g2d.setColor(borderColor);
		g2d.setStroke(new BasicStroke(borderWidth));
		g2d.drawOval(-width / 2, -height / 2, width, height);
		g2d.setStroke(stroke);

		// 绘制图章名称
		g2d.setFont(nameFont);
		g2d.setColor(nameColor);
		FontMetrics fm = g2d.getFontMetrics();
		int w = fm.stringWidth(name);// 名称宽度
		int h = fm.getHeight();// 名称高度
		int y = fm.getAscent() - h / 2;// 求得中心线经过字体的高度的一半时的字体的起绘点
		g2d.drawString(name, -w / 2, y + nameOffset);

		// 绘制图章单位
		g2d.setFont(companyFont);
		g2d.setColor(companyColor);
		fm = g2d.getFontMetrics();
		h = fm.getHeight();// 字高度

		int count = company.length();// 字数
		int r = width / 2;// 半径,就假设此图章是个矩形,方便计算

		int span = count - 1;
		if (count == 1){
			span = 1;
			companyAngle = 360;
		}
		float angle = (360 - companyAngle) / span;// 字间角度
		float start = 90 + companyAngle / 2;// 以x轴正向为0,顺时针旋转
		double vr = Math.toRadians(90);// 垂直旋转弧度
		char[] chars = company.toCharArray();
		for (int i = 0; i < count; i++) {
			char c = chars[i];// 需要绘制的字符
			int cw = fm.charWidth(c);// 此字符宽度
			float a = start + angle * i;// 现在角度

			double radians = Math.toRadians(a);
			g2d.rotate(radians);// 旋转坐标系,让要绘制的字符处于x正轴
			float x = r - h;// 绘制字符的x坐标为半径减去字高度
			// g2d.drawLine(0, 0, (int) x, 0);// debug
			g2d.translate(x, 0);// 移动到此位置,此时字和x轴垂直
			g2d.rotate(vr);// 旋转90度,让字平行于x轴
			g2d.scale(companyScale, 1);// 缩放字体宽度
			g2d.drawString(String.valueOf(c), -cw / 2, 0);// 此点为字的中心点
			// 将所有设置还原,等待绘制下一个
			g2d.scale(1 / companyScale, 1);
			g2d.rotate(-vr);
			g2d.translate(-x, 0);
			g2d.rotate(-radians);
		}
	}

	/**
	 * 获取具有指定半径外接圆的五角星顶点
	 * 
	 * @param radius
	 *            圆半径
	 */
	private Polygon getPentaclePoints(float radius) {
		if (radius <= 0)
			return null;
		float lradius = radius * 0.381966f;// 根据radius求内圆半径
		double halfpi = Math.PI / 180f;
		Point[] points = new Point[10];
		for (int i = 0; i < points.length; i++) {
			if (i % 2 == 1)
				points[i] = new Point((int) (Math.sin(halfpi * 36 * i) * radius),
						(int) (Math.cos(halfpi * 36 * i) * radius));
			else
				points[i] = new Point((int) (Math.sin(halfpi * 36 * i) * lradius),
						(int) (Math.cos(halfpi * 36 * i) * lradius));
		}
		Polygon polygon = new Polygon();
		for (Point p : points) {
			polygon.addPoint(p.x, p.y);
		}
		return polygon;
	}

	/**
	 * 去白色背景
	 * 
	 * @param image
	 * @return
	 */
	public BufferedImage convert(BufferedImage image) {
		ImageIcon imageIcon = new ImageIcon(image);
		BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
		g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
		int alpha = 0;
		for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
			for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
				int rgb = bufferedImage.getRGB(j2, j1);
				if (colorInRange(rgb))
					alpha = 0;
				else
					alpha = 255;
				rgb = (alpha << 24) | (rgb & 0x00ffffff);
				bufferedImage.setRGB(j2, j1, rgb);
			}
		}
		g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
		return bufferedImage;
	}

	public boolean colorInRange(int color) {
		int red = (color & 0xff0000) >> 16;
		int green = (color & 0x00ff00) >> 8;
		int blue = (color & 0x0000ff);
		if (red >= 210 && green >= 210 && blue >= 210)
			return true;
		return false;
	}

	public static void main(String[] args) throws Exception {
		StampVO stampVO = new StampVO();
		stampVO.setCompany("北京世纪信安科技有限公司");
		stampVO.setName("账务专用章");
		CircleUtil seal = new CircleUtil(stampVO);
		byte[] data = seal.genStampData();

		FileUtil.storeFile("e://seal.png", data);

		/*
		 * byte[] photoData = FileUtil.getFile("f:/temp/seal.png"); photoData =
		 * StampUtil.convertBackgroundColor(photoData);
		 * FileUtil.storeFile("f:/temp/seal1.png", photoData);
		 */
		// seal.zoom(150, 75);
		System.out.println("complete");
	}
}