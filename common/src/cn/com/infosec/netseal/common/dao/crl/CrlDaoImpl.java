package cn.com.infosec.netseal.common.dao.crl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Crl;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class CrlDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增加
	 * 
	 * @param crl
	 * @throws Exception 
	 */
	public void insertCrl(Crl crl) throws  Exception {
		crl.setId(idGenerator.getNewId(Constants.TABLE_SEAL_CRL));

		String sql = "insert into SEAL_CRL(ID,CERT_ID,MAC) values(?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { crl.getId(), crl.getCertId(), crl.calMac() });
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Crl getCrl(long id) {
		String sql = "select ID, CERT_ID,MAC from SEAL_CRL where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new CrlRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 清空表
	 * 
	 * @param id
	 * @return
	 */
	public int deleteCrl() {
		String sql = "delete table SEAL_CRL";
		return getJdbcTemplate().update(sql);
	}

	/**
	 * 批量增加
	 * 
	 * @param crls
	 */
	public void insertCrl(final Crl[] crls) {
		String sql = "insert into SEAL_CRL(ID,CERT_ID,MAC) values(?,?,?)";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public int getBatchSize() {
				return crls.length;
			}

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, crls[i].getId());
				ps.setBigDecimal(2, new BigDecimal(crls[i].getCertId()));
				ps.setString(3, crls[i].getMac());
			}
		});
	}

	private class CrlRowMapper implements RowMapper<Crl> {
		public Crl mapRow(ResultSet rs, int rowNum) throws SQLException {
			Crl crl = new Crl();
			crl.setId(ObjectUtil.toLong(rs.getObject("ID")));
			crl.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
			crl.setMac(rs.getString("MAC"));
			return crl;
		}
	}
}
