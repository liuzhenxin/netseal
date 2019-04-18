package cn.com.infosec.netseal.common.entity.vo;

public class PdfTemplateVO extends BaseVO{
	private String  name;
	private String  templatePath;
	private Long  pdfTemplateDataId;
	
	
	
	public String getTemplatePath() {
		return templatePath;
	}
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getPdfTemplateDataId() {
		return pdfTemplateDataId;
	}
	public void setPdfTemplateDataId(Long pdfTemplateDataId) {
		this.pdfTemplateDataId = pdfTemplateDataId;
	}

}
