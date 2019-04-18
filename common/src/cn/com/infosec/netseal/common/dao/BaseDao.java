package cn.com.infosec.netseal.common.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.com.infosec.netseal.common.config.ConfigUtil;

@Repository
public class BaseDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public String searchByPage(String sql, int start, int end, List<Object> valueList) {
		String ret = "";
		String dbType = ConfigUtil.getInstance().getDbType();

		switch (dbType) {
		case "oracle":
			ret = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" + sql + ") A WHERE ROWNUM <= ? ) TEMP WHERE RN > ?";
			valueList.add(end);
			valueList.add(start);
			break;

		case "db2":
			ret = "SELECT * FROM (SELECT TEMP.*, ROW_NUMBER() OVER() AS NUM FROM (" + sql + ") TEMP ) WHERE NUM <= ? AND NUM > ?";
			valueList.add(end);
			valueList.add(start);
			break;

		case "sqlserver":
			sql = sql.replaceFirst("select", "");
			sql =  sql.substring(0,sql.lastIndexOf("order by"));
			String str = sql.substring(0,sql.indexOf(","));
			ret = "select * from ( select row_number() over(order by " + str + " desc) as row," + sql + "  ) tt where tt.row BETWEEN ? and ?";
			valueList.add(start+1);
			valueList.add(end);
			break;

		case "mysql":
			ret = sql + " LIMIT ?, ?";
			valueList.add(start);
			valueList.add(end - start);
			break;

		default:
			ret = "not support db type, is " + dbType;
			break;
		}

		return ret;
	}

}
