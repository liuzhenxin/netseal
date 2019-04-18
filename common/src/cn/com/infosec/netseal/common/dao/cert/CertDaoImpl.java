package cn.com.infosec.netseal.common.dao.cert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class CertDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param cert
	 */
	public void insertCert(final Cert cert) {
		cert.setId(idGenerator.getNewId(Constants.TABLE_SEAL_CERT));

		String sql = "insert into SEAL_CERT(ID,CERT_DN,CERT_SN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,MAC,CERT_DATA_ID,GENERATE_TIME,UPDATE_TIME,USER_ID,SYS_USER_ID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { cert.getId(), cert.getCertDn(), cert.getCertSn(), cert.getCertIssueDn(), cert.getCertUsage(), cert.getCertPath(), cert.getNotBefor(),
				cert.getNotAfter(), cert.calMac(), cert.getCertDataId(), cert.getGenerateTime(), cert.getUpdateTime(), cert.getUserId(), cert.getSysUserId() });

	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteCert(Long id) {
		String sql = "delete from SEAL_CERT where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Cert> getCerts() {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT";
		return getJdbcTemplate().query(sql, new CertRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Cert getCert(Long id) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new CertRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @param certUsage
	 * @return
	 */
	public Cert getCert(Long id, Integer certUsage) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where ID=? and CERT_USAGE=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id, certUsage }, new CertRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查列表
	 * 
	 * @param ids
	 * @return
	 */
	public List<Cert> getCerts(String ids) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where ID in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", Arrays.asList(ids.split(Constants.SPLIT_1)));
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).query(sql, params, new CertRowMapper());
	}

	/**
	 * 查列表
	 * 
	 * @param ids
	 * @param certUsage
	 * @return
	 */
	public List<Cert> getCerts(String ids, Integer certUsage) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where ID in (:ids) and CERT_USAGE= certUsage";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", Arrays.asList(ids.split(Constants.SPLIT_1)));
		params.put("certUsage", certUsage);
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).query(sql, params, new CertRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param certDn
	 * @return
	 */
	public List<Cert> getCert(String certDn) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where CERT_DN=?";
		try {
			return getJdbcTemplate().query(sql, new CertRowMapper(), certDn);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "cert DN in db is not unique, DN is " + certDn);
		}
	}
	/**
	 * 根据certDn查
	 * 
	 * @param certDn
	 * @return
	 */
	public List<Cert> getCertAll(String certDn) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where CERT_DN=?";
		try {
			return getJdbcTemplate().query(sql, new CertRowMapper(), certDn);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "cert DN in db is not unique, DN is " + certDn);
		}
	}
	/**
	 * 查单条
	 * 
	 * @param certDn
	 * @param certUsage
	 * @return
	 */
	public List<Cert> getCert(String certDn, Integer certUsage) {
		String sql = "select ID,CERT_SN,CERT_DN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_CERT where CERT_DN=? and CERT_USAGE=?";
		try {
			return getJdbcTemplate().query(sql, new CertRowMapper(), certDn, certUsage);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NetSealRuntimeException(ErrCode.CERT_NOT_UNIQUE_IN_DB, "cert DN in db is not unique, DN is " + certDn);
		}
	}

	private class CertRowMapper implements RowMapper<Cert> {
		public Cert mapRow(ResultSet rs, int rowNum) throws SQLException {
			Cert cert = new Cert();
			cert.setId(ObjectUtil.toLong(rs.getObject("ID")));
			cert.setCertSn(rs.getString("CERT_SN"));
			cert.setCertDn(rs.getString("CERT_DN"));
			cert.setCertIssueDn(rs.getString("CERT_ISSUE_DN"));
			cert.setCertUsage(ObjectUtil.toInteger(rs.getObject("CERT_USAGE")));
			cert.setCertPath(rs.getString("CERT_PATH"));
			cert.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			cert.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			cert.setCertDataId(ObjectUtil.toLong(rs.getObject("CERT_DATA_ID")));
			cert.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
			cert.setSysUserId(ObjectUtil.toLong(rs.getObject("SYS_USER_ID")));
			cert.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			cert.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			cert.setMac(rs.getString("MAC"));
			return cert;
		}

	}

	/**
	 * 获取用户下的证书列表
	 * 
	 * @param id
	 * @return
	 */
	public List<Cert> getCertByUserId(Long id) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,MAC,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME from SEAL_CERT where USER_ID=?";
		return getJdbcTemplate().query(sql, new CertRowMapper(), id);
	}

	/**
	 * 获取用户下的证书列表
	 * 
	 * @param id
	 * @param certUsage
	 * @return
	 */
	public List<Cert> getCertByUserId(Long id, Integer certUsage) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,MAC,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME from SEAL_CERT where USER_ID=? and CERT_USAGE=?";
		return getJdbcTemplate().query(sql, new CertRowMapper(), id, certUsage);
	}
	/**
	 * 获取用户下的证书列表(certDN)
	 * 
	 * @param id
	 * @param certUsage
	 * @return
	 */
	public List<Cert> getCertByDN(String certDN,Long userId) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,MAC,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME from SEAL_CERT where CERT_DN=? and USER_ID=?";
		return getJdbcTemplate().query(sql, new CertRowMapper(), certDN, userId);
	}

	/**
	 * 获取管理员下的证书列表
	 * 
	 * @param id
	 * @return
	 */
	public List<Cert> getCertBySysUserId(Long id) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_ISSUE_DN,CERT_USAGE,CERT_PATH,NOTBEFOR,NOTAFTER,MAC,CERT_DATA_ID,USER_ID,SYS_USER_ID,GENERATE_TIME,UPDATE_TIME from SEAL_CERT where SYS_USER_ID=?";
		return getJdbcTemplate().query(sql, new CertRowMapper(), id);
	}

	/**
	 * 按表主键标识查询总数
	 * 
	 * @param serverId
	 *            表前缀id
	 * @return
	 */
	public Integer searchTotal(final String serverId) {
		String sql = "select count(1) from SEAL_CERT where ID like ?";

		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setObject(1, serverId + "%");
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
	 * 管理员证书统计
	 * 
	 * @return
	 */
	public Integer sysUserCertCount() {
		String sql = "select count(1) from SEAL_CERT WHERE USER_ID = -1";

		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
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
	 * 签章人证书统计
	 * 
	 * @return
	 */
	public Integer userCertCount() {
		String sql = "select count(1) from SEAL_CERT WHERE SYS_USER_ID = -1";

		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
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

}
