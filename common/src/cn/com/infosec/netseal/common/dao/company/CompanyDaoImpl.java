package cn.com.infosec.netseal.common.dao.company;

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
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;


@Repository
public class CompanyDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param company
	 * @throws Exception 
	 */
	public void insertCompany(Company company) throws Exception {
		company.setId(idGenerator.getNewId(Constants.TABLE_SEAL_COMPANY));

		String sql = "insert into SEAL_COMPANY(ID,PID,NAME,REMARK,MAC,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME)values(?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { company.getId(), company.getPid(), company.getName(), company.getRemark(), company.calMac(), company.getIsParent(), company.getTreeId(),
				company.getGenerateTime(), company.getUpdateTime() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteCompany(Long id) {
		String sql = "delete from SEAL_COMPANY where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 * @throws Exception 
	 */
	public int updateCompany(Company company) throws  Exception {
		String sql = "update SEAL_COMPANY set NAME=?,REMARK=?,MAC=?,UPDATE_TIME=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { company.getName(), company.getRemark(), company.calMac(), company.getUpdateTime(), company.getId() });
	}

	/**
	 * 改 当前单位是否为父单位
	 * 
	 * @param id
	 * @param isParent
	 * @return
	 */
	public int updateCompanyIsParent(Company company) {
		String sql = "update SEAL_COMPANY set IS_PARENT=?,UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { company.getIsParent(), company.getUpdateTime(), company.calMac(), company.getId()});
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Company> getCompanys() {
		String sql = "select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY";
		return getJdbcTemplate().query(sql, new CompanyRowMapper());
	}

	/**
	 * 查列表
	 * 
	 * @param company
	 * @return
	 */
	public List<Company> getCompanys(Company company) {
		String sql = "select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY where 1=1";

		List<Object> valueList = new ArrayList<Object>();
		if (company.getPid() != null) {
			sql += " and PID=?";
			valueList.add(company.getPid());
		}

		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new CompanyRowMapper());
	}

	/**
	 * 查列表
	 * 
	 * @param pid
	 * @return
	 */
	public List<Company> getCompanys(Long pid) {
		String sql = "select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY where PID=?";
		return getJdbcTemplate().query(sql, new Object[] { pid }, new CompanyRowMapper());
	}

	/**
	 * 查列表
	 * 
	 * @param treeId
	 * @return
	 */
	public List<Company> getCompanys(String treeId) {
		String sql = "select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY where TREE_ID like ? ";
		return getJdbcTemplate().query(sql, new Object[] { treeId + "%" }, new CompanyRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Company getCompany(Long id) {
		String sql = "select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new CompanyRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查单条
	 * 
	 * @param name
	 * @return
	 */
	public List<Company> getCompany(String name) {
		String sql = "select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY where NAME=?";
		try {
			return getJdbcTemplate().query(sql, new CompanyRowMapper(), name);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	
	/**
	 * 根据name,pid 查
	 * @param  name pid
	 * @return
	 */
	public List<Company> getCompany(String name ,Long pid ) {
		String sql="select ID,PID,NAME,REMARK,IS_PARENT,TREE_ID,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_COMPANY where NAME=? and PID=?";
		return   getJdbcTemplate().query(sql, new Object[] { name , pid }, new CompanyRowMapper());
	}
	

	private class CompanyRowMapper implements RowMapper<Company> {
		public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
			Company company = new Company();
			company.setId(ObjectUtil.toLong(rs.getObject("ID")));
			company.setPid(ObjectUtil.toLong(rs.getObject("PID")));
			company.setName(rs.getString("NAME"));
			company.setIsParent(ObjectUtil.toInteger(rs.getObject("IS_PARENT")));
			company.setTreeId(rs.getString("TREE_ID"));
			company.setRemark(rs.getString("REMARK"));
			company.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			company.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			company.setMac(rs.getString("MAC"));
			return company;
		}
	}

	public List<String> subCompanyTreeId(Long pid) {
		String sql = "select TREE_ID from SEAL_COMPANY where PID=?";
		try {
			return getJdbcTemplate().query(sql, new Object[] { pid }, new TreeIdRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Long> subCompanyId(String treeId) {
		String sql = "select ID from SEAL_COMPANY where TREE_ID like ? ";
		try {
			return getJdbcTemplate().query(sql, new Object[] { treeId + "%" }, new IdRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class TreeIdRowMapper implements RowMapper<String> {
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String treeId = rs.getString("TREE_ID");
			return treeId;
		}
	}

	private class IdRowMapper implements RowMapper<Long> {
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("ID");
			return id;
		}
	}

}
