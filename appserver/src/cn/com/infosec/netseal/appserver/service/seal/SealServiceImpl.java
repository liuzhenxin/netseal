package cn.com.infosec.netseal.appserver.service.seal;

import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.appserver.service.cert.CertServiceImpl;
import cn.com.infosec.netseal.appserver.service.key.KeyServiceImpl;
import cn.com.infosec.netseal.common.config.ConfigUtil;
import cn.com.infosec.netseal.common.dao.key.KeyDaoImpl;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.dao.seal.SealDaoImpl;
import cn.com.infosec.netseal.common.dao.sealData.SealDataDaoImpl;
import cn.com.infosec.netseal.common.dao.template.TemplateDaoImpl;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.SealData;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.GenSealUtil;
import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.util.photo.PhotoUitl;
import cn.com.infosec.netseal.itextpdf.text.Image;

@Service
public class SealServiceImpl extends BaseService {

	@Autowired
	private SealDaoImpl sealDao;
	@Autowired
	private TemplateDaoImpl templateDao;
	@Autowired
	private SealDataDaoImpl sealDataDao;
	@Autowired
	private PhotoDataDaoImpl photoDataDao;
	@Autowired
	private KeyDaoImpl keyDao;
	@Autowired
	private KeyServiceImpl keyService;
	@Autowired
	private CertServiceImpl certService;

	private Hashtable<Long, byte[]> htFile = new Hashtable<Long, byte[]>();
	private Hashtable<Long, List<Seal>> htDb = new Hashtable<Long, List<Seal>>();

	/**
	 * 增 (如果该用户无申请印模、启用和终止日期超出印模的启用和终止日期 不能插入)
	 * 
	 * @param seal
	 */
	public void insertSeal(Seal seal) {
		Template template = templateDao.getTemplate(seal.getTemplateId());
		if (template == null)
			throw new NetSealRuntimeException(ErrCode.TEMPATE_NOT_EXIST_IN_DB, "template not exist");

		long notBefor = template.getNotBefor();
		long notAfter = template.getNotAfter();
		if (seal.getNotBefor() < notBefor || seal.getNotAfter() > notAfter)
			throw new NetSealRuntimeException(ErrCode.DATE_EXCEED_LIMIT, "not in the period of validity");

		sealDao.insertSeal(seal);
	}

	/**
	 * 改
	 * 
	 * @param seal
	 */
	public int updateSealCount(Long id, Long updateTime) {
		Seal s = sealDao.getSeal(id);
		s.setUsedCount(s.getUsedCount() + 1);
		s.setUpdateTime(updateTime);

		return sealDao.updateSealCount(s);
	}

