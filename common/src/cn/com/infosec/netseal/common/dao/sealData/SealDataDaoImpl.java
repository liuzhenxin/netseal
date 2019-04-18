package cn.com.infosec.netseal.common.dao.sealData;

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
import cn.com.infosec.netseal.common.entity.po.SealData;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class SealDataDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param sys
	 * @throws Exception 
	 */
	public Long insertSealData(final SealData sealData) {
		sealData.setId(idGenerator.getNewId(Constants.TABLE_SEAL_SEAL_DATA));

		String sql = "insert into SEAL_SEAL_DATA(ID,DATA,GENERATE_TIME,MAC,UPDATE_TIME) values(?,?,?,?,?)";

		final LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
				ps.setLong(1, sealData.getId());
				lobCreator.setBlobAsBytes(ps, 2, sealData.getData());
				ps.setLong(3, sealData.getGenerateTime());
				try {
					ps.setString(4, sealData.calMac());
				} catch (Exception e) {
					e.printStackTrace();
				}
				ps.setLong(5, sealData.getUpdateTime());
			}
		});
		return sealData.getId();
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteSealData(Long id) {
		String sql = "delete from SEAL_SEAL_DATA where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateSealData(Long oldId, SealData sd) {
		String sql = "update SEAL_SEAL_DATA set ID=?,UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { sd.getId(), sd.getUpdateTime(), sd.calMac(), oldId });
	}
	
	/**
	 * 改数据
	 * 
	 * @param sys
	 * @return
	 */
	public int updateSealData(SealData sd) {
		String sql = "update SEAL_SEAL_DATA set DATA=?,UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { sd.getData(), sd.getUpdateTime(), sd.calMac(), sd.getId() });
	}
	

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public SealData getSealData(Long id) {
		String sql = "select ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_SEAL_DATA where ID=?";

		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper<SealData>() {
				public SealData mapRow(ResultSet rs, int rowNum) throws SQLException {
					SealData sealData = new SealData();
					sealData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					sealData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					sealData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					sealData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					sealData.setMac(rs.getString("MAC"));
					return sealData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
