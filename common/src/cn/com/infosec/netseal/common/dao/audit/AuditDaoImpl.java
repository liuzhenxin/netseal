package cn.com.infosec.netseal.common.dao.audit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Audit;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class AuditDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param audit
	 */
	public void insertAudit(Audit audit) throws  Exception {
		audit.setId(idGenerator.getNewId(Constants.TABLE_SEAL_AUDIT));

		String sql = "insert into SEAL_AUDIT(ID,NAME,TEMPLATE_ID,NOTBEFOR,NOTAFTER,SYS_USER_ID,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,USEDLIMIT,REMARK,MAC)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { audit.getId(), audit.getName(), audit.getTemplateId(), audit.getNotBefor(), audit.getNotAfter(), audit.getSysUserId(), audit.getUserId(),
				audit.getCertId(), audit.getGenerateTime(), audit.getUpdateTime(), audit.getUsedLimit(), audit.getRemark(), audit.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteAudit(Long id) {
		String sql = "delete from SEAL_AUDIT where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Audit> getAudits() {
		String sql = "select ID,NAME,TEMPLATE_ID,NOTBEFOR,NOTAFTER,SYS_USER_ID,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,USEDLIMIT,REMARK,MAC from SEAL_AUDIT";
		return getJdbcTemplate().query(sql, new AuditRowMapper());
	}

	/**
	 * 查列表
	 * 
	 * @param ids
	 * @return
	 */
	public List<Audit> getAudits(String ids) {
		String sql = "select ID,NAME,TEMPLATE_ID,NOTBEFOR,NOTAFTER,SYS_USER_ID,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,USEDLIMIT,REMARK,MAC from SEAL_AUDIT where ID in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", Arrays.asList(ids.split(Constants.SPLIT_1)));
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).query(sql, params, new AuditRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Audit getAudit(Long id) {
		String sql = "select ID,NAME,TEMPLATE_ID,NOTBEFOR,NOTAFTER,SYS_USER_ID,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,USEDLIMIT,REMARK,MAC from SEAL_AUDIT where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new AuditRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class AuditRowMapper implements RowMapper<Audit> {
		public Audit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Audit audit = new Audit();
			audit.setId(ObjectUtil.toLong(rs.getObject("ID")));
			audit.setName(rs.getString("NAME"));
			audit.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));
			audit.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			audit.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			audit.setSysUserId(ObjectUtil.toLong(rs.getObject("SYS_USER_ID")));
			audit.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
			audit.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
			audit.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			audit.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			audit.setUsedLimit(ObjectUtil.toInteger(rs.getObject("USEDLIMIT")));
			audit.setRemark(rs.getString("REMARK"));
			audit.setMac(rs.getString("MAC"));
			return audit;
		}
	}

	public Integer searchTotal(Audit audit) {
		String sql = "select count(1) from SEAL_AUDIT where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(audit.getName())) {
			sql += " and NAME =? ";
			valueList.add(audit.getName());
		}

		Long userId = audit.getUserId();
		if (userId != null) {
			sql += " and USER_ID =? ";
			valueList.add(userId);
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

	public List<Audit> searchByPage(Audit audit, final int start, final int end) {
		String sql = "select ID,NAME,TEMPLATE_ID,NOTBEFOR,NOTAFTER,SYS_USER_ID,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,USEDLIMIT,REMARK,MAC from SEAL_AUDIT where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(audit.getName())) {
			sql += " and NAME =? ";
			valueList.add(audit.getName());
		}
		sql += " order by ID desc";
		
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);

		List<Audit> auditList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<Audit>>() {
			@Override
			public List<Audit> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<Audit> list = new ArrayList<Audit>();
				while (rs.next()) {
					Audit audit = new Audit();
					audit.setId(ObjectUtil.toLong(rs.getObject("ID")));
					audit.setName(rs.getString("NAME"));
					audit.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));
					audit.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
					audit.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
					audit.setSysUserId(ObjectUtil.toLong(rs.getObject("SYS_USER_ID")));
					audit.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
					audit.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
					audit.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					audit.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					audit.setUsedLimit(ObjectUtil.toInteger(rs.getObject("USEDLIMIT")));
					audit.setRemark(rs.getString("REMARK"));
					audit.setMac(rs.getString("MAC"));
				}
				return list;
			}

		});

		return auditList;
	}
}
