package cn.com.infosec.netseal.common.dao.csrData;

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
import cn.com.infosec.netseal.common.entity.po.CsrData;
import cn.com.infosec.netseal.common.entity.po.KeyData;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class CsrDataDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param keyData
	 * @return id
	 * @throws Exception 
	 */
	public Long insertCsrData(final CsrData CsrData)  {
		CsrData.setId(idGenerator.getNewId(Constants.TABLE_SEAL_CSR_DATA));

		String sql = "insert into SEAL_CSR_DATA(ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC) values(?,?,?,?,?)";
		final LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
				ps.setLong(1, CsrData.getId());
				lobCreator.setBlobAsBytes(ps, 2, CsrData.getData());
				ps.setLong(3, CsrData.getGenerateTime());
				ps.setLong(4, CsrData.getUpdateTime());
				try {
					ps.setString(5, CsrData.calMac());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return CsrData.getId();
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteCsrData(Long id) {
		String sql = "delete from SEAL_CSR_DATA where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public CsrData getCsrData(Long id) {
		String sql = "select ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CSR_DATA where ID=?";
		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper<CsrData>() {
				public CsrData mapRow(ResultSet rs, int rowNum) throws SQLException {
					CsrData CsrData = new CsrData();
					CsrData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					CsrData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					CsrData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					CsrData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					CsrData.setMac(rs.getString("MAC"));
					return CsrData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
