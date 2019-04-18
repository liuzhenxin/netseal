package cn.com.infosec.netseal.appserver.service.photoData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.appserver.service.BaseService;
import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;

@Service
public class PhotoDataServiceImpl extends BaseService {

	@Autowired
	private PhotoDataDaoImpl photoDataDao;

	/**
	 * 获取图片数据
	 * 
	 * @param ID
	 * @return
	 * @throws Exception
	 */
	public byte[] getPhotoData(String photoPath, Long photoDataId) {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(photoPath)) {
			data = FileUtil.getFile(photoPath);
		} else {
			PhotoData photoData = photoDataDao.getPhotoData(photoDataId);
			if (photoData == null)
				throw new NetSealRuntimeException(ErrCode.PHOTO_DATA_NOT_EXIST_IN_DB, "photo data is not exist in db, photo data ID is " + photoDataId);

			// 校验图片数据信息
			isModify(photoData, "the data of PhotoData which id is " + photoData.getId());

			data = photoData.getData();
			FileUtil.storeFile(photoPath, data);
		}
		return data;
	}

}
