package cn.com.infosec.netseal.common.dao.certChain;

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
import cn.com.infosec.netseal.common.entity.po.CertChain;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class CertChainDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param printer
	 */
	public void insertCertChain(CertChain certChain)  {
		certChain.setId(idGenerator.getNewId(Constants.TABLE_SEAL_CERT_CHAIN));

		String sql = "insert into SEAL_CERT_CHAIN(ID,PID,CERT_DN,CERT_ISSUE_DN,CERT_PATH,GENERATE_TIME,MAC,CERT_DATA_ID,UPDATE_TIME,NOTBEFOR,NOTAFTER)values(?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { certChain.getId(), certChain.getPid(), certChain.getCertDn(), certChain.getCertIssueDn(), certChain.getCertPath(), certChain.getGenerateTime(),
				certChain.calMac(), certChain.getCertDataId(), certChain.getUpdateTime(),certChain.getNotBefor(),certChain.getNotAfter() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteCertChain(Long id) {
		String sql = "delete from SEAL_CERT_CHAIN where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateCertChain(CertChain certChain) {
		String sql = "update SEAL_CERT_CHAIN set PID=?,CERT_DN=?,CERT_ISSUE_DN=?,CERT_PATH=?,GENERATE_TIME=?,MAC=?,CERT_DATA_ID=?,UPDATE_TIME=?,NOTBEFOR=?,NOTAFTER=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { certChain.getPid(), certChain.getCertDn(), certChain.getCertIssueDn(), certChain.getCertPath(), certChain.getGenerateTime(),
				certChain.getMac(), certChain.getCertDataId(), certChain.getUpdateTime(), certChain.getId(),certChain.getNotBefor(),certChain.getNotAfter() });
	}

	/**
	 * 改
	 * 
	 * @param num
	 * @param isIssueCert
	 * @return
	 */
	public int updateCertPid(String certIssueDN, CertChain certChain) {
		String sql = "update SEAL_CERT_CHAIN set PID=?, UPDATE_TIME=?, MAC=? where CERT_ISSUE_DN=? and CERT_ISSUE_DN!=CERT_DN";
		return getJdbcTemplate().update(sql, new Object[] { certChain.getPid(), certChain.getUpdateTime(), certChain.calMac(), certIssueDN });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<CertChain> getCertChains() {
		String sql = "select ID,PID,CERT_DN,CERT_ISSUE_DN,CERT_PATH,CERT_DATA_ID,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT_CHAIN";
		return getJdbcTemplate().query(sql, new CertChainRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public CertChain getCertChain(Long id) {
		String sql = "select ID,PID,CERT_DN,CERT_ISSUE_DN,CERT_PATH,CERT_DATA_ID,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT_CHAIN where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new CertChainRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查单条
	 * 
	 * @param certDn
	 * @return
	 */
	public List<CertChain> getCertChain(String certDn) {
		String sql = "select ID,PID,CERT_DN,CERT_ISSUE_DN,CERT_PATH,CERT_DATA_ID,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT_CHAIN where CERT_DN=?";
		try {
			return getJdbcTemplate().query(sql, new CertChainRowMapper(), certDn);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查子证书
	 * 
	 * @param certDn
	 * @return
	 */
	public List<CertChain> getCerts(String certDn) {
		String sql = "select ID,PID,CERT_DN,CERT_ISSUE_DN,CERT_PATH,CERT_DATA_ID,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT_CHAIN where CERT_ISSUE_DN=? and CERT_DN !=CERT_ISSUE_DN";
		return getJdbcTemplate().query(sql, new CertChainRowMapper(), certDn);
	}

	private class CertChainRowMapper implements RowMapper<CertChain> {
		public CertChain mapRow(ResultSet rs, int rowNum) throws SQLException {
			CertChain certChain = new CertChain();
			certChain.setId(ObjectUtil.toLong(rs.getObject("ID")));
			certChain.setPid(ObjectUtil.toLong(rs.getObject("PID")));
			certChain.setCertDn(rs.getString("CERT_DN"));
			certChain.setCertIssueDn(rs.getString("CERT_ISSUE_DN"));
			certChain.setCertPath(rs.getString("CERT_PATH"));
			certChain.setCertDataId(ObjectUtil.toLong(rs.getObject("CERT_DATA_ID")));
			certChain.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			certChain.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			certChain.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			certChain.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			certChain.setMac(rs.getString("MAC"));
			return certChain;
		}
	}

	/**
	 * 分页查总条
	 * 
	 * @param certChain
	 * @return
	 */
	public int searchTotal(CertChain certChain) {
		String sql = "select count(1) from SEAL_CERT_CHAIN c where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(certChain.getCertDn())) {
			sql += " and c.CERT_DN like ? ";
			valueList.add(certChain.getCertDn().trim() + "%");
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

	/**
	 * 分页查全部
	 * 
	 * @param certChain
	 * @param start
	 * @param end
	 * @return
	 */
	public List<CertChain> searchByPage(CertChain certChain, final int start, final int end) {
		String sql = "select ID,PID,CERT_DN,CERT_ISSUE_DN,CERT_PATH,CERT_DATA_ID,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT_CHAIN c where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(certChain.getCertDn())) {
			sql += " and c.CERT_DN like ? ";
			valueList.add(certChain.getCertDn().trim() + "%");
		}
		sql += " order by ID desc ";
		
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<CertChain> certChainList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<CertChain>>() {
			@Override
			public List<CertChain> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<CertChain> list = new ArrayList<CertChain>();
				while (rs.next()) {
					CertChain certChain = new CertChain();
					certChain.setId(ObjectUtil.toLong(rs.getObject("ID")));
					certChain.setPid(ObjectUtil.toLong(rs.getObject("PID")));
					certChain.setCertDn(rs.getString("CERT_DN"));
					certChain.setCertIssueDn(rs.getString("CERT_ISSUE_DN"));
					certChain.setCertPath(rs.getString("CERT_PATH"));
					certChain.setCertDataId(ObjectUtil.toLong(rs.getObject("CERT_DATA_ID")));
					certChain.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
					certChain.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
					certChain.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					certChain.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					certChain.setMac(rs.getString("MAC"));
					list.add(certChain);
				}
				return list;
			}

		});
		return certChainList;
	}

}
