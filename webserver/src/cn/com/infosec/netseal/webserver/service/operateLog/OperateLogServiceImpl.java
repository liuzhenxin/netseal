package cn.com.infosec.netseal.webserver.service.operateLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.operateLog.OperateLogDaoImpl;
import cn.com.infosec.netseal.common.entity.po.OperateLog;
import cn.com.infosec.netseal.common.entity.vo.OperateLogVO;
import cn.com.infosec.netseal.common.exceptions.WebDataException;
import cn.com.infosec.netseal.webserver.service.BaseService;
import cn.com.infosec.netseal.webserver.util.CommonUtil;
import cn.com.infosec.netseal.webserver.util.LoggerFileUtil;
import cn.com.infosec.netseal.webserver.util.Page;

@Service
public class OperateLogServiceImpl extends BaseService{
	@Autowired
	protected OperateLogDaoImpl operateLogDao;
	
	/**
	 * 添加操作日志信息
	 * 
	 * @param operateLogVO
	 */
	public void insertOperateLog(OperateLogVO operateLogVO) {
		OperateLog operateLog=new OperateLog();
		BeanUtils.copyProperties(operateLogVO, operateLog);
		operateLogDao.insertOperateLog(operateLog);
	}
	
	/**
	 * 获取单条日志信息
	 * 
	 * @param id
	 * @return
	 */
	public OperateLogVO getOperateLog(Long id) {
		try{
			OperateLog operateLog=operateLogDao.getOperateLog(id);
			if(operateLog==null)
				return null;
			OperateLogVO operateLogVO=new OperateLogVO();
			BeanUtils.copyProperties(operateLog, operateLogVO);
			return operateLogVO;
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	/**
	 * 删除操作日志
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteOperateLog(Long id) throws Exception {
		operateLogDao.deleteOperateLog(id);
	}
	
	/**
	 * 修改操作日志信息
	 * 
	 * @param operateLogVO
	 * @throws Exception
	 */
	public void updateOperateLog(OperateLogVO operateLogVO) throws Exception{
		OperateLog operateLog=new OperateLog();
		BeanUtils.copyProperties(operateLogVO, operateLog);
		int r=operateLogDao.updateOperateLog(operateLog);
		if(r==0){
			throw new WebDataException("更新失败");
		}
	}
	
	/**
	 * 分页查询操作日志信息
	 * 
	 * @param page
	 * @param operateLogVO
	 * @return
	 * @throws Exception 
	 */
	public Page<OperateLogVO> searchOperateLog(Page<OperateLogVO> page,OperateLogVO operateLogVO) throws Exception {
		OperateLog operateLog=new OperateLog();
		BeanUtils.copyProperties(operateLogVO, operateLog);
		
		Long timeStrat=CommonUtil.timeStrStart(operateLogVO.getOpTimeStart());
		Long timeEnd=CommonUtil.timeStrEnd(operateLogVO.getOpTimeEnd());
		int total=operateLogDao.searchTotal(operateLog,timeStrat,timeEnd);
		int start=page.getStart();
		int end=page.getEnd();
		List<OperateLog> list= operateLogDao.searchOperateLog(operateLog, start, end,timeStrat,timeEnd);
		List<OperateLogVO> olVOlist=new ArrayList<OperateLogVO>();
		for(OperateLog ol:list){
			OperateLogVO olVO=new OperateLogVO();
			BeanUtils.copyProperties(ol, olVO);
			
			// 校验信息
			isModify(ol, olVO);
			
			olVOlist.add(olVO);
		}
		page.setTotalNo(total);
		page.setResult(olVOlist);
		return page;
	}
	
	/**
	 * 查询操作日志文件信息
	 * 
	 * @param page
	 * @param operateLogVO
	 * @return
	 */
	public Page<OperateLogVO> operateLogFileList(Page<OperateLogVO> page,OperateLogVO operateLogVO) {
		File[] fs=LoggerFileUtil.logFile("operate");
		int total=fs.length;
		int start=page.getStart();
		int end=page.getEnd();
		List<OperateLogVO> list= new ArrayList<OperateLogVO>();
		for (int i = 1; i <= fs.length; i++) {
			File f = fs[i - 1];
			if (f.isFile() && i >= start && i <= end){
				OperateLogVO log=new OperateLogVO();
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
