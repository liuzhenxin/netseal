package cn.com.infosec.netseal.common.dao.pdfTemplateData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.define.Constants;
import cn.com.infosec.netseal.common.entity.po.PdfTemplateData;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class PdfTemplateDataDaoImpl extends BaseDao {

	private static final String PreparedStatement = null;
	@Autowired
	private IDGenerator idGenerator;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 增加
	 * 
	 * @param pdfTemplateData
	 * @return id
	 * @throws Exception
	 */
	public Long insertPdfTemplateData(final PdfTemplateData pdfTemplateData) {
		pdfTemplateData.setId(idGenerator.getNewId(Constants.TABLE_SEAL_KEY));

		String sql = "insert into SEAL_PDF_TEMPLATE_DATA(ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC)values(?,?,?,?,?)";
		final LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
				ps.setLong(1, pdfTemplateData.getId());
				lobCreator.setBlobAsBytes(ps, 2, pdfTemplateData.getData());
				ps.setLong(3, pdfTemplateData.getUpdateTime());
				ps.setLong(4, pdfTemplateData.getUpdateTime());
				try {
					ps.setString(5, pdfTemplateData.calMac());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return pdfTemplateData.getId();
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deletePdfTemplateData(Long id) {
		String sql = "delete from SEAL_PDF_TEMPLATE_DATA where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public PdfTemplateData getPdfTemplateData(Long id) {
		String sql = "select ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_PDF_TEMPLATE_DATA where ID= ?";
		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper<PdfTemplateData>() {
				public PdfTemplateData mapRow(ResultSet rs, int rowNum) throws SQLException {
					PdfTemplateData pdfTemplateData = new PdfTemplateData();
					pdfTemplateData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					pdfTemplateData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					pdfTemplateData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					pdfTemplateData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					pdfTemplateData.setMac(rs.getString("MAC"));
					return pdfTemplateData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
