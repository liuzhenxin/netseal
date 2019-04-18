package cn.com.infosec.netseal.common.dao.photoData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
import cn.com.infosec.netseal.common.entity.po.PhotoData;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;

@Repository
public class PhotoDataDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param photoData
	 * @return id
	 * @throws Exception 
	 */
	public Long insertPhotoData(final PhotoData photoData)  {
		photoData.setId(idGenerator.getNewId(Constants.TABLE_SEAL_PHOTO_DATA));

		String sql = "insert into SEAL_PHOTO_DATA(ID,DATA,GENERATE_TIME,MAC,UPDATE_TIME) values(?,?,?,?,?)";

		final LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
			protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
				ps.setLong(1, photoData.getId());
				lobCreator.setBlobAsBytes(ps, 2, photoData.getData());
				ps.setLong(3, photoData.getGenerateTime());
				try {
					ps.setString(4, photoData.calMac());
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				ps.setLong(5, photoData.getUpdateTime());
			}
		});
		return photoData.getId();
	}

	/**
	 * 删
	 * 
	 * @param id
	 * @return
	 */
	public int deletePhotoData(Long id) {
		String sql = "delete from SEAL_PHOTO_DATA where ID=?";
		return getJdbcTemplate().update(sql, id);
	}

	/**
	 * 改
	 * 
	 * @param sys
	 * @return
	 */
	public int updatePhotoData(Long oldId, PhotoData pd) {
		String sql = "update SEAL_PHOTO_DATA set ID=?, UPDATE_TIME=?, MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { pd.getId(), pd.getUpdateTime(), pd.calMac(), oldId });
	}

	/**
	 * 查单条
	 * 
	 * @param id
	 * @return
	 */
	public PhotoData getPhotoData(Long id) {
		String sql = "select ID,DATA,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_PHOTO_DATA where ID=?";

		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper<PhotoData>() {
				public PhotoData mapRow(ResultSet rs, int rowNum) throws SQLException {
					PhotoData photoData = new PhotoData();
					photoData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					photoData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					photoData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					photoData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					photoData.setMac(rs.getString("MAC"));
					return photoData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查单条
	 * 
	 * @param timeStamp
	 * @return
	 */
	public List<PhotoData> photoDataList(long timeStamp) {
		String sql = "select ID,DATA,GENERATE_TIME,MAC,UPDATE_TIME from SEAL_PHOTO_DATA where UPDATE_TIME>=? order by UPDATE_TIME";

		final LobHandler lobHandler = new DefaultLobHandler();
		try {
			return getJdbcTemplate().query(sql, new Object[] { timeStamp }, new RowMapper<PhotoData>() {
				public PhotoData mapRow(ResultSet rs, int rowNum) throws SQLException {
					PhotoData photoData = new PhotoData();
					photoData.setId(ObjectUtil.toLong(rs.getObject("ID")));
					photoData.setData(lobHandler.getBlobAsBytes(rs, "DATA"));
					photoData.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					photoData.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					photoData.setMac(rs.getString("MAC"));
					return photoData;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public int updatePhotoData(PhotoData photoData) {
		String sql = "update SEAL_PHOTO_DATA set DATA=?, UPDATE_TIME=?, MAC=? where ID=?";
		return getJdbcTemplate().update(sql, new Object[] { photoData.getData(), photoData.getUpdateTime(), photoData.calMac(),photoData.getId()});
	}

}
