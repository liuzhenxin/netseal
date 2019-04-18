package cn.com.infosec.netseal.common.dao.operateLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.OperateLog;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class OperateLogDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param sys
	 * @throws Exception 
	 */
	public void insertOperateLog(OperateLog operateLog)  {
		operateLog.setId(idGenerator.getNewId(Constants.TABLE_SEAL_OPERATE_LOG));

		String sql = "insert into SEAL_OPERATE_LOG(ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC)values(?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { operateLog.getId(), operateLog.getAccount(), operateLog.getClientHost(), operateLog.getOpType(), operateLog.getGenerateTime(),
				operateLog.getUpdateTime(), operateLog.getReturnCode(), operateLog.getErrMsg(), operateLog.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteOperateLog(Long id) {
		String sql = "delete from SEAL_OPERATE_LOG where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateOperateLog(OperateLog operateLog) {
		String sql = "update SEAL_OPERATE_LOG set ACCOUNT=?,CLIENT_HOST=?,OPTYPE=?,GENERATE_TIME=?,UPDATE_TIME=?,RETURN_CODE=?,ERR_MSG=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { operateLog.getAccount(), operateLog.getClientHost(), operateLog.getOpType(), operateLog.getGenerateTime(), operateLog.getUpdateTime(),
				operateLog.getReturnCode(), operateLog.getErrMsg(), operateLog.getMac(), operateLog.getId() });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<OperateLog> getOperateLogs() {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC from SEAL_OPERATE_LOG";
		return getJdbcTemplate().query(sql, new OperateLogRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public OperateLog getOperateLog(Long id) {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC from SEAL_OPERATE_LOG where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new OperateLogRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class OperateLogRowMapper implements RowMapper<OperateLog> {
		public OperateLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			OperateLog operateLog = new OperateLog();
			operateLog.setId(ObjectUtil.toLong(rs.getObject("ID")));
			operateLog.setAccount(rs.getString("ACCOUNT"));
			operateLog.setClientHost(rs.getString("CLIENT_HOST"));
			operateLog.setOpType(rs.getString("OPTYPE"));
			operateLog.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			operateLog.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			operateLog.setReturnCode(rs.getString("RETURN_CODE"));
			operateLog.setErrMsg(rs.getString("ERR_MSG"));
			operateLog.setMac(rs.getString("MAC"));
			return operateLog;
		}
	}

	public Integer searchTotal(OperateLog operateLog, long timeStrat, long timeEnd) {
		String sql = "select count(1) from SEAL_OPERATE_LOG where 1=1 ";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(operateLog.getAccount())) {
			sql += " and ACCOUNT like ?";
			valueList.add(operateLog.getAccount().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getOpType())) {
			sql += " and OPTYPE like ?";
			valueList.add(operateLog.getOpType().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getClientHost())) {
			sql += " and CLIENT_HOST like ?";
			valueList.add(operateLog.getClientHost().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getReturnCode())) {
			sql += " and RETURN_CODE like ?";
			valueList.add(operateLog.getReturnCode().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getOpTimeStart())) {
			sql += " and GENERATE_TIME >= ?";
			valueList.add(timeStrat);
		}

		if (StringUtil.isNotBlank(operateLog.getOpTimeEnd())) {
			sql += " and GENERATE_TIME <= ?";
			valueList.add(timeEnd);
		}
		if (operateLog.getId() != null) {
			sql += " and ID=?";
			valueList.add(operateLog.getId());
		}
		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ps.execute();
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {
					return rs.getInt(1);
				} else {
					return 0;
				}
			}

		});
		return total;
	}

	public List<OperateLog> searchOperateLog(OperateLog operateLog, final int start, final int end, long timeStrat, long timeEnd) {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC from SEAL_OPERATE_LOG where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(operateLog.getAccount())) {
			sql += " and ACCOUNT like ?";
			valueList.add(operateLog.getAccount().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getOpType())) {
			sql += " and OPTYPE like ?";
			valueList.add(operateLog.getOpType().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getReturnCode())) {
			sql += " and RETURN_CODE like ?";
			valueList.add(operateLog.getReturnCode().trim() + "%");
		}
		if (StringUtil.isNotBlank(operateLog.getOpTimeStart())) {
			sql += " and GENERATE_TIME >= ?";
			valueList.add(timeStrat);
		}
		if (StringUtil.isNotBlank(operateLog.getOpTimeEnd())) {
			sql += " and GENERATE_TIME <= ?";
			valueList.add(timeEnd);
		}
		if (operateLog.getId() != null) {
			sql += " and ID=?";
			valueList.add(operateLog.getId());
		}
		sql += " order by ID desc";

		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<OperateLog> operateLogList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<OperateLog>>() {
			@Override
			public List<OperateLog> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<OperateLog> list = new ArrayList<OperateLog>();
				while (rs.next()) {
					OperateLog operateLog = new OperateLog();
					operateLog.setId(ObjectUtil.toLong(rs.getObject("ID")));
					operateLog.setAccount(rs.getString("ACCOUNT"));
					operateLog.setClientHost(rs.getString("CLIENT_HOST"));
					operateLog.setOpType(rs.getString("OPTYPE"));
					operateLog.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					operateLog.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					operateLog.setReturnCode(rs.getString("RETURN_CODE"));
					operateLog.setErrMsg(rs.getString("ERR_MSG"));
					operateLog.setMac(rs.getString("MAC"));
					list.add(operateLog);
				}
				return list;
			}

		});
		return operateLogList;
	}
}
