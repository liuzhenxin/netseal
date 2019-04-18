package cn.com.infosec.netseal.common.dao.certData;

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
import cn.com.infosec.netseal.common.entity.po.CertData;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;


@Repository
public class CertDataDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param certData
	 * @return id
	 */
	public Long insertCertData(final CertData certData) {
		certData.setId(idGenerator.getNewId(Constants.TABLE_SEAL_CERT_DATA));
		String sql = "insert into SEAL_CERT_DATA(ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC) values(?,?,?,?,?)";

		final LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
				ps.setLong(1, certData.getId());
				lobCreator.setBlobAsBytes(ps, 2, certData.getData());
				ps.setLong(3, certData.getGenerateTime());
				ps.setLong(4, certData.getUpdateTime());
				try {
					ps.setString(5, certData.calMac());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return certData.getId();
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteCertData(Long id) {
		String sql = "delete from SEAL_CERT_DATA where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateCertData(final CertData certData) {
		String sql = "update SEAL_CERT_DATA set UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { certData.getUpdateTime(), certData.getMac(), certData.getId() });
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public CertData getCertData(Long id) {
		String sql = "select ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT_DATA where ID=?";

		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper<CertData>() {
				public CertData mapRow(ResultSet rs, int rowNum) throws SQLException {
					CertData certData = new CertData();
					certData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					certData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					certData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					certData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					certData.setMac(rs.getString("MAC"));
					return certData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
