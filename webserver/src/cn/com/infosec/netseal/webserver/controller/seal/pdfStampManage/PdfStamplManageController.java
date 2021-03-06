package cn.com.infosec.netseal.webserver.controller.seal.pdfStampManage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.FileVO;
import cn.com.infosec.netseal.common.entity.vo.SealVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.ZipUtil;
import cn.com.infosec.netseal.webserver.controller.BaseController;
import cn.com.infosec.netseal.webserver.service.seal.SealServiceImpl;

/**
 * 印章管理
 */
@RequestMapping(value = "/sealManage/pdfStamp")
@Controller
public class PdfStamplManageController extends BaseController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private SealServiceImpl sealService;
	
	
	/**
	 * PDF文件批签管理
	 * 
	 * @return
	 */
	@RequestMapping(value = "pdfStampManage")
	public String pdfStampManage(){
		return "seal/pdfStamp/pdfStampManage";
	}
	
	/**
	 * PDF文件夹管理
	 * 
	 * @return
	 */
	@RequestMapping(value = "pdfFolderManage")
	public String pdfFolderManage(){
		ArrayList<String> files = new ArrayList<>();
		File f = new File(Constants.PDF_PATH);
		File[] tempList = f.listFiles();
		for (int i = 0; i < tempList.length; i++) {
	        if (tempList[i].isDirectory()){ // 文件夹
	        	String dir = tempList[i].toString();
	        	File df = new File(dir);
	        	files.add(df.getName());
	        }
	    }
		request.setAttribute("files", files);

		return "seal/pdfStamp/pdfFolderList";
	}
	
	/**
	 * 新增文件夹
	 * 
	 * @throws Exception 
	 * 
	 */
	@RequestMapping(value = "addPdfFolder")
	public ModelAndView addPdfFolder(String folderName) throws Exception {
		try {
			// 匹配文件夹名称
			File f = new File(Constants.PDF_PATH);
			File[] tempList = f.listFiles();
			for (int i = 0; i < tempList.length; i++) {
		        if (tempList[i].isDirectory()){ // 文件夹
		        	String dir = tempList[i].toString();
		        	File df = new File(dir);
		        	if (folderName.equals(df.getName())) {
						throw new WebDataException("文件夹名称重复");
					}
		        }
		    }
			
			File newFolder = new File(Constants.PDF_PATH + folderName);
			
			//获取父目录
			File fileParent = newFolder.getParentFile();
			if (!fileParent.exists()) 
				fileParent.mkdirs();
				
			newFolder.mkdir();
			
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}
	
	/**
	 * 删除PDF文件夹及文件
	 * 
	 */
	@RequestMapping(value = "delPdfFolder")
	public ModelAndView delPdfFolder(String ids) {
		try {
			String[] temp = ids.split(Constants.SPLIT_2);
			if (temp.length > 0) {
				for (String dirPath : temp) 
					FileUtil.deleteDir(Constants.PDF_PATH + dirPath);
				
			}
			
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}
	
	/**
	 * 删除PDF文件
	 * 
	 */
	@RequestMapping(value = "delPdf")
	public ModelAndView delPdf(String ids, String folder) {
		try {
			String[] temp = ids.split(Constants.SPLIT_2);
			if (temp.length > 0) {
				for (String dirPath : temp) 
					FileUtil.deleteFile(Constants.PDF_PATH + folder + Constants.SPLIT_DIR + dirPath);
				
			}
			
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}
	
	/**
	 * 查看PDF文件夹内文件
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "viewPdfFolder")
	public String viewPdfFolder(String ids, HttpServletRequest request) throws Exception {
		File f = new File(Constants.PDF_PATH + ids);
		File[] tempList = f.listFiles();
		List<FileVO> fileList = new ArrayList<>();
		for (File file : tempList) {
			FileVO fileVO = new FileVO();
			String fileName = file.getName();
			String sf = Constants.SPOT + FileUtil.getFileSuffix(fileName);
			if (sf.equals(Constants.PDF_SUFFIX)){
				fileVO.setFileName(fileName); // 名称
				fileVO.setFileTime(file.lastModified()); // 修改时间
				fileVO.setFileSize(file.length() / 1024); // 大小
				fileList.add(fileVO);
			}
				
		}
		request.setAttribute("folder", ids);
		request.setAttribute("files", fileList);
		return "seal/pdfStamp/pdfViewPdfFolder";
	}
	
	/**
	 * PDF文件上传
	 * 
	 * @return
	 */
	@RequestMapping(value = "pdfUploadManage")
	public String pdfUploadManage(){
		ArrayList<String> files = new ArrayList<>();
		File f = new File(Constants.PDF_PATH);
		File[] tempList = f.listFiles();
		for (int i = 0; i < tempList.length; i++) {
	        if (tempList[i].isDirectory()){ // 文件夹
	        	String dir = tempList[i].toString();
	        	File df = new File(dir);
	        	files.add(df.getName());
	        }
	    }
		request.setAttribute("files", files);

		return "seal/pdfStamp/pdfUploadList";
	}
	
	/**
	 * 上传PDF文件 .zip
	 * 
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "uploadPdfFile")
	public ModelAndView uploadPdfFile(String ids, String pwd, HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		String copyPath = "";
		String zipPath = "";
		try {
			// 目标文件
			String pdfDir = Constants.PDF_PATH + ids;
			File fileDir = new File(pdfDir);
			File[] listFile = fileDir.listFiles();
			if (listFile.length >= 100)
				throw new WebDataException("文件数量超过100,无法上传");
			
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request)) {
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multiRequest.getFile("pdfFile");
				if (file != null) {
					String fileOriName = file.getOriginalFilename();
					if (!fileOriName.endsWith(Constants.ZIP_SUFFIX))
						throw new WebDataException("文件类型错误..");

					// 创建解压文件路径
					copyPath = Constants.TMP_PATH + FileUtil.getFileName() + "/";
					File copyFile = new File(copyPath);
					if (!copyFile.exists()) 
						copyFile.mkdirs();// 创建文件夹
					

					String fileOriginalName = Constants.TMP_PATH + FileUtil.getFileName() + "/" + Constants.BACKUP_TMP;
					File targetFile = new File(fileOriginalName);
					if (!targetFile.exists()) 
						targetFile.mkdirs();
					
					file.transferTo(targetFile);
					// 上传文件夹路径
					zipPath = targetFile.getPath();
					
					try {
						ZipUtil.unZip(zipPath, copyPath, pwd);
					} catch (Exception we) {
						throw new WebDataException("文件解压失败,请确认文件校验密码");
					}
					// PDF文件大小
					File zipF = new File(copyPath);
					File[] zipFs = zipF.listFiles();
					for (File fs : zipFs) {
						String fsPath = fs.getPath();
						byte[] zipData = FileUtil.getFile(fsPath);
						String base64PD = StringUtil.base64Encode(zipData);
						if(base64PD.length() > Constants.LENGTH_3MB_B64) 
							throw new WebDataException("zip包内PDF文件不能大于3MB");
					}
					
					// 解压成功将解压的文件copy到目标目录
					File copyFs = new File(copyPath);
					File[] copyList = copyFs.listFiles();
					int length = copyList.length + listFile.length;
					if (length > 100)
						throw new WebDataException("文件数量过多,上传失败,确保上传后文件数量少于100");
					
					FileUtil.copyDir(copyPath, pdfDir);
					
					response.getWriter().write("ok");
				} else {
					throw new WebDataException("未获取到上传文件");
				}

			}
		} catch (WebDataException ex) {
			response.getWriter().write(ex.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			// 删除copyPath中文件
			if (StringUtil.isNotBlank(copyPath))
				FileUtil.deleteDir(copyPath);
			if (StringUtil.isNotBlank(zipPath))
				FileUtil.deleteFileAndDir(zipPath);
		}

		return null;
	}
	
	/**
	 * PDF批签
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "pdfBatchStampManage")
	public String pdfBatchStampManage(HttpServletRequest request) throws Exception {
		ArrayList<String> files = new ArrayList<>();
		File f = new File(Constants.PDF_PATH);
		File[] tempList = f.listFiles();
		for (int i = 0; i < tempList.length; i++) {
	        if (tempList[i].isDirectory()){ // 文件夹
	        	String dir = tempList[i].toString();
	        	File df = new File(dir);
	        	files.add(df.getName());
	        }
	    }
		
		// 获取印章集合
		SealVO sealVO = new SealVO();
		sealVO.setStatus(1);
		List<SealVO> sealVoList = sealService.getNoHandSeals(sealVO);
		request.setAttribute("sealList", sealVoList);
		
		request.setAttribute("files", files);
		return "seal/pdfStamp/pdfStampList";
	}

	/**
	 * 批签
	 * 
	 * @param ids
	 * @throws Exception 
	 */
	@RequestMapping(value = "pdfBatchStamp")
	public ModelAndView pdfBatchStamp(String ids, String name, String type, String keyWord, String X, String Y, String QFZ) throws Exception {
		try {
			if (StringUtil.isBlank(ids))
				throw new WebDataException("请求参数为空");
			// 校验盖章类型
			if (type.equals("1") && StringUtil.isBlank(keyWord)) 
				throw new WebDataException("关键字盖章未获取到关键字");
			
			if (type.equals("2") && ( StringUtil.isBlank(X) || StringUtil.isBlank(X)))
				throw new WebDataException("坐标盖章未获取到坐标值");
			
			if (type.equals("3") && ( StringUtil.isBlank(QFZ)))
				throw new WebDataException("骑缝盖章未获取到骑缝位置");
			
			if (type.equals("2")) {
				try {
					Float.parseFloat(X);
					Float.parseFloat(Y);
				} catch (Exception e) {
					throw new WebDataException("请输入正确格式坐标值");
				}
			}
			
			// 批签
			sealService.pdfBatchStamp(ids, name , type, keyWord, X, Y, QFZ);
			
		} catch (WebDataException e) {
			return getModelAndView(getErrMap(e));
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}
	
	/**
	 * 下载已盖章PDF文件页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(value = "toDownloadPdf")
	public String toDownloadPdf(){
		ArrayList<String> files = new ArrayList<>();
		File f = new File(Constants.PDF_STAMP_PATH);
		File[] tempList = f.listFiles();
		for (File file : tempList) {
			files.add(file.getName());
		}
		request.setAttribute("stamPdf", files);
		
		return "seal/pdfStamp/pdfDownload";
	}
	
	/**
	 * 批量压缩已盖章PDF
	 * 
	 * @param ids
	 * @throws WebDataException 
	 */
	@RequestMapping(value = "toDownloadStampPdf")
	public ModelAndView downloadStampPdf(String ids) throws Exception{
		String[] temp = ids.split(Constants.SPLIT_2);
		
		// 打包目录
		String downPath = Constants.TMP_PATH + Constants.BACKUP_PDF;
		
		try {
			// 复制PDF文件到打包目录
			if (temp.length > 0) {
				for (int i = 0; i < temp.length; i++) { 
					String pdfPath = Constants.PDF_STAMP_PATH + temp[i] + Constants.SPLIT_DIR;
					try {
						// 压缩文件
						ZipUtil.zipDir(pdfPath, downPath, "");
					} catch (Exception e) {
						throw new WebDataException("文件压缩出错");
					}
				}
			}
			
			
		} catch (Exception e) {
			FileUtil.deleteFile(downPath);
			throw  new WebDataException(e.getMessage());
		}
		
		return getModelAndView(getSuccMap(downPath));
		
	}
	
	/**
	 * 查看已签PDF文件夹内文件
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "viewStampFolder")
	public String viewStampFolder(String ids, HttpServletRequest request) throws Exception {
		File f = new File(Constants.PDF_STAMP_PATH + ids);
		File[] tempList = f.listFiles();
		List<FileVO> fileList = new ArrayList<>();
		for (File file : tempList) {
			FileVO fileVO = new FileVO();
			String fileName = file.getName();
			String sf = Constants.SPOT + FileUtil.getFileSuffix(fileName);
			if (sf.equals(Constants.PDF_SUFFIX)){
				fileVO.setFileName(fileName); // 名称
				fileVO.setFileTime(file.lastModified()); // 修改时间
				fileVO.setFileSize(file.length() / 1024); // 大小
				fileList.add(fileVO);
			}
				
		}
		
		request.setAttribute("files", fileList);
		return "seal/pdfStamp/pdfViewFolder";
	}
	
	/**
	 * 删除已签PDF文件夹及文件
	 * 
	 */
	@RequestMapping(value = "delStampPdfFolder")
	public ModelAndView delStampPdfFolder(String ids) {
		try {
			String[] temp = ids.split(Constants.SPLIT_2);
			if (temp.length > 0) {
				for (String dirPath : temp) 
					FileUtil.deleteDir(Constants.PDF_STAMP_PATH + dirPath);
				
			}
			
		} catch (Exception e) {
			throw e;
		}
		return getModelAndView(getSuccMap());
	}
	
	/**
	 *  下载PDF压缩文件
	 * 
	 * @param zipPath
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "downloadPdf")
	public void downloadPdf(String zipPath, HttpServletRequest request, HttpServletResponse response){
		// 下载文件
		String fileName = Constants.BACKUP_PDF;
		download(zipPath, fileName, request, response);
		
		FileUtil.deleteFile(zipPath);
	}
	
	
}
