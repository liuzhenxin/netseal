package cn.com.infosec.netseal.common.dao.idCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.entity.po.IDCache;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class IDCacheDaoImpl extends BaseDao {

	/**
	 * 增加
	 * 
	 * @param idCache
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void insertIDCache(IDCache idCache)  {
		String sql = "insert into SEAL_ID_CACHE(TABLE_NAME,LAST_ID,MAC)values(?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { idCache.getTableName(), idCache.getLastId(), idCache.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteIDCache(long id) {
		String sql = "delete from SEAL_ID_CACHE where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public int updateIDCache(IDCache idCache, long lastId) {
		String sql = "update SEAL_ID_CACHE set LAST_ID=?,MAC=? where TABLE_NAME=? and LAST_ID=?";
		return getJdbcTemplate().update(sql, new Object[] { idCache.getLastId(), idCache.calMac(), idCache.getTableName(), lastId });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<IDCache> getIDCache() {
		String sql = "select TABLE_NAME,LAST_ID from SEAL_ID_CACHE";
		return getJdbcTemplate().query(sql, new IDCacheRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param tableName
	 * @return
	 */
	public IDCache getIDCache(String tableName) {
		String sql = "select TABLE_NAME,LAST_ID,MAC from SEAL_ID_CACHE where TABLE_NAME=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new IDCacheRowMapper(), tableName);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class IDCacheRowMapper implements RowMapper<IDCache> {
		public IDCache mapRow(ResultSet rs, int rowNum) throws SQLException {
			IDCache IDCache = new IDCache();
			IDCache.setTableName(rs.getString("TABLE_NAME"));
			IDCache.setLastId(ObjectUtil.toLong(rs.getObject("LAST_ID")));
			IDCache.setMac(rs.getString("MAC"));
			return IDCache;
		}
	}
}
