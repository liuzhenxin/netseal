package cn.com.infosec.netseal.appserver.service.request;

import java.util.List;

import cn.com.infosec.netseal.appserver.service.certChain.CertChainServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.appserver.service.photoData.PhotoDataServiceImpl;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.dao.request.RequestDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.sealData.SealDataDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.po.Request;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.SealData;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.GenSealUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.itextpdf.text.Image;

@Service
public class RequestServiceImpl extends BaseService {

	@Autowired
	private RequestDaoImpl requestDao;
	@Autowired
	private PhotoDataDaoImpl photoDataDao;
	@Autowired
	private CertChainServiceImpl certChainService;
	@Autowired
	private SealDaoImpl sealDao;
	@Autowired
	private KeyDaoImpl keyDao;
	@Autowired
	private SealDataDaoImpl sealDataDao;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertServiceImpl certService;
	@Autowired
	private PhotoDataServiceImpl photoDataService;

	public Request getRequestByCert(long certId) {
		Request req = new Request();
		req.setCertId(certId);

		List<Request> list = requestDao.getRequests(req);
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	public List<Request> getRequesList(String name) {
		if (StringUtil.isBlank(name))
			return null;
		Request request = new Request();
		request.setName(name);
		List<Request> list = requestDao.getRequests(request);
		return list;
	}

	/**
	 * 增加申请
	 * 
	 * @param request
	 * @param photoDataB64
	 * @param template
	 * @param cert
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertRequest(Request request, String photoDataB64, Template template, Cert cert) {
		long currentTime = DateUtil.getCurrentTime();
		String photoPath = "";
		PhotoData photoData = new PhotoData();
		byte[] photoDataBs = null;
		if (template.getType() != 3) {
			photoPath = Constants.PHOTO_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.PHOTO_SUFFIX;
			// 上传图片不为空
			if (StringUtil.isNotBlank(photoDataB64)) {
				photoDataBs = StringUtil.base64Decode(photoDataB64);
				try {
					Image.getInstance(photoDataBs);
				} catch (Exception e) {
					throw new NetSealRuntimeException(ErrCode.GEN_SEAL_PHOTO_ERROR, "image data error");
				}
			} else // 获取印模图片数据
				photoDataBs = photoDataService.getPhotoData(template.getPhotoPath(), template.getPhotoDataId());
			
			// 保存图片数据
			photoData.setGenerateTime(currentTime);
			photoData.setData(photoDataBs);
			photoData.setUpdateTime(currentTime);
			Long photoId = photoDataDao.insertPhotoData(photoData);

			request.setPhotoDataId(photoId);
			
			// 生成图片文件
			FileUtil.storeFile(photoPath, photoDataBs);
		} else 
			request.setPhotoDataId(-1L);
		
		request.setGenerateTime(currentTime);
		request.setPhotoPath(photoPath);
		request.setUpdateTime(currentTime);

		// 审核
		if (template.getIsAuditReq() != null && template.getIsAuditReq() == 0) { // 申请不需要审核,生成印章
			// 获取默认制章密钥
			ConfigUtil config = ConfigUtil.getInstance();
			long serverKeyId = config.getSignKeyId();
			if (serverKeyId == -1)
				throw new NetSealRuntimeException(ErrCode.SERVERCERT_NOT_SET, "No key was set");

			Key key = keyDao.getKey(serverKeyId);
			if (key == null)
				throw new NetSealRuntimeException(ErrCode.KEY_NOT_EXIST_IN_DB, "key is not exist in db");
			// 校验制章密钥信息
			keyService.isModify(key);

			// 初始化密钥文件
			byte[] keyData = keyService.getKeyData(key);

			// 获取证书数据
			byte[] serverCertData = certService.getCertData(key.getCertPath(), key.getCertDataId());
			byte[] userCertData = certService.getCertData(cert.getCertPath(), cert.getCertDataId());
			if (template.getIsAuthCertGenSeal() != null && template.getIsAuthCertGenSeal() == 1) {// 审核验证书
				// 验证服务器证书
				certChainService.verifyCert(serverCertData);
				// 验证签章人证书
				certChainService.verifyCert(userCertData);
			}

			// 获取图片宽、高
			int width = 0 ,height=0;
			if(photoDataBs != null) {
				List list = FileUtil.getImagePro(photoDataBs);
				width = (Integer) list.get(1);
				height = (Integer) list.get(2);
			}
			String sealPath = Constants.SEAL_PATH + DateUtil.getDateDir() + FileUtil.getFileName() + Constants.SEAL_SUFFIX;
			Seal seal = new Seal();
			BeanUtils.copyProperties(request, seal);
			seal.setStatus(1);
			seal.setType(template.getType());
			seal.setIsDownload(template.getIsDownload());
			seal.setPhotoPath(photoPath);
			seal.setPhotoHigh(height);
			seal.setPhotoWidth(width);
			seal.setIsAuthCertDownload(template.getIsAuthCertDownload());
			seal.setIsAuditReq(template.getIsAuditReq());
			seal.setIsAuthCertGenSeal(template.getIsAuthCertGenSeal());
			seal.setSealPath(sealPath);
			seal.setTransparency(template.getTransparency());

			// 1. 生成印章
			seal.setPhotoData(photoData);
			List<byte[]> certDataList = keyService.getSignCertData();
			byte[] data = GenSealUtil.genSealData(seal, certDataList, key, keyData, serverCertData, userCertData, photoDataBs, ConfigUtil.getInstance().getGmOid().getBytes());

			// 2.生成印章数据记录
			SealData sealData = new SealData();
			sealData.setGenerateTime(currentTime);
			sealData.setData(data);
			sealData.setUpdateTime(seal.getUpdateTime());
			Long sealId = sealDataDao.insertSealData(sealData);

			// 3.保存印章
			seal.setSealDataId(sealId);
			seal.setAuditId(Long.valueOf(-1));
			seal.setUsedCount(0);
			seal.setUsedLimit(0);
			seal.setDownloadTime(0L);
			seal.setSysUserId(-1L);
			seal.setUserId(cert.getUserId());
			sealDao.insertSeal(seal);
		} else {
			// 需要审核
			requestDao.insertRequest(request);
		}
	}

}
