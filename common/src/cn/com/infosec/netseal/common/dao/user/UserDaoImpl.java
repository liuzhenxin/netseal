package cn.com.infosec.netseal.common.dao.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class UserDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param user
	 * @throws Exception 
	 */
	public void insertUser(User user)  {
		user.setId(idGenerator.getNewId(Constants.TABLE_SEAL_USER));

		String sql = "insert into SEAL_USER(ID,NAME,PASSWORD,EMAIL,PHONE,COMPANY_ID,GENERATE_TIME,MAC,UPDATE_TIME)values(?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql,
				new Object[] { user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getPhone(), user.getCompanyId(), user.getGenerateTime(), user.calMac(), user.getUpdateTime() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteUser(Long id) {
		String sql = "delete from SEAL_USER where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改 组织机构 修改用户信息
	 * 
	 * @param user
	 * @return
	 */
	public int updateUser(User user) {
		String sql = "update SEAL_USER set NAME=?,PASSWORD=?,EMAIL=?,PHONE=?,COMPANY_ID=?,MAC=?,UPDATE_TIME=? where ID=?";
		return getJdbcTemplate().update(sql,
				new Object[] { user.getName(), user.getPassword(), user.getEmail(), user.getPhone(), user.getCompanyId(), user.calMac(), user.getUpdateTime(), user.getId() });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<User> getUsers() {
		String sql = "select ID,NAME,PASSWORD,EMAIL,PHONE,COMPANY_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER";
		return getJdbcTemplate().query(sql, new UserRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(Long id) {
		String sql = "select u.ID,u.NAME USERNAME,u.PASSWORD,u.EMAIL,u.PHONE,u.COMPANY_ID,u.GENERATE_TIME,u.UPDATE_TIME,u.MAC,c.NAME c_NAME from SEAL_USER u left join SEAL_COMPANY c on c.ID=u.COMPANY_ID  where u.ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new UserRowMapper2(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class UserRowMapper implements RowMapper<User> {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(ObjectUtil.toLong(rs.getObject("ID")));
			user.setName(rs.getString("NAME"));
			user.setPassword(rs.getString("PASSWORD"));
			user.setEmail(rs.getString("EMAIL"));
			user.setPhone(rs.getString("PHONE"));
			user.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
			user.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			user.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			user.setMac(rs.getString("MAC"));
			return user;
		}
	}

	private class UserRowMapper2 implements RowMapper<User> {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(ObjectUtil.toLong(rs.getObject("ID")));
			user.setName(rs.getString("USERNAME"));
			user.setPassword(rs.getString("PASSWORD"));
			user.setEmail(rs.getString("EMAIL"));
			user.setPhone(rs.getString("PHONE"));
			user.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
			user.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			user.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			user.setMac(rs.getString("MAC"));

			Company company = new Company();
			company.setName(rs.getString("c_NAME"));
			user.setCompany(company);

			return user;
		}
	}

	/**
	 * 统计所属公司的人员数据
	 * 
	 * @param treeId
	 *            公司treeId
	 * @return
	 */
	public Integer countUserByCompany(String treeId) {
		String sql = "select count(t.id) from SEAL_USER t where t.company_id in (select c.id from SEAL_COMPANY c where c.tree_id like ?)";
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

	public Integer searchTotal(User user,String treeId) {
		String sql = "select count(1) from SEAL_USER u inner join (select c.ID,c.NAME  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID=u.COMPANY_ID where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		if (treeId != null) {
			valueList.add(treeId + "%");
		}

		String name = user.getName();
		if (StringUtil.isNotBlank(name)) {
			sql += " and u.NAME like ? ";
			valueList.add(name.trim() + "%");
		}
		List<Cert> certs = user.getCerts();
		if (certs != null) {
			for (Cert cert : certs) {
				String certDN = cert.getCertDn();
				if (StringUtil.isNotBlank(certDN)) {
					sql += " and exists(select 1 from SEAL_CERT sc where u.ID=sc.USER_ID and sc.CERT_DN like ?) ";
					valueList.add(certDN.trim() + "%");
				}
			}
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

	public List<User> searchByPage(User user, final int start, final int end,String treeId) {
		final List<Object> valueList = new ArrayList<Object>();
		String sql = "select u.ID,u.NAME username,u.PASSWORD,u.EMAIL,u.PHONE,u.COMPANY_ID,u.GENERATE_TIME,u.UPDATE_TIME,u.MAC," + " k.NAME c_Name "
				+ " from SEAL_USER u inner join (select c.NAME, c.ID  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID=u.COMPANY_ID where 1=1";
		
		if (treeId != null) {
			valueList.add(treeId + "%");
		}
		String name = user.getName();
		if (StringUtil.isNotBlank(name)) {
			sql += " and u.NAME like ? ";
			valueList.add(name.trim() + "%");
		}
		List<Cert> certs = user.getCerts();
		if (certs != null) {
			for (Cert cert : certs) {
				String certDN = cert.getCertDn();
				if (StringUtil.isNotBlank(certDN)) {
					sql += " and exists(select 1 from SEAL_CERT sc where u.ID=sc.USER_ID and sc.CERT_DN like ?) ";
					valueList.add(certDN.trim() + "%");
				}
			}
		}

		sql += " order by u.ID desc ";

		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<User> userList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<User>>() {
			@Override
			public List<User> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<User> list = new ArrayList<User>();
				while (rs.next()) {
					User user = new User();
					user.setId(ObjectUtil.toLong(rs.getObject("ID")));
					user.setName(rs.getString("USERNAME"));
					user.setPassword(rs.getString("PASSWORD"));
					user.setEmail(rs.getString("EMAIL"));
					user.setPhone(rs.getString("PHONE"));
					user.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
					user.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					user.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					user.setMac(rs.getString("MAC"));

					Company company = new Company();
					company.setName(rs.getString("c_Name"));
					user.setCompany(company);

					list.add(user);
				}
				return list;
			}
		});
		return userList;
	}

	public List<User> getUser(User user) {
		String sql = "select ID,NAME,PASSWORD,EMAIL,PHONE,COMPANY_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER where 1=1 ";
		List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(user.getName())) {
			sql += " and NAME=? ";
			valueList.add(user.getName());
		}
		if (user.getCompanyId() != null) {
			sql += " and COMPANY_ID=? ";
			valueList.add(user.getCompanyId());
		}

		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new UserRowMapper());
	}

	/**
	 * 
	 * @param name
	 * @param companyID
	 * @return
	 */
	public List<User> getUser(String name, Long companyID) {
		String sql = "select ID,NAME,PASSWORD,EMAIL,PHONE,COMPANY_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER where NAME=? and COMPANY_ID=?";
		try {
			return getJdbcTemplate().query(sql, new UserRowMapper(), new Object[] { name, companyID });
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NetSealRuntimeException(ErrCode.USER_NOT_UNIQUE_IN_DB, "user in db is not unique, name is " + name);
		}
	}

	/**
	 * 
	 * @param name
	 * @param companyId
	 * @param certId
	 * @return
	 */
	public List<User> getUser(String name, Long companyId, Long certId) {
		String sql = "select u.ID,u.NAME,u.PASSWORD,u.EMAIL,u.PHONE,u.COMPANY_ID,u.GENERATE_TIME,u.UPDATE_TIME,u.MAC from SEAL_USER u left join SEAL_CERT c on u.ID=c.USER_ID where u.NAME=? and u.COMPANY_ID=? and c.ID=?";
		try {
			return getJdbcTemplate().query(sql, new UserRowMapper(), new Object[] { name, companyId, certId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NetSealRuntimeException(ErrCode.USER_NOT_UNIQUE_IN_DB, "user in db is not unique, name is " + name);
		}
	}

	/**
	 * 公司下的所有员工
	 * 
	 * @param user
	 * @return
	 */
	public Integer searchSonTotal(User user, String treeId) {
		String sql = "SELECT count(1) from (SELECT c.ID, c.NAME from  SEAL_COMPANY c where c.TREE_ID like ? ) k inner JOIN  SEAL_USER u on k.ID=u.COMPANY_ID where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		if (treeId != null) {
			valueList.add(treeId + "%");
		}
		String name = user.getName();
		if (StringUtil.isNotBlank(name)) {
			sql += " and u.NAME like ? ";
			valueList.add(name.trim() + "%");
		}
		Company company = user.getCompany();
		if (company != null) {
			String c_Name = company.getName();
			if (StringUtil.isNotBlank(c_Name)) {
				sql += " and k.NAME like ? ";
				valueList.add(c_Name + "%");
			}
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

	/**
	 * 查询公司下用户
	 * 
	 * @param user
	 * @param start
	 * @param end
	 * @param treeId
	 * @return
	 */
	public List<User> searchUserPageByCompanyId(User user, final int start, final int end, String treeId) {
		final List<Object> valueList = new ArrayList<Object>();
		String sql = "select u.ID, u.NAME u_NAME, k.NAME k_NAME from (select c.ID, c.NAME from SEAL_COMPANY c where c.TREE_ID like ? ) k inner JOIN SEAL_USER u on k.ID = u.COMPANY_ID where 1=1 ";
		if (treeId != null) {
			valueList.add(treeId + "%");
		}
		String name = user.getName();
		if (StringUtil.isNotBlank(name)) {
			sql += " and u.NAME like ? ";
			valueList.add(name + "%");
		}
		Company company = user.getCompany();
		if (company != null) {
			String c_Name = company.getName();
			if (StringUtil.isNotBlank(c_Name)) {
				sql += " and k.NAME like ? ";
				valueList.add(c_Name + "%");
			}
		}
		sql += " order by u.ID desc ";
		
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);

		List<User> userList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<User>>() {
			@Override
			public List<User> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<User> list = new ArrayList<User>();
				while (rs.next()) {
					User user = new User();
					user.setId(ObjectUtil.toLong(rs.getObject("ID")));
					user.setName(rs.getString("u_NAME"));
					
					Company company = new Company();
					company.setName(rs.getString("k_NAME"));
					user.setCompany(company);

					list.add(user);
				}
				return list;
			}

		});

		return userList;
	}

	/**
	 * 获取所有用户数量
	 * 
	 * @return
	 */
	public int getTotalUsreCount() {
		String sql = "select count(1) from SEAL_USER where 1=1 ";

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
