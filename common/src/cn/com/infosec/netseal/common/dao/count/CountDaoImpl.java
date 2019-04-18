package cn.com.infosec.netseal.common.dao.count;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.infosec.netseal.common.dao.BaseDao;
import cn.com.infosec.netseal.common.entity.po.Count;
import cn.com.infosec.netseal.common.util.ObjectUtil;


@Repository
public class CountDaoImpl extends BaseDao {

	/**
	 * 增加
	 * @throws Exception 
	 * @throws IOException 
	 * @throws DataAccessException 
	 * 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void insert(Count count)  {
		String sql = "insert into SEAL_COUNT(NAME, NUM, LOCATION, MAC)values(?,?,?,?)";
		getJdbcTemplate().update(sql, new Object[] { count.getName(), count.getNum(), count.getLocation(), count.calMac() });
	}

	/**
	 * 改num 保持num值与证书表对应证书数一致
	 * 
	 * @param count
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 * @throws DataAccessException 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public int updateNum(Count count)  {
		String sql = "update SEAL_COUNT set NUM=?, MAC=? where NAME=? and LOCATION=?";
		return getJdbcTemplate().update(sql, new Object[] { count.getNum(), count.calMac(), count.getName(), count.getLocation() });
	}

	/**
	 * 改 数量 + 1
	 * @throws Exception 
	 * @throws IOException 
	 * @throws DataAccessException 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public int updateCountPlus(Count count, long limit) {
		String sql = "update SEAL_COUNT set NUM=?, MAC=? where NAME=? and LOCATION=? and NUM < ?";
		return getJdbcTemplate().update(sql, new Object[] { count.getNum(), count.calMac(), count.getName(), count.getLocation(), limit });
	}

	/**
	 * 改 数量
	 * @throws Exception 
	 * @throws IOException 
	 * @throws DataAccessException 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public int updateCountMinus(Count count) throws Exception {
		String sql = "update SEAL_COUNT set NUM=?, MAC=? where NAME=? and LOCATION=? and NUM > 0";
		return getJdbcTemplate().update(sql, new Object[] { count.getNum(), count.calMac(), count.getName(), count.getLocation() });
	}

	/**
	 * 查列表
	 * 
	 */
	public List<Count> getCount(Count count) {
		String sql = "select NAME,NUM,LOCATION,MAC from SEAL_COUNT where NAME=? and LOCATION=?";
		try {
			return getJdbcTemplate().query(sql, new Object[] { count.getName(), count.getLocation() }, new RowMapper<Count>() {
				public Count mapRow(ResultSet rs, int rowNum) throws SQLException {
					Count c = new Count();
					c.setName(rs.getString("NAME"));
					c.setNum(ObjectUtil.toLong(rs.getObject("NUM")));
					c.setLocation(rs.getString("LOCATION"));
					c.setMac(rs.getString("MAC"));
					return c;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
