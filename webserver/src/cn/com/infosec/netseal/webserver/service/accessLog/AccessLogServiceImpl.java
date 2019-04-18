package cn.com.infosec.netseal.webserver.service.accessLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.accessLog.AccessLogDaoImpl;
import cn.com.infosec.netseal.common.entity.po.AccessLog;
import cn.com.infosec.netseal.common.entity.po.AccessLogCount;
import cn.com.infosec.netseal.common.entity.vo.AccessLogCountVO;
import cn.com.infosec.netseal.common.entity.vo.AccessLogVO;
import cn.com.infosec.netseal.common.util.CountMsgUtil;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.LoggerFileUtil;
import cn.com.infosec.netseal.webserver.util.Page;

@Service
public class AccessLogServiceImpl extends BaseService {

	@Autowired
	protected AccessLogDaoImpl accessLogDao;

	/**
	 * 增加访问日志信息
	 * 
	 * @param accessLog
	 */
	public void insertAccessLog(AccessLogVO accessLogVO) {
		AccessLog accessLog=new AccessLog();
		BeanUtils.copyProperties(accessLogVO, accessLog);
		accessLogDao.insertAccessLog(accessLog);
	}

	/**
	 * 删除访问日志
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteAccessLog(long ID) {
		accessLogDao.deleteAccessLog(ID);
	}

	/**
	 * 改
	 * 
	 * @param accessLog
	 * @throws Exception
	 */
	public void updateAccessLog(AccessLogVO accessLogVO) {
		//accessLogDao.updateAccessLog(accessLog);
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public AccessLogVO getAccessLog(long ID) {
		try {
			AccessLog accessLog=accessLogDao.getAccessLog(ID);
			if(accessLog==null)
				return null;
			AccessLogVO accessLogVO=new AccessLogVO();
			BeanUtils.copyProperties(accessLog, accessLogVO);
			return accessLogVO;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 分页
	 * 
	 * @param page
	 * @param accessLog
	 * @return
	 * @throws Exception
	 */
	public Page<AccessLogVO> searchAccessLog(Page<AccessLogVO> page, AccessLogVO accessLogVO) throws Exception {
		AccessLog accessLog=new AccessLog();
		BeanUtils.copyProperties(accessLogVO, accessLog);
		int total = accessLogDao.searchTotal(accessLog);
		int start = page.getStart();
		int end = page.getEnd();

		List<AccessLog> list = accessLogDao.searchAccessLog(accessLog, start, end);
		List<AccessLogVO> alVOlist=new ArrayList<AccessLogVO>();
		for(AccessLog al:list){
			AccessLogVO alVO=new AccessLogVO();
			BeanUtils.copyProperties(al, alVO);
			
			// 校验信息
			isModify(al, alVO);
			
			alVOlist.add(alVO);
		}
		page.setTotalNo(total);
		page.setResult(alVOlist);
		return page;
	}
	
	
	

	/**
	 * 文件
	 * 
	 * @param page
	 * @param accessLog
	 * @return
	 */
	public Page<AccessLogVO> accessLogFileList(Page<AccessLogVO> page, AccessLogVO accessLogVO) {
		File[] fs = LoggerFileUtil.logFile("access");
		int total = fs.length;
		int start = page.getStart();
		int end = page.getEnd();

		List<AccessLogVO> list = new ArrayList<AccessLogVO>();
		for (int i = 1; i <= fs.length; i++) {
			File f = fs[i - 1];
			if (f.isFile() && i >= start && i <= end) {
				AccessLogVO log = new AccessLogVO();
				String fileName = f.getName();
				log.setFileName(fileName);
				log.setFilePath(f.getAbsolutePath());
				log.setFileSize(f.length() / 1024);
				log.setFileTime(f.lastModified());
				list.add(log);
			}
		}
		page.setTotalNo(total);
		page.setResult(list);

		return page;
	}
	
	
	/**
	 * 按操作类型查询
	 * @return
	 */
	
	public  List<AccessLogCountVO> getAccessLogCount(){
		List<AccessLogCount> accessLogCountList = accessLogDao.getAccessLogCount();
		List<AccessLogCountVO>	accessLogCountVOList = new ArrayList<AccessLogCountVO>();
		for (AccessLogCount accessLogCount:accessLogCountList) {
			AccessLogCountVO accessLogCountVO =new AccessLogCountVO();
			BeanUtils.copyProperties(accessLogCount, accessLogCountVO);
			accessLogCountVO.setOptype(CountMsgUtil.getCountMsg(accessLogCount.getOptype()));
			accessLogCountVOList.add(accessLogCountVO);
		}
		return accessLogCountVOList;
	}
	
	

}
