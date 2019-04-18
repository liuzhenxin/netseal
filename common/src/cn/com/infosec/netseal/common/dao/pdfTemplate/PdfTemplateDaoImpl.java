package cn.com.infosec.netseal.common.dao.pdfTemplate;

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
import cn.com.infosec.netseal.common.entity.po.PdfTemplate;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class PdfTemplateDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增加
	 * 
	 * @param key
	 * @throws Exception 
	 */
	public void insertPdfTemplate(PdfTemplate pdfTemplate)  {
		pdfTemplate.setId(idGenerator.getNewId(Constants.TABLE_SEAL_PDF_TEMPLATE));

		String sql = "insert into SEAL_PDF_TEMPLATE(ID,NAME,TEMPLATE_PATH,GENERATE_TIME,UPDATE_TIME,PDF_TEMPLATE_DATA_ID,MAC)values(?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { pdfTemplate.getId(), pdfTemplate.getName(),pdfTemplate.getTemplatePath(),pdfTemplate.getGenerateTime(),pdfTemplate.getUpdateTime(),pdfTemplate.getPdfTemplateDataId(), pdfTemplate.calMac()});
	}

	
	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deletePdfTemplate(Long id) {
		String sql = "delete from SEAL_PDF_TEMPLATE where ID=?";
		return getJdbcTemplate().update(sql, id);
	}
	
	
	/**
	 * 根据名称查单条数量
	 */
	public int countPdfByName(String name) {
		String sql = "select count(1) from SEAL_PDF_TEMPLATE s where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		if (StringUtil.isNotBlank(name)) {
			sql += " and s.NAME = ? ";
			valueList.add(name.trim());
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
	 * 查单条
	 * 
	 * @param String
	 * @return
	 */
	public PdfTemplate getPdfTemplate(Long id) {
		String sql = "select ID,NAME,TEMPLATE_PATH,GENERATE_TIME,UPDATE_TIME,PDF_TEMPLATE_DATA_ID,MAC from SEAL_PDF_TEMPLATE where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new PdfTemplateRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	/**
	 * 按名称查
	 * 
	 * @param String
	 * @return
	 */
	public List<PdfTemplate> getPdfTemplate(String name) {
		String sql = "select ID,NAME,TEMPLATE_PATH,GENERATE_TIME,UPDATE_TIME,PDF_TEMPLATE_DATA_ID,MAC from SEAL_PDF_TEMPLATE where NAME=?";
		try {
			return getJdbcTemplate().query(sql, new PdfTemplateRowMapper(), name);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	
	/**
	 * 查所有
	 * 
	 * @return
	 */
	public List<PdfTemplate> getPdfTemplates() {
		String sql = "select ID,NAME,TEMPLATE_PATH,GENERATE_TIME,UPDATE_TIME,PDF_TEMPLATE_DATA_ID,MAC from SEAL_PDF_TEMPLATE order by GENERATE_TIME asc";
		try {
			return getJdbcTemplate().query(sql, new PdfTemplateRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	
	
	
	
	
	
	private class PdfTemplateRowMapper implements RowMapper<PdfTemplate> {
		public PdfTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
			PdfTemplate pdfTemplate = new PdfTemplate();
			pdfTemplate.setId(ObjectUtil.toLong(rs.getObject("ID")));
			pdfTemplate.setName(rs.getString("NAME"));
			pdfTemplate.setTemplatePath(rs.getString("TEMPLATE_PATH"));
			pdfTemplate.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			pdfTemplate.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			pdfTemplate.setPdfTemplateDataId(ObjectUtil.toLong(rs.getObject("PDF_TEMPLATE_DATA_ID")));
			pdfTemplate.setMac(rs.getString("MAC"));
			return pdfTemplate;
		}
	}
	
	
	/**
	 * 分页查总条
	 * 
	 * @param certChain
	 * @return
	 */
	public int searchTotal() {
		String sql = "select count(1) from SEAL_PDF_TEMPLATE where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		
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

	/**
	 * 分页查全部
	 * 
	 * @param certChain
	 * @param start
	 * @param end
	 * @return
	 */
	public List<PdfTemplate> searchByPage(final int start, final int end) {
		String sql = "select ID,NAME,TEMPLATE_PATH,GENERATE_TIME,UPDATE_TIME,PDF_TEMPLATE_DATA_ID,MAC from SEAL_PDF_TEMPLATE where 1=1";
		final List<Object> valueList = new ArrayList<Object>();
		
		
		sql += " order by GENERATE_TIME desc ";
		//统一查询
		String baseSql = baseDao.searchByPage(sql, start, end, valueList);
		
		List<PdfTemplate> pdfTemplateList = getJdbcTemplate().execute(baseSql, new PreparedStatementCallback<List<PdfTemplate>>() {
			@Override
			public List<PdfTemplate> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ResultSet rs = ps.executeQuery();
				List<PdfTemplate> list = new ArrayList<PdfTemplate>();
				while (rs.next()) {
					PdfTemplate pdfTemplate = new PdfTemplate();
					pdfTemplate.setId(ObjectUtil.toLong(rs.getObject("ID")));
					pdfTemplate.setName(rs.getString("NAME"));
					pdfTemplate.setTemplatePath(rs.getString("TEMPLATE_PATH"));
					pdfTemplate.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					pdfTemplate.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					pdfTemplate.setPdfTemplateDataId(ObjectUtil.toLong(rs.getObject("PDF_TEMPLATE_DATA_ID")));
					pdfTemplate.setMac(rs.getString("MAC"));
					list.add(pdfTemplate);
				}
				return list;
			}

		});
		return pdfTemplateList;
	}

	
}
