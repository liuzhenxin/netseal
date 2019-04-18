package cn.com.infosec.netseal.webserver.service.stamp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.util.StringUtil;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.vo.stamp.StampVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.ZipUtil;
import cn.com.infosec.netseal.common.util.photo.CircleUtil;
import cn.com.infosec.netseal.common.util.photo.EllipseUtil;
import cn.com.infosec.netseal.common.util.photo.RectangleUtil;
import cn.com.infosec.netseal.common.util.photo.SquareUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;

@Service
public class StampServiceImpl extends BaseService {

	/** 图章预览
	 * @param stampVO
	 * @return
	 * @throws Exception
	 */
	public byte[] viewStamp(StampVO stampVO) throws Exception {
		byte[] data = null;
		Integer stampStyle = stampVO.getStampStyle();
		if (stampStyle == null)
			throw new WebDataException("图章样式为空");
		if (stampStyle.intValue() == 1) {
			CircleUtil stamp = new CircleUtil(stampVO);
			data = stamp.genStampData();
		} else if (stampStyle.intValue() == 2) {
			EllipseUtil stamp = new EllipseUtil(stampVO);
			data = stamp.genStampData();
		} else if (stampStyle.intValue() == 3) {
			SquareUtil stamp = new SquareUtil(stampVO);
			data = stamp.genStampData();
		} else if (stampStyle.intValue() == 4) {
			RectangleUtil stamp = new RectangleUtil(stampVO);
			data = stamp.genStampData();
		} else {
			throw new WebDataException("未知的图章样式");
		}

		return data;
	}

	/** 批量制作图章
	 * @param stampVO
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public String gemStamp(StampVO stampVO, InputStream inputStream) throws Exception {
		List<String> txtList = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				if (txtList.size() > 500)
					throw new WebDataException("批量制作图章个数超出限制,每次最多制作500个");
				if (StringUtil.isNotBlank(str))
					txtList.add(str);
			}
		} catch (WebDataException e) {
			throw e;
		} catch (Exception e) {
			throw new WebDataException("读出文件错误");
		} finally {
			if (inputStream != null)
				inputStream.close();
			if (bufferedReader != null)
				bufferedReader.close();
		}
		Integer stampStyle = stampVO.getStampStyle();
		if (stampStyle == null)
			throw new WebDataException("图章样式为空");

		String baseDir = Constants.STAMP_PATH + DateUtil.getDateDir();
		if (new File(Constants.REPORT_PATH).exists()) {
			// 删除昨天以后目录
			FileUtil.deleteDir(Constants.STAMP_PATH, baseDir);
		}
		String path = baseDir + DateUtil.getCurrentTime() + "/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		if (stampStyle.intValue() == 1) {
			CircleUtil stamp = new CircleUtil(stampVO);
			for (int i = 0; i < txtList.size(); i++) {
				String[] temps = txtList.get(i).split(",");
				String name = "";
				String company = "";
				if (temps.length > 0)
					name = temps[0];
				if (temps.length > 1)
					company = temps[1];
				byte[] stampData = stamp.genStampData(name, company);
				FileUtil.storeFile(path + (i + 1) + Constants.PHOTO_SUFFIX, stampData);
			}
		} else if (stampStyle.intValue() == 2) {
			EllipseUtil stamp = new EllipseUtil(stampVO);
			for (int i = 0; i < txtList.size(); i++) {
				String[] temps = txtList.get(i).split(",");
				String name = "";
				String company = "";
				if (temps.length > 0)
					name = temps[0];
				if (temps.length > 1)
					company = temps[1];
				byte[] stampData = stamp.genStampData(name, company);
				FileUtil.storeFile(path + (i + 1) + Constants.PHOTO_SUFFIX, stampData);
			}
		} else if (stampStyle.intValue() == 3) {
			SquareUtil stamp = new SquareUtil(stampVO);
			for (int i = 0; i < txtList.size(); i++) {
				String[] temps = txtList.get(i).split(",");
				String name = "";
				if (temps.length > 0)
					name = temps[0];

				byte[] stampData = stamp.genStampData(name);
				FileUtil.storeFile(path + (i + 1) + Constants.PHOTO_SUFFIX, stampData);
			}
		} else if (stampStyle.intValue() == 4) {
			RectangleUtil stamp = new RectangleUtil(stampVO);
			for (int i = 0; i < txtList.size(); i++) {
				String[] temps = txtList.get(i).split(",");
				String name = "";
				if (temps.length > 0)
					name = temps[0];
				byte[] stampData = stamp.genStampData(name);
				FileUtil.storeFile(path + (i + 1) + Constants.PHOTO_SUFFIX, stampData);
			}
		} else {
			throw new WebDataException("未知的图章样式");
		}

		String stampZip = "stamp_" + DateUtil.getCurrentTime() + ".zip";
		// 压缩
		ZipUtil.zip(path, baseDir + stampZip, null);
		// 删除目录
		FileUtil.deleteDir(path);

		return stampZip;
	}
}
