package cn.com.infosec.netseal.common.dao.menu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Menu;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class MenuDaoImpl extends BaseDao {

	/**
	 * 增加
	 * 
	 * @param menu
	 */
	public void insertMenu(Menu menu) {
		String sql = "insert into SEAL_MENU(PID,NAME,URL)values(?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { menu.getPid(), menu.getName(), menu.getUrl() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteMenu(Long id) {
		String sql = "delete from SEAL_MENU where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updateMenu(Menu menu) {
		String sql = "update SEAL_MENU set PID=?,NAME=?,URL=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { menu.getPid(), menu.getName(), menu.getUrl(), menu.getId() });
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Menu> getMenu() {
		String sql = "select ID,PID,NAME,URL,IMG,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_MENU";
		return getJdbcTemplate().query(sql, new MenuRowMapper());
	}

	/**
	 * 根据多个Id查询
	 * 
	 * @return
	 */
	public List<Menu> getMenuByIds(String ids) {

		String sql = "select ID,PID,NAME,URL,IMG,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_MENU where ID in(:ids) order by ID asc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", Arrays.asList(ids.split(Constants.SPLIT_1)));
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).query(sql, params, new MenuRowMapper());
	}

	/**
	 * 根据pid查询
	 * 
	 * @param pid
	 * @return
	 */
	public List<Menu> getMenuByPid(Long pid) {
		String sql = "select ID,PID,NAME,URL,IMG,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_MENU where PID=? order by ID asc";
		return getJdbcTemplate().query(sql, new MenuRowMapper(), pid);
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Menu getMenu(Long id) {
		String sql = "select ID,PID,NAME,URL,IMG,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_MENU where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new MenuRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class MenuRowMapper implements RowMapper<Menu> {
		public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
			Menu menu = new Menu();
			menu.setId(ObjectUtil.toLong(rs.getObject("ID")));
			menu.setName(rs.getString("NAME"));
			menu.setPid(ObjectUtil.toLong(rs.getObject("PID")));
			menu.setUrl(rs.getString("URL"));
			menu.setImg(rs.getString("IMG"));
			menu.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			menu.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			menu.setMac(rs.getString("MAC"));
			return menu;
		}
	}

}
