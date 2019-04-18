package cn.com.infosec.netseal.webserver.service.photoData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.photoData.PhotoDataDaoImpl;
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;

@Service
public class PhotoDataServiceImpl extends BaseService {

	@Autowired
	private PhotoDataDaoImpl photoDataDao;

	/**
	 * 读取印模图片,本地不存在,从数据库读取,存储到本地
	 * 
	 * @param id
	 * @return 印模图片数据
	 * @throws Exception
	 */
	public byte[] getPhotoData(String photoPath, Long photoDataId) throws Exception {
		byte[] data = new byte[0];
		if (FileUtil.checkPath(photoPath)) {
			data = FileUtil.getFile(photoPath);
		} else {
			PhotoData photoData = photoDataDao.getPhotoData(photoDataId);
			if (photoData == null)
				throw new WebDataException("数据库中印模图片数据不存在");

			// 校验图片数据信息
			isModify(photoData, "图片数据表id为 " + photoData.getId(), "PhotoData which id is " + photoData.getId() );

			data = photoData.getData();
			FileUtil.storeFile(photoPath, data);
		}

		return data;
	}

}
