package cn.com.infosec.netseal.common.entity.vo.stamp;

public class StampVO {
	private Integer stampStyle;// 印章样式 1圆形 2椭圆形 3方形 4长方形 
	private String company;// 印章所属单位
	private String name;//印章名称
	private Integer width;//印章宽度
	private Integer height;//印章高度
	private String fontType;
	
	private Integer nameFontSize;//印章名称字体大小
	private Integer companyFontSize;//印章单位字体大小
	
	
	public Integer getStampStyle() {
		return stampStyle;
	}
	public void setStampStyle(Integer stampStyle) {
		this.stampStyle = stampStyle;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getFontType() {
		return fontType;
	}
	public void setFontType(String fontType) {
		this.fontType = fontType;
	}
	public Integer getNameFontSize() {
		return nameFontSize;
	}
	public void setNameFontSize(Integer nameFontSize) {
		this.nameFontSize = nameFontSize;
	}
	public Integer getCompanyFontSize() {
		return companyFontSize;
	}
	public void setCompanyFontSize(Integer companyFontSize) {
		this.companyFontSize = companyFontSize;
	}
	
	
}
