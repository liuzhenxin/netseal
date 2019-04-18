package cn.com.infosec.netseal.common.dao.seal;

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
import cn.com.infosec.netseal.common.entity.po.Cert;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Seal;
import cn.com.infosec.netseal.common.entity.po.SysUser;
import cn.com.infosec.netseal.common.entity.po.Template;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.DateUtil;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class SealDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param seal
	 * @throws Exception 
	 */
	public void insertSeal(Seal seal)  {
		seal.setId(idGenerator.getNewId(Constants.TABLE_SEAL_SEAL));

		String sql = "insert into SEAL_SEAL(ID,NAME,TEMPLATE_ID,AUDIT_ID,SYS_USER_ID,USER_ID,CERT_ID,TYPE,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,IS_DOWNLOAD,PHOTO_DATA_ID,PHOTO_PATH,TRANSPARENCY,STATUS,USEDCOUNT,USEDLIMIT,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,DOWNLOAD_TIME,PHOTO_WIDTH,PHOTO_HIGH,SEAL_DATA_ID,SEAL_PATH,REMARK,MAC ) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql,
				new Object[] { seal.getId(), seal.getName(), seal.getTemplateId(), seal.getAuditId(), seal.getSysUserId(), seal.getUserId(), seal.getCertId(), seal.getType(), seal.getIsAuditReq(),seal.getIsAuthCertGenSeal(),
						seal.getIsAuthCertDownload(), seal.getIsDownload(), seal.getPhotoDataId(), seal.getPhotoPath(), seal.getTransparency(), seal.getStatus(), seal.getUsedCount(), seal.getUsedLimit(), seal.getNotBefor(),
						seal.getNotAfter(), seal.getGenerateTime(), seal.getUpdateTime(), seal.getDownloadTime(), seal.getPhotoWidth(), seal.getPhotoHigh(), seal.getSealDataId(), seal.getSealPath(),
						seal.getRemark(), seal.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteSeal(Long id) {
		String sql = "delete from SEAL_SEAL where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param seal
	 * @return
	 */
	public int updateSealCount(Seal seal) {
		String sql = "update SEAL_SEAL set USEDCOUNT=?,UPDATE_TIME=?,MAC=? where ID=? and USEDCOUNT < USEDLIMIT";
		return getJdbcTemplate().update(sql, new Object[] { seal.getUsedCount(), seal.getUpdateTime(), seal.calMac(), seal.getId() });
	}

	/**
	 * 改
	 * 
	 * @param seal
	 * @return
	 */
	public int updateSeal(Seal seal) {
		String sql = "update SEAL_SEAL set NAME=?,TEMPLATE_ID=?,AUDIT_ID=?,SYS_USER_ID=?,USER_ID=?,CERT_ID=?,TYPE=?,IS_AUDIT_REQ=?,IS_AUTH_CERT_GEN_SEAL=?,IS_AUTH_CERT_DOWNLOAD=?,IS_DOWNLOAD=?,PHOTO_PATH=?,TRANSPARENCY=?,STATUS=?,USEDCOUNT=?,USEDLIMIT=?,NOTBEFOR=?,NOTAFTER=?,GENERATE_TIME=?,DOWNLOAD_TIME=?,PHOTO_WIDTH=?,PHOTO_HIGH=?,SEAL_PATH=?,REMARK=?,MAC=?,PHOTO_DATA_ID=?,SEAL_DATA_ID=?,UPDATE_TIME=? where ID=?";
		return getJdbcTemplate().update(sql,
				new Object[] { seal.getName(), seal.getTemplateId(), seal.getAuditId(), seal.getSysUserId(), seal.getUserId(), seal.getCertId(), seal.getType(), seal.getIsAuditReq(),seal.getIsAuthCertGenSeal(),
						seal.getIsAuthCertDownload(), seal.getIsDownload(), seal.getPhotoPath(), seal.getTransparency(), seal.getStatus(), seal.getUsedCount(), seal.getUsedLimit(), seal.getNotBefor(), seal.getNotAfter(),
						seal.getGenerateTime(), seal.getDownloadTime(), seal.getPhotoWidth(), seal.getPhotoHigh(), seal.getSealPath(), seal.getRemark(), seal.calMac(), seal.getPhotoDataId(),
						seal.getSealDataId(), seal.getUpdateTime(), seal.getId() });
	}

	/**
	 * 批量改
	 * 
	 * @param seal
	 * @param ids
	 * @return
	 */
	public int updateStatus(Long id, Integer status, Long updateTime, String macs) {
		String sql = "update SEAL_SEAL set STATUS=?, UPDATE_TIME=?, MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[]{ status, updateTime, macs, id});
	}

	/**
	 * 修改印章中证书ID
	 * 
	 * @param userId
	 *            用户id
	 * @param preCertId
	 *            原来证书id
	 * @param certId
	 *            新证书id
	 * @return
	 */
	public int updateCertId(Long userId, Long preCertId, Long certId) {
		String sql = "update SEAL_SEAL set CERT_ID=:certId,UPDATE_TIME=:updateTime where USER_ID=:userId and CERT_ID=:preCertId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("updateTime", DateUtil.getCurrentTime());
		params.put("preCertId", preCertId);
		params.put("certId", certId);
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).update(sql, params);
	}

	/**
	 * 按条件查询
	 * 
	 * @return
	 */
	public List<Seal> getSeals(Seal seal) {
		String sql = "select ID,NAME,TEMPLATE_ID,AUDIT_ID,SYS_USER_ID,USER_ID,CERT_ID,TYPE,IS_AUDIT_REQ,IS_AUTH_CERT_GEN_SEAL,IS_AUTH_CERT_DOWNLOAD,IS_DOWNLOAD,PHOTO_PATH,TRANSPARENCY,STATUS,USEDCOUNT,USEDLIMIT,NOTBEFOR,NOTAFTER,GENERATE_TIME,UPDATE_TIME,DOWNLOAD_TIME,PHOTO_WIDTH,PHOTO_HIGH,SEAL_PATH,REMARK,MAC,PHOTO_DATA_ID,SEAL_DATA_ID from SEAL_SEAL";
		sql += " where 1=1";
		List<Object> valueList = new ArrayList<Object>();
		if (null != seal.getId()) {
			sql += " and ID=?";
			valueList.add(seal.getId());
		}
		if (StringUtil.isNotBlank(seal.getName())) {
			sql += " and NAME=?";
			valueList.add(seal.getName());
		}
		if (null != seal.getType()) {
			sql += " and TYPE=?";
			valueList.add(seal.getType());
		}
		if (null != seal.getStatus()) {
			sql += " and STATUS=?";
			valueList.add(seal.getStatus());
		}
		if (null != seal.getTemplateId()) {
			sql += " and TEMPLATE_ID=?";
			valueList.add(seal.getTemplateId());
		}
		if (null != seal.getUserId()) {
			sql += " and USER_ID=?";
			valueList.add(seal.getUserId());
		}
		if (null != seal.getCertId()) {
			sql += " and CERT_ID=?";
			valueList.add(seal.getCertId());
		}
		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new SealRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Seal getSeal(Long id) {
		String sql = "select ss.ID,ss.NAME,ss.TEMPLATE_ID,ss.AUDIT_ID,ss.SYS_USER_ID,ss.USER_ID,ss.CERT_ID,ss.TYPE,ss.IS_AUDIT_REQ,ss.IS_AUTH_CERT_GEN_SEAL,ss.IS_AUTH_CERT_DOWNLOAD,ss.IS_DOWNLOAD,ss.PHOTO_PATH,ss.TRANSPARENCY,ss.STATUS,ss.USEDCOUNT,ss.USEDLIMIT,ss.NOTBEFOR,ss.NOTAFTER,ss.GENERATE_TIME,ss.UPDATE_TIME,ss.DOWNLOAD_TIME,ss.PHOTO_WIDTH,ss.PHOTO_HIGH,ss.SEAL_PATH,ss.REMARK,ss.MAC,ss.PHOTO_DATA_ID sealPhotoID, ss.SEAL_DATA_ID, "
				+ "ssu.ID sys_ID,ssu.account sys_account,ssu.name sys_Name,ssu.password sys_password,ssu.role_id,ssu.status sys_status,ssu.failed_num,ssu.change_pass,ssu.company_id sys_companyId,ssu.generate_time sys_generateTime,ssu.update_time sys_updateTime,ssu.mac sys_Mac, "
				+ "su.ID u_ID,su.NAME u_Name,su.EMAIL, su.PHONE, su.COMPANY_ID u_companyId, su.GENERATE_TIME u_generateTime, su.UPDATE_TIME u_updateTime, su.MAC u_Mac, "
				+ "sc.ID c_ID,sc.PID,sc.NAME c_NAME,sc.IS_PARENT,sc.TREE_ID,sc.REMARK c_remark,sc.GENERATE_TIME c_GENERATE_TIME,sc.UPDATE_TIME c_UPDATE_TIME,sc.MAC c_MAC, "
				+ "st.ID t_ID,st.NAME t_Name,st.TYPE t_Type,st.STATUS t_Status,st.IS_PHOTO t_IsPhoto,st.PHOTO_DATA_ID t_PhotoDataId,st.PHOTO_PATH t_PhotoPath,st.IS_AUDIT_REQ t_IsAuditReq,st.IS_AUTH_CERT_GEN_SEAL t_IsAuthCertGenSeal,st.IS_AUTH_CERT_DOWNLOAD t_IsAuthCertDownload,st.IS_DOWNLOAD t_IsDownload,st.TRANSPARENCY t_TRANSPARENCY,st.NOTBEFOR t_Notbefor,st.NOTAFTER t_Notafter,st.GENERATE_TIME t_GenerateTime,st.UPDATE_TIME t_UpdateTime,st.REMARK t_remark,st.MAC t_Mac,  "
				+ "scert.ID cer_Id,scert.CERT_SN certSn,scert.CERT_DN certDn,scert.CERT_ISSUE_DN IssueDn,scert.CERT_PATH certPath,scert.NOTBEFOR cer_Notbefor,scert.NOTAFTER cer_Notafter,scert.CERT_DATA_ID cer_DataID,scert.USER_ID cer_UserId,scert.SYS_USER_ID cer_SysuserID,scert.GENERATE_TIME cer_GenerateTime,scert.UPDATE_TIME cer_UpdateTime,scert.MAC cer_Mac "
				+ "from SEAL_SEAL ss left join SEAL_TEMPLATE st on ss.template_id =st.id left join SEAL_SYS_USER ssu on ss.sys_user_id = ssu.id left join SEAL_USER su on ss.user_id=su.id left join SEAL_COMPANY sc on su.company_id =sc.ID left join SEAL_CERT scert on ss.CERT_ID =scert.id where ss.ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new SealRowMapper2(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class SealRowMapper implements RowMapper<Seal> {
		public Seal mapRow(ResultSet rs, int rowNum) throws SQLException {
			Seal seal = new Seal();
			seal.setId(ObjectUtil.toLong(rs.getObject("ID")));
			seal.setName(rs.getString("NAME"));
			seal.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));

			seal.setAuditId(ObjectUtil.toLong(rs.getObject("AUDIT_ID")));
			seal.setSysUserId(ObjectUtil.toLong(rs.getObject("SYS_USER_ID")));
			seal.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));

			seal.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
			seal.setType(ObjectUtil.toInteger(rs.getObject("TYPE")));

			seal.setIsAuditReq(ObjectUtil.toInteger(rs.getObject("IS_AUDIT_REQ")));
			seal.setIsAuthCertGenSeal(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_GEN_SEAL")));
			seal.setIsAuthCertDownload(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_DOWNLOAD")));
			seal.setIsDownload(ObjectUtil.toInteger(rs.getObject("IS_DOWNLOAD")));

			seal.setPhotoPath(rs.getString("PHOTO_PATH"));
			seal.setTransparency((ObjectUtil.toInteger(rs.getObject("TRANSPARENCY"))));
			seal.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			seal.setUsedCount(ObjectUtil.toInteger(rs.getObject("USEDCOUNT")));

			seal.setUsedLimit(ObjectUtil.toInteger(rs.getObject("USEDLIMIT")));
			seal.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			seal.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));

			seal.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			seal.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			seal.setDownloadTime(ObjectUtil.toLong(rs.getObject("DOWNLOAD_TIME")));
			seal.setPhotoWidth(ObjectUtil.toInteger(rs.getObject("PHOTO_WIDTH")));

			seal.setPhotoHigh(ObjectUtil.toInteger(rs.getObject("PHOTO_HIGH")));
			seal.setSealPath(rs.getString("SEAL_PATH"));
			seal.setRemark(rs.getString("REMARK"));

			seal.setMac(rs.getString("MAC"));
			seal.setPhotoDataId(ObjectUtil.toLong(rs.getObject("PHOTO_DATA_ID")));
			seal.setSealDataId(ObjectUtil.toLong(rs.getObject("SEAL_DATA_ID")));
			return seal;
		}
	}

	private class SealRowMapper2 implements RowMapper<Seal> {
		public Seal mapRow(ResultSet rs, int rowNum) throws SQLException {
			Seal seal = new Seal();
			seal.setId(ObjectUtil.toLong(rs.getObject("ID")));
			seal.setName(rs.getString("NAME"));
			seal.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));

			seal.setAuditId(ObjectUtil.toLong(rs.getObject("AUDIT_ID")));
			seal.setSysUserId(ObjectUtil.toLong(rs.getObject("SYS_USER_ID")));
			seal.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));

			seal.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
			seal.setType(ObjectUtil.toInteger(rs.getObject("TYPE")));

			seal.setIsAuditReq(ObjectUtil.toInteger(rs.getObject("IS_AUDIT_REQ")));
			seal.setIsAuthCertGenSeal(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_GEN_SEAL")));
			seal.setIsAuthCertDownload(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_DOWNLOAD")));
			seal.setIsDownload(ObjectUtil.toInteger(rs.getObject("IS_DOWNLOAD")));

			seal.setPhotoPath(rs.getString("PHOTO_PATH"));
			seal.setTransparency((ObjectUtil.toInteger(rs.getObject("TRANSPARENCY"))));
			seal.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
			seal.setUsedCount(ObjectUtil.toInteger(rs.getObject("USEDCOUNT")));

			seal.setUsedLimit(ObjectUtil.toInteger(rs.getObject("USEDLIMIT")));
			seal.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			seal.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));

			seal.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			seal.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			seal.setDownloadTime(ObjectUtil.toLong(rs.getObject("DOWNLOAD_TIME")));
			seal.setPhotoWidth(ObjectUtil.toInteger(rs.getObject("PHOTO_WIDTH")));

			seal.setPhotoHigh(ObjectUtil.toInteger(rs.getObject("PHOTO_HIGH")));
			seal.setSealPath(rs.getString("SEAL_PATH"));
			seal.setRemark(rs.getString("REMARK"));

			seal.setMac(rs.getString("MAC"));
			seal.setPhotoDataId(ObjectUtil.toLong(rs.getObject("sealPhotoID")));
			seal.setSealDataId(ObjectUtil.toLong(rs.getObject("SEAL_DATA_ID")));

			SysUser sys = new SysUser();
			sys.setId(ObjectUtil.toLong(rs.getObject("sys_ID")));
			sys.setAccount(rs.getString("sys_account"));
			sys.setName(rs.getString("sys_Name"));
			sys.setPassword(rs.getString("sys_password"));
			sys.setRoleId(rs.getLong("role_id"));
			sys.setStatus(rs.getInt("sys_status"));
			sys.setFailedNum(rs.getInt("failed_num"));
			sys.setChangePass(rs.getInt("change_pass"));
			sys.setCompanyId(rs.getLong("sys_companyId"));
			sys.setGenerateTime(ObjectUtil.toLong(rs.getObject("sys_generateTime")));
			sys.setUpdateTime(ObjectUtil.toLong(rs.getObject("sys_updateTime")));
			sys.setMac(rs.getString("sys_Mac"));

			User u = new User();
			u.setId(ObjectUtil.toLong(rs.getObject("u_ID")));
			u.setName(rs.getString("u_Name"));
			u.setEmail(rs.getString("EMAIL"));
			u.setPhone(rs.getString("PHONE"));
			u.setCompanyId(rs.getLong("u_companyId"));
			u.setGenerateTime(ObjectUtil.toLong(rs.getObject("u_generateTime")));
			u.setUpdateTime(ObjectUtil.toLong(rs.getObject("u_updateTime")));
			u.setMac(rs.getString("u_Mac"));

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

			Template template = new Template();
			template.setId(ObjectUtil.toLong(rs.getObject("t_ID")));
			template.setName(rs.getString("t_Name"));
			template.setType(rs.getInt("t_Type"));
			template.setStatus(rs.getInt("t_Status"));
			template.setIsPhoto(rs.getInt("t_IsPhoto"));
			template.setPhotoDataId(rs.getLong("t_PhotoDataId"));
			template.setPhotoPath(rs.getString("t_PhotoPath"));
			template.setIsAuditReq(rs.getInt("t_IsAuditReq"));
			template.setIsAuthCertGenSeal(rs.getInt("t_IsAuthCertGenSeal"));
			template.setIsAuthCertDownload(rs.getInt("t_IsAuthCertDownload"));
			template.setIsDownload(rs.getInt("t_IsDownload"));
			template.setTransparency(rs.getInt("t_TRANSPARENCY"));
			template.setNotBefor(ObjectUtil.toLong(rs.getObject("t_Notbefor")));
			template.setNotAfter(ObjectUtil.toLong(rs.getObject("t_Notafter")));
			template.setGenerateTime(ObjectUtil.toLong(rs.getObject("t_GenerateTime")));
			template.setUpdateTime(ObjectUtil.toLong(rs.getObject("t_UpdateTime")));
			template.setRemark(rs.getString("t_remark"));
			template.setMac(rs.getString("t_Mac"));

			Cert cert = new Cert();
			cert.setId(ObjectUtil.toLong(rs.getObject("cer_Id")));
			cert.setCertSn(rs.getString("certSn"));
			cert.setCertDn(rs.getString("certDn"));
			cert.setCertIssueDn(rs.getString("IssueDn"));
			cert.setCertPath(rs.getString("certPath"));
			cert.setNotBefor(ObjectUtil.toLong(rs.getObject("cer_Notbefor")));
			cert.setNotAfter(ObjectUtil.toLong(rs.getObject("cer_Notafter")));
			cert.setCertDataId(rs.getLong("cer_DataID"));
			cert.setUserId(rs.getLong("cer_UserId"));
			cert.setSysUserId(rs.getLong("cer_SysuserID"));
			cert.setGenerateTime(ObjectUtil.toLong(rs.getObject("cer_GenerateTime")));
			cert.setUpdateTime(ObjectUtil.toLong(rs.getObject("cer_UpdateTime")));
			cert.setMac(rs.getString("cer_Mac"));

			u.setCompany(company);
			seal.setSysUser(sys);
			seal.setUser(u);
			seal.setTemplate(template);
			seal.setCert(cert);

			return seal;
		}
	}

	public Integer searchTotal(Seal seal,String treeID) {
		String sql = "select count(1) from SEAL_SEAL s left join SEAL_USER u on u.ID=s.USER_ID inner join (select c.NAME, c.ID  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID=u.COMPANY_ID left join SEAL_CERT sc on sc.ID= s.CERT_ID  where 1=1 ";

		final List<Object> valueList = new ArrayList<Object>();
		if (treeID != null) {
			valueList.add(treeID + "%");
		}
		if (null != seal.getStatus()) {
			sql += " and s.STATUS =?";
			valueList.add(seal.getStatus());
		}
		if (null != seal.getIsDownload()) {
			sql += " and s.IS_DOWNLOAD =?";
			valueList.add(seal.getIsDownload());
		}
		// 多条件查询
		if (StringUtil.isNotBlank(seal.getName())) {
			sql += " and s.NAME like ?";
			valueList.add(seal.getName() + "%");
		}

		if (null != seal.getType()) {
			sql += " and s.TYPE=?";
			valueList.add(seal.getType());
		}

		User user = seal.getUser();
		if (user != null) {
			String u_Name = user.getName();
			if (StringUtil.isNotBlank(u_Name)) {
				sql += " and u.NAME like ?";
				valueList.add(u_Name + "%");
			}
		}

		/*
		 * SysUser sysUser = seal.getSysUser(); if (sysUser != null) { Company company = sysUser.getCompany(); if (company != null) { String c_Name = company.getName(); if
		 * (StringUtil.isNotBlank(c_Name)) { sql += " and c.NAME like ?"; valueList.add(c_Name + "%"); } } String sys_account = sysUser.getAccount(); if (StringUtil.isNotBlank(sys_account)) { sql +=
		 * " and u.ACCOUNT like ?"; valueList.add(sys_account + "%"); } }
		 */
		Cert cert = seal.getCert();
		if (cert != null) {
			String certDn = seal.getCert().getCertDn();
			if (StringUtil.isNotBlank(certDn)) {
				sql += " and sc.CERT_DN like ?";
				valueList.add(certDn + "%");
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

	public List<Seal> searchByPage(Seal seal, final int start, final int end,String treeID) {
		final List<Object> valueList = new ArrayList<Object>();
		String sql = "select s.ID,s.NAME sealName,s.TEMPLATE_ID,s.AUDIT_ID,s.SYS_USER_ID,s.USER_ID,s.CERT_ID,u.COMPANY_ID,s.TYPE,s.IS_AUDIT_REQ,s.IS_AUTH_CERT_GEN_SEAL,s.IS_AUTH_CERT_DOWNLOAD,s.IS_DOWNLOAD,s.PHOTO_PATH,s.TRANSPARENCY,s.STATUS,s.USEDCOUNT,s.USEDLIMIT,s.NOTBEFOR,s.NOTAFTER,s.GENERATE_TIME,s.UPDATE_TIME,s.DOWNLOAD_TIME,s.PHOTO_WIDTH,s.PHOTO_HIGH,s.SEAL_PATH,s.REMARK,s.MAC "
				+ ",k.NAME c_Name, " + "u.NAME u_Name, " + "s.PHOTO_DATA_ID sealPhotoID, s.SEAL_DATA_ID," + "sc.CERT_DN certdn "
				+ "from SEAL_SEAL s left join SEAL_USER u on u.ID=s.USER_ID inner join (select c.NAME, c.ID  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID=u.COMPANY_ID left join SEAL_CERT sc on sc.ID= s.CERT_ID where 1=1 ";
		
		if (treeID != null) {
			valueList.add(treeID + "%");
		}
		if (null != seal.getStatus()) {
			sql += " and s.STATUS =?";
			valueList.add(seal.getStatus());
		}
		if (null != seal.getIsDownload()) {
			sql += " and s.IS_DOWNLOAD =?";
			valueList.add(seal.getIsDownload());
		}
		// 多条件查询
		if (StringUtil.isNotBlank(seal.getName())) {
			sql += " and s.NAME like ?";
			valueList.add(seal.getName() + "%");
		}

		if (null != seal.getType()) {
			sql += " and s.TYPE=?";
			valueList.add(seal.getType());
		}

		User user = seal.getUser();
		if (user != null) {
			String u_Name = user.getName();
			if (StringUtil.isNotBlank(u_Name)) {
				sql += " and u.NAME like ?";
				valueList.add(u_Name + "%");
			}
		}

		Cert cert = seal.getCert();
		if (cert != null) {
			String certDn = seal.getCert().getCertDn();
			if (StringUtil.isNotBlank(certDn)) {
				sql += " and sc.CERT_DN like ?";
				valueList.add(certDn + "%");
			}
		}
		sql += " order by s.ID desc ";

		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<Seal> sealList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<Seal>>() {
			@Override
			public List<Seal> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<Seal> list = new ArrayList<Seal>();
				while (rs.next()) {
					Seal seal = new Seal();
					seal.setId(ObjectUtil.toLong(rs.getObject("ID")));
					seal.setName(rs.getString("sealName"));
					seal.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));

					seal.setAuditId(ObjectUtil.toLong(rs.getObject("AUDIT_ID")));
					seal.setSysUserId(ObjectUtil.toLong(rs.getObject("SYS_USER_ID")));
					seal.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));

					seal.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
					seal.setType(ObjectUtil.toInteger(rs.getObject("TYPE")));

					seal.setIsAuditReq(ObjectUtil.toInteger(rs.getObject("IS_AUDIT_REQ")));
					seal.setIsAuthCertGenSeal(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_GEN_SEAL")));
					seal.setIsAuthCertDownload(ObjectUtil.toInteger(rs.getObject("IS_AUTH_CERT_DOWNLOAD")));
					seal.setIsDownload(ObjectUtil.toInteger(rs.getObject("IS_DOWNLOAD")));

					seal.setPhotoPath(rs.getString("PHOTO_PATH"));
					seal.setTransparency(rs.getInt("TRANSPARENCY"));
					seal.setStatus(ObjectUtil.toInteger(rs.getObject("STATUS")));
					seal.setUsedCount(ObjectUtil.toInteger(rs.getObject("USEDCOUNT")));

					seal.setUsedLimit(ObjectUtil.toInteger(rs.getObject("USEDLIMIT")));
					seal.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
					seal.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));

					seal.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					seal.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					seal.setDownloadTime(ObjectUtil.toLong(rs.getObject("DOWNLOAD_TIME")));
					seal.setPhotoWidth(ObjectUtil.toInteger(rs.getObject("PHOTO_WIDTH")));

					seal.setPhotoHigh(ObjectUtil.toInteger(rs.getObject("PHOTO_HIGH")));
					seal.setSealPath(rs.getString("SEAL_PATH"));
					seal.setRemark(rs.getString("REMARK"));

					seal.setMac(rs.getString("MAC"));

					Company company = new Company();
					company.setName(rs.getString("c_Name"));

					User user = new User();
					user.setName(rs.getString("u_Name"));

					seal.setPhotoDataId(ObjectUtil.toLong(rs.getObject("sealPhotoID")));
					seal.setSealDataId(ObjectUtil.toLong(rs.getObject("SEAL_DATA_ID")));

					Cert cert = new Cert();
					cert.setCertDn(rs.getString("certdn"));

					user.setCompany(company);
					seal.setUser(user);
					seal.setCert(cert);

					list.add(seal);
				}
				return list;
			}

		});

		return sealList;
	}

	/**
	 * 统计印章数量
	 * 
	 * @param startTime
	 * @return
	 */
	public Integer getSealCount(long startTime) {
		String sql = "select count(1) from SEAL_SEAL where 1=1 ";

		final List<Object> valueList = new ArrayList<Object>();
		if (startTime != -1) {
			sql += " and NOTBEFOR >= ? ";
			valueList.add(startTime);
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
	 * 统计制发印章的用户数量
	 * 
	 * @return
	 */
	public int getSealUserCount() {
		String sql = "select count(distinct USER_ID) from SEAL_SEAL where 1=1 ";

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

	/**
	 * 统计所属公司的人员印章数量
	 * 
	 * @param treeId
	 *            公司treeId
	 * @return
	 */
	public int countSealByCompany(final Long companyId) {
		String sql = "select count(1) from SEAL_SEAL s left join SEAL_USER u on s.user_id=u.id left join SEAL_COMPANY c on c.ID=u.COMPANY_ID where c.id=?";

		Integer total = getJdbcTemplate().execute(sql, new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

				ps.setLong(1, companyId);
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
