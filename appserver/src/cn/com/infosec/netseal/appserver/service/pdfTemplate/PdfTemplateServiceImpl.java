package cn.com.infosec.netseal.appserver.service.pdfTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.pdfTemplate.PdfTemplateDaoImpl;
import cn.com.infosec.netseal.common.dao.pdfTemplateData.PdfTemplateDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.PdfTemplate;
import cn.com.infosec.netseal.common.entity.po.PdfTemplateData;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;

@Service
public class PdfTemplateServiceImpl extends BaseService{
	
	@Autowired
	private PdfTemplateDaoImpl pdfTemplateDao;
	@Autowired
	private PdfTemplateDataDaoImpl pdfTemplateDataDao;
	
	public List<PdfTemplate> getPdfTemplate(String name) {
		
		return pdfTemplateDao.getPdfTemplate(name);
	}
	
	/**
	 * 获取PDFTemplate数据
	 * 
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public byte[] getPdfTemplate(String pdfTemplatePath, long pdfTemplateDataId) {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(pdfTemplatePath)) {
			data = FileUtil.getFile(pdfTemplatePath);
		} else {
			PdfTemplateData pdfTemplateData = pdfTemplateDataDao.getPdfTemplateData(pdfTemplateDataId);
			if (pdfTemplateData == null)
				throw new NetSealRuntimeException(ErrCode.PDF_TEMPLATE_DATA_NOT_EXIST_IN_DB, "pdf template data is not exist in db, pdf template data ID is " + pdfTemplateDataId);

			// 校验证书数据信息
			isModify(pdfTemplateData, "the data of pdfTemplateData which id is " + pdfTemplateData.getId());

			data = pdfTemplateData.getData();
			FileUtil.storeFile(pdfTemplatePath, data);
		}
		return data;
	}

	/**
	 * 校验PDFTemplate数据完整性
	 * 
	 * @param pt
	 */
	public void isModify(PdfTemplate pt){
		isModify(pt, "the data of pdfTemplate which id is " + pt.getId() + " and name is " + pt.getName());
	}
	
}
