package cn.com.infosec.netseal.common.dao.key;

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
import cn.com.infosec.netseal.common.entity.po.Key;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class KeyDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增加
	 * 
	 * @param key
	 * @throws Exception 
	 */
	public void insertKey(Key key)  {
		key.setId(idGenerator.getNewId(Constants.TABLE_SEAL_KEY));

		String sql = "insert into SEAL_KEY(ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,STATUS,CSR_DATA_ID,HSM_ID,MAC)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { key.getId(), key.getCertDn(), key.getCertSn(), key.getCertUsage(), key.getCertIssueDn(), key.getCsrPath(), key.getCertPath(), key.getKeyPath(), key.getBackupPath(), key.getNotBefor(), key.getNotAfter(),
				key.getGenerateTime(), key.getUpdateTime(), key.getCertDataId(), key.getKeyDataId(), key.getKeyMode(), key.getKeyPwd(), key.getStatus(),key.getCsrDataId(),key.getHsmId(),key.calMac()});
	}

	/**
	 * 增加
	 * 
	 * @param pfx
	 * @throws Exception 
	 */
	public void insertPfxKey(Key key)  {
		key.setId(idGenerator.getNewId(Constants.TABLE_SEAL_KEY));

		String sql = "insert into SEAL_KEY(ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,MAC,STATUS,CSR_DATA_ID,HSM_ID)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { key.getId(), key.getCertDn(),key.getCertSn(), key.getCertUsage(), key.getCertIssueDn(), key.getCsrPath(), key.getCertPath(), key.getKeyPath(), key.getBackupPath(), key.getNotBefor(), key.getNotAfter(),
				key.getGenerateTime(), key.getUpdateTime(), key.getCertDataId(), key.getKeyDataId(), key.getKeyMode(), key.getKeyPwd(), key.calMac(), key.getStatus(),key.getCsrDataId(),key.getHsmId() });
	}
	
	/**
	 * 增加 加密卡
	 * 
	 * @param key
	 * @throws Exception 
	 */
	public void insertKeyCard(Key key){
		key.setId(idGenerator.getNewId(Constants.TABLE_SEAL_KEY));
		
		String sql = "insert into SEAL_KEY(ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,STATUS,CSR_DATA_ID,HSM_ID,MAC)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { key.getId(), key.getCertDn(), key.getCertSn(), key.getCertUsage(), key.getCertIssueDn(), key.getCsrPath(), key.getCertPath(), key.getKeyPath(), key.getBackupPath(), key.getNotBefor(), key.getNotAfter(),
				key.getGenerateTime(), key.getUpdateTime(), key.getCertDataId(), key.getKeyDataId(), key.getKeyMode(), key.getKeyPwd(), key.getStatus(),key.getCsrDataId(),key.getHsmId(),key.calMac()});
	}
	
	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteKey(Long id) {
		String sql = "delete from SEAL_KEY where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 * @throws Exception 
	 */
	public int updateKey(Key key)  {
		String sql = "update SEAL_KEY set CERT_DN=?,CERT_SN=?,CERT_USAGE=?,CERT_ISSUE_DN=?,CSR_PATH=?,CERT_PATH=?,KEY_PATH=?,BACKUP_PATH=?,NOTBEFOR=?,NOTAFTER=?,CERT_DATA_ID=?,KEY_DATA_ID=?,KEY_MODE=?,KEY_PWD=?,GENERATE_TIME=?,UPDATE_TIME=?,MAC=?,STATUS=?,CSR_DATA_ID =?,HSM_ID =? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { key.getCertDn(), key.getCertSn(), key.getCertUsage(), key.getCertIssueDn(), key.getCsrPath(), key.getCertPath(), key.getKeyPath(), key.getBackupPath(),key.getNotBefor(), key.getNotAfter(),
				key.getCertDataId(), key.getKeyDataId(), key.getKeyMode(), key.getKeyPwd(), key.getGenerateTime(), key.getUpdateTime(), key.calMac(), key.getStatus(), key.getCsrDataId(),key.getHsmId(),key.getId()});
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Key> getKeys() {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,GENERATE_TIME,UPDATE_TIME,STATUS,MAC,CSR_DATA_ID,HSM_ID from SEAL_KEY order by GENERATE_TIME asc";
		return getJdbcTemplate().query(sql, new KeyRowMapper());
	}

	/**
	 * 查询所有已导入的证书
	 * @param certDN
	 * @return
	 */
	public List<Key> getCheckKey(Key key) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,GENERATE_TIME,UPDATE_TIME,STATUS,MAC,CSR_DATA_ID,HSM_ID from SEAL_KEY";
		sql += " where 1=1 ";
		List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(key.getCertDn())) {
			sql += " and CERT_DN != ?";
			valueList.add(key.getCertDn());
		}
		if (key.getCertUsage() != null) {
			sql += " and CERT_USAGE = ? or CERT_USAGE = ?";
			valueList.add(key.getCertUsage());
			valueList.add(Constants.USAGE_SIGN_ENC);
		}
		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new KeyRowMapper());
		
	}
	
	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Key getKey(Long id) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,GENERATE_TIME,UPDATE_TIME,STATUS,MAC,CSR_DATA_ID,HSM_ID from SEAL_KEY where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new KeyRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 条件查询密钥集合
	 *  
	 * @param key
	 * @return
	 */
	public List<Key> getkeylList(Key key) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,GENERATE_TIME,UPDATE_TIME,STATUS,MAC,CSR_DATA_ID,HSM_ID from SEAL_KEY";
		sql += " where 1=1 ";
		List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(key.getCertDn())) {
			sql += " and CERT_DN = ?";
			valueList.add(key.getCertDn());
		}
		if (StringUtil.isNotBlank(key.getKeyMode())) {
			sql += " and KEY_MODE = ?";
			valueList.add(key.getKeyMode());
		}
		if (key.getCertUsage() != null) {
			sql += " and (CERT_USAGE = ? or CERT_USAGE = ?)";
			valueList.add(key.getCertUsage());
			valueList.add(Constants.USAGE_SIGN_ENC);
		}
		sql += " order by ID desc ";
		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new KeyRowMapper());
		
	}
	/**所有签名证书
	 * @return
	 */
	public List<Key> getKeySign() {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,GENERATE_TIME,UPDATE_TIME,STATUS,MAC,CSR_DATA_ID,HSM_ID from SEAL_KEY";
		List<Object> valueList = new ArrayList<Object>();

		sql += " where CERT_USAGE = ? or CERT_USAGE = ?";
		valueList.add(Constants.USAGE_SIGNATURE);
		valueList.add(Constants.USAGE_SIGN_ENC);

		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new KeyRowMapper());
		
	}
	
	/**
	 * 分页查总条
	 * 
	 * @param certChain
	 * @return
	 */
	public int searchTotal(Key key) {
		String sql = "select count(1) from SEAL_KEY where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(key.getKeyMode())) {
			sql += " and KEY_MODE = ? ";
			valueList.add( key.getKeyMode() );
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
	public List<Key> searchByPage(Key key, final int start, final int end) {
		String sql = "select ID,CERT_DN,CERT_SN,CERT_USAGE,CERT_ISSUE_DN,CSR_PATH,CERT_PATH,KEY_PATH,BACKUP_PATH,NOTBEFOR,NOTAFTER,CERT_DATA_ID,KEY_DATA_ID,KEY_MODE,KEY_PWD,GENERATE_TIME,UPDATE_TIME,STATUS,MAC,CSR_DATA_ID,HSM_ID from SEAL_KEY where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		
		if (StringUtil.isNotBlank(key.getKeyMode())) {
			sql += " and KEY_MODE = ? ";
			valueList.add( key.getKeyMode() );
		}
		
		sql += " order by ID desc ";
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<Key> certChainList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<Key>>() {
			@Override
			public List<Key> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<Key> list = new ArrayList<Key>();
				while (rs.next()) {
					Key key = new Key();
					key.setId(ObjectUtil.toLong(rs.getObject("ID")));
					key.setCertDn(rs.getString("CERT_DN"));
					key.setCertSn(rs.getString("CERT_SN"));
					key.setCertUsage(ObjectUtil.toInteger(rs.getObject("CERT_USAGE")));
					key.setCertIssueDn(rs.getString("CERT_ISSUE_DN"));
					key.setCsrPath(rs.getString("CSR_PATH"));
					key.setCertPath(rs.getString("CERT_PATH"));
					key.setKeyPath(rs.getString("KEY_PATH"));
					key.setBackupPath(rs.getString("BACKUP_PATH"));
					key.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
					key.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
					key.setCertDataId(ObjectUtil.toLong(rs.getObject("CERT_DATA_ID")));
					key.setKeyDataId(ObjectUtil.toLong(rs.getObject("KEY_DATA_ID")));
					key.setKeyMode(rs.getString("KEY_MODE"));
					key.setKeyPwd(rs.getString("KEY_PWD"));
					key.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					key.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					key.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
					key.setMac(rs.getString("MAC"));
					key.setCsrDataId(ObjectUtil.toLong(rs.getObject("CSR_DATA_ID")));
					key.setHsmId(ObjectUtil.toInteger(rs.getObject("HSM_ID")));
					list.add(key);
				}
				return list;
			}

		});
		return certChainList;
	}

	private class KeyRowMapper implements RowMapper<Key> {
		public Key mapRow(ResultSet rs, int rowNum) throws SQLException {
			Key key = new Key();
			key.setId(ObjectUtil.toLong(rs.getObject("ID")));
			key.setCertDn(rs.getString("CERT_DN"));
			key.setCertSn(rs.getString("CERT_SN"));
			key.setCertUsage(ObjectUtil.toInteger(rs.getObject("CERT_USAGE")));
			key.setCertIssueDn(rs.getString("CERT_ISSUE_DN"));
			key.setCsrPath(rs.getString("CSR_PATH"));
			key.setCertPath(rs.getString("CERT_PATH"));
			key.setKeyPath(rs.getString("KEY_PATH"));
			key.setBackupPath(rs.getString("BACKUP_PATH"));
			key.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			key.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			key.setCertDataId(ObjectUtil.toLong(rs.getObject("CERT_DATA_ID")));
			key.setKeyDataId(ObjectUtil.toLong(rs.getObject("KEY_DATA_ID")));
			key.setKeyMode(rs.getString("KEY_MODE"));
			key.setKeyPwd(rs.getString("KEY_PWD"));
			key.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			key.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			key.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			key.setMac(rs.getString("MAC"));
			key.setCsrDataId(ObjectUtil.toLong(rs.getObject("CSR_DATA_ID")));
			key.setHsmId(ObjectUtil.toInteger(rs.getObject("HSM_ID")));
			return key;
		}
	}
}
