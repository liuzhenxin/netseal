package cn.com.infosec.netseal.common.dao.manageLog;

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
import cn.com.infosec.netseal.common.entity.po.ManageLog;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class ManageLogDaoImpl extends BaseDao {

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
	public void insertManageLog(ManageLog manageLog) {
		manageLog.setId(idGenerator.getNewId(Constants.TABLE_SEAL_MANAGE_LOG));

		String sql = "insert into SEAL_MANAGE_LOG(ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC)values(?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { manageLog.getId(), manageLog.getAccount(), manageLog.getClientHost(), manageLog.getOpType(), manageLog.getGenerateTime(),
				manageLog.getUpdateTime(), manageLog.getReturnCode(), manageLog.getErrMsg(), manageLog.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteManageLog(Long id) {
		String sql = "delete from SEAL_MANAGE_LOG where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateManageLog(ManageLog manageLog) {
		String sql = "update SEAL_MANAGE_LOG set ACCOUNT=?,CLIENT_HOST=?,OPTYPE=?,GENERATE_TIME=?,UPDATE_TIME=?,RETURN_CODE=?,ERR_MSG=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { manageLog.getAccount(), manageLog.getClientHost(), manageLog.getOpType(), manageLog.getGenerateTime(), manageLog.getUpdateTime(),
				manageLog.getReturnCode(), manageLog.getErrMsg(), manageLog.getMac(), manageLog.getId() });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<ManageLog> getManageLogs() {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC from SEAL_MANAGE_LOG";
		return getJdbcTemplate().query(sql, new ManageLogRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public ManageLog getManageLog(Long id) {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC from SEAL_MANAGE_LOG where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new ManageLogRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class ManageLogRowMapper implements RowMapper<ManageLog> {
		public ManageLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			ManageLog manageLog = new ManageLog();
			manageLog.setId(ObjectUtil.toLong(rs.getObject("ID")));
			manageLog.setAccount(rs.getString("ACCOUNT"));
			manageLog.setClientHost(rs.getString("CLIENT_HOST"));
			manageLog.setOpType(rs.getString("OPTYPE"));
			manageLog.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			manageLog.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			manageLog.setReturnCode(rs.getString("RETURN_CODE"));
			manageLog.setErrMsg(rs.getString("ERR_MSG"));
			manageLog.setMac(rs.getString("MAC"));
			return manageLog;
		}
	}

	public Integer searchTotal(ManageLog manageLog, long timeStart, long timeEnd) {
		String sql = "select count(1) from SEAL_MANAGE_LOG where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(manageLog.getAccount())) {
			sql += " and ACCOUNT like ?";
			valueList.add(manageLog.getAccount().trim() + "%");
		}
		if (StringUtil.isNotBlank(manageLog.getOpType())) {
			sql += " and OPTYPE like ?";
			valueList.add(manageLog.getOpType().trim() + "%");
		}
		if (StringUtil.isNotBlank(manageLog.getClientHost())) {
			sql += " and CLIENT_HOST like ?";
			valueList.add(manageLog.getClientHost().trim() + "%");
		}
		if (StringUtil.isNotBlank(manageLog.getOpTimeStart())) {
			sql += " and GENERATE_TIME >= ?";
			valueList.add(timeStart);
		}
		if (StringUtil.isNotBlank(manageLog.getOpTimeEnd())) {
			sql += " and GENERATE_TIME <= ?";
			valueList.add(timeEnd);
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

	public List<ManageLog> searchManageLog(ManageLog manageLog, final int start, final int end, long timeStart, long timeEnd) {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,RETURN_CODE,ERR_MSG,MAC from SEAL_MANAGE_LOG where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(manageLog.getAccount())) {
			sql += " and ACCOUNT like ?";
			valueList.add(manageLog.getAccount().trim() + "%");
		}
		if (StringUtil.isNotBlank(manageLog.getOpType())) {
			sql += " and OPTYPE like ?";
			valueList.add(manageLog.getOpType().trim() + "%");
		}
		if (StringUtil.isNotBlank(manageLog.getClientHost())) {
			sql += " and CLIENT_HOST like ?";
			valueList.add(manageLog.getClientHost().trim() + "%");
		}
		if (StringUtil.isNotBlank(manageLog.getOpTimeStart())) {
			sql += " and GENERATE_TIME >= ?";
			valueList.add(timeStart);
		}
		if (StringUtil.isNotBlank(manageLog.getOpTimeEnd())) {
			sql += " and GENERATE_TIME <= ?";
			valueList.add(timeEnd);
		}
		sql += " order by ID desc ";
		
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);

		List<ManageLog> manageLogList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<ManageLog>>() {
			@Override
			public List<ManageLog> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<ManageLog> list = new ArrayList<ManageLog>();
				while (rs.next()) {
					ManageLog manageLog = new ManageLog();
					manageLog.setId(ObjectUtil.toLong(rs.getObject("ID")));
					manageLog.setAccount(rs.getString("ACCOUNT"));
					manageLog.setClientHost(rs.getString("CLIENT_HOST"));
					manageLog.setOpType(rs.getString("OPTYPE"));
					manageLog.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					manageLog.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					manageLog.setReturnCode(rs.getString("RETURN_CODE"));
					manageLog.setErrMsg(rs.getString("ERR_MSG"));
					manageLog.setMac(rs.getString("MAC"));
					list.add(manageLog);
				}
				return list;
			}

		});
		return manageLogList;
	}
}
