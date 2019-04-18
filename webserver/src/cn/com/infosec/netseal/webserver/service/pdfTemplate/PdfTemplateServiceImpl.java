package cn.com.infosec.netseal.webserver.service.pdfTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.idDelete.IDDeleteDaoImpl;
import cn.com.infosec.netseal.common.dao.pdfTemplate.PdfTemplateDaoImpl;
import cn.com.infosec.netseal.common.dao.pdfTemplateData.PdfTemplateDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.PdfTemplate;
import cn.com.infosec.netseal.common.entity.po.PdfTemplateData;
import cn.com.infosec.netseal.common.entity.vo.PdfTemplateVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.Page;
@Service
public class PdfTemplateServiceImpl extends BaseService{
	
	@Autowired
	private PdfTemplateDaoImpl pdfTemplateDao;
	@Autowired
	private PdfTemplateDataDaoImpl pdfTemplateDataDao;
	@Autowired
	private IDDeleteDaoImpl idDeleteDao;
	



	/**
	 * pdf模板分页查询
	 * @param page
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	public Page<PdfTemplateVO> searchPdfTemplate (Page<PdfTemplateVO> page) throws Exception{
		
		int total = pdfTemplateDao.searchTotal();
		int start = page.getStart();
		int end = page.getEnd();
		File file;
		List<PdfTemplate> list = pdfTemplateDao.searchByPage(start, end);
		List<PdfTemplateVO> pdfTemplateVOList = new ArrayList<PdfTemplateVO>();
		for(PdfTemplate pdfs :list) {
			PdfTemplateVO pdfTemplateVO = new PdfTemplateVO();
			BeanUtils.copyProperties(pdfs, pdfTemplateVO);
			file = new File(pdfTemplateVO.getTemplatePath());
			if((file.exists() && file.isFile())) {
				pdfTemplateVO.setFileSize(file.length()/1024);
			}
			// 校验信息
			isModify(pdfs, pdfTemplateVO);
			
			pdfTemplateVOList.add(pdfTemplateVO);
			
		}
		page.setTotalNo(total);
		page.setResult(pdfTemplateVOList);
		return page;
	}
	
	/**
	 * pdf模板删除 
	 * @param name
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public void deletePdfTemplate(String id) throws Exception{
		if(id != null && !"".equals(id)) {
			String pdfTemplateId[] = id.split(Constants.SPLIT_2);
			for(int i = 0;i < pdfTemplateId.length; i++) {
				if(pdfTemplateId[i] != null) {
					PdfTemplate pdfTemplate = pdfTemplateDao.getPdfTemplate(Long.parseLong(pdfTemplateId[i]));
					int pdfId = pdfTemplateDao.deletePdfTemplate(pdfTemplate.getId());
					if (pdfId == 0)
						throw new WebDataException("操作失败");
					
					int pdfDataId = pdfTemplateDataDao.deletePdfTemplateData(pdfTemplate.getPdfTemplateDataId());
					if(pdfDataId ==0)
						throw new WebDataException("操作失败");
					
					// 增加删除记录
					idDeleteDao.insertIDDelete(pdfTemplate.getId(), Constants.TABLE_SEAL_PDF_TEMPLATE);
					idDeleteDao.insertIDDelete(pdfTemplate.getPdfTemplateDataId(), Constants.TABLE_SEAL_PDF_TEMPLATE_DATA);
					FileUtil.deleteFile(pdfTemplate.getTemplatePath());
				}
			}
		}else {
			throw new WebDataException("操作失败");
		}
	}
	
	/**
	 * 保存pdf数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void pdfTemplateDB(PdfTemplateVO pdfTemplateVO ) {
		byte[] pdfData = FileUtil.getFile(pdfTemplateVO.getTemplatePath());
		
		//保存pdfTemplateData
		PdfTemplateData  pdfTemplateData = new PdfTemplateData();
		pdfTemplateData.setData(pdfData);
		pdfTemplateData.setGenerateTime(DateUtil.getCurrentTime());
		pdfTemplateData.setUpdateTime(DateUtil.getCurrentTime());
		pdfTemplateDataDao.insertPdfTemplateData(pdfTemplateData);
		
		//保存pdfTemplate
		PdfTemplate  pdfTemplate = new PdfTemplate();
		pdfTemplate.setGenerateTime(DateUtil.getCurrentTime());
		pdfTemplate.setUpdateTime(DateUtil.getCurrentTime());
		pdfTemplate.setPdfTemplateDataId(pdfTemplateData.getId());
		pdfTemplate.setName(pdfTemplateVO.getName());
		pdfTemplate.setTemplatePath(pdfTemplateVO.getTemplatePath());
		pdfTemplateDao.insertPdfTemplate(pdfTemplate);
		
	}
	
	
	/**
	 * 查全部
	 * @throws Exception 
	 */
	public List<PdfTemplateVO> getPdfTemplates() throws Exception{
		List<PdfTemplate> pdfTemplateList = pdfTemplateDao.getPdfTemplates();
		List<PdfTemplateVO> pdfTemplateVOList = new ArrayList<PdfTemplateVO>();
		for(PdfTemplate pdfTemplate : pdfTemplateList) {
			PdfTemplateVO pdfTemplateVO = new PdfTemplateVO();
			BeanUtils.copyProperties(pdfTemplate, pdfTemplateVO);
			pdfTemplateVOList.add(pdfTemplateVO);
		}
		return pdfTemplateVOList;
		
	}
	
	
	
	/**
	 * 本地不存在,从数据库读取,存储到本地
	 * 
	 * @param 
	 * @throws Exception 
	 * @throws Exception
	 */
	public void getPdfData(PdfTemplate pdfTemplate) throws Exception {
		if(pdfTemplate == null) 
			throw new WebDataException("获取服务器PDF模板为空");
		
		if (StringUtil.isNotBlank(pdfTemplate.getTemplatePath())) {
			if (!FileUtil.checkPath(pdfTemplate.getTemplatePath())) {
				PdfTemplateData pdfTemplateData = pdfTemplateDataDao.getPdfTemplateData(pdfTemplate.getPdfTemplateDataId());
				if (pdfTemplateData != null) {
					isModify(pdfTemplateData, "Pdf模板数据表id为" + pdfTemplateData.getId(), "PdfTemplateData which id is " + pdfTemplateData.getId());
					byte[] data = pdfTemplateData.getData();
					FileUtil.storeFile(pdfTemplate.getTemplatePath(), data);
				}
			}
		}
	}

}
