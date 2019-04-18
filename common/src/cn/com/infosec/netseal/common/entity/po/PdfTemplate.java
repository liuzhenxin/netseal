package cn.com.infosec.netseal.common.entity.po;

import java.io.ByteArrayOutputStream;

import cn.com.infosec.netseal.common.crypto.CryptoHandler;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;

/**
 * PDF模板
 */
public class PdfTemplate extends Base{
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
	
	
	public String calMac() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(calMac(getId()));
			baos.write(calMac(getName()));
			baos.write(calMac(getTemplatePath()));
			baos.write(calMac(getPdfTemplateDataId()));
			baos.write(calMac(getGenerateTime()));
			baos.write(calMac(getUpdateTime()));
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.CALC_MAC_ERROR, e.getMessage());
		}
		return CryptoHandler.hashEnc64(baos.toByteArray());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" id:");
		sb.append(getId());
		sb.append(" generateTime:");
		sb.append(getGenerateTime());
		sb.append(" updateTime:");
		sb.append(getUpdateTime());
		sb.append(" mac:");
		sb.append(getMac());
		sb.append(" name:");
		sb.append(getName());
		sb.append(" templatePath:");
		sb.append(getTemplatePath());
		sb.append(" pdfTemplateData:");
		sb.append(getPdfTemplateDataId());
		return sb.toString();
	}
	
	
}
