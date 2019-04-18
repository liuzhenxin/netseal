package cn.com.infosec.netseal.common.dao.role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.entity.po.Role;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class RoleDaoImpl extends BaseDao {
	/**
	 * 增
	 * 
	 * @param sys
	 * @throws Exception 
	 */
	public void insertRole(Role role)  {
		String sql = "insert into SEAL_ROLE(NAME,MENU_ID,MAC) values(?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { role.getName(), role.getMenuId(), role.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteRole(Long id) {
		String sql = "delete from SEAL_ROLE where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateRole(Role role) {
		String sql = "update SEAL_ROLE set NAME=?,MENU_ID=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { role.getName(), role.getMenuId(), role.getMac(), role.getId() });
	}

	/**
	 * 授权
	 * 
	 * @param role
	 * @param menuIds
	 * @return
	 */
	public int roleMenu(Long id, String menuIds, long update_time, String sealMac) {
		String sql = "update SEAL_ROLE set MENU_ID=?,UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { menuIds, update_time, sealMac, id });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Role> getRoles() {
		String sql = "select ID,NAME,MENU_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_ROLE order by ID";
		return getJdbcTemplate().query(sql, new RoleRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Role getRole(Long id) {
		String sql = "select ID,NAME,MENU_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_ROLE where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new RoleRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class RoleRowMapper implements RowMapper<Role> {
		public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
			Role role = new Role();
			role.setId(ObjectUtil.toLong(rs.getObject("ID")));
			role.setName(rs.getString("NAME"));
			role.setMenuId(rs.getString("MENU_ID"));
			role.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			role.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			role.setMac(rs.getString("MAC"));
			return role;
		}
	}

}
