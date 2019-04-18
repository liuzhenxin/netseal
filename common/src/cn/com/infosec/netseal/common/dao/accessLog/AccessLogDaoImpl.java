package cn.com.infosec.netseal.common.dao.accessLog;

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
import cn.com.infosec.netseal.common.entity.po.AccessLog;
import cn.com.infosec.netseal.common.entity.po.AccessLogCount;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class AccessLogDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param sys
	 */
	public void insertAccessLog(AccessLog accessLog) {
		accessLog.setId(idGenerator.getNewId(Constants.TABLE_SEAL_ACCESS_LOG));

		String sql = "insert into SEAL_ACCESS_LOG(ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,SEAL_ID,RETURN_CODE,ERR_MSG,MAC) values(?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { accessLog.getId(), accessLog.getAccount(), accessLog.getClientHost(), accessLog.getOptype(), accessLog.getGenerateTime(),
				accessLog.getUpdateTime(), accessLog.getSealId(), accessLog.getReturnCode(), accessLog.getErrMsg(), accessLog.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteAccessLog(Long id) {
		String sql = "delete from SEAL_ACCESS_LOG where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateAccessLog(AccessLog accessLog) {
		String sql = "update SEAL_ACCESS_LOG set ACCOUNT=?,CLIENT_HOST=?,OPTYPE=?,GENERATE_TIME=?,UPDATE_TIME=?,SEAL_ID=?,RETURN_CODE=?,ERR_MSG=?,MAC=?, where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { accessLog.getAccount(), accessLog.getClientHost(), accessLog.getOptype(), accessLog.getGenerateTime(), accessLog.getUpdateTime(),
				accessLog.getSealId(), accessLog.getReturnCode(), accessLog.getErrMsg(), accessLog.getMac(), accessLog.getId() });
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public AccessLog getAccessLog(Long id) {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,SEAL_ID,RETURN_CODE,ERR_MSG,MAC from SEAL_ACCESS_LOG where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new AccessLogRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<AccessLog> getAccessLogs() {
		String sql = "select ID,ACCOUNT,CLIENT_HOST,OPTYPE,GENERATE_TIME,UPDATE_TIME,SEAL_ID,RETURN_CODE,ERR_MSG,MAC from SEAL_ACCESS_LOG";
		return getJdbcTemplate().query(sql, new AccessLogRowMapper());
	}

	/**
	 * 分页
	 * 
	 * @param accessLog
	 * @return
	 * @throws Exception
	 */
	public Integer searchTotal(AccessLog accessLog) {
		String sql = "select count(1) from SEAL_ACCESS_LOG where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(accessLog.getOptype())) {
			sql += " and OPTYPE like ?";
			valueList.add(accessLog.getOptype().trim() + "%");
		}
		if (StringUtil.isNotBlank(accessLog.getClientHost())) {
			sql += " and CLIENT_HOST like ?";
			valueList.add(accessLog.getClientHost().trim() + "%");
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

	public List<AccessLog> searchAccessLog(AccessLog accessLog, final int start, final int end) {
		String sql = "select a.ID,a.ACCOUNT,a.CLIENT_HOST,a.OPTYPE,a.GENERATE_TIME,a.UPDATE_TIME,a.SEAL_ID,a.RETURN_CODE,a.ERR_MSG,a.MAC from SEAL_ACCESS_LOG a where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(accessLog.getOptype())) {
			sql += " and OPTYPE like ?";
			valueList.add(accessLog.getOptype().trim() + "%");
		}
		if (StringUtil.isNotBlank(accessLog.getClientHost())) {
			sql += " and CLIENT_HOST like ?";
			valueList.add(accessLog.getClientHost().trim() + "%");
		}
		sql += " order by a.ID desc";

		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<AccessLog> accessLogList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<AccessLog>>() {
			@Override
			public List<AccessLog> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<AccessLog> list = new ArrayList<AccessLog>();
				while (rs.next()) {
					AccessLog accessLog = new AccessLog();
					accessLog.setId(ObjectUtil.toLong(rs.getObject("ID")));
					accessLog.setAccount(rs.getString("ACCOUNT"));
					accessLog.setClientHost(rs.getString("CLIENT_HOST"));
					accessLog.setOptype(rs.getString("OPTYPE"));
					accessLog.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					accessLog.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					accessLog.setSealId(ObjectUtil.toLong(rs.getObject("SEAL_ID")));
					accessLog.setReturnCode(rs.getString("RETURN_CODE"));
					accessLog.setErrMsg(rs.getString("ERR_MSG"));
					accessLog.setMac(rs.getString("MAC"));

					list.add(accessLog);
				}
				return list;
			}

		});
		return accessLogList;
	}

	private class AccessLogRowMapper implements RowMapper<AccessLog> {
		public AccessLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			AccessLog accessLog = new AccessLog();
			accessLog.setId(ObjectUtil.toLong(rs.getObject("ID")));
			accessLog.setClientHost(rs.getString("CLIENT_HOST"));
			accessLog.setOptype(rs.getString("OPTYPE"));
			accessLog.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			accessLog.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			accessLog.setSealId(ObjectUtil.toLong(rs.getObject("SEAL_ID")));
			accessLog.setReturnCode(rs.getString("RETURN_CODE"));
			accessLog.setErrMsg(rs.getString("ERR_MSG"));
			accessLog.setMac(rs.getString("MAC"));
			return accessLog;
		}
	}
	
	/**
	 * 按操作类型查询
	 * @return
	 */
	public List<AccessLogCount> getAccessLogCount(){
		String sql = "select count(1) as counts , t.OPTYPE  as optype from SEAL_ACCESS_LOG t group by t.OPTYPE";
		try {
			return getJdbcTemplate().query(sql, new RowMapper<AccessLogCount>() {
				public  AccessLogCount  mapRow(ResultSet rs,int rowNum) throws SQLException {
					AccessLogCount ac=new AccessLogCount();
					ac.setCount(ObjectUtil.toInteger(rs.getObject("counts")));
					ac.setOptype(rs.getString("optype"));
					return ac;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
}
