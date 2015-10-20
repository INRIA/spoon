package spoon.test.lambda.testclasses;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tacos {

	public void m(String id) {
		queryForObject("SELECT * FROM persons WHERE id = " + id, (rs, i) ->rs.getString("FIRST_NAME"));
	}

	public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
		return null;
	}

	public interface RowMapper<T> {
		T mapRow(ResultSet var1, int var2) throws SQLException;
	}
}
