package cn.com.infosec.netseal.appserver.service.operateLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.infosec.netseal.common.dao.operateLog.OperateLogDaoImpl;
import cn.com.infosec.netseal.common.entity.po.OperateLog;

@Service
public class OperateLogServiceImpl {

	@Autowired
	private OperateLogDaoImpl operateLogDao;

	/**
	 * 增
	 * 
	 * @param sys
	 */
	public void insertOperateLog(OperateLog operateLog) {
		operateLogDao.insertOperateLog(operateLog);
	}

}