	/**
	 * 改
	 * 
	 * @param seal
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSeal(Seal seal, Cert cert, String photoDataB64, Company company) {
		long currentTime = DateUtil.getCurrentTime();
		String fileName = FileUtil.getFileName();
		String photoPath = Constants.PHOTO_PATH + DateUtil.getDateDir() + fileName + Constants.PHOTO_SUFFIX;
		String sealPath = Constants.SEAL_PATH + DateUtil.getDateDir() + fileName + Constants.SEAL_SUFFIX;

		String photoPathOld = seal.getPhotoPath();
		String sealPathOld = seal.getSealPath();
		byte[] photoDataBs = StringUtil.base64Decode(photoDataB64);
		photoDataBs = PhotoUitl.convertBGColor(photoDataBs, (int) (seal.getTransparency() * 2.55));

		try {
			Image.getInstance(photoDataBs);
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.GEN_SEAL_PHOTO_ERROR, "image data error");
		}
		try {
			seal.setSealPath(sealPath);
			seal.setPhotoPath(photoPath);
			seal.setUpdateTime(currentTime);

			// 产生印章图片
			FileUtil.storeFile(photoPath, photoDataBs);

			ConfigUtil config = ConfigUtil.getInstance();
			long serverKeyId = config.getSignKeyId();
			if (serverKeyId == -1)
				throw new NetSealRuntimeException(ErrCode.SERVERCERT_NOT_SET, "No key was set");

			Key key = keyDao.getKey(serverKeyId);
			// 校验制章密钥信息
			keyService.isModify(key);

			// 初始化证书文件
			byte[] keyData = keyService.getKeyData(key);

			byte[] serverCertData = FileUtil.getFile(key.getCertPath());
			// 初始化证书文件
			byte[] userCertData = certService.getCertData(cert.getCertPath(), cert.getCertDataId());

			// 产生印章文件
			List<byte[]> certDataList = keyService.getSignCertData();
			GenSealUtil.genSealData(seal, certDataList, key, keyData, serverCertData, userCertData, photoDataBs, ConfigUtil.getInstance().getGmOid().getBytes());

			PhotoData photoData = new PhotoData();
			photoData.setGenerateTime(seal.getGenerateTime());
			photoData.setUpdateTime(currentTime);
			photoData.setData(StringUtil.base64Decode(photoDataB64));

			SealData sealData = new SealData();
			sealData.setGenerateTime(seal.getGenerateTime());
			sealData.setUpdateTime(currentTime);
			byte[] sealDate = getSealData(seal.getSealPath(), seal.getSealDataId());
			sealData.setData(sealDate);

			// 如果印模和印章图片相同,不删除印章图片
			Template template = new Template();
			template.setPhotoDataId(seal.getPhotoDataId());
			int templateNum = templateDao.searchTotal(template, company.getTreeId());
			if (templateNum == 0)
				photoDataDao.deletePhotoData(seal.getPhotoDataId());
			sealDataDao.deleteSealData(seal.getSealDataId());

			photoDataDao.insertPhotoData(photoData);
			sealDataDao.insertSealData(sealData);

			if (templateNum == 0) {
				long oid = photoData.getId();
				PhotoData pd = photoDataDao.getPhotoData(oid);
				pd.setId(seal.getPhotoDataId());
				pd.setUpdateTime(DateUtil.getCurrentTime());

				photoDataDao.updatePhotoData(oid, pd);
			} else {
				seal.setPhotoDataId(photoData.getId());
			}

			SealData sd = sealDataDao.getSealData(sealData.getId());
			long oid = sealData.getId();
			sd.setId(seal.getSealDataId());
			sd.setUpdateTime(DateUtil.getCurrentTime());

			sealDataDao.updateSealData(oid, sd);
			sealDao.updateSeal(seal);

			// 删除旧文件
			if (templateNum == 0)
				FileUtil.deleteFile(photoPathOld);
			FileUtil.deleteFile(sealPathOld);
		} catch (Exception e) {
			FileUtil.deleteFile(photoPath);
			FileUtil.deleteFile(sealPath);

			if (e instanceof RuntimeException)
				throw e;
			else
				throw new RuntimeException(e.getMessage());
		}

	}

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public Seal getSeal(String name) {
		Seal seal = new Seal();
		seal.setName(name);

		List<Seal> list = sealDao.getSeals(seal);
		if (list == null || list.size() == 0)
			return null;
		else
			return list.get(0);
	}

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public Seal getSeal(long ID) {
		return sealDao.getSeal(ID);
	}

	/**
	 * 查列表
	 * 
	 * @param seal
	 * @return
	 */
	public List<Seal> getSeal(Seal seal) {
		return sealDao.getSeals(seal);
	}

	/**
	 * 获取印章数据
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public byte[] getSealData(long id) {
		byte[] data = new byte[0];
		Seal seal = sealDao.getSeal(id);
		if (seal == null)
			throw new NetSealRuntimeException(ErrCode.SEAL_NOT_EXIST_IN_DB, "seal not exist in db, seal id is " + id);

		// 校验信息
		isModify(seal);
		return getSealData(seal.getSealPath(), seal.getSealDataId());
	}

	public byte[] getSealData(String sealPath, Long sealDataId) {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(sealPath)) {
			data = FileUtil.getFile(sealPath);
		} else {
			SealData sealData = sealDataDao.getSealData(sealDataId);
			if (sealData == null) {
				throw new NetSealRuntimeException(ErrCode.SEAL_DATA_NOT_EXIST_IN_DB, "seal data not exist in db");
			}

			// 校验印章数据信息
			if (!(sealData.calMac()).equals(sealData.getMac()))
				throw new NetSealRuntimeException(ErrCode.MAC_INVALID, "seal data info had been tampered with, the seal_data id is " + sealData.getId());

			data = sealData.getData();
			FileUtil.storeFile(sealPath, data);
		}

		return data;
	}

	/**
	 * 校验信息
	 * 
	 * @param seal
	 * @param msg
	 */
	public void isModify(Seal seal) {
		isModify(seal, "the data of Seal which id is " + seal.getId() + " and name is " + seal.getName());
	}
}
