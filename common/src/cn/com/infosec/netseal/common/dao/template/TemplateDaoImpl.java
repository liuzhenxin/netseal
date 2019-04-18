package cn.com.infosec.netseal.common.dao.template;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class TemplateDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param template
	 * @throws Exception 
	 * @throws IOException 
	 * @throws DataAccessException 
	 */
	public void insertTemplate(Template template) throws Exception {
		template.setId(idGenerator.getNewId(Constants.TABLE_SEAL_TEMPLATE));

		String sql = "insert into SEAL_TEMPLATE(ID,NAME,TYPE,COMPANY_ID,IS_PHOTO,PHOTO_PATH,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,STATUS,IS_DOWNLOAD,TRANSPARENCY,NOTBEFOR,NOTAFTER,GENERATE_TIME,REMARK,MAC,PHOTO_DATA_ID,UPDATE_TIME)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql,
				new Object[] { template.getId(), template.getName(), template.getType(), template.getCompanyId(), template.getIsPhoto(), template.getPhotoPath(), template.getIsAuditReq(),
						template.getIsAuthCertGenSeal(), template.getIsAuthCertDownload(),template.getStatus(), template.getIsDownload(),template.getTransparency(), template.getNotBefor(), template.getNotAfter(),
						template.getGenerateTime(), template.getRemark(), template.calMac(), template.getPhotoDataId(), template.getUpdateTime() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteTemplate(Long id) {
		String sql = "delete from SEAL_TEMPLATE where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param template
	 * @param notAfterTime
	 * @param notBeforTime
	 * @return
	 * @throws Exception 
	 */
	public int updateTemplate(Template template) throws  Exception {
		String sql = "update SEAL_TEMPLATE set NAME=?,TYPE=?,COMPANY_ID=?,IS_PHOTO=?,PHOTO_PATH=?,IS_AUDIT_REQ=?,IS_AUTH_CERT_GEN_SEAL=?,IS_AUTH_CERT_DOWNLOAD=?,STATUS=?,IS_DOWNLOAD=?,TRANSPARENCY=?,NOTBEFOR=?,NOTAFTER=?,GENERATE_TIME=?,UPDATE_TIME=?,REMARK=?,MAC=?,PHOTO_DATA_ID=? where ID=?";
		return getJdbcTemplate().update(sql,
				new Object[] { template.getName(), template.getType(), template.getCompanyId(), template.getIsPhoto(), template.getPhotoPath(), template.getIsAuditReq(),
						template.getIsAuthCertGenSeal(), template.getIsAuthCertDownload(), template.getStatus(), template.getIsDownload(),template.getTransparency(), template.getNotBefor(), template.getNotAfter(),
						template.getGenerateTime(), template.getUpdateTime(), template.getRemark(), template.calMac(), template.getPhotoDataId(), template.getId() });
	}

	/**
	 * 修改状态
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	public int updateTemplateStatus(long id, Integer status, long systime, String macs ) {
		String sql = "update SEAL_TEMPLATE set STATUS=?, UPDATE_TIME=?, MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[]{ status, systime, macs, id});
	}
	
	
	/*public int updateTemplateStatus(String ids, Integer status) {
		String sql = "update SEAL_TEMPLATE set STATUS=:status,UPDATE_TIME=:updateTime where ID in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("updateTime", DateUtil.getCurrentTime());
		params.put("ids", Arrays.asList(ids.split(Constants.SPLIT_1)));
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).update(sql, params);
	}*/

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Template> getTemplate() {
		String sql = "select ID,NAME,TYPE,COMPANY_ID,IS_PHOTO,PHOTO_PATH,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,STATUS,IS_DOWNLOAD,TRANSPARENCY,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,REMARK,MAC,PHOTO_DATA_ID from SEAL_TEMPLATE";
		return getJdbcTemplate().query(sql, new TemplateRowMapper());
	}

	/**
	 * 查
	 * 
	 * @return
	 */
	public List<Template> getTemplate(String name) {
		String sql = "select ID,NAME,TYPE,COMPANY_ID,IS_PHOTO,PHOTO_PATH,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,STATUS,IS_DOWNLOAD,TRANSPARENCY,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,REMARK,MAC,PHOTO_DATA_ID from SEAL_TEMPLATE";
		sql += " where NAME=?";
		return getJdbcTemplate().query(sql, new TemplateRowMapper(), name);
	}

	/**
	 * 获取签章人可制作印章的印模 status为1
	 * 
	 * @param treeIdList
	 *            单位树Id List
	 * @return
	 */
	public List<Template> getUserTemplate(List<String> treeIdList) {
		String sql = "select t.ID,t.NAME templateName,t.TYPE,t.COMPANY_ID,t.IS_PHOTO,t.PHOTO_PATH,t.IS_AUDIT_REQ,t.IS_AUTH_CERT_GEN_SEAL,t.IS_AUTH_CERT_DOWNLOAD,t.STATUS,t.IS_DOWNLOAD,t.TRANSPARENCY,t.NOTBEFOR,t.NOTAFTER,t.GENERATE_TIME,t.UPDATE_TIME,t.REMARK,t.MAC,t.PHOTO_DATA_ID "
				+ ",c.ID c_ID,c.PID,c.NAME c_NAME,c.IS_PARENT,c.TREE_ID,c.REMARK c_REMARK,c.GENERATE_TIME c_GENERATE_TIME,c.UPDATE_TIME c_UPDATE_TIME,c.MAC c_MAC "
				+ "from SEAL_TEMPLATE t left join SEAL_COMPANY c on c.ID=t.COMPANY_ID " + "where t.STATUS=1 and c.TREE_ID in(:treeIdList) ";
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("treeIdList", treeIdList);

			return new NamedParameterJdbcTemplate(getJdbcTemplate()).query(sql, params, new TemplateRowMapper2());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Template> getTemplates(Template template) {
		String sql = "select ID,NAME,TYPE,COMPANY_ID,IS_PHOTO,PHOTO_PATH,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,STATUS,IS_DOWNLOAD,TRANSPARENCY,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,REMARK,MAC,PHOTO_DATA_ID from SEAL_TEMPLATE where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(template.getName())) {
			sql += " and NAME=? ";
			valueList.add(template.getName());
		}

		Object[] values = new Object[valueList.size()];
		valueList.toArray(values);
		return getJdbcTemplate().query(sql, new TemplateRowMapper(), values);
	}

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public Template getTemplate(Long id) {
		String sql = "select t.ID,t.NAME templateName,t.TYPE,t.COMPANY_ID,t.IS_PHOTO,t.PHOTO_PATH,t.IS_AUDIT_REQ,t.IS_AUTH_CERT_GEN_SEAL,t.IS_AUTH_CERT_DOWNLOAD,t.STATUS,t.IS_DOWNLOAD,t.TRANSPARENCY,t.NOTBEFOR,t.NOTAFTER,t.GENERATE_TIME,t.UPDATE_TIME,t.REMARK,t.MAC,t.PHOTO_DATA_ID, "
				+ " c.ID c_ID,c.PID,c.NAME c_NAME,c.IS_PARENT,c.TREE_ID,c.REMARK c_REMARK,c.GENERATE_TIME c_GENERATE_TIME,c.UPDATE_TIME c_UPDATE_TIME,c.MAC c_MAC "
				+ " from SEAL_TEMPLATE t left join SEAL_COMPANY c on c.ID=t.COMPANY_ID  where t.ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new TemplateRowMapper2(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 根据单位id查询
	 * 
	 * @param companyId
	 * @return
	 */
	public List<Template> getTemplateByCompanyId(Long companyId) {
		String sql = "select ID,NAME,TYPE,COMPANY_ID,IS_PHOTO,PHOTO_PATH,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,STATUS,IS_DOWNLOAD,TRANSPARENCY,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,REMARK,MAC,PHOTO_DATA_ID from SEAL_TEMPLATE where COMPANY_ID=?";
		return getJdbcTemplate().query(sql, new Object[] { companyId }, new TemplateRowMapper());
	}

	private class TemplateRowMapper implements RowMapper<Template> {
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			Template template = new Template();
			template.setId(ObjectUtil.toLong(rs.getObject("ID")));
			template.setName(rs.getString("NAME"));
			template.setType(ObjectUtil.toInteger(rs.getObject("TYPE")));
			template.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
			template.setIsPhoto(ObjectUtil.toInteger(rs.getObject("IS_PHOTO")));
			template.setPhotoPath(rs.getString("PHOTO_PATH"));
			template.setIsAuditReq(ObjectUtil.toInteger(rs.getObject("IS_AUDIT_REQ")));
			template.setIsAuthCertGenSeal(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_GEN_SEAL")));
			template.setIsAuthCertDownload(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_DOWNLOAD")));
			template.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			template.setIsDownload(ObjectUtil.toInteger(rs.getObject("IS_DOWNLOAD")));
			template.setTransparency((ObjectUtil.toInteger(rs.getObject("TRANSPARENCY"))));
			template.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			template.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			template.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			template.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			template.setRemark(rs.getString("REMARK"));
			template.setMac(rs.getString("MAC"));
			template.setPhotoDataId(ObjectUtil.toLong(rs.getObject("PHOTO_DATA_ID")));
			return template;
		}
	}

	private class TemplateRowMapper2 implements RowMapper<Template> {
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			Template template = new Template();
			template.setId(ObjectUtil.toLong(rs.getObject("ID")));
			template.setName(rs.getString("templateName"));
			template.setType(ObjectUtil.toInteger(rs.getObject("TYPE")));
			template.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
			template.setIsPhoto(ObjectUtil.toInteger(rs.getObject("IS_PHOTO")));
			template.setPhotoPath(rs.getString("PHOTO_PATH"));
			template.setIsAuditReq(ObjectUtil.toInteger(rs.getObject("IS_AUDIT_REQ")));
			template.setIsAuthCertGenSeal(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_GEN_SEAL")));
			template.setIsAuthCertDownload(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_DOWNLOAD")));
			template.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			template.setIsDownload(ObjectUtil.toInteger(rs.getObject("IS_DOWNLOAD")));
			template.setTransparency((ObjectUtil.toInteger(rs.getObject("TRANSPARENCY"))));
			template.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			template.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			template.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			template.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			template.setRemark(rs.getString("REMARK"));
			template.setMac(rs.getString("MAC"));
			template.setPhotoDataId(ObjectUtil.toLong(rs.getObject("PHOTO_DATA_ID")));

			Company company = new Company();
			company.setId(ObjectUtil.toLong(rs.getObject("c_ID")));
			company.setPid(ObjectUtil.toLong(rs.getLong("PID")));
			company.setName(rs.getString("c_Name"));
			company.setIsParent(rs.getInt("IS_PARENT"));
			company.setTreeId(rs.getString("TREE_ID"));
			company.setRemark(rs.getString("c_remark"));
			company.setGenerateTime(ObjectUtil.toLong(rs.getObject("c_GENERATE_TIME")));
			company.setUpdateTime(ObjectUtil.toLong(rs.getObject("c_UPDATE_TIME")));
			company.setMac(rs.getString("c_MAC"));
			template.setCompany(company);

			return template;
		}
	}

	public Integer searchTotal(Template template, String treeId) {
		String sql = "select count(1) from SEAL_TEMPLATE t inner join (select c.ID,c.NAME  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID = t.COMPANY_ID where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		if (treeId != null) 
			valueList.add(treeId + "%");
		
		if (StringUtil.isNotBlank(template.getName())) {
			sql += " and NAME like ? ";
			valueList.add(template.getName() + "%");
		}
		if(template.getPhotoDataId()!=null){
			sql += " and PHOTO_DATA_ID =? ";
			valueList.add(template.getPhotoDataId());
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

	public List<Template> searchByPage(Template template, final int start, final int end, String treeId) {
		String sql = "select t.ID,t.NAME templateName,t.TYPE,t.COMPANY_ID,t.IS_PHOTO,t.PHOTO_PATH,t.IS_AUDIT_REQ,t.IS_AUTH_CERT_GEN_SEAL,t.IS_AUTH_CERT_DOWNLOAD,t.STATUS,t.IS_DOWNLOAD,t.TRANSPARENCY,t.NOTBEFOR,t.NOTAFTER,t.GENERATE_TIME,t.UPDATE_TIME,t.REMARK,t.MAC,t.PHOTO_DATA_ID, "
				+ " c.ID c_ID,c.PID,c.NAME c_NAME,c.IS_PARENT,c.TREE_ID,c.REMARK c_remark,c.GENERATE_TIME c_GENERATE_TIME,c.UPDATE_TIME c_UPDATE_TIME,c.MAC c_MAC "
				+ " from SEAL_TEMPLATE t inner join (select k.ID, k.PID, k.NAME, k.IS_PARENT, k.TREE_ID, k.REMARK, k.GENERATE_TIME, k.UPDATE_TIME, k.MAC  from SEAL_COMPANY k where k.TREE_ID like ? ) c on c.ID=t.COMPANY_ID where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		if (treeId != null) 
			valueList.add(treeId + "%");
		
		if (template.getName() != null && !"".equals(template.getName())) {
			sql += " and t.NAME like ? ";
			valueList.add(template.getName() + "%");
		}
		sql += " order by t.ID desc";
		
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<Template> templateList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<Template>>() {
			@Override
			public List<Template> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<Template> list = new ArrayList<Template>();
				while (rs.next()) {
					Template template = new Template();
					template.setId(ObjectUtil.toLong(rs.getObject("ID")));
					template.setName(rs.getString("templateName"));
					template.setType(ObjectUtil.toInteger(rs.getObject("TYPE")));
					template.setCompanyId(ObjectUtil.toLong(rs.getObject("COMPANY_ID")));
					template.setIsPhoto(ObjectUtil.toInteger(rs.getObject("IS_PHOTO")));
					template.setPhotoPath(rs.getString("PHOTO_PATH"));
					template.setIsAuditReq(ObjectUtil.toInteger(rs.getObject("IS_AUDIT_REQ")));
					template.setIsAuthCertGenSeal(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_GEN_SEAL")));
					template.setIsAuthCertDownload(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_DOWNLOAD")));
					template.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
					template.setIsDownload(ObjectUtil.toInteger(rs.getObject("IS_DOWNLOAD")));
					template.setTransparency((ObjectUtil.toInteger(rs.getObject("TRANSPARENCY"))));
					template.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
					template.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
					template.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					template.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					template.setRemark(rs.getString("REMARK"));
					template.setMac(rs.getString("MAC"));
					template.setPhotoDataId(ObjectUtil.toLong(rs.getObject("PHOTO_DATA_ID")));

					Company company = new Company();
					company.setId(ObjectUtil.toLong(rs.getObject("c_ID")));
					company.setPid(ObjectUtil.toLong(rs.getLong("PID")));
					company.setName(rs.getString("c_Name"));
					company.setIsParent(rs.getInt("IS_PARENT"));
					company.setTreeId(rs.getString("TREE_ID"));
					company.setRemark(rs.getString("c_remark"));
					company.setGenerateTime(ObjectUtil.toLong(rs.getObject("c_GENERATE_TIME")));
					company.setUpdateTime(ObjectUtil.toLong(rs.getObject("c_UPDATE_TIME")));
					company.setMac(rs.getString("c_MAC"));
					template.setCompany(company);

					list.add(template);
				}
				return list;
			}

		});

		return templateList;
	}
}
