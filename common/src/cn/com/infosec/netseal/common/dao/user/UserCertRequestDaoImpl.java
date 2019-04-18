package cn.com.infosec.netseal.common.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.UserCertRequest;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class UserCertRequestDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param userCertRequest
	 * @throws Exception 
	 */
	public void insertUserCertRequest(UserCertRequest userCertRequest)  {
		userCertRequest.setId(idGenerator.getNewId(Constants.TABLE_SEAL_USER_CERT_REQUEST));

		String sql = "insert into SEAL_USER_CERT_REQUEST(ID,USER_ID,CERT_TYPE,CERT_TEMPLATE,CERT_DN,USER_UUID,CERT_REFNO,CERT_AUTHCODE,STATUS,VALIDITY_LEN,GENERATE_TIME,UPDATE_TIME,MAC)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql,
				new Object[] { userCertRequest.getId(), userCertRequest.getUserId(), userCertRequest.getCertType(), userCertRequest.getCertTemplate(), userCertRequest.getCertDn(), userCertRequest.getUserUuid(),
				userCertRequest.getCertRefno(), userCertRequest.getCertAuthCode(), userCertRequest.getStatus(), userCertRequest.getValidityLen(), userCertRequest.getGenerateTime(), userCertRequest.getUpdateTime(), userCertRequest.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteUserCertRequest(Long id) {
		String sql = "delete from SEAL_USER_CERT_REQUEST where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 修改
	 * 
	 * @param userCertRequest
	 * @return
	 */
	public int updateUserCertRequest(UserCertRequest userCertRequest) {
		String sql = "update SEAL_USER_CERT_REQUEST set USER_ID=?,CERT_TYPE=?,CERT_TEMPLATE=?,CERT_DN=?,USER_UUID=?,CERT_REFNO=?,CERT_AUTHCODE=?,STATUS=?,VALIDITY_LEN=?,GENERATE_TIME=?,UPDATE_TIME=?,MAC=?,UPDATE_TIME=? where ID=?";
		return getJdbcTemplate().update(sql,
				new Object[] { userCertRequest.getUserId(), userCertRequest.getCertType(), userCertRequest.getCertTemplate(), userCertRequest.getCertDn(), userCertRequest.getUserUuid(),
				userCertRequest.getCertRefno(), userCertRequest.getCertAuthCode(), userCertRequest.getStatus(), userCertRequest.getValidityLen(), userCertRequest.getGenerateTime(), userCertRequest.getUpdateTime(), userCertRequest.calMac(), userCertRequest.getId() });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<UserCertRequest> userCertRequestlist() {
		String sql = "select ID,USER_ID,CERT_TYPE,CERT_TEMPLATE,CERT_DN,USER_UUID,CERT_REFNO,CERT_AUTHCODE,STATUS,VALIDITY_LEN,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER_CERT_REQUEST";
		return getJdbcTemplate().query(sql, new UserCertRequestRowMapper());
	}
	
	public List<UserCertRequest> userCertRequestList(UserCertRequest userCertRequest) {
		String sql = "select ID,USER_ID,CERT_TYPE,CERT_TEMPLATE,CERT_DN,USER_UUID,CERT_REFNO,CERT_AUTHCODE,STATUS,VALIDITY_LEN,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER_CERT_REQUEST where 1=1 ";
		List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(userCertRequest.getCertType())) {
			sql += " and CERT_TYPE=? ";
			valueList.add(userCertRequest.getCertType());
		}
		if (StringUtil.isNotBlank(userCertRequest.getCertTemplate())) {
			sql += " and CERT_TEMPLATE=? ";
			valueList.add(userCertRequest.getCertTemplate());
		}
		if (StringUtil.isNotBlank(userCertRequest.getCertDn())) {
			sql += " and CERT_DN=? ";
			valueList.add(userCertRequest.getCertDn());
		}

		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new UserCertRequestRowMapper());
	}
	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public UserCertRequest getUserCertRequest(Long id) {
		String sql = "select ID,USER_ID,CERT_TYPE,CERT_TEMPLATE,CERT_DN,USER_UUID,CERT_REFNO,CERT_AUTHCODE,STATUS,VALIDITY_LEN,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER_CERT_REQUEST where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new UserCertRequestRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class UserCertRequestRowMapper implements RowMapper<UserCertRequest> {
		public UserCertRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserCertRequest userCertRequest = new UserCertRequest();
			userCertRequest.setId(ObjectUtil.toLong(rs.getObject("ID")));
			userCertRequest.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
			userCertRequest.setCertType(rs.getString("CERT_TYPE"));
			userCertRequest.setCertTemplate(rs.getString("CERT_TEMPLATE"));
			userCertRequest.setCertDn(rs.getString("CERT_DN"));
			userCertRequest.setUserUuid(rs.getString("USER_UUID"));
			userCertRequest.setCertRefno(rs.getString("CERT_REFNO"));
			userCertRequest.setCertAuthCode(rs.getString("CERT_AUTHCODE"));
			userCertRequest.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			userCertRequest.setValidityLen(ObjectUtil.toInteger(rs.getObject("VALIDITY_LEN")));
			userCertRequest.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			userCertRequest.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			userCertRequest.setMac(rs.getString("MAC"));
			return userCertRequest;
		}
	}

}
