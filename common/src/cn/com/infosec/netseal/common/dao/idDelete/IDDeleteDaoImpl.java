package cn.com.infosec.netseal.common.dao.idDelete;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.IDDelete;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class IDDeleteDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param IDDelete
	 * @throws Exception 
	 */
	public void insertIDDelete(IDDelete idDelete) throws  Exception {
		idDelete.setId(idGenerator.getNewId(Constants.TABLE_SEAL_ID_DELETE));
		String sql = "insert into SEAL_ID_DELETE(ID,TABLE_NAME,DELETE_ID,MAC,GENERATE_TIME,UPDATE_TIME)values(?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { idDelete.getId(), idDelete.getTableName(), idDelete.getDeleteId(), idDelete.calMac(), idDelete.getGenerateTime(), idDelete.getUpdateTime() });
	}

	/**
	 * 增
	 * 
	 * @param deleteId
	 * @param tableName
	 * @throws Exception 
	 */
	public void insertIDDelete(long deleteId, String tableName) throws Exception {
		Long currentTime = DateUtil.getCurrentTime();
		IDDelete idDelete = new IDDelete();
		idDelete.setDeleteId(deleteId);
		idDelete.setTableName(tableName);
		idDelete.setGenerateTime(currentTime);
		idDelete.setUpdateTime(currentTime);
		insertIDDelete(idDelete);
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteIDDelete(long id) {
		String sql = "delete from SEAL_ID_DELETE where ID=?";
		return getJdbcTemplate().update(sql, id);
	}
	
	
	/**
	 * 删 
	 * 
	 * @param time
	 * @return
	 */
	public int deleteIDDeleteByTime(long time) {
		String sql = "delete from SEAL_ID_DELETE where GENERATE_TIME < ? ";
		return getJdbcTemplate().update(sql, time);
	}
	
	
	

	/**
	 * 查列表
	 * 
	 * @return
	 */
	public List<IDDelete> getIDDeletes(long timeStamp, String tableName) {
		String sql = "select ID,TABLE_NAME,DELETE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_ID_DELETE where UPDATE_TIME>=? and TABLE_NAME=? order by UPDATE_TIME";
		return getJdbcTemplate().query(sql, new IDDeleteRowMapper(), timeStamp, tableName);
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public IDDelete getIDDelete(String id) {
		String sql = "select ID,TABLE_NAME,DELETE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_ID_DELETE where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new IDDeleteRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class IDDeleteRowMapper implements RowMapper<IDDelete> {
		public IDDelete mapRow(ResultSet rs, int rowNum) throws SQLException {
			IDDelete idDelete = new IDDelete();
			idDelete.setId(ObjectUtil.toLong(rs.getObject("ID")));
			idDelete.setTableName(rs.getString("TABLE_NAME"));
			idDelete.setDeleteId(ObjectUtil.toLong(rs.getObject("DELETE_ID")));
			idDelete.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			idDelete.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			idDelete.setMac(rs.getString("MAC"));
			return idDelete;
		}
	}
}
