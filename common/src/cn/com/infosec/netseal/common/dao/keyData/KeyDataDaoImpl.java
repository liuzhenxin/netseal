package cn.com.infosec.netseal.common.dao.keyData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.KeyData;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class KeyDataDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param keyData
	 * @return id
	 * @throws Exception 
	 */
	public Long insertKeyData(final KeyData keyData)  {
		keyData.setId(idGenerator.getNewId(Constants.TABLE_SEAL_KEY_DATA));

		String sql = "insert into SEAL_KEY_DATA(ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC) values(?,?,?,?,?)";
		final LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
				ps.setLong(1, keyData.getId());
				lobCreator.setBlobAsBytes(ps, 2, keyData.getData());
				ps.setLong(3, keyData.getGenerateTime());
				ps.setLong(4, keyData.getUpdateTime());
				try {
					ps.setString(5, keyData.calMac());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return keyData.getId();
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteKeyData(Long id) {
		String sql = "delete from SEAL_KEY_DATA where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public KeyData getKeyData(Long id) {
		String sql = "select ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_KEY_DATA where ID=?";
		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper<KeyData>() {
				public KeyData mapRow(ResultSet rs, int rowNum) throws SQLException {
					KeyData keyData = new KeyData();
					keyData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					keyData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					keyData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					keyData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					keyData.setMac(rs.getString("MAC"));
					return keyData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
