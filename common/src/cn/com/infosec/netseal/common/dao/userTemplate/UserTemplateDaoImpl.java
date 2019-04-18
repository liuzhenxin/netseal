package cn.com.infosec.netseal.common.dao.userTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.entity.po.UserTemplate;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class UserTemplateDaoImpl extends BaseDao {
	/**
	 * 增
	 * 
	 * @param userTemplate
	 * @throws Exception 
	 */
	public void insertUserTemplate(UserTemplate userTemplate)  {

		String sql = "insert into SEAL_USER_TEMPLATE(USER_ID,TEMPLATE_ID,GENERATE_TIME,UPDATE_TIME,MAC)values(?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { userTemplate.getUserId(), userTemplate.getTemplateId(), userTemplate.getGenerateTime(), userTemplate.getUpdateTime(),userTemplate.calMac() });
	}

	/**
	 * 按印模id删
	 * 
	 * @param templateId
	 * @return
	 */
	public int deleteUserTemplate(Long templateId) {
		String sql = "delete from SEAL_USER_TEMPLATE where TEMPLATE_ID=?";
		return getJdbcTemplate().update(sql, templateId);
	}

	/**
	 * 按用户id删
	 * 
	 * @param userId
	 * @return
	 */
	public int deleteUserTemplate2(Long userId) {
		String sql = "delete from SEAL_USER_TEMPLATE where USER_ID=?";
		return getJdbcTemplate().update(sql, userId);
	}

	/**
	 * 查询全部
	 * 
	 * @return
	 */
	public List<UserTemplate> getUserTemplate() {
		String sql = "select USER_ID,TEMPLATE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER_TEMPLATE";
		return getJdbcTemplate().query(sql, new UserTemplateRowMapper());
	}

	/**
	 * 按印模id查询
	 * 
	 * @param templateId
	 * @return
	 */
	public List<UserTemplate> getUserTemplate(Long templateId) {
		String sql = "select USER_ID,TEMPLATE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER_TEMPLATE where TEMPLATE_ID=?";
		return getJdbcTemplate().query(sql, new UserTemplateRowMapper(), templateId);
	}

	/**
	 * 按用户id查询
	 * 
	 * @param userId
	 * @return
	 */
	public List<UserTemplate> getUserTemplate2(Long userId) {
		String sql = "select USER_ID,TEMPLATE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_USER_TEMPLATE where USER_ID=? or USER_ID=0";
		return getJdbcTemplate().query(sql, new UserTemplateRowMapper(), userId);
	}

	private class UserTemplateRowMapper implements RowMapper<UserTemplate> {
		public UserTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserTemplate userTemplate = new UserTemplate();
			userTemplate.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
			userTemplate.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));
			userTemplate.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			userTemplate.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			userTemplate.setMac(rs.getString("MAC"));
			return userTemplate;
		}
	}
}
