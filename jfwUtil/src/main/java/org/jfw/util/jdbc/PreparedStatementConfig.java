package org.jfw.util.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementConfig {
	void config(PreparedStatement ps) throws SQLException;
}
