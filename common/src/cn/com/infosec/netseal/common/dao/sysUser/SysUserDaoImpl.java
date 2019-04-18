package cn.com.infosec.netseal.common.dao.sysUser;

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
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Role;
import cn.com.infosec.netseal.common.entity.po.SysUser;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class SysUserDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param sysUser
	 * @throws Exception 
	 */
	public int insertSysUser(SysUser sysUser) throws  Exception {
		sysUser.setId(idGenerator.getNewId(Constants.TABLE_SEAL_SYS_USER));

		String sql = "insert into SEAL_SYS_USER(ID,ACCOUNT,NAME,PASSWORD,ROLE_ID,STATUS,FAILED_NUM,CHANGE_PASS,COMPANY_ID,TOKEN_SEED,GENERATE_TIME,UPDATE_TIME,MAC) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return getJdbcTemplate().update(sql, new Object[] { sysUser.getId(), sysUser.getAccount(), sysUser.getName(), sysUser.getPassword(), sysUser.getRoleId(), sysUser.getStatus(),
				sysUser.getFailedNum(), sysUser.getChangePass(), sysUser.getCompanyId(), sysUser.getTokenSeed(), sysUser.getGenerateTime(), sysUser.getUpdateTime(), sysUser.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteSysUser(Long id) {
		String sql = "delete from SEAL_SYS_USER where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sysUser
	 * @return
	 * @throws Exception 
	 */
	public int updateSysUser(SysUser sysUser) throws  Exception {
		String sql = "update SEAL_SYS_USER set NAME=?,PASSWORD=?,ROLE_ID=?,STATUS=?,FAILED_NUM=?,CHANGE_PASS=?,COMPANY_ID=?,TOKEN_SEED=?,UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { sysUser.getName(), sysUser.getPassword(), sysUser.getRoleId(), sysUser.getStatus(), sysUser.getFailedNum(), sysUser.getChangePass(),
				sysUser.getCompanyId(), sysUser.getTokenSeed(), sysUser.getUpdateTime(), sysUser.calMac(), sysUser.getId() });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<SysUser> getSysUsers() {
		String sql = "select ID,ACCOUNT,NAME,PASSWORD,ROLE_ID,STATUS,FAILED_NUM,CHANGE_PASS,COMPANY_ID,TOKEN_SEED,GENERATE_TIME,MAC from SEAL_SYS_USER";
		try {
			return getJdbcTemplate().query(sql, new SysUserRowMapper2());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查单条 ---整合---
	 * 
	 * @param sysUser
	 * @return
	 */
	public List<SysUser> getSysUser(SysUser sysUser) {
		String sql = "select ID,ACCOUNT,NAME,PASSWORD,ROLE_ID,STATUS,FAILED_NUM,CHANGE_PASS,COMPANY_ID,TOKEN_SEED,GENERATE_TIME,MAC from SEAL_SYS_USER ";
		sql += " where 1=1 ";
		List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(sysUser.getAccount())) {
			sql += " and ACCOUNT= ?";
			valueList.add(sysUser.getAccount());
		}
		if (StringUtil.isNotBlank(sysUser.getPassword())) {
			sql += " and PASSWORD=? ";
			valueList.add(sysUser.getPassword());
		}
		if (StringUtil.isNotBlank(sysUser.getName())) {
			sql += " and NAME=?";
			valueList.add(sysUser.getName());
		}
		if (sysUser.getCompanyId() != null) {
			sql += " and COMPANY_ID=?";
			valueList.add(sysUser.getCompanyId());
		}

		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new SysUserRowMapper2());
	}

	/**
	 * 查单条
	 * 
	 * @param account
	 * @return
	 */
	public List<SysUser> getSysUser(String account) {
		String sql = "select s.ID, s.ACCOUNT, s.NAME, s.PASSWORD, s.ROLE_ID, STATUS, s.FAILED_NUM, s.CHANGE_PASS, s.COMPANY_ID, s.TOKEN_SEED, s.GENERATE_TIME, s.UPDATE_TIME, s.MAC, "
				+ "c.ID c_ID,c.PID,c.NAME c_NAME,c.IS_PARENT,c.TREE_ID,c.REMARK c_remark,c.GENERATE_TIME c_GENERATE_TIME,c.UPDATE_TIME c_UPDATE_TIME,c.MAC c_MAC,  "
				+ "r.ID r_Id,r.NAME r_Name,r.MENU_ID,r.MAC r_Mac "
				+ "from SEAL_SYS_USER s left join SEAL_COMPANY c on c.ID = s.COMPANY_ID left join SEAL_ROLE r on r.ID = s.ROLE_ID where s.ACCOUNT = ?";
		try {
			return getJdbcTemplate().query(sql, new SysUserRowMapper(), new Object[] { account });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public SysUser getSysUser(Long id) {
		String sql = "select s.ID,s.ACCOUNT,s.NAME,s.PASSWORD,s.ROLE_ID,STATUS,s.FAILED_NUM,s.CHANGE_PASS,s.COMPANY_ID,s.TOKEN_SEED,s.GENERATE_TIME,s.UPDATE_TIME,s.MAC,"
				+ "c.ID c_ID,c.PID,c.NAME c_NAME,c.IS_PARENT,c.TREE_ID,c.REMARK c_remark,c.GENERATE_TIME c_GENERATE_TIME,c.UPDATE_TIME c_UPDATE_TIME,c.MAC c_MAC, "
				+ "r.ID r_Id,r.NAME r_Name,r.MENU_ID,r.MAC r_Mac " + "from SEAL_SYS_USER s left join SEAL_COMPANY c on c.ID = s.COMPANY_ID left join SEAL_ROLE r on r.ID = s.ROLE_ID where s.ID = ?";
		try {
			return getJdbcTemplate().queryForObject(sql, new SysUserRowMapper(), new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class SysUserRowMapper2 implements RowMapper<SysUser> {
		public SysUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			SysUser sysUser = new SysUser();
			sysUser.setId(ObjectUtil.toLong(rs.getObject("ID")));
			sysUser.setAccount(rs.getString("ACCOUNT"));
			sysUser.setName(rs.getString("NAME"));
			sysUser.setPassword(rs.getString("PASSWORD"));
			sysUser.setRoleId(ObjectUtil.toLong(rs.getObject("ROLE_ID")));
			sysUser.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			sysUser.setFailedNum(ObjectUtil.toInteger(rs.getObject("FAILED_NUM")));
			sysUser.setChangePass(ObjectUtil.toInteger(rs.getObject("CHANGE_PASS")));
			sysUser.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
			sysUser.setTokenSeed(rs.getString("TOKEN_SEED"));
			sysUser.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			sysUser.setMac(rs.getString("MAC"));

			return sysUser;
		}
	}

	private class SysUserRowMapper implements RowMapper<SysUser> {
		public SysUser mapRow(ResultSet rs, int rowNum) throws SQLException {
			SysUser sysUser = new SysUser();
			sysUser.setId(ObjectUtil.toLong(rs.getObject("ID")));
			sysUser.setAccount(rs.getString("ACCOUNT"));
			sysUser.setName(rs.getString("NAME"));
			sysUser.setPassword(rs.getString("PASSWORD"));
			sysUser.setRoleId(ObjectUtil.toLong(rs.getObject("ROLE_ID")));
			sysUser.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			sysUser.setFailedNum(ObjectUtil.toInteger(rs.getObject("FAILED_NUM")));
			sysUser.setChangePass(ObjectUtil.toInteger(rs.getObject("CHANGE_PASS")));
			sysUser.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
			sysUser.setTokenSeed(rs.getString("TOKEN_SEED"));
			sysUser.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			sysUser.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			sysUser.setMac(rs.getString("MAC"));

			Company company = new Company();
			company.setId(ObjectUtil.toLong(rs.getObject("c_ID")));
			company.setPid(rs.getLong("PID"));
			company.setName(rs.getString("c_Name"));
			company.setIsParent(rs.getInt("IS_PARENT"));
			company.setTreeId(rs.getString("TREE_ID"));
			company.setRemark(rs.getString("c_remark"));
			company.setGenerateTime(ObjectUtil.toLong(rs.getObject("c_GENERATE_TIME")));
			company.setUpdateTime(ObjectUtil.toLong(rs.getObject("c_UPDATE_TIME")));
			company.setMac(rs.getString("c_MAC"));

			Role role = new Role();
			role.setId(ObjectUtil.toLong(rs.getObject("r_Id")));
			role.setName(rs.getString("r_Name"));
			role.setMenuId(rs.getString("MENU_ID"));
			role.setMac(rs.getString("r_Mac"));

			sysUser.setCompany(company);
			sysUser.setRole(role);

			return sysUser;
		}
	}

	// private class SysUserRowMapper2 implements RowMapper<SysUser> {
	// public SysUser mapRow(ResultSet rs, int rowNum) throws SQLException {
	// SysUser sysUser = new SysUser();
	// sysUser.setAccount(rs.getString("ACCOUNT"));
	// sysUser.setName(rs.getString("NAME"));
	// sysUser.setPassword(rs.getString("PASSWORD"));
	// sysUser.setCertDN(rs.getString("CERT_DN"));
	// sysUser.setCertPath(rs.getString("CERT_PATH"));
	// sysUser.setRoleId(ObjectUtil.toInteger(rs.getObject("ROLE_ID"));
	// sysUser.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS"));
	// sysUser.setFailedNum(ObjectUtil.toInteger(rs.getObject("FAILED_NUM"));
	// sysUser.setChangedPass(ObjectUtil.toInteger(rs.getObject("CHANGED_PASS"));
	// sysUser.setCompanyId(ObjectUtil.toInteger(rs.getObject("COMPANY_ID"));
	// sysUser.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME"));
	// sysUser.setMac(rs.getString("MAC"));
	// sysUser.setCompanyName(rs.getString("companyName"));
	// sysUser.setRoleName(rs.getString("roleName"));
	// return sysUser;
	// }
	// }

	public Integer searchTotal(SysUser sysUser, String treeId) {
		String sql = "select count(1) from SEAL_SYS_USER s inner join (select c.ID,c.NAME  from SEAL_COMPANY c where c.TREE_ID like ? ) c on c.ID=s.COMPANY_ID inner join SEAL_ROLE r on r.ID=s.ROLE_ID where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		if (treeId != null) {
			valueList.add(treeId + "%");
		}
		
		String account = sysUser.getAccount();
		if (StringUtil.isNotBlank(account)) {
			sql += " and s.ACCOUNT like ? ";
			valueList.add(account.trim() + "%");
		}
		if (StringUtil.isNotBlank(sysUser.getName())) {
			sql += " and s.NAME like ?  ";
			valueList.add(sysUser.getName().trim() + "%");
		}
		if (null != sysUser.getId()) {
			sql += " and s.id <>? ";
			valueList.add(sysUser.getId());
		}

		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
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

	public List<SysUser> searchByPage(SysUser sysUser, final int start, final int end, String treeId) {
		String sql = "select s.ID,s.ACCOUNT,s.NAME,s.PASSWORD,s.ROLE_ID,STATUS,s.FAILED_NUM,s.CHANGE_PASS,s.COMPANY_ID,s.TOKEN_SEED,s.GENERATE_TIME,s.UPDATE_TIME,s.MAC,"
				+ " c.ID c_ID,c.PID,c.NAME c_NAME,c.IS_PARENT,c.TREE_ID,c.REMARK c_remark,c.GENERATE_TIME c_GENERATE_TIME,c.UPDATE_TIME c_UPDATE_TIME,c.MAC c_MAC, "
				+ " r.ID r_Id,r.NAME r_Name,r.MENU_ID,r.MAC r_Mac from SEAL_SYS_USER s inner join (select k.ID, k.PID, k.NAME, k.IS_PARENT, k.TREE_ID, k.REMARK, k.GENERATE_TIME, "
				+ " k.UPDATE_TIME, k.MAC from SEAL_COMPANY k where k.TREE_ID like ? ) c on c.ID=s.COMPANY_ID inner join SEAL_ROLE r on r.ID=s.ROLE_ID where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		
		if (treeId != null) {
			valueList.add(treeId + "%");
		}
		String account = sysUser.getAccount();

		if (StringUtil.isNotBlank(account)) {
			sql += " and s.ACCOUNT like ? ";
			valueList.add(account.trim() + "%");
		}
		if (StringUtil.isNotBlank(sysUser.getName())) {
			sql += " and s.NAME like ?";
			valueList.add(sysUser.getName().trim() + "%");
		}
		if (null != sysUser.getId()) {
			sql += " and s.ID <>? ";
			valueList.add(sysUser.getId());
		}

		sql += " order by s.ID desc ";

		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<SysUser> sysUserList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<SysUser>>() {
			@Override
			public List<SysUser> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<SysUser> list = new ArrayList<SysUser>();
				while (rs.next()) {
					SysUser sysUser = new SysUser();
					sysUser.setId(ObjectUtil.toLong(rs.getObject("ID")));
					sysUser.setAccount(rs.getString("ACCOUNT"));
					sysUser.setName(rs.getString("NAME"));
					sysUser.setPassword(rs.getString("PASSWORD"));
					sysUser.setRoleId(ObjectUtil.toLong(rs.getObject("ROLE_ID")));
					sysUser.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
					sysUser.setFailedNum(ObjectUtil.toInteger(rs.getObject("FAILED_NUM")));
					sysUser.setChangePass(ObjectUtil.toInteger(rs.getObject("CHANGE_PASS")));
					sysUser.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
					sysUser.setTokenSeed(rs.getString("TOKEN_SEED"));
					sysUser.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					sysUser.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					sysUser.setMac(rs.getString("MAC"));

					Company company = new Company();
					company.setId(ObjectUtil.toLong(rs.getObject("c_ID")));
					company.setPid(rs.getLong("PID"));
					company.setName(rs.getString("c_Name"));
					company.setIsParent(rs.getInt("IS_PARENT"));
					company.setTreeId(rs.getString("TREE_ID"));
					company.setRemark(rs.getString("c_remark"));
					company.setGenerateTime(ObjectUtil.toLong(rs.getObject("c_GENERATE_TIME")));
					company.setUpdateTime(ObjectUtil.toLong(rs.getObject("c_UPDATE_TIME")));
					company.setMac(rs.getString("c_MAC"));

					Role role = new Role();
					role.setId(ObjectUtil.toLong(rs.getObject("r_Id")));
					role.setName(rs.getString("r_Name"));
					role.setMenuId(rs.getString("MENU_ID"));
					role.setMac(rs.getString("r_Mac"));

					sysUser.setCompany(company);
					sysUser.setRole(role);

					list.add(sysUser);
				}
				return list;
			}

		});

		return sysUserList;
	}

	public int countSysUserByCompany(String treeId) {
		String sql = "select count(t.id) from SEAL_SYS_USER t where t.company_id in (select c.id from SEAL_COMPANY c where c.tree_id like ?)";
		final List<Object> valueList = new ArrayList<Object>();

		valueList.add(treeId + "%");

		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
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
}
