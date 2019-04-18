package cn.com.infosec.netseal.common.dao.printer;

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
import cn.com.infosec.netseal.common.entity.po.Printer;
import cn.com.infosec.netseal.common.idCache.IDGenerator;
import cn.com.infosec.netseal.common.util.ObjectUtil;
import cn.com.infosec.netseal.common.util.StringUtil;

@Repository
public class PrinterDaoImpl extends BaseDao {

	@Autowired
	private IDGenerator idGenerator;

	/**
	 * 增
	 * 
	 * @param printer
	 * @throws Exception 
	 */
	public void insertPrinter(Printer printer) {
		printer.setId(idGenerator.getNewId(Constants.TABLE_SEAL_PRINTER));

		String sql = "insert into SEAL_PRINTER(ID,NAME,SEAL_NAME,SEAL_TYPE,USERNAME,PRINT_NUM,PRINTED_NUM,PASSWORD,GENERATE_TIME,UPDATE_TIME,MAC)values(?,?,?,?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(
				sql,
				new Object[] { printer.getId(), printer.getName(), printer.getSealName(), printer.getSealType(), printer.getUserName(), printer.getPrintNum(), printer.getPrintedNum(),
						printer.getPassword(),printer.getGenerateTime(),printer.getUpdateTime(), printer.calMac() });
	}

	/**
	 * 删
	 * 
	 * @param ID
	 * @return
	 */
	public int deletePrinter(int ID) {
		String sql = "delete from SEAL_PRINTER where ID=?";
		return getJdbcTemplate().update(sql, ID);
	}

	/**
	 * 改
	 * 
	 * @param printer
	 * @return
	 * @throws Exception 
	 */
	public int updatePrinter(Printer printer) throws  Exception {
		String sql = "update SEAL_PRINTER set NAME=?,SEAL_NAME=?,SEAL_TYPE=?,USERNAME=?,PRINT_NUM=?,PRINTED_NUM=?,PASSWORD=?,GENERATE_TIME=?,UPDATE_TIME=?,MAC=? where ID=?";
		return getJdbcTemplate().update(
				sql,
				new Object[] { printer.getName(), printer.getSealName(), printer.getSealType(), printer.getUserName(), printer.getPrintNum(), printer.getPrintedNum(), printer.getPassword(),
						printer.getGenerateTime(),printer.getUpdateTime(), printer.calMac(), printer.getId() });
	}

	/**
	 * 查单条
	 * 
	 * @param ID
	 * @return
	 */
	public Printer getPrinter(int ID) {
		String sql = "select ID,NAME,SEAL_NAME,SEAL_TYPE,USERNAME,PRINT_NUM,PRINTED_NUM,PASSWORD,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_PRINTER where ID=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new PrinterRowMapper(), ID);
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
	public List<Printer> getPrinter(String name) {
		String sql = "select ID,NAME,SEAL_NAME,SEAL_TYPE,USERNAME,PRINT_NUM,PRINTED_NUM,PASSWORD,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_PRINTER where NAME=?";
		try {
			return getJdbcTemplate().query(sql, new PrinterRowMapper(), name);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查全部
	 * 
	 * @return
	 */
	public List<Printer> getPrinterList() {
		String sql = "select ID,NAME,SEAL_NAME,SEAL_TYPE,USERNAME,PRINT_NUM,PRINTED_NUM,PASSWORD,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_PRINTER";
		return getJdbcTemplate().query(sql, new PrinterRowMapper());
	}

	/**
	 * 分页
	 * 
	 * @param printer
	 * @return
	 */
	public int searchTotal(Printer printer) {
		String sql = "select count(1) from SEAL_PRINTER where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		sql = searchCondition(printer, sql, valueList);

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
	 * 分页
	 * 
	 * @param printer
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Printer> searchPage(Printer printer, final int start, final int end) {
		String sql = "select ID,NAME,SEAL_NAME,SEAL_TYPE,USERNAME,PRINT_NUM,PRINTED_NUM,PASSWORD,GENERATE_TIME,UPDATE_TIME,MAC from SEAL_PRINTER where 1=1 ";
		final List<Object> valueList = new ArrayList<Object>();
		sql = searchCondition(printer, sql, valueList);
		sql += " order by ID desc";

		List<Printer> printerList = getJdbcTemplate().execute(sql, new PreparedStatementCallback<List<Printer>>() {
			@Override
			public List<Printer> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				for (int i = 0; i < valueList.size(); i++) {
					ps.setObject(i + 1, valueList.get(i));
				}
				ps.setMaxRows(end);
				ResultSet rs = ps.executeQuery();
				for (int i = 1; i < start; i++) {
					if (!rs.next())
						break;
				}
				List<Printer> list = new ArrayList<Printer>();
				while (rs.next()) {
					Printer p = new Printer();
					p.setId(ObjectUtil.toLong(rs.getObject("ID")));
					p.setName(rs.getString("NAME"));
					p.setSealName(rs.getString("SEAL_NAME"));
					p.setSealType(ObjectUtil.toInteger(rs.getObject("SEAL_TYPE")));
					p.setUserName(rs.getString("USERNAME"));
					p.setPrintNum(ObjectUtil.toInteger(rs.getObject("PRINT_NUM")));
					p.setPrintedNum(ObjectUtil.toInteger(rs.getObject("PRINTED_NUM")));
					p.setPassword(rs.getString("PASSWORD"));
					p.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
					p.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
					p.setMac(rs.getString("MAC"));
					list.add(p);
				}
				return list;
			}

		});

		return printerList;
	}

	private String searchCondition(Printer printer, String sql, final List<Object> valueList) {
		if (null != printer.getId()) {
			sql += " and ID=? ";
			valueList.add(printer.getId());
		}
		if (StringUtil.isNotBlank(printer.getName())) {
			sql += " and NAME like ? ";
			valueList.add(printer.getName() + "%");
		}
		if (StringUtil.isNotBlank(printer.getSealName())) {
			sql += " and SEAL_NAME like ? ";
			valueList.add(printer.getSealName() + "%");
		}
		if (null != printer.getSealType()) {
			sql += " and SEAL_TYPE=? ";
			valueList.add(printer.getSealType());
		}
		return sql;
	}

	private class PrinterRowMapper implements RowMapper<Printer> {
		public Printer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Printer printer = new Printer();
			printer.setId(ObjectUtil.toLong(rs.getObject("ID")));
			printer.setName(rs.getString("NAME"));
			printer.setSealName(rs.getString("SEAL_NAME"));
			printer.setSealType(ObjectUtil.toInteger(rs.getObject("SEAL_TYPE")));
			printer.setUserName(rs.getString("USERNAME"));
			printer.setPrintNum(ObjectUtil.toInteger(rs.getObject("PRINT_NUM")));
			printer.setPrintedNum(ObjectUtil.toInteger(rs.getObject("PRINTED_NUM")));
			printer.setPassword(rs.getString("PASSWORD"));
			printer.setGenerateTime(ObjectUtil.toLong(rs.getObject("GENERATE_TIME")));
			printer.setUpdateTime(ObjectUtil.toLong(rs.getObject("UPDATE_TIME")));
			printer.setMac(rs.getString("MAC"));
			return printer;
		}
	}

}
