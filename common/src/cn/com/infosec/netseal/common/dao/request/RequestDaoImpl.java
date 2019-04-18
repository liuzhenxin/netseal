package cn.com.infosec.netseal.common.dao.request;

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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.Company;
import cn.com.infosec.netseal.common.entity.po.Request;
import cn.com.infosec.netseal.common.entity.po.User;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class RequestDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增
	 * 
	 * @param request
	 * @throws Exception 
	 */
	public void insertRequest(Request request)  {
		request.setId(idGenerator.getNewId(Constants.TABLE_SEAL_REQUEST));

		String sql = "insert into SEAL_REQUEST(ID,NAME,TEMPLATE_ID,PHOTO_DATA_ID,PHOTO_PATH,TRANSPARENCY,NOTBEFOR,NOTAFTER,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,REMARK,MAC)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { request.getId(), request.getName(), request.getTemplateId(), request.getPhotoDataId(), request.getPhotoPath(), request.getTransparency(),request.getNotBefor(),
				request.getNotAfter(), request.getUserId(), request.getCertId(), request.getGenerateTime(), request.getUpdateTime(), request.getRemark(), request.calMac() });
	}

	/**
	 * 批量增
	 * 
	 * @param requestList
	 */
	public void insertBatch(final List<Request> requestList) {
		String sql = "insert into SEAL_REQUEST(ID,NAME,TEMPLATE_ID,PHOTO_DATA_ID,PHOTO_PATH,TRANSPARENCY,NOTBEFOR,NOTAFTER,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,REMARK,MAC)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Request request = requestList.get(i);
				ps.setLong(1, request.getId());
				ps.setString(2, request.getName());
				ps.setLong(3, request.getTemplateId());
				ps.setLong(4, request.getPhotoDataId());
				ps.setString(5, request.getPhotoPath());
				ps.setInt(6, request.getTransparency());
				ps.setLong(7, request.getNotBefor());
				ps.setLong(8, request.getNotAfter());
				ps.setLong(9, request.getUserId());
				ps.setLong(10, request.getCertId());
				ps.setLong(11, request.getGenerateTime());
				ps.setLong(12, request.getUpdateTime());
				ps.setString(13, request.getRemark());
				ps.setString(14, request.getMac());
			}

			public int getBatchSize() {
				return requestList.size();
			}
		});
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteRequest(Long id) {
		String sql = "delete from SEAL_REQUEST where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deleteRequests(long timeStamp) {
		String sql = "delete from SEAL_REQUEST where UPDATE_TIME>=?";
		return getJdbcTemplate().update(sql, timeStamp);
	}

	/**
	 * 改
	 * 
	 * @param request
	 * @return
	 */
	public int updateRequest(Request request) {
		String sql = "update SEAL_REQUEST set NAME=?,TEMPLATE_ID=?,PHOTO_DATA_ID=?,PHOTO_PATH=?,TRANSPARENCY=?,NOTBEFOR=?,NOTAFTER=?,USER_ID=?,CERT_ID=?,GENERATE_TIME=?,UPDATE_TIME=?,REMARK=?,MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { request.getName(), request.getTemplateId(), request.getPhotoDataId(), request.getPhotoPath(),request.getTransparency(), request.getNotBefor(), request.getNotAfter(),
				request.getUserId(), request.getCertId(), request.getGenerateTime(), request.getUpdateTime(), request.getRemark(), request.getMac(), request.getId() });
	}

	/**
	 * 查列表
	 * 
	 * @return
	 */
	public List<Request> getRequests(Request request) {
		String sql = "select ID,NAME,TEMPLATE_ID,PHOTO_DATA_ID,PHOTO_PATH,TRANSPARENCY,NOTBEFOR,NOTAFTER,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,REMARK,MAC from SEAL_REQUEST where 1=1";

		List<Object> valueList = new ArrayList<Object>();
		if (request.getName() != null) {
			sql += " and NAME=?";
			valueList.add(request.getName());
		}
		if (request.getUserId() != null) {
			sql += " and USER_ID=?";
			valueList.add(request.getUserId());
		}
		if (request.getCertId() != null) {
			sql += " and CERT_ID=?";
			valueList.add(request.getCertId());
		}
		if (request.getTemplateId() != null) {
			sql += " and TEMPLATE_ID=?";
			valueList.add(request.getTemplateId());
		}
		Object[] values = valueList.toArray();
		return getJdbcTemplate().query(sql, values, new RequestRowMapper());
	}

	/**
	 * 查列表
	 * 
	 * @return
	 */
	public List<Request> getRequests(String ids) {
		String sql = "select ID,NAME,TEMPLATE_ID,PHOTO_DATA_ID,PHOTO_PATH,TRANSPARENCY,NOTBEFOR,NOTAFTER,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,REMARK,MAC from SEAL_REQUEST where ID in(:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", Arrays.asList(ids.split(Constants.SPLIT_1)));
		return new NamedParameterJdbcTemplate(getJdbcTemplate()).query(sql, params, new RequestRowMapper());
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public Request getRequest(Long id) {
		String sql = "select ID,NAME,TEMPLATE_ID,PHOTO_DATA_ID,PHOTO_PATH,TRANSPARENCY,NOTBEFOR,NOTAFTER,USER_ID,CERT_ID,GENERATE_TIME,UPDATE_TIME,REMARK,MAC from SEAL_REQUEST where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new RequestRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class RequestRowMapper implements RowMapper<Request> {
		public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
			Request request = new Request();
			request.setId(ObjectUtil.toLong(rs.getObject("ID")));
			request.setName(rs.getString("NAME"));
			request.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));
			request.setPhotoDataId(ObjectUtil.toLong(rs.getObject("PHOTO_DATA_ID")));
			request.setPhotoPath(rs.getString("PHOTO_PATH"));
			request.setTransparency(ObjectUtil.toInteger(rs.getObject("TRANSPARENCY")));
			request.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
			request.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
			request.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
			request.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
			request.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			request.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			request.setRemark(rs.getString("REMARK"));
			request.setMac(rs.getString("MAC"));
			return request;
		}
	}

	public Integer searchTotal(Request request, String treeID) {
		String sql = "select count(1) from SEAL_REQUEST r left join SEAL_USER u on u.ID=r.USER_ID inner join (select c.NAME, c.ID  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID=u.COMPANY_ID where 1=1";

		final List<Object> valueList = new ArrayList<Object>();
		if (treeID != null) {
			valueList.add(treeID + "%");
		}
		if (request.getName() != null) {
			sql += " and r.NAME=? ";
			valueList.add(request.getName());
		}
		if (request.getUserId() != null) {
			sql += " and r.USER_ID=? ";
			valueList.add(request.getUserId());
		}
		if (request.getTemplateId() != null) {
			sql += " and r.TEMPLATE_ID=? ";
			valueList.add(request.getTemplateId());
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

	public List<Request> searchByPage(Request request, final int start, final int end,String treeID) {
		String sql = "select r.ID,r.NAME,r.TEMPLATE_ID,r.PHOTO_DATA_ID,r.PHOTO_PATH,r.TRANSPARENCY,r.NOTBEFOR,r.NOTAFTER,r.USER_ID,r.CERT_ID,r.GENERATE_TIME,r.UPDATE_TIME,r.REMARK r_REMARK,r.MAC,"
				+ "u.ID u_ID,u.NAME u_NAME,u.PASSWORD,u.EMAIL,u.PHONE,u.COMPANY_ID,u.GENERATE_TIME u_GENERATE_TIME,u.UPDATE_TIME u_UPDATE_TIME,u.MAC u_MAC,"
				+ "k.ID c_ID,k.PID,k.NAME c_NAME,k.REMARK c_REMARK,k.IS_PARENT,k.TREE_ID,k.GENERATE_TIME c_GENERATE_TIME,k.UPDATE_TIME c_UPDATE_TIME,k.MAC c_MAC "
				+ "from SEAL_REQUEST r left join SEAL_USER u on u.ID = r.USER_ID inner join (select c.NAME, c.ID,c.PID,c.REMARK,c.IS_PARENT,c.TREE_ID,c.GENERATE_TIME,c.UPDATE_TIME,c.MAC  from SEAL_COMPANY c where c.TREE_ID like ? ) k on k.ID=u.COMPANY_ID where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		if (treeID != null) {
			valueList.add(treeID + "%");
		}
		if (request.getName() != null) {
			sql += " and r.NAME=? ";
			valueList.add(request.getName());
		}
		Long templateId = request.getTemplateId();
		if (templateId != null) {
			sql += " and r.TEMPLATE_ID=? ";
			valueList.add(templateId);
		}
		sql += " order by r.ID desc ";

		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<Request> requestList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<Request>>() {
			@Override
			public List<Request> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<Request> list = new ArrayList<Request>();
				while (rs.next()) {
					Request request = new Request();
					request.setId(ObjectUtil.toLong(rs.getObject("ID")));
					request.setName(rs.getString("NAME"));
					request.setTemplateId(ObjectUtil.toLong(rs.getObject("TEMPLATE_ID")));
					request.setPhotoDataId(ObjectUtil.toLong(rs.getObject("PHOTO_DATA_ID")));
					request.setPhotoPath(rs.getString("PHOTO_PATH"));
					request.setTransparency(ObjectUtil.toInteger(rs.getObject("TRANSPARENCY")));
					request.setNotBefor(ObjectUtil.toLong(rs.getObject("NOTBEFOR")));
					request.setNotAfter(ObjectUtil.toLong(rs.getObject("NOTAFTER")));
					request.setUserId(ObjectUtil.toLong(rs.getObject("USER_ID")));
					request.setCertId(ObjectUtil.toLong(rs.getObject("CERT_ID")));
					request.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					request.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					request.setRemark(rs.getString("r_REMARK"));
					request.setMac(rs.getString("MAC"));

					User user = new User();
					user.setId(ObjectUtil.toLong(rs.getObject("u_ID")));
					user.setName(rs.getString("u_NAME"));
					user.setPassword(rs.getString("PASSWORD"));
					user.setEmail(rs.getString("EMAIL"));
					user.setPhone(rs.getString("PHONE"));
					user.setCompanyId(rs.getLong("COMPANY_ID"));
					user.setGenerateTime(ObjectUtil.toLong(rs.getObject("c_GENERATE_TIME")));
					user.setUpdateTime(ObjectUtil.toLong(rs.getObject("u_UPDATE_TIME")));
					user.setMac(rs.getString("u_MAC"));

					Company company = new Company();
					company.setId(ObjectUtil.toLong(rs.getObject("c_ID")));
					company.setPid(rs.getLong("PID"));
					company.setName(rs.getString("c_NAME"));
					company.setRemark(rs.getString("c_REMARK"));
					company.setIsParent(rs.getInt("IS_PARENT"));
					company.setTreeId(rs.getString("TREE_ID"));
					company.setGenerateTime(ObjectUtil.toLong(rs.getObject("c_GENERATE_TIME")));
					company.setUpdateTime(ObjectUtil.toLong(rs.getObject("c_UPDATE_TIME")));
					company.setMac(rs.getString("c_MAC"));

					user.setCompany(company);
					request.setUser(user);

					list.add(request);
				}
				return list;
			}

		});
		return requestList;

	}
}
