package org.jfw.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class JdbcTemplate {

	private ThreadLocal<Connection> connHolder = new ThreadLocal<Connection>();
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		Connection con = connHolder.get();
		if (con == null) {
			con = this.dataSource.getConnection();
			connHolder.set(con);
		}
		return con;
	}

	public Connection getConnection(boolean polling) throws SQLException {
		Connection con = connHolder.get();
		if (con == null && polling) {
			con = this.dataSource.getConnection();
			connHolder.set(con);
		}
		return con;
	}

	public void commit() throws SQLException {
		Connection con = connHolder.get();
		if (null != con)
			con.commit();
	}

	public void rollback() {
		Connection con = connHolder.get();
		if (null != con)
			try {
				con.rollback();
			} catch (SQLException e) {
			}
	}

	public void close() throws SQLException {
		Connection con = connHolder.get();
		if (null != con) {
			try {
				con.close();
			} finally {
				connHolder.remove();
			}
		}
	}

	public int execute(String sql) throws SQLException {
		return JdbcUtil.execute(this.getConnection(), sql);
	}

	public int execute(String sql, PreparedStatementConfig config) throws SQLException {
		return JdbcUtil.execute(this.getConnection(), sql, config);
	}

	public int[] batchExecute(String sql, BatchPreparedStatementConfig config) throws SQLException {
		return JdbcUtil.batchExecute(this.getConnection(), sql, config);
	}

	public <T> T query(String sql, ResultSetExtractor<T> rse) throws SQLException {
		return JdbcUtil.query(this.getConnection(), sql, rse);
	}

	public <T> T query(String sql, ResultSetExtractor<T> rse, PreparedStatementConfig config) throws SQLException {
		return JdbcUtil.query(this.getConnection(), sql, rse, config);
	}

	public <T> List<T> queryList(String sql, ResultSetExtractor<T> rse) throws SQLException {
		return JdbcUtil.queryList(this.getConnection(), sql, rse);
	}

	public <T> List<T> queryList(String sql, ResultSetExtractor<T> rse, PreparedStatementConfig config)
			throws SQLException {
		return JdbcUtil.queryList(this.getConnection(), sql, rse, config);
	}

	public int queryInt(String sql, int defaultValue) throws SQLException {
		return JdbcUtil.queryInt(this.getConnection(), sql, defaultValue);
	}

	public int queryInt(String sql, int valWithNoFound, int valWithNull) throws SQLException {
		return JdbcUtil.queryInt(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public byte queryByte(String sql, byte defaultValue) throws SQLException {
		return JdbcUtil.queryByte(this.getConnection(), sql, defaultValue);
	}

	public byte queryByte(String sql, byte valWithNoFound, byte valWithNull) throws SQLException {
		return JdbcUtil.queryByte(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public short queryShort(String sql, short defaultValue) throws SQLException {
		return JdbcUtil.queryShort(this.getConnection(), sql, defaultValue);
	}

	public short queryShort(String sql, short valWithNoFound, short valWithNull) throws SQLException {
		return JdbcUtil.queryShort(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public float queryFloat(String sql, float defaultValue) throws SQLException {
		return JdbcUtil.queryFloat(this.getConnection(), sql, defaultValue);
	}

	public float queryFloat(String sql, float valWithNoFound, float valWithNull) throws SQLException {
		return JdbcUtil.queryFloat(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public double queryDouble(String sql, double defaultValue) throws SQLException {
		return JdbcUtil.queryDouble(this.getConnection(), sql, defaultValue);
	}

	public double queryDouble(String sql, double valWithNoFound, double valWithNull) throws SQLException {
		return JdbcUtil.queryDouble(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public String queryString(String sql, String defaultValue) throws SQLException {
		return JdbcUtil.queryString(this.getConnection(), sql, defaultValue);
	}

	public String queryString(String sql, String valWithNoFound, String valWithNull) throws SQLException {
		return JdbcUtil.queryString(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public int queryInt(String sql, PreparedStatementConfig config, int defaultValue) throws SQLException {
		return JdbcUtil.queryInt(this.getConnection(), sql, config, defaultValue);
	}

	public int queryInt(String sql, PreparedStatementConfig config, int valWithNoFound, int valWithNull)
			throws SQLException {
		return JdbcUtil.queryInt(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public byte queryByte(String sql, PreparedStatementConfig config, byte defaultValue) throws SQLException {
		return JdbcUtil.queryByte(this.getConnection(), sql, config, defaultValue);
	}

	public byte queryByte(String sql, PreparedStatementConfig config, byte valWithNoFound, byte valWithNull)
			throws SQLException {
		return JdbcUtil.queryByte(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public long queryLong(String sql, PreparedStatementConfig config, long defaultValue) throws SQLException {
		return JdbcUtil.queryLong(this.getConnection(), sql, config, defaultValue);
	}

	public long queryLong(String sql, PreparedStatementConfig config, long valWithNoFound, long valWithNull)
			throws SQLException {
		return JdbcUtil.queryLong(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public long queryLong(String sql, long defaultValue) throws SQLException {
		return JdbcUtil.queryLong(this.getConnection(), sql, defaultValue);
	}

	public long queryLong(String sql, long valWithNoFound, long valWithNull) throws SQLException {
		return JdbcUtil.queryLong(this.getConnection(), sql, valWithNoFound, valWithNull);
	}

	public short queryShort(String sql, PreparedStatementConfig config, short defaultValue) throws SQLException {
		return JdbcUtil.queryShort(this.getConnection(), sql, config, defaultValue);
	}

	public short queryShort(String sql, PreparedStatementConfig config, short valWithNoFound, short valWithNull)
			throws SQLException {
		return JdbcUtil.queryShort(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public float queryFloat(String sql, PreparedStatementConfig config, float defaultValue) throws SQLException {
		return JdbcUtil.queryFloat(this.getConnection(), sql, config, defaultValue);
	}

	public float queryfloat(String sql, PreparedStatementConfig config, float valWithNoFound, float valWithNull)
			throws SQLException {
		return JdbcUtil.queryfloat(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public double queryDouble(String sql, PreparedStatementConfig config, double defaultValue) throws SQLException {
		return JdbcUtil.queryDouble(this.getConnection(), sql, config, defaultValue);
	}

	public double queryDouble(String sql, PreparedStatementConfig config, double valWithNoFound, double valWithNull)
			throws SQLException {
		return JdbcUtil.queryDouble(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public String queryString(String sql, PreparedStatementConfig config, String defaultValue) throws SQLException {
		return JdbcUtil.queryString(this.getConnection(), sql, config, defaultValue);
	}

	public String queryString(String sql, PreparedStatementConfig config, String valWithNoFound, String valWithNull)
			throws SQLException {
		return JdbcUtil.queryString(this.getConnection(), sql, config, valWithNoFound, valWithNull);
	}

	public Map<String, Object> queryMap(String sql) throws SQLException {
		return JdbcUtil.queryMap(this.getConnection(), sql);
	}

	public Map<String, Object> queryMap(String sql, PreparedStatementConfig config) throws SQLException {
		return JdbcUtil.queryMap(this.getConnection(), sql, config);
	}

	public List<Map<String, Object>> queryMaps(String sql) throws SQLException {
		return JdbcUtil.queryMaps(this.getConnection(), sql);

	}

	public List<Map<String, Object>> queryMaps(String sql, PreparedStatementConfig config) throws SQLException {
		return JdbcUtil.queryMaps(this.getConnection(), sql, config);
	}

	public <T> T queryObject(String sql, Class<T> clazz) throws SQLException {
		return JdbcUtil.queryObject(this.getConnection(), sql, clazz);
	}

	public <T> List<T> queryObjects(String sql, Class<T> clazz) throws SQLException {
		return JdbcUtil.queryObjects(this.getConnection(), sql, clazz);
	}

	public <T> T queryObject(Connection con, String sql, PreparedStatementConfig config, Class<T> clazz)
			throws SQLException {
		return JdbcUtil.queryObject(this.getConnection(), sql, config, clazz);
	}

	public <T> List<T> queryObjects(Connection con, String sql, PreparedStatementConfig config, Class<T> clazz)
			throws SQLException {
		return JdbcUtil.queryObjects(this.getConnection(), sql, config, clazz);

	}

}
