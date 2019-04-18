package cn.com.infosec.netseal.webserver.service.manageLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.manageLog.ManageLogDaoImpl;
import cn.com.infosec.netseal.common.entity.po.ManageLog;
import cn.com.infosec.netseal.common.entity.vo.ManageLogVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.CommonUtil;
import cn.com.infosec.netseal.webserver.util.LoggerFileUtil;
import cn.com.infosec.netseal.webserver.util.Page;

@Service
public class ManageLogServiceImpl extends BaseService{
	@Autowired
	protected ManageLogDaoImpl manageLogDao;
	
	/**
	 * 添加管理日志
	 * 
	 * @param manageLogVO
	 */
	public void insertManageLog(ManageLogVO manageLogVO) {
		ManageLog manageLog=new ManageLog();
		BeanUtils.copyProperties(manageLogVO, manageLog);
		manageLogDao.insertManageLog(manageLog);
	}
	
	/**
	 * 单条查询管理日志信息
	 * @param id
	 * @return
	 */
	public ManageLogVO getManageLog(Long id) {
		try{
			ManageLog manageLog=manageLogDao.getManageLog(id);
			if(manageLog==null)
				return null;
			ManageLogVO manageLogVO=new ManageLogVO();
			BeanUtils.copyProperties(manageLog, manageLogVO);
			return manageLogVO;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	/**
	 * 删除管理日志信息
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteManageLog(Long id) throws Exception {
		manageLogDao.deleteManageLog(id);
	}
	
	
	/**
	 * 修改管理日志信息
	 * 
	 * @param manageLogVO
	 * @throws Exception
	 */
	public void updateManageLog(ManageLogVO manageLogVO) throws Exception{
		ManageLog manageLog=new ManageLog();
		BeanUtils.copyProperties(manageLogVO, manageLog);
		int r=manageLogDao.updateManageLog(manageLog);
		if(r==0){
			throw new WebDataException("更新管理失败");
		}
	}
	
	/**
	 * 分页查询管理日志信息
	 * 
	 * @param page
	 * @param manageLogVO
	 * @return
	 * @throws Exception 
	 */
	public Page<ManageLogVO> searchManageLog(Page<ManageLogVO> page,ManageLogVO manageLogVO) throws Exception {
		ManageLog manageLog=new ManageLog();
		BeanUtils.copyProperties(manageLogVO, manageLog);
		Long timeStart=CommonUtil.timeStrStart(manageLogVO.getOpTimeStart());
		Long timeEnd=CommonUtil.timeStrEnd(manageLogVO.getOpTimeEnd());
		int total=manageLogDao.searchTotal(manageLog,timeStart,timeEnd);
		int start=page.getStart();
		int end=page.getEnd();
		List<ManageLog> list= manageLogDao.searchManageLog(manageLog, start, end,timeStart,timeEnd);
		List<ManageLogVO> mlVOlist=new ArrayList<ManageLogVO>();
		for(ManageLog ml:list){
			ManageLogVO mlVO=new ManageLogVO();
			BeanUtils.copyProperties(ml, mlVO);
			
			// 校验信息
			isModify(ml, mlVO);
			
			mlVOlist.add(mlVO);
		}
		page.setTotalNo(total);
		page.setResult(mlVOlist);
		return page;
	}
	
	/**
	 * 管理日志文件查询
	 * 
	 * @param page
	 * @param manageLogVO
	 * @return
	 */
	public Page<ManageLogVO> manageLogFileList(Page<ManageLogVO> page,ManageLogVO manageLogVO) {
		File[] fs=LoggerFileUtil.logFile("manage");
		int total=fs.length;
		int start=page.getStart();
		int end=page.getEnd();
		List<ManageLogVO> list= new ArrayList<ManageLogVO>();
		for (int i = 1; i <= fs.length; i++) {
			File f = fs[i - 1];
			if (f.isFile() && i >= start && i <= end){
				ManageLogVO log=new ManageLogVO();
				log.setFileName(f.getName());
				log.setFilePath(f.getAbsolutePath());
				log.setFileSize(f.length()/1024);
				log.setGenerateTime(f.lastModified());
				list.add(log);
			}
		}
		page.setTotalNo(total);
		page.setResult(list);
		return page;
	}
}
